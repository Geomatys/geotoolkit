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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.image.BufferedImage;

import javax.swing.tree.TreePath;

import org.geotoolkit.map.MapContext;
import org.jdesktop.swingx.JXTreeTable;

/**
 * Drop Class used for drag and drop purpose
 * 
 * @author Johann Sorel
 */
final class DADContextTreeDrop extends DropTarget {

    /* ----------------- class fields ------------------ */

    /** bounding rectangle of the last row a dragOver was recorded for */
    private Rectangle lastRowBounds;

    /** height of the gap between any two node rows to treat as an area for inserts */
    private int insertAreaHeight = 8;

    /** insets for autoscroll */
    private Insets autoscrollInsets = new Insets(20, 20, 20, 20);

    /** rectangle to clear (where the last image was drawn) */
    private Rectangle rect2D = new Rectangle();

    /** the transfer handler that provides the image for the currently dragged node */
    private DADContextTreeTransferHandler handler;

    private Point mostRecentLocation;
    
    
    /**
     * constructor
     * @param h handler for drop purpose.
     */
    DADContextTreeDrop(DADContextTreeTransferHandler h) {
        this.handler = h;
    }

    /* -------------- DropTargetListener start ----------------- */

    /**
     * use method dragOver to constantly update the drag mark and darg image as
     * well as to support automatic scrolling durng a drag operation
     * @param dtde drop target event
     */
    @Override
    public void dragOver(DropTargetDragEvent dtde) {
        Point loc = dtde.getLocation();
        JXTreeTable tree = (JXTreeTable) dtde.getDropTargetContext().getComponent();
        updateDragMark(tree, loc);
        paintImage(tree, loc);
        autoscroll(tree, loc);
        super.dragOver(dtde);
    }

    /**
     * clear the drawings on exit
     * @param dtde drage event
     */
    public void dragExit(DropTargetDragEvent dtde) {
        clearImage((JXTreeTable) dtde.getDropTargetContext().getComponent());
        super.dragExit(dtde);
    }

    /**
     * clear the drawings on drop
     * @param dtde drag event
     */
    @Override
    public void drop(DropTargetDropEvent dtde) {
        clearImage((JXTreeTable) dtde.getDropTargetContext().getComponent());
        super.drop(dtde);
    }

    /* ----------------- DropTartgetListener end ------------------ */

    /* ----------------- drag image painting start ------------------ */

    /**
     * paint the dragged node
     * @param tree 
     * @param pt 
     */
    private final void paintImage(JXTreeTable tree, Point pt) {
        BufferedImage image = handler.getDragImage(tree);
        if (image != null) {
            tree.paintImmediately(rect2D.getBounds());
            rect2D.setRect((int) pt.getX() - 15, (int) pt.getY() - 15, image.getWidth(), image.getHeight());
            tree.getGraphics().drawImage(image, (int) pt.getX() - 15, (int) pt.getY() - 15, tree);
        }
    }

    /**
     * clear drawings
     * @param tree 
     */
    private final void clearImage(JXTreeTable tree) {
        tree.paintImmediately(rect2D.getBounds());
    }

    /* ----------------- drag image painting end ------------------ */

    /* ----------------- autoscroll implementation start ------------------ */

    private Insets getAutoscrollInsets() {
        return autoscrollInsets;
    }

    /**
     * scroll visible tree parts when user drags outside an 'inner part' of
     * the visible region
     * @param tree 
     * @param cursorLocation 
     */
    private void autoscroll(JXTreeTable tree, Point cursorLocation) {
        Insets insets = getAutoscrollInsets();
        Rectangle outer = tree.getVisibleRect();
        Rectangle inner = new Rectangle(outer.x + insets.left, outer.y + insets.top, outer.width - (insets.left + insets.right), outer.height - (insets.top + insets.bottom));
        if (!inner.contains(cursorLocation)) {
            Rectangle scrollRect = new Rectangle(cursorLocation.x - insets.left, cursorLocation.y - insets.top, insets.left + insets.right, insets.top + insets.bottom);
            tree.scrollRectToVisible(scrollRect);
        }
    }

    /* ----------------- autoscroll implementation end ------------------ */

    /* ----------------- insertion mark painting start ------------------ */

    /**
     * manage display of a drag mark either highlighting a node or drawing an
     * insertion mark
     * @param tree associate jxtreetable
     * @param location drop position
     */
    public void updateDragMark(JXTreeTable tree, Point location) {
        mostRecentLocation = location;
        int row = tree.getRowForPath(tree.getPathForLocation(location.x, location.y));
        TreePath path = tree.getPathForRow(row);
        if (path != null) {
            Rectangle rowBounds = tree.getCellRect(row, 0, false); // tree.getPathBounds(path);
            /*
             * find out if we have to mark a tree node or if we
             * have to draw an insertion marker
             */
            int rby = rowBounds.y;
            int topBottomDist = insertAreaHeight / 2;
            // x = top, y = bottom of insert area
            Point topBottom = new Point(rby - topBottomDist, rby + topBottomDist);
            if (topBottom.x <= location.y && topBottom.y >= location.y) {
                // we are inside an insertArea
                paintInsertMarker(tree, location);
            } else {
                // we are inside a node
                markNode(tree, location);
            }
        }
    }

    /**
     * get the most recent mouse location, i.e. the drop location when called upon drop
     * @return the mouse location recorded most recently during a drag operation
     */
    public Point getMostRecentDragLocation() {
        return mostRecentLocation;
    }

    /**
     * mark the node that is closest to the current mouse location
     * @param tree 
     * @param location 
     */
    private void markNode(JXTreeTable tree, Point location) {
        TreePath path = tree.getPathForLocation(location.x, location.y);
        int row = tree.getRowForPath(path);
        if (path != null) {
            if (lastRowBounds != null) {
                Graphics g = tree.getGraphics();
                g.setColor(Color.white);
                g.drawLine(lastRowBounds.x, lastRowBounds.y, lastRowBounds.x + lastRowBounds.width, lastRowBounds.y);
            }
            tree.setRowSelectionInterval(row, row);
            //tree.setSelectionPath(path);
            
            
            //only expand subnode if its a mapcontext node
            Object node = path.getLastPathComponent();
            if(node instanceof ContextTreeNode){
                Object obj = ((ContextTreeNode)node).getUserObject();
                if(obj instanceof MapContext){
                    tree.expandPath(path);
                }
            }
        }
    }

    /**
     * paint an insert marker between the nodes closest to the current mouse location
     * @param tree 
     * @param location 
     */
    private void paintInsertMarker(JXTreeTable tree, Point location) {
        Graphics g = tree.getGraphics();
        tree.clearSelection();
        int row = tree.getRowForPath(tree.getPathForLocation(location.x, location.y));
        TreePath path = tree.getPathForRow(row);
        if (path != null) {
            Rectangle rowBounds = tree.getCellRect(row, 0, false); //tree.getPathBounds(path);
            if (lastRowBounds != null) {
                g.setColor(Color.white);
                g.drawLine(lastRowBounds.x, lastRowBounds.y, lastRowBounds.x + lastRowBounds.width, lastRowBounds.y);
            }
            if (rowBounds != null) {
                g.setColor(Color.black);
                g.drawLine(rowBounds.x, rowBounds.y, rowBounds.x + rowBounds.width, rowBounds.y);
            }
            lastRowBounds = rowBounds;
        }
    }

    /* ----------------- insertion mark painting end ------------------ */


}
