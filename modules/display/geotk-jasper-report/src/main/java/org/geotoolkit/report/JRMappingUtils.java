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
package org.geotoolkit.report;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ServiceLoader;
import java.util.logging.Logger;

import org.geotoolkit.lang.Static;
import org.geotoolkit.util.logging.Logging;

/**
 * Constants values for reports.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
@Static
public final class JRMappingUtils {

    public static final Logger LOGGER = Logging.getLogger("org.geotoolkit.report");

    private static final ServiceLoader<JRMapperFactory> FACTORIES = ServiceLoader.load(JRMapperFactory.class);
    private static final ServiceLoader<JRFieldRenderer> RENDERERS = ServiceLoader.load(JRFieldRenderer.class);

    private JRMappingUtils(){}

    public static Collection<JRFieldRenderer> getFieldRenderers(){
        final List<JRFieldRenderer> renderers = new ArrayList<JRFieldRenderer>();
        for(final JRFieldRenderer r : RENDERERS){
            renderers.add(r);
        }
        return renderers;
    }

    public static List<JRMapperFactory> getFactories(Class type){
        final List<JRMapperFactory> factories = new ArrayList<JRMapperFactory>();

        for(final JRMapperFactory factory : FACTORIES){
            if(factory.getFieldClass().equals(type)) factories.add(factory);
        }
        return factories;
    }

}
