/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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
package org.geotoolkit.sts.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class MultiDatastream extends Datastream implements STSResponse {

    @JsonProperty("ObservedProperty")
    private List<ObservedProperty> observedProperties = null;

    @JsonProperty("multiObservationDataTypes")
    protected List<String> multiObservationDataTypes = null;

    public MultiDatastream ObservedProperties(List<ObservedProperty> observedProperties) {
        this.observedProperties = observedProperties;
        return this;
    }

    public MultiDatastream addObservedPropertiesItem(ObservedProperty observedPropertyItem) {
        if (this.observedProperties == null) {
            this.observedProperties = new ArrayList<>();
        }

        this.observedProperties.add(observedPropertyItem);
        return this;
    }

    public List<ObservedProperty> getObservedProperties() {
        return observedProperties;
    }

    public void setObservedProperties(List<ObservedProperty> observedProperties) {
        this.observedProperties = observedProperties;
    }

    public List<String> getMultiObservationDataTypes() {
        return multiObservationDataTypes;
    }

    public void setMultiObservationDataTypes(List<String> multiObservationDataTypes) {
        this.multiObservationDataTypes = multiObservationDataTypes;
    }

    public MultiDatastream MultiObservationDataTypes(List<String> multiObservationDataTypes) {
        this.multiObservationDataTypes = multiObservationDataTypes;
        return this;
    }

    public MultiDatastream addMultiObservationDataTypesItem(String multiObservationDataType) {
        if (this.multiObservationDataTypes == null) {
            this.multiObservationDataTypes = new ArrayList<>();
        }

        this.multiObservationDataTypes.add(multiObservationDataType);
        return this;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MultiDatastream datastream = (MultiDatastream) o;
        return super.equals(o)
                && Objects.equals(this.multiObservationDataTypes, datastream.multiObservationDataTypes)
                && Objects.equals(this.observedProperties, datastream.observedProperties);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(super.hashCode(), observedProperties, multiObservationDataTypes);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class MultiDatastream {\n");

        sb.append("    iotId: ").append(toIndentedString(super.getIotId())).append("\n");
        sb.append("    iotSelfLink: ").append(toIndentedString(super.getIotSelfLink())).append("\n");
        sb.append("    description: ").append(toIndentedString(super.getDescription())).append("\n");
        sb.append("    unitOfMeasurement: ").append(toIndentedString(super.getUnitOfMeasurement())).append("\n");
        sb.append("    observationType: ").append(toIndentedString(super.getObservationType())).append("\n");
        sb.append("    observedArea: ").append(toIndentedString(super.getObservedArea())).append("\n");
        sb.append("    phenomenonTime: ").append(toIndentedString(super.getPhenomenonTime())).append("\n");
        sb.append("    resultTime: ").append(toIndentedString(super.getResultTime())).append("\n");
        sb.append("    thing: ").append(toIndentedString(super.getThing())).append("\n");
        sb.append("    sensor: ").append(toIndentedString(super.getSensor())).append("\n");
        sb.append("    observedProperty: ").append(toIndentedString(super.getObservedProperty())).append("\n");
        sb.append("    observations: ").append(toIndentedString(observations)).append("\n");
        sb.append("    thingIotNavigationLink: ").append(toIndentedString(super.getThingIotNavigationLink())).append("\n");
        sb.append("    sensorIotNavigationLink: ").append(toIndentedString(super.getSensorIotNavigationLink())).append("\n");
        sb.append("    observedPropertyIotNavigationLink: ").append(toIndentedString(super.getObservedPropertyIotNavigationLink())).append("\n");
        sb.append("    observationsIotNavigationLink: ").append(toIndentedString(super.getObservationsIotNavigationLink())).append("\n");
        sb.append("    observedProperties: ").append(toIndentedString(observedProperties)).append("\n");
        sb.append("    multiObservationDataTypes: ").append(toIndentedString(multiObservationDataTypes)).append("\n");
        sb.append("}");
        return sb.toString();
    }

}
