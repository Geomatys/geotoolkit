/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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
package org.geotoolkit.display2d.container.statefull;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.primitive.ProjectedGeometry;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.ElevationModel;
import org.geotoolkit.util.collection.Cache;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.referencing.CRS;

import org.opengis.geometry.Envelope;
import org.opengis.referencing.operation.TransformException;

/**
 * Not thread safe. 
 * Use it knowing you make clear cache operation in a synchronize way.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class StatefullProjectedCoverage implements ProjectedCoverage {

    private final Cache<GridCoverageReadParam,GridCoverage2D> cache = new Cache<GridCoverageReadParam,GridCoverage2D>(1,0,false);

    private final StatefullContextParams params;
    private final CoverageMapLayer layer;
    private StatefullProjectedGeometry border;

    public StatefullProjectedCoverage(final StatefullContextParams params, final CoverageMapLayer layer) {
        this.params = params;
        this.layer = layer;
    }

    public void clearObjectiveCache(){
        border = null;
    }

    public void clearDisplayCache(){
        border = null;
    }

    @Override
    public GridCoverage2D getCoverage(final GridCoverageReadParam param) throws CoverageStoreException{
        GridCoverage2D value = cache.peek(param);
        if (value == null) {
            Cache.Handler<GridCoverage2D> handler = cache.lock(param);
            try {
                value = handler.peek();
                if (value == null) {
                    value = (GridCoverage2D) layer.getCoverageReader().read(0,param);
                }
            } finally {
                handler.putAndUnlock(value);
            }

        }
        return value;
    }

    @Override
    public GridCoverage2D getElevationCoverage(final GridCoverageReadParam param)
        throws CoverageStoreException{
        final ElevationModel elevationModel = layer.getElevationModel();

        if(elevationModel != null){
            return (GridCoverage2D) elevationModel.getCoverageReader().read(0,param);
        }else{
            return null;
        }
    }

    @Override
    public CoverageMapLayer getLayer() {
        return layer;
    }

    @Override
    public ProjectedGeometry getEnvelopeGeometry() {
        if(border == null){
            Envelope env = layer.getBounds();
            try {
                env = CRS.transform(env, params.context.getObjectiveCRS2D());
            } catch (TransformException ex) {
                Logger.getLogger(StatefullProjectedCoverage.class.getName()).log(Level.SEVERE, null, ex);
            }

            border = new StatefullProjectedGeometry(params, Polygon.class, createGeometry(env));
        }
        return border;
    }

    private static Geometry createGeometry(final Envelope env){
        final GeometryFactory fact = new GeometryFactory();
        final Coordinate[] coordinates = new Coordinate[]{
            new Coordinate(env.getMinimum(0), env.getMinimum(1)),
            new Coordinate(env.getMinimum(0), env.getMaximum(1)),
            new Coordinate(env.getMaximum(0), env.getMaximum(1)),
            new Coordinate(env.getMaximum(0), env.getMinimum(1)),
            new Coordinate(env.getMinimum(0), env.getMinimum(1)),
        };
        final LinearRing ring = fact.createLinearRing(coordinates);
        return fact.createPolygon(ring, new LinearRing[0]);
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
    public ReferencedCanvas2D getCanvas() {
        return params.canvas;
    }

    @Override
    public CoverageMapLayer getCandidate() {
        return layer;
    }

    @Override
    public ProjectedGeometry getGeometry(String name) {
        throw new UnsupportedOperationException("Not supported.");
    }

}
