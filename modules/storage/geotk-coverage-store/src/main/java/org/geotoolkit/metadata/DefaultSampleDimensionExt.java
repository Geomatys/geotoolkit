/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.metadata;

import org.apache.sis.metadata.iso.content.DefaultSampleDimension;

/**
 * Extent ISO-19115 SampleDimension to store band histogram informations.
 *
 * @author Johann Sorel (Geomatys)
 */
public class DefaultSampleDimensionExt extends DefaultSampleDimension{

    private double histogramMin;
    private double histogramMax;
    private long[] histogram;

    public double getHistogramMin() {
        return histogramMin;
    }

    public void setHistogramMin(double histogramMin) {
        this.histogramMin = histogramMin;
    }

    public double getHistogramMax() {
        return histogramMax;
    }

    public void setHistogramMax(double histogramMax) {
        this.histogramMax = histogramMax;
    }

    /**
     * Get band histogram.
     * Histogram uses regular intervals between histogram min and max values.
     * 
     * @return histogram, can be null.
     */
    public long[] getHistogram(){
        return histogram;
    }

    /**
     * Set band histogram.
     * 
     * @param histogram, can be null.
     */
    public void setHistogram(long[] histogram) {
        this.histogram = histogram;
    }
    
}
