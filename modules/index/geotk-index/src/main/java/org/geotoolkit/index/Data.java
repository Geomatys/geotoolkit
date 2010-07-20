/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.index;

/**
 * Holds values (with associated DataDefinition)
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface Data {
    /**
     * Check to see if a <code>Data</code> respects its
     * <code>DataDefinition</code>
     */
    boolean isValid();

    /**
     * DOCUMENT ME!
     * 
     * @param val
     * @return - this Data object
     * @throws TreeException
     */
    Data addValue(Object val) throws TreeException;

    /**
     * Return the KeyDefinition
     */
    DataDefinition getDefinition();

    /**
     * DOCUMENT ME!
     */
    int getValuesCount();

    /**
     * DOCUMENT ME!
     * @param i
     */
    Object getValue(int i);

}
