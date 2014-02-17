/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.sos.xml.v200;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.swe.xml.v200.AbstractEncodingType;
import org.geotoolkit.swe.xml.v200.BinaryEncodingType;
import org.geotoolkit.swe.xml.v200.TextEncodingType;
import org.geotoolkit.swe.xml.v200.XMLEncodingType;

/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained
 * within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/swe/2.0}AbstractEncoding"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "abstractEncoding"
})
public class ResultEncoding {

    @XmlElementRef(name = "AbstractEncoding", namespace = "http://www.opengis.net/swe/2.0", type = JAXBElement.class)
    private JAXBElement<? extends AbstractEncodingType> abstractEncoding;

    public ResultEncoding() {
    }

    public ResultEncoding(final AbstractEncodingType encoding) {
        final org.geotoolkit.swe.xml.v200.ObjectFactory factory = new org.geotoolkit.swe.xml.v200.ObjectFactory();
        if (encoding instanceof TextEncodingType) {
            this.abstractEncoding = factory.createTextEncoding((TextEncodingType) encoding);
        } else if (encoding instanceof XMLEncodingType) {
            this.abstractEncoding = factory.createXMLEncoding((XMLEncodingType) encoding);
        } else if (encoding instanceof BinaryEncodingType) {
            this.abstractEncoding = factory.createBinaryEncoding((BinaryEncodingType) encoding);
        } else {
            this.abstractEncoding = factory.createAbstractEncoding(encoding);
        }
    }

    /**
     * Gets the value of the abstractEncoding property.
     *
     * @return possible object is      {@code <}{@link TextEncodingType }{@code >}
         *     {@code <}{@link XMLEncodingType }{@code >}
     *     {@code <}{@link BinaryEncodingType }{@code >}
     *     {@code <}{@link AbstractEncodingType }{@code >}
     *
     */
    public AbstractEncodingType getAbstractEncoding() {
        if (abstractEncoding != null) {
            return abstractEncoding.getValue();
        }
        return null;
    }

    /**
     * Sets the value of the abstractEncoding property.
     *
     * @param value allowed object is      {@link JAXBElement }{@code <}{@link TextEncodingType }{@code >}
         *     {@link JAXBElement }{@code <}{@link XMLEncodingType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryEncodingType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractEncodingType }{@code >}
     *
     */
    public void setAbstractEncoding(JAXBElement<? extends AbstractEncodingType> value) {
        this.abstractEncoding = ((JAXBElement<? extends AbstractEncodingType>) value);
    }
}
