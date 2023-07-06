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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311.AbstractFeatureType;
import org.geotoolkit.gml.xml.v311.SurfacePropertyType;


/**
 * <p>Classe Java pour MaskFeatureType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="MaskFeatureType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractFeatureType">
 *       &lt;sequence>
 *         &lt;element name="maskType" type="{http://earth.esa.int/eop}CodeWithAuthorityType"/>
 *         &lt;element ref="{http://www.opengis.net/gml}extentOf"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MaskFeatureType", propOrder = {
    "maskType",
    "extentOf"
})
public class MaskFeatureType
    extends AbstractFeatureType
{

    @XmlElement(required = true)
    protected CodeWithAuthorityType maskType;
    @XmlElement(namespace = "http://www.opengis.net/gml", required = true)
    protected SurfacePropertyType extentOf;

    /**
     * Obtient la valeur de la propriété maskType.
     *
     * @return
     *     possible object is
     *     {@link CodeWithAuthorityType }
     *
     */
    public CodeWithAuthorityType getMaskType() {
        return maskType;
    }

    /**
     * Définit la valeur de la propriété maskType.
     *
     * @param value
     *     allowed object is
     *     {@link CodeWithAuthorityType }
     *
     */
    public void setMaskType(CodeWithAuthorityType value) {
        this.maskType = value;
    }

    /**
     * Mask member extent. Expected structure is gml:Polygon/gml:exterior/gml:LinearRing/gml:posList with 0 to n gml:Polygon/gml:interior/gml:LinearRing/gml:posList elements representing the holes.
     *
     * @return
     *     possible object is
     *     {@link SurfacePropertyType }
     *
     */
    public SurfacePropertyType getExtentOf() {
        return extentOf;
    }

    /**
     * Définit la valeur de la propriété extentOf.
     *
     * @param value
     *     allowed object is
     *     {@link SurfacePropertyType }
     *
     */
    public void setExtentOf(SurfacePropertyType value) {
        this.extentOf = value;
    }

}
