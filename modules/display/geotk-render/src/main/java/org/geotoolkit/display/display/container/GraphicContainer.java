/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.display.container;

import java.util.Collections;
import java.util.List;
import org.geotoolkit.display.DisplayElement;
import org.geotoolkit.display.FlattenVisitor;
import org.geotoolkit.display.canvas.Canvas;
import org.geotoolkit.display.primitive.SceneNode;

/**
 * A Graphic Container holds a scene definition.
 * The scene tree might not be modifiable or dynamic.
 * 
 * @author Johann Sorel (Geomatys)
 */
public abstract class GraphicContainer extends DisplayElement{
    
    /**
     * The name of the {@linkplain PropertyChangeEvent property change event} fired when the
     * {@linkplain GraphicContainer#getRoot node } changed.
     */
    public static final String ROOT_KEY = "root";
    
    /**
     * Get the canvas attached to this container.
     * @return Canvas
     */
    public abstract Canvas getCanvas();
    
    /**
     * Get the root scene node.
     * @return SceneNode, can be null.
     */
    public abstract SceneNode getRoot();
    
    /**
     * Set the root scene node.
     * @param node SceneNode, can be null.
     */
    public abstract void setRoot(SceneNode node);
    
    /**
     * Get a list snapshot of the scene nodes.
     * @return List<SceneNode> never null.
     */
    public List<SceneNode> flatten(boolean onlyVisible){
        final SceneNode root = getRoot();
        if(root == null){
            return Collections.EMPTY_LIST;
        }
        return (List)root.accept( (onlyVisible)?FlattenVisitor.ONLY_VISIBLE:FlattenVisitor.ALL, null);
    }
    
    /**
     * Clear any cache used by the container.
     */
    public void clearCache(){
    }
    
}
