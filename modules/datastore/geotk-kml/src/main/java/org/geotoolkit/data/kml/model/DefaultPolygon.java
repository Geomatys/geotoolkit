/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
package org.geotoolkit.data.kml.model;

import com.vividsolutions.jts.geom.GeometryFactory;
import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleTypeContainer;
import static org.geotoolkit.data.kml.xml.KmlConstants.*;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultPolygon extends com.vividsolutions.jts.geom.Polygon implements Polygon {

    private final Extensions extensions = new Extensions();
    private IdAttributes idAttributes;
    private boolean extrude;
    private boolean tessellate;
    private AltitudeMode altitudeMode;
    private Boundary outerBoundary;
    private List<Boundary> innerBoundaries;

    /**
     * <p>This method extract LinearaRings from Boundaries list</p>
     * 
     * @param boundaries
     * @return
     */
    private static com.vividsolutions.jts.geom.LinearRing[] extract(List<Boundary> boundaries) {
        if (boundaries == null) {
            return null;
        }
        com.vividsolutions.jts.geom.LinearRing[] linearRing = new com.vividsolutions.jts.geom.LinearRing[boundaries.size()];
        for (int i = 0, size = boundaries.size(); i < size; i++) {
            linearRing[i] = (com.vividsolutions.jts.geom.LinearRing) boundaries.get(i).getLinearRing();
        }
        return linearRing;
    }

    /**
     *
     * @param outerBoundary
     * @param innerBoundaries
     * @param factory
     */
    public DefaultPolygon(Boundary outerBoundary,
            List<Boundary> innerBoundaries, GeometryFactory factory) {
        super((com.vividsolutions.jts.geom.LinearRing) outerBoundary.getLinearRing(),
                (com.vividsolutions.jts.geom.LinearRing[]) extract(innerBoundaries),
                factory);
        this.extrude = DEF_EXTRUDE;
        this.tessellate = DEF_TESSELLATE;
        this.altitudeMode = DEF_ALTITUDE_MODE;
        this.outerBoundary = outerBoundary;
        this.innerBoundaries = (innerBoundaries == null) ? EMPTY_LIST : innerBoundaries;
    }

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param abstractGeometrySimpleExtensions
     * @param abstractGeometryObjectExtensions
     * @param extrude
     * @param tessellate
     * @param altitudeMode
     * @param outerBoundary
     * @param innerBoundaries
     * @param factory
     * @param polygonSimpleExtensions
     * @param polygonObjectExtensions
     */
    public DefaultPolygon(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleTypeContainer> abstractGeometrySimpleExtensions,
            List<Object> abstractGeometryObjectExtensions,
            boolean extrude, boolean tessellate, AltitudeMode altitudeMode,
            Boundary outerBoundary,
            List<Boundary> innerBoundaries, GeometryFactory factory,
            List<SimpleTypeContainer> polygonSimpleExtensions,
            List<Object> polygonObjectExtensions) {

        super((com.vividsolutions.jts.geom.LinearRing) outerBoundary.getLinearRing(),
                extract(innerBoundaries),
                factory);

        if (objectSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.OBJECT).addAll(objectSimpleExtensions);
        }
        this.idAttributes = idAttributes;

        if (abstractGeometrySimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.GEOMETRY).addAll(abstractGeometrySimpleExtensions);
        }
        if (abstractGeometryObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.GEOMETRY).addAll(abstractGeometryObjectExtensions);
        }
        this.extrude = extrude;
        this.tessellate = tessellate;
        this.altitudeMode = altitudeMode;
        this.outerBoundary = outerBoundary;
        this.innerBoundaries = innerBoundaries;

        if (polygonSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.POLYGON).addAll(polygonSimpleExtensions);
        }
        if (polygonObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.POLYGON).addAll(polygonObjectExtensions);
        }
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public boolean getExtrude() {
        return this.extrude;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public boolean getTessellate() {
        return this.tessellate;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AltitudeMode getAltitudeMode() {
        return this.altitudeMode;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setExtrude(boolean extrude) {
        this.extrude = extrude;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setTessellate(boolean tesselate) {
        this.tessellate = tesselate;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAltitudeMode(EnumAltitudeMode altitudeMode) {
        this.altitudeMode = altitudeMode;
    }

    @Override
    public Extensions extensions() {
        return this.extensions;
    }

    @Override
    public IdAttributes getIdAttributes() {
        return this.idAttributes;
    }

    @Override
    public void setIdAttributes(IdAttributes idAttributes) {
        this.idAttributes = idAttributes;
    }

    @Override
    public Boundary getOuterBoundary() {
        return this.outerBoundary;
    }

    @Override
    public List<Boundary> getInnerBoundaries() {
        return this.innerBoundaries;
    }
}
