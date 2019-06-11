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
package org.geotoolkit.eop.xml.v201;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v321.AbstractFeatureType;
import org.geotoolkit.gml.xml.v321.MultiSurfacePropertyType;
import org.geotoolkit.gml.xml.v321.PointPropertyType;


/**
 * <p>Classe Java pour FootprintType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="FootprintType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml/3.2}AbstractFeatureType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/eop/2.1}multiExtentOf"/>
 *         &lt;element name="centerOf" type="{http://www.opengis.net/gml/3.2}PointPropertyType" minOccurs="0"/>
 *         &lt;element name="orientation" type="{http://www.opengis.net/eop/2.1}PolygonOrientationValueType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FootprintType", propOrder = {
    "multiExtentOf",
    "centerOf",
    "orientation"
})
public class FootprintType
    extends AbstractFeatureType
{

    @XmlElement(required = true)
    protected MultiSurfacePropertyType multiExtentOf;
    protected PointPropertyType centerOf;
    @XmlSchemaType(name = "string")
    protected PolygonOrientationValueType orientation;

    /**
     * Acquisition footprint coordinates, described by a closed polygon (last point=first point), using CRS:WGS84, Latitude,Longitude pairs (per-WGS84 definition of point ordering, not necessarily per all WFS implementations). Expected structure is gml:Polygon/gml:exterior/gml:LinearRing/gml:posList.
     *
     * @return
     *     possible object is
     *     {@link MultiSurfacePropertyType }
     *
     */
    public MultiSurfacePropertyType getMultiExtentOf() {
        return multiExtentOf;
    }

    /**
     * Définit la valeur de la propriété multiExtentOf.
     *
     * @param value
     *     allowed object is
     *     {@link MultiSurfacePropertyType }
     *
     */
    public void setMultiExtentOf(MultiSurfacePropertyType value) {
        this.multiExtentOf = value;
    }

    /**
     * Obtient la valeur de la propriété centerOf.
     *
     * @return
     *     possible object is
     *     {@link PointPropertyType }
     *
     */
    public PointPropertyType getCenterOf() {
        return centerOf;
    }

    /**
     * Définit la valeur de la propriété centerOf.
     *
     * @param value
     *     allowed object is
     *     {@link PointPropertyType }
     *
     */
    public void setCenterOf(PointPropertyType value) {
        this.centerOf = value;
    }

    /**
     * Obtient la valeur de la propriété orientation.
     *
     * @return
     *     possible object is
     *     {@link PolygonOrientationValueType }
     *
     */
    public PolygonOrientationValueType getOrientation() {
        return orientation;
    }

    /**
     * Définit la valeur de la propriété orientation.
     *
     * @param value
     *     allowed object is
     *     {@link PolygonOrientationValueType }
     *
     */
    public void setOrientation(PolygonOrientationValueType value) {
        this.orientation = value;
    }

}
