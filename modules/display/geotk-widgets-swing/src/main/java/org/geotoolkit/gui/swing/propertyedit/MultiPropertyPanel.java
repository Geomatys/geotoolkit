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
package org.geotoolkit.gui.swing.propertyedit;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import org.geotoolkit.gui.swing.misc.JImagePane;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.JXTree;

/**
 * Multiproperty panel
 *
 * @author  Johann Sorel
 * @module pending
 */
public abstract class MultiPropertyPanel extends javax.swing.JPanel implements PropertyPane {

    private final List<PropertyPane> panels = new ArrayList<PropertyPane>();
    private final DefaultTreeModel model = new DefaultTreeModel(new DefaultMutableTreeNode("Styles"));
    private PropertyPane active = null;
    

    /** Creates new form MultiPropertyPanel */
    public MultiPropertyPanel() {
        super();
        initComponents();

        tree.setModel(model);
        tree.setRootVisible(false);
        tree.setCellRenderer(new MultiTreeRenderer());
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        tree.addTreeSelectionListener(new TreeSelectionListener() {

            @Override
                    public void valueChanged(TreeSelectionEvent e) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
                        final Object obj = node.getUserObject();

                        if (obj != null) {
                            if (obj instanceof PropertyPane) {
                                setSelectedPropertyPanel((PropertyPane) obj);
                            }
                        }
                    }
                });
        
        guiTabIndex.add(BorderLayout.SOUTH, guiImage);        

    }

    public void addPropertyPanel(final PropertyPane panel) {
        if (panel != null) {

            if (!panels.contains(panel)) {
                DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(panel);
                root.add(node);

                model.reload();
                tree.expandAll();

                panels.add(panel);

                if (panels.size() == 1) {
                    setSelectedPropertyPanel(panel);
                }
            }


        }
    }

    public boolean setSelectedPropertyPanel(final PropertyPane panel) {

        guiImage.setBorder(null);
        guiImage.setImage(null);
        guiImage.setPreferredSize(new Dimension(1, 1));
        guiImage.revalidate();
        guiView.revalidate();
        guiView.repaint();
        
        
        if (panel != null) {
            if (panels.contains(panel)) {
                active = panel;
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                            public void run() {
                                panprop.removeAll();
                                panprop.add(panel.getComponent());
                                panprop.revalidate();
                                panprop.repaint();
                            }
                        });

                final Image img = panel.getPreview();
                guiImage.setImage(img);
                if(img != null){
                    guiImage.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
                    guiImage.setPreferredSize(new Dimension(100, 140));
                    guiImage.revalidate();
                    guiView.revalidate();
                    guiView.repaint();
                }
                
                return true;
            }
            
        }else{
            active = null;
            panprop.removeAll();
            panprop.revalidate();
            panprop.repaint();
        }
        
        return false;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new JSplitPane();
        guiView = new JPanel();
        guiTabIndex = new JXTitledPanel();
        jScrollPane1 = new JScrollPane();
        tree = new JXTree();
        guiImage = new JImagePane();
        panprop = new JPanel();

        jSplitPane1.setBorder(null);
        jSplitPane1.setDividerSize(4);

        guiView.setLayout(new BorderLayout());

        guiTabIndex.setBorder(BorderFactory.createEtchedBorder());
        guiTabIndex.setTitle(MessageBundle.getString("property_editor")); // NOI18N
        guiTabIndex.setTitleFont(guiTabIndex.getTitleFont().deriveFont(guiTabIndex.getTitleFont().getStyle() | Font.BOLD));

        jScrollPane1.setBorder(null);
        jScrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setMinimumSize(new Dimension(152, 202));
        jScrollPane1.setPreferredSize(new Dimension(152, 202));

        tree.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        tree.setMinimumSize(new Dimension(150, 200));
        tree.setPreferredSize(new Dimension(150, 200));
        jScrollPane1.setViewportView(tree);

        guiTabIndex.add(jScrollPane1, BorderLayout.CENTER);

        guiView.add(guiTabIndex, BorderLayout.CENTER);

        guiImage.setMinimumSize(new Dimension(1, 1));
        guiImage.setPreferredSize(new Dimension(1, 1));

        GroupLayout guiImageLayout = new GroupLayout(guiImage);
        guiImage.setLayout(guiImageLayout);
        guiImageLayout.setHorizontalGroup(
            guiImageLayout.createParallelGroup(Alignment.LEADING)
            .addGap(0, 227, Short.MAX_VALUE)
        );
        guiImageLayout.setVerticalGroup(
            guiImageLayout.createParallelGroup(Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        guiView.add(guiImage, BorderLayout.SOUTH);

        jSplitPane1.setLeftComponent(guiView);

        panprop.setLayout(new GridLayout(1, 1));
        jSplitPane1.setRightComponent(panprop);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addComponent(jSplitPane1, GroupLayout.DEFAULT_SIZE, 475, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addComponent(jSplitPane1, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    @Override
    public void setTarget(final Object target) {
        //select only panels which handle this target
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
        root.removeAllChildren();
        
        for (PropertyPane pan : panels) {            
            if(pan.canHandle(target)){
                pan.setTarget(target);
                root.add(new DefaultMutableTreeNode(pan));
            }else{
                pan.setTarget(null);
            }
        }
                
        if(root.getChildCount() > 0){
            PropertyPane pan = (PropertyPane) ((DefaultMutableTreeNode)root.getChildAt(0)).getUserObject();
            setSelectedPropertyPanel(pan);
        }
        
        
        model.reload();
        tree.expandAll();
    }

    @Override
    public void apply() {
        for (PropertyPane pan : panels) {

            if (pan.equals(active)) {
                pan.apply();
            } else {
                pan.reset();
            }
        }
    }

    @Override
    public void reset() {
        for (PropertyPane pan : panels) {
            pan.reset();
        }
    }

    @Override
    public abstract String getTitle();

    @Override
    public abstract ImageIcon getIcon();

    @Override
    public abstract String getToolTip();

    @Override
    public Component getComponent() {
        return this;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JImagePane guiImage;
    private JXTitledPanel guiTabIndex;
    private JPanel guiView;
    private JScrollPane jScrollPane1;
    private JSplitPane jSplitPane1;
    private JPanel panprop;
    private JXTree tree;
    // End of variables declaration//GEN-END:variables

    private class MultiTreeRenderer extends DefaultTreeCellRenderer {

        /**
         * Creates a new instance of MultiTreeRenderer
         */
        public MultiTreeRenderer() {
            super();
        }

        @Override
        public Component getTreeCellRendererComponent(final JTree tree, final Object value, final boolean sel, final boolean expanded, final boolean leaf, final int row, final boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

            if (value instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                Object obj = node.getUserObject();
                if (obj instanceof PropertyPane) {
                    PropertyPane pane = (PropertyPane) obj;
                    setIcon(pane.getIcon());
                    setText(pane.getTitle());
                }

            }

            return this;
        }
    }


}
