/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.image.io.mosaic;

import java.awt.*;
import javax.swing.*;
import java.io.IOException;
import java.awt.geom.AffineTransform;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import org.geotoolkit.internal.GraphicsUtilities;
import org.geotoolkit.gui.swing.tree.TreeNode;


/**
 * A panel showing the content of a {@link TreeTileManager}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
@SuppressWarnings("serial")
final class TreeTileManagerViewer extends JPanel implements TreeSelectionListener {
    /**
     * The tile manager.
     */
    private final TreeTileManager tiles;

    /**
     * The region enclosing all tiles.
     */
    private final Rectangle bounds;

    /**
     * The currently selected tile.
     */
    private TreeNode selected;

    /**
     * Creates a viewer for the given manager.
     *
     * @throws IOException if an I/O operation was required and failed.
     */
    public TreeTileManagerViewer(final TreeTileManager tiles) throws IOException {
        this.tiles = tiles;
        this.bounds = tiles.getRegion();
    }

    /**
     * Invoked when the tree selection changed.
     */
    @Override
    public void valueChanged(final TreeSelectionEvent event) {
        selected = (TreeNode) event.getPath().getLastPathComponent();
        repaint();
    }

    /**
     * Paints this component.
     */
    @Override
    protected void paintComponent(final Graphics graphics) {
        super.paintComponent(graphics);
        final Graphics2D gr = (Graphics2D) graphics;
        final AffineTransform oldTransform = gr.getTransform();
        gr.scale(getWidth() / (double) bounds.width, getHeight() / (double) bounds.height);
        gr.translate(-bounds.x, -bounds.y);
        for (TreeNode node=selected; node!=null; node=(TreeNode) node.getParent()) {
            final Object value = node.getUserObject();
            if (!(value instanceof Tile)) {
                continue;
            }
            final Tile tile = (Tile) node.getUserObject();
            final Rectangle region;
            try {
                region = tile.getAbsoluteRegion();
            } catch (IOException e) {
                GraphicsUtilities.paintStackTrace(gr, getBounds(), e);
                break;
            }
            gr.draw(region);
        }
        gr.setTransform(oldTransform);
    }

    /**
     * Creates a control panel for this viewer.
     *
     * @return A new control panel.
     * @throws IOException if an I/O operation was required and failed.
     */
    protected JComponent createControlPanel() throws IOException {
        final JTree tree = new JTree(tiles.toTree());
        tree.addTreeSelectionListener(this);
        final JSplitPane panel = new JSplitPane();
        panel.setLeftComponent(new JScrollPane(tree));
        panel.setRightComponent(this);
        return panel;
    }
}
