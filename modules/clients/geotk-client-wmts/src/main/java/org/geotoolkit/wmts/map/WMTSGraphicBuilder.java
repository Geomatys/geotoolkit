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

package org.geotoolkit.wmts.map;

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
import org.geotoolkit.wmts.GetTileRequest;
import org.geotoolkit.wmts.WebMapTileServer;
import org.geotoolkit.wmts.xml.v100.ContentsType;
import org.geotoolkit.wmts.xml.v100.LayerType;
import org.geotoolkit.wmts.xml.v100.Style;
import org.geotoolkit.wmts.xml.v100.TileMatrix;
import org.geotoolkit.wmts.xml.v100.TileMatrixLimits;
import org.geotoolkit.wmts.xml.v100.TileMatrixSet;
import org.geotoolkit.wmts.xml.v100.TileMatrixSetLimits;
import org.geotoolkit.wmts.xml.v100.TileMatrixSetLink;

import org.opengis.display.canvas.Canvas;
import org.opengis.display.primitive.Graphic;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * Render WMTS layer in default geotoolkit rendering engine.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class WMTSGraphicBuilder implements GraphicBuilder<GraphicJ2D>{
                    
    /**
     * One instance for all WMTS map layers. Object is concurrent.
     */
    static final WMTSGraphicBuilder INSTANCE = new WMTSGraphicBuilder();
    
    private WMTSGraphicBuilder(){};
    
    @Override
    public Collection<GraphicJ2D> createGraphics(final MapLayer layer, final Canvas canvas) {
        if(layer instanceof WMTSMapLayer && canvas instanceof J2DCanvas){
            return Collections.singleton((GraphicJ2D)
                    new WMTSGraphic((J2DCanvas)canvas, (WMTSMapLayer)layer));
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
        final WMTSMapLayer wmtsLayer = (WMTSMapLayer) layer;
        
        //TODO : how could we generate a proper legend for this layer ...
        final BufferedImage buffer = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        return buffer;
    }

    public static class WMTSGraphic extends AbstractTiledGraphic{
        
        private final WMTSMapLayer layer;

        private WMTSGraphic(final J2DCanvas canvas, final WMTSMapLayer layer){
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
            
            final WebMapTileServer server = layer.getServer();
            final ContentsType contents = server.getCapabilities().getContents();
            final LayerType wmtsLayer = WMTSUtilities.getLayer(server, layer.getLayer());            
            
            //find the tile set to use
            final String wantedTileSet = layer.getTileSet();            
            TileMatrixSetLink link = null;
            
            if(wantedTileSet == null){
                //find the set with the best crs
                link = WMTSUtilities.getOptimalTileSet(
                        server, wmtsLayer, env.getCoordinateReferenceSystem());
            }else{
                //search the wanted tile set
                for(TileMatrixSetLink lk : wmtsLayer.getTileMatrixSetLink()){
                    if(wantedTileSet.equals(lk.getTileMatrixSet())){
                        link = lk;
                        break;
                    }
                }
            }
            
            if(link == null){
                //no reliable tile set
                return;
            }
            
            final TileMatrixSet matrixSet = contents.getTileMatrixSetByIdentifier(link.getTileMatrixSet());                        
            final CoordinateReferenceSystem matrixCRS;
            final GeneralEnvelope matrixEnv;
            try {
                matrixCRS = CRS.decode(matrixSet.getSupportedCRS());
                matrixEnv = new GeneralEnvelope(CRS.transform(env, matrixCRS));
            } catch (NoSuchAuthorityCodeException ex) {
                monitor.exceptionOccured(ex, Level.WARNING);
                return;
            } catch (FactoryException ex) {
                monitor.exceptionOccured(ex, Level.WARNING);
                return;
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
            
            
            //resolution in crs unit by pixel
            final double wantedResolution = matrixEnv.getSpan(0)/ dim.width;     
            
            //find the best matrix ---------------------------------------------
            double unitByPixel = Double.MAX_VALUE;
            TileMatrix matrix = null;
            for(TileMatrix candidate : matrixSet.getTileMatrix()){
                
                final double candidateUnitByPixel = WMTSUtilities.unitsByPixel(matrixSet, matrixCRS, candidate);
                
                if(matrix == null){
                    matrix = candidate;
                    unitByPixel = candidateUnitByPixel;
                } else if(candidateUnitByPixel > wantedResolution && candidateUnitByPixel < unitByPixel) {
                    matrix = candidate;
                    unitByPixel = candidateUnitByPixel;
                } else {
                    //we found the most accurate matrix
                    matrix = candidate;
                    unitByPixel = candidateUnitByPixel;
                    break;
                }
            }
            
            if(matrix == null){
                //no appropriate matrix found
                return;
            }
                                            
            //find all the tiles we need --------------------------------------
            //tiles to render         
            final Map<Entry<CoordinateReferenceSystem,MathTransform>,Request> queries = 
                    new HashMap<Entry<CoordinateReferenceSystem, MathTransform>, Request>();
                               
            //the tiles index we will need
            //start at max available extent
            int minX = 0;
            int maxX = matrix.getMatrixWidth();
            int minY = 0;
            int maxY = matrix.getMatrixHeight();
            
            //reduce to limits if defined
            final TileMatrixSetLimits limitSet = link.getTileMatrixSetLimits();
            if(limitSet != null){
                final List<TileMatrixLimits> limits = limitSet.getTileMatrixLimits();
                if(limits != null){
                    for(final TileMatrixLimits limit : limits){
                        if(matrix.getIdentifier().getValue().equals(limit.getTileMatrix())){
                            minX = Math.max(minX, limit.getMinTileCol());
                            maxX = Math.min(maxX, limit.getMaxTileCol());
                            minY = Math.max(minY, limit.getMinTileRow());
                            maxY = Math.min(maxY, limit.getMaxTileRow());                            
                        }
                    }
                }
            }
            
            //reduce to envelope
            final List<Double> topleftcorner = matrix.getTopLeftCorner();
            final double tileMatrixMinX = topleftcorner.get(0);
            final double tileMatrixMaxY = topleftcorner.get(1); 
            final double tileWidth = matrix.getTileWidth();
            final double tileHeight = matrix.getTileHeight();
            
            // tileSpanX = tileWidth × pixelSpan;
            final double tileSpanX = tileWidth * unitByPixel;
            // tileSpanY = tileHeight × pixelSpan;
            final double tileSpanY = tileHeight * unitByPixel;
            
            System.out.println(">>>>>>>>>> X["+ minX +"..."+ maxX +"]  Y["+ minY +"..."+ maxY +"]");
            
            // to compensate for floating point computation inaccuracies
//            final double epsilon = 1e-6;
//            double tileMinCol = Math.floor( (bBoxMinX - tileMatrixMinX) / tileSpanX + epsilon);
//            double tileMaxCol = Math.floor( (bBoxMaxX - tileMatrixMinX) / tileSpanX - epsilon);
//            double tileMinRow = Math.floor( (tileMatrixMaxY - bBoxMaxY) / tileSpanY + epsilon);
//            double tileMaxRow = Math.floor( (tileMatrixMaxY - bBoxMinY) / tileSpanY - epsilon);

            
//            minX = Math.max(minX, (int)( Math.abs( (cornerOffsetX-matrixEnv.getMinimum(0))/tileSpanX)) );
//            maxX = Math.min(maxX, (int)( Math.abs( (cornerOffsetX-matrixEnv.getMaximum(0))/tileSpanX)) );
//            minY = Math.max(minY, (int)( Math.abs( (cornerOffsetY-matrixEnv.getMaximum(1))/tileSpanY)) );
//            maxY = Math.min(maxY, (int)( Math.abs( (cornerOffsetY-matrixEnv.getMinimum(1))/tileSpanY)) );    
//            
//            matrixEnv.getMinimum(0);
            
            
            
            System.out.println(">>>>>>>>>> " + matrix.getIdentifier().getValue());
            System.out.println(">>>>>>>>>> X["+ minX +"..."+ maxX +"]  Y["+ minY +"..."+ maxY +"]");
                           
            //find the style
            String style = layer.getTileSetStyle();
            if(style == null){
                //get the default style
                for(Style st : wmtsLayer.getStyle()){
                    if(style == null){
                        style = st.getIdentifier().getValue();
                    }
                    if(st.isIsDefault()){
                        break;
                    }
                }
            }
            
            //create requests
            for(int tileCol=minX; tileCol<maxX; tileCol++){
                for(int tileRow=minY; tileRow<maxY; tileRow++){

                    //tile bbox
                    final double leftX  = tileMatrixMinX + tileCol * tileSpanX ;
                    final double upperY = tileMatrixMaxY - tileRow * tileSpanY;
                    final double rightX = tileMatrixMinX + (tileCol+1) * tileSpanX;
                    final double lowerY = tileMatrixMaxY - (tileRow+1) * tileSpanY;

                    final double scaleX = (rightX - leftX) / tileWidth ;
                    final double scaleY = (upperY - lowerY) / tileHeight ;
                    
                    final AffineTransform2D gridToCRS = new AffineTransform2D(
                        scaleX, 0, 0, -scaleY, leftX, upperY);
                    

                    final GetTileRequest request = server.createGetTile();
                    request.setFormat(layer.getFormat());
                    request.setLayer(layer.getLayer());
                    request.setStyle(style);
                    request.setTileMatrixSet(matrixSet.getIdentifier().getValue());
                    request.setTileMatrix(matrix.getIdentifier().getValue());
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
