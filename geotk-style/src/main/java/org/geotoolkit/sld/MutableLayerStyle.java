/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.sld;

import java.beans.PropertyChangeListener;
import java.util.EventListener;
import org.geotoolkit.style.StyleListener;
import org.opengis.sld.LayerStyle;

/**
 * Mutable style.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public interface MutableLayerStyle extends LayerStyle{

    //--------------------------------------------------------------------------
    // listeners management ----------------------------------------------------
    //--------------------------------------------------------------------------

    void addListener(StyleListener listener);

    void addListener(PropertyChangeListener listener);

    void removeListener(PropertyChangeListener listener);

    <T extends EventListener> T[] getListeners(final Class<T> listenerClass);

    Object[] getListeners();
}
