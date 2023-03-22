/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2022, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.hdf.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Resource;
import org.apache.sis.util.iso.Names;
import org.opengis.util.GenericName;

/**
 * Parent interface for Group and Dataset.
 *
 * @author Johann Sorel (Geomatys)
 */
public sealed interface Node extends Resource permits Group, Dataset{

    Group getParent();

    String getName();

    /**
     * Offset in the file of this node.
     * This is used by Reference data types.
     * @return object file address
     */
    long getAddress();

    /**
     * Search the resource and it's children for the node with given address.
     */
    default Node findNode(long address) throws DataStoreException {
        if (getAddress() == address) {
            return this;
        }
        if (this instanceof Aggregate agg) {
            for (Resource r : agg.components()) {
                if (r instanceof Node n) {
                    Node cdt = n.findNode(address);
                    if (cdt != null) return cdt;
                }
            }
        }
        return null;
    }

    Map<String,Object> getAttributes();

    public static GenericName createName(Node node) {
        if (node.getParent() == null) {
            //root node
            return Names.createLocalName(null, null, node.getName());
        }

        final List<String> parts = new ArrayList<>();
        for (Node n = node; n != null && n.getParent() != null; n = n.getParent()) {
            parts.add(0, n.getName());
        }
        return Names.createGenericName(null, null, parts.toArray(String[]::new));
    }
}
