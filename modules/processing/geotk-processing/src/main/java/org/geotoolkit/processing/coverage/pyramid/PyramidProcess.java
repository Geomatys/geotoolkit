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
import org.geotoolkit.storage.coverage.CoverageStore;
import org.geotoolkit.util.NamesExt;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.storage.coverage.PyramidCoverageBuilder;
import static org.geotoolkit.parameter.Parameters.*;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.process.ProcessException;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.storage.coverage.CoverageReference;
import org.geotoolkit.process.ProcessEvent;
import org.geotoolkit.process.ProcessListener;
import org.geotoolkit.utility.parameter.ParametersExt;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.ParameterValueGroup;

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
    public PyramidProcess(CoverageReference covref,CoverageStore covstore,double[] fillValues,
            InterpolationCase interpolation,String pyramidName,Map resPerEnvelope, Boolean reuseTile, Dimension tileSize){
        super(PyramidDescriptor.INSTANCE, asParameters(covref,covstore,fillValues,
                interpolation,pyramidName,resPerEnvelope,reuseTile,tileSize));
    }

    private static ParameterValueGroup asParameters(CoverageReference covref,CoverageStore covstore,double[] fillValues,
            InterpolationCase interpolation,String pyramidName,Map resPerEnvelope, Boolean reuseTile, Dimension tileSize){
        final ParameterValueGroup params = PyramidDescriptor.INPUT_DESC.createValue();
        ParametersExt.getOrCreateValue(params, PyramidDescriptor.IN_COVERAGEREF.getName().getCode()).setValue(covref);
        ParametersExt.getOrCreateValue(params, PyramidDescriptor.IN_COVERAGESTORE.getName().getCode()).setValue(covstore);
        ParametersExt.getOrCreateValue(params, PyramidDescriptor.IN_FILLVALUES.getName().getCode()).setValue(fillValues);
        ParametersExt.getOrCreateValue(params, PyramidDescriptor.IN_INTERPOLATIONCASE.getName().getCode()).setValue(interpolation);
        ParametersExt.getOrCreateValue(params, PyramidDescriptor.IN_PYRAMID_NAME.getName().getCode()).setValue(pyramidName);
        ParametersExt.getOrCreateValue(params, PyramidDescriptor.IN_RES_PER_ENVELOPE.getName().getCode()).setValue(resPerEnvelope);
        ParametersExt.getOrCreateValue(params, PyramidDescriptor.IN_REUSETILES.getName().getCode()).setValue(reuseTile);
        ParametersExt.getOrCreateValue(params, PyramidDescriptor.IN_TILE_SIZE.getName().getCode()).setValue(tileSize);
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

        final CoverageReference coverageref       = value(PyramidDescriptor.IN_COVERAGEREF      , inputParameters);
        final CoverageStore coverageStore         = value(PyramidDescriptor.IN_COVERAGESTORE    , inputParameters);
        final InterpolationCase interpolationcase = value(PyramidDescriptor.IN_INTERPOLATIONCASE, inputParameters);
        final Map resolution_per_envelope         = value(PyramidDescriptor.IN_RES_PER_ENVELOPE , inputParameters);
        final String pyramid_name                 = value(PyramidDescriptor.IN_PYRAMID_NAME     , inputParameters);
        final Dimension tilesize                  = value(PyramidDescriptor.IN_TILE_SIZE        , inputParameters);
        final double[] fillvalue                  = value(PyramidDescriptor.IN_FILLVALUES       , inputParameters);
        Boolean reuseTiles                        = value(PyramidDescriptor.IN_REUSETILES       , inputParameters);

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
        getOrCreate(PyramidDescriptor.OUT_COVERAGESTORE, outputParameters).setValue(coverageStore);
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
