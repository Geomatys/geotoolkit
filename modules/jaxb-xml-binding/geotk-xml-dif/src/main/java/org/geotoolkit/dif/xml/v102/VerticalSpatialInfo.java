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
 *                 This entity contains the domain value and type for the granule's
 *                 vertical spatial domain. The reference frame or system from
 *                 which altitude or depths are measured. The term 'altitude' is
 *                 used instead of the common term 'elevation' to conform to the
 *                 terminology in Federal Information Processing Standards 70-1 and
 *                 173. The information contains the datum name, distance units and
 *                 encoding method, which provide the definition for the system.
 *
 *                 * Type : This attribute describes the type of the area of vertical space covered by the locality.
 *                 * Value : This attribute describes the extent of the area of vertical space covered by the granule. Must be accompanied by an Altitude Encoding Method description. The datatype for this attribute is the value of the attribute VerticalSpatialDomainType. The unit for this attribute is the value of either DepthDistanceUnits or AltitudeDistanceUnits.
 *
 *                 | DIF 9        | ECHO 10                     | UMM                    | DIF 10                         | Notes                                                   |
 *                 | ------------ | ----------------------------|----------------------- | ------------------------------ | ------------------------------------------------------- |
 *                 |      -       | VerticalSpatialDomain       | VerticalSpatialDomain  | Vertical_Spatial_Info          | Added from ECHO, inside Vertical_Spatial_Info, use type |
 *
 *
 * <p>Classe Java pour VerticalSpatialInfo complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="VerticalSpatialInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Type" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Value" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VerticalSpatialInfo", propOrder = {
    "type",
    "value"
})
public class VerticalSpatialInfo {

    @XmlElement(name = "Type", required = true)
    protected String type;
    @XmlElement(name = "Value", required = true)
    protected String value;

    /**
     * Obtient la valeur de la propriété type.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getType() {
        return type;
    }

    /**
     * Définit la valeur de la propriété type.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Obtient la valeur de la propriété value.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getValue() {
        return value;
    }

    /**
     * Définit la valeur de la propriété value.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setValue(String value) {
        this.value = value;
    }

}
