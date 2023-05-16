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
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlType;
import org.geotoolkit.observation.xml.v200.OMObservationType;


/**
 * <p>Classe Java pour EarthObservationType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="EarthObservationType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/om/2.0}OM_ObservationType">
 *       &lt;sequence>
 *         &lt;element name="metaDataProperty" type="{http://www.opengis.net/eop/2.1}EarthObservationMetaDataPropertyType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EarthObservationType", propOrder = {
    "rest"
})
public class EarthObservationType
    extends OMObservationType
{

    @XmlElementRef(name = "metaDataProperty", namespace = "http://www.opengis.net/eop/2.1", type = JAXBElement.class, required = false)
    protected List<JAXBElement<EarthObservationMetaDataPropertyType>> rest;

    /**
     * Obtient le reste du modèle de contenu.
     *
     * <p>
     * Vous obtenez la propriété "catch-all" pour la raison suivante :
     * Le nom de champ "MetaDataProperty" est utilisé par deux parties différentes d'un schéma. Reportez-vous à :
     * ligne 26 sur file:/home/guilhem/xsd/eompom/1.1.0/eop.xsd
     * ligne 38 sur file:/home/guilhem/xsd/SOS/gml/3.2.1/gmlBase.xsd
     * <p>
     * Pour vous débarrasser de cette propriété, appliquez une personnalisation de propriété à l'une
     * des deux déclarations suivantes afin de modifier leurs noms :
     * Gets the value of the rest property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rest property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRest().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link EarthObservationMetaDataPropertyType }{@code >}
     *
     *
     */
    public List<JAXBElement<EarthObservationMetaDataPropertyType>> getRest() {
        if (rest == null) {
            rest = new ArrayList<JAXBElement<EarthObservationMetaDataPropertyType>>();
        }
        return this.rest;
    }

}
