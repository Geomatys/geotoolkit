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
import java.util.Date;
import java.util.List;
import net.jcip.annotations.Immutable;

import org.geotoolkit.util.Version;
import org.geotoolkit.util.collection.UnmodifiableArrayList;

import org.opengis.feature.type.Name;
import org.opengis.geometry.Envelope;
import org.opengis.sld.StyledLayerDescriptor;


/**
 * Representation of a {@code WMS GetFeatureInfo} request, with its parameters. It
 * is an extension of the {@link GetMap} request.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @author Guilhem Legal (Geomatys)
 *
 * @see GetMap
 */
@Immutable
public final class GetFeatureInfo extends GetMap {
    /**
     * X coordinate to request.
     */
    private final int x;

    /**
     * Y coordinate to request.
     */
    private final int y;

    /**
     * Layers to request.
     */
    private final UnmodifiableArrayList<Name> queryLayers;

    /**
     * Format of the returned information.
     */
    private final String infoFormat;

    /**
     * Number of maximal features that the request has to handle. Optional.
     */
    private final Integer featureCount;

    public GetFeatureInfo(final GetMap getMap, final int x, final int y,
                          final List<Name> queryLayers, final String infoFormat,
                          final Integer featureCount)
    {
        super(getMap);
        this.x = x;
        this.y = y;
        this.queryLayers = UnmodifiableArrayList.wrap(queryLayers.toArray(new Name[queryLayers.size()]));
        this.infoFormat  = infoFormat;
        this.featureCount = featureCount;
    }

    public GetFeatureInfo(final Envelope envelope, final Version version,
                  final String format, final List<Name> layers, final List<String> styles,
                  final StyledLayerDescriptor sld, final Double elevation, final Date date,
                  final Dimension size, final Color background,
                  final Boolean transparent, final String exceptions, final int x, final int y,
                  final List<Name> queryLayers, final String infoFormat, final Integer featureCount,
                  final Object parameters)
    {
        super(envelope, version, format, layers, styles, sld, elevation, date, size,
                background, transparent, 0,exceptions, parameters);
        this.x = x;
        this.y = y;
        this.queryLayers = UnmodifiableArrayList.wrap(queryLayers.toArray(new Name[queryLayers.size()]));
        this.infoFormat  = infoFormat;
        this.featureCount = featureCount;
    }

    /**
     * Returns the X coordinate to request value.
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the Y coordinate to request value.
     */
    public int getY() {
        return y;
    }

    /**
     * Returns an immutable list of layers to request.
     */
    public List<Name> getQueryLayers() {
        return queryLayers;
    }

    /**
     * Returns the format of the information to returned.
     */
    public String getInfoFormat() {
        return infoFormat;
    }

    /**
     * Returns the number of features to request.
     */
    public Integer getFeatureCount() {
        return featureCount;
    }
}
