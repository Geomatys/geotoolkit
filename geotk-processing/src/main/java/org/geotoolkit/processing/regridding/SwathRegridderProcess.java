package org.geotoolkit.processing.regridding;

import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.AbstractProcess;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.ParameterValueGroup;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * Execution class for the Swath Regridding Process.
 */
public class SwathRegridderProcess extends AbstractProcess {

    public SwathRegridderProcess(final ProcessDescriptor desc, final ParameterValueGroup parameter) {
        super(desc, parameter);
    }

    @Override
    protected void execute() throws ProcessException {
        try {
            final Path[] filesArray = inputParameters.getValue(SwathRegridderDescriptor.FILES);
            final Path outputPath = inputParameters.getValue(SwathRegridderDescriptor.OUTPUT_PATH);
            final Double resolution = inputParameters.getValue(SwathRegridderDescriptor.RESOLUTION);
            final Envelope bboxEnv = inputParameters.getValue(SwathRegridderDescriptor.BBOX);
            final String[] varsArray = inputParameters.getValue(SwathRegridderDescriptor.VARIABLES);
            final Double radiusOfInfluence = inputParameters.getValue(SwathRegridderDescriptor.RADIUS_OF_INFLUENCE);
            final String resampleMethodStr = inputParameters.getValue(SwathRegridderDescriptor.RESAMPLE_METHOD);
            final Double sigma = inputParameters.getValue(SwathRegridderDescriptor.SIGMA);
            final Integer minQualityLevel = inputParameters.getValue(SwathRegridderDescriptor.MIN_QUALITY_LEVEL);
            final String outputLonConvention = inputParameters.getValue(SwathRegridderDescriptor.OUTPUT_LON_CONVENTION);
            final Boolean keepTime = inputParameters.getValue(SwathRegridderDescriptor.KEEP_TIME);

            List<Path> files = List.of(filesArray);

            if (files == null || files.isEmpty()) {
                throw new ProcessException("No files provided for regridding.", this);
            }
            if (outputPath == null) {
                throw new ProcessException("No output path provided for regridding.", this);
            }

            double[] bbox = null;
            if (bboxEnv != null) {
                bbox = new double[] {
                    bboxEnv.getMinimum(0), bboxEnv.getMinimum(1),
                    bboxEnv.getMaximum(0), bboxEnv.getMaximum(1)
                };
            }

            List<String> variables = varsArray != null ? Arrays.asList(varsArray) : null;
            SwathRegridder.ResampleMethod method = SwathRegridder.ResampleMethod.NEAREST;
            if (resampleMethodStr != null) {
                method = SwathRegridder.ResampleMethod.valueOf(resampleMethodStr.toUpperCase());
            }

            SwathRegridder regridder = new SwathRegridder(
                resolution, bbox, variables, radiusOfInfluence, method,
                sigma != null ? sigma : 0.0,
                minQualityLevel != null ? minQualityLevel : 0,
                outputLonConvention != null ? outputLonConvention : "[-180, 180]",
                keepTime != null ? keepTime : false
            );

            regridder.merge(files, outputPath);

            outputParameters.getOrCreate(SwathRegridderDescriptor.OUTPUT).setValue(outputPath);
        } catch (Exception ex) {
            throw new ProcessException("Failed to execute Swath Regridding process: " + ex.getMessage(), this, ex);
        }
    }
}
