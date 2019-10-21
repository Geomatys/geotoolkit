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

import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.event.StoreEvent;
import org.apache.sis.storage.event.StoreListener;
import org.geotoolkit.storage.event.FeatureStoreContentEvent;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.storage.event.StorageListener;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class StatefullFeatureMapLayerJ2D extends StatefullMapLayerJ2D<FeatureMapLayer> implements StoreListener<StoreEvent> {

    protected StorageListener.Weak weakSessionListener = new StorageListener.Weak(this);

    public StatefullFeatureMapLayerJ2D(J2DCanvas canvas, FeatureMapLayer layer) {
        super(canvas, layer, false);

        final FeatureSet resource = layer.getResource();
        if (resource instanceof FeatureSet) {
            weakSessionListener.registerSource(resource);
        }
    }

    @Override
    public void eventOccured(StoreEvent event) {
        if (event instanceof FeatureStoreContentEvent && item.isVisible() && getCanvas().isAutoRepaint()) {
            update();
        }
    }
}
