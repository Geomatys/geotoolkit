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
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GNC_OrganisationEntitie_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GNC_OrganisationEntitie_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.mdweb-project.org/files/xsd}GNC_Resource_Type">
 *       &lt;sequence>
 *         &lt;element name="typeOfOrganisation" type="{http://www.mdweb-project.org/files/xsd}GNC_OrganisationTypeCode_PropertyType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GNC_OrganisationEntitie_Type", propOrder = {
    "typeOfOrganisation"
})
@XmlRootElement(name = "GNC_OrganisationEntitie", namespace = "http://www.mdweb-project.org/files/xsd")
public class GNC_OrganisationEntitie extends GNC_Resource implements org.opengis.metadata.geonetcab.GNC_OrganisationEntitie {

    @XmlElement(required = true)
    private GNC_OrganisationTypeCode typeOfOrganisation;

    @XmlTransient
    private String href;
    

    /**
     * Gets the value of the typeOfOrganisation property.
     * 
     * @return
     *     possible object is
     *     {@link GNCOrganisationTypeCodePropertyType }
     *     
     */
    @Override
    public GNC_OrganisationTypeCode getTypeOfOrganisation() {
        return typeOfOrganisation;
    }

    /**
     * Sets the value of the typeOfOrganisation property.
     * 
     * @param value
     *     allowed object is
     *     {@link GNCOrganisationTypeCodePropertyType }
     *     
     */
    public void setTypeOfOrganisation(final GNC_OrganisationTypeCode value) {
        this.typeOfOrganisation = value;
    }

    /**
     * @return the href
     */
    @Override
    public String getHref() {
        return href;
    }

    /**
     * @param href the href to set
     */
    public void setHref(String href) {
        this.href = href;
    }

}
