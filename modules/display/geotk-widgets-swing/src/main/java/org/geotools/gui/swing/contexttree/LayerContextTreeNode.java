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
import org.geotools.gui.swing.resource.IconBundle;
import org.geotoolkit.map.MapLayer;

/**
 * a specific mutabletreenode for jcontexttree
 * 
 * @author Johann Sorel
 */
public final class LayerContextTreeNode extends ContextTreeNode {

    private static final ImageIcon ICON_LAYER_VISIBLE = IconBundle.getInstance().getIcon("16_maplayer_visible");
    private static final ImageIcon ICON_LAYER_UNVISIBLE = IconBundle.getInstance().getIcon("16_maplayer_unvisible");

    /**
     * 
     * @param model
     * @param layer 
     */
    public LayerContextTreeNode(LightContextTreeModel model, MapLayer layer) {
        super(model);
        setUserObject(layer);
    }

    @Override
    public ImageIcon getIcon() {
        MapLayer layer = (MapLayer) getUserObject();

        //TODO fix icons check;
            return ((layer.isVisible()) ? ICON_LAYER_VISIBLE : ICON_LAYER_UNVISIBLE);
        
//        if(layer instanceof FeatureMapLayer){
//            
//        }else{
//            
//        }
//        
//        FeatureSource fs = layer.getFeatureSource();
//
//        if (fs != null) {
//            
//            //choose icon from datastoretype
//            DataStore ds = (DataStore) fs.getDataStore();
//
//            if (layer.getFeatureSource().getSchema().getName().getLocalPart().equals("GridCoverage")) {
//                return ((layer.isVisible()) ? ICON_LAYER_FILE_RASTER_VISIBLE : ICON_LAYER_FILE_RASTER_UNVISIBLE);
//            } else if (AbstractFileDataStore.class.isAssignableFrom(ds.getClass())) {
//                return ((layer.isVisible()) ? ICON_LAYER_FILE_VECTOR_VISIBLE : ICON_LAYER_FILE_VECTOR_UNVISIBLE);
//            } else if (JDBC1DataStore.class.isAssignableFrom(ds.getClass())) {
//                return ((layer.isVisible()) ? ICON_LAYER_DB_VISIBLE : ICON_LAYER_DB_UNVISIBLE);
//            } else {
//                return ((layer.isVisible()) ? ICON_LAYER_VISIBLE : ICON_LAYER_UNVISIBLE);
//            }
//        } else {
//            return ((layer.isVisible()) ? ICON_LAYER_VISIBLE : ICON_LAYER_UNVISIBLE);
//        }

    }

    public boolean isEditable() {
        return true;
    }

    @Override
    public Object getValue() {
        MapLayer layer = (MapLayer) getUserObject();
        return layer.getDescription().getTitle().toString();
    }

    @Override
    public void setValue(Object obj) {
        MapLayer layer = (MapLayer) getUserObject();
        layer.setDescription(FactoryFinder.getStyleFactory(null).description(
                new SimpleInternationalString(obj.toString()),
                layer.getDescription().getAbstract()));
    }
}
