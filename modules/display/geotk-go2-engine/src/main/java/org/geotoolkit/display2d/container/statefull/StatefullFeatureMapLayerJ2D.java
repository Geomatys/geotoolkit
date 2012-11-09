/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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

package org.geotoolkit.display2d.container.statefull;

import org.geotoolkit.data.StorageContentEvent;
import org.geotoolkit.data.StorageListener;
import org.geotoolkit.data.StorageManagementEvent;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.map.FeatureMapLayer;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class StatefullFeatureMapLayerJ2D extends StatefullMapLayerJ2D<FeatureMapLayer> implements StorageListener {

    protected StorageListener.Weak weakSessionListener = new StorageListener.Weak(this);

    
    public StatefullFeatureMapLayerJ2D(J2DCanvas canvas, StatefullMapItemJ2D parent, FeatureMapLayer layer) {
        super(canvas, parent, layer);
        final Session session = layer.getCollection().getSession();
        weakSessionListener.registerSource(session);
    }

    @Override
    public void structureChanged(StorageManagementEvent event) {
    }

    @Override
    public void contentChanged(StorageContentEvent event) {
        if(item.isVisible() && getCanvas().getController().isAutoRepaint()){
            update();
        }
    }
    
}
