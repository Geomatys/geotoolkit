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
package org.geotoolkit.metadata.iso.distribution;

import java.util.Collection;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import net.jcip.annotations.ThreadSafe;

import org.opengis.metadata.distribution.Format;
import org.opengis.metadata.distribution.DataFile;
import org.opengis.util.LocalName;

import org.geotoolkit.xml.Namespaces;
import org.geotoolkit.metadata.iso.MetadataEntity;


/**
 * Description of a transfer data file.
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
@ThreadSafe
@XmlType(name = "MX_DataFile_Type", propOrder={
    "featureTypes",
    "fileFormat"
})
@XmlRootElement(name = "MX_DataFile", namespace = Namespaces.GMX)
public class DefaultDataFile extends MetadataEntity implements DataFile {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 5737775725403867273L;

    /**
     * Provides the list of feature types concerned by the transfer data file. Depending on
     * the transfer choices, a data file may contain data related to one or many feature types.
     * This attribute may be omitted when the dataset is composed of a single file and/or the
     * data does not relate to a feature catalogue.
     */
    private Collection<LocalName> featureTypes;

    /**
     * Defines the format of the transfer data file.
     */
    private Format fileFormat;

    /**
     * Constructs an initially empty data file.
     */
    public DefaultDataFile() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy, or {@code null} if none.
     */
    public DefaultDataFile(final DataFile source) {
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
    public static DefaultDataFile castOrCopy(final DataFile object) {
        return (object == null) || (object instanceof DefaultDataFile)
                ? (DefaultDataFile) object : new DefaultDataFile(object);
    }

    /**
     * Returns the list of feature types concerned by the transfer data file. Depending on
     * the transfer choices, a data file may contain data related to one or many feature types.
     * This attribute may be omitted when the dataset is composed of a single file and/or the
     * data does not relate to a feature catalogue.
     */
    @Override
    @XmlElement(name = "featureType", namespace = Namespaces.GMX)
    public synchronized Collection<LocalName> getFeatureTypes() {
        return featureTypes = nonNullCollection(featureTypes, LocalName.class);
    }

    /**
     * Sets the list of feature types concerned by the transfer data file.
     *
     * @param newValues The new feature type values.
     */
    public synchronized void setFeatureTypes(final Collection<? extends LocalName> newValues) {
        featureTypes = copyCollection(newValues, featureTypes, LocalName.class);
    }

    /**
     * Returns the format of the transfer data file.
     */
    @Override
    @XmlElement(name = "fileFormat", namespace = Namespaces.GMX, required = true)
    public synchronized Format getFileFormat() {
        return fileFormat;
    }

    /**
     * Sets the format of the transfer data file.
     *
     * @param newValue The new file format value.
     */
    public synchronized void setFileFormat(final Format newValue) {
        checkWritePermission();
        fileFormat = newValue;
    }
}
