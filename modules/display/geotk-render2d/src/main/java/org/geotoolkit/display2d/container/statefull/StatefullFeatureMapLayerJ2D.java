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

import org.geotoolkit.data.FeatureStoreContentEvent;
import org.geotoolkit.data.FeatureStoreListener;
import org.geotoolkit.data.FeatureStoreManagementEvent;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.map.FeatureMapLayer;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class StatefullFeatureMapLayerJ2D extends StatefullMapLayerJ2D<FeatureMapLayer> implements FeatureStoreListener {

    protected FeatureStoreListener.Weak weakSessionListener = new FeatureStoreListener.Weak(this);


    public StatefullFeatureMapLayerJ2D(J2DCanvas canvas, FeatureMapLayer layer) {
        super(canvas, layer, false);
        final Session session = layer.getCollection().getSession();
        weakSessionListener.registerSource(session);
    }

    @Override
    public void structureChanged(FeatureStoreManagementEvent event) {
    }

    @Override
    public void contentChanged(FeatureStoreContentEvent event) {
        if(item.isVisible() && getCanvas().isAutoRepaint()){
            update();
        }
    }

}
