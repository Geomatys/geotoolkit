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

import javax.media.jai.operator.InvertDescriptor;

import org.apache.sis.measure.NumberRange;
import org.geotoolkit.coverage.processing.OperationJAI;


/**
 * Inverts the sample values of a coverage. For source coverages with signed data types,
 * the sample values of the destination coverage are defined by the pseudocode:
 *
 * {@preformat java
 *     dst[x][y][b] = -src[x][y][b]
 * }
 *
 * <P><b>Name:</b>&nbsp;{@code "Invert"}<BR>
 *    <b>JAI operator:</b>&nbsp;<CODE>"{@linkplain InvertDescriptor Invert}"</CODE><BR>
 *    <b>Parameters:</b></P>
 * <table border='3' cellpadding='6' bgcolor='F4F8FF'>
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
 * @see org.geotoolkit.coverage.processing.Operations#invert
 * @see InvertDescriptor
 *
 * @since 2.2
 * @module
 */
public class Invert extends OperationJAI {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 7297641092994880308L;

    /**
     * Constructs a default {@code "Invert"} operation.
     */
    public Invert() {
        super("Invert");
    }

    /**
     * Returns the expected range of values for the resulting image.
     */
    @Override
    protected NumberRange<?> deriveRange(final NumberRange<?>[] ranges, final Parameters parameters) {
        final NumberRange<?> range = ranges[0];
        final double min = -range.getMaxDouble();
        final double max = -range.getMinDouble();
        return NumberRange.create(min, true, max, true);
    }
}
