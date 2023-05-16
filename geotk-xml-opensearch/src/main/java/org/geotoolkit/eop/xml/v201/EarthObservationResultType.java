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
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v321.AbstractFeatureType;
import org.geotoolkit.gml.xml.v321.ReferenceType;


/**
 * <p>Classe Java pour EarthObservationResultType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="EarthObservationResultType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml/3.2}AbstractFeatureType">
 *       &lt;sequence>
 *         &lt;element name="browse" type="{http://www.opengis.net/eop/2.1}BrowseInformationPropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="product" type="{http://www.opengis.net/eop/2.1}ProductInformationPropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="mask" type="{http://www.opengis.net/eop/2.1}MaskInformationPropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="parameter" type="{http://www.opengis.net/eop/2.1}ParameterInformationPropertyType" minOccurs="0"/>
 *         &lt;element name="coverage" type="{http://www.opengis.net/gml/3.2}ReferenceType" maxOccurs="unbounded" minOccurs="0"/>
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
    "mask",
    "parameter",
    "coverage"
})
public class EarthObservationResultType
    extends AbstractFeatureType
{

    protected List<BrowseInformationPropertyType> browse;
    protected List<ProductInformationPropertyType> product;
    protected List<MaskInformationPropertyType> mask;
    protected ParameterInformationPropertyType parameter;
    protected List<ReferenceType> coverage;

    /**
     * Gets the value of the browse property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the browse property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBrowse().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BrowseInformationPropertyType }
     *
     *
     */
    public List<BrowseInformationPropertyType> getBrowse() {
        if (browse == null) {
            browse = new ArrayList<BrowseInformationPropertyType>();
        }
        return this.browse;
    }

    /**
     * Gets the value of the product property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the product property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProduct().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProductInformationPropertyType }
     *
     *
     */
    public List<ProductInformationPropertyType> getProduct() {
        if (product == null) {
            product = new ArrayList<ProductInformationPropertyType>();
        }
        return this.product;
    }

    /**
     * Gets the value of the mask property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the mask property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMask().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MaskInformationPropertyType }
     *
     *
     */
    public List<MaskInformationPropertyType> getMask() {
        if (mask == null) {
            mask = new ArrayList<MaskInformationPropertyType>();
        }
        return this.mask;
    }

    /**
     * Obtient la valeur de la propriété parameter.
     *
     * @return
     *     possible object is
     *     {@link ParameterInformationPropertyType }
     *
     */
    public ParameterInformationPropertyType getParameter() {
        return parameter;
    }

    /**
     * Définit la valeur de la propriété parameter.
     *
     * @param value
     *     allowed object is
     *     {@link ParameterInformationPropertyType }
     *
     */
    public void setParameter(ParameterInformationPropertyType value) {
        this.parameter = value;
    }

    /**
     * Gets the value of the coverage property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the coverage property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCoverage().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ReferenceType }
     *
     *
     */
    public List<ReferenceType> getCoverage() {
        if (coverage == null) {
            coverage = new ArrayList<ReferenceType>();
        }
        return this.coverage;
    }

}
