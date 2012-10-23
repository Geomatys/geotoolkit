/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.geotoolkit.coverage.*;
import org.geotoolkit.display.canvas.control.CanvasMonitor;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.style.CachedRule;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.storage.DataStoreException;
import org.opengis.feature.type.Name;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

/**
 * Graphic for pyramidal coverage layers.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class StatefullPyramidalCoverageLayerJ2D extends StatefullMapLayerJ2D<CoverageMapLayer>{
    
    private final PyramidalModel model;
    private final double tolerance;
    private final Set<StatefullTileJ2D> gtiles = new HashSet<StatefullTileJ2D>();

    public StatefullPyramidalCoverageLayerJ2D(final J2DCanvas canvas, final StatefullMapItemJ2D parent, final CoverageMapLayer layer){
        super(canvas, parent, layer);
        
        model = (PyramidalModel)layer.getCoverageReference();
        tolerance = 0.1; // in % , TODO use a flag to allow change value
    }
    
    /**
     * {@inheritDoc }
     * @param context2D 
     */
    @Override
    public void paint(RenderingContext2D context2D) {
        
        if(!item.isVisible()) return;
        
        final Name coverageName = item.getCoverageName();
        final CachedRule[] rules = GO2Utilities.getValidCachedRules(item.getStyle(),
                context2D.getSEScale(), coverageName,null);

        //we perform a first check on the style to see if there is at least
        //one valid rule at this scale, if not we just continue.
        if (rules.length == 0) {
            return;
        }

        final CanvasMonitor monitor = context2D.getMonitor();

        final Envelope canvasEnv = context2D.getCanvasObjectiveBounds2D();   
        final PyramidSet pyramidSet;
        try {
            pyramidSet = model.getPyramidSet();
        } catch (DataStoreException ex) {
            monitor.exceptionOccured(ex, Level.WARNING);
            return;
        }
        
        final Pyramid pyramid = CoverageUtilities.findPyramid(pyramidSet, canvasEnv.getCoordinateReferenceSystem());
                
        if(pyramid == null){
            //no reliable pyramid
            return;
        }
        
        final CoordinateReferenceSystem pyramidCRS = pyramid.getCoordinateReferenceSystem();
        final GeneralEnvelope wantedEnv;
        try {
            wantedEnv = new GeneralEnvelope(CRS.transform(canvasEnv, pyramidCRS));
        } catch (TransformException ex) {
            monitor.exceptionOccured(ex, Level.WARNING);
            return;
        }

        //ensure we don't go out of the crs envelope
        final Envelope maxExt = CRS.getEnvelope(pyramidCRS);
        if(maxExt != null){
            wantedEnv.intersect(maxExt);
            if(Double.isNaN(wantedEnv.getMinimum(0))){ wantedEnv.setRange(0, maxExt.getMinimum(0), wantedEnv.getMaximum(0));  }
            if(Double.isNaN(wantedEnv.getMaximum(0))){ wantedEnv.setRange(0, wantedEnv.getMinimum(0), maxExt.getMaximum(0));  }
            if(Double.isNaN(wantedEnv.getMinimum(1))){ wantedEnv.setRange(1, maxExt.getMinimum(1), wantedEnv.getMaximum(1));  }
            if(Double.isNaN(wantedEnv.getMaximum(1))){ wantedEnv.setRange(1, wantedEnv.getMinimum(1), maxExt.getMaximum(1));  }
        }
        
        //the wanted image resolution
        final double wantedResolution;
        try {
            wantedResolution = GO2Utilities.pixelResolution(context2D, wantedEnv);
        } catch (TransformException ex) {
            monitor.exceptionOccured(ex, Level.WARNING);
            return;
        }

        final GridMosaic mosaic = CoverageUtilities.findMosaic(pyramid, wantedResolution, tolerance, wantedEnv,100);
        if(mosaic == null){
            //no reliable mosaic
            return;
        }
        
        
        //we definitly do not want some NaN values
        if(Double.isNaN(wantedEnv.getMinimum(0))){ wantedEnv.setRange(0, Double.NEGATIVE_INFINITY, wantedEnv.getMaximum(0));  }
        if(Double.isNaN(wantedEnv.getMaximum(0))){ wantedEnv.setRange(0, wantedEnv.getMinimum(0), Double.POSITIVE_INFINITY);  }
        if(Double.isNaN(wantedEnv.getMinimum(1))){ wantedEnv.setRange(1, Double.NEGATIVE_INFINITY, wantedEnv.getMaximum(1));  }
        if(Double.isNaN(wantedEnv.getMaximum(1))){ wantedEnv.setRange(1, wantedEnv.getMinimum(1), Double.POSITIVE_INFINITY);  }
        
        

        final double tileMatrixMinX = mosaic.getUpperLeftCorner().getX();
        final double tileMatrixMaxY = mosaic.getUpperLeftCorner().getY();
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
        double tileMinRow = Math.floor( (tileMatrixMaxY - bBoxMaxY) / tileSpanY + epsilon);
        double tileMaxRow = Math.floor( (tileMatrixMaxY - bBoxMinY) / tileSpanY - epsilon)+1;

        //ensure we dont go out of the grid
        if(tileMinCol < 0) tileMinCol = 0;
        if(tileMaxCol > gridWidth) tileMaxCol = gridWidth;
        if(tileMinRow < 0) tileMinRow = 0;
        if(tileMaxRow > gridHeight) tileMaxRow = gridHeight;
                
        //don't render layer if it requieres more then 100 queries
        if( (tileMaxCol-tileMinCol) * (tileMaxRow-tileMinRow) > 500 ){
            System.out.println("Too much tiles requiered to render layer at this scale.");
            return;
        }
        
        //tiles to render         
        final Set<Coordinate> ttiles = new HashSet<Coordinate>();
        
        for(int tileCol=(int)tileMinCol; tileCol<tileMaxCol; tileCol++){   
            for(int tileRow=(int)tileMinRow; tileRow<tileMaxRow; tileRow++){
                if(mosaic.isMissing(tileCol, tileRow)){
                    //tile not available
                    continue;
                }
                
                ttiles.add(new Coordinate(tileCol, tileRow, scale));
            }
        }

        //update graphic tiles -------------------------------------------------
        final Collection<StatefullTileJ2D> toRemove = new ArrayList<StatefullTileJ2D>();
        loop:
        for(StatefullTileJ2D st : gtiles){
            for(Coordinate c : ttiles){
                if(st.coordinate.equals3D(c)){
                    continue loop;
                }
            }
            toRemove.add(st);
        }
        gtiles.removeAll(toRemove);
        
        for(Coordinate c : ttiles){
            if(!gtiles.contains(c)){
                gtiles.add(new StatefullTileJ2D(mosaic, c, getCanvas(), this, item));
            }
        }
        
        
        //paint sub tiles ------------------------------------------------------
        for(final StatefullTileJ2D gt : gtiles){
            gt.paint(context2D);
        }
        
    }
    
}
