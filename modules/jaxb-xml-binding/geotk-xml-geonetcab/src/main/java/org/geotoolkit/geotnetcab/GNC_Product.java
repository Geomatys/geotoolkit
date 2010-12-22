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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GNC_Product_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GNC_Product_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.mdweb-project.org/files/xsd}GNC_MaterialResource_Type">
 *       &lt;sequence>
 *         &lt;element name="typeOfProducts" type="{http://www.mdweb-project.org/files/xsd}GNC_ProductTypeCode_PropertyType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GNC_Product_Type", propOrder = {
    "typeOfProducts"
})
@XmlSeeAlso({
    GNC_Training.class,
    GNC_Software.class,
    GNC_EOProduct.class
})
@XmlRootElement(name = "GNC_Product", namespace = "http://www.mdweb-project.org/files/xsd")
public class GNC_Product extends GNC_MaterialResource implements org.opengis.metadata.geonetcab.GNC_Product {

    @XmlElement(required = true)
    private GNC_ProductTypeCode typeOfProducts;

    /**
     * Gets the value of the typeOfProducts property.
     * 
     * @return
     *     possible object is
     *     {@link GNCProductTypeCodePropertyType }
     *     
     */
    public GNC_ProductTypeCode getTypeOfProducts() {
        return typeOfProducts;
    }

    /**
     * Sets the value of the typeOfProducts property.
     * 
     * @param value
     *     allowed object is
     *     {@link GNCProductTypeCodePropertyType }
     *     
     */
    public void setTypeOfProducts(GNC_ProductTypeCode value) {
        this.typeOfProducts = value;
    }

}
