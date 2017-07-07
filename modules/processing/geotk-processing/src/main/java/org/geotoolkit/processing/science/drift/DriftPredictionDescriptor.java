package org.geotoolkit.processing.science.drift;

import java.nio.file.Path;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.process.Process;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.opengis.geometry.DirectPosition;
import org.opengis.metadata.identification.Identification;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.InternationalString;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class DriftPredictionDescriptor extends AbstractProcessDescriptor {

    public static final String NAME = "driftPrediction";
    private static final InternationalString REMARKS = new SimpleInternationalString("Aims to compute probable trajectories for a given location at a given time.");

    public static final ParameterDescriptorGroup INPUT;
    public static final ParameterDescriptor<DirectPosition> START_POINT;
    public static final ParameterDescriptor<Long> START_TIMESTAMP;
    public static final ParameterDescriptor<Long> END_TIMESTAMP;
    public static final ParameterDescriptor<Path> DATA_DIRECTORY;

    public static final ParameterDescriptorGroup OUTPUT;
    public static final ParameterDescriptor<Path> OUTPUT_DATA;
    public static final ParameterDescriptor<Long> ACTUAL_END_TIMESTAMP;

    static {
        final ParameterBuilder builder = new ParameterBuilder();
        // Define input parameters
        START_POINT = builder
                .addName("startPoint")
                .setRemarks("Point to compute drift from")
                .setRequired(true)
                .create(DirectPosition.class, null);

        START_TIMESTAMP = builder
                .addName("startTimestamp")
                .setRemarks("Timestamp of the given start point. Milliseconds from the Epoch, UTC.")
                .setRequired(true)
                .create(Long.class, null);

        END_TIMESTAMP = builder
                .addName("endTimestamp")
                .setRemarks("Timestamp to reach when computing drift.")
                .setRequired(true)
                .create(Long.class, null);

        DATA_DIRECTORY = builder
                .addName("dataDirectory")
                .setRemarks("Root directory of HYCOM and Météo-France data.")
                .setRequired(true)
                .create(Path.class, null);

        INPUT = builder.addName("input").createGroup(START_POINT, START_TIMESTAMP, END_TIMESTAMP, DATA_DIRECTORY);

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

    public DriftPredictionDescriptor(Identification factoryId) {
        super(NAME, factoryId, REMARKS, INPUT, OUTPUT);
    }

    @Override
    public Process createProcess(ParameterValueGroup input) {
        return new DriftPredictor(this, input);
    }
}
