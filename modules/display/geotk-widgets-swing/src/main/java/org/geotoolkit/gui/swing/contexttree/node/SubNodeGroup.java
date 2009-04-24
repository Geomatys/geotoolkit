/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 * 
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.gui.swing.contexttree.node;

import org.geotoolkit.gui.swing.contexttree.ContextTreeNode;
import org.geotoolkit.gui.swing.contexttree.LightContextTreeModel;

/**
 * subnode group interface
 * 
 * @author Johann Sorel
 */
public interface SubNodeGroup {

    public boolean isValid(Object target);
    
    public void installInNode(LightContextTreeModel model,ContextTreeNode parentnode);
    
    public void removeForNode(LightContextTreeModel model,ContextTreeNode parentnode);
    
}
