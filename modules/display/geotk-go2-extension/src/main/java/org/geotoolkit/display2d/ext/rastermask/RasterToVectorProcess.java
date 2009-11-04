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

package org.geotoolkit.display2d.ext.rastermask;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.media.jai.iterator.RectIter;
import javax.media.jai.iterator.RectIterFactory;

import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.util.NumberRange;

import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class RasterToVectorProcess {

    private static final GeometryFactory GF = new GeometryFactory();
    private static final LinearRing[] EMPTY_RING_ARRAY = new LinearRing[0];

    public RasterToVectorProcess(){
    }


    private NumberRange lastNumberRange = null;
    private int y = -1;
    private int startX = -1;
    private int endX = -1;

    public Map<NumberRange,Geometry> toPolygon(GridCoverage2D coverage, final Collection<NumberRange> ranges, int band)
            throws IOException, TransformException{
        coverage = coverage.view(ViewType.GEOPHYSICS);

        final Map<NumberRange,List<Geometry>> polygons = new HashMap<NumberRange, List<Geometry>>();
        for(final NumberRange range : ranges){
            polygons.put(range, new ArrayList<Geometry>());
        }

        final RectIter iter = RectIterFactory.create(coverage.getRenderedImage(), null);
        final MathTransform2D gridToCRS = coverage.getGridGeometry().getGridToCRS2D();
        final Point gridPosition = new Point(0,0);

        int bandNum = -1;
        iter.startBands();
        if(!iter.finishedBands()){

            //iteration over bands
            do{
                bandNum++;
                System.out.println("bande " + bandNum);

                if(bandNum == band){
                    //iteration over lines
                    iter.startLines();
                    if(!iter.finishedLines()){
                        do{

                            System.out.println("ligne " + gridPosition.y);
                            //iteration over pixels
                            iter.startPixels();
                            if(!iter.finishedPixels()){
                                do{
                                    //----------------------------------------------
                                    final double value = iter.getSampleDouble();
                                    append(polygons, gridPosition, value);
                                    //----------------------------------------------

                                    gridPosition.x += 1;
                                }while(!iter.nextPixelDone());
                            }

                            //insert last geometry
                            final Polygon pixel = toPolygon(startX, endX, y);
                            polygons.get(lastNumberRange).add(pixel);

                            lastNumberRange = null;
                            startX = -1;
                            endX = -1;
                            y = -1;

                            gridPosition.x = 0;
                            gridPosition.y += 1;
                        }while(!iter.nextLineDone());
                    }
                }

                gridPosition.x = 0;
                gridPosition.y = 0;
            }while(!iter.nextBandDone());
        }

        System.out.println("packing");
        final Map<NumberRange,Geometry> polygones = new HashMap<NumberRange, Geometry>();
        for(final NumberRange range : polygons.keySet()){
            GeometryCollection gc = GF.createGeometryCollection(polygons.get(range).toArray(new Geometry[0]));
            Geometry union = gc.buffer(0); //union();
            polygones.put(range, JTS.transform(union, gridToCRS));
        }

        return polygones;
    }

    private void append(Map<NumberRange,List<Geometry>> polygons, Point point, Number value){
        for(final NumberRange range : polygons.keySet()){
            
            if(range.contains(value)){
                if(lastNumberRange == range){
                    //last pixel was in the same range
                    endX = point.x+1;
                    return;
                }else if(lastNumberRange != null){
                    //last pixel was in a different range, save it's geometry
                    final Polygon pixel = toPolygon(startX, endX, y);
                    polygons.get(lastNumberRange).add(pixel);
                }

                //start a pixel serie
                lastNumberRange = range;
                startX = point.x;
                endX = point.x+1;
                y = point.y;
                
                return;
            }
        }
    }

//    private static Polygon toPolygon(int x, int y){
//        final Coordinate coord = new Coordinate(x, y);
//        final LinearRing ring = GF.createLinearRing(
//            new Coordinate[]{
//                coord,
//                new Coordinate(x+1,y),
//                new Coordinate(x+1,y+1),
//                new Coordinate(x,  y+1),
//                coord
//            }
//        );
//        return GF.createPolygon(ring, EMPTY_RING_ARRAY);
//    }

    private static Polygon toPolygon(int startx, int endx, int y){
        final Coordinate coord = new Coordinate(startx, y);
        final LinearRing ring = GF.createLinearRing(
            new Coordinate[]{
                coord,
                new Coordinate(endx,y),
                new Coordinate(endx,y+1),
                new Coordinate(startx,  y+1),
                coord
            }
        );
        return GF.createPolygon(ring, EMPTY_RING_ARRAY);
    }

}
