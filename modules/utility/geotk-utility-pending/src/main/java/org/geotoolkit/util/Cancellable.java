/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.util;

/**
 * Interface for Cancelable objects.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface Cancellable {
    
    /**
     * Ask to stop any running tasks. this method should not block.
     */
    void cancel();
    
    /**
     * @return true if a call to cancel has been made.
     */
    boolean isCancelled();
    
}
