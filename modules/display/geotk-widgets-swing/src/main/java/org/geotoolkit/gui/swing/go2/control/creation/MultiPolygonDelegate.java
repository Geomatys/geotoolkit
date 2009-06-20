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
 * multipolygon creation handler
 * 
 * @author Johann Sorel
 */
public class MultiPolygonDelegate extends AbstractMouseDelegate {

    public MultiPolygonDelegate(DefaultEditionDecoration handler) {
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
            nbRightClick = 0;
            coords.add(handler.toCoord(e.getX(), e.getY()));
            updateCreationGeoms();
        } else if (button == MouseEvent.BUTTON3) {
            nbRightClick++;
            if (nbRightClick == 1) {
                inCreation = false;
                if (coords.size() > 2) {
                    if (geoms.size() > 0) {
                        geoms.remove(geoms.size() - 1);
                    }
                    Geometry geo = EditionHelper.createPolygon(coords);
                    geoms.add(geo);
                } else if (coords.size() > 0) {
                    if (geoms.size() > 0) {
                        geoms.remove(geoms.size() - 1);
                    }
                }
            } else {
                if (geoms.size() > 0) {
                    Geometry geo = EditionHelper.createMultiPolygon(geoms);
                    handler.editAddGeometry(new Geometry[]{geo});
                    nbRightClick = 0;
                    geoms.clear();
                }
            }
            coords.clear();
        }

        handler.clearMemoryLayer();
        handler.setMemoryLayerGeometry(geoms);
    }
}
