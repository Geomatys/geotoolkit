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
package org.geotoolkit.gui.swing.go.control.creation;

import com.vividsolutions.jts.geom.Geometry;
import java.awt.event.MouseEvent;

/**
 * line creation handler
 * 
 * @author Johann Sorel
 */
public class LineDelegate extends AbstractMouseDelegate {

    public LineDelegate(DefaultEditionDecoration handler) {
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
        final int button = e.getButton();

        if (button == MouseEvent.BUTTON1) {
            coords.add(handler.toCoord(e.getX(), e.getY()));
            updateCreationGeoms();
        } else if (button == MouseEvent.BUTTON3) {
            inCreation = false;
            if (coords.size() > 1) {
                Geometry geo = EditionHelper.createLine(coords);
                handler.editAddGeometry(new Geometry[]{geo});
                geoms.clear();
            }
            coords.clear();
        }
        handler.clearMemoryLayer();
        handler.setMemoryLayerGeometry(geoms);
    }
}
