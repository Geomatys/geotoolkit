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
import javax.xml.bind.annotation.XmlType;


/**
 *
 *                 This entity stores the reference frame or system from which
 *                 altitudes (elevations) are measured. The information contains
 *                 the datum name, distance units and encoding method, which
 *                 provide the definition for the system. This table also stores
 *                 the characteristics of the reference frame or system from which
 *                 depths are measured. The additional information in the table are
 *                 geometry reference data etc.
 *
 *                 | ECHO 10                    | UMM                        | DIF 10                       | Notes                              |
 *                 | -------------------------- | -------------------------- | ---------------------------- | ---------------------------------- |
 *                 | SpatialCoverageType        | SpatialCoverageType        | Spatial_Coverage_Type        | Added and renamed from ECHO 10     |
 *                 | VerticalCoordinateSystem   | VerticalCoordinateSystem   | Vertical_Coordinate_System   | Added and renamed from ECHO 10     |
 *                 | HorizontalCoordinateSystem | HorizontalCoordinateSystem | Horizontal_Coordinate_System | Added and renamed from ECHO 10     |
 *                 | TwoDCoordinateSystem       | TwoDCoordinateSystem       | TwoD_Coordinate_System       | Added from top level ECHO 10 field |
 *
 *
 *
 * <p>Classe Java pour SpatialInfo complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="SpatialInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Spatial_Coverage_Type" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Horizontal_Coordinate_System" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}HorizontalCoordinateSystem" minOccurs="0"/>
 *         &lt;element name="TwoD_Coordinate_System" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}TwoDCoordinateSystem" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SpatialInfo", propOrder = {
    "spatialCoverageType",
    "horizontalCoordinateSystem",
    "twoDCoordinateSystem"
})
public class SpatialInfo {

    @XmlElement(name = "Spatial_Coverage_Type", required = true)
    protected String spatialCoverageType;
    @XmlElement(name = "Horizontal_Coordinate_System")
    protected HorizontalCoordinateSystem horizontalCoordinateSystem;
    @XmlElement(name = "TwoD_Coordinate_System")
    protected List<TwoDCoordinateSystem> twoDCoordinateSystem;

    /**
     * Obtient la valeur de la propriété spatialCoverageType.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSpatialCoverageType() {
        return spatialCoverageType;
    }

    /**
     * Définit la valeur de la propriété spatialCoverageType.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSpatialCoverageType(String value) {
        this.spatialCoverageType = value;
    }

    /**
     * Obtient la valeur de la propriété horizontalCoordinateSystem.
     *
     * @return
     *     possible object is
     *     {@link HorizontalCoordinateSystem }
     *
     */
    public HorizontalCoordinateSystem getHorizontalCoordinateSystem() {
        return horizontalCoordinateSystem;
    }

    /**
     * Définit la valeur de la propriété horizontalCoordinateSystem.
     *
     * @param value
     *     allowed object is
     *     {@link HorizontalCoordinateSystem }
     *
     */
    public void setHorizontalCoordinateSystem(HorizontalCoordinateSystem value) {
        this.horizontalCoordinateSystem = value;
    }

    /**
     * Gets the value of the twoDCoordinateSystem property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the twoDCoordinateSystem property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTwoDCoordinateSystem().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TwoDCoordinateSystem }
     *
     *
     */
    public List<TwoDCoordinateSystem> getTwoDCoordinateSystem() {
        if (twoDCoordinateSystem == null) {
            twoDCoordinateSystem = new ArrayList<TwoDCoordinateSystem>();
        }
        return this.twoDCoordinateSystem;
    }

}
