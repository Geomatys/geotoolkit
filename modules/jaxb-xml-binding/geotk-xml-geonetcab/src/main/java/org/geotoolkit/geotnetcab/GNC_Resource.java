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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.metadata.iso.identification.DefaultDataIdentification;
import org.opengis.metadata.citation.OnlineResource;


/**
 * <p>Java class for GNC_Resource_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GNC_Resource_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.isotc211.org/2005/gmd}MD_DataIdentification_Type">
 *       &lt;sequence>
 *         &lt;element name="onlineInformation" type="{http://www.isotc211.org/2005/gmd}CI_OnlineResource_PropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="references" type="{http://www.mdweb-project.org/files/xsd}GNC_Resource_PropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GNC_Resource_Type", propOrder = {
    "onlineInformation",
    "references"
})
@XmlSeeAlso({
    GNC_OrganisationEntitie.class,
    GNC_MaterialResource.class
})
@XmlRootElement(name = "GNC_Resource", namespace = "http://www.mdweb-project.org/files/xsd")
public class GNC_Resource extends DefaultDataIdentification {

    private List<OnlineResource> onlineInformation;
    private List<GNC_Resource> references;

    /**
     * Gets the value of the onlineInformation property.
     * 
     */
    public List<OnlineResource> getOnlineInformation() {
        if (onlineInformation == null) {
            onlineInformation = new ArrayList<OnlineResource>();
        }
        return this.onlineInformation;
    }

    /**
     * Sets the value of the onlineInformation property.
     *
     */
    public void setOnlineInformation(List<OnlineResource> onlineInformation) {
        this.onlineInformation = onlineInformation;
    }

    /**
     * Sets the value of the onlineInformation property.
     *
     */
    public void setOnlineInformation(OnlineResource onlineInformation) {
        if (this.onlineInformation == null) {
            this.onlineInformation = new ArrayList<OnlineResource>();
        }
        this.onlineInformation.add(onlineInformation);
    }

    /**
     * Gets the value of the references property.
     * 
     */
    public List<GNC_Resource> getReferences() {
        if (references == null) {
            references = new ArrayList<GNC_Resource>();
        }
        return this.references;
    }

    /**
     * Sets the value of the references property.
     *
     */
    public void setReferences(List<GNC_Resource> references) {
        this.references = references;
    }

    /**
     * Sets the value of the references property.
     *
     */
    public void setReferences(GNC_Resource references) {
        if (this.references == null) {
            this.references = new ArrayList<GNC_Resource>();
        }
        this.references.add(references);
    }

}
