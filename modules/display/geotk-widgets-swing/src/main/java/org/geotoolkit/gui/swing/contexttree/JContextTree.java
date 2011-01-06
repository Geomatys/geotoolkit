/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2011, Johann Sorel
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
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.display2d.service.DefaultGlyphService;
import org.geotoolkit.gui.swing.style.JOpacitySlider;
import org.geotoolkit.map.GraphicBuilder;
import org.geotoolkit.map.ItemListener;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapItem;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.util.collection.CollectionChangeEvent;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.util.SimpleInternationalString;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.util.NullArgumentException;
import org.geotoolkit.util.XArrays;
import org.geotoolkit.util.logging.Logging;

import org.opengis.style.Description;
import org.opengis.style.FeatureTypeStyle;
import org.opengis.style.Rule;

public class JContextTree extends JScrollPane {

    private static final DataFlavor ITEM_FLAVOR = new DataFlavor(org.geotoolkit.map.MapItem.class, "geo/item");
    private static final MutableStyleFactory SF = new DefaultStyleFactory();
    private static final ImageIcon ICON_FTS = IconBundle.getIcon("16_style_fts");
    private static final ImageIcon ICON_GROUP = IconBundle.getIcon("16_attach");

    private final List<TreePopupItem> controls = new ArrayList<TreePopupItem>();
    private final JTree tree = new JTree(new DefaultTreeModel(new DefaultMutableTreeNode()));
    private final TreePopup popup = new TreePopup(this);

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

    public boolean isEditable(){
        return tree.isEditable();
    }

    public void setEditable(boolean edit){
        tree.setEditable(edit);
    }

    public void setContext(final MapContext context) {
        final DefaultMutableTreeNode node;
        if(context != null){
            node = new MapItemTreeNode(context);
        }else{
            node = new DefaultMutableTreeNode();
        }
        tree.setModel(new DefaultTreeModel(node));
        tree.expandPath(new TreePath(node.getPath()));
    }

    public MapContext getContext() {
        final DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getModel().getRoot();
        return (MapContext) node.getUserObject();
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
                    if (value != null && value instanceof MapItem) {
                        ((MapItem) value).setVisible(visibleCheck.isSelected());
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

            if (obj instanceof MapLayer) {
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
                    this.label.setText(label(layer.getDescription())+" ");
                    panel.add(label);
                }

            } else if (obj instanceof MapItem) {
                final MapItem item = (MapItem) obj;
                
                if(!(item instanceof MapContext)){
                    this.icon.setIcon(ICON_GROUP);
                    panel.add(icon);
                }

                this.visibleCheck.setSelected(item.isVisible());
                panel.add(visibleCheck);

                if(edition){
                    this.field.setText(label(item.getDescription()));
                    panel.add(field);
                }else{
                    this.label.setText(label(item.getDescription())+" ");
                    panel.add(label);
                }

            }else if(obj instanceof FeatureTypeStyle){
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
            if (value instanceof MapLayer) {
                final MapLayer layer = (MapLayer) value;
                final Description old = layer.getDescription();
                layer.setDescription(SF.description(new SimpleInternationalString(field.getText()), old.getAbstract()));
                layer.setSelectable(selectCheck.isSelected());
                layer.setVisible(visibleCheck.isSelected());
            } else if (value instanceof MapItem) {
                final MapItem item = (MapItem) value;
                final Description old = item.getDescription();
                item.setDescription(SF.description(new SimpleInternationalString(field.getText()), old.getAbstract()));

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
                return obj instanceof MapItem;
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

            if (node != null && (node.getUserObject() instanceof MapItem && !(node.getUserObject() instanceof MapContext))) {
                return new MapItemTransferable(path);
            }

            return null;
        }

        @Override
        public boolean canImport(TransferHandler.TransferSupport support) {

            if (!support.isDataFlavorSupported(ITEM_FLAVOR) || !support.isDrop()) {
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
            final TreePath dropPath = dropLocation.getPath();
            final Transferable transferable = support.getTransferable();
            int dropIndex = dropLocation.getChildIndex();

            final TreePath sourcePath;
            try {
                sourcePath = (TreePath) transferable.getTransferData(ITEM_FLAVOR);
            } catch (UnsupportedFlavorException ex) {
                Logging.getLogger(JContextTree.class).log(Level.INFO, null, ex);
                return false;
            } catch (IOException ex) {
                Logging.getLogger(JContextTree.class).log(Level.INFO, null, ex);
                return false;
            }

            if(sourcePath == null){
                return false;
            }

            final DefaultMutableTreeNode lastNode = ((DefaultMutableTreeNode)sourcePath.getLastPathComponent());
            final MapItem dragged = (MapItem) lastNode.getUserObject();
            final MapItem lastParent = (MapItem) ((DefaultMutableTreeNode)lastNode.getParent()).getUserObject();

            if(XArrays.contains(dropPath.getPath(), lastNode)){
                //trying to drop a node in himself, not possible
                return true;
            }


            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) dropPath.getLastPathComponent();
            Object parent = parentNode.getUserObject();
            if(!(parent instanceof MapItem)){
                return true;
            }

            //flip drop index
            if(dropIndex != -1){
                dropIndex = ((MapItem)parent).items().size() - dropIndex;
            }

            if(parent instanceof MapLayer){
                //we adjust the drop location
                final MapItem newParent = (MapItem) ((DefaultMutableTreeNode)(parentNode.getParent())).getUserObject();
                final MapLayer layer = (MapLayer) parent;
                parent = newParent;
                dropIndex = newParent.items().indexOf(layer);
            }
            
            
            //this far we are sure it's a MapItem
            final MapItem newParent = (MapItem) parent;
            
            if(lastParent == newParent && dropIndex != -1){
                //moving node is the same parent, readjust dropIndex
                if(lastParent.items().indexOf(dragged) < dropIndex){
                    dropIndex--;
                }

            }else{
                dropIndex = 0;
            }
            
            //remove from previous position
            lastParent.items().remove(dragged);
            newParent.items().add(dropIndex, dragged);

            return true;
        }
    }

    private class MapItemTransferable implements Transferable {

        private final TreePath path;

        private MapItemTransferable(TreePath path) {
            this.path = path;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{ITEM_FLAVOR};
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return flavor.equals(ITEM_FLAVOR);
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            if(ITEM_FLAVOR.equals(flavor)){
                return path;
            }
            return null;
        }
    }

    private class MapItemTreeNode extends DefaultMutableTreeNode implements ItemListener{

        private MapItemTreeNode(MapItem item){
            super(item);
            if(item == null){
                throw new NullArgumentException("Item can not be null.");
            }
            item.addItemListener(new ItemListener.Weak(item, MapItemTreeNode.this));

            resetStructure();
        }

        @Override
        public MapItem getUserObject() {
            return (MapItem)super.getUserObject();
        }

        @Override
        public void setUserObject(Object userObject) {
            //not allowed to modify this object
        }

        private synchronized void resetStructure(){
            removeAllChildren();
            final MapItem item = (MapItem) getUserObject();
            
            final List<MapItem> childs = new ArrayList<MapItem>(item.items());
            Collections.reverse(childs);
            for(final MapItem child : childs){
                final MapItemTreeNode childNode = new MapItemTreeNode(child);
                add(childNode);
            }
            
            if(item instanceof MapLayer){
                fillStyleNodes((MapLayer)item);
            }
        }

        @Override
        public synchronized void itemChange(CollectionChangeEvent<MapItem> event) {

            if(CollectionChangeEvent.ITEM_ADDED == event.getType()){

                final List<MapItem> childs = new ArrayList<MapItem>(getUserObject().items());
                Collections.reverse(childs);

                int i=0;
                for(MapItem child : childs){
                    final MapItemTreeNode pair = (i<getChildCount()) ? ((MapItemTreeNode)getChildAt(i)) : null;
                    if(pair == null || !child.equals(pair.getUserObject())){
                        //this child was added
                        MapItemTreeNode childNode = new MapItemTreeNode(child);
                        ((DefaultTreeModel)tree.getModel()).insertNodeInto(childNode, this, i);
                        tree.expandPath(new TreePath(this.getPath()));
                    }
                    i++;
                }

            }else if(CollectionChangeEvent.ITEM_REMOVED == event.getType()){

                final List<MutableTreeNode> toRemove = new ArrayList<MutableTreeNode>();

                for(final MapItem item : event.getItems()){
                    for(int i=0,n=getChildCount(); i<n;i++){
                        final DefaultMutableTreeNode child = ((DefaultMutableTreeNode)getChildAt(i));
                        if(item.equals( child.getUserObject())){
                            toRemove.add(child);
                        }
                    }
                }

                for(final MutableTreeNode n : toRemove){
                    ((DefaultTreeModel)tree.getModel()).removeNodeFromParent(n);
                }
            }
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {

            if(MapLayer.STYLE_PROPERTY.equals(evt.getPropertyName())){
                //must regenerate style node elements
                resetStructure();
                ((DefaultTreeModel)tree.getModel()).nodeStructureChanged(this);
            }else{
                ((DefaultTreeModel)tree.getModel()).nodeChanged(this);
            }
        }

        private void fillStyleNodes(MapLayer layer){

            final GraphicBuilder gb = layer.getGraphicBuilder(GraphicJ2D.class);

            if(gb != null){
                //this kind of layer have there own style systems we rely on it
                try {
                    final Image img = gb.getLegend(layer);
                    final DefaultMutableTreeNode imgNode = new DefaultMutableTreeNode(img);
                    this.add(imgNode);
                } catch (PortrayalException ex) {
                    Logger.getLogger(JContextTree.class.getName()).log(Level.WARNING, null, ex);
                }

            }else{
                final List<MutableFeatureTypeStyle> ftss = layer.getStyle().featureTypeStyles();
                if(ftss.size() == 1){
                    for(FeatureTypeStyle fts : layer.getStyle().featureTypeStyles()){
                        for(Rule rule : fts.rules()){
                            final DefaultMutableTreeNode ruleNode = new DefaultMutableTreeNode(rule);
                            this.add(ruleNode);
                        }
                    }
                }else{
                    for(FeatureTypeStyle fts : layer.getStyle().featureTypeStyles()){
                        final DefaultMutableTreeNode ftsNode = new DefaultMutableTreeNode(fts);
                        for(Rule rule : fts.rules()){
                            final DefaultMutableTreeNode ruleNode = new DefaultMutableTreeNode(rule);
                            ftsNode.add(ruleNode);
                        }
                        this.add(ftsNode);
                    }
                }

            }
        }

    }

}
