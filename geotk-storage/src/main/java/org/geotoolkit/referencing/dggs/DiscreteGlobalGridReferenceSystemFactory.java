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

import java.util.Collection;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.util.FactoryException;

/**
 * Factory to create DGGRS instances.
 *
 * @author Johann Sorel (Geomatys)
 */
public interface DiscreteGlobalGridReferenceSystemFactory {

    /**
     * List supported DGGH names.
     * Example : Healpix, H3, IVEA3H, ...
     * @return never null
     */
    Collection<String> listDggh();

    /**
     * List the possible zonal referencing identifier for a DGGH.
     * The first entry is the default, which should be the most common case.
     *
     * @param dggh Dggh name, not null
     * @return never null, must contain at least one item
     */
    Collection<String> listZonalRefId(String dggh);

    /**
     * Create a DGGRS.
     *
     * @param dgghId not null
     * @param zonalRefId can be null, in which case the default one from listZonalRefId will be used
     * @param base the base CRS attached, null for default
     * @throws FactoryException if creation failed
     */
    DiscreteGlobalGridReferenceSystem createDggrs(String dgghId, String zonalRefId, GeographicCRS base) throws FactoryException;

}
