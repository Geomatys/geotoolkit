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
package org.geotoolkit.sld.xml;

import org.geotoolkit.ows.xml.RequestBase;
import org.geotoolkit.sld.xml.Specification.StyledLayerDescriptor;
import org.apache.sis.util.Version;

import org.opengis.util.GenericName;


/**
 * Representation of a {@code WMS GetLegendGraphic} request, with its parameters.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @author Guilhem Legal (Geomatys)
 */
public final class GetLegendGraphic implements RequestBase {
    /**
     * Layer to consider.
     */
    private final GenericName layer;

    /**
     * Format of the legend file returned.
     */
    private final String format;

    /**
     * Width of the generated legend image. Optional.
     */
    private final Integer width;

    /**
     * Height of the generated legend image. Optional.
     */
    private final Integer height;

    /**
     * Style to apply for the legend output. Optional.
     */
    private final String style;

    /**
     * SLD to apply on the legend output. Optional.
     */
    private final String sld;

    /**
     * Version of the SLD.
     */
    private final StyledLayerDescriptor sldVersion;

    /**
     * Rule from SLD to apply for styling the legend output.
     */
    private final String rule;

    /**
     * Scale for the style.
     */
    private final Double scale;

    private Version version;

    private String service;

    /**
     * Builds a {@code GetLegendGraphic} request, using the layer and mime-type specified
     * and width and height for the image.
     */
    public GetLegendGraphic(final GenericName layer, final String format, final Integer width,
                            final Integer height, final String style, final String sld,
                            final StyledLayerDescriptor sldVersion, final String rule,
                            final Double scale, final Version version)
    {
        this.version    = version;
        this.layer      = layer;
        this.format     = format;
        this.width      = width;
        this.height     = height;
        this.style      = style;
        this.sld        = sld;
        this.sldVersion = sldVersion;
        this.rule       = rule;
        this.scale      = scale;
    }

    /**
     * Returns the layer to consider for this request.
     */
    public GenericName getLayer() {
        return layer;
    }

    /**
     * Returns the format for the legend file.
     */
    public String getFormat() {
        return format;
    }

    /**
     * Returns the width of the legend image.
     */
    public Integer getWidth() {
        return width;
    }

    /**
     * Returns the height of the legend image.
     */
    public Integer getHeight() {
        return height;
    }

    /**
     * {@inheritDoc}
     */
    public String getExceptionFormat() {
        return "application/vnd.ogc.se_xml";
    }

    /**
     * Returns the scale for the rule that can be applied.
     */
    public Double getScale() {
        return scale;
    }

    /**
     * Returns the sld value.
     */
    public String getSld() {
        return sld;
    }

    /**
     * Returns the style for this legend.
     */
    public String getStyle() {
        return style;
    }

    /**
     * Returns the SLD version for the given SLD file. Note that this parameter is
     * mandatory when {@link #sld} is specified.
     */
    public StyledLayerDescriptor getSldVersion() {
        return sldVersion;
    }

    /**
     * Returns the rule to apply from the SLD file.
     */
    public String getRule() {
        return rule;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getService() {
        if (service == null) {
            return "WMS";
        }
        return service;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Version getVersion() {
        return version;
    }

    @Override
    public void setService(final String value) {
        this.service = value;
    }

    @Override
    public void setVersion(final String version) {
        if (version != null) {
            this.version = new Version(version);
        } else {
            this.version = null;
        }
    }
}
