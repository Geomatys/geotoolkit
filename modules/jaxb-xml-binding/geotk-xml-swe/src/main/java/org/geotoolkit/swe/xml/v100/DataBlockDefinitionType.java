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
package org.geotoolkit.swe.xml.v100;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.swe.xml.AbstractDataComponent;
import org.geotoolkit.swe.xml.DataBlockDefinition;


/**
 * <p>Java class for DataBlockDefinitionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DataBlockDefinitionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="components" type="{http://www.opengis.net/swe/1.0}DataComponentPropertyType"/>
 *         &lt;element name="encoding" type="{http://www.opengis.net/swe/1.0}BlockEncodingPropertyType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DataBlockDefinitionType", propOrder = {
    "components",
    "encoding"
})
public class DataBlockDefinitionType implements DataBlockDefinition {

    @XmlElement(required = true)
    private DataComponentPropertyType components;
    @XmlElement(required = true)
    private BlockEncodingPropertyType encoding;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    private String id;

    /**
     * constructeur utilisé par jaxB
     */
    DataBlockDefinitionType() {}

    public DataBlockDefinitionType(final DataBlockDefinition db) {
        if (db != null) {
            this.id = db.getId();
            if (db.getEncoding() != null) {
                this.encoding = new BlockEncodingPropertyType(db.getEncoding());
            }
            if (db.getComponents() != null && db.getComponents().size() > 0) {
                this.components = new DataComponentPropertyType(db.getComponents().iterator().next());
            }
        }
    }
    
    /**
     * Gets the value of the components property.
     */
    public Collection<? extends AbstractDataComponentType> getComponents() {
        return Arrays.asList((AbstractDataComponentType)components.getValue());
    }

    public DataComponentPropertyType getRealComponent() {
        return components;
    }

    /**
     * Sets the value of the components property.
     */
    public void setComponents(final DataComponentPropertyType value) {
        this.components = value;
    }

    /**
     * Gets the value of the encoding property.
     */
    public BlockEncodingPropertyType getEncoding() {
        return encoding;
    }

    /**
     * Sets the value of the encoding property.
     */
    public void setEncoding(final BlockEncodingPropertyType value) {
        this.encoding = value;
    }

    /**
     * Gets the value of the id property.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     */
    public void setId(final String value) {
        this.id = value;
    }

}
