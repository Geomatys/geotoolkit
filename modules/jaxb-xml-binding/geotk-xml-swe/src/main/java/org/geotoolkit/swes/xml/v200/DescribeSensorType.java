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

package org.geotoolkit.swes.xml.v200;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v321.AbstractTimeGeometricPrimitiveType;
import org.geotoolkit.gml.xml.v321.TimeInstantType;
import org.geotoolkit.gml.xml.v321.TimePeriodType;


/**
 * <p>Java class for DescribeSensorType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DescribeSensorType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swes/2.0}ExtensibleRequestType">
 *       &lt;sequence>
 *         &lt;element name="procedure" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element name="procedureDescriptionFormat" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element name="validTime" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.opengis.net/gml/3.2}AbstractTimeGeometricPrimitive"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DescribeSensorType", propOrder = {
    "procedure",
    "procedureDescriptionFormat",
    "validTime"
})
public class DescribeSensorType
    extends ExtensibleRequestType
{

    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    private String procedure;
    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    private String procedureDescriptionFormat;
    private DescribeSensorType.ValidTime validTime;

    /**
     * Gets the value of the procedure property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProcedure() {
        return procedure;
    }

    /**
     * Sets the value of the procedure property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProcedure(String value) {
        this.procedure = value;
    }

    /**
     * Gets the value of the procedureDescriptionFormat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProcedureDescriptionFormat() {
        return procedureDescriptionFormat;
    }

    /**
     * Sets the value of the procedureDescriptionFormat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProcedureDescriptionFormat(String value) {
        this.procedureDescriptionFormat = value;
    }

    /**
     * Gets the value of the validTime property.
     * 
     * @return
     *     possible object is
     *     {@link DescribeSensorType.ValidTime }
     *     
     */
    public DescribeSensorType.ValidTime getValidTime() {
        return validTime;
    }

    /**
     * Sets the value of the validTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link DescribeSensorType.ValidTime }
     *     
     */
    public void setValidTime(DescribeSensorType.ValidTime value) {
        this.validTime = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element ref="{http://www.opengis.net/gml/3.2}AbstractTimeGeometricPrimitive"/>
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
        "abstractTimeGeometricPrimitive"
    })
    public static class ValidTime {

        @XmlElementRef(name = "AbstractTimeGeometricPrimitive", namespace = "http://www.opengis.net/gml/3.2", type = JAXBElement.class)
        private JAXBElement<? extends AbstractTimeGeometricPrimitiveType> abstractTimeGeometricPrimitive;

        /**
         * Gets the value of the abstractTimeGeometricPrimitive property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link TimePeriodType }{@code >}
         *     {@link JAXBElement }{@code <}{@link TimeInstantType }{@code >}
         *     {@link JAXBElement }{@code <}{@link AbstractTimeGeometricPrimitiveType }{@code >}
         *     
         */
        public JAXBElement<? extends AbstractTimeGeometricPrimitiveType> getAbstractTimeGeometricPrimitive() {
            return abstractTimeGeometricPrimitive;
        }

        /**
         * Sets the value of the abstractTimeGeometricPrimitive property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link TimePeriodType }{@code >}
         *     {@link JAXBElement }{@code <}{@link TimeInstantType }{@code >}
         *     {@link JAXBElement }{@code <}{@link AbstractTimeGeometricPrimitiveType }{@code >}
         *     
         */
        public void setAbstractTimeGeometricPrimitive(JAXBElement<? extends AbstractTimeGeometricPrimitiveType> value) {
            this.abstractTimeGeometricPrimitive = ((JAXBElement<? extends AbstractTimeGeometricPrimitiveType> ) value);
        }

    }

}
