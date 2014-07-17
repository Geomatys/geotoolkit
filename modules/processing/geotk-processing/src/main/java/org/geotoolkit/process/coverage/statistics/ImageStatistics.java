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

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author bgarcia
 * Image statistic from an image :
 * Get min, max and repartition array by bands
 */
public class ImageStatistics {


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
            bands[i] = new Band();
        }
    }

    public Band[] getBands() {
        return bands;
    }

    public void setBands(final Band[] bands) {
        this.bands = bands;
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
    public class Band {

        /**
         * data repartition map
         */
        private TreeMap<Double, Long> repartition;

        /**
         * no data values
         */
        private double[] noData;

        public Band() {
            repartition = new TreeMap<>();
        }

        public Map<Double, Long> getRepartition() {
            return repartition;
        }

        public double[] getNoData() {
            return noData;
        }

        public void setNoData(final double[] noData) {
            this.noData = noData;
        }

        /**
         * Increment or create new value on {@link org.geotoolkit.process.coverage.statistics.ImageStatistics.Band#repartition}
         * @param data data which need to be add on {@link org.geotoolkit.process.coverage.statistics.ImageStatistics.Band#repartition}
         */
        public void addValue(Double data){
            final Long aLong = repartition.get(data);
            if(aLong==null){
                repartition.put(data, 1L);
            }else{
                repartition.put(data, aLong+1L);
            }
        }

        /**
         * Get image min value
         * @return data which have min elements on image
         */
        public double getMin() {
            return repartition.firstKey();
        }


        /**
         * Get image max value
         * @return data which have max elements on image
         */
        public double getMax() {
            return repartition.lastKey();
        }

        @Override
        public String toString() {
            return "Band{" +
                    "repartition=" + repartition +
                    ", noData=" + Arrays.toString(noData) +
                    "}\n";
        }
    }

}
