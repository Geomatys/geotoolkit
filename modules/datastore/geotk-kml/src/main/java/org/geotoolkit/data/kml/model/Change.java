/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.data.kml.model;

import java.util.List;

/**
 * <p>This interface maps Change element.</p>
 *
 * <pre>
 * &lt;element name="Change" type="kml:ChangeType"/>
 *
 * &lt;complexType name="ChangeType">
 *  &lt;sequence>
 *      &lt;element ref="kml:AbstractObjectGroup" minOccurs="0" maxOccurs="unbounded"/>
 *  &lt;/sequence>
 * &lt;/complexType>
 * </pre>
 *
 * @author Samuel Andrés
 * @module pending
 */
public interface Change {

    /**
     *
     * @return the lis of AbstractObjects
     */
    List<Object> getObjects();

    /**
     * 
     * @param objects
     */
    void setObjects(List<Object> objects);
}
