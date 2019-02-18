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


package org.geotoolkit.opt.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311.AngleType;


/**
 * <p>Classe Java pour AcquisitionType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="AcquisitionType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://earth.esa.int/eop}AcquisitionType">
 *       &lt;sequence>
 *         &lt;element name="illuminationAzimuthAngle" type="{http://www.opengis.net/gml}AngleType" minOccurs="0"/>
 *         &lt;element name="illuminationElevationAngle" type="{http://www.opengis.net/gml}AngleType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AcquisitionType", propOrder = {
    "illuminationAzimuthAngle",
    "illuminationElevationAngle"
})
public class AcquisitionType
    extends org.geotoolkit.eop.xml.v100.AcquisitionType
{

    protected AngleType illuminationAzimuthAngle;
    protected AngleType illuminationElevationAngle;

    /**
     * Obtient la valeur de la propriété illuminationAzimuthAngle.
     *
     * @return
     *     possible object is
     *     {@link AngleType }
     *
     */
    public AngleType getIlluminationAzimuthAngle() {
        return illuminationAzimuthAngle;
    }

    /**
     * Définit la valeur de la propriété illuminationAzimuthAngle.
     *
     * @param value
     *     allowed object is
     *     {@link AngleType }
     *
     */
    public void setIlluminationAzimuthAngle(AngleType value) {
        this.illuminationAzimuthAngle = value;
    }

    /**
     * Obtient la valeur de la propriété illuminationElevationAngle.
     *
     * @return
     *     possible object is
     *     {@link AngleType }
     *
     */
    public AngleType getIlluminationElevationAngle() {
        return illuminationElevationAngle;
    }

    /**
     * Définit la valeur de la propriété illuminationElevationAngle.
     *
     * @param value
     *     allowed object is
     *     {@link AngleType }
     *
     */
    public void setIlluminationElevationAngle(AngleType value) {
        this.illuminationElevationAngle = value;
    }

}
