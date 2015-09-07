/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

import org.geotoolkit.image.internal.SampleType;

import java.io.Serializable;
import java.util.*;
import org.apache.sis.util.ArgumentChecks;
import org.opengis.coverage.Coverage;
import org.opengis.metadata.content.AttributeGroup;
import org.opengis.metadata.content.CoverageDescription;
import org.opengis.metadata.content.RangeDimension;
import org.opengis.metadata.content.SampleDimension;

/**
 * Image statistic from an image :
 * Get min, max, datatype and histogram array by bands
 *
 * @author bgarcia
 * @author Quentin Boileau (Geomatys)
 */
public class ImageStatistics implements Serializable{

    /**
     * Image bands
     */
    private Band[] bands;

    public ImageStatistics(int nbBands) {
        this(nbBands, null);
    }

    /**
     * constructor with a band numbers to create bands
     * @param nbBands image band numbers
     */
    public ImageStatistics(int nbBands, SampleType dataType) {
        bands = new Band[nbBands];
        for (int i = 0; i < bands.length; i++) {
            bands[i] = new Band(i, dataType);
        }
    }

    public Band[] getBands() {
        return bands;
    }

    public void setBands(final Band[] bands) {
        this.bands = bands;
    }

    public Band getBand(int bandNumber) {
        return bands[bandNumber];
    }

    @Override
    public String toString() {
        return "StatisticContainer{\n" +
                "bands=" + Arrays.toString(bands) +
                "}";
    }

    /**
     * Band Inner class
     */
    public static class Band implements Serializable {

        private final int bandIndex;

        private String name = null;

        private SampleType dataType = null;

        private Double min = null;
        private Double max = null;
        private Double mean = null;
        private Double std = null;

        /**
         * no data values
         */
        private double[] noData = null;
        private long[] histogram = null;

        public Band(int bandIndex) {
            this(bandIndex, null);
        }

        public Band(int bandIndex, SampleType dataType) {
            this.bandIndex = bandIndex;
            this.dataType = dataType;
        }

        public int getBandIndex() {
            return bandIndex;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public SampleType getDataType() {
            return dataType;
        }

        public void setDataType(SampleType dataType) {
            this.dataType = dataType;
        }

        public void setHistogram(long[] histogram) {
            this.histogram = histogram;
        }

        public long[] getHistogram() {
            return histogram;
        }

        public Map<Double, Long> getDistribution() {

            int nbBins = histogram.length;
            final Map<Double, Long> map = new HashMap(nbBins);

            double binSize = (max - min) / ((double)nbBins-1.0);
            for (int j = 0; j <nbBins; j++) {
                double value = min+binSize*j;
                long occurs = histogram[j];
                map.put(value, occurs);
            }
            return new TreeMap<>(map);
        }

        public long[] tightenHistogram(int distributionSize) {
            int fullSize = histogram.length;
            if (fullSize <= distributionSize) {
                return histogram;
            }

            final long[] distArr = new long[distributionSize];
            double steps = (double)fullSize / (double)distributionSize;
            //int middleStep = (int) Math.ceil(steps / 2f);

            double pos = 0.0;
            for (int i = 0; i < distributionSize; i++) {
                long distSum = 0;

                double endStep = pos + steps;
                int start = (int) pos;
                int end = (int) endStep;

                for (int j = start; j < end; j++) {
                    distSum += histogram[j];
                }

                pos = endStep;

                distArr[i] = distSum;
            }
            return distArr;
        }

        public double[] getNoData() {
            return noData;
        }

        public void setNoData(final double[] noData) {
            this.noData = noData;
        }

        /**
         * Get image min value
         * @return data which have min elements on image
         */
        public Double getMin() {
            return min;
        }

        public void setMin(Double min) {
            this.min = min;
        }

        /**
         * Get image max value
         * @return data which have max elements on image
         */
        public Double getMax() {
            return max;
        }

        public void setMax(Double max) {
            this.max = max;
        }

        public Double getMean() {
            return mean;
        }

        public void setMean(Double mean) {
            this.mean = mean;
        }

        public Double getStd() {
            return std;
        }

        public void setStd(Double std) {
            this.std = std;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Band ").append(bandIndex).append(" {")
                    .append(" min=").append(min)
                    .append(", max=").append(max)
                    .append(", mean=").append(mean)
                    .append(", std=").append(std)
                    .append(", dataType=").append(dataType.name())
                    .append(", histogram=")
                    .append(Arrays.toString(histogram))
                    .append(", noData=").append(Arrays.toString(noData))
                    .append("}\n");
            return sb.toString();
        }

    }

    /**
     * Create {@link ImageStatistics} from {@link CoverageDescription}.<br/>
     * Moreover travel all existing {@link SampleDimension} from {@link CoverageDescription} 
     * to define appropriate statistics.
     * 
     * @param covdesc Description of the studied {@link Coverage}.
     * @return {@link ImageStatistics} or {@code null} if it is impossible to define 
     * statistic for each bands, or {@link CoverageDescription#getAttributeGroups() } is {@code null} or empty,
     * or also if internaly {@link AttributeGroup#getAttributes()} is {@code null} or empty.
     */
    public static ImageStatistics transform(final CoverageDescription covdesc) {
        ArgumentChecks.ensureNonNull("CoverageDescription", covdesc);
        
        final Collection<? extends AttributeGroup> attributeGroups = covdesc.getAttributeGroups();
        if (attributeGroups == null || attributeGroups.isEmpty()) 
            return null;

        final List<Band> bands = new ArrayList<>();
        
        //search for band statistics
        for (AttributeGroup attg : attributeGroups) {
            final Collection<? extends RangeDimension> attributes = attg.getAttributes();
            if (attributes == null || attributes.isEmpty())
                return null;
            for (RangeDimension rd : attributes) {
                if (!(rd instanceof SampleDimension)) continue;
                final int i = Integer.parseInt(rd.getSequenceIdentifier().tip().toString());
                final SampleDimension sd = (SampleDimension) rd;
                final Band band = new Band(i);
                band.setMin(sd.getMinValue());
                band.setMax(sd.getMaxValue());
                band.setStd(sd.getStandardDeviation());
                band.setMean(sd.getMeanValue());
                
                if (sd instanceof DefaultSampleDimensionExt) {
                    final DefaultSampleDimensionExt ext = (DefaultSampleDimensionExt) sd;
                    band.setHistogram(ext.getHistogram());
                }

                bands.add(band);
            }
        }

        if (bands.isEmpty()) return null;

        final ImageStatistics stats = new ImageStatistics(bands.size());
        stats.setBands(bands.toArray(new Band[0]));
        return stats;
    }
}
