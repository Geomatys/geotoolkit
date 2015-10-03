/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

package org.geotoolkit.gui.javafx.render2d.data;

import java.util.logging.Level;
import javafx.event.ActionEvent;
import javax.xml.bind.JAXBException;
import org.geotoolkit.gui.javafx.chooser.FXContextChooser;
import org.geotoolkit.gui.javafx.render2d.FXMap;
import org.geotoolkit.gui.javafx.render2d.FXMapAction;
import org.geotoolkit.internal.GeotkFX;
import org.geotoolkit.internal.Loggers;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.apache.sis.util.logging.Logging;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXSaveContextAction extends FXMapAction {

    public FXSaveContextAction(FXMap map) {
        super(map,GeotkFX.getString(FXSaveContextAction.class,"label"),
                GeotkFX.getString(FXSaveContextAction.class,"label"),GeotkFX.ICON_SAVE);
    }

    @Override
    public void accept(ActionEvent event) {
        if(map==null) return;

        try {
            FXContextChooser.showSaveChooser(map);

        } catch (JAXBException | FactoryException ex) {
            Loggers.DATA.log(Level.WARNING, ex.getMessage(), ex);
        } catch (TransformException ex) {
            Logging.getLogger("org.geotoolkit.gui.javafx.render2d.data").log(Level.SEVERE, null, ex);
        }

    }

}
