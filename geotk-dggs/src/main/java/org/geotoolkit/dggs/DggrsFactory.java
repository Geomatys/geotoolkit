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
package org.geotoolkit.dggs;

import java.util.Collection;
import java.util.List;
import org.geotoolkit.dggs.a5.A5Dggrs;
import org.geotoolkit.dggs.h3.H3Dggrs;
import org.geotoolkit.dggs.healpix.HealpixDggrs;
import org.geotoolkit.dggs.s2.S2Dggrs;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGridReferenceSystem;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGridReferenceSystemFactory;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.util.FactoryException;
import org.opengis.util.NoSuchIdentifierException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class DggrsFactory implements DiscreteGlobalGridReferenceSystemFactory{

    private static final List<String> DGGHS = List.of(
            S2Dggrs.IDENTIFIER,
            H3Dggrs.IDENTIFIER,
            HealpixDggrs.IDENTIFIER,
            A5Dggrs.IDENTIFIER
        );

    @Override
    public Collection<String> listDggh() {
        return DGGHS;
    }

    @Override
    public Collection<String> listZonalRefId(String dggh) {
        return List.of("default");
    }

    @Override
    public DiscreteGlobalGridReferenceSystem createDggrs(String dgghId, String zonalRefId, GeographicCRS base) throws FactoryException {
        switch (dgghId) {
            case A5Dggrs.IDENTIFIER : return A5Dggrs.INSTANCE;
            case H3Dggrs.IDENTIFIER : return H3Dggrs.INSTANCE;
            case HealpixDggrs.IDENTIFIER : return HealpixDggrs.INSTANCE;
            case S2Dggrs.IDENTIFIER : return S2Dggrs.INSTANCE;
            default : throw new NoSuchIdentifierException("Unknown identifier " + dgghId, dgghId);
        }
    }

}
