/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2013, Geomatys
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
package org.geotoolkit.display2d.primitive;

import java.util.logging.Level;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.util.collection.Cache;
import org.geotoolkit.storage.coverage.CoverageReference;
import org.geotoolkit.coverage.GridCoverageStack;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.DisjointCoverageDomainException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.display.canvas.AbstractCanvas2D;
import org.geotoolkit.display2d.GO2Hints;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.container.stateless.StatelessContextParams;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.ElevationModel;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.filter.expression.Expression;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.operation.TransformException;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.util.logging.Logging;

/**
 * Convenient representation of a coverage for rendering.
 * Caches coverage based on given parameters.
 *
 * Not thread safe.
 * Use it knowing you make clear cache operation in a synchronize way.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class ProjectedCoverage implements ProjectedObject<CoverageMapLayer> {

    private final Cache<GridCoverageReadParam, GridCoverage2D> cache = new Cache<>(1, 0, false);

    private final StatelessContextParams params;
    private final CoverageMapLayer layer;
    private ProjectedGeometry border;

    public ProjectedCoverage(final StatelessContextParams params, final CoverageMapLayer layer) {
        this.params = params;
        this.layer = layer;
    }

    public void clearObjectiveCache(){
        border = null;
    }

    public void clearDisplayCache(){
        border = null;
    }

    /**
     * Get the original CoverageMapLayer from where the feature is from.
     *
     * @return CoverageMapLayer
     */
    @Override
    public CoverageMapLayer getLayer() {
        return layer;
    }

    /**
     * Return internal coverage.
     *
     * TODO return a GridCoverage instead of GridCoverage2D ?
     *
     * @param param : expected coverage parameters
     * @return GridCoverage2D or {@code null} if the requested parameters are out of the coverage area.
     *
     * @throws CoverageStoreException if problem during {@link CoverageReference#acquireReader() }
     * or {@link GridCoverageReader#dispose() } call.
     */
    public GridCoverage2D getCoverage(final GridCoverageReadParam param) throws CoverageStoreException{
        GridCoverage2D value = cache.peek(param);
        if (value == null) {
            Cache.Handler<GridCoverage2D> handler = cache.lock(param);
            try {
                value = handler.peek();
                if (value == null) {
                    final CoverageReference ref = layer.getCoverageReference();
                    final GridCoverageReader reader = ref.acquireReader();
                    try {
                        final GridCoverage result = reader.read(layer.getCoverageReference().getImageIndex(), param);
                        if (result instanceof GridCoverageStack) {
                            throw new CoverageStoreException("Coverage reader return more than one slice.");
                        }
                        value = (GridCoverage2D) result;
                        ref.recycle(reader);
                    } catch (DisjointCoverageDomainException ex) {
                        //-- DisjointCoverageDomainException is return when we request area out of source boundary.
                        //-- it's an expected comportement
                        //-- this method will return null in accordance with its contract.
                        ref.recycle(reader);
                    } catch (Throwable e) {
                        reader.dispose();
                        throw e;
                    }
                }
            } finally {
                 handler.putAndUnlock(value);
            }
        }
        return value;
    }

    /**
     * Get the projected geometry representation of the coverage border.
     *
     * @return ProjectedGeometry
     */
    public ProjectedGeometry getEnvelopeGeometry() {
        if(border == null){
            Envelope env = layer.getBounds();
            try {
                env = Envelopes.transform(env, params.context.getObjectiveCRS2D());
            } catch (TransformException ex) {
                Logging.getLogger("org.geotoolkit.display2d.primitive").log(Level.SEVERE, null, ex);
            }
            env = new GeneralEnvelope(env);
            GO2Utilities.removeNaN((GeneralEnvelope)env);

            border = new ProjectedGeometry(params);
            border.setDataGeometry(JTS.toGeometry(env),env.getCoordinateReferenceSystem());
        }
        return border;
    }


    /**
     * Get a coverage reference for the elevation model.
     *
     * @param param : expected coverage parameters
     * @return GridCoverage2D or null if the requested parameters are out of the coverage area.
     *
     * @throws CoverageStoreException
     */
    public GridCoverage2D getElevationCoverage(final GridCoverageReadParam param) throws CoverageStoreException{
        ElevationModel elevationModel = layer.getElevationModel();
        if(elevationModel == null){
             elevationModel = (ElevationModel) params.context.getRenderingHints().get(GO2Hints.KEY_ELEVATION_MODEL);
        }

        if(elevationModel != null){
            return (GridCoverage2D) elevationModel.getCoverageReader().read(0,param);
        }else{
            return null;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isVisible() {
        return true;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setVisible(final boolean visible) {
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void dispose() {
        cache.clear();
    }

    @Override
    public AbstractCanvas2D getCanvas() {
        return params.canvas;
    }

    @Override
    public CoverageMapLayer getCandidate() {
        return layer;
    }

    @Override
    public ProjectedGeometry getGeometry(Expression name) {
        throw new UnsupportedOperationException("Not supported.");
    }

}
