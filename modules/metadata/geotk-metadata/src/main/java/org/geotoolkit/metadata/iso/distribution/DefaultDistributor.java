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

import org.opengis.metadata.citation.ResponsibleParty;
import org.opengis.metadata.distribution.Format;
import org.opengis.metadata.distribution.Distributor;
import org.opengis.metadata.distribution.StandardOrderProcess;
import org.opengis.metadata.distribution.DigitalTransferOptions;

import org.geotoolkit.metadata.iso.MetadataEntity;


/**
 * Information about the distributor.
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
@ThreadSafe
@XmlType(name = "MD_Distributor_Type", propOrder={
    "distributorContact",
    "distributionOrderProcesses",
    "distributorFormats",
    "distributorTransferOptions"
})
@XmlRootElement(name = "MD_Distributor")
public class DefaultDistributor extends MetadataEntity implements Distributor {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 7142984376823483766L;

    /**
     * Party from whom the resource may be obtained. This list need not be exhaustive.
     */
    private ResponsibleParty distributorContact;

    /**
     * Provides information about how the resource may be obtained, and related
     * instructions and fee information.
     */
    private Collection<StandardOrderProcess> distributionOrderProcesses;

    /**
     * Provides information about the format used by the distributor.
     */
    private Collection<Format> distributorFormats;

    /**
     * Provides information about the technical means and media used by the distributor.
     */
    private Collection<DigitalTransferOptions> distributorTransferOptions;

    /**
     * Constructs an initially empty distributor.
     */
    public DefaultDistributor() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy, or {@code null} if none.
     *
     * @since 2.4
     */
    public DefaultDistributor(final Distributor source) {
        super(source);
    }

    /**
     * Creates a distributor with the specified contact.
     *
     * @param distributorContact Party from whom the resource may be obtained,
     *        or {@code null} if none.
     */
    public DefaultDistributor(final ResponsibleParty distributorContact) {
        if (distributorContact != null) {
            setDistributorContact(distributorContact);
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
    public static DefaultDistributor castOrCopy(final Distributor object) {
        return (object == null) || (object instanceof DefaultDistributor)
                ? (DefaultDistributor) object : new DefaultDistributor(object);
    }

    /**
     * Party from whom the resource may be obtained. This list need not be exhaustive.
     */
    @Override
    @XmlElement(name = "distributorContact", required = true)
    public synchronized ResponsibleParty getDistributorContact() {
        return distributorContact;
    }

    /**
     * Sets the party from whom the resource may be obtained. This list need not be exhaustive.
     *
     * @param newValue The new distributor contact.
     */
    public synchronized void setDistributorContact(final ResponsibleParty newValue) {
        checkWritePermission();
        distributorContact = newValue;
    }

    /**
     * Provides information about how the resource may be obtained, and related
     * instructions and fee information.
     */
    @Override
    @XmlElement(name = "distributionOrderProcess")
    public synchronized Collection<StandardOrderProcess> getDistributionOrderProcesses() {
        return distributionOrderProcesses = nonNullCollection(distributionOrderProcesses, StandardOrderProcess.class);
    }

    /**
     * Sets information about how the resource may be obtained, and related
     * instructions and fee information.
     *
     * @param newValues The new distribution order processes.
     */
    public synchronized void setDistributionOrderProcesses(final Collection<? extends StandardOrderProcess> newValues) {
        distributionOrderProcesses = copyCollection(newValues, distributionOrderProcesses, StandardOrderProcess.class);
    }

    /**
     * Provides information about the format used by the distributor.
     */
    @Override
    @XmlElement(name = "distributorFormat")
    public synchronized Collection<Format> getDistributorFormats() {
        return distributorFormats = nonNullCollection(distributorFormats, Format.class);
    }

    /**
     * Sets information about the format used by the distributor.
     *
     * @param newValues The new distributor formats.
     */
    public synchronized void setDistributorFormats(final Collection<? extends Format> newValues) {
        distributorFormats = copyCollection(newValues, distributorFormats, Format.class);
    }

    /**
     * Provides information about the technical means and media used by the distributor.
     */
    @Override
    @XmlElement(name = "distributorTransferOptions")
    public synchronized Collection<DigitalTransferOptions> getDistributorTransferOptions() {
        return distributorTransferOptions = nonNullCollection(distributorTransferOptions, DigitalTransferOptions.class);
    }

    /**
     * Provides information about the technical means and media used by the distributor.
     *
     * @param newValues The new distributor transfer options.
     */
    public synchronized void setDistributorTransferOptions(final Collection<? extends DigitalTransferOptions> newValues) {
        distributorTransferOptions = copyCollection(newValues, distributorTransferOptions, DigitalTransferOptions.class);
    }
}
