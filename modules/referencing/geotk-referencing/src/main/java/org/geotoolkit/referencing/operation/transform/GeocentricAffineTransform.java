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
package org.geotoolkit.referencing.operation.transform;

import net.jcip.annotations.Immutable;

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.referencing.crs.GeocentricCRS;
import org.opengis.referencing.operation.Matrix;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.parameter.FloatParameter;
import org.geotoolkit.parameter.ParameterGroup;
import org.apache.sis.referencing.datum.BursaWolfParameters;
import org.geotoolkit.referencing.operation.provider.CoordinateFrameRotation;
import org.geotoolkit.referencing.operation.provider.GeocentricTranslation;
import org.geotoolkit.referencing.operation.provider.PositionVector7Param;
import org.apache.sis.referencing.operation.matrix.Matrices;
import org.apache.sis.referencing.operation.transform.LinearTransform;
import org.apache.sis.referencing.operation.transform.ProjectiveTransform_tmp;
import org.apache.sis.util.ComparisonMode;

import static org.geotoolkit.util.Utilities.hash;


/**
 * An affine transform applied on {@linkplain GeocentricCRS geocentric} coordinates.
 * This transform is a step in a <cite>datum shift</cite> transformation chain. It is
 * typically used for geocentric translation, but a rotation can also be applied.
 * <p>
 * This transform is used for the following operations:
 * <p>
 * <table border="1">
 *   <tr><th>EPSG name</th>                               <th>EPSG code</th></tr>
 *   <tr><td>Geocentric translations</td>                 <td>9603</td></tr>
 *   <tr><td>Position Vector 7-param. transformation</td> <td>9606</td></tr>
 *   <tr><td>Coordinate Frame rotation</td>               <td>9607</td></tr>
 * </table>
 * <p>
 * See any of the following providers for a list of programmatic parameters:
 * <p>
 * <ul>
 *   <li>{@link org.geotoolkit.referencing.operation.provider.PositionVector7Param}</li>
 *   <li>{@link org.geotoolkit.referencing.operation.provider.GeocentricTranslation}</li>
 *   <li>{@link org.geotoolkit.referencing.operation.provider.CoordinateFrameRotation}</li>
 * </ul>
 * <p>
 * The conversion between geographic and geocentric coordinates is usually <strong>not</strong>
 * part of this transform. However the Geotk implementation accepts the following extensions:
 * <p>
 * <ul>
 *   <li>If {@code "src_semi_major"} and {@code "src_semi_minor"} parameters are provided, then
 *       a {@code "Ellipsoid_To_Geocentric"} transform is concatenated before this transform.</li>
 *   <li>If {@code "tgt_semi_major"} and {@code "tgt_semi_minor"} parameters are provided, then
 *       a {@code "Geocentric_To_Ellipsoid"} transform is concatenated after this transform.</li>
 * </ul>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.18
 *
 * @since 2.2
 * @module
 */
@Immutable
public class GeocentricAffineTransform extends ProjectiveTransform_tmp {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -3588786513463289242L;

    /**
     * The transform type.
     */
    private static final byte TRANSLATION=1, SEVEN_PARAM=2, FRAME_ROTATION=3;

    /**
     * The transform type, as one of {@link #TRANSLATION}, {@link #SEVEN_PARAM}
     * or {@link #FRAME_ROTATION}. We stores a code of the type instead than a
     * reference to the parameter descriptor in order to avoid serialization of
     * a full {@link ParameterDescriptorGroup} object.
     */
    private final byte type;

    /**
     * Creates a new geocentric affine transform. If the parameters don't contain rotation terms,
     * then this transform will be of kind "<cite>Geocentric translations</cite>". Otherwise, it
     * will be of kind "<cite>Position Vector 7-param. transformation</cite>".
     *
     * @param parameters The Bursa-Wolf parameters to use for initializing the transformation.
     */
    public GeocentricAffineTransform(final BursaWolfParameters parameters) {
        super(parameters.getPositionVectorTransformation(null));
        type = parameters.isTranslation() ? TRANSLATION : SEVEN_PARAM;
    }

    /**
     * Creates a new geocentric affine transform using the specified parameter descriptor.
     *
     * @param parameters The Bursa-Wolf parameters to use for initializing the transformation.
     * @param descriptor The parameter descriptor.
     */
    public GeocentricAffineTransform(final BursaWolfParameters parameters,
                                     final ParameterDescriptorGroup descriptor)
    {
        super(parameters.getPositionVectorTransformation(null));
        final String name, value;
        if (GeocentricTranslation.PARAMETERS.equals(descriptor)) {
            if (parameters.isTranslation()) {
                type = TRANSLATION;
                return;
            }
            name  = "ex|ey|ez|ppm";
            value = "≠0";
        } else if (PositionVector7Param.PARAMETERS.equals(descriptor)) {
            type = SEVEN_PARAM;
            return;
        } else if (CoordinateFrameRotation.PARAMETERS.equals(descriptor)) {
            type = FRAME_ROTATION;
            return;
        } else {
            name = "descriptor";
            value = (descriptor != null) ? descriptor.getName().getCode() : null;
        }
        throw new IllegalArgumentException(Errors.format(Errors.Keys.ILLEGAL_PARAMETER_VALUE_2, name, value));
    }

    /**
     * Creates a new geocentric affine transform for the given type.
     */
    private GeocentricAffineTransform(final Matrix matrix, final byte type) {
        super(matrix);
        this.type = type;
    }

    /**
     * Returns the parameter descriptors for this math transform.
     */
    @Override
    public ParameterDescriptorGroup getParameterDescriptors() {
        switch (type) {
            case TRANSLATION:    return GeocentricTranslation   .PARAMETERS;
            case SEVEN_PARAM:    return PositionVector7Param    .PARAMETERS;
            case FRAME_ROTATION: return CoordinateFrameRotation .PARAMETERS;
            default: throw new AssertionError(type); // Should never happen.
        }
    }

    /**
     * Returns the parameters for this math transform.
     */
    @Override
    @SuppressWarnings("fallthrough")
    public ParameterValueGroup getParameterValues() {
        final BursaWolfParameters parameters = new BursaWolfParameters(null, null);
        parameters.setPositionVectorTransformation(getMatrix(), Double.POSITIVE_INFINITY);
        final FloatParameter[] param = new FloatParameter[type == TRANSLATION ? 3 : 7];
        switch (type) {
            default: {
                throw new AssertionError(type);
            }
            case FRAME_ROTATION: {
                parameters.reverseRotation();
                // Fall through
            }
            case SEVEN_PARAM: {
                param[3] = new FloatParameter(PositionVector7Param.EX,  parameters.rX);
                param[4] = new FloatParameter(PositionVector7Param.EY,  parameters.rY);
                param[5] = new FloatParameter(PositionVector7Param.EZ,  parameters.rZ);
                param[6] = new FloatParameter(PositionVector7Param.PPM, parameters.dS);
                // Fall through
            }
            case TRANSLATION: {
                param[0] = new FloatParameter(PositionVector7Param.DX,  parameters.tX);
                param[1] = new FloatParameter(PositionVector7Param.DY,  parameters.tY);
                param[2] = new FloatParameter(PositionVector7Param.DZ,  parameters.tZ);
                break;
            }
        }
        return new ParameterGroup(getParameterDescriptors(), param);
    }

    /**
     * Creates an inverse transform using the specified matrix.
     *
     * @deprecated To be removed from public API after the port to Apache SIS.
     */
    @Override
    protected final GeocentricAffineTransform createInverse(final Matrix matrix) {
        return new GeocentricAffineTransform(matrix, type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int computeHashCode() {
        return hash(type, super.computeHashCode());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) { // Slight optimization
            return true;
        }
        if (mode != ComparisonMode.STRICT) {
            if (object instanceof LinearTransform) {
                return Matrices.equals(this, ((LinearTransform) object).getMatrix(), mode);
            }
        } else if (super.equals(object, mode)) {
            return ((GeocentricAffineTransform) object).type == type;
        }
        return false;
    }
}
