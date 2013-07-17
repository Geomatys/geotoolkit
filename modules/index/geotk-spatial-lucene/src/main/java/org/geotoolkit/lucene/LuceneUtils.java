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

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.measure.converter.UnitConverter;
import javax.measure.unit.Unit;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.geotoolkit.filter.SpatialFilterType;
import org.geotoolkit.geometry.Envelopes;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.geometry.jts.SRIDGenerator;
import org.apache.sis.measure.Units;
import org.geotoolkit.referencing.CRS;
import org.apache.sis.util.logging.Logging;
import org.opengis.filter.Filter;
import org.opengis.filter.spatial.*;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.operation.CylindricalProjection;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Guilhem Legal
 */
public class LuceneUtils {
    private static final Logger LOGGER = Logging.getLogger(LuceneUtils.class);
    
    public static Directory getAppropriateDirectory(final File indexDirectory) throws IOException {
        
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
        System.out.println("OBTAINED REPROJECTED ENV:" + bound);
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
    
}
