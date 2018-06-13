package com.weikan.app.original.utils;

import android.support.annotation.NonNull;

/**
 * abstract of class/interface and so on
 *
 * @author kailun on 16/4/6
 */
public class MathUtils {

    public static double angle(@NonNull Location a, @NonNull Location  b) {
        double dx = b.x - a.x;
        double dy = b.y - a.y;
        return Math.atan2(dy, dx);
    }

    public static double angle(double ax, double ay, double bx, double by) {
        double dx = bx - ax;
        double dy = by - ay;
        return Math.atan2(dy, dx);
    }

    public static double distance(@NonNull Location a, @NonNull Location b) {
        double dx = b.x - a.x;
        double dy = b.y - a.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public static double distance(double ax, double ay, double bx, double by) {
        double dx = bx - ax;
        double dy = by - ay;
        return Math.sqrt(dx * dx + dy * dy);
    }
}
