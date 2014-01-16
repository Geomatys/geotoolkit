package org.geotoolkit.process.coverage.resample;

import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.process.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.coverage.CoverageProcessingRegistry;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.operation.MathTransform;

import javax.imageio.ImageReader;
import java.io.File;

/**
 * Descriptor for a generic resample operation. A generic resample converts a given coverage using a
 * {@link org.opengis.referencing.operation.MathTransform} specified as input. The result is stored in a given coverage.
 * The process can be streamed if the user specifies the appropriate parameter, and if the input reader and writer allow
 * tile reading.
 *
 * @author Alexis Manin (Geomatys)
 */
public class GenericResampleDescriptor extends AbstractProcessDescriptor {

    public static final String NAME = "GenericResample";

    public static final ParameterDescriptor<ImageReader> IN_COVERAGE;
    public static final ParameterDescriptor<MathTransform> OPERATOR;
    public static final ParameterDescriptor<String> INTERPOLATOR;
    public static final ParameterDescriptor<Integer> OUT_WIDTH;
    public static final ParameterDescriptor<Integer> OUT_HEIGHT;
    public static final ParameterDescriptor<String> OUT_LOC;
    public static final ParameterDescriptor<Integer> THREAD_COUNT;
    public static final ParameterDescriptor<Long> BLOCK_SIZE;

    public static final ParameterDescriptorGroup INPUT_DESC;

    public static final DefaultParameterDescriptor<File> OUT_COVERAGE;

    public static final ParameterDescriptorGroup OUTPUT_DESC;

    static {
        IN_COVERAGE = new DefaultParameterDescriptor<ImageReader>
                ("image", "The image to resample", ImageReader.class, null, true);
        OPERATOR = new DefaultParameterDescriptor<MathTransform>
                ("operation", "The transformation to apply on source coverage.", MathTransform.class, null, true);
        INTERPOLATOR = new DefaultParameterDescriptor<String>
                ("interpolation", "The interpolation to apply on pixel transfer.", String.class, "bilinear", true);
        OUT_WIDTH = new DefaultParameterDescriptor<Integer>
                ("width", "The width to set for the result image.", Integer.class, null, true);
        OUT_HEIGHT = new DefaultParameterDescriptor<Integer>
                ("height", "The height to set for the result image.", Integer.class, null, true);
        OUT_LOC = new DefaultParameterDescriptor<String>("outputLocation", "An absolute path to specify where target ortho-Image should be saved (future file path or its parent directory)." +
                    " If this parameter is not specified, a temporary file is created.", String.class, null, false);
        THREAD_COUNT = new DefaultParameterDescriptor<Integer>
                ("threadCount", "The number of threads to use for orthorectification computing.", Integer.class, null, false);
        BLOCK_SIZE = new DefaultParameterDescriptor<Long>
                ("blockSize", "The size (in bytes) of the reading block for input image. If not set, a default size of 4 Mo will be taken.", Long.class, null, false);
        INPUT_DESC = new DefaultParameterDescriptorGroup(NAME + ".input",
                IN_COVERAGE,
                OPERATOR,
                INTERPOLATOR,
                OUT_WIDTH,
                OUT_HEIGHT,
                OUT_LOC,
                THREAD_COUNT,
                BLOCK_SIZE);


        OUT_COVERAGE = new DefaultParameterDescriptor<File>
                ("image", "The image to store the resampled result.", File.class, null, true);

        OUTPUT_DESC  = new DefaultParameterDescriptorGroup(NAME + ".output", OUT_COVERAGE);
    }

    public GenericResampleDescriptor() {
                super(NAME, CoverageProcessingRegistry.IDENTIFICATION,
                new SimpleInternationalString("Resample a coverage using the given operation."),
                INPUT_DESC,
                OUTPUT_DESC);
    }

    public static final ProcessDescriptor INSTANCE = new GenericResampleDescriptor();

    @Override
    public Process createProcess(ParameterValueGroup input) {
        return new GenericResampleProcess(this, input);
    }
}
