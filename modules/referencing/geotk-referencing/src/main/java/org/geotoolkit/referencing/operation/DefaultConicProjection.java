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

import org.opengis.referencing.operation.Conversion;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.OperationMethod;
import org.opengis.referencing.operation.ConicProjection;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


/**
 * Base class for conical map projections.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @see org.geotoolkit.referencing.crs.DefaultProjectedCRS
 * @see <A HREF="http://mathworld.wolfram.com/ConicProjection.html">Conic projection on MathWorld</A>
 *
 * @since 2.1
 * @module
 */
@Immutable
public class DefaultConicProjection extends DefaultProjection implements ConicProjection {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -8717453834398763963L;

    /**
     * Constructs a new projection with the same values than the specified one, together with the
     * specified source and target CRS. While the source conversion can be an arbitrary one, it is
     * typically a {@linkplain DefiningConversion defining conversion}.
     *
     * @param definition The defining conversion.
     * @param sourceCRS  The source CRS.
     * @param targetCRS  The target CRS.
     * @param transform  Transform from positions in the {@linkplain #getSourceCRS source CRS}
     *                   to positions in the {@linkplain #getTargetCRS target CRS}.
     */
    public DefaultConicProjection(final Conversion                definition,
                                  final CoordinateReferenceSystem sourceCRS,
                                  final CoordinateReferenceSystem targetCRS,
                                  final MathTransform             transform)
    {
        super(definition, sourceCRS, targetCRS, transform);
    }

    /**
     * Constructs a projection from a set of properties.
     * The properties given in argument follow the same rules than for the
     * {@linkplain AbstractCoordinateOperation#AbstractCoordinateOperation(Map,
     * CoordinateReferenceSystem, CoordinateReferenceSystem, MathTransform)
     * base-class constructor}.
     *
     * @param properties Set of properties. Should contains at least {@code "name"}.
     * @param sourceCRS  The source CRS, or {@code null} if not available.
     * @param targetCRS  The target CRS, or {@code null} if not available.
     * @param transform  Transform from positions in the {@linkplain #getSourceCRS source coordinate
     *                   reference system} to positions in the {@linkplain #getTargetCRS target
     *                   coordinate reference system}.
     * @param method     The operation method.
     */
    public DefaultConicProjection(final Map<String,?>             properties,
                                  final CoordinateReferenceSystem sourceCRS,
                                  final CoordinateReferenceSystem targetCRS,
                                  final MathTransform             transform,
                                  final OperationMethod           method)
    {
        super(properties, sourceCRS, targetCRS, transform, method);
    }

    /**
     * Returns the GeoAPI interface implemented by this class.
     * The SIS implementation returns {@code ConicProjection.class}.
     *
     * {@note Subclasses usually do not need to override this method since GeoAPI does not define
     *        <code>ConicProjection</code> sub-interface. Overriding possibility is left mostly for
     *        implementors who wish to extend GeoAPI with their own set of interfaces.}
     *
     * @return {@code ConicProjection.class} or a user-defined sub-interface.
     */
    @Override
    public Class<? extends ConicProjection> getInterface() {
        return ConicProjection.class;
    }
}
