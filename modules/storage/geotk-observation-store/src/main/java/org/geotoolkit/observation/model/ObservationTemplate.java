/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
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

import java.util.List;
import org.geotoolkit.swe.xml.PhenomenonProperty;
import org.opengis.observation.Observation;
import org.geotoolkit.observation.xml.Process;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class ObservationTemplate {

    private Process procedure;
    private List<PhenomenonProperty> observedProperties;
    private String featureOfInterest;
    private Observation observation;

    public ObservationTemplate(Process procedure, List<PhenomenonProperty> observedProperties, String featureOfInterest, Observation observation) {
        this.featureOfInterest = featureOfInterest;
        this.observedProperties = observedProperties;
        this.observation = observation;
        this.procedure = procedure;
    }

    /**
     * @return the procedure
     */
    public Process getProcedure() {
        return procedure;
    }

    /**
     * @param procedure the procedure to set
     */
    public void setProcedure(Process procedure) {
        this.procedure = procedure;
    }

    /**
     * @return the observableProperty
     */
    public List<PhenomenonProperty> getObservedProperties() {
        return observedProperties;
    }

    /**
     * @param observedProperties the observableProperty to set
     */
    public void setObservedProperty(List<PhenomenonProperty> observedProperties) {
        this.observedProperties = observedProperties;
    }

    /**
     * @return the featureOfInterest
     */
    public String getFeatureOfInterest() {
        return featureOfInterest;
    }

    /**
     * @param featureOfInterest the featureOfInterest to set
     */
    public void setFeatureOfInterest(String featureOfInterest) {
        this.featureOfInterest = featureOfInterest;
    }

    /**
     * @return the observation
     */
    public Observation getObservation() {
        return observation;
    }

    /**
     * @param observation the observation to set
     */
    public void setObservation(Observation observation) {
        this.observation = observation;
    }



}
