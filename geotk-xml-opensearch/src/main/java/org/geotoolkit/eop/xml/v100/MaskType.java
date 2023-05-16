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


/**
 * <p>Classe Java pour MaskType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="MaskType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractFeatureType">
 *       &lt;sequence>
 *         &lt;element ref="{http://earth.esa.int/eop}maskMembers"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MaskType", propOrder = {
    "maskMembers"
})
public class MaskType
    extends AbstractFeatureType
{

    @XmlElement(required = true)
    protected MaskMembersPropertyType maskMembers;

    /**
     * Obtient la valeur de la propriété maskMembers.
     *
     * @return
     *     possible object is
     *     {@link MaskMembersPropertyType }
     *
     */
    public MaskMembersPropertyType getMaskMembers() {
        return maskMembers;
    }

    /**
     * Définit la valeur de la propriété maskMembers.
     *
     * @param value
     *     allowed object is
     *     {@link MaskMembersPropertyType }
     *
     */
    public void setMaskMembers(MaskMembersPropertyType value) {
        this.maskMembers = value;
    }

}
