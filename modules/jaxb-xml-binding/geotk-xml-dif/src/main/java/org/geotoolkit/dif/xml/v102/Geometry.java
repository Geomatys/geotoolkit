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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 *
 *                | DIF 9        | ECHO 10                      | UMM                              | DIF 10                       | Notes                                   |
 *                | ------------ | ---------------------------- | -------------------------------- |------------------------------| --------------------------------------- |
 *                |      -       |               -              | HorizontalSpatialDomain/Geometry | Geometry                     | Added from ECHO HorizontalSpatialDomain |
 *                |      -       | Choice maxOccurs="unbounded" | Choice maxOccurs="unbounded"     | Choice maxOccurs="unbounded" | Changed in 10.2 from CMRQ-618           |
 *
 *
 *
 * <p>Classe Java pour Geometry complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="Geometry">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Coordinate_System" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}CoordinateSystemEnum"/>
 *         &lt;choice maxOccurs="unbounded">
 *           &lt;element name="Bounding_Rectangle" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}BoundingRectangleType"/>
 *           &lt;element name="Point" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}Point"/>
 *           &lt;element name="Line" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}Line"/>
 *           &lt;element name="Polygon" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}GPolygon"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Geometry", propOrder = {
    "coordinateSystem",
    "boundingRectangleOrPointOrLine"
})
public class Geometry {

    @XmlElement(name = "Coordinate_System", required = true)
    @XmlSchemaType(name = "string")
    protected CoordinateSystemEnum coordinateSystem;
    @XmlElements({
        @XmlElement(name = "Bounding_Rectangle", type = BoundingRectangleType.class),
        @XmlElement(name = "Point", type = Point.class),
        @XmlElement(name = "Line", type = Line.class),
        @XmlElement(name = "Polygon", type = GPolygon.class)
    })
    protected List<Object> boundingRectangleOrPointOrLine;

    public Geometry() {

    }

    public Geometry(BoundingRectangleType rec) {
        this.boundingRectangleOrPointOrLine = new ArrayList<>();
        this.boundingRectangleOrPointOrLine.add(rec);
    }

    /**
     * Obtient la valeur de la propriété coordinateSystem.
     *
     * @return
     *     possible object is
     *     {@link CoordinateSystemEnum }
     *
     */
    public CoordinateSystemEnum getCoordinateSystem() {
        return coordinateSystem;
    }

    /**
     * Définit la valeur de la propriété coordinateSystem.
     *
     * @param value
     *     allowed object is
     *     {@link CoordinateSystemEnum }
     *
     */
    public void setCoordinateSystem(CoordinateSystemEnum value) {
        this.coordinateSystem = value;
    }

    public BoundingRectangleType getBoundingRectangle() {
        if (boundingRectangleOrPointOrLine != null) {
            for (Object obj : boundingRectangleOrPointOrLine) {
                if (obj instanceof BoundingRectangleType) {
                    return (BoundingRectangleType)obj;
                }
            }
        }
        return null;
    }

    public void setBoundingRectangle(BoundingRectangleType rect) {
        boundingRectangleOrPointOrLine = new ArrayList<>();
        if (rect != null) {
            boundingRectangleOrPointOrLine.add(rect);
        }
    }

    /**
     * Gets the value of the boundingRectangleOrPointOrLine property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the boundingRectangleOrPointOrLine property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBoundingRectangleOrPointOrLine().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BoundingRectangleType }
     * {@link Point }
     * {@link Line }
     * {@link GPolygon }
     *
     *
     */
    public List<Object> getBoundingRectangleOrPointOrLine() {
        if (boundingRectangleOrPointOrLine == null) {
            boundingRectangleOrPointOrLine = new ArrayList<>();
        }
        return this.boundingRectangleOrPointOrLine;
    }

}
