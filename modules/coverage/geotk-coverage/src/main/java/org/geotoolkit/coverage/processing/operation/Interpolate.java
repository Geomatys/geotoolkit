/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.lang.reflect.Array;
import javax.media.jai.Interpolation;
import net.jcip.annotations.Immutable;

import org.opengis.coverage.Coverage;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterValueGroup;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.Interpolator2D;
import org.geotoolkit.coverage.processing.Operation2D;
import org.geotoolkit.metadata.Citations;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.internal.image.ImageUtilities;


/**
 * Specifies the interpolation type to be used to interpolate values for points which fall between
 * grid cells. The default value is nearest neighbor. The new interpolation type operates on all
 * sample dimensions. Possible values for type are: {@code "NearestNeighbor"}, {@code "Bilinear"}
 * and {@code "Bicubic"} (the {@code "Optimal"} interpolation type is currently not supported).
 *
 * {@section Geotoolkit.org extension}
 * The Geotk implementation provides two extensions to OpenGIS specification: First,
 * it accepts also an {@link Interpolation} argument type, for inter-operability with
 * <A HREF="http://java.sun.com/products/java-media/jai/">Java Advanced Imaging</A>.
 * Second, it accepts also an array of {@link String} or {@link Interpolation} objects.
 * When an array is specified, the first interpolation in the array is applied. If this
 * interpolation returns a {@code NaN} value, then the second interpolation is tried as a fallback.
 * If the second interpolation returns also a {@code NaN} value, then the third one is tried and so
 * on until an interpolation returns a real number or until we reach the end of interpolation list.
 * This behavior is convenient when processing remote sensing images of geophysics data, for example
 * <cite>Sea Surface Temperature</cite> (SST), in which clouds may mask many pixels (i.e. set them
 * to some {@code NaN} values). Because {@code "Bicubic"} interpolation needs 4&times;4 pixels while
 * {@code "Bilinear"} interpolation needs only 2x2 pixels, the {@code "Bilinear"} interpolation is
 * less likely to fails because of clouds ({@code NaN} values) than the {@code "Bicubic"} one
 * (note: only one {@code NaN} value is enough to make an interpolation fails). One can workaround
 * the problem by trying a bicubic interpolation first, then a linear interpolation if
 * {@code "Bicubic"} failed at a particular location, <i>etc.</i> This behavior can be
 * specified with the following {@code "Type"} argument:
 *
 * {@preformat java
 *     new String[] {"Bicubic", "Bilinear", "NearestNeighbor"}
 * }
 *
 * <P><b>Name:</b>&nbsp;{@code "Interpolate"}<BR>
 *    <b>JAI operator:</b>&nbsp;N/A<BR>
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
 *     <td>{@link GridCoverage2D}</td>
 *     <td align="center">N/A</td>
 *     <td align="center">N/A</td>
 *     <td align="center">N/A</td>
 *   </tr>
 *   <tr>
 *     <td>{@code "Type"}</td>
 *     <td>{@link CharSequence}</td>
 *     <td>"NearestNeighbor"</td>
 *     <td align="center">N/A</td>
 *     <td align="center">N/A</td>
 *   </tr>
 * </table>
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @see org.geotoolkit.coverage.processing.Operations#interpolate
 * @see Interpolator2D
 *
 * @since 2.2
 * @module
 */
@Immutable
public class Interpolate extends Operation2D {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 6742127682733620661L;

    /**
     * The parameter descriptor for the interpolation type. Values should be
     * either a {@link String} or an {@link Interpolation} object.
     */
    public static final ParameterDescriptor<Object> TYPE =
            new DefaultParameterDescriptor<>(Citations.OGC, "Type",
                Object.class,       // Value class (mandatory)
                null,               // Array of valid values
                "NearestNeighbor",  // Default value
                null,               // Minimal value
                null,               // Maximal value
                null,               // Unit of measure
                true);              // Parameter is mandatory

    /**
     * Constructs an {@code "Interpolate"} operation.
     */
    public Interpolate() {
        super(new DefaultParameterDescriptorGroup(Citations.OGC, "Interpolate", SOURCE_0, TYPE));
    }

    /**
     * Returns {@link ViewType#SAME} as the preferred view for computation purpose.
     */
    @Override
    protected ViewType getComputationView(final ParameterValueGroup parameters) {
        return ViewType.SAME;
    }

    /**
     * Applies an interpolation to a grid coverage. This method is invoked by
     * {@link org.geotoolkit.coverage.processing.DefaultCoverageProcessor} for
     * the {@code "Interpolate"} operation.
     */
    @Override
    protected Coverage doOperation(final ParameterValueGroup parameters, final Hints hints) {
        final GridCoverage2D[] sources = new GridCoverage2D[1];
        final ViewType      targetView = extractSources(parameters, sources);
        final GridCoverage2D    source = sources[0];
        final Object type = parameters.parameter("Type").getValue();
        final Interpolation[] interpolations;
        if (type.getClass().isArray()) {
            interpolations = new Interpolation[Array.getLength(type)];
            for (int i=0; i<interpolations.length; i++) {
                interpolations[i] = ImageUtilities.toInterpolation(Array.get(type, i));
            }
        } else {
            interpolations = new Interpolation[] {ImageUtilities.toInterpolation(type)};
        }
        return Interpolator2D.create(source, interpolations).view(targetView);
    }
}
