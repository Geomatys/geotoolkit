/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2016, Geomatys
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
package org.geotoolkit.image.io.plugin;

import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;

import org.geotoolkit.metadata.geotiff.GeoTiffMetaDataReader;

import org.w3c.dom.Node;

/**
 * Adapted implementation of {@link IIOMetadata} class for tiff metadatas.
 *
 * @author Remi Marechal (Geomatys).
 * @see TiffImageReader#createMetadata(int) use case
 * @see GeoTiffMetaDataReader#GeoTiffMetaDataReader(javax.imageio.metadata.IIOMetadata) GeoTiffMetaDataReader
 */
public final class IIOTiffMetadata extends IIOMetadata {

    /**
     * Metadatas root Node.
     */
    IIOMetadataNode root;

    /**
     * Build {@link IIOMetadata} object adapted for tiff specification.
     *
     * @param root parent {@link IIOMetadataNode} of all metadatas.
     */
    public IIOTiffMetadata (IIOMetadataNode root) {
        this.root = root;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isReadOnly() {
        return false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Node getAsTree(String formatName) {
        return root;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void mergeTree(String formatName, Node root) throws IIOInvalidTreeException {
        final IIOMetadataNode tempRoot = this.root;
        this.root = new IIOMetadataNode(formatName);
        this.root.appendChild(tempRoot);
        this.root.appendChild(root);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void reset() {
        this.root = null;
    }
}
