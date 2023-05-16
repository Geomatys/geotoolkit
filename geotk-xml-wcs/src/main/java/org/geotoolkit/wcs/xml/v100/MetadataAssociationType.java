/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.wcs.xml.v100;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311.MetaDataPropertyType;
import org.geotoolkit.ows.xml.AbstractMetadata;


/**
 * Refers to a metadata package that contains metadata properties for an object.
 *
 * <p>Java class for MetadataAssociationType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="MetadataAssociationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.opengis.net/gml}MetaDataPropertyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.opengis.net/gml}AssociationAttributeGroup"/>
 *       &lt;attribute name="about" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * @author Guilhem Legal
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MetadataAssociationType")
@XmlSeeAlso({
    MetadataLinkType.class
})
public class MetadataAssociationType extends MetaDataPropertyType implements AbstractMetadata{

    /**
     * An empty constructor used by JAXB
     */
    MetadataAssociationType() {
        super(null);
    }

    /**
     * build a metadata association
     */
    public MetadataAssociationType(final String href) {
        super(href);
    }

    @Override
    public Object getAbstractMetaData() {
        return super.getMetaData();
    }

}
