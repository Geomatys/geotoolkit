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
package org.geotoolkit.gui.swing.image;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.util.Collections;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;

import org.opengis.metadata.spatial.PixelOrientation;

import org.geotoolkit.gui.swing.ZoomPane;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.image.io.mosaic.Tile;
import org.geotoolkit.image.io.mosaic.TileManager;
import org.geotoolkit.coverage.grid.ImageGeometry;
import org.apache.sis.referencing.operation.matrix.AffineTransforms2D;


/**
 * Paints the silhouette of a set of tiles.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.15
 *
 * @since 3.00
 * @module
 */
@SuppressWarnings("serial")
final class MosaicPanel extends ZoomPane {
    /**
     * The margin to add around the tiles area, as a fraction of area width and height.
     */
    private static final double MARGIN = 0.125;

    /**
     * An empty array of tile managers.
     */
    static final TileManager[] NO_TILES = new TileManager[0];

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
        new Color(0, 64, 255, 92),
        new Color(255, 0, 0, 92)
    };

    /**
     * The colors of selected tiles. By default they are derived from {@link #tileColors}.
     */
    private Color[] selectedColors;

    /**
     * The area covered by all tiles in "real world" units. Will be computed when
     * {@link #getArea} will be invoked.
     */
    private Rectangle2D area;

    /**
     * The selected tiles.
     */
    private Set<Tile> selected;

    /**
     * Creates an initially empty canvas.
     */
    public MosaicPanel() {
        super(UNIFORM_SCALE | TRANSLATE_X | TRANSLATE_Y | ROTATE | RESET);
        managers = NO_TILES;
        deriveSelectedColors();
    }

    /**
     * Derives the selected colors from the current tile colors.
     */
    private void deriveSelectedColors() {
        selectedColors = tileColors.clone();
        for (int i=0; i<selectedColors.length; i++) {
            selectedColors[i] = selectedColors[i].darker();
        }
    }

    /**
     * Sets the tiles to be displayed as selected tiles. Elements that are not in the set of tiles
     * managed by a {@link TileManger} will be ignored.
     *
     * @param selected The selected tiles.
     */
    public void setSelectedTiles(final Tile... tiles) {
        if (tiles == null || tiles.length == 0) {
            selected = null;
        } else {
            final Collection<Tile> asList = Arrays.asList(tiles);
            if (selected == null) {
                selected = new HashSet<>(asList);
            } else {
                selected.clear();
                selected.addAll(asList);
            }
        }
        repaint();
    }

    /**
     * Returns the tiles to be displayed as selected tiles, or an empty array if none.
     */
    public Tile[] getSelectedTiles() {
        Set<Tile> selected = this.selected;
        if (selected == null) {
            selected = Collections.emptySet();
        }
        return selected.toArray(new Tile[selected.size()]);
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
    public void setTileManagers(final TileManager... managers) {
        this.managers = managers.clone();
        area = null; // For forcing computation.
        reset();
    }

    /**
     * Returns the tile managers. The returned array may be empty but should never be null.
     *
     * @return The tile managers.
     */
    public TileManager[] getTileManagers() {
        return managers.clone();
    }

    /**
     * Convenience method returning the first tile manager, or {@code null} if none.
     *
     * @return The first tile mamanger, or {@code null}.
     */
    public TileManager getTileManager() {
        return managers.length != 0 ? managers[0] : null;
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
                    final Rectangle2D region = geometry.getEnvelope(PixelOrientation.UPPER_LEFT);
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
        final Set<Tile> selected = this.selected;
        final AffineTransform imageToDisplay = new AffineTransform(zoom);
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
            final int ci = Math.min(tileColors.length-1, j);
            final Color selectedColor = selectedColors[ci];
            final Color color = tileColors[ci];
            graphics.setColor(color);
            boolean isSelected = false;
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
                // The affine transform is usually the same instance for every tiles at
                // the same pyramid level, so it is worth to perform the check below.
                final AffineTransform tr = tile.getGridToCRS();
                if (tr != lastTr) {
                    imageToDisplay.setTransform(zoom);
                    imageToDisplay.concatenate(tr);
                    lastTr = tr;
                }
                AffineTransforms2D.transform(imageToDisplay, bounds, bounds);
                if (bounds.width >= 5 && bounds.height >= 5) {
                    bounds.x++; bounds.width  -= 2;
                    bounds.y++; bounds.height -= 2;
                }
                if (selected != null && selected.contains(tile) != isSelected) {
                    isSelected = !isSelected;
                    graphics.setColor(isSelected ? selectedColor : color);
                }
                graphics.fill(bounds);
            }
        }
    }
}
