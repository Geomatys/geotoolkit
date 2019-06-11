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
package org.geotoolkit.csw.xml.v202;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.csw.xml.GetRecordById;


/**
 * Convenience operation to retrieve default record representations by identifier.
 *    Id - object identifier (a URI) that provides a reference to a catalogue item
 *         (or a result set if the catalogue supports persistent result sets).
 *
 *    ElementSetName - one of "brief, "summary", or "full"
 *
 *
 * <p>Java class for GetRecordByIdType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="GetRecordByIdType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/cat/csw/2.0.2}RequestBaseType">
 *       &lt;sequence>
 *         &lt;element name="Id" type="{http://www.w3.org/2001/XMLSchema}anyURI" maxOccurs="unbounded"/>
 *         &lt;element ref="{http://www.opengis.net/cat/csw/2.0.2}ElementSetName" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="outputFormat" type="{http://www.w3.org/2001/XMLSchema}string" default="application/xml" />
 *       &lt;attribute name="outputSchema" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetRecordByIdType", propOrder = {
    "id",
    "elementSetName"
})
@XmlRootElement(name="GetRecordById")
public class GetRecordByIdType extends RequestBaseType implements GetRecordById {

    @XmlElement(name = "Id", required = true)
    @XmlSchemaType(name = "anyURI")
    private List<String> id;
    @XmlElement(name = "ElementSetName", defaultValue = "summary")
    private ElementSetNameType elementSetName;
    @XmlAttribute
    private String outputFormat;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String outputSchema;

    /**
     * An empty constructor used by JAXB
     */
     GetRecordByIdType(){

     }

     /**
     * An empty constructor used by JAXB
     */
     public GetRecordByIdType(final String service, final String version, final ElementSetNameType elementSetName,
             final String outputFormat, final String outputSchema, final List<String> id){
         super(service, version);
         this.elementSetName = elementSetName;
         this.outputFormat   = outputFormat;
         this.outputSchema   = outputSchema;
         this.id             = id;
     }

    /**
     * Gets the value of the id property.
     */
    @Override
    public List<String> getId() {
        if (id == null) {
            id = new ArrayList<>();
        }
        return id;
    }

    public void setId(final List<String> id) {
        this.id = id;
    }

    /**
     * Gets the value of the elementSetName property.
     */
    @Override
    public ElementSetNameType getElementSetName() {
        return elementSetName;
    }

    public void setElementSetName(final ElementSetNameType elementSetName) {
        this.elementSetName = elementSetName;
    }

    /**
     * Gets the value of the outputFormat property.
     */
    @Override
    public String getOutputFormat() {
        return outputFormat;
    }

    /**
     * Gets the value of the outputFormat property.
     */
    @Override
    public void setOutputFormat(final String outputFormat) {
        this.outputFormat = outputFormat;
    }

    /**
     * Gets the value of the outputSchema property.
     */
    @Override
    public String getOutputSchema() {
        return outputSchema;
    }

    public void setOutputSchema(final String outputSchema) {
        this.outputSchema = outputSchema;
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof GetRecordByIdType && super.equals(object)) {
            final GetRecordByIdType that = (GetRecordByIdType) object;
            return Objects.equals(this.elementSetName, that.elementSetName) &&
                   Objects.equals(this.outputFormat,   that.outputFormat)   &&
                   Objects.equals(this.outputSchema,   that.outputSchema)   &&
                   Objects.equals(this.id,             that.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 59 * hash + (this.elementSetName != null ? this.elementSetName.hashCode() : 0);
        hash = 59 * hash + (this.outputFormat != null ? this.outputFormat.hashCode() : 0);
        hash = 59 * hash + (this.outputSchema != null ? this.outputSchema.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString());

        if (elementSetName != null) {
            s.append("elementSetName: ").append(elementSetName).append('\n');
        }
        if (outputFormat != null) {
            s.append("outputFormat: ").append(outputFormat).append('\n');
        }
        if (outputSchema != null) {
            s.append("outputSchema: ").append(outputSchema).append('\n');
        }
        if (id != null) {
            s.append("id: ").append('\n');
            for (String i : id) {
                s.append(i).append('\n');
            }
        }
        return s.toString();
    }
}
