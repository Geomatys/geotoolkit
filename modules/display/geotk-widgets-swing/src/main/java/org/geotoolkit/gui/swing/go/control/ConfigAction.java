/*
 *    GeoTools - OpenSource mapping toolkit
 *    http://geotools.org
 *    (C) 2002-2007, GeoTools Project Managment Committee (PMC)
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
package org.geotoolkit.gui.swing.go.control;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import org.geotoolkit.gui.swing.go.GoMap2D;


/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ConfigAction extends AbstractAction {

    private GoMap2D map = null;

    @Override
    public void actionPerformed(ActionEvent arg0) {
        if (map != null ) {
            JDialog dia = new JConfigDialog(null, map);
            dia.setLocationRelativeTo(null);
            dia.setVisible(true);
        }
    }

    public GoMap2D getMap() {
        return map;
    }

    public void setMap(GoMap2D map) {
        this.map = map;
        setEnabled(map != null);
    }
}
