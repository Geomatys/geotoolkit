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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.opengis.referencing.operation.TransformException;

/**
 * Series of discrete global grids organized in a hierarchy of successive levels of zone refinement,
 * using a specific set of parameters fully establishing the geometry of all zones.
 *
 * @author Johann Sorel (Geomatys)
 * @see https://docs.ogc.org/DRAFTS/21-038r1.html#term-dggh
 */
public interface DiscreteGlobalGridHierarchy {

    /**
     * @return true if long zone identifiers are supported.
     */
    boolean supportLongIdentifiers();

    /**
     * Returns a textual representation of a zone identifier.
     *
     * @param zoneId
     * @return String representation
     * @throws IllegalArgumentException if zone id is not valid
     */
    String toTextIdentifier(Object zoneId) throws IllegalArgumentException;

    /**
     * Returns a Long representation of a zone identifier.
     *
     * @param zoneId
     * @return Long representation
     * @throws IllegalArgumentException if zone id is not valid
     * @throws UnsupportedOperationException if long types are not supported
     */
    long toLongIdentifier(Object zoneId) throws IllegalArgumentException;

    /**
     * Get zone for given identifier.
     *
     * @param identifier at least String and Long must be supported
     * @return Zone
     * @throws TransformException if identifier do not exist in this DGGRS
     */
    Zone getZone(Object identifier) throws TransformException;

    /**
     * Ordered list of grids by refinement level.
     *
     * @return list of grids, never null
     */
    List<DiscreteGlobalGrid> getGrids();

    /**
     * Compact the given list of zones.
     * A parent zone is added in the list when all it's children are present,
     * those children will be removed if all parents are present.
     * Returned zones are sorted.
     *
     * @param zones to compact, expected to all be at the same level
     * @return sorted list of compact zones
     */
    default List<Zone> compact(List<Zone> zones) {
        if (zones.size() <= 1) return zones;

        final Set<Zone> allChildren = new HashSet(zones);
        final Set<Zone> candidateParents = new HashSet<>();

        for (Zone z : zones) {
            candidateParents.addAll(z.getParents());
        }

        //we keep the children to remove in a separate colleciton because they can be referenced by multiple parents
        final Set<Zone> childrenToRemove = new HashSet<>();
        final List<Zone> fullParents = new ArrayList<>();
        for (Zone parent : candidateParents) {
            Collection<? extends Zone> children = parent.getChildren();
            if (allChildren.containsAll(children)) {
                fullParents.add(parent);
                childrenToRemove.addAll(children);
            }
        }

        //remove the child zone if and only if all parents are in the list
        final Iterator<Zone> iterator = childrenToRemove.iterator();
        while (iterator.hasNext()) {
            final Zone zone = iterator.next();
            if (!fullParents.containsAll(zone.getParents())) {
                //we don't have all parents for the zone, we must keep it
                iterator.remove();
            }
        }

        //try to compact parents further
        final List<Zone> compacted = compact(fullParents);
        //add leftover children
        allChildren.removeAll(childrenToRemove);
        compacted.addAll(allChildren);

        Collections.sort(compacted);

        return compacted;
    }
}
