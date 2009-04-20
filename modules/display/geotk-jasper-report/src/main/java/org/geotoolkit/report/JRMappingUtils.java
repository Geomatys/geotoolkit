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
package org.geotoolkit.report;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.geotoolkit.report.JRMapperFactory;

/**
 * Constants values for reports.
 *
 * @author Johann Sorel (Geomatys)
 */
public class JRMappingUtils {

    private static final ServiceLoader<JRMapperFactory> FACTORIES = ServiceLoader.load(JRMapperFactory.class);

    public static List<JRMapperFactory> getFactories(Class type){
        final List<JRMapperFactory> factories = new ArrayList<JRMapperFactory>();

        for(final JRMapperFactory factory : FACTORIES){
            if(factory.getFieldClass().equals(type)) factories.add(factory);
        }
        return factories;
    }

}
