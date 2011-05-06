/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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

package org.geotoolkit.gui.swing.go2.control.edition;

import java.awt.event.KeyEvent;
import java.util.logging.Logger;
import org.geotoolkit.gui.swing.go2.JMap2D;
import org.geotoolkit.gui.swing.go2.control.navigation.MouseNavigatonListener;
import org.geotoolkit.gui.swing.go2.decoration.MapDecoration;
import org.geotoolkit.util.logging.Logging;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class AbstractEditionDelegate extends MouseNavigatonListener implements EditionDelegate{

    protected static final Logger LOGGER = Logging.getLogger(AbstractEditionDelegate.class);
    
    protected AbstractEditionDelegate(final JMap2D map){
        super(map);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public MapDecoration getDecoration(){
        return super.getDecoration();
    }

}
