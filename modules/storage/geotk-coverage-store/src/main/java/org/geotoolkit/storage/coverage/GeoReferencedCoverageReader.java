/*
 *    (C) 2018, Geomatys
 */
package org.geotoolkit.storage.coverage;

import java.util.concurrent.CancellationException;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.operation.matrix.Matrices;
import org.apache.sis.referencing.operation.matrix.MatrixSIS;
import org.geotoolkit.coverage.grid.GridCoverage;
import org.geotoolkit.coverage.io.CoverageReader;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.DisjointCoverageDomainException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * Simplified GridCoverageReader which ensures the given GridCoverageReadParam
 * is not null and in the coverage CoordinateReferenceSystem.
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class GeoReferencedCoverageReader implements CoverageReader {

    protected final CoverageResource resource;

    public GeoReferencedCoverageReader(CoverageResource resource) {
        this.resource = resource;
    }

    /**
     * Return coverage CoordinateReferenceSystem.
     *
     * @return CoordinateReferenceSystem, should not be null
     * @throws CoverageStoreException
     */
    protected abstract CoordinateReferenceSystem getCoordinateReferenceSystem() throws CoverageStoreException;

    /**
     * Return coverage envelope.
     *
     * @return Envelope, may be null
     * @throws CoverageStoreException
     */
    protected abstract Envelope getEnvelope() throws CoverageStoreException;


    @Override
    public final GridCoverage read(GridCoverageReadParam param) throws CoverageStoreException, CancellationException {

        final CoordinateReferenceSystem coverageCrs = getCoordinateReferenceSystem();

        try {
            //find requested envelope
            Envelope queryEnv = param == null ? null : param.getEnvelope();
            if(queryEnv == null && param != null && param.getCoordinateReferenceSystem()!= null){
                queryEnv = Envelopes.transform(getEnvelope(), param.getCoordinateReferenceSystem());
            }

            //convert resolution to coverage crs
            final double[] queryRes = param == null ? null : param.getResolution();
            double[] coverageRes = queryRes;
            if (queryRes != null) {
                try {
                    //this operation works only for 2D CRS
                    coverageRes = ReferencingUtilities.convertResolution(queryEnv, queryRes, coverageCrs);
                } catch (TransformException | IllegalArgumentException ex) {
                    //more general case, less accurate
                    coverageRes = convertCentralResolution(queryRes, queryEnv, coverageCrs);
                }
            }

            //if no envelope is defined, use the full extent
            final Envelope coverageEnv;
            if (queryEnv==null) {
                coverageEnv = getEnvelope();
            } else {
                final GeneralEnvelope genv = new GeneralEnvelope(Envelopes.transform(queryEnv, coverageCrs));
                //clip to coverage envelope
                genv.intersect(getEnvelope());
                coverageEnv = genv;

                //check for disjoint envelopes
                int dimension = 0;
                for (int i=genv.getDimension(); --i>=0;) {
                    if (genv.getSpan(i) > 0) {
                        dimension++;
                    }
                }
                if (dimension < 2) {
                    throw new DisjointCoverageDomainException("No coverage matched parameters");
                }
            }


            final GridCoverageReadParam cparam = new GridCoverageReadParam();
            cparam.setCoordinateReferenceSystem(coverageEnv.getCoordinateReferenceSystem());
            cparam.setEnvelope(coverageEnv);
            cparam.setResolution(coverageRes);
            cparam.setDestinationBands((param == null) ? null : param.getDestinationBands());
            cparam.setSourceBands((param == null) ? null : param.getSourceBands());
            cparam.setDeferred((param == null) ? false : param.isDeferred());

            return readInNativeCRS(cparam);
        } catch (TransformException | FactoryException ex) {
            throw new CoverageStoreException(ex.getMessage(), ex);
        }
    }

    /**
     * Read coverage,
     *
     * @param param Parameters are guarantee to be in coverage CRS.
     */
    protected abstract GridCoverage readInNativeCRS(GridCoverageReadParam cparam) throws CoverageStoreException, CancellationException;

    /**
     * Convert resolution from one CRS to another at the center of given envelope.
     */
    private static double[] convertCentralResolution(final double[] resolution, final Envelope area,
            final CoordinateReferenceSystem targetCRS) throws FactoryException, TransformException {
        final CoordinateReferenceSystem areaCrs = area.getCoordinateReferenceSystem();
        if (areaCrs.equals(targetCRS)) {
            //nothing to do.
            return resolution;
        }

        final GeneralDirectPosition center = new GeneralDirectPosition(area.getDimension());
        for (int i=center.getDimension(); --i >= 0;) {
            center.setOrdinate(i, area.getMedian(i));
        }
        final Matrix derivative = CRS.findOperation(areaCrs, targetCRS, null).getMathTransform().derivative(center);
        final Matrix vector = Matrices.createZero(resolution.length, 1);
        for (int i=0; i<resolution.length; i++) {
            vector.setElement(i, 0, resolution[i]);
        }
        final Matrix result = Matrices.multiply(derivative, vector);
        return MatrixSIS.castOrCopy(result).getElements();
    }


}
