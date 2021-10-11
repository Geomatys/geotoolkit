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
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
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
 *         &lt;element ref="{http://www.opengis.net/ows/2.0}Operation" maxOccurs="unbounded" minOccurs="2"/>
 *         &lt;element name="Parameter" type="{http://www.opengis.net/ows/2.0}DomainType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Constraint" type="{http://www.opengis.net/ows/2.0}DomainType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows/2.0}ExtendedCapabilities" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
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
    @XmlElement(name = "ExtendedCapabilities")
    private Object extendedCapabilities;

    /**
     * Empty constructor used by JAXB.
     */
    public OperationsMetadata() {

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
            this.extendedCapabilities = that.extendedCapabilities;
        }
    }

    /**
     * Build a new operation metadata.
     *
     * @param operation
     * @param parameter
     * @param constraint
     * @param extendedCapabilities
     */
    public OperationsMetadata(final List<Operation> operation, final List<DomainType> parameter, final List<DomainType> constraint,
            final Object extendedCapabilities){

        this.constraint           = constraint;
        this.extendedCapabilities = extendedCapabilities;
        this.operation            = operation;
        this.parameter            = parameter;
    }


    /**
     * Metadata for unordered list of all the (requests for) operations that this server interface implements.
     * The list of required and optional operations implemented shall be specified in
     * the Implementation Specification for this service.Gets the value of the operation property.
     *
     * @return
     */
    @Override
    public List<Operation> getOperation() {
        if (operation == null) {
            operation = new ArrayList<>();
        }
        return this.operation;
    }

    /**
     * Return the operation for the specified name
     * @param operationName
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
            if (op.getName().equals(operationName)) {
                operation.remove(op);
                return;
            }
        }
    }

    /**
     * Gets the value of the parameter property.
     *
     */
    public List<DomainType> getParameter() {
        if (parameter == null) {
            parameter = new ArrayList<>();
        }
        return this.parameter;
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
     *
     */
    public List<DomainType> getConstraint() {
        if (constraint == null) {
            constraint = new ArrayList<>();
        }
        return this.constraint;
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

    /**
     * Gets the value of the extendedCapabilities property.
     *
     * @return
     *     possible object is
     *     {@link Object }
     *
     */
    @Override
    public Object getExtendedCapabilities() {
        return extendedCapabilities;
    }

    /**
     * Sets the value of the extendedCapabilities property.
     *
     * @param value
     *     allowed object is
     *     {@link Object }
     *
     */
    @Override
    public void setExtendedCapabilities(Object value) {
        this.extendedCapabilities = value;
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
                for (OnlineResourceType method: dcp.getHTTP().getGetOrPost()) {
                    method.setHref(url);
                }
            }
       }
    }

    @Override
    public AbstractOperationsMetadata clone() {
        return new OperationsMetadata(this);
    }

}
