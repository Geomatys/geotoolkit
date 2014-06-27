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
import org.opengis.referencing.operation.MathTransform1D;
import org.apache.sis.measure.NumberRange;
import org.geotoolkit.coverage.Category;
import org.apache.sis.util.Exceptions;


/**
 * Offset, scale factor and transfer function type inferred from a given {@link MathTransform1D}.
 * This is used in order to split a {@link MathTransform1D} into the information that can be
 * inserted in the coverages database.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 4.0
 *
 * @since 3.13
 * @module
 */
public final class TransferFunction extends org.apache.sis.referencing.operation.transform.TransferFunction {
    /**
     * The minimum and maximum sample values, inclusive.
     */
    public final int minimum, maximum;

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
     * Extracts the transfer function from the given category.
     *
     * @todo Needs to handle the logarithmic case.
     *
     * @param category The category for which to get the transfer function type.
     * @param locale The locale to use for formatting error message, if any.
     */
    public TransferFunction(final Category category, final Locale locale) {
        final NumberRange<?> range = category.getRange();
        minimum = (int) Math.round(range.getMinDouble(true));
        maximum = (int) Math.round(range.getMaxDouble(true));
        MathTransform1D function = category.getSampleToGeophysics();
        if (function != null) {
            isQuantitative = true;
            isGeophysics = function.isIdentity();
            try {
                setTransform(function);
            } catch (IllegalArgumentException e) {
                warning = Exceptions.getLocalizedMessage(e, locale);
            }
        }
    }
}
