/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2012, Geomatys
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


package org.geotoolkit.gml.xml.v321;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DirectionPropertyType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="DirectionPropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice minOccurs="0">
 *         &lt;element name="DirectionVector" type="{http://www.opengis.net/gml/3.2}DirectionVectorType"/>
 *         &lt;element name="DirectionDescription" type="{http://www.opengis.net/gml/3.2}DirectionDescriptionType"/>
 *         &lt;element name="CompassPoint" type="{http://www.opengis.net/gml/3.2}CompassPointEnumeration"/>
 *         &lt;element name="DirectionKeyword" type="{http://www.opengis.net/gml/3.2}CodeType"/>
 *         &lt;element name="DirectionString" type="{http://www.opengis.net/gml/3.2}StringOrRefType"/>
 *       &lt;/choice>
 *       &lt;attGroup ref="{http://www.opengis.net/gml/3.2}AssociationAttributeGroup"/>
 *       &lt;attGroup ref="{http://www.opengis.net/gml/3.2}OwnershipAttributeGroup"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DirectionPropertyType", propOrder = {
    "directionVector",
    "directionDescription",
    "compassPoint",
    "directionKeyword",
    "directionString"
})
public class DirectionPropertyType {

    @XmlElement(name = "DirectionVector")
    private DirectionVectorType directionVector;
    @XmlElement(name = "DirectionDescription")
    private DirectionDescriptionType directionDescription;
    @XmlElement(name = "CompassPoint")
    private CompassPointEnumeration compassPoint;
    @XmlElement(name = "DirectionKeyword")
    private CodeType directionKeyword;
    @XmlElement(name = "DirectionString")
    private StringOrRefType directionString;
    @XmlAttribute
    private List<String> nilReason;
    @XmlAttribute(namespace = "http://www.opengis.net/gml/3.2")
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
    @XmlAttribute
    private java.lang.Boolean owns;

    /**
     * Gets the value of the directionVector property.
     *
     * @return
     *     possible object is
     *     {@link DirectionVectorType }
     *
     */
    public DirectionVectorType getDirectionVector() {
        return directionVector;
    }

    /**
     * Sets the value of the directionVector property.
     *
     * @param value
     *     allowed object is
     *     {@link DirectionVectorType }
     *
     */
    public void setDirectionVector(DirectionVectorType value) {
        this.directionVector = value;
    }

    /**
     * Gets the value of the directionDescription property.
     *
     * @return
     *     possible object is
     *     {@link DirectionDescriptionType }
     *
     */
    public DirectionDescriptionType getDirectionDescription() {
        return directionDescription;
    }

    /**
     * Sets the value of the directionDescription property.
     *
     * @param value
     *     allowed object is
     *     {@link DirectionDescriptionType }
     *
     */
    public void setDirectionDescription(DirectionDescriptionType value) {
        this.directionDescription = value;
    }

    /**
     * Gets the value of the compassPoint property.
     *
     * @return
     *     possible object is
     *     {@link CompassPointEnumeration }
     *
     */
    public CompassPointEnumeration getCompassPoint() {
        return compassPoint;
    }

    /**
     * Sets the value of the compassPoint property.
     *
     * @param value
     *     allowed object is
     *     {@link CompassPointEnumeration }
     *
     */
    public void setCompassPoint(CompassPointEnumeration value) {
        this.compassPoint = value;
    }

    /**
     * Gets the value of the directionKeyword property.
     *
     * @return
     *     possible object is
     *     {@link CodeType }
     *
     */
    public CodeType getDirectionKeyword() {
        return directionKeyword;
    }

    /**
     * Sets the value of the directionKeyword property.
     *
     * @param value
     *     allowed object is
     *     {@link CodeType }
     *
     */
    public void setDirectionKeyword(CodeType value) {
        this.directionKeyword = value;
    }

    /**
     * Gets the value of the directionString property.
     *
     * @return
     *     possible object is
     *     {@link StringOrRefType }
     *
     */
    public StringOrRefType getDirectionString() {
        return directionString;
    }

    /**
     * Sets the value of the directionString property.
     *
     * @param value
     *     allowed object is
     *     {@link StringOrRefType }
     *
     */
    public void setDirectionString(StringOrRefType value) {
        this.directionString = value;
    }

    /**
     * Gets the value of the nilReason property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the nilReason property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNilReason().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getNilReason() {
        if (nilReason == null) {
            nilReason = new ArrayList<String>();
        }
        return this.nilReason;
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

    /**
     * Gets the value of the owns property.
     *
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *
     */
    public boolean isOwns() {
        if (owns == null) {
            return false;
        } else {
            return owns;
        }
    }

    /**
     * Sets the value of the owns property.
     *
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *
     */
    public void setOwns(java.lang.Boolean value) {
        this.owns = value;
    }

}
