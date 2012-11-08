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
package org.geotoolkit.metadata.iso.spatial;

import java.util.List;
import java.util.Collection;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import net.jcip.annotations.ThreadSafe;

import org.opengis.util.Record;
import org.opengis.util.InternationalString;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.spatial.Dimension;
import org.opengis.metadata.spatial.CellGeometry;
import org.opengis.metadata.spatial.Georeferenceable;
import org.opengis.metadata.spatial.GeolocationInformation;

import org.geotoolkit.xml.Namespaces;


/**
 * Grid with cells irregularly spaced in any given geographic/map projection coordinate
 * system, whose individual cells can be geolocated using geolocation information
 * supplied with the data but cannot be geolocated from the grid properties alone.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Touraïvane (IRD)
 * @author Cédric Briançon (Geomatys)
 * @version 3.19
 *
 * @since 2.1
 * @module
 */
@ThreadSafe
@XmlType(name = "MD_Georeferenceable_Type", propOrder={
    "controlPointAvailable",
    "orientationParameterAvailable",
    "orientationParameterDescription",
    "parameterCitations",
    "geolocationInformation"
})
@XmlRootElement(name = "MD_Georeferenceable")
@XmlSeeAlso(org.geotoolkit.internal.jaxb.gmi.MI_Georeferenceable.class)
public class DefaultGeoreferenceable extends DefaultGridSpatialRepresentation implements Georeferenceable {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 7369639367164358759L;

    /**
     * Indication of whether or not control point(s) exists.
     */
    private boolean controlPointAvailable;

    /**
     * Indication of whether or not orientation parameters are available.
     */
    private boolean orientationParameterAvailable;

    /**
     * Description of parameters used to describe sensor orientation.
     */
    private InternationalString orientationParameterDescription;

    /**
     * Terms which support grid data georeferencing.
     */
    private Record georeferencedParameters;

    /**
     * Reference providing description of the parameters.
     */
    private Collection<Citation> parameterCitations;

    /**
     * Information that can be used to geolocate the data.
     */
    private Collection<GeolocationInformation> geolocationInformation;

    /**
     * Constructs an initially empty georeferenceable.
     */
    public DefaultGeoreferenceable() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy, or {@code null} if none.
     *
     * @since 2.4
     */
    public DefaultGeoreferenceable(final Georeferenceable source) {
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
    public static DefaultGeoreferenceable castOrCopy(final Georeferenceable object) {
        return (object == null) || (object instanceof DefaultGeoreferenceable)
                ? (DefaultGeoreferenceable) object : new DefaultGeoreferenceable(object);
    }

    /**
     * Returns an indication of whether or not control point(s) exists.
     */
    @Override
    @XmlElement(name = "controlPointAvailability", required = true)
    public synchronized boolean isControlPointAvailable() {
        return controlPointAvailable;
    }

    /**
     * Sets an indication of whether or not control point(s) exists.
     *
     * @param newValue {@code true} if control points are available.
     */
    public synchronized void setControlPointAvailable(final boolean newValue) {
       checkWritePermission();
       controlPointAvailable = newValue;
    }

    /**
     * Returns an indication of whether or not orientation parameters are available.
     */
    @Override
    @XmlElement(name = "orientationParameterAvailability", required = true)
    public synchronized boolean isOrientationParameterAvailable() {
        return orientationParameterAvailable;
    }

    /**
     * Sets an indication of whether or not orientation parameters are available.
     *
     * @param newValue {@code true} if orientation parameter are available.
     */
    public synchronized void setOrientationParameterAvailable(final boolean newValue) {
        checkWritePermission();
        orientationParameterAvailable = newValue;
    }

    /**
     * Returns a description of parameters used to describe sensor orientation.
     */
    @Override
    @XmlElement(name = "orientationParameterDescription")
    public synchronized InternationalString getOrientationParameterDescription() {
        return orientationParameterDescription;
    }

    /**
     * Sets a description of parameters used to describe sensor orientation.
     *
     * @param newValue The new orientation parameter description.
     */
    public synchronized void setOrientationParameterDescription(final InternationalString newValue) {
        checkWritePermission();
        orientationParameterDescription = newValue;
    }

    /**
     * Returns the terms which support grid data georeferencing.
     *
     * @since 2.4
     */
    @Override
/// @XmlElement(name = "georeferencedParameters", required = true)
    public synchronized Record getGeoreferencedParameters() {
        return georeferencedParameters;
    }

    /**
     * Sets the terms which support grid data georeferencing.
     *
     * @param newValue The new georeferenced parameters.
     *
     * @since 2.4
     */
    public synchronized void setGeoreferencedParameters(final Record newValue) {
        checkWritePermission();
        georeferencedParameters = newValue;
    }

    /**
     * Returns a reference providing description of the parameters.
     */
    @Override
    @XmlElement(name = "parameterCitation")
    public synchronized Collection<Citation> getParameterCitations() {
        return parameterCitations = nonNullCollection(parameterCitations, Citation.class);
    }

    /**
     * Sets a reference providing description of the parameters.
     *
     * @param newValues The new parameter citations.
     */
    public synchronized void setParameterCitations(final Collection<? extends Citation> newValues) {
        parameterCitations = copyCollection(newValues, parameterCitations, Citation.class);
    }

    /**
     * Returns the information that can be used to geolocate the data.
     *
     * @since 3.03
     *
     * @todo This attribute is declared as mandatory in ISO 19115-2. However metadata compliant
     *       with ISO 19115 (without the -2 part) do not contains this attribute. How should we
     *       handle the XML formatting for this one?
     */
    @Override
    @XmlElement(name = "geolocationInformation", namespace = Namespaces.GMI, required = true)
    public synchronized Collection<GeolocationInformation> getGeolocationInformation() {
        return geolocationInformation = nonNullCollection(geolocationInformation, GeolocationInformation.class);
    }

    /**
     * Sets the information that can be used to geolocate the data.
     *
     * @param newValues The new geolocation information values.
     *
     * @since 3.03
     */
    public synchronized void setGeolocationInformation(final Collection<? extends GeolocationInformation> newValues) {
        geolocationInformation = copyCollection(newValues, geolocationInformation, GeolocationInformation.class);
    }
}
