package com.example.renderer.service;

import com.example.renderer.model.light.LightSource;
import com.example.renderer.model.Material;
import com.example.renderer.model.RayHit;
import com.example.renderer.model.Scene;
import com.example.renderer.model.object.Renderable;
import javafx.concurrent.Task;
import javafx.geometry.Point3D;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.example.renderer.model.RayHit.*;
import static java.lang.Math.*;
import static java.util.Comparator.comparing;

public class RayTracer {
    private Color backgroundColor = Color.LIGHTBLUE;
    private Color selectionColor = Color.MAGENTA;
    private int maxDepth = 4;

    private Scene scene;
    private Task task;

    public WritableImage renderScene(Scene scene, Task task) throws InterruptedException {
        this.scene = scene;
        this.task = task;
        return getImage(scene, this::render);
    }

    public WritableImage renderOutline(Scene scene, Task task) throws InterruptedException {
        this.scene = scene;
        this.task = task;
        return getImage(scene, this::drawOutline);
    }

    private WritableImage getImage(Scene scene, BiFunction<Integer, Integer, Color> colorFunction) throws InterruptedException {
        WritableImage image = new WritableImage(scene.getWidth(), scene.getHeight());
        forEachPixel(image, new BiConsumer<Integer, Integer>() {
            @Override
            public void accept(Integer x, Integer y) {
                Color color = colorFunction.apply(x, y);
                image.getPixelWriter().setColor(x, y, color);
            }
        });
        return image;
    }

    private void forEachPixel(Image image, BiConsumer<Integer, Integer> consumer) throws InterruptedException {
        for (int j = 0; j < image.getHeight(); j++) {
            for (int i = 0; i < image.getWidth(); i++) {
                if (task.isCancelled()) {
                    throw new InterruptedException();
                }
                consumer.accept(i, j);
            }
        }
    }

    private double cameraX(int x) {
        return (2 * (x + 0.5) / scene.getWidth() - 1) * tan(scene.getFov() / 2) * scene.getWidth() / scene.getHeight();
    }

    private double cameraY(int y) {
        return (1 - 2 * (y + 0.5) / scene.getHeight()) * tan(scene.getFov() / 2);
    }

    private Color render(int i, int j) {
        Point3D origin = scene.getCameraOrigin();
        Point3D direction = new Point3D(cameraX(i), cameraY(j), -1).normalize();

        return rayCast(origin, direction, 0);
    }

    private Color drawOutline(int i, int j) {
        Set<Renderable> selected = scene.getSelected();
        double x = cameraX(i);
        double y = cameraY(j);

        Point3D origin = scene.getCameraOrigin();
        Point3D direction = new Point3D(x, y, -1).normalize();

        boolean noneHit = selected.stream().noneMatch(obj -> obj.intersection(origin, direction).isHit());
        if (noneHit) {
            for (int k = -1; k < 2; k++) {
                for (int l = -1; l < 2; l++) {
                    x = cameraX(i + k);
                    y = cameraY(j + l);
                    Point3D directionNear = new Point3D(x, y, -1).normalize();

                    boolean anyHit = selected.stream().anyMatch(obj -> obj.intersection(origin, directionNear).isHit());
                    if (anyHit) {
                        return selectionColor;
                    }
                }
            }
        }
        return Color.TRANSPARENT;
    }

    private RayHit getRayHit(Point3D origin, Point3D direction) {
        return scene.getObjects().stream()
                .map(renderable -> renderable.intersection(origin, direction))
                .filter(RayHit::isHit)
                .min(comparing(RayHit::getDistance))
                .orElse(MISS);
    }

    private Color rayCast(Point3D origin, Point3D direction, int depth) {
        if (depth > maxDepth) {
            return backgroundColor;
        }

        RayHit rayHit = getRayHit(origin, direction);

        if (rayHit == MISS) {
            return backgroundColor;
        }

        Renderable hitObject = rayHit.getHitObject();
        Material material = hitObject.getMaterial();
        Point3D hitPoint = rayHit.getHitPoint();
        double diffuseIntensity = 0;
        double specularIntensity = 0;
        Color reflectionColor = backgroundColor;
        Color refractionColor = backgroundColor;

        if (material.isReflective()) {
            Point3D reflectDirection = reflect(direction, rayHit.getNormal());
            Point3D reflectOrigin = (reflectDirection.dotProduct(rayHit.getNormal()) < 0)
                    ? hitPoint.subtract(rayHit.getNormal().multiply(0.001))
                    : hitPoint.add(rayHit.getNormal().multiply(0.001));
            reflectionColor = rayCast(reflectOrigin, reflectDirection, depth + 1);
        }

        if (material.isTranslucent()) {
            Point3D refractDirection = refract(direction, rayHit.getNormal(), material.getIor());
            Point3D refractOrigin = (refractDirection.dotProduct(rayHit.getNormal()) < 0)
                    ? hitPoint.subtract(rayHit.getNormal().multiply(0.001))
                    : hitPoint.add(rayHit.getNormal().multiply(0.001));
            refractionColor = rayCast(refractOrigin, refractDirection, depth + 1);
        }

        if (material.isReflective() && material.isTranslucent()) {
            double fresnel = fresnel(direction, rayHit.getNormal(), material.getIor());
            reflectionColor = computeColor(reflectionColor, colorComponent -> colorComponent * fresnel);
            refractionColor = computeColor(refractionColor, colorComponent -> colorComponent * (1 - fresnel));
        }

        for (LightSource light : scene.getLights()) {
            Point3D lightDirection = light.getCenter().subtract(hitPoint).normalize();
            double lightDistance = light.getCenter().subtract(hitPoint).magnitude();
            double lightIntensity = light.getIntensity();

            Point3D shadowOrigin = (lightDirection.dotProduct(rayHit.getNormal()) < 0)
                    ? hitPoint.subtract(rayHit.getNormal().multiply(0.001))
                    : hitPoint.add(rayHit.getNormal().multiply(0.001));

            Optional<RayHit> shadowed = scene.getObjects().stream()
                    .filter(renderable -> !renderable.equals(hitObject))
                    .map(renderable -> renderable.intersection(shadowOrigin, lightDirection))
                    .filter(RayHit::isHit)
                    .filter(hit -> hit.getHitPoint().subtract(hitPoint).magnitude() < lightDistance)
                    .findFirst();

            if (shadowed.isPresent()) {
                lightIntensity *= shadowed.get().getHitObject().getMaterial().getTransmittance();
            }

            diffuseIntensity += lightIntensity * max(0, lightDirection.dotProduct(rayHit.getNormal()));
            specularIntensity += pow(
                    max(0, -reflect(lightDirection, rayHit.getNormal()).dotProduct(lightDirection)),
                    material.getSpecularExp()) * lightIntensity;
        }
        return computeColor(material, diffuseIntensity, specularIntensity, reflectionColor, refractionColor);
    }

    private static Point3D reflect(Point3D I, Point3D N) {
        return I.subtract(N.multiply(2 * I.dotProduct(N)));
    }

    private static Point3D refract(Point3D I, Point3D N, double ior) {
        double cosi = -max(-1, min(1, I.dotProduct(N)));
        if (cosi < 0) {
            return refract(I, N.multiply(-1), ior);
        }
        double eta = 1 / ior;
        double k = 1 - eta * eta * (1 - cosi * cosi);
        return k < 0 ? Point3D.ZERO : I.multiply(eta).add(N.multiply(eta * cosi - sqrt(k))).normalize();
    }

    private static double fresnel(Point3D I, Point3D N, double ior) {
        double cosi = max(-1, min(1, I.dotProduct(N)));
        double etai = 1;
        if (cosi > 0) {
            double t = etai;
            etai = ior;
            ior = t;
        }
        double sin = etai / ior * sqrt(max(0, 1 - cosi * cosi));
        if (sin < 1) {
            double cos = sqrt(max(0, 1 - sin * sin));
            cosi = abs(cosi);
            double rs = ((ior * cosi) - (etai * cos)) / ((ior * cosi) + (etai * cos));
            double rp = ((etai * cosi) - (ior * cos)) / ((etai * cosi) + (ior * cos));
            return (rs * rs + rp * rp) / 2;
        } else {
            return 1;
        }
    }

    private static Color computeColor(Material material,
                               double diffuseIntensity,
                               double specularIntensity,
                               Color reflection,
                               Color refraction) {
        Color color = material.getColor();
        double diffuseHardness = material.getDiffuse();
        double specularHardness = material.getSpecular();
        double reflectivity = material.getReflectivity();
        double transmittance = material.getTransmittance();

        double r = clamp(
                color.getRed() * diffuseIntensity * diffuseHardness
                        + specularIntensity * specularHardness
                        + reflection.getRed() * reflectivity
                        + refraction.getRed() * transmittance);
        double g = clamp(
                color.getGreen() * diffuseIntensity * diffuseHardness
                        + specularIntensity * specularHardness
                        + reflection.getGreen() * reflectivity
                        + refraction.getGreen() * transmittance);
        double b = clamp(
                color.getBlue() * diffuseIntensity * diffuseHardness
                        + specularIntensity * specularHardness
                        + reflection.getBlue() * reflectivity
                        + refraction.getBlue() * transmittance);

        return Color.color(r, g, b);
    }

    private static Color computeColor(Color sourceColor, Function<Double, Double> computeFunction) {
        double r = computeFunction.apply(sourceColor.getRed());
        double g = computeFunction.apply(sourceColor.getGreen());
        double b = computeFunction.apply(sourceColor.getBlue());

        return Color.color(r, g, b);
    }

    private static double clamp(double value) {
        return value < 0 ? 0 : value > 1 ? 1 : value;
    }
}
