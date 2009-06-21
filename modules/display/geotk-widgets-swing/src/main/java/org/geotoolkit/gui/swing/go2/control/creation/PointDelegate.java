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
package org.geotoolkit.gui.swing.go2.control.creation;

import com.vividsolutions.jts.geom.Geometry;

import java.awt.event.MouseEvent;

/**
 * point creation handler
 * 
 * @author Johann Sorel
 */
public class PointDelegate extends AbstractMouseDelegate {

    public PointDelegate(DefaultEditionDecoration handler) {
        super(handler);
    }

    @Override
    public void fireStateChange() {
        coords.clear();
        geoms.clear();
        nbRightClick = 0;
        inCreation = false;
        hasEditionGeometry = false;
        hasGeometryChanged = false;
        editedFeatureID = null;
        editedNodes.clear();
        handler.clearMemoryLayer();
        handler.setMemoryLayerGeometry(geoms);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Geometry geo = handler.toJTS(e.getX(), e.getY());
        handler.editAddGeometry(new Geometry[]{geo});
        handler.clearMemoryLayer();
        handler.setMemoryLayerGeometry(geoms);
                
    }
    
}
