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
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.event.MouseInputListener;

import org.geotoolkit.display.container.AbstractContainer2D;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.container.ContextContainer2D;
import org.geotoolkit.display2d.primitive.ProjectedGeometry;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.gui.swing.RoundedBorder;
import org.geotoolkit.gui.swing.go2.Map2D;
import org.geotoolkit.gui.swing.go2.decoration.AbstractGeometryDecoration;
import org.geotoolkit.gui.swing.misc.LayerListRenderer;
import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.operation.matrix.AffineMatrix3;

import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.Transaction;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.JTS;

import org.jdesktop.swingx.combobox.ListComboBoxModel;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Expression;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
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

    protected static final FilterFactory2 FF = (FilterFactory2) FactoryFinder.getFilterFactory(
                                                new Hints(Hints.FILTER_FACTORY, FilterFactory2.class));

    private static final Icon ICON_EDIT = IconBundle.getInstance().getIcon("16_edit_geom");
    private static final Icon ICON_MULTI_POINT = IconBundle.getInstance().getIcon("16_multi_point");
    private static final Icon ICON_MULTI_LINE = IconBundle.getInstance().getIcon("16_multi_line");
    private static final Icon ICON_MULTI_POLYGON = IconBundle.getInstance().getIcon("16_multi_polygon");
    private static final Icon ICON_SINGLE_POINT = IconBundle.getInstance().getIcon("16_single_point");
    private static final Icon ICON_SINGLE_LINE = IconBundle.getInstance().getIcon("16_single_line");
    private static final Icon ICON_SINGLE_POLYGON = IconBundle.getInstance().getIcon("16_single_polygon");

    private static final Color MAIN_COLOR = Color.RED;

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
            final ModificationDelegate delegate = new ModificationDelegate(DefaultEditionDecoration.this);
            mouseListener.delegate = delegate;
            currentAction = ACTION.EDIT;
            resetDetails();
            delegate.prepare(panDetail);
        }
    });
    private final JToggleButton guiSinglePoint = new JToggleButton(new AbstractAction("", ICON_SINGLE_POINT) {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            mouseListener.delegate = new PointDelegate(DefaultEditionDecoration.this);
            currentAction = ACTION.CREATE_POINT;
            resetDetails();
        }
    });
    private final JToggleButton guiSingleLine = new JToggleButton(new AbstractAction("", ICON_SINGLE_LINE) {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            mouseListener.delegate = new LineDelegate(DefaultEditionDecoration.this);
            currentAction = ACTION.CREATE_LINE;
            resetDetails();
        }
    });
    private final JToggleButton guiSinglePolygon = new JToggleButton(new AbstractAction("", ICON_SINGLE_POLYGON) {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            mouseListener.delegate = new PolygonDelegate(DefaultEditionDecoration.this);
            currentAction = ACTION.CREATE_POLYGON;
            resetDetails();
        }
    });
    private final JToggleButton guiMultiPoint = new JToggleButton(new AbstractAction("", ICON_MULTI_POINT) {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            mouseListener.delegate = new MultiPointDelegate(DefaultEditionDecoration.this);
            currentAction = ACTION.CREATE_MULTIPOINT;
            resetDetails();
        }
    });
    private final JToggleButton guiMultiLine = new JToggleButton(new AbstractAction("", ICON_MULTI_LINE) {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            mouseListener.delegate = new MultiLineDelegate(DefaultEditionDecoration.this);
            currentAction = ACTION.CREATE_MULTILINE;
            resetDetails();
        }
    });
    private final JToggleButton guiMultiPolygon = new JToggleButton(new AbstractAction("", ICON_MULTI_POLYGON) {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            mouseListener.delegate = new MultiPolygonDelegate(DefaultEditionDecoration.this);
            currentAction = ACTION.CREATE_MULTIPOLYGON;
            resetDetails();
        }
    });
    private final JButton guiEnd = new JButton(new AbstractAction("End") {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            reset();
        }
    });

    private ACTION currentAction = ACTION.NONE;

    private final JPanel panDetail = new JPanel();

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

        JPanel panNorth = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panNorth.setOpaque(false);
        JPanel panTools = new JPanel();
        panTools.setLayout(new FlowLayout());
        panTools.setOpaque(false);
        panTools.setBorder(new RoundedBorder());

        panTools.setOpaque(false);
        panTools.add(guiLayers);
        panTools.add(guiStart);
        panTools.add(new JLabel("      "));
        panTools.add(guiEdit);
        panTools.add(guiSinglePoint);
        panTools.add(guiSingleLine);
        panTools.add(guiSinglePolygon);
        panTools.add(guiMultiPoint);
        panTools.add(guiMultiLine);
        panTools.add(guiMultiPolygon);
        panTools.add(new JLabel("      "));
        panTools.add(guiEnd);
        panNorth.add(panTools);

        JPanel panEast = new JPanel(new FlowLayout(FlowLayout.LEADING));
        panEast.setOpaque(false);
        
        panDetail.setVisible(false);
        panDetail.setOpaque(false);
        panDetail.setBorder(new RoundedBorder());
        panEast.add(panDetail);

        add(BorderLayout.NORTH, panNorth);
        add(BorderLayout.EAST, panEast);
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

        mouseListener.delegate = new AbstractMouseDelegate(this) {
            @Override
            public void fireStateChange() {
            }
        };
        resetDetails();
    }

    public void resetDetails(){
        panDetail.removeAll();
        panDetail.revalidate();
        panDetail.setVisible(false);
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

    public FeatureMapLayer getEditedLayer(){
        MapLayer layer = (MapLayer) guiLayers.getSelectedItem();

        if(layer instanceof FeatureMapLayer){
            return (FeatureMapLayer) layer;
        }else{
            return null;
        }
    }

    //--------------------Geometry Edition--------------------------------------

    /**
     * transform a mouse coordinate in JTS Geometry using the CRS of the mapcontext
     * @param mx : x coordinate of the mouse on the map (in pixel)
     * @param my : y coordinate of the mouse on the map (in pixel)
     * @return JTS geometry (corresponding to a square of 6x6 pixel around mouse coordinate)
     */
    public Polygon mousePositionToGeometry(int mx, int my) throws NoninvertibleTransformException {
        Coordinate[] coord = new Coordinate[5];
        int taille = 4;

        coord[0] = toCoord(mx - taille, my - taille);
        coord[1] = toCoord(mx - taille, my + taille);
        coord[2] = toCoord(mx + taille, my + taille);
        coord[3] = toCoord(mx + taille, my - taille);
        coord[4] = coord[0];

        LinearRing lr1 = GEOMETRY_FACTORY.createLinearRing(coord);
        return GEOMETRY_FACTORY.createPolygon(lr1, null);
    }

    public void editAddGeometry(Geometry[] geoms) {

        FeatureMapLayer editionLayer = getEditedLayer();

        if (editionLayer != null) {

            for (Geometry geom : geoms) {
                SimpleFeatureType featureType = (SimpleFeatureType) editionLayer.getFeatureSource().getSchema();
                FeatureCollection<SimpleFeatureType, SimpleFeature> collection = FeatureCollections.newCollection();
                Object[] values = new Object[featureType.getAttributeCount()];

                AttributeDescriptor geomAttribut = featureType.getGeometryDescriptor();

                final CoordinateReferenceSystem dataCrs = editionLayer.getFeatureSource().getSchema().getCoordinateReferenceSystem();

                try {
                    geom = JTS.transform(geom, CRS.findMathTransform(map.getCanvas().getObjectiveCRS(), dataCrs, true));
                } catch (Exception ex) {
                    Logger.getLogger(DefaultEditionDecoration.class.getName()).log(Level.SEVERE, null, ex);
                }

                List<AttributeDescriptor> lst = featureType.getAttributeDescriptors();
                for (int i = 0,  n = lst.size(); i < n; i++) {
                    AttributeDescriptor desc = lst.get(i);

                    if (desc.equals(geomAttribut)) {
                        values[i] = geom;
                    } else {
                        values[i] = desc.getDefaultValue();
                    }
                }

                SimpleFeature sf = SimpleFeatureBuilder.build(featureType, values, null);
                collection.add(sf);

                DefaultTransaction transaction = null;
                Transaction oldTransaction = null;
                FeatureStore<SimpleFeatureType, SimpleFeature> store = null;
                try {
//                    String featureName = data.getTypeNames()[0]; // there is only one in a shapefile

                    // Create the DefaultTransaction Object
                    transaction = new DefaultTransaction();

//                    String name = editionLayer.getFeatureSource().getName().getLocalPart();
//                    try {
//                        //GR: question: why not just editionLayer.getFeatureSource()?
//                        FeatureSource<SimpleFeatureType, SimpleFeature> source = ((DataStore) editionLayer.getFeatureSource().getDataStore()).getFeatureSource(name);
//                        store = (FeatureStore<SimpleFeatureType, SimpleFeature>) source;
//                    } catch (IOException e) {
//                        // Tell it the name of the shapefile it should look for in our DataStore
//                        store = (FeatureStore<SimpleFeatureType, SimpleFeature>) data.getFeatureSource(featureName);
//                    }

                    store = (FeatureStore<SimpleFeatureType, SimpleFeature>) editionLayer.getFeatureSource();

                    // Then set the transaction for that FeatureStore
                    oldTransaction = store.getTransaction();
                    store.setTransaction(transaction);

                    store.addFeatures(collection);
                    transaction.commit();
                } catch (Exception eek) {
                    eek.printStackTrace();
                    try {
                        store.getTransaction().rollback();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } finally {
                    transaction.close();
                    store.setTransaction(oldTransaction);
                }


            }

            map.getCanvas().getController().repaint();
        }

    }

    public void validateModifiedGeometry(final Geometry geo, final String ID) {

        if (geo == null || ID == null) {
            throw new NullPointerException();
        }


        FeatureMapLayer editionLayer = getEditedLayer();

        FeatureStore<SimpleFeatureType, SimpleFeature> store;
        if (editionLayer != null && editionLayer.getFeatureSource() instanceof FeatureStore) {

//            String name = editionLayer.getFeatureSource().getName().getLocalPart();
//            try {
//                //GR question: why not just editionLayer.getFeatureSource()?
//                FeatureSource<SimpleFeatureType, SimpleFeature> source = ((DataStore) editionLayer.getFeatureSource().getDataStore()).getFeatureSource(name);
//                store = (FeatureStore<SimpleFeatureType, SimpleFeature>) source;
//            } catch (IOException e) {
//                store = (FeatureStore<SimpleFeatureType, SimpleFeature>) editionLayer.getFeatureSource();
//            }

            store = (FeatureStore<SimpleFeatureType, SimpleFeature>) editionLayer.getFeatureSource();
//                    store.getDataStore().dispose();

            DefaultTransaction transaction = new DefaultTransaction("trans_maj");
            Transaction previoustransaction = store.getTransaction();

            store.setTransaction(transaction);
            Filter filter = FF.id(Collections.singleton(FF.featureId(ID)));

            SimpleFeatureType featureType = (SimpleFeatureType) editionLayer.getFeatureSource().getSchema();
            AttributeDescriptor geomAttribut = featureType.getGeometryDescriptor();

            final CoordinateReferenceSystem dataCrs = store.getSchema().getCoordinateReferenceSystem();

            try {
                final Geometry geom = JTS.transform(geo, CRS.findMathTransform(map.getCanvas().getObjectiveCRS(), dataCrs,true));
                store.modifyFeatures(geomAttribut, geom, filter);
                transaction.commit();
            } catch (Exception ex) {
                ex.printStackTrace();
                try {
                    transaction.rollback();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } finally {
                transaction.close();
                store.setTransaction(Transaction.AUTO_COMMIT);
            }

            map.getCanvas().getController().repaint();
        }

    }

    public void removeSelectedGeometry(final String ID) {

        if (ID == null) {
            throw new NullPointerException();
        }

        FeatureMapLayer editionLayer = getEditedLayer();

        FeatureStore<SimpleFeatureType, SimpleFeature> store;
        if (editionLayer != null && editionLayer.getFeatureSource() instanceof FeatureStore) {
            store = (FeatureStore<SimpleFeatureType, SimpleFeature>) editionLayer.getFeatureSource();

            DefaultTransaction transaction = new DefaultTransaction("trans_maj");

            store.setTransaction(transaction);
            Filter filter = FF.id(Collections.singleton(FF.featureId(ID)));

            try {
                store.removeFeatures(filter);
                transaction.commit();
            } catch (Exception ex) {
                ex.printStackTrace();
                try {
                    transaction.rollback();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } finally {
                transaction.close();
                store.setTransaction(Transaction.AUTO_COMMIT);
            }

            map.getCanvas().getController().repaint();
        }

    }


    public Point toJTS(int x, int y){
        Coordinate coord = toCoord(x, y);
        Point geom = GEOMETRY_FACTORY.createPoint(coord);                
        return geom;
    }

    public Coordinate toCoord(int x, int y){
        AffineMatrix3 trs = map.getCanvas().getController().getTransform();
        AffineTransform dispToObj;
        try {
            dispToObj = trs.createInverse();
        } catch (NoninvertibleTransformException ex) {
            dispToObj = new AffineTransform();
            Logger.getLogger(DefaultEditionDecoration.class.getName()).log(Level.SEVERE, null, ex);
        }
        double[] crds = new double[]{x,y};
        dispToObj.transform(crds, 0, crds, 0, 1);
        return new Coordinate(crds[0], crds[1]);
    }

    /**
     * 
     * @param geom : in canvas objective CRS
     * @param layer : target layer filter
     * @return geometry filter
     */
    public Filter toFilter(Geometry poly, FeatureMapLayer fl) throws FactoryException, MismatchedDimensionException, TransformException{

        final String geoStr = fl.getFeatureSource().getSchema().getGeometryDescriptor().getLocalName();
        final Expression geomField = FF.property(geoStr);

        final CoordinateReferenceSystem dataCrs = fl.getFeatureSource().getSchema().getCoordinateReferenceSystem();

        final Geometry dataPoly = JTS.transform(poly, CRS.findMathTransform(map.getCanvas().getObjectiveCRS(), dataCrs,true));

        final Expression geomData = FF.literal(dataPoly);
        final Filter f = FF.intersects(geomField, geomData);

        return f;
    }

    //---------------------Memory Layer-----------------------------------------

    public void setMemoryLayerGeometry(List<Geometry> geoms) {
        setGeometries(geoms);
    }

    public void clearMemoryLayer() {
        setGeometries(null);
    }

    public void reprojectEditionLayer() {
//TODO handle the case of reprojection while editing
//        List<Geometry> geoms = getGeometries();
//
//        for (Geometry geo : geoms) {
//            geomsOut.add(FACILITIES_FACTORY.projectGeometry(geo, map2D.getRenderingStrategy().getContext().getCoordinateReferenceSystem(), map2D.getRenderingStrategy().getContext().getCoordinateReferenceSystem()));
//        //geomsOut.add(map2D.projectGeometry(geo, memoryMapContext.getCoordinateReferenceSystem(), map2D.getRenderingStrategy().getContext().getCoordinateReferenceSystem()));
//        }
//
//        clearMemoryLayer();
//        setMemoryLayerGeometry(geomsOut);
    }


    //---------------------PRIVATE CLASSES--------------------------------------

    public class MouseListen implements MouseInputListener,KeyListener, MouseWheelListener {

       AbstractMouseDelegate delegate;

        public void install(Component component){
            component.addMouseListener(this);
            component.addMouseMotionListener(this);
            component.addMouseWheelListener(this);
            component.addKeyListener(this);
        }

        public void uninstall(Component component){
            component.removeMouseListener(this);
            component.removeMouseMotionListener(this);
            component.removeMouseWheelListener(this);
            component.removeKeyListener(this);
        }


        @Override
        public void mouseClicked(MouseEvent arg0) {
            if(delegate != null){
                delegate.setMap((Map2D) map);
                delegate.mouseClicked(arg0);
            }
        }

        @Override
        public void mousePressed(MouseEvent arg0) {
            if(delegate != null){
                delegate.setMap((Map2D) map);
                delegate.mousePressed(arg0);
            }
        }

        @Override
        public void mouseReleased(MouseEvent arg0) {
            if(delegate != null){
                delegate.setMap((Map2D) map);
                delegate.mouseReleased(arg0);
            }
        }

        @Override
        public void mouseEntered(MouseEvent arg0) {
            map.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
            if(delegate != null){
                delegate.setMap((Map2D) map);
                delegate.mouseEntered(arg0);
            }
        }

        @Override
        public void mouseExited(MouseEvent arg0) {
            if(delegate != null){
                delegate.setMap((Map2D) map);
                delegate.mouseExited(arg0);
            }
        }

        @Override
        public void mouseDragged(MouseEvent arg0) {
            if(delegate != null){
                delegate.setMap((Map2D) map);
                delegate.mouseDragged(arg0);
            }
        }

        @Override
        public void mouseMoved(MouseEvent arg0) {
            if(delegate != null){
                delegate.setMap((Map2D) map);
                delegate.mouseMoved(arg0);
            }
        }

        @Override
        public void keyTyped(KeyEvent arg0) {
            if(delegate != null){
                delegate.setMap((Map2D) map);
                delegate.keyTyped(arg0);
            }
        }

        @Override
        public void keyPressed(KeyEvent arg0) {
            if(delegate != null){
                delegate.setMap((Map2D) map);
                delegate.keyPressed(arg0);
            }
        }

        @Override
        public void keyReleased(KeyEvent arg0) {
            if(delegate != null){
                delegate.setMap((Map2D) map);
                delegate.keyReleased(arg0);
            }
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent arg0) {
            if(delegate != null){
                delegate.setMap((Map2D) map);
                delegate.mouseWheelMoved(arg0);
            }
        }

    }
    

}
