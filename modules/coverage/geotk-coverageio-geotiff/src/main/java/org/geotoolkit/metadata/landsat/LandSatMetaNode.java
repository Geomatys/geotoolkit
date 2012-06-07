/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.metadata.landsat;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import org.geotoolkit.gui.swing.tree.DefaultMutableTreeNode;

/**
 * LandSat metadata node are composed of a key and associated value.
 * Model is organised in a tree where node with children have the key 'GROUP'
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class LandSatMetaNode extends DefaultMutableTreeNode {

    public LandSatMetaNode(final String key, final String value) {
        super(new SimpleEntry<String, Object>(key, value));
    }

    @Override
    public Entry<String, String> getUserObject() {
        return (Entry) super.getUserObject();
    }

    public String getKey() {
        return getUserObject().getKey();
    }

    public Object getValue() {
        String value = getUserObject().getValue();

        if ("GROUP".equalsIgnoreCase(getKey())) {
            //value is a string without quote
            return value;
        }

        if (value.startsWith("\"")) {
            value = value.substring(1, value.length() - 1);
            return value;
        }

        //can be a number or a date or time
        return value;
    }

    @Override
    public String toString() {
        return getKey() + " = " + getValue();
    }
    
}
