/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009 - 2012, Geomatys
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
package org.geotoolkit.process.coverage.pyramid;

import java.awt.Dimension;
import java.util.Map;
import org.geotoolkit.coverage.CoverageStore;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.process.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.coverage.CoverageProcessingRegistry;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.coverage.CoverageReference;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Define input and output objects necessary to {@link PyramidProcess}.
 *
 * @author Remi Marechal (Geomatys).
 */
public class PyramidDescriptor extends AbstractProcessDescriptor{

    public static final String NAME = "coveragepyramid";

                                    /*INPUT*/
    /**************************************************************************/
    /**
     * Input coverage which will be resample.
     */
    public static final ParameterDescriptor<CoverageReference> IN_COVERAGEREF =
            new DefaultParameterDescriptor<>("coverageref",
            "Coverage which will be tile.", CoverageReference.class, null, true);

    /**
     * Coverage store within pyramid will be store.
     */
    public static final ParameterDescriptor<CoverageStore> IN_COVERAGESTORE =
            new DefaultParameterDescriptor<>("in_coverage_store",
            "Coverage store within tiled coverage is stored.", CoverageStore.class, null, true);

    /**
     * Tile size of pyramid mosaic.
     */
    public static final ParameterDescriptor<Dimension> IN_TILE_SIZE =
            new DefaultParameterDescriptor<>("tile_size",
            "Tile size of mosaic.", Dimension.class, null, false);

    /**
     * Pixel interpolation type used during resampling.
     */
    public static final ParameterDescriptor<InterpolationCase> IN_INTERPOLATIONCASE =
            new DefaultParameterDescriptor<>("interpolation_type",
            "Tile size of mosaic.", InterpolationCase.class, null, true);

    /**
     * Pyramid name in database.
     */
    public static final ParameterDescriptor<String> IN_PYRAMID_NAME =
            new DefaultParameterDescriptor<>("pyramid_name",
            "Tile size of mosaic.", String.class, null, true);

    /**
     * Pixel scale associate at each geographic envelope.
     */
    public static final ParameterDescriptor<Map> IN_RES_PER_ENVELOPE =
            new DefaultParameterDescriptor<>("resolution_per_envelope",
            "Define different resample scales for each envelope.", Map.class, null, true);

    /**
     * Double table value used during interpolation if pixel coordinates are out of source image boundary.
     */
    public static final ParameterDescriptor<double[]> IN_FILLVALUES =
            new DefaultParameterDescriptor<>("fillvalue",
            "Contains value use when pixel transformation is out of source image boundary during resampling.", double[].class, null, false);

    /**
     * Double table value used during interpolation if pixel coordinates are out of source image boundary.
     */
    public static final ParameterDescriptor<Boolean> IN_REUSETILES =
            new DefaultParameterDescriptor<>("reuseTile",
                    "Optional flat that activate the re-using of input CoverageStore tiles if exist instead of override them.", Boolean.class, false, false);


    /**************************************************************************/

                                      /*OUTPUT*/
    /**************************************************************************/
    /**
     * FeatureStore where pyramid is stored.
     */
    public static final ParameterDescriptor<CoverageStore> OUT_COVERAGESTORE =
            new DefaultParameterDescriptor<>("out_coverage_store",
            "Coverage store within tiled coverage is stored.", CoverageStore.class, null, true);
    /**************************************************************************/

                                      /*GROUP*/
    /**************************************************************************/
    //Input group
    public static final ParameterDescriptorGroup INPUT_DESC =
            new DefaultParameterDescriptorGroup(NAME+"InputParameters",
                IN_COVERAGEREF, IN_COVERAGESTORE, IN_PYRAMID_NAME, IN_TILE_SIZE, IN_INTERPOLATIONCASE, IN_RES_PER_ENVELOPE, IN_FILLVALUES, IN_REUSETILES);
    //Output group
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new DefaultParameterDescriptorGroup(NAME+"OutputParameters", OUT_COVERAGESTORE);
    /**************************************************************************/

    public static final ProcessDescriptor INSTANCE = new PyramidDescriptor();

    private PyramidDescriptor(){
        super(NAME, CoverageProcessingRegistry.IDENTIFICATION,
                new SimpleInternationalString("Create a pyramid/mosaic from the given"
                + "coverage. Created tiles are stored in the given Coverage store."),
                INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public Process createProcess(ParameterValueGroup input) {
        return new PyramidProcess(input);
    }
}
