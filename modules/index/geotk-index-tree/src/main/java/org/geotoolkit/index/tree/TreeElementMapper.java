/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.index.tree;

import java.io.IOException;
import org.opengis.geometry.Envelope;

/**
 * Interface which permit to attribut an appropriate identifier to build RTree.
 *
 * @author Remi Marechal (Geomatys).
 */
public interface TreeElementMapper<E> {
    
    /**
     * Return an appropriate tree identifier from object define by user.
     * 
     * @param object
     * @return an appropriate tree identifier from object define by user.
     */
    public int getTreeIdentifier(E object) throws IOException;
    
    /**
     * Return {@link Envelope} boundary from object.
     * 
     * @param object
     * @return {@link Envelope} boundary from object. 
     */
    public Envelope getEnvelope(E object) throws IOException;
    
    /**
     * Affect a tree identifier define by user.
     * 
     * @param object
     * @param treeIdentifier 
     */
    public void setTreeIdentifier(E object, int treeIdentifier) throws IOException;
    
    /**
     * <p>Return object from its tree identifier.<br/>
     * User must set tree identifier ({@link #setTreeIdentifier(java.lang.Object, int)}) 
     * before it.</p>
     * 
     * @param treeIdentifier
     * @return object from its tree identifier.
     */
    public E getObjectFromTreeIdentifier(int treeIdentifier) throws IOException;
    
    /**
     * Initialize attributs like just after class constructor.
     */
    public void clear() throws IOException;
    
    /**
     * Close write and read stream if exist.
     */
    public void close() throws IOException;
    
    /**
     * Return true if {@link TreeElementMapper} has already been closed else false.
     * 
     * @return true if {@link TreeElementMapper} has already been closed else false.
     */
    public boolean isClosed();
}
