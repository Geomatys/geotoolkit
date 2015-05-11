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
package org.geotoolkit.coverage.processing.operation;

import javax.media.jai.operator.AbsoluteDescriptor;

import org.apache.sis.measure.NumberRange;
import org.geotoolkit.coverage.processing.OperationJAI;


/**
 * Computes the mathematical absolute value of each sample value.
 *
 * <P><b>Name:</b>&nbsp;{@code "Absolute"}<BR>
 *    <b>JAI operator:</b>&nbsp;<CODE>"{@linkplain AbsoluteDescriptor Absolute}"</CODE><BR>
 *    <b>Parameters:</b></P>
 * <table border='3' cellpadding='6' bgcolor='#F4F8FF'>
 *   <tr bgcolor='#B9DCFF'>
 *     <th>Name</th>
 *     <th>Class</th>
 *     <th>Default value</th>
 *     <th>Minimum value</th>
 *     <th>Maximum value</th>
 *   </tr>
 *   <tr>
 *     <td>{@code "Source"}</td>
 *     <td>{@link org.geotoolkit.coverage.grid.GridCoverage2D}</td>
 *     <td align="center">N/A</td>
 *     <td align="center">N/A</td>
 *     <td align="center">N/A</td>
 *   </tr>
 * </table>
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @see org.geotoolkit.coverage.processing.Operations#absolute
 * @see AbsoluteDescriptor
 *
 * @since 2.2
 * @module
 */
public class Absolute extends OperationJAI {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 3723059532452772794L;

    /**
     * Constructs a default {@code "Absolute"} operation.
     */
    public Absolute() {
        super("Absolute");
    }

    /**
     * Returns the expected range of values for the resulting image.
     */
    @Override
    protected NumberRange<?> deriveRange(final NumberRange<?>[] ranges, final Parameters parameters) {
        final NumberRange<?> range = ranges[0];
        final double min = Math.abs(range.getMinDouble());
        final double max = Math.abs(range.getMaxDouble());
        return (max < min) ? NumberRange.create(max, true, min, true) : NumberRange.create(min, true, max, true);
    }
}
