/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.storage.coverage;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CancellationException;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.DisjointCoverageDomainException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.GenericName;

/**
 * Simplified GridCoverageReader which ensures the given GridCoverageReadParam
 * is not null and in the coverage CoordinateReferenceSystem.
 * 
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractGridCoverageReader extends GridCoverageReader {

    protected final CoverageReference ref;
    
    protected AbstractGridCoverageReader(CoverageReference ref){
        this.ref = ref;
    }
    
    @Override
    public List<? extends GenericName> getCoverageNames() throws CoverageStoreException, CancellationException {
        return Collections.singletonList(ref.getName());
    }

    /**
     * {@inheritDoc }
     * 
     * Checks params envelope, CRS and resolution and create or fix them to match
     * this coverage CRS.
     * 
     * @param index
     * @param param
     * @return
     * @throws CoverageStoreException
     * @throws CancellationException 
     */
    @Override
    public final GridCoverage read(int index, GridCoverageReadParam param) throws CoverageStoreException, CancellationException {
        if (index!=ref.getImageIndex()) throw new CoverageStoreException("Unvalid image index "+index);

        try {
            final GeneralGridGeometry gridGeometry = getGridGeometry(index);
            final CoordinateReferenceSystem coverageCrs = gridGeometry.getCoordinateReferenceSystem();
            
            //find requested envelope
            Envelope queryEnv = param == null ? null : param.getEnvelope();
            if(queryEnv == null && param != null && param.getCoordinateReferenceSystem()!= null){
                queryEnv = Envelopes.transform(gridGeometry.getEnvelope(), param.getCoordinateReferenceSystem());
            }

            //convert resolution to coverage crs
            final double[] queryRes = param == null ? null : param.getResolution();
            double[] coverageRes = queryRes;
            if (queryRes != null) {
                coverageRes = ReferencingUtilities.convertResolution(queryEnv, queryRes, coverageCrs);
            }

            //if no envelope is defined, use the full extent
            final Envelope coverageEnv;
            if (queryEnv==null) {
                coverageEnv = gridGeometry.getEnvelope();
            } else {
                final GeneralEnvelope genv = new GeneralEnvelope(Envelopes.transform(queryEnv, coverageCrs));
                //clip to coverage envelope
                genv.intersect(gridGeometry.getEnvelope());
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
            
            return read(cparam);
        } catch (TransformException ex) {
            throw new CoverageStoreException(ex.getMessage(), ex);
        }
    }

    /**
     * Read coverage, 
     * 
     * @param param Parameters are guarantee to be in coverage CRS.
     * @return
     * @throws TransformException
     * @throws CoverageStoreException 
     */
    protected abstract GridCoverage read(GridCoverageReadParam param) throws TransformException, CoverageStoreException;
    
}
