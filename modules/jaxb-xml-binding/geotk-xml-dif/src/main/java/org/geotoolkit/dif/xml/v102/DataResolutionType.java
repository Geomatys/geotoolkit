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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 *
 *
 *             | DIF 9               | ECHO 10       | UMM        | DIF 10                   | Note                                               |
 *             | ------------------- | ------------- | ---------- | ------------------------ | -------------------------------------------------- |
 *             | Data_Resolution     |       -       | Resolution | Data_Resolution          | No change                                          |
 *             |
 *             | Vertical_Resolution | Resolution    | ?          | Vertical_Resolution      | Should be a number in the future, migrate all over |
 *             |         -           | DistanceUnits | ?          | Vertical_Resolution_Unit |                                                    |
 *
 *             Resolution is /Collection/VerticalCoordinateSystem/AltitudeSystemDefinition/Resolution/
 *
 *
 *
 * <p>Classe Java pour DataResolutionType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="DataResolutionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Latitude_Resolution" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Longitude_Resolution" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Horizontal_Resolution_Range" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}HorizontalResolutionRangeType" minOccurs="0"/>
 *         &lt;element name="Vertical_Resolution" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Vertical_Resolution_Unit" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Vertical_Resolution_Range" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}VerticalResolutionRangeType" minOccurs="0"/>
 *         &lt;element name="Temporal_Resolution" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Temporal_Resolution_Range" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}TemporalResolutionRangeType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DataResolutionType", propOrder = {
    "latitudeResolution",
    "longitudeResolution",
    "horizontalResolutionRange",
    "verticalResolution",
    "verticalResolutionUnit",
    "verticalResolutionRange",
    "temporalResolution",
    "temporalResolutionRange"
})
public class DataResolutionType {

    @XmlElement(name = "Latitude_Resolution")
    protected String latitudeResolution;
    @XmlElement(name = "Longitude_Resolution")
    protected String longitudeResolution;
    @XmlElement(name = "Horizontal_Resolution_Range")
    protected HorizontalResolutionRangeType horizontalResolutionRange;
    @XmlElement(name = "Vertical_Resolution")
    protected String verticalResolution;
    @XmlElement(name = "Vertical_Resolution_Unit")
    protected String verticalResolutionUnit;
    @XmlElement(name = "Vertical_Resolution_Range")
    protected VerticalResolutionRangeType verticalResolutionRange;
    @XmlElement(name = "Temporal_Resolution")
    protected String temporalResolution;
    @XmlElement(name = "Temporal_Resolution_Range")
    protected TemporalResolutionRangeType temporalResolutionRange;

    /**
     * Obtient la valeur de la propriété latitudeResolution.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLatitudeResolution() {
        return latitudeResolution;
    }

    /**
     * Définit la valeur de la propriété latitudeResolution.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLatitudeResolution(String value) {
        this.latitudeResolution = value;
    }

    /**
     * Obtient la valeur de la propriété longitudeResolution.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLongitudeResolution() {
        return longitudeResolution;
    }

    /**
     * Définit la valeur de la propriété longitudeResolution.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLongitudeResolution(String value) {
        this.longitudeResolution = value;
    }

    /**
     * Obtient la valeur de la propriété horizontalResolutionRange.
     *
     * @return
     *     possible object is
     *     {@link HorizontalResolutionRangeType }
     *
     */
    public HorizontalResolutionRangeType getHorizontalResolutionRange() {
        return horizontalResolutionRange;
    }

    /**
     * Définit la valeur de la propriété horizontalResolutionRange.
     *
     * @param value
     *     allowed object is
     *     {@link HorizontalResolutionRangeType }
     *
     */
    public void setHorizontalResolutionRange(HorizontalResolutionRangeType value) {
        this.horizontalResolutionRange = value;
    }

    /**
     * Obtient la valeur de la propriété verticalResolution.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getVerticalResolution() {
        return verticalResolution;
    }

    /**
     * Définit la valeur de la propriété verticalResolution.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setVerticalResolution(String value) {
        this.verticalResolution = value;
    }

    /**
     * Obtient la valeur de la propriété verticalResolutionUnit.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getVerticalResolutionUnit() {
        return verticalResolutionUnit;
    }

    /**
     * Définit la valeur de la propriété verticalResolutionUnit.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setVerticalResolutionUnit(String value) {
        this.verticalResolutionUnit = value;
    }

    /**
     * Obtient la valeur de la propriété verticalResolutionRange.
     *
     * @return
     *     possible object is
     *     {@link VerticalResolutionRangeType }
     *
     */
    public VerticalResolutionRangeType getVerticalResolutionRange() {
        return verticalResolutionRange;
    }

    /**
     * Définit la valeur de la propriété verticalResolutionRange.
     *
     * @param value
     *     allowed object is
     *     {@link VerticalResolutionRangeType }
     *
     */
    public void setVerticalResolutionRange(VerticalResolutionRangeType value) {
        this.verticalResolutionRange = value;
    }

    /**
     * Obtient la valeur de la propriété temporalResolution.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTemporalResolution() {
        return temporalResolution;
    }

    /**
     * Définit la valeur de la propriété temporalResolution.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTemporalResolution(String value) {
        this.temporalResolution = value;
    }

    /**
     * Obtient la valeur de la propriété temporalResolutionRange.
     *
     * @return
     *     possible object is
     *     {@link TemporalResolutionRangeType }
     *
     */
    public TemporalResolutionRangeType getTemporalResolutionRange() {
        return temporalResolutionRange;
    }

    /**
     * Définit la valeur de la propriété temporalResolutionRange.
     *
     * @param value
     *     allowed object is
     *     {@link TemporalResolutionRangeType }
     *
     */
    public void setTemporalResolutionRange(TemporalResolutionRangeType value) {
        this.temporalResolutionRange = value;
    }

}
