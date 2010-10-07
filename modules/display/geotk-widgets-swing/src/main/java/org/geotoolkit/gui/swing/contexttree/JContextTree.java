/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2010, Johann Sorel
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
import java.awt.FlowLayout;
import java.awt.Image;
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
import java.util.logging.Level;
import java.util.logging.Logger;
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

import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.display2d.service.DefaultGlyphService;
import org.geotoolkit.gui.swing.style.JOpacitySlider;
import org.geotoolkit.map.ContextListener;
import org.geotoolkit.map.DynamicMapLayer;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.CollectionChangeEvent;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.util.SimpleInternationalString;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableFeatureTypeStyle;

import org.opengis.style.Description;
import org.opengis.style.FeatureTypeStyle;
import org.opengis.style.Rule;

public class JContextTree extends JScrollPane implements ContextListener {

    private static final DataFlavor LAYER_FLAVOR = new DataFlavor(org.geotoolkit.map.MapLayer.class, "geo/layer");
    private static final MutableStyleFactory SF = new DefaultStyleFactory();
    private static final ImageIcon ICON_LAYER_VISIBLE = IconBundle.getInstance().getIcon("16_maplayer_visible");
    private static final ImageIcon ICON_LAYER_UNVISIBLE = IconBundle.getInstance().getIcon("16_maplayer_unvisible");
    private static final ImageIcon ICON_FTS = IconBundle.getInstance().getIcon("16_style_fts");

    private final List<TreePopupItem> controls = new ArrayList<TreePopupItem>();
    private final DefaultMutableTreeNode root = new DefaultMutableTreeNode(null);
    private final DefaultTreeModel model = new DefaultTreeModel(root);
    private final JTree tree = new JTree(model);
    private final TreePopup popup = new TreePopup(this);
    private MapContext context = null;

    private final ContextCellRenderer editor = new ContextCellRenderer();
    private final ContextCellRenderer renderer = new ContextCellRenderer();
    private final ContextListener.Weak weakListener = new ContextListener.Weak(this);

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

    public boolean isEditable(){
        return tree.isEditable();
    }

    public void setEditable(boolean edit){
        tree.setEditable(edit);
    }

    public void setContext(MapContext context) {
        if(this.context != null){
            weakListener.unregisterSource(this.context);
        }
        this.context = context;

        if(this.context != null){
            weakListener.registerSource(this.context);
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

        final List<MapLayer> reversed = new ArrayList<MapLayer>(context.layers());
        Collections.reverse(reversed);

        for (MapLayer layer : reversed) {
            root.add(createNode(layer));
        }
        model.reload();
        
        tree.revalidate();
        tree.repaint();
    }

    private void updateLayer(MapLayer layer) {
        final DefaultMutableTreeNode node = createNode(layer);

        for (int i = 0; i < root.getChildCount(); i++) {
            final DefaultMutableTreeNode candidate = (DefaultMutableTreeNode) root.getChildAt(i);
            if(candidate.getUserObject().equals(layer)){
                if(!tree.isEditing() || tree.stopEditing()){
                    final TreePath selectedPath = tree.getSelectionPath();
                    final TreePath candidatePath = new TreePath(model.getPathToRoot(candidate));
                    final boolean expanded = tree.isExpanded(candidatePath);
                    final boolean selected = candidatePath.equals(selectedPath);
                    model.removeNodeFromParent(candidate);
                    model.insertNodeInto(node, root, i);
                    final TreePath newPath = new TreePath(model.getPathToRoot(node));

                    if(expanded){
                        tree.expandPath(newPath);
                    }else{
                        model.reload(node);
                    }
                    tree.expandPath(new TreePath(root));

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

        final DefaultMutableTreeNode layerNode = new DefaultMutableTreeNode(layer);

        if(layer instanceof DynamicMapLayer){
            //this kind of layer have there own style systems we rely on it
            final DynamicMapLayer dynlayer = (DynamicMapLayer) layer;
            try {
                final Image img = dynlayer.getLegend();
                final DefaultMutableTreeNode imgNode = new DefaultMutableTreeNode(img);
                layerNode.add(imgNode);
            } catch (PortrayalException ex) {
                Logger.getLogger(JContextTree.class.getName()).log(Level.WARNING, null, ex);
            }

        }else{
            final List<MutableFeatureTypeStyle> ftss = layer.getStyle().featureTypeStyles();
            if(ftss.size() == 1){
                for(FeatureTypeStyle fts : layer.getStyle().featureTypeStyles()){
                    for(Rule rule : fts.rules()){
                        final DefaultMutableTreeNode ruleNode = new DefaultMutableTreeNode(rule);
                        layerNode.add(ruleNode);
                    }
                }
            }else{
                for(FeatureTypeStyle fts : layer.getStyle().featureTypeStyles()){
                    final DefaultMutableTreeNode ftsNode = new DefaultMutableTreeNode(fts);
                    for(Rule rule : fts.rules()){
                        final DefaultMutableTreeNode ruleNode = new DefaultMutableTreeNode(rule);
                        ftsNode.add(ruleNode);
                    }
                    layerNode.add(ftsNode);
                }
            }

        }
        return layerNode;
    }

    private int getRowAt(Point p){
        int row = tree.getRowForLocation(p.x, p.y);
        if(row == -1){
            //more intensive search, row selectable area might be small
            for(int i=0,n=tree.getRowCount();i<n;i++){
                final Rectangle rect = tree.getRowBounds(i);
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
                final Point p = e.getPoint();

                if (p == null) {
                    return;
                }

                int row = getRowAt(p);

                final TreePath overPath = tree.getPathForRow(row);
                final TreePath editPath = tree.getEditingPath();

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

                if(releaseEvent.getButton() != MouseEvent.BUTTON1){
                    //forward event only on left click to avoid disturbing popup menu.
                    return;
                }

                int row = getRowAt(releaseEvent.getPoint());
                final TreePath under = tree.getPathForRow(row);

                if (under != null && tree.getRowForLocation(releaseEvent.getPoint().x, releaseEvent.getPoint().y) == -1) {

                    if (!under.equals(tree.getEditingPath())) {
                        //different node, change edition
                        tree.stopEditing();
                        tree.startEditingAtPath(under);

                        //propagate event to undereath component

                        final Point componentPoint = SwingUtilities.convertPoint(tree, new Point(releaseEvent.getX(), releaseEvent.getY()), editor.panel);
                        final Component destination = SwingUtilities.getDeepestComponentAt(editor.panel, componentPoint.x, componentPoint.y);
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

    private static String label(Description desc){
        if(desc != null && desc.getTitle() != null){
            return desc.getTitle().toString();
        }else{
            return "";
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    //Layer listener ///////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    @Override
    public void propertyChange(PropertyChangeEvent event) {
    }

    @Override
    public void layerChange(CollectionChangeEvent<MapLayer> event) {
        if(event.getType() != CollectionChangeEvent.ITEM_CHANGED){
            updateContent();

        }else{
            final MapLayer layer = event.getItems().iterator().next();
            updateLayer(layer);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    //private classes //////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    private class ContextCellRenderer extends DefaultTreeCellRenderer implements TreeCellEditor {

        private final JPanel panel;
        private final JLabel icon = new JLabel();
        private final JOpacitySlider opacity = new JOpacitySlider();
        private final JCheckBox visibleCheck = new VisibleCheck();
        private final JCheckBox selectCheck = new SelectionCheck();
        private final JLabel label = new JLabel(" "){
            @Override
            public Dimension getPreferredSize() {
                final Dimension dim = super.getPreferredSize();
                dim.height += 2;
                return dim;
            }
        };
        private final JTextField field = new JTextField();

        private Object value = null;

        public ContextCellRenderer() {
            final FlowLayout layout = new FlowLayout(FlowLayout.LEFT,1,0);
            panel = new JPanel(layout);

            field.setOpaque(false);
            field.setPreferredSize(new Dimension(140,label.getPreferredSize().height));

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

            opacity.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (value != null && value instanceof MapLayer) {
                        ((MapLayer) value).setOpacity(opacity.getOpacity());
                    }
                }
            });

            panel.setOpaque(false);
            opacity.setPreferredSize(new Dimension(60, 22));
            opacity.setSize(new Dimension(60, 22));
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

            this.label.setIcon(null);

            panel.removeAll();
            panel.revalidate();
            panel.repaint();

            if (obj instanceof MapContext) {
                final MapContext context = (MapContext) obj;
                if(edition){
                    this.field.setText(label(context.getDescription()));
                    panel.add(field);
                }else{
                    this.label.setText(label(context.getDescription()));
                    panel.add(label);
                }

            } else if (obj instanceof MapLayer) {
                final MapLayer layer = (MapLayer) obj;

                opacity.setOpacity(layer.getOpacity());
                panel.add(opacity);
                this.visibleCheck.setSelected(layer.isVisible());
                panel.add(visibleCheck);
                this.selectCheck.setSelected(layer.isSelectable());
                panel.add(selectCheck);
                if(edition){
                    this.field.setText(label(layer.getDescription()));
                    panel.add(field);
                }else{
                    this.label.setText(label(layer.getDescription()));
                    panel.add(label);
                }

            } else if(obj instanceof FeatureTypeStyle){
                final FeatureTypeStyle fts = (FeatureTypeStyle) obj;
                this.icon.setIcon(ICON_FTS);
                panel.add(icon);
                this.label.setText(label(fts.getDescription()));
                panel.add(label);
            }else if(obj instanceof Rule){
                final Rule rule = (Rule) obj;

                MapLayer layer = null;
                DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();

                while(layer == null && parent != null){
                    Object cdt = parent.getUserObject();
                    if(cdt instanceof MapLayer){
                        layer = (MapLayer) cdt;
                    }else{
                        parent = (DefaultMutableTreeNode) parent.getParent();
                    }
                }

                final Dimension dim = DefaultGlyphService.glyphPreferredSize(rule, null, layer);
                final BufferedImage img = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB);
                DefaultGlyphService.render(rule, new Rectangle(dim), img.createGraphics(),layer);

                this.icon.setIcon(new ImageIcon(img));
                panel.add(icon);

                this.label.setText(label(rule.getDescription()));
                panel.add(label);
            } else if(obj instanceof Image){
                final Image img = (Image) obj;
                this.label.setText("");
                this.label.setIcon(new ImageIcon(img));
                panel.add(label);
            } else {
                this.label.setText("-");
                panel.add(label);
            }
            panel.revalidate();
            panel.repaint();
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            if (value instanceof MapContext) {
                final MapContext context = (MapContext) value;
                final Description old = context.getDescription();
                context.setDescription(SF.description(new SimpleInternationalString(field.getText()), old.getAbstract()));

            } else if (value instanceof MapLayer) {
                final MapLayer layer = (MapLayer) value;
                final Description old = layer.getDescription();
                layer.setDescription(SF.description(new SimpleInternationalString(field.getText()), old.getAbstract()));
                layer.setSelectable(selectCheck.isSelected());
                layer.setVisible(visibleCheck.isSelected());
            }

            Object temp = this.value;
            this.value = null;
            return temp;
        }

        @Override
        public boolean isCellEditable(EventObject anEvent) {
            final TreePath path = tree.getSelectionPath();
            if(path != null){
                final Object obj = ((DefaultMutableTreeNode)path.getLastPathComponent()).getUserObject();
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
            final JTree tree = (JTree) c;
            final TreePath path = tree.getSelectionPath();
            final DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();

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

            final JTree.DropLocation dropLocation = (JTree.DropLocation) support.getDropLocation();
            return dropLocation.getPath() != null;
        }

        @Override
        public boolean importData(TransferHandler.TransferSupport support) {
            if (!canImport(support)) {
                return false;
            }

            final JTree.DropLocation dropLocation = (JTree.DropLocation) support.getDropLocation();
            final TreePath path = dropLocation.getPath();
            final Transferable transferable = support.getTransferable();

            MapLayer transferedLayer;
            try {
                transferedLayer = (MapLayer) transferable.getTransferData(LAYER_FLAVOR);
            } catch (IOException e) {
                return false;
            } catch (UnsupportedFlavorException e) {
                return false;
            }

            final DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) path.getLastPathComponent();
            Object parent = parentNode.getUserObject();

            while( !(parent instanceof MapContext) && !(parent instanceof MapLayer)){
                parent = ((DefaultMutableTreeNode)parentNode.getParent()).getUserObject();
            }


            if(parent instanceof MapContext){
                final MapContext context = (MapContext) parent;
                context.layers().remove(transferedLayer);
                context.layers().add(transferedLayer);
            }else if(parent instanceof MapLayer){

                final MapContext context = getContext();
                final MapLayer layer = (MapLayer) parent;

                if(layer == transferedLayer){
                    return true;
                }

                final int index = context.layers().indexOf(layer);
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
