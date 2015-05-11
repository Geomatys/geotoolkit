/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1999-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.operation.provider;

import java.util.NoSuchElementException;

import org.opengis.util.GenericName;
import org.opengis.metadata.citation.Citation;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.referencing.operation.Projection;

import org.apache.sis.referencing.NamedIdentifier;
import org.geotoolkit.referencing.operation.MathTransformProvider;
import org.geotoolkit.metadata.Citations;

/*
 * Do not import UnitaryProjection, and do not use it neither except as fully-qualified names
 * only in javadoc comments. As of Java 6 update 10, using UnitaryProjection seems to confuse
 * javac when it tries to compile the Parameters nested class with protected access.  I guess
 * this is related to cyclic dependency, which is nice to avoid anyway.
 */


/**
 * The base provider for {@linkplain org.geotoolkit.referencing.operation.projection map projections}.
 * This base class defines the descriptors for the most commonly used parameters. Subclasses will
 * declare the parameters they use in a {@linkplain ParameterDescriptorGroup descriptor group}
 * named {@code PARAMETERS}.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @see <A HREF="http://mathworld.wolfram.com/MapProjection.html">Map projections on MathWorld</A>
 * @see <A HREF="http://atlas.gc.ca/site/english/learningresources/carto_corner/map_projections.html">Map projections on the atlas of Canada</A>
 * @see <a href="{@docRoot}/../modules/referencing/operation-parameters.html">Geotk coordinate operations matrix</a>
 *
 * @since 2.0
 * @module
 */
public abstract class MapProjection extends MathTransformProvider {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 6280666068007678702L;

    /**
     * The operation parameter descriptor for the {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#semiMajor
     * semi major} parameter value. Valid values range is (0 &hellip; &infin;). This parameter
     * is mandatory and has no default value.
     */
    static final ParameterDescriptor<Double> SEMI_MAJOR = UniversalParameters.SEMI_MAJOR;

    /**
     * The operation parameter descriptor for the {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#semiMinor
     * semi minor} parameter value. Valid values range is (0 &hellip; &infin;). This parameter
     * is mandatory and has no default value.
     */
    static final ParameterDescriptor<Double> SEMI_MINOR = UniversalParameters.SEMI_MINOR;

    /**
     * The operation parameter descriptor for the ESRI {@code "X_Scale"} parameter value.
     * Valid values range is unrestricted. This parameter is optional and its default value is 1.
     * <p>
     * This is an ESRI-specific parameter, but its usage could be extended to any projection.
     * The choice to allow this parameter or not is taken on a projection-by-projection basis.
     *
     * @since 3.00
     */
    static final ParameterDescriptor<Double> X_SCALE = UniversalParameters.X_SCALE;

    /**
     * The operation parameter descriptor for the ESRI {@code "Y_Scale"} parameter value.
     * Valid values range is unrestricted. This parameter is optional and its default value is 1.
     * <p>
     * This is an ESRI-specific parameter, but its usage could be extended to any projection.
     * The choice to allow this parameter or not is taken on a projection-by-projection basis.
     *
     * @since 3.00
     */
    static final ParameterDescriptor<Double> Y_SCALE = UniversalParameters.Y_SCALE;

    /**
     * The operation parameter descriptor for the ESRI {@code "XY_Plane_Rotation"} parameter value.
     * The rotation is applied before the <cite>false easting</cite> and <cite>false northing</cite>
     * translation, if any. Valid values range is [-360 &hellip; 360]&deg;. This parameter is
     * optional and its default value is 0&deg;.
     * <p>
     * This is an ESRI-specific parameter, but its usage could be extended to any projections.
     * The choice to allow this parameter or not is taken on a projection-by-projection basis.
     *
     * @since 3.00
     *
     * @deprecated Invoke <code>PARAMETERS.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    @Deprecated
    public static final ParameterDescriptor<Double> XY_PLANE_ROTATION =
            UniversalParameters.RECTIFIED_GRID_ANGLE.select(false, 0.0, new Citation[] {
                Citations.EPSG, Citations.OGC, Citations.NETCDF, Citations.GEOTIFF, Citations.PROJ4
            }, null);

    /**
     * Returns the name of the given authority declared in the given parameter descriptor.
     * This method is used only as a way to avoid creating many instances of the same name.
     */
    static NamedIdentifier sameNameAs(final Citation authority, final GeneralParameterDescriptor parameters) {
        for (final GenericName candidate : parameters.getAlias()) {
            if (candidate instanceof NamedIdentifier) {
                final NamedIdentifier name = (NamedIdentifier) candidate;
                if (name.getAuthority() == authority) {
                    return name;
                }
            }
        }
        throw new NoSuchElementException();
    }

    /**
     * Returns the parameters of the given name declared in the given group. This method
     * is used for sharing the same parameter instance across different map projections.
     */
    static ParameterDescriptor<?> sameParameterAs(final ParameterDescriptorGroup parameters, final String name) {
        return (ParameterDescriptor<?>) parameters.descriptor(name);
    }

    /**
     * Constructs a math transform provider from a set of parameters. The provider
     * {@linkplain #getIdentifiers identifiers} will be the same than the parameter
     * ones.
     *
     * @param parameters The set of parameters (never {@code null}).
     */
    protected MapProjection(final ParameterDescriptorGroup parameters) {
        super(2, 2, parameters);
    }

    /**
     * Returns the operation type for this map projection.
     */
    @Override
    public Class<? extends Projection> getOperationType() {
        return Projection.class;
    }

    /**
     * Creates a map projection from the specified group of parameter values.
     *
     * @param  values The group of parameter values.
     * @return The created map projection.
     * @throws ParameterNotFoundException if a required parameter was not found.
     */
    @Override
    public abstract MathTransform2D createMathTransform(MathTransformFactory factory, ParameterValueGroup values)
            throws ParameterNotFoundException;
}
