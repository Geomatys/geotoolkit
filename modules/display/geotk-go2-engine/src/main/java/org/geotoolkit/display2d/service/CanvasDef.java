/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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
package org.geotoolkit.display2d.service;

import java.awt.Color;
import java.awt.Dimension;

/**
 * Definition of a canvas, dimension, background and image stretching.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class CanvasDef {

    private Dimension dimension;
    private Color background;
    private boolean stretchImage = true;

    public CanvasDef() {
    }

    public CanvasDef(Dimension dim, Color background) {
        this(dim,background,true);
    }

    public CanvasDef(Dimension dim, Color background, boolean stretch) {
        setDimension(dim);
        setBackground(background);
        setStretchImage(stretch);
    }

    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public void setBackground(Color background) {
        this.background = background;
    }

    public Color getBackground() {
        return background;
    }

    public void setStretchImage(boolean stretchImage) {
        this.stretchImage = stretchImage;
    }

    public boolean isStretchImage() {
        return stretchImage;
    }
    
    @Override
    public String toString() {
        return "CanvasDef[dimension="+ dimension +", background="+ background +", stretchImage="+ stretchImage +"]";
    }
}
