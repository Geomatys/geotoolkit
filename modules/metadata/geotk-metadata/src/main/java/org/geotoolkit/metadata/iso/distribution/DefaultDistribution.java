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
package org.geotoolkit.metadata.iso.distribution;

import java.util.Collection;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import net.jcip.annotations.ThreadSafe;

import org.opengis.metadata.distribution.DigitalTransferOptions;
import org.opengis.metadata.distribution.Distribution;
import org.opengis.metadata.distribution.Distributor;
import org.opengis.metadata.distribution.Format;

import org.geotoolkit.metadata.iso.MetadataEntity;


/**
 * Information about the distributor of and options for obtaining the resource.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Touraïvane (IRD)
 * @author Cédric Briançon (Geomatys)
 * @version 3.19
 *
 * @since 2.1
 * @module
 *
 * @deprecated Moved to the {@link org.apache.sis.metadata.iso} package.
 */
@Deprecated
@ThreadSafe
@XmlType(name = "MD_Distribution_Type", propOrder={
    "distributionFormats",
    "distributors",
    "transferOptions"
})
@XmlRootElement(name = "MD_Distribution")
public class DefaultDistribution extends MetadataEntity implements Distribution {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -5899590027802365131L;

    /**
     * Provides a description of the format of the data to be distributed.
     */
    private Collection<Format> distributionFormats;

    /**
     * Provides information about the distributor.
     */
    private Collection<Distributor> distributors;

    /**
     * Provides information about technical means and media by which a resource is obtained
     * from the distributor.
     */
    private Collection<DigitalTransferOptions> transferOptions;

    /**
     * Constructs an initially empty distribution.
     */
    public DefaultDistribution() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy, or {@code null} if none.
     *
     * @since 2.4
     */
    public DefaultDistribution(final Distribution source) {
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
    public static DefaultDistribution castOrCopy(final Distribution object) {
        return (object == null) || (object instanceof DefaultDistribution)
                ? (DefaultDistribution) object : new DefaultDistribution(object);
    }

    /**
     * Provides a description of the format of the data to be distributed.
     */
    @Override
    @XmlElement(name = "distributionFormat")
    public synchronized Collection<Format> getDistributionFormats() {
        return distributionFormats = nonNullCollection(distributionFormats, Format.class);
    }

    /**
     * Sets a description of the format of the data to be distributed.
     *
     * @param newValues The new distribution formats.
     */
    public synchronized void setDistributionFormats(final Collection<? extends Format> newValues) {
        distributionFormats = copyCollection(newValues, distributionFormats, Format.class);
    }

    /**
     * Provides information about the distributor.
     */
    @Override
    @XmlElement(name = "distributor")
    public synchronized Collection<Distributor> getDistributors() {
        return distributors = nonNullCollection(distributors, Distributor.class);
    }

    /**
     * Sets information about the distributor.
     *
     * @param newValues The new distributors.
     */
    public synchronized void setDistributors(final Collection<? extends Distributor> newValues) {
        distributors = copyCollection(newValues, distributors, Distributor.class);
    }

    /**
     * Provides information about technical means and media by which a resource is obtained
     * from the distributor.
     */
    @Override
    @XmlElement(name = "transferOptions")
    public synchronized Collection<DigitalTransferOptions> getTransferOptions() {
        return transferOptions = nonNullCollection(transferOptions, DigitalTransferOptions.class);
    }

    /**
     * Sets information about technical means and media by which a resource is obtained
     * from the distributor.
     *
     * @param newValues The new transfer options.
     */
    public synchronized void setTransferOptions(final Collection<? extends DigitalTransferOptions> newValues) {
        transferOptions = copyCollection(newValues, transferOptions, DigitalTransferOptions.class);
    }
}
