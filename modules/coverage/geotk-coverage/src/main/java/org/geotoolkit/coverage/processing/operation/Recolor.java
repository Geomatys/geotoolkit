/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2006-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.awt.Color;
import net.jcip.annotations.Immutable;

import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterValueGroup;

import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.processing.ColorMap;
import org.geotoolkit.coverage.processing.IndexColorOperation;
import org.geotoolkit.metadata.Citations;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;


/**
 * Replaces the colors of a {@link org.geotoolkit.coverage.grid.GridCoverage2D}. This operation
 * accepts one argument, {@code ColorMaps}, which must be an instance of {@link ColorMap}.
 *
 * <P><b>Name:</b>&nbsp;<CODE>"Recolor"</CODE><BR>
 *    <b>JAI operator:</b>&nbsp;none<BR>
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
 *     <td>{@code "ColorMaps"}</td>
 *     <td><code>{@linkplain ColorMap}[]</code></td>
 *     <td align="center">A gray scale</td>
 *     <td align="center">N/A</td>
 *     <td align="center">N/A</td>
 *   </tr>
 * </table>
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 1.2
 * @module
 */
@Immutable
public class Recolor extends IndexColorOperation {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 915698753323176492L;

    /**
     * The parameter descriptor for the color map.
     */
    public static final ParameterDescriptor<ColorMap[]> COLOR_MAPS = new DefaultParameterDescriptor<>(
            Citations.GEOTOOLKIT, "ColorMaps",
            ColorMap[].class, // Value class (mandatory)
            null,             // Array of valid values
            new ColorMap[] {  // Default value - a gray scale
                new ColorMap(new Color(16, 16, 16), new Color(240, 240, 240))
            },
            null,   // Minimal value
            null,   // Maximal value
            null,   // Unit of measure
            true);  // Parameter is mandatory

    /**
     * Constructs a new "Recolor" operation.
     */
    public Recolor() {
        super(new DefaultParameterDescriptorGroup(Citations.GEOTOOLKIT, "Recolor", SOURCE_0, COLOR_MAPS));
    }

    /**
     * Transforms the supplied RGB colors.
     *
     * @see ColorMap#recolor
     */
    @Override
    protected GridSampleDimension transformColormap(final int[] ARGB, final int band,
            final GridSampleDimension sampleDimension, final ParameterValueGroup parameters)
    {
        final Object[] colorMaps = (Object[]) parameters.parameter("ColorMaps").getValue();
        if (colorMaps == null || colorMaps.length == 0) {
            return sampleDimension;
        }
        Object colorMap = colorMaps[Math.min(band, colorMaps.length - 1)];
        return ((ColorMap) colorMap).recolor(sampleDimension, ARGB);
    }
}
