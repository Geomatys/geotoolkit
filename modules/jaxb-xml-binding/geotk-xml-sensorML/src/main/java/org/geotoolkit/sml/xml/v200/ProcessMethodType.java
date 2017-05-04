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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.swe.xml.v200.AbstractSWEIdentifiableType;


/**
 * <p>Java class for ProcessMethodType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ProcessMethodType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swe/2.0}AbstractSWEIdentifiableType">
 *       &lt;sequence>
 *         &lt;element name="algorithm" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.opengis.net/sensorml/2.0}AbstractAlgorithm"/>
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
@XmlType(name = "ProcessMethodType", propOrder = {
    "algorithm"
})
public class ProcessMethodType
    extends AbstractSWEIdentifiableType
{

    protected List<ProcessMethodType.Algorithm> algorithm;

    /**
     * Gets the value of the algorithm property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the algorithm property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAlgorithm().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProcessMethodType.Algorithm }
     *
     *
     */
    public List<ProcessMethodType.Algorithm> getAlgorithm() {
        if (algorithm == null) {
            algorithm = new ArrayList<ProcessMethodType.Algorithm>();
        }
        return this.algorithm;
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
     *         &lt;element ref="{http://www.opengis.net/sensorml/2.0}AbstractAlgorithm"/>
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
        "abstractAlgorithm"
    })
    public static class Algorithm {

        @XmlElement(name = "AbstractAlgorithm", required = true)
        protected AbstractAlgorithmType abstractAlgorithm;

        /**
         * Gets the value of the abstractAlgorithm property.
         *
         * @return
         *     possible object is
         *     {@link AbstractAlgorithmType }
         *
         */
        public AbstractAlgorithmType getAbstractAlgorithm() {
            return abstractAlgorithm;
        }

        /**
         * Sets the value of the abstractAlgorithm property.
         *
         * @param value
         *     allowed object is
         *     {@link AbstractAlgorithmType }
         *
         */
        public void setAbstractAlgorithm(AbstractAlgorithmType value) {
            this.abstractAlgorithm = value;
        }

    }

}
