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
import org.geotoolkit.swe.xml.v200.AbstractDataComponentPropertyType;
import org.geotoolkit.swe.xml.v200.AbstractDataComponentType;

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
 *         &lt;element ref="{http://www.opengis.net/swe/2.0}AbstractDataComponent"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
* @author Guilhem Legal (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "abstractDataComponent"
})
public class ResultStructure {

    @XmlElementRef(name = "AbstractDataComponent", namespace = "http://www.opengis.net/swe/2.0", type = JAXBElement.class)
    private JAXBElement<? extends AbstractDataComponentType> abstractDataComponent;

    public ResultStructure() {
    }

    public ResultStructure(final AbstractDataComponentType value) {
        this.abstractDataComponent = AbstractDataComponentPropertyType.getJAXBElement(value);
    }

    /**
     * Gets the value of the abstractDataComponent property.
     *
     * @return possible object is
     *     {@code <}{@link BooleanType }{@code >}
     *     {@code <}{@link VectorType }{@code >}
     *     {@code <}{@link TimeType }{@code >}
     *     {@code <}{@link CategoryRangeType }{@code >}
     *     {@code <}{@link DataChoiceType }{@code >}
     *     {@code <}{@link MatrixType }{@code >}
     *     {@code <}{@link AbstractSimpleComponentType }{@code >}
     *     {@code <}{@link TimeRangeType }{@code >}
     *     {@code <}{@link CategoryType }{@code >}
     *     {@code <}{@link DataRecordType }{@code >}
     *     {@code <}{@link DataArrayType }{@code >}
     *     {@code <}{@link QuantityRangeType }{@code >}
     *     {@code <}{@link CountRangeType }{@code >}
     *     {@code <}{@link QuantityType }{@code >}
     *     {@code <}{@link TextType }{@code >}
     *     {@code <}{@link AbstractDataComponentType }{@code >}
     *     {@code <}{@link CountType }{@code >}
     *
     */
    public AbstractDataComponentType getAbstractDataComponent() {
        if (abstractDataComponent != null) {
            return abstractDataComponent.getValue();
        }
        return null;
    }

    /**
     * Sets the value of the abstractDataComponent property.
     *
     * @param value allowed object is      {@link JAXBElement }{@code <}{@link BooleanType }{@code >}
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
        this.abstractDataComponent = ((JAXBElement<? extends AbstractDataComponentType>) value);
    }
}
