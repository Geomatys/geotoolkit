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

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 *
 *
 *
 * <p>Classe Java pour GeodeticModel complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="GeodeticModel">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Horizontal_DatumName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Ellipsoid_Name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Semi_Major_Axis" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="Denominator_Of_Flattening_Ratio" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GeodeticModel", propOrder = {
    "horizontalDatumName",
    "ellipsoidName",
    "semiMajorAxis",
    "denominatorOfFlatteningRatio"
})
public class GeodeticModel {

    @XmlElement(name = "Horizontal_DatumName")
    protected String horizontalDatumName;
    @XmlElement(name = "Ellipsoid_Name")
    protected String ellipsoidName;
    @XmlElement(name = "Semi_Major_Axis")
    protected BigDecimal semiMajorAxis;
    @XmlElement(name = "Denominator_Of_Flattening_Ratio")
    protected BigDecimal denominatorOfFlatteningRatio;

    /**
     * Obtient la valeur de la propriété horizontalDatumName.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getHorizontalDatumName() {
        return horizontalDatumName;
    }

    /**
     * Définit la valeur de la propriété horizontalDatumName.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setHorizontalDatumName(String value) {
        this.horizontalDatumName = value;
    }

    /**
     * Obtient la valeur de la propriété ellipsoidName.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getEllipsoidName() {
        return ellipsoidName;
    }

    /**
     * Définit la valeur de la propriété ellipsoidName.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setEllipsoidName(String value) {
        this.ellipsoidName = value;
    }

    /**
     * Obtient la valeur de la propriété semiMajorAxis.
     *
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *
     */
    public BigDecimal getSemiMajorAxis() {
        return semiMajorAxis;
    }

    /**
     * Définit la valeur de la propriété semiMajorAxis.
     *
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *
     */
    public void setSemiMajorAxis(BigDecimal value) {
        this.semiMajorAxis = value;
    }

    /**
     * Obtient la valeur de la propriété denominatorOfFlatteningRatio.
     *
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *
     */
    public BigDecimal getDenominatorOfFlatteningRatio() {
        return denominatorOfFlatteningRatio;
    }

    /**
     * Définit la valeur de la propriété denominatorOfFlatteningRatio.
     *
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *
     */
    public void setDenominatorOfFlatteningRatio(BigDecimal value) {
        this.denominatorOfFlatteningRatio = value;
    }

}
