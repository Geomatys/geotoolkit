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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 *
 * In KML 2.3, the allowed value range in decimal degrees used by kml:east and kml:west was extended by a factor of 2 (from ±180 in KML 2.2) to ±360. This was done in order to accommodate bounding boxes anywhere on the earth, including overlaps of the anti-meridian, and of any size up to full global coverage. With the extension of the longitude range, all degree values, except -360 = 0 = 360 (mod 360), have exactly two equivalent choices modulo 360, e.g. -359 = 1 (mod 360). The latitude range for kml:north and kml:south remain the same as in KML 2.2 and the following constraints C1 (i.e. the non-trivial latitude interval constraint) and C2 (i.e. the non-trivial longitude interval constraint) are unchanged:
 *     C1  kml:south < kml:north (non-trivial latitude interval);
 *     C2  kml:west < kml:east (non-trivial longitude interval).
 * New constraints in KML 2.3 are introduced with the longitude range extension to avoid self overlaps and to preserve uniqueness of longitude interval values:
 *     C3  kml:east - kml:west ≤ 360 (non-self-overlap);
 *     C4  If (|kml:west| or |kml:east|) > 180, then kml:east > 0 and kml:west < 180 (uniqueness).
 * The constraint C3 ensures that the longitude interval does not overlap itself. The constraint C4 ensures the choice of the kml:west and kml:east values are unique for every longitude interval. See also: kml:east, kml:west, kml:north, kml:south.
 *
 *
 * <p>Classe Java pour AbstractLatLonBoxType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="AbstractLatLonBoxType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/kml/2.2}AbstractExtentType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}north" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}south" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}east" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}west" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}AbstractLatLonBoxSimpleExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}AbstractLatLonBoxObjectExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractLatLonBoxType", namespace = "http://www.opengis.net/kml/2.2", propOrder = {
    "north",
    "south",
    "east",
    "west",
    "abstractLatLonBoxSimpleExtensionGroup",
    "abstractLatLonBoxObjectExtensionGroup"
})
@XmlSeeAlso({
    LatLonBoxType.class,
    LatLonAltBoxType.class
})
public abstract class AbstractLatLonBoxType
    extends AbstractExtentType
{

    @XmlElement(namespace = "http://www.opengis.net/kml/2.2", defaultValue = "90.0")
    protected Double north;
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2", defaultValue = "-90.0")
    protected Double south;
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2", defaultValue = "180.0")
    protected Double east;
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2", defaultValue = "-180.0")
    protected Double west;
    @XmlElement(name = "AbstractLatLonBoxSimpleExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<Object> abstractLatLonBoxSimpleExtensionGroup;
    @XmlElement(name = "AbstractLatLonBoxObjectExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<AbstractObjectType> abstractLatLonBoxObjectExtensionGroup;

    /**
     * Obtient la valeur de la propriété north.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getNorth() {
        return north;
    }

    /**
     * Définit la valeur de la propriété north.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setNorth(Double value) {
        this.north = value;
    }

    /**
     * Obtient la valeur de la propriété south.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getSouth() {
        return south;
    }

    /**
     * Définit la valeur de la propriété south.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setSouth(Double value) {
        this.south = value;
    }

    /**
     * Obtient la valeur de la propriété east.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getEast() {
        return east;
    }

    /**
     * Définit la valeur de la propriété east.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setEast(Double value) {
        this.east = value;
    }

    /**
     * Obtient la valeur de la propriété west.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getWest() {
        return west;
    }

    /**
     * Définit la valeur de la propriété west.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setWest(Double value) {
        this.west = value;
    }

    /**
     * Gets the value of the abstractLatLonBoxSimpleExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the abstractLatLonBoxSimpleExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAbstractLatLonBoxSimpleExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     *
     *
     */
    public List<Object> getAbstractLatLonBoxSimpleExtensionGroup() {
        if (abstractLatLonBoxSimpleExtensionGroup == null) {
            abstractLatLonBoxSimpleExtensionGroup = new ArrayList<Object>();
        }
        return this.abstractLatLonBoxSimpleExtensionGroup;
    }

    /**
     * Gets the value of the abstractLatLonBoxObjectExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the abstractLatLonBoxObjectExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAbstractLatLonBoxObjectExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractObjectType }
     *
     *
     */
    public List<AbstractObjectType> getAbstractLatLonBoxObjectExtensionGroup() {
        if (abstractLatLonBoxObjectExtensionGroup == null) {
            abstractLatLonBoxObjectExtensionGroup = new ArrayList<AbstractObjectType>();
        }
        return this.abstractLatLonBoxObjectExtensionGroup;
    }

}
