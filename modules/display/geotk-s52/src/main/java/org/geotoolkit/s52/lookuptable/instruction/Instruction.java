/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.s52.lookuptable.instruction;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.TopologyException;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.List;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.ProjectedObject;
import org.geotoolkit.display2d.primitive.jts.JTSLineIterator;
import org.geotoolkit.display2d.style.j2d.PathWalker;
import org.geotoolkit.s52.S52Context;
import org.geotoolkit.s52.S52Palette;
import org.geotoolkit.s52.symbolizer.S52Graphic;

/**
 * S-52 rendering instruction.
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class Instruction {

    private final String code;

    public Instruction(String code) {
        this.code = code;
    }

    public String getCode(){
        return code;
    }

    /**
     * Read parameters from given string.
     * @param str
     */
    public final void read(String str) throws IOException{
        if(str.startsWith(code)){
            if(str.charAt(code.length()) != '(' || str.charAt(str.length()-1) != ')'){
                throw new IOException("Uncorrect instruction, missing () : "+str);
            }
            str = str.substring(code.length()+1,str.length()-1);
        }else{
            throw new IOException("Uncorrect instruction, does not start by "+code + "  : "+str);
        }
        readParameters(str);
    }


    /**
     * Read instruction parameters from given string.
     * @param str
     */
    protected abstract void readParameters(String str) throws IOException;

    /**
     * Create a new instance of this instruction.
     * @return Instruction
     */
    public Instruction newInstance(){
        try {
            return (Instruction)this.getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException("Failed to create a new instance of : "+this.getClass().getName());
        }
    }

    public abstract void render(RenderingContext2D ctx, S52Context context, S52Palette colorTable, List<S52Graphic> all, S52Graphic s52graphic) throws PortrayalException;

    protected static Coordinate getPivotPoint(Geometry geom){
        try{
            if(geom instanceof Point || geom instanceof MultiPoint){
                return (Coordinate)geom.getCoordinate().clone();

            }else if(geom instanceof LineString || geom instanceof MultiLineString){
                //S-52 Annex A Part I p.47 7.1.1
                // The pivot-point for text for a line is the centre of a single segment line.
                // For a multi-segment-line the pivot-point is the mid-point of the run-length of the line.

                double length;
                if(geom instanceof MultiLineString){
                    //we use the longest line segment
                    MultiLineString mls = (MultiLineString) geom;
                    final int nbls = mls.getNumGeometries();
                    geom = mls.getGeometryN(0);
                    length = geom.getLength();
                    for(int i=0;i<nbls;i++){
                        final Geometry candidate = mls.getGeometryN(i);
                        final double l = candidate.getLength();
                        if(l>length){
                            length = l;
                            geom = candidate;
                        }
                    }

                }else{
                    length = geom.getLength();
                }

                if(length==0){
                    return geom.getCoordinate();
                }else{
                    final PathWalker walker = new PathWalker(new JTSLineIterator((LineString)geom, null));
                    walker.walk(0);
                    walker.walk((float)length/2);
                    Point2D pt = new Point2D.Double();
                    pt = walker.getPosition(pt);
                    return new Coordinate(pt.getX(), pt.getY());
                }

            }else if(geom instanceof Polygon || geom instanceof MultiPolygon){
                // The pivot-point for text for an area object is the centre of the area
                return geom.getInteriorPoint().getCoordinate();

            }else{
                //some other kind of geometry, normaly not happening but might be possible
                //if S-52 style is used on something else then S-57 datas.
                return geom.getInteriorPoint().getCoordinate();
            }
        }catch(TopologyException ex){
            //renderingContext.getMonitor().exceptionOccured(ex, Level.INFO);
            //JTS is sometimes unstable
            //falback on centroid if we have problems.
            return geom.getCentroid().getCoordinate();
        }
    }

}
