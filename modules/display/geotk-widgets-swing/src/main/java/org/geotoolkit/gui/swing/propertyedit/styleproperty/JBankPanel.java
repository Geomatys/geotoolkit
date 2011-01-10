/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.gui.swing.propertyedit.styleproperty;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.util.concurrent.Callable;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.geotoolkit.display2d.service.DefaultGlyphService;

import org.geotoolkit.gui.swing.propertyedit.PropertyPane;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.bank.ElementNode;
import org.geotoolkit.style.bank.ElementType;
import org.geotoolkit.style.bank.StyleBanks;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class JBankPanel extends javax.swing.JPanel implements PropertyPane{

    private MapLayer layer = null;

//    private TreeModel styleModel = new DefaultTreeModel(StyleBanks.createTree(ElementType.SYMBOLIZER));

    private ButtonGroup group = null;

    private static final int maxWidth = 150;


    public JBankPanel() {
        initComponents();

        jsp.addComponentListener(new ComponentListener() {

            @Override
            public void componentResized(ComponentEvent ce) {
                int col = (jsp.getWidth()-30) / maxWidth;
                if(col == 0) col = 1;
                guiPreviewPane.setLayout(new GridLayout(0,col));
                guiPreviewPane.revalidate();
                guiPreviewPane.repaint();
            }

            @Override
            public void componentMoved(ComponentEvent ce) {
            }

            @Override
            public void componentShown(ComponentEvent ce) {
            }

            @Override
            public void componentHidden(ComponentEvent ce) {
            }
        });


        final ElementNode root = StyleBanks.createTree(ElementType.STYLE);
        guiBanks.setModel(new DefaultTreeModel(root));
        guiBanks.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        guiBanks.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent tse) {
                updatePreviews(tse.getPath());
            }
        });
    }

    private void updatePreviews(final TreePath path){

        group = new ButtonGroup();

        int col = (jsp.getWidth()-30) / maxWidth;
        if(col == 0) col = 1;
        guiPreviewPane.removeAll();
        guiPreviewPane.setLayout(new GridLayout(0,col));

//        FlowLayout layout = new WrapFlowLayout();
//        layout.setAlignment(SwingConstants.WEST);

//        guiPreviewPane.setLayout(new WrapFlowLayout());
        guiPreviewPane.revalidate();
        guiPreviewPane.repaint();
        jsp.revalidate();
        jsp.repaint();


        if(path != null && path.getLastPathComponent() instanceof ElementNode);

        new Thread(){
            @Override
            public void run() {
//                ElementNode n = null;
//                for(int i=0;i<path.getPathCount();i++){
//                    if(n==null){
//                        n = (ElementNode) ((ElementNode)styleModel.getRoot()).getChildAt(i);
//                    }else{
//                        n = 
//                    }
//                }

                ElementNode node = (ElementNode) path.getLastPathComponent();
                explore(node);
            }
        }.start();
    }

    private void explore(final ElementNode node){

        Object obj = node.getUserObject();

        if(obj instanceof MutableStyle){
            append(node.getName(), (MutableStyle)obj);
        }

        for(int i=0,n=node.getChildCount(); i<n; i++){
            explore((ElementNode) node.getChildAt(i));
        }
    }

    private void append(final String name, final MutableStyle style){
        JToggleButton jb = new JToggleButton();
        jb.setBorderPainted(false);
        jb.setMaximumSize(new Dimension(maxWidth, maxWidth));
        jb.setVerticalTextPosition(AbstractButton.BOTTOM);
        jb.setHorizontalTextPosition(AbstractButton.CENTER);
        group.add(jb);

        BufferedImage image = DefaultGlyphService.create(style, new Dimension(60, 60),null);
        jb.setIcon(new ImageIcon(image));
        jb.setText(name);

        guiPreviewPane.add(jb);
        guiPreviewPane.revalidate();
        guiPreviewPane.repaint();
        jsp.revalidate();
        jsp.repaint();
    }


    @Override
    public void setTarget(final Object target) {
        if(layer instanceof MapLayer){
            this.layer = (MapLayer) layer;
        }else{
            this.layer = null;
        }
    }

    @Override
    public void apply() {
        if(layer == null) return;
    }

    @Override
    public void reset() {
    }

    @Override
    public String getTitle() {
        return MessageBundle.getString("styleBank");
    }

    @Override
    public ImageIcon getIcon() {
        return null;
    }

    @Override
    public String getToolTip() {
        return "";
    }

    @Override
    public Component getComponent() {
        return this;
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
        jPanel1 = new javax.swing.JPanel();
        jsp = new javax.swing.JScrollPane();
        guiPreviewPane = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        guiBanks = new javax.swing.JTree();

        jSplitPane1.setDividerLocation(400);

        jsp.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        javax.swing.GroupLayout guiPreviewPaneLayout = new javax.swing.GroupLayout(guiPreviewPane);
        guiPreviewPane.setLayout(guiPreviewPaneLayout);
        guiPreviewPaneLayout.setHorizontalGroup(
            guiPreviewPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 398, Short.MAX_VALUE)
        );
        guiPreviewPaneLayout.setVerticalGroup(
            guiPreviewPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 298, Short.MAX_VALUE)
        );

        jsp.setViewportView(guiPreviewPane);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jsp, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jsp, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );

        jSplitPane1.setLeftComponent(jPanel1);

        guiBanks.setShowsRootHandles(false);
        jScrollPane1.setViewportView(guiBanks);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );

        jSplitPane1.setRightComponent(jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 498, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTree guiBanks;
    private javax.swing.JPanel guiPreviewPane;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JScrollPane jsp;
    // End of variables declaration//GEN-END:variables

}
