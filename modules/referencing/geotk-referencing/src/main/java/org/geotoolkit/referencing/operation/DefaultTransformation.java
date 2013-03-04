/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.referencing.operation;

import java.util.Map;
import net.jcip.annotations.Immutable;

import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.Transformation;
import org.opengis.referencing.operation.OperationMethod;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import static org.apache.sis.util.ArgumentChecks.ensureNonNull;


/**
 * An operation on coordinates that usually includes a change of
 * {@linkplain org.opengis.referencing.datum.Datum datum}. The parameters of a coordinate
 * transformation are empirically derived from data containing the coordinates of a
 * series of points in both coordinate reference systems. This computational process
 * is usually "over-determined", allowing derivation of error (or accuracy) estimates
 * for the transformation. Also, the stochastic nature of the parameters may result
 * in multiple (different) versions of the same coordinate transformation.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @see DefaultConversion
 *
 * @since 2.0
 * @module
 */
@Immutable
public class DefaultTransformation extends DefaultSingleOperation implements Transformation {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -7486704846151648971L;

    /**
     * Constructs a transformation from a set of properties.
     * The properties given in argument follow the same rules than for the
     * {@linkplain AbstractCoordinateOperation#AbstractCoordinateOperation(Map,
     * CoordinateReferenceSystem, CoordinateReferenceSystem, MathTransform)
     * base-class constructor}.
     *
     * @param properties Set of properties. Should contains at least {@code "name"}.
     * @param sourceCRS  The source CRS.
     * @param targetCRS  The target CRS.
     * @param transform  Transform from positions in the {@linkplain #getSourceCRS source CRS}
     *                   to positions in the {@linkplain #getTargetCRS target CRS}.
     * @param method     The operation method.
     */
    public DefaultTransformation(final Map<String,?>             properties,
                                 final CoordinateReferenceSystem sourceCRS,
                                 final CoordinateReferenceSystem targetCRS,
                                 final MathTransform             transform,
                                 final OperationMethod           method)
    {
        super(properties, sourceCRS, targetCRS, transform, method);
        if (false) {
            // The EPSG database do not always defines an operation version.
            // Consequently, we relax the rule saying that version is mandatory.
            ensureNonNull(OPERATION_VERSION_KEY, operationVersion);
        }
    }
}
