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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.geotoolkit.data.DataStore;
import org.geotoolkit.data.DataStoreException;
import org.geotoolkit.data.DataStoreFinder;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.osm.model.Node;
import org.geotoolkit.data.osm.model.Relation;
import org.geotoolkit.data.osm.model.Way;
import org.geotoolkit.data.osm.xml.OSMXMLConstants;
import org.geotoolkit.data.query.QueryBuilder;

import org.jdesktop.swingx.JXErrorPane;
import org.opengis.feature.simple.SimpleFeature;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class JOSMAnalyzePane extends javax.swing.JPanel {

    private Map<String,Serializable> dbParameters = null;
    private DataStore store = null;

    private final DefaultTreeModel model;
    private final DefaultMutableTreeNode rootTreeNode = new DefaultMutableTreeNode("root");
    private final DefaultMutableTreeNode nodeTreeNode = new DefaultMutableTreeNode(Node.class);
    private final DefaultMutableTreeNode wayTreeNode = new DefaultMutableTreeNode(Way.class);
    private final DefaultMutableTreeNode relationTreeNode = new DefaultMutableTreeNode(Relation.class);

    private JOSMAnalyzeResultPane currentDetail = null;

    /** Creates new form JOSMAnalyzePane */
    public JOSMAnalyzePane() {
        initComponents();
        
        rootTreeNode.add(nodeTreeNode);
        rootTreeNode.add(wayTreeNode);
        rootTreeNode.add(relationTreeNode);

        model = new DefaultTreeModel(rootTreeNode);

        guiTree.setModel(model);
        guiTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        guiTree.setRootVisible(false);
        guiTree.setCellRenderer(new TreeRenderer());
        guiTree.setComponentPopupMenu(new TreeMenu());

        guiTree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent tse) {
                final TreePath path = tse.getNewLeadSelectionPath();

                if(path != null){
                    final DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                    final Object obj = node.getUserObject();
                    if(obj instanceof AnalyzeResult){
                        final AnalyzeResult res = (AnalyzeResult) obj;
                        currentDetail = new JOSMAnalyzeResultPane();
                        currentDetail.setDataStore(store);
                        currentDetail.setAnalyzeResult(res);
                        currentDetail.sortBy(guiSortBy.isSelected());

                    }
                }

                guiDetail.removeAll();
                if(currentDetail != null){
                    guiDetail.add(BorderLayout.CENTER,currentDetail);
                }
                guiDetail.revalidate();
                guiDetail.repaint();
            }
        });

    }

    private DataStore getDataStore(){
        if(store == null){
            try {
                store = DataStoreFinder.getDataStore(dbParameters);
            } catch (DataStoreException ex) {
                JXErrorPane.showDialog(ex);
            }
        }

        return store;
    }

    public void setDBParameters(Map<String, Serializable> dBConnectionParameters) {
        if(store != null){
            store.dispose();
            store = null;
        }
        this.dbParameters = dBConnectionParameters;
    }

    private void reorder(DefaultMutableTreeNode node){

        final List<AnalyzeResult> results = new ArrayList<AnalyzeResult>();

        //get all analyze results
        final int count = node.getChildCount();
        for(int i=0;i<count;i++){
            final DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
            final Object obj = child.getUserObject();
            if(obj instanceof AnalyzeResult){
                results.add((AnalyzeResult)obj);
            }
        }

        if(!results.isEmpty()){
            if(guiSortBy.isSelected()){
                Collections.sort(results, new Comparator<AnalyzeResult>(){
                    @Override
                    public int compare(AnalyzeResult t, AnalyzeResult t1) {
                        return t1.tagCount - t.tagCount;
                    }
                });
            }else{
                Collections.sort(results, new Comparator<AnalyzeResult>(){
                    @Override
                    public int compare(AnalyzeResult t, AnalyzeResult t1) {
                        return t.tagKey.compareTo(t1.tagKey);
                    }
                });
            }
            node.removeAllChildren();
            for(AnalyzeResult res : results){
                node.add(new DefaultMutableTreeNode(res));
            }
            model.nodeStructureChanged(node);
        }

        //reorder the childs
        for(int i=0;i<count;i++){
            reorder((DefaultMutableTreeNode) node.getChildAt(i));
        }

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jsp1 = new javax.swing.JScrollPane();
        guiTree = new javax.swing.JTree();
        guiDetail = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        guiSortBy = new javax.swing.JCheckBox();
        guiSplit = new javax.swing.JCheckBox();

        setLayout(new java.awt.BorderLayout());

        jSplitPane1.setDividerLocation(250);
        jSplitPane1.setDividerSize(4);

        jsp1.setViewportView(guiTree);

        jSplitPane1.setLeftComponent(jsp1);

        guiDetail.setLayout(new java.awt.BorderLayout());
        jSplitPane1.setRightComponent(guiDetail);

        add(jSplitPane1, java.awt.BorderLayout.CENTER);

        guiSortBy.setSelected(true);
        guiSortBy.setText("Sort by occurence count");
        guiSortBy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guiSortByActionPerformed(evt);
            }
        });

        guiSplit.setSelected(true);
        guiSplit.setText("Split on [;]");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(guiSortBy)
                .addGap(18, 18, 18)
                .addComponent(guiSplit)
                .addContainerGap(259, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(guiSortBy)
                    .addComponent(guiSplit))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(jPanel1, java.awt.BorderLayout.NORTH);
    }// </editor-fold>//GEN-END:initComponents

    private void guiSortByActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guiSortByActionPerformed
        new Thread(){

            @Override
            public void run() {
                reorder(rootTreeNode);
                guiTree.revalidate();
                guiTree.repaint();
            }
        }.start();

        if(currentDetail != null){
            currentDetail = new JOSMAnalyzeResultPane();
            currentDetail.sortBy(guiSortBy.isSelected());
        }

    }//GEN-LAST:event_guiSortByActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel guiDetail;
    private javax.swing.JCheckBox guiSortBy;
    private javax.swing.JCheckBox guiSplit;
    private javax.swing.JTree guiTree;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JScrollPane jsp1;
    // End of variables declaration//GEN-END:variables


    private class TreeRenderer extends DefaultTreeCellRenderer{

        @Override
        public Component getTreeCellRendererComponent(JTree jtree, Object o, boolean bln, boolean bln1, boolean bln2, int i, boolean bln3) {
            final JLabel lbl = (JLabel) super.getTreeCellRendererComponent(jtree, o, bln, bln1, bln2, i, bln3);

            if(o instanceof DefaultMutableTreeNode){
                o = ((DefaultMutableTreeNode)o).getUserObject();
            }

            if(o.equals(Node.class)){
                lbl.setText("Node");
            }else if(o .equals(Way.class)){
                lbl.setText("Way");
            }else if(o.equals(Relation.class)){
                lbl.setText("Relation");
            }else if(o instanceof AnalyzeResult){
                AnalyzeResult res = (AnalyzeResult) o;
                lbl.setText("<html><b>" + res.tagCount +"</b> - "+res.tagKey+"</html>");
            }

            return lbl;
        }

    }

    private class TreeMenu extends JPopupMenu{

        @Override
        public void setVisible(boolean bln) {
            TreeMenu.this.removeAll();
            if(bln){
                final DefaultMutableTreeNode node = (DefaultMutableTreeNode) guiTree.getSelectionPath().getLastPathComponent();
                final Object o = node.getUserObject();

                final JMenuItem item = new JMenuItem();
                item.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        new AnalyzeThread((Class)o).start();
                    }
                });
                
                if(o.equals(Node.class)){
                    item.setText("Analyze node tags");
                    TreeMenu.this.add(item);
                }else if(o.equals(Way.class)){
                    item.setText("Analyze way tags");
                    TreeMenu.this.add(item);
                }else if(o.equals(Relation.class)){
                    item.setText("Analyze relation tags");
                    TreeMenu.this.add(item);
                }
            }

            super.setVisible(bln);
        }

    }

    private class AnalyzeThread extends Thread{

        private final String table;

        public AnalyzeThread(Class type) {
            if(type.equals(Node.class)){
                table = "NodeTag";
            }else if(type.equals(Way.class)){
                table = "WayTag";
            }else if(type.equals(Relation.class)){
                table = "RelationTag";
            }else{
                throw new IllegalArgumentException("Unknowned type : " + type);
            }
        }

        private void analyze() throws DataStoreException{
            final DataStore store = getDataStore();

            final QueryBuilder qb = new QueryBuilder();
            qb.setTypeName(store.getFeatureType(table).getName());
            final FeatureReader reader = store.getFeatureReader(qb.buildQuery());

            final long before = System.currentTimeMillis();
            System.out.println("start analyze");

            //collect results
            final Map<String,AnalyzeResult> analyze = new HashMap<String, AnalyzeResult>();
            try{
                while(reader.hasNext()){
                    final SimpleFeature f = (SimpleFeature) reader.next();
                    final String key = f.getAttribute(OSMXMLConstants.ATT_TAG_KEY).toString();
                    final String value = f.getAttribute(OSMXMLConstants.ATT_TAG_VALUE).toString();

                    AnalyzeResult result = analyze.get(key);
                    if(result == null){
                        result = new AnalyzeResult(table,key);
                        analyze.put(key, result);
                    }
                    result.tagCount++;

                    if(guiSplit.isSelected()){
                        for(String part : value.split(";")){
                            result.incrementValue(part.trim());
                        }
                    }else{
                        result.incrementValue(value);
                    }
                    
                }
            }finally{
                reader.close();
            }

            final long after = System.currentTimeMillis();
            System.out.println("end analyze : " + (after-before) +" ms");
            System.out.println("size = "+analyze.size());

            if(table.equals("NodeTag")){
                nodeTreeNode.removeAllChildren();
                int i=0;
                for(AnalyzeResult res : analyze.values()){
                    model.insertNodeInto(new DefaultMutableTreeNode(res), nodeTreeNode, i);
                    i++;
                }
                reorder(nodeTreeNode);
            }else if(table.equals("WayTag")){
                wayTreeNode.removeAllChildren();
                int i=0;
                for(AnalyzeResult res : analyze.values()){
                    model.insertNodeInto(new DefaultMutableTreeNode(res), wayTreeNode, i);
                    i++;
                }
                reorder(wayTreeNode);
            }else if(table.equals("RelationTag")){
                relationTreeNode.removeAllChildren();
                int i=0;
                for(AnalyzeResult res : analyze.values()){
                    model.insertNodeInto(new DefaultMutableTreeNode(res), relationTreeNode, i);
                    i++;
                }
                reorder(relationTreeNode);
            }else{
                throw new IllegalArgumentException("Unknowned table : " + table);
            }

            guiTree.revalidate();
            guiTree.repaint();

        }

        @Override
        public void run() {
            try {
                analyze();
            } catch (DataStoreException ex) {
                JXErrorPane.showDialog(ex);
            }
        }

    }

}
