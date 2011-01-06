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
package org.geotoolkit.gui.swing.style;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTree;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.display2d.service.DefaultGlyphService;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.gui.swing.tree.DefaultMutableTreeNode;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.util.RandomStyleFactory;
import org.jdesktop.swingx.JXTree;

import org.opengis.style.Symbolizer;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class JStyleTree<T> extends JXTree implements DragGestureListener, DragSourceListener, DropTargetListener {

    private static final MutableStyleFactory SF = (MutableStyleFactory) FactoryFinder.getStyleFactory(null);

    private static final Icon ICON_STYLE = IconBundle.getIcon("16_style");
    private static final Icon ICON_FTS = IconBundle.getIcon("16_style_fts");
    private static final Icon ICON_RULE = IconBundle.getIcon("16_style_rule");
    private static final Icon ICON_NEW = IconBundle.getIcon("16_add_data");
    private static final Icon ICON_DUPLICATE = IconBundle.getIcon("16_duplicate");
    private static final Icon ICON_DELETE = IconBundle.getIcon("16_delete");
    
    private T style = null;
    private StyleTreeModel<T> treemodel = null;
    /** Variables needed for DnD */
    private DragSource dragSource = null;

    public JStyleTree() {
        super();        
        setModel(treemodel);
        setEditable(false);

        setCellRenderer(new StyleCellRenderer());
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        setComponentPopupMenu(new StylePopup(this));

        dragSource = DragSource.getDefaultDragSource();
        DragGestureRecognizer dgr = dragSource.createDefaultDragGestureRecognizer(this,DnDConstants.ACTION_COPY_OR_MOVE, this);
        dgr.setSourceActions(dgr.getSourceActions() & ~InputEvent.BUTTON3_MASK);
        DropTarget dropTarget = new DropTarget(this, this);
    }

    public T getStyleElement() {
        return style;
    }

    public void setStyleElement(T style) {

        this.style = style;

        if (style != null) {
            treemodel = new StyleTreeModel(style);
            setModel(treemodel);
            revalidate();
        }
        expandAll();
    }

    //-------------Drag & drop -------------------------------------------------
    private StyleElementTransferable dd = null;

    @Override
    public void dragGestureRecognized(DragGestureEvent e) {
        final TreePath path = getSelectionModel().getSelectionPath();
        final DefaultMutableTreeNode dragNode = (DefaultMutableTreeNode) path.getLastPathComponent();

        if (dragNode != null) {
            final Object dragged = dragNode.getUserObject();
            final DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) dragNode.getParent();
            final Object parent = (parentNode != null) ? parentNode.getUserObject() : null;

            dd = new StyleElementTransferable(dragged, parent);
            e.startDrag(null, dd);
        }
    }

    //--------------------drag events-------------------------------------------
    @Override
    public void dragEnter(DragSourceDragEvent dsde) {
    }

    @Override
    public void dragOver(DragSourceDragEvent dsde) {
    }

    @Override
    public void dropActionChanged(DragSourceDragEvent dsde) {
    }

    @Override
    public void dragExit(DragSourceEvent dse) {
    }

    @Override
    public void dragDropEnd(DragSourceDropEvent dsde) {
    }

    //--------------------drop events-------------------------------------------
    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
    }

    @Override
    public void dragOver(DropTargetDragEvent dtde) {
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {
    }

    @Override
    public void dragExit(DropTargetEvent dte) {
    }

    @Override
    public void drop(DropTargetDropEvent dtde) {
        final Point loc = dtde.getLocation();
        final TreePath targetPath = getPathForLocation(loc.x, loc.y);

        final Transferable trs = dd;

        if(!(trs instanceof StyleElementTransferable)) return;        
        final StyleElementTransferable strs = (StyleElementTransferable) trs;


        if (targetPath != null && strs.getStyleElement() != null && strs.getParent() != null) {
            final DefaultMutableTreeNode targetNode = (DefaultMutableTreeNode) targetPath.getLastPathComponent();
            final DefaultMutableTreeNode targetParentNode = (DefaultMutableTreeNode)targetNode.getParent();
            final Object targetObj = targetNode.getUserObject();
            final Object targetParentObj = (targetParentNode != null) ? targetParentNode.getUserObject() : null;

            final Object movedObj = strs.getStyleElement();
            final Object movedParentObj = strs.getParent();

            if(targetObj == movedObj){
                //same object , don't do anything
                return;
            }

            if (targetObj instanceof MutableFeatureTypeStyle && movedObj instanceof MutableFeatureTypeStyle) {

                if(movedParentObj != null){
                    ((MutableStyle)movedParentObj).featureTypeStyles().remove((MutableFeatureTypeStyle)movedObj);
                }

                final MutableFeatureTypeStyle targetFTS = (MutableFeatureTypeStyle) targetObj;
                final int targetIndex = ((MutableStyle)targetParentObj).featureTypeStyles().indexOf(targetFTS);

                ((MutableStyle)targetParentObj).featureTypeStyles().add(targetIndex,(MutableFeatureTypeStyle)movedObj);

            } else if (targetObj instanceof MutableFeatureTypeStyle && movedObj instanceof MutableRule) {

                if (movedParentObj == targetParentObj) {
                    return;
                }

                if(movedParentObj != null){
                    ((MutableFeatureTypeStyle)movedParentObj).rules().remove((MutableRule)movedObj);
                }
                ((MutableFeatureTypeStyle)targetObj).rules().add((MutableRule)movedObj);

            } else if (targetObj instanceof MutableRule && movedObj instanceof MutableRule) {

                if(movedParentObj != null){
                    ((MutableFeatureTypeStyle)movedParentObj).rules().remove((MutableRule)movedObj);
                }

                final MutableRule targetRule = (MutableRule) targetObj;
                final int targetIndex = ((MutableFeatureTypeStyle)targetParentObj).rules().indexOf(targetRule);

                ((MutableFeatureTypeStyle)targetParentObj).rules().add(targetIndex,(MutableRule)movedObj);

            } else if (targetObj instanceof MutableRule && movedObj instanceof Symbolizer) {

                if(movedParentObj != null){
                    ((MutableRule)movedParentObj).symbolizers().remove((Symbolizer)movedObj);
                }

                ((MutableRule)targetObj).symbolizers().add((Symbolizer)movedObj);

            } else if (targetObj instanceof Symbolizer && movedObj instanceof Symbolizer) {

                if(movedParentObj != null){
                    ((MutableRule)movedParentObj).symbolizers().remove((Symbolizer)movedObj);
                }

                final Symbolizer targetSymbol = (Symbolizer) targetObj;
                final int targetIndex = ((MutableRule)targetParentObj).symbolizers().indexOf(targetSymbol);

                ((MutableRule)targetParentObj).symbolizers().add(targetIndex,(Symbolizer)movedObj);

            }

        }

    }

    //-------------private classes----------------------------------------------
    class StyleCellRenderer extends DefaultTreeCellRenderer {
        
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected,
                boolean expanded, boolean leaf, int row, boolean hasFocus) {
            final Component comp = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

            if (comp instanceof JLabel) {
                final JLabel lbl = (JLabel) comp;
                final DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                final Object val = node.getUserObject();

                if (val instanceof MutableStyle) {
                    final MutableStyle style = (MutableStyle) val;
                    lbl.setText(style.getDescription().getTitle().toString());
                    lbl.setIcon(ICON_STYLE);
                } else if (val instanceof MutableFeatureTypeStyle) {
                    final MutableFeatureTypeStyle fts = (MutableFeatureTypeStyle) val;
                    lbl.setText(fts.getDescription().getTitle().toString());
                    lbl.setIcon(ICON_FTS);
                } else if (val instanceof MutableRule) {
                    final MutableRule r = (MutableRule) val;
                    lbl.setText(r.getDescription().getTitle().toString());
                    lbl.setIcon(ICON_RULE);
                } else if (val instanceof Symbolizer) {
                    final Symbolizer symb = (Symbolizer) val;
                    final BufferedImage img = new BufferedImage(30, 22, BufferedImage.TYPE_INT_ARGB);
                    DefaultGlyphService.render(symb, new Rectangle(30,22),img.createGraphics(),null);
                    final Icon ico = new ImageIcon(img);
                    lbl.setText("");
                    lbl.setIcon(ico);
                }
            }
            return comp;
        }
    }

    class StylePopup extends JPopupMenu {

        private final JTree tree;

        StylePopup(JTree tree) {
            super();
            this.tree = tree;
        }

        @Override
        public void setVisible(boolean visible) {
            final TreePath path = tree.getSelectionModel().getSelectionPath();

            if (path != null && visible) {
                removeAll();

                final DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                final Object val = node.getUserObject();

                if (val instanceof MutableStyle) {
                    final MutableStyle style = (MutableStyle) val;
                    add(new NewFTSAction(style));
                    add(new JSeparator(SwingConstants.HORIZONTAL));
                    add(new ExpandAction(node));
                    add(new CollapseAction(node));
                } else if (val instanceof MutableFeatureTypeStyle) {
                    final MutableFeatureTypeStyle fts = (MutableFeatureTypeStyle) val;
                    add(new NewRuleAction(fts));
                    add(new JSeparator(SwingConstants.HORIZONTAL));
                    add(new ExpandAction(node));
                    add(new CollapseAction(node));
                    add(new ChangeRuleScaleAction(fts));
                    add(new JSeparator(SwingConstants.HORIZONTAL));
                    add(new DuplicateAction(node));
                } else if (val instanceof MutableRule) {
                    final MutableRule rule = (MutableRule) val;
                    add(new NewPointSymbolizerAction(rule));
                    add(new NewLineSymbolizerAction(rule));
                    add(new NewPolygonSymbolizerAction(rule));
                    add(new NewRasterSymbolizerAction(rule));
                    add(new NewTextSymbolizerAction(rule));
                    add(new JSeparator(SwingConstants.HORIZONTAL));
                    add(new ExpandAction(node));
                    add(new CollapseAction(node));
                    add(new JSeparator(SwingConstants.HORIZONTAL));
                    add(new DuplicateAction(node));
                } else if (val instanceof Symbolizer) {
                    final Symbolizer symb = (Symbolizer) val;
                    add(new DuplicateAction(node));
                }
                                
                if(treemodel.isDeletable(node)){
                    add(new JSeparator(SwingConstants.HORIZONTAL));
                    add(new DeleteAction(node));
                }
            }

            super.setVisible(visible);
        }
    }

    class CollapseAction extends AbstractAction{
        private final DefaultMutableTreeNode parentNode;

        CollapseAction(DefaultMutableTreeNode node) {
            super("Collapse sub nodes.");
            this.parentNode = node;
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            for(int i=0,n=parentNode.getChildCount(); i<n; i++){
                collapsePath(new TreePath(treemodel.getPathToRoot(parentNode.getChildAt(i))));
            }
        }
    }

    class ExpandAction extends AbstractAction{
        private final DefaultMutableTreeNode parentNode;

        ExpandAction(DefaultMutableTreeNode node) {
            super("Expand sub nodes.");
            this.parentNode = node;
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            for(int i=0,n=parentNode.getChildCount(); i<n; i++){
                final TreeNode child = parentNode.getChildAt(i);
                for(int k=0,l=child.getChildCount(); k<l; k++){
                    expandPath(new TreePath(treemodel.getPathToRoot(child.getChildAt(k))));
                }
            }
        }
    }

    class ChangeRuleScaleAction extends AbstractAction{
        private final MutableFeatureTypeStyle fts;

        ChangeRuleScaleAction(MutableFeatureTypeStyle cdt) {
            super("Change rules valid scale.");
            this.fts = cdt;
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            final JPanel pan = new JPanel();
            pan.add(new JLabel(" Min scale : "));
            final JSpinner spiMin = new JSpinner(new SpinnerNumberModel());
            spiMin.setPreferredSize(new Dimension(150, spiMin.getPreferredSize().height));
            pan.add(spiMin);
            pan.add(new JLabel(" Max scale : "));
            final JSpinner spiMax = new JSpinner(new SpinnerNumberModel());
            spiMax.setPreferredSize(new Dimension(150, spiMax.getPreferredSize().height));
            spiMax.setValue(Double.MAX_VALUE);
            pan.add(spiMax);

            final JOptionPane jop = new JOptionPane(pan);
            final JDialog dialog = jop.createDialog("Change scale");
            dialog.pack();
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);

            final double min = ((Number)spiMin.getValue()).doubleValue();
            final double max = ((Number)spiMax.getValue()).doubleValue();

            for(MutableRule rule : fts.rules()){
                rule.setMinScaleDenominator(min);
                rule.setMaxScaleDenominator(max);
            }
        }
    }

    class NewFTSAction extends AbstractAction{
        private final MutableStyle style;

        NewFTSAction(MutableStyle cdt) {
            super("new FTS",ICON_NEW);
            this.style = cdt;
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            style.featureTypeStyles().add(SF.featureTypeStyle(RandomStyleFactory.createPointSymbolizer()));
        }
    }

    class NewRuleAction extends AbstractAction{
        private final MutableFeatureTypeStyle fts;

        NewRuleAction(MutableFeatureTypeStyle cdt) {
            super("new Rule",ICON_NEW);
            this.fts = cdt;
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            fts.rules().add(SF.rule(RandomStyleFactory.createPointSymbolizer()));
        }
    }

    class NewPointSymbolizerAction extends AbstractAction {
        private final MutableRule rule;

        NewPointSymbolizerAction(MutableRule cdt) {
            super("Point Symbolizer",ICON_NEW);
            this.rule = cdt;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            rule.symbolizers().add(RandomStyleFactory.createPointSymbolizer());
        }
    }

    class NewLineSymbolizerAction extends AbstractAction {
        private final MutableRule rule;

        NewLineSymbolizerAction(MutableRule cdt) {
            super("Line Symbolizer", ICON_NEW);
            this.rule = cdt;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            rule.symbolizers().add(RandomStyleFactory.createLineSymbolizer());
        }
    }

    class NewPolygonSymbolizerAction extends AbstractAction {
        private final MutableRule rule;

        NewPolygonSymbolizerAction(MutableRule cdt) {
            super("Polygon Symbolizer",ICON_NEW);
            this.rule = cdt;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            rule.symbolizers().add(RandomStyleFactory.createPolygonSymbolizer());
        }
    }
    
    class NewTextSymbolizerAction extends AbstractAction {
        private final MutableRule rule;

        NewTextSymbolizerAction(MutableRule cdt) {
            super("Text Symbolizer", ICON_NEW);
            this.rule = cdt;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            rule.symbolizers().add(SF.textSymbolizer());
        }
    }

    class NewRasterSymbolizerAction extends AbstractAction {
        private final MutableRule rule;

        NewRasterSymbolizerAction(MutableRule cdt) {
            super("Raster Symbolizer", ICON_NEW);
            this.rule = cdt;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            rule.symbolizers().add(SF.rasterSymbolizer());
        }
    }

    class DuplicateAction extends AbstractAction {
        private final DefaultMutableTreeNode parentNode;

        DuplicateAction(DefaultMutableTreeNode node) {
            super("Duplicate", ICON_DUPLICATE);
            this.parentNode = node;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            treemodel.duplicateNode(parentNode);
        }
    }

    class DeleteAction extends AbstractAction {
        private final DefaultMutableTreeNode parentNode;

        DeleteAction(DefaultMutableTreeNode node) {
            super("Delete",ICON_DELETE);
            this.parentNode = node;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            treemodel.deleteNode(parentNode);
        }
    }

    private static class StyleElementTransferable implements Transferable{

        private final Object styleElement;
        private final Object parent;

        public StyleElementTransferable(Object styleElement, Object parent){
            this.styleElement = styleElement;
            this.parent = parent;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[0];
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor df) {
            return true;
        }

        @Override
        public Object getTransferData(DataFlavor df) throws UnsupportedFlavorException, IOException {
            return styleElement;
        }

        public Object getStyleElement(){
            return styleElement;
        }

        public Object getParent(){
            return parent;
        }

    }

}
