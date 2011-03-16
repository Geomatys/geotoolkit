/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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

import org.opengis.geometry.Geometry;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.acquisition.Event;
import org.opengis.metadata.acquisition.PlatformPass;

import org.geotoolkit.lang.ThreadSafe;
import org.geotoolkit.metadata.iso.MetadataEntity;


/**
 * Identification of collection coverage.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.17
 *
 * @since 3.03
 * @module
 */
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
    private static final long serialVersionUID = -1695097227120034433L;

    /**
     * Unique name of the pass.
     */
    private Identifier identifier;

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
     * Returns the unique name of the pass.
     */
    @Override
    @XmlElement(name = "identifier", required = true)
    public synchronized Identifier getIdentifier() {
        return identifier;
    }

    /**
     * Sets the unique name of the pass.
     *
     * @param newValue The new identifier value.
     */
    public synchronized void setIdentifier(final Identifier newValue) {
        checkWritePermission();
        identifier = newValue;
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
        return xmlOptional(relatedEvents = nonNullCollection(relatedEvents, Event.class));
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
