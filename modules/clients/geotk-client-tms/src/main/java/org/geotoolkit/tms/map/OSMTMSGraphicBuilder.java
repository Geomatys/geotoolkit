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

package org.geotoolkit.tms.map;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.geotoolkit.client.Request;
import org.geotoolkit.client.map.AbstractTiledGraphic;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.canvas.control.CanvasMonitor;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display.primitive.SearchArea;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.map.GraphicBuilder;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;
import org.geotoolkit.tms.GetTileRequest;
import org.geotoolkit.tms.OSMTileMapServer;

import org.opengis.display.canvas.Canvas;
import org.opengis.display.primitive.Graphic;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 * Render TMS layer in default geotoolkit rendering engine.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class OSMTMSGraphicBuilder implements GraphicBuilder<GraphicJ2D>{
                    
    /**
     * One instance for all WMTS map layers. Object is concurrent.
     */
    static final OSMTMSGraphicBuilder INSTANCE = new OSMTMSGraphicBuilder();
    
    private OSMTMSGraphicBuilder(){};
    
    @Override
    public Collection<GraphicJ2D> createGraphics(final MapLayer layer, final Canvas canvas) {
        if(layer instanceof OSMTMSMapLayer && canvas instanceof J2DCanvas){
            return Collections.singleton((GraphicJ2D)
                    new TMSGraphic((J2DCanvas)canvas, (OSMTMSMapLayer)layer));
        }else{
            return Collections.emptyList();
        }
    }

    @Override
    public Class<GraphicJ2D> getGraphicType() {
        return GraphicJ2D.class;
    }

    @Override
    public Image getLegend(final MapLayer layer) throws PortrayalException {
        final OSMTMSMapLayer tmsLayer = (OSMTMSMapLayer) layer;
        
        //TODO : how could we generate a proper legend for this layer ...
        final BufferedImage buffer = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        return buffer;
    }

    public static class TMSGraphic extends AbstractTiledGraphic{
        
        private final OSMTMSMapLayer layer;

        private TMSGraphic(final J2DCanvas canvas, final OSMTMSMapLayer layer){
            super(canvas,canvas.getObjectiveCRS2D());
            this.layer = layer;
        }

        @Override
        public void paint(final RenderingContext2D context2D) {
            final CanvasMonitor monitor = context2D.getMonitor();


            final GeneralEnvelope env = new GeneralEnvelope(context2D.getCanvasObjectiveBounds2D());
            final Dimension dim = context2D.getCanvasDisplayBounds().getSize();
            final double[] resolution = context2D.getResolution(context2D.getDisplayCRS());

            //resolution contain dpi adjustments, to obtain an image of the correct dpi
            //we raise the request dimension so that when we reduce it it will have the
            //wanted dpi.
            dim.width /= resolution[0];
            dim.height /= resolution[1];
            
            final OSMTileMapServer server = layer.getServer();
            
            final CoordinateReferenceSystem matrixCRS = server.getCoordinateReferenceSystem();
            final GeneralEnvelope matrixEnv;
            try {
                matrixEnv = new GeneralEnvelope(CRS.transform(env, matrixCRS));
            } catch (TransformException ex) {
                monitor.exceptionOccured(ex, Level.WARNING);
                return;
            }
            
            //ensure we don't go out of the crs envelope
            final Envelope maxExt = CRS.getEnvelope(matrixCRS);
            if(maxExt != null){
                matrixEnv.intersect(maxExt);
                if(Double.isNaN(matrixEnv.getMinimum(0))){ matrixEnv.setRange(0, maxExt.getMinimum(0), matrixEnv.getMaximum(0));  }
                if(Double.isNaN(matrixEnv.getMaximum(0))){ matrixEnv.setRange(0, matrixEnv.getMinimum(0), maxExt.getMaximum(0));  }
                if(Double.isNaN(matrixEnv.getMinimum(1))){ matrixEnv.setRange(1, maxExt.getMinimum(1), matrixEnv.getMaximum(1));  }
                if(Double.isNaN(matrixEnv.getMaximum(1))){ matrixEnv.setRange(1, matrixEnv.getMinimum(1), maxExt.getMaximum(1));  }
            }
            
            final double scale0Resolution = maxExt.getSpan(0) / OSMTMSUtilities.BASE_TILE_SIZE;
            
            //the wanted image resolution
            final double wantedResolution = matrixEnv.getSpan(0) / dim.getWidth() ;
            final double result = Math.log(wantedResolution/scale0Resolution) / Math.log(0.5d);        
            int scale = Math.round( (float)result );
            if(scale < 0){ scale = 0; }
            if(scale > server.getMaxZoomLevel()){ scale = server.getMaxZoomLevel(); }
                                           
            final double tileMatrixMinX = maxExt.getMinimum(0);
            final double tileMatrixMaxY = maxExt.getMaximum(1);
            final double tileWidth = OSMTMSUtilities.BASE_TILE_SIZE;
            final double tileHeight = OSMTMSUtilities.BASE_TILE_SIZE;
            final double tileSpanX = (maxExt.getSpan(0) / (Math.pow(2, scale) ));
            final double tileSpanY = (maxExt.getSpan(1) / (Math.pow(2, scale) ));
            
            //find all the tiles we need --------------------------------------
            //tiles to render         
            final Map<Entry<CoordinateReferenceSystem,MathTransform>,Request> queries = 
                    new HashMap<Entry<CoordinateReferenceSystem, MathTransform>, Request>();
            
            final double epsilon = 1e-6;
            final double bBoxMinX = matrixEnv.getMinimum(0);
            final double bBoxMaxX = matrixEnv.getMaximum(0);
            final double bBoxMinY = matrixEnv.getMinimum(1);
            final double bBoxMaxY = matrixEnv.getMaximum(1);
            double tileMinCol = Math.floor( (bBoxMinX - tileMatrixMinX) / tileSpanX + epsilon);
            double tileMaxCol = Math.floor( (bBoxMaxX - tileMatrixMinX) / tileSpanX - epsilon)+1;
            double tileMinRow = Math.floor( (tileMatrixMaxY - bBoxMaxY) / tileSpanY + epsilon);
            double tileMaxRow = Math.floor( (tileMatrixMaxY - bBoxMinY) / tileSpanY - epsilon)+1;
            
            System.out.println(">>>>>> X["+ tileMinCol +" ... "+ tileMaxCol + "]  Y["+ tileMinRow +" ... "+ tileMaxRow +"]");
            
            for(int tileCol=(int)tileMinCol; tileCol<tileMaxCol; tileCol++){
                for(int tileRow=(int)tileMinRow; tileRow<tileMaxRow; tileRow++){
                    
                    //tile bbox
                    final double leftX  = tileMatrixMinX + tileCol * tileSpanX ;
                    final double upperY = tileMatrixMaxY - tileRow * tileSpanY;
                    final double rightX = tileMatrixMinX + (tileCol+1) * tileSpanX;
                    final double lowerY = tileMatrixMaxY - (tileRow+1) * tileSpanY;

                    final double scaleX = (rightX - leftX) / tileWidth ;
                    final double scaleY = (upperY - lowerY) / tileHeight ;
                    
                    final MathTransform gridToCRS = new AffineTransform2D(
                            scaleX, 0, 0, -scaleY, leftX, upperY);
                    
                    final GetTileRequest request = server.createGetTile();
                    request.setScaleLevel(scale);
                    request.setTileCol(tileCol);
                    request.setTileRow(tileRow);
                                        
                    final Entry<CoordinateReferenceSystem,MathTransform> key;
                    key = new SimpleImmutableEntry<CoordinateReferenceSystem, MathTransform>(matrixCRS, gridToCRS);

                    queries.put(key,request);
                    
                }
            }
                               
            paint(context2D, queries);            
            
        }

        @Override
        public List<Graphic> getGraphicAt(final RenderingContext context, final SearchArea mask, final VisitFilter filter, final List<Graphic> graphics) {
            if(!(context instanceof RenderingContext2D)){
                return graphics;
            }
            graphics.add(this);
            return graphics;
        }
        
    }

}
