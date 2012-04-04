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
package org.geotoolkit.referencing.operation.provider;

import javax.media.jai.Warp;
import javax.media.jai.WarpCubic;
import javax.media.jai.WarpAffine;
import javax.media.jai.WarpQuadratic;
import javax.media.jai.WarpGeneralPolynomial;
import net.jcip.annotations.Immutable;

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.referencing.operation.Transformation;
import org.opengis.referencing.operation.MathTransform;

import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.referencing.NamedIdentifier;
import org.geotoolkit.referencing.operation.MathTransformProvider;
import org.geotoolkit.referencing.operation.transform.WarpTransform2D;

import static org.geotoolkit.parameter.Parameters.*;
import static org.geotoolkit.internal.referencing.Identifiers.createDescriptorGroup;


/**
 * The provider for {@link WarpTransform2D}. This provider constructs a JAI
 * {@linkplain javax.media.jai.WarpPolynomial} from a set of polynomial coefficients,
 * and wraps it in a {@link WarpTransform2D} object.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.1
 * @module
 */
@Immutable
public class WarpPolynomial extends MathTransformProvider {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -7949539694656719923L;

    /**
     * The operation parameter descriptor for the
     * "{@link javax.media.jai.WarpPolynomial#getDegree degree}" parameter value.
     */
    public static final ParameterDescriptor<Integer> DEGREE = DefaultParameterDescriptor.create(
            "degree", 2, 1, WarpTransform2D.MAX_DEGREE);

    /**
     * The operation parameter descriptor for the
     * "{@link javax.media.jai.WarpPolynomial#getXCoeffs xCoeffs}" parameter value.
     */
    public static final ParameterDescriptor<float[]> X_COEFFS = new DefaultParameterDescriptor<float[]>(
            "xCoeffs", float[].class, null, null);

    /**
     * The operation parameter descriptor for the
     * "{@link javax.media.jai.WarpPolynomial#getYCoeffs yCoeffs}" parameter value.
     */
    public static final ParameterDescriptor<float[]> Y_COEFFS = new DefaultParameterDescriptor<float[]>(
            "yCoeffs", float[].class, null, null);

    /**
     * The operation parameter descriptor for the
     * "{@link javax.media.jai.WarpPolynomial#getPreScaleX preScaleX}" parameter value.
     */
    public static final ParameterDescriptor<Float> PRE_SCALE_X;

    /**
     * The operation parameter descriptor for the
     * "{@link javax.media.jai.WarpPolynomial#getPreScaleY preScaleY}" parameter value.
     */
    public static final ParameterDescriptor<Float> PRE_SCALE_Y;

    /**
     * The operation parameter descriptor for the
     * "{@link javax.media.jai.WarpPolynomial#getPostScaleX postScaleX}" parameter value.
     */
    public static final ParameterDescriptor<Float> POST_SCALE_X;

    /**
     * The operation parameter descriptor for the
     * "{@link javax.media.jai.WarpPolynomial#getPostScaleY postScaleY}" parameter value.
     */
    public static final ParameterDescriptor<Float> POST_SCALE_Y;
    static {
        final Float ONE = 1f;
        final Class<Float> type = Float.class;
        PRE_SCALE_X  = new DefaultParameterDescriptor<Float>( "preScaleX", null, type, ONE, false);
        PRE_SCALE_Y  = new DefaultParameterDescriptor<Float>( "preScaleY", null, type, ONE, false);
        POST_SCALE_X = new DefaultParameterDescriptor<Float>("postScaleX", null, type, ONE, false);
        POST_SCALE_Y = new DefaultParameterDescriptor<Float>("postScaleY", null, type, ONE, false);
    }

    /**
     * The parameters group.
     */
    public static final ParameterDescriptorGroup PARAMETERS = createDescriptorGroup(new NamedIdentifier[] {
            new NamedIdentifier(Citations.GEOTOOLKIT, "WarpPolynomial")
        }, null, new ParameterDescriptor<?>[] {
            DEGREE, X_COEFFS, Y_COEFFS, PRE_SCALE_X, PRE_SCALE_Y, POST_SCALE_X, POST_SCALE_Y
        });

    /**
     * Creates a provider for warp transforms.
     */
    public WarpPolynomial() {
        super(2, 2, PARAMETERS);
    }

    /**
     * Returns the operation type.
     */
    @Override
    public Class<Transformation> getOperationType() {
        return Transformation.class;
    }

    /**
     * Creates a warp transform from the specified group of parameter values.
     *
     * @param  values The group of parameter values.
     * @return The created math transform.
     * @throws ParameterNotFoundException if a required parameter was not found.
     */
    @Override
    protected MathTransform createMathTransform(final ParameterValueGroup values)
            throws ParameterNotFoundException
    {
        final int      degree   = integerValue(DEGREE, values);
        final float[] xCoeffs   = value(X_COEFFS,      values);
        final float[] yCoeffs   = value(Y_COEFFS,      values);
        final float   preScaleX = scale( PRE_SCALE_X,  values);
        final float   preScaleY = scale( PRE_SCALE_Y,  values);
        final float  postScaleX = scale(POST_SCALE_X,  values);
        final float  postScaleY = scale(POST_SCALE_Y,  values);
        final Warp warp;
        switch (degree) {
            case 1:  warp = new WarpAffine           (xCoeffs, yCoeffs, preScaleX, preScaleY, postScaleX, postScaleY); break;
            case 2:  warp = new WarpQuadratic        (xCoeffs, yCoeffs, preScaleX, preScaleY, postScaleX, postScaleY); break;
            case 3:  warp = new WarpCubic            (xCoeffs, yCoeffs, preScaleX, preScaleY, postScaleX, postScaleY); break;
            default: warp = new WarpGeneralPolynomial(xCoeffs, yCoeffs, preScaleX, preScaleY, postScaleX, postScaleY); break;
        }
        return WarpTransform2D.create(warp);
    }

    /**
     * Returns the parameter value for the specified operation parameter.
     *
     * @param  param The parameter to look for.
     * @param  group The parameter value group to search into.
     * @return The requested parameter value, or {@code 1} if none.
     */
    private static float scale(final ParameterDescriptor<Float> param,
                               final ParameterValueGroup group)
            throws ParameterNotFoundException
    {
        final Object value = value(param, group);
        return (value != null) ? ((Number) value).floatValue() : 1;
    }
}
