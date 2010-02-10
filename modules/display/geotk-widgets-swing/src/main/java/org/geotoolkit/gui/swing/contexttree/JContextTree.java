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
package org.geotoolkit.gui.swing.contexttree;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EventObject;
import java.util.List;
import javax.swing.DropMode;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.event.CellEditorListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.jdesktop.swingx.JXTree;

import org.geotoolkit.gui.swing.resource.IconBundle;

import org.geotoolkit.display2d.service.DefaultGlyphService;
import org.geotoolkit.map.ContextListener;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.CollectionChangeEvent;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.util.SimpleInternationalString;
import org.geotoolkit.style.DefaultStyleFactory;

import org.opengis.style.Description;
import org.opengis.style.FeatureTypeStyle;
import org.opengis.style.Rule;

public class JContextTree extends JScrollPane {

    private static final DataFlavor LAYER_FLAVOR = new DataFlavor(org.geotoolkit.map.MapLayer.class, "geo/layer");
    private static final MutableStyleFactory SF = new DefaultStyleFactory();
    private static final ImageIcon ICON_LAYER_VISIBLE = IconBundle.getInstance().getIcon("16_maplayer_visible");
    private static final ImageIcon ICON_LAYER_UNVISIBLE = IconBundle.getInstance().getIcon("16_maplayer_unvisible");

    private final List<TreePopupItem> controls = new ArrayList<TreePopupItem>();
    private final DefaultMutableTreeNode root = new DefaultMutableTreeNode(null);
    private final DefaultTreeModel model = new DefaultTreeModel(root);
    private final JXTree tree = new JXTree(model);
    private final TreePopup popup = new TreePopup(this);
    private MapContext context = null;

    private final ContextListener listener = new ContextListener() {

        @Override
        public void propertyChange(PropertyChangeEvent event) {
        }

        @Override
        public void layerChange(CollectionChangeEvent<MapLayer> event) {
            if(event.getType() != CollectionChangeEvent.ITEM_CHANGED){
                updateContent();

            }else{
                MapLayer layer = event.getItems().iterator().next();
                updateLayer(layer);
            }
        }

    };

    private final ContextCellRenderer editor = new ContextCellRenderer();
    private final ContextCellRenderer renderer = new ContextCellRenderer();

    public JContextTree() {
        add(tree);
        tree.setCellRenderer(renderer);
        tree.setCellEditor(editor);
        tree.setShowsRootHandles(false);
        tree.setEditable(true);
        tree.setDragEnabled(true);
        tree.setTransferHandler(new LayerHandler());
        tree.setDropMode(DropMode.ON_OR_INSERT);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setComponentPopupMenu(popup);
        tree.setScrollsOnExpand(false);
        tree.setLargeModel(true);
        setViewportView(tree);
        setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        initCellEditAcceleration();

    }

    public void setRootVisible(boolean visible){
        tree.setRootVisible(visible);
    }

    public boolean isRootVisible(){
        return tree.isRootVisible();
    }

    public void setContext(MapContext context) {
        if(this.context != null){
            context.removeContextListener(listener);
        }
        this.context = context;

        if(this.context != null){
            context.addContextListener(listener);
        }

        updateContent();

    }

    public MapContext getContext() {
        return context;
    }

    private void updateContent() {

        root.removeAllChildren();
        root.setUserObject(context);

        if (context == null) {
            return;
        }

        List<MapLayer> reversed = new ArrayList<MapLayer>(context.layers());
        Collections.reverse(reversed);

        for (MapLayer layer : reversed) {
            root.add(createNode(layer));
        }
        model.reload();

        tree.expandAll();
        tree.revalidate();
        tree.repaint();
    }

    private void updateLayer(MapLayer layer) {
        DefaultMutableTreeNode node = createNode(layer);

        for (int i = 0; i < root.getChildCount(); i++) {
            DefaultMutableTreeNode candidate = (DefaultMutableTreeNode) root.getChildAt(i);
            if(candidate.getUserObject().equals(layer)){
                if(!tree.isEditing() || tree.stopEditing()){
                    TreePath selectedPath = tree.getSelectionPath();
                    TreePath candidatePath = new TreePath(model.getPathToRoot(candidate));
                    boolean expanded = tree.isExpanded(candidatePath);
                    boolean selected = candidatePath.equals(selectedPath);
                    model.removeNodeFromParent(candidate);
                    model.insertNodeInto(node, root, i);
                    TreePath newPath = new TreePath(model.getPathToRoot(node));

                    if(expanded){
                        tree.expandPath(newPath);
                    }else{
                        model.reload(node);
                    }

                    if(selected){
                        tree.setSelectionPath(newPath);
                    }

                    break;
                }
            }

        }

    }

    /**
     * @return live list of TreePopupItem
     */
    public List<TreePopupItem> controls() {
        return controls;
    }

    JTree getRealTree(){
        return tree;
    }

    private DefaultMutableTreeNode createNode(MapLayer layer){
        DefaultMutableTreeNode layerNode = new DefaultMutableTreeNode(layer);
        for(FeatureTypeStyle fts : layer.getStyle().featureTypeStyles()){
            for(Rule rule : fts.rules()){
                DefaultMutableTreeNode ruleNode = new DefaultMutableTreeNode(rule);
                layerNode.add(ruleNode);
            }
        }
        return layerNode;
    }

    private int getRowAt(Point p){
        int row = tree.getRowForLocation(p.x, p.y);
        if(row == -1){
            //more intensive search, row selectable area might be small
            for(int i=0,n=tree.getRowCount();i<n;i++){
                Rectangle rect = tree.getRowBounds(i);
                if(p.y> rect.y && p.y< rect.y+rect.height){
                    row = i;
                    break;
                }
            }
        }

        return row;
    }

    /**
     * add mouse listener to set cell in edit mode when mouseover
     */
    private void initCellEditAcceleration() {
        //listener to set cell in select mode on mouse over
        tree.addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged(MouseEvent e) {
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                Point p = e.getPoint();

                if (p == null) {
                    return;
                }

                int row = getRowAt(p);

                TreePath overPath = tree.getPathForRow(row);
                TreePath editPath = tree.getEditingPath();

                if(tree.isEditing()){
                    tree.stopEditing();
                }

                tree.setSelectionPath(overPath);

            }
        });

        //listener to propage mouse events in edition mode
        tree.addMouseListener(new MouseListener() {

            private MouseEvent pressedEvent = null;

            @Override
            public void mouseClicked(final MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                this.pressedEvent = e;
            }

            @Override
            public void mouseReleased(MouseEvent releaseEvent) {

                int row = getRowAt(releaseEvent.getPoint());
                final TreePath under = tree.getPathForRow(row);

                if (under != null && tree.getRowForLocation(releaseEvent.getPoint().x, releaseEvent.getPoint().y) == -1) {

                    if (!under.equals(tree.getEditingPath())) {
                        //different node, change edition
                        tree.stopEditing();
                        tree.startEditingAtPath(under);

                        //propagate event to undereath component

                        Point componentPoint = SwingUtilities.convertPoint(tree, new Point(releaseEvent.getX(), releaseEvent.getY()), editor.panel);
                        Component destination = SwingUtilities.getDeepestComponentAt(editor.panel, componentPoint.x, componentPoint.y);
                        if (destination != null && pressedEvent != null) {
                            destination.dispatchEvent(SwingUtilities.convertMouseEvent(tree, pressedEvent, destination));
                            destination.dispatchEvent(SwingUtilities.convertMouseEvent(tree, releaseEvent, destination));
                        }
                        releaseEvent.consume();
                    }
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
    }

    private class ContextCellRenderer extends DefaultTreeCellRenderer implements TreeCellEditor {

        private final JPanel panel = new JPanel(new GridBagLayout());
        private final GridBagConstraints gc = new GridBagConstraints();
        private final JLabel icon = new JLabel();
        private final JCheckBox visibleCheck = new VisibleCheck();
        private final JCheckBox selectCheck = new SelectionCheck();
        private final JLabel label = new JLabel();
        private final JTextField field = new JTextField();

        private Object value = null;

        public ContextCellRenderer() {
            field.setOpaque(false);
            field.setPreferredSize(new Dimension(140,field.getPreferredSize().height));

            visibleCheck.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (value != null && value instanceof MapLayer) {
                        ((MapLayer) value).setVisible(visibleCheck.isSelected());
                    }
                }
            });

            selectCheck.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (value != null && value instanceof MapLayer) {
                        ((MapLayer) value).setSelectable(selectCheck.isSelected());
                    }
                }
            });

            panel.setOpaque(false);
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object obj, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            return getComponent(obj, false);
        }

        @Override
        public Component getTreeCellEditorComponent(JTree tree, Object obj, boolean isSelected, boolean expanded, boolean leaf, int row) {
            return getComponent(obj, true);
        }

        private Component getComponent(Object obj, boolean edition){
            final DefaultMutableTreeNode node = (DefaultMutableTreeNode) obj;
            if (node != null) obj = node.getUserObject();
            value = obj;

            panel.removeAll();

            if (obj instanceof MapContext) {
                final MapContext context = (MapContext) obj;

                gc.weightx = 1;
                gc.weighty = 1;
                gc.gridx = 0;

                if(edition){
                    this.field.setText(context.getDescription().getTitle().toString());
                    panel.add(field,gc);
                }else{
                    this.label.setText(context.getDescription().getTitle().toString());
                    panel.add(label,gc);
                }
                
            } else if (obj instanceof MapLayer) {
                final MapLayer layer = (MapLayer) obj;

                gc.weightx = 0;
                gc.weighty = 1;
                gc.gridx = 0;
//                this.icon.setIcon((layer.isVisible()) ? ICON_LAYER_VISIBLE : ICON_LAYER_UNVISIBLE);
//                panel.add(icon,gc);
                gc.gridx = 1;
                this.visibleCheck.setSelected(layer.isVisible());
                panel.add(visibleCheck,gc);
                gc.gridx = 2;
                this.selectCheck.setSelected(layer.isSelectable());
                panel.add(selectCheck,gc);

                gc.weightx = 1;
                gc.weighty = 1;
                gc.gridx = 3;
                if(edition){
                    this.field.setText(layer.getDescription().getTitle().toString());
                    panel.add(field,gc);
                }else{
                    this.label.setText(layer.getDescription().getTitle().toString());
                    panel.add(label,gc);
                }

            } else if(obj instanceof Rule){
                final Rule rule = (Rule) obj;

                final MapLayer layer = (MapLayer) ((DefaultMutableTreeNode)node.getParent()).getUserObject();

                Dimension dim = DefaultGlyphService.glyphPreferredSize(rule, null, layer);
                final BufferedImage img = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB);
                DefaultGlyphService.render(rule, new Rectangle(dim), img.createGraphics(),layer);

                gc.weightx = 0;
                gc.weighty = 1;
                gc.gridx = 0;
                this.icon.setIcon(new ImageIcon(img));
                panel.add(icon,gc);

                gc.weightx = 1;
                gc.weighty = 1;
                gc.gridx = 1;
                this.label.setText(rule.getDescription().getTitle().toString());
                panel.add(label,gc);
            }else {
                gc.weightx = 1;
                gc.weighty = 1;
                this.label.setText("-");
                panel.add(label,gc);
            }
            panel.revalidate();
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            if (value instanceof MapContext) {
                MapContext context = (MapContext) value;
                Description old = context.getDescription();
                context.setDescription(SF.description(new SimpleInternationalString(field.getText()), old.getAbstract()));

            } else if (value instanceof MapLayer) {
                MapLayer layer = (MapLayer) value;
                Description old = layer.getDescription();
                layer.setDescription(SF.description(new SimpleInternationalString(field.getText()), old.getAbstract()));
                layer.setSelectable(selectCheck.isSelected());
                layer.setVisible(visibleCheck.isSelected());
            }

            Object value = this.value;
            this.value = null;
            return value;
        }

        @Override
        public boolean isCellEditable(EventObject anEvent) {
            TreePath path = tree.getSelectionPath();
            if(path != null){
                Object obj = ((DefaultMutableTreeNode)path.getLastPathComponent()).getUserObject();
                return obj instanceof MapContext || obj instanceof MapLayer;
            }
            return false;
        }

        @Override
        public boolean shouldSelectCell(EventObject anEvent) {
            return true;
        }

        @Override
        public boolean stopCellEditing() {
            return true;
        }

        @Override
        public void cancelCellEditing() {
        }

        @Override
        public void addCellEditorListener(CellEditorListener l) {
        }

        @Override
        public void removeCellEditorListener(CellEditorListener l) {
        }
    }

    private class LayerHandler extends TransferHandler {

        @Override
        public int getSourceActions(JComponent c) {
            return TransferHandler.MOVE;
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            JTree tree = (JTree) c;
            TreePath path = tree.getSelectionPath();
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();

            if (node != null && node.getUserObject() instanceof MapLayer) {
                return new LaterTransferable((MapLayer) node.getUserObject());
            } else {
                return null;
            }

        }

        @Override
        public Icon getVisualRepresentation(Transferable t) {
            return ICON_LAYER_VISIBLE;
        }

        @Override
        public boolean canImport(TransferHandler.TransferSupport support) {

            if (!support.isDataFlavorSupported(LAYER_FLAVOR) || !support.isDrop()) {
                return false;
            }

            JTree.DropLocation dropLocation = (JTree.DropLocation) support.getDropLocation();
            return dropLocation.getPath() != null;
        }

        @Override
        public boolean importData(TransferHandler.TransferSupport support) {
            if (!canImport(support)) {
                return false;
            }

            JTree.DropLocation dropLocation = (JTree.DropLocation) support.getDropLocation();
            TreePath path = dropLocation.getPath();
            Transferable transferable = support.getTransferable();

            MapLayer transferedLayer;
            try {
                transferedLayer = (MapLayer) transferable.getTransferData(LAYER_FLAVOR);
            } catch (IOException e) {
                return false;
            } catch (UnsupportedFlavorException e) {
                return false;
            }

            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) path.getLastPathComponent();
            Object parent = parentNode.getUserObject();

            while( !(parent instanceof MapContext) && !(parent instanceof MapLayer)){
                parent = ((DefaultMutableTreeNode)parentNode.getParent()).getUserObject();
            }


            if(parent instanceof MapContext){
                MapContext context = (MapContext) parent;
                context.layers().remove(transferedLayer);
                context.layers().add(transferedLayer);
            }else if(parent instanceof MapLayer){

                MapContext context = getContext();
                MapLayer layer = (MapLayer) parent;
                
                if(layer == transferedLayer){
                    return true;
                }
                
                int index = context.layers().indexOf(layer);
                context.layers().remove(transferedLayer);
                context.layers().add(index,transferedLayer);

            }else{
            }

            return true;
        }
    }

    private class LaterTransferable implements Transferable {

        private final MapLayer layer;

        private LaterTransferable(MapLayer mapLayer) {
            this.layer = mapLayer;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{LAYER_FLAVOR};
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return flavor.equals(LAYER_FLAVOR);
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            if(flavor.equals(LAYER_FLAVOR)){
                return layer;
            }else{
                return null;
            }
        }
    }

}
