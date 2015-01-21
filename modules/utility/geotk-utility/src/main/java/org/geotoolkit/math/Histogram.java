/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
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
package org.geotoolkit.math;

/**
 * Histogram querying class.
 *
 * @author Johann Sorel (Geomatys)
 */
public class Histogram {

    private final long[] buckets;
    private final double start;
    private final double end;

    private long sum = -1;

    public Histogram(long[] buckets, double start, double end) {
        this.buckets = buckets;
        this.start = start;
        this.end = end;
    }

    public double getStart() {
        return start;
    }

    public double getEnd() {
        return end;
    }

    public long[] getBuckets() {
        return buckets;
    }

    /**
     * Calculate sum of all values in the buckets.
     *
     * @return number of values in the histogram (sum of all buckets)
     */
    public long getSum() {
        if(sum!=-1) return sum;

        //calculate sum
        long s = 0;
        for(long l : buckets) s+=l;
        sum = s;
        return sum;
    }

    /**
     * Get the span of a bucket.
     * @return bucket span
     */
    public double getBucketSize(){
        return (end-start) / buckets.length;
    }

    public double[] getBucketRange(int index){
        final double span = getBucketSize();
        return new double[]{start+index*span, start+(index+1)*span};
    }

    /**
     * Estimate the thredhold value for a given ratio .
     *
     * @param ratio [0-1]
     */
    public double getValueAt(double ratio){
        if(ratio<=0) return start;
        if(ratio>=1) return end;

        final long sum = getSum();
        double remain = sum * ratio;

        for(int i=0;i<buckets.length;i++){
            if(buckets[i]>remain){
                //this bucket contain the searched %
                //linear interpolatation
                final double span = getBucketSize();
                final double bucketStart = start+i*span;
                final double bucketEnd = start+(i+1)*span;
                
                ratio = 1.0 - (((double)buckets[i]-remain) / (double)buckets[i]);

                return bucketStart + (bucketEnd-bucketStart) * ratio;
            }else{
                remain -= buckets[i];
            }
        }

        //we should be reach here, because of double rounding values it might happen
        //if the % value is very close to 1
        return end;
    }


}
