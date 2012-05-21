/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.feature;

import java.util.Arrays;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Alexis MANIN (geomatys)
 */
public class MockDirectPosition2D implements DirectPosition {

    double coord[];

    public MockDirectPosition2D(double x, double y) {
        coord = new double[2];
        coord[0] = x;
        coord[1] = y;
    }
    
    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getDimension() {
        return 2;
    }

    @Override
    public double[] getCoordinate() {
        return coord;
    }

    @Override
    public double getOrdinate(int dimension) throws IndexOutOfBoundsException {
        return coord[dimension];
    }

    @Override
    public void setOrdinate(int dimension, double value) throws IndexOutOfBoundsException, UnsupportedOperationException {
        coord[dimension] = value;
    }

    @Override
    public DirectPosition getDirectPosition() {
        return this;
    }
    
    @Override
    public boolean equals(Object other){
        if (this == other)
            return true;
        if(other instanceof DirectPosition){
            double otherValues[] = ((DirectPosition)other).getCoordinate();
            return Arrays.equals(coord, otherValues);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash;
        return hash;
    }
    
}
