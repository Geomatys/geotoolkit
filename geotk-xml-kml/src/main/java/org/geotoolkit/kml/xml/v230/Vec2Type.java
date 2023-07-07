/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2023, Geomatys
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


package org.geotoolkit.kml.xml.v230;

import java.util.HashMap;
import java.util.Map;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyAttribute;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;


/**
 * <p>Classe Java pour vec2Type complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="vec2Type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="x" type="{http://www.w3.org/2001/XMLSchema}double" default="1.0" />
 *       &lt;attribute name="y" type="{http://www.w3.org/2001/XMLSchema}double" default="1.0" />
 *       &lt;attribute name="xunits" type="{http://www.opengis.net/kml/2.2}unitsEnumType" default="fraction" />
 *       &lt;attribute name="yunits" type="{http://www.opengis.net/kml/2.2}unitsEnumType" default="fraction" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "vec2Type", namespace = "http://www.opengis.net/kml/2.2")
public class Vec2Type {

    @XmlAttribute(name = "x")
    protected Double x;
    @XmlAttribute(name = "y")
    protected Double y;
    @XmlAttribute(name = "xunits")
    protected UnitsEnumType xunits;
    @XmlAttribute(name = "yunits")
    protected UnitsEnumType yunits;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Obtient la valeur de la propriété x.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public double getX() {
        if (x == null) {
            return  1.0D;
        } else {
            return x;
        }
    }

    /**
     * Définit la valeur de la propriété x.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setX(Double value) {
        this.x = value;
    }

    /**
     * Obtient la valeur de la propriété y.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public double getY() {
        if (y == null) {
            return  1.0D;
        } else {
            return y;
        }
    }

    /**
     * Définit la valeur de la propriété y.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setY(Double value) {
        this.y = value;
    }

    /**
     * Obtient la valeur de la propriété xunits.
     *
     * @return
     *     possible object is
     *     {@link UnitsEnumType }
     *
     */
    public UnitsEnumType getXunits() {
        if (xunits == null) {
            return UnitsEnumType.FRACTION;
        } else {
            return xunits;
        }
    }

    /**
     * Définit la valeur de la propriété xunits.
     *
     * @param value
     *     allowed object is
     *     {@link UnitsEnumType }
     *
     */
    public void setXunits(UnitsEnumType value) {
        this.xunits = value;
    }

    /**
     * Obtient la valeur de la propriété yunits.
     *
     * @return
     *     possible object is
     *     {@link UnitsEnumType }
     *
     */
    public UnitsEnumType getYunits() {
        if (yunits == null) {
            return UnitsEnumType.FRACTION;
        } else {
            return yunits;
        }
    }

    /**
     * Définit la valeur de la propriété yunits.
     *
     * @param value
     *     allowed object is
     *     {@link UnitsEnumType }
     *
     */
    public void setYunits(UnitsEnumType value) {
        this.yunits = value;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     *
     * <p>
     * the map is keyed by the name of the attribute and
     * the value is the string value of the attribute.
     *
     * the map returned by this method is live, and you can add new attribute
     * by updating the map directly. Because of this design, there's no setter.
     *
     *
     * @return
     *     always non-null
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

}
