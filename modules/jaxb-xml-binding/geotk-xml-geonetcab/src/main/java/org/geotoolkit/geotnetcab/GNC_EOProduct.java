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
import org.opengis.metadata.lineage.Lineage;


/**
 * <p>Java class for GNC_EOProduct_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GNC_EOProduct_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.mdweb-project.org/files/xsd}GNC_Product_Type">
 *       &lt;sequence>
 *         &lt;element name="lineage" type="{http://www.isotc211.org/2005/gmd}LI_Lineage_PropertyType"/>
 *         &lt;element name="typeOfEOProducts" type="{http://www.mdweb-project.org/files/xsd}GNC_EOProductTypeCode_PropertyType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GNC_EOProduct_Type", propOrder = {
    "lineage",
    "typeOfEOProducts"
})
@XmlRootElement(name = "GNC_EOProduct", namespace = "http://www.mdweb-project.org/files/xsd")
public class GNC_EOProduct extends GNC_Product implements org.opengis.metadata.geonetcab.GNC_EOProduct {

    @XmlElement(required = true)
    private Lineage lineage;
    @XmlElement(required = true)
    private GNC_EOProductTypeCode typeOfEOProducts;

    /**
     * Gets the value of the lineage property.
     * 
     * @return
     *     possible object is
     *     {@link Lineage }
     *     
     */
    @Override
    public Lineage getLineage() {
        return lineage;
    }

    /**
     * Sets the value of the lineage property.
     * 
     * @param value
     *     allowed object is
     *     {@link Lineage }
     *     
     */
    public void setLineage(final Lineage value) {
        this.lineage = value;
    }

    /**
     * Gets the value of the typeOfEOProducts property.
     * 
     * @return
     *     possible object is
     *     {@link GNCEOProductTypeCodePropertyType }
     *     
     */
    public GNC_EOProductTypeCode getTypeOfEOProducts() {
        return typeOfEOProducts;
    }

    /**
     * Sets the value of the typeOfEOProducts property.
     * 
     * @param value
     *     allowed object is
     *     {@link GNCEOProductTypeCodePropertyType }
     *     
     */
    public void setTypeOfEOProducts(final GNC_EOProductTypeCode value) {
        this.typeOfEOProducts = value;
    }

}
