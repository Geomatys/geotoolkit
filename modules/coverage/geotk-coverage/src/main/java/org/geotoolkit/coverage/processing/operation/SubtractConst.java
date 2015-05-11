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

import javax.media.jai.operator.SubtractConstDescriptor;

import org.apache.sis.measure.NumberRange;
import org.geotoolkit.coverage.processing.OperationJAI;


/**
 * Subtracts constants (one for each band) from every sample values of the source coverage.
 * If the number of constants supplied is less than the number of bands of the destination,
 * then the constant from entry 0 is applied to all the bands. Otherwise, a constant from a
 * different entry is applied to each band.
 *
 * <P><b>Name:</b>&nbsp;{@code "SubtractConst"}<BR>
 *    <b>JAI operator:</b>&nbsp;<CODE>"{@linkplain SubtractConstDescriptor SubtractConst}"</CODE><BR>
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
 *   <tr>
 *     <td>{@code "constants"}</td>
 *     <td>{@code double[]}</td>
 *     <td align="center">N/A</td>
 *     <td align="center">N/A</td>
 *     <td align="center">N/A</td>
 *   </tr>
 * </table>
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @see org.geotoolkit.coverage.processing.Operations#subtract
 * @see SubtractConstDescriptor
 *
 * @todo Should operates on {@code sampleToGeophysics} transform when possible.
 *       See <A HREF="http://jira.codehaus.org/browse/GEOT-610">GEOT-610</A>.
 *
 * @since 2.2
 * @module
 */
public class SubtractConst extends OperationJAI {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 279426577290256732L;

    /**
     * Constructs a default {@code "SubtractConst"} operation.
     */
    public SubtractConst() {
        super("SubtractConst");
    }

    /**
     * Returns the expected range of values for the resulting image.
     */
    @Override
    protected NumberRange<?> deriveRange(final NumberRange<?>[] ranges, final Parameters parameters) {
        final double[] constants = (double[]) parameters.parameters.getObjectParameter("constants");
        if (constants.length == 1) {
            final double c = constants[0];
            final NumberRange<?> range = ranges[0];
            final double min = range.getMinDouble() - c;
            final double max = range.getMaxDouble() - c;
            return NumberRange.create(min, true, max, true);
        }
        return super.deriveRange(ranges, parameters);
    }
}
