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
package org.geotoolkit.processing.coverage.pyramid;

import java.awt.Dimension;
import java.util.Map;
import org.apache.sis.parameter.ParameterBuilder;
import org.geotoolkit.storage.coverage.CoverageStore;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.processing.coverage.CoverageProcessingRegistry;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.storage.coverage.CoverageReference;
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
    public static final ParameterDescriptor<CoverageReference> IN_COVERAGEREF = new ParameterBuilder()
            .addName("coverageref")
            .setRemarks("Coverage which will be tile.")
            .setRequired(true)
            .create(CoverageReference.class, null);

    /**
     * Coverage store within pyramid will be store.
     */
    public static final ParameterDescriptor<CoverageStore> IN_COVERAGESTORE = new ParameterBuilder()
            .addName("in_coverage_store")
            .setRemarks("Coverage store within tiled coverage is stored.")
            .setRequired(true)
            .create(CoverageStore.class, null);

    /**
     * Tile size of pyramid mosaic.
     */
    public static final ParameterDescriptor<Dimension> IN_TILE_SIZE = new ParameterBuilder()
            .addName("tile_size")
            .setRemarks("Tile size of mosaic.")
            .setRequired(false)
            .create(Dimension.class, null);

    /**
     * Pixel interpolation type used during resampling.
     */
    public static final ParameterDescriptor<InterpolationCase> IN_INTERPOLATIONCASE = new ParameterBuilder()
            .addName("interpolation_type")
            .setRemarks("Tile size of mosaic.")
            .setRequired(true)
            .create(InterpolationCase.class, null);

    /**
     * Pyramid name in database.
     */
    public static final ParameterDescriptor<String> IN_PYRAMID_NAME = new ParameterBuilder()
            .addName("pyramid_name")
            .setRemarks("Tile size of mosaic.")
            .setRequired(true)
            .create(String.class, null);

    /**
     * Pixel scale associate at each geographic envelope.
     */
    public static final ParameterDescriptor<Map> IN_RES_PER_ENVELOPE =  new ParameterBuilder()
            .addName("resolution_per_envelope")
            .setRemarks("Define different resample scales for each envelope.")
            .setRequired(true)
            .create(Map.class, null);

    /**
     * Double table value used during interpolation if pixel coordinates are out of source image boundary.
     */
    public static final ParameterDescriptor<double[]> IN_FILLVALUES = new ParameterBuilder()
            .addName("fillvalue")
            .setRemarks("Contains value use when pixel transformation is out of source image boundary during resampling.")
            .setRequired(false)
            .create(double[].class, null);

    /**
     * Boolean flag that activate the re-using of input CoverageStore tiles.
     */
    public static final ParameterDescriptor<Boolean> IN_REUSETILES = new ParameterBuilder()
            .addName("reuse_tiles")
            .setRemarks("Optional flat that activate the re-using of input CoverageStore tiles if exist instead of override them.")
            .setRequired(false)
            .create(Boolean.class, false);

    
    /**************************************************************************/

                                      /*OUTPUT*/
    /**************************************************************************/
    /**
     * FeatureStore where pyramid is stored.
     */
    public static final ParameterDescriptor<CoverageStore> OUT_COVERAGESTORE = new ParameterBuilder()
            .addName("out_coverage_store")
            .setRemarks("Coverage store within tiled coverage is stored.")
            .setRequired(true)
            .create(CoverageStore.class, null);

    /**************************************************************************/

                                      /*GROUP*/
    /**************************************************************************/
    //Input group
    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName(NAME+"InputParameters").createGroup(
                IN_COVERAGEREF, IN_COVERAGESTORE, IN_PYRAMID_NAME, IN_TILE_SIZE, IN_INTERPOLATIONCASE, IN_RES_PER_ENVELOPE, IN_FILLVALUES, IN_REUSETILES);
    //Output group
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new ParameterBuilder().addName(NAME+"OutputParameters").createGroup(OUT_COVERAGESTORE);
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
