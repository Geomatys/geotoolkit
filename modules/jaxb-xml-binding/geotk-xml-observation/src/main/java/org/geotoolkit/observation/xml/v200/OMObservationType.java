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
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.gml.xml.v321.AbstractFeatureType;
import org.geotoolkit.gml.xml.v321.FeaturePropertyType;
import org.geotoolkit.gml.xml.v321.ReferenceType;
import org.geotoolkit.gml.xml.v321.TimeInstantPropertyType;
import org.geotoolkit.gml.xml.v321.TimePeriodPropertyType;
import org.geotoolkit.internal.jaxb.metadata.DQ_Element;
import org.geotoolkit.internal.jaxb.metadata.direct.MetadataAdapter;
import org.geotoolkit.observation.xml.AbstractObservation;
import org.geotoolkit.sampling.xml.v200.SFSamplingFeatureType;
import org.opengis.metadata.Metadata;
import org.opengis.metadata.quality.Element;
import org.opengis.observation.Observation;
import org.opengis.observation.Phenomenon;
import org.opengis.observation.sampling.SamplingFeature;
import org.opengis.temporal.TemporalGeometricPrimitive;
import org.opengis.temporal.TemporalObject;


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
public class OMObservationType extends AbstractFeatureType implements AbstractObservation {

    private ReferenceType type;
    @XmlJavaTypeAdapter(MetadataAdapter.class)
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
    @XmlJavaTypeAdapter(DQ_Element.class)
    private List<Element> resultQuality;
    @XmlElement(required = true)
    private Object result;

    
    @Override
    public String getDefinition() {
        return null; // not defined in SOS 2.0.0
    }
    
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
    @Override
    public Metadata getObservationMetadata() {
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
    public void setObservationMetadata(Metadata value) {
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
    
    @Override
    public TemporalObject getSamplingTime() {
        if (phenomenonTime != null) {
            return phenomenonTime.getTimeObject();
        }
        return null;
    }

    @Override
    public TemporalObject getProcedureTime() {
        throw new UnsupportedOperationException("Not supported yet.");
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
    @Override
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
    public void setProcedure(final OMProcessPropertyType value) {
        this.procedure = value;
    }
    
    @Override
    public void setProcedure(final String procedureID) {
        if (procedureID != null) {
            this.procedure = new OMProcessPropertyType(procedureID);
        }
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

    @Override
    public Object getProcedureParameter() {
        if (parameter != null && !parameter.isEmpty()) {
            return parameter.get(0);
        }
        return null;
    }
    
    /**
     * Gets the value of the observedProperty property.
     * 
     * @return
     *     possible object is
     *     {@link ReferenceType }
     *     
     */
    public ReferenceType getObservedPropertyRef() {
        return observedProperty;
    }
    
    @Override
    public Phenomenon getObservedProperty() {
        throw new UnsupportedOperationException("there is no full Phenonon in O&M v2.0.0");
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
    public FeaturePropertyType getFeatureOfInterestProperty() {
        return featureOfInterest;
    }
    
    @Override
    public SamplingFeature getFeatureOfInterest() {
         if (featureOfInterest != null) {
            if (featureOfInterest.getAbstractFeature() instanceof SamplingFeature) {
                return (SFSamplingFeatureType)featureOfInterest.getAbstractFeature();
            } else {
                LOGGER.warning("information lost getFeatureOfInterest() is deprecated use getPropertyFeatureOfInterest() instead");
            }
        }
        return null;
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

    @Override
    public Element getQuality() {
        if (resultQuality != null && !resultQuality.isEmpty()) {
            return resultQuality.get(0);
        }
        return null;
    }
    /**
     * Gets the value of the result property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    @Override
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

    @Override
    public boolean matchTemplate(final Observation abstractTemplate) {
        return true;
    }

    @Override
    public Observation getTemporaryTemplate(final String temporaryName, final TemporalGeometricPrimitive time) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
