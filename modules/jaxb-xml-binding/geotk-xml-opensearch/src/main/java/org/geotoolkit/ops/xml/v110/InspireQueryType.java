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


package org.geotoolkit.ops.xml.v110;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour InspireQueryType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="InspireQueryType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://a9.com/-/spec/opensearch/1.1/}QueryType">
 *       &lt;attribute ref="{http://inspire.ec.europa.eu/schemas/inspire_dls/1.0}spatial_dataset_identifier_code"/>
 *       &lt;attribute ref="{http://inspire.ec.europa.eu/schemas/inspire_dls/1.0}spatial_dataset_identifier_namespace"/>
 *       &lt;attribute ref="{http://inspire.ec.europa.eu/schemas/inspire_dls/1.0}crs"/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InspireQueryType")
public class InspireQueryType extends QueryType {

    @XmlAttribute(name = "spatial_dataset_identifier_code", namespace = "http://inspire.ec.europa.eu/schemas/inspire_dls/1.0")
    protected String spatialDatasetIdentifierCode;
    @XmlAttribute(name = "spatial_dataset_identifier_namespace", namespace = "http://inspire.ec.europa.eu/schemas/inspire_dls/1.0")
    @XmlSchemaType(name = "anyURI")
    protected String spatialDatasetIdentifierNamespace;
    @XmlAttribute(name = "crs", namespace = "http://inspire.ec.europa.eu/schemas/inspire_dls/1.0")
    @XmlSchemaType(name = "anyURI")
    protected String crs;

    public InspireQueryType() {

    }

    public InspireQueryType(InspireQueryType that) {
        super(that);
        if (that != null) {
            this.crs = that.crs;
            this.spatialDatasetIdentifierCode = that.spatialDatasetIdentifierCode;
            this.spatialDatasetIdentifierNamespace = that.spatialDatasetIdentifierNamespace;
        }
    }
    /**
     * Obtient la valeur de la propriété spatialDatasetIdentifierCode.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSpatialDatasetIdentifierCode() {
        return spatialDatasetIdentifierCode;
    }

    /**
     * Définit la valeur de la propriété spatialDatasetIdentifierCode.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSpatialDatasetIdentifierCode(String value) {
        this.spatialDatasetIdentifierCode = value;
    }

    /**
     * Obtient la valeur de la propriété spatialDatasetIdentifierNamespace.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSpatialDatasetIdentifierNamespace() {
        return spatialDatasetIdentifierNamespace;
    }

    /**
     * Définit la valeur de la propriété spatialDatasetIdentifierNamespace.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSpatialDatasetIdentifierNamespace(String value) {
        this.spatialDatasetIdentifierNamespace = value;
    }

    /**
     * Obtient la valeur de la propriété crs.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCrs() {
        return crs;
    }

    /**
     * Définit la valeur de la propriété crs.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCrs(String value) {
        this.crs = value;
    }

}
