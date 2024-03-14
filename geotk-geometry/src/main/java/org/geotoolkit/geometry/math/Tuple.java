/*
 * (C) 2018 Geomatys.
 */
package org.geotoolkit.geometry.math;

import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public interface Tuple extends DirectPosition{

    @Override
    public default CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return null;
    }

    double get(int indice);

    void set(int indice, double value);

    void set(double[] values);

    int[] toArrayInt();

    float[] toArrayFloat();

    double[] toArrayDouble();

    /**
     * Tuple to array.
     *
     * @param buffer array to write into
     * @param offset offset at which to write
     */
    void toArrayInt(int[] buffer, int offset);

    /**
     * Tuple to array.
     *
     * @param buffer array to write into
     * @param offset offset at which to write
     */
    void toArrayFloat(float[] buffer, int offset);

    /**
     * Tuple to array.
     *
     * @param buffer array to write into
     * @param offset offset at which to write
     */
    void toArrayDouble(double[] buffer, int offset);

    @Override
    public default double getCoordinate(int dimension) throws IndexOutOfBoundsException {
        return get(dimension);
    }

    @Override
    public default void setCoordinate(int dimension, double value) throws IndexOutOfBoundsException, UnsupportedOperationException {
        set(dimension,value);
    }

    @Override
    public default double[] getCoordinates() {
        return toArrayDouble();
    }

    @Override
    public default DirectPosition getDirectPosition() {
        return this;
    }

}
