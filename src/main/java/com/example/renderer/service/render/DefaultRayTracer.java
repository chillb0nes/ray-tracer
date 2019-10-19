package com.example.renderer.service.render;

import com.example.renderer.model.Material;
import com.example.renderer.model.RayHit;
import com.example.renderer.model.Scene;
import com.example.renderer.model.light.LightSource;
import com.example.renderer.model.object.Renderable;
import javafx.concurrent.Task;
import javafx.geometry.Point3D;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Comparator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import static com.example.renderer.service.render.RayTraceUtils.*;

@Component
public class DefaultRayTracer implements TaskAwareExecutorRenderer {

    @Value("${maxBounceCount}")
    private int maxBounceCount;

    @Value("${backgroundColor}")
    private Color background;

    @Override
    public Image getImage(final Scene scene, Task task) throws InterruptedException {
        ExecutorService executor = getExecutor();
        WritableImage image = new WritableImage(scene.getWidth(), scene.getHeight());
        PixelWriter writer = image.getPixelWriter();
        int pixelCount = scene.getWidth() * scene.getHeight();
        CountDownLatch latch = new CountDownLatch(pixelCount);
        for (int j = 0; j < scene.getHeight(); j++) {
            for (int i = 0; i < scene.getWidth(); i++) {
                final int x = i;
                final int y = j;
                executor.execute(() -> {
                    if (task.isCancelled()) {
                        executor.shutdownNow();
                        throw new RuntimeException("Task was cancelled");
                    }
                    Color result = castRay(scene, x, y);
                    writer.setColor(x, y, result);
                    latch.countDown();
                });
            }
        }
        latch.await();
        executor.shutdownNow();
        return image;
    }

    private Color castRay(Scene scene, int x, int y) {
        Point3D direction = getDirectionForPixel(x, y, scene.getWidth(), scene.getHeight(), scene.getFov());
        return castRay(scene, scene.getCameraOrigin(), direction, 0);
    }

    private Color castRay(Scene scene, Point3D origin, Point3D direction, int bounceCount) {
        if (bounceCount > maxBounceCount) {
            return background;
        }

        RayHit rayHit = getRayHit(scene.getObjects(), origin, direction);
        if (RayHit.MISS == rayHit) {
            return background;
        }

        Material material = rayHit.getHitObject().getMaterial();
        Color reflectionColor = background;
        Color refractionColor = background;

        if (material.isReflective()) {
            Point3D reflectDirection = reflect(direction, rayHit.getNormal());
            Point3D reflectOrigin = avoidSelfHit(rayHit, reflectDirection);
            reflectionColor = castRay(scene, reflectOrigin, reflectDirection, bounceCount + 1);
        }

        if (material.isTranslucent()) {
            Point3D refractDirection = refract(direction, rayHit.getNormal(), material.getIor());
            Point3D refractOrigin = avoidSelfHit(rayHit, refractDirection);
            refractionColor = castRay(scene, refractOrigin, refractDirection, bounceCount + 1);
        }

        if (material.isReflective() && material.isTranslucent()) {
            double fresnel = fresnel(direction, rayHit.getNormal(), material.getIor());
            reflectionColor = transformColor(reflectionColor, c -> c * fresnel);
            refractionColor = transformColor(refractionColor, c -> c * (1 - fresnel));
        }

        double diffuseIntensity = 0;
        double specularIntensity = 0;

        for (LightSource light : scene.getLights()) {
            Point3D lightDirection = light.getCenter().subtract(rayHit.getHitPoint()).normalize();
            double lightDistance = light.getCenter().subtract(rayHit.getHitPoint()).magnitude();
            double lightIntensity = light.getIntensity();

            Point3D shadowOrigin = avoidSelfHit(rayHit, lightDirection);
            RayHit shadowHit = getRayHit(scene.getObjects(), shadowOrigin, lightDirection);

            if (RayHit.MISS != shadowHit) {
                double shadowDistance = shadowHit.getHitPoint().subtract(rayHit.getHitPoint()).magnitude();
                if (shadowDistance < lightDistance) {
                    lightIntensity *= shadowHit.getHitObject().getMaterial().getTransmittance();
                }
            }

            diffuseIntensity += Math.max(0, lightDirection.dotProduct(rayHit.getNormal())) * lightIntensity;

            Point3D reflection = reflect(lightDirection, rayHit.getNormal());
            specularIntensity += Math.pow(Math.max(0, -reflection.dotProduct(lightDirection)), material.getSpecularExp()) * lightIntensity;
        }

        return computeColor(material, diffuseIntensity, specularIntensity, reflectionColor, refractionColor);
    }

    private static Color computeColor(Material material,
                                      double diffuseIntensity,
                                      double specularIntensity,
                                      Color reflectionColor,
                                      Color refractionColor
    ) {
        Color color = material.getColor();
        double diffuseHardness = material.getDiffuse();
        double specularHardness = material.getSpecular();
        double reflectivity = material.getReflectivity();
        double transmittance = material.getTransmittance();

        double r = clamp(color.getRed() * diffuseIntensity * diffuseHardness
                + specularIntensity * specularHardness
                + reflectionColor.getRed() * reflectivity
                + refractionColor.getRed() * transmittance
        );
        double g = clamp(color.getGreen() * diffuseIntensity * diffuseHardness
                + specularIntensity * specularHardness
                + reflectionColor.getGreen() * reflectivity
                + refractionColor.getGreen() * transmittance
        );
        double b = clamp(color.getBlue() * diffuseIntensity * diffuseHardness
                + specularIntensity * specularHardness
                + reflectionColor.getBlue() * reflectivity
                + refractionColor.getBlue() * transmittance
        );
        return Color.color(r, g, b);
    }

    private static Point3D avoidSelfHit(RayHit rayHit, Point3D direction) {
        Point3D normal = rayHit.getNormal();
        double shift = 0.0001;
        return direction.dotProduct(normal) < 0
                ? rayHit.getHitPoint().subtract(normal.multiply(shift))
                : rayHit.getHitPoint().add(normal.multiply(shift));
    }
}
