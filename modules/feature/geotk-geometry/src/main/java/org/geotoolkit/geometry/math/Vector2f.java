/*
 * (C) 2018 Geomatys.
 */
package org.geotoolkit.geometry.math;

import java.util.Arrays;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class Vector2f implements Vector {

    public float x;
    public float y;

    public Vector2f() {
    }

    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2f(float[] array) {
        this.x = array[0];
        this.y = array[1];
    }

    @Override
    public int getDimension() {
        return 2;
    }

    @Override
    public double get(int indice) {
        switch (indice) {
            case 0 : return x;
            case 1 : return y;
            default : throw new IndexOutOfBoundsException("Invalid index " + indice);
        }
    }

    @Override
    public void set(int indice, double value) {
        switch (indice) {
            case 0 : x = (float) value; break;
            case 1 : y = (float) value; break;
            default : throw new IndexOutOfBoundsException("Invalid index " + indice);
        }
    }

    @Override
    public void set(double[] values) {
        if (getDimension() != values.length) {
            throw new IllegalArgumentException("Vectors size are different : "+getDimension()+" and "+values.length);
        }
        x = (float) values[0];
        y = (float) values[1];
    }

    @Override
    public double length() {
        return Math.sqrt(x*x + y*y);
    }

    @Override
    public double lengthSquare() {
        return x*x + y*y;
    }

    @Override
    public void normalize() {
        final double s = 1.0 / length();
        x *= s;
        y *= s;
    }

    @Override
    public void add(Tuple other) {
        if (getDimension() != other.getDimension()) {
            throw new IllegalArgumentException("Vectors size are different : "+getDimension()+" and "+other.getDimension());
        }
        x += other.get(0);
        y += other.get(1);
    }

    @Override
    public void subtract(Tuple other) {
        if (getDimension() != other.getDimension()) {
            throw new IllegalArgumentException("Vectors size are different : "+getDimension()+" and "+other.getDimension());
        }
        x -= other.get(0);
        y -= other.get(1);
    }

    @Override
    public void scale(double scale) {
        x *= scale;
        y *= scale;
    }

    @Override
    public int[] toArrayInt() {
        return new int[]{(int)x, (int)y};
    }

    @Override
    public float[] toArrayFloat() {
        return new float[]{x,y};
    }

    @Override
    public double[] toArrayDouble() {
        return new double[]{x,y};
    }

    @Override
    public void toArrayInt(int[] buffer, int offset) {
        buffer[offset  ] = (int) x;
        buffer[offset+1] = (int) y;
    }

    @Override
    public void toArrayFloat(float[] buffer, int offset) {
        buffer[offset  ] = x;
        buffer[offset+1] = y;
    }

    @Override
    public void toArrayDouble(double[] buffer, int offset) {
        buffer[offset  ] = x;
        buffer[offset+1] = y;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()+Arrays.toString(toArrayDouble());
    }
}
