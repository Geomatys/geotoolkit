/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2022 Geomatys
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

package org.geotoolkit.observation.model;

import java.time.Instant;
import java.time.temporal.Temporal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.sis.temporal.TemporalObjects;
import org.geotoolkit.temporal.object.TemporalUtilities;
import org.opengis.geometry.Envelope;
import org.opengis.temporal.Period;
import org.opengis.temporal.TemporalPrimitive;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class Offering extends AbstractOMEntity {

    private TemporalPrimitive time;

    private Envelope bounds;

    private List<String> srsNames;

    private String procedure;

    private List<String> featureOfInterestIds;

    private List<String> observedProperties;

    // for JSON
    private Offering() {}

    public Offering(String id, String name, String description, Map<String, Object> properties, Envelope bounds, List<String> srsNames,
            TemporalPrimitive time, String procedure, List<String> observedProperties, List<String> featureOfInterestIds) {
        super(id, name, description, properties);
        this.bounds = bounds;
        this.srsNames = srsNames;
        this.time = time;
        this.procedure = procedure;
        this.observedProperties = observedProperties;
        this.featureOfInterestIds = featureOfInterestIds;
    }

    public TemporalPrimitive getTime() {
        return time;
    }

    public void setTime(TemporalPrimitive time) {
        this.time = time;
    }

    public Envelope getBounds() {
        return bounds;
    }

    public void setBounds(Envelope bounds) {
        this.bounds = bounds;
    }

    public String getProcedure() {
        return procedure;
    }

    public void setProcedure(String procedure) {
        this.procedure = procedure;
    }

    public List<String> getFeatureOfInterestIds() {
        return featureOfInterestIds;
    }

    public void setFeatureOfInterestIds(List<String> featureOfInterestIds) {
        this.featureOfInterestIds = featureOfInterestIds;
    }

    public List<String> getObservedProperties() {
        return observedProperties;
    }

    public void setObservedProperties(List<String> observedProperties) {
        this.observedProperties = observedProperties;
    }

    public List<String> getSrsNames() {
        return srsNames;
    }

    public void setSrsNames(List<String> srsNames) {
        this.srsNames = srsNames;
    }

    @Deprecated
    public void extendSamplingTime(final Date newDate) {
        extendSamplingTime((newDate != null) ? newDate.toInstant() : null);
    }

    /**
     * Extend the current offering time span by adding a new date.
     * If the new date is before or after the current sampling time, the period will be expanded.
     * If no time is currently set, a time instant with the supplied date will be set as the new time span.
     *
     * @param newDate a date to integrate into the time span of the offering.
     */
    public void extendSamplingTime(final Temporal newDate) {
        if (newDate != null) {
            final Instant newInstant = TemporalUtilities.toInstant(newDate);
            if (time instanceof Period p) {
                Temporal currentStDate = p.getBeginning().getPosition();
                Temporal currentEnDate = p.getEnding().getPosition();
                if (newInstant.isBefore(TemporalUtilities.toInstant(currentStDate))) {
                    time = TemporalObjects.createPeriod(newDate, currentEnDate);
                    addIdentifiers((Period) time);
                } else if (newInstant.isAfter(TemporalUtilities.toInstant(currentEnDate))) {
                    time = TemporalObjects.createPeriod(currentStDate, newDate);
                    addIdentifiers((Period) time);
                }
                // date is within to the current period so no changes are applied
            } else if (time instanceof org.opengis.temporal.Instant i) {
                Temporal currentDate = i.getPosition();
                Instant currentInstant = TemporalUtilities.toInstant(currentDate);
                if (newInstant.isBefore(currentInstant)) {
                    time = TemporalObjects.createPeriod(newDate, currentDate);
                    addIdentifiers((Period) time);
                } else if (newInstant.isAfter(currentInstant)) {
                    time = TemporalObjects.createPeriod(currentDate, newDate);
                    addIdentifiers((Period) time);
                }
                // date is equals to the current date so no changes are applied
            } else if (time == null) {
                time = TemporalObjects.createInstant(newDate);
                addIdentifier((org.opengis.temporal.Instant) time);
            } else {
                throw new IllegalStateException("Unknown time implementeation: " + time.getClass().getName());
            }
        }
    }
}
