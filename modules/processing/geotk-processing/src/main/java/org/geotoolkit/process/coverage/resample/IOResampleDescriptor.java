/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.process.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.coverage.CoverageProcessingRegistry;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.operation.MathTransform;

import javax.imageio.ImageReader;
import java.awt.*;
import java.io.File;

/**
 * Descriptor for a generic resample operation. It converts a given image source (given by {@link javax.imageio.ImageReader})
 * using a {@link org.opengis.referencing.operation.MathTransform} specified as input. The result is stored using the
 * {@link javax.imageio.ImageWriter} given as input.
 * The process can be streamed if the user specifies the appropriate parameter, and if the input reader and writer allow
 * tile reading.
 *
 * @author Alexis Manin (Geomatys)
 */
public class IOResampleDescriptor extends AbstractProcessDescriptor {

    public static final String NAME = "IOResample";

    public static final ParameterDescriptor<ImageReader> IN_COVERAGE;
    public static final ParameterDescriptor<MathTransform> OPERATOR;
    public static final ParameterDescriptor<String> INTERPOLATOR;
    public static final ParameterDescriptor<Integer> OUT_WIDTH;
    public static final ParameterDescriptor<Integer> OUT_HEIGHT;
    public static final ParameterDescriptor<String> OUT_LOC;
    public static final ParameterDescriptor<Integer> THREAD_COUNT;
    public static final ParameterDescriptor<Dimension> TILE_SIZE;

    public static final ParameterDescriptorGroup INPUT_DESC;

    public static final ParameterDescriptor<File> OUT_COVERAGE;

    public static final ParameterDescriptorGroup OUTPUT_DESC;

    static {
        ParameterBuilder builder = new ParameterBuilder();
        IN_COVERAGE = builder.addName("image").setRemarks("The image to resample").setRequired(true).create(ImageReader.class, null);
        OPERATOR = builder.addName("operation").setRemarks("The transformation to apply on source coverage.").setRequired(true).create(MathTransform.class, null);
        INTERPOLATOR = builder.addName("interpolation").setRemarks("The interpolation to apply on pixel transfer.").setRequired(true).create(String.class, "bilinear");
        OUT_WIDTH = builder.addName("width").setRemarks("The width to set for the result image.").setRequired(false).create(Integer.class, null);
        OUT_HEIGHT = builder.addName("height").setRemarks("The height to set for the result image.").setRequired(false).create(Integer.class, null);
        OUT_LOC = builder.addName("outputLocation").setRemarks("An absolute path to specify where target ortho-Image should be saved (future file path or its parent directory)."+
                " If this parameter is not specified, a temporary file is created.").setRequired(false).create(String.class, null);
        THREAD_COUNT = builder.addName("threadCount").setRemarks("The number of threads to use for orthorectification computing.").setRequired(false).create(Integer.class, null);
        TILE_SIZE = builder.addName("tileSize").setRemarks("The size of the tiles to generate for output image. If not specified, a default value will be set.").setRequired(false).create(Dimension.class, null);
        INPUT_DESC = builder.addName(NAME + ".input").createGroup(
                IN_COVERAGE,
                OPERATOR,
                INTERPOLATOR,
                OUT_WIDTH,
                OUT_HEIGHT,
                OUT_LOC,
                THREAD_COUNT,
                TILE_SIZE);


        OUT_COVERAGE = builder.addName("image").setRemarks("The image to store the resampled result in.").setRequired(true).create(File.class, null);

        OUTPUT_DESC  = builder.addName(NAME + ".output").createGroup(OUT_COVERAGE);
    }

    public IOResampleDescriptor() {
                super(NAME, CoverageProcessingRegistry.IDENTIFICATION,
                new SimpleInternationalString("Resample a coverage using the given operation."),
                INPUT_DESC,
                OUTPUT_DESC);
    }

    public static final ProcessDescriptor INSTANCE = new IOResampleDescriptor();

    @Override
    public Process createProcess(ParameterValueGroup input) {
        return new IOResampleProcess(this, input);
    }
}
