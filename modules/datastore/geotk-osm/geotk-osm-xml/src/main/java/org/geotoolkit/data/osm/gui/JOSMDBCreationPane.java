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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.xml.stream.XMLStreamException;

import org.geotoolkit.data.osm.db.OSMPostgresDB;
import org.geotoolkit.data.osm.xml.OSMXMLReader;
import org.geotoolkit.data.osm.model.IdentifiedElement;
import org.geotoolkit.data.osm.model.Node;
import org.geotoolkit.data.osm.model.Relation;
import org.geotoolkit.data.osm.model.Way;

import org.jdesktop.swingx.JXErrorPane;


/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class JOSMDBCreationPane extends javax.swing.JPanel {

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        JFrame frm = new JFrame("OSM Postgres import.");
        JOSMDBCreationPane pane = new JOSMDBCreationPane();
        frm.setContentPane(pane);
        frm.pack();
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.setLocationRelativeTo(null);
        frm.setVisible(true);
    }


    private OSMPostgresDB pgDB=null;
    private OSMXMLReader reader=null;


    private final int batchSize = 1000;
    private final List<Node> nodeBuffer = new ArrayList<Node>(batchSize);
    private final List<Way> wayBuffer = new ArrayList<Way>(batchSize);
    private final List<Relation> relationBuffer = new ArrayList<Relation>(batchSize);

    private int nbNodes=0;
    private int nbNodesTag=0;
    private int nbWays=0;
    private int nbWaysTag=0;
    private int nbWaysMember=0;
    private int nbRelations=0;
    private int nbRelationsTag=0;
    private int nbRelationsMember=0;
    private int nbUser=0;
    private long restartReference=-1;

    /** Creates new form JOSMDBCreationPane */
    public JOSMDBCreationPane() {
        initComponents();
        guiSepCreate.setTitle("Creation");
        guiSepIntegrity.setTitle("Integrity");
        guiSepGeometry.setTitle("PostGIS Geometry");
    }

    private void reset(){

        guiLblCreateFK.setBusy(false);
        guiLblCreatePK.setBusy(false);
        guiLblCreateTable.setBusy(false);
        guiLblDropTable.setBusy(false);
        guiLblInsertData.setBusy(false);
        guiLblRestart.setBusy(false);

        nodeBuffer.clear();
        wayBuffer.clear();
        relationBuffer.clear();
        nbNodes=0;
        nbNodesTag=0;
        nbWays=0;
        nbWaysTag=0;
        nbWaysMember=0;
        nbRelations=0;
        nbRelationsTag=0;
        nbRelationsMember=0;
        nbUser=0;
        restartReference=-1;
        updatecounters();
    }

    private void updatecounters(){
        guiNodes.setText(String.valueOf(nbNodes));
        guiNodesTag.setText(String.valueOf(nbNodesTag));
        guiWays.setText(String.valueOf(nbWays));
        guiWaysTag.setText(String.valueOf(nbWaysTag));
        guiWaysMember.setText(String.valueOf(nbWaysMember));
        guiRelations.setText(String.valueOf(nbRelations));
        guiRelationsTag.setText(String.valueOf(nbRelationsTag));
        guiRelationsMember.setText(String.valueOf(nbRelationsMember));
        guiUsers.setText(String.valueOf(nbUser));
        guiCrash.setText(String.valueOf(restartReference));
    }

    private void dropTables() throws SQLException{
        guiLblDropTable.setBusy(true);
        final long before = System.currentTimeMillis();

        pgDB.clearDataBase();
        pgDB.commit();

        final long after = System.currentTimeMillis();
        guiLblDropTable.setBusy(false);
        guiLblDropTable.setText("Drop tables : " + (after-before) + " ms");
    }

    private void createTables() throws SQLException{
        guiLblCreateTable.setBusy(true);
        final long before = System.currentTimeMillis();

        pgDB.createTables();
        pgDB.commit();

        final long after = System.currentTimeMillis();
        guiLblCreateTable.setBusy(false);
        guiLblCreateTable.setText("Create tables : " + (after-before) + " ms");
    }

    private void createGeometryFields() throws SQLException{
        guiLblCreateGeomField.setBusy(true);
        final long before = System.currentTimeMillis();

        pgDB.createGeometryFields();
        pgDB.commit();

        final long after = System.currentTimeMillis();
        guiLblCreateGeomField.setBusy(false);
        guiLblCreateGeomField.setText("Create geometry fields : " + (after-before) + " ms");
    }

    private void generateGeometries() throws SQLException{
        guiLblGenerateGeometry.setBusy(true);
        final long before = System.currentTimeMillis();

        pgDB.generateGeometries();
        pgDB.commit();

        final long after = System.currentTimeMillis();
        guiLblGenerateGeometry.setBusy(false);
        guiLblGenerateGeometry.setText("Generate geometries : " + (after-before) + " ms");
    }

    private void generateIndex() throws SQLException{
        guiLblGenerateIndex.setBusy(true);
        final long before = System.currentTimeMillis();

        pgDB.createIndexes();
        pgDB.commit();

        final long after = System.currentTimeMillis();
        guiLblGenerateIndex.setBusy(false);
        guiLblGenerateIndex.setText("Generate index : " + (after-before) + " ms");
    }

    private void commitNodeBuffer() throws SQLException{
        restartReference = nodeBuffer.get(0).getId();

        final int[] nbs = pgDB.insertNode(nodeBuffer);
        nodeBuffer.clear();
        nbNodes += nbs[0];
        nbNodesTag += nbs[1];
        nbUser += nbs[2];
        updatecounters();

        pgDB.commit();
    }

    private void commitWayBuffer() throws SQLException{
        restartReference = wayBuffer.get(0).getId();

        final int[] nbs = pgDB.insertWay(wayBuffer);
        wayBuffer.clear();
        nbWays += nbs[0];
        nbWaysTag += nbs[1];
        nbWaysMember += nbs[2];
        nbUser += nbs[3];
        updatecounters();

        pgDB.commit();
    }

    private void commitRelationBuffer() throws SQLException{
        restartReference = relationBuffer.get(0).getId();
        
        final int[] nbs = pgDB.insertRelation(relationBuffer);
        relationBuffer.clear();
        nbRelations += nbs[0];
        nbRelationsTag += nbs[1];
        nbRelationsMember += nbs[2];
        nbUser += nbs[3];
        updatecounters();
        
        pgDB.commit();
    }

    private void restartAt(long index) throws SQLException, XMLStreamException{
        guiLblRestart.setBusy(true);
        final long before = System.currentTimeMillis();

        //we must restart at a given id
        //load the already register user ids
        pgDB.checkUserIds();
        //move to the given id in the xml
        reader.moveTo(index);

        int[] nbs = pgDB.countAll();
        nbNodes = nbs[0];
        nbNodesTag = nbs[1];
        nbWays = nbs[2];
        nbWaysTag = nbs[3];
        nbWaysMember = nbs[4];
        nbRelations = nbs[5];
        nbRelationsTag = nbs[6];
        nbRelationsMember = nbs[7];
        nbUser = nbs[8];
        updatecounters();

        final long after = System.currentTimeMillis();
        guiLblRestart.setBusy(false);
        guiLblRestart.setText("Restart at : " + (after-before) + " ms");
    }

    private void readDatas() throws SQLException, XMLStreamException, IOException{
        guiLblInsertData.setBusy(true);
        final long before = System.currentTimeMillis();

        IdentifiedElement ele = null;
        try{
            while(reader.hasNext()){
                //elements are ordered in the osm file

                ele = reader.next();
                if(ele instanceof Node){
                    nodeBuffer.add((Node) ele);

                    if(nodeBuffer.size() == batchSize){
                        commitNodeBuffer();
                    }
                }

                else if(ele instanceof Way){
                    //insert the remaining if any, to preserve order
                    if(!nodeBuffer.isEmpty()) commitNodeBuffer();

                    wayBuffer.add((Way) ele);

                    if(wayBuffer.size() == batchSize){
                        commitWayBuffer();
                    }
                }

                else if(ele instanceof Relation){
                    //insert the remaining if any, to preserve order
                    if(!nodeBuffer.isEmpty()) commitNodeBuffer();
                    if(!wayBuffer.isEmpty()) commitWayBuffer();

                    relationBuffer.add((Relation) ele);

                    if(relationBuffer.size() == batchSize){
                        commitRelationBuffer();
                    }
                }
            }
        }finally{
            reader.close();
        }

        //insert the remaining if any
        if(!nodeBuffer.isEmpty()) commitNodeBuffer();
        if(!wayBuffer.isEmpty()) commitWayBuffer();
        if(!relationBuffer.isEmpty()) commitRelationBuffer();

        final long after = System.currentTimeMillis();
        guiLblInsertData.setBusy(false);
        guiLblInsertData.setText("Insert Datas : " + (after-before) + " ms");

        System.out.println("Time to read = " + (after-before) + " ms");
    }

    private void eraseDuplicate() throws SQLException{
        guiLblEraseDuplicate.setBusy(true);
        final long before = System.currentTimeMillis();

        pgDB.eraseDuplicates();
        pgDB.commit();

        final long after = System.currentTimeMillis();
        guiLblEraseDuplicate.setBusy(false);
        guiLblEraseDuplicate.setText("Erase duplicate : " + (after-before) + " ms");
    }

    private void createPK() throws SQLException{
        guiLblCreatePK.setBusy(true);
        final long before = System.currentTimeMillis();

        pgDB.createPrimaryKeys();
        pgDB.commit();

        final long after = System.currentTimeMillis();
        guiLblCreatePK.setBusy(false);
        guiLblCreatePK.setText("Create PK : " + (after-before) + " ms");
    }

    private void createFK() throws SQLException{
        guiLblCreateFK.setBusy(true);
        final long before = System.currentTimeMillis();

        pgDB.createForeignKeys();
        pgDB.commit();

        final long after = System.currentTimeMillis();
        guiLblCreateFK.setBusy(false);
        guiLblCreateFK.setText("Create FK : " + (after-before) + " ms");
    }

    private void processOperations() throws SQLException, ClassNotFoundException, FileNotFoundException, XMLStreamException, IOException{
        reset();

        File f= new File(guiPath.getText());

        pgDB = new OSMPostgresDB(guiHost.getText(),guiDatabase.getText(),guiUser.getText(),new String(guiPassword.getPassword()));
        reader = new OSMXMLReader(f);

        if(guiDropTable.isSelected()){
            dropTables();
        }

        if(guiCreateTable.isSelected()){
            createTables();
        }

        final Long index = (Long) guiRestartAt.getValue();
        if(index > 0){
            restartAt(index);
        }

        if(guiinsertData.isSelected()){
            try{
                readDatas();
            }catch(Exception ex){
                //return to the last valid point.
                JXErrorPane.showDialog(ex);
                pgDB.rollBack();
            }
        }

        if(guiEraseDuplicate.isSelected()){
            eraseDuplicate();
        }

        if(guiCreatePK.isSelected()){
            createPK();
        }

        if(guiCreateFK.isSelected()){
            createFK();
        }

        if(guiCreateGeomField.isSelected()){
            createGeometryFields();
        }

        if(guiGenerateGeometries.isSelected()){
            generateGeometries();
        }

        if(guiGenerateIndex.isSelected()){
            generateIndex();
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

        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        guiNodes = new javax.swing.JTextField();
        guiNodesTag = new javax.swing.JTextField();
        guiWays = new javax.swing.JTextField();
        guiWaysTag = new javax.swing.JTextField();
        guiWaysMember = new javax.swing.JTextField();
        guiRelations = new javax.swing.JTextField();
        guiRelationsTag = new javax.swing.JTextField();
        guiRelationsMember = new javax.swing.JTextField();
        guiUsers = new javax.swing.JTextField();
        guiCreate = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        guiLblDropTable = new org.jdesktop.swingx.JXBusyLabel();
        guiLblCreateTable = new org.jdesktop.swingx.JXBusyLabel();
        guiLblInsertData = new org.jdesktop.swingx.JXBusyLabel();
        guiLblCreatePK = new org.jdesktop.swingx.JXBusyLabel();
        guiLblCreateFK = new org.jdesktop.swingx.JXBusyLabel();
        guiDropTable = new javax.swing.JCheckBox();
        guiCreateTable = new javax.swing.JCheckBox();
        guiinsertData = new javax.swing.JCheckBox();
        guiCreatePK = new javax.swing.JCheckBox();
        guiCreateFK = new javax.swing.JCheckBox();
        guiRestartAt = new javax.swing.JSpinner();
        guiLblRestart = new org.jdesktop.swingx.JXBusyLabel();
        guiLblEraseDuplicate = new org.jdesktop.swingx.JXBusyLabel();
        guiEraseDuplicate = new javax.swing.JCheckBox();
        guiSepIntegrity = new org.jdesktop.swingx.JXTitledSeparator();
        guiSepCreate = new org.jdesktop.swingx.JXTitledSeparator();
        guiSepGeometry = new org.jdesktop.swingx.JXTitledSeparator();
        guiLblCreateGeomField = new org.jdesktop.swingx.JXBusyLabel();
        guiCreateGeomField = new javax.swing.JCheckBox();
        guiLblGenerateGeometry = new org.jdesktop.swingx.JXBusyLabel();
        guiGenerateGeometries = new javax.swing.JCheckBox();
        guiLblGenerateIndex = new org.jdesktop.swingx.JXBusyLabel();
        guiGenerateIndex = new javax.swing.JCheckBox();
        jLabel12 = new javax.swing.JLabel();
        guiCrash = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        guiPath = new javax.swing.JTextField();
        guiChooser = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        guiHost = new javax.swing.JTextField();
        guiDatabase = new javax.swing.JTextField();
        guiUser = new javax.swing.JTextField();
        guiPassword = new javax.swing.JPasswordField();

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel2.setFont(jLabel2.getFont().deriveFont(jLabel2.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel2.setText("Nodes =");

        jLabel3.setText("Nodes tag =");

        jLabel4.setFont(jLabel4.getFont().deriveFont(jLabel4.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel4.setText("Ways =");

        jLabel5.setText("Ways tag =");

        jLabel6.setText("Ways member =");

        jLabel7.setFont(jLabel7.getFont().deriveFont(jLabel7.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel7.setText("Relations =");

        jLabel8.setText("Relations tag =");

        jLabel9.setText("Relations member =");

        jLabel10.setFont(jLabel10.getFont().deriveFont(jLabel10.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel10.setText("Users =");

        guiNodes.setEditable(false);
        guiNodes.setFont(guiNodes.getFont().deriveFont(guiNodes.getFont().getStyle() | java.awt.Font.BOLD));
        guiNodes.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        guiNodes.setText("0");

        guiNodesTag.setEditable(false);
        guiNodesTag.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        guiNodesTag.setText("0");

        guiWays.setEditable(false);
        guiWays.setFont(guiWays.getFont().deriveFont(guiWays.getFont().getStyle() | java.awt.Font.BOLD));
        guiWays.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        guiWays.setText("0");

        guiWaysTag.setEditable(false);
        guiWaysTag.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        guiWaysTag.setText("0");

        guiWaysMember.setEditable(false);
        guiWaysMember.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        guiWaysMember.setText("0");

        guiRelations.setEditable(false);
        guiRelations.setFont(guiRelations.getFont().deriveFont(guiRelations.getFont().getStyle() | java.awt.Font.BOLD));
        guiRelations.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        guiRelations.setText("0");

        guiRelationsTag.setEditable(false);
        guiRelationsTag.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        guiRelationsTag.setText("0");

        guiRelationsMember.setEditable(false);
        guiRelationsMember.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        guiRelationsMember.setText("0"); // NOI18N

        guiUsers.setEditable(false);
        guiUsers.setFont(guiUsers.getFont().deriveFont(guiUsers.getFont().getStyle() | java.awt.Font.BOLD));
        guiUsers.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        guiUsers.setText("0");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(guiNodes, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(guiNodesTag, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(guiWays, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(guiWaysTag, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(guiWaysMember, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(guiRelations, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(guiRelationsTag, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(guiRelationsMember, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(guiUsers, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel10, jLabel2, jLabel3, jLabel4, jLabel5, jLabel6, jLabel7, jLabel8, jLabel9});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(guiNodes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(guiNodesTag, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(guiWays, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(guiWaysTag, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(guiWaysMember, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(guiRelations, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(guiRelationsTag, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(guiRelationsMember, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(guiUsers, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(117, Short.MAX_VALUE))
        );

        guiCreate.setText("Run");
        guiCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guiCreateActionPerformed(evt);
            }
        });

        guiLblDropTable.setText("Drop tables");

        guiLblCreateTable.setText("Create tables");

        guiLblInsertData.setText("Insert Datas");

        guiLblCreatePK.setText("Create PK");

        guiLblCreateFK.setText("Create FK");

        guiDropTable.setSelected(true);

        guiCreateTable.setSelected(true);

        guiinsertData.setSelected(true);

        guiCreatePK.setSelected(true);

        guiCreateFK.setSelected(true);

        guiRestartAt.setModel(new javax.swing.SpinnerNumberModel(Long.valueOf(-1L), Long.valueOf(-1L), null, Long.valueOf(1L)));

        guiLblRestart.setText("Restart at");

        guiLblEraseDuplicate.setText("Erase duplicate");

        guiLblCreateGeomField.setText("Create geometry fields");

        guiCreateGeomField.setSelected(true);

        guiLblGenerateGeometry.setText("Generate geometries");

        guiGenerateGeometries.setSelected(true);

        guiLblGenerateIndex.setText("Generate index");

        guiGenerateIndex.setSelected(true);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(guiSepCreate, javax.swing.GroupLayout.DEFAULT_SIZE, 379, Short.MAX_VALUE)
                    .addComponent(guiSepIntegrity, javax.swing.GroupLayout.DEFAULT_SIZE, 379, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(guiLblDropTable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(guiLblCreateTable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(guiLblInsertData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(guiDropTable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(guiinsertData, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(guiCreateTable)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(guiLblRestart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(guiRestartAt, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(guiLblEraseDuplicate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(guiEraseDuplicate))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(guiLblCreatePK, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(guiLblCreateFK, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(guiCreatePK, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(guiCreateFK)))
                    .addComponent(guiSepGeometry, javax.swing.GroupLayout.DEFAULT_SIZE, 379, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(guiLblCreateGeomField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(guiCreateGeomField))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(guiLblGenerateGeometry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(guiGenerateGeometries))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(guiLblGenerateIndex, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(guiGenerateIndex)))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {guiLblCreateFK, guiLblCreatePK, guiLblEraseDuplicate});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {guiLblCreateTable, guiLblDropTable, guiLblInsertData});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {guiLblCreateGeomField, guiLblGenerateGeometry, guiLblGenerateIndex});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(guiSepCreate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(guiLblDropTable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(guiDropTable, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(guiLblCreateTable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(guiCreateTable, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(guiLblInsertData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(guiinsertData, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(guiRestartAt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(guiLblRestart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(guiSepIntegrity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(guiLblEraseDuplicate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(guiEraseDuplicate, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(guiLblCreatePK, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(guiCreatePK, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(guiLblCreateFK, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(guiCreateFK, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(guiSepGeometry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(guiCreateGeomField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(guiLblCreateGeomField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(guiGenerateGeometries, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(guiLblGenerateGeometry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(guiGenerateIndex, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(guiLblGenerateIndex, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel12.setFont(jLabel12.getFont().deriveFont(jLabel12.getFont().getStyle() | java.awt.Font.BOLD, jLabel12.getFont().getSize()+2));
        jLabel12.setForeground(new java.awt.Color(255, 0, 0));
        jLabel12.setText("Restart ID (in crash cases) :");

        guiCrash.setEditable(false);
        guiCrash.setText("-1");

        jLabel1.setText("File :");

        guiPath.setText("/home/sorel/GIS_DATA/switzerland.osm");

        guiChooser.setText("...");
        guiChooser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guiChooserActionPerformed(evt);
            }
        });

        jLabel11.setText("Host :");

        jLabel13.setText("DataBase :");

        jLabel14.setText("User :");

        jLabel15.setText("Password :");

        guiHost.setText("localhost:5432");

        guiDatabase.setText("osm_swiss");

        guiUser.setText("postgres");

        guiPassword.setText("postgres");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(guiPath, javax.swing.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(guiChooser))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(guiHost, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(guiDatabase, javax.swing.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(guiUser, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(guiPassword, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(guiPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(guiChooser))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(guiHost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(guiDatabase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(guiUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(guiPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(guiCrash, javax.swing.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(guiCreate))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jPanel2, jPanel3});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(guiCreate)
                    .addComponent(jLabel12)
                    .addComponent(guiCrash, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void guiCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guiCreateActionPerformed

        Thread t = new Thread(){

            @Override
            public void run() {
                guiPath.setEnabled(false);
                guiChooser.setEnabled(false);
                guiCreate.setEnabled(false);
                try {
                    processOperations();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JXErrorPane.showDialog(ex);
                }
                guiPath.setEnabled(true);
                guiChooser.setEnabled(true);
                guiCreate.setEnabled(true);
            }

        };
        t.start();

    }//GEN-LAST:event_guiCreateActionPerformed

    private void guiChooserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guiChooserActionPerformed

        final JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setMultiSelectionEnabled(false);
        int val = chooser.showOpenDialog(this);

        if(val == JFileChooser.APPROVE_OPTION){
            File f = chooser.getSelectedFile();
            guiPath.setText(f.getAbsolutePath());
        }

    }//GEN-LAST:event_guiChooserActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton guiChooser;
    private javax.swing.JTextField guiCrash;
    private javax.swing.JButton guiCreate;
    private javax.swing.JCheckBox guiCreateFK;
    private javax.swing.JCheckBox guiCreateGeomField;
    private javax.swing.JCheckBox guiCreatePK;
    private javax.swing.JCheckBox guiCreateTable;
    private javax.swing.JTextField guiDatabase;
    private javax.swing.JCheckBox guiDropTable;
    private javax.swing.JCheckBox guiEraseDuplicate;
    private javax.swing.JCheckBox guiGenerateGeometries;
    private javax.swing.JCheckBox guiGenerateIndex;
    private javax.swing.JTextField guiHost;
    private org.jdesktop.swingx.JXBusyLabel guiLblCreateFK;
    private org.jdesktop.swingx.JXBusyLabel guiLblCreateGeomField;
    private org.jdesktop.swingx.JXBusyLabel guiLblCreatePK;
    private org.jdesktop.swingx.JXBusyLabel guiLblCreateTable;
    private org.jdesktop.swingx.JXBusyLabel guiLblDropTable;
    private org.jdesktop.swingx.JXBusyLabel guiLblEraseDuplicate;
    private org.jdesktop.swingx.JXBusyLabel guiLblGenerateGeometry;
    private org.jdesktop.swingx.JXBusyLabel guiLblGenerateIndex;
    private org.jdesktop.swingx.JXBusyLabel guiLblInsertData;
    private org.jdesktop.swingx.JXBusyLabel guiLblRestart;
    private javax.swing.JTextField guiNodes;
    private javax.swing.JTextField guiNodesTag;
    private javax.swing.JPasswordField guiPassword;
    private javax.swing.JTextField guiPath;
    private javax.swing.JTextField guiRelations;
    private javax.swing.JTextField guiRelationsMember;
    private javax.swing.JTextField guiRelationsTag;
    private javax.swing.JSpinner guiRestartAt;
    private org.jdesktop.swingx.JXTitledSeparator guiSepCreate;
    private org.jdesktop.swingx.JXTitledSeparator guiSepGeometry;
    private org.jdesktop.swingx.JXTitledSeparator guiSepIntegrity;
    private javax.swing.JTextField guiUser;
    private javax.swing.JTextField guiUsers;
    private javax.swing.JTextField guiWays;
    private javax.swing.JTextField guiWaysMember;
    private javax.swing.JTextField guiWaysTag;
    private javax.swing.JCheckBox guiinsertData;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    // End of variables declaration//GEN-END:variables

}
