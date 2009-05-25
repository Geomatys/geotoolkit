/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2009, Open Source Geospatial Foundation (OSGeo)
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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.referencing.operation;

import java.util.Map;

import org.opengis.referencing.operation.Conversion;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.SingleOperation;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


/**
 * A single (not {@linkplain DefaultConcatenatedOperation concatenated}) coordinate operation.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.0
 * @module
 */
public class DefaultSingleOperation extends AbstractCoordinateOperation implements SingleOperation {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = 672935231344137185L;

    /**
     * Constructs a new single operation with the same values than the specified defining
     * conversion, together with the specified source and target CRS. This constructor
     * is used by {@link DefaultConversion} only.
     */
    DefaultSingleOperation(final Conversion               definition,
                           final CoordinateReferenceSystem sourceCRS,
                           final CoordinateReferenceSystem targetCRS,
                           final MathTransform             transform)
    {
        super(definition, sourceCRS, targetCRS, transform);
    }

    /**
     * Constructs a single operation from a set of properties.
     * The properties given in argument follow the same rules than for the
     * {@linkplain AbstractCoordinateOperation#AbstractCoordinateOperation(Map,
     * CoordinateReferenceSystem, CoordinateReferenceSystem, MathTransform)
     * super-class constructor}.
     *
     * @param properties Set of properties. Should contains at least {@code "name"}.
     * @param sourceCRS  The source CRS.
     * @param targetCRS  The target CRS.
     * @param transform  Transform from positions in the {@linkplain #getSourceCRS source CRS}
     *                   to positions in the {@linkplain #getTargetCRS target CRS}.
     */
    public DefaultSingleOperation(final Map<String,?>             properties,
                                  final CoordinateReferenceSystem sourceCRS,
                                  final CoordinateReferenceSystem targetCRS,
                                  final MathTransform             transform)
    {
        super(properties, sourceCRS, targetCRS, transform);
    }
}
