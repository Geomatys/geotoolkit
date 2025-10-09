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

/**
 * Reference system establishing a specific association of zone identifiers to zones for one or more
 * discrete global grid hierarchy.
 * <p>
 * Synonym of “zonal identifier reference system” and “zone indexing scheme”.
 *
 * @author Johann Sorel (Geomatys)
 * @see https://docs.ogc.org/DRAFTS/21-038r1.html#term-zirs
 */
public interface ZonalReferenceSystem {

    /**
     * @return identifier of the zonal reference system
     */
    String getIdentifier();

    /**
     * @return description of the zonal reference system
     */
    String getDescription();

    /**
     * @return true if create ZonalIdentifiers have a Long type representation
     */
    boolean supportUInt64Form();
}
