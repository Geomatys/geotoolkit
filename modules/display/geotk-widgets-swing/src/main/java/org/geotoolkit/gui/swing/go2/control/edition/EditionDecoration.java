/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Johann Sorel
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
package org.geotoolkit.gui.swing.go2.control.edition;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.logging.Level;

import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.DefaultRenderingContext2D;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.DefaultProjectedGeometry;
import org.geotoolkit.display2d.primitive.ProjectedGeometry;
import org.geotoolkit.gui.swing.go2.control.edition.EditionHelper.EditionGeometry;
import org.geotoolkit.gui.swing.go2.decoration.AbstractGeometryDecoration;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;

import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public final class EditionDecoration extends AbstractGeometryDecoration {

    private static final Color MAIN_COLOR = Color.RED;
    private static final Color SELECTION_COLOR = Color.YELLOW;

    private EditionHelper.EditionGeometry nodeSelection = null;

    public EditionDecoration() {
        setFocusable(false);
    }

    public void reset(){
        setMap2D(map);
    }

    public void setNodeSelection(EditionGeometry nodeSelection) {
        this.nodeSelection = nodeSelection;
        repaint();
    }

    public EditionGeometry getNodeSelection() {
        return nodeSelection;
    }

    @Override
    protected void paintComponent(final Graphics2D g2, final DefaultRenderingContext2D context,
            final AffineTransform objToDisp) {
        super.paintComponent(g2, context, objToDisp);

        //paint the selected node
        if(nodeSelection == null) return;
        if(nodeSelection.numSubGeom < 0) return;

        final Geometry geo = nodeSelection.geometry.getGeometryN(nodeSelection.numSubGeom);

        if(nodeSelection.selectedNode[0] < 0) return;

        if(nodeSelection.numHole < 0){
            final Coordinate coord = geo.getCoordinates()[nodeSelection.selectedNode[0]];
            final DefaultProjectedGeometry projected = new DefaultProjectedGeometry(new GeometryFactory().createPoint(coord));
            projected.setObjToDisplay(new AffineTransform2D(objToDisp));
            try {
                final Point p = (Point) projected.getDisplayGeometryJTS();
                final double[] crds = new double[]{p.getX(),p.getY()};
                paintRound(g2,crds);
            } catch (TransformException ex) {
                getLogger().log(Level.WARNING, null, ex);
            }
        }else{
            throw new UnsupportedOperationException("not yet implemented");
        }

    }

    @Override
    protected void paintGeometry(final Graphics2D g2, final RenderingContext2D context, final ProjectedGeometry projectedGeom) throws TransformException {
        context.switchToDisplayCRS();

        final Geometry objectiveGeom = projectedGeom.getDisplayGeometryJTS();

        if(objectiveGeom instanceof Point){
            paintPoint(g2, (Point)objectiveGeom);
        }else if(objectiveGeom instanceof LineString){
            paintLineString(g2, (LineString)objectiveGeom, projectedGeom.getDisplayShape());
        }else if(objectiveGeom instanceof Polygon){
            paintPolygon(g2, (Polygon)objectiveGeom, projectedGeom.getDisplayShape());
        }else if(objectiveGeom instanceof MultiPoint){
            MultiPoint mp = (MultiPoint) objectiveGeom;
            for(int i=0,n=mp.getNumGeometries();i<n;i++){
                paintPoint(g2,(Point) mp.getGeometryN(i));
            }
        }else if(objectiveGeom instanceof MultiLineString){
            MultiLineString mp = (MultiLineString) objectiveGeom;
            for(int i=0,n=mp.getNumGeometries();i<n;i++){
                paintLineString(g2,(LineString) mp.getGeometryN(i),GO2Utilities.toJava2D(mp.getGeometryN(i)));
            }
        }else if(objectiveGeom instanceof MultiPolygon){
            MultiPolygon mp = (MultiPolygon) objectiveGeom;
            for(int i=0,n=mp.getNumGeometries();i<n;i++){
                paintPolygon(g2,(Polygon) mp.getGeometryN(i),GO2Utilities.toJava2D(mp.getGeometryN(i)));
            }
        }

    }

    private void paintPoint(final Graphics2D g2, final Point objectiveGeom){
        //draw a single cross
        final Point p = (Point) objectiveGeom;
        final double[] crds = new double[]{p.getX(),p.getY()};
        paintCross(g2, crds);
    }

    private void paintLineString(final Graphics2D g2, final LineString line, final Shape displayShape) throws TransformException{
        g2.setStroke(new BasicStroke(2,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));

        g2.setColor(MAIN_COLOR);
        g2.draw(displayShape);

        for(Coordinate coord : line.getCoordinates()){
            paintCross(g2, new double[]{coord.x,coord.y});
        }
    }

    private void paintPolygon(final Graphics2D g2, final Polygon poly, final Shape displayShape) throws TransformException{
        g2.setStroke(new BasicStroke(2,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
        g2.setColor(MAIN_COLOR);
        g2.fill(displayShape);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        g2.setColor(Color.BLACK);
        g2.draw(displayShape);

        for(Coordinate coord : poly.getCoordinates()){
            paintCross(g2, new double[]{coord.x,coord.y});
        }
    }

    private void paintCross(final Graphics2D g2, final double[] crds){
        g2.setStroke(new BasicStroke(1,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL));

        g2.setColor(MAIN_COLOR);
        g2.fillRect((int)crds[0]-3, (int)crds[1]-3, 6, 6);
        g2.setColor(Color.BLACK);
        g2.drawRect((int)crds[0]-3, (int)crds[1]-3, 6, 6);
    }

    private void paintRound(final Graphics2D g2, final double[] crds){
        g2.setStroke(new BasicStroke(1,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL));

        g2.setColor(SELECTION_COLOR);
        g2.fillOval((int)crds[0]-4, (int)crds[1]-4, 8, 8);
        g2.setColor(Color.BLACK);
        g2.drawOval((int)crds[0]-4, (int)crds[1]-4, 8, 8);
    }

}
