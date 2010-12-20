/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2010, Open Source Geospatial Foundation (OSGeo)
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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */

package org.geotoolkit.geotnetcab;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GNC_Document_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GNC_Document_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.mdweb-project.org/files/xsd}GNC_MaterialResource_Type">
 *       &lt;sequence>
 *         &lt;element name="typeOfDocuments" type="{http://www.mdweb-project.org/files/xsd}GNC_DocumentTypeCode_PropertyType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GNC_Document_Type", propOrder = {
    "typeOfDocuments"
})
@XmlRootElement(name = "GNC_Document", namespace = "http://www.mdweb-project.org/files/xsd")
public class GNC_Document extends GNC_MaterialResource {

    @XmlElement(required = true)
    private GNC_DocumentsTypeCode typeOfDocuments;

    /**
     * Gets the value of the typeOfDocuments property.
     * 
     * @return
     *     possible object is
     *     {@link GNCDocumentTypeCodePropertyType }
     *     
     */
    public GNC_DocumentsTypeCode getTypeOfDocuments() {
        return typeOfDocuments;
    }

    /**
     * Sets the value of the typeOfDocuments property.
     * 
     * @param value
     *     allowed object is
     *     {@link GNCDocumentTypeCodePropertyType }
     *     
     */
    public void setTypeOfDocuments(GNC_DocumentsTypeCode value) {
        this.typeOfDocuments = value;
    }

}
