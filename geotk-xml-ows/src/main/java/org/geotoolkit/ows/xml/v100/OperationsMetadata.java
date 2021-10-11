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
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.inspire.xml.MultiLingualCapabilities;
import org.geotoolkit.ows.xml.AbstractDomain;
import org.geotoolkit.ows.xml.AbstractOperationsMetadata;


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
 *         &lt;element ref="{http://www.opengis.net/ows}Operation" maxOccurs="unbounded" minOccurs="2"/>
 *         &lt;element name="Parameter" type="{http://www.opengis.net/ows}DomainType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Constraint" type="{http://www.opengis.net/ows}DomainType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows}ExtendedCapabilities" minOccurs="0"/>
 *       &lt;/sequence>
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
    "operation",
    "parameter",
    "constraint",
    "extendedCapabilities"
})
@XmlRootElement(name = "OperationsMetadata")
public class OperationsMetadata implements AbstractOperationsMetadata {

    @XmlElement(name = "Operation", required = true)
    private List<Operation> operation;
    @XmlElement(name = "Parameter")
    private List<DomainType> parameter;
    @XmlElement(name = "Constraint")
    private List<DomainType> constraint;

    // TODO find a way to set the type to AbstractExtendedCapabilitiesType
    @XmlElement(name = "ExtendedCapabilities")
    private MultiLingualCapabilities extendedCapabilities;

    /**
     * Empty constructor used by JAXB.
     */
    public OperationsMetadata(){
    }

    /**
     * Build a new operation metadata.
     * @param operation
     * @param parameter
     * @param constraint
     * @param extendedCapabilities
     */
    public OperationsMetadata(final List<Operation> operation, final List<DomainType> parameter, final List<DomainType> constraint,
            final MultiLingualCapabilities extendedCapabilities){

        this.constraint           = constraint;
        this.extendedCapabilities = extendedCapabilities;
        this.operation            = operation;
        this.parameter            = parameter;
    }

    public OperationsMetadata(final OperationsMetadata that){
        if (that != null)  {
            if (that.constraint != null) {
                this.constraint = new ArrayList<>();
                for (DomainType d : that.constraint) {
                    this.constraint.add(new DomainType(d));
                }
            }
            if (that.parameter != null) {
                this.parameter = new ArrayList<>();
                for (DomainType d : that.parameter) {
                    this.parameter.add(new DomainType(d));
                }
            }
            if (that.operation != null) {
                this.operation = new ArrayList<>();
                for (Operation d : that.operation) {
                    this.operation.add(new Operation(d));
                }
            }
            // unable to clone this attribute of type object
            if (that.extendedCapabilities != null) {
                this.extendedCapabilities = new MultiLingualCapabilities(that.extendedCapabilities);
            }
        }
    }

    /**
     * Metadata for unordered list of all the (requests for) operations that this server interface implements.
     * The list of required and optional operations implemented shall be specified in the Implementation Specification for this service.
     * Gets the value of the operation property.
     *
     */
    @Override
    public List<Operation> getOperation() {
        if (operation == null) {
            operation = new ArrayList<>();
        }
        return operation;
    }

    /**
     * @param operationName
     * @return the operation for the specified name
     */
    @Override
    public Operation getOperation(final String operationName) {
        for (Operation op: operation){
            if (op.getName().equalsIgnoreCase(operationName)) {
                return op;
            }
        }
        return null;
    }

    /**
     * Remove the specified operation from the list of available operations displayed in the capabilities document.
     *
     * @param operationName the name of the operation to remove.
     */
    @Override
    public void removeOperation(final String operationName) {
        for (Operation op: operation){
            if (op.getName().equalsIgnoreCase(operationName)) {
                operation.remove(op);
                return;
            }
        }
    }

    /**
     * Update all the url in a OWS capabilities document.
     *
     * @param url The url of the web application.
     */
    @Override
    public void updateURL(final String url) {
       for (Operation op: operation) {
            for (DCP dcp: op.getDCP()) {
                for (OnlineResourceType method:dcp.getHTTP().getGetOrPost()) {
                    method.setHref(url);
                }
            }
       }
    }

    /**
     * Gets the value of the parameter property.
     */
    public List<DomainType> getParameter() {
        if (parameter == null) {
            parameter = new ArrayList<>();
        }
        return Collections.unmodifiableList(parameter);
    }

    @Override
    public DomainType getParameter(final String name) {
        if (parameter == null) {
            parameter = new ArrayList<>();
        }
        for (DomainType d : parameter) {
            if (d.getName().equalsIgnoreCase(name)) {
                return d;
            }
        }
        return null;
    }

    /**
     * Gets the value of the constraint property.
     */
    public List<DomainType> getConstraint() {
        if (constraint == null) {
            constraint = new ArrayList<>();
        }
        return constraint;
    }

    @Override
    public DomainType getConstraint(final String name) {
        if (constraint == null) {
            constraint = new ArrayList<>();
        }
        for (DomainType d : constraint) {
            if (d.getName().equalsIgnoreCase(name)) {
                return d;
            }
        }
        return null;
    }

    @Override
    public void addConstraint(final AbstractDomain domain) {
        if (constraint == null) {
            constraint = new ArrayList<>();
        }
        if (domain instanceof DomainType) {
            constraint.add((DomainType)domain);
        } else if (domain != null) {
            throw new IllegalArgumentException("bad version of the domain object");
        }
    }

    @Override
    public void removeConstraint(final String name) {
        if (constraint == null) {
            constraint = new ArrayList<>();
        }
        for (DomainType d : constraint) {
            if (d.getName().equalsIgnoreCase(name)) {
                constraint.remove(d);
            }
        }
    }

     public void removeConstraint(final DomainType constraint) {
        if (this.constraint == null) {
            this.constraint = new ArrayList<>();
        }
        for (DomainType d : this.constraint) {
            if (d.equals(constraint)) {
                this.constraint.remove(d);
            }
        }
    }

    /**
     * Gets the value of the extendedCapabilities property.
     *
     */
    @Override
    public MultiLingualCapabilities getExtendedCapabilities() {
        return extendedCapabilities;
    }

    /**
     * Gets the value of the extendedCapabilities property.
     *
     */
    @Override
    public void setExtendedCapabilities(final Object extendedCapabilities) {
        if (extendedCapabilities instanceof MultiLingualCapabilities) {
            this.extendedCapabilities = (MultiLingualCapabilities) extendedCapabilities;
        } else {
            throw new IllegalArgumentException("only MultiLingualCapabilities type is accepted in this implementation");
        }
    }

    @Override
    public OperationsMetadata clone() {
        return new OperationsMetadata(this);
    }

    /**
     * Verify that this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof OperationsMetadata) {
            final OperationsMetadata that = (OperationsMetadata) object;
            return Objects.equals(this.constraint,           that.constraint)           &&
                   Objects.equals(this.extendedCapabilities, that.extendedCapabilities) &&
                   Objects.equals(this.operation,            that.operation)            &&
                   Objects.equals(this.parameter,            that.parameter);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.operation != null ? this.operation.hashCode() : 0);
        hash = 89 * hash + (this.parameter != null ? this.parameter.hashCode() : 0);
        hash = 89 * hash + (this.constraint != null ? this.constraint.hashCode() : 0);
        hash = 89 * hash + (this.extendedCapabilities != null ? this.extendedCapabilities.hashCode() : 0);
        return hash;
    }

    /**
     *
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[OperationsMetadata]\n");
        if (constraint != null) {
            s.append("Constraint:").append('\n');
            for (int i = 0; i < constraint.size(); i++) {
                s.append(constraint.get(i)).append('\n');
            }
        }

        if (operation != null) {
            s.append("Operation:").append('\n');
            for (int i = 0; i < operation.size(); i++) {
                if (operation.get(i) != null) {
                    s.append(operation.get(i)).append('\n');
                } else {
                    s.append("operation n").append(i).append(" is null").append('\n');
                }
            }
        }
        if (parameter != null) {
            s.append("Parameter:").append('\n');
            for (int i = 0; i < parameter.size(); i++) {
                s.append(parameter.get(i)).append('\n');
            }
        }
        if (extendedCapabilities != null) {
            s.append("extended capabilities:").append(extendedCapabilities).append('\n');
        }
        return s.toString();
    }

}
