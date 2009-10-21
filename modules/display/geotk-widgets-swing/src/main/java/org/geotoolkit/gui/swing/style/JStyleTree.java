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
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
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
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.util.Enumeration;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.geotoolkit.gui.swing.resource.IconBundle;

import org.geotoolkit.display2d.service.DefaultGlyphService;
import org.geotoolkit.gui.swing.tree.DefaultMutableTreeNode;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.MutableStyle;

import org.opengis.style.Symbolizer;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class JStyleTree extends JTree implements DragGestureListener, DragSourceListener, DropTargetListener {

    private static final Icon ICON_STYLE = IconBundle.getInstance().getIcon("16_style");
    private static final Icon ICON_FTS = IconBundle.getInstance().getIcon("16_style_fts");
    private static final Icon ICON_RULE = IconBundle.getInstance().getIcon("16_style_rule");
    private static final Icon ICON_NEW = IconBundle.getInstance().getIcon("16_add_data");
    private static final Icon ICON_DUPLICATE = IconBundle.getInstance().getIcon("16_duplicate");
    private static final Icon ICON_DELETE = IconBundle.getInstance().getIcon("16_delete");
    
    private MutableStyle style = null;
    private StyleTreeModel treemodel = null;
    /** Variables needed for DnD */
    private DragSource dragSource = null;

    public JStyleTree() {
        super();
        
        putClientProperty("JTree.lineStyle", "Angled");
        
        setModel(treemodel);
        setEditable(false);

        StyleCellRenderer renderer = new StyleCellRenderer();
        setCellRenderer(renderer);
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        setComponentPopupMenu(new StylePopup(this));


        dragSource = DragSource.getDefaultDragSource();

        DragGestureRecognizer dgr = dragSource.createDefaultDragGestureRecognizer(this,DnDConstants.ACTION_COPY_OR_MOVE, this);

        dgr.setSourceActions(dgr.getSourceActions() & ~InputEvent.BUTTON3_MASK);
        DropTarget dropTarget = new DropTarget(this, this);

    }

    private void parseStyle() {
        if (style != null) {
            treemodel = new StyleTreeModel(style);
            setModel(treemodel);
            revalidate();
        }
        expandAll(new TreePath(getModel().getRoot()),true);
    }

    private void expandAll(TreePath parent, boolean expand) {
        // Traverse children
        TreeNode node = (TreeNode)parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration e=node.children(); e.hasMoreElements(); ) {
                TreeNode n = (TreeNode)e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                expandAll(path, expand);
            }
        }

        // Expansion or collapse must be done bottom-up
        if (expand) {
            expandPath(parent);
        } else {
            collapsePath(parent);
        }
    }

    public MutableStyle getStyle() {
        return style;
    }

    public void setStyle(MutableStyle style) {
        this.style = style;
        parseStyle();
    }

    //-------------Drag & drop -------------------------------------------------
    @Override
    public void dragGestureRecognized(DragGestureEvent e) {

        TreePath path = getSelectionModel().getSelectionPath();
        DefaultMutableTreeNode dragNode = (DefaultMutableTreeNode) path.getLastPathComponent();

        if (dragNode != null) {
            Transferable transferable = new StringSelection("");

            e.startDrag(null, transferable);
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

        TreePath originPath = getSelectionModel().getSelectionPath();
        Point loc = dtde.getLocation();
        TreePath targetPath = getPathForLocation(loc.x, loc.y);


        if (targetPath != null && originPath != null) {
            DefaultMutableTreeNode dragNode = (DefaultMutableTreeNode) originPath.getLastPathComponent();
            DefaultMutableTreeNode targetNode = (DefaultMutableTreeNode) targetPath.getLastPathComponent();

            treemodel.moveNode(dragNode, targetNode);


            DefaultMutableTreeNode dragNodeParent = (DefaultMutableTreeNode) dragNode.getParent();
            DefaultMutableTreeNode oldParent = (DefaultMutableTreeNode) dragNode.getParent();
            Object parentObj = targetNode.getUserObject();
            Transferable trans = dtde.getTransferable();



//            if (parentObj instanceof MutableStyle) {
//                MutableStyle style = (MutableStyle) parentObj;
//                if (trans.isDataFlavorSupported(FLAVOR_FTS)) {
//
//                }
//                dtde.rejectDrop();
//                return;
//            } else if (parentObj instanceof MutableFeatureTypeStyle) {
//                dtde.rejectDrop();
//                return;
//            } else if (parentObj instanceof MutableRule) {
//                MutableRule rule = (MutableRule) parentObj;
//
//                try {
//                    Object obj = trans.getTransferData(FLAVOR_SYMBOL);
//                    if (trans.isDataFlavorSupported(FLAVOR_SYMBOL)) {
//                        Symbolizer symbol = (Symbolizer) obj;
//                        Symbolizer[] symls = rule.symbolizers();
//                        Symbolizer[] nsymls = new Symbolizer[symls.length + 1];
//
//                        for (int i = 0; i < symls.length; i++) {
//                            nsymls[i] = symls[i];
//                        }
//
//                        nsymls[nsymls.length - 1] = symbol;
//
//                        rule.symbolizers(nsymls);
//                    } else {
//                        dtde.rejectDrop();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            } else if (parentObj instanceof Symbolizer) {
//                if (parentObj instanceof MutableRule) {
//
//                    dragNodeParent.remove(dragNode);
//                }
//            } else {
//
//            }

            parseStyle();
            expandAll(new TreePath(getModel().getRoot()),true);
        }


    }


    //-------------private classes----------------------------------------------
    class StyleCellRenderer extends DefaultTreeCellRenderer {
        
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

            Component comp = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

            if (comp instanceof JLabel) {
                JLabel lbl = (JLabel) comp;
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                Object val = node.getUserObject();

                if (val instanceof MutableStyle) {
                    MutableStyle style = (MutableStyle) val;
                    lbl.setText(style.getDescription().getTitle().toString());
                    lbl.setIcon(ICON_STYLE);
                } else if (val instanceof MutableFeatureTypeStyle) {
                    MutableFeatureTypeStyle fts = (MutableFeatureTypeStyle) val;
                    lbl.setText(fts.getDescription().getTitle().toString());
                    lbl.setIcon(ICON_FTS);
                } else if (val instanceof MutableRule) {
                    MutableRule r = (MutableRule) val;
                    lbl.setText(r.getDescription().getTitle().toString());
                    lbl.setIcon(ICON_RULE);
                } else if (val instanceof Symbolizer) {
                    Symbolizer symb = (Symbolizer) val;
                    BufferedImage img = new BufferedImage(30, 22, BufferedImage.TYPE_INT_ARGB);
                    DefaultGlyphService.render(symb, new Rectangle(30,22),img.createGraphics());
                    Icon ico = new ImageIcon(img);
                    lbl.setText("");
                    lbl.setIcon(ico);
                }

            }

            return comp;
        }
    }

    class StylePopup extends JPopupMenu {

        private final JTree tree;
        private Object buffer = null;

        StylePopup(JTree tree) {
            super();
            this.tree = tree;
        }

        @Override
        public void setVisible(boolean b) {

            TreePath path = tree.getSelectionModel().getSelectionPath();

            if (path != null && b == true) {
                removeAll();


                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                Object val = node.getUserObject();

                if (val instanceof MutableStyle) {
                    MutableStyle style = (MutableStyle) val;
                    add(new NewFTSItem());
                } else if (val instanceof MutableFeatureTypeStyle) {
                    MutableFeatureTypeStyle fts = (MutableFeatureTypeStyle) val;
                    add(new NewRuleItem(node));
                    add(new JSeparator(SwingConstants.HORIZONTAL));
                    add(new DuplicateItem(node));
                } else if (val instanceof MutableRule) {
                    MutableRule rule = (MutableRule) val;
                    add(new NewPointSymbolizerItem(node));
                    add(new NewLineSymbolizerItem(node));
                    add(new NewPolygonSymbolizerItem(node));
                    add(new NewRasterSymbolizerItem(node));
                    add(new NewTextSymbolizerItem(node));
                    add(new JSeparator(SwingConstants.HORIZONTAL));
                    add(new DuplicateItem(node));
                } else if (val instanceof Symbolizer) {
                    Symbolizer symb = (Symbolizer) val;
                    add(new DuplicateItem(node));
                }
                                
                if(treemodel.isDeletable(node)){
                    add(new JSeparator(SwingConstants.HORIZONTAL));
                    add(new DeleteItem(node));
                }
                
                super.setVisible(b);
            } else {
                super.setVisible(false);
            }
        }
    }

    class NewFTSItem extends JMenuItem {

        NewFTSItem() {
            setText("new FTS");
            setIcon(ICON_NEW);
            addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    treemodel.newFeatureTypeStyle();
                }
            });
        }
    }

    class NewRuleItem extends JMenuItem {

        private final DefaultMutableTreeNode NODE;

        NewRuleItem(DefaultMutableTreeNode node) {

            this.NODE = node;
            setText("New Rule");
            setIcon(ICON_NEW);
            addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    treemodel.newRule(NODE);
                }
            });
        }
    }

    class DuplicateItem extends JMenuItem {

        private final DefaultMutableTreeNode NODE;

        DuplicateItem(DefaultMutableTreeNode node) {
            this.NODE = node;
            setText("Duplicate");
            setIcon(ICON_DUPLICATE);
            addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    DefaultMutableTreeNode copy = treemodel.duplicateNode(NODE);
                }
            });
        }
    }
    
    class DeleteItem extends JMenuItem {
        
        private final DefaultMutableTreeNode NODE;

        DeleteItem(DefaultMutableTreeNode node) {
            this.NODE = node;
            setText("Delete");
            setIcon(ICON_DELETE);
            addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    treemodel.deleteNode(NODE);
                }
            });
        }
        
    }

    class NewPointSymbolizerItem extends JMenuItem {

        private final DefaultMutableTreeNode NODE;

        NewPointSymbolizerItem(DefaultMutableTreeNode node) {
            this.NODE = node;
            setText("Point Symbolizer");
            setIcon(ICON_NEW);
            addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    treemodel.newPointSymbolizer(NODE);
                }
            });
        }
    }

    class NewLineSymbolizerItem extends JMenuItem {

        private final DefaultMutableTreeNode NODE;

        NewLineSymbolizerItem(DefaultMutableTreeNode node) {
            this.NODE = node;
            setText("Line Symbolizer");
            setIcon(ICON_NEW);
            addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    treemodel.newLineSymbolizer(NODE);
                }
            });
        }
    }

    class NewPolygonSymbolizerItem extends JMenuItem {

        private final DefaultMutableTreeNode NODE;

        NewPolygonSymbolizerItem(DefaultMutableTreeNode node) {
            this.NODE = node;
            setText("Polygon Symbolizer");
            setIcon(ICON_NEW);
            addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    treemodel.newPolygonSymbolizer(NODE);
                }
            });
        }
    }
    
    class NewTextSymbolizerItem extends JMenuItem {

        private final DefaultMutableTreeNode NODE;

        NewTextSymbolizerItem(DefaultMutableTreeNode node) {
            this.NODE = node;
            setText("Text Symbolizer");
            setIcon(ICON_NEW);
            addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    treemodel.newTextSymbolizer(NODE);
                    
                }
            });
        }
    }

    class NewRasterSymbolizerItem extends JMenuItem {

        private final DefaultMutableTreeNode NODE;

        NewRasterSymbolizerItem(DefaultMutableTreeNode node) {
            this.NODE = node;
            setText("Raster Symbolizer");
            setIcon(ICON_NEW);
            addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    treemodel.newRasterSymbolizer(NODE);
                }
            });
        }
    }
    
}
