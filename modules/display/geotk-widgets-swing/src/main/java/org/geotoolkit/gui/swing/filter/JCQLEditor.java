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
package org.geotoolkit.gui.swing.filter;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.geotoolkit.cql.CQLException;
import org.geotoolkit.filter.function.FunctionFactory;
import org.geotoolkit.filter.function.Functions;
import org.geotoolkit.gui.swing.util.JOptionDialog;
import org.geotoolkit.gui.swing.resource.FontAwesomeIcons;
import org.geotoolkit.gui.swing.resource.IconBuilder;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.MapLayer;
import org.jdesktop.swingx.combobox.ListComboBoxModel;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.expression.Expression;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.util.InternationalString;

/**
 * CQL filter panel
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class JCQLEditor extends javax.swing.JPanel{

    private static final ImageIcon ICON_FILTER = IconBuilder.createIcon(FontAwesomeIcons.ICON_FILTER, 16, FontAwesomeIcons.DEFAULT_COLOR);
    private static final ImageIcon ICON_FUNCTION = IconBuilder.createIcon(FontAwesomeIcons.ICON_COG, 16, Color.GRAY);
    private static final ImageIcon ICON_GROUP = IconBuilder.createIcon(FontAwesomeIcons.ICON_FOLDER, 16, Color.GRAY);

    private MapLayer layer;

    /** Creates new form JCQLPropertyPanel */
    public JCQLEditor() {
        initComponents();

        final DefaultMutableTreeNode root = new org.geotoolkit.gui.swing.tree.DefaultMutableTreeNode("root");

        for(FunctionFactory ff : Functions.getFactories()){
            final DefaultMutableTreeNode fnode = new org.geotoolkit.gui.swing.tree.DefaultMutableTreeNode(ff.getIdentifier());
            String[] names = ff.getNames();
            Arrays.sort(names);
            for(String str : names){
                final ParameterDescriptorGroup desc = ff.describeFunction(str);
                final DefaultMutableTreeNode enode = new org.geotoolkit.gui.swing.tree.DefaultMutableTreeNode(desc);
                fnode.add(enode);
            }
            root.add(fnode);
        }

        guiFunctions.setModel(new DefaultTreeModel(root));
        guiFunctions.setRootVisible(false);
        guiFunctions.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        guiFunctions.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {

                final TreePath ob = guiFunctions.getSelectionPath();
                if(ob != null){
                    final DefaultMutableTreeNode node = (DefaultMutableTreeNode) ob.getLastPathComponent();
                    if(node.getChildCount()==0 && node.getUserObject() instanceof ParameterDescriptorGroup){
                        final ParameterDescriptorGroup desc = (ParameterDescriptorGroup) node.getUserObject();
                        final StringBuilder sb = new StringBuilder();
                        sb.append(desc.getName().getCode()).append('(');
                        final List<GeneralParameterDescriptor> gpds = desc.descriptors();
                        for(int i=0;i<gpds.size();i++){
                            if(i>0) sb.append(',');
                            sb.append(gpds.get(i).getName().getCode());
                        }
                        sb.append(')');
                        guiCQL.insertText(" "+sb.toString());
                        guiFunctions.clearSelection();
                    }
                }
            }
        });
        guiFunctions.setCellRenderer(new DefaultTreeCellRenderer(){
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
                    boolean expanded, boolean leaf, int row, boolean hasFocus) {
                final JLabel lbl =  (JLabel)super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
                if(value instanceof DefaultMutableTreeNode){
                    value = ((DefaultMutableTreeNode)value).getUserObject();
                }
                if(value instanceof ParameterDescriptorGroup){
                    final ParameterDescriptorGroup desc = (ParameterDescriptorGroup) value;
                    lbl.setText(desc.getName().getCode());
                    lbl.setIcon(ICON_FUNCTION);
                }else{
                    lbl.setIcon(ICON_GROUP);
                }
                return lbl;
            }

        });

        guiProperties.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                Object ob = guiProperties.getSelectedValue();
                if(ob != null){
                    final String name = ((PropertyDescriptor)ob).getName().getLocalPart();
                    guiCQL.insertText(" "+name);
                    guiProperties.clearSelection();
                }
            }
        });
        guiProperties.setCellRenderer(new PropertyRenderer());

        final ActionListener actListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final JButton but = (JButton) e.getSource();
                guiCQL.insertText(but.getName());
            }
        };

        jButton1.addActionListener(actListener);
        jButton2.addActionListener(actListener);
        jButton3.addActionListener(actListener);
        jButton4.addActionListener(actListener);
        jButton5.addActionListener(actListener);
        jButton6.addActionListener(actListener);
        jButton7.addActionListener(actListener);
        jButton8.addActionListener(actListener);
        jButton9.addActionListener(actListener);
        jButton10.addActionListener(actListener);
        jButton11.addActionListener(actListener);
        jButton12.addActionListener(actListener);
        jButton13.addActionListener(actListener);
        jButton14.addActionListener(actListener);
        jButton15.addActionListener(actListener);
        jButton16.addActionListener(actListener);
        jButton17.addActionListener(actListener);
        jButton18.addActionListener(actListener);
        jButton19.addActionListener(actListener);
        jButton20.addActionListener(actListener);
        jButton21.addActionListener(actListener);
        jButton22.addActionListener(actListener);
        jButton23.addActionListener(actListener);
        jButton24.addActionListener(actListener);
        jButton25.addActionListener(actListener);
        jButton26.addActionListener(actListener);
        jButton27.addActionListener(actListener);
        jButton28.addActionListener(actListener);
        jButton29.addActionListener(actListener);
        jButton30.addActionListener(actListener);
        jButton31.addActionListener(actListener);
        jButton32.addActionListener(actListener);
        jButton33.addActionListener(actListener);
        jButton34.addActionListener(actListener);
        jButton35.addActionListener(actListener);
        jButton36.addActionListener(actListener);
        jButton37.addActionListener(actListener);
        jButton38.addActionListener(actListener);
        jButton39.addActionListener(actListener);
        jButton40.addActionListener(actListener);
        jButton41.addActionListener(actListener);
        jButton42.addActionListener(actListener);
        jButton43.addActionListener(actListener);

        guiPropertiesPane.setVisible(false);
        guiFilterOps.setVisible(false);
        guiFilterOps.setSize(1,1);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.BorderLayout());

        jSplitPane1.setDividerLocation(300);
        jSplitPane1.setDividerSize(2);

        guiTextPropertySplit.setDividerSize(2);
        guiTextPropertySplit.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        guiProperties.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        guiScroll.setViewportView(guiProperties);

        jXTitledSeparator6.setTitle(MessageBundle.getString("properties")); // NOI18N

        javax.swing.GroupLayout guiPropertiesPaneLayout = new javax.swing.GroupLayout(guiPropertiesPane);
        guiPropertiesPane.setLayout(guiPropertiesPaneLayout);
        guiPropertiesPaneLayout.setHorizontalGroup(
            guiPropertiesPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(guiPropertiesPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jXTitledSeparator6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(guiScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 451, Short.MAX_VALUE)
        );
        guiPropertiesPaneLayout.setVerticalGroup(
            guiPropertiesPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(guiPropertiesPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jXTitledSeparator6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(guiScroll))
        );

        guiTextPropertySplit.setLeftComponent(guiPropertiesPane);
        guiTextPropertySplit.setRightComponent(guiCQL);

        jSplitPane1.setRightComponent(guiTextPropertySplit);

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jXTitledSeparator1.setTitle(MessageBundle.getString("operand")); // NOI18N

        jPanel3.setLayout(new java.awt.GridLayout(1, 0));

        jButton1.setText(" + ");
        jButton1.setBorderPainted(false);
        jButton1.setName(" +"); // NOI18N
        jPanel3.add(jButton1);

        jButton2.setText(" - ");
        jButton2.setBorderPainted(false);
        jButton2.setName(" -"); // NOI18N
        jPanel3.add(jButton2);

        jButton6.setText(" / ");
        jButton6.setBorderPainted(false);
        jButton6.setName(" /"); // NOI18N
        jPanel3.add(jButton6);

        jButton7.setText(" * ");
        jButton7.setBorderPainted(false);
        jButton7.setName(" *"); // NOI18N
        jPanel3.add(jButton7);

        jButton8.setText(" ( ) ");
        jButton8.setBorderPainted(false);
        jButton8.setName("( )"); // NOI18N
        jPanel3.add(jButton8);

        jXTitledSeparator5.setTitle(MessageBundle.getString("function")); // NOI18N

        jXTitledSeparator2.setTitle(MessageBundle.getString("compare")); // NOI18N

        jPanel4.setLayout(new java.awt.GridLayout(0, 4));

        jButton3.setFont(jButton3.getFont().deriveFont(jButton3.getFont().getStyle() & ~java.awt.Font.BOLD, jButton3.getFont().getSize()-4));
        jButton3.setText(" = ");
        jButton3.setBorderPainted(false);
        jButton3.setName(" ="); // NOI18N
        jPanel4.add(jButton3);

        jButton9.setFont(jButton9.getFont().deriveFont(jButton9.getFont().getStyle() & ~java.awt.Font.BOLD, jButton9.getFont().getSize()-4));
        jButton9.setText(" <> ");
        jButton9.setBorderPainted(false);
        jButton9.setName(" <>"); // NOI18N
        jPanel4.add(jButton9);

        jButton10.setFont(jButton10.getFont().deriveFont(jButton10.getFont().getStyle() & ~java.awt.Font.BOLD, jButton10.getFont().getSize()-4));
        jButton10.setText(" > ");
        jButton10.setBorderPainted(false);
        jButton10.setName(" >"); // NOI18N
        jPanel4.add(jButton10);

        jButton11.setFont(jButton11.getFont().deriveFont(jButton11.getFont().getStyle() & ~java.awt.Font.BOLD, jButton11.getFont().getSize()-4));
        jButton11.setText(" >= ");
        jButton11.setBorderPainted(false);
        jButton11.setName(" >="); // NOI18N
        jPanel4.add(jButton11);

        jButton12.setFont(jButton12.getFont().deriveFont(jButton12.getFont().getStyle() & ~java.awt.Font.BOLD, jButton12.getFont().getSize()-4));
        jButton12.setText(" < ");
        jButton12.setBorderPainted(false);
        jButton12.setName(" <"); // NOI18N
        jPanel4.add(jButton12);

        jButton13.setFont(jButton13.getFont().deriveFont(jButton13.getFont().getStyle() & ~java.awt.Font.BOLD, jButton13.getFont().getSize()-4));
        jButton13.setText(" <= ");
        jButton13.setBorderPainted(false);
        jButton13.setName(" <="); // NOI18N
        jPanel4.add(jButton13);

        jButton14.setFont(jButton14.getFont().deriveFont(jButton14.getFont().getStyle() & ~java.awt.Font.BOLD, jButton14.getFont().getSize()-4));
        jButton14.setText("LIKE");
        jButton14.setBorderPainted(false);
        jButton14.setName(" LIKE"); // NOI18N
        jPanel4.add(jButton14);

        jButton15.setFont(jButton15.getFont().deriveFont(jButton15.getFont().getStyle() & ~java.awt.Font.BOLD, jButton15.getFont().getSize()-4));
        jButton15.setText(" IS ");
        jButton15.setBorderPainted(false);
        jButton15.setName(" IS"); // NOI18N
        jPanel4.add(jButton15);

        jButton16.setFont(jButton16.getFont().deriveFont(jButton16.getFont().getStyle() & ~java.awt.Font.BOLD, jButton16.getFont().getSize()-4));
        jButton16.setText(" IN ");
        jButton16.setBorderPainted(false);
        jButton16.setName(" IN"); // NOI18N
        jPanel4.add(jButton16);

        jButton18.setFont(jButton18.getFont().deriveFont(jButton18.getFont().getStyle() & ~java.awt.Font.BOLD, jButton18.getFont().getSize()-4));
        jButton18.setText("BETWEEN");
        jButton18.setBorderPainted(false);
        jButton18.setName(" BETWEEN"); // NOI18N
        jPanel4.add(jButton18);

        jButton19.setFont(jButton19.getFont().deriveFont(jButton19.getFont().getStyle() & ~java.awt.Font.BOLD, jButton19.getFont().getSize()-4));
        jButton19.setText("AND");
        jButton19.setBorderPainted(false);
        jButton19.setName(" AND"); // NOI18N
        jPanel4.add(jButton19);

        jButton20.setFont(jButton20.getFont().deriveFont(jButton20.getFont().getStyle() & ~java.awt.Font.BOLD, jButton20.getFont().getSize()-4));
        jButton20.setText("OR");
        jButton20.setBorderPainted(false);
        jButton20.setName(" OR"); // NOI18N
        jPanel4.add(jButton20);

        jButton17.setFont(jButton17.getFont().deriveFont(jButton17.getFont().getStyle() & ~java.awt.Font.BOLD, jButton17.getFont().getSize()-4));
        jButton17.setText("NOT");
        jButton17.setBorderPainted(false);
        jButton17.setName(" NOT"); // NOI18N
        jPanel4.add(jButton17);

        jXTitledSeparator3.setTitle(MessageBundle.getString("spatial")); // NOI18N

        jPanel5.setLayout(new java.awt.GridLayout(0, 3));

        jButton4.setFont(jButton4.getFont().deriveFont(jButton4.getFont().getStyle() & ~java.awt.Font.BOLD, jButton4.getFont().getSize()-4));
        jButton4.setText("BBOX");
        jButton4.setBorderPainted(false);
        jButton4.setName(" BBOX(att,minx,miny,maxx,maxy,'crs')"); // NOI18N
        jPanel5.add(jButton4);

        jButton21.setFont(jButton21.getFont().deriveFont(jButton21.getFont().getStyle() & ~java.awt.Font.BOLD, jButton21.getFont().getSize()-4));
        jButton21.setText("BEYOND");
        jButton21.setBorderPainted(false);
        jButton21.setName(" BEYOND(exp1,exp2)"); // NOI18N
        jPanel5.add(jButton21);

        jButton22.setFont(jButton22.getFont().deriveFont(jButton22.getFont().getStyle() & ~java.awt.Font.BOLD, jButton22.getFont().getSize()-4));
        jButton22.setText("CONTAINS");
        jButton22.setBorderPainted(false);
        jButton22.setName(" CONTAINs(exp1,exp2)"); // NOI18N
        jPanel5.add(jButton22);

        jButton23.setFont(jButton23.getFont().deriveFont(jButton23.getFont().getStyle() & ~java.awt.Font.BOLD, jButton23.getFont().getSize()-4));
        jButton23.setText("CROSS");
        jButton23.setBorderPainted(false);
        jButton23.setName(" CROSS(exp1,exp2)"); // NOI18N
        jPanel5.add(jButton23);

        jButton24.setFont(jButton24.getFont().deriveFont(jButton24.getFont().getStyle() & ~java.awt.Font.BOLD, jButton24.getFont().getSize()-4));
        jButton24.setText("DISJOINT");
        jButton24.setBorderPainted(false);
        jButton24.setName(" DISJOINT(exp1,exp2)"); // NOI18N
        jPanel5.add(jButton24);

        jButton25.setFont(jButton25.getFont().deriveFont(jButton25.getFont().getStyle() & ~java.awt.Font.BOLD, jButton25.getFont().getSize()-4));
        jButton25.setText("DWITHIN");
        jButton25.setBorderPainted(false);
        jButton25.setName(" DWITHIN(exp1,exp2)"); // NOI18N
        jPanel5.add(jButton25);

        jButton26.setFont(jButton26.getFont().deriveFont(jButton26.getFont().getStyle() & ~java.awt.Font.BOLD, jButton26.getFont().getSize()-4));
        jButton26.setText("EQUALS");
        jButton26.setBorderPainted(false);
        jButton26.setName(" EQUALS(exp1,exp2)"); // NOI18N
        jPanel5.add(jButton26);

        jButton27.setFont(jButton27.getFont().deriveFont(jButton27.getFont().getStyle() & ~java.awt.Font.BOLD, jButton27.getFont().getSize()-4));
        jButton27.setText("INTERSECT");
        jButton27.setBorderPainted(false);
        jButton27.setName(" INTERSECT(exp1,exp2)"); // NOI18N
        jPanel5.add(jButton27);

        jButton28.setFont(jButton28.getFont().deriveFont(jButton28.getFont().getStyle() & ~java.awt.Font.BOLD, jButton28.getFont().getSize()-4));
        jButton28.setText("OVERLAP");
        jButton28.setBorderPainted(false);
        jButton28.setName(" OVERLAP(exp1,exp2)"); // NOI18N
        jPanel5.add(jButton28);

        jButton30.setFont(jButton30.getFont().deriveFont(jButton30.getFont().getStyle() & ~java.awt.Font.BOLD, jButton30.getFont().getSize()-4));
        jButton30.setText("TOUCH");
        jButton30.setBorderPainted(false);
        jButton30.setName(" TOUCH(exp1,exp2)"); // NOI18N
        jPanel5.add(jButton30);

        jButton29.setFont(jButton29.getFont().deriveFont(jButton29.getFont().getStyle() & ~java.awt.Font.BOLD, jButton29.getFont().getSize()-4));
        jButton29.setText("WITHIN");
        jButton29.setBorderPainted(false);
        jButton29.setName(" WITHIN(exp1,exp2)"); // NOI18N
        jPanel5.add(jButton29);

        jXTitledSeparator4.setTitle(MessageBundle.getString("temporal")); // NOI18N

        jPanel6.setLayout(new java.awt.GridLayout(0, 3));

        jButton5.setFont(jButton5.getFont().deriveFont(jButton5.getFont().getStyle() & ~java.awt.Font.BOLD, jButton5.getFont().getSize()-4));
        jButton5.setText("AFTER");
        jButton5.setBorderPainted(false);
        jButton5.setName(" att AFTER exp"); // NOI18N
        jPanel6.add(jButton5);

        jButton31.setFont(jButton31.getFont().deriveFont(jButton31.getFont().getStyle() & ~java.awt.Font.BOLD, jButton31.getFont().getSize()-4));
        jButton31.setText("ANYINTERACTS");
        jButton31.setBorderPainted(false);
        jButton31.setName(" att ANYINTERACTS exp"); // NOI18N
        jPanel6.add(jButton31);

        jButton32.setFont(jButton32.getFont().deriveFont(jButton32.getFont().getStyle() & ~java.awt.Font.BOLD, jButton32.getFont().getSize()-4));
        jButton32.setText("BEFORE");
        jButton32.setBorderPainted(false);
        jButton32.setName(" att BEFORE exp"); // NOI18N
        jPanel6.add(jButton32);

        jButton33.setFont(jButton33.getFont().deriveFont(jButton33.getFont().getStyle() & ~java.awt.Font.BOLD, jButton33.getFont().getSize()-4));
        jButton33.setText("BEGINS");
        jButton33.setBorderPainted(false);
        jButton33.setName(" att BEGINS exp"); // NOI18N
        jPanel6.add(jButton33);

        jButton34.setFont(jButton34.getFont().deriveFont(jButton34.getFont().getStyle() & ~java.awt.Font.BOLD, jButton34.getFont().getSize()-4));
        jButton34.setText("BEGUNBY");
        jButton34.setBorderPainted(false);
        jButton34.setName(" att BEGUNBY exp"); // NOI18N
        jPanel6.add(jButton34);

        jButton43.setFont(jButton43.getFont().deriveFont(jButton43.getFont().getStyle() & ~java.awt.Font.BOLD, jButton43.getFont().getSize()-4));
        jButton43.setText("DURING");
        jButton43.setBorderPainted(false);
        jButton43.setName(" att DURING exp"); // NOI18N
        jPanel6.add(jButton43);

        jButton42.setFont(jButton42.getFont().deriveFont(jButton42.getFont().getStyle() & ~java.awt.Font.BOLD, jButton42.getFont().getSize()-4));
        jButton42.setText("ENDEDBY");
        jButton42.setBorderPainted(false);
        jButton42.setName(" att ENDEDBY exp"); // NOI18N
        jPanel6.add(jButton42);

        jButton41.setFont(jButton41.getFont().deriveFont(jButton41.getFont().getStyle() & ~java.awt.Font.BOLD, jButton41.getFont().getSize()-4));
        jButton41.setText("ENDS");
        jButton41.setBorderPainted(false);
        jButton41.setName(" att ENDS exp"); // NOI18N
        jPanel6.add(jButton41);

        jButton40.setFont(jButton40.getFont().deriveFont(jButton40.getFont().getStyle() & ~java.awt.Font.BOLD, jButton40.getFont().getSize()-4));
        jButton40.setText("MEETS");
        jButton40.setBorderPainted(false);
        jButton40.setName(" att MEETS exp"); // NOI18N
        jPanel6.add(jButton40);

        jButton39.setFont(jButton39.getFont().deriveFont(jButton39.getFont().getStyle() & ~java.awt.Font.BOLD, jButton39.getFont().getSize()-4));
        jButton39.setText("METBY");
        jButton39.setBorderPainted(false);
        jButton39.setName(" att METBY exp"); // NOI18N
        jPanel6.add(jButton39);

        jButton38.setFont(jButton38.getFont().deriveFont(jButton38.getFont().getStyle() & ~java.awt.Font.BOLD, jButton38.getFont().getSize()-4));
        jButton38.setText("OVERLAPPEDBY");
        jButton38.setBorderPainted(false);
        jButton38.setName(" att OVERLAPPEDBY exp"); // NOI18N
        jPanel6.add(jButton38);

        jButton35.setFont(jButton35.getFont().deriveFont(jButton35.getFont().getStyle() & ~java.awt.Font.BOLD, jButton35.getFont().getSize()-4));
        jButton35.setText("TCONTAINS");
        jButton35.setBorderPainted(false);
        jButton35.setName(" att TCONTAINS exp"); // NOI18N
        jPanel6.add(jButton35);

        jButton37.setFont(jButton37.getFont().deriveFont(jButton37.getFont().getStyle() & ~java.awt.Font.BOLD, jButton37.getFont().getSize()-4));
        jButton37.setText("TEQUALS");
        jButton37.setBorderPainted(false);
        jButton37.setName(" att TEQUALS exp"); // NOI18N
        jPanel6.add(jButton37);

        jButton36.setFont(jButton36.getFont().deriveFont(jButton36.getFont().getStyle() & ~java.awt.Font.BOLD, jButton36.getFont().getSize()-4));
        jButton36.setText("TOVERLAPS");
        jButton36.setBorderPainted(false);
        jButton36.setName(" att TOVERLAPS exp"); // NOI18N
        jPanel6.add(jButton36);

        javax.swing.GroupLayout guiFilterOpsLayout = new javax.swing.GroupLayout(guiFilterOps);
        guiFilterOps.setLayout(guiFilterOpsLayout);
        guiFilterOpsLayout.setHorizontalGroup(
            guiFilterOpsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(guiFilterOpsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(guiFilterOpsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jXTitledSeparator3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jXTitledSeparator2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jXTitledSeparator4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        guiFilterOpsLayout.setVerticalGroup(
            guiFilterOpsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(guiFilterOpsLayout.createSequentialGroup()
                .addComponent(jXTitledSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jXTitledSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jXTitledSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jScrollPane3.setViewportView(guiFunctions);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(guiFilterOps, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jXTitledSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jXTitledSeparator5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
            .addComponent(jScrollPane3)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jXTitledSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(guiFilterOps, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jXTitledSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE))
        );

        jScrollPane1.setViewportView(jPanel2);

        jSplitPane1.setLeftComponent(jScrollPane1);

        add(jSplitPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private final org.geotoolkit.cql.JCQLTextPane guiCQL = new org.geotoolkit.cql.JCQLTextPane();
    private final javax.swing.JPanel guiFilterOps = new javax.swing.JPanel();
    private final javax.swing.JTree guiFunctions = new javax.swing.JTree();
    private final javax.swing.JList guiProperties = new javax.swing.JList();
    private final javax.swing.JPanel guiPropertiesPane = new javax.swing.JPanel();
    private final javax.swing.JScrollPane guiScroll = new javax.swing.JScrollPane();
    private final javax.swing.JSplitPane guiTextPropertySplit = new javax.swing.JSplitPane();
    private final javax.swing.JButton jButton1 = new javax.swing.JButton();
    private final javax.swing.JButton jButton10 = new javax.swing.JButton();
    private final javax.swing.JButton jButton11 = new javax.swing.JButton();
    private final javax.swing.JButton jButton12 = new javax.swing.JButton();
    private final javax.swing.JButton jButton13 = new javax.swing.JButton();
    private final javax.swing.JButton jButton14 = new javax.swing.JButton();
    private final javax.swing.JButton jButton15 = new javax.swing.JButton();
    private final javax.swing.JButton jButton16 = new javax.swing.JButton();
    private final javax.swing.JButton jButton17 = new javax.swing.JButton();
    private final javax.swing.JButton jButton18 = new javax.swing.JButton();
    private final javax.swing.JButton jButton19 = new javax.swing.JButton();
    private final javax.swing.JButton jButton2 = new javax.swing.JButton();
    private final javax.swing.JButton jButton20 = new javax.swing.JButton();
    private final javax.swing.JButton jButton21 = new javax.swing.JButton();
    private final javax.swing.JButton jButton22 = new javax.swing.JButton();
    private final javax.swing.JButton jButton23 = new javax.swing.JButton();
    private final javax.swing.JButton jButton24 = new javax.swing.JButton();
    private final javax.swing.JButton jButton25 = new javax.swing.JButton();
    private final javax.swing.JButton jButton26 = new javax.swing.JButton();
    private final javax.swing.JButton jButton27 = new javax.swing.JButton();
    private final javax.swing.JButton jButton28 = new javax.swing.JButton();
    private final javax.swing.JButton jButton29 = new javax.swing.JButton();
    private final javax.swing.JButton jButton3 = new javax.swing.JButton();
    private final javax.swing.JButton jButton30 = new javax.swing.JButton();
    private final javax.swing.JButton jButton31 = new javax.swing.JButton();
    private final javax.swing.JButton jButton32 = new javax.swing.JButton();
    private final javax.swing.JButton jButton33 = new javax.swing.JButton();
    private final javax.swing.JButton jButton34 = new javax.swing.JButton();
    private final javax.swing.JButton jButton35 = new javax.swing.JButton();
    private final javax.swing.JButton jButton36 = new javax.swing.JButton();
    private final javax.swing.JButton jButton37 = new javax.swing.JButton();
    private final javax.swing.JButton jButton38 = new javax.swing.JButton();
    private final javax.swing.JButton jButton39 = new javax.swing.JButton();
    private final javax.swing.JButton jButton4 = new javax.swing.JButton();
    private final javax.swing.JButton jButton40 = new javax.swing.JButton();
    private final javax.swing.JButton jButton41 = new javax.swing.JButton();
    private final javax.swing.JButton jButton42 = new javax.swing.JButton();
    private final javax.swing.JButton jButton43 = new javax.swing.JButton();
    private final javax.swing.JButton jButton5 = new javax.swing.JButton();
    private final javax.swing.JButton jButton6 = new javax.swing.JButton();
    private final javax.swing.JButton jButton7 = new javax.swing.JButton();
    private final javax.swing.JButton jButton8 = new javax.swing.JButton();
    private final javax.swing.JButton jButton9 = new javax.swing.JButton();
    private final javax.swing.JPanel jPanel2 = new javax.swing.JPanel();
    private final javax.swing.JPanel jPanel3 = new javax.swing.JPanel();
    private final javax.swing.JPanel jPanel4 = new javax.swing.JPanel();
    private final javax.swing.JPanel jPanel5 = new javax.swing.JPanel();
    private final javax.swing.JPanel jPanel6 = new javax.swing.JPanel();
    private final javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
    private final javax.swing.JScrollPane jScrollPane3 = new javax.swing.JScrollPane();
    private final javax.swing.JSplitPane jSplitPane1 = new javax.swing.JSplitPane();
    private final org.jdesktop.swingx.JXTitledSeparator jXTitledSeparator1 = new org.jdesktop.swingx.JXTitledSeparator();
    private final org.jdesktop.swingx.JXTitledSeparator jXTitledSeparator2 = new org.jdesktop.swingx.JXTitledSeparator();
    private final org.jdesktop.swingx.JXTitledSeparator jXTitledSeparator3 = new org.jdesktop.swingx.JXTitledSeparator();
    private final org.jdesktop.swingx.JXTitledSeparator jXTitledSeparator4 = new org.jdesktop.swingx.JXTitledSeparator();
    private final org.jdesktop.swingx.JXTitledSeparator jXTitledSeparator5 = new org.jdesktop.swingx.JXTitledSeparator();
    private final org.jdesktop.swingx.JXTitledSeparator jXTitledSeparator6 = new org.jdesktop.swingx.JXTitledSeparator();
    // End of variables declaration//GEN-END:variables

    public void setExpression(final Expression exp){
        guiCQL.setExpression(exp);
        guiFilterOps.setVisible(false);
        guiFilterOps.setSize(1,1);
    }

    public Expression getExpression() throws CQLException{
        return guiCQL.getExpression();
    }

    public void setFilter(final Filter filter) {
        guiCQL.setFilter(filter);
        guiFilterOps.setVisible(true);
        guiFilterOps.setSize(guiFilterOps.getPreferredSize());
    }

    public Filter getFilter() throws CQLException {
        return guiCQL.getFilter();
    }

    public void setLayer(final MapLayer layer) {
        this.layer = layer;

        if (layer instanceof FeatureMapLayer) {
            final FeatureMapLayer fml = (FeatureMapLayer) layer;

            final Collection<PropertyDescriptor> col = fml.getCollection().getFeatureType().getDescriptors();
            guiProperties.setModel(new ListComboBoxModel(new ArrayList(col)));
            guiPropertiesPane.setVisible(true);
            guiTextPropertySplit.setDividerLocation(120);
        }else{
            guiPropertiesPane.setVisible(false);
            guiTextPropertySplit.setDividerLocation(0);
        }
        guiTextPropertySplit.revalidate();
        guiTextPropertySplit.repaint();

    }

    public MapLayer getLayer() {
        return layer;
    }


    public String getTitle() {
        return MessageBundle.getString("property_cql_filter");
    }

    public ImageIcon getIcon() {
        return ICON_FILTER;
    }

    public String getToolTip() {
        return null;
    }

    public JComponent getComponent() {
        return this;
    }

    public static Filter showDialog(Component parent, final MapLayer layer, final Filter filter) throws CQLException{

        final JCQLEditor editor = new JCQLEditor();
        editor.setLayer(layer);
        editor.setFilter(filter);

        final int res = JOptionDialog.show(parent, editor, JOptionPane.OK_CANCEL_OPTION);

        if(res == JOptionPane.OK_OPTION){
            return editor.getFilter();
        }else{
            return filter;
        }
    }

    public static Expression showDialog(Component parent, final MapLayer layer, final Expression expression) throws CQLException{

        final JCQLEditor editor = new JCQLEditor();
        editor.setLayer(layer);
        editor.setExpression(expression);

        final int res = JOptionDialog.show(parent, editor, JOptionPane.OK_CANCEL_OPTION);

        if(res == JOptionPane.OK_OPTION){
            return editor.getExpression();
        }else{
            return expression;
        }
    }

    private static final class PropertyRenderer extends DefaultListCellRenderer{

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            final JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if(value instanceof PropertyDescriptor){
                final PropertyDescriptor desc = (PropertyDescriptor) value;
                String text = desc.getName().getLocalPart().toString();

                final InternationalString is = desc.getType().getDescription();
                if(is!=null && !is.toString().isEmpty()){
                    text += "  ("+is.toString()+")";
                }

                lbl.setText(text);
            }

            return lbl;
        }

    }

}
