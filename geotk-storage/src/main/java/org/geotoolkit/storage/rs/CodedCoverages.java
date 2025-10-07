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
package org.geotoolkit.storage.rs;

import javax.measure.IncommensurableException;
import org.apache.sis.referencing.CRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.GridCoverageResource;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGridReferenceSystem;
import org.geotoolkit.storage.dggs.internal.shared.GridAsDiscreteGlobalGridResource;
import org.geotoolkit.storage.rs.internal.shared.GridAsCodedResource;
import org.geotoolkit.storage.rs.internal.shared.CodedCoverageAsFeatureSet;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class CodedCoverages {

    private CodedCoverages(){}

    /**
     * View given grid coverage as a DiscreteGlobalGridResource.
     */
    public static CodedResource viewAsDggrs(GenericName name, GridCoverageResource base, DiscreteGlobalGridReferenceSystem dggrs) throws DataStoreException, IncommensurableException, TransformException, FactoryException {
        final CoordinateReferenceSystem baseCrs = base.getGridGeometry().getCoordinateReferenceSystem();
        if (CRS.isHorizontalCRS(baseCrs)) {
            //use a more efficient implementation
            return new GridAsDiscreteGlobalGridResource(dggrs, base);
        }
        return new GridAsCodedResource(name, dggrs, base);
    }

    /**
     * View given ReferencedGridCoverage as a FeatureSet.
     */
    public static FeatureSet viewAsFeatureSet(CodedCoverage coverage, boolean idAsLong, String geometryType) {
        return new CodedCoverageAsFeatureSet(coverage, idAsLong, geometryType);
    }

}
