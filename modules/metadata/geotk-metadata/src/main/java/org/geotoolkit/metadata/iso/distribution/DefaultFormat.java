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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import net.jcip.annotations.ThreadSafe;

import org.opengis.util.InternationalString;
import org.opengis.metadata.distribution.Format;
import org.opengis.metadata.distribution.Distributor;

import org.geotoolkit.metadata.iso.MetadataEntity;


/**
 * Description of the computer language construct that specifies the representation
 * of data objects in a record, file, message, storage device or transmission channel.
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
@XmlType(name = "MD_Format_Type", propOrder={
    "name",
    "version",
    "amendmentNumber",
    "specification",
    "fileDecompressionTechnique",
    "formatDistributors"
})
@XmlRootElement(name = "MD_Format")
public class DefaultFormat extends MetadataEntity implements Format {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 6498897239493553607L;

    /**
     * Name of the data transfer format(s).
     */
    private InternationalString name;

    /**
     * Version of the format (date, number, etc.).
     */
    private InternationalString version;

    /**
     * Amendment number of the format version.
     */
    private InternationalString amendmentNumber;

    /**
     * Name of a subset, profile, or product specification of the format.
     */
    private InternationalString specification;

    /**
     * Recommendations of algorithms or processes that can be applied to read or
     * expand resources to which compression techniques have been applied.
     */
    private InternationalString fileDecompressionTechnique;

    /**
     * Provides information about the distributors format.
     */
    private Collection<Distributor> formatDistributors;

    /**
     * Constructs an initially empty format.
     */
    public DefaultFormat() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy, or {@code null} if none.
     *
     * @since 2.4
     */
    public DefaultFormat(final Format source) {
        super(source);
    }

    /**
     * Creates a format initialized to the given name.
     *
     * @param name    The name of the data transfer format(s).
     * @param version The version of the format (date, number, etc.).
     */
    public DefaultFormat(final InternationalString name, final InternationalString version) {
        setName   (name   );
        setVersion(version);
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
    public static DefaultFormat castOrCopy(final Format object) {
        return (object == null) || (object instanceof DefaultFormat)
                ? (DefaultFormat) object : new DefaultFormat(object);
    }

    /**
     * Returns the name of the data transfer format(s).
     */
    @Override
    @XmlElement(name = "name", required = true)
    public synchronized InternationalString getName() {
        return name;
    }

    /**
     * Sets the name of the data transfer format(s).
     *
     * @param newValue The new name.
     */
    public synchronized void setName(final InternationalString newValue) {
         checkWritePermission();
         name = newValue;
     }

    /**
     * Returne the version of the format (date, number, etc.).
     */
    @Override
    @XmlElement(name = "version", required = true)
    public synchronized InternationalString getVersion() {
        return version;
    }

    /**
     * Sets the version of the format (date, number, etc.).
     *
     * @param newValue The new version.
     */
    public synchronized void setVersion(final InternationalString newValue) {
        checkWritePermission();
        version = newValue;
    }

    /**
     * Returns the amendment number of the format version.
     */
    @Override
    @XmlElement(name = "amendmentNumber")
    public synchronized InternationalString getAmendmentNumber() {
        return amendmentNumber;
    }

    /**
     * Sets the amendment number of the format version.
     *
     * @param newValue The new amendment number.
     */
    public synchronized void setAmendmentNumber(final InternationalString newValue) {
        checkWritePermission();
        amendmentNumber = newValue;
    }

    /**
     * Returns the name of a subset, profile, or product specification of the format.
     */
    @Override
    @XmlElement(name = "specification")
    public synchronized InternationalString getSpecification() {
        return specification;
    }

    /**
     * Sets the name of a subset, profile, or product specification of the format.
     *
     * @param newValue The new specification.
     */
    public synchronized void setSpecification(final InternationalString newValue) {
        checkWritePermission();
        specification = newValue;
    }

    /**
     * Returns recommendations of algorithms or processes that can be applied to read or
     * expand resources to which compression techniques have been applied.
     */
    @Override
    @XmlElement(name = "fileDecompressionTechnique")
    public synchronized InternationalString getFileDecompressionTechnique() {
        return fileDecompressionTechnique;
    }

    /**
     * Sets recommendations of algorithms or processes that can be applied to read or
     * expand resources to which compression techniques have been applied.
     *
     * @param newValue The new file decompression technique.
     */
    public synchronized void setFileDecompressionTechnique(final InternationalString newValue) {
        checkWritePermission();
        fileDecompressionTechnique = newValue;
    }

    /**
     * Provides information about the distributors format.
     */
    @Override
    @XmlElement(name = "formatDistributor")
    public synchronized Collection<Distributor> getFormatDistributors() {
        return formatDistributors = nonNullCollection(formatDistributors, Distributor.class);
    }

    /**
     * Sets information about the distributors format.
     *
     * @param newValues The new format distributors.
     */
    public synchronized void setFormatDistributors(final Collection<? extends Distributor> newValues) {
        formatDistributors = copyCollection(newValues, formatDistributors, Distributor.class);
    }
}
