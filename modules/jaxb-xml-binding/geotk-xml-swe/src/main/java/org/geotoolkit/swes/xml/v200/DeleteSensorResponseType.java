/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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

package org.geotoolkit.swes.xml.v200;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.swes.xml.DeleteSensorResponse;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for DeleteSensorResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DeleteSensorResponseType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swes/2.0}ExtensibleResponseType">
 *       &lt;sequence>
 *         &lt;element name="deletedProcedure" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DeleteSensorResponseType", propOrder = {
    "deletedProcedure"
})
@XmlRootElement(name="DeleteSensorResponse")
public class DeleteSensorResponseType extends ExtensibleResponseType implements DeleteSensorResponse {

    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    private String deletedProcedure;

    public DeleteSensorResponseType() {
        
    }
    
    public DeleteSensorResponseType(final String deletedProcedure) {
        this.deletedProcedure = deletedProcedure;
    }
    
    /**
     * Gets the value of the deletedProcedure property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeletedProcedure() {
        return deletedProcedure;
    }

    /**
     * Sets the value of the deletedProcedure property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeletedProcedure(String value) {
        this.deletedProcedure = value;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        if (deletedProcedure != null) {
            sb.append("deletedProcedure:").append(deletedProcedure).append('\n');
        }
        return sb.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof DeleteSensorResponseType && super.equals(obj)) {
            final DeleteSensorResponseType that = (DeleteSensorResponseType)obj;
            return Utilities.equals(this.deletedProcedure, that.deletedProcedure);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + super.hashCode();
        hash = 97 * hash + (this.deletedProcedure != null ? this.deletedProcedure.hashCode() : 0);
        return hash;
    }
}
