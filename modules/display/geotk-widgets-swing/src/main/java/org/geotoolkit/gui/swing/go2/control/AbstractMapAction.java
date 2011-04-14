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

import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import org.geotoolkit.gui.swing.go2.JMap2D;
import org.geotoolkit.util.logging.Logging;

/**
 * Abstract class for actions related to a map.
 * this action is automatically enable when a map is set.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractMapAction extends AbstractAction{

    private static final Logger LOGGER = Logging.getLogger(AbstractMapAction.class);

    protected JMap2D map = null;

    public AbstractMapAction() {
        super();
    }

    public AbstractMapAction(final JMap2D map) {
        this.map = map;
    }

    public AbstractMapAction(final String name, final Icon icon, final JMap2D map) {
        super(name, icon);
        this.map = map;
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled() && (map != null);
    }

    public JMap2D getMap() {
        return map;
    }

    public void setMap(final JMap2D map) {
        final boolean old = isEnabled();
        this.map = map;
        setEnabled(enabled);
        firePropertyChange("enabled", old, isEnabled());
    }

    public static Logger getLogger() {
        return LOGGER;
    }

}
