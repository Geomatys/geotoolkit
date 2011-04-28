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

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TemporalCRSType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TemporalCRSType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml/3.2}AbstractCRSType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element ref="{http://www.opengis.net/gml/3.2}timeCSProperty"/>
 *           &lt;element ref="{http://www.opengis.net/gml/3.2}usesTemporalCS"/>
 *         &lt;/choice>
 *         &lt;element ref="{http://www.opengis.net/gml/3.2}temporalDatumProperty"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TemporalCRSType", propOrder = {
    "timeCSProperty",
    "usesTemporalCS",
    "temporalDatumProperty"
})
public class TemporalCRSType
    extends AbstractCRSType
{

    @XmlElementRef(name = "timeCSProperty", namespace = "http://www.opengis.net/gml/3.2", type = JAXBElement.class)
    private JAXBElement<TimeCSPropertyType> timeCSProperty;
    private TemporalCSPropertyType usesTemporalCS;
    @XmlElementRef(name = "temporalDatumProperty", namespace = "http://www.opengis.net/gml/3.2", type = JAXBElement.class)
    private JAXBElement<TemporalDatumPropertyType> temporalDatumProperty;

    /**
     * Gets the value of the timeCSProperty property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link TimeCSPropertyType }{@code >}
     *     {@link JAXBElement }{@code <}{@link TimeCSPropertyType }{@code >}
     *     
     */
    public JAXBElement<TimeCSPropertyType> getTimeCSProperty() {
        return timeCSProperty;
    }

    /**
     * Sets the value of the timeCSProperty property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link TimeCSPropertyType }{@code >}
     *     {@link JAXBElement }{@code <}{@link TimeCSPropertyType }{@code >}
     *     
     */
    public void setTimeCSProperty(JAXBElement<TimeCSPropertyType> value) {
        this.timeCSProperty = ((JAXBElement<TimeCSPropertyType> ) value);
    }

    /**
     * Gets the value of the usesTemporalCS property.
     * 
     * @return
     *     possible object is
     *     {@link TemporalCSPropertyType }
     *     
     */
    public TemporalCSPropertyType getUsesTemporalCS() {
        return usesTemporalCS;
    }

    /**
     * Sets the value of the usesTemporalCS property.
     * 
     * @param value
     *     allowed object is
     *     {@link TemporalCSPropertyType }
     *     
     */
    public void setUsesTemporalCS(TemporalCSPropertyType value) {
        this.usesTemporalCS = value;
    }

    /**
     * Gets the value of the temporalDatumProperty property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link TemporalDatumPropertyType }{@code >}
     *     {@link JAXBElement }{@code <}{@link TemporalDatumPropertyType }{@code >}
     *     
     */
    public JAXBElement<TemporalDatumPropertyType> getTemporalDatumProperty() {
        return temporalDatumProperty;
    }

    /**
     * Sets the value of the temporalDatumProperty property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link TemporalDatumPropertyType }{@code >}
     *     {@link JAXBElement }{@code <}{@link TemporalDatumPropertyType }{@code >}
     *     
     */
    public void setTemporalDatumProperty(JAXBElement<TemporalDatumPropertyType> value) {
        this.temporalDatumProperty = ((JAXBElement<TemporalDatumPropertyType> ) value);
    }

}
