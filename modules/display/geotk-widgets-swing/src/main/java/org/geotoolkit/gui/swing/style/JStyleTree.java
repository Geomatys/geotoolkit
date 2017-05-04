/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Johann Sorel
 *    (C) 2009 - 2014, Geomatys
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
import java.util.List;
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
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.geotoolkit.display2d.service.DefaultGlyphService;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.font.FontAwesomeIcons;
import org.geotoolkit.font.IconBuilder;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.style.RandomStyleBuilder;
import org.jdesktop.swingx.JXTree;
import org.opengis.style.Description;
import org.opengis.style.Symbolizer;
import org.opengis.util.InternationalString;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module
 */
public class JStyleTree<T> extends JXTree implements DragGestureListener, DragSourceListener, DropTargetListener {

    private static final MutableStyleFactory SF = (MutableStyleFactory) FactoryFinder.getStyleFactory(null);

    public static final ImageIcon ICON_STYLE     = IconBuilder.createIcon(FontAwesomeIcons.ICON_BOOK,16,FontAwesomeIcons.DEFAULT_COLOR);
    public static final ImageIcon ICON_FTS       = IconBuilder.createIcon(FontAwesomeIcons.ICON_TAG,16,FontAwesomeIcons.DEFAULT_COLOR);
    public static final ImageIcon ICON_RULE      = IconBuilder.createIcon(FontAwesomeIcons.ICON_FILTER,16,FontAwesomeIcons.DEFAULT_COLOR);
    public static final ImageIcon ICON_NEW       = IconBuilder.createIcon(FontAwesomeIcons.ICON_PLUS,16,FontAwesomeIcons.DEFAULT_COLOR);
    public static final ImageIcon ICON_DUPLICATE = IconBuilder.createIcon(FontAwesomeIcons.ICON_FILES_O,16,FontAwesomeIcons.DEFAULT_COLOR);
    public static final ImageIcon ICON_DELETE    = IconBuilder.createIcon(FontAwesomeIcons.ICON_TRASH_O,16,FontAwesomeIcons.DEFAULT_COLOR);

    private T style = null;
    private final StyleTreeModel treemodel = new StyleTreeModel(null);
    /** Variables needed for DnD */
    private DragSource dragSource = null;

    public JStyleTree() {
        super();
        setModel(treemodel);
        setEditable(false);

        setCellRenderer(new StyleCellRenderer());
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        setComponentPopupMenu(new StylePopup());

        dragSource = DragSource.getDefaultDragSource();
        DragGestureRecognizer dgr = dragSource.createDefaultDragGestureRecognizer(this,DnDConstants.ACTION_COPY_OR_MOVE, this);
        dgr.setSourceActions(dgr.getSourceActions() & ~InputEvent.BUTTON3_MASK);
        DropTarget dropTarget = new DropTarget(this, this);

    }

    public T getStyleElement() {
        return style;
    }

    public void setStyleElement(final T style) {

        this.style = style;

        if (style != null) {
            treemodel.setRoot(style);
            revalidate();
        }

        expandAll();
    }

    //-------------Drag & drop -------------------------------------------------
    private StyleElementTransferable dd = null;

    @Override
    public void dragGestureRecognized(final DragGestureEvent e) {
        final TreePath path = getSelectionModel().getSelectionPath();
        final Object[] pathObjs = path.getPath();
        final Object dragged = path.getLastPathComponent();
        if (dragged != null) {
            final Object parent = (pathObjs.length>1) ? pathObjs[pathObjs.length-2] : null;
            dd = new StyleElementTransferable(dragged, parent);
            e.startDrag(null, dd);
        }
    }

    //--------------------drag events-------------------------------------------
    @Override
    public void dragEnter(final DragSourceDragEvent dsde) {
    }

    @Override
    public void dragOver(final DragSourceDragEvent dsde) {
    }

    @Override
    public void dropActionChanged(final DragSourceDragEvent dsde) {
    }

    @Override
    public void dragExit(final DragSourceEvent dse) {
    }

    @Override
    public void dragDropEnd(final DragSourceDropEvent dsde) {
    }

    //--------------------drop events-------------------------------------------
    @Override
    public void dragEnter(final DropTargetDragEvent dtde) {
    }

    @Override
    public void dragOver(final DropTargetDragEvent dtde) {
    }

    @Override
    public void dropActionChanged(final DropTargetDragEvent dtde) {
    }

    @Override
    public void dragExit(final DropTargetEvent dte) {
    }

    @Override
    public void drop(final DropTargetDropEvent dtde) {
        final Point loc = dtde.getLocation();
        final TreePath targetPath = getPathForLocation(loc.x, loc.y);

        final Transferable trs = dd;

        if(!(trs instanceof StyleElementTransferable)) return;
        final StyleElementTransferable strs = (StyleElementTransferable) trs;


        if (targetPath != null && strs.getStyleElement() != null && strs.getParent() != null) {
            final Object[] pathObjs = targetPath.getPath();
            final Object targetObj = targetPath.getLastPathComponent();
            final Object targetParentObj =  (pathObjs.length>1) ? pathObjs[pathObjs.length-2] : null;

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

    private static boolean isDeletable(final Object removeObject){
        return     removeObject instanceof MutableFeatureTypeStyle
                || removeObject instanceof MutableRule
                || removeObject instanceof Symbolizer;
    }


    //-------------private classes----------------------------------------------
    class StyleCellRenderer extends DefaultTreeCellRenderer {

        @Override
        public Component getTreeCellRendererComponent(final JTree tree, final Object value, final boolean selected,
                final boolean expanded, final boolean leaf, final int row, final boolean hasFocus) {
            final Component comp = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

            if (comp instanceof JLabel) {
                final JLabel lbl = (JLabel) comp;
                final Object val = value;

                if (val instanceof MutableStyle) {
                    final MutableStyle style = (MutableStyle) val;
                    lbl.setText(text(style.getDescription()));
                    lbl.setIcon(ICON_STYLE);
                } else if (val instanceof MutableFeatureTypeStyle) {
                    final MutableFeatureTypeStyle fts = (MutableFeatureTypeStyle) val;
                    lbl.setText(text(fts.getDescription()));
                    lbl.setIcon(ICON_FTS);
                } else if (val instanceof MutableRule) {
                    final MutableRule r = (MutableRule) val;
                    lbl.setText(text(r.getDescription()));
                    lbl.setIcon(ICON_RULE);
                } else if (val instanceof Symbolizer) {
                    final Symbolizer symb = (Symbolizer) val;
                    final Dimension dim = DefaultGlyphService.glyphPreferredSize(symb, null, null);
                    final BufferedImage img = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB);
                    DefaultGlyphService.render(symb, new Rectangle(dim),img.createGraphics(),null);
                    final Icon ico = new ImageIcon(img);
                    lbl.setText(symb.getName()==null?"":symb.getName());
                    lbl.setIcon(ico);
                }
            }
            return comp;
        }

        private String text(Description desc){
            if(desc == null){
                return " - ";
            }else{
                final InternationalString str = desc.getTitle();
                if(str != null && !str.toString().trim().isEmpty()){
                    return str.toString();
                }else{
                    return " - ";
                }
            }
        }
    }

    private class StylePopup extends JPopupMenu {

        StylePopup() {
            super();
        }

        @Override
        public void setVisible(final boolean visible) {
            TreePath path = JStyleTree.this.getSelectionModel().getSelectionPath();

            final Point mousePosition = JStyleTree.this.getMousePosition();
            if(mousePosition != null){
                final int rowIndex = getRowForLocation(mousePosition.x, mousePosition.y);
                if(rowIndex>=0){
                    path = JStyleTree.this.getPathForRow(rowIndex);
                }
            }


            if (path != null && visible) {
                removeAll();

                final Object val = path.getLastPathComponent();

                if (val instanceof MutableStyle) {
                    final MutableStyle style = (MutableStyle) val;
                    add(new NewFTSAction(style));
                    add(new JSeparator(SwingConstants.HORIZONTAL));
                    add(new ExpandAction(path));
                    add(new CollapseAction(path));
                } else if (val instanceof MutableFeatureTypeStyle) {
                    final MutableFeatureTypeStyle fts = (MutableFeatureTypeStyle) val;
                    add(new NewRuleAction(fts));
                    add(new JSeparator(SwingConstants.HORIZONTAL));
                    add(new ExpandAction(path));
                    add(new CollapseAction(path));
                    add(new ChangeRuleScaleAction(fts));
                    add(new JSeparator(SwingConstants.HORIZONTAL));
                    add(new DuplicateAction(path));
                } else if (val instanceof MutableRule) {
                    final MutableRule rule = (MutableRule) val;
                    final List<StyleElementEditor> editors = StyleElementEditor.findEditorsForType(Symbolizer.class);
                    for(StyleElementEditor editor : editors){
                        add(new NewSymbolizerAction(rule,editor));
                    }
                    add(new JSeparator(SwingConstants.HORIZONTAL));
                    add(new ExpandAction(path));
                    add(new CollapseAction(path));
                    add(new JSeparator(SwingConstants.HORIZONTAL));
                    add(new DuplicateAction(path));
                } else if (val instanceof Symbolizer) {
                    add(new DuplicateAction(path));
                }

                if(isDeletable(path.getLastPathComponent())){
                    add(new JSeparator(SwingConstants.HORIZONTAL));
                    add(new DeleteAction(path));
                }
            }

            super.setVisible(visible);
        }
    }

    class CollapseAction extends AbstractAction{
        private final TreePath path;

        CollapseAction(final TreePath path) {
            super("Collapse sub nodes.");
            this.path = path;
        }

        @Override
        public void actionPerformed(final ActionEvent ae) {
            final Object parent = path.getLastPathComponent();
            for(int i=0,n=treeModel.getChildCount(parent); i<n; i++){
                final Object child = treemodel.getChild(parent, i);
                collapsePath(path.pathByAddingChild(child));
            }
        }
    }

    class ExpandAction extends AbstractAction{
        private final TreePath path;

        ExpandAction(final TreePath path) {
            super("Expand sub nodes.");
            this.path = path;
        }

        @Override
        public void actionPerformed(final ActionEvent ae) {
            final Object parent = path.getLastPathComponent();
            for(int i=0,n=treeModel.getChildCount(parent); i<n; i++){
                final Object child = treemodel.getChild(parent, i);
                final TreePath tp1 = path.pathByAddingChild(child);
                expandPath(tp1);
                for(int k=0,l=treeModel.getChildCount(child); k<l; k++){
                    expandPath(tp1.pathByAddingChild(treemodel.getChild(child, k)));
                }
            }
        }
    }

    class ChangeRuleScaleAction extends AbstractAction{
        private final MutableFeatureTypeStyle fts;

        ChangeRuleScaleAction(final MutableFeatureTypeStyle cdt) {
            super("Change rules valid scale.");
            this.fts = cdt;
        }

        @Override
        public void actionPerformed(final ActionEvent ae) {
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
            final JDialog dialog = jop.createDialog(JStyleTree.this,"Change scale");
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

        NewFTSAction(final MutableStyle cdt) {
            super("new FTS",ICON_NEW);
            this.style = cdt;
        }

        @Override
        public void actionPerformed(final ActionEvent ae) {
            style.featureTypeStyles().add(SF.featureTypeStyle(RandomStyleBuilder.createRandomPointSymbolizer()));
        }
    }

    class NewRuleAction extends AbstractAction{
        private final MutableFeatureTypeStyle fts;

        NewRuleAction(final MutableFeatureTypeStyle cdt) {
            super("new Rule",ICON_NEW);
            this.fts = cdt;
        }

        @Override
        public void actionPerformed(final ActionEvent ae) {
            fts.rules().add(SF.rule(RandomStyleBuilder.createRandomPointSymbolizer()));
        }
    }

    class NewSymbolizerAction extends AbstractAction{
        private final MutableRule rule;
        private final StyleElementEditor editor;

        NewSymbolizerAction(final MutableRule cdt, final StyleElementEditor editor) {
            super(editor.getEditedClass().getSimpleName(),ICON_NEW);
            this.rule = cdt;
            this.editor = editor;
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            rule.symbolizers().add((Symbolizer)editor.create());
        }
    }

    class DuplicateAction extends AbstractAction {
        private final TreePath path;

        DuplicateAction(final TreePath path) {
            super("Duplicate", ICON_DUPLICATE);
            this.path = path;
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            final Object[] pathObjs = path.getPath();
            if(pathObjs.length>1){
                treemodel.duplicateNode(pathObjs[pathObjs.length-2],pathObjs[pathObjs.length-1]);
            }
        }
    }

    class DeleteAction extends AbstractAction {
        private final TreePath path;

        DeleteAction(final TreePath path) {
            super("Delete",ICON_DELETE);
            this.path = path;
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            final Object[] pathObjs = path.getPath();
            if(pathObjs.length>1){
                treemodel.removeChild(pathObjs[pathObjs.length-2],pathObjs[pathObjs.length-1]);
            }
        }
    }

    private static class StyleElementTransferable implements Transferable{

        private final Object styleElement;
        private final Object parent;

        public StyleElementTransferable(final Object styleElement, final Object parent){
            this.styleElement = styleElement;
            this.parent = parent;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[0];
        }

        @Override
        public boolean isDataFlavorSupported(final DataFlavor df) {
            return true;
        }

        @Override
        public Object getTransferData(final DataFlavor df) throws UnsupportedFlavorException, IOException {
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
