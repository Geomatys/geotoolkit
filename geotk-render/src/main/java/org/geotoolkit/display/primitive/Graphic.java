/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008, Geomatys
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
package org.geotoolkit.display.primitive;


/**
 * Defines the root abstraction of a graphic object taxonomy. This base interface
 * specifies the methods common to a lightweight set of graphic objects.
 *
 * @author Johann Sorel (Geomatys)
 */
public interface Graphic {
    /**
     * Returns {@code true} if this graphic is visible.
     *
     * @return {@code true} if this graphic is visible.
     */
    boolean isVisible();

    /**
     * Sets whether this graphic should be visible.
     *
     * @param visible {@code true} if this graphic should be visible.
     */
    void setVisible(boolean visible);

    /**
     * Invoked by the container when this graphic is no longer needed.
     * Implementations may use this method to release resources, if needed. Implementations
     * may also implement this method to return an object to an object pool. It is an error
     * to reference a {@code Graphic} in any way after its dispose method has been called.
     */
    void dispose();
}
