/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.client.map;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.geotoolkit.client.Request;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.canvas.control.CanvasMonitor;
import org.geotoolkit.display.primitive.SearchArea;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;

import org.opengis.display.primitive.Graphic;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 * Abstract graphic rendering tiled services.
 * Child class must provide a pyramid definition.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractPyramidGraphic extends AbstractTiledGraphic{

    private final double tolerance;
    
    /**
     * 
     * @param canvas
     * @param crs
     * @param resolutionTolerance : since tiles are at given scale level
     * if tile contain texts it is not always efficient to take the highest mosaic
     * above. it might be better to have text slightly bigger rather then N times bigger.
     * value in %.
     */
    public AbstractPyramidGraphic(final J2DCanvas canvas, 
            final CoordinateReferenceSystem crs, final double resolutionTolerance){
        super(canvas, crs);
        this.tolerance = resolutionTolerance / 100d;
    }
    
    protected abstract PyramidSet getPyramidSet();
    
    protected abstract Request createRequest(GridMosaic mosaic, int col, int row);
    
    @Override
    public void paint(final RenderingContext2D context2D) {
        final CanvasMonitor monitor = context2D.getMonitor();

        final Envelope canvasEnv = context2D.getCanvasObjectiveBounds2D();   
        final PyramidSet pyramidSet = getPyramidSet();
        
        final Pyramid pyramid = findOptimalPyramid(pyramidSet, canvasEnv.getCoordinateReferenceSystem());
                
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

        final GridMosaic mosaic = findOptimalMosaic(pyramid, wantedResolution, tolerance);
        if(mosaic == null){
            //no reliable mosaic
            return;
        }
        

        final double tileMatrixMinX = mosaic.getUpperLeftCorner().getX();
        final double tileMatrixMaxY = mosaic.getUpperLeftCorner().getY();
        final int gridWidth = mosaic.getWidth();
        final int gridHeight = mosaic.getHeight();
        final double tileWidth = mosaic.getTileWidth();
        final double tileHeight = mosaic.getTileHeight();
        final double tileSpanX = mosaic.getTileSpanX();
        final double tileSpanY = mosaic.getTileSpanY();

        //find all the tiles we need --------------------------------------
        //tiles to render         
        final Map<Entry<CoordinateReferenceSystem,MathTransform>,Request> queries = 
                new HashMap<Entry<CoordinateReferenceSystem, MathTransform>, Request>();

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
        if(tileMaxCol > gridWidth) tileMaxCol = gridWidth-1;
        if(tileMinRow < 0) tileMinRow = 0;
        if(tileMaxRow > gridHeight) tileMaxRow = gridHeight-1;
                
        //don't render layer if it requieres more then 100 queries
        if( (tileMaxCol-tileMinCol) * (tileMaxRow-tileMinRow) > 100 ){
            System.out.println("Too much tiles requiered to render layer at this scale.");
            return;
        }
        
        loopCol:
        for(int tileCol=(int)tileMinCol; tileCol<tileMaxCol; tileCol++){
            
            loopRow:
            for(int tileRow=(int)tileMinRow; tileRow<tileMaxRow; tileRow++){

                if(mosaic.isMissing(tileCol, tileRow)){
                    //tile not available
                    continue;
                }
                
                //tile bbox
                final double leftX  = tileMatrixMinX + tileCol * tileSpanX ;
                final double upperY = tileMatrixMaxY - tileRow * tileSpanY;
                final double rightX = tileMatrixMinX + (tileCol+1) * tileSpanX;
                final double lowerY = tileMatrixMaxY - (tileRow+1) * tileSpanY;

                final double scaleX = (rightX - leftX) / tileWidth ;
                final double scaleY = (upperY - lowerY) / tileHeight ;

                final MathTransform gridToCRS = new AffineTransform2D(
                        scaleX, 0, 0, -scaleY, leftX, upperY);

                final Request request = createRequest(mosaic, tileCol, tileRow);

                final Entry<CoordinateReferenceSystem,MathTransform> key;
                key = new SimpleImmutableEntry<CoordinateReferenceSystem, MathTransform>(pyramidCRS, gridToCRS);

                queries.put(key,request);
                //break loopCol;
            }
        }

        paint(context2D, queries);
    }

    @Override
    public List<Graphic> getGraphicAt(RenderingContext context, SearchArea mask, VisitFilter filter, List<Graphic> graphics) {
        if(!(context instanceof RenderingContext2D)){
            return graphics;
        }
        graphics.add(this);
        return graphics;
    }
    
    private static Pyramid findOptimalPyramid(final PyramidSet set, final CoordinateReferenceSystem crs){
        
        Pyramid result = null;
        for(Pyramid pyramid : set.getPyramids()){
            
            if(result == null){
                result = pyramid;
            }
            
            if(CRS.equalsApproximatively(pyramid.getCoordinateReferenceSystem(),crs)){
                //we found a pyramid for this crs
                result = pyramid;
                break;
            }
            
        }
        
        return result;
    }
    
    private static GridMosaic findOptimalMosaic(final Pyramid pyramid, final double resolution, final double tolerance){
        
        GridMosaic result = null;
        
        final double[] scales = pyramid.getScales();
        
        for(int i=0;i<scales.length;i++){
            
            if(result == null){
                result = pyramid.getMosaic(i);
            }
            
            if( (scales[i] * (1-tolerance)) < resolution){
                //we found the most accurate resolution
                result = pyramid.getMosaic(i);
                break;
            }else{
                result = pyramid.getMosaic(i);
            }            
        }
                
        return result;
        
    }
    
}
