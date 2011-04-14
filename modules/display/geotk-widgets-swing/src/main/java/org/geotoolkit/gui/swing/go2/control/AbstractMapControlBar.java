/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2011, Johann Sorel
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

package org.geotoolkit.gui.swing.go2.control;

import java.awt.Component;
import javax.swing.JToolBar;
import org.geotoolkit.gui.swing.go2.JMap2D;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractMapControlBar extends JToolBar implements MapControlBar{

    protected JMap2D map = null;

    protected AbstractMapControlBar(){
        this(null);
    }

    protected AbstractMapControlBar(final JMap2D map){
        this.map = map;
    }

    @Override
    public void setMap(final JMap2D map2d) {
        map = map2d;
    }

    @Override
    public JMap2D getMap() {
        return map;
    }

    @Override
    public Component getComponent() {
        return this;
    }

}
