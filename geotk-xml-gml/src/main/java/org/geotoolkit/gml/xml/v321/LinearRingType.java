/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2012, Geomatys
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


package org.geotoolkit.gml.xml.v321;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.LinearRing;


/**
 * <p>Java class for LinearRingType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="LinearRingType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml/3.2}AbstractRingType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;choice maxOccurs="unbounded" minOccurs="4">
 *             &lt;element ref="{http://www.opengis.net/gml/3.2}pos"/>
 *             &lt;element ref="{http://www.opengis.net/gml/3.2}pointProperty"/>
 *             &lt;element ref="{http://www.opengis.net/gml/3.2}pointRep"/>
 *           &lt;/choice>
 *           &lt;element ref="{http://www.opengis.net/gml/3.2}posList"/>
 *           &lt;element ref="{http://www.opengis.net/gml/3.2}coordinates"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LinearRingType", propOrder = {
    "posOrPointPropertyOrPointRep",
    "posList",
    "coordinates"
})
public class LinearRingType extends AbstractRingType implements LinearRing {

    @XmlElementRefs({
        @XmlElementRef(name = "pos", namespace = "http://www.opengis.net/gml/3.2", type = JAXBElement.class),
        @XmlElementRef(name = "pointProperty", namespace = "http://www.opengis.net/gml/3.2", type = JAXBElement.class),
        @XmlElementRef(name = "pointRep", namespace = "http://www.opengis.net/gml/3.2", type = JAXBElement.class)
    })
    private List<JAXBElement<?>> posOrPointPropertyOrPointRep;
    private DirectPositionListType posList;
    private CoordinatesType coordinates;

    public LinearRingType() {

    }

    public LinearRingType(final String srsName, final DirectPositionListType posList) {
        super(srsName);
        this.posList = posList;
    }

    /**
     * Gets the value of the posOrPointPropertyOrPointRep property.
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link PointPropertyType }{@code >}
     * {@link JAXBElement }{@code <}{@link DirectPositionType }{@code >}
     * {@link JAXBElement }{@code <}{@link PointPropertyType }{@code >}
     *
     *
     */
    public List<JAXBElement<?>> getPosOrPointPropertyOrPointRep() {
        if (posOrPointPropertyOrPointRep == null) {
            posOrPointPropertyOrPointRep = new ArrayList<JAXBElement<?>>();
        }
        return this.posOrPointPropertyOrPointRep;
    }

    /**
     * Gets the value of the posList property.
     *
     * @return
     *     possible object is
     *     {@link DirectPositionListType }
     *
     */
    public DirectPositionListType getPosList() {
        return posList;
    }

    /**
     * Sets the value of the posList property.
     *
     * @param value
     *     allowed object is
     *     {@link DirectPositionListType }
     *
     */
    public void setPosList(DirectPositionListType value) {
        this.posList = value;
    }

    /**
     * Gets the value of the coordinates property.
     *
     * @return
     *     possible object is
     *     {@link CoordinatesType }
     *
     */
    public CoordinatesType getCoordinates() {
        return coordinates;
    }

    /**
     * Sets the value of the coordinates property.
     *
     * @param value
     *     allowed object is
     *     {@link CoordinatesType }
     *
     */
    public void setCoordinates(CoordinatesType value) {
        this.coordinates = value;
    }

}
