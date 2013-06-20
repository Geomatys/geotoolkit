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
package org.geotoolkit.metadata.iso.lineage;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import net.jcip.annotations.ThreadSafe;

import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.lineage.Algorithm;
import org.opengis.util.InternationalString;

import org.geotoolkit.metadata.iso.MetadataEntity;
import org.geotoolkit.xml.Namespaces;


/**
 * Details of the methodology by which geographic information was derived from the instrument
 * readings.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Guilhem Legal (Geomatys)
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
@XmlType(name = "LE_Algorithm_Type", propOrder={
    "citation",
    "description"
})
@XmlRootElement(name = "LE_Algorithm", namespace = Namespaces.GMI)
public class DefaultAlgorithm extends MetadataEntity implements Algorithm {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 6343760610092069341L;

    /**
     * Information identifying the algorithm and version or date.
     */
    private Citation citation;

    /**
     * Information describing the algorithm used to generate the data.
     */
    private InternationalString description;

    /**
     * Constructs an initially empty algorithm.
     */
    public DefaultAlgorithm() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy, or {@code null} if none.
     */
    public DefaultAlgorithm(final Algorithm source) {
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
    public static DefaultAlgorithm castOrCopy(final Algorithm object) {
        return (object == null) || (object instanceof DefaultAlgorithm)
                ? (DefaultAlgorithm) object : new DefaultAlgorithm(object);
    }

    /**
     * Returns the information identifying the algorithm and version or date.
     */
    @Override
    @XmlElement(name = "citation", namespace = Namespaces.GMI, required = true)
    public synchronized Citation getCitation() {
        return citation;
    }

    /**
     * Sets the information identifying the algorithm and version or date.
     *
     * @param newValue The new citation value.
     */
    public synchronized void setCitation(final Citation newValue) {
        checkWritePermission();
        citation = newValue;
    }

    /**
     * Returns the information describing the algorithm used to generate the data.
     */
    @Override
    @XmlElement(name = "description", namespace = Namespaces.GMI, required = true)
    public synchronized InternationalString getDescription() {
        return description;
    }

    /**
     * Sets the information describing the algorithm used to generate the data.
     *
     * @param newValue The new description value.
     */
    public synchronized void setDescription(final InternationalString newValue) {
        checkWritePermission();
        description = newValue;
    }
}
