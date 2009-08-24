/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.metadata.iso.acquisition;

import java.util.Collection;
import java.util.Date;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.opengis.metadata.Identifier;
import org.opengis.metadata.acquisition.Context;
import org.opengis.metadata.acquisition.Event;
import org.opengis.metadata.acquisition.Instrument;
import org.opengis.metadata.acquisition.Objective;
import org.opengis.metadata.acquisition.PlatformPass;
import org.opengis.metadata.acquisition.Sequence;
import org.opengis.metadata.acquisition.Trigger;

import org.geotoolkit.metadata.iso.MetadataEntity;


/**
 * Identification of a significant collection point within an operation.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.03
 *
 * @since 3.03
 * @module
 */
@XmlType(propOrder={
    "identifier",
    "trigger",
    "context",
    "sequence",
    "time",
    "expectedObjectives",
    "relatedPass",
    "relatedSensors"
})
@XmlRootElement(name = "MI_Event")
public class DefaultEvent extends MetadataEntity implements Event {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = -5625600499628778406L;

    /**
     * Event name or number.
     */
    private Identifier identifier;

    /**
     * Initiator of the event.
     */
    private Trigger trigger;

    /**
     * Meaning of the event.
     */
    private Context context;

    /**
     * Relative time ordering of the event.
     */
    private Sequence sequence;

    /**
     * Time the event occurred, or {@link Long#MIN_VALUE} if none.
     */
    private long time;

    /**
     * Objective or objectives satisfied by an event.
     */
    private Collection<Objective> expectedObjectives;

    /**
     * Pass during which an event occurs.
     */
    private PlatformPass relatedPass;

    /**
     * Instrument or instruments for which the event is meaningful.
     */
    private Collection<Instrument> relatedSensors;

    /**
     * Constructs an initially empty acquisition information.
     */
    public DefaultEvent() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy.
     */
    public DefaultEvent(final Event source) {
        super(source);
    }

    /**
     * Returns the event name or number.
     */
    @Override
    @XmlElement(name = "identifier")
    public synchronized Identifier getIdentifier() {
        return identifier;
    }

    /**
     * Sets the event name or number.
     *
     * @param newValue The event identifier value.
     */
    public synchronized void setIdentifier(final Identifier newValue) {
        checkWritePermission();
        identifier = newValue;
    }

    /**
     * Returns the initiator of the event.
     */
    @Override
    @XmlElement(name = "trigger")
    public synchronized Trigger getTrigger() {
        return trigger;
    }

    /**
     * Sets the initiator of the event.
     *
     * @param newValue The new trigger value.
     */
    public synchronized void setTrigger(final Trigger newValue) {
        checkWritePermission();
        trigger = newValue;
    }

    /**
     * Meaning of the event.
     */
    @Override
    @XmlElement(name = "context")
    public synchronized Context getContext() {
        return context;
    }

    /**
     * Sets the meaning of the event.
     *
     * @param newValue The new context value.
     */
    public synchronized void setContext(final Context newValue) {
        checkWritePermission();
        context = newValue;
    }

    /**
     * Returns the relative time ordering of the event.
     */
    @Override
    @XmlElement(name = "sequence")
    public synchronized Sequence getSequence() {
        return sequence;
    }

    /**
     * Sets the relative time ordering of the event.
     *
     * @param newValue The new sequence value.
     */
    public synchronized void setSequence(final Sequence newValue) {
        checkWritePermission();
        sequence = newValue;
    }

    /**
     * Returns the time the event occurred.
     */
    @Override
    @XmlElement(name = "time")
    public synchronized Date getTime() {
        final long date = this.time;
        return (date != Long.MIN_VALUE) ? new Date(date) : null;
    }

    /**
     * Sets the time the event occurred.
     *
     * @param newValue The new time value.
     */
    public synchronized void setTime(final Date newValue) {
        checkWritePermission();
        time = (newValue != null) ? newValue.getTime() : Long.MIN_VALUE;
    }

    /**
     * Returns the objective or objectives satisfied by an event.
     */
    @Override
    @XmlElement(name = "expectedObjective")
    public synchronized Collection<Objective> getExpectedObjectives() {
        return xmlOptional(expectedObjectives = nonNullCollection(expectedObjectives, Objective.class));
    }

    /**
     * Sets the objective or objectives satisfied by an event.
     *
     * @param newValues The new expected objectives values.
     */
    public synchronized void setExpectedObjectives(final Collection<? extends Objective> newValues) {
        expectedObjectives = copyCollection(newValues, expectedObjectives, Objective.class);
    }

    /**
     * Returns the pass during which an event occurs. {@code null} if unspecified.
     */
    @Override
    @XmlElement(name = "relatedPass")
    public synchronized PlatformPass getRelatedPass() {
        return relatedPass;
    }

    /**
     * Sets the pass during which an event occurs.
     *
     * @param newValue The new platform pass value.
     */
    public synchronized void setRelatedPass(final PlatformPass newValue) {
        relatedPass = newValue;
    }

    /**
     * Returns the instrument or instruments for which the event is meaningful.
     */
    @Override
    @XmlElement(name = "relatedSensor")
    public synchronized Collection<? extends Instrument> getRelatedSensors() {
        return xmlOptional(relatedSensors = nonNullCollection(relatedSensors, Instrument.class));
    }

    /**
     * Sets the instrument or instruments for which the event is meaningful.
     *
     * @param newValues The new instrument values.
     */
    public synchronized void setRelatedSensors(final Collection<? extends Instrument> newValues) {
        relatedSensors = copyCollection(newValues, relatedSensors, Instrument.class);
    }
}
