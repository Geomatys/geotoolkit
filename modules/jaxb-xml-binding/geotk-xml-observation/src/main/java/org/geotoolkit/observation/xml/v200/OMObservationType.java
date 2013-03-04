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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.gml.xml.v321.AbstractFeatureType;
import org.geotoolkit.gml.xml.v321.AbstractTimeObjectType;
import org.geotoolkit.gml.xml.v321.FeaturePropertyType;
import org.geotoolkit.gml.xml.v321.ReferenceType;
import org.geotoolkit.gml.xml.v321.TimeInstantPropertyType;
import org.geotoolkit.gml.xml.v321.TimePeriodPropertyType;
import org.geotoolkit.gml.xml.v321.TimePeriodType;
import org.geotoolkit.internal.jaxb.metadata.DQ_Element;
import org.geotoolkit.internal.jaxb.metadata.MD_Metadata;
import org.geotoolkit.observation.xml.AbstractObservation;
import org.geotoolkit.observation.xml.v200.OMObservationType.InternalPhenomenon;
import org.geotoolkit.sampling.xml.v200.SFSamplingFeatureType;
import org.geotoolkit.swe.xml.v200.DataArrayPropertyType;
import org.apache.sis.util.ComparisonMode;
import org.geotoolkit.util.Utilities;
import org.opengis.metadata.Metadata;
import org.opengis.metadata.quality.Element;
import org.opengis.observation.Observation;
import org.opengis.observation.Phenomenon;
import org.opengis.observation.sampling.SamplingFeature;
import org.opengis.temporal.Period;
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
@XmlSeeAlso(InternalPhenomenon.class)
@XmlRootElement(name = "Observation")
public class OMObservationType extends AbstractFeatureType implements AbstractObservation {

    private ReferenceType type;
    @XmlJavaTypeAdapter(MD_Metadata.class)
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

    
    public OMObservationType() {
    }
    
    public OMObservationType(final String name, final String type, final AbstractTimeObjectType phenomenonTime, 
            final String procedure, final String observedProperty, final FeaturePropertyType foi, final Object result) {
        super(null, name, null);
        this.type = new ReferenceType(type);
        if (phenomenonTime != null) {
            this.phenomenonTime = new TimeObjectPropertyType(phenomenonTime);
        }
        if (procedure != null) {
            this.procedure      = new OMProcessPropertyType(procedure);
        }
        this.observedProperty    = new ReferenceType(observedProperty);
        this.featureOfInterest   = foi;
        this.result = result;
    }
    
    /**
     * Build a clone of an observation
     */
    public OMObservationType(final OMObservationType observation) {
        super(observation);
        this.type                = observation.type;
        this.metadata            = observation.metadata;
        if (observation.relatedObservation != null) {
            this.relatedObservation  = new ArrayList<ObservationContextPropertyType>(observation.relatedObservation);
        }
        this.phenomenonTime      = observation.phenomenonTime;
        this.validTime           = observation.validTime;
        this.resultTime          = observation.resultTime;
        this.procedure           = observation.procedure;
        if (observation.parameter != null) {
            this.parameter       = new ArrayList<NamedValuePropertyType>(observation.parameter);
        }
        this.observedProperty    = observation.observedProperty;
        this.featureOfInterest   = observation.featureOfInterest;
        if (observation.resultQuality != null) {
            this.resultQuality   = new ArrayList<Element>(observation.resultQuality);
        }
        if (observation.result instanceof DataArrayPropertyType) {
            this.result = new DataArrayPropertyType((DataArrayPropertyType)observation.result);
        } else {
            this.result = observation.result;
        }
    }
    
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
    
    public void setPhenomenonTime(AbstractTimeObjectType value) {
        if (value != null) {
            this.phenomenonTime = new TimeObjectPropertyType(value);
        }
    }
    
    @Override
    public TemporalObject getSamplingTime() {
        if (phenomenonTime != null) {
            return phenomenonTime.getTimeObject();
        }
        return null;
    }

    @Override
    public void emptySamplingTime() {
        this.phenomenonTime = null;
    }
    @Override
    public void setSamplingTimePeriod(final Period period) {
        if (period instanceof TimePeriodType) {
            this.phenomenonTime = new TimeObjectPropertyType((TimePeriodType)period);
        } else if (period != null) {
            final TimePeriodType pt = new TimePeriodType(period.getBeginning().getPosition(), period.getEnding().getPosition());
            this.phenomenonTime = new TimeObjectPropertyType(pt);
        }
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
        if (observedProperty != null) {
            return new InternalPhenomenon(observedProperty.getHref());
        }
        return null;
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
    @Override
    public void setResult(final Object value) {
        this.result = value;
    }

    @Override
    public boolean matchTemplate(final Observation abstractTemplate) {
        return true;
    }

    @Override
    public OMObservationType getTemporaryTemplate(final String temporaryName, final TemporalGeometricPrimitive time) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        if (featureOfInterest != null) {
            sb.append("featureOfInterest:").append(featureOfInterest).append('\n');
        }
        if (metadata != null) {
            sb.append("metadata:").append(metadata).append('\n');
        }
        if (observedProperty != null) {
            sb.append("observedProperty:").append(observedProperty).append('\n');
        }
        if (parameter != null) {
            sb.append("parameter:\n");
            for (NamedValuePropertyType p: parameter) {
                sb.append(p).append('\n');
            }
        }
        if (phenomenonTime != null) {
            sb.append("phenomenonTime:").append(phenomenonTime).append('\n');
        }
        if (procedure != null) {
            sb.append("procedure:").append(procedure).append('\n');
        }
        if (relatedObservation != null) {
            sb.append("relatedObservation:\n");
            for (ObservationContextPropertyType p: relatedObservation) {
                sb.append(p).append('\n');
            }
        }
        if (result != null) {
            sb.append("result:").append(result).append('\n');
        }
        if (resultQuality != null) {
            sb.append("resultQuality:\n");
            for (Element p: resultQuality) {
                sb.append(p).append('\n');
            }
        }
        if (resultTime != null) {
            sb.append("resultTime:").append(resultTime).append('\n');
        }
        if (type != null) {
            sb.append("type:").append(type).append('\n');
        }
        if (validTime != null) {
            sb.append("validTime:").append(validTime).append('\n');
        }
        return sb.toString();
    }
    
    /**
     * Vérifie que cette station est identique à l'objet spécifié
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true;
        }

        if (object instanceof OMObservationType && super.equals(object, mode)) {
            final OMObservationType that = (OMObservationType) object;
            return Utilities.equals(this.featureOfInterest,  that.featureOfInterest)   &&
                   Utilities.equals(this.metadata,           that.metadata)   &&
                   Utilities.equals(this.observedProperty,   that.observedProperty)   &&
                   Utilities.equals(this.parameter,          that.parameter)   &&
                   Utilities.equals(this.phenomenonTime,     that.phenomenonTime)   &&
                   Utilities.equals(this.procedure,          that.procedure)   &&
                   Utilities.equals(this.relatedObservation, that.relatedObservation)   &&
                   Utilities.equals(this.result,             that.result)   &&
                   Utilities.equals(this.resultQuality,      that.resultQuality)   &&
                   Utilities.equals(this.resultTime,         that.resultTime)   &&
                   Utilities.equals(this.type,               that.type)   &&
                   Utilities.equals(this.validTime,          that.validTime);
        } 
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + super.hashCode();
        hash = 37 * hash + (this.featureOfInterest != null ? this.featureOfInterest.hashCode() : 0);
        hash = 37 * hash + (this.metadata != null ? this.metadata.hashCode() : 0);
        hash = 37 * hash + (this.observedProperty != null ? this.observedProperty.hashCode() : 0);
        hash = 37 * hash + (this.parameter != null ? this.parameter.hashCode() : 0);
        hash = 37 * hash + (this.phenomenonTime != null ? this.phenomenonTime.hashCode() : 0);
        hash = 37 * hash + (this.procedure != null ? this.procedure.hashCode() : 0);
        hash = 37 * hash + (this.relatedObservation != null ? this.relatedObservation.hashCode() : 0);
        hash = 37 * hash + (this.result != null ? this.result.hashCode() : 0);
        hash = 37 * hash + (this.resultQuality != null ? this.resultQuality.hashCode() : 0);
        hash = 37 * hash + (this.resultTime != null ? this.resultTime.hashCode() : 0);
        hash = 37 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 37 * hash + (this.validTime != null ? this.validTime.hashCode() : 0);
        return hash;
    }

    @XmlRootElement
    public static class InternalPhenomenon implements org.geotoolkit.swe.xml.Phenomenon {
    
        private final String name;
        
        public InternalPhenomenon() {
            this.name = null;
        }
        
        public InternalPhenomenon(final String name) {
            this.name = name;
        }
        
        @Override
        public String getName() {
            return name;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof Phenomenon) {
                final org.geotoolkit.swe.xml.Phenomenon that = (org.geotoolkit.swe.xml.Phenomenon) obj;
                return Utilities.equals(this.getName(), that.getName());
            }
            return false;
        }

        @Override
        public String toString() {
            return "[Anonymous Phenomenon] name:" + getName();
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 7 * hash + (this.getName() != null ? this.getName().hashCode() : 0);
            return hash;
        }
    }
}
