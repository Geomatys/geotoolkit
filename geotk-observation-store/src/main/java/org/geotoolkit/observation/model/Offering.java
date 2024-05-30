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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.geotoolkit.temporal.object.DefaultInstant;
import org.geotoolkit.temporal.object.DefaultPeriod;
import org.geotoolkit.temporal.object.InstantWrapper;
import org.opengis.geometry.Envelope;
import static org.opengis.referencing.IdentifiedObject.NAME_KEY;
import org.opengis.temporal.Period;
import org.opengis.temporal.TemporalGeometricPrimitive;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class Offering extends AbstractOMEntity {

    private TemporalGeometricPrimitive time;

    private Envelope bounds;

    private List<String> srsNames;

    private String procedure;

    private List<String> featureOfInterestIds;

    private List<String> observedProperties;

    // for JSON
    private Offering() {}

    public Offering(String id, String name, String description, Map<String, Object> properties, Envelope bounds, List<String> srsNames,
            TemporalGeometricPrimitive time, String procedure, List<String> observedProperties, List<String> featureOfInterestIds) {
        super(id, name, description, properties);
        this.bounds = bounds;
        this.srsNames = srsNames;
        this.time = time;
        this.procedure = procedure;
        this.observedProperties = observedProperties;
        this.featureOfInterestIds = featureOfInterestIds;
    }

    public TemporalGeometricPrimitive getTime() {
        return time;
    }

    public void setTime(TemporalGeometricPrimitive time) {
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
            final Instant newInstant = InstantWrapper.toInstant(newDate);
            if (time instanceof Period p) {
                Temporal currentStDate = p.getBeginning();
                Temporal currentEnDate = p.getEnding();
                if (newInstant.isBefore(InstantWrapper.toInstant(currentStDate))) {
                    time = new DefaultPeriod(Collections.singletonMap(NAME_KEY, getId() + "-time"),
                            new DefaultInstant(Collections.singletonMap(NAME_KEY, getId() + "-st-time"), newDate),
                            new DefaultInstant(Collections.singletonMap(NAME_KEY, getId() + "-en-time"), currentEnDate));
                } else if (newInstant.isAfter(InstantWrapper.toInstant(currentEnDate))) {
                    time = new DefaultPeriod(Collections.singletonMap(NAME_KEY, getId() + "-time"),
                            new DefaultInstant(Collections.singletonMap(NAME_KEY, getId() + "-st-time"), currentStDate),
                            new DefaultInstant(Collections.singletonMap(NAME_KEY, getId() + "-en-time"), newDate));
                }
                // date is within to the current period so no changes are applied
            } else if (time instanceof InstantWrapper i) {
                Temporal currentDate = i.getTemporal();
                Instant currentInstant = InstantWrapper.toInstant(currentDate);
                if (newInstant.isBefore(currentInstant)) {
                    time = new DefaultPeriod(Collections.singletonMap(NAME_KEY, getId() + "-time"),
                            new DefaultInstant(Collections.singletonMap(NAME_KEY, getId() + "-st-time"), newDate),
                            new DefaultInstant(Collections.singletonMap(NAME_KEY, getId() + "-en-time"), currentDate));
                } else if (newInstant.isAfter(currentInstant)) {
                    time = new DefaultPeriod(Collections.singletonMap(NAME_KEY, getId() + "-time"),
                            new DefaultInstant(Collections.singletonMap(NAME_KEY, getId() + "-st-time"), currentDate),
                            new DefaultInstant(Collections.singletonMap(NAME_KEY, getId() + "-en-time"), newDate));
                }
                // date is equals to the current date so no changes are applied
            } else if (time == null) {
                time = new DefaultInstant(Collections.singletonMap(NAME_KEY, getId() + "-time"), newDate);
            } else {
                throw new IllegalStateException("Unknown time implementeation: " + time.getClass().getName());
            }
        }
    }
}
