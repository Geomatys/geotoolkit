/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
import net.jcip.annotations.ThreadSafe;

import org.opengis.metadata.Identifier;
import org.opengis.metadata.acquisition.Context;
import org.opengis.metadata.acquisition.Event;
import org.opengis.metadata.acquisition.Instrument;
import org.opengis.metadata.acquisition.Objective;
import org.opengis.metadata.acquisition.PlatformPass;
import org.opengis.metadata.acquisition.Sequence;
import org.opengis.metadata.acquisition.Trigger;

import org.geotoolkit.metadata.iso.MetadataEntity;
import org.geotoolkit.internal.jaxb.NonMarshalledAuthority;


/**
 * Identification of a significant collection point within an operation.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.03
 * @module
 *
 * @deprecated Moved to the {@link org.apache.sis.metadata.iso} package.
 */
@ThreadSafe
@XmlType(name = "MI_Event_Type", propOrder={
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
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -5625600499628778407L;

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
        time = Long.MIN_VALUE;
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy, or {@code null} if none.
     */
    public DefaultEvent(final Event source) {
        super(source);
        if (source != null) {
            // Be careful to not overwrite date value (GEOTK-170).
            if (time == 0 && source.getTime() == null) {
                time = Long.MIN_VALUE;
            }
        }
    }

    /**
     * Returns a Geotk metadata implementation with the same values than the given arbitrary
     * implementation. If the given object is {@code null}, then this method returns {@code null}.
     * Otherwise if the given object is already a Geotk implementation, then the given object is
     * returned unchanged. Otherwise a new Geotk implementation is created and initialized to the
     * attribute values of the given object, using a <cite>shallow</cite> copy operation
     * (i.e. attributes are not cloned).
     *
     * @param  object The object to get as a Geotk implementation, or {@code null} if none.
     * @return A Geotk implementation containing the values of the given object (may be the
     *         given object itself), or {@code null} if the argument was null.
     *
     * @since 3.18
     */
    public static DefaultEvent castOrCopy(final Event object) {
        return (object == null) || (object instanceof DefaultEvent)
                ? (DefaultEvent) object : new DefaultEvent(object);
    }

    /**
     * Returns the event name or number.
     */
    @Override
    @XmlElement(name = "identifier", required = true)
    public Identifier getIdentifier() {
        return super.getIdentifier();
    }

    /**
     * Sets the event name or number.
     *
     * @param newValue The event identifier value.
     */
    public synchronized void setIdentifier(final Identifier newValue) {
        checkWritePermission();
        NonMarshalledAuthority.setMarshallable(super.getIdentifiers(), newValue);
    }

    /**
     * Returns the initiator of the event.
     */
    @Override
    @XmlElement(name = "trigger", required = true)
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
    @XmlElement(name = "context", required = true)
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
    @XmlElement(name = "sequence", required = true)
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
    @XmlElement(name = "time", required = true)
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
        return expectedObjectives = nonNullCollection(expectedObjectives, Objective.class);
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
        return relatedSensors = nonNullCollection(relatedSensors, Instrument.class);
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
