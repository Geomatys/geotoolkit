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
package org.geotoolkit.wfs.xml.v110;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import org.geotoolkit.util.Utilities;

/**
 * An Insert element may contain a feature collection or one or more feature instances to be inserted into the 
 * repository.
 *          
 * 
 * <p>Java class for InsertElementType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InsertElementType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml}_Feature" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="idgen" type="{http://www.opengis.net/wfs}IdentifierGenerationOptionType" default="GenerateNew" />
 *       &lt;attribute name="handle" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="inputFormat" type="{http://www.w3.org/2001/XMLSchema}string" default="text/xml; subtype=gml/3.1.1" />
 *       &lt;attribute name="srsName" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InsertElementType", propOrder = {
    "feature"
})
public class InsertElementType {

    @XmlAnyElement(lax = true)
    private List<Object> feature;
    @XmlAttribute
    private IdentifierGenerationOptionType idgen;
    @XmlAttribute
    private String handle;
    @XmlAttribute
    private String inputFormat;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String srsName;

    public InsertElementType() {

    }

    public InsertElementType(final Object feature) {
         this.feature = new ArrayList<Object>();
         this.feature.add(feature);
    }

    public InsertElementType(final List<Object> feature) {
         this.feature = feature;
    }

    /**
     * Gets the value of the feature property.
     */
    public List<Object> getFeature() {
        if (feature == null) {
            feature = new ArrayList<Object>();
        }
        return this.feature;
    }

    /**
     * Gets the value of the idgen property.
     * 
     * @return
     *     possible object is
     *     {@link IdentifierGenerationOptionType }
     *     
     */
    public IdentifierGenerationOptionType getIdgen() {
        if (idgen == null) {
            return IdentifierGenerationOptionType.GENERATE_NEW;
        } else {
            return idgen;
        }
    }

    /**
     * Sets the value of the idgen property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdentifierGenerationOptionType }
     *     
     */
    public void setIdgen(final IdentifierGenerationOptionType value) {
        this.idgen = value;
    }

    /**
     * Gets the value of the handle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHandle() {
        return handle;
    }

    /**
     * Sets the value of the handle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHandle(final String value) {
        this.handle = value;
    }

    /**
     * Gets the value of the inputFormat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInputFormat() {
        if (inputFormat == null) {
            return "text/xml; subtype=gml/3.1.1";
        } else {
            return inputFormat;
        }
    }

    /**
     * Sets the value of the inputFormat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInputFormat(final String value) {
        this.inputFormat = value;
    }

    /**
     * Gets the value of the srsName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSrsName() {
        return srsName;
    }

    /**
     * Sets the value of the srsName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSrsName(final String value) {
        this.srsName = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[InsertElementType]\n");
        if (idgen != null) {
            sb.append("idgen").append(idgen).append('\n');
        }
        if (inputFormat != null) {
            sb.append("inputFormat").append(inputFormat).append('\n');
        }
        if (handle != null) {
            sb.append("handle").append(handle).append('\n');
        }
        if (srsName != null) {
            sb.append("srsName").append(srsName).append('\n');
        }
        if (feature != null) {
            sb.append("feature:\n");
            for (Object q : feature) {
                sb.append(q).append("\nclass:").append(q.getClass().getName());
            }
        }
        return sb.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (obj instanceof InsertElementType) {
            InsertElementType that = (InsertElementType) obj;
            return Utilities.equals(this.feature, that.feature) &&
                   Utilities.equals(this.idgen, that.idgen) &&
                   Utilities.equals(this.handle, that.handle) &&
                   Utilities.equals(this.inputFormat, that.inputFormat) &&
                   Utilities.equals(this.srsName, that.srsName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.feature != null ? this.feature.hashCode() : 0);
        hash = 17 * hash + (this.idgen != null ? this.idgen.hashCode() : 0);
        hash = 17 * hash + (this.handle != null ? this.handle.hashCode() : 0);
        hash = 17 * hash + (this.inputFormat != null ? this.inputFormat.hashCode() : 0);
        hash = 17 * hash + (this.srsName != null ? this.srsName.hashCode() : 0);
        return hash;
    }
}
