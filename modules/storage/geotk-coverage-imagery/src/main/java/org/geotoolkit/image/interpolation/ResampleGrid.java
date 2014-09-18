/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.image.interpolation;

/**
 * A grid which represent source positions use during resampling.
 * 
 * @author Remi Marechal (Geomatys).
 */
public class ResampleGrid {
    
    /**
     * Minimum grid coordinate (usually pixel unity) in X direction.
     */
    private final int minGridX;
    
    /**
     * Minimum grid coordinate (usually pixel unity) in Y direction.
     */
    private final int minGridY;
    
    /**
     * Grid cells number in X direction.
     */
    private final int gridWidth;
    
    /**
     * Grid cells number in Y direction.
     */
    private final int gridHeight;
    
    /**
     * Step in X direction.
     */
    private final int stepX;
    
    /**
     * Step in Y direction.
     */
    private final int stepY;
    
    /**
     * The grid use to transform destination coordinate to source coordinate.
     */
    private final double[] grid;

    /**
     * Create a grid adapted to transform destination coordinate to source coordinate.
     * 
     * @param startX minimum grid position in X direction.
     * @param startY minimum grid position in Y direction.
     * @param stepX grid step in X direction.
     * @param stepY grid step in Y direction.
     * @param nbCellX cell number in X direction.
     * @param nbCellY cell number in Y direction.
     * @param positions all cells grid source positions.
     */
    ResampleGrid(final int startX, final int startY,final int stepX, final int stepY, 
                        final int nbCellX, final int nbCellY, final double[] positions) {
        minGridX   = startX;
        minGridY   = startY;
        this.stepX = stepX;
        this.stepY = stepY;
        gridWidth  = nbCellX;
        gridHeight = nbCellY;
        grid       = positions;
    }

    /**
     * Returns minimum grid coordinate in X direction.
     * 
     * @return minimum grid coordinate in X direction.
     */
    int getMinGridX() {
        return minGridX;
    }
    
    /**
     * Returns minimum grid coordinate in Y direction.
     * 
     * @return minimum grid coordinate in Y direction.
     */
    int getMinGridY() {
        return minGridY;
    }
    
    /**
     * Returns step in X direction.
     * 
     * @return step in X direction.
     */
    int getStepX() {
        return stepX;
    }

    /**
     * Returns step in Y direction.
     * 
     * @return step in Y direction.
     */
    int getStepY() {
        return stepY;
    }
    
    /**
     * Returns grid which contain source positions coordinates.
     * 
     * @return grid which contain source positions coordinates.
     */
    double[] getGrid() {
        return grid;
    }
    
    /**
     * Returns cells number in X direction.
     * 
     * @return cells number in X direction.
     */
    int getGridWidth() {
        return gridWidth;
    }
    
    /**
     * Returns cells number in Y direction.
     * 
     * @return cells number in Y direction.
     */
    int getGridHeight() {
        return gridHeight;
    }
    
    /**
     * Returns index of first grid cell in X direction.
     * 
     * @return index of first grid cell in X direction.
     */
    int getMinGridXIndex() {
        return 0;
    }
    
    /**
     * Returns index of first grid cell in Y direction.
     * 
     * @return index of first grid cell in Y direction.
     */
    int getMinGridYIndex() {
        return 0;
    }
}
