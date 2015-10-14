/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.geotoolkit.gml.xml.v311.*;

import org.apache.sis.internal.jaxb.geometry.GM_Object;
import org.apache.sis.util.logging.Logging;
import org.opengis.geometry.Geometry;
import org.apache.sis.internal.jaxb.AdapterReplacement;


/**
 * JAXB adapter in order to map implementing class with the Types interface. See
 * package documentation for more information about JAXB and interface.
 *
 * @author Guilhem Legal (Geomatys)
 * @version 3.12
 *
 * @since 3.00
 * @module
 */
public final class GmlGeometryAdapter extends GM_Object implements AdapterReplacement {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.gml");

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
        final ObjectFactory factory = new ObjectFactory();
        final org.geotoolkit.gml.xml.v321.ObjectFactory factory321 = new org.geotoolkit.gml.xml.v321.ObjectFactory();

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

        } else if (metadata instanceof PolygonType) {
            this.geometry = factory.createPolygon((PolygonType) metadata);

        } else if (metadata instanceof SurfaceType) {
            this.geometry = factory.createSurface((SurfaceType) metadata);

        } else if (metadata instanceof org.geotoolkit.gml.xml.v321.PointType) {
            this.geometry = factory321.createPoint((org.geotoolkit.gml.xml.v321.PointType) metadata);

        } else if (metadata instanceof org.geotoolkit.gml.xml.v321.MultiSurfaceType) {
            this.geometry = factory321.createMultiSurface((org.geotoolkit.gml.xml.v321.MultiSurfaceType) metadata);

        } else if (metadata instanceof org.geotoolkit.gml.xml.v321.CurveType) {
            this.geometry = factory321.createCurve((org.geotoolkit.gml.xml.v321.CurveType) metadata);

        } else if (metadata instanceof org.geotoolkit.gml.xml.v321.LineStringType) {
            this.geometry = factory321.createLineString((org.geotoolkit.gml.xml.v321.LineStringType) metadata);

        /*} else if (metadata instanceof org.geotoolkit.gml.xml.v321.MultiLineStringType) {
            this.geometry = factory321.createMultiLineString((org.geotoolkit.gml.xml.v321.MultiLineStringType) metadata); ==> ISSUE

        */} else if (metadata instanceof org.geotoolkit.gml.xml.v321.MultiPointType) {
            this.geometry = factory321.createMultiPoint((org.geotoolkit.gml.xml.v321.MultiPointType) metadata);


        } else if (metadata instanceof org.geotoolkit.gml.xml.v321.MultiCurveType) {
            this.geometry = factory321.createMultiCurve((org.geotoolkit.gml.xml.v321.MultiCurveType) metadata);


        } else if (metadata instanceof org.geotoolkit.gml.xml.v321.MultiGeometryType) {
            this.geometry = factory321.createMultiGeometry((org.geotoolkit.gml.xml.v321.MultiGeometryType) metadata);


        } else if (metadata instanceof org.geotoolkit.gml.xml.v321.MultiSolidType) {
            this.geometry = factory321.createMultiSolid((org.geotoolkit.gml.xml.v321.MultiSolidType) metadata);

        /*} else if (metadata instanceof org.geotoolkit.gml.xml.v321.MultiPolygonType) {
            this.geometry = factory321.createMultiPolygon((org.geotoolkit.gml.xml.v321.MultiPolygonType) metadata); ==> ISSUE

        */} else if (metadata instanceof org.geotoolkit.gml.xml.v321.PolygonType) {
            this.geometry = factory321.createPolygon((org.geotoolkit.gml.xml.v321.PolygonType) metadata);

        } else if (metadata instanceof org.geotoolkit.gml.xml.v321.SurfaceType) {
            this.geometry = factory321.createSurface((org.geotoolkit.gml.xml.v321.SurfaceType) metadata);

        } else if (metadata != null){
            LOGGER.log(Level.WARNING, "Unexpected geometry class in geometryAdpater:{0}", metadata.getClass().getName());
        }
    }

    /**
     * Invoked when a new adapter is created by {@link org.apache.sis.xml.MarshallerPool}.
     *
     * @param marshaller The marshaller to be configured.
     */
    @Override
    public void register(final Marshaller marshaller) {
        marshaller.setAdapter(GM_Object.class, this);
    }

    /**
     * Invoked when a new adapter is created by {@link org.apache.sis.xml.MarshallerPool}.
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
