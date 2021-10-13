/*
 *     (C) 2019, Geomatys
 */
package org.geotoolkit.processing.science.drift.v2;

import java.util.concurrent.TimeUnit;
import org.apache.sis.measure.MeasurementRange;
import org.apache.sis.measure.Units;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.util.SimpleInternationalString;
import org.geotoolkit.process.Process;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.processing.science.drift.DriftPredictionDescriptor;
import org.opengis.metadata.identification.Identification;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.InternationalString;

/**
 * TODO: parameter descriptions, doc, etc.
 *
 * @author Alexis Manin (Geomatys)
 */
public class PredictorDescriptor extends AbstractProcessDescriptor {

    public static final String NAME = "drift.v2";

    public static final ParameterDescriptor<GridCoverageResource> WIND_RESOURCE;
    public static final ParameterDescriptor<GridCoverageResource> CURRENT_RESOURCE;

    public static final ParameterDescriptor<Long> TIMESTEP;
    public static final ParameterDescriptor<Integer> TARGET_WIDTH;
    public static final ParameterDescriptor<Integer> TARGET_HEIGHT;
    public static final ParameterDescriptor<Double> TARGET_RESOLUTION;
    public static final ParameterDescriptor<Integer> MAX_POINTS;

    public static final ParameterDescriptorGroup WEIGHTS;
    public static final ParameterDescriptor<Float> WIND_WEIGHT;
    public static final ParameterDescriptor<Float> CURRENT_WEIGHT;
    public static final ParameterDescriptor<Float> WEIGHT_PROBABILITY;

    private static final ParameterDescriptorGroup INPUT;
    private static final ParameterDescriptorGroup OUTPUT = DriftPredictionDescriptor.OUTPUT;
    static {
        final ParameterBuilder builder = new ParameterBuilder();

        builder.setRequired(true);

        TIMESTEP = builder.addName("timestep").createBounded(
                new MeasurementRange<>(Long.class, 1l, true, TimeUnit.DAYS.toSeconds(1), true, Units.SECOND), 10l
        );

        MAX_POINTS = builder.addName("max-points")
                .createBounded(16, Integer.MAX_VALUE, Short.MAX_VALUE);

        TARGET_WIDTH = builder.addName("target.width")
                .createBounded(2, Short.MAX_VALUE, 1024);
        TARGET_HEIGHT = builder.addName("target.height")
                .createBounded(2, Short.MAX_VALUE, 1024);
        TARGET_RESOLUTION = builder.addName("target.resolution")
                .setDescription("Specify output image resolution as meter per pixel.")
                .createStrictlyPositive(100d, Units.METRE);

        final MeasurementRange<Float> ratio = MeasurementRange.create(0f, true, 1f, true, Units.UNITY);

        WIND_WEIGHT = builder.addName("wind")
                .createBounded(ratio, 0.2f);

        CURRENT_WEIGHT = builder.addName("current")
                .createBounded(ratio, 0.8f);

        WEIGHT_PROBABILITY = builder.addName("probability")
                .createBounded(ratio, 0.5f);

        WEIGHTS = builder.addName("weights")
                .createGroup(1, Byte.MAX_VALUE, WIND_WEIGHT, CURRENT_WEIGHT, WEIGHT_PROBABILITY);

        WIND_RESOURCE = builder.addName("data.wind")
                .create(GridCoverageResource.class, null);

        CURRENT_RESOURCE = builder.addName("data.current")
                .create(GridCoverageResource.class, null);

        INPUT = builder.addName("input")
                .createGroup(
                        DriftPredictionDescriptor.START_POINT,
                        DriftPredictionDescriptor.START_TIMESTAMP,
                        DriftPredictionDescriptor.END_TIMESTAMP,
                        WIND_RESOURCE, CURRENT_RESOURCE,
                        TIMESTEP, MAX_POINTS, WEIGHTS,
                        TARGET_WIDTH, TARGET_HEIGHT, TARGET_RESOLUTION
                );
    }

    private static final InternationalString TITLE = new SimpleInternationalString("Drift computing");

    public PredictorDescriptor(Identification factoryId) {
        super(NAME, factoryId, DriftPredictionDescriptor.REMARKS, TITLE, INPUT, OUTPUT);
    }

    @Override
    public Process createProcess(ParameterValueGroup input) {
        return new Predictor(this, input);
    }
}
