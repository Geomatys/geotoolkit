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

package org.geotoolkit.swe.xml.v200;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.xlink.xml.v100.ActuateType;
import org.geotoolkit.xlink.xml.v100.ShowType;
import org.geotoolkit.xlink.xml.v100.TypeType;


/**
 * <p>Java class for AbstractDataComponentPropertyType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractDataComponentPropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence minOccurs="0">
 *         &lt;element ref="{http://www.opengis.net/swe/2.0}AbstractDataComponent"/>
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
@XmlType(name = "AbstractDataComponentPropertyType", propOrder = {
    "abstractDataComponent"
})
@XmlSeeAlso({
    DataArrayType.ElementType.class,
    org.geotoolkit.swe.xml.v200.DataStreamType.ElementType.class,
    DataChoiceType.Item.class,
    DataRecordType.Field.class
})
public class AbstractDataComponentPropertyType {

    @XmlElementRef(name = "AbstractDataComponent", namespace = "http://www.opengis.net/swe/2.0", type = JAXBElement.class)
    private JAXBElement<? extends AbstractDataComponentType> abstractDataComponent;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private TypeType type;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String href;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String role;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String arcrole;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String titleTemp;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private ShowType show;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private ActuateType actuate;

    /**
     * Gets the value of the abstractDataComponent property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link BooleanType }{@code >}
     *     {@link JAXBElement }{@code <}{@link VectorType }{@code >}
     *     {@link JAXBElement }{@code <}{@link TimeType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CategoryRangeType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DataChoiceType }{@code >}
     *     {@link JAXBElement }{@code <}{@link MatrixType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractSimpleComponentType }{@code >}
     *     {@link JAXBElement }{@code <}{@link TimeRangeType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CategoryType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DataRecordType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DataArrayType }{@code >}
     *     {@link JAXBElement }{@code <}{@link QuantityRangeType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CountRangeType }{@code >}
     *     {@link JAXBElement }{@code <}{@link QuantityType }{@code >}
     *     {@link JAXBElement }{@code <}{@link TextType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractDataComponentType }{@code >}
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
     *     {@link JAXBElement }{@code <}{@link BooleanType }{@code >}
     *     {@link JAXBElement }{@code <}{@link VectorType }{@code >}
     *     {@link JAXBElement }{@code <}{@link TimeType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CategoryRangeType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DataChoiceType }{@code >}
     *     {@link JAXBElement }{@code <}{@link MatrixType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractSimpleComponentType }{@code >}
     *     {@link JAXBElement }{@code <}{@link TimeRangeType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CategoryType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DataRecordType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DataArrayType }{@code >}
     *     {@link JAXBElement }{@code <}{@link QuantityRangeType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CountRangeType }{@code >}
     *     {@link JAXBElement }{@code <}{@link QuantityType }{@code >}
     *     {@link JAXBElement }{@code <}{@link TextType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractDataComponentType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CountType }{@code >}
     *     
     */
    public void setAbstractDataComponent(JAXBElement<? extends AbstractDataComponentType> value) {
        this.abstractDataComponent = ((JAXBElement<? extends AbstractDataComponentType> ) value);
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link TypeType }
     *     
     */
    public TypeType getType() {
        if (type == null) {
            return TypeType.SIMPLE;
        } else {
            return type;
        }
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link TypeType }
     *     
     */
    public void setType(TypeType value) {
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
     * Gets the value of the titleTemp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitleTemp() {
        return titleTemp;
    }

    /**
     * Sets the value of the titleTemp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitleTemp(String value) {
        this.titleTemp = value;
    }

    /**
     * Gets the value of the show property.
     * 
     * @return
     *     possible object is
     *     {@link ShowType }
     *     
     */
    public ShowType getShow() {
        return show;
    }

    /**
     * Sets the value of the show property.
     * 
     * @param value
     *     allowed object is
     *     {@link ShowType }
     *     
     */
    public void setShow(ShowType value) {
        this.show = value;
    }

    /**
     * Gets the value of the actuate property.
     * 
     * @return
     *     possible object is
     *     {@link ActuateType }
     *     
     */
    public ActuateType getActuate() {
        return actuate;
    }

    /**
     * Sets the value of the actuate property.
     * 
     * @param value
     *     allowed object is
     *     {@link ActuateType }
     *     
     */
    public void setActuate(ActuateType value) {
        this.actuate = value;
    }

}
