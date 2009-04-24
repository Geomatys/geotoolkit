/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 * 
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.TransferHandler;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.TreePath;

import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.jdesktop.swingx.JXTreeTable;

/**
 * TransferHandler Class used for drag and drop purpose
 * 
 * @author Johann Sorel
 * 
 */
final class DADContextTreeTransferHandler extends TransferHandler {

    
    private ArrayList<DADMetaTransfer> metaTransfers = null;

    /**
     * constructor
     */
    DADContextTreeTransferHandler() {
        super();
    }

    /**
     * create a transferable that contains all paths that are currently selected in
     * a given tree
     * @param c the draged component
     * @return all selected paths in the given tree
     * (or null if the given component is not a tree table)
     * @see javax.swing.TransferHandler#createTransferable(javax.swing.JComponent)
     */
    @Override
    public Transferable createTransferable(JComponent c) {
        metaTransfers = new ArrayList<DADMetaTransfer>();

        Transferable t = null;
        if (c instanceof JXTreeTable) {
            JXTreeTable treeTable = (JXTreeTable) c;

            int[] selectedRows = treeTable.getSelectedRows();
            int nbSelected = selectedRows.length;

            for (int i = 0; i < nbSelected; i++) {

                DADMetaTransfer mt = new DADMetaTransfer();
                mt.dragPath = treeTable.getPathForRow(selectedRows[i]);
                metaTransfers.add(mt);
            }

            if (metaTransfers.size() > 0) {

                for (DADMetaTransfer mt : metaTransfers) {
                    mt.draggedNode = (ContextTreeNode) mt.dragPath.getLastPathComponent();
                    ContextTreeNode parent = (ContextTreeNode) mt.draggedNode.getParent();
                    mt.origine = parent.getIndex(mt.draggedNode);
                    mt.origine_parent = parent.getUserObject();
                }
            }

            t = new DADContextTreeTransferable(metaTransfers);
        }
        return t;
    }

    /**
     * move selected paths when export of drag is done
     * @param source  the component that was the source of the data
     * @param data  the data that was transferred or possibly null if the action is NONE.
     * @param action  the actual action that was performed
     */
    @Override
    protected void exportDone(JComponent source, Transferable data, int action) {

        if (source instanceof JXTreeTable) {
            JXTreeTable treeTable = (JXTreeTable) source;
            ContextTreeModel model = (ContextTreeModel) treeTable.getTreeTableModel();
            TreePath currentPath = treeTable.getPathForRow(treeTable.getSelectedRow());
            if (currentPath != null) {
                addNodes(currentPath, model, data);
            } else {
                insertNodes(treeTable, model, data);
            }
        }

        super.exportDone(source, data, action);
    }

    protected void addNodes(TreePath currentPath, ContextTreeModel model, Transferable data) {
        ContextTreeNode targetNode = (ContextTreeNode) currentPath.getLastPathComponent();

        try {
            List<DADMetaTransfer> metatransfers = (List<DADMetaTransfer>) data.getTransferData(DataFlavor.stringFlavor);

            for (DADMetaTransfer metatransfer : metatransfers) {
                TreePath movedPath = metatransfer.dragPath;
                ContextTreeNode moveNode = (ContextTreeNode) movedPath.getLastPathComponent();

                if (!moveNode.equals(targetNode) && !targetNode.isNodeAncestor(moveNode)) {

                    //deplacement de maplayer dans un mapcontext
                    if ((targetNode.getUserObject() instanceof MapContext) && (moveNode.getUserObject() instanceof MapLayer)) {
                        int place = targetNode.getChildCount();

                        model.removeLayerFromParent(moveNode);
                        model.insertLayerInto(moveNode, targetNode, place);
                    } //deplacement de mapcontext sur mapcontext
                    else if ((targetNode.getUserObject() instanceof MapContext) && (moveNode.getUserObject() instanceof MapContext)) {

                        ContextTreeNode father = (ContextTreeNode) targetNode.getParent();
                        int place = father.getIndex(targetNode);

                        model.moveMapContext(moveNode, father, place);
                    } //deplacement de maplayer sur maplayer
                    else if ((targetNode.getUserObject() instanceof MapLayer) && (moveNode.getUserObject() instanceof MapLayer)) {

                        ContextTreeNode father = (ContextTreeNode) targetNode.getParent();
                        int place = father.getIndex(targetNode);

                        model.removeLayerFromParent(moveNode);
                        model.insertLayerInto(moveNode, father, place);
                    }
                }
            }


        } catch (UnsupportedFlavorException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * insert a number of given nodes
     * @param treeTable the target treetable
     * @param model the model containing the nodes
     * @param data the nodes to insert
     */
    protected void insertNodes(JXTreeTable treeTable, ContextTreeModel model, Transferable data) {

        Point location = ((DADContextTreeDrop) treeTable.getDropTarget()).getMostRecentDragLocation();
        TreePath path = treeTable.getPathForLocation(location.x, location.y);
        ContextTreeNode targetNode = (ContextTreeNode) path.getLastPathComponent();
        ContextTreeNode parent = (ContextTreeNode) targetNode.getParent();
        try {
            List<DADMetaTransfer> metatransfers = (List<DADMetaTransfer>) data.getTransferData(DataFlavor.stringFlavor);

            for (DADMetaTransfer metatransfer : metatransfers) {
                TreePath movedPath = metatransfer.dragPath;
                ContextTreeNode moveNode = (ContextTreeNode) movedPath.getLastPathComponent();

                if (!moveNode.equals(targetNode) && !parent.isNodeAncestor(moveNode)) {

                    // deplacement maplayer dans mapcontext
                    if ((moveNode.getUserObject() instanceof MapLayer) && (parent.getUserObject() instanceof MapContext)) {
                        int place = model.getIndexOfChild(parent, targetNode);

                        if (parent.getUserObject().equals(metatransfer.origine_parent) && place > metatransfer.origine) {
                            place--;
                        }

                        if (moveNode.getParent().equals(parent)) {
                            model.moveLayer(moveNode, parent, place);
                        } else {
                            model.removeLayerFromParent(moveNode);
                            model.insertLayerInto(moveNode, parent, place);
                        }
                    } // delacement de mapcontext
                    else if ((moveNode.getUserObject() instanceof MapContext) && (parent.getUserObject() == null)) {

                        int place = model.getIndexOfChild(parent, targetNode);

                        if (parent.getUserObject() != null) {
                            if (parent.getUserObject().equals(metatransfer.origine_parent) && place > metatransfer.origine) {
                                place--;
                            }
                        }

                        model.moveMapContext(moveNode, parent, place);

                    }
                }
            }
        } catch (UnsupportedFlavorException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the type of transfer actions supported by the source.
     * This transfer handler supports moving of tree nodes so it returns MOVE.
     * @param c the draged component
     * @return TransferHandler.MOVE
     */
    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.MOVE;
    }

    /**
     * get a drag image from the currently dragged node (if any)
     * @param tree  the tree table showing the node
     * @return  the image to draw during drag
     */
    public BufferedImage getDragImage(JXTreeTable tree) {
        BufferedImage image = null;
        try {

            for (DADMetaTransfer mt : metaTransfers) {

                if (mt.dragPath != null) {
                    int row = tree.getRowForPath(mt.dragPath);
                    Rectangle pathBounds = tree.getCellRect(row, 0, false);
                    TableCellRenderer r = tree.getCellRenderer(row, 0);
                    JComponent lbl = (JComponent) r.getTableCellRendererComponent(tree, mt.draggedNode.toString(), false, false, row, 0);
                    lbl.setBounds(pathBounds);
                    image = new BufferedImage((int) pathBounds.getWidth(), (int) pathBounds.getHeight(), java.awt.image.BufferedImage.TYPE_INT_ARGB_PRE);
                    Graphics2D graphics = image.createGraphics();
                    graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                    boolean previousOpaque = lbl.isOpaque();
                    lbl.setOpaque(false);
                    lbl.paint(graphics);
                    lbl.setOpaque(previousOpaque);
                    graphics.dispose();
                }

            }


        } catch (RuntimeException re) {
        }
        return image;
    }
}




