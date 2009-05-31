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
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.ArrayList;
import java.util.Collection;
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

import org.geotoolkit.display.container.AbstractContainer2D;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.container.ContextContainer2D;
import org.geotoolkit.display2d.primitive.ProjectedGeometry;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.gui.swing.RoundedBorder;
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

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false);
        JPanel sub = new JPanel();
        sub.setLayout(new FlowLayout());
        sub.setOpaque(false);
        sub.setBorder(new RoundedBorder());


//        guiEdit.setBorderPainted(false);
//        guiSinglePoint.setBorderPainted(false);
//        guiSingleLine.setBorderPainted(false);

        sub.setOpaque(false);
        sub.add(guiLayers);
        sub.add(guiStart);
        sub.add(new JLabel("      "));
        sub.add(guiEdit);
        sub.add(guiSinglePoint);
        sub.add(guiSingleLine);
        sub.add(guiSinglePolygon);
        sub.add(guiMultiPoint);
        sub.add(guiMultiLine);
        sub.add(guiMultiPolygon);
        sub.add(new JLabel("      "));
        sub.add(guiEnd);
        panel.add(sub);

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
//
//        MapLayer editionLayer = map2D.getEditedMapLayer();
//
//        if (editionLayer != null) {
//
//            for (Geometry geom : geoms) {
//
//                SimpleFeatureType featureType = (SimpleFeatureType) editionLayer.getFeatureSource().getSchema();
//                FeatureCollection<SimpleFeatureType, SimpleFeature> collection = FeatureCollections.newCollection();
//                Object[] values = new Object[featureType.getAttributeCount()];
//
//                AttributeDescriptor geomAttribut = featureType.getGeometryDescriptor();
//
//                geom = FACILITIES_FACTORY.projectGeometry(geom, map2D.getRenderingStrategy().getContext(), editionLayer);
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
//                //commit in shape
//                DataStore data = (DataStore) editionLayer.getFeatureSource().getDataStore();
//
//                DefaultTransaction transaction = null;
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
//                }
//
//
//            }
//
//        }

    }

    public void validateModifiedGeometry(final Geometry geo, final String ID) {
//
//        if (geo == null || ID == null) {
//            throw new NullPointerException();
//        }
//
//
//        MapLayer editionLayer = map2D.getEditedMapLayer();
//
//        FeatureStore<SimpleFeatureType, SimpleFeature> store;
//        if (editionLayer.getFeatureSource() instanceof FeatureStore) {
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
////                    Transaction previoustransaction = store.getTransaction();
//
//            store.setTransaction(transaction);
//            FilterFactory ff = FactoryFinder.getFilterFactory(null);
//            Filter filter = ff.id(Collections.singleton(ff.featureId(ID)));
//
//
//            SimpleFeatureType featureType = (SimpleFeatureType) editionLayer.getFeatureSource().getSchema();
//            AttributeDescriptor geomAttribut = featureType.getGeometryDescriptor();
//
//            Geometry geom = FACILITIES_FACTORY.projectGeometry(geo, map2D.getRenderingStrategy().getContext(), editionLayer);
//
//            try {
//                store.modifyFeatures(geomAttribut, geom, filter);
//                transaction.commit();
//            } catch (IOException ex) {
//                ex.printStackTrace();
//                try {
//                    transaction.rollback();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            } finally {
//                transaction.close();
////                store.setTransaction(Transaction.AUTO_COMMIT);
//            }
//
//        }

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
//
//        if (memoryLayer != null) {
//
//            //memory layer--------------------------
//            FeatureCollection<SimpleFeatureType, SimpleFeature> collection = FeatureCollections.newCollection();
//
//            for (Geometry geom : geoms) {
//
//                //geom = projectGeometry(geom, memoryLayer);
//                SimpleFeatureType featureType = (SimpleFeatureType) memoryLayer.getFeatureSource().getSchema();
//                Object[] values = new Object[featureType.getAttributeCount()];
//                AttributeDescriptor geomAttribut = featureType.getGeometryDescriptor();
//                List<AttributeDescriptor> lst = featureType.getAttributeDescriptors();
//
//                for (int i = 0,  n = lst.size(); i < n; i++) {
//                    AttributeDescriptor desc = lst.get(i);
//                    values[i] = (desc.equals(geomAttribut)) ? geom : desc.getDefaultValue();
//                }
//
//                SimpleFeature sf = SimpleFeatureBuilder.build(featureType, values, null);
//                collection.add(sf);
//            }
//
//
//            //commit
//            FeatureStore<SimpleFeatureType, SimpleFeature> store = (FeatureStore<SimpleFeatureType, SimpleFeature>) memoryLayer.getFeatureSource();
//            try {
//                store.addFeatures(collection);
//            } catch (Exception eek) {
//                eek.printStackTrace();
//            }
//
//
//            //edges layer --------------------------------
//            collection = FeatureCollections.newCollection();
//            for (Geometry geom : geoms) {
//
//                Coordinate[] coords = geom.getCoordinates();
//                for (Coordinate coord : coords) {
//
//                    //geom = projectGeometry(geom, memoryLayer);
//                    SimpleFeatureType featureType = (SimpleFeatureType) edgesLayer.getFeatureSource().getSchema();
//                    Object[] values = new Object[featureType.getAttributeCount()];
//                    AttributeDescriptor geomAttribut = featureType.getGeometryDescriptor();
//
//                    List<AttributeDescriptor> lst = featureType.getAttributeDescriptors();
//                    for (int i = 0,  n = lst.size(); i < n; i++) {
//                        AttributeDescriptor desc = lst.get(i);
//
//                        if (desc.equals(geomAttribut)) {
//                            values[i] = GEOMETRY_FACTORY.createPoint(coord);
//                        } else {
//                            values[i] = desc.getDefaultValue();
//                        }
//                    }
//
//                    //featureType.
//                    SimpleFeature sf = SimpleFeatureBuilder.build(featureType, values, null);
//                    collection.add(sf);
//
//                }
//
//                //commit
//                store = (FeatureStore<SimpleFeatureType, SimpleFeature>) edgesLayer.getFeatureSource();
//                try {
//                    store.addFeatures(collection);
//                } catch (Exception eek) {
//                    eek.printStackTrace();
//                }
//
//            }
//        }
//
//        map2D.repaintMemoryDecoration();
    }

    public void clearMemoryLayer() {
//
//        try {
//            FeatureStore<SimpleFeatureType, SimpleFeature> fst = (FeatureStore<SimpleFeatureType, SimpleFeature>) memoryLayer.getFeatureSource();
//            fst.removeFeatures(Filter.INCLUDE);
//            fst = (FeatureStore<SimpleFeatureType, SimpleFeature>) edgesLayer.getFeatureSource();
//            fst.removeFeatures(Filter.INCLUDE);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        map2D.repaintMemoryDecoration();
    }

    public void reprojectEditionLayer() {
//
//        List<Geometry> geoms = new ArrayList<Geometry>();
//        List<Geometry> geomsOut = new ArrayList<Geometry>();
//
//        try {
//            FeatureCollection<SimpleFeatureType, SimpleFeature> col = (FeatureCollection<SimpleFeatureType, SimpleFeature>) memoryLayer.getFeatureSource().getFeatures();
//            FeatureIterator<SimpleFeature> ite = col.features();
//
//            while (ite.hasNext()) {
//                SimpleFeature sf = ite.next();
//                geoms.add((Geometry) sf.getDefaultGeometry());
//            }
//            ite.close();
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
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
                    try {
                        Point geom = toJTS(e.getX(), e.getY());

                        FeatureMapLayer layer = getEditedLayer();
                        if(layer != null){
                            CoordinateReferenceSystem dataCrs = layer.getFeatureSource().getSchema().getCoordinateReferenceSystem();
                            geom = (Point) JTS.transform(geom, CRS.findMathTransform(dataCrs, map.getCanvas().getObjectiveCRS(),true));
                            EditionUtils.editAddGeometry(layer, new Geometry[]{geom});
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
