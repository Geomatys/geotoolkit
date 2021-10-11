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
package org.geotoolkit.gml.xml.v311;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * A dynamic feature collection may possess a history and/or a timestamp.
 *
 * <p>Java class for DynamicFeatureCollectionType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="DynamicFeatureCollectionType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}FeatureCollectionType">
 *       &lt;group ref="{http://www.opengis.net/gml}dynamicProperties"/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DynamicFeatureCollectionType", propOrder = {
    "validTime",
    "history",
    "dataSource"
})
public class DynamicFeatureCollectionType extends FeatureCollectionType {

    private TimePrimitivePropertyType validTime;
    @XmlElementRef(name = "history", namespace = "http://www.opengis.net/gml", type = JAXBElement.class)
    private JAXBElement<? extends HistoryPropertyType> history;
    private StringOrRefType dataSource;

    /**
     * Gets the value of the validTime property.
     *
     * @return
     *     possible object is
     *     {@link TimePrimitivePropertyType }
     *
     */
    public TimePrimitivePropertyType getValidTime() {
        return validTime;
    }

    /**
     * Sets the value of the validTime property.
     *
     * @param value
     *     allowed object is
     *     {@link TimePrimitivePropertyType }
     *
     */
    public void setValidTime(final TimePrimitivePropertyType value) {
        this.validTime = value;
    }

    /**
     * Gets the value of the history property.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link TrackType }{@code >}
     *     {@link JAXBElement }{@code <}{@link HistoryPropertyType }{@code >}
     *
     */
    public JAXBElement<? extends HistoryPropertyType> getHistory() {
        return history;
    }

    /**
     * Sets the value of the history property.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link TrackType }{@code >}
     *     {@link JAXBElement }{@code <}{@link HistoryPropertyType }{@code >}
     *
     */
    public void setHistory(final JAXBElement<? extends HistoryPropertyType> value) {
        this.history = ((JAXBElement<? extends HistoryPropertyType> ) value);
    }

    /**
     * Gets the value of the dataSource property.
     *
     * @return
     *     possible object is
     *     {@link StringOrRefType }
     *
     */
    public StringOrRefType getDataSource() {
        return dataSource;
    }

    /**
     * Sets the value of the dataSource property.
     *
     * @param value
     *     allowed object is
     *     {@link StringOrRefType }
     *
     */
    public void setDataSource(final StringOrRefType value) {
        this.dataSource = value;
    }

}
