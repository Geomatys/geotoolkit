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

import org.geotoolkit.display.container.GraphicContainer;
import org.geotoolkit.display.primitive.SceneNode;
import org.geotoolkit.display3d.Map3D;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class Scene3D extends GraphicContainer{

    private final Map3D canvas;
    private SceneNode root;

    public Scene3D(final Map3D canvas) {
        this.canvas = canvas;
        this.root = new SceneNode3D(canvas);
    }

    @Override
    public Map3D getCanvas() {
        return canvas;
    }

    @Override
    public SceneNode getRoot() {
        return root;
    }

    @Override
    public void setRoot(SceneNode node) {
        this.root = node;
    }

}
