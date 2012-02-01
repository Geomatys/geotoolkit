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

package org.geotoolkit.process.coverage.pyramid;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.BitSet;
import org.geotoolkit.coverage.GridMosaic;
import org.geotoolkit.coverage.Pyramid;
import org.geotoolkit.coverage.PyramidalModel;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.J2DCanvasBuffered;
import org.geotoolkit.display2d.container.ContextContainer2D;
import org.geotoolkit.display2d.container.DefaultContextContainer2D;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.process.AbstractProcess;
import org.geotoolkit.process.ProcessEvent;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.storage.DataStoreException;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

/**
 * Create a pyramid in the given PyramidalModel.
 * If a pyramid with the given CRS already exist it will be reused.
 * If a mosaic at the given scale exist it will be used.
 * Missing tiles in the mosaic will be generated.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class MapcontextPyramidProcess extends AbstractProcess{

    MapcontextPyramidProcess(final ParameterValueGroup input){
        super(MapcontextPyramidDescriptor.INSTANCE,input);
    }

    @Override
    public ParameterValueGroup call() throws ProcessException{
        if (inputParameters == null) {
            fireFailEvent(new ProcessEvent(this,
                    "Input parameters not set.",0,
                    new NullPointerException("Input parameters not set.")));
        }

        final MapContext context = (MapContext) inputParameters.parameter(
                MapcontextPyramidDescriptor.IN_MAPCONTEXT.getName().getCode()).getValue();
        final Envelope envelope = (Envelope) inputParameters.parameter(
                MapcontextPyramidDescriptor.IN_EXTENT.getName().getCode()).getValue();
        final Dimension tileSize = (Dimension) inputParameters.parameter(
                MapcontextPyramidDescriptor.IN_TILE_SIZE.getName().getCode()).getValue();
        final double[] scales = (double[]) inputParameters.parameter(
                MapcontextPyramidDescriptor.IN_SCALES.getName().getCode()).getValue();
        final PyramidalModel container = (PyramidalModel) inputParameters.parameter(
                MapcontextPyramidDescriptor.IN_CONTAINER.getName().getCode()).getValue();
        
        //find if we already have a pyramid in the given CRS
        Pyramid pyramid = null;
        final CoordinateReferenceSystem crs = envelope.getCoordinateReferenceSystem();
        try{
            for(Pyramid candidate : container.getPyramidSet().getPyramids()){
                if(CRS.equalsIgnoreMetadata(crs, candidate.getCoordinateReferenceSystem())){
                    pyramid = candidate;
                    break;
                }
            }
            
            if(pyramid == null){
                //we didn't find a pyramid, create one
                pyramid = container.createPyramid(crs);
            }

            //prepare a J2DCanvas to render several tiles in the same tile
            //we consider a 2000*2000 size to be the maximum, which is 16Mb in memory
            final int nbtileonwidth;
            final int nbtileonheight;
            final Dimension maxCanvasSize;
            //will be used when cutting the canvas buffer in tiles
            final BufferedImage tileBuffer;
            final GeneralEnvelope canvasEnv = new GeneralEnvelope(crs);
            if(tileSize.width > 2000 || tileSize.height > 2000){
                //tiles are big already, we will generate them one by one
                nbtileonwidth = 1;
                nbtileonheight = 1;
                maxCanvasSize = tileSize;
                tileBuffer = null;
            }else{
                //tiles are small, we generate several in one painting pass
                nbtileonwidth = 2000 / tileSize.width;
                nbtileonheight = 2000 / tileSize.height;
                maxCanvasSize = new Dimension(nbtileonwidth*tileSize.width, nbtileonheight*tileSize.height);
                tileBuffer = new BufferedImage(tileSize.width, tileSize.height, BufferedImage.TYPE_INT_ARGB);
            }
            final J2DCanvasBuffered canvas = new J2DCanvasBuffered(crs, maxCanvasSize);
            final ContextContainer2D cc = new DefaultContextContainer2D(canvas, false);
            canvas.getController().setAutoRepaint(false);
            canvas.getController().setAxisProportions(Double.NaN);
            canvas.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            canvas.setContainer(cc);
            cc.setContext(context);
            
            //generate each mosaic
            for(final double scale : scales){
                final double gridWidth  = envelope.getSpan(0) / (scale*tileSize.width);
                final double gridHeight = envelope.getSpan(1) / (scale*tileSize.height);
                
                //those parameters can change if another mosaic already exist
                Point2D upperleft = new Point2D.Double(envelope.getMinimum(0), envelope.getMaximum(1));
                Dimension tileDim = tileSize;
                Dimension gridSize = new Dimension( (int)(gridWidth+0.5), (int)(gridHeight+0.5));
                                
                //check if we already have a mosaic at this scale
                GridMosaic mosaic = null;
                int index = 0;
                for(double sc : pyramid.getScales()){
                    if(sc == scale){
                        mosaic = pyramid.getMosaic(index);
                        //this mosaic definition replaces the given one
                        upperleft = mosaic.getUpperLeftCorner();
                        tileDim = mosaic.getTileSize();
                        gridSize = mosaic.getGridSize();
                        break;
                    }
                    index++;
                }
                
                if(mosaic == null){
                    //create a new mosaic
                    mosaic = container.createMosaic(pyramid.getId(), 
                        gridSize, tileDim, upperleft, scale);
                }
                
                
                //mark the tiles we already generated
                final BitSet completed = new BitSet(gridSize.width*gridSize.height);
                
                final double tilespanX = scale*tileSize.width;
                final double tilespanY = scale*tileSize.height;
                
                //generate all tiles
                for(int col=0;col<gridSize.width;col++){
                    for(int row=0;row<gridSize.height;row++){
                        
                        if(completed.get(row*gridSize.width+col)){
                            //we already render on this tile
                            continue;
                        }
                        
                        if(!mosaic.isMissing(col, row)){
                            //tile is not missing, continue
                            continue;
                        }
                        
                        //generate the image
                        canvasEnv.setRange(0, 
                                upperleft.getX() + (col)*tilespanX, 
                                upperleft.getX() + (col+nbtileonwidth)*tilespanX
                                );
                        canvasEnv.setRange(1, 
                                upperleft.getY() - (row+nbtileonheight)*tilespanY, 
                                upperleft.getY() - (row)*tilespanY
                                );
                        canvas.getController().setVisibleArea(canvasEnv);
                        canvas.repaint();                        
                        final BufferedImage buffer = canvas.getSnapShot();
                        
                        //cut it in pieces for each tile
                        if(nbtileonwidth == 1 && nbtileonheight == 1){
                            //no need to cut it
                            container.updateTile(pyramid.getId(), mosaic.getId(), col, row, buffer);
                            completed.set(row*gridSize.width+col, true);
                        }else{
                            //cut image in tiles
                            final Graphics2D g2d = (Graphics2D) tileBuffer.getGraphics();
                            for(int cx=0; cx<nbtileonwidth ; cx++){
                                final int targetcol = col+cx;
                                if(targetcol>=gridSize.width) break;
                                
                                for(int cy=0; cy<nbtileonheight ; cy++){
                                    final int targetrow = row+cy;
                                    if(targetrow>=gridSize.height) break;
                                    
                                    completed.set(targetrow*gridSize.width+targetcol, true);
                                    
                                    if(!mosaic.isMissing(targetcol, targetrow)){
                                        //tile is not missing, continue
                                        continue;
                                    }
                                    
                                    //clear the buffer
                                    g2d.setComposite(GO2Utilities.ALPHA_COMPOSITE_0F);
                                    g2d.fillRect(0,0,tileSize.width,tileSize.height);
                                    g2d.setComposite(GO2Utilities.ALPHA_COMPOSITE_1F);
                                    //paint the wanted area
                                    g2d.drawImage(buffer, -cx*tileSize.width, -cy*tileSize.height, null);
                                                                        
                                    if(isEmpty(tileBuffer)){
                                        //do not write empty tiles
                                        continue;
                                    }
                                    
                                    container.updateTile(pyramid.getId(), mosaic.getId(), targetcol, targetrow, tileBuffer);
                                }
                            }
                            
                        }
                        
                    }
                }
            }
            
        }catch(DataStoreException ex){
            throw new ProcessException(ex.getMessage(), this, ex);
        }catch(TransformException ex){
            throw new ProcessException(ex.getMessage(), this, ex);
        }catch(NoninvertibleTransformException ex){
            throw new ProcessException(ex.getMessage(), this, ex);
        }
        
        return outputParameters;
    }

    private static boolean isEmpty(BufferedImage image){
        
        //check if image is empty
        final Raster raster = image.getData();
        double[] array = null;
        searchEmpty:
        for(int x=0,width=image.getWidth(); x<width; x++){
            for(int y=0,height=image.getHeight(); y<height; y++){
                array = raster.getPixel(x, y, array);
                for(double d : array){
                    if(d != 0){
                        return false;
                    }
                }
            }
        }
        
        return true;
    }
    
}
