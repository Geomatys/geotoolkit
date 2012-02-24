/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.referencing.factory.epsg;

import java.util.Map;
import java.util.HashMap;

import org.opengis.util.FactoryException;
import org.opengis.referencing.AuthorityFactory;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.CoordinateOperationAuthorityFactory;

import org.geotoolkit.referencing.factory.IdentifiedObjectSet;


/**
 * A lazy set of {@link CoordinateOperation} objects to be returned by the
 * {@link DirectEpsgFactory#createFromCoordinateReferenceSystemCode
 * createFromCoordinateReferenceSystemCodes} method.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.2
 * @module
 */
final class CoordinateOperationSet extends IdentifiedObjectSet<CoordinateOperation> {
    /**
     * For compatibility with previous versions.
     */
    private static final long serialVersionUID = -2421669857023064667L;

    /**
     * The codes of {@link ProjectedCRS} objects for the specified {@link Conversion} codes,
     * or {@code null} if none.
     */
    private Map<String,String> projections;

    /**
     * Creates a new instance of this lazy set.
     */
    CoordinateOperationSet(final AuthorityFactory factory) {
        super(factory, CoordinateOperation.class);
    }

    /**
     * Adds the specified authority code.
     *
     * @param code The code for the {@link CoordinateOperation} to add.
     * @param crs  The code for the CRS is create instead of the operation, or {@code null} if none.
     */
    final boolean addAuthorityCode(final String code, final String crs) {
        if (crs != null) {
            if (projections == null) {
                projections = new HashMap<>();
            }
            projections.put(code, crs);
        }
        return super.addAuthorityCode(code);
    }

    /**
     * Creates an object for the specified code.
     */
    @Override
    protected CoordinateOperation createObject(final String code) throws FactoryException {
        if (projections != null) {
            final String crs = projections.get(code);
            if (crs != null) {
                final CRSAuthorityFactory factory = (CRSAuthorityFactory) getAuthorityFactory();
                return factory.createProjectedCRS(crs).getConversionFromBase();
            }
        }
        final CoordinateOperationAuthorityFactory factory = (CoordinateOperationAuthorityFactory) getAuthorityFactory();
        return factory.createCoordinateOperation(code);
    }
}
