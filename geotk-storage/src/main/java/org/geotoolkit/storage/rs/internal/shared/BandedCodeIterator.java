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
package org.geotoolkit.storage.rs.internal.shared;

import java.util.HashMap;
import java.util.Map;
import org.apache.sis.feature.AbstractFeature;
import org.geotoolkit.storage.rs.CodeIterator;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyNotFoundException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class BandedCodeIterator extends AbstractFeature implements CodeIterator {

    protected final FeatureType type;
    protected final Map<String,Integer> index = new HashMap<>();

    public BandedCodeIterator(FeatureType type, String[] mapping) {
        super(type);
        this.type = type;
        for (int i = 0; i<mapping.length; i++) {
            index.put(mapping[i], i);
        }
    }

    /**
     * @return number of bands in the coverage iterator
     */
    public int getNumBands() {
        return index.size();
    }

    /**
     * Get a cell sample.
     *
     * @param band index
     * @return band value
     */
    public int getSample(int band) {
        return (int) getSampleDouble(band);
    }

    /**
     * Get a cell sample.
     *
     * @param band index
     * @return band value
     */
    public float getSampleFloat(int band) {
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
    public int[] getCell(int[] dest) {
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
    public float[] getCell(float[] dest) {
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
    public double[] getCell(double[] dest) {
        if (dest == null) dest = new double[getNumBands()];
        for (int i = 0; i < dest.length; i++) dest[i] = getSampleDouble(i);
        return dest;
    }

    @Override
    public Feature getSample() {
        return this;
    }

    @Override
    public Object getValueOrFallback(String propName, Object missingPropertyFallback) {
        final Integer idx = index.get(propName);
        if (idx == null) return missingPropertyFallback;
        return getSampleDouble(idx);
    }

    @Override
    public Object getPropertyValue(String propName) throws PropertyNotFoundException {
        final Integer idx = index.get(propName);
        if (idx == null) throw new PropertyNotFoundException();
        return getSampleDouble(idx);
    }

}
