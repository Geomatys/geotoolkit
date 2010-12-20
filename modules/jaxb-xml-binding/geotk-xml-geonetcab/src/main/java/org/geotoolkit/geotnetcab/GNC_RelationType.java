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


/**
 * <p>Java class for GNC_RelationType_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GNC_RelationType_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.isotc211.org/2005/gco}AbstractObject_Type">
 *       &lt;sequence>
 *         &lt;element name="relationName" type="{http://www.mdweb-project.org/files/xsd}GNC_RelationNameCode_PropertyType"/>
 *         &lt;element name="organisationIdentifier" type="{http://www.mdweb-project.org/files/xsd}GNC_OrganisationEntitie_PropertyType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GNC_RelationType_Type", propOrder = {
    "relationName",
    "organisationIdentifier"
})
public class GNC_RelationType {

    @XmlElement(required = true)
    private GNC_RelationNameCode relationName;
    @XmlElement(required = true)
    private List<GNC_OrganisationEntitie> organisationIdentifier;

    /**
     * Gets the value of the relationName property.
     * 
     * @return
     *     possible object is
     *     {@link GNCRelationNameCodePropertyType }
     *     
     */
    public GNC_RelationNameCode getRelationName() {
        return relationName;
    }

    /**
     * Sets the value of the relationName property.
     * 
     * @param value
     *     allowed object is
     *     {@link GNCRelationNameCodePropertyType }
     *     
     */
    public void setRelationName(GNC_RelationNameCode value) {
        this.relationName = value;
    }

    /**
     * Gets the value of the organisationIdentifier property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link GNCOrganisationEntitiePropertyType }
     * 
     * 
     */
    public List<GNC_OrganisationEntitie> getOrganisationIdentifier() {
        if (organisationIdentifier == null) {
            organisationIdentifier = new ArrayList<GNC_OrganisationEntitie>();
        }
        return this.organisationIdentifier;
    }

    public void setOrganisationIdentifier(List<GNC_OrganisationEntitie> organisationIdentifier) {
        this.organisationIdentifier = organisationIdentifier;
    }

    public void setOrganisationIdentifier(GNC_OrganisationEntitie organisationIdentifier) {
        if (this.organisationIdentifier == null) {
            this.organisationIdentifier = new ArrayList<GNC_OrganisationEntitie>();
        }
        this.organisationIdentifier.add(organisationIdentifier);
    }

}
