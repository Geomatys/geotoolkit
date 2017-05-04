/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
import java.util.Arrays;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.geotoolkit.filter.function.FunctionFactory;
import org.geotoolkit.filter.function.Functions;
import static org.geotoolkit.gui.swing.filter.JCQLEditor.DEFAULT_SIMPLE;
import org.geotoolkit.font.FontAwesomeIcons;
import org.geotoolkit.font.IconBuilder;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class JCQLShortcutPanel extends javax.swing.JPanel {

    /**
     * A property event of this type is fired when a shortcut (button or tree) has been clicked.
     * Value is a String.
     */
    public static final String KEY_SELECTION = "selectionText";

    public static volatile boolean DEFAULT_SIMPLE = true;

    private static final ImageIcon ICON_FUNCTION = IconBuilder.createIcon(FontAwesomeIcons.ICON_COG, 16, Color.GRAY);
    private static final ImageIcon ICON_GROUP = IconBuilder.createIcon(FontAwesomeIcons.ICON_FOLDER, 16, Color.GRAY);

    private boolean filter = true;

    /**
     * Creates new form JCQLShortcutPanel
     */
    public JCQLShortcutPanel() {
        initComponents();

        guiFunctions.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("-")));
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
                        guiFunctions.clearSelection();
                        fireShortCutText(" "+sb.toString());
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


        final ActionListener actListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final JButton but = (JButton) e.getSource();
                fireShortCutText(but.getName());
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

        guiSimple.setSelected(DEFAULT_SIMPLE);
        updateSimpleAdvanced();
    }

    public boolean isFilterOpVisible() {
        return filter;
    }

    public void setFilterOpVisible(boolean filter) {
        this.filter = filter;
        updateSimpleAdvanced();
    }

    private void updateSimpleAdvanced(){
        final DefaultMutableTreeNode root = new org.geotoolkit.gui.swing.tree.DefaultMutableTreeNode("root");

        final boolean simple = guiSimple.isSelected();
        for(FunctionFactory ff : Functions.getFactories()){
            final String factoryName = ff.getIdentifier();
            if(simple && !"math".equals(factoryName)) continue;
            final DefaultMutableTreeNode fnode = new org.geotoolkit.gui.swing.tree.DefaultMutableTreeNode(factoryName);
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

        if(guiSimple.isSelected()){
            guiFilterOps.setVisible(false);
            guiFilterOps.setSize(1,1);
        }else if(filter){
            guiFilterOps.setVisible(true);
            guiFilterOps.setSize(guiFilterOps.getPreferredSize());
        }

    }

    private void fireShortCutText(String text){
        firePropertyChange(KEY_SELECTION, null, text);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        typeGroup = new javax.swing.ButtonGroup();
        jXTitledSeparator5 = new org.jdesktop.swingx.JXTitledSeparator();
        guiFilterOps = new javax.swing.JPanel();
        jXTitledSeparator2 = new org.jdesktop.swingx.JXTitledSeparator();
        jPanel4 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        jButton16 = new javax.swing.JButton();
        jButton18 = new javax.swing.JButton();
        jButton19 = new javax.swing.JButton();
        jButton20 = new javax.swing.JButton();
        jButton17 = new javax.swing.JButton();
        jXTitledSeparator3 = new org.jdesktop.swingx.JXTitledSeparator();
        jPanel5 = new javax.swing.JPanel();
        jButton4 = new javax.swing.JButton();
        jButton21 = new javax.swing.JButton();
        jButton22 = new javax.swing.JButton();
        jButton23 = new javax.swing.JButton();
        jButton24 = new javax.swing.JButton();
        jButton25 = new javax.swing.JButton();
        jButton26 = new javax.swing.JButton();
        jButton27 = new javax.swing.JButton();
        jButton28 = new javax.swing.JButton();
        jButton30 = new javax.swing.JButton();
        jButton29 = new javax.swing.JButton();
        jXTitledSeparator4 = new org.jdesktop.swingx.JXTitledSeparator();
        jPanel6 = new javax.swing.JPanel();
        jButton5 = new javax.swing.JButton();
        jButton31 = new javax.swing.JButton();
        jButton32 = new javax.swing.JButton();
        jButton33 = new javax.swing.JButton();
        jButton34 = new javax.swing.JButton();
        jButton43 = new javax.swing.JButton();
        jButton42 = new javax.swing.JButton();
        jButton41 = new javax.swing.JButton();
        jButton40 = new javax.swing.JButton();
        jButton39 = new javax.swing.JButton();
        jButton38 = new javax.swing.JButton();
        jButton35 = new javax.swing.JButton();
        jButton37 = new javax.swing.JButton();
        jButton36 = new javax.swing.JButton();
        jXTitledSeparator1 = new org.jdesktop.swingx.JXTitledSeparator();
        jPanel3 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        guiFunctions = new javax.swing.JTree();
        jPanel1 = new javax.swing.JPanel();
        guiSimple = new javax.swing.JToggleButton();
        guiAdvanced = new javax.swing.JToggleButton();

        setLayout(new java.awt.GridBagLayout());

        jXTitledSeparator5.setTitle(MessageBundle.format("function")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 0);
        add(jXTitledSeparator5, gridBagConstraints);

        jXTitledSeparator2.setTitle(MessageBundle.format("compare")); // NOI18N

        jPanel4.setLayout(new java.awt.GridLayout(0, 4));

        jButton3.setFont(jButton3.getFont().deriveFont(jButton3.getFont().getSize()-3f));
        jButton3.setText(" = ");
        jButton3.setBorderPainted(false);
        jButton3.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton3.setName(" ="); // NOI18N
        jPanel4.add(jButton3);

        jButton9.setFont(jButton9.getFont().deriveFont(jButton9.getFont().getSize()-3f));
        jButton9.setText(" <> ");
        jButton9.setBorderPainted(false);
        jButton9.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton9.setName(" <>"); // NOI18N
        jPanel4.add(jButton9);

        jButton10.setFont(jButton10.getFont().deriveFont(jButton10.getFont().getSize()-3f));
        jButton10.setText(" > ");
        jButton10.setBorderPainted(false);
        jButton10.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton10.setName(" >"); // NOI18N
        jPanel4.add(jButton10);

        jButton11.setFont(jButton11.getFont().deriveFont(jButton11.getFont().getSize()-3f));
        jButton11.setText(" >= ");
        jButton11.setBorderPainted(false);
        jButton11.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton11.setName(" >="); // NOI18N
        jPanel4.add(jButton11);

        jButton12.setFont(jButton12.getFont().deriveFont(jButton12.getFont().getSize()-3f));
        jButton12.setText(" < ");
        jButton12.setBorderPainted(false);
        jButton12.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton12.setName(" <"); // NOI18N
        jPanel4.add(jButton12);

        jButton13.setFont(jButton13.getFont().deriveFont(jButton13.getFont().getSize()-3f));
        jButton13.setText(" <= ");
        jButton13.setBorderPainted(false);
        jButton13.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton13.setName(" <="); // NOI18N
        jPanel4.add(jButton13);

        jButton14.setFont(jButton14.getFont().deriveFont(jButton14.getFont().getSize()-3f));
        jButton14.setText("LIKE");
        jButton14.setBorderPainted(false);
        jButton14.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton14.setName(" LIKE"); // NOI18N
        jPanel4.add(jButton14);

        jButton15.setFont(jButton15.getFont().deriveFont(jButton15.getFont().getSize()-3f));
        jButton15.setText(" IS ");
        jButton15.setBorderPainted(false);
        jButton15.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton15.setName(" IS"); // NOI18N
        jPanel4.add(jButton15);

        jButton16.setFont(jButton16.getFont().deriveFont(jButton16.getFont().getSize()-3f));
        jButton16.setText(" IN ");
        jButton16.setBorderPainted(false);
        jButton16.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton16.setName(" IN"); // NOI18N
        jPanel4.add(jButton16);

        jButton18.setFont(jButton18.getFont().deriveFont(jButton18.getFont().getSize()-3f));
        jButton18.setText("BETWEEN");
        jButton18.setBorderPainted(false);
        jButton18.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton18.setName(" BETWEEN"); // NOI18N
        jPanel4.add(jButton18);

        jButton19.setFont(jButton19.getFont().deriveFont(jButton19.getFont().getSize()-3f));
        jButton19.setText("AND");
        jButton19.setBorderPainted(false);
        jButton19.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton19.setName(" AND"); // NOI18N
        jPanel4.add(jButton19);

        jButton20.setFont(jButton20.getFont().deriveFont(jButton20.getFont().getSize()-3f));
        jButton20.setText("OR");
        jButton20.setBorderPainted(false);
        jButton20.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton20.setName(" OR"); // NOI18N
        jPanel4.add(jButton20);

        jButton17.setFont(jButton17.getFont().deriveFont(jButton17.getFont().getSize()-3f));
        jButton17.setText("NOT");
        jButton17.setBorderPainted(false);
        jButton17.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton17.setName(" NOT"); // NOI18N
        jPanel4.add(jButton17);

        jXTitledSeparator3.setTitle(MessageBundle.format("spatial")); // NOI18N

        jPanel5.setLayout(new java.awt.GridLayout(0, 3));

        jButton4.setFont(jButton4.getFont().deriveFont(jButton4.getFont().getSize()-3f));
        jButton4.setText("BBOX");
        jButton4.setBorderPainted(false);
        jButton4.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton4.setName(" BBOX(att,minx,miny,maxx,maxy,'crs')"); // NOI18N
        jPanel5.add(jButton4);

        jButton21.setFont(jButton21.getFont().deriveFont(jButton21.getFont().getSize()-3f));
        jButton21.setText("BEYOND");
        jButton21.setBorderPainted(false);
        jButton21.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton21.setName(" BEYOND(exp1,exp2)"); // NOI18N
        jPanel5.add(jButton21);

        jButton22.setFont(jButton22.getFont().deriveFont(jButton22.getFont().getSize()-3f));
        jButton22.setText("CONTAINS");
        jButton22.setBorderPainted(false);
        jButton22.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton22.setName(" CONTAINs(exp1,exp2)"); // NOI18N
        jPanel5.add(jButton22);

        jButton23.setFont(jButton23.getFont().deriveFont(jButton23.getFont().getSize()-3f));
        jButton23.setText("CROSS");
        jButton23.setBorderPainted(false);
        jButton23.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton23.setName(" CROSS(exp1,exp2)"); // NOI18N
        jPanel5.add(jButton23);

        jButton24.setFont(jButton24.getFont().deriveFont(jButton24.getFont().getSize()-3f));
        jButton24.setText("DISJOINT");
        jButton24.setBorderPainted(false);
        jButton24.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton24.setName(" DISJOINT(exp1,exp2)"); // NOI18N
        jPanel5.add(jButton24);

        jButton25.setFont(jButton25.getFont().deriveFont(jButton25.getFont().getSize()-3f));
        jButton25.setText("DWITHIN");
        jButton25.setBorderPainted(false);
        jButton25.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton25.setName(" DWITHIN(exp1,exp2)"); // NOI18N
        jPanel5.add(jButton25);

        jButton26.setFont(jButton26.getFont().deriveFont(jButton26.getFont().getSize()-3f));
        jButton26.setText("EQUALS");
        jButton26.setBorderPainted(false);
        jButton26.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton26.setName(" EQUALS(exp1,exp2)"); // NOI18N
        jPanel5.add(jButton26);

        jButton27.setFont(jButton27.getFont().deriveFont(jButton27.getFont().getSize()-3f));
        jButton27.setText("INTERSECT");
        jButton27.setBorderPainted(false);
        jButton27.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton27.setName(" INTERSECT(exp1,exp2)"); // NOI18N
        jPanel5.add(jButton27);

        jButton28.setFont(jButton28.getFont().deriveFont(jButton28.getFont().getSize()-3f));
        jButton28.setText("OVERLAP");
        jButton28.setBorderPainted(false);
        jButton28.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton28.setName(" OVERLAP(exp1,exp2)"); // NOI18N
        jPanel5.add(jButton28);

        jButton30.setFont(jButton30.getFont().deriveFont(jButton30.getFont().getSize()-3f));
        jButton30.setText("TOUCH");
        jButton30.setBorderPainted(false);
        jButton30.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton30.setName(" TOUCH(exp1,exp2)"); // NOI18N
        jPanel5.add(jButton30);

        jButton29.setFont(jButton29.getFont().deriveFont(jButton29.getFont().getSize()-3f));
        jButton29.setText("WITHIN");
        jButton29.setBorderPainted(false);
        jButton29.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton29.setName(" WITHIN(exp1,exp2)"); // NOI18N
        jPanel5.add(jButton29);

        jXTitledSeparator4.setTitle(MessageBundle.format("temporal")); // NOI18N

        jPanel6.setLayout(new java.awt.GridLayout(0, 3));

        jButton5.setFont(jButton5.getFont().deriveFont(jButton5.getFont().getSize()-3f));
        jButton5.setText("AFTER");
        jButton5.setBorderPainted(false);
        jButton5.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton5.setName(" att AFTER exp"); // NOI18N
        jPanel6.add(jButton5);

        jButton31.setFont(jButton31.getFont().deriveFont(jButton31.getFont().getSize()-3f));
        jButton31.setText("ANYINTERACTS");
        jButton31.setBorderPainted(false);
        jButton31.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton31.setName(" att ANYINTERACTS exp"); // NOI18N
        jPanel6.add(jButton31);

        jButton32.setFont(jButton32.getFont().deriveFont(jButton32.getFont().getSize()-3f));
        jButton32.setText("BEFORE");
        jButton32.setBorderPainted(false);
        jButton32.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton32.setName(" att BEFORE exp"); // NOI18N
        jPanel6.add(jButton32);

        jButton33.setFont(jButton33.getFont().deriveFont(jButton33.getFont().getSize()-3f));
        jButton33.setText("BEGINS");
        jButton33.setBorderPainted(false);
        jButton33.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton33.setName(" att BEGINS exp"); // NOI18N
        jPanel6.add(jButton33);

        jButton34.setFont(jButton34.getFont().deriveFont(jButton34.getFont().getSize()-3f));
        jButton34.setText("BEGUNBY");
        jButton34.setBorderPainted(false);
        jButton34.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton34.setName(" att BEGUNBY exp"); // NOI18N
        jPanel6.add(jButton34);

        jButton43.setFont(jButton43.getFont().deriveFont(jButton43.getFont().getSize()-3f));
        jButton43.setText("DURING");
        jButton43.setBorderPainted(false);
        jButton43.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton43.setName(" att DURING exp"); // NOI18N
        jPanel6.add(jButton43);

        jButton42.setFont(jButton42.getFont().deriveFont(jButton42.getFont().getSize()-3f));
        jButton42.setText("ENDEDBY");
        jButton42.setBorderPainted(false);
        jButton42.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton42.setName(" att ENDEDBY exp"); // NOI18N
        jPanel6.add(jButton42);

        jButton41.setFont(jButton41.getFont().deriveFont(jButton41.getFont().getSize()-3f));
        jButton41.setText("ENDS");
        jButton41.setBorderPainted(false);
        jButton41.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton41.setName(" att ENDS exp"); // NOI18N
        jPanel6.add(jButton41);

        jButton40.setFont(jButton40.getFont().deriveFont(jButton40.getFont().getSize()-3f));
        jButton40.setText("MEETS");
        jButton40.setBorderPainted(false);
        jButton40.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton40.setName(" att MEETS exp"); // NOI18N
        jPanel6.add(jButton40);

        jButton39.setFont(jButton39.getFont().deriveFont(jButton39.getFont().getSize()-3f));
        jButton39.setText("METBY");
        jButton39.setBorderPainted(false);
        jButton39.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton39.setName(" att METBY exp"); // NOI18N
        jPanel6.add(jButton39);

        jButton38.setFont(jButton38.getFont().deriveFont(jButton38.getFont().getSize()-3f));
        jButton38.setText("OVERLAPPEDBY");
        jButton38.setBorderPainted(false);
        jButton38.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton38.setName(" att OVERLAPPEDBY exp"); // NOI18N
        jPanel6.add(jButton38);

        jButton35.setFont(jButton35.getFont().deriveFont(jButton35.getFont().getSize()-3f));
        jButton35.setText("TCONTAINS");
        jButton35.setBorderPainted(false);
        jButton35.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton35.setName(" att TCONTAINS exp"); // NOI18N
        jPanel6.add(jButton35);

        jButton37.setFont(jButton37.getFont().deriveFont(jButton37.getFont().getSize()-3f));
        jButton37.setText("TEQUALS");
        jButton37.setBorderPainted(false);
        jButton37.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton37.setName(" att TEQUALS exp"); // NOI18N
        jPanel6.add(jButton37);

        jButton36.setFont(jButton36.getFont().deriveFont(jButton36.getFont().getSize()-3f));
        jButton36.setText("TOVERLAPS");
        jButton36.setBorderPainted(false);
        jButton36.setMargin(new java.awt.Insets(0, 0, 0, 0));
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
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
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

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(guiFilterOps, gridBagConstraints);

        jXTitledSeparator1.setTitle(MessageBundle.format("operand")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        add(jXTitledSeparator1, gridBagConstraints);

        jPanel3.setLayout(new java.awt.GridLayout());

        jButton1.setText(" + ");
        jButton1.setBorderPainted(false);
        jButton1.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton1.setName(" +"); // NOI18N
        jPanel3.add(jButton1);

        jButton2.setText(" - ");
        jButton2.setBorderPainted(false);
        jButton2.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton2.setName(" -"); // NOI18N
        jPanel3.add(jButton2);

        jButton6.setText(" / ");
        jButton6.setBorderPainted(false);
        jButton6.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton6.setName(" /"); // NOI18N
        jPanel3.add(jButton6);

        jButton7.setText(" * ");
        jButton7.setBorderPainted(false);
        jButton7.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton7.setName(" *"); // NOI18N
        jPanel3.add(jButton7);

        jButton8.setText(" ( ) ");
        jButton8.setBorderPainted(false);
        jButton8.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton8.setName("( )"); // NOI18N
        jPanel3.add(jButton8);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(jPanel3, gridBagConstraints);

        jScrollPane3.setViewportView(guiFunctions);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jScrollPane3, gridBagConstraints);

        jPanel1.setLayout(new java.awt.GridLayout(1, 2));

        typeGroup.add(guiSimple);
        guiSimple.setText(MessageBundle.format("cql_simple")); // NOI18N
        guiSimple.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guiSimpleguiToggleAction(evt);
            }
        });
        jPanel1.add(guiSimple);

        typeGroup.add(guiAdvanced);
        guiAdvanced.setText(MessageBundle.format("cql_advanced")); // NOI18N
        guiAdvanced.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guiAdvancedguiToggleAction(evt);
            }
        });
        jPanel1.add(guiAdvanced);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(jPanel1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void guiSimpleguiToggleAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guiSimpleguiToggleAction
        DEFAULT_SIMPLE = guiSimple.isSelected();
        updateSimpleAdvanced();
    }//GEN-LAST:event_guiSimpleguiToggleAction

    private void guiAdvancedguiToggleAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guiAdvancedguiToggleAction
        DEFAULT_SIMPLE = guiSimple.isSelected();
        updateSimpleAdvanced();
    }//GEN-LAST:event_guiAdvancedguiToggleAction


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton guiAdvanced;
    private javax.swing.JPanel guiFilterOps;
    private javax.swing.JTree guiFunctions;
    private javax.swing.JToggleButton guiSimple;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton22;
    private javax.swing.JButton jButton23;
    private javax.swing.JButton jButton24;
    private javax.swing.JButton jButton25;
    private javax.swing.JButton jButton26;
    private javax.swing.JButton jButton27;
    private javax.swing.JButton jButton28;
    private javax.swing.JButton jButton29;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton30;
    private javax.swing.JButton jButton31;
    private javax.swing.JButton jButton32;
    private javax.swing.JButton jButton33;
    private javax.swing.JButton jButton34;
    private javax.swing.JButton jButton35;
    private javax.swing.JButton jButton36;
    private javax.swing.JButton jButton37;
    private javax.swing.JButton jButton38;
    private javax.swing.JButton jButton39;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton40;
    private javax.swing.JButton jButton41;
    private javax.swing.JButton jButton42;
    private javax.swing.JButton jButton43;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane3;
    private org.jdesktop.swingx.JXTitledSeparator jXTitledSeparator1;
    private org.jdesktop.swingx.JXTitledSeparator jXTitledSeparator2;
    private org.jdesktop.swingx.JXTitledSeparator jXTitledSeparator3;
    private org.jdesktop.swingx.JXTitledSeparator jXTitledSeparator4;
    private org.jdesktop.swingx.JXTitledSeparator jXTitledSeparator5;
    private javax.swing.ButtonGroup typeGroup;
    // End of variables declaration//GEN-END:variables
}
