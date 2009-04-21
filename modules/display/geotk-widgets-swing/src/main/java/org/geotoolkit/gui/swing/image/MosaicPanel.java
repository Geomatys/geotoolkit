/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.gui.swing.image;

import java.io.IOException;
import java.util.Collection;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;

import org.geotoolkit.gui.swing.ZoomPane;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.image.io.mosaic.Tile;
import org.geotoolkit.image.io.mosaic.TileManager;
import org.geotoolkit.coverage.grid.ImageGeometry;


/**
 * Paints the silhouette of a set of tiles.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.0
 *
 * @since 3.0
 * @module
 */
@SuppressWarnings("serial")
final class MosaicPanel extends ZoomPane {
    /**
     * The margin to add around the tiles area, as a fraction of area width and height.
     */
    private static final double MARGIN = 0.125;

    /**
     * The maximal amount of tiles for which to paint the border. If there is more
     * tiles than this maximum to paint, their border will be omitted.
     */
    private static final int TILES_THRESHOLD = 100;

    /**
     * The manager of the tiles to be displayed.
     * May be empty but shall never be null.
     */
    private TileManager[] managers;

    /**
     * The colors to be given to tiles of each tile managers. If this array is shorter
     * than {@code managers} array, then the last color is reused for all remaining managers.
     */
    private Color[] tileColors = new Color[] {
        new Color(0, 64, 255, 128),
        new Color(255, 0, 0, 128)
    };

    /**
     * The area covered by all tiles in "real world" units. Will be computed when
     * {@link #getArea} will be invoked.
     */
    private Rectangle2D area;

    /**
     * Creates an initially empty canvas.
     */
    public MosaicPanel() {
        super(UNIFORM_SCALE | TRANSLATE_X | TRANSLATE_Y | ROTATE | RESET);
        managers = new TileManager[0];
        setBackground(Color.WHITE);
        setPaintingWhileAdjusting(true);
        setMagnifierEnabled(false);
    }

    /**
     * Sets the tiles to display. Only one tile manager is usually provided. However more managers
     * can be provided if, for example, {@link org.geotoolkit.image.io.mosaic.TileManagerFactory}
     * failed to create only one instance from a set of tiles. Only the first manager will be painted
     * with the foreground color. The other ones will be painted in a different color in order to
     * suggest that they should not be there.
     *
     * @param managers The new tile managers.
     */
    public void setTileManager(final TileManager... managers) {
        this.managers = managers.clone();
        area = null; // For forcing computation.
        reset();
    }

    /**
     * Returns the bounds of all tiles, or {@code null} if unknown.
     */
    @Override
    public Rectangle2D getArea() {
        if (area == null) {
            for (int j=managers.length; --j>=0;) {
                final TileManager manager = managers[j];
                if (manager == null) {
                    continue;
                }
                final ImageGeometry geometry;
                try {
                    geometry = manager.getGridGeometry();
                } catch (IOException e) {
                    Logging.recoverableException(MosaicPanel.class, "getArea", e);
                    managers[j] = null;
                    continue; // We will just ignore that tile manager.
                }
                if (geometry != null) {
                    final Rectangle2D region = geometry.getEnvelope();
                    if (area == null) {
                        area = region;
                    } else {
                        area.add(region);
                    }
                }
            }
            if (area == null) {
                return null;
            }
            final double width  = area.getWidth();
            final double height = area.getHeight();
            area.setRect(area.getX() -  width*(MARGIN/2),
                         area.getY() - height*(MARGIN/2),
                         width  * (MARGIN+1),
                         height * (MARGIN+1));
        }
        return (Rectangle2D) area.clone();
    }

    /**
     * Paints the tiles.
     */
    @Override
    protected void paintComponent(final Graphics2D graphics) {
        final AffineTransform textTr = graphics.getTransform();
        graphics.transform(zoom);
        final AffineTransform worldTr = graphics.getTransform();
        AffineTransform lastTr = null;
        for (int j=managers.length; --j>=0;) {
            final TileManager manager = managers[j];
            if (manager == null) {
                continue;
            }
            final Collection<Tile> tiles;
            try {
                tiles = manager.getTiles();
            } catch (IOException e) {
                Logging.recoverableException(MosaicPanel.class, "paintComponent", e);
                managers[j] = null;
                continue; // We will just ignore that tile manager.
            }
            final boolean paintBorder = tiles.size() <= TILES_THRESHOLD;
            final Color color = tileColors[Math.min(tileColors.length-1, j)];
            graphics.setColor(color);
            for (final Tile tile : tiles) {
                final Rectangle bounds;
                try {
                    bounds = tile.getRegion();
                } catch (IOException e) {
                    // Unexpected because if this exception were to occur, it should have
                    // been thrown sooner (when we asked for the bounds of the whole mosaic).
                    Logging.unexpectedException(MosaicPanel.class, "paintComponent", e);
                    continue;
                }
                // The affine transform is usually the same for every tiles at the
                // same pyramid level, so it is worth to perform the check below.
                final AffineTransform tr = tile.getGridToCRS();
                if (tr != lastTr) {
                    graphics.setTransform(worldTr);
                    graphics.transform(tr);
                    lastTr = tr;
                }
                graphics.fill(bounds);
                if (paintBorder) {
                    graphics.setColor(Color.DARK_GRAY);
                    graphics.draw(bounds);
                    graphics.setColor(color);
                }
            }
        }
        graphics.setTransform(textTr);
    }
}
