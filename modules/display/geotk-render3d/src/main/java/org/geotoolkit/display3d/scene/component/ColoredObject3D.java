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
package org.geotoolkit.display3d.scene.component;

import org.geotoolkit.display3d.Map3D;


/**
 *
 * @author Thomas Rouby (Geomatys)
 */
public abstract class ColoredObject3D extends Object3D {

    private final float[] color = new float[]{0.0f,0.0f,0.0f,1.0f};
    private final float[] defaultColor = new float[]{0.0f,0.0f,0.0f,1.0f};

    protected ColoredObject3D(Map3D map){
        super(map);
    }

    public ColoredObject3D(ColoredObject3D orig){
        super(orig);

        this.setColor(orig.color);
        this.setDefaultColor(orig.defaultColor);
    }

    public float[] getColor(){
        return this.color;
    }

    public float[] getDefaultColor(){
        return this.defaultColor;
    }

    public void setColor(float[] color){
        if (color == null) return;
        System.arraycopy(color, 0, this.color, 0, Math.min(color.length, 4));
    }

    public void setDefaultColor(float[] color){
        if (color == null) return;
        System.arraycopy(color, 0, this.defaultColor, 0, Math.min(color.length, 4));
    }
}
