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

import java.io.Serializable;
import java.util.*;

/**
 * Image statistic from an image :
 * Get min, max and fullDistribution array by bands
 *
 * @author bgarcia
 * @author Quentin Boileau (Geomatys)
 */
public class ImageStatistics implements Serializable{

    /**
     * Image bands
     */
    private Band[] bands;

    /**
     * constructor with a band numbers to create bands
     * @param nbBands image band numbers
     */
    public ImageStatistics(int nbBands) {
        bands = new Band[nbBands];
        for (int i = 0; i < bands.length; i++) {
            bands[i] = new Band(i);
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

    /**
     * Finalize analysis.
     */
    void finish() {
        for (int i = 0; i < bands.length; i++) {
            bands[i].finish();
        }
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

        private final int bandNumber;
        /**
         * data fullDistribution map
         */
        private Map<Double, Long> fullDistribution = new HashMap<>();

        private Double min = null;
        private Double max = null;

        /**
         * no data values
         */
        private double[] noData;

        public Band(int bandNumber) {
            this.bandNumber = bandNumber;
        }

        public Map<Double, Long> getFullDistribution() {
            return new TreeMap<>(fullDistribution);
        }

        public Long[] tightenDistribution(int distributionSize) {
            Map<Double, Long> fullDistribution = getFullDistribution();
            int fullSize = fullDistribution.size();
            final Long[] fullDistArr = fullDistribution.values().toArray(new Long[fullSize]);
            if (fullSize < distributionSize) {
                return fullDistArr;
            }

            final Long[] distArr = new Long[distributionSize];
            double steps = (double)fullSize / (double)distributionSize;
            //int middleStep = (int) Math.ceil(steps / 2f);

            double pos = 0.0;
            for (int i = 0; i < distributionSize; i++) {
                long distSum = 0;

                double endStep = pos + steps;
                int start = (int) pos;
                int end = (int) endStep;

                for (int j = start; j < end; j++) {
                    distSum += fullDistArr[j];
                }

                pos = endStep;

                distArr[i] = distSum;
            }
            return distArr;
        }

        public void setFullDistribution(Map<Double, Long> repartition) {
            this.fullDistribution = repartition;
        }

        public double[] getNoData() {
            return noData;
        }

        public void setNoData(final double[] noData) {
            this.noData = noData;
        }

        /**
         * Increment or create new value on {@link org.geotoolkit.process.coverage.statistics.ImageStatistics.Band#fullDistribution}
         * @param data data which need to be add on {@link org.geotoolkit.process.coverage.statistics.ImageStatistics.Band#fullDistribution}
         */
        public void addValue(double data){
            final Long aLong = fullDistribution.get(data);
            if(aLong == null){
                fullDistribution.put(data, 1L);
            }else{
                fullDistribution.put(data, aLong + 1L);
            }
        }

        /**
         * Get image min value
         * @return data which have min elements on image
         */
        public double getMin() {
            if (min == null) {
                computeMin();
            }
            return min;
        }

        /**
         * Get image max value
         * @return data which have max elements on image
         */
        public double getMax() {
            if (max == null) {
                computeMax();
            }
            return max;
        }

        private void computeMin() {
            Double[] values = fullDistribution.keySet().toArray(new Double[fullDistribution.size()]);
            Double min = null;
            for (Double value : values) {
                if (min == null) {
                    min = value;
                    continue;
                }

                min = Math.min(min, value);
            }
            this.min = min != null ? min : 0.0;
        }

        private void computeMax() {
            Double[] values = fullDistribution.keySet().toArray(new Double[fullDistribution.size()]);
            Double max = null;
            for (Double value : values) {
                if (max == null) {
                    max = value;
                    continue;
                }

                max = Math.max(max, value);
            }
            this.max = max != null ? max : 0.0;
        }

        void finish() {
            fullDistribution = new TreeMap<>(fullDistribution);
            this.min = (Double) ((TreeMap)fullDistribution).firstKey();
            this.max = (Double) ((TreeMap)fullDistribution).lastKey();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Band ").append(bandNumber).append(" {")
                    .append(" min=").append(min)
                    .append(", max=").append(max)
                    .append(", fullDistribution=")
                    .append(fullDistribution)
                    .append(", noData=").append(Arrays.toString(noData))
                    .append("}\n");
            return sb.toString();
        }
    }

}
