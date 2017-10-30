/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Geomatys
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

package org.geotoolkit.gml;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import java.io.Reader;
import java.io.StringReader;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.geotoolkit.gml.xml.*;

import org.opengis.util.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;

/**
 *
 * @author Johann sorel (Geomatys)
 * @module
 */
public class GeometrytoJTS {

    private static final GeometryFactory GF = new GeometryFactory();

    private GeometrytoJTS(){}

    /**
     * Unmarshall given GML String and transform it in a JTS geometry.
     *
     * @param gmlString
     * @return
     * @throws JAXBException
     * @throws FactoryException
     */
    public static Geometry toJTS(String gmlString) throws JAXBException, FactoryException{
        final Reader reader = new StringReader(gmlString);

        final Geometry geom;
        final Unmarshaller unmarshaller = GMLMarshallerPool.getInstance().acquireUnmarshaller();
        Object jax = unmarshaller.unmarshal(reader);
        GMLMarshallerPool.getInstance().recycle(unmarshaller);

        if(jax instanceof JAXBElement){
            jax = ((JAXBElement)jax).getValue();
        }

        if(jax instanceof AbstractGeometry){
            return  GeometrytoJTS.toJTS((AbstractGeometry)jax);
        }

        throw new JAXBException("Object is not a valid GML "+jax);
    }

    public static Geometry toJTS(final AbstractGeometry gml)
            throws NoSuchAuthorityCodeException, FactoryException {
        return toJTS(gml, true);
    }

    public static Geometry toJTS(final AbstractGeometry gml, boolean longitudeFirst)
            throws NoSuchAuthorityCodeException, FactoryException{

        GeometryTransformer gt = new GeometryTransformer(gml);
        gt.setLongitudeFirst(longitudeFirst);

        return gt.get();
    }
}
