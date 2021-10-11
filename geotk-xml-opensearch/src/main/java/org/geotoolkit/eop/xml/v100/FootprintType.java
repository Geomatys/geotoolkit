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


package org.geotoolkit.eop.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311.AbstractFeatureType;
import org.geotoolkit.gml.xml.v311.MultiSurfacePropertyType;
import org.geotoolkit.gml.xml.v311.PointPropertyType;


/**
 * <p>Classe Java pour FootprintType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="FootprintType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractFeatureType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml}multiExtentOf"/>
 *         &lt;element ref="{http://www.opengis.net/gml}centerOf" minOccurs="0"/>
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
    "centerOf"
})
public class FootprintType
    extends AbstractFeatureType
{

    @XmlElement(namespace = "http://www.opengis.net/gml", required = true)
    protected MultiSurfacePropertyType multiExtentOf;
    @XmlElement(namespace = "http://www.opengis.net/gml")
    protected PointPropertyType centerOf;

    /**
     * Acquisition footprint coordinates, described by a closed polygon (last point=first point), using CRS:WGS84, Latitude,Longitude pairs (per-WGS84 definition of point ordering, not necessarily per all WFS implementations). Expected structure is gml:Polygon/gml:exterior/gml:LinearRing/gml:posList.
     *
     * eop/EOLI : polygon/coordinates (F B b s)
     *
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
     * Acquisition center coordinates.  Expected structure is gml:Point/gml:pos.
     *
     * eop/EOLI : scCenter/coordinates (F B b s)
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

}
