/*
 *     (C) 2019, Geomatys
 */
package org.geotoolkit.processing.science.drift.v2;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import org.opengis.geometry.DirectPosition;
import org.opengis.metadata.identification.Identification;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.InternationalString;

import org.apache.sis.measure.MeasurementRange;
import org.apache.sis.measure.Units;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.util.SimpleInternationalString;

import org.geotoolkit.process.Process;
import org.geotoolkit.processing.AbstractProcessDescriptor;

/**
 * TODO: parameter descriptions, doc, etc.
 *
 * @author Alexis Manin (Geomatys)
 */
public class PredictorDescriptor extends AbstractProcessDescriptor {

    public static final String NAME = "drift.v2";

    public static final ParameterDescriptor<GridCoverageResource> WIND_RESOURCE;
    public static final ParameterDescriptor<GridCoverageResource> CURRENT_RESOURCE;

    public static final ParameterDescriptor<DirectPosition> START_POINT;
    public static final ParameterDescriptor<Long> START_TIMESTAMP;
    public static final ParameterDescriptor<Long> END_TIMESTAMP;

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

    public static final ParameterDescriptor<Path> OUTPUT_DATA;
    public static final ParameterDescriptor<Long> ACTUAL_END_TIMESTAMP;

    private static final ParameterDescriptorGroup OUTPUT;
    static {
        final ParameterBuilder builder = new ParameterBuilder();

        builder.setRequired(true);

        START_POINT = builder
                .addName("startPoint")
                .setRemarks("Point to compute drift from")
                .create(DirectPosition.class, null);

        START_TIMESTAMP = builder
                .addName("startTimestamp")
                .setRemarks("Timestamp of the given start point. Milliseconds from the Epoch, UTC.")
                .create(Long.class, null);

        END_TIMESTAMP = builder
                .addName("endTimestamp")
                .setRemarks("Timestamp to reach when computing drift.")
                .create(Long.class, null);

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
                        START_POINT,
                        START_TIMESTAMP,
                        END_TIMESTAMP,
                        WIND_RESOURCE, CURRENT_RESOURCE,
                        TIMESTEP, MAX_POINTS, WEIGHTS,
                        TARGET_WIDTH, TARGET_HEIGHT, TARGET_RESOLUTION
                );

        // Define output
        OUTPUT_DATA = builder
                .addName("driftMap")
                .setRemarks("Probability map for drift trajectory")
                .setRequired(true)
                .create(Path.class, null);

        ACTUAL_END_TIMESTAMP = builder
                .addName("endTimestamp")
                .setRemarks("Timestamp of actual model end (for example because of missing data).")
                .setRequired(true)
                .create(Long.class, null);

        OUTPUT = builder.addName("output").createGroup(OUTPUT_DATA, ACTUAL_END_TIMESTAMP);
    }

    private static final InternationalString TITLE = new SimpleInternationalString("Drift computing");
    public static final InternationalString REMARKS = new SimpleInternationalString("Aims to compute probable trajectories for a given location at a given time.");

    public PredictorDescriptor(Identification factoryId) {
        super(NAME, factoryId, REMARKS, TITLE, INPUT, OUTPUT);
    }

    @Override
    public Process createProcess(ParameterValueGroup input) {
        return new Predictor(this, input);
    }
}
