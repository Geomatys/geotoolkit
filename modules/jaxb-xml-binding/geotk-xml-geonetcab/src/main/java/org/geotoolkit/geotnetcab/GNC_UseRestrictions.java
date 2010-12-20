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
import javax.xml.bind.annotation.XmlType;
import org.opengis.metadata.extent.GeographicDescription;


/**
 * <p>Java class for GNC_UseRestrictions_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GNC_UseRestrictions_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.isotc211.org/2005/gco}AbstractObject_Type">
 *       &lt;sequence>
 *         &lt;element name="extentOfRestrictionsl" type="{http://www.isotc211.org/2005/gmd}EX_GeographicDescription_PropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="geographicRestrictions" type="{http://www.isotc211.org/2005/gco}Boolean_PropertyType"/>
 *         &lt;element name="otherConstraints" type="{http://www.isotc211.org/2005/gco}CharacterString_PropertyType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GNC_UseRestrictions_Type", propOrder = {
    "extentOfRestrictionsl",
    "geographicRestrictions",
    "otherConstraints"
})
public class GNC_UseRestrictions {

    private List<GeographicDescription> extentOfRestrictionsl;
    @XmlElement(required = true)
    private Boolean geographicRestrictions;
    private String otherConstraints;

    /**
     * Gets the value of the extentOfRestrictionsl property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link GeographicDescription }
     * 
     * 
     */
    public List<GeographicDescription> getExtentOfRestrictionsl() {
        if (extentOfRestrictionsl == null) {
            extentOfRestrictionsl = new ArrayList<GeographicDescription>();
        }
        return this.extentOfRestrictionsl;
    }

    public void setExtentOfRestrictionsl(List<GeographicDescription> extentOfRestrictionsl) {
        this.extentOfRestrictionsl = extentOfRestrictionsl;
    }

    public void setExtentOfRestrictionsl(GeographicDescription extentOfRestrictionsl) {
        if (this.extentOfRestrictionsl == null) {
            this.extentOfRestrictionsl = new ArrayList<GeographicDescription>();
        }
        this.extentOfRestrictionsl.add(extentOfRestrictionsl);
    }

    /**
     * Gets the value of the geographicRestrictions property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean getGeographicRestrictions() {
        return geographicRestrictions;
    }

    /**
     * Sets the value of the geographicRestrictions property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setGeographicRestrictions(Boolean value) {
        this.geographicRestrictions = value;
    }

    /**
     * Gets the value of the otherConstraints property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOtherConstraints() {
        return otherConstraints;
    }

    /**
     * Sets the value of the otherConstraints property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOtherConstraints(String value) {
        this.otherConstraints = value;
    }

}
