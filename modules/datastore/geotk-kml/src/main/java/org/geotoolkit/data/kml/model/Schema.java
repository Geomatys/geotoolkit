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
 * <p>This interface maps Schema element.</p>
 *
 * <pre>
 * &lt;element name="Schema" type="kml:SchemaType"/>
 *
 * &lt;complexType name="SchemaType" final="#all">
 *  &lt;sequence>
 *      &lt;element ref="kml:SimpleField" minOccurs="0" maxOccurs="unbounded"/>
 *      &lt;element ref="kml:SchemaExtension" minOccurs="0" maxOccurs="unbounded"/>
 *  &lt;/sequence>
 *  &lt;attribute name="name" type="string"/>
 *  &lt;attribute name="id" type="ID"/>
 * &lt;/complexType>
 *
 * &lt;element name="SchemaExtension" abstract="true"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface Schema {

    /**
     *
     * @return
     */
    List<SimpleField> getSimpleFields();

    /**
     *
     * @return
     */
    String getName();

    /**
     * 
     * @return
     */
    String getId();

    /**
     *
     * @return
     */
    List<Object> getSchemaExtensions();

    /**
     *
     * @param simpleFields
     */
    void setSimpleFields(List<SimpleField> simpleFields);

    /**
     *
     * @param name
     */
    void setName(String name);

    /**
     *
     * @param id
     */
    void setId(String id);

    /**
     * 
     * @param schemaExtensions
     */
    void setSchemaExtensions(List<Object> schemaExtensions);
}
