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
package org.geotoolkit.storage.dggs;

/**
 * Set of descriptive information on the dggrs cell properties.
 *
 * @author Johann Sorel (Geomatys)
 * @see https://docs.ogc.org/as/20-040r3/20-040r3.html#tab-DGG_GridConstraint
 */
public final class CellConstraints {

    private final boolean cellAxisAligned;
    private final boolean cellConformal;
    private final boolean cellEquiangular;
    private final boolean cellEquidistant;
    private final boolean cellEqualSized;

    /**
     * @param cellAxisAligned Cell edges are parallel to the base CRS’s coordinate system axes.
     * @param cellConformal Variation in shape between all the cells in each DiscreteGlobalGrid is minimized.
     * @param cellEquiangular Variation in bearing from one cell’s representative position to the next neighboring cell’s representative positions in each DiscreteGlobalGrid is minimized.
     * @param cellEquidistant Variation in distance from a cell’s representative position to all of it’s neighboring cell’s representative positions in each DiscreteGlobalGrid is minimized.
     * @param cellEqualSized Variation in interior size between all cells in each DiscreteGlobalGrid is minimized.
     */
    public CellConstraints(boolean cellAxisAligned, boolean cellConformal,
            boolean cellEquiangular, boolean cellEquidistant, boolean cellEqualSized) {
        this.cellAxisAligned = cellAxisAligned;
        this.cellConformal = cellConformal;
        this.cellEquiangular = cellEquiangular;
        this.cellEquidistant = cellEquidistant;
        this.cellEqualSized = cellEqualSized;
    }

    /**
     * Cell edges are parallel to the base CRS’s coordinate system axes.
     */
    public boolean isCellAxisAligned() {
        return cellAxisAligned;
    }

    /**
     * Variation in shape between all the cells in each DiscreteGlobalGrid is minimized.
     */
    public boolean isCellConformal() {
        return cellConformal;
    }

    /**
     * Variation in bearing from one cell’s representative position to the next neighboring cell’s representative positions in each DiscreteGlobalGrid is minimized.
     */
    public boolean isCellEquiangular() {
        return cellEquiangular;
    }

    /**
     * Variation in distance from a cell’s representative position to all of it’s neighboring cell’s representative positions in each DiscreteGlobalGrid is minimized.
     */
    public boolean isCellEquidistant() {
        return cellEquidistant;
    }

    /**
     * Variation in interior size between all cells in each DiscreteGlobalGrid is minimized.
     */
    public boolean isCellEqualSized() {
        return cellEqualSized;
    }
}
