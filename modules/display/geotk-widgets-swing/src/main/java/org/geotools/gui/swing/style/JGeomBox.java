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
package org.geotools.gui.swing.style;

import org.geotools.gui.swing.resource.MessageBundle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.swing.JComboBox;

import org.jdesktop.swingx.combobox.ListComboBoxModel;

import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.MapLayer;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.PropertyDescriptor;

/**
 * Geometrie box attribut
 * 
 * @author Johann Sorel (Puzzle-GIS)
 */
public class JGeomBox extends JComboBox {

    private static final String allGeom = MessageBundle.getString("allGeom");
    private MapLayer layer = null;

    public JGeomBox() {
        setEnabled(false);
        setOpaque(false);
    }

    public void setLayer(MapLayer layer) {

        this.layer = layer;

        setEnabled(layer != null);

        if (layer != null && layer instanceof FeatureMapLayer) {
            Collection<PropertyDescriptor> col = ((FeatureMapLayer)layer).getFeatureSource().getSchema().getDescriptors();
            Iterator<PropertyDescriptor> ite = col.iterator();

            List<String> geoms = new ArrayList<String>();
            geoms.add(allGeom);

            while (ite.hasNext()) {
                PropertyDescriptor desc = ite.next();
                if (desc instanceof GeometryDescriptor) {
                    geoms.add(desc.getName().toString());
                }
            }

            setModel(new ListComboBoxModel(geoms));
            setSelectedItem(allGeom);
        }

    }

    public MapLayer getLayer() {
        return layer;
    }

    public String getGeom() {
        if(getSelectedItem().toString().equals(allGeom)){
            return null;
        }else{
            return getSelectedItem().toString();
        }
    }

    public void setGeom(String name) {
        if (layer != null && name != null) {
            setSelectedItem(name);
        }else{
            setSelectedItem(allGeom);
        }
    }

}