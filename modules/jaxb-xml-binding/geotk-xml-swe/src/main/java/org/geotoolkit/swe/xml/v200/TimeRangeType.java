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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import org.geotoolkit.swe.xml.AbstractTimeRange;


/**
 * <p>Java class for TimeRangeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TimeRangeType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swe/2.0}AbstractSimpleComponentType">
 *       &lt;sequence>
 *         &lt;element name="uom" type="{http://www.opengis.net/swe/2.0}UnitReference"/>
 *         &lt;element name="constraint" type="{http://www.opengis.net/swe/2.0}AllowedTimesPropertyType" minOccurs="0"/>
 *         &lt;element name="value" type="{http://www.opengis.net/swe/2.0}TimePair" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="referenceTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="localFrame" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TimeRangeType", propOrder = {
    "uom",
    "constraint",
    "value"
})
public class TimeRangeType extends AbstractSimpleComponentType implements AbstractTimeRange {

    @XmlElement(required = true)
    private UnitReference uom;
    private AllowedTimesPropertyType constraint;
    @XmlList
    private List<String> value;
    @XmlAttribute
    @XmlSchemaType(name = "dateTime")
    private XMLGregorianCalendar referenceTime;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String localFrame;

    public TimeRangeType() {
        
    }
    
    public TimeRangeType(final String definition, final List<String> value) {
        super(null, definition);
        this.value = value;
    }
    
    /**
     * Gets the value of the uom property.
     * 
     * @return
     *     possible object is
     *     {@link UnitReference }
     *     
     */
    @Override
    public UnitReference getUom() {
        return uom;
    }

    /**
     * Sets the value of the uom property.
     * 
     * @param value
     *     allowed object is
     *     {@link UnitReference }
     *     
     */
    public void setUom(UnitReference value) {
        this.uom = value;
    }

    /**
     * Gets the value of the constraint property.
     * 
     * @return
     *     possible object is
     *     {@link AllowedTimesPropertyType }
     *     
     */
    public AllowedTimesPropertyType getConstraint() {
        return constraint;
    }

    /**
     * Sets the value of the constraint property.
     * 
     * @param value
     *     allowed object is
     *     {@link AllowedTimesPropertyType }
     *     
     */
    public void setConstraint(AllowedTimesPropertyType value) {
        this.constraint = value;
    }

    /**
     * Gets the value of the value property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    @Override
    public List<String> getValue() {
        if (value == null) {
            value = new ArrayList<String>();
        }
        return this.value;
    }

    /**
     * Gets the value of the referenceTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getReferenceTimeCalendar() {
        return referenceTime;
    }
    
    @Override
    public String getReferenceTime() {
        if (referenceTime != null) {
            return referenceTime.toXMLFormat();
        }
        return null;
    }

    /**
     * Sets the value of the referenceTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setReferenceTime(XMLGregorianCalendar value) {
        this.referenceTime = value;
    }

    /**
     * Gets the value of the localFrame property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Override
    public String getLocalFrame() {
        return localFrame;
    }

    /**
     * Sets the value of the localFrame property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocalFrame(String value) {
        this.localFrame = value;
    }

}
