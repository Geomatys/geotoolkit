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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import net.jcip.annotations.ThreadSafe;

import org.opengis.metadata.Identifier;
import org.opengis.metadata.acquisition.Event;
import org.opengis.metadata.acquisition.Instrument;
import org.opengis.metadata.acquisition.Objective;
import org.opengis.metadata.acquisition.ObjectiveType;
import org.opengis.metadata.acquisition.PlatformPass;
import org.opengis.metadata.extent.Extent;
import org.opengis.util.InternationalString;

import org.geotoolkit.xml.IdentifierSpace;
import org.geotoolkit.metadata.iso.MetadataEntity;
import org.geotoolkit.internal.jaxb.NonMarshalledAuthority;

import static org.geotoolkit.internal.jaxb.MarshalContext.filterIdentifiers;


/**
 * Describes the characteristics, spatial and temporal extent of the intended object to be
 * observed.
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
@Deprecated
@ThreadSafe
@XmlType(name = "MI_Objective_Type", propOrder={
    "identifiers",
    "priority",
    "types",
    "functions",
    "extents",
    "objectiveOccurences",
    "pass",
    "sensingInstruments"
})
@XmlRootElement(name = "MI_Objective")
public class DefaultObjective extends MetadataEntity implements Objective {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -4633298523976029384L;

    /**
     * Priority applied to the target.
     */
    private InternationalString priority;

    /**
     * Collection technique for the objective.
     */
    private Collection<ObjectiveType> types;

    /**
     * Role or purpose performed by or activity performed at the objective.
     */
    private Collection<InternationalString> functions;

    /**
     * Extent information including the bounding box, bounding polygon, vertical and
     * temporal extent of the objective.
     */
    private Collection<Extent> extents;

    /**
     * Event or events associated with objective completion.
     */
    private Collection<Event> objectiveOccurences;

    /**
     * Pass of the platform over the objective.
     */
    private Collection<PlatformPass> pass;

    /**
     * Instrument which senses the objective data.
     */
    private Collection<Instrument> sensingInstruments;

    /**
     * Constructs an initially empty objective.
     */
    public DefaultObjective() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy, or {@code null} if none.
     */
    public DefaultObjective(final Objective source) {
        super(source);
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
    public static DefaultObjective castOrCopy(final Objective object) {
        return (object == null) || (object instanceof DefaultObjective)
                ? (DefaultObjective) object : new DefaultObjective(object);
    }

    /**
     * Returns the code used to identify the objective.
     */
    @Override
    @XmlElement(name = "identifier", required = true)
    public Collection<Identifier> getIdentifiers() {
        return filterIdentifiers(super.getIdentifiers());
    }

    /**
     * Sets the code used to identify the objective.
     *
     * The {@linkplain IdentifierSpace XML identifiers} ({@linkplain IdentifierSpace#ID ID},
     * {@linkplain IdentifierSpace#UUID UUID}, <i>etc.</i>) are ignored because they are
     * associated to particular metadata instances.
     *
     * @param newValues The new identifiers values.
     */
    public synchronized void setIdentifiers(final Collection<? extends Identifier> newValues) {
        final Collection<Identifier> oldIds = NonMarshalledAuthority.getIdentifiers(identifiers);
        identifiers = copyCollection(newValues, identifiers, Identifier.class);
        NonMarshalledAuthority.setIdentifiers(identifiers, oldIds);
    }

    /**
     * Returns the priority applied to the target. {@code null} if unspecified.
     */
    @Override
    @XmlElement(name = "priority")
    public synchronized InternationalString getPriority() {
        return priority;
    }

    /**
     * Sets the priority applied to the target.
     *
     * @param newValue The new priority value.
     */
    public synchronized void setPriority(final InternationalString newValue) {
        checkWritePermission();
        priority = newValue;
    }

    /**
     * Returns the collection technique for the objective.
     */
    @Override
    @XmlElement(name = "type")
    public synchronized Collection<ObjectiveType> getTypes() {
        return types = nonNullCollection(types, ObjectiveType.class);
    }

    /**
     * Sets the collection technique for the objective.
     *
     * @param newValues The new types values.
     */
    public synchronized void setTypes(final Collection<? extends ObjectiveType> newValues) {
        types = copyCollection(newValues, types, ObjectiveType.class);
    }

    /**
     * Returns the role or purpose performed by or activity performed at the objective.
     */
    @Override
    @XmlElement(name = "function")
    public synchronized Collection<InternationalString> getFunctions() {
        return functions = nonNullCollection(functions, InternationalString.class);
    }

    /**
     * Sets the role or purpose performed by or activity performed at the objective.
     *
     * @param newValues The new functions values.
     */
    public synchronized void setFunctions(final Collection<? extends InternationalString> newValues) {
        functions = copyCollection(newValues, functions, InternationalString.class);
    }

    /**
     * Returns the extent information including the bounding box, bounding polygon, vertical and
     * temporal extent of the objective.
     */
    @Override
    @XmlElement(name = "extent")
    public synchronized Collection<Extent> getExtents() {
        return extents = nonNullCollection(extents, Extent.class);
    }

    /**
     * Set the extent information including the bounding box, bounding polygon, vertical and
     * temporal extent of the objective.
     *
     * @param newValues The new extents values.
     */
    public synchronized void setExtents(final Collection<? extends Extent> newValues) {
        extents = copyCollection(newValues, extents, Extent.class);
    }

    /**
     * Returns the event or events associated with objective completion.
     */
    @Override
    @XmlElement(name = "objectiveOccurence", required = true)
    public synchronized Collection<Event> getObjectiveOccurences() {
        return objectiveOccurences = nonNullCollection(objectiveOccurences, Event.class);
    }

    /**
     * Sets the event or events associated with objective completion.
     *
     * @param newValues The new objective occurrences values.
     */
    public synchronized void setObjectiveOccurences(final Collection<? extends Event> newValues) {
        objectiveOccurences = copyCollection(newValues, objectiveOccurences, Event.class);
    }

    /**
     * Returns the pass of the platform over the objective.
     */
    @Override
    @XmlElement(name = "pass")
    public synchronized Collection<PlatformPass> getPass() {
        return pass = nonNullCollection(pass, PlatformPass.class);
    }

    /**
     * Sets the pass of the platform over the objective.
     *
     * @param newValues The new pass values.
     */
    public synchronized void setPass(final Collection<? extends PlatformPass> newValues) {
        pass = copyCollection(newValues, pass, PlatformPass.class);
    }

    /**
     * Returns the instrument which senses the objective data.
     */
    @Override
    @XmlElement(name = "sensingInstrument")
    public synchronized Collection<Instrument> getSensingInstruments() {
        return sensingInstruments = nonNullCollection(sensingInstruments, Instrument.class);
    }

    /**
     * Sets the instrument which senses the objective data.
     *
     * @param newValues The new sensing instruments values.
     */
    public synchronized void setSensingInstruments(final Collection<? extends Instrument> newValues) {
        sensingInstruments = copyCollection(newValues, sensingInstruments, Instrument.class);
    }
}
