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

import org.opengis.metadata.lineage.Lineage;
import org.opengis.metadata.quality.DataQuality;
import org.opengis.metadata.quality.Element;
import org.opengis.metadata.quality.Scope;

import org.geotoolkit.metadata.iso.MetadataEntity;


/**
 * Quality information for the data specified by a data quality scope.
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
@ThreadSafe
@XmlType(name = "DQ_DataQuality_Type", propOrder={
    "scope",
    "reports",
    "lineage"
})
@XmlRootElement(name = "DQ_DataQuality")
public class DefaultDataQuality extends MetadataEntity implements DataQuality {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 7964896551368382214L;

    /**
     * The specific data to which the data quality information applies.
     */
    private Scope scope;

    /**
     * Quantitative quality information for the data specified by the scope.
     * Should be provided only if {@linkplain Scope#getLevel scope level} is
     * {@linkplain org.opengis.metadata.maintenance.ScopeCode#DATASET dataset}.
     */
    private Collection<Element> reports;

    /**
     * Non-quantitative quality information about the lineage of the data specified by the scope.
     * Should be provided only if {@linkplain Scope#getLevel scope level} is
     * {@linkplain org.opengis.metadata.maintenance.ScopeCode#DATASET dataset}.
     */
    private Lineage lineage;

    /**
     * Constructs an initially empty data quality.
     */
    public DefaultDataQuality() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy, or {@code null} if none.
     *
     * @since 2.4
     */
    public DefaultDataQuality(final DataQuality source) {
        super(source);
    }

    /**
     * Creates a data quality initialized to the given scope.
     *
     * @param scope The specific data to which the data quality information applies.
     */
    public DefaultDataQuality(Scope scope) {
        setScope(scope);
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
    public static DefaultDataQuality castOrCopy(final DataQuality object) {
        return (object == null) || (object instanceof DefaultDataQuality)
                ? (DefaultDataQuality) object : new DefaultDataQuality(object);
    }

    /**
     * Returns the specific data to which the data quality information applies.
     */
    @Override
    @XmlElement(name = "scope", required = true)
    public synchronized Scope getScope() {
        return scope;
    }

    /**
     * Sets the specific data to which the data quality information applies.
     *
     * @param newValue The new scope.
     */
    public synchronized void setScope(final Scope newValue) {
        checkWritePermission();
        scope = newValue;
    }

    /**
     * Returns the quantitative quality information for the data specified by the
     * scope. Should be provided only if {@linkplain Scope#getLevel scope level}
     * is {@linkplain org.opengis.metadata.maintenance.ScopeCode#DATASET dataset}.
     */
    @Override
    @XmlElement(name = "report")
    public synchronized Collection<Element> getReports() {
        return reports = nonNullCollection(reports, Element.class);
    }

    /**
     * Sets the quantitative quality information for the data specified by the scope.
     * Should be provided only if {@linkplain Scope#getLevel scope level} is
     * {@linkplain org.opengis.metadata.maintenance.ScopeCode#DATASET dataset}.
     *
     * @param newValues The new reports.
     */
    public synchronized void setReports(final Collection<? extends Element> newValues) {
        reports = copyCollection(newValues, reports, Element.class);
    }

    /**
     * Returns non-quantitative quality information about the lineage of the data specified
     * by the scope. Should be provided only if {@linkplain Scope#getLevel scope level} is
     * {@linkplain org.opengis.metadata.maintenance.ScopeCode#DATASET dataset}.
     */
    @Override
    @XmlElement(name = "lineage")
    public synchronized Lineage getLineage() {
        return lineage;
    }

    /**
     * Sets the non-quantitative quality information about the lineage of the data specified
     * by the scope. Should be provided only if {@linkplain Scope#getLevel scope level} is
     * {@linkplain org.opengis.metadata.maintenance.ScopeCode#DATASET dataset}.
     *
     * @param newValue The new lineage.
     */
    public synchronized void setLineage(final Lineage newValue) {
        checkWritePermission();
        lineage = newValue;
    }
}
