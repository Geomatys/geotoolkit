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
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SpecificationLinkType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SpecificationLinkType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}RegistryObjectType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}UsageDescription" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}UsageParameter" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="serviceBinding" use="required" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}referenceURI" />
 *       &lt;attribute name="specificationObject" use="required" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}referenceURI" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SpecificationLinkType", propOrder = {
    "usageDescription",
    "usageParameter"
})
@XmlRootElement(name = "SpecificationLink")
public class SpecificationLinkType extends RegistryObjectType {

    @XmlElement(name = "UsageDescription")
    private InternationalStringType usageDescription;
    @XmlElement(name = "UsageParameter")
    private List<String> usageParameter;
    @XmlAttribute(required = true)
    private String serviceBinding;
    @XmlAttribute(required = true)
    private String specificationObject;

    /**
     * Gets the value of the usageDescription property.
     */
    public InternationalStringType getUsageDescription() {
        return usageDescription;
    }

    /**
     * Sets the value of the usageDescription property.
     * 
     */
    public void setUsageDescription(final InternationalStringType value) {
        this.usageDescription = value;
    }

    /**
     * Gets the value of the usageParameter property.
     * 
     */
    public List<String> getUsageParameter() {
        if (usageParameter == null) {
            usageParameter = new ArrayList<String>();
        }
        return this.usageParameter;
    }
    
    /**
     * Sets the value of the usageParameter property.
     */
    public void setUsageParameter(final String usageParameter) {
        if (this.usageParameter == null) {
            this.usageParameter = new ArrayList<String>();
        }
        this.usageParameter.add(usageParameter);
    }
    
    /**
     * Sets the value of the usageParameter property.
     */
    public void setUsageParameter(final List<String> usageParameter) {
        this.usageParameter = usageParameter;
    }

    /**
     * Gets the value of the serviceBinding property.
     */
    public String getServiceBinding() {
        return serviceBinding;
    }

    /**
     * Sets the value of the serviceBinding property.
     */
    public void setServiceBinding(final String value) {
        this.serviceBinding = value;
    }

    /**
     * Gets the value of the specificationObject property.
     */
    public String getSpecificationObject() {
        return specificationObject;
    }

    /**
     * Sets the value of the specificationObject property.
     */
    public void setSpecificationObject(final String value) {
        this.specificationObject = value;
    }

}
