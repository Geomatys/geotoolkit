package org.geotoolkit.processing.vector.drift;

import java.nio.file.Path;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.util.iso.Names;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.storage.coverage.CoverageReference;
import org.geotoolkit.storage.coverage.DefaultCoverageReference;
import org.opengis.geometry.DirectPosition;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class DriftPredictor extends AbstractProcess {

    public DriftPredictor(ProcessDescriptor desc, ParameterValueGroup input) {
        super(desc, input);
    }

    @Override
    protected void execute() throws ProcessException {
        final Parameters input = Parameters.castOrWrap(inputParameters);
        final DirectPosition startPoint = input.getValue(DriftPredictionDescriptor.START_POINT);
        final long startTimestamp = input.getValue(DriftPredictionDescriptor.START_TIMESTAMP);
        final long endTimestamp = input.getValue(DriftPredictionDescriptor.END_TIMESTAMP);

        // TODO : Put the processing

        final Path outputPath = null; // TODO : replace with real result
        final CoverageReference result = new DefaultCoverageReference(outputPath, Names.createLocalName(null, ":", "drift"));
        Parameters.castOrWrap(outputParameters).getOrCreate(DriftPredictionDescriptor.OUTPUT_DATA).setValue(this);
    }
}
