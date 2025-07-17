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

import java.util.Collection;
import java.util.stream.Stream;
import org.apache.sis.geometries.Geometry;
import org.opengis.referencing.gazetteer.Location;
import org.opengis.util.InternationalString;

/**
 * Particular region of space-time.
 *
 * @author Johann Sorel (Geomatys)
 */
public interface Zone extends Location {

    ZonalIdentifier getIdentifier();

    /**
     * Spatiotemporal reference in the form of a compact 64bit integer that uniquely identifies a zone.
     */
    long getIndexedIdentifier();

    /**
     * Spatiotemporal reference in the form of a label or code that uniquely identifies a zone.
     */
    @Override
    InternationalString getGeographicIdentifier();

    /**
     * @return geometry of the zone
     */
    Geometry getGeometry();

    /**
     * @return geometry type name of the zone
     */
    String getShapeType();

    /**
     * @return area of the zone, can be null if not 2D
     */
    Double getAreaMetersSquare();

    /**
     * @return volume if the zone, can be null if not 3D
     */
    Double volumeMetersCube();

    /**
     * @return duration of the zone, can be null if it has no temporal dimension
     */
    Double temporalDurationSeconds();

    /**
     * @return refinement level
     */
    @Override
    RefinementLevel getLocationType();

    /**
     * Get parent zones of given zone.
     * Most DGGRS zones have a single parent, but DGGRS which have children who do not exactly overlap the parent
     * zone may then have severa parents.
     *
     * @return list of parents
     */
    @Override
    Collection<? extends Zone> getParents();

    /**
     * Get zone children zones.
     *
     * @return list of childrens
     */
    @Override
    Collection<? extends Zone> getChildren();

    /**
     * Get neighbor zones.
     *
     * @return list of neighbors
     */
    Collection<? extends Zone> getNeighbors();

    /**
     * @param depth children zones to return, a depth of 0 returns this zone.
     * @return children zones
     */
    default Stream<Zone> getChildrenAtRelativeDepth(int depth) {
        Stream<Zone> stream = Stream.of(this);
        for (int i = 0; i < depth; i++) {
            stream = stream.flatMap((z) -> z.getChildren().stream());
        }
        return stream;
    }

}
