/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019
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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
//import org.geotoolkit.eop.xml.v100.EarthObservationType;


/**
 * <p>Classe Java pour ObservationType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="ObservationType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractFeatureType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml}validTime"/>
 *         &lt;element ref="{http://www.opengis.net/gml}using" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/gml}target" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/gml}resultOf"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ObservationType", propOrder = {
    "validTime",
    "using",
    "target",
    "resultOf"
})
@XmlSeeAlso({
    //EarthObservationType.class,
    DirectedObservationType.class
})
public class ObservationType extends AbstractFeatureType {

    @XmlElement(required = true)
    protected TimePrimitivePropertyType validTime;
    protected FeaturePropertyType using;
    @XmlElementRef(name = "target", namespace = "http://www.opengis.net/gml", type = JAXBElement.class, required = false)
    protected JAXBElement<TargetPropertyType> target;
    @XmlElement(required = true)
    protected AssociationType resultOf;

    /**
     * Obtient la valeur de la propriété validTime.
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
     * Définit la valeur de la propriété validTime.
     *
     * @param value
     *     allowed object is
     *     {@link TimePrimitivePropertyType }
     *
     */
    public void setValidTime(TimePrimitivePropertyType value) {
        this.validTime = value;
    }

    /**
     * Obtient la valeur de la propriété using.
     *
     * @return
     *     possible object is
     *     {@link FeaturePropertyType }
     *
     */
    public FeaturePropertyType getUsing() {
        return using;
    }

    /**
     * Définit la valeur de la propriété using.
     *
     * @param value
     *     allowed object is
     *     {@link FeaturePropertyType }
     *
     */
    public void setUsing(FeaturePropertyType value) {
        this.using = value;
    }

    /**
     * Obtient la valeur de la propriété target.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link TargetPropertyType }{@code >}
     *     {@link JAXBElement }{@code <}{@link TargetPropertyType }{@code >}
     *
     */
    public JAXBElement<TargetPropertyType> getTarget() {
        return target;
    }

    /**
     * Définit la valeur de la propriété target.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link TargetPropertyType }{@code >}
     *     {@link JAXBElement }{@code <}{@link TargetPropertyType }{@code >}
     *
     */
    public void setTarget(JAXBElement<TargetPropertyType> value) {
        this.target = value;
    }

    /**
     * Obtient la valeur de la propriété resultOf.
     *
     * @return
     *     possible object is
     *     {@link AssociationType }
     *
     */
    public AssociationType getResultOf() {
        return resultOf;
    }

    /**
     * Définit la valeur de la propriété resultOf.
     *
     * @param value
     *     allowed object is
     *     {@link AssociationType }
     *
     */
    public void setResultOf(AssociationType value) {
        this.resultOf = value;
    }

}
