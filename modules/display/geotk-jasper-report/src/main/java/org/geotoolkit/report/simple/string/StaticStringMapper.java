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

package org.geotoolkit.report.simple.string;

import java.awt.Component;
import java.util.Collection;
import org.geotoolkit.report.JRMapper;
import org.geotoolkit.report.JRMapperFactory;

/**
 * Static String mapper for fields of type String.
 *
 * @author Johann sorel (Geomatys)
 */
public class StaticStringMapper implements JRMapper<String,Object> {

    private String value = "";
    private final JRMapperFactory<String,Object> factory;

    StaticStringMapper(JRMapperFactory<String,Object> factory){
        this("",factory);
    }

    StaticStringMapper(final String value,JRMapperFactory<String,Object> factory){
        this.value = value;
        this.factory = factory;
    }

    public void setValue(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setCandidate(Object candidate) {
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Component getComponent() {
        return new JStaticStringEditor(this);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getValue(Collection renderedValues) {
        return value;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public JRMapperFactory<String,Object> getFactory() {
        return factory;
    }

}
