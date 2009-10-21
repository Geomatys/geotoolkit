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
 * multipoint creation handler
 * 
 * @author Johann Sorel
 * @module pending
 */
public class MultiPointDelegate extends AbstractMouseDelegate {

    public MultiPointDelegate(DefaultEditionDecoration handler) {
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
        editionDecoration.clearMemoryLayer();
        editionDecoration.setMemoryLayerGeometry(geoms);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        final int button = e.getButton();

        if (button == MouseEvent.BUTTON1) {
            Geometry geo = EditionHelper.createPoint(editionDecoration.toCoord(e.getX(), e.getY()));
            geoms.add(geo);
            updateCreationGeoms();

        } else if (button == MouseEvent.BUTTON3) {
            if (geoms.size() > 0) {
                Geometry geo = EditionHelper.createMultiPoint(geoms);
                editionDecoration.editAddGeometry(new Geometry[]{geo});
                geoms.clear();
            }
            coords.clear();
        }
        editionDecoration.clearMemoryLayer();
        editionDecoration.setMemoryLayerGeometry(geoms);
    }
}
