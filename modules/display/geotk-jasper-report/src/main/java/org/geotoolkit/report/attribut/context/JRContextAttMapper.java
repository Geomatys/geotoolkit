/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.report.attribut.context;

import java.util.Collection;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.report.JRMapper;
import org.geotoolkit.report.JRMapperFactory;

/**
 * Abstract JRMapper for String values and MapContext records.
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class JRContextAttMapper implements JRMapper<String,MapContext> {

    private final JRMapperFactory<String,MapContext> factory;

    private MapContext candidate;

    protected JRContextAttMapper(JRMapperFactory<String,MapContext> factory){
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
    public String getValue(Collection renderedValues) {
        return create(candidate,renderedValues);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public JRMapperFactory<String,MapContext> getFactory() {
        return factory;
    }

    protected abstract String create(MapContext candidate, Collection<Object> renderedValues);

}
