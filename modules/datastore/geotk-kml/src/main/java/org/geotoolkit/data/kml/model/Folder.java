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
import org.geotoolkit.data.kml.xsd.SimpleType;

/**
 * <p>This interface maps Foder element.</p>
 *
 * <pre>
 * &lt;element name="Folder" type="kml:FolderType" substitutionGroup="kml:AbstractContainerGroup"/>
 *
 * &lt;complexType name="FolderType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractContainerType">
 *          &lt;sequence>
 *              &lt;element ref="kml:AbstractFeatureGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:FolderSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:FolderObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="FolderSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="FolderObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface Folder extends AbstractContainer {

    /**
     *
     * @return
     */
    public List<AbstractFeature> getAbstractFeatures();

    /**
     *
     * @param abstractFeatures
     */
    public void setAbstractFeatures(List<AbstractFeature> abstractFeatures);

}
