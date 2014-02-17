/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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

package org.geotoolkit.ows.xml.v200;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.AbstractOperation;
import org.geotoolkit.ows.xml.Range;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ows/2.0}DCP" maxOccurs="unbounded"/>
 *         &lt;element name="Parameter" type="{http://www.opengis.net/ows/2.0}DomainType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Constraint" type="{http://www.opengis.net/ows/2.0}DomainType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows/2.0}Metadata" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "dcp",
    "parameter",
    "constraint",
    "metadata"
})
@XmlRootElement(name = "Operation")
public class Operation implements AbstractOperation {

    @XmlElement(name = "DCP", required = true)
    private List<DCP> dcp;
    @XmlElement(name = "Parameter")
    private List<DomainType> parameter;
    @XmlElement(name = "Constraint")
    private List<DomainType> constraint;
    @XmlElementRef(name = "Metadata", namespace = "http://www.opengis.net/ows/2.0", type = JAXBElement.class)
    private List<JAXBElement<? extends MetadataType>> metadata;
    @XmlAttribute(required = true)
    private String name;

    /**
     * Empty constructor used by JAXB.
     */
    Operation(){
    }
    
    public Operation(final Operation that){
        if (that != null) {
            if (that.constraint != null) {
                this.constraint = new ArrayList<DomainType>();
                for (DomainType d : that.constraint) {
                    this.constraint.add(new DomainType(d));
                }
            }
            if (that.dcp != null) {
                this.dcp        = new ArrayList<DCP>();
                for (DCP d : that.dcp) {
                    this.dcp.add(new DCP(d));
                }
            }
            if (that.metadata != null) {
                final ObjectFactory factory = new ObjectFactory();
                this.metadata   =  new ArrayList<JAXBElement<? extends MetadataType>>();
                for (JAXBElement<? extends MetadataType> m : that.metadata) {
                    this.metadata.add(factory.createMetadata(new MetadataType(m.getValue())));
                }
            }
            this.name       = that.name;
            if (that.parameter != null) {
                this.parameter = new ArrayList<DomainType>();
                for (DomainType d : that.parameter) {
                    this.parameter.add(new DomainType(d));
                }
            }
        }
    }
    
    /**
     * Build a new Operation.
     */
    public Operation(final List<DCP> dcp, final List<DomainType> parameter, final List<DomainType> constraint,
            final List<JAXBElement<? extends MetadataType>> metadata, final String name){
        this.constraint = constraint;
        this.dcp        = dcp;
        this.metadata   = metadata;
        this.name       = name;
        this.parameter  = parameter;
    }
    
    /**
     * Unordered list of Distributed Computing Platforms
     *             (DCPs) supported for this operation. At present, only the HTTP DCP
     *             is defined, so this element will appear only once.Gets the value of the dcp property.
     * 
     */
    @Override
    public List<DCP> getDCP() {
        if (dcp == null) {
            dcp = new ArrayList<DCP>();
        }
        return this.dcp;
    }

    /**
     * Gets the value of the parameter property.
     * 
     */
    @Override
    public List<DomainType> getParameter() {
        if (parameter == null) {
            parameter = new ArrayList<DomainType>();
        }
        return this.parameter;
    }
    
    /**
     * Get a parameter from the specified parameter name
     */
    @Override
    public DomainType getParameter(final String name) {
        if (parameter == null) {
            return null;
        } else {
            for (DomainType domain: parameter) {
                if (domain.getName().equals(name)) {
                    return domain;
                }
            }
            return null;
        }
    }

    /**
     * Get a parameter from the specified parameter name
     */
    @Override
    public DomainType getParameterIgnoreCase(final String name) {
        if (parameter == null) {
            return null;
        } else {
            for (DomainType domain: parameter) {
                if (domain.getName().equalsIgnoreCase(name)) {
                    return domain;
                }
            }
            return null;
        }
    }
    
    /**
     * Update the specified parameter with the list of values.
     */
    @Override
    public void updateParameter(final String parameterName, final Collection<String> values) {
        for (DomainType dom: parameter) {
            if (dom.getName().equals(parameterName)) {
                dom.setAllowedValues(new AllowedValues(values));
            }
        }
    }
    
    /**
     * Update the specified parameter with the specified range.
     */
    @Override
    public void updateParameter(final String parameterName, final Range range) {
        for (DomainType dom: parameter) {
            if (dom.getName().equals(parameterName)) {
                dom.setAllowedValues(new AllowedValues(new RangeType(range))); 
            }
        }
    }

    /**
     * Gets the value of the constraint property.
     * 
     */
    @Override
    public List<DomainType> getConstraint() {
        if (constraint == null) {
            constraint = new ArrayList<DomainType>();
        }
        return this.constraint;
    }
    
    @Override
    public DomainType getConstraint(final String name) {
        if (constraint != null) {
            for (DomainType d : constraint) {
                if (d.getName().equals(name)) {
                    return d;
                }
            }
        }
        return null;

    }

    @Override
    public DomainType getConstraintIgnoreCase(final String name) {
        if (constraint != null) {
            for (DomainType d : constraint) {
                if (d.getName().equalsIgnoreCase(name)) {
                    return d;
                }
            }
        }
        return null;
    }

    /**
     * Optional unordered list of additional metadata
     *             about this operation and its' implementation. A list of required
     *             and optional metadata elements for this operation should be
     *             specified in the Implementation Specification for this service.
     *             (Informative: This metadata might specify the operation request
     *             parameters or provide the XML Schemas for the operation
     *             request.) Gets the value of the metadata property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link MetadataType }{@code >}
     * {@link JAXBElement }{@code <}{@link AdditionalParametersType }{@code >}
     * 
     * 
     */
    public List<JAXBElement<? extends MetadataType>> getMetadata() {
        if (metadata == null) {
            metadata = new ArrayList<JAXBElement<? extends MetadataType>>();
        }
        return this.metadata;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

}
