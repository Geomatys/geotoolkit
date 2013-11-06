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
package org.geotoolkit.display.canvas;

import org.geotoolkit.display.DisplayElement;
import org.geotoolkit.display.container.GraphicContainer;
import org.geotoolkit.factory.Hints;
import org.opengis.display.canvas.CanvasController;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class Canvas extends DisplayElement {

    public Canvas() {
    }

    public Canvas(Hints hints) {
        super(hints);
    }

    public abstract GraphicContainer getContainer();

}
