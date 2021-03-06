/*
 * (C) 2018 Geomatys.
 */
package org.geotoolkit.geometry.math;

import java.util.Arrays;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class Vector3f implements Vector {

    public float x;
    public float y;
    public float z;

    public Vector3f() {
    }

    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3f(float[] array) {
        this.x = array[0];
        this.y = array[1];
        this.z = array[2];
    }

    @Override
    public int getDimension() {
        return 3;
    }

    @Override
    public double get(int indice) {
        switch (indice) {
            case 0 : return x;
            case 1 : return y;
            case 2 : return z;
            default : throw new IndexOutOfBoundsException("Invalid index " + indice);
        }
    }

    @Override
    public void set(int indice, double value) {
        switch (indice) {
            case 0 : x = (float) value; break;
            case 1 : y = (float) value; break;
            case 2 : z = (float) value; break;
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
        z = (float) values[2];
    }

    public void set(Tuple v) {
        if (getDimension() != v.getDimension()) {
            throw new IllegalArgumentException("Vectors size are different : "+getDimension()+" and "+v.getDimension());
        }
        x = (float) v.get(0);
        y = (float) v.get(1);
        z = (float) v.get(2);
    }

    @Override
    public double length() {
        return Math.sqrt(x*x + y*y + z*z);
    }

    @Override
    public double lengthSquare() {
        return x*x + y*y + z*z;
    }

    @Override
    public void normalize() {
        final double s = 1.0 / length();
        x *= s;
        y *= s;
        z *= s;
    }

    @Override
    public void add(Tuple other) {
        if (getDimension() != other.getDimension()) {
            throw new IllegalArgumentException("Vectors size are different : "+getDimension()+" and "+other.getDimension());
        }
        x += other.get(0);
        y += other.get(1);
        z += other.get(2);
    }

    @Override
    public void subtract(Tuple other) {
        if (getDimension() != other.getDimension()) {
            throw new IllegalArgumentException("Vectors size are different : "+getDimension()+" and "+other.getDimension());
        }
        x -= other.get(0);
        y -= other.get(1);
        z -= other.get(2);
    }

    @Override
    public void scale(double scale) {
        x *= scale;
        y *= scale;
        z *= scale;
    }

    @Override
    public int[] toArrayInt() {
        return new int[]{(int)x, (int)y, (int)z};
    }

    @Override
    public float[] toArrayFloat() {
        return new float[]{x,y,z};
    }

    @Override
    public double[] toArrayDouble() {
        return new double[]{x,y,z};
    }

    @Override
    public void toArrayInt(int[] buffer, int offset) {
        buffer[offset  ] = (int) x;
        buffer[offset+1] = (int) y;
        buffer[offset+2] = (int) z;
    }

    @Override
    public void toArrayFloat(float[] buffer, int offset) {
        buffer[offset  ] = x;
        buffer[offset+1] = y;
        buffer[offset+2] = z;
    }

    @Override
    public void toArrayDouble(double[] buffer, int offset) {
        buffer[offset  ] = x;
        buffer[offset+1] = y;
        buffer[offset+2] = z;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()+Arrays.toString(toArrayDouble());
    }
}
