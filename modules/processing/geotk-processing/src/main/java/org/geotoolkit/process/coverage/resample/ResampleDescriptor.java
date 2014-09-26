/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.process.coverage.resample;

import java.util.HashMap;
import java.util.Map;

import org.apache.sis.util.iso.ResourceInternationalString;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.metadata.Citations;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.process.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.coverage.CoverageProcessingRegistry;
import org.apache.sis.referencing.NamedIdentifier;
import org.opengis.coverage.Coverage;
import org.opengis.coverage.grid.GridGeometry;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Resample a grid coverage using a different grid geometry. This operation provides the following
 * functionality:
 * <p>
 * <UL>
 *   <LI><b>Resampling</b><br>
 *       The grid coverage can be resampled at a different cell resolution. Some implementations
 *       may be able to do resampling efficiently at any resolution. Also a non-rectilinear grid
 *       coverage can be accessed as rectilinear grid coverage with this operation.</LI>
 *   <LI><b>Reprojecting</b><br>
 *       The new grid geometry can have a different coordinate reference system than the underlying
 *       grid geometry. For example, a grid coverage can be reprojected from a geodetic coordinate
 *       reference system to Universal Transverse Mercator CRS.</LI>
 *   <LI><b>Subsetting</b><br>
 *       A subset of a grid can be viewed as a separate coverage by using this operation with a
 *       grid geometry which as the same geoferencing and a region. Grid envelope in the grid
 *       geometry defines the region to subset in the grid coverage.</LI>
 * </UL>
 *
 * <P><b>Name:</b>&nbsp;{@code "Resample"}<BR>
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
 *     <td>{@code "InterpolationType"}</td>
 *     <td>{@link java.lang.CharSequence}</td>
 *     <td>"NearestNieghbor"</td>
 *     <td align="center">N/A</td>
 *     <td align="center">N/A</td>
 *   </tr>
 *   <tr>
 *     <td>{@code "CoordinateReferenceSystem"}</td>
 *     <td>{@link org.opengis.referencing.crs.CoordinateReferenceSystem}</td>
 *     <td>Same as source grid coverage</td>
 *     <td align="center">N/A</td>
 *     <td align="center">N/A</td>
 *   </tr>
 *   <tr>
 *     <td>{@code "GridGeometry"}</td>
 *     <td>{@link org.opengis.coverage.grid.GridGeometry}</td>
 *     <td>(automatic)</td>
 *     <td align="center">N/A</td>
 *     <td align="center">N/A</td>
 *   </tr>
 *   <tr>
 *     <td>{@code "Background"}</td>
 *     <td>{@code double[]}</td>
 *     <td>(automatic)</td>
 *     <td align="center">N/A</td>
 *     <td align="center">N/A</td>
 *   </tr>
 * </table>
 *
 * {@section Geotoolkit.org extension}
 * The {@code "Resample"} operation use the default
 * {@link org.opengis.referencing.operation.CoordinateOperationFactory} for creating a
 * transformation from the source to the destination coordinate reference systems.
 * If a custom factory is desired, it may be supplied as a rendering hint with the
 * {@link org.geotoolkit.factory.Hints#COORDINATE_OPERATION_FACTORY} key. Rendering
 * hints can be supplied to {@link org.geotoolkit.coverage.processing.DefaultCoverageProcessor}
 * at construction time.
 * <p>
 * Geotk adds a background parameter which is not part of OGC specification. This parameter
 * specifies the color to use for pixels in the destination image that don't map to a pixel
 * in the source image. If this parameter is not specified, then it is inferred from the
 * "no data" category in the source coverage.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Johann Sorel (Geomatys)
 * 
 * @version 4.x
 * @since 2.2
 * 
 * @module
 */
public class ResampleDescriptor extends AbstractProcessDescriptor {
    
    public static final String NAME = "Resample";

    /**
     * Convenience constant for the first source {@link GridCoverage2D}. The parameter name
     * is {@code "Source"} (as specified in OGC implementation specification) and the alias
     * is {@code "source0"} (for compatibility with <cite>Java Advanced Imaging</cite>).
     */
    public static final ParameterDescriptor<GridCoverage2D> IN_COVERAGE;
    
    /**
     * The parameter descriptor for the interpolation type.
     */
    public static final ParameterDescriptor<InterpolationCase> IN_INTERPOLATION_TYPE =
            new DefaultParameterDescriptor<InterpolationCase>(Citations.OGC,
                "InterpolationType",                // Parameter name
                InterpolationCase.class,            // Value class (mandatory)
                null,                               // Array of valid values
                InterpolationCase.NEIGHBOR,         // Default value
                null,                               // Minimal value
                null,                               // Maximal value
                null,                               // Unit of measure
                false);                             // Parameter is optional

    /**
     * The parameter descriptor for the coordinate reference system.
     */
    public static final ParameterDescriptor<CoordinateReferenceSystem> IN_COORDINATE_REFERENCE_SYSTEM =
            new DefaultParameterDescriptor<CoordinateReferenceSystem>(Citations.OGC,
                "CoordinateReferenceSystem",        // Parameter name
                CoordinateReferenceSystem.class,    // Value class (mandatory)
                null,                               // Array of valid values
                null,                               // Default value
                null,                               // Minimal value
                null,                               // Maximal value
                null,                               // Unit of measure
                false);                             // Parameter is optional

    /**
     * The parameter descriptor for the grid geometry.
     */
    public static final ParameterDescriptor<GridGeometry> IN_GRID_GEOMETRY =
            new DefaultParameterDescriptor<GridGeometry>(Citations.OGC,
                "GridGeometry",                     // Parameter name
                GridGeometry.class,                 // Value class (mandatory)
                null,                               // Array of valid values
                null,                               // Default value
                null,                               // Minimal value
                null,                               // Maximal value
                null,                               // Unit of measure
                false);                             // Parameter is optional

    /**
     * The parameter descriptor for the background values.
     *
     * @since 3.16
     */
    public static final ParameterDescriptor<double[]> IN_BACKGROUND =
            new DefaultParameterDescriptor<double[]>(Citations.GEOTOOLKIT,
                "Background",                       // Parameter name
                double[].class,                     // Value class (mandatory)
                null,                               // Array of valid values
                null,                               // Default value
                null,                               // Minimal value
                null,                               // Maximal value
                null,                               // Unit of measure
                false);                             // Parameter is optional

    /**
     * Input parameters descriptor of this process.
     */
    public static final ParameterDescriptorGroup INPUT_DESC;    
    
    /**
     * Output coverage result of the process execution.
     */
    public static final ParameterDescriptor<Coverage> OUT_COVERAGE;
    
    /**
     * Output parameters descriptor of this process.
     */
    public static final ParameterDescriptorGroup OUTPUT_DESC;
    
    static {
        final Map<String,Object> properties = new HashMap<>(4);
        properties.put(IdentifiedObject.NAME_KEY,  new NamedIdentifier(Citations.OGC, "Source"));
        properties.put(IdentifiedObject.ALIAS_KEY, new NamedIdentifier(Citations.JAI, "source0"));
        IN_COVERAGE = new DefaultParameterDescriptor<>(properties, GridCoverage2D.class,
                        null, null, null, null, null, true);
        
        final String BUNDLE_PATH = "org/geotoolkit/process/coverage/bundle";
        
        INPUT_DESC = new DefaultParameterDescriptorGroup(NAME + "InputParameters", 
                IN_COVERAGE, IN_INTERPOLATION_TYPE, IN_COORDINATE_REFERENCE_SYSTEM, IN_GRID_GEOMETRY, IN_BACKGROUND);

        final Map<String, Object> propertiesOut = new HashMap<>();
        propertiesOut.put(IdentifiedObject.NAME_KEY, "result");
        propertiesOut.put(IdentifiedObject.ALIAS_KEY, new ResourceInternationalString(BUNDLE_PATH, "resample.outCoverage"));
        propertiesOut.put(IdentifiedObject.REMARKS_KEY, new ResourceInternationalString(BUNDLE_PATH, "resample.outCoverageDesc"));
        OUT_COVERAGE = new DefaultParameterDescriptor<>(
                propertiesOut, Coverage.class, null, null, null, null, null, true);

        OUTPUT_DESC  = new DefaultParameterDescriptorGroup(NAME + "OutputParameters", OUT_COVERAGE);
    }

    /**
     * Unique instance of this descriptor.
     */
    public static final ProcessDescriptor INSTANCE = new ResampleDescriptor();

    private ResampleDescriptor() {
        super(NAME, CoverageProcessingRegistry.IDENTIFICATION, 
                new SimpleInternationalString("Resample a coverage."), 
                INPUT_DESC, 
                OUTPUT_DESC);
    }

    @Override
    public Process createProcess(ParameterValueGroup input) {
        return new ResampleProcess(input);
    }
        
}
