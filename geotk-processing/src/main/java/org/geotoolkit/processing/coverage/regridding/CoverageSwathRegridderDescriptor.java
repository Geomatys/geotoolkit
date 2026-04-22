package org.geotoolkit.processing.coverage.regridding;

import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.storage.GridCoverageResource;
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

import java.util.List;

/**
 * Descriptor for the Coverage Swath Regridding Process.
 * Regrids a list of swath (curvilinear) GridCoverageResource onto a regular lat/lon grid,
 * producing a single merged GridCoverage.
 */
public class CoverageSwathRegridderDescriptor extends AbstractProcessDescriptor {

    public static final String NAME = "coverage:regridding.swath";
    public static final InternationalString ABSTRACT =
            new SimpleInternationalString("Regrids swath (curvilinear) GridCoverage tiles onto a regular lat/lon GridCoverage.");

    public static final String COVERAGE_RESOURCES_NAME = "coverages";
    @SuppressWarnings("unchecked")
    public static final ParameterDescriptor<GridCoverage[]> COVERAGE_RESOURCES = new ParameterBuilder()
            .addName(COVERAGE_RESOURCES_NAME)
            .setRemarks("Array of GridCoverage tiles to regrid.")
            .setRequired(true)
            .create(GridCoverage[].class, null);

    public static final String RESOLUTION_NAME = "resolution";
    public static final ParameterDescriptor<Double> RESOLUTION = new ParameterBuilder()
            .addName(RESOLUTION_NAME)
            .setRemarks("Target resolution in degrees.")
            .setRequired(true)
            .create(Double.class, null);

    public static final String BBOX_NAME = "bbox";
    public static final ParameterDescriptor<Envelope> BBOX = new ParameterBuilder()
            .addName(BBOX_NAME)
            .setRemarks("Bounding box of the merged output. If absent, computed from input coverages.")
            .setRequired(false)
            .create(Envelope.class, null);

    public static final String RADIUS_OF_INFLUENCE_NAME = "radiusOfInfluence";
    public static final ParameterDescriptor<Double> RADIUS_OF_INFLUENCE = new ParameterBuilder()
            .addName(RADIUS_OF_INFLUENCE_NAME)
            .setRemarks("Maximum search radius in meters for nearest-neighbour lookup.")
            .setRequired(true)
            .create(Double.class, null);

    public static final String RESAMPLE_METHOD_NAME = "resampleMethod";
    public static final ParameterDescriptor<String> RESAMPLE_METHOD = new ParameterBuilder()
            .addName(RESAMPLE_METHOD_NAME)
            .setRemarks("Resample method: NEAREST or GAUSS.")
            .setRequired(false)
            .create(String.class, "NEAREST");

    public static final String SIGMA_NAME = "sigma";
    public static final ParameterDescriptor<Double> SIGMA = new ParameterBuilder()
            .addName(SIGMA_NAME)
            .setRemarks("Sigma in meters for Gaussian resampling.")
            .setRequired(false)
            .create(Double.class, 0.0);

    public static final String QUALITY_BAND_INDEX_NAME = "qualityBandIndex";
    public static final ParameterDescriptor<Integer> QUALITY_BAND_INDEX = new ParameterBuilder()
            .addName(QUALITY_BAND_INDEX_NAME)
            .setRemarks("Band index used as quality level for quality-aware mosaicking. Use -1 to disable.")
            .setRequired(false)
            .create(Integer.class, -1);

    public static final String MIN_QUALITY_LEVEL_NAME = "minQualityLevel";
    public static final ParameterDescriptor<Integer> MIN_QUALITY_LEVEL = new ParameterBuilder()
            .addName(MIN_QUALITY_LEVEL_NAME)
            .setRemarks("Minimum accepted quality level. Pixels below this threshold are ignored.")
            .setRequired(false)
            .create(Integer.class, 0);

    public static final String OUTPUT_LON_CONVENTION_NAME = "outputLonConvention";
    public static final ParameterDescriptor<String> OUTPUT_LON_CONVENTION = new ParameterBuilder()
            .addName(OUTPUT_LON_CONVENTION_NAME)
            .setRemarks("Output longitude convention: \"[-180, 180]\" or \"[0, 360]\".")
            .setRequired(false)
            .create(String.class, "[-180, 180]");

    public static final ParameterDescriptorGroup INPUT_DESC = new ParameterBuilder()
            .addName(NAME + "InputParameters").setRequired(true)
            .createGroup(COVERAGE_RESOURCES, RESOLUTION, BBOX, RADIUS_OF_INFLUENCE,
                    RESAMPLE_METHOD, SIGMA, QUALITY_BAND_INDEX, MIN_QUALITY_LEVEL, OUTPUT_LON_CONVENTION);

    public static final String OUT_COVERAGE_NAME = "coverage";
    public static final ParameterDescriptor<GridCoverage> OUT_COVERAGE = new ParameterBuilder()
            .addName(OUT_COVERAGE_NAME)
            .setRemarks("Merged regridded coverage on a regular lat/lon grid.")
            .setRequired(true)
            .create(GridCoverage.class, null);

    public static final ParameterDescriptorGroup OUTPUT_DESC = new ParameterBuilder()
            .addName(NAME + "OutputParameters").setRequired(true)
            .createGroup(OUT_COVERAGE);

    public static final ProcessDescriptor INSTANCE = new CoverageSwathRegridderDescriptor();

    private CoverageSwathRegridderDescriptor() {
        super(NAME, GeotkProcessingRegistry.IDENTIFICATION, ABSTRACT, INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new CoverageSwathRegridderProcess(this, input);
    }
}
