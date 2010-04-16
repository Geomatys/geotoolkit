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
import org.geotoolkit.swe.xml.DataBlockDefinitionProperty;
import org.geotoolkit.swe.xml.DataStreamDefinition;


/**
 * <p>Java class for DataStreamDefinitionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DataStreamDefinitionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="streamComponent" type="{http://www.opengis.net/swe/1.0}DataBlockDefinitionPropertyType" maxOccurs="unbounded"/>
 *         &lt;element name="streamEncoding" type="{http://www.opengis.net/swe/1.0}MultiplexedStreamFormatPropertyType"/>
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
@XmlType(name = "DataStreamDefinitionType", propOrder = {
    "streamComponent",
    "streamEncoding"
})
public class DataStreamDefinitionType implements DataStreamDefinition {

    @XmlElement(required = true)
    private List<DataBlockDefinitionPropertyType> streamComponent;
    @XmlElement(required = true)
    private MultiplexedStreamFormatPropertyType streamEncoding;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    private String id;

    public DataStreamDefinitionType() {

    }

    public DataStreamDefinitionType(DataStreamDefinition da) {
        if (da != null) {
            this.id = da.getId();
            if (da.getStreamEncoding() != null) {
                this.streamEncoding = new MultiplexedStreamFormatPropertyType(da.getStreamEncoding());
            }
            if (da.getStreamComponent() != null) {
                this.streamComponent = new ArrayList<DataBlockDefinitionPropertyType>();
                for (DataBlockDefinitionProperty db : da.getStreamComponent()) {
                    this.streamComponent.add(new DataBlockDefinitionPropertyType(db));
                }
            }
            this.id = da.getId();
        }
    }
    
    /**
     * Gets the value of the streamComponent property.
     */
    public List<DataBlockDefinitionPropertyType> getStreamComponent() {
        if (streamComponent == null) {
            streamComponent = new ArrayList<DataBlockDefinitionPropertyType>();
        }
        return this.streamComponent;
    }

    /**
     * Gets the value of the streamEncoding property.
     * 
     * @return
     *     possible object is
     *     {@link MultiplexedStreamFormatPropertyType }
     *     
     */
    public MultiplexedStreamFormatPropertyType getStreamEncoding() {
        return streamEncoding;
    }

    /**
     * Sets the value of the streamEncoding property.
     */
    public void setStreamEncoding(MultiplexedStreamFormatPropertyType value) {
        this.streamEncoding = value;
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
    public void setId(String value) {
        this.id = value;
    }

}
