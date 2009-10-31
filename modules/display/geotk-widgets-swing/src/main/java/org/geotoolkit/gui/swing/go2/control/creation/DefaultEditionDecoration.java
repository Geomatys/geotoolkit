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
import org.geotoolkit.gui.swing.go2.Map2D;
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
        panNorth.add(BorderLayout.WEST,comp);
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
    public void setMap2D(Map2D map2d){
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
//
//    public FeatureMapLayer getEditedLayer(){
//        MapLayer layer = (MapLayer) guiLayers.getSelectedItem();
//
//        if(layer instanceof FeatureMapLayer){
//            return (FeatureMapLayer) layer;
//        }else{
//            return null;
//        }
//    }
//
//    //--------------------Geometry Edition--------------------------------------
//
//    /**
//     * transform a mouse coordinate in JTS Geometry using the CRS of the mapcontext
//     * @param mx : x coordinate of the mouse on the map (in pixel)
//     * @param my : y coordinate of the mouse on the map (in pixel)
//     * @return JTS geometry (corresponding to a square of 6x6 pixel around mouse coordinate)
//     */
//    public Polygon mousePositionToGeometry(int mx, int my) throws NoninvertibleTransformException {
//        Coordinate[] coord = new Coordinate[5];
//        int taille = 4;
//
//        coord[0] = toCoord(mx - taille, my - taille);
//        coord[1] = toCoord(mx - taille, my + taille);
//        coord[2] = toCoord(mx + taille, my + taille);
//        coord[3] = toCoord(mx + taille, my - taille);
//        coord[4] = coord[0];
//
//        LinearRing lr1 = GEOMETRY_FACTORY.createLinearRing(coord);
//        return GEOMETRY_FACTORY.createPolygon(lr1, null);
//    }
//
//    public void editAddGeometry(Geometry[] geoms) {
//
//        FeatureMapLayer editionLayer = getEditedLayer();
//
//        if (editionLayer != null) {
//
//            for (Geometry geom : geoms) {
//                SimpleFeatureType featureType = (SimpleFeatureType) editionLayer.getFeatureSource().getSchema();
//                FeatureCollection<SimpleFeatureType, SimpleFeature> collection = FeatureCollectionUtilities.createCollection();
//                Object[] values = new Object[featureType.getAttributeCount()];
//
//                AttributeDescriptor geomAttribut = featureType.getGeometryDescriptor();
//
//                final CoordinateReferenceSystem dataCrs = editionLayer.getFeatureSource().getSchema().getCoordinateReferenceSystem();
//
//                try {
//                    geom = JTS.transform(geom, CRS.findMathTransform(map.getCanvas().getObjectiveCRS(), dataCrs, true));
//                } catch (Exception ex) {
//                    LOGGER.log(Level.SEVERE, null, ex);
//                }
//
//                List<AttributeDescriptor> lst = featureType.getAttributeDescriptors();
//                for (int i = 0,  n = lst.size(); i < n; i++) {
//                    AttributeDescriptor desc = lst.get(i);
//
//                    if (desc.equals(geomAttribut)) {
//                        values[i] = geom;
//                    } else {
//                        values[i] = desc.getDefaultValue();
//                    }
//                }
//
//                SimpleFeature sf = SimpleFeatureBuilder.build(featureType, values, null);
//                collection.add(sf);
//
//                DefaultTransaction transaction = null;
//                Transaction oldTransaction = null;
//                FeatureStore<SimpleFeatureType, SimpleFeature> store = null;
//                try {
////                    String featureName = data.getTypeNames()[0]; // there is only one in a shapefile
//
//                    // Create the DefaultTransaction Object
//                    transaction = new DefaultTransaction();
//
////                    String name = editionLayer.getFeatureSource().getName().getLocalPart();
////                    try {
////                        //GR: question: why not just editionLayer.getFeatureSource()?
////                        FeatureSource<SimpleFeatureType, SimpleFeature> source = ((DataStore) editionLayer.getFeatureSource().getDataStore()).getFeatureSource(name);
////                        store = (FeatureStore<SimpleFeatureType, SimpleFeature>) source;
////                    } catch (IOException e) {
////                        // Tell it the name of the shapefile it should look for in our DataStore
////                        store = (FeatureStore<SimpleFeatureType, SimpleFeature>) data.getFeatureSource(featureName);
////                    }
//
//                    store = (FeatureStore<SimpleFeatureType, SimpleFeature>) editionLayer.getFeatureSource();
//
//                    // Then set the transaction for that FeatureStore
//                    oldTransaction = store.getTransaction();
//                    store.setTransaction(transaction);
//
//                    store.addFeatures(collection);
//                    transaction.commit();
//                } catch (Exception eek) {
//                    eek.printStackTrace();
//                    try {
//                        store.getTransaction().rollback();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                } finally {
//                    transaction.close();
//                    store.setTransaction(oldTransaction);
//                }
//
//
//            }
//
//            map.getCanvas().getController().repaint();
//        }
//
//    }
//
//    public void validateModifiedGeometry(final Geometry geo, final String ID) {
//
//        if (geo == null || ID == null) {
//            throw new NullPointerException();
//        }
//
//
//        final FeatureMapLayer editionLayer = getEditedLayer();
//        final FeatureStore<SimpleFeatureType, SimpleFeature> store;
//
//        if (editionLayer != null && editionLayer.getFeatureSource() instanceof FeatureStore) {
//
////            String name = editionLayer.getFeatureSource().getName().getLocalPart();
////            try {
////                //GR question: why not just editionLayer.getFeatureSource()?
////                FeatureSource<SimpleFeatureType, SimpleFeature> source = ((DataStore) editionLayer.getFeatureSource().getDataStore()).getFeatureSource(name);
////                store = (FeatureStore<SimpleFeatureType, SimpleFeature>) source;
////            } catch (IOException e) {
////                store = (FeatureStore<SimpleFeatureType, SimpleFeature>) editionLayer.getFeatureSource();
////            }
//
//            store = (FeatureStore<SimpleFeatureType, SimpleFeature>) editionLayer.getFeatureSource();
////                    store.getDataStore().dispose();
//
//            DefaultTransaction transaction = new DefaultTransaction("trans_maj");
//            Transaction previoustransaction = store.getTransaction();
//
//            store.setTransaction(transaction);
//            Filter filter = FF.id(Collections.singleton(FF.featureId(ID)));
//
//            SimpleFeatureType featureType = (SimpleFeatureType) editionLayer.getFeatureSource().getSchema();
//            AttributeDescriptor geomAttribut = featureType.getGeometryDescriptor();
//
//            final CoordinateReferenceSystem dataCrs = store.getSchema().getCoordinateReferenceSystem();
//
//            try {
//                final Geometry geom = JTS.transform(geo, CRS.findMathTransform(map.getCanvas().getObjectiveCRS(), dataCrs,true));
//                store.updateFeatures(geomAttribut, geom, filter);
//                transaction.commit();
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                try {
//                    transaction.rollback();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            } finally {
//                transaction.close();
//                store.setTransaction(Transaction.AUTO_COMMIT);
//            }
//
//            map.getCanvas().getController().repaint();
//        }
//
//    }
//
//    public void removeSelectedGeometry(final String ID) {
//
//        if (ID == null) {
//            throw new NullPointerException();
//        }
//
//        FeatureMapLayer editionLayer = getEditedLayer();
//
//        FeatureStore<SimpleFeatureType, SimpleFeature> store;
//        if (editionLayer != null && editionLayer.getFeatureSource() instanceof FeatureStore) {
//            store = (FeatureStore<SimpleFeatureType, SimpleFeature>) editionLayer.getFeatureSource();
//
//            DefaultTransaction transaction = new DefaultTransaction("trans_maj");
//
//            store.setTransaction(transaction);
//            Filter filter = FF.id(Collections.singleton(FF.featureId(ID)));
//
//            try {
//                store.removeFeatures(filter);
//                transaction.commit();
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                try {
//                    transaction.rollback();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            } finally {
//                transaction.close();
//                store.setTransaction(Transaction.AUTO_COMMIT);
//            }
//
//            map.getCanvas().getController().repaint();
//        }
//
//    }
//
//
//    public Point toJTS(int x, int y){
//        Coordinate coord = toCoord(x, y);
//        Point geom = GEOMETRY_FACTORY.createPoint(coord);
//        return geom;
//    }
//
//    public Coordinate toCoord(int x, int y){
//        AffineMatrix3 trs = map.getCanvas().getController().getTransform();
//        AffineTransform dispToObj;
//        try {
//            dispToObj = trs.createInverse();
//        } catch (NoninvertibleTransformException ex) {
//            dispToObj = new AffineTransform();
//            LOGGER.log(Level.SEVERE, null, ex);
//        }
//        double[] crds = new double[]{x,y};
//        dispToObj.transform(crds, 0, crds, 0, 1);
//        return new Coordinate(crds[0], crds[1]);
//    }
//
//    /**
//     *
//     * @param geom : in canvas objective CRS
//     * @param layer : target layer filter
//     * @return geometry filter
//     */
//    public Filter toFilter(Geometry poly, FeatureMapLayer fl) throws FactoryException, MismatchedDimensionException, TransformException{
//
//        final String geoStr = fl.getFeatureSource().getSchema().getGeometryDescriptor().getLocalName();
//        final Expression geomField = FF.property(geoStr);
//
//        final CoordinateReferenceSystem dataCrs = fl.getFeatureSource().getSchema().getCoordinateReferenceSystem();
//
//        final Geometry dataPoly = JTS.transform(poly, CRS.findMathTransform(map.getCanvas().getObjectiveCRS(), dataCrs,true));
//
//        final Expression geomData = FF.literal(dataPoly);
//        final Filter f = FF.intersects(geomField, geomData);
//
//        return f;
//    }
//
//    //---------------------Memory Layer-----------------------------------------
//
//    public void setMemoryLayerGeometry(List<Geometry> geoms) {
//        setGeometries(geoms);
//    }
//
//    public void clearMemoryLayer() {
//        setGeometries(null);
//    }
//
//    public void reprojectEditionLayer() {
////TODO handle the case of reprojection while editing
////        List<Geometry> geoms = getGeometries();
////
////        for (Geometry geo : geoms) {
////            geomsOut.add(FACILITIES_FACTORY.projectGeometry(geo, map2D.getRenderingStrategy().getContext().getCoordinateReferenceSystem(), map2D.getRenderingStrategy().getContext().getCoordinateReferenceSystem()));
////        //geomsOut.add(map2D.projectGeometry(geo, memoryMapContext.getCoordinateReferenceSystem(), map2D.getRenderingStrategy().getContext().getCoordinateReferenceSystem()));
////        }
////
////        clearMemoryLayer();
////        setMemoryLayerGeometry(geomsOut);
//    }
//
//
//    //---------------------PRIVATE CLASSES--------------------------------------
//
//    public final class UIEventProxy implements MouseInputListener,KeyListener, MouseWheelListener {
//
//        private AbstractMouseDelegate delegate;
//
//        public void install(Component component){
//            component.addMouseListener(this);
//            component.addMouseMotionListener(this);
//            component.addMouseWheelListener(this);
//            component.addKeyListener(this);
//        }
//
//        public void uninstall(Component component){
//            component.removeMouseListener(this);
//            component.removeMouseMotionListener(this);
//            component.removeMouseWheelListener(this);
//            component.removeKeyListener(this);
//        }
//
//        @Override
//        public void mouseClicked(MouseEvent arg0) {
//            if(delegate != null){
//                delegate.setMap((Map2D) map);
//                delegate.mouseClicked(arg0);
//            }
//        }
//
//        @Override
//        public void mousePressed(MouseEvent arg0) {
//            if(delegate != null){
//                delegate.setMap((Map2D) map);
//                delegate.mousePressed(arg0);
//            }
//        }
//
//        @Override
//        public void mouseReleased(MouseEvent arg0) {
//            if(delegate != null){
//                delegate.setMap((Map2D) map);
//                delegate.mouseReleased(arg0);
//            }
//        }
//
//        @Override
//        public void mouseEntered(MouseEvent arg0) {
//            map.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
//            if(delegate != null){
//                delegate.setMap((Map2D) map);
//                delegate.mouseEntered(arg0);
//            }
//        }
//
//        @Override
//        public void mouseExited(MouseEvent arg0) {
//            if(delegate != null){
//                delegate.setMap((Map2D) map);
//                delegate.mouseExited(arg0);
//            }
//        }
//
//        @Override
//        public void mouseDragged(MouseEvent arg0) {
//            if(delegate != null){
//                delegate.setMap((Map2D) map);
//                delegate.mouseDragged(arg0);
//            }
//        }
//
//        @Override
//        public void mouseMoved(MouseEvent arg0) {
//            if(delegate != null){
//                delegate.setMap((Map2D) map);
//                delegate.mouseMoved(arg0);
//            }
//        }
//
//        @Override
//        public void keyTyped(KeyEvent arg0) {
//            if(delegate != null){
//                delegate.setMap((Map2D) map);
//                delegate.keyTyped(arg0);
//            }
//        }
//
//        @Override
//        public void keyPressed(KeyEvent arg0) {
//            if(delegate != null){
//                delegate.setMap((Map2D) map);
//                delegate.keyPressed(arg0);
//            }
//        }
//
//        @Override
//        public void keyReleased(KeyEvent arg0) {
//            if(delegate != null){
//                delegate.setMap((Map2D) map);
//                delegate.keyReleased(arg0);
//            }
//        }
//
//        @Override
//        public void mouseWheelMoved(MouseWheelEvent arg0) {
//            if(delegate != null){
//                delegate.setMap((Map2D) map);
//                delegate.mouseWheelMoved(arg0);
//            }
//        }
//
//    }
    

}
