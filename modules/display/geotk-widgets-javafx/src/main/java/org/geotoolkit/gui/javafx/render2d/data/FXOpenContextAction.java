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
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.gui.javafx.chooser.FXContextChooser;
import org.geotoolkit.gui.javafx.render2d.FXMap;
import org.geotoolkit.gui.javafx.render2d.FXMapAction;
import org.geotoolkit.internal.GeotkFX;
import org.geotoolkit.internal.Loggers;
import org.geotoolkit.map.MapContext;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXOpenContextAction extends FXMapAction {
    
    public FXOpenContextAction(FXMap map) {
        super(map,GeotkFX.getString(FXOpenContextAction.class,"label"),
                GeotkFX.getString(FXOpenContextAction.class,"label"),GeotkFX.ICON_OPEN);
    }
    
    @Override
    public void accept(ActionEvent event) {
        if(map==null) return;
        
        try {
            final MapContext context = FXContextChooser.showOpenChooser(map.getScene().getWindow());
            if(context!=null){
                map.getContainer().setContext(context);
            }

        } catch (JAXBException | FactoryException | DataStoreException ex) {
            Loggers.DATA.log(Level.WARNING, ex.getMessage(), ex);
        }
        
    }
    
}
