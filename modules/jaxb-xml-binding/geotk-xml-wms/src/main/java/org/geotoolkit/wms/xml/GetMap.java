/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.wms.xml;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.geotoolkit.geometry.ImmutableEnvelope;
import org.geotoolkit.lang.Immutable;
import org.geotoolkit.util.Version;
import org.geotoolkit.util.collection.UnmodifiableArrayList;

import org.opengis.feature.type.Name;
import org.opengis.geometry.Envelope;
import org.opengis.sld.StyledLayerDescriptor;


/**
 * Representation of a {@code WMS GetMap} request, with its parameters.
 * This class is nearly immutable except the StyleLayerDescriptor which might be mutable.
 *
 * @version $Id$
 * @author Cédric Briançon (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @author Guilhem Legal (Geomatys)
 */
@Immutable
public class GetMap {
    /**
     * Envelope which contains the bounds and the crs for the request.
     */
    private final Envelope envelope;

    /**
     * Format of the request, equivalent to the mime-type of the output file.
     */
    private final String format;

    /**
     * List of layers to request.
     */
    private final List<Name> layers;

    /**
     * List of style names to apply.
     */
    private final List<String> styles;

    /**
     * Elevation to request in a nD layer. Optional.
     */
    private final Double elevation;

    /**
     * Time to request in a nD layer. It can be a period. Optional.
     */
    private final Date time;

    /**
     * Dimension of the output file, which matches with the {@code Width} and {@code Height} parameters.
     */
    private final Dimension size;

    /**
     * Background color. Optional.
     */
    private final Color background;

    /**
     * Transparent attribute. Optional.
     */
    private final Boolean transparent;

    /**
     * SLD definition to apply as a style for this layer.
     */
    private final StyledLayerDescriptor sld;

    /**
     * Azimuth, map orientation.
     */
    private final double azimuth;

    /**
     * Exceptions format. Optional.
     */
    private final String exceptions;

    private final Version version;

    /**
     * All query parameters, this might hold additional parameters that providers
     * or renderers may understand.
     */
    private final Object parameters;
    
    /**
     * Default minimal constructor to generate a {@code GetMap} request.
     */
    public GetMap(final Envelope envelope, final Version version, final String format,
                  final List<Name> layers, final Dimension size, Object parameters)
    {
        this(envelope, version, format, layers, new ArrayList<String>(), size, parameters);
    }

    /**
     * GetMap with a list of styles defined.
     */
    public GetMap(final Envelope envelope, final Version version, final String format,
                  final List<Name> layers, final List<String> styles, final Dimension size,
                  Object parameters)
    {
        this(envelope, version, format, layers, styles, null, null, size, parameters);
    }

    /**
     * GetMap with a list of styles, an elevation and a time value.
     */
    public GetMap(final Envelope envelope, final Version version, final String format,
                  final List<Name> layers, final List<String> styles, final Double elevation,
                  final Date date, final Dimension size, Object parameters)
    {
        this(envelope, version, format, layers, styles, null, elevation, date, size, null, null, 0, null, parameters);
    }

    /**
     * Constructor which contains all possible parameters in a {@code GetMap} request.
     */
    public GetMap(final Envelope envelope, final Version version, final String format,
                  final List<Name> layers, final List<String> styles,
                  final StyledLayerDescriptor sld, final Double elevation, final Date date,
                  final Dimension size, final Color background,
                  final Boolean transparent, double azimuth, final String exceptions,
                  final Object parameters) {
        this.version = version;
        this.parameters = parameters;
        this.envelope = new ImmutableEnvelope(envelope);
        this.format = format;
        this.layers = UnmodifiableArrayList.wrap(layers.toArray(new Name[layers.size()]));
        this.styles = UnmodifiableArrayList.wrap(styles.toArray(new String[styles.size()]));
        this.sld = sld;
        this.elevation = elevation;
        this.time = date;
        this.size = size;
        this.background = background;
        this.transparent = transparent;
        this.exceptions = exceptions;
        this.azimuth = azimuth % 360 ;
    }

    public GetMap(final GetMap getMap, final Boolean transparent) {
        this(   getMap.envelope,
                getMap.getVersion(),
                getMap.format,
                getMap.layers,
                getMap.styles,
                getMap.sld,
                getMap.elevation,
                getMap.time,
                getMap.size,
                getMap.background,
                transparent,
                getMap.azimuth,
                getMap.exceptions,
                getMap.parameters);
    }

    /**
     * Build a {@link GetMap} request using the parameter values found in the {@code getMap}
     * given, and replacing the {@code layers} value by an immutable singleton list containing
     * the layer specified.
     *
     * @param getMap A {@link GetMap} request.
     * @param layer  The only layer we want to keep for the {@code WMS GetMap} request.
     */
    public GetMap(final GetMap getMap, final Name layer) {
        this(   getMap.envelope,
                getMap.getVersion(),
                getMap.format,
                Collections.singletonList(layer),
                getMap.styles,
                getMap.sld,
                getMap.elevation,
                getMap.time,
                getMap.size,
                getMap.background,
                getMap.transparent,
                getMap.azimuth,
                getMap.exceptions,
                getMap.parameters);
    }

    /**
     * Build a {@link GetMap} request using the parameter values found in the {@code getMap}
     * given, and replacing the {@code layers} value by an immutable singleton list containing
     * the layer specified.
     *
     * @param getMap A {@link GetMap} request.
     * @param layers A list of layers that will be requested, instead of the ones present in the
     *               GetMap request given.
     */
    public GetMap(final GetMap getMap, final List<Name> layers) {
        this(   getMap.envelope,
                getMap.getVersion(),
                getMap.format,
                layers,
                getMap.styles,
                getMap.sld,
                getMap.elevation,
                getMap.time,
                getMap.size,
                getMap.background,
                getMap.transparent,
                getMap.azimuth,
                getMap.exceptions,
                getMap.parameters);
    }

    /**
     * Copy constructor for subclasses.
     */
    protected GetMap(final GetMap getMap) {
        this(   getMap.envelope,
                getMap.getVersion(),
                getMap.format,
                getMap.layers,
                getMap.styles,
                getMap.sld,
                getMap.elevation,
                getMap.time,
                getMap.size,
                getMap.background,
                getMap.transparent,
                getMap.azimuth,
                getMap.exceptions,
                getMap.parameters);
    }

    /**
     * Returns the background color, or {@code null} if not defined.
     */
    public Color getBackground() {
        return background;
    }

    /**
     * Returns the time to request in a nD layer, or {@code null} if not defined.
     */
    public Date getTime() {
        return time;
    }

    /**
     * Returns the elevation to request in a nD layer, or {@code null} if not defined.
     */
    public Double getElevation() {
        return elevation;
    }

    /**
     * Returns the map orientation in degree, azimuth.
     */
    public double getAzimuth(){
        return azimuth;
    }

    /**
     * Returns the envelope which contains the bounds and the crs for the request.
     * The ND envelope from the query BBOX + CRS + TIME + ELEVATION
     
    public Envelope getEnvelope() throws TransformException {
        return GO2Utilities.combine(getEnvelope2D(), new Date[]{time, time}, new Double[]{elevation, elevation});
    }*/

    /**
     * Returns the envelope which contains the bounds and the crs for the request.
     * Only the 2D envelope from the query BBOX + CRS
     */
    public Envelope getEnvelope2D(){
        return envelope;
    }

    /**
     * Returns the format of the request, equivalent to the mime-type of the output file.
     */
    public String getFormat() {
        return format;
    }

    /**
     * Returns the list of layers to request. This list may be immutable, depending on the
     * constructor chosen.
     */
    public List<Name> getLayers() {
        return layers;
    }

    /**
     * Returns the dimension of the output file, which matches with the {@code Width}
     * and {@code Height} parameters.
     */
    public Dimension getSize() {
        return size;
    }

    /**
     * Returns the SLD definition to apply as a style for this layer, or {@code null} if not defined.
     */
    public StyledLayerDescriptor getSld() {
        return sld;
    }

    /**
     * Returns the list of style names to apply, or {@code null} if not defined.
     */
    public List<String> getStyles(){
        return styles;
    }

    /**
     * Transparent attribute, or {@code null} if not defined.
     */
    public Boolean getTransparent() {
        return transparent;
    }

    /**
     * Returns the exception format specified, or {@code MimeType.APP_SE_XML}
     * if {@code null}.
     */
    public String getExceptionFormat() {
        if (exceptions != null) {
            return exceptions;
        }
        return (version.toString().equals("1.1.1")) ?
            "application/vnd.ogc.se_xml" : "text/xml";
    }

    /**
     * {@inheritDoc}
     */
    public final String getService() {
        return "WMS";
    }

    /**
     * {@inheritDoc}
     */
    public final Version getVersion() {
        return version;
    }

    /**
     * @return the parameters
     */
    public Object getParameters() {
        return parameters;
    }


}
