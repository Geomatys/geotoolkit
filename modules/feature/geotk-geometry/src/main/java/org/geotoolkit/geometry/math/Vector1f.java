/*
 * (C) 2018 Geomatys.
 */
package org.geotoolkit.geometry.math;

import java.util.Arrays;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class Vector1f implements Vector {

    public float x;

    public Vector1f() {
    }

    public Vector1f(float x) {
        this.x = x;
    }

    public Vector1f(float[] array) {
        this.x = array[0];
    }

    @Override
    public int getDimension() {
        return 1;
    }

    @Override
    public double get(int indice) {
        switch (indice) {
            case 0 : return x;
            default : throw new IndexOutOfBoundsException("Invalid index " + indice);
        }
    }

    @Override
    public void set(int indice, double value) {
        switch (indice) {
            case 0 : x = (float) value; break;
            default : throw new IndexOutOfBoundsException("Invalid index " + indice);
        }
    }

    @Override
    public void set(double[] values) {
        if (getDimension() != values.length) {
            throw new IllegalArgumentException("Vectors size are different : "+getDimension()+" and "+values.length);
        }
        x = (float) values[0];
    }

    @Override
    public double length() {
        return Math.sqrt(x*x);
    }

    @Override
    public double lengthSquare() {
        return x*x;
    }

    @Override
    public void normalize() {
        final double s = 1.0 / length();
        x *= s;
    }

    @Override
    public void add(Tuple other) {
        if (getDimension() != other.getDimension()) {
            throw new IllegalArgumentException("Vectors size are different : "+getDimension()+" and "+other.getDimension());
        }
        x += other.get(0);
    }

    @Override
    public void subtract(Tuple other) {
        if (getDimension() != other.getDimension()) {
            throw new IllegalArgumentException("Vectors size are different : "+getDimension()+" and "+other.getDimension());
        }
        x -= other.get(0);
    }

    @Override
    public void scale(double scale) {
        x *= scale;
    }

    @Override
    public int[] toArrayInt() {
        return new int[]{(int)x};
    }

    @Override
    public float[] toArrayFloat() {
        return new float[]{x};
    }

    @Override
    public double[] toArrayDouble() {
        return new double[]{x};
    }

    @Override
    public void toArrayInt(int[] buffer, int offset) {
        buffer[offset] = (int) x;
    }

    @Override
    public void toArrayFloat(float[] buffer, int offset) {
        buffer[offset] = x;
    }

    @Override
    public void toArrayDouble(double[] buffer, int offset) {
        buffer[offset] = x;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()+Arrays.toString(toArrayDouble());
    }
}
