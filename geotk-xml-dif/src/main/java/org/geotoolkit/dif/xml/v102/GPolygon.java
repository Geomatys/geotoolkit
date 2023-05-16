/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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
package org.geotoolkit.dif.xml.v102;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 *
 *
 *
 * <p>Classe Java pour GPolygon complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="GPolygon">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Boundary" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}Boundary"/>
 *         &lt;element name="Exclusive_Zone" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}ExclusiveZone" minOccurs="0"/>
 *         &lt;element name="Center_Point" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}Point" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GPolygon", propOrder = {
    "boundary",
    "exclusiveZone",
    "centerPoint"
})
public class GPolygon {

    @XmlElement(name = "Boundary", required = true)
    protected Boundary boundary;
    @XmlElement(name = "Exclusive_Zone")
    protected ExclusiveZone exclusiveZone;
    @XmlElement(name = "Center_Point")
    protected Point centerPoint;

    /**
     * Obtient la valeur de la propriété boundary.
     *
     * @return
     *     possible object is
     *     {@link Boundary }
     *
     */
    public Boundary getBoundary() {
        return boundary;
    }

    /**
     * Définit la valeur de la propriété boundary.
     *
     * @param value
     *     allowed object is
     *     {@link Boundary }
     *
     */
    public void setBoundary(Boundary value) {
        this.boundary = value;
    }

    /**
     * Obtient la valeur de la propriété exclusiveZone.
     *
     * @return
     *     possible object is
     *     {@link ExclusiveZone }
     *
     */
    public ExclusiveZone getExclusiveZone() {
        return exclusiveZone;
    }

    /**
     * Définit la valeur de la propriété exclusiveZone.
     *
     * @param value
     *     allowed object is
     *     {@link ExclusiveZone }
     *
     */
    public void setExclusiveZone(ExclusiveZone value) {
        this.exclusiveZone = value;
    }

    /**
     * Obtient la valeur de la propriété centerPoint.
     *
     * @return
     *     possible object is
     *     {@link Point }
     *
     */
    public Point getCenterPoint() {
        return centerPoint;
    }

    /**
     * Définit la valeur de la propriété centerPoint.
     *
     * @param value
     *     allowed object is
     *     {@link Point }
     *
     */
    public void setCenterPoint(Point value) {
        this.centerPoint = value;
    }

}
