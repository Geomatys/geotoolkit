/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.sml.xml.v100;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.sml.xml.AbstractDataSource;

/**
 * <p>Java class for DataSourceType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DataSourceType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/sensorML/1.0}AbstractProcessType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;sequence>
 *             &lt;element name="dataDefinition">
 *               &lt;complexType>
 *                 &lt;complexContent>
 *                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                     &lt;choice minOccurs="0">
 *                       &lt;element ref="{http://www.opengis.net/swe/1.0}DataBlockDefinition"/>
 *                       &lt;element ref="{http://www.opengis.net/swe/1.0}DataStreamDefinition"/>
 *                     &lt;/choice>
 *                     &lt;attGroup ref="{http://www.opengis.net/gml}AssociationAttributeGroup"/>
 *                   &lt;/restriction>
 *                 &lt;/complexContent>
 *               &lt;/complexType>
 *             &lt;/element>
 *             &lt;element name="values">
 *               &lt;complexType>
 *                 &lt;complexContent>
 *                   &lt;extension base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                   &lt;/extension>
 *                 &lt;/complexContent>
 *               &lt;/complexType>
 *             &lt;/element>
 *           &lt;/sequence>
 *           &lt;element name="observationReference">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                   &lt;attGroup ref="{http://www.opengis.net/gml}AssociationAttributeGroup"/>
 *                 &lt;/restriction>
 *               &lt;/complexContent>
 *             &lt;/complexType>
 *           &lt;/element>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DataSourceType", propOrder = {
    "dataDefinition",
    "values",
    "observationReference"
})
public class DataSourceType extends AbstractProcessType implements AbstractDataSource {

    private DataDefinition dataDefinition;
    private Values values;
    private ObservationReference observationReference;

    /**
     * Gets the value of the dataDefinition property.
     */
    public DataDefinition getDataDefinition() {
        return dataDefinition;
    }

    /**
     * Sets the value of the dataDefinition property.
     */
    public void setDataDefinition(DataDefinition value) {
        this.dataDefinition = value;
    }

    /**
     * Gets the value of the values property.
     */
    public Values getValues() {
        return values;
    }

    /**
     * Sets the value of the values property.
     */
    public void setValues(Values value) {
        this.values = value;
    }

    /**
     * Gets the value of the observationReference property.
     */
    public ObservationReference getObservationReference() {
        return observationReference;
    }

    /**
     * Sets the value of the observationReference property.
     */
    public void setObservationReference(ObservationReference value) {
        this.observationReference = value;
    }

}
