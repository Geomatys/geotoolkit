/*******************************************************************************
 * $ * * $Id$ * *
 * $Source:
 * /cvs/ctree/LiteGO1/src/jar/com/polexis/lite/spatialschema/geometry/primitive/PrimitiveFactoryImpl.java,v $ * *
 * Copyright (C) 2003 Open GIS Consortium, Inc. All Rights Reserved.
 * http://www.opengis.org/Legal/ *
 ******************************************************************************/
package org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive;

import java.util.List;

import org.apache.sis.geometry.GeneralDirectPosition;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.geometry.JTSGeometryFactory;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.geometry.JTSPolygon;

import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.geometry.DirectPosition;
import org.opengis.coordinate.MismatchedDimensionException;
import org.opengis.coordinate.MismatchedCoordinateMetadataException;
import org.opengis.geometry.coordinate.Polygon;
import org.opengis.geometry.coordinate.PolyhedralSurface;
import org.opengis.geometry.primitive.Curve;
import org.opengis.geometry.primitive.CurveSegment;
import org.opengis.geometry.primitive.Point;
import org.opengis.geometry.primitive.Primitive;
import org.opengis.geometry.primitive.Ring;
import org.opengis.geometry.primitive.Shell;
import org.opengis.geometry.primitive.Solid;
import org.opengis.geometry.primitive.SolidBoundary;
import org.opengis.geometry.primitive.Surface;
import org.opengis.geometry.primitive.SurfaceBoundary;
import org.opengis.geometry.primitive.SurfacePatch;

/**
 * Factory that knows how to create instances of the 19107 primitives as
 * implemented in LiteGO1.
 */
public class JTSPrimitiveFactory {

    /**
     * a default CRS to use when creating primitives
     */
    private final CoordinateReferenceSystem crs;

    private JTSGeometryFactory geomFact;

    public JTSPrimitiveFactory() {
        this(null);
    }

    public JTSPrimitiveFactory(final CoordinateReferenceSystem crs) {
        this.crs = crs;
        geomFact = new JTSGeometryFactory(crs);
    }

    //*************************************************************************
    //  implement the PrimitiveFactory interface
    //*************************************************************************

    /**
     * Returns the coordinate reference system in use for all
     * {@linkplain Primitive primitive}geometric objects to be created through
     * this interface.
     */
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return crs;
    }

    /**
     * Create a direct position at the specified location specified by
     * coordinates. If the parameter is null, the position is left
     * uninitialized.
     */
    public DirectPosition createDirectPosition(final double[] coordinates) {
        if(coordinates == null){
            GeneralDirectPosition position = new GeneralDirectPosition(crs);
            return position;
        }else{
            GeneralDirectPosition position = new GeneralDirectPosition(coordinates);
            position.setCoordinateReferenceSystem(crs);
            return position;
        }
    }

    /**
     * Creates a point at the specified location specified by coordinates.
     */
    public Point createPoint(final double[] coordinates) {
        return new JTSPoint(createDirectPosition(coordinates), crs);
    }

    /**
     * Creates a point at the specified position.
     */
    public Point createPoint(final DirectPosition position) {
        return new JTSPoint(position, crs);
    }

    /**
     * Takes a list of {@linkplain CurveSegment curve segments}with the
     * appropriate end-to-start relationships and creates a
     * {@linkplain Curve curve}. This may throw an IllegalArgumentException if
     * the List contains objects that are not instances of the CurveSegment
     * interface.
     */
    public Curve createCurve(final List<CurveSegment> segments) {
        JTSCurve result = new JTSCurve(crs);
        if (segments != null)
            result.getSegments().addAll(segments);
        return result;
    }

    /**
     * Creates a new Surface. This method can't possibly be used in the current
     * implementation since there are no implementations of the SurfacePatch
     * interface. Returns null.
     */
    @SuppressWarnings("unchecked")
    public Surface createSurface(final List<SurfacePatch> patches) {
        JTSSurface result = new JTSSurface(crs);
        List<?> cast = (List<?>) patches;
        result.getPatches().addAll( (List<JTSSurfacePatch>) cast );
        return result;
    }

    public Surface createSurface(final SurfaceBoundary boundary) {
        // For now, our implementation has to assume that the boundary is a
        // polygon.
        Surface result = new JTSSurface(crs);
        Polygon poly = geomFact.createPolygon(boundary);
        // PENDING(jdc): the following line is 1.5 specific.
        // the result.getPatches() list is a generic list with a type of "? extends SurfacePatch"
        // we can compile without the generic if we cast down to List, but why do we need the cast?
        // Polygon extends SurfacePatch, so in theory this should work...
        //((List<SurfacePatch>) result.getPatches()).add(poly);
        ((List)result.getPatches()).add(poly);
        return result;
    }

    public SurfaceBoundary createSurfaceBoundary(final Ring exterior, final List interiors)
            throws MismatchedCoordinateMetadataException, MismatchedDimensionException {
        return new JTSSurfaceBoundary(crs, exterior, (Ring []) interiors.toArray(new Ring[interiors.size()]));
    }

    /**
     * Constructs a {@linkplain Solid solid}by indicating its boundary as a
     * collection of {@linkplain Shell shells}organized into a
     * {@linkplain SolidBoundary solid boundary}. Since this specification is
     * limited to 3-dimensional coordinate reference systems, any solid is
     * definable by its boundary.
     * @return a {@code Solid} based on the given {@code boundary}
     */
    public Solid createSolid(final SolidBoundary boundary) {
        return null;
    }

    public Ring createRing(final List curves) {
        Ring result = new JTSRing(crs);
        if (curves != null)
            result.getGenerators().addAll(curves);
        return result;
    }

    public PolyhedralSurface createPolyhedralSurface(final List<Polygon> patches)
            throws MismatchedCoordinateMetadataException, MismatchedDimensionException {
        JTSPolyhedralSurface result = new JTSPolyhedralSurface(crs);
        List<?> cast = (List<?>) patches;
        result.getPatches().addAll((List<JTSPolygon>) cast );
        return result;
    }
}
