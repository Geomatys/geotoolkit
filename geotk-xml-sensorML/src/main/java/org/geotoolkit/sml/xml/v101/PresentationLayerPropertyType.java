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
package org.geotoolkit.sml.xml.v101;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.sml.xml.AbstractPresentationLayerProperty;
import org.geotoolkit.swe.xml.AbstractDataRecord;
import org.geotoolkit.swe.xml.DataRecord;
import org.geotoolkit.swe.xml.SimpleDataRecord;
import org.geotoolkit.swe.xml.v101.AbstractDataRecordType;
import org.geotoolkit.swe.xml.v101.Category;
import org.geotoolkit.swe.xml.v101.DataBlockDefinitionType;
import org.geotoolkit.swe.xml.v101.DataRecordType;
import org.geotoolkit.swe.xml.v101.DataStreamDefinitionType;
import org.geotoolkit.swe.xml.v101.SimpleDataRecordType;



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
 *         &lt;element ref="{http://www.opengis.net/swe/1.0.1}AbstractDataRecord"/>
 *         &lt;element ref="{http://www.opengis.net/swe/1.0.1}Category"/>
 *         &lt;element ref="{http://www.opengis.net/swe/1.0.1}DataBlockDefinition"/>
 *         &lt;element ref="{http://www.opengis.net/swe/1.0.1}DataStreamDefinition"/>
 *       &lt;/choice>
 *       &lt;attGroup ref="{http://www.opengis.net/gml}AssociationAttributeGroup"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PresentationLayerPropertyType", propOrder = {
    "abstractDataRecord",
    "category",
    "dataBlockDefinition",
    "dataStreamDefinition"
})
public class PresentationLayerPropertyType extends SensorObject implements AbstractPresentationLayerProperty{

    @XmlElementRef(name = "AbstractDataRecord", namespace = "http://www.opengis.net/swe/1.0.1", type = JAXBElement.class)
    private JAXBElement<? extends AbstractDataRecordType> abstractDataRecord;
    @XmlElement(name = "Category", namespace = "http://www.opengis.net/swe/1.0.1")
    private Category category;
    @XmlElement(name = "DataBlockDefinition", namespace = "http://www.opengis.net/swe/1.0.1")
    private DataBlockDefinitionType dataBlockDefinition;
    @XmlElement(name = "DataStreamDefinition", namespace = "http://www.opengis.net/swe/1.0.1")
    private DataStreamDefinitionType dataStreamDefinition;
    @XmlAttribute(namespace = "http://www.opengis.net/gml")
    @XmlSchemaType(name = "anyURI")
    private String remoteSchema;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String type;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    @XmlSchemaType(name = "anyURI")
    private String href;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    @XmlSchemaType(name = "anyURI")
    private String role;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    @XmlSchemaType(name = "anyURI")
    private String arcrole;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String title;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String show;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String actuate;

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
                org.geotoolkit.swe.xml.v101.ObjectFactory factory = new org.geotoolkit.swe.xml.v101.ObjectFactory();
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

    /**
     * Gets the value of the abstractDataRecord property.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link AbstractDataRecordType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PositionType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ConditionalDataType }{@code >}
     *     {@link JAXBElement }{@code <}{@link GeoLocationArea }{@code >}
     *     {@link JAXBElement }{@code <}{@link VectorType }{@code >}
     *     {@link JAXBElement }{@code <}{@link NormalizedCurveType }{@code >}
     *     {@link JAXBElement }{@code <}{@link EnvelopeType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ConditionalValueType }{@code >}
     *     {@link JAXBElement }{@code <}{@link SimpleDataRecordType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DataRecordType }{@code >}
     *
     */
    public JAXBElement<? extends AbstractDataRecordType> getAbstractDataRecord() {
        return abstractDataRecord;
    }

    public AbstractDataRecordType getDataRecord() {
        if (abstractDataRecord != null) {
            return abstractDataRecord.getValue();
        }
        return null;
    }

    /**
     * Sets the value of the abstractDataRecord property.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link AbstractDataRecordType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PositionType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ConditionalDataType }{@code >}
     *     {@link JAXBElement }{@code <}{@link GeoLocationArea }{@code >}
     *     {@link JAXBElement }{@code <}{@link VectorType }{@code >}
     *     {@link JAXBElement }{@code <}{@link NormalizedCurveType }{@code >}
     *     {@link JAXBElement }{@code <}{@link EnvelopeType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ConditionalValueType }{@code >}
     *     {@link JAXBElement }{@code <}{@link SimpleDataRecordType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DataRecordType }{@code >}
     *
     */
    public void setAbstractDataRecord(final JAXBElement<? extends AbstractDataRecordType> value) {
        this.abstractDataRecord = ((JAXBElement<? extends AbstractDataRecordType> ) value);
    }

    /**
     * Gets the value of the category property.
     *
     * @return
     *     possible object is
     *     {@link Category }
     *
     */
    public Category getCategory() {
        return category;
    }

    /**
     * Sets the value of the category property.
     *
     * @param value
     *     allowed object is
     *     {@link Category }
     *
     */
    public void setCategory(final Category value) {
        this.category = value;
    }

    /**
     * Gets the value of the dataBlockDefinition property.
     *
     * @return
     *     possible object is
     *     {@link DataBlockDefinitionType }
     *
     */
    public DataBlockDefinitionType getDataBlockDefinition() {
        return dataBlockDefinition;
    }

    /**
     * Sets the value of the dataBlockDefinition property.
     *
     * @param value
     *     allowed object is
     *     {@link DataBlockDefinitionType }
     *
     */
    public void setDataBlockDefinition(final DataBlockDefinitionType value) {
        this.dataBlockDefinition = value;
    }

    /**
     * Gets the value of the dataStreamDefinition property.
     *
     * @return
     *     possible object is
     *     {@link DataStreamDefinitionType }
     *
     */
    public DataStreamDefinitionType getDataStreamDefinition() {
        return dataStreamDefinition;
    }

    /**
     * Sets the value of the dataStreamDefinition property.
     *
     * @param value
     *     allowed object is
     *     {@link DataStreamDefinitionType }
     *
     */
    public void setDataStreamDefinition(final DataStreamDefinitionType value) {
        this.dataStreamDefinition = value;
    }

    /**
     * Gets the value of the remoteSchema property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRemoteSchema() {
        return remoteSchema;
    }

    /**
     * Sets the value of the remoteSchema property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRemoteSchema(final String value) {
        this.remoteSchema = value;
    }

    /**
     * Gets the value of the type property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getType() {
        return type;
     }

    /**
     * Sets the value of the type property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setType(final String value) {
        this.type = value;
    }

    /**
     * Gets the value of the href property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getHref() {
        return href;
    }

    /**
     * Sets the value of the href property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setHref(final String value) {
        this.href = value;
    }

    /**
     * Gets the value of the role property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the value of the role property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRole(final String value) {
        this.role = value;
    }

    /**
     * Gets the value of the arcrole property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getArcrole() {
        return arcrole;
    }

    /**
     * Sets the value of the arcrole property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setArcrole(final String value) {
        this.arcrole = value;
    }

    /**
     * Gets the value of the title property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTitle(final String value) {
        this.title = value;
    }

    /**
     * Gets the value of the show property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getShow() {
        return show;
    }

    /**
     * Sets the value of the show property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setShow(final String value) {
        this.show = value;
    }

    /**
     * Gets the value of the actuate property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getActuate() {
        return actuate;
    }

    /**
     * Sets the value of the actuate property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setActuate(final String value) {
        this.actuate = value;
    }

}
