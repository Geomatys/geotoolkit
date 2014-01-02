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
import org.opengis.referencing.operation.Projection;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.OperationMethod;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


/**
 * A {@linkplain DefaultConversion conversion} transforming
 * (<var>longitude</var>,<var>latitude</var>) coordinates to Cartesian coordinates
 * (<var>x</var>,<var>y</var>).
 * <p>
 * An unofficial list of projections and their parameters can
 * be found <A HREF="http://www.remotesensing.org/geotiff/proj_list/">there</A>.
 * Most projections expect the following parameters:
 * <p>
 * <ul>
 *   <li>{@code "central_meridian"} (default to 0),
 *   <li>{@code "latitude_of_origin"} (default to 0),
 *   <li>{@code "scale_factor"} (default to 1),
 *   <li>{@code "false_easting"} (default to 0) and
 *   <li>{@code "false_northing"} (default to 0).
 * </ul>
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @see org.geotoolkit.referencing.crs.DefaultProjectedCRS
 * @see <A HREF="http://mathworld.wolfram.com/MapProjection.html">Map projections on MathWorld</A>
 *
 * @since 1.2
 * @module
 */
@Immutable
public class DefaultProjection extends DefaultConversion implements Projection {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -7176751851369816864L;

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
    public DefaultProjection(final Conversion                definition,
                             final CoordinateReferenceSystem sourceCRS,
                             final CoordinateReferenceSystem targetCRS,
                             final MathTransform             transform)
    {
        super(definition, sourceCRS, targetCRS, transform);
    }

    /**
     * Constructs a projection from a set of properties. The properties given in argument
     * follow the same rules than for the {@link AbstractCoordinateOperation} constructor.
     *
     * @param properties Set of properties. Should contains at least {@code "name"}.
     * @param sourceCRS  The source CRS, or {@code null} if not available.
     * @param targetCRS  The target CRS, or {@code null} if not available.
     * @param transform  Transform from positions in the {@linkplain #getSourceCRS source coordinate
     *                   reference system} to positions in the {@linkplain #getTargetCRS target
     *                   coordinate reference system}.
     * @param method     The operation method.
     */
    public DefaultProjection(final Map<String,?>             properties,
                             final CoordinateReferenceSystem sourceCRS,
                             final CoordinateReferenceSystem targetCRS,
                             final MathTransform             transform,
                             final OperationMethod           method)
    {
        super(properties, sourceCRS, targetCRS, transform, method);
    }

    /**
     * Returns the GeoAPI interface implemented by this class.
     * The default implementation returns {@code Projection.class}.
     * Subclasses implementing a more specific GeoAPI interface shall override this method.
     *
     * @return The conversion interface implemented by this class.
     */
    @Override
    public Class<? extends Projection> getInterface() {
        return Projection.class;
    }
}
