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
package org.geotoolkit.referencing.dggs;

import org.opengis.util.CodeList;
import static org.opengis.util.CodeList.valueOf;
import static org.opengis.util.CodeList.values;

/**
 * Set of descriptive information on the dggrs cell properties.
 *
 * @author Johann Sorel (Geomatys)
 * @see https://docs.ogc.org/as/20-040r3/20-040r3.html#tab-DGG_GridConstraint
 */
public final class GridConstraints extends CodeList<GridConstraints> {

    /**
     * We need to construct values with `valueOf(String)` instead of the constructor because this package is not
     * exported to GeoAPI. See `CodeList` class javadoc.
     */

    /**
     * Cell edges are parallel to the base CRS’s coordinate system axes.
     */
    public static final GridConstraints cellAxisAligned = valueOf("cellAxisAligned");

    /**
     * Variation in shape between all the cells in each DiscreteGlobalGrid is minimized.
     */
    public static final GridConstraints cellConformal = valueOf("cellConformal");

    /**
     * Variation in bearing from one cell’s representative position to the next neighboring cell’s representative
     * positions in each DiscreteGlobalGrid is minimized.
     */
    public static final GridConstraints cellEquiAngular = valueOf("cellEquiAngular");

    /**
     * ariation in distance from a cell’s representative position to all of it’s neighboring cell’s representative
     * positions in each DiscreteGlobalGrid is minimized.
     */
    public static final GridConstraints cellEquiDistant = valueOf("cellEquiDistant");

    /**
     * Each parent cell of dimension greater than 2 has a child cell for which the cell⇐zone.representativePosition
     * lies on each of the parent’s faces (two-Dimensional topological boundary element)
     */
    public static final GridConstraints cellEquiSized = valueOf("cellEquiSized");

    private final String description;

    /**
     * Constructs an element of the given name.
     *
     * @param name the name of the new element.
     */
    private GridConstraints(final String name) {
        this(name, null);
    }

    private GridConstraints(final String name, String description) {
        super(name);
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Returns the list of codes of the same kind than this code list element.
     *
     * @return all code {@linkplain #values() values} for this code list.
     */
    @Override
    public GridConstraints[] family() {
        return values(GridConstraints.class);
    }

    /**
     * Returns the sub zone order type that matches the given string, or returns a new one if none match it.
     *
     * @param code the name of the code to fetch or to create.
     * @return a code matching the given name.
     */
    public static GridConstraints valueOf(String code) {
        return valueOf(GridConstraints.class, code, GridConstraints::new).get();
    }

    /**
     * Returns the sub zone order type that matches the given string, or returns a new one if none match it.
     *
     * @param code the name of the code to fetch or to create.
     * @param description if not found set this description to the newly created instance
     * @return a code matching the given name.
     */
    public static GridConstraints valueOf(String code, String description) {
        return valueOf(GridConstraints.class, code, (c) -> new GridConstraints(c, description)).get();
    }

}
