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

import java.net.URI;
import java.util.List;

/**
 * <p>This interface maps SchemaData element.</p>
 *
 * <pre>
 * &lt;element name="SchemaData" type="kml:SchemaDataType" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * &lt;complexType name="SchemaDataType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractObjectType">
 *          &lt;sequence>
 *              &lt;element ref="kml:SimpleData" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:SchemaDataExtension" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *          &lt;attribute name="schemaUrl" type="anyURI"/>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="SchemaDataExtension" abstract="true"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface SchemaData extends AbstractObject {

    /**
     *
     * @return
     */
    URI getSchemaURL();

    /**
     *
     * @return the list of SimpleData simple extensions.
     */
    List<SimpleData> getSimpleDatas();

    /**
     *
     * @return the list of SimpleData object extensions.
     */
    List<Object> getSchemaDataExtensions();

    /**
     *
     * @return
     */
    void setSchemaURL(URI schemaURL);

    /**
     *
     * @return the list of SimpleData simple extensions.
     */
    void setSimpleDatas(List<SimpleData> simpleDatas);

    /**
     *
     * @return the list of SimpleData object extensions.
     */
    void setSchemaDataExtensions(List<Object> schemaDataExtensions);

}
