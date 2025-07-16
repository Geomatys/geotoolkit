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
package org.geotoolkit.dggal;

import java.util.Collection;
import java.util.List;
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
            //DGGALDggrs.HEALPIX_IDENTIFIER, //conflicts with CDS Healpix
            DGGALDggrs.ISEA3H_IDENTIFIER,
            DGGALDggrs.ISEA4R_IDENTIFIER,
            DGGALDggrs.ISEA7H_IDENTIFIER,
            DGGALDggrs.ISEA7H_Z7_IDENTIFIER,
            DGGALDggrs.ISEA9R_IDENTIFIER,
            DGGALDggrs.IVEA3H_IDENTIFIER,
            DGGALDggrs.IVEA4R_IDENTIFIER,
            DGGALDggrs.IVEA7H_IDENTIFIER,
            DGGALDggrs.IVEA7H_Z7_IDENTIFIER,
            DGGALDggrs.IVEA9R_IDENTIFIER,
            DGGALDggrs.RTEA3H_IDENTIFIER,
            DGGALDggrs.RTEA4R_IDENTIFIER,
            DGGALDggrs.RTEA7H_IDENTIFIER,
            DGGALDggrs.RTEA7H_Z7_IDENTIFIER,
            DGGALDggrs.RTEA9R_IDENTIFIER,
            DGGALDggrs.RHEALPIX_IDENTIFIER,
            DGGALDggrs.GNOSISGLOBALGRID_IDENTIFIER

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
            //case DGGALDggrs.HEALPIX_IDENTIFIER : return DGGALDggrs.HEALPIX_INSTANCE; //conflicts with CDS Healpix
            case DGGALDggrs.ISEA3H_IDENTIFIER : return DGGALDggrs.ISEA3H_INSTANCE;
            case DGGALDggrs.ISEA4R_IDENTIFIER : return DGGALDggrs.ISEA4R_INSTANCE;
            case DGGALDggrs.ISEA7H_IDENTIFIER : return DGGALDggrs.ISEA7H_INSTANCE;
            case DGGALDggrs.ISEA7H_Z7_IDENTIFIER : return DGGALDggrs.ISEA7H_Z7_INSTANCE;
            case DGGALDggrs.ISEA9R_IDENTIFIER : return DGGALDggrs.ISEA9R_INSTANCE;
            case DGGALDggrs.IVEA3H_IDENTIFIER : return DGGALDggrs.IVEA3H_INSTANCE;
            case DGGALDggrs.IVEA4R_IDENTIFIER : return DGGALDggrs.IVEA4R_INSTANCE;
            case DGGALDggrs.IVEA7H_IDENTIFIER : return DGGALDggrs.IVEA7H_INSTANCE;
            case DGGALDggrs.IVEA7H_Z7_IDENTIFIER : return DGGALDggrs.IVEA7H_Z7_INSTANCE;
            case DGGALDggrs.IVEA9R_IDENTIFIER : return DGGALDggrs.IVEA9R_INSTANCE;
            case DGGALDggrs.RTEA3H_IDENTIFIER : return DGGALDggrs.RTEA3H_INSTANCE;
            case DGGALDggrs.RTEA4R_IDENTIFIER : return DGGALDggrs.RTEA4R_INSTANCE;
            case DGGALDggrs.RTEA7H_IDENTIFIER : return DGGALDggrs.RTEA7H_INSTANCE;
            case DGGALDggrs.RTEA7H_Z7_IDENTIFIER : return DGGALDggrs.RTEA7H_Z7_INSTANCE;
            case DGGALDggrs.RTEA9R_IDENTIFIER : return DGGALDggrs.RTEA9R_INSTANCE;
            case DGGALDggrs.RHEALPIX_IDENTIFIER : return DGGALDggrs.RHEALPIX_INSTANCE;
            case DGGALDggrs.GNOSISGLOBALGRID_IDENTIFIER : return DGGALDggrs.GNOSISGLOBALGRID_INSTANCE;
            default : throw new NoSuchIdentifierException("Unknown identifier " + dgghId, dgghId);
        }
    }

}
