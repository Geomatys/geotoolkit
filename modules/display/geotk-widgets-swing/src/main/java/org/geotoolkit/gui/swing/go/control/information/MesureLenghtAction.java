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
package org.geotoolkit.gui.swing.go.control.information;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import org.geotoolkit.gui.swing.go.GoMap2D;
import org.geotoolkit.gui.swing.resource.IconBundle;


/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class MesureLenghtAction extends AbstractAction {

    private static final ImageIcon ICON_LENGHT = IconBundle.getInstance().getIcon("16_mesure_lenght");

    private GoMap2D map = null;

    public MesureLenghtAction(){
        super("",ICON_LENGHT);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        if (map != null ) {
            map.setHandler(new LenghtHandler(map));
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
