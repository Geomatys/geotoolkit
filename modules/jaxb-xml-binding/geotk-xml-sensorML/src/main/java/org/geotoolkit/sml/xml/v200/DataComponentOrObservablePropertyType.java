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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.swe.xml.v200.AbstractDataComponentType;
import org.geotoolkit.swe.xml.v200.AbstractSimpleComponentType;
import org.geotoolkit.swe.xml.v200.BooleanType;
import org.geotoolkit.swe.xml.v200.CategoryRangeType;
import org.geotoolkit.swe.xml.v200.CategoryType;
import org.geotoolkit.swe.xml.v200.CountRangeType;
import org.geotoolkit.swe.xml.v200.CountType;
import org.geotoolkit.swe.xml.v200.DataArrayType;
import org.geotoolkit.swe.xml.v200.DataChoiceType;
import org.geotoolkit.swe.xml.v200.DataRecordType;
import org.geotoolkit.swe.xml.v200.MatrixType;
import org.geotoolkit.swe.xml.v200.QuantityRangeType;
import org.geotoolkit.swe.xml.v200.QuantityType;
import org.geotoolkit.swe.xml.v200.TextType;
import org.geotoolkit.swe.xml.v200.TimeRangeType;
import org.geotoolkit.swe.xml.v200.TimeType;
import org.geotoolkit.swe.xml.v200.VectorType;


/**
 * <p>Java class for DataComponentOrObservablePropertyType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="DataComponentOrObservablePropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence minOccurs="0">
 *         &lt;group ref="{http://www.opengis.net/sensorml/2.0}DataComponentOrObservable"/>
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
@XmlType(name = "DataComponentOrObservablePropertyType", propOrder = {
    "abstractDataComponent",
    "observableProperty",
    "dataInterface"
})
@XmlSeeAlso({
    InputListType.Input.class,
    OutputListType.Output.class,
    ParameterListType.Parameter.class
})
public class DataComponentOrObservablePropertyType {

    @XmlElementRef(name = "AbstractDataComponent", namespace = "http://www.opengis.net/swe/2.0", type = JAXBElement.class, required = false)
    protected JAXBElement<? extends AbstractDataComponentType> abstractDataComponent;
    @XmlElement(name = "ObservableProperty")
    protected ObservablePropertyType observableProperty;
    @XmlElement(name = "DataInterface")
    protected DataInterfaceType dataInterface;
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
     * A single digital number (DN) or aggregate of DNs that represent the value of some property. Single data components can be of type Quantity, Count, Category, Boolean, Text, or Time; these can be aggregated in records, arrays, vector, and matrices.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link TimeType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DataArrayType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CategoryType }{@code >}
     *     {@link JAXBElement }{@code <}{@link VectorType }{@code >}
     *     {@link JAXBElement }{@code <}{@link QuantityType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractDataComponentType }{@code >}
     *     {@link JAXBElement }{@code <}{@link QuantityRangeType }{@code >}
     *     {@link JAXBElement }{@code <}{@link MatrixType }{@code >}
     *     {@link JAXBElement }{@code <}{@link TextType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DataChoiceType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractSimpleComponentType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CountRangeType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BooleanType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DataRecordType }{@code >}
     *     {@link JAXBElement }{@code <}{@link TimeRangeType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CategoryRangeType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CountType }{@code >}
     *
     */
    public JAXBElement<? extends AbstractDataComponentType> getAbstractDataComponent() {
        return abstractDataComponent;
    }

    /**
     * Sets the value of the abstractDataComponent property.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link TimeType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DataArrayType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CategoryType }{@code >}
     *     {@link JAXBElement }{@code <}{@link VectorType }{@code >}
     *     {@link JAXBElement }{@code <}{@link QuantityType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractDataComponentType }{@code >}
     *     {@link JAXBElement }{@code <}{@link QuantityRangeType }{@code >}
     *     {@link JAXBElement }{@code <}{@link MatrixType }{@code >}
     *     {@link JAXBElement }{@code <}{@link TextType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DataChoiceType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractSimpleComponentType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CountRangeType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BooleanType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DataRecordType }{@code >}
     *     {@link JAXBElement }{@code <}{@link TimeRangeType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CategoryRangeType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CountType }{@code >}
     *
     */
    public void setAbstractDataComponent(JAXBElement<? extends AbstractDataComponentType> value) {
        this.abstractDataComponent = value;
    }

    /**
     * A physical property of the environment that can be observed by an appropriate detector (e.g. temperature, pressure, etc.); Typically,an ObservableProperty serves as the input of a detector and the output of an actuator.
     *
     * @return
     *     possible object is
     *     {@link ObservablePropertyType }
     *
     */
    public ObservablePropertyType getObservableProperty() {
        return observableProperty;
    }

    /**
     * Sets the value of the observableProperty property.
     *
     * @param value
     *     allowed object is
     *     {@link ObservablePropertyType }
     *
     */
    public void setObservableProperty(ObservablePropertyType value) {
        this.observableProperty = value;
    }

    /**
     * A data interface serves as an intermediary between the pure digital domain and the physical domain where DN are encoded into a format and perhaps transmitted through physical connections using some well-defined protocol. The DataInterface element allows one to define the components, semantics, encoding, connections, and protocol at an input, output, or parameter port.
     *
     * @return
     *     possible object is
     *     {@link DataInterfaceType }
     *
     */
    public DataInterfaceType getDataInterface() {
        return dataInterface;
    }

    /**
     * Sets the value of the dataInterface property.
     *
     * @param value
     *     allowed object is
     *     {@link DataInterfaceType }
     *
     */
    public void setDataInterface(DataInterfaceType value) {
        this.dataInterface = value;
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
