/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.gml;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.geotoolkit.gml.xml.v311.CurveType;
import org.geotoolkit.gml.xml.v311.LineStringType;
import org.geotoolkit.gml.xml.v311.MultiCurveType;
import org.geotoolkit.gml.xml.v311.MultiGeometryType;
import org.geotoolkit.gml.xml.v311.MultiLineStringType;
import org.geotoolkit.gml.xml.v311.MultiPointType;
import org.geotoolkit.gml.xml.v311.MultiPolygonType;
import org.geotoolkit.gml.xml.v311.MultiSolidType;
import org.geotoolkit.gml.xml.v311.MultiSurfaceType;
import org.geotoolkit.gml.xml.v311.ObjectFactory;
import org.geotoolkit.gml.xml.v311.PointType;

import org.geotoolkit.internal.jaxb.RegisterableAdapter;
import org.geotoolkit.internal.jaxb.geometry.GM_Object;
import org.opengis.geometry.Geometry;


/**
 * JAXB adapter in order to map implementing class with the GeoAPI interface. See
 * package documentation for more information about JAXB and interface.
 *
 * @author Guilhem Legal (Geomatys)
 * @version 3.12
 *
 * @since 3.00
 * @module
 */
public final class GmlGeometryAdapter extends GM_Object implements RegisterableAdapter {

    /**
     * Empty constructor for JAXB only.
     */
    public GmlGeometryAdapter() {
    }

    /**
     * Wraps a Reference System value with a {@code MD_ReferenceSystem} element at marshalling-time.
     *
     * @param metadata The metadata value to marshall.
     */
    protected GmlGeometryAdapter(final Geometry metadata) {
        ObjectFactory factory = new ObjectFactory();
        if (metadata instanceof PointType) {
            this.geometry = factory.createPoint((PointType) metadata);

        } else if (metadata instanceof MultiSurfaceType) {
            this.geometry = factory.createMultiSurface((MultiSurfaceType) metadata);

        } else if (metadata instanceof CurveType) {
            this.geometry = factory.createCurve((CurveType) metadata);

        } else if (metadata instanceof LineStringType) {
            this.geometry = factory.createLineString((LineStringType) metadata);

        } else if (metadata instanceof MultiLineStringType) {
            this.geometry = factory.createMultiLineString((MultiLineStringType) metadata);

        } else if (metadata instanceof MultiPointType) {
            this.geometry = factory.createMultiPoint((MultiPointType) metadata);


        } else if (metadata instanceof MultiCurveType) {
            this.geometry = factory.createMultiCurve((MultiCurveType) metadata);


        } else if (metadata instanceof MultiGeometryType) {
            this.geometry = factory.createMultiGeometry((MultiGeometryType) metadata);


        } else if (metadata instanceof MultiSolidType) {
            this.geometry = factory.createMultiSolid((MultiSolidType) metadata);

        } else if (metadata instanceof MultiPolygonType) {
            this.geometry = factory.createMultiPolygon((MultiPolygonType) metadata);
        }
    }

    /**
     * Invoked when a new adapter is created by {@link org.geotoolkit.xml.MarshallerPool}.
     *
     * @param marshaller The marshaller to be configured.
     */
    @Override
    public void register(final Marshaller marshaller) {
        marshaller.setAdapter(GM_Object.class, this);
    }

    /**
     * Invoked when a new adapter is created by {@link org.geotoolkit.xml.MarshallerPool}.
     *
     * @param unmarshaller The marshaller to be configured.
     */
    @Override
    public void register(final Unmarshaller unmarshaller) {
        unmarshaller.setAdapter(GM_Object.class, this);
    }

    /**
     * Returns the Reference System value covered by a {@code MD_ReferenceSystem} element.
     *
     * @param value The value to marshall.
     * @return The adapter which covers the metadata value.
     */
    @Override
    protected GM_Object wrap(final Geometry value) {
        return new GmlGeometryAdapter(value);
    }

  
}
