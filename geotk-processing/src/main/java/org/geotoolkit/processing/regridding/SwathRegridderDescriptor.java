package org.geotoolkit.processing.regridding;

import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.util.SimpleInternationalString;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.processing.GeotkProcessingRegistry;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.InternationalString;

import java.nio.file.Path;
import java.util.List;

/**
 * Descriptor for the Swath Regridding Process.
 */
public class SwathRegridderDescriptor extends AbstractProcessDescriptor {

    public static final String NAME = "regridding:netcdf.swath";
    public static final InternationalString ABSTRACT =
            new SimpleInternationalString("Regrids swath (curvilinear) NetCDF tiles onto a regular lat/lon grid.");

    public static final String FILES_NAME = "files";
    @SuppressWarnings("unchecked")
    public static final ParameterDescriptor<Path[]> FILES = new ParameterBuilder()
            .addName(FILES_NAME)
            .setRemarks("List of NetCDF items to regrid.")
            .setRequired(true)
            .create(Path[].class, null);

    public static final String OUTPUT_PATH_NAME = "output_path";
    public static final ParameterDescriptor<Path> OUTPUT_PATH = new ParameterBuilder()
            .addName(OUTPUT_PATH_NAME)
            .setRemarks("Path for the output merged NetCDF file.")
            .setRequired(true)
            .create(Path.class, null);

    public static final String RESOLUTION_NAME = "resolution";
    public static final ParameterDescriptor<Double> RESOLUTION = new ParameterBuilder()
            .addName(RESOLUTION_NAME)
            .setRemarks("Target resolution in degrees.")
            .setRequired(true)
            .create(Double.class, null);

    public static final String BBOX_NAME = "bbox";
    public static final ParameterDescriptor<Envelope> BBOX = new ParameterBuilder()
            .addName(BBOX_NAME)
            .setRemarks("Bounding box limits for the merged output.")
            .setRequired(false)
            .create(Envelope.class, null);

    public static final String VARIABLES_NAME = "variables";
    public static final ParameterDescriptor<String[]> VARIABLES = new ParameterBuilder()
            .addName(VARIABLES_NAME)
            .setRemarks("List of variables to regrid.")
            .setRequired(false)
            .create(String[].class, null);

    public static final String RADIUS_OF_INFLUENCE_NAME = "radius_of_influence";
    public static final ParameterDescriptor<Double> RADIUS_OF_INFLUENCE = new ParameterBuilder()
            .addName(RADIUS_OF_INFLUENCE_NAME)
            .setRemarks("Radius of influence in meters.")
            .setRequired(true)
            .create(Double.class, null);

    public static final String RESAMPLE_METHOD_NAME = "resample_method";
    public static final ParameterDescriptor<String> RESAMPLE_METHOD = new ParameterBuilder()
            .addName(RESAMPLE_METHOD_NAME)
            .setRemarks("Resample method: NEAREST or GAUSS.")
            .setRequired(true)
            .create(String.class, "NEAREST");

    public static final String SIGMA_NAME = "sigma";
    public static final ParameterDescriptor<Double> SIGMA = new ParameterBuilder()
            .addName(SIGMA_NAME)
            .setRemarks("Sigma for Gaussian resampling.")
            .setRequired(false)
            .create(Double.class, 0.0);

    public static final String MIN_QUALITY_LEVEL_NAME = "min_quality_level";
    public static final ParameterDescriptor<Integer> MIN_QUALITY_LEVEL = new ParameterBuilder()
            .addName(MIN_QUALITY_LEVEL_NAME)
            .setRemarks("Minimum accepted quality level.")
            .setRequired(false)
            .create(Integer.class, 0);

    public static final String OUTPUT_LON_CONVENTION_NAME = "output_lon_convention";
    public static final ParameterDescriptor<String> OUTPUT_LON_CONVENTION = new ParameterBuilder()
            .addName(OUTPUT_LON_CONVENTION_NAME)
            .setRemarks("Output longitude convention.")
            .setRequired(false)
            .create(String.class, "[-180, 180]");

    public static final String KEEP_TIME_NAME = "keep_time";
    public static final ParameterDescriptor<Boolean> KEEP_TIME = new ParameterBuilder()
            .addName(KEEP_TIME_NAME)
            .setRemarks("Keep time steps in the merged output.")
            .setRequired(false)
            .create(Boolean.class, false);

    public static final ParameterDescriptorGroup INPUT_DESC = new ParameterBuilder().addName("InputParameters").setRequired(true)
            .createGroup(FILES, OUTPUT_PATH, RESOLUTION, BBOX, VARIABLES, RADIUS_OF_INFLUENCE, RESAMPLE_METHOD, SIGMA, MIN_QUALITY_LEVEL, OUTPUT_LON_CONVENTION, KEEP_TIME);

    public static final String OUTPUT_NAME = "output";
    public static final ParameterDescriptor<Path> OUTPUT = new ParameterBuilder()
            .addName(OUTPUT_NAME)
            .setRemarks("Path for the output merged NetCDF file.")
            .setRequired(true)
            .create(Path.class, null);

    public static final ParameterDescriptorGroup OUTPUT_DESC = new ParameterBuilder().addName("OutputParameters").setRequired(true)
            .createGroup(OUTPUT);

    public SwathRegridderDescriptor() {
        super(NAME, GeotkProcessingRegistry.IDENTIFICATION, ABSTRACT, INPUT_DESC, OUTPUT_DESC);
    }

    public static final ProcessDescriptor INSTANCE = new SwathRegridderDescriptor();

    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new SwathRegridderProcess(this, input);
    }
}
