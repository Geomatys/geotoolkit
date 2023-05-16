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
 * <p>Classe Java pour HorizontalCoordinateSystem complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="HorizontalCoordinateSystem">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Geodetic_Model" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}GeodeticModel" minOccurs="0"/>
 *         &lt;choice minOccurs="0">
 *           &lt;element name="Geographic_Coordinate_System" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}GeographicCoordinateSystem"/>
 *           &lt;element name="Local_Coordinate_System" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}LocalCoordinateSystem"/>
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
@XmlType(name = "HorizontalCoordinateSystem", propOrder = {
    "geodeticModel",
    "geographicCoordinateSystem",
    "localCoordinateSystem"
})
public class HorizontalCoordinateSystem {

    @XmlElement(name = "Geodetic_Model")
    protected GeodeticModel geodeticModel;
    @XmlElement(name = "Geographic_Coordinate_System")
    protected GeographicCoordinateSystem geographicCoordinateSystem;
    @XmlElement(name = "Local_Coordinate_System")
    protected LocalCoordinateSystem localCoordinateSystem;

    /**
     * Obtient la valeur de la propriété geodeticModel.
     *
     * @return
     *     possible object is
     *     {@link GeodeticModel }
     *
     */
    public GeodeticModel getGeodeticModel() {
        return geodeticModel;
    }

    /**
     * Définit la valeur de la propriété geodeticModel.
     *
     * @param value
     *     allowed object is
     *     {@link GeodeticModel }
     *
     */
    public void setGeodeticModel(GeodeticModel value) {
        this.geodeticModel = value;
    }

    /**
     * Obtient la valeur de la propriété geographicCoordinateSystem.
     *
     * @return
     *     possible object is
     *     {@link GeographicCoordinateSystem }
     *
     */
    public GeographicCoordinateSystem getGeographicCoordinateSystem() {
        return geographicCoordinateSystem;
    }

    /**
     * Définit la valeur de la propriété geographicCoordinateSystem.
     *
     * @param value
     *     allowed object is
     *     {@link GeographicCoordinateSystem }
     *
     */
    public void setGeographicCoordinateSystem(GeographicCoordinateSystem value) {
        this.geographicCoordinateSystem = value;
    }

    /**
     * Obtient la valeur de la propriété localCoordinateSystem.
     *
     * @return
     *     possible object is
     *     {@link LocalCoordinateSystem }
     *
     */
    public LocalCoordinateSystem getLocalCoordinateSystem() {
        return localCoordinateSystem;
    }

    /**
     * Définit la valeur de la propriété localCoordinateSystem.
     *
     * @param value
     *     allowed object is
     *     {@link LocalCoordinateSystem }
     *
     */
    public void setLocalCoordinateSystem(LocalCoordinateSystem value) {
        this.localCoordinateSystem = value;
    }

}
