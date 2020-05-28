/*
 * (C) 2018 Geomatys.
 */
package org.geotoolkit.geometry.math;

import java.util.Arrays;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class Vector3d implements Vector {

    public double x;
    public double y;
    public double z;

    public Vector3d() {
    }

    public Vector3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3d(double[] array) {
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
            case 0 : x = value; break;
            case 1 : y = value; break;
            case 2 : z = value; break;
            default : throw new IndexOutOfBoundsException("Invalid index " + indice);
        }
    }

    @Override
    public void set(double[] values) {
        if (getDimension() != values.length) {
            throw new IllegalArgumentException("Vectors size are different : "+getDimension()+" and "+values.length);
        }
        x = values[0];
        y = values[1];
        z = values[2];
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
        return new float[]{(float)x,(float)y,(float)z};
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
        buffer[offset  ] = (float) x;
        buffer[offset+1] = (float) y;
        buffer[offset+2] = (float) z;
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
