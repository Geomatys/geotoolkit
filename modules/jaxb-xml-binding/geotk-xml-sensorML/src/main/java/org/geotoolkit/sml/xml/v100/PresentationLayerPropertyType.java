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
package org.geotoolkit.sml.xml.v100;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.sml.xml.AbstractPresentationLayerProperty;
import org.geotoolkit.swe.xml.AbstractDataRecord;
import org.geotoolkit.swe.xml.DataRecord;
import org.geotoolkit.swe.xml.SimpleDataRecord;
import org.geotoolkit.swe.xml.v100.Category;
import org.geotoolkit.swe.xml.v100.DataBlockDefinitionType;
import org.geotoolkit.swe.xml.v100.DataStreamDefinitionType;
import org.geotoolkit.swe.xml.v100.AbstractDataRecordType;
import org.geotoolkit.swe.xml.v100.DataRecordType;
import org.geotoolkit.swe.xml.v100.SimpleDataRecordType;


/**
 * <p>Java class for PresentationLayerPropertyType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PresentationLayerPropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice minOccurs="0">
 *         &lt;element ref="{http://www.opengis.net/swe/1.0}AbstractDataRecord"/>
 *         &lt;element ref="{http://www.opengis.net/swe/1.0}Category"/>
 *         &lt;element ref="{http://www.opengis.net/swe/1.0}DataBlockDefinition"/>
 *         &lt;element ref="{http://www.opengis.net/swe/1.0}DataStreamDefinition"/>
 *       &lt;/choice>
 *       &lt;attGroup ref="{http://www.opengis.net/gml}AssociationAttributeGroup"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PresentationLayerPropertyType", propOrder = {
    "abstractDataRecord",
    "category",
    "dataBlockDefinition",
    "dataStreamDefinition"
})
public class PresentationLayerPropertyType implements AbstractPresentationLayerProperty {

    @XmlElementRef(name = "AbstractDataRecord", namespace = "http://www.opengis.net/swe/1.0", type = JAXBElement.class)
    private JAXBElement<? extends AbstractDataRecordType> abstractDataRecord;
    @XmlElement(name = "Category", namespace = "http://www.opengis.net/swe/1.0")
    private Category category;
    @XmlElement(name = "DataBlockDefinition", namespace = "http://www.opengis.net/swe/1.0")
    private DataBlockDefinitionType dataBlockDefinition;
    @XmlElement(name = "DataStreamDefinition", namespace = "http://www.opengis.net/swe/1.0")
    private DataStreamDefinitionType dataStreamDefinition;
    @XmlAttribute
    private List<String> nilReason;
    @XmlAttribute(namespace = "http://www.opengis.net/gml/")
    private String remoteSchema;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String actuate;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String arcrole;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String href;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String role;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String show;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String title;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String type;

    public PresentationLayerPropertyType() {

    }
    public PresentationLayerPropertyType(final AbstractPresentationLayerProperty la) {
        if (la != null) {
            if (la.getDataBlockDefinition() != null) {
                this.dataBlockDefinition = new DataBlockDefinitionType(la.getDataBlockDefinition());
            }
            if (la.getDataStreamDefinition() != null) {
                this.dataStreamDefinition = new DataStreamDefinitionType(la.getDataStreamDefinition());
            }
            if (la.getCategory() != null) {
                this.category = new Category(la.getCategory());
            }
            if (la.getDataRecord() != null) {
                AbstractDataRecord record = la.getDataRecord();
                org.geotoolkit.swe.xml.v100.ObjectFactory factory = new org.geotoolkit.swe.xml.v100.ObjectFactory();
                if (record instanceof SimpleDataRecord) {
                    abstractDataRecord = factory.createSimpleDataRecord(new SimpleDataRecordType((SimpleDataRecord)record));
                } else if (record instanceof DataRecord) {
                    abstractDataRecord = factory.createDataRecord(new DataRecordType((DataRecord)record));
                } else {
                    System.out.println("UNINPLEMENTED CASE:" + record);
                }
            }
            this.actuate = la.getActuate();
            this.arcrole = la.getArcrole();
            this.href    = la.getHref();
            this.remoteSchema = la.getRemoteSchema();
            this.role         = la.getRole();
            this.show         = la.getShow();
            this.title        = la.getTitle();
            this.type         = la.getType();
        }
    }


    public PresentationLayerPropertyType(final Category category) {
        this.category = category;
    }

    /**
     * Gets the value of the abstractDataRecord property.
     */
    public JAXBElement<? extends AbstractDataRecordType> getAbstractDataRecord() {
        return abstractDataRecord;
    }

    /**
     * Gets the value of the abstractDataRecord property.
     */
    public AbstractDataRecordType getDataRecord() {
        if (abstractDataRecord != null) {
            return abstractDataRecord.getValue();
        }
        return null;
    }
    
    /**
     * Sets the value of the abstractDataRecord property.
     */
    public void setAbstractDataRecord(final JAXBElement<? extends AbstractDataRecordType> value) {
        this.abstractDataRecord = ((JAXBElement<? extends AbstractDataRecordType> ) value);
    }

    /**
     * Gets the value of the category property.
     */
    public Category getCategory() {
        return category;
    }

    /**
     * Sets the value of the category property.
     */
    public void setCategory(final Category value) {
        this.category = value;
    }

    /**
     * Gets the value of the dataBlockDefinition property.
     */
    public DataBlockDefinitionType getDataBlockDefinition() {
        return dataBlockDefinition;
    }

    /**
     * Sets the value of the dataBlockDefinition property.
     * 
     */
    public void setDataBlockDefinition(final DataBlockDefinitionType value) {
        this.dataBlockDefinition = value;
    }

    /**
     * Gets the value of the dataStreamDefinition property.
     */
    public DataStreamDefinitionType getDataStreamDefinition() {
        return dataStreamDefinition;
    }

    /**
     * Sets the value of the dataStreamDefinition property.
     */
    public void setDataStreamDefinition(final DataStreamDefinitionType value) {
        this.dataStreamDefinition = value;
    }

    /**
     * Gets the value of the nilReason property.
     */
    public List<String> getNilReason() {
        if (nilReason == null) {
            nilReason = new ArrayList<String>();
        }
        return this.nilReason;
    }

    /**
     * Gets the value of the remoteSchema property.
     */
    public String getRemoteSchema() {
        return remoteSchema;
    }

    /**
     * Sets the value of the remoteSchema property.
     */
    public void setRemoteSchema(final String value) {
        this.remoteSchema = value;
    }

    /**
     * Gets the value of the actuate property.
     */
    public String getActuate() {
        return actuate;
    }

    /**
     */
    public void setActuate(final String value) {
        this.actuate = value;
    }

    /**
     * Gets the value of the arcrole property.
     */
    public String getArcrole() {
        return arcrole;
    }

    /**
     * Sets the value of the arcrole property.
     */
    public void setArcrole(final String value) {
        this.arcrole = value;
    }

    /**
     * Gets the value of the href property.
     */
    public String getHref() {
        return href;
    }

    /**
     * Sets the value of the href property.
     */
    public void setHref(final String value) {
        this.href = value;
    }

    /**
     * Gets the value of the role property.
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the value of the role property.
     */
    public void setRole(final String value) {
        this.role = value;
    }

    /**
     * Gets the value of the show property.
     */
    public String getShow() {
        return show;
    }

    /**
     * Sets the value of the show property.
     *     
     */
    public void setShow(final String value) {
        this.show = value;
    }

    /**
     * Gets the value of the title property.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     */
    public void setTitle(final String value) {
        this.title = value;
    }

    /**
     * Gets the value of the type property.
     */
    public String getType() {
        return type;
     }

    /**
     * Sets the value of the type property.
     */
    public void setType(final String value) {
        this.type = value;
    }

}
