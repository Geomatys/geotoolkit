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
package org.geotoolkit.image.io.metadata;

import javax.imageio.metadata.IIOMetadataNode;
import org.w3c.dom.Node;


/**
 * A subclass of {@link IIOMetadataNode} with the {@link #toString()} method overloaded
 * for making debugging easier.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.06
 *
 * @since 3.06
 * @module
 */
final class IIONode extends IIOMetadataNode {
    /**
     * Creates a new node of the given name.
     */
    IIONode(final String name) {
        super(name);
    }

    /**
     * Returns a string representation of this node.
     */
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder("IIOMetadataNode[\"");
        path(this, buffer);
        return buffer.append("\"]").toString();
    }

    /**
     * Builds the path of the given node in the given buffer. This method
     * invokes itself recursively for prepending the path of parent nodes.
     */
    private static void path(final Node node, final StringBuilder buffer) {
        final Node parent = node.getParentNode();
        if (parent != null) {
            path(parent, buffer);
            buffer.append('/');
        }
        buffer.append(node.getLocalName());
    }
}
