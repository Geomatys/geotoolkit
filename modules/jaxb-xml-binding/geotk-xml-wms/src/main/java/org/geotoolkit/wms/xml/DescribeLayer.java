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

import java.util.List;
import net.jcip.annotations.Immutable;
import org.geotoolkit.ows.xml.RequestBase;

import org.geotoolkit.util.Version;
import org.apache.sis.util.collection.UnmodifiableArrayList;


/**
 * Representation of a {@code WMS DescribeLayer} request, with its parameters.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @author Guilhem Legal (Geomatys)
 */
@Immutable
public final class DescribeLayer implements RequestBase {
    /**
     * List of layers to request.
     */
    private final UnmodifiableArrayList<String> layers;

    private Version version;
    
    private String service;

    /**
     * Builds a {@code DescribeLayer} request, using the layer and mime-type specified.
     */
    public DescribeLayer(final List<String> layers, final Version version) {
        this.version = version;
        this.layers = UnmodifiableArrayList.wrap(layers.toArray(new String[layers.size()]));
    }

    /**
     * {@inheritDoc}
     */
    public String getExceptionFormat() {
        return "application/vnd.ogc.se_xml";
    }

    /**
     * Returns an immutable list of layers.
     */
    public List<String> getLayers() {
        return layers;
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
