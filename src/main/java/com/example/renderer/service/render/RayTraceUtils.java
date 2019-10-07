package com.example.renderer.service.render;

import com.example.renderer.model.RayHit;
import com.example.renderer.model.object.Renderable;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;

import java.util.Collection;
import java.util.Comparator;
import java.util.function.UnaryOperator;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.sqrt;
import static java.lang.Math.tan;

public class RayTraceUtils {

    public static double getXForPixel(int x, double width, double height, double fov) {
        return (2 * (x + 0.5) / width - 1) * tan(fov / 2) * width / height;
    }

    public static double getYForPixel(int y, double height, double fov) {
        return (1 - 2 * (y + 0.5) / height) * tan(fov / 2);
    }

    public static Point3D getDirectionForPixel(int pixelX, int pixelY, double width, double height, double fov) {
        double x = getXForPixel(pixelX, width, height, fov);
        double y = getYForPixel(pixelY, height, fov);
        return new Point3D(x, y, -1).normalize();
    }

    public static RayHit getRayHit(Collection<Renderable> objects, Point3D origin, Point3D direction) {
        return objects.stream()
                .map(object3D -> object3D.intersection(origin, direction))
                .filter(RayHit::isHit)
                .min(Comparator.comparing(RayHit::getDistance))
                .orElse(RayHit.MISS);
    }

    public static Point3D reflect(Point3D I, Point3D N) {
        return I.subtract(N.multiply(2 * I.dotProduct(N)));
    }

    public static Point3D refract(Point3D I, Point3D N, double ior) {
        double cosi = -max(-1, min(1, I.dotProduct(N)));
        if (cosi < 0) {
            return refract(I, N.multiply(-1), ior);
        }
        double eta = 1 / ior;
        double k = 1 - eta * eta * (1 - cosi * cosi);
        return k < 0 ? Point3D.ZERO : I.multiply(eta).add(N.multiply(eta * cosi - sqrt(k))).normalize();
    }

    public static double fresnel(Point3D I, Point3D N, double ior) {
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

    public static Color transformColor(Color source, UnaryOperator<Double> transform) {
        double r = transform.apply(source.getRed());
        double g = transform.apply(source.getGreen());
        double b = transform.apply(source.getBlue());
        return Color.color(r, g, b);
    }

    public static double clamp(double value) {
        return value < 0 ? 0 : value > 1 ? 1 : value;
    }

}
