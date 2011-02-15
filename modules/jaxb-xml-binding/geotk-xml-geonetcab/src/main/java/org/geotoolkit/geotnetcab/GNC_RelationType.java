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
import org.geotoolkit.util.Utilities;


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
public class GNC_RelationType implements org.opengis.metadata.geonetcab.GNC_RelationType {

    @XmlElement(required = true)
    private GNC_RelationNameCode relationName;
    @XmlElement(required = true)
    private List<String> organisationIdentifier;

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
    public void setRelationName(final GNC_RelationNameCode value) {
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
    @Override
    public List<String> getOrganisationIdentifier() {
        if (organisationIdentifier == null) {
            organisationIdentifier = new ArrayList<String>();
        }
        return this.organisationIdentifier;
    }

    public void setOrganisationIdentifier(final List<String> organisationIdentifier) {
        this.organisationIdentifier = organisationIdentifier;
    }

    public void setOrganisationIdentifier(final String organisationIdentifier) {
        if (this.organisationIdentifier == null) {
            this.organisationIdentifier = new ArrayList<String>();
        }
        this.organisationIdentifier.add(organisationIdentifier);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof GNC_RelationType) {
            GNC_RelationType that = (GNC_RelationType) obj;
            return Utilities.equals(this.organisationIdentifier, that.organisationIdentifier) &&
                   Utilities.equals(this.relationName, that.relationName);

        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + (this.relationName != null ? this.relationName.hashCode() : 0);
        hash = 67 * hash + (this.organisationIdentifier != null ? this.organisationIdentifier.hashCode() : 0);
        return hash;
    }
}
