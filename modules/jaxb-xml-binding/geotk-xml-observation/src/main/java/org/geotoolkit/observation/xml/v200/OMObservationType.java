/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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

package org.geotoolkit.observation.xml.v200;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v321.AbstractFeatureType;
import org.geotoolkit.gml.xml.v321.FeaturePropertyType;
import org.geotoolkit.gml.xml.v321.ReferenceType;
import org.geotoolkit.gml.xml.v321.TimeInstantPropertyType;
import org.geotoolkit.gml.xml.v321.TimePeriodPropertyType;
import org.opengis.metadata.Metadata;
import org.opengis.metadata.quality.Element;


/**
 *  Generic observation, whose result is anyType The following properties
 * 				are inherited from AbstractFeatureType: 
 * 				
 * 			
 * 
 * <p>Java class for OM_ObservationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OM_ObservationType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml/3.2}AbstractFeatureType">
 *       &lt;sequence>
 *         &lt;group ref="{http://www.opengis.net/om/2.0}OM_CommonProperties"/>
 *         &lt;element ref="{http://www.opengis.net/om/2.0}result"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OM_ObservationType", propOrder = {
    "type",
    "metadata",
    "relatedObservation",
    "phenomenonTime",
    "resultTime",
    "validTime",
    "procedure",
    "parameter",
    "observedProperty",
    "featureOfInterest",
    "resultQuality",
    "result"
})
public class OMObservationType extends AbstractFeatureType {

    private ReferenceType type;
    private Metadata metadata;
    private List<ObservationContextPropertyType> relatedObservation;
    @XmlElement(required = true)
    private TimeObjectPropertyType phenomenonTime;
    @XmlElement(required = true)
    private TimeInstantPropertyType resultTime;
    private TimePeriodPropertyType validTime;
    @XmlElement(required = true, nillable = true)
    private OMProcessPropertyType procedure;
    private List<NamedValuePropertyType> parameter;
    @XmlElement(required = true, nillable = true)
    private ReferenceType observedProperty;
    @XmlElement(required = true, nillable = true)
    private FeaturePropertyType featureOfInterest;
    private List<Element> resultQuality;
    @XmlElement(required = true)
    private Object result;

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link ReferenceType }
     *     
     */
    public ReferenceType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReferenceType }
     *     
     */
    public void setType(ReferenceType value) {
        this.type = value;
    }

    /**
     * Gets the value of the metadata property.
     * 
     * @return
     *     possible object is
     *     {@link MDMetadataPropertyType }
     *     
     */
    public Metadata getMetadata() {
        return metadata;
    }

    /**
     * Sets the value of the metadata property.
     * 
     * @param value
     *     allowed object is
     *     {@link MDMetadataPropertyType }
     *     
     */
    public void setMetadata(Metadata value) {
        this.metadata = value;
    }

    /**
     * Gets the value of the relatedObservation property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link ObservationContextPropertyType }
     * 
     * 
     */
    public List<ObservationContextPropertyType> getRelatedObservation() {
        if (relatedObservation == null) {
            relatedObservation = new ArrayList<ObservationContextPropertyType>();
        }
        return this.relatedObservation;
    }

    /**
     * Gets the value of the phenomenonTime property.
     * 
     * @return
     *     possible object is
     *     {@link TimeObjectPropertyType }
     *     
     */
    public TimeObjectPropertyType getPhenomenonTime() {
        return phenomenonTime;
    }

    /**
     * Sets the value of the phenomenonTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link TimeObjectPropertyType }
     *     
     */
    public void setPhenomenonTime(TimeObjectPropertyType value) {
        this.phenomenonTime = value;
    }

    /**
     * Gets the value of the resultTime property.
     * 
     * @return
     *     possible object is
     *     {@link TimeInstantPropertyType }
     *     
     */
    public TimeInstantPropertyType getResultTime() {
        return resultTime;
    }

    /**
     * Sets the value of the resultTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link TimeInstantPropertyType }
     *     
     */
    public void setResultTime(TimeInstantPropertyType value) {
        this.resultTime = value;
    }

    /**
     * Gets the value of the validTime property.
     * 
     * @return
     *     possible object is
     *     {@link TimePeriodPropertyType }
     *     
     */
    public TimePeriodPropertyType getValidTime() {
        return validTime;
    }

    /**
     * Sets the value of the validTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link TimePeriodPropertyType }
     *     
     */
    public void setValidTime(TimePeriodPropertyType value) {
        this.validTime = value;
    }

    /**
     * Gets the value of the procedure property.
     * 
     * @return
     *     possible object is
     *     {@link OMProcessPropertyType }
     *     
     */
    public OMProcessPropertyType getProcedure() {
        return procedure;
    }

    /**
     * Sets the value of the procedure property.
     * 
     * @param value
     *     allowed object is
     *     {@link OMProcessPropertyType }
     *     
     */
    public void setProcedure(OMProcessPropertyType value) {
        this.procedure = value;
    }

    /**
     * Gets the value of the parameter property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link NamedValuePropertyType }
     * 
     * 
     */
    public List<NamedValuePropertyType> getParameter() {
        if (parameter == null) {
            parameter = new ArrayList<NamedValuePropertyType>();
        }
        return this.parameter;
    }

    /**
     * Gets the value of the observedProperty property.
     * 
     * @return
     *     possible object is
     *     {@link ReferenceType }
     *     
     */
    public ReferenceType getObservedProperty() {
        return observedProperty;
    }

    /**
     * Sets the value of the observedProperty property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReferenceType }
     *     
     */
    public void setObservedProperty(ReferenceType value) {
        this.observedProperty = value;
    }

    /**
     * Gets the value of the featureOfInterest property.
     * 
     * @return
     *     possible object is
     *     {@link FeaturePropertyType }
     *     
     */
    public FeaturePropertyType getFeatureOfInterest() {
        return featureOfInterest;
    }

    /**
     * Sets the value of the featureOfInterest property.
     * 
     * @param value
     *     allowed object is
     *     {@link FeaturePropertyType }
     *     
     */
    public void setFeatureOfInterest(FeaturePropertyType value) {
        this.featureOfInterest = value;
    }

    /**
     * Gets the value of the resultQuality property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link DQElementPropertyType }
     * 
     */
    public List<Element> getResultQuality() {
        if (resultQuality == null) {
            resultQuality = new ArrayList<Element>();
        }
        return this.resultQuality;
    }

    /**
     * Gets the value of the result property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getResult() {
        return result;
    }

    /**
     * Sets the value of the result property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setResult(Object value) {
        this.result = value;
    }

}
