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
package org.geotoolkit.process.coverage.statistics;

import org.geotoolkit.image.internal.SampleType;

import java.io.Serializable;
import java.util.*;

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
    public class Band implements Serializable {

        private final int bandIndex;

        private String name = null;

        private SampleType dataType = null;

        private Double min = null;
        private Double max = null;

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
            if (fullSize < distributionSize) {
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
        public double getMin() {
            return min;
        }

        /**
         * Get image max value
         * @return data which have max elements on image
         */
        public double getMax() {
            return max;
        }

        public void setMin(Double min) {
            this.min = min;
        }

        public void setMax(Double max) {
            this.max = max;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Band ").append(bandIndex).append(" {")
                    .append(" min=").append(min)
                    .append(", max=").append(max)
                    .append(", dataType=").append(dataType.name())
                    .append(", histogram=")
                    .append(Arrays.toString(histogram))
                    .append(", noData=").append(Arrays.toString(noData))
                    .append("}\n");
            return sb.toString();
        }

    }

}
