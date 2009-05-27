/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.filter.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * JaxB adapter to marshall String to internationalString.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class ObjectAdapter extends XmlAdapter<String,Object>{

    /**
     * {@inheritDoc }
     */
    @Override
    public Object unmarshal(String str) throws Exception {
        return str;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String marshal(Object inter) throws Exception {
        return inter.toString();
    }

}
