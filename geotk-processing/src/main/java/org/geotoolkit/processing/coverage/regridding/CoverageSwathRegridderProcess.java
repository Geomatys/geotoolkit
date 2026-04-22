package org.geotoolkit.processing.coverage.regridding;

import org.apache.sis.coverage.grid.GridCoverage;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.AbstractProcess;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.ParameterValueGroup;

import java.util.List;

/**
 * Execution class for the Coverage Swath Regridding Process.
 */
public class CoverageSwathRegridderProcess extends AbstractProcess {

    public CoverageSwathRegridderProcess(final ProcessDescriptor desc, final ParameterValueGroup parameter) {
        super(desc, parameter);
    }

    @Override
    protected void execute() throws ProcessException {
        try {
            final GridCoverage[] resourcesArray =
                    inputParameters.getValue(CoverageSwathRegridderDescriptor.COVERAGE_RESOURCES);
            final Double resolution =
                    inputParameters.getValue(CoverageSwathRegridderDescriptor.RESOLUTION);
            final Envelope bboxEnv =
                    inputParameters.getValue(CoverageSwathRegridderDescriptor.BBOX);
            final Double radiusOfInfluence =
                    inputParameters.getValue(CoverageSwathRegridderDescriptor.RADIUS_OF_INFLUENCE);
            final String resampleMethodStr =
                    inputParameters.getValue(CoverageSwathRegridderDescriptor.RESAMPLE_METHOD);
            final Double sigma =
                    inputParameters.getValue(CoverageSwathRegridderDescriptor.SIGMA);
            final Integer qualityBandIndex =
                    inputParameters.getValue(CoverageSwathRegridderDescriptor.QUALITY_BAND_INDEX);
            final Integer minQualityLevel =
                    inputParameters.getValue(CoverageSwathRegridderDescriptor.MIN_QUALITY_LEVEL);
            final String outputLonConvention =
                    inputParameters.getValue(CoverageSwathRegridderDescriptor.OUTPUT_LON_CONVENTION);

            List<GridCoverage> resources = List.of(resourcesArray);

            if (resources == null || resources.isEmpty()) {
                throw new ProcessException("No coverage resources provided for regridding.", this);
            }
            if (resolution == null) {
                throw new ProcessException("Resolution parameter is required.", this);
            }
            if (radiusOfInfluence == null) {
                throw new ProcessException("Radius of influence parameter is required.", this);
            }

            double[] bbox = null;
            if (bboxEnv != null) {
                bbox = new double[]{
                    bboxEnv.getMinimum(0), bboxEnv.getMinimum(1),
                    bboxEnv.getMaximum(0), bboxEnv.getMaximum(1)
                };
            }

            CoverageSwathRegridder.ResampleMethod method = CoverageSwathRegridder.ResampleMethod.NEAREST;
            if (resampleMethodStr != null) {
                method = CoverageSwathRegridder.ResampleMethod.valueOf(resampleMethodStr.toUpperCase());
            }

            CoverageSwathRegridder regridder = new CoverageSwathRegridder(
                    resolution,
                    bbox,
                    radiusOfInfluence,
                    method,
                    sigma != null ? sigma : 0.0,
                    qualityBandIndex != null ? qualityBandIndex : -1,
                    minQualityLevel != null ? minQualityLevel : 0,
                    outputLonConvention != null ? outputLonConvention : "[-180, 180]"
            );

            GridCoverage result = regridder.merge(resources);
            outputParameters.getOrCreate(CoverageSwathRegridderDescriptor.OUT_COVERAGE).setValue(result);

        } catch (ProcessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ProcessException("Failed to execute Coverage Swath Regridding process: " + ex.getMessage(), this, ex);
        }
    }
}
