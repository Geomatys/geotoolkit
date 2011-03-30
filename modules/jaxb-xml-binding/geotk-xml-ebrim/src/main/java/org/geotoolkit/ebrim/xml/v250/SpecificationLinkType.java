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
package org.geotoolkit.ebrim.xml.v250;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for SpecificationLinkType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SpecificationLinkType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}RegistryObjectType">
 *       &lt;sequence minOccurs="0">
 *         &lt;element ref="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}UsageDescription" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}UsageParameter" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="serviceBinding" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="specificationObject" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
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
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String serviceBinding;
    @XmlAttribute(required = true)
    @XmlSchemaType(name = "anyURI")
    private String specificationObject;

    /**
     * Gets the value of the usageDescription property.
     */
    public InternationalStringType getUsageDescription() {
        return usageDescription;
    }

    /**
     * Sets the value of the usageDescription property.
     */
    public void setUsageDescription(final InternationalStringType value) {
        this.usageDescription = value;
    }

    /**
     * Gets the value of the usageParameter property.
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        if (serviceBinding != null) {
            sb.append("serviceBinding:").append(serviceBinding).append('\n');
        }
        if (specificationObject != null) {
            sb.append("specificationObject:").append(specificationObject).append('\n');
        }
        if (usageDescription != null) {
            sb.append("usageDescription:").append(usageDescription).append('\n');
        }
        if (usageParameter != null) {
            sb.append("usageParameter:\n");
            for (String sbi : usageParameter) {
                sb.append(sbi).append('\n');
            }
        }
        return sb.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof SpecificationLinkType && super.equals(obj)) {
            final SpecificationLinkType that = (SpecificationLinkType) obj;
            return Utilities.equals(this.serviceBinding,      that.serviceBinding) &&
                   Utilities.equals(this.specificationObject, that.specificationObject) &&
                   Utilities.equals(this.usageDescription,    that.usageDescription) &&
                   Utilities.equals(this.usageParameter,      that.usageParameter);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + super.hashCode();
        hash = 53 * hash + (this.usageDescription != null ? this.usageDescription.hashCode() : 0);
        hash = 53 * hash + (this.usageParameter != null ? this.usageParameter.hashCode() : 0);
        hash = 53 * hash + (this.serviceBinding != null ? this.serviceBinding.hashCode() : 0);
        hash = 53 * hash + (this.specificationObject != null ? this.specificationObject.hashCode() : 0);
        return hash;
    }
}
