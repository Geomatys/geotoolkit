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
package org.geotoolkit.display;

import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.display.primitive.SceneNode;

/**
 * Loops on all scene node, adding them in a list preserving the tree order.
 * 
 * @author Johann Sorel (Geomatys)
 */
public final class FlattenVisitor implements SceneVisitor{

    public static final FlattenVisitor ALL = new FlattenVisitor(false);
    public static final FlattenVisitor ONLY_VISIBLE = new FlattenVisitor(true);

    private final boolean onlyVisible;
    
    private FlattenVisitor(boolean onlyVisible) {
        this.onlyVisible = onlyVisible;
    }
    
    @Override
    public List visit(SceneNode node, Object extraData) {
        
        final List lst;
        if(extraData instanceof List){
            lst = (List)extraData;
        }else{
            lst = new ArrayList();
            extraData = lst;
        }
        if(!(onlyVisible && !node.isVisible())){
        
            lst.add(node);
            for(SceneNode c : node.getChildren()){
                c.accept(this, extraData);
            }
        }
        return lst;
    }
    
}
