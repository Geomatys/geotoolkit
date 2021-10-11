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
package org.geotoolkit.ows.xml.v100;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
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
 *         &lt;element ref="{http://www.opengis.net/ows}DCP" maxOccurs="unbounded"/>
 *         &lt;element name="Parameter" type="{http://www.opengis.net/ows}DomainType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Constraint" type="{http://www.opengis.net/ows}DomainType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows}Metadata" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
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
    @XmlElement(name = "Metadata")
    private List<MetadataType> metadata;
    @XmlAttribute(required = true)
    private String name;

    /**
     * Empty constructor used by JAXB.
     */
    Operation(){
    }

    /**
     * Build a new Operation.
     */
    public Operation(final List<DCP> dcp, final List<DomainType> parameter, final List<DomainType> constraint,
            final List<MetadataType> metadata, final String name){
        this.constraint = constraint;
        this.dcp        = dcp;
        this.metadata   = metadata;
        this.name       = name;
        this.parameter  = parameter;
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
                this.metadata   = new ArrayList<MetadataType>();
                for (MetadataType m : that.metadata) {
                    this.metadata.add(new MetadataType(m));
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
     * Unordered list of Distributed Computing Platforms (DCPs) supported for this operation.
     * At present, only the HTTP DCP is defined, so this element will appear only once.
     * Gets the value of the dcp property.
     */
    @Override
    public List<DCP> getDCP() {
        return dcp;
    }

    /**
     * Gets the value of the parameter property.
     */
    @Override
    public List<DomainType> getParameter() {
        if (parameter == null) {
            parameter = new ArrayList<DomainType>();
        }
        return Collections.unmodifiableList(parameter);
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
     * Gets the value of the constraint property.
     */
    @Override
    public List<DomainType> getConstraint() {
        if (constraint == null) {
            constraint = new ArrayList<DomainType>();
        }
        return Collections.unmodifiableList(constraint);
    }

    @Override
    public DomainType getConstraint(final String name) {
        if (constraint != null) {
            for (DomainType d : constraint) {
                if (d.getName().equals(name)) {
                    return d;
                }
            }
            final DomainType d = new DomainType(name, new ArrayList<String>());
            constraint.add(d);
            return d;
        } else {
            return null;
        }
    }

    @Override
    public DomainType getConstraintIgnoreCase(final String name) {
        if (constraint != null) {
            for (DomainType d : constraint) {
                if (d.getName().equalsIgnoreCase(name)) {
                    return d;
                }
            }
            final DomainType d = new DomainType(name, new ArrayList<String>());
            constraint.add(d);
            return d;
        } else {
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
                dom.setValue(new ArrayList<String>(values));
            }
        }
    }

    /**
     * Update the specified parameter with the specified range.
     */
    @Override
    public void updateParameter(final String parameterName, final Range range) {
        throw new UnsupportedOperationException("unsupported in OWS version 1.0.0");
    }

    /**
     * Optional unordered list of additional metadata about this operation and its' implementation.
     * A list of required and optional metadata elements for this operation should be specified in the Implementation Specification for this service.
     * (Informative: This metadata might specify the operation request parameters or provide the XML Schemas for the operation request.)
     * Gets the value of the metadata property.
     */
    public List<MetadataType> getMetadata() {
        if (metadata == null) {
            metadata = new ArrayList<MetadataType>();
        }
        return Collections.unmodifiableList(metadata);
    }

    /**
     * Gets the value of the name property.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Verify that this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof Operation) {
            final Operation that = (Operation) object;
            return Objects.equals(this.constraint, that.constraint) &&
                   Objects.equals(this.dcp,        that.dcp)        &&
                   Objects.equals(this.metadata,   that.metadata)   &&
                   Objects.equals(this.parameter,  that.parameter)  &&
                   Objects.equals(this.name,       that.name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + (this.dcp != null ? this.dcp.hashCode() : 0);
        hash = 71 * hash + (this.parameter != null ? this.parameter.hashCode() : 0);
        hash = 71 * hash + (this.constraint != null ? this.constraint.hashCode() : 0);
        hash = 71 * hash + (this.metadata != null ? this.metadata.hashCode() : 0);
        hash = 71 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("Constraint:").append('\n');
        if (constraint != null) {
            for (int i = 0; i < constraint.size(); i++) {
                s.append(constraint.get(i).toString()).append('\n');
            }
        }
        if (dcp != null) {
            s.append("dcp:").append('\n');
            for (int i = 0; i < dcp.size(); i++) {
                s.append(dcp.get(i).toString()).append('\n');
            }
        }
        if (metadata != null) {
            s.append("metadata:").append('\n');
            for (int i = 0; i < metadata.size(); i++) {
                s.append(metadata.get(i).toString()).append('\n');
            }
        }
        if (parameter != null) {
            s.append("parameter:").append('\n');
            for (int i = 0; i < parameter.size(); i++) {
                s.append(parameter.get(i).toString()).append('\n');
            }
        }
        if (name == null) {
            s.append("Name is null").append('\n');
        } else {
            s.append(name).append('\n');
        }
        return s.toString();
    }
}
