/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.constellation.sml.v101;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.constellation.swe.v101.AbstractDataRecordEntry;
import org.constellation.swe.v101.Category;
import org.constellation.swe.v101.DataBlockDefinitionEntry;
import org.constellation.swe.v101.DataStreamDefinitionType;



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
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PresentationLayerPropertyType", propOrder = {
    "abstractDataRecord",
    "category",
    "dataBlockDefinition",
    "dataStreamDefinition"
})
public class PresentationLayerPropertyType {

    @XmlElementRef(name = "AbstractDataRecord", namespace = "http://www.opengis.net/swe/1.0.1", type = JAXBElement.class)
    private JAXBElement<? extends AbstractDataRecordEntry> abstractDataRecord;
    @XmlElement(name = "Category", namespace = "http://www.opengis.net/swe/1.0.1")
    private Category category;
    @XmlElement(name = "DataBlockDefinition", namespace = "http://www.opengis.net/swe/1.0.1")
    private DataBlockDefinitionEntry dataBlockDefinition;
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
    public JAXBElement<? extends AbstractDataRecordEntry> getAbstractDataRecord() {
        return abstractDataRecord;
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
    public void setAbstractDataRecord(JAXBElement<? extends AbstractDataRecordEntry> value) {
        this.abstractDataRecord = ((JAXBElement<? extends AbstractDataRecordEntry> ) value);
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
    public void setCategory(Category value) {
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
    public DataBlockDefinitionEntry getDataBlockDefinition() {
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
    public void setDataBlockDefinition(DataBlockDefinitionEntry value) {
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
    public void setDataStreamDefinition(DataStreamDefinitionType value) {
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
    public void setRemoteSchema(String value) {
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
        if (type == null) {
            return "simple";
        } else {
            return type;
        }
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
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
    public void setHref(String value) {
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
    public void setRole(String value) {
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
    public void setArcrole(String value) {
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
    public void setTitle(String value) {
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
    public void setShow(String value) {
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
    public void setActuate(String value) {
        this.actuate = value;
    }

}
