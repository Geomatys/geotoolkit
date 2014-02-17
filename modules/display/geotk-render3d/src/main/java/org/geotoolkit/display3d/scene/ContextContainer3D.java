/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.display3d.scene;

import org.geotoolkit.display.container.MapContextContainer;
import org.geotoolkit.display3d.Map3D;
import org.geotoolkit.map.MapContext;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ContextContainer3D extends Scene3D implements MapContextContainer{

    private MapContext context;

    //keep reference of the displayed terrain
    //todo will need to remove this for the generic engine
    private Terrain terrain = null;


    public ContextContainer3D(Map3D canvas) {
        super(canvas);
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public Terrain createTerrain(Envelope orig, int numMosaic) throws TransformException, FactoryException {
        if(terrain != null){
            terrain.dispose(getCanvas().getDrawable());
        }
        getRoot().getChildren().clear();
        terrain = new Terrain(getCanvas(), orig, numMosaic);
        getRoot().getChildren().add(terrain);
        return terrain;
    }

    @Override
    public void setContext(MapContext context) {
        this.context = context;
    }

    @Override
    public MapContext getContext() {
        return context;
    }

}
