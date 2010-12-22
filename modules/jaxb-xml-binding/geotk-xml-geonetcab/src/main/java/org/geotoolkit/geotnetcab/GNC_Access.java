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
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;

/**
 * <p>Java class for GNC_Access_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GNC_Access_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.isotc211.org/2005/gco}AbstractObject_Type">
 *       &lt;sequence>
 *         &lt;element name="description" type="{http://www.isotc211.org/2005/gco}CharacterString_PropertyType" minOccurs="0"/>
 *         &lt;element name="detailAccessConstraints" type="{http://www.mdweb-project.org/files/xsd}GNC_AccessConstraints_PropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GNC_Access_Type", propOrder = {
    "description",
    "detailAccessConstraints"
})
public class GNC_Access implements org.opengis.metadata.geonetcab.GNC_Access{

    private String description;
    private List<GNC_AccessConstraints> detailAccessConstraints;

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the detailAccessConstraints property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link GNCAccessConstraintsPropertyType }
     * 
     * 
     */
    @Override
    public List<GNC_AccessConstraints> getDetailAccessConstraints() {
        if (detailAccessConstraints == null) {
            detailAccessConstraints = new ArrayList<GNC_AccessConstraints>();
        }
        return this.detailAccessConstraints;
    }

    public void setDetailAccessConstraints(List<GNC_AccessConstraints> detailAccessConstraints) {
        this.detailAccessConstraints = detailAccessConstraints;
    }

    public void setDetailAccessConstraints(GNC_AccessConstraints detailAccessConstraints) {
        if (this.detailAccessConstraints == null) {
            this.detailAccessConstraints = new ArrayList<GNC_AccessConstraints>();
        }
        this.detailAccessConstraints.add(detailAccessConstraints);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof GNC_Access) {
            GNC_Access that = (GNC_Access) obj;
            return Utilities.equals(this.description, that.description) &&
                   Utilities.equals(this.detailAccessConstraints, that.detailAccessConstraints);

        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 13 * hash + (this.description != null ? this.description.hashCode() : 0);
        hash = 13 * hash + (this.detailAccessConstraints != null ? this.detailAccessConstraints.hashCode() : 0);
        return hash;
    }
}
