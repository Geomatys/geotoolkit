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
package org.geotoolkit.index.tree.basic;

import org.geotoolkit.index.tree.TreeUtils;

/**
 * To choose split made.
 * 
 * <blockquote><font size=-1>
 * <strong>NOTE: 
 *      - LINEAR split    : Choose two objects as seeds for the two nodes, 
 *                          where these objects are as apart as possible.
 *                          Then consider each remaining object in a random order 
 *                          and assign it to the node requiring the smallest 
 *                          enlargement of its respective boundary.
 *      
 *      - QUADRATIC split : Choose two objects as seeds for the two nodes, 
 *                          where these objects if put together create as much 
 *                          dead space as possible. Then, until there are no 
 *                          remaining objects, insert the object for which the 
 *                          difference of dead space if assigned to each of the two nodes
 *                          that requires lesser enlargement of its respective boundary.</strong> 
 * </font></blockquote>
 * @see TreeUtils#getDeadSpace(org.geotoolkit.index.tree.Node2D).(About dead space).
 * @see BasicRTree#splitNode(org.geotoolkit.index.tree.Node2D).
 * @author Rémi Mréchal (Geomatys).
 */
public enum SplitCase {
    QUADRATIC,
    LINEAR
}
