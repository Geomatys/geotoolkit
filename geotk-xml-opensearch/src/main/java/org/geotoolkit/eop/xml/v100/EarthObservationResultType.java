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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311.AbstractFeatureType;


/**
 * <p>Classe Java pour EarthObservationResultType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="EarthObservationResultType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractFeatureType">
 *       &lt;sequence>
 *         &lt;element name="browse" type="{http://earth.esa.int/eop}BrowseInformationArrayPropertyType" minOccurs="0"/>
 *         &lt;element name="product" type="{http://earth.esa.int/eop}ProductInformationArrayPropertyType" minOccurs="0"/>
 *         &lt;element name="mask" type="{http://earth.esa.int/eop}MaskInformationArrayPropertyType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EarthObservationResultType", propOrder = {
    "browse",
    "product",
    "mask"
})
public class EarthObservationResultType
    extends AbstractFeatureType
{

    protected BrowseInformationArrayPropertyType browse;
    protected ProductInformationArrayPropertyType product;
    protected MaskInformationArrayPropertyType mask;

    /**
     * Obtient la valeur de la propriété browse.
     *
     * @return
     *     possible object is
     *     {@link BrowseInformationArrayPropertyType }
     *
     */
    public BrowseInformationArrayPropertyType getBrowse() {
        return browse;
    }

    /**
     * Définit la valeur de la propriété browse.
     *
     * @param value
     *     allowed object is
     *     {@link BrowseInformationArrayPropertyType }
     *
     */
    public void setBrowse(BrowseInformationArrayPropertyType value) {
        this.browse = value;
    }

    /**
     * Obtient la valeur de la propriété product.
     *
     * @return
     *     possible object is
     *     {@link ProductInformationArrayPropertyType }
     *
     */
    public ProductInformationArrayPropertyType getProduct() {
        return product;
    }

    /**
     * Définit la valeur de la propriété product.
     *
     * @param value
     *     allowed object is
     *     {@link ProductInformationArrayPropertyType }
     *
     */
    public void setProduct(ProductInformationArrayPropertyType value) {
        this.product = value;
    }

    /**
     * Obtient la valeur de la propriété mask.
     *
     * @return
     *     possible object is
     *     {@link MaskInformationArrayPropertyType }
     *
     */
    public MaskInformationArrayPropertyType getMask() {
        return mask;
    }

    /**
     * Définit la valeur de la propriété mask.
     *
     * @param value
     *     allowed object is
     *     {@link MaskInformationArrayPropertyType }
     *
     */
    public void setMask(MaskInformationArrayPropertyType value) {
        this.mask = value;
    }

}
