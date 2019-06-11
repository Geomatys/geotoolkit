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
package org.geotoolkit.eop.xml.v201;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v321.UnitOfMeasureType;
import org.geotoolkit.swe.xml.v101.PhenomenonPropertyType;


/**
 * <p>Classe Java pour ParameterInformationType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="ParameterInformationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="unitOfMeasure" type="{http://www.opengis.net/gml/3.2}UnitOfMeasureType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="phenomenon" type="{http://www.opengis.net/swe/1.0}PhenomenonPropertyType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ParameterInformationType", propOrder = {
    "unitOfMeasure",
    "phenomenon"
})
public class ParameterInformationType {

    protected List<UnitOfMeasureType> unitOfMeasure;
    protected PhenomenonPropertyType phenomenon;

    /**
     * Gets the value of the unitOfMeasure property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the unitOfMeasure property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUnitOfMeasure().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UnitOfMeasureType }
     *
     *
     */
    public List<UnitOfMeasureType> getUnitOfMeasure() {
        if (unitOfMeasure == null) {
            unitOfMeasure = new ArrayList<UnitOfMeasureType>();
        }
        return this.unitOfMeasure;
    }

    /**
     * Obtient la valeur de la propriété phenomenon.
     *
     * @return
     *     possible object is
     *     {@link PhenomenonPropertyType }
     *
     */
    public PhenomenonPropertyType getPhenomenon() {
        return phenomenon;
    }

    /**
     * Définit la valeur de la propriété phenomenon.
     *
     * @param value
     *     allowed object is
     *     {@link PhenomenonPropertyType }
     *
     */
    public void setPhenomenon(PhenomenonPropertyType value) {
        this.phenomenon = value;
    }

}
