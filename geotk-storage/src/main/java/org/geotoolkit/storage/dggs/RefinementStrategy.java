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

import org.opengis.util.CodeList;

/**
 * List of characteristics of the DGGS refinement strategy.
 *
 * @author Johann Sorel (Geomatys)
 * @see https://docs.ogc.org/as/20-040r3/20-040r3.html#tab-DGG_RefinementStrategy
 */
public final class RefinementStrategy extends CodeList<RefinementStrategy> {

    /**
     * We need to construct values with `valueOf(String)` instead of the constructor because this package is not
     * exported to GeoAPI. See `CodeList` class javadoc.
     */

    /**
     * parent⇐zone.representativePosition() = child⇐zone.representativePosition() for one child.
     * Each parent cell shares a cell←zone.representativePosition with one of its child cells.
     */
    public static final RefinementStrategy centredChildCell = valueOf("centredChildCell");

    /**
     * parent.boundary = parent.child().boundary.
     * The boundary of the set of child cells for a parent is identical to the parent’s boundary.
     */
    public static final RefinementStrategy nestedChildCell = valueOf("nestedChildCell");

    /**
     * Each parent cell has a child⇐zone.representativePosition coincident with each of the parent’s nodes
     * (zero-Dimensional topological boundary element).
     */
    public static final RefinementStrategy nodeCentredChildCell = valueOf("nodeCentredChildCell");

    /**
     * Each parent cell of dimension greater than 1 has a child cell for which the cell⇐zone.representativePosition
     * lies on each of the parent’s edges (one-Dimensional topological boundary element)
     */
    public static final RefinementStrategy edgeCentredChildCell = valueOf("edgeCentredChildCell");

    /**
     * Each parent cell of dimension greater than 2 has a child cell for which the cell⇐zone.representativePosition
     * lies on each of the parent’s faces (two-Dimensional topological boundary element)
     */
    public static final RefinementStrategy faceCentredChildCell = valueOf("faceCentredChildCell");

    /**
     * Each parent cell of dimension greater than 3 has a child cell for which the cell⇐zone.representativePosition
     * lies on each of the parent’s solids (three-Dimensional topological boundary element)
     */
    public static final RefinementStrategy solidCentredChildCell = valueOf("solidCentredChildCell");

    private final String description;

    /**
     * Constructs an element of the given name.
     *
     * @param name the name of the new element.
     */
    private RefinementStrategy(final String name) {
        this(name, null);
    }

    private RefinementStrategy(final String name, String description) {
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
    public RefinementStrategy[] family() {
        return values(RefinementStrategy.class);
    }

    /**
     * Returns the sub zone order type that matches the given string, or returns a new one if none match it.
     *
     * @param code the name of the code to fetch or to create.
     * @return a code matching the given name.
     */
    public static RefinementStrategy valueOf(String code) {
        return valueOf(RefinementStrategy.class, code, RefinementStrategy::new).get();
    }

    /**
     * Returns the sub zone order type that matches the given string, or returns a new one if none match it.
     *
     * @param code the name of the code to fetch or to create.
     * @param description if not found set this description to the newly created instance
     * @return a code matching the given name.
     */
    public static RefinementStrategy valueOf(String code, String description) {
        return valueOf(RefinementStrategy.class, code, (c) -> new RefinementStrategy(c, description)).get();
    }
}
