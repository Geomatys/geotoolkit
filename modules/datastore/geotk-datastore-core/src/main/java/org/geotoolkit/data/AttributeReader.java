/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 * 
 *    (C) 2003-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.data;

import java.io.IOException;

import org.opengis.feature.type.AttributeDescriptor;

/**
 * The low-level attribute reading API.  An AttributeReader is responsible for
 * reading a finite set of attributes from an underlying storage format. It
 * provides meta-data regarding the data it can provide, and an iterative,
 * row-based approach for accessing the data.
 *
 * @author Ian Schneider
 * @version $Id$
 *
 * @module pending
 */
public interface AttributeReader {

    /**
     * The number of attributes this reader can read, i.e the length of a row.
     *
     * @return Number of attribtues this reader can read
     */
    int getAttributeCount();

    /**
     * Retrieve the AttributeType at the given index.
     *
     * @return AttributeType at given index
     */
    AttributeDescriptor getAttributeDescriptor(int index) throws ArrayIndexOutOfBoundsException;

    /**
     * Retrieve all AttributeDescriptors.
     * 
     * @return Array of all AttributeDescriptor in index order
     */
    AttributeDescriptor[] getAttributeDescriptors();

    /**
     * Release any resources associated with this reader
     */
    void close() throws IOException;

    /**
     * Does another set of attributes exist in this reader?
     *
     * @return <code>true</code> if additional content exists for AttributeReader
     */
    boolean hasNext() throws IOException;

    /**
     * Advance the reader to the next set of attributes.
     */
    void next() throws IOException;

    /**
     * Read the attribute at the given index.
     *
     * @return Object Attribute at given index
     */
    Object read(int index) throws IOException, ArrayIndexOutOfBoundsException;

    /**
     * Read all attributs in one call, improves performances when iterating.
     *
     * @param buffer : be sure it is not null and has the good size
     * @throws IOException
     */
    void read(Object[] buffer) throws IOException;
}
