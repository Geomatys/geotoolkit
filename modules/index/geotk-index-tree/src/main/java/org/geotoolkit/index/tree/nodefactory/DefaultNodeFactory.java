/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.index.tree.nodefactory;

import java.util.List;
import org.geotoolkit.index.tree.DefaultNode;
import org.geotoolkit.index.tree.Node;
import org.geotoolkit.index.tree.Tree;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;

/**Create appropriate {@code Node} to R-Tree.
 *
 * @author Rémi Maréchal (Geomatys)
 */
class DefaultNodeFactory implements NodeFactory{

    /**
     * {@inheritDoc }
     */
    @Override
    public Node createNode(final Tree tree, final Node parent, final DirectPosition lowerCorner, final DirectPosition upperCorner, final List<Node> children, final List<Envelope> entries) {
        return new DefaultNode(tree, parent, lowerCorner, upperCorner, children, entries);
    }
    
}
