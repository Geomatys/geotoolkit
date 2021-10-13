/*
 * (C) 2018 Geomatys.
 */
package org.geotoolkit.geometry.math;

import java.util.Arrays;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class Tuple3i implements Tuple {

    public int x;
    public int y;
    public int z;

    public Tuple3i() {
    }

    public Tuple3i(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
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
            case 0 : x = (int) value; break;
            case 1 : y = (int) value; break;
            case 2 : z = (int) value; break;
            default : throw new IndexOutOfBoundsException("Invalid index " + indice);
        }
    }

    @Override
    public void set(double[] values) {
        if (getDimension() != values.length) {
            throw new IllegalArgumentException("Vectors size are different : "+getDimension()+" and "+values.length);
        }
        x = (int) values[0];
        y = (int) values[1];
        z = (int) values[2];
    }

    @Override
    public int[] toArrayInt() {
        return new int[]{x,y,z};
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
        buffer[offset  ] = x;
        buffer[offset+1] = y;
        buffer[offset+2] = z;
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
