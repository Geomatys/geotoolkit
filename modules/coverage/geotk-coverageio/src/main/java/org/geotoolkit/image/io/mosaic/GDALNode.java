/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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

import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.geotoolkit.resources.Errors;


/**
 * A tree with nodes organized in the GDAL way, were each overview is contained in the same file.
 * The size of some overview may be bigger than the size of the base tile, which is not legal for
 * a usual RTree but happen in practice such files.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.15
 *
 * @since 3.15
 * @module
 */
@SuppressWarnings("serial") // Not expected to be serialized.
final class GDALNode extends TreeNode implements Comparator<GDALNode> {
    /**
     * Creates a node for a single tile.
     *
     * @param  tile The tile.
     * @throws IOException if an I/O operation was required and failed.
     */
    private GDALNode(final Tile tile) throws IOException {
        super(tile.getAbsoluteRegion());
        this.tile = tile;
    }

    /**
     * Builds the root of a tree for the given tiles.
     *
     * @param  tiles The tiles to be inserted in the tree.
     * @throws IOException if an I/O operation was required and failed.
     * @throws IllegalArgumentException If the tiles doesn't have GDAL layout.
     */
    public GDALNode(final Tile[] tiles) throws IOException, IllegalArgumentException {
        /*
         * Get the list of tiles for each input.
         * They should have different image index.
         */
        final Map<Object,List<GDALNode>> byInput = new HashMap<Object,List<GDALNode>>();
        for (final Tile tile : tiles) {
            final Object input = tile.getInput();
            List<GDALNode> sameInput = byInput.get(input);
            if (sameInput == null) {
                sameInput = new ArrayList<GDALNode>();
                byInput.put(input, sameInput);
            }
            sameInput.add(new GDALNode(tile));
        }
        /*
         * Sort each tile by decreasing subsampling and create the nodes.
         * We expect at least one overview for each base tile, otherwise
         * it may not be a GDAL layout.
         */
        for (final List<GDALNode> sameInput : byInput.values()) {
            if (sameInput.size() <= 1) {
                throw new IllegalArgumentException(Errors.format(Errors.Keys.FILE_HAS_TOO_FEW_DATA));
            }
            Collections.sort(sameInput, this);
            final GDALNode base = sameInput.get(sameInput.size() - 1);
            Rectangle intersection = base;
            GDALNode child = null;
            for (final GDALNode node : sameInput) {
                intersection = intersection.intersection(node);
                if (intersection.isEmpty()) {
                    throw new IllegalArgumentException(Errors.format(Errors.Keys.INCOMPATIBLE_GRID_GEOMETRY));
                }
                node.setBounds(base);
                node.addChild(child);
                child = node;
            }
            if (child != null) {
                addChild(child);
                add(child);
            }
        }
    }

    /**
     * Compares two nodes for decreasing order of subsampling.
     *
     * @param t1 The first node to compare.
     * @param t2 The second node to compare.
     */
    @Override
    public int compare(final GDALNode t1, final GDALNode t2) {
        final Dimension d1 = t1.tile.getSubsampling();
        final Dimension d2 = t2.tile.getSubsampling();
        return (d2.width * d2.height) - (d1.width * d1.height);
    }
}
