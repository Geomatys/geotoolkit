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
import org.apache.sis.storage.event.ChangeEvent;
import org.apache.sis.storage.event.ChangeListener;
import org.geotoolkit.data.FeatureStoreContentEvent;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.storage.StorageListener;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class StatefullFeatureMapLayerJ2D extends StatefullMapLayerJ2D<FeatureMapLayer> implements ChangeListener<ChangeEvent> {

    protected StorageListener.Weak weakSessionListener = new StorageListener.Weak(this);

    public StatefullFeatureMapLayerJ2D(J2DCanvas canvas, FeatureMapLayer layer) {
        super(canvas, layer, false);

        final FeatureSet resource = layer.getResource();
        if (resource instanceof FeatureSet) {
            weakSessionListener.registerSource(resource);
        }
    }

    @Override
    public void changeOccured(ChangeEvent event) {
        if (event instanceof FeatureStoreContentEvent && item.isVisible() && getCanvas().isAutoRepaint()) {
            update();
        }
    }

}
