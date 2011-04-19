/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2011, Open Source Geospatial Foundation (OSGeo)
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
 * @author Martin Desruisseaux (IRD)
 * @author Touraïvane (IRD)
 * @version 3.04
 *
 * @since 2.1
 * @module
 */
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
