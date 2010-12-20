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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GNC_Software_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GNC_Software_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.mdweb-project.org/files/xsd}GNC_Product_Type">
 *       &lt;sequence>
 *         &lt;element name="applicationField" type="{http://www.mdweb-project.org/files/xsd}GNC_ApplicationFieldCode_PropertyType" maxOccurs="unbounded"/>
 *         &lt;element name="typeOf" type="{http://www.mdweb-project.org/files/xsd}GNC_SoftwareTypeCode_PropertyType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GNC_Software_Type", propOrder = {
    "applicationField",
    "typeOf"
})
@XmlRootElement(name = "GNC_Software", namespace = "http://www.mdweb-project.org/files/xsd")
public class GNC_Software extends GNC_Product {

    @XmlElement(required = true)
    private List<GNC_ApplicationFieldCode> applicationField;
    @XmlElement(required = true)
    private GNC_SoftwareTypeCode typeOf;

    /**
     * Gets the value of the applicationField property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link GNCApplicationFieldCodePropertyType }
     * 
     * 
     */
    public List<GNC_ApplicationFieldCode> getApplicationField() {
        if (applicationField == null) {
            applicationField = new ArrayList<GNC_ApplicationFieldCode>();
        }
        return this.applicationField;
    }

    /**
     * Gets the value of the typeOf property.
     * 
     * @return
     *     possible object is
     *     {@link GNCSoftwareTypeCodePropertyType }
     *     
     */
    public GNC_SoftwareTypeCode getTypeOf() {
        return typeOf;
    }

    /**
     * Sets the value of the typeOf property.
     * 
     * @param value
     *     allowed object is
     *     {@link GNCSoftwareTypeCodePropertyType }
     *     
     */
    public void setTypeOf(GNC_SoftwareTypeCode value) {
        this.typeOf = value;
    }

}
