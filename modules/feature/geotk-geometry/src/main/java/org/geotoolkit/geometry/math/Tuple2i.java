/*
 * (C) 2018 Geomatys.
 */
package org.geotoolkit.geometry.math;

import java.util.Arrays;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class Tuple2i implements Tuple {

    public int x;
    public int y;

    public Tuple2i() {
    }

    public Tuple2i(int x, int y) {
        this.x = x;
        this.y = y;
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
            case 0 : x = (int) value; break;
            case 1 : y = (int) value; break;
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
    }

    @Override
    public int[] toArrayInt() {
        return new int[]{x,y};
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
        buffer[offset  ] = x;
        buffer[offset+1] = y;
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
