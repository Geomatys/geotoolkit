/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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
package org.geotoolkit.lucene;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.measure.converter.UnitConverter;
import javax.measure.unit.Unit;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.geotoolkit.filter.SpatialFilterType;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.geometry.jts.SRIDGenerator;
import org.apache.sis.measure.Units;
import org.geotoolkit.referencing.CRS;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.index.tree.manager.NamedEnvelope;
import org.opengis.filter.Filter;
import org.opengis.filter.spatial.*;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Guilhem Legal
 */
public class LuceneUtils {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.lucene");

    public static final GeometryFactory GF = new GeometryFactory();

    public static Directory getAppropriateDirectory(final Path indexDirectory) throws IOException {

        // for windows
        if (System.getProperty("os.name", "").startsWith("Windows")) {
             return new SimpleFSDirectory(indexDirectory);

        // for unix
        } else {
            final String archModel = System.getProperty("sun.arch.data.model");
            LOGGER.log(Level.FINER, "archmodel:{0}", archModel);
            if ("64".equals(archModel)) {
                return new MMapDirectory(indexDirectory);
            } else {
                return new NIOFSDirectory(indexDirectory);
            }
        }
    }

    public static GeneralEnvelope getExtendedReprojectedEnvelope(final Object geom, final CoordinateReferenceSystem treeCrs, final String strUnit, final double distance) throws FactoryException {
        GeneralEnvelope bound = getReprojectedEnvelope(geom, treeCrs);

        // add the reprojected distance
        if (bound != null) {
            final Unit unit    = Units.valueOf(strUnit);
            final Unit crsUnit = treeCrs.getCoordinateSystem().getAxis(0).getUnit();
            final CoordinateReferenceSystem crs;
            final boolean reproj;
            final GeneralEnvelope e;
            if (unit.isCompatible(crsUnit)) {
                crs = treeCrs;
                e   = bound;
                reproj = false;
            } else {
                if (Units.isLinear(unit)) {
                    crs = CRS.decode("EPSG:3857");
                } else {
                    crs = CRS.decode("CRS:84");
                }
                e   = getReprojectedEnvelope(bound, crs);
                reproj = true;
            }
            final UnitConverter converter = unit.getConverterTo(crs.getCoordinateSystem().getAxis(0).getUnit());
            final double rdistance = converter.convert(distance);
            final double minx = e.getLower(0) - rdistance;
            final double miny = e.getLower(1) - rdistance;
            final double maxx = e.getUpper(0) + rdistance;
            final double maxy = e.getUpper(1) + rdistance;
            e.setRange(0, minx, maxx);
            e.setRange(1, miny, maxy);
            if (reproj) {
                bound = getReprojectedEnvelope(e, treeCrs);
            }
        }
        LOGGER.log(Level.FINER, "OBTAINED REPROJECTED ENV:{0}", bound);
        return bound;
    }

    public static GeneralEnvelope getReprojectedEnvelope(final Object geom, final CoordinateReferenceSystem treeCrs) {
        if (geom instanceof Geometry) {
            return getReprojectedEnvelope((Geometry) geom, treeCrs);

        } else if (geom instanceof org.opengis.geometry.Envelope) {
            return getReprojectedEnvelope((org.opengis.geometry.Envelope) geom, treeCrs);

        }
        LOGGER.log(Level.WARNING, "Not a geometry for literal:{0} (class: {1})", new Object[]{geom, geom.getClass().getName()});
        return null;
    }
    /**
     * Extract the internal envelope from the geometry and reprojected it to the treeCRS.
     *
     * @param geom
     * @param treeCrs
     * @return
     */
    private static GeneralEnvelope getReprojectedEnvelope(final Geometry geom, final CoordinateReferenceSystem treeCrs) {
        final Envelope jtsBound = geom.getEnvelopeInternal();
        final String epsgCode = SRIDGenerator.toSRS(geom.getSRID(), SRIDGenerator.Version.V1);
        try {
            final CoordinateReferenceSystem geomCRS = CRS.decode(epsgCode);
            final GeneralEnvelope bound = new GeneralEnvelope(geomCRS);
            bound.setRange(0, jtsBound.getMinX(), jtsBound.getMaxX());
            bound.setRange(1, jtsBound.getMinY(), jtsBound.getMaxY());

            // reproject to cartesian CRS
            return (GeneralEnvelope) Envelopes.transform(bound, treeCrs);
        } catch (FactoryException ex) {
            LOGGER.log(Level.WARNING, "Factory exception while getting filter geometry crs", ex);
        } catch (TransformException ex) {
            LOGGER.log(Level.WARNING, "Transform exception while reprojecting filter geometry", ex);
        }
        return null;
    }

    /**
     * Reproject the envelope in the tree CRS.
     * @param env
     * @param treeCrs
     * @return
     */
    private static GeneralEnvelope getReprojectedEnvelope(final org.opengis.geometry.Envelope env, final CoordinateReferenceSystem treeCrs) {
        try {
            return (GeneralEnvelope) Envelopes.transform(env, treeCrs);
        } catch (TransformException ex) {
            LOGGER.log(Level.WARNING, "Transform exception while reprojecting filter geometry", ex);
        }
        return null;
    }

    public static SpatialFilterType getSpatialFilterType(final Filter filter) {

        if (filter instanceof BBOX) {
            return SpatialFilterType.BBOX;
        } else if (filter instanceof Beyond){
            return SpatialFilterType.BEYOND;
        } else if (filter instanceof Contains){
            return SpatialFilterType.CONTAINS;
        } else if (filter instanceof Crosses){
            return SpatialFilterType.CROSSES;
        } else if (filter instanceof Disjoint){
            return SpatialFilterType.DISJOINT;
        } else if (filter instanceof DWithin){
            return SpatialFilterType.DWITHIN;
        } else if (filter instanceof Equals){
            return SpatialFilterType.EQUALS;
        } else if (filter instanceof Intersects){
            return SpatialFilterType.INTERSECTS;
        } else if (filter instanceof Overlaps){
            return SpatialFilterType.OVERLAPS;
        } else if (filter instanceof Touches){
            return SpatialFilterType.TOUCHES;
        } else if (filter instanceof Within){
            return SpatialFilterType.WITHIN;
        }
        throw new IllegalArgumentException("unexpected filter type:" + filter);
    }

    public static NamedEnvelope getNamedEnvelope(final String id, final Geometry geom, final CoordinateReferenceSystem crs) throws FactoryException, TransformException {
        final com.vividsolutions.jts.geom.Envelope jtsBound = geom.getEnvelopeInternal();
        final String epsgCode = SRIDGenerator.toSRS(geom.getSRID(), SRIDGenerator.Version.V1);
        final CoordinateReferenceSystem geomCRS = CRS.decode(epsgCode);
        final GeneralEnvelope bound = new GeneralEnvelope(geomCRS);
        bound.setRange(0, jtsBound.getMinX(), jtsBound.getMaxX());
        bound.setRange(1, jtsBound.getMinY(), jtsBound.getMaxY());

        // reproject to specified CRS
        return new NamedEnvelope(Envelopes.transform(bound, crs), id);
    }

    public static Polygon getPolygon(final org.opengis.geometry.Envelope env){
        return getPolygon(env.getMinimum(0), env.getMaximum(0), env.getMinimum(1), env.getMaximum(1), env.getCoordinateReferenceSystem());
    }
    /**
     * Return a JTS polygon from bounding box coordinate.
     *
     * @param minx minimal X coordinate.
     * @param maxx maximal X coordinate.
     * @param miny minimal Y coordinate.
     * @param maxy maximal Y coordinate.
     * @param crs coordinate spatial reference.
     */
    public static Polygon getPolygon(final double minx, final double maxx, final double miny, final double maxy, final CoordinateReferenceSystem crs){
        final Coordinate[] crds = new Coordinate[]{
        new Coordinate(0, 0),
        new Coordinate(0, 0),
        new Coordinate(0, 0),
        new Coordinate(0, 0),
        new Coordinate(0, 0)};

        final CoordinateSequence pts = new CoordinateArraySequence(crds);
        final LinearRing rg          = new LinearRing(pts, GF);
        final Polygon poly           = new Polygon(rg, new LinearRing[0],GF);
        crds[0].x = minx;
        crds[0].y = miny;
        crds[1].x = minx;
        crds[1].y = maxy;
        crds[2].x = maxx;
        crds[2].y = maxy;
        crds[3].x = maxx;
        crds[3].y = miny;
        crds[4].x = minx;
        crds[4].y = miny;
        JTS.setCRS(poly, crs);
        return poly;
    }
    
    public static Polygon[] getPolygons(final List<Double> minx, final List<Double> maxx, final List<Double> miny, final List<Double> maxy, final CoordinateReferenceSystem crs) {
        final List<Polygon> polygonList = new ArrayList<>();
        for (int i = 0; i < minx.size(); i++) {
            if (Double.isNaN(minx.get(i)) || Double.isNaN(maxx.get(i)) || Double.isNaN(miny.get(i)) || Double.isNaN(maxy.get(i))) {
                LOGGER.info("skip NaN envelope");
            } else {
                polygonList.add(LuceneUtils.getPolygon(minx.get(i), maxx.get(i), miny.get(i), maxy.get(i), crs));
            }
        }
        return polygonList.toArray(new Polygon[polygonList.size()]);
    }
}
