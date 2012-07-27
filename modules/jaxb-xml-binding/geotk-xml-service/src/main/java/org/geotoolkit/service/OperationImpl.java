/*
 *    GeotoolKit - An Open Source Java GIS Toolkit
 *    http://geotoolkit.org
 * 
 *    (C) 2009, Geomatys
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

package org.geotoolkit.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.opengis.service.Operation;
import org.opengis.service.Parameter;
import org.opengis.util.MemberName;


/**
 * <p>Java class for SV_Operation_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SV_Operation_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.isotc211.org/2005/gco}AbstractObject_Type">
 *       &lt;sequence>
 *         &lt;element name="operationName" type="{http://www.isotc211.org/2005/gco}MemberName_PropertyType"/>
 *         &lt;element name="dependsOn" type="{http://www.isotc211.org/2005/srv}SV_Operation_PropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="parameter" type="{http://www.isotc211.org/2005/srv}SV_Parameter_PropertyType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SV_Operation_Type", propOrder = {
    "operationName",
    "dependsOn",
    "parameter"
})
@XmlRootElement(name="SV_Operation")
public class OperationImpl implements Operation {

    @XmlElement(required = true)
    private MemberName operationName;
    private Collection<Operation> dependsOn;
    @XmlElement(required = true)
    private Parameter parameter;

    /**
     * An empty constructor used by JAXB
     */
    public OperationImpl() {
        
    }
    
    /**
     * Clone an Operation
     */
    public OperationImpl(final Operation operation) {
        this.dependsOn     = operation.getDependsOn();
        this.operationName = operation.getOperationName();
        this.parameter     = operation.getParameter();
    }
    /**
     * Gets the value of the operationName property.
     * 
    */
    public MemberName getOperationName() {
        return operationName;
    }

    /**
     * Sets the value of the operationName property.
     */
    public void setOperationName(final MemberName value) {
        this.operationName = value;
    }

    /**
     * Gets the value of the dependsOn property.
     */
    public Collection<Operation> getDependsOn() {
        if (dependsOn == null) {
            dependsOn = new ArrayList<Operation>();
        }
        return this.dependsOn;
    }
    
    public void setDependsOn(final Collection<Operation> dependsOn) {
         this.dependsOn = dependsOn;
    }
    
    public void setDependsOn(final Operation dependsOn) {
        if (this.dependsOn == null) {
            this.dependsOn = new ArrayList<Operation>();
        }
        this.dependsOn.add(dependsOn);
    }

    /**
     * Gets the value of the parameter property.
     * 
     */
    public Parameter getParameter() {
        return parameter;
    }

    /**
     * Sets the value of the parameter property.
     * 
     */
    public void setParameter(final Parameter value) {
        this.parameter = value;
    }

}
