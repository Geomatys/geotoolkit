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
package org.geotoolkit.gui.swing.go.control;

import java.awt.event.ActionEvent;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;

import org.geotoolkit.gui.swing.crschooser.JCRSChooser;
import org.geotoolkit.gui.swing.crschooser.JCRSChooser.ACTION;
import org.geotoolkit.gui.swing.go.GoMap2D;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author johann sorel
 */
public class CRSAction extends AbstractAction {

    private GoMap2D map = null;

    @Override
    public void actionPerformed(ActionEvent arg0) {
        if (map != null ) {
            JCRSChooser chooser = new JCRSChooser(null, true);
            chooser.setCRS(map.getCanvas().getObjectiveCRS());
            ACTION act = chooser.showDialog();

            if(ACTION.APPROVE.equals(act)){
                try {
                    map.getCanvas().setObjectiveCRS(chooser.getCRS());
                } catch (TransformException ex) {
                    Logger.getLogger(CRSAction.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
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
