/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Johann Sorel
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
package org.geotoolkit.display3d.primitive;

import com.ardor3d.scenegraph.Node;
import org.geotoolkit.display3d.canvas.A3DCanvas;
import org.opengis.display.primitive.Graphic;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public abstract class A3DGraphic extends Node implements Graphic{

    protected final A3DCanvas canvas;
    protected boolean visible = true;

    protected A3DGraphic(A3DCanvas canvas){
        this.canvas = canvas;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public void dispose() {
    }

}
