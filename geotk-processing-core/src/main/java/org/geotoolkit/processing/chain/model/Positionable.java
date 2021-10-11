/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2013, Geomatys
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
package org.geotoolkit.processing.chain.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import org.geotoolkit.util.Utilities;

/**
 * Chain element which has a position.
 *
 * @author Guilhem Legal (Geomatys)
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
public abstract class Positionable {

    @XmlAttribute(name = "x")
    private int x;
    @XmlAttribute(name = "y")
    private int y;

    public Positionable() {
        this.x     = -1;
        this.y     = -1;
    }

    public Positionable(final int x, final int y) {
        this.x     = x;
        this.y     = y;
    }

    public Positionable(final Positionable toCopy) {
        this.x     = toCopy.x;
        this.y     = toCopy.y;
    }

    /**
     * @return the x
     */
    public int getX() {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * @return the y
     */
    public int getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Positionable) {
            final Positionable that = (Positionable) obj;
            return Utilities.equals(this.x,     that.x)
                && Utilities.equals(this.y,     that.y);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + this.x;
        hash = 89 * hash + this.y;
        return hash;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[").append(this.getClass().getSimpleName()).append("]\n");
        sb.append("x:").append(x).append('\n');
        sb.append("y:").append(y).append('\n');
        return sb.toString();
    }
}
