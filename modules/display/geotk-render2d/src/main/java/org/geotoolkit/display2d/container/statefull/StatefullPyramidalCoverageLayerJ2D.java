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
package org.geotoolkit.display2d.container.statefull;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import javax.vecmath.Point3d;
import org.geotoolkit.coverage.*;
import org.geotoolkit.coverage.finder.CoverageFinder;
import org.geotoolkit.coverage.finder.CoverageFinderFactory;
import org.geotoolkit.display.canvas.control.CanvasMonitor;
import org.geotoolkit.display2d.GO2Hints;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.style.CachedRule;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.apache.sis.storage.DataStoreException;
import static org.geotoolkit.storage.StorageListener.LOGGER;
import org.opengis.feature.type.Name;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * Graphic for pyramidal coverage layers.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class StatefullPyramidalCoverageLayerJ2D extends StatefullMapLayerJ2D<CoverageMapLayer> implements CoverageStoreListener{

    protected CoverageStoreListener.Weak weakStoreListener = new CoverageStoreListener.Weak(this);

    private final PyramidalCoverageReference model;
    private final double tolerance;
    private final CoverageFinder coverageFinder;
    private final Map<Point3d,StatefullTileJ2D> gtiles = new TreeMap<>(new Comparator<Point3d>() {
        @Override
        public int compare(Point3d c1, Point3d c2) {
            final double zdiff = c2.z - c1.z; //we want lower res tiles to be painted first

            if(zdiff > 0) return +1;
            else if(zdiff < 0) return -1;
            else{
                final double ydiff = c1.y - c2.y;
                if(ydiff > 0) return +1;
                else if(ydiff < 0) return -1;
                else{
                    final double xdiff = c1.x - c2.x;
                    if(xdiff > 0) return +1;
                    else if(xdiff < 0) return -1;
                    else return 0; //same point
                }
            }

        }
    });

    public StatefullPyramidalCoverageLayerJ2D(final J2DCanvas canvas, final CoverageMapLayer layer) {
        super(canvas, layer, true);
        this.coverageFinder = CoverageFinderFactory.createDefaultCoverageFinder();
        model = (PyramidalCoverageReference)layer.getCoverageReference();
        tolerance = 0.1; // in % , TODO use a flag to allow change value
        weakStoreListener.registerSource(layer.getCoverageReference());
    }

    public StatefullPyramidalCoverageLayerJ2D(final J2DCanvas canvas, final CoverageMapLayer layer, CoverageFinder coverageFinder) {
        super(canvas, layer, true);
        this.coverageFinder = coverageFinder;
        model = (PyramidalCoverageReference)layer.getCoverageReference();
        tolerance = 0.1; // in % , TODO use a flag to allow change value
        weakStoreListener.registerSource(layer.getCoverageReference());
    }

    /**
     * {@inheritDoc }
     * @param context2D
     */
    @Override
    public void paint(RenderingContext2D context2D) {

        if(!item.isVisible()) return;

        final Name coverageName = item.getCoverageReference().getName();
        final CachedRule[] rules = GO2Utilities.getValidCachedRules(item.getStyle(),
                context2D.getSEScale(), coverageName,null);

        //we perform a first check on the style to see if there is at least
        //one valid rule at this scale, if not we just continue.
        if (rules.length == 0) {
            return;
        }

        final CanvasMonitor monitor = context2D.getMonitor();

        final Envelope canvasEnv2D = context2D.getCanvasObjectiveBounds2D();
        final Envelope canvasEnv = context2D.getCanvasObjectiveBounds2D();
        final PyramidSet pyramidSet;
        try {
            pyramidSet = model.getPyramidSet();
        } catch (DataStoreException ex) {
            monitor.exceptionOccured(ex, Level.WARNING);
            return;
        }

        Pyramid pyramid = null;
        try {
            pyramid = coverageFinder.findPyramid(pyramidSet, canvasEnv.getCoordinateReferenceSystem());
        } catch (FactoryException ex) {
            monitor.exceptionOccured(ex, Level.WARNING);
            return;
        }

        if(pyramid == null){
            //no reliable pyramid
            return;
        }

        final CoordinateReferenceSystem pyramidCRS = pyramid.getCoordinateReferenceSystem();
        final CoordinateReferenceSystem pyramidCRS2D;
        GeneralEnvelope wantedEnv2D;
        GeneralEnvelope wantedEnv;
        try {
            pyramidCRS2D = CRSUtilities.getCRS2D(pyramidCRS);
            wantedEnv2D = new GeneralEnvelope(CRS.transform(canvasEnv2D, pyramidCRS2D));
            wantedEnv = new GeneralEnvelope(ReferencingUtilities.transform(canvasEnv, pyramidCRS));
        } catch (TransformException ex) {
            monitor.exceptionOccured(ex, Level.WARNING);
            return;
        }


        //ensure we don't go out of the crs envelope
        final Envelope maxExt = CRS.getEnvelope(pyramidCRS);
        if(maxExt != null){
            wantedEnv2D.intersect(maxExt);
            if(Double.isNaN(wantedEnv2D.getMinimum(0))){ wantedEnv2D.setRange(0, maxExt.getMinimum(0), wantedEnv2D.getMaximum(0));  }
            if(Double.isNaN(wantedEnv2D.getMaximum(0))){ wantedEnv2D.setRange(0, wantedEnv2D.getMinimum(0), maxExt.getMaximum(0));  }
            if(Double.isNaN(wantedEnv2D.getMinimum(1))){ wantedEnv2D.setRange(1, maxExt.getMinimum(1), wantedEnv2D.getMaximum(1));  }
            if(Double.isNaN(wantedEnv2D.getMaximum(1))){ wantedEnv2D.setRange(1, wantedEnv2D.getMinimum(1), maxExt.getMaximum(1));  }
            wantedEnv.setRange(0, wantedEnv2D.getMinimum(0), wantedEnv2D.getMaximum(0));
            wantedEnv.setRange(1, wantedEnv2D.getMinimum(1), wantedEnv2D.getMaximum(1));
        }

        //the wanted image resolution
        final double wantedResolution;
        try {
            wantedResolution = GO2Utilities.pixelResolution(context2D, wantedEnv);
        } catch (TransformException ex) {
            monitor.exceptionOccured(ex, Level.WARNING);
            return;
        }

        GridMosaic mosaic = null;
        try {
            mosaic = coverageFinder.findMosaic(pyramid, wantedResolution, tolerance, wantedEnv,100);
        } catch (FactoryException ex) {
            monitor.exceptionOccured(ex, Level.WARNING);
            return;
        }
        if(mosaic == null){
            //no reliable mosaic
            return;
        }

        GO2Utilities.removeNaN(wantedEnv);
        
        final DirectPosition ul = mosaic.getUpperLeftCorner();
        final double tileMatrixMinX = ul.getOrdinate(0);
        final double tileMatrixMaxY = ul.getOrdinate(1);
        final Dimension gridSize = mosaic.getGridSize();
        final Dimension tileSize = mosaic.getTileSize();
        final double scale = mosaic.getScale();
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
        double tileMinCol = Math.floor( (bBoxMinX - tileMatrixMinX) / tileSpanX + epsilon);
        double tileMaxCol = Math.floor( (bBoxMaxX - tileMatrixMinX) / tileSpanX - epsilon)+1;
        double tileMinRow = Math.floor( (tileMatrixMaxY - bBoxMaxY) / tileSpanY - epsilon);
        double tileMaxRow = Math.floor( (tileMatrixMaxY - bBoxMinY) / tileSpanY + epsilon)+1;

        //ensure we dont go out of the grid
        if(tileMinCol < 0) tileMinCol = 0;
        if(tileMaxCol > gridWidth) tileMaxCol = gridWidth;
        if(tileMinRow < 0) tileMinRow = 0;
        if(tileMaxRow > gridHeight) tileMaxRow = gridHeight;

        //don't render layer if it requieres more then 100 queries
        Integer maxTiles = (Integer)context2D.getRenderingHints().get(GO2Hints.KEY_MAX_TILES);
        if(maxTiles==null) maxTiles = 500;
        if( (tileMaxCol-tileMinCol) * (tileMaxRow-tileMinRow) > maxTiles) {
            LOGGER.log(Level.INFO, "Too much tiles requiered to render layer at this scale.");
            return;
        }

        //tiles to render
        final Set<Point3d> ttiles = new HashSet<>();

        for(int tileCol=(int)tileMinCol; tileCol<tileMaxCol; tileCol++){
            for(int tileRow=(int)tileMinRow; tileRow<tileMaxRow; tileRow++){
                if(mosaic.isMissing(tileCol, tileRow)){
                    //tile not available
                    continue;
                }

                ttiles.add(new Point3d(tileCol, tileRow, scale));
            }
        }

        //update graphic tiles -------------------------------------------------
        final Collection<Point3d> toRemove = new ArrayList<>();
        loop:
        for(StatefullTileJ2D st : gtiles.values()){
            if(ttiles.contains(st.getCoordinate())){
                continue loop;
            }

            //avoids loading the tile if it's already in the update queue
            st.setObsoleted(true);

            if(st.isLoaded()){
                final Point3d[] coords = getReplacements(pyramid, st.getCoordinate(), mosaic,
                        tileMinCol,tileMaxCol,tileMinRow,tileMaxRow);
                for(Point3d c : coords){
                    if(!ttiles.contains(c)) continue;

                    final StatefullTileJ2D rst = gtiles.get(c);
                    if(rst == null || !rst.isLoaded()){
                        //replacement tiles are not all loaded, keep the previous tile
                        continue loop;
                    }
                }
            }

            toRemove.add(st.getCoordinate());
        }

        //remove old tiles
        for(Point3d pt : toRemove){
            StatefullTileJ2D tile = gtiles.remove(pt);
            getChildren().remove(tile);
        }

        //add new tiles
        for(Point3d c : ttiles){
            if(!gtiles.containsKey(c)){
                StatefullTileJ2D tile = new StatefullTileJ2D(mosaic, c, getCanvas(), item, rules);
                gtiles.put(c,tile);
                getChildren().add(tile);
            }
        }

        //paint sub tiles ------------------------------------------------------
        final Object[] cp = getChildren().toArray();
        for(Object obj : cp){
            ((StatefullTileJ2D)obj).paint(context2D);
        }
    }

    @Override
    protected synchronized void update() {
    }

    @Override
    public void structureChanged(CoverageStoreManagementEvent event) {
    }

    @Override
    public void contentChanged(CoverageStoreContentEvent event) {
        //TODO should call a repaint only on this graphic
        gtiles.clear();
        getCanvas().repaint();
    }

    private Point3d[] getReplacements(Pyramid pyramid, Point3d coord, final GridMosaic mosaicUpdate,
            double qtileMinCol, double qtileMaxCol, double qtileMinRow, double qtileMaxRow){
        double[] tscales = pyramid.getScales();

        final int indexBase = Arrays.binarySearch(tscales, coord.z);
        final GridMosaic mosaicBase = pyramid.getMosaics(indexBase).iterator().next();
        final Envelope env = mosaicBase.getEnvelope((int)coord.x, (int)coord.y);

        double bBoxMinX = env.getMinimum(0);
        double bBoxMinY = env.getMinimum(1);
        double bBoxMaxX = env.getMaximum(0);
        double bBoxMaxY = env.getMaximum(1);

        final DirectPosition ul = mosaicUpdate.getUpperLeftCorner();
        final double tileMatrixMinX = ul.getOrdinate(0);
        final double tileMatrixMaxY = ul.getOrdinate(1);
        final Dimension gridSize = mosaicUpdate.getGridSize();
        final Dimension tileSize = mosaicUpdate.getTileSize();
        final double scale = mosaicUpdate.getScale();
        final double tileSpanX = scale * tileSize.width;
        final double tileSpanY = scale * tileSize.height;
        final int gridWidth = gridSize.width;
        final int gridHeight = gridSize.height;

        final double epsilon = 1e-6;
        double tileMinCol = Math.floor( (bBoxMinX - tileMatrixMinX) / tileSpanX + epsilon);
        double tileMaxCol = Math.floor( (bBoxMaxX - tileMatrixMinX) / tileSpanX - epsilon)+1;
        double tileMinRow = Math.floor( (tileMatrixMaxY - bBoxMaxY) / tileSpanY + epsilon);
        double tileMaxRow = Math.floor( (tileMatrixMaxY - bBoxMinY) / tileSpanY - epsilon)+1;
        tileMinCol = Math.max(tileMinCol, qtileMinCol);
        tileMaxCol = Math.min(tileMaxCol, qtileMaxCol);
        tileMinRow = Math.max(tileMinRow, qtileMinRow);
        tileMaxRow = Math.min(tileMaxRow, qtileMaxRow);

        //ensure we dont go out of the grid
        if(tileMinCol < 0) tileMinCol = 0;
        if(tileMaxCol > gridWidth) tileMaxCol = gridWidth;
        if(tileMinRow < 0) tileMinRow = 0;
        if(tileMaxRow > gridHeight) tileMaxRow = gridHeight;

        //tiles to render
        final Set<Point3d> ttiles = new HashSet<>();

        if( ((tileMaxCol-tileMinCol)*(tileMaxRow-tileMinRow)) > 100){

        }

        for(int tileCol=(int)tileMinCol; tileCol<tileMaxCol; tileCol++){
            for(int tileRow=(int)tileMinRow; tileRow<tileMaxRow; tileRow++){
                if(mosaicUpdate.isMissing(tileCol, tileRow)){
                    //tile not available
                    continue;
                }
                ttiles.add(new Point3d(tileCol, tileRow, scale));
            }
        }

        return ttiles.toArray(new Point3d[0]);
    }
}
