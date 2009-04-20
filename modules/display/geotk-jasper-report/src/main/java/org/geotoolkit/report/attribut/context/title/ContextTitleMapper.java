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

package org.geotoolkit.report.attribut.context.title;

import java.awt.Component;
import java.util.Collection;

import org.geotoolkit.map.MapContext;
import org.geotoolkit.report.JRMapperFactory;
import org.geotoolkit.report.attribut.context.JRContextAttMapper;

/**
 * Mapper to extract the map context title value.
 *
 * @author Johann Sorel (Geomatys)
 */
public class ContextTitleMapper extends JRContextAttMapper{

    ContextTitleMapper(JRMapperFactory<String,MapContext> factory){
        super(factory);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String create(MapContext candidate, Collection<Object> renderedValues) {
        return candidate.getDescription().getTitle().toString();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Component getComponent() {
        return null;
    }

}
