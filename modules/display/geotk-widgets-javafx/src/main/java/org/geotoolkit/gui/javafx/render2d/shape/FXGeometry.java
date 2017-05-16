/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.gui.javafx.render2d.shape;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Paint;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import org.geotoolkit.gui.javafx.util.FXUtilities;

/**
 * Convert a JTS Geometry in a JavaFX Shape.
 * JavaFX has a very limited geometry model, multi part geometries and holes
 * can not be create directly.
 * As a result the jts geometry is translated as a group of smaller geometries.
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXGeometry extends Group {

    private final Geometry geometry;
    private final Node shape;

    public FXGeometry(Geometry geometry) {
        this.geometry = geometry;
        this.shape = toShape(geometry);
        getChildren().add(shape);
    }

    public void setFill(final Paint paint){
        FXUtilities.visit(this, (Node n) -> {if(n instanceof Shape)((Shape)n).setFill(paint);});
    }

    public void setStroke(final Paint paint){
        FXUtilities.visit(this, (Node n) -> {if(n instanceof Shape)((Shape)n).setStroke(paint);});
    }

    public void setStrokeDashOffset(final double offset){
        FXUtilities.visit(this, (Node n) -> {if(n instanceof Shape)((Shape)n).setStrokeDashOffset(offset);});
    }

    public void setStrokeLineCap(final StrokeLineCap cap){
        FXUtilities.visit(this, (Node n) -> {if(n instanceof Shape)((Shape)n).setStrokeLineCap(cap);});
    }

    public void setStrokeLineJoin(final StrokeLineJoin join){
        FXUtilities.visit(this, (Node n) -> {if(n instanceof Shape)((Shape)n).setStrokeLineJoin(join);});
    }

    public void setStrokeMiterLimit(final double value){
        FXUtilities.visit(this, (Node n) -> {if(n instanceof Shape)((Shape)n).setStrokeMiterLimit(value);});
    }

    public void setStrokeType(final StrokeType type){
        FXUtilities.visit(this, (Node n) -> {if(n instanceof Shape)((Shape)n).setStrokeType(type);});
    }

    public void setStrokeWidth(final double value){
        FXUtilities.visit(this, (Node n) -> {if(n instanceof Shape)((Shape)n).setStrokeWidth(value);});
    }

    public void setSmooth(final boolean smooth){
        FXUtilities.visit(this, (Node n) -> {if(n instanceof Shape)((Shape)n).setSmooth(smooth);});
    }


    private static Node toShape(Geometry jts){

        if(jts.isEmpty()){
            //do nothing
            return new Group();
        }else if(jts instanceof Point){
            final Point geom = (Point) jts;
            final Path fxgeom = new Path();
            fxgeom.getElements().add(new MoveTo(geom.getX(),geom.getY()));
            fxgeom.getElements().add(new LineTo(geom.getX(),geom.getY()));
            fxgeom.setCache(false);
            return fxgeom;

        }else if(jts instanceof LineString){
            final LineString geom = (LineString) jts;
            return toShape(geom,false);

        }else if(jts instanceof Polygon){
            //append exterior
            final Polygon geom = (Polygon) jts;
            final LineString exterior = geom.getExteriorRing();
            Shape fxgeom = toShape(exterior, true);
            //remove holes
            final int nbHole = geom.getNumInteriorRing();
            for(int i=0;i<nbHole;i++){
                final LineString interior = geom.getInteriorRingN(i);
                fxgeom = Shape.subtract(fxgeom, toShape(interior, true));
            }
            fxgeom.setCache(false);
            return fxgeom;

        }else if(jts instanceof GeometryCollection){
            final GeometryCollection geom = (GeometryCollection)jts;
            final Group fxgeom = new Group();
            final int nbGeom = geom.getNumGeometries();
            for(int i=0;i<nbGeom;i++){
                fxgeom.getChildren().add(toShape(geom.getGeometryN(i)));
            }
            fxgeom.setCache(false);
            return fxgeom;
        }else{
            throw new IllegalArgumentException("Unexpected geometry type : "+jts);
        }
    }

    private static Shape toShape(LineString geom, boolean closed){
        final Coordinate[] coords = geom.getCoordinates();
        final double[] vals = new double[coords.length*2];
        for(int x=0,i=0;i<coords.length;i++){
            vals[x++] = coords[i].x;
            vals[x++] = coords[i].y;
        }
        final Shape fxgeom;
        if(closed){
            fxgeom = new javafx.scene.shape.Polygon(vals);

        }else{
            fxgeom = new Polyline(vals);
        }
        fxgeom.setCache(false);
        return fxgeom;
    }

}
