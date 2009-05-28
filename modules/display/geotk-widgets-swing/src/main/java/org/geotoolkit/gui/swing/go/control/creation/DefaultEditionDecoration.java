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
package org.geotoolkit.gui.swing.go.control.creation;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import javax.swing.JToolBar;
import org.geotoolkit.display.container.AbstractContainer2D;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.container.ContextContainer2D;
import org.geotoolkit.display2d.primitive.ProjectedGeometry;
import org.geotoolkit.gui.swing.go.control.navigation.MouseNavigatonListener;
import org.geotoolkit.gui.swing.go.decoration.AbstractGeometryDecoration;
import org.geotoolkit.gui.swing.map.map2d.Map2D;
import org.geotoolkit.gui.swing.misc.Render.LayerListRenderer;
import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.operation.matrix.AffineMatrix3;

import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.geometry.jts.JTS;
import org.jdesktop.swingx.combobox.ListComboBoxModel;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public class DefaultEditionDecoration extends AbstractGeometryDecoration {

    private enum ACTION{
        NONE,
        EDIT,
        CREATE_POINT,
        CREATE_LINE,
        CREATE_POLYGON,
        CREATE_MULTIPOINT,
        CREATE_MULTILINE,
        CREATE_MULTIPOLYGON
    }

    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();

    private static final Icon ICON_EDIT = IconBundle.getInstance().getIcon("16_edit_geom");
    private static final Icon ICON_MULTI_POINT = IconBundle.getInstance().getIcon("16_multi_point");
    private static final Icon ICON_MULTI_LINE = IconBundle.getInstance().getIcon("16_multi_line");
    private static final Icon ICON_MULTI_POLYGON = IconBundle.getInstance().getIcon("16_multi_polygon");
    private static final Icon ICON_SINGLE_POINT = IconBundle.getInstance().getIcon("16_single_point");
    private static final Icon ICON_SINGLE_LINE = IconBundle.getInstance().getIcon("16_single_line");
    private static final Icon ICON_SINGLE_POLYGON = IconBundle.getInstance().getIcon("16_single_polygon");

    private static final Color MAIN_COLOR = Color.ORANGE;
    private static final Color SHADOW_COLOR = new Color(0f, 0f, 0f, 0.7f);
    private static final int SHADOW_STEP = 2;

    private final MouseListen mouseListener = new MouseListen();

    private final ButtonGroup group = new ButtonGroup();
    private final JComboBox guiLayers = new JComboBox();
    private final JButton guiStart = new JButton(new AbstractAction("Start") {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            final Object candidate = guiLayers.getSelectedItem();
            if(candidate instanceof FeatureMapLayer){
                FeatureMapLayer layer = (FeatureMapLayer)candidate;
                FeatureSource fs = layer.getFeatureSource();
                if(fs instanceof FeatureStore){
                    final Class c = fs.getSchema().getGeometryDescriptor().getType().getBinding();
                    guiLayers.setEnabled(false);
                    guiStart.setEnabled(false);
                    guiEnd.setEnabled(true);
                    guiEdit.setEnabled(true);
                    if(c == Point.class){
                        guiSinglePoint.setEnabled(true);
                    }else if(c == LineString.class){
                        guiSingleLine.setEnabled(true);
                    }else if(c == Polygon.class){
                        guiSinglePolygon.setEnabled(true);
                    }else if(c == MultiPoint.class){
                        guiMultiPoint.setEnabled(true);
                    }else if(c == MultiLineString.class){
                        guiMultiLine.setEnabled(true);
                    }else if(c == MultiPolygon.class){
                        guiMultiPolygon.setEnabled(true);
                    }else if(c == Geometry.class){
                        guiSinglePoint.setEnabled(true);
                        guiSingleLine.setEnabled(true);
                        guiSinglePolygon.setEnabled(true);
                        guiMultiPoint.setEnabled(true);
                        guiMultiLine.setEnabled(true);
                        guiMultiPolygon.setEnabled(true);
                    }
                }
            }

        }
    });
    private final JToggleButton guiEdit = new JToggleButton(new AbstractAction("", ICON_EDIT) {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            currentAction = ACTION.EDIT;
        }
    });
    private final JToggleButton guiSinglePoint = new JToggleButton(new AbstractAction("", ICON_SINGLE_POINT) {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            currentAction = ACTION.CREATE_POINT;
        }
    });
    private final JToggleButton guiSingleLine = new JToggleButton(new AbstractAction("", ICON_SINGLE_LINE) {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            currentAction = ACTION.CREATE_LINE;
        }
    });
    private final JToggleButton guiSinglePolygon = new JToggleButton(new AbstractAction("", ICON_SINGLE_POLYGON) {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            currentAction = ACTION.CREATE_POLYGON;
        }
    });
    private final JToggleButton guiMultiPoint = new JToggleButton(new AbstractAction("", ICON_MULTI_POINT) {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            currentAction = ACTION.CREATE_MULTIPOINT;
        }
    });
    private final JToggleButton guiMultiLine = new JToggleButton(new AbstractAction("", ICON_MULTI_LINE) {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            currentAction = ACTION.CREATE_MULTILINE;
        }
    });
    private final JToggleButton guiMultiPolygon = new JToggleButton(new AbstractAction("", ICON_MULTI_POLYGON) {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            currentAction = ACTION.CREATE_MULTIPOLYGON;
        }
    });
    private final JButton guiEnd = new JButton(new AbstractAction("End") {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            reset();
        }
    });

    private ACTION currentAction = ACTION.NONE;

    DefaultEditionDecoration() {

        group.add(guiEdit);
        group.add(guiSingleLine);
        group.add(guiSinglePoint);
        group.add(guiSinglePolygon);
        group.add(guiMultiLine);
        group.add(guiMultiPoint);
        group.add(guiMultiPolygon);

        guiLayers.setRenderer(new LayerListRenderer());

        guiLayers.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                final Object candidate = guiLayers.getSelectedItem();
            if(candidate instanceof FeatureMapLayer){
                FeatureMapLayer layer = (FeatureMapLayer)candidate;
                FeatureSource fs = layer.getFeatureSource();
                if(fs instanceof FeatureStore){
                    guiStart.setEnabled(true);
                }
            }
            }
        });


        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER,1,1)) {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(1f, 1f, 1f, 0.8f));
                ((Graphics2D) g).fill(getBounds());
                super.paintComponent(g);
            }
        };
//        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));


        guiEdit.setBorderPainted(false);
        guiSinglePoint.setBorderPainted(false);
        guiSingleLine.setBorderPainted(false);

        panel.setOpaque(false);
        panel.add(guiLayers);
        panel.add(guiStart);
        panel.add(new JLabel("      "));
        panel.add(guiEdit);
        panel.add(guiSinglePoint);
        panel.add(guiSingleLine);
        panel.add(guiSinglePolygon);
        panel.add(guiMultiPoint);
        panel.add(guiMultiLine);
        panel.add(guiMultiPolygon);
        panel.add(new JLabel("      "));
        panel.add(guiEnd);

        add(BorderLayout.NORTH, panel);

    }

    public void reset(){
        currentAction = ACTION.NONE;
        setMap2D(map);
        guiStart.setEnabled(false);
        guiEdit.setEnabled(false);
        guiSingleLine.setEnabled(false);
        guiSinglePoint.setEnabled(false);
        guiSinglePolygon.setEnabled(false);
        guiMultiLine.setEnabled(false);
        guiMultiPoint.setEnabled(false);
        guiMultiPolygon.setEnabled(false);
        guiEnd.setEnabled(false);
    }

    @Override
    public void setMap2D(Map2D map2d){
        super.setMap2D(map2d);

        guiLayers.setEnabled(false);

        final List<Object> objects = new ArrayList<Object>();
        objects.add("-");

        if(map != null){
            AbstractContainer2D container = map.getCanvas().getContainer();
            if(container instanceof ContextContainer2D){
                guiLayers.setEnabled(true);
                ContextContainer2D cc = (ContextContainer2D) container;
                objects.addAll(cc.getContext().layers());
            }
        }

        guiLayers.setModel(new ListComboBoxModel(objects));
        guiLayers.setSelectedIndex(0);
    }

    public MouseListen getMouseListener() {
        return mouseListener;
    }

    @Override
    public void setGeometries(Collection<Geometry> geoms) {
        super.setGeometries(geoms);
    }
    
    @Override
    protected void paintGeometry(Graphics2D g2, RenderingContext2D context, ProjectedGeometry projectedGeom) throws TransformException {
        context.switchToDisplayCRS();

        final Geometry objectiveGeom = projectedGeom.getObjectiveGeometry();

        if(objectiveGeom instanceof Point){
            //draw a single cross
            final Point p = (Point) objectiveGeom;
            final double[] crds = toDisplay(p.getCoordinate());
            paintCross(g2, crds);

        }else if(objectiveGeom instanceof LineString){
            final LineString line = (LineString)objectiveGeom;

            g2.setStroke(new BasicStroke(2,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
            //draw a shadow
            g2.translate(SHADOW_STEP,SHADOW_STEP);
            g2.setColor(SHADOW_COLOR);
            g2.draw(projectedGeom.getDisplayShape());
            //draw the lines
            g2.translate(-SHADOW_STEP, -SHADOW_STEP);
            g2.setColor(MAIN_COLOR);
            g2.draw(projectedGeom.getDisplayShape());

            //draw start cross
            Point p = line.getStartPoint();
            double[] crds = toDisplay(p.getCoordinate());
            paintCross(g2, crds);

            //draw end cross
            p = line.getEndPoint();
            crds = toDisplay(p.getCoordinate());
            paintCross(g2, crds);
        }else if(objectiveGeom instanceof Polygon){
            final Polygon poly = (Polygon)objectiveGeom;

            g2.setStroke(new BasicStroke(2,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
            //draw a shadow
            g2.translate(SHADOW_STEP, SHADOW_STEP);
            g2.setColor(SHADOW_COLOR);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
            g2.fill(projectedGeom.getDisplayShape());

            //draw the lines
            g2.translate(-SHADOW_STEP, -SHADOW_STEP);
            g2.setColor(MAIN_COLOR);
            g2.fill(projectedGeom.getDisplayShape());
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            g2.draw(projectedGeom.getDisplayShape());

            //draw start cross
            Point p = poly.getExteriorRing().getStartPoint();
            double[] crds = toDisplay(p.getCoordinate());
            paintCross(g2, crds);

            //draw end cross
            p = poly.getExteriorRing().getPointN(poly.getExteriorRing().getNumPoints()-2);
            crds = toDisplay(p.getCoordinate());
            paintCross(g2, crds);
        }

    }

    private void paintCross(Graphics2D g2, double[] crds){
        g2.setStroke(new BasicStroke(3,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER));
        //draw a shadow
        crds[0] +=SHADOW_STEP;
        crds[1] +=SHADOW_STEP;
        g2.setColor(SHADOW_COLOR);
        g2.drawLine((int)crds[0], (int)crds[1]-6, (int)crds[0], (int)crds[1]+6);
        g2.drawLine((int)crds[0]-6, (int)crds[1], (int)crds[0]+6, (int)crds[1]);
        ///draw the start cross
        crds[0] -=SHADOW_STEP;
        crds[1] -=SHADOW_STEP;
        g2.setColor(MAIN_COLOR);
        g2.drawLine((int)crds[0], (int)crds[1]-6, (int)crds[0], (int)crds[1]+6);
        g2.drawLine((int)crds[0]-6, (int)crds[1], (int)crds[0]+6, (int)crds[1]);
    }

    //---------------------PRIVATE CLASSES--------------------------------------
    public class MouseListen extends MouseNavigatonListener {

        private final List<Coordinate> coords = new ArrayList<Coordinate>();

        MouseListen(){
            super(null);
        }

        private void updateGeometry(){
            final List<Geometry> geoms = new ArrayList<Geometry>();
            if(coords.size() == 1){
                //single point
                geoms.add(GEOMETRY_FACTORY.createPoint(coords.get(0)));
            }else if(coords.size() == 2){
                //line
                geoms.add(GEOMETRY_FACTORY.createLineString(coords.toArray(new Coordinate[coords.size()])));
            }else if(coords.size() > 2){
                //polygon
                Coordinate[] ringCoords = coords.toArray(new Coordinate[coords.size()+1]);
                ringCoords[coords.size()] = coords.get(0);
                LinearRing ring = GEOMETRY_FACTORY.createLinearRing(ringCoords);
                geoms.add(GEOMETRY_FACTORY.createPolygon(ring, new LinearRing[0]));
            }

            setGeometries(geoms);
        }

        @Override
        public void mouseClicked(MouseEvent e) {

            int mousebutton = e.getButton();
            if (mousebutton == MouseEvent.BUTTON1) {

                System.out.println(currentAction);

                if(currentAction == ACTION.CREATE_POINT){

                    //add a coordinate
                    AffineMatrix3 trs = map.getCanvas().getController().getTransform();
                    try {
                        AffineTransform dispToObj = trs.createInverse();
                        double[] crds = new double[]{e.getX(),e.getY()};
                        dispToObj.transform(crds, 0, crds, 0, 1);

                        Coordinate coord = new Coordinate(crds[0], crds[1]);
                        Point geom = GEOMETRY_FACTORY.createPoint(coord);

                        MapLayer layer = (MapLayer) guiLayers.getSelectedItem();
                        if(layer instanceof FeatureMapLayer){
                            FeatureMapLayer flayer = (FeatureMapLayer) layer;

                            CoordinateReferenceSystem dataCrs = flayer.getFeatureSource().getSchema().getCoordinateReferenceSystem();

                            geom = (Point) JTS.transform(geom, CRS.findMathTransform(dataCrs, map.getCanvas().getObjectiveCRS()));

                            EditionUtils.editAddGeometry(flayer, new Geometry[]{geom});
                            map.getCanvas().getController().repaint();
                        }


    //                    coords.add(new Coordinate(crds[0], crds[1]));
    //                    updateGeometry();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                

                

            } else if (mousebutton == MouseEvent.BUTTON3) {
                //erase coordiantes
                coords.clear();
                updateGeometry();
            }

        }

        @Override
        public void mouseEntered(MouseEvent e) {
            map.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        }

    }

}
