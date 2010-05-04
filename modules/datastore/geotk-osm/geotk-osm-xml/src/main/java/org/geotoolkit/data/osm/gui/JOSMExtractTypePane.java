/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.data.osm.gui;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.data.DataStore;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.data.DataStoreFinder;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.memory.GenericMappingFeatureCollection;
import org.geotoolkit.data.query.DefaultJoin;
import org.geotoolkit.data.query.DefaultSelector;
import org.geotoolkit.data.query.Join;
import org.geotoolkit.data.query.JoinType;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.query.QueryUtilities;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.referencing.CRS;
import org.jdesktop.swingx.JXErrorPane;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.FilterFactory;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann sorel (Geomatys)
 * @module pending
 */
public class JOSMExtractTypePane extends javax.swing.JPanel {

    private static final FilterFactory FF = FactoryFinder.getFilterFactory(null);
    private static CoordinateReferenceSystem EPSG_4326 = null;

    static {
        try {
            EPSG_4326 = CRS.decode("EPSG:4326");
        } catch (NoSuchAuthorityCodeException ex) {
            Logger.getLogger(JOSMExtractTypePane.class.getName()).log(Level.WARNING, null, ex);
        } catch (FactoryException ex) {
            Logger.getLogger(JOSMExtractTypePane.class.getName()).log(Level.WARNING, null, ex);
        }
    }

    private Map<String,Serializable> dbParameters = null;

    /** Creates new form JOSMExtractTypePane */
    public JOSMExtractTypePane() {
        initComponents();
    }

    public void setDBParameters(Map<String, Serializable> dBConnectionParameters) {
        this.dbParameters = dBConnectionParameters;
    }

    private void process() throws DataStoreException, NoSuchAuthorityCodeException, FactoryException{
        final DataStore store = DataStoreFinder.getDataStore(dbParameters);
        final Session session = store.createSession(false);

//        processLanduse(store, session);
//        processHighWay(store, session);
//        processLeisure(store, session);
//        processWaterWay(store, session);
//        processBuilding(store, session);
        processNatural(store, session);
        processRailWay(store, session);
        
    }

    private void processRailWay(DataStore store, Session session) throws DataStoreException{
        //create the new schema-------------------------------------------------
        final FeatureTypeBuilder sftb = new FeatureTypeBuilder();
        sftb.setName("RailWay");
        sftb.add("id", Long.class, 1,1,false, FeatureTypeBuilder.PRIMARY_KEY);
        sftb.add("geometry", LineString.class, EPSG_4326);
        sftb.add("type", String.class);
        final SimpleFeatureType sft = sftb.buildSimpleFeatureType();
        store.createSchema(sft.getName(), sft);
        final SimpleFeatureType targetType = (SimpleFeatureType) store.getFeatureType("RailWay");
        //final SimpleFeatureType targetType = sft;

        //source datas----------------------------------------------------------
        final Join join1 = new DefaultJoin(
                new DefaultSelector(session, store.getFeatureType("Way").getName(), "s1"),
                new DefaultSelector(session, store.getFeatureType("WayTag").getName(), "s2"),
                JoinType.INNER,
                FF.equals(FF.property("id"), FF.property("wayId"))
                );

        final QueryBuilder qb = new QueryBuilder();
        qb.setSource(join1);
        qb.setFilter(FF.equals(FF.property("k"), FF.literal("railway")));
        final FeatureCollection col = QueryUtilities.evaluate("translateCol", qb.buildQuery());
        final SimpleFeatureType sourceType = (SimpleFeatureType) col.getFeatureType();


        //insert the datas------------------------------------------------------
        final Map<PropertyDescriptor,List<PropertyDescriptor>> mappings = new HashMap<PropertyDescriptor,List<PropertyDescriptor>>();
        List<PropertyDescriptor> target = null;
        PropertyDescriptor p = null;

        p = sourceType.getDescriptor("id");
        target = new ArrayList<PropertyDescriptor>();
        target.add(targetType.getDescriptor("id"));
        mappings.put(p, target);

        p = sourceType.getDescriptor("geometry");
        target = new ArrayList<PropertyDescriptor>();
        target.add(targetType.getDescriptor("geometry"));
        mappings.put(p, target);

        p = sourceType.getDescriptor("v");
        target = new ArrayList<PropertyDescriptor>();
        target.add(targetType.getDescriptor("type"));
        mappings.put(p, target);

        GenericMappingFeatureCollection mapped = new GenericMappingFeatureCollection(col, targetType, mappings, new HashMap());

        store.addFeatures(targetType.getName(), mapped);
    }

    private void processNatural(DataStore store, Session session) throws DataStoreException{
        //create the new schema-------------------------------------------------
        final FeatureTypeBuilder sftb = new FeatureTypeBuilder();
        sftb.setName("Natural");
        sftb.add("id", Long.class, 1,1,false, FeatureTypeBuilder.PRIMARY_KEY);
        sftb.add("geometry", LineString.class, EPSG_4326);
        sftb.add("type", String.class);
        final SimpleFeatureType sft = sftb.buildSimpleFeatureType();
        store.createSchema(sft.getName(), sft);
        final SimpleFeatureType targetType = (SimpleFeatureType) store.getFeatureType("Natural");
        //final SimpleFeatureType targetType = sft;

        //source datas----------------------------------------------------------
        final Join join1 = new DefaultJoin(
                new DefaultSelector(session, store.getFeatureType("Way").getName(), "s1"),
                new DefaultSelector(session, store.getFeatureType("WayTag").getName(), "s2"),
                JoinType.INNER,
                FF.equals(FF.property("id"), FF.property("wayId"))
                );

        final QueryBuilder qb = new QueryBuilder();
        qb.setSource(join1);
        qb.setFilter(FF.equals(FF.property("k"), FF.literal("natural")));
        final FeatureCollection col = QueryUtilities.evaluate("translateCol", qb.buildQuery());
        final SimpleFeatureType sourceType = (SimpleFeatureType) col.getFeatureType();


        //insert the datas------------------------------------------------------
        final Map<PropertyDescriptor,List<PropertyDescriptor>> mappings = new HashMap<PropertyDescriptor,List<PropertyDescriptor>>();
        List<PropertyDescriptor> target = null;
        PropertyDescriptor p = null;

        p = sourceType.getDescriptor("id");
        target = new ArrayList<PropertyDescriptor>();
        target.add(targetType.getDescriptor("id"));
        mappings.put(p, target);

        p = sourceType.getDescriptor("geometry");
        target = new ArrayList<PropertyDescriptor>();
        target.add(targetType.getDescriptor("geometry"));
        mappings.put(p, target);

        p = sourceType.getDescriptor("v");
        target = new ArrayList<PropertyDescriptor>();
        target.add(targetType.getDescriptor("type"));
        mappings.put(p, target);

        GenericMappingFeatureCollection mapped = new GenericMappingFeatureCollection(col, targetType, mappings, new HashMap());

        store.addFeatures(targetType.getName(), mapped);
    }

    private void processBuilding(DataStore store, Session session) throws DataStoreException{
        //create the new schema-------------------------------------------------
        final FeatureTypeBuilder sftb = new FeatureTypeBuilder();
        sftb.setName("Building");
        sftb.add("id", Long.class, 1,1,false, FeatureTypeBuilder.PRIMARY_KEY);
        sftb.add("geometry", LineString.class, EPSG_4326);
        sftb.add("type", String.class);
        final SimpleFeatureType sft = sftb.buildSimpleFeatureType();
        store.createSchema(sft.getName(), sft);
        final SimpleFeatureType targetType = (SimpleFeatureType) store.getFeatureType("Building");
        //final SimpleFeatureType targetType = sft;

        //source datas----------------------------------------------------------
        final Join join1 = new DefaultJoin(
                new DefaultSelector(session, store.getFeatureType("Way").getName(), "s1"),
                new DefaultSelector(session, store.getFeatureType("WayTag").getName(), "s2"),
                JoinType.INNER,
                FF.equals(FF.property("id"), FF.property("wayId"))
                );

        final QueryBuilder qb = new QueryBuilder();
        qb.setSource(join1);
        qb.setFilter(FF.equals(FF.property("k"), FF.literal("building")));
        final FeatureCollection col = QueryUtilities.evaluate("translateCol", qb.buildQuery());
        final SimpleFeatureType sourceType = (SimpleFeatureType) col.getFeatureType();


        //insert the datas------------------------------------------------------
        final Map<PropertyDescriptor,List<PropertyDescriptor>> mappings = new HashMap<PropertyDescriptor,List<PropertyDescriptor>>();
        List<PropertyDescriptor> target = null;
        PropertyDescriptor p = null;

        p = sourceType.getDescriptor("id");
        target = new ArrayList<PropertyDescriptor>();
        target.add(targetType.getDescriptor("id"));
        mappings.put(p, target);

        p = sourceType.getDescriptor("geometry");
        target = new ArrayList<PropertyDescriptor>();
        target.add(targetType.getDescriptor("geometry"));
        mappings.put(p, target);

        p = sourceType.getDescriptor("v");
        target = new ArrayList<PropertyDescriptor>();
        target.add(targetType.getDescriptor("type"));
        mappings.put(p, target);

        GenericMappingFeatureCollection mapped = new GenericMappingFeatureCollection(col, targetType, mappings, new HashMap());

        store.addFeatures(targetType.getName(), mapped);
    }

    private void processLanduse(DataStore store, Session session) throws DataStoreException{
        //create the new schema-------------------------------------------------
        final FeatureTypeBuilder sftb = new FeatureTypeBuilder();
        sftb.setName("Landuse");
        sftb.add("id", Long.class, 1,1,false, FeatureTypeBuilder.PRIMARY_KEY);
        sftb.add("geometry", LineString.class, EPSG_4326);
        sftb.add("type", String.class);
        final SimpleFeatureType sft = sftb.buildSimpleFeatureType();
        store.createSchema(sft.getName(), sft);
        final SimpleFeatureType targetType = (SimpleFeatureType) store.getFeatureType("Landuse");
        //final SimpleFeatureType targetType = sft;

        //source datas----------------------------------------------------------
        final Join join1 = new DefaultJoin(
                new DefaultSelector(session, store.getFeatureType("Way").getName(), "s1"),
                new DefaultSelector(session, store.getFeatureType("WayTag").getName(), "s2"),
                JoinType.INNER,
                FF.equals(FF.property("id"), FF.property("wayId"))
                );

        final QueryBuilder qb = new QueryBuilder();
        qb.setSource(join1);
        qb.setFilter(FF.equals(FF.property("k"), FF.literal("landuse")));
        final FeatureCollection col = QueryUtilities.evaluate("translateCol", qb.buildQuery());
        final SimpleFeatureType sourceType = (SimpleFeatureType) col.getFeatureType();


        //insert the datas------------------------------------------------------
        final Map<PropertyDescriptor,List<PropertyDescriptor>> mappings = new HashMap<PropertyDescriptor,List<PropertyDescriptor>>();
        List<PropertyDescriptor> target = null;
        PropertyDescriptor p = null;

        p = sourceType.getDescriptor("id");
        target = new ArrayList<PropertyDescriptor>();
        target.add(targetType.getDescriptor("id"));
        mappings.put(p, target);

        p = sourceType.getDescriptor("geometry");
        target = new ArrayList<PropertyDescriptor>();
        target.add(targetType.getDescriptor("geometry"));
        mappings.put(p, target);

        p = sourceType.getDescriptor("v");
        target = new ArrayList<PropertyDescriptor>();
        target.add(targetType.getDescriptor("type"));
        mappings.put(p, target);

        GenericMappingFeatureCollection mapped = new GenericMappingFeatureCollection(col, targetType, mappings, new HashMap());

        store.addFeatures(targetType.getName(), mapped);
    }

    private void processLeisure(DataStore store, Session session) throws DataStoreException{
        //create the new schema-------------------------------------------------
        final FeatureTypeBuilder sftb = new FeatureTypeBuilder();
        sftb.setName("Leisure");
        sftb.add("id", Long.class, 1,1,false, FeatureTypeBuilder.PRIMARY_KEY);
        sftb.add("geometry", LineString.class, EPSG_4326);
        sftb.add("type", String.class);
        final SimpleFeatureType sft = sftb.buildSimpleFeatureType();
        store.createSchema(sft.getName(), sft);
        final SimpleFeatureType targetType = (SimpleFeatureType) store.getFeatureType("Leisure");
        //final SimpleFeatureType targetType = sft;

        //source datas----------------------------------------------------------
        final Join join1 = new DefaultJoin(
                new DefaultSelector(session, store.getFeatureType("Way").getName(), "s1"),
                new DefaultSelector(session, store.getFeatureType("WayTag").getName(), "s2"),
                JoinType.INNER,
                FF.equals(FF.property("id"), FF.property("wayId"))
                );

        final QueryBuilder qb = new QueryBuilder();
        qb.setSource(join1);
        qb.setFilter(FF.equals(FF.property("k"), FF.literal("leisure")));
        final FeatureCollection col = QueryUtilities.evaluate("translateCol", qb.buildQuery());
        final SimpleFeatureType sourceType = (SimpleFeatureType) col.getFeatureType();


        //insert the datas------------------------------------------------------
        final Map<PropertyDescriptor,List<PropertyDescriptor>> mappings = new HashMap<PropertyDescriptor,List<PropertyDescriptor>>();
        List<PropertyDescriptor> target = null;
        PropertyDescriptor p = null;

        p = sourceType.getDescriptor("id");
        target = new ArrayList<PropertyDescriptor>();
        target.add(targetType.getDescriptor("id"));
        mappings.put(p, target);

        p = sourceType.getDescriptor("geometry");
        target = new ArrayList<PropertyDescriptor>();
        target.add(targetType.getDescriptor("geometry"));
        mappings.put(p, target);

        p = sourceType.getDescriptor("v");
        target = new ArrayList<PropertyDescriptor>();
        target.add(targetType.getDescriptor("type"));
        mappings.put(p, target);

        GenericMappingFeatureCollection mapped = new GenericMappingFeatureCollection(col, targetType, mappings, new HashMap());

        store.addFeatures(targetType.getName(), mapped);
    }

    private void processWaterWay(DataStore store, Session session) throws DataStoreException{
        //create the new schema-------------------------------------------------
        final FeatureTypeBuilder sftb = new FeatureTypeBuilder();
        sftb.setName("Waterway");
        sftb.add("id", Long.class, 1,1,false, FeatureTypeBuilder.PRIMARY_KEY);
        sftb.add("geometry", LineString.class, EPSG_4326);
        sftb.add("type", String.class);
        final SimpleFeatureType sft = sftb.buildSimpleFeatureType();
        store.createSchema(sft.getName(), sft);
        final SimpleFeatureType targetType = (SimpleFeatureType) store.getFeatureType("Waterway");
        //final SimpleFeatureType targetType = sft;

        //source datas----------------------------------------------------------
        final Join join1 = new DefaultJoin(
                new DefaultSelector(session, store.getFeatureType("Way").getName(), "s1"),
                new DefaultSelector(session, store.getFeatureType("WayTag").getName(), "s2"),
                JoinType.INNER,
                FF.equals(FF.property("id"), FF.property("wayId"))
                );

        final QueryBuilder qb = new QueryBuilder();
        qb.setSource(join1);
        qb.setFilter(FF.equals(FF.property("k"), FF.literal("waterway")));
        final FeatureCollection col = QueryUtilities.evaluate("translateCol", qb.buildQuery());
        final SimpleFeatureType sourceType = (SimpleFeatureType) col.getFeatureType();


        //insert the datas------------------------------------------------------
        final Map<PropertyDescriptor,List<PropertyDescriptor>> mappings = new HashMap<PropertyDescriptor,List<PropertyDescriptor>>();
        List<PropertyDescriptor> target = null;
        PropertyDescriptor p = null;

        p = sourceType.getDescriptor("id");
        target = new ArrayList<PropertyDescriptor>();
        target.add(targetType.getDescriptor("id"));
        mappings.put(p, target);

        p = sourceType.getDescriptor("geometry");
        target = new ArrayList<PropertyDescriptor>();
        target.add(targetType.getDescriptor("geometry"));
        mappings.put(p, target);

        p = sourceType.getDescriptor("v");
        target = new ArrayList<PropertyDescriptor>();
        target.add(targetType.getDescriptor("type"));
        mappings.put(p, target);

        GenericMappingFeatureCollection mapped = new GenericMappingFeatureCollection(col, targetType, mappings, new HashMap());

        store.addFeatures(targetType.getName(), mapped);
    }

    private void processHighWay(DataStore store, Session session) throws DataStoreException{
        //create the new schema-------------------------------------------------
        final FeatureTypeBuilder sftb = new FeatureTypeBuilder();
        sftb.setName("Highway");
        sftb.add("id", Long.class, 1,1,false, FeatureTypeBuilder.PRIMARY_KEY);
        sftb.add("geometry", LineString.class, EPSG_4326);
        sftb.add("type", String.class);
        final SimpleFeatureType sft = sftb.buildSimpleFeatureType();
        store.createSchema(sft.getName(), sft);
        final SimpleFeatureType targetType = (SimpleFeatureType) store.getFeatureType("Highway");
        //final SimpleFeatureType targetType = sft;

        //source datas----------------------------------------------------------
        final Join join1 = new DefaultJoin(
                new DefaultSelector(session, store.getFeatureType("Way").getName(), "s1"),
                new DefaultSelector(session, store.getFeatureType("WayTag").getName(), "s2"),
                JoinType.INNER,
                FF.equals(FF.property("id"), FF.property("wayId"))
                );

        final QueryBuilder qb = new QueryBuilder();
        qb.setSource(join1);
        qb.setFilter(FF.equals(FF.property("k"), FF.literal("highway")));
        final FeatureCollection col = QueryUtilities.evaluate("translateCol", qb.buildQuery());
        final SimpleFeatureType sourceType = (SimpleFeatureType) col.getFeatureType();


        //insert the datas------------------------------------------------------
        final Map<PropertyDescriptor,List<PropertyDescriptor>> mappings = new HashMap<PropertyDescriptor,List<PropertyDescriptor>>();
        List<PropertyDescriptor> target = null;
        PropertyDescriptor p = null;

        p = sourceType.getDescriptor("id");
        target = new ArrayList<PropertyDescriptor>();
        target.add(targetType.getDescriptor("id"));
        mappings.put(p, target);

        p = sourceType.getDescriptor("geometry");
        target = new ArrayList<PropertyDescriptor>();
        target.add(targetType.getDescriptor("geometry"));
        mappings.put(p, target);

        p = sourceType.getDescriptor("v");
        target = new ArrayList<PropertyDescriptor>();
        target.add(targetType.getDescriptor("type"));
        mappings.put(p, target);

        GenericMappingFeatureCollection mapped = new GenericMappingFeatureCollection(col, targetType, mappings, new HashMap());

        store.addFeatures(targetType.getName(), mapped);
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        jXTitledSeparator1 = new org.jdesktop.swingx.JXTitledSeparator();
        jXTitledSeparator2 = new org.jdesktop.swingx.JXTitledSeparator();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        guiCreate = new javax.swing.JButton();

        jSplitPane1.setDividerLocation(200);
        jSplitPane1.setDividerSize(3);

        jScrollPane1.setViewportView(jTree1);

        jSplitPane1.setLeftComponent(jScrollPane1);

        jLabel1.setText("Import from :");

        jRadioButton1.setText("Node");

        jRadioButton2.setText("Way");

        jRadioButton3.setText("Relation");
        jRadioButton3.setEnabled(false);

        jLabel2.setText("Has tag :");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(jTable1);

        jLabel3.setText("Value match :");

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane3.setViewportView(jTable2);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jXTitledSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 612, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRadioButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRadioButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRadioButton3))
                    .addComponent(jXTitledSeparator2, javax.swing.GroupLayout.DEFAULT_SIZE, 612, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
                            .addComponent(jLabel3))))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jScrollPane2, jScrollPane3});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jXTitledSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jRadioButton1)
                    .addComponent(jRadioButton2)
                    .addComponent(jRadioButton3))
                .addGap(18, 18, 18)
                .addComponent(jXTitledSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addGap(7, 7, 7)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(165, 165, 165))
        );

        jSplitPane1.setRightComponent(jPanel1);

        guiCreate.setText("create");
        guiCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guiCreateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(guiCreate)
                    .addComponent(jSplitPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 777, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 530, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(guiCreate)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void guiCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guiCreateActionPerformed

        new Thread(){
            @Override
            public void run() {
                guiCreate.setEnabled(false);
                try {
                    process();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JXErrorPane.showDialog(ex);
                }
                guiCreate.setEnabled(true);
            }
        }.start();

    }//GEN-LAST:event_guiCreateActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton guiCreate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTree jTree1;
    private org.jdesktop.swingx.JXTitledSeparator jXTitledSeparator1;
    private org.jdesktop.swingx.JXTitledSeparator jXTitledSeparator2;
    // End of variables declaration//GEN-END:variables

}
