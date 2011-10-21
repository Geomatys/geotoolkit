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

package org.geotoolkit.sml.xml.v101;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.sml.xml.AbstractDataSource;
import org.geotoolkit.util.ComparisonMode;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for DataSourceType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DataSourceType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/sensorML/1.0.1}AbstractProcessType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;sequence>
 *             &lt;element name="dataDefinition">
 *               &lt;complexType>
 *                 &lt;complexContent>
 *                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                     &lt;choice minOccurs="0">
 *                       &lt;element ref="{http://www.opengis.net/swe/1.0.1}DataBlockDefinition"/>
 *                       &lt;element ref="{http://www.opengis.net/swe/1.0.1}DataStreamDefinition"/>
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

    public DataSourceType() {

    }
    
    public DataSourceType(final DataDefinition dataDefinition, final Values values, final ObservationReference observationReference) {
        this.dataDefinition = dataDefinition;
        this.observationReference = observationReference;
        this.values = values;
    }

    public DataSourceType(final AbstractDataSource ds) {
        super(ds);
        if (ds != null) {
            if (ds.getDataDefinition() != null) {
                this.dataDefinition = new DataDefinition(ds.getDataDefinition());
            }
            if (ds.getValues() != null) {
                this.values = new Values(ds.getValues());
            }
            if  (ds.getObservationReference() != null) {
                this.observationReference = new ObservationReference(ds.getObservationReference());
            }
        }
    }

    /**
     * Gets the value of the dataDefinition property.
     * 
     * @return
     *     possible object is
     *     {@link DataSourceType.DataDefinition }
     *     
     */
    public DataDefinition getDataDefinition() {
        return dataDefinition;
    }

    /**
     * Sets the value of the dataDefinition property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataSourceType.DataDefinition }
     *     
     */
    public void setDataDefinition(final DataDefinition value) {
        this.dataDefinition = value;
    }

    /**
     * Gets the value of the values property.
     * 
     * @return
     *     possible object is
     *     {@link DataSourceType.Values }
     *     
     */
    public Values getValues() {
        return values;
    }

    /**
     * Sets the value of the values property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataSourceType.Values }
     *     
     */
    public void setValues(final Values value) {
        this.values = value;
    }

    /**
     * Gets the value of the observationReference property.
     * 
     * @return
     *     possible object is
     *     {@link DataSourceType.ObservationReference }
     *     
     */
    public ObservationReference getObservationReference() {
        return observationReference;
    }

    /**
     * Sets the value of the observationReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataSourceType.ObservationReference }
     *     
     */
    public void setObservationReference(final ObservationReference value) {
        this.observationReference = value;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        if (dataDefinition != null) {
            sb.append("dataDefinition:").append(dataDefinition).append('\n');
        }
        if (observationReference != null) {
            sb.append("observationReference:").append(observationReference).append('\n');
        }
        if (values != null) {
            sb.append("values:").append(values).append('\n');
        }
        return sb.toString();
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true;
        }

        if (object instanceof DataSourceType && super.equals(object, mode)) {
            final DataSourceType that = (DataSourceType) object;
            return Utilities.equals(this.dataDefinition,    that.dataDefinition)       &&
                   Utilities.equals(this.observationReference, that.observationReference)    &&
                   Utilities.equals(this.values,  that.values);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.dataDefinition != null ? this.dataDefinition.hashCode() : 0);
        hash = 17 * hash + (this.observationReference != null ? this.observationReference.hashCode() : 0);
        hash = 17 * hash + (this.values != null ? this.values.hashCode() : 0);
        return hash;
    }
}
