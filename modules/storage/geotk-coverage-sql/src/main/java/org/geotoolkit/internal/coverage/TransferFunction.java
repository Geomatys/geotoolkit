/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.internal.coverage;

import java.util.Locale;

import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform1D;
import org.opengis.metadata.content.TransferFunctionType;

import org.apache.sis.measure.NumberRange;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.coverage.Category;
import org.geotoolkit.referencing.operation.MathTransforms;
import org.geotoolkit.referencing.operation.transform.LinearTransform;
import org.geotoolkit.referencing.operation.transform.ExponentialTransform1D;
import org.geotoolkit.referencing.operation.transform.LogarithmicTransform1D;


/**
 * Offset, scale factor and transfer function type inferred from a given {@link MathTransform1D}.
 * This is used in order to split a {@link MathTransform1D} into the information that can be
 * inserted in the coverages database.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.14
 *
 * @since 3.13
 * @module
 */
public final class TransferFunction {
    /**
     * The minimum and maximum sample values, inclusive.
     */
    public final int minimum, maximum;

    /**
     * The offset and scale factor.
     */
    public double offset, scale = 1;

    /**
     * The transfer function type, or {@code null} if unknown.
     */
    public TransferFunctionType type;

    /**
     * {@code true} if a transfer function exists.
     */
    public boolean isQuantitative;

    /**
     * {@code true} if a transfer function exists and is the identity transform.
     */
    public boolean isGeophysics;

    /**
     * If an error occurred while fetching the information, the error message.
     * Otherwise {@code null}.
     */
    public String warning;

    /**
     * The locale used for formatting error message.
     */
    private final Locale locale;

    /**
     * Extracts the transfer function from the given category.
     *
     * @todo Needs to handle the logarithmic case.
     *
     * @param category The category for which to get the transfer function type.
     * @param locale The locale to use for formatting error message, if any.
     */
    public TransferFunction(final Category category, final Locale locale) {
        this.locale = locale; // Must be before the call to any 'check' method.
        final NumberRange<?> range = category.getRange();
        minimum = (int) Math.round(range.getMinDouble(true));
        maximum = (int) Math.round(range.getMaxDouble(true));
        MathTransform1D function = category.getSampleToGeophysics();
        if (function != null) {
            isQuantitative = true;
            isGeophysics = function.isIdentity();
            if (function instanceof LinearTransform) {
                type = TransferFunctionType.LINEAR;
            } else {
                /*
                 * Maybe the function is exponential? Try to concatenate a
                 * logarithmic transform and check if the result is linear.
                 */
                MathTransform1D candidate = MathTransforms.concatenate(function, LogarithmicTransform1D.create(10));
                if (candidate instanceof LinearTransform) {
                    function = candidate;
                    type = TransferFunctionType.EXPONENTIAL;
                } else {
                    /*
                     * Maybe the function is logarithmic?
                     */
                    candidate = MathTransforms.concatenate(ExponentialTransform1D.create(10), function);
                    if (candidate instanceof LinearTransform) {
                        function = candidate;
                        type = TransferFunctionType.LOGARITHMIC;
                    }
                }
            }
            final LinearTransform linear = checkType(function, LinearTransform.class);
            if (linear != null) {
                final Matrix m = linear.getMatrix();
                scale  = m.getElement(0, 0);
                offset = m.getElement(0, m.getNumCol() - 1);
            }
        }
    }

    /**
     * Checks if the given transform is of the expected type.
     * If it is not, formats a warning message and return {@code null}.
     */
    private <E extends MathTransform> E checkType(final MathTransform tr, final Class<E> expected) {
        if (expected.isInstance(tr)) {
            return expected.cast(tr);
        }
        if (tr != null && warning == null) {
            warning = Errors.getResources(locale).getString(Errors.Keys.UNKNOWN_TYPE_1, tr.getClass());
        }
        return null;
    }
}
