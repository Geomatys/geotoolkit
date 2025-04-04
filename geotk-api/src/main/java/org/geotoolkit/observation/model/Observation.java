/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2022, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.observation.model;

import java.time.Instant;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.apache.sis.temporal.TemporalObjects;
import org.geotoolkit.temporal.object.TemporalUtilities;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.Metadata;
import org.opengis.metadata.quality.Element;
import org.opengis.temporal.Period;
import org.opengis.temporal.TemporalPrimitive;

/**
 * TODO : remove the opengis implements and add the AbstractOMEntity extends
 *
 * @author Guilhem Legal (Geomatys)
 */
public class Observation implements org.opengis.observation.Observation {

    private String id;
    private String name;
    private String description;
    private String definition;

    private Procedure procedure;
    private TemporalPrimitive samplingTime;
    private SamplingFeature featureOfInterest;
    private Phenomenon observedProperty;
    private List<Element> quality = new ArrayList<>();

    private Result result;

    private String type;

    private Envelope bounds;

    private Map<String, Object> properties = new HashMap<>();

    private Map<String, Object> parameters = new HashMap<>();

    private Observation() {}

    public Observation(String id, String name, String description, String definition, String type,
            Procedure procedure, TemporalPrimitive samplingTime, SamplingFeature featureOfInterest,
            Phenomenon observedProperty, List<Element> quality, Result result, Map<String, Object> properties,
            Map<String, Object> parameters) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.definition = definition;
        if (type == null) {
            this.type = "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_ComplexObservation";
        } else {
            this.type = type;
        }
        this.procedure = procedure;
        this.samplingTime = samplingTime;
        this.featureOfInterest = featureOfInterest;
        this.observedProperty = observedProperty;
        this.quality = quality;
        if (quality == null) {
            this.quality = new ArrayList<>();
        } else {
            this.quality = quality;
        }
        this.result = result;
        if (properties == null) {
            this.properties = new HashMap<>();
        } else {
            this.properties = properties;
        }
        if (parameters == null) {
            this.parameters = new HashMap<>();
        } else {
            this.parameters = parameters;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public Identifier getName() {
        if (name != null) {
            return new DefaultIdentifier(name);
        }
        return null;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public Procedure getProcedure() {
        return procedure;
    }

    public void setProcedure(Procedure procedure) {
        this.procedure = procedure;
    }

    @Override
    public TemporalPrimitive getSamplingTime() {
        return samplingTime;
    }

    public void setSamplingTime(TemporalPrimitive samplingTime) {
        this.samplingTime = samplingTime;
    }

    @Override
    public SamplingFeature getFeatureOfInterest() {
        return featureOfInterest;
    }

    public void setFeatureOfInterest(SamplingFeature featureOfInterest) {
        this.featureOfInterest = featureOfInterest;
    }

    @Override
    public Phenomenon getObservedProperty() {
        return observedProperty;
    }

    public void setObservedProperty(Phenomenon observedProperty) {
        this.observedProperty = observedProperty;
    }

    public List<Element> getResultQuality() {
        return quality;
    }

    public void setResultQuality(List<Element> quality) {
        this.quality = quality;
    }

    @Override
    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Envelope getBounds() {
        return bounds;
    }

    public void setBounds(Envelope bounds) {
        this.bounds = bounds;
    }

    @Deprecated
    public void extendSamplingTime(final Date newDate) {
        extendSamplingTime((newDate != null) ? newDate.toInstant() : null);
    }

    /**
     * Extend the current observation time span by adding a new date.
     * If the new date is before or after the current sampling time, the period will be expanded.
     * If no time is currently set, a time instant with the supplied date will be set as the new time span.
     *
     * @param newDate a date to integrate into the time span of the offering.
     */
    public void extendSamplingTime(final Temporal newDate) {
        if (newDate != null) {
            final Instant newInstant = TemporalUtilities.toInstant(newDate);
            if (samplingTime instanceof Period p) {
                Temporal currentStDate = p.getBeginning().getPosition();
                Temporal currentEnDate = p.getEnding().getPosition();
                if (newInstant.isBefore(TemporalUtilities.toInstant(currentStDate))) {
                    samplingTime = TemporalObjects.createPeriod(newDate, currentEnDate);
                    addIdentifiers((Period) samplingTime);
                } else if (newInstant.isAfter(TemporalUtilities.toInstant(currentEnDate))) {
                    samplingTime = TemporalObjects.createPeriod(currentStDate, newDate);
                    addIdentifiers((Period) samplingTime);
                }
                // date is within to the current period so no changes are applied
            } else if (samplingTime instanceof org.opengis.temporal.Instant i) {
                Temporal currentDate = i.getPosition();
                Instant currentInstant = TemporalUtilities.toInstant(currentDate);
                if (newInstant.isBefore(currentInstant)) {
                    samplingTime = TemporalObjects.createPeriod(newDate, currentDate);
                    addIdentifiers((Period) samplingTime);
                } else if (newInstant.isAfter(currentInstant)) {
                    samplingTime = TemporalObjects.createPeriod(currentDate, newDate);
                    addIdentifiers((Period) samplingTime);
                }
                // date is equals to the current date so no changes are applied
            } else if (samplingTime == null) {
                samplingTime = TemporalObjects.createInstant(newDate);
                addIdentifier((org.opengis.temporal.Instant) samplingTime);
            } else {
                throw new IllegalStateException("Unknown time implementeation: " + samplingTime.getClass().getName());
            }
        }
    }

    protected final void addIdentifier(org.opengis.temporal.Instant i) {
        ObservationUtils.setIdentifier(i, getId() + "-time");
    }

    protected final void addIdentifiers(Period p) {
        ObservationUtils.setIdentifiers(p, getId());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[").append(this.getClass().getName()).append("]");
        sb.append("id=").append(id).append('\n');
        sb.append("name=").append(name).append('\n');
        sb.append("type=").append(type).append('\n');
        sb.append("description=").append(description).append('\n');
        sb.append("definition=").append(definition).append('\n');
        sb.append("bounds=").append(bounds).append('\n');
        sb.append("samplingTime=").append(samplingTime).append('\n');
        sb.append("featureOfinterest=").append(featureOfInterest).append('\n');
        sb.append("observedProperty=").append(observedProperty).append('\n');
        sb.append("procedure=").append(procedure).append('\n');
        sb.append("quality=").append(quality).append('\n');
        sb.append("result=").append(result).append('\n');
        sb.append("properties=[\n");
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            sb.append(" - ").append(entry.getKey()).append( "=> ").append(entry.getValue()).append('\n');
        }
        sb.append("]\n");
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Observation that) {
            return Objects.equals(this.id,                that.id) &&
                   Objects.equals(this.name,              that.name) &&
                   Objects.equals(this.bounds,            that.bounds) &&
                   Objects.equals(this.type,              that.type) &&
                   Objects.equals(this.description,       that.description) &&
                   Objects.equals(this.definition,        that.definition) &&
                   Objects.equals(this.featureOfInterest, that.featureOfInterest) &&
                   Objects.equals(this.observedProperty,  that.observedProperty) &&
                   Objects.equals(this.procedure,         that.procedure) &&
                   Objects.equals(this.result,            that.result) &&
                   Objects.equals(this.quality,           that.quality) &&
                   Objects.equals(this.samplingTime,      that.samplingTime) &&
                   Objects.equals(this.parameters,        that.parameters) &&
                   Objects.equals(this.properties,        that.properties);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.id);
        hash = 89 * hash + Objects.hashCode(this.name);
        hash = 89 * hash + Objects.hashCode(this.type);
        hash = 89 * hash + Objects.hashCode(this.bounds);
        hash = 89 * hash + Objects.hashCode(this.description);
        hash = 89 * hash + Objects.hashCode(this.definition);
        hash = 89 * hash + Objects.hashCode(this.procedure);
        hash = 89 * hash + Objects.hashCode(this.samplingTime);
        hash = 89 * hash + Objects.hashCode(this.featureOfInterest);
        hash = 89 * hash + Objects.hashCode(this.observedProperty);
        hash = 89 * hash + Objects.hashCode(this.quality);
        hash = 89 * hash + Objects.hashCode(this.result);
        hash = 89 * hash + Objects.hashCode(this.properties);
        hash = 89 * hash + Objects.hashCode(this.parameters);
        return hash;
    }

    @Override
    public Object getProcedureParameter() {
        return null;
    }

    @Override
    public TemporalPrimitive getProcedureTime() {
        return null;
    }

    @Override
    public Metadata getObservationMetadata() {
        return null;
    }

    @Override
    public Element getQuality() {
        return null;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
}
