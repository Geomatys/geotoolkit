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

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.referencing.operation.Transformation;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransformFactory;

import org.geotoolkit.metadata.Citations;
import org.apache.sis.referencing.NamedIdentifier;
import org.geotoolkit.referencing.operation.MathTransformProvider;
import org.geotoolkit.referencing.operation.transform.WarpTransform2D;

import org.apache.sis.parameter.ParameterBuilder;
import static org.geotoolkit.parameter.Parameters.*;
import static org.geotoolkit.referencing.operation.provider.UniversalParameters.createDescriptorGroup;


/**
 * The provider for {@link WarpTransform2D}. This provider constructs a JAI
 * {@linkplain javax.media.jai.WarpPolynomial} from a set of polynomial coefficients,
 * and wraps it in a {@link WarpTransform2D} object.
 *
 * <!-- PARAMETERS WarpPolynomial -->
 * <p>The following table summarizes the parameters recognized by this provider.
 * For a more detailed parameter list, see the {@link #PARAMETERS} constant.</p>
 * <blockquote><p><b>Operation name:</b> {@code WarpPolynomial}</p>
 * <table class="geotk">
 *   <tr><th>Parameter name</th><th>Default value</th></tr>
 *   <tr><td>{@code degree}</td><td>2</td></tr>
 *   <tr><td>{@code xCoeffs}</td><td></td></tr>
 *   <tr><td>{@code yCoeffs}</td><td></td></tr>
 *   <tr><td>{@code preScaleX}</td><td>1</td></tr>
 *   <tr><td>{@code preScaleY}</td><td>1</td></tr>
 *   <tr><td>{@code postScaleX}</td><td>1</td></tr>
 *   <tr><td>{@code postScaleY}</td><td>1</td></tr>
 * </table></blockquote>
 * <!-- END OF PARAMETERS -->
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @see <a href="{@docRoot}/../modules/referencing/operation-parameters.html">Geotk coordinate operations matrix</a>
 *
 * @since 2.1
 * @module
 */
public class WarpPolynomial extends MathTransformProvider {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -7949539694656719923L;

    /**
     * The operation parameter descriptor for the
     * "{@link javax.media.jai.WarpPolynomial#getDegree degree}" parameter value.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    public static final ParameterDescriptor<Integer> DEGREE;

    /**
     * The operation parameter descriptor for the
     * "{@link javax.media.jai.WarpPolynomial#getXCoeffs xCoeffs}" parameter value.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    public static final ParameterDescriptor<float[]> X_COEFFS;

    /**
     * The operation parameter descriptor for the
     * "{@link javax.media.jai.WarpPolynomial#getYCoeffs yCoeffs}" parameter value.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    public static final ParameterDescriptor<float[]> Y_COEFFS;

    /**
     * The operation parameter descriptor for the
     * "{@link javax.media.jai.WarpPolynomial#getPreScaleX preScaleX}" parameter value.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    public static final ParameterDescriptor<Float> PRE_SCALE_X;

    /**
     * The operation parameter descriptor for the
     * "{@link javax.media.jai.WarpPolynomial#getPreScaleY preScaleY}" parameter value.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    public static final ParameterDescriptor<Float> PRE_SCALE_Y;

    /**
     * The operation parameter descriptor for the
     * "{@link javax.media.jai.WarpPolynomial#getPostScaleX postScaleX}" parameter value.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    public static final ParameterDescriptor<Float> POST_SCALE_X;

    /**
     * The operation parameter descriptor for the
     * "{@link javax.media.jai.WarpPolynomial#getPostScaleY postScaleY}" parameter value.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    public static final ParameterDescriptor<Float> POST_SCALE_Y;
    static {
        final ParameterBuilder builder = new ParameterBuilder().setCodeSpace(Citations.GEOTOOLKIT, null);
        final Float ONE = 1f;
        final Class<Float> type = Float.class;
        PRE_SCALE_X  = builder.addName("preScaleX") .create(type, ONE);
        PRE_SCALE_Y  = builder.addName("preScaleY") .create(type, ONE);
        POST_SCALE_X = builder.addName("postScaleX").create(type, ONE);
        POST_SCALE_Y = builder.addName("postScaleY").create(type, ONE);
        builder.setRequired(true);
        X_COEFFS = builder.addName("xCoeffs").create(float[].class, null);
        Y_COEFFS = builder.addName("yCoeffs").create(float[].class, null);
        DEGREE = builder.addName("degree").createBounded(1, WarpTransform2D.MAX_DEGREE, 2);
    }

    /**
     * The group of all parameters expected by this coordinate operation.
     * The following table lists the operation names and the parameters recognized by Geotk:
     * <p>
     * <!-- GENERATED PARAMETERS - inserted by ProjectionParametersJavadoc -->
     * <table class="geotk" border="1">
     *   <tr><th colspan="2">
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>Geotk</code>:</td><td class="onleft"><code>WarpPolynomial</code></td></tr>
     *     </table>
     *   </th></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>Geotk</code>:</td><td class="onleft"><code>degree</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Integer}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
     *       <tr><td><b>Value range:</b></td><td>[1…7]</td></tr>
     *       <tr><td><b>Default value:</b></td><td>2</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>Geotk</code>:</td><td class="onleft"><code>xCoeffs</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code float[]}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>Geotk</code>:</td><td class="onleft"><code>yCoeffs</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code float[]}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>Geotk</code>:</td><td class="onleft"><code>preScaleX</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Float}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>optional</td></tr>
     *       <tr><td><b>Value range:</b></td><td>(-∞ … ∞)</td></tr>
     *       <tr><td><b>Default value:</b></td><td>1</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>Geotk</code>:</td><td class="onleft"><code>preScaleY</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Float}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>optional</td></tr>
     *       <tr><td><b>Value range:</b></td><td>(-∞ … ∞)</td></tr>
     *       <tr><td><b>Default value:</b></td><td>1</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>Geotk</code>:</td><td class="onleft"><code>postScaleX</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Float}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>optional</td></tr>
     *       <tr><td><b>Value range:</b></td><td>(-∞ … ∞)</td></tr>
     *       <tr><td><b>Default value:</b></td><td>1</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>Geotk</code>:</td><td class="onleft"><code>postScaleY</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Float}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>optional</td></tr>
     *       <tr><td><b>Value range:</b></td><td>(-∞ … ∞)</td></tr>
     *       <tr><td><b>Default value:</b></td><td>1</td></tr>
     *     </table>
     *   </td></tr>
     * </table>
     */
    public static final ParameterDescriptorGroup PARAMETERS = createDescriptorGroup(new NamedIdentifier[] {
            new NamedIdentifier(Citations.GEOTOOLKIT, "WarpPolynomial")
        }, null, new ParameterDescriptor<?>[] {
            DEGREE, X_COEFFS, Y_COEFFS, PRE_SCALE_X, PRE_SCALE_Y, POST_SCALE_X, POST_SCALE_Y
        }, 0);

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
    public MathTransform createMathTransform(MathTransformFactory factory, final ParameterValueGroup values)
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
