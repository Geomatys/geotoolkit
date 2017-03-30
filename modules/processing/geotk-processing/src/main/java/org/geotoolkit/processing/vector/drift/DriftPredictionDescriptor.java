package org.geotoolkit.processing.vector.drift;

import java.nio.file.Path;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.process.Process;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.storage.coverage.CoverageReference;
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
    public static final ParameterDescriptor<CoverageReference> OUTPUT_DATA;

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
                .setRemarks("Root directory of HYCOM and WindSat data.")
                .setRequired(true)
                .create(Path.class, null);

        INPUT = builder.addName("input").createGroup(START_POINT, START_TIMESTAMP, END_TIMESTAMP, DATA_DIRECTORY);

        // Define output
        OUTPUT_DATA = builder
                .addName("driftMap")
                .setRemarks("Probability map for drift trajectory")
                .setRequired(true)
                .create(CoverageReference.class, null);

        OUTPUT = builder.addName("output").createGroup(OUTPUT_DATA);
    }

    public DriftPredictionDescriptor(Identification factoryId) {
        super(NAME, factoryId, REMARKS, INPUT, OUTPUT);
    }

    @Override
    public Process createProcess(ParameterValueGroup input) {
        return new DriftPredictor(this, input);
    }
}
