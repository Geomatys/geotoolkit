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
import java.util.Objects;
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
import org.apache.sis.internal.jaxb.metadata.DQ_Element;
import org.apache.sis.internal.jaxb.metadata.MD_Metadata;
import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.geotoolkit.observation.xml.AbstractObservation;
import org.geotoolkit.sampling.xml.v200.SFSamplingFeatureType;
import org.geotoolkit.swe.xml.v200.DataArrayPropertyType;
import org.apache.sis.util.ComparisonMode;
import org.geotoolkit.gml.xml.v321.TimeInstantType;
import org.geotoolkit.gml.xml.v321.TimePositionType;
import org.geotoolkit.swe.xml.AnyScalar;
import org.geotoolkit.swe.xml.DataArray;
import org.geotoolkit.swe.xml.DataArrayProperty;
import org.geotoolkit.swe.xml.DataComponentProperty;
import org.geotoolkit.swe.xml.DataRecord;
import org.geotoolkit.swe.xml.PhenomenonProperty;
import org.geotoolkit.swe.xml.SimpleDataRecord;
import org.opengis.metadata.Identifier;
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
@XmlSeeAlso({OMObservationType.InternalPhenomenon.class, OMObservationType.InternalCompositePhenomenon.class})
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

    public OMObservationType(final String id, final String name, final String type, final AbstractTimeObjectType phenomenonTime,
            final String procedure, final String observedProperty, final FeaturePropertyType foi, final Object result) {
        super(id, name, null);
        this.type = new ReferenceType(type);
        this.phenomenonTime = new TimeObjectPropertyType(phenomenonTime);
        this.resultTime     = new TimeInstantPropertyType();

        if (procedure != null) {
            this.procedure      = new OMProcessPropertyType(procedure);
        }
        this.observedProperty    = new ReferenceType(observedProperty);
        this.featureOfInterest   = foi;
        this.result = result;
    }

    /**
     * Build a clone of an observation
     * 
     * @param observation observation to clone.
     */
    public OMObservationType(final OMObservationType observation) {
        super(observation);
        this.type                = observation.type;
        this.metadata            = observation.metadata;
        if (observation.relatedObservation != null) {
            this.relatedObservation  = new ArrayList<>(observation.relatedObservation);
        }
        this.phenomenonTime      = observation.phenomenonTime;
        this.validTime           = observation.validTime;
        this.resultTime          = observation.resultTime;
        this.procedure           = observation.procedure;
        if (observation.parameter != null) {
            this.parameter       = new ArrayList<>(observation.parameter);
        }
        this.observedProperty    = observation.observedProperty;
        this.featureOfInterest   = observation.featureOfInterest;
        if (observation.resultQuality != null) {
            this.resultQuality   = new ArrayList<>(observation.resultQuality);
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
            relatedObservation = new ArrayList<>();
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
    public void extendSamplingTime(final String newEndBound) {
        if (newEndBound != null) {
            if (phenomenonTime != null && phenomenonTime.getTimeObject() instanceof TimePeriodType) {
                ((TimePeriodType)phenomenonTime.getTimeObject()).setEndPosition(new TimePositionType(newEndBound));
            } else if (phenomenonTime != null && phenomenonTime.getTimeObject() instanceof TimeInstantType) {
                final TimeInstantType instant = (TimeInstantType) phenomenonTime.getTimeObject();
                if (!newEndBound.equals(instant.getTimePosition().getValue())) {
                    final TimePeriodType period = new TimePeriodType(instant.getId(), instant.getTimePosition().getValue(), newEndBound);
                    phenomenonTime.setTimeObject(period);
                }
            }
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
            parameter = new ArrayList<>();
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
            if (result instanceof DataArrayProperty) {
                final List<String> fields = getFieldsFromResult((DataArrayProperty) result);
                final List<InternalPhenomenon> phenomenons = new ArrayList<>();
                for (String field : fields) {
                    phenomenons.add(new InternalPhenomenon(field));
                }
                return new InternalCompositePhenomenon(observedProperty.getHref(), phenomenons);
            }
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

    @Override
    public PhenomenonProperty getPropertyObservedProperty() {
        if (observedProperty != null) {
            final Phenomenon phen = getObservedProperty();
            return new InternalPhenomenonProperty(observedProperty, (org.geotoolkit.swe.xml.Phenomenon) phen);
        }
        return null;
    }

    
    /**
     * Gets the value of the featureOfInterest property.
     *
     * @return
     *     possible object is
     *     {@link FeaturePropertyType }
     *
     */
    @Override
    public FeaturePropertyType getPropertyFeatureOfInterest() {
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

    private List<String> getFieldsFromResult(final DataArrayProperty arrayProp) {
        final List<String> fields = new ArrayList<>();
        if (arrayProp.getDataArray() != null) {
            final DataArray array = arrayProp.getDataArray();
            if (array.getPropertyElementType().getAbstractRecord() instanceof DataRecord) {
                final DataRecord record = (DataRecord)array.getPropertyElementType().getAbstractRecord();
                for (DataComponentProperty field : record.getField()) {
                    fields.add(field.getName());
                }

            } else if (array.getPropertyElementType().getAbstractRecord() instanceof SimpleDataRecord) {
                final SimpleDataRecord record = (SimpleDataRecord)array.getPropertyElementType().getAbstractRecord();
                for (AnyScalar field : record.getField()) {
                    fields.add(field.getName());
                }
            }
        }
        return fields;
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
            return Objects.equals(this.featureOfInterest,  that.featureOfInterest)   &&
                   Objects.equals(this.metadata,           that.metadata)   &&
                   Objects.equals(this.observedProperty,   that.observedProperty)   &&
                   Objects.equals(this.parameter,          that.parameter)   &&
                   Objects.equals(this.phenomenonTime,     that.phenomenonTime)   &&
                   Objects.equals(this.procedure,          that.procedure)   &&
                   Objects.equals(this.relatedObservation, that.relatedObservation)   &&
                   Objects.equals(this.result,             that.result)   &&
                   Objects.equals(this.resultQuality,      that.resultQuality)   &&
                   Objects.equals(this.resultTime,         that.resultTime)   &&
                   Objects.equals(this.type,               that.type)   &&
                   Objects.equals(this.validTime,          that.validTime);
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
        public Identifier getName() {
            return new DefaultIdentifier(name);
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof Phenomenon) {
                final org.geotoolkit.swe.xml.Phenomenon that = (org.geotoolkit.swe.xml.Phenomenon) obj;
                return Objects.equals(this.getName(), that.getName());
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
    
    @XmlRootElement
    public static class InternalCompositePhenomenon implements org.geotoolkit.swe.xml.CompositePhenomenon {

        private final String name;
        
        private final List<InternalPhenomenon> phenomenons;

        public InternalCompositePhenomenon() {
            this.name = null;
            this.phenomenons = new ArrayList<>();
        }

        public InternalCompositePhenomenon(final String name, List<InternalPhenomenon> phenomenons) {
            this.name = name;
            this.phenomenons = phenomenons;
        }

        @Override
        public Identifier getName() {
            return new DefaultIdentifier(name);
        }
        
        @Override
        public List<? extends PhenomenonProperty> getRealComponent() {
            final List<InternalPhenomenonProperty> phenProps = new ArrayList<>();
            for (InternalPhenomenon phen : phenomenons) {
                phenProps.add(new InternalPhenomenonProperty(new ReferenceType(phen.name), phen));
            }
            return phenProps;
        }

        @Override
        public List<? extends Phenomenon> getComponent() {
            return phenomenons;
        }

        @Override
        public Phenomenon getBase() {
            return null;
        }

        @Override
        public int getDimension() {
            return phenomenons.size();
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof Phenomenon) {
                final org.geotoolkit.swe.xml.Phenomenon that = (org.geotoolkit.swe.xml.Phenomenon) obj;
                return Objects.equals(this.getName(), that.getName());
            }
            return false;
        }

        @Override
        public String toString() {
            return "[Anonymous composite Phenomenon] name:" + getName();
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 7 * hash + (this.getName() != null ? this.getName().hashCode() : 0);
            return hash;
        }
    }
    
    @XmlRootElement
    public static class InternalPhenomenonProperty implements org.geotoolkit.swe.xml.PhenomenonProperty {

        private final String href;
        
        private final org.geotoolkit.swe.xml.Phenomenon phenomenon;

        public InternalPhenomenonProperty() {
            this.href = null;
            this.phenomenon = null;
        }

        public InternalPhenomenonProperty(final ReferenceType phenRef, final org.geotoolkit.swe.xml.Phenomenon phenomenon) {
            this.href = phenRef.getHref();
            this.phenomenon = phenomenon;
        }

        @Override
        public String getHref() {
            return href;
        }
        
        @Override
        public void setToHref() {
            // already in href mode
        }

        @Override
        public org.geotoolkit.swe.xml.Phenomenon getPhenomenon() {
            return phenomenon;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof org.geotoolkit.swe.xml.PhenomenonProperty) {
                final org.geotoolkit.swe.xml.PhenomenonProperty that = (org.geotoolkit.swe.xml.PhenomenonProperty) obj;
                return Objects.equals(this.href, that.getHref());
            }
            return false;
        }

        @Override
        public String toString() {
            return "[Anonymous Phenomenon Property] href:" + getHref();
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 7 * hash + (this.getHref() != null ? this.getHref().hashCode() : 0);
            return hash;
        }

    }
}
