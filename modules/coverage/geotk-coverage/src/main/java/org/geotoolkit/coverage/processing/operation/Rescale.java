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

import org.apache.sis.measure.NumberRange;
import org.geotoolkit.coverage.processing.OperationJAI;


/**
 * Maps the sample values of a coverage from one range to another range. The rescaling is done by
 * multiplying each sample value by one of a set of constants and then adding another constant to
 * the result of the multiplication. The destination sample values are defined by the pseudocode:
 *
 * {@preformat java
 *     dst[x][y][b] = src[x][y][b] * constant + offset
 * }
 *
 * <P><b>Name:</b>&nbsp;<CODE>"Rescale"</CODE><BR>
 *    <b>JAI operator:</b>&nbsp;<CODE>"{@linkplain javax.media.jai.operator.RescaleDescriptor Rescale}"</CODE><BR>
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
 *     <td><code>double[]</code></td>
 *     <td align="center">1.0</td>
 *     <td align="center">N/A</td>
 *     <td align="center">N/A</td>
 *   </tr>
 *   <tr>
 *     <td>{@code "offsets"}</td>
 *     <td><code>double[]</code></td>
 *     <td align="center">0.0</td>
 *     <td align="center">N/A</td>
 *     <td align="center">N/A</td>
 *   </tr>
 * </table>
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @see org.geotoolkit.coverage.processing.Operations#rescale
 * @see javax.media.jai.operator.RescaleDescriptor
 *
 * @since 2.2
 * @module
 *
 * @todo Should operates on {@code sampleToGeophysics} transform when possible.
 *       See <A HREF="http://jira.codehaus.org/browse/GEOT-610">GEOT-610</A>.
 */
public class Rescale extends OperationJAI {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -9150531690336265741L;

    /**
     * Constructs a default {@code "Rescale"} operation.
     */
    public Rescale() {
        super("Rescale");
    }

    /**
     * Returns the expected range of values for the resulting image.
     */
    @Override
    protected NumberRange<?> deriveRange(final NumberRange<?>[] ranges, final Parameters parameters) {
        final double[] constants = (double[]) parameters.parameters.getObjectParameter("constants");
        final double[] offsets   = (double[]) parameters.parameters.getObjectParameter("offsets");
        if (constants.length == 1) {
            final double c = constants[0];
            final double t = offsets[0];
            final NumberRange<?> range = ranges[0];
            final double min = range.getMinDouble() * c + t;
            final double max = range.getMaxDouble() * c + t;
            return (max < min) ? NumberRange.create(max, true, min, true) : NumberRange.create(min, true, max, true);
        }
        return super.deriveRange(ranges, parameters);
    }
}
