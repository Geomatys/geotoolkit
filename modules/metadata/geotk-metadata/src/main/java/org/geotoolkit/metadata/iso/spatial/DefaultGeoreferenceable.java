/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.opengis.metadata.spatial.GeolocationInformation;
import org.opengis.util.Record;
import org.opengis.util.InternationalString;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.spatial.Dimension;
import org.opengis.metadata.spatial.CellGeometry;
import org.opengis.metadata.spatial.Georeferenceable;


/**
 * Grid with cells irregularly spaced in any given geographic/map projection coordinate
 * system, whose individual cells can be geolocated using geolocation information
 * supplied with the data but cannot be geolocated from the grid properties alone.
 *
 * @author Martin Desruisseaux (IRD)
 * @author Touraïvane (IRD)
 * @author Cédric Briançon (Geomatys)
 * @version 3.01
 *
 * @since 2.1
 * @module
 */
@XmlType(name = "MD_Georeferenceable", propOrder={
    "controlPointAvailable",
    "orientationParameterAvailable",
    "orientationParameterDescription",
    "parameterCitations",
    "geolocationInformation"
})
@XmlRootElement(name = "MD_Georeferenceable")
public class DefaultGeoreferenceable extends DefaultGridSpatialRepresentation implements Georeferenceable {
    /**
     * Serial number for interoperability with different versions.
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
     * @param source The metadata to copy.
     *
     * @since 2.4
     */
    public DefaultGeoreferenceable(final Georeferenceable source) {
        super(source);
    }

    /**
     * Creates a georeferencable initialized to the given parameters.
     *
     * @param numberOfDimensions The number of independent spatial-temporal axes.
     * @param axisDimensionsProperties Information about spatial-temporal axis properties.
     * @param cellGeometry Identification of grid data as point or cell.
     * @param transformationParameterAvailable Indication of whether or not parameters for
     *          transformation exists.
     * @param controlPointAvailable An indication of whether or not control point(s) exists.
     * @param orientationParameterAvailable An indication of whether or not orientation parameters
     *          are available.
     */
    public DefaultGeoreferenceable(final int numberOfDimensions,
                                final List<? extends Dimension> axisDimensionsProperties,
                                final CellGeometry cellGeometry,
                                final boolean transformationParameterAvailable,
                                final boolean controlPointAvailable,
                                final boolean orientationParameterAvailable)
    {
        super(numberOfDimensions,
              axisDimensionsProperties,
              cellGeometry,
              transformationParameterAvailable);
        setControlPointAvailable(controlPointAvailable);
        setOrientationParameterAvailable(orientationParameterAvailable);
    }

    /**
     * Returns an indication of whether or not control point(s) exists.
     */
    @Override
    @XmlElement(name = "controlPointAvailability", required = true)
    public boolean isControlPointAvailable() {
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
    public boolean isOrientationParameterAvailable() {
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
    public InternationalString getOrientationParameterDescription() {
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
    public Record getGeoreferencedParameters() {
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
        return xmlOptional(parameterCitations = nonNullCollection(parameterCitations, Citation.class));
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
     */
    @Override
    @XmlElement(name = "geolocationInformation")
    public synchronized Collection<GeolocationInformation> getGeolocationInformation() {
        return geolocationInformation = nonNullCollection(geolocationInformation,
                                                          GeolocationInformation.class);
    }

    /**
     * Sets the information that can be used to geolocate the data.
     *
     * @param newValues The new geolocation information values.
     */
    public synchronized void setGeolocationInformation(
            final Collection<? extends GeolocationInformation> newValues)
    {
        geolocationInformation = copyCollection(newValues, geolocationInformation,
                                                GeolocationInformation.class);
    }

    /**
     * Sets the {@code xmlMarshalling} flag to {@code true}, since the marshalling
     * process is going to be done. This method is automatically called by JAXB
     * when the marshalling begins.
     *
     * @param marshaller Not used in this implementation.
     */
    @SuppressWarnings("unused")
    private void beforeMarshal(Marshaller marshaller) {
        xmlMarshalling(true);
    }

    /**
     * Sets the {@code xmlMarshalling} flag to {@code false}, since the marshalling
     * process is finished. This method is automatically called by JAXB when the
     * marshalling ends.
     *
     * @param marshaller Not used in this implementation
     */
    @SuppressWarnings("unused")
    private void afterMarshal(Marshaller marshaller) {
        xmlMarshalling(false);
    }
}
