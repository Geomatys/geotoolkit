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
package org.geotoolkit.ows.xml.v110;

import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


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
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}Operation" maxOccurs="unbounded" minOccurs="2"/>
 *         &lt;element name="Parameter" type="{http://www.opengis.net/ows/1.1}DomainType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Constraint" type="{http://www.opengis.net/ows/1.1}DomainType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}ExtendedCapabilities" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "operation",
    "parameter",
    "constraint",
    "extendedCapabilities"
})
@XmlRootElement(name = "OperationsMetadata")
public class OperationsMetadata {

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
    OperationsMetadata(){
    }
    
    /**
     * Build a new operation metadata.
     */
    OperationsMetadata(List<Operation> operation, List<DomainType> parameter, List<DomainType> constraint,
            Object extendedCapabilities){
        
        this.constraint           = constraint;
        this.extendedCapabilities = extendedCapabilities;
        this.operation            = operation;
        this.parameter            = parameter;
    }
    
    /**
     * Metadata for unordered list of all the (requests for) operations that this server interface implements. 
     * The list of required and optional operations implemented shall be specified in the Implementation Specification for this service. 
     * Gets the value of the operation property.
     * 
     */
    public List<Operation> getOperation() {
        return operation;
    }
    
    /**
     * Remove the specified operation from the list of available operations displayed in the capabilities document.
     * 
     * @param operationName the name of the operation to remove.
     */
    public void removeOperation(String operationName) {
        for (Operation op: operation){
            if (op.getName().equals(operationName)) {
                operation.remove(op);
                return;
            }
        }
    }
    
    /**
     * Return the operation for the specified name
     */
    public Operation getOperation(String operationName) {
        for (Operation op: operation){
            if (op.getName().equals(operationName)) {
                return op;
            }
        }
        return null;
    }

    /**
     * Gets the value of the parameter property.
     */
    public List<DomainType> getParameter() {
        return Collections.unmodifiableList(parameter);
    }

    /**
     * Gets the value of the constraint property.
     */
    public List<DomainType> getConstraint() {
        return Collections.unmodifiableList(constraint);
    }

    /**
     * Gets the value of the extendedCapabilities property.
     * 
     */
    public Object getExtendedCapabilities() {
        return extendedCapabilities;
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

            return Utilities.equals(this.constraint,           that.constraint)           &&
                   Utilities.equals(this.extendedCapabilities, that.extendedCapabilities) &&
                   Utilities.equals(this.operation,            that.operation)            &&
                   Utilities.equals(this.parameter,            that.parameter);
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
        StringBuilder s = new StringBuilder();
        s.append("Constraint:").append('\n');
        if (constraint == null) {
            s.append("null");
        } else {
            for (int i = 0; i < constraint.size(); i++) {
                s.append(constraint.get(i).toString()).append('\n');
            }
        }
        s.append("Operation:").append('\n');
        if (operation == null) {
            s.append("null");
        } else {
            for (int i = 0; i < operation.size(); i++) {
                if (operation.get(i)!= null)
                    s.append(operation.get(i).toString()).append('\n');
                else
                    s.append("operation n" + i + " is null").append('\n');
            }
        }
        s.append("Parameter:").append('\n');
        if (parameter == null) {
            s.append("null");
        } else {
            for (int i = 0; i < parameter.size(); i++) {
                s.append(parameter.get(i).toString()).append('\n');
            }
        }
        if (extendedCapabilities != null) {
            s.append(extendedCapabilities.toString());
        }
        return s.toString();
    }

}
