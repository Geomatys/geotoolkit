/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.metadata.iso.quality;

import java.util.Collection;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import net.jcip.annotations.ThreadSafe;

import org.opengis.metadata.extent.Extent;
import org.opengis.metadata.quality.Scope;
import org.opengis.metadata.maintenance.ScopeCode;
import org.opengis.metadata.maintenance.ScopeDescription;

import org.geotoolkit.metadata.iso.MetadataEntity;


/**
 * Description of the data specified by the scope.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Touraïvane (IRD)
 * @version 3.19
 *
 * @since 2.1
 * @module
 *
 * @deprecated Moved to the {@link org.apache.sis.metadata.iso} package.
 */
@Deprecated
@ThreadSafe
@XmlType(name = "DQ_Scope_Type", propOrder={
   "level",
   "extent",
   "levelDescription"
})
@XmlRootElement(name = "DQ_Scope")
public class DefaultScope extends MetadataEntity implements Scope {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -8021256328527422972L;

    /**
     * Hierarchical level of the data specified by the scope.
     */
    private ScopeCode level;

    /**
     * Information about the spatial, vertical and temporal extent of the data specified by the
     * scope.
     */
    private Extent extent;

    /**
     * Detailed description about the level of the data specified by the scope.
     */
    private Collection<ScopeDescription> levelDescription;

    /**
     * Constructs an initially empty scope.
     */
    public DefaultScope() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy, or {@code null} if none.
     *
     * @since 2.4
     */
    public DefaultScope(final Scope source) {
        super(source);
    }

    /**
     * Creates a scope initialized to the given level.
     *
     * @param level The hierarchical level of the data specified by the scope.
     */
    public DefaultScope(final ScopeCode level) {
        setLevel(level);
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
    public static DefaultScope castOrCopy(final Scope object) {
        return (object == null) || (object instanceof DefaultScope)
                ? (DefaultScope) object : new DefaultScope(object);
    }

    /**
     * Returns the hierarchical level of the data specified by the scope.
     */
    @Override
    @XmlElement(name = "level", required = true)
    public synchronized ScopeCode getLevel() {
        return level;
    }

    /**
     * Sets the hierarchical level of the data specified by the scope.
     *
     * @param newValue The new level.
     */
    public synchronized void setLevel(final ScopeCode newValue) {
        checkWritePermission();
        level = newValue;
    }

    /**
     * Returns detailed descriptions about the level of the data specified by the scope.
     * Should be defined only if the {@linkplain #getLevel level} is not equal
     * to {@link ScopeCode#DATASET DATASET} or {@link ScopeCode#SERIES SERIES}.
     *
     * @since 2.4
     */
    @Override
    @XmlElement(name = "levelDescription")
    public synchronized Collection<ScopeDescription> getLevelDescription() {
        return levelDescription = nonNullCollection(levelDescription, ScopeDescription.class);
    }

    /**
     * Sets detailed descriptions about the level of the data specified by the scope.
     *
     * @param newValues The new level description.
     *
     * @since 2.4
     */
    public synchronized void setLevelDescription(final Collection<? extends ScopeDescription> newValues) {
        levelDescription = copyCollection(newValues, levelDescription, ScopeDescription.class);
    }

    /**
     * Information about the spatial, vertical and temporal extent of the data specified by the
     * scope.
     */
    @Override
    @XmlElement(name = "extent")
    public synchronized Extent getExtent() {
        return extent;
    }

    /**
     * Sets information about the spatial, vertical and temporal extent of the data specified
     * by the scope.
     *
     * @param newValue The new extent.
     */
    public synchronized void setExtent(final Extent newValue) {
        checkWritePermission();
        extent = newValue;
    }
}
