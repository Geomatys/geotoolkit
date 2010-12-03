/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Johann Sorel
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
package org.geotoolkit.gui.swing.go2.control.creation;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Shape;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import javax.swing.SwingConstants;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.ProjectedGeometry;
import org.geotoolkit.gui.swing.RoundedBorder;
import org.geotoolkit.gui.swing.go2.JMap2D;
import org.geotoolkit.gui.swing.go2.decoration.AbstractGeometryDecoration;
import org.geotoolkit.gui.swing.resource.IconBundle;

import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public final class DefaultEditionDecoration extends AbstractGeometryDecoration {

    private static final Icon ICON_MOUSE_LEFT = IconBundle.getInstance().getIcon("22_mouse_left");
    private static final Icon ICON_MOUSE_RIGHT = IconBundle.getInstance().getIcon("22_mouse_right");
    private static final Icon ICON_MOUSE_CENTER = IconBundle.getInstance().getIcon("22_mouse_center");
    private static final Icon ICON_MOUSE_WHEEL = IconBundle.getInstance().getIcon("22_mouse_wheel");

    public static final String MSG_GEOM_SELECT = MessageBundle.getString("gesture_geom_select");
    public static final String MSG_GEOM_MOVE = MessageBundle.getString("gesture_geom_move");
    public static final String MSG_GEOM_ADD = MessageBundle.getString("gesture_geom_add");
    public static final String MSG_GEOM_DELETE = MessageBundle.getString("gesture_geom_delete");
    public static final String MSG_NODE_SELECT = MessageBundle.getString("gesture_node_select");
    public static final String MSG_NODE_MOVE = MessageBundle.getString("gesture_node_move");
    public static final String MSG_NODE_ADD = MessageBundle.getString("gesture_node_add");
    public static final String MSG_NODE_DELETE = MessageBundle.getString("gesture_node_delete");
    public static final String MSG_SUBGEOM_MOVE = MessageBundle.getString("gesture_subgeom_move");
    public static final String MSG_SUBGEOM_ADD = MessageBundle.getString("gesture_subgeom_add");
    public static final String MSG_SUBGEOM_DELETE = MessageBundle.getString("gesture_subgeom_delete");
    public static final String MSG_SUBGEOM_VALIDATE = MessageBundle.getString("gesture_subgeom_validate");
    public static final String MSG_ZOOM = MessageBundle.getString("gesture_zoom");
    public static final String MSG_DRAG = MessageBundle.getString("gesture_drag");
    public static final String MSG_VALIDATE = MessageBundle.getString("gesture_validate");

    private static final Color MAIN_COLOR = Color.RED;
    private final JPanel panEast = new JPanel(new FlowLayout(FlowLayout.LEADING));
    private final JPanel panNorth = new JPanel(new BorderLayout(2,2));
    private final JPanel panGesture = new JPanel();


    DefaultEditionDecoration() {
        setLayout(new BorderLayout());
        panEast.setOpaque(false);
        panNorth.setOpaque(false);
        panGesture.setOpaque(false);
        panNorth.add(BorderLayout.EAST,panGesture);
        add(BorderLayout.EAST,panEast);
        add(BorderLayout.NORTH,panNorth);
    }

    public void reset(){
        setMap2D(map);
    }

    public void setToNorth(JComponent comp){
        comp.getInsets().set(5, 5, 5, 5);

        final JPanel pan = new JPanel(new BorderLayout());
        pan.setOpaque(false);
        pan.add(BorderLayout.EAST,comp);

        panNorth.add(BorderLayout.CENTER,pan);
    }

    public void setGestureMessages(String left, String right, String center, String wheel){

        final JPanel panLabels = new JPanel(new GridLayout(4, 1,4,4));
        panLabels.setOpaque(false);
        panLabels.getInsets().set(5, 5, 5, 5);

        if(left != null){
            panLabels.add(new JLabel(left, ICON_MOUSE_LEFT, SwingConstants.LEFT));
        }
        if(right != null){
            panLabels.add(new JLabel(right, ICON_MOUSE_RIGHT, SwingConstants.LEFT));
        }
        if(center != null){
            panLabels.add(new JLabel(center, ICON_MOUSE_CENTER, SwingConstants.LEFT));
        }
        if(wheel != null){
            panLabels.add(new JLabel(wheel, ICON_MOUSE_WHEEL, SwingConstants.LEFT));
        }

        this.panGesture.removeAll();

        if(left != null || right != null || center != null || wheel != null){
            panLabels.setBackground(Color.WHITE);
            panLabels.setBorder(new RoundedBorder());
            this.panGesture.add(panLabels);
        }
        repaint();
        revalidate();
    }

    public void setToolsPane(JComponent comp){
        panEast.removeAll();
        final JPanel panDetail = new JPanel();
        panDetail.setOpaque(false);
        panDetail.setBackground(Color.WHITE);
        panDetail.setBorder(new RoundedBorder());

        if(comp != null){
            comp.setOpaque(false);
            panDetail.add(comp);
        }
        panEast.add(panDetail);
        repaint();
        revalidate();
    }

    @Override
    public void setMap2D(JMap2D map2d){
        super.setMap2D(map2d);
    }


    @Override
    protected void paintGeometry(Graphics2D g2, RenderingContext2D context, ProjectedGeometry projectedGeom) throws TransformException {
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

    private void paintPoint(Graphics2D g2, Point objectiveGeom){
        //draw a single cross
        final Point p = (Point) objectiveGeom;
        final double[] crds = new double[]{p.getX(),p.getY()};
        paintCross(g2, crds);
    }

    private void paintLineString(Graphics2D g2, LineString line, Shape displayShape) throws TransformException{
        g2.setStroke(new BasicStroke(2,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));

        g2.setColor(MAIN_COLOR);
        g2.draw(displayShape);

        for(Coordinate coord : line.getCoordinates()){
            paintCross(g2, new double[]{coord.x,coord.y});
        }
    }

    private void paintPolygon(Graphics2D g2, Polygon poly, Shape displayShape) throws TransformException{
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

    private void paintCross(Graphics2D g2, double[] crds){
        g2.setStroke(new BasicStroke(1,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL));

        g2.setColor(MAIN_COLOR);
        g2.fillRect((int)crds[0]-4, (int)crds[1]-4, 8, 8);
        g2.setColor(Color.BLACK);
        g2.drawRect((int)crds[0]-4, (int)crds[1]-4, 8, 8);
    }

}
