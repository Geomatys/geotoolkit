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
package org.geotoolkit.display3d.animation;

/**
 *
 * @author Thomas Rouby (Geomatys)
 */
public class AnimationNotValidException extends Exception {

    /**
     * Creates a new instance of
     * <code>AnimationNotValidException</code> without detail message.
     */
    public AnimationNotValidException() {
    }

    /**
     * Constructs an instance of
     * <code>AnimationNotValidException</code> with the specified detail
     * message.
     *
     * @param msg the detail message.
     */
    public AnimationNotValidException(String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of
     * <code>AnimationNotValidException</code>
     *
     * @param cause
     */
    public AnimationNotValidException(Throwable cause) {
        super(cause);
    }
    
}
