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
package org.geotoolkit.storage.dggs;

import org.apache.sis.storage.DataStoreException;

/**
 * Writable DGGS coverage zone iterator.
 *
 * @author Johann Sorel (Geomatys)
 */
public interface WritableZoneIterator extends ZoneIterator, AutoCloseable {

    /**
     * @return always true
     */
    @Override
    default boolean isWritable() {
        return true;
    }

    /**
     * @param band coverage band index
     * @param value cell band new value
     */
    default void setSample(int band, int value) {
        setSample(band, (double)value);
    }

    /**
     * @param band coverage band index
     * @param value cell band new value
     */
    default void setSample(int band, float value) {
        setSample(band, (double)value);
    }

    /**
     * @param band coverage band index
     * @param value cell band new value
     */
    void setSample(int band, double value);

    /**
     * @param values cell bands new values
     */
    default void setCell(int[] values) {
        final double[] cell = new double[values.length];
        for (int i = 0; i < cell.length; i++) cell[i] = values[i];
        setCell(cell);
    }

    /**
     * @param values cell bands new values
     */
    default void setCell(float[] values) {
        final double[] cell = new double[values.length];
        for (int i = 0; i < cell.length; i++) cell[i] = values[i];
        setCell(cell);
    }

    /**
     * @param values cell bands new values
     */
    default void setCell(double[] values) {
        for (int i = 0; i < values.length; i++) {
            setSample(i, values[i]);
        }
    }

    /**
     * Release any resource attached to the writer.
     */
    @Override
    public void close() throws DataStoreException;

}
