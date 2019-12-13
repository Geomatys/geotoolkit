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

import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.referencing.CRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.Resource;
import org.apache.sis.util.collection.Cache;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.coverage.grid.GridCoverageStack;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.display.canvas.AbstractCanvas2D;
import org.geotoolkit.display2d.GO2Hints;
import org.geotoolkit.display2d.container.stateless.StatelessContextParams;
import org.geotoolkit.geometry.GeometricUtilities;
import org.geotoolkit.map.ElevationModel;
import org.geotoolkit.map.MapLayer;
import org.locationtech.jts.geom.Geometry;
import org.opengis.filter.expression.Expression;
import org.opengis.geometry.Envelope;

/**
 * Convenient representation of a coverage for rendering.
 * Caches coverage based on given parameters.
 *
 * Not thread safe.
 * Use it knowing you make clear cache operation in a synchronize way.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class ProjectedCoverage implements ProjectedObject<MapLayer> {

    private final Cache<ReadParam, GridCoverage> cache = new Cache<>(1, 0, false);

    private final StatelessContextParams params;
    private final MapLayer layer;
    private ProjectedGeometry border;

    public ProjectedCoverage(final StatelessContextParams params, final MapLayer layer) {
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
    public MapLayer getLayer() {
        return layer;
    }

    /**
     * Return internal coverage.
     *
     * @param param : expected output geometry.
     * @return A coverage covering requested area, or at least a piece of it, in case queried area is not completely
     * contained in this data. Never null.
     *
     * @throws DataStoreException if reading fails for specified geometry. The error can be a {@link org.apache.sis.coverage.grid.DisjointExtentException}
     * if queried area is completely outside this coverage extent.
     */
    public GridCoverage getCoverage(final GridGeometry param, int... bands) throws DataStoreException {
        return getCoverage(new ReadParam(param, bands));
    }

    private GridCoverage getCoverage(final ReadParam param) throws DataStoreException {
        GridCoverage value = cache.peek(param);
        if (value == null) {
            Cache.Handler<GridCoverage> handler = cache.lock(param);
            try {
                value = handler.peek();
                if (value == null) {
                    Resource resource = layer.getResource();
                    if (resource instanceof GridCoverageResource) {
                        GridCoverage result = ((GridCoverageResource)resource).read(param.geometry, param.bands);
                        if (result instanceof GridCoverageStack) {
                            Logging.getLogger("org.geotoolkit.display2d.primitive").log(Level.WARNING, "Coverage reader return more than one slice.");
                        }
                        while (result instanceof GridCoverageStack) {
                            //pick the first slice
                            result = ((GridCoverageStack)result).coverageAtIndex(0);
                        }
                        value = result;
                    } else {
                        throw new DataStoreException("Resource is not a coverage" + resource);
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
        final Envelope env = layer.getBounds();
        final Geometry jtsBounds = GeometricUtilities.toJTSGeometry(env, GeometricUtilities.WrapResolution.NONE);
        border = new ProjectedGeometry(params);
        border.setDataGeometry(jtsBounds,CRS.getHorizontalComponent(env.getCoordinateReferenceSystem()));
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
    public GridCoverage getElevationCoverage(final GridGeometry param) throws DataStoreException {
        ElevationModel elevationModel = layer.getElevationModel();
        if(elevationModel == null){
             elevationModel = (ElevationModel) params.context.getRenderingHints().get(GO2Hints.KEY_ELEVATION_MODEL);
        }

        if(elevationModel != null){
            return elevationModel.getCoverageReader().read(param);
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
    public MapLayer getCandidate() {
        return layer;
    }

    @Override
    public ProjectedGeometry getGeometry(Expression name) {
        throw new UnsupportedOperationException("Not supported.");
    }

    private static class ReadParam {
        final GridGeometry geometry;
        final int[] bands;

        ReadParam(GridGeometry geometry, int... bands) {
            this.geometry = geometry;
            this.bands = (bands == null || bands.length < 1)? null : bands;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ReadParam)) return false;
            ReadParam readParam = (ReadParam) o;
            return Objects.equals(geometry, readParam.geometry) &&
                    Arrays.equals(bands, readParam.bands);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(geometry);
            result = 31 * result + Arrays.hashCode(bands);
            return result;
        }
    }
}
