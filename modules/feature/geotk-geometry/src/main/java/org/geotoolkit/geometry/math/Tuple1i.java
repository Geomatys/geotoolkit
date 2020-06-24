/*
 * (C) 2018 Geomatys.
 */
package org.geotoolkit.geometry.math;

import java.util.Arrays;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class Tuple1i implements Tuple {

    public int x;

    public Tuple1i() {
    }

    public Tuple1i(int x) {
        this.x = x;
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
            case 0 : x = (int) value; break;
            default : throw new IndexOutOfBoundsException("Invalid index " + indice);
        }
    }

    @Override
    public void set(double[] values) {
        if (getDimension() != values.length) {
            throw new IllegalArgumentException("Vectors size are different : "+getDimension()+" and "+values.length);
        }
        x = (int) values[0];
    }

    @Override
    public int[] toArrayInt() {
        return new int[]{x};
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
        buffer[offset] = x;
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
