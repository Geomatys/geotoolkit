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
 * <p>This interface maps Document element.</p>
 *
 * <pre>
 * &lt;element name="Document" type="kml:DocumentType" substitutionGroup="kml:AbstractContainerGroup"/>
 *
 * &lt;complexType name="DocumentType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractContainerType">
 *          &lt;sequence>
 *              &lt;element ref="kml:Schema" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:AbstractFeatureGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:DocumentSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:DocumentObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="DocumentSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="DocumentObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface Document extends AbstractContainer {

    /**
     *
     * @return
     */
    public List<Schema> getSchemas();

    /**
     *
     * @return
     */
    public List<AbstractFeature> getAbstractFeatures();

    /**
     *
     * @param schemas
     */
    public void setSchemas(List<Schema> schemas);

    /**
     *
     * @param abstractFeatures
     */
    public void setAbstractFeatures(List<AbstractFeature> features);
}
