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
 *
 * <p>This interface maps Data element.</p>
 *
 * <pre>
 * &lt;element name="Data" type="kml:DataType" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * &lt;complexType name="DataType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractObjectType">
 *          &lt;sequence>
 *              &lt;element ref="kml:displayName" minOccurs="0"/>
 *              &lt;element ref="kml:value"/>
 *              &lt;element ref="kml:DataExtension" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *          &lt;attribute name="name" type="string"/>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="DataExtension" abstract="true"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface Data extends AbstractObject {

    /**
     *
     * @return the name.
     */
    public String getName();

    /**
     *
     * @return the display name.
     */
    public Object getDisplayName();

    /**
     *
     * @return the value.
     */
    public String getValue();

    /**
     *
     * @return the list of data extensions.
     */
    public List<Object> getDataExtensions();

    /**
     *
     * @param name
     */
    public void setName(String name);

    /**
     *
     * @param displayName
     */
    public void setDisplayName(String displayName);

    /**
     *
     * @param value
     */
    public void setValue(String value);

    /**
     *
     * @param dataExtensions
     */
    public void setDataExtensions(List<Object> dataExtensions);
}
