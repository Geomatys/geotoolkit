/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.ebrim.xml.v300;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ServiceBindingType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ServiceBindingType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}RegistryObjectType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}SpecificationLink" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="service" use="required" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}referenceURI" />
 *       &lt;attribute name="accessURI" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="targetBinding" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}referenceURI" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ServiceBindingType", propOrder = {
    "specificationLink"
})
@XmlRootElement(name = "ServiceBinding")
public class ServiceBindingType extends RegistryObjectType {

    @XmlElement(name = "SpecificationLink")
    private List<SpecificationLinkType> specificationLink;
    @XmlAttribute(required = true)
    private String service;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String accessURI;
    @XmlAttribute
    private String targetBinding;

    /**
     * Gets the value of the specificationLink property.
     */
    public List<SpecificationLinkType> getSpecificationLink() {
        if (specificationLink == null) {
            specificationLink = new ArrayList<SpecificationLinkType>();
        }
        return this.specificationLink;
    }
    
    /**
     * Sets the value of the specificationLink property.
     */
    public void setSpecificationLink(SpecificationLinkType specification) {
        if (this.specificationLink == null) {
            this.specificationLink = new ArrayList<SpecificationLinkType>();
        }
        this.specificationLink.add(specification);
    }
    
    /**
     * Sets the value of the specificationLink property.
     */
    public void setSpecificationLink(List<SpecificationLinkType> specification) {
        this.specificationLink = specification;
    }

    /**
     * Gets the value of the service property.
     */
    public String getService() {
        return service;
    }

    /**
     * Sets the value of the service property.
     */
    public void setService(String value) {
        this.service = value;
    }

    /**
     * Gets the value of the accessURI property.
     */
    public String getAccessURI() {
        return accessURI;
    }

    /**
     * Sets the value of the accessURI property.
     */
    public void setAccessURI(String value) {
        this.accessURI = value;
    }

    /**
     * Gets the value of the targetBinding property.
     */
    public String getTargetBinding() {
        return targetBinding;
    }

    /**
     * Sets the value of the targetBinding property.
     */
    public void setTargetBinding(String value) {
        this.targetBinding = value;
    }

}
