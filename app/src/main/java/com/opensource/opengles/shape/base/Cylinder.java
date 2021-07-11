package com.opensource.opengles.shape.base;

import android.graphics.Point;

public class Cylinder {
    public final Point center;
    public final float radius;
    public final float height;

    public Cylinder(Point center, float radius, float height) {
        this.center = center;
        this.radius = radius;
        this.height = height;
    }
}
