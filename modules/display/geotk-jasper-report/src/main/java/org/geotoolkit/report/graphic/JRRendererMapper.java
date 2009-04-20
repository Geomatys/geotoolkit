/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
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
 */

package org.geotoolkit.report.graphic;

import java.util.Collection;
import net.sf.jasperreports.engine.JRRenderable;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.report.JRMapper;
import org.geotoolkit.report.JRMapperFactory;

/**
 * Abstract JRMapper for JRRenderable values and MapContext records.
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class JRRendererMapper implements JRMapper<JRRenderable,MapContext> {

    public static String MAP_START_DATE = "jasper_context_date";

    private final JRMapperFactory<JRRenderable,MapContext> factory;

    private MapContext candidate;

    protected JRRendererMapper(JRMapperFactory<JRRenderable,MapContext> factory){
        this.factory = factory;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setCandidate(final MapContext candidate) {
        this.candidate = candidate;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public JRRenderable getValue(Collection renderedValues) {
        return create(candidate,renderedValues);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public JRMapperFactory<JRRenderable,MapContext> getFactory() {
        return factory;
    }

    protected abstract JRRenderable create(MapContext candidate, Collection<Object> renderedValues);

}
