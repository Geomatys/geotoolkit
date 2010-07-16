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
 * <p>This interface maps SimpleField element.</p>
 *
 * <pre>
 * &lt;element name="SimpleField" type="kml:SimpleFieldType"/>
 *
 * &lt;complexType name="SimpleFieldType" final="#all">
 *  &lt;sequence>
 *      &lt;element ref="kml:displayName" minOccurs="0"/>
 *      &lt;element ref="kml:SimpleFieldExtension" minOccurs="0" maxOccurs="unbounded"/>
 *  &lt;/sequence>
 *  &lt;attribute name="type" type="string"/>
 *  &lt;attribute name="name" type="string"/>
 *  &lt;/complexType>
 * 
 *  &lt;element name="SimpleFieldExtension" abstract="true"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface SimpleField {

    /**
     *
     * @return
     */
    public Object getDisplayName();

    /**
     *
     * @return
     */
    public String getType();

    /**
     * 
     * @return
     */
    public String getName();

    /**
     *
     * @return
     */
    public List<Object> getSimpleFieldExtensions();

    /**
     *
     * @param displayName
     */
    public void setDisplayName(Object displayName);

    /**
     *
     * @param type
     */
    public void setType(String type);

    /**
     *
     * @param name
     */
    public void setName(String name);

    /**
     * 
     * @param simpleFieldExtensions
     */
    public void setSimpleFieldExtensions(List<Object> simpleFieldExtensions);
}
