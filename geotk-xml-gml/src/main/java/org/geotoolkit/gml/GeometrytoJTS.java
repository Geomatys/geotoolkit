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

import org.locationtech.jts.geom.Geometry;
import java.io.Reader;
import java.io.StringReader;
import java.util.logging.Level;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import org.geotoolkit.gml.xml.*;
import static org.geotoolkit.internal.sql.DefaultDataSource.LOGGER;

import org.opengis.util.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;

/**
 *
 * @author Johann sorel (Geomatys)
 * @module
 */
public class GeometrytoJTS {

    private GeometrytoJTS() {
    }

    /**
     * Unmarshall given GML String and transform it in a JTS geometry.
     *
     * @param gmlString
     * @return
     * @throws JAXBException
     * @throws FactoryException
     */
    public static Geometry toJTS(String gmlString) throws JAXBException, FactoryException {
        final Reader reader = new StringReader(gmlString);
        final Unmarshaller unmarshaller = GMLMarshallerPool.getInstance().acquireUnmarshaller();
        Object jax = unmarshaller.unmarshal(reader);
        GMLMarshallerPool.getInstance().recycle(unmarshaller);

        if (jax instanceof JAXBElement) {
            jax = ((JAXBElement) jax).getValue();
        }

        if (jax instanceof AbstractGeometry) {
            return GeometrytoJTS.toJTS((AbstractGeometry) jax);
        }

        throw new JAXBException("Object is not a valid GML " + jax);
    }

    public static Geometry toJTS(final AbstractGeometry gml)
            throws NoSuchAuthorityCodeException, FactoryException {
        return toJTS(false, gml);
    }


    /**
     *Constructor with Default AxisResolve Strategy.
     *
     * (Note : input forceMultiPolygon was set in non instinctive first position
     * to differentiate this method from a {@linkplain #toJTS(org.geotoolkit.gml.xml.AbstractGeometry, boolean) deprecated one})

     * @param forceMultiPolygon
     * @param gml
     * @return
     */
    public static Geometry toJTS(final boolean forceMultiPolygon, final AbstractGeometry gml ) {

        GeometryTransformer gt = new GeometryTransformer(gml);
        gt.setForceMultiPolygon(forceMultiPolygon);

        return gt.get();

    }


    public static Geometry toJTS(final AbstractGeometry gml, final AxisResolve axisResolve)
            throws NoSuchAuthorityCodeException, FactoryException {
        return toJTS(gml, axisResolve, false);
    }

    /**
     * Use this constructor if you really want to manage the AxisResolve strategy.
     * Else use {@linkplain
     *
     * @param gml
     * @param axisResolve
     * @param forceMultiPolygon
     * @return
     * @throws NoSuchAuthorityCodeException
     * @throws FactoryException
     */
    public static Geometry toJTS(final AbstractGeometry gml, final AxisResolve axisResolve, boolean forceMultiPolygon)
            throws NoSuchAuthorityCodeException, FactoryException {

        GeometryTransformer gt = new GeometryTransformer(gml);
        gt.setAxisResolve(axisResolve);
        gt.setForceMultiPolygon(forceMultiPolygon);

        return gt.get();
    }

    /**
     * Deprecated, use {@linkplain #toJTS(org.geotoolkit.gml.xml.AbstractGeometry, org.geotoolkit.gml.AxisResolve) toJTS}
     * insteed.
     *
     * @param gml
     * @param longitudeFirst
     * @return
     * @throws NoSuchAuthorityCodeException
     * @throws FactoryException
     * @deprecated
     */
    @Deprecated
    public static Geometry toJTS(final AbstractGeometry gml, boolean longitudeFirst)
            throws NoSuchAuthorityCodeException, FactoryException {
        return toJTS(gml, longitudeFirst, false);
    }

    /**
     * Deprecated, use {@linkplain #toJTS(org.geotoolkit.gml.xml.AbstractGeometry, org.geotoolkit.gml.AxisResolve, boolean)  toJTS}
     * insteed.
     *
     * @param gml
     * @param longitudefirst
     * @param forceMultiPolygon
     * @return
     * @throws NoSuchAuthorityCodeException
     * @throws FactoryException
     * @deprecated
     */
    @Deprecated
    public static Geometry toJTS(final AbstractGeometry gml, final boolean longitudefirst, boolean forceMultiPolygon)
            throws NoSuchAuthorityCodeException, FactoryException {
        return toJTS(gml, (longitudefirst)? AxisResolve.RIGHT_HANDED: AxisResolve.STRICT,forceMultiPolygon);
    }

}
