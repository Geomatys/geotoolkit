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
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import net.jcip.annotations.ThreadSafe;

import org.opengis.util.InternationalString;
import org.opengis.metadata.citation.OnlineResource;
import org.opengis.metadata.distribution.DigitalTransferOptions;
import org.opengis.metadata.distribution.Medium;

import org.geotoolkit.lang.ValueRange;
import org.geotoolkit.metadata.iso.MetadataEntity;
import org.geotoolkit.internal.jaxb.gco.GO_Real;


/**
 * Technical means and media by which a resource is obtained from the distributor.
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
@XmlType(name = "MD_DigitalTransferOptions_Type", propOrder={
    "unitsOfDistribution",
    "transferSize",
    "onLines",
    "offLine"
})
@XmlRootElement(name = "MD_DigitalTransferOptions")
public class DefaultDigitalTransferOptions extends MetadataEntity implements DigitalTransferOptions {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -1533064478468754337L;

    /**
     * Tiles, layers, geographic areas, etc., in which data is available.
     */
    private InternationalString unitsOfDistribution;

    /**
     * Estimated size of a unit in the specified transfer format, expressed in megabytes.
     * The transfer size is &gt; 0.0.
     * Returns {@code null} if the transfer size is unknown.
     */
    private Double transferSize;

    /**
     * Information about online sources from which the resource can be obtained.
     */
    private Collection<OnlineResource> onLines;

    /**
     * Information about offline media on which the resource can be obtained.
     */
    private Medium offLines;

    /**
     * Constructs an initially empty digital transfer options.
     */
    public DefaultDigitalTransferOptions() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy, or {@code null} if none.
     *
     * @since 2.4
     */
    public DefaultDigitalTransferOptions(final DigitalTransferOptions source) {
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
    public static DefaultDigitalTransferOptions castOrCopy(final DigitalTransferOptions object) {
        return (object == null) || (object instanceof DefaultDigitalTransferOptions)
                ? (DefaultDigitalTransferOptions) object : new DefaultDigitalTransferOptions(object);
    }

    /**
     * Returns tiles, layers, geographic areas, etc., in which data is available.
     */
    @Override
    @XmlElement(name = "unitsOfDistribution")
    public synchronized InternationalString getUnitsOfDistribution() {
        return unitsOfDistribution;
    }

    /**
     * Sets tiles, layers, geographic areas, etc., in which data is available.
     *
     * @param newValue The new units of distribution.
     */
    public synchronized void setUnitsOfDistribution(final InternationalString newValue) {
        checkWritePermission();
        unitsOfDistribution = newValue;
    }

    /**
     * Returns an estimated size of a unit in the specified transfer format, expressed in megabytes.
     * The transfer size is &gt; 0.0. Returns {@code null} if the transfer size is unknown.
     */
    @Override
    @ValueRange(minimum=0)
    @XmlElement(name = "transferSize")
    @XmlJavaTypeAdapter(GO_Real.class)
    public synchronized Double getTransferSize() {
        return transferSize;
    }

    /**
     * Sets an estimated size of a unit in the specified transfer format, expressed in megabytes.
     * The transfer size is &gt; 0.0.
     *
     * @param newValue The new transfer size.
     */
    public synchronized void setTransferSize(final Double newValue) {
        checkWritePermission();
        transferSize = newValue;
    }

    /**
     * Returns information about online sources from which the resource can be obtained.
     */
    @Override
    @XmlElement(name = "onLine")
    public synchronized Collection<OnlineResource> getOnLines() {
        return onLines = nonNullCollection(onLines, OnlineResource.class);
    }

    /**
     * Sets information about online sources from which the resource can be obtained.
     *
     * @param newValues The new online sources.
     */
    public synchronized void setOnLines(final Collection<? extends OnlineResource> newValues) {
        onLines = copyCollection(newValues, onLines, OnlineResource.class);
    }

    /**
     * Returns information about offline media on which the resource can be obtained.
     */
    @Override
    @XmlElement(name = "offLine")
    public synchronized Medium getOffLine() {
        return offLines;
    }

    /**
     * Sets information about offline media on which the resource can be obtained.
     *
     * @param newValue The new offline media.
     */
    public synchronized void setOffLine(final Medium newValue) {
        checkWritePermission();
        offLines = newValue;
    }
}
