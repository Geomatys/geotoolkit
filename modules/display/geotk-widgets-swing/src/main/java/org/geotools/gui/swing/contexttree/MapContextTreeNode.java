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
package org.geotools.gui.swing.contexttree;

import javax.swing.ImageIcon;

import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.util.SimpleInternationalString;
import org.geotoolkit.map.MapContext;

import org.geotools.gui.swing.resource.IconBundle;

/**
 * a specific mutabletreenode for jcontexttree
 * 
 * @author Johann Sorel
 */
public final class MapContextTreeNode extends ContextTreeNode {

    private static final ImageIcon ICON_CONTEXT_ACTIVE = IconBundle.getInstance().getIcon("16_mapcontext_enable");
    private static final ImageIcon ICON_CONTEXT_DESACTIVE = IconBundle.getInstance().getIcon("16_mapcontext_disable");

    /**
     * 
     * @param model
     * @param context 
     */
    public MapContextTreeNode(LightContextTreeModel model, MapContext context) {
        super(model);
        setUserObject(context);
    }
    
    @Override
    public ImageIcon getIcon() {
        MapContext context = (MapContext) getUserObject();
       
        if (context.equals( lightModel.completeModel.getActiveContext())) {
            return ICON_CONTEXT_ACTIVE;
        } else {
            return ICON_CONTEXT_DESACTIVE;
        }

    }

    @Override
    public boolean isEditable() {
        return true;
    }

    @Override
    public Object getValue() {
        MapContext context = (MapContext) getUserObject();
        return context.getDescription().getTitle().toString();
    }

    @Override
    public void setValue(Object obj) {
        MapContext context = (MapContext) getUserObject();
        context.setDescription(FactoryFinder.getStyleFactory(null).description(
                new SimpleInternationalString(obj.toString()),
                context.getDescription().getAbstract()));
    }
}
