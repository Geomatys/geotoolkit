/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.storage.rs;

/**
 * Referenced coverage location iterator.
 *
 * @author Johann Sorel (Geomatys)
 */
public interface CodeIterator {

    /**
     * Returns true of the iterator is writable.
     * @return may be true
     */
    default boolean isWritable() {
        return false;
    }

    /**
     * @return current location identifier
     */
    int[] getPosition();

    /**
     * Move iterator to given zone
     *
     * @param zid searched location identifier
     */
    void moveTo(int[] zid);

    /**
     * Move to next location.
     *
     * @return true if there is a next location
     */
    boolean next();

    /**
     * @return number of bands in the coverage iterator
     */
    int getNumBands();

    /**
     * Get a cell sample.
     *
     * @param band index
     * @return band value
     */
    default int getSample(int band) {
        return (int) getSampleDouble(band);
    }

    /**
     * Get a cell sample.
     *
     * @param band index
     * @return band value
     */
    default float getSampleFloat(int band) {
        return (float) getSampleDouble(band);
    }

    /**
     * Get a cell sample.
     *
     * @param band index
     * @return band value
     */
    public abstract double getSampleDouble(int band);

    /**
     * Get cell values..
     *
     * @return all band values
     */
    default int[] getCell(int[] dest) {
        final double[] cell = getCell((double[])null);
        if (dest == null) dest = new int[cell.length];
        for (int i = 0; i < cell.length; i++) dest[i] = (int) cell[i];
        return dest;
    }

    /**
     * Get cell values..
     *
     * @return all band values
     */
    default float[] getCell(float[] dest) {
        final double[] cell = getCell((double[]) null);
        if (dest == null) dest = new float[cell.length];
        for (int i = 0; i < cell.length; i++) dest[i] = (int) cell[i];
        return dest;
    }

    /**
     * Get cell values.
     *
     * @return all band values
     */
    default double[] getCell(double[] dest) {
        if (dest == null) dest = new double[getNumBands()];
        for (int i = 0; i < dest.length; i++) dest[i] = getSampleDouble(i);
        return dest;
    }

    /**
     * Move iterator back to the starting position.
     */
    void rewind();
}
