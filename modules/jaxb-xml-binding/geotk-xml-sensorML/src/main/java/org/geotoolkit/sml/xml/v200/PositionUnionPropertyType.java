/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

package org.geotoolkit.sml.xml.v200;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v321.PointType;
import org.geotoolkit.swe.xml.v200.DataArrayType;
import org.geotoolkit.swe.xml.v200.DataRecordType;
import org.geotoolkit.swe.xml.v200.MatrixType;
import org.geotoolkit.swe.xml.v200.TextType;
import org.geotoolkit.swe.xml.v200.VectorType;


/**
 * <p>Java class for PositionUnionPropertyType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PositionUnionPropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence minOccurs="0">
 *         &lt;group ref="{http://www.opengis.net/sensorml/2.0}PositionUnion"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.opengis.net/swe/2.0}AssociationAttributeGroup"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PositionUnionPropertyType", propOrder = {
    "text",
    "point",
    "vector",
    "dataRecord",
    "dataArray",
    "abstractProcess"
})
public class PositionUnionPropertyType {

    @XmlElement(name = "Text", namespace = "http://www.opengis.net/swe/2.0")
    protected TextType text;
    @XmlElement(name = "Point", namespace = "http://www.opengis.net/gml/3.2")
    protected PointType point;
    @XmlElement(name = "Vector", namespace = "http://www.opengis.net/swe/2.0")
    protected VectorType vector;
    @XmlElement(name = "DataRecord", namespace = "http://www.opengis.net/swe/2.0")
    protected DataRecordType dataRecord;
    @XmlElementRef(name = "DataArray", namespace = "http://www.opengis.net/swe/2.0", type = JAXBElement.class, required = false)
    protected JAXBElement<? extends DataArrayType> dataArray;
    @XmlElementRef(name = "AbstractProcess", namespace = "http://www.opengis.net/sensorml/2.0", type = JAXBElement.class, required = false)
    protected JAXBElement<? extends AbstractProcessType> abstractProcess;
    @XmlAttribute(name = "type", namespace = "http://www.w3.org/1999/xlink")
    protected String type;
    @XmlAttribute(name = "href", namespace = "http://www.w3.org/1999/xlink")
    @XmlSchemaType(name = "anyURI")
    protected String href;
    @XmlAttribute(name = "role", namespace = "http://www.w3.org/1999/xlink")
    @XmlSchemaType(name = "anyURI")
    protected String role;
    @XmlAttribute(name = "arcrole", namespace = "http://www.w3.org/1999/xlink")
    @XmlSchemaType(name = "anyURI")
    protected String arcrole;
    @XmlAttribute(name = "title", namespace = "http://www.w3.org/1999/xlink")
    protected String title;
    @XmlAttribute(name = "show", namespace = "http://www.w3.org/1999/xlink")
    protected String show;
    @XmlAttribute(name = "actuate", namespace = "http://www.w3.org/1999/xlink")
    protected String actuate;

    /**
     * Provides positional information in textual form (e.g. "located on the intake line before the catalytic converter"); shall only be used when a more precise location is unknown or irrelevant.
     * 
     * @return
     *     possible object is
     *     {@link TextType }
     *     
     */
    public TextType getText() {
        return text;
    }

    /**
     * Sets the value of the text property.
     * 
     * @param value
     *     allowed object is
     *     {@link TextType }
     *     
     */
    public void setText(TextType value) {
        this.text = value;
    }

    /**
     * Provides static location only using a gml:Point element.
     * 
     * @return
     *     possible object is
     *     {@link PointType }
     *     
     */
    public PointType getPoint() {
        return point;
    }

    /**
     * Sets the value of the point property.
     * 
     * @param value
     *     allowed object is
     *     {@link PointType }
     *     
     */
    public void setPoint(PointType value) {
        this.point = value;
    }

    /**
     * Provides a static location using a swe:Vector.
     * 
     * @return
     *     possible object is
     *     {@link VectorType }
     *     
     */
    public VectorType getVector() {
        return vector;
    }

    /**
     * Sets the value of the vector property.
     * 
     * @param value
     *     allowed object is
     *     {@link VectorType }
     *     
     */
    public void setVector(VectorType value) {
        this.vector = value;
    }

    /**
     * Provides location and orientation as a DataRecord consisting of one or two Vector elements.
     * 
     * @return
     *     possible object is
     *     {@link DataRecordType }
     *     
     */
    public DataRecordType getDataRecord() {
        return dataRecord;
    }

    /**
     * Sets the value of the dataRecord property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataRecordType }
     *     
     */
    public void setDataRecord(DataRecordType value) {
        this.dataRecord = value;
    }

    /**
     * Provides time-tagged dynamic state information that can include, for instance, location, orientation, velocity, acceleration, angular velocity, angular acceleration; shall be a DataArray consisting of a DataRecord element of multiple Vector fields.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link DataArrayType }{@code >}
     *     {@link JAXBElement }{@code <}{@link MatrixType }{@code >}
     *     
     */
    public JAXBElement<? extends DataArrayType> getDataArray() {
        return dataArray;
    }

    /**
     * Sets the value of the dataArray property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link DataArrayType }{@code >}
     *     {@link JAXBElement }{@code <}{@link MatrixType }{@code >}
     *     
     */
    public void setDataArray(JAXBElement<? extends DataArrayType> value) {
        this.dataArray = value;
    }

    /**
     * Provides for positional information to be provided by a process; example processes could include a physical sensor such as a GPS, a computational process such as an orbital propagation model, a specific web service such as a SOS, or any process who's output provides positional information.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link AbstractPhysicalProcessType }{@code >}
     *     {@link JAXBElement }{@code <}{@link SimpleProcessType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AggregateProcessType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PhysicalComponentType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PhysicalSystemType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractProcessType }{@code >}
     *     
     */
    public JAXBElement<? extends AbstractProcessType> getAbstractProcess() {
        return abstractProcess;
    }

    /**
     * Sets the value of the abstractProcess property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link AbstractPhysicalProcessType }{@code >}
     *     {@link JAXBElement }{@code <}{@link SimpleProcessType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AggregateProcessType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PhysicalComponentType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PhysicalSystemType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractProcessType }{@code >}
     *     
     */
    public void setAbstractProcess(JAXBElement<? extends AbstractProcessType> value) {
        this.abstractProcess = value;
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
