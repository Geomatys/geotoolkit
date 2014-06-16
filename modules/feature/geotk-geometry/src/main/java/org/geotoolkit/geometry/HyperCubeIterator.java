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

package org.geotoolkit.geometry;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.geotoolkit.geometry.HyperCubeIterator.HyperCube;
import org.opengis.coverage.grid.GridEnvelope;

/**
 * Iterator over an hypercube, will iterate on all dimensions of the hypercube
 * returning a smaller hypercube each time. the maximum size is given in the constructor. 
 * 
 * @author Johann Sorel (Geomatys)
 */
public class HyperCubeIterator implements Iterator<HyperCube>{
 
    private final int[] maxSize;
    private final int nbDim;
    private final int[] corner;
    private final int[] mins;
    private final int[] maxs;
    private HyperCube next = null;

    /**
     * 
     * @param mins hypercube lower corner, inclusive
     * @param maxs hypercube upper corner, exclusive
     * @param maxSize maximum size of the iteration hypercube
     */
    public HyperCubeIterator(int[] mins, int[] maxs, int[] maxSize) {
        this.maxSize = maxSize;
        this.nbDim = mins.length;
        this.mins = mins;
        this.maxs = maxs;
        
        //place corner on the minimum
        this.corner = new int[nbDim];
        System.arraycopy(mins, 0, corner, 0, nbDim);
    }
    
    @Override
    public boolean hasNext() {
        findNext();
        return next != null;
    }

    @Override
    public HyperCube next() {
        findNext();
        if(next==null){
            throw new NoSuchElementException("No more elements.");
        }
        HyperCube t = next;
        next = null;
        return t;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported.");
    }
    
    private void findNext(){
        if(next!=null) return;
        
        if(corner[nbDim-1] >= maxs[nbDim-1]){
            //nothing left
             return;
        }
        
        final int[] lower = new int[nbDim];
        final int[] upper = new int[nbDim];
        
        for(int i=0;i<nbDim;i++){
            lower[i] = corner[i];
            upper[i] = corner[i] + maxSize[i];
            //ensure the upper value is not greater then the data limit.
            if(upper[i]>=maxs[i]) upper[i] = maxs[i];
        }
        
        //prepare next iteration
        for(int k=0;k<nbDim;k++){
            corner[k] += maxSize[k];
            if(k<nbDim-1 && corner[k]>=maxs[k]){
                //we are too far on this axis
                //return to minimum and increment next dimension
                corner[k] = mins[k];
            }else{
                break;
            }
        }
                
        next = new HyperCube(lower, upper);
    }
    
    public static class HyperCube{
        private final int[] lower;
        private final int[] upper;

        /**
         * 
         * @param lower corner, inclusive
         * @param upper corner, exclusive
         */
        public HyperCube(int[] lower, int[] upper) {
            this.lower = lower;
            this.upper = upper;
        }

        /**
         * 
         * @return lower corner, inclusive
         */
        public int[] getLower() {
            return lower;
        }

        /**
         * 
         * @return upper corner, exclusive
         */
        public int[] getUpper() {
            return upper;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final HyperCube other = (HyperCube) obj;
            if (!Arrays.equals(this.lower, other.lower)) {
                return false;
            }
            if (!Arrays.equals(this.upper, other.upper)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "HyperCube "+Arrays.toString(lower)+" "+Arrays.toString(upper);
        }
        
    }
    
    
    /**
     * Create an iterator from GridEnvelope.
     * @param gridEnv envelope to iterate in
     * @param maxSize maximum size of the iteration hypercube
     * @return HyperCubeIterator
     */
    public static HyperCubeIterator create(GridEnvelope gridEnv, int[] maxSize) {
        final int nbDim = gridEnv.getDimension();
        final int[] mins = new int[nbDim];
        final int[] maxs = new int[nbDim];
        
        for(int i=0;i<nbDim;i++){
            mins[i] = gridEnv.getLow(i);
            maxs[i] = gridEnv.getHigh(i)+1; //high value is inclusive in grid envelopes
        }
        
        return new HyperCubeIterator(mins, maxs, maxSize);
    }
    
}
