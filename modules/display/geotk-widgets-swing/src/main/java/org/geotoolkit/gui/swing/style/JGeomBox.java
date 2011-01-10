/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Johann Sorel
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
package org.geotoolkit.gui.swing.style;

import org.geotoolkit.gui.swing.resource.MessageBundle;
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
 * @module pending
 */
public class JGeomBox extends JComboBox {

    private static final String ALL_GEOM = MessageBundle.getString("allGeom");
    private MapLayer layer = null;

    public JGeomBox() {
        setEnabled(false);
        setOpaque(false);
    }

    public void setLayer(final MapLayer layer) {

        this.layer = layer;

        setEnabled(layer != null);

        if (layer instanceof FeatureMapLayer && layer != null) {
            final Collection<PropertyDescriptor> col = ((FeatureMapLayer)layer).getCollection().getFeatureType().getDescriptors();
            final Iterator<PropertyDescriptor> ite = col.iterator();

            final List<String> geoms = new ArrayList<String>();
            geoms.add(ALL_GEOM);

            while (ite.hasNext()) {
                final PropertyDescriptor desc = ite.next();
                if (desc instanceof GeometryDescriptor) {
                    geoms.add(desc.getName().toString());
                }
            }

            setModel(new ListComboBoxModel(geoms));
            setSelectedItem(ALL_GEOM);
        }

    }

    public MapLayer getLayer() {
        return layer;
    }

    public String getGeom() {
        if(getSelectedItem().toString().equals(ALL_GEOM)){
            return null;
        }else{
            return getSelectedItem().toString();
        }
    }

    public void setGeom(final String name) {
        if (layer != null && name != null) {
            setSelectedItem(name);
        }else{
            setSelectedItem(ALL_GEOM);
        }
    }

}
