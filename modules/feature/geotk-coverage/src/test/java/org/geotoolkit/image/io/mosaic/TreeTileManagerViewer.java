/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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

import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.awt.geom.AffineTransform;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import org.geotoolkit.util.Exceptions;
import org.geotoolkit.gui.swing.tree.TreeNode;
import org.geotoolkit.internal.GraphicsUtilities;


/**
 * A panel showing the content of a {@link TreeTileManager}. This is a component of the
 * {@link MosaicImageViewer} widget, but can also be used as a standalone widget when
 * the manager to debug is known to be an instance of {@code TreeTileManager} and we
 * don't want to load the image.
 * <p>
 * The tiles having a subsampling of (1,1) are outlined in gray, because they are often
 * the basic building blocks of the grid. When a tile is selected, that tile is outlined
 * in black, and all its parent tiles are outlines in black as well. The direct children
 * (and only them, not the children of children) of the selected tiles are filled in blue,
 * so we can check if the tile contains all the expected children.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.04
 *
 * @since 3.00
 */
@SuppressWarnings("serial")
final strictfp class TreeTileManagerViewer extends JPanel implements TreeSelectionListener {
    /**
     * The margin to put on left, right, top and bottom of the area where tile are drawn.
     */
    private static final int MARGIN = 6;

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
     * The fill color of tiles.
     */
    private final Color tileColor = new Color(248, 248, 248);

    /**
     * The color to use for filling the children of the selected node.
     */
    private final Color selectedChildren = new Color(0, 0, 255, 32);

    /**
     * Creates a viewer for the given manager.
     *
     * @param  tiles The tile manager to display.
     * @throws IOException if an I/O operation was required and failed.
     */
    public TreeTileManagerViewer(final TreeTileManager tiles) throws IOException {
        this.tiles = tiles;
        this.bounds = tiles.getRegion();
        setBackground(Color.WHITE);
    }

    /**
     * Invoked when the tree selection changed. This method is public as
     * an implementation side-effect and should not be invoked directly.
     *
     * @param event Contains the path to the selected node.
     */
    @Override
    public void valueChanged(final TreeSelectionEvent event) {
        selected = (TreeNode) event.getPath().getLastPathComponent();
        repaint();
    }

    /**
     * Paints the outlines of tiles. First we paint with a gray color every tiles having a
     * subsampling of (1,1), since they are the most "basic" tiles typically used as the
     * source of other tiles. Next we paint the selected tiles on top of the previous ones.
     *
     * @param graphics The handler to use for drawing the tiles.
     */
    @Override
    protected void paintComponent(final Graphics graphics) {
        super.paintComponent(graphics);
        final Graphics2D gr = (Graphics2D) graphics;
        final Paint oldPaint = gr.getPaint();
        final AffineTransform oldTransform = gr.getTransform();
        gr.translate(MARGIN, MARGIN);
        gr.scale((getWidth()  - MARGIN*2) / (double) bounds.width,
                 (getHeight() - MARGIN*2) / (double) bounds.height);
        gr.translate(-bounds.x, -bounds.y);
        try {
            try {
                /*
                 * Outline in light gray the tiles having a subsampling of (1,1).
                 */
                for (final Tile tile : tiles.getTiles()) {
                    final Dimension s = tile.getSubsampling();
                    if (s.width == 1 && s.height == 1) {
                        final Rectangle region = tile.getAbsoluteRegion();
                        gr.setColor(tileColor);
                        gr.fill(region);
                        gr.setColor(Color.LIGHT_GRAY);
                        gr.draw(region);
                    }
                }
                /*
                 * Paint the children of the selected tile in a transparent blue.
                 * Only immediate children are painted, not the children of children.
                 * Overlapping area are painted in red.
                 */
                if (selected != null) {
                    final int count = selected.getChildCount();
                    final Rectangle[] regions = new Rectangle[count];
                    int n = 0;
                    for (int i=0; i<count; i++) {
                        final Rectangle region = getAbsoluteRegion((TreeNode) selected.getChildAt(i));
                        if (region != null) {
                            regions[n++] = region;
                            gr.setPaint(selectedChildren);
                            gr.fill(region);
                            gr.setPaint(Color.BLUE);
                            gr.draw(region);
                        }
                    }
                    gr.setPaint(Color.RED);
                    for (int i=0; i<n; i++) {
                        for (int j=i+1; j<n; j++) {
                            final Rectangle overlaps = regions[i].intersection(regions[j]);
                            if (!overlaps.isEmpty()) {
                                gr.draw(overlaps);
                            }
                        }
                    }
                }
                /*
                 * Outline in black the selected tile and all its parents.
                 */
                gr.setColor(Color.BLACK);
                for (TreeNode node=selected; node!=null; node=(TreeNode) node.getParent()) {
                    final Rectangle region = getAbsoluteRegion(node);
                    if (region != null) {
                        gr.draw(region);
                    }
                }
            } finally {
                gr.setTransform(oldTransform);
                gr.setPaint(oldPaint);
            }
        } catch (IOException e) {
            Exceptions.paintStackTrace(gr, getBounds(), e);
        }
    }

    /**
     * Returns the absolute region for the tile at the given node, or {@code null} if unknown.
     */
    private static Rectangle getAbsoluteRegion(final TreeNode node) throws IOException {
        if (node instanceof Rectangle) {
            return (Rectangle) node;
        } else {
            final Object value = node.getUserObject();
            if (value instanceof Rectangle) {
                return (Rectangle) value;
            } else if (value instanceof Tile) {
                final Tile tile = (Tile) node.getUserObject();
                return tile.getAbsoluteRegion();
            } else {
                return null;
            }
        }
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

    /**
     * Displays this viewer in a frame. This is a convenience method only.
     *
     * @throws IOException If an I/O operation was required and failed.
     */
    public void showInFrame() throws IOException {
        final JFrame frame = new JFrame("TreeTileManagerViewer");
        frame.add(createControlPanel());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }

    /**
     * Loads a serialized {@link TreeTileManager} and display it.
     *
     * @param  args A single command-line argument which is the name of the serialized mosaic.
     * @throws IOException If the tiles can not be deserialized.
     * @throws ClassNotFoundException If the serialized stream contains an unknown class.
     */
    public static void main(final String[] args) throws IOException, ClassNotFoundException {
        final Object tiles;
        try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(args[0])))) {
            tiles = in.readObject();
        }
        final TreeTileManager manager;
        if (tiles instanceof ComparedTileManager) {
            final ComparedTileManager cm = (ComparedTileManager) tiles;
            if (cm.first instanceof TreeTileManager) {
                manager = (TreeTileManager) cm.first;
            } else {
                manager = (TreeTileManager) cm.second;
            }
        } else {
            manager = (TreeTileManager) tiles;
        }
        GraphicsUtilities.setLookAndFeel(MosaicImageViewer.class, "main");
        final TreeTileManagerViewer viewer = new TreeTileManagerViewer(manager);
        viewer.showInFrame();
    }
}
