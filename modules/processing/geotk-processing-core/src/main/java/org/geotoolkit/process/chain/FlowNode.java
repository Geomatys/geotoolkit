/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2013, Geomatys
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
package org.geotoolkit.process.chain;

import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.process.chain.model.FlowLink;

/**
 * Chain flow node.
 * @author Johann Sorel (Geomatys)
 */
final class FlowNode {
    
    final Object object;
    final List<FlowNode> children = new ArrayList<FlowNode>();
    final List<FlowLink> links = new ArrayList<FlowLink>();
    final boolean isInput;
    final boolean isOutput;

    FlowNode(Object object, boolean isInput, boolean isOutput) {
        this.object = object;
        this.isInput = isInput;
        this.isOutput = isOutput;
    }

    public Object getObject() {
        return object;
    }

    public List<FlowNode> getChildren() {
        return children;
    }

    public List<FlowLink> getLinks() {
        return links;
    }

    public boolean isInput() {
        return isInput;
    }

    public boolean isOutput() {
        return isOutput;
    }
    
}
