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

import org.opengis.geometry.Geometry;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.acquisition.Event;
import org.opengis.metadata.acquisition.PlatformPass;

import org.geotoolkit.metadata.iso.MetadataEntity;
import org.geotoolkit.internal.jaxb.NonMarshalledAuthority;


/**
 * Identification of collection coverage.
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
@XmlType(name = "MI_PlatformPass_Type", propOrder={
    "identifier",
    "extent",
    "relatedEvents"
})
@XmlRootElement(name = "MI_PlatformPass")
public class DefaultPlatformPass extends MetadataEntity implements PlatformPass {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -1695097227120034432L;

    /**
     * Area covered by the pass.
     */
    private Geometry extent;

    /**
     * Occurrence of one or more events for a pass.
     */
    private Collection<Event> relatedEvents;

    /**
     * Constructs an initially empty platform pass.
     */
    public DefaultPlatformPass() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy, or {@code null} if none.
     */
    public DefaultPlatformPass(final PlatformPass source) {
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
    public static DefaultPlatformPass castOrCopy(final PlatformPass object) {
        return (object == null) || (object instanceof DefaultPlatformPass)
                ? (DefaultPlatformPass) object : new DefaultPlatformPass(object);
    }

    /**
     * Returns the unique name of the pass.
     */
    @Override
    @XmlElement(name = "identifier", required = true)
    public Identifier getIdentifier() {
        return super.getIdentifier();
    }

    /**
     * Sets the unique name of the pass.
     *
     * @param newValue The new identifier value.
     */
    public synchronized void setIdentifier(final Identifier newValue) {
        checkWritePermission();
        NonMarshalledAuthority.setMarshallable(super.getIdentifiers(), newValue);
    }

    /**
     * Returns the area covered by the pass. {@code null} if unspecified.
     *
     * @todo annotate an implementation of {@link Geometry} in order to annotate this method.
     */
    @Override
    @XmlElement(name = "extent")
    public synchronized Geometry getExtent() {
        return extent;
    }

    /**
     * Sets the area covered by the pass.
     *
     * @param newValue The new extent value.
     */
    public synchronized void setExtent(final Geometry newValue) {
        checkWritePermission();
        extent = newValue;
    }

    /**
     * Returns the occurrence of one or more events for a pass.
     */
    @Override
    @XmlElement(name = "relatedEvent")
    public synchronized Collection<Event> getRelatedEvents() {
        return relatedEvents = nonNullCollection(relatedEvents, Event.class);
    }

    /**
     * Sets the occurrence of one or more events for a pass.
     *
     * @param newValues The new related events values.
     */
    public synchronized void setRelatedEvents(final Collection<? extends Event> newValues) {
        relatedEvents = copyCollection(newValues, relatedEvents, Event.class);
    }
}
