/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2013, Geomatys
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
package org.geotoolkit.display2d.container.stateless;

import com.vividsolutions.jts.geom.Geometry;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.coverage.AbstractGridMosaic;
import org.geotoolkit.storage.coverage.AbstractPyramidalCoverageResource;
import org.geotoolkit.storage.coverage.CoverageStoreContentEvent;
import org.geotoolkit.storage.coverage.CoverageStoreListener;
import org.geotoolkit.storage.coverage.CoverageStoreManagementEvent;
import org.geotoolkit.storage.coverage.GridMosaic;
import org.geotoolkit.storage.coverage.Pyramid;
import org.geotoolkit.storage.coverage.PyramidSet;
import org.geotoolkit.storage.coverage.TileReference;
import org.geotoolkit.coverage.finder.CoverageFinder;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display.SearchArea;
import org.geotoolkit.display.VisitFilter;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display.canvas.control.CanvasMonitor;
import org.geotoolkit.display2d.GO2Hints;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.DefaultSearchAreaJ2D;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.display2d.style.CachedRule;
import org.geotoolkit.display2d.style.CachedSymbolizer;
import org.geotoolkit.display2d.style.renderer.DefaultRasterSymbolizerRenderer;
import org.opengis.util.GenericName;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.GraphicBuilder;
import org.geotoolkit.map.MapBuilder;
import org.apache.sis.referencing.CRS;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.geotoolkit.util.Cancellable;
import org.opengis.display.primitive.Graphic;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.geotoolkit.coverage.finder.DefaultCoverageFinder;
import org.apache.sis.geometry.Envelopes;
import org.geotoolkit.storage.coverage.PyramidalCoverageResource;

/**
 * Graphic for pyramidal coverage layers.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class StatelessPyramidalCoverageLayerJ2D extends StatelessMapLayerJ2D<CoverageMapLayer> implements CoverageStoreListener{

    protected CoverageStoreListener.Weak weakStoreListener = new CoverageStoreListener.Weak(this);

    private final PyramidalCoverageResource model;
    private final double tolerance;
    private final CoverageFinder coverageFinder;

    public StatelessPyramidalCoverageLayerJ2D(final J2DCanvas canvas, final CoverageMapLayer layer){
        super(canvas, layer, false);
        this.coverageFinder = new DefaultCoverageFinder();
        model = (PyramidalCoverageResource)layer.getCoverageReference();
        tolerance = 0.25; // in % , TODO use a flag to allow change value
        this.weakStoreListener.registerSource(layer.getCoverageReference());
    }

    public StatelessPyramidalCoverageLayerJ2D(final J2DCanvas canvas, final CoverageMapLayer layer, CoverageFinder coverageFinder){
        super(canvas, layer, false);
        this.coverageFinder = coverageFinder;
        model = (PyramidalCoverageResource)layer.getCoverageReference();
        tolerance = 0.25; // in % , TODO use a flag to allow change value
        this.weakStoreListener.registerSource(layer.getCoverageReference());
    }

    /**
     * {@inheritDoc }
     * @param context2D
     */
    @Override
    public void paintLayer(final RenderingContext2D context2D) {

        final CanvasMonitor monitor = context2D.getMonitor();
        final TileSetResult result = listTiles(context2D);
        if(result==null){
            //no pyramid or tiles match this context definition
            return;
        }

        //tiles to render
        final Map<Point,MathTransform> queries = new HashMap<Point,MathTransform>();
        final Map hints = new HashMap(item.getUserProperties());

        for(int tileCol=(int)result.tileMinCol; tileCol<result.tileMaxCol; tileCol++){
            for(int tileRow=(int)result.tileMinRow; tileRow<result.tileMaxRow; tileRow++){
                if(result.mosaic.isMissing(tileCol, tileRow)){
                    //tile not available
                    continue;
                }

                final Point pt = new Point(tileCol, tileRow);
                final MathTransform trs = AbstractGridMosaic.getTileGridToCRS(result.mosaic, pt);
                queries.put(pt,trs);
            }
        }

        //paint tiles ----------------------------------------------------------
        if(queries.isEmpty()){
            //bypass if no queries
            return;
        }
        Integer maxTiles = (Integer)context2D.getRenderingHints().get(GO2Hints.KEY_MAX_TILES);
        if(maxTiles==null) maxTiles = 500;
        if( queries.size() > maxTiles) {
            LOGGER.log(Level.INFO, "Too much tiles required to render layer at this scale.");
            return;
        }

        final BlockingQueue<Object> queue;
        try {
            queue = result.mosaic.getTiles(queries.keySet(), hints);
        } catch (DataStoreException ex) {
            monitor.exceptionOccured(ex, Level.WARNING);
            return;
        }

        final StatelessContextParams params = new StatelessContextParams(getCanvas(), getUserObject());
        params.update(context2D);
        while(true){
            Object obj = null;
            try {
                obj = queue.poll(50, TimeUnit.MILLISECONDS);
            } catch (InterruptedException ex) {
                monitor.exceptionOccured(ex, Level.INFO);
            }

            if(monitor.stopRequested()){
                if(queue instanceof Cancellable){
                    ((Cancellable)queue).cancel();
                }
                break;
            }

            if(obj == GridMosaic.END_OF_QUEUE){
                break;
            }

            if(obj instanceof TileReference){
                final TileReference tile = (TileReference)obj;
                try {
                    paintTile(context2D, params, result.rules, result.pyramid.getId(), result.mosaic.getId(), tile);
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Error on tile : "+tile.getPosition()+". Input is : "+tile.getInput(), e);
                }
            }
        }

    }

    /**
     * {@inheritDoc }
     * @param context
     * @param mask
     * @param filter
     * @param graphics
     */
    @Override
    public List<Graphic> getGraphicAt(final RenderingContext context,
            final SearchArea mask, final VisitFilter filter, List<Graphic> graphics) {

        if(!(context instanceof RenderingContext2D) ) return graphics;
        if(!item.isSelectable())                     return graphics;
        if(!item.isVisible())                        return graphics;

        final RenderingContext2D renderingContext = (RenderingContext2D) context;

        //search special graphic builders
        final GraphicBuilder<GraphicJ2D> builder = (GraphicBuilder<GraphicJ2D>) item.getGraphicBuilder(GraphicJ2D.class);
        if(builder != null){
            //this layer hasa special graphic rendering, use it instead of normal rendering
            final Collection<GraphicJ2D> gras = builder.createGraphics(item, canvas);
            for(final GraphicJ2D gra : gras){
                graphics = gra.getGraphicAt(renderingContext, mask, filter,graphics);
            }
            return graphics;
        }

        if(graphics == null) graphics = new ArrayList<>();
        if(mask instanceof SearchAreaJ2D){
            graphics = searchAt(renderingContext,(SearchAreaJ2D)mask,filter,graphics);
        }else{
            graphics = searchAt(renderingContext,new DefaultSearchAreaJ2D(mask),filter,graphics);
        }

        return graphics;
    }

    private List<Graphic> searchAt(final RenderingContext2D context2D,
            final SearchAreaJ2D mask, final VisitFilter filter, List<Graphic> graphics) {

        //search tiles visible on this area
        final TileSetResult result = listTiles(context2D);
        if(result==null){
            //no pyramid or tiles match this context definition
            return graphics;
        }

        final Geometry searchGeom;
        try {
            searchGeom = JTS.transform(mask.getObjectiveGeometryJTS(), CRS.findOperation(context2D.getObjectiveCRS2D(), result.pyramidCRS2D, null).getMathTransform());
        } catch (Exception ex) {
            LOGGER.log(Level.INFO, ex.getMessage(),ex);
            return graphics;
        }

        final StatelessContextParams params = new StatelessContextParams(getCanvas(), getUserObject());
        params.update(context2D);

        //search for a tile which intersects the seach area
        final PyramidalCoverageResource covRef = (PyramidalCoverageResource) item.getCoverageReference();
        for(int tileCol=(int)result.tileMinCol; tileCol<result.tileMaxCol; tileCol++){
            for(int tileRow=(int)result.tileMinRow; tileRow<result.tileMaxRow; tileRow++){
                if(result.mosaic.isMissing(tileCol, tileRow)){
                    //tile not available
                    continue;
                }

                final Envelope tileEnvelope = result.mosaic.getEnvelope(tileCol, tileRow);
                final Geometry geom = JTS.toGeometry(tileEnvelope);

                if(searchGeom.intersects(geom)){
                    final ProjectedCoverage pc = asCoverage(context2D, params, covRef, result.pyramid.getId(), result.mosaic.getId(),tileCol,tileRow);
                    graphics.add(pc);
                }
            }
        }

        return graphics;
    }

    /**
     * File the tiles to read for rendering.
     *
     * @param context2D
     * @return
     */
    private TileSetResult listTiles(RenderingContext2D context2D){
        final TileSetResult result = new TileSetResult();

        final GenericName coverageName = item.getCoverageReference().getName();
        result.rules = GO2Utilities.getValidCachedRules(item.getStyle(),
                context2D.getSEScale(), coverageName,null);

        //we perform a first check on the style to see if there is at least
        //one valid rule at this scale, if not we just continue.
        if (result.rules.length == 0) {
            return null;
        }

        final CanvasMonitor monitor = context2D.getMonitor();
        final Envelope canvasEnv2D = context2D.getCanvasObjectiveBounds2D();
        final Envelope canvasEnv = context2D.getCanvasObjectiveBounds();

        //find the best pyramid
        try {
            result.pyramidSet = model.getPyramidSet();
        } catch (DataStoreException ex) {
            monitor.exceptionOccured(ex, Level.WARNING);
            return null;
        }

        try {
            result.pyramid = coverageFinder.findPyramid(result.pyramidSet, canvasEnv2D.getCoordinateReferenceSystem());
        } catch (FactoryException ex) {
            monitor.exceptionOccured(ex, Level.WARNING);
            return null;
        }

        if(result.pyramid == null){
            //no reliable pyramid
            return null;
        }

        //convert context envelope to pyramid CRS
        final CoordinateReferenceSystem pyramidCRS = result.pyramid.getCoordinateReferenceSystem();
        GeneralEnvelope wantedEnv2D;
        GeneralEnvelope wantedEnv;
        try {
            result.pyramidCRS2D = CRSUtilities.getCRS2D(pyramidCRS);
            wantedEnv2D = new GeneralEnvelope(Envelopes.transform(canvasEnv2D, result.pyramidCRS2D));
            wantedEnv = new GeneralEnvelope(ReferencingUtilities.transform(canvasEnv, pyramidCRS));
        } catch (TransformException ex) {
            monitor.exceptionOccured(ex, Level.WARNING);
            return null;
        }

        /*
         * Apply CoverageMapLayer query (if not null) to wantedEnv Envelope.
         */
        final Map<String, Double> queryValues = DefaultRasterSymbolizerRenderer.extractQuery(item);
        wantedEnv = new GeneralEnvelope(DefaultRasterSymbolizerRenderer.fixEnvelopeWithQuery(queryValues, wantedEnv, pyramidCRS));

        //ensure we don't go out of the crs envelope
        final Envelope maxExt = org.geotoolkit.referencing.CRS.getEnvelope(pyramidCRS);
        if(maxExt != null){
            wantedEnv2D.intersect(maxExt);
            if(Double.isNaN(wantedEnv2D.getMinimum(0))){ wantedEnv2D.setRange(0, maxExt.getMinimum(0), wantedEnv2D.getMaximum(0));  }
            if(Double.isNaN(wantedEnv2D.getMaximum(0))){ wantedEnv2D.setRange(0, wantedEnv2D.getMinimum(0), maxExt.getMaximum(0));  }
            if(Double.isNaN(wantedEnv2D.getMinimum(1))){ wantedEnv2D.setRange(1, maxExt.getMinimum(1), wantedEnv2D.getMaximum(1));  }
            if(Double.isNaN(wantedEnv2D.getMaximum(1))){ wantedEnv2D.setRange(1, wantedEnv2D.getMinimum(1), maxExt.getMaximum(1));  }
            wantedEnv.setRange(0, wantedEnv2D.getMinimum(0), wantedEnv2D.getMaximum(0));
            wantedEnv.setRange(1, wantedEnv2D.getMinimum(1), wantedEnv2D.getMaximum(1));
        }

        //find the correct pyramid
        final double wantedResolution;
        try {
            wantedResolution = GO2Utilities.pixelResolution(context2D, wantedEnv);
        } catch (TransformException ex) {
            monitor.exceptionOccured(ex, Level.WARNING);
            return null;
        }

        try {
            result.mosaic = coverageFinder.findMosaic(result.pyramid, wantedResolution, tolerance, wantedEnv,100);
        } catch (FactoryException ex) {
            monitor.exceptionOccured(ex, Level.WARNING);
            return null;
        }
        if(result.mosaic == null){
            //no reliable mosaic
            return null;
        }


        //we definitly do not want some NaN values
        if(Double.isNaN(wantedEnv.getMinimum(0))){ wantedEnv.setRange(0, Double.NEGATIVE_INFINITY, wantedEnv.getMaximum(0));  }
        if(Double.isNaN(wantedEnv.getMaximum(0))){ wantedEnv.setRange(0, wantedEnv.getMinimum(0), Double.POSITIVE_INFINITY);  }
        if(Double.isNaN(wantedEnv.getMinimum(1))){ wantedEnv.setRange(1, Double.NEGATIVE_INFINITY, wantedEnv.getMaximum(1));  }
        if(Double.isNaN(wantedEnv.getMaximum(1))){ wantedEnv.setRange(1, wantedEnv.getMinimum(1), Double.POSITIVE_INFINITY);  }


        final DirectPosition ul = result.mosaic.getUpperLeftCorner();
        final double tileMatrixMinX = ul.getOrdinate(0);
        final double tileMatrixMaxY = ul.getOrdinate(1);
        final Dimension gridSize = result.mosaic.getGridSize();
        final Dimension tileSize = result.mosaic.getTileSize();
        final double scale = result.mosaic.getScale();
        final double tileSpanX = scale * tileSize.width;
        final double tileSpanY = scale * tileSize.height;
        final int gridWidth = gridSize.width;
        final int gridHeight = gridSize.height;

        //find all the tiles we need --------------------------------------

        final double epsilon = 1e-6;
        final double bBoxMinX = wantedEnv.getMinimum(0);
        final double bBoxMaxX = wantedEnv.getMaximum(0);
        final double bBoxMinY = wantedEnv.getMinimum(1);
        final double bBoxMaxY = wantedEnv.getMaximum(1);
        result.tileMinCol = Math.floor( (bBoxMinX - tileMatrixMinX) / tileSpanX + epsilon);
        result.tileMaxCol = Math.floor( (bBoxMaxX - tileMatrixMinX) / tileSpanX - epsilon)+1;
        result.tileMinRow = Math.floor( (tileMatrixMaxY - bBoxMaxY) / tileSpanY - epsilon);
        result.tileMaxRow = Math.floor( (tileMatrixMaxY - bBoxMinY) / tileSpanY + epsilon)+1;

        //ensure we dont go out of the grid
        if(result.tileMinCol < 0) result.tileMinCol = 0;
        if(result.tileMaxCol > gridWidth) result.tileMaxCol = gridWidth;
        if(result.tileMinRow < 0) result.tileMinRow = 0;
        if(result.tileMaxRow > gridHeight) result.tileMaxRow = gridHeight;

        return result;
    }

    private ProjectedCoverage asCoverage(final RenderingContext2D context, StatelessContextParams params,
            PyramidalCoverageResource ref, String pyramidId, String mosaicId, int tileX, int tileY) {

        final GridCoverage2D coverage;
        try {
            coverage = AbstractPyramidalCoverageResource.getTileAsCoverage(ref,pyramidId,mosaicId,tileX,tileY);
        } catch (DataStoreException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            return null;
        }

        final CoverageMapLayer tilelayer = MapBuilder.createCoverageLayer(coverage, getUserObject().getStyle(), getUserObject().getName());
        tilelayer.setElevationModel(getUserObject().getElevationModel());
        return new ProjectedCoverage(params, tilelayer);
    }

    private ProjectedCoverage asCoverage(final RenderingContext2D context, StatelessContextParams params,
            PyramidalCoverageResource ref, String pyramidId, String mosaicId, TileReference tile) {

        final GridCoverage2D coverage;
        try {
            coverage = AbstractPyramidalCoverageResource.getTileAsCoverage(ref,pyramidId,mosaicId,tile);
        } catch (DataStoreException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            return null;
        }

        final CoverageMapLayer tilelayer = MapBuilder.createCoverageLayer(coverage, getUserObject().getStyle(), getUserObject().getName());
        tilelayer.setElevationModel(getUserObject().getElevationModel());
        return new ProjectedCoverage(params, tilelayer);
    }

    private void paintTile(final RenderingContext2D context, StatelessContextParams params, CachedRule[] rules,
            final String pyramidId, final String mosaicId, final TileReference tile) {
        final PyramidalCoverageResource covRef = (PyramidalCoverageResource) item.getCoverageReference();

        final ProjectedCoverage projectedCoverage = asCoverage(context,
                params, covRef, pyramidId, mosaicId, tile);
        for(final CachedRule rule : rules){
            for(final CachedSymbolizer symbol : rule.symbolizers()){
                try {
                    GO2Utilities.portray(projectedCoverage, symbol, context);
                } catch (PortrayalException ex) {
                    context.getMonitor().exceptionOccured(ex, Level.WARNING);
                }
            }
        }
    }

    @Override
    public void structureChanged(CoverageStoreManagementEvent event) {
    }

    @Override
    public void contentChanged(CoverageStoreContentEvent event) {
        if(item.isVisible() && getCanvas().isAutoRepaint()){
            //TODO should call a repaint only on this graphic
            getCanvas().repaint();
        }
    }


    private static class TileSetResult{
        //style informations
        private CachedRule[] rules;
        private CoordinateReferenceSystem pyramidCRS2D;
        //tiles informations
        private PyramidSet pyramidSet;
        private Pyramid pyramid;
        private GridMosaic mosaic;
        private double tileMinCol;
        private double tileMaxCol;
        private double tileMinRow;
        private double tileMaxRow;
    }

}
