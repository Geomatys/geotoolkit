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
package org.geotoolkit.display.container;

import java.util.Objects;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.display.canvas.Canvas;
import org.geotoolkit.display.primitive.SceneNode;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class DefaultGraphicContainer extends GraphicContainer{

    private final Canvas canvas;
    private SceneNode root;

    public DefaultGraphicContainer(final Canvas canvas) {
        this(canvas,null);
    }

    public DefaultGraphicContainer(final Canvas canvas, final SceneNode root) {
        ArgumentChecks.ensureNonNull("canvas", canvas);
        this.canvas = canvas;
        this.root = root;
    }

    @Override
    public Canvas getCanvas() {
        return canvas;
    }

    @Override
    public SceneNode getRoot() {
        return root;
    }

    @Override
    public void setRoot(final SceneNode root) {
        if (Objects.equals(root, this.root)) return;
        final SceneNode old = this.root;
        this.root = root;
        firePropertyChange(ROOT_KEY, old, root);
    }

}
