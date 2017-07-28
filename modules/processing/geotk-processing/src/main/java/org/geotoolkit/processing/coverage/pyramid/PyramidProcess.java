/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009 - 2012, Geomatys
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
package org.geotoolkit.processing.coverage.pyramid;

import java.awt.Dimension;
import java.util.Map;
import java.util.concurrent.CancellationException;
import javax.swing.JLabel;
import javax.swing.ProgressMonitor;
import org.apache.sis.parameter.Parameters;
import org.geotoolkit.storage.coverage.CoverageStore;
import org.geotoolkit.util.NamesExt;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.storage.coverage.PyramidCoverageBuilder;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.process.ProcessException;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.process.ProcessEvent;
import org.geotoolkit.process.ProcessListener;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.ParameterValueGroup;
import org.geotoolkit.storage.coverage.CoverageResource;

/**
 * <p>Build and store a image pyramid.<br/><br/>
 *
 * For more explanations about input output objects see {@link PGCoverageBuilder} javadoc.
 *
 * @author Remi Marechal (Geomatys).
 */
public class PyramidProcess extends AbstractProcess implements ProcessListener {

    PyramidProcess(final ParameterValueGroup input) {
        super(PyramidDescriptor.INSTANCE, input);
    }

    /**
     *
     * @param covref input coverage reference
     * @param covstore output store
     * @param fillValues tiles fill values
     * @param interpolation tile interpolation method
     * @param pyramidName name of created pyramid
     * @param resPerEnvelope resolution of mosaics
     * @param reuseTile reuse tiles
     * @param tileSize tile size
     */
    public PyramidProcess(CoverageResource covref,CoverageStore covstore,double[] fillValues,
            InterpolationCase interpolation,String pyramidName,Map resPerEnvelope, Boolean reuseTile, Dimension tileSize){
        super(PyramidDescriptor.INSTANCE, asParameters(covref,covstore,fillValues,
                interpolation,pyramidName,resPerEnvelope,reuseTile,tileSize));
    }

    private static ParameterValueGroup asParameters(CoverageResource covref,CoverageStore covstore,double[] fillValues,
            InterpolationCase interpolation,String pyramidName,Map resPerEnvelope, Boolean reuseTile, Dimension tileSize){
        final Parameters params = Parameters.castOrWrap(PyramidDescriptor.INPUT_DESC.createValue());
        params.getOrCreate(PyramidDescriptor.IN_COVERAGEREF).setValue(covref);
        params.getOrCreate(PyramidDescriptor.IN_COVERAGESTORE).setValue(covstore);
        params.getOrCreate(PyramidDescriptor.IN_FILLVALUES).setValue(fillValues);
        params.getOrCreate(PyramidDescriptor.IN_INTERPOLATIONCASE).setValue(interpolation);
        params.getOrCreate(PyramidDescriptor.IN_PYRAMID_NAME).setValue(pyramidName);
        params.getOrCreate(PyramidDescriptor.IN_RES_PER_ENVELOPE).setValue(resPerEnvelope);
        params.getOrCreate(PyramidDescriptor.IN_REUSETILES).setValue(reuseTile);
        params.getOrCreate(PyramidDescriptor.IN_TILE_SIZE).setValue(tileSize);
        return params;
    }

    /**
     * Execute process now.
     *
     * @return result coverage store
     * @throws ProcessException
     */
    public CoverageStore executeNow() throws ProcessException {
        execute();
        return (CoverageStore) outputParameters.parameter(PyramidDescriptor.OUT_COVERAGESTORE.getName().getCode()).getValue();
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected void execute() throws ProcessException {

        ArgumentChecks.ensureNonNull("inputParameters", inputParameters);

        final CoverageResource coverageref        = inputParameters.getValue(PyramidDescriptor.IN_COVERAGEREF      );
        final CoverageStore coverageStore         = inputParameters.getValue(PyramidDescriptor.IN_COVERAGESTORE    );
        final InterpolationCase interpolationcase = inputParameters.getValue(PyramidDescriptor.IN_INTERPOLATIONCASE);
        final Map resolution_per_envelope         = inputParameters.getValue(PyramidDescriptor.IN_RES_PER_ENVELOPE );
        final String pyramid_name                 = inputParameters.getValue(PyramidDescriptor.IN_PYRAMID_NAME     );
        final Dimension tilesize                  = inputParameters.getValue(PyramidDescriptor.IN_TILE_SIZE        );
        final double[] fillvalue                  = inputParameters.getValue(PyramidDescriptor.IN_FILLVALUES       );
        Boolean reuseTiles                        = inputParameters.getValue(PyramidDescriptor.IN_REUSETILES       );

        if (reuseTiles == null) {
            reuseTiles = Boolean.FALSE;
        }

        //check map values
        for(Object obj : resolution_per_envelope.keySet()) {
            if (!(obj instanceof Envelope))
                throw new ProcessException("Map key must be instance of Envelope", this, null);
            if (!(resolution_per_envelope.get(obj) instanceof double[]))
                throw new ProcessException("Map store objects must be instance of double[]", this, null);
        }

        final PyramidCoverageBuilder pgcb = new PyramidCoverageBuilder(tilesize, interpolationcase, 2, reuseTiles);
        if (isCanceled()) {
            throw new CancellationException();
        }
        try {
            pgcb.create(coverageref, coverageStore, NamesExt.create(pyramid_name), resolution_per_envelope, fillvalue,
                    this, new PyramidMonitor(this));
        } catch (Exception ex) {
            throw new ProcessException(ex.getMessage(), this, ex);
        }

        if (isCanceled()) {
            throw new CancellationException();
        }
        outputParameters.getOrCreate(PyramidDescriptor.OUT_COVERAGESTORE).setValue(coverageStore);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void started(ProcessEvent event) {
        // do nothing volontary
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void progressing(ProcessEvent event) {
        fireProgressing(event.getTask(), event.getProgress(), false);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void paused(ProcessEvent event) {
        fireProcessPaused(event.getTask(), event.getProgress());
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void resumed(ProcessEvent event) {
        fireProcessResumed(event.getTask(), event.getProgress());
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void completed(ProcessEvent event) {
        // do nothing volontary
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void failed(ProcessEvent event) {
        fireProcessFailed(event.getTask(), event.getException());
    }

    /**
     * Allow to cancel the tiles creation process.
     */
    private class PyramidMonitor extends ProgressMonitor {
        private final PyramidProcess process;

        public PyramidMonitor(final PyramidProcess process) {
            super(new JLabel(), "", "", 0, 100);
            this.process = process;
        }

        @Override
        public boolean isCanceled() {
            return process.isCanceled();
        }
    }
}
