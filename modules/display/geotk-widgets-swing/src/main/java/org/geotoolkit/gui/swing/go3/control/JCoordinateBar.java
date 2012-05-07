/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Johann Sorel
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.gui.swing.go3.control;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import org.geotoolkit.display3d.canvas.A3DCanvas;
import org.geotoolkit.gui.swing.referencing.AuthorityCodesComboBox;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.util.logging.Logging;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class JCoordinateBar extends JPanel{

    private static final Logger LOGGER = Logging.getLogger(JCoordinateBar.class);
    
    private final AuthorityCodesComboBox guiCRS = new AuthorityCodesComboBox(CRS.getAuthorityFactory(false));
    
    private A3DCanvas canvas;

    public JCoordinateBar() {
        add(guiCRS);
        
        final PropertyChangeListener propListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if(canvas != null && evt.getPropertyName().equals(AuthorityCodesComboBox.SELECTED_CODE_PROPERTY)){
                    try {
                        final CoordinateReferenceSystem crs = (CoordinateReferenceSystem) guiCRS.getSelectedItem();
                        if(crs != null){
                            canvas.setObjectiveCRS(crs);
                        }
                    } catch (TransformException ex) {
                        LOGGER.log(Level.WARNING, null, ex);
                    } catch (FactoryException ex) {
                        LOGGER.log(Level.WARNING, null, ex);
                    }
                }
            }
        };
        guiCRS.addPropertyChangeListener(propListener);
    }

    public void setCanvas(final A3DCanvas map) {
        this.canvas = map;

    }

    public A3DCanvas getCanvas() {
        return canvas;
    }

}
