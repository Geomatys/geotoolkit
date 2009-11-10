/*$************************************************************************************************
 **
 ** $Id$
 **
 ** $Source: /cvs/ctree/LiteGO1/src/jar/com/polexis/lite/spatialschema/geometry/primitive/SurfacePatchImpl.java,v $
 **
 ** Copyright (C) 2003 Open GIS Consortium, Inc. All Rights Reserved. http://www.opengis.org/Legal/
 **
 *************************************************************************************************/
package org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive;

// OpenGIS direct dependencies
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.primitive.Ring;
import org.opengis.geometry.primitive.Surface;
import org.opengis.geometry.primitive.SurfaceBoundary;
import org.opengis.geometry.primitive.SurfaceInterpolation;
import org.opengis.geometry.primitive.SurfacePatch;

import com.vividsolutions.jts.geom.Geometry;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.JTSGeometry;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.geometry.AbstractJTSGenericSurface;
import org.geotoolkit.internal.jaxb.SurfaceBoundaryAdapter;
import org.geotoolkit.util.Utilities;

/**
 * Defines a homogeneous portion of a {@linkplain Surface surface}.
 * Each {@code SurfacePatch} shall be in at most one {@linkplain Surface surface}.
 *
 * @author ISO/DIS 19107
 * @author <A HREF="http://www.opengis.org">OpenGIS&reg; consortium</A>
 * @version 2.0
 * @module pending
 */
@XmlAccessorType(XmlAccessType.NONE)
public abstract class JTSSurfacePatch extends AbstractJTSGenericSurface implements SurfacePatch, JTSGeometry {
    private Surface surface;
    private SurfaceInterpolation interpolation;

    @XmlElement(name = "PolygonPatch", namespace = "http://www.opengis.net/gml")
    @XmlJavaTypeAdapter(SurfaceBoundaryAdapter.class)
    private SurfaceBoundary boundary;
    private com.vividsolutions.jts.geom.Geometry jtsPeer;

    public JTSSurfacePatch() {
        this(null, null);
    }
    
    public JTSSurfacePatch(SurfaceInterpolation interpolation, SurfaceBoundary boundary) {
        this.interpolation = interpolation;
        this.boundary = boundary;
    }

    /**
     * Returns the patch which own this surface patch.
     *
     * <blockquote><font size=2>
     * <strong>NOTE:</strong> In this specification, surface patches do not appear except in the
     * context of a surface, and therefore this method should never returns {@code null}
     * which would preclude the use of surface patches except in this manner. While this would
     * not affect this specification, allowing {@code null} owner allows other standards
     * based on this one to use surface patches in a more open-ended manner.
     * </font></blockquote>
     *
     * @return The owner of this surface patch, or {@code null} if none.
     *
     * @see Surface#getPatches
     */
    @Override
    public Surface getSurface() {
        return null;
    }

    public void setSurface(Surface surface) {
        this.surface = surface;
    }

    /**
     * Determines the surface interpolation mechanism used for this {@code SurfacePatch}.
     * This mechanism uses the control points and control parameters defined in the various
     * subclasses to determine the position of this {@code SurfacePatch}.
     *
     * @return The interpolation mechanism.
     */
    @Override
    public SurfaceInterpolation getInterpolation() {
        return interpolation;
    }

    /**
     * Specifies the type of continuity between this surface patch and its immediate neighbors
     * with which it shares a boundary curve. The sequence of values corresponds to the
     * {@linkplain Ring rings} in the {@linkplain SurfaceBoundary surface boundary} returned by
     * {@link #getBoundary} for this patch. The default value of "0" means simple continuity, which
     * is a mandatory minimum level of continuity. This level is referred to as "C<sup>0</sup>" in
     * mathematical texts. A value of 1 means that the functions are continuous and differentiable
     * at the appropriate end point: "C<sup>1</sup>" continuity. A value of "n" for any integer means
     * <var>n</var>-times differentiable: "C<sup>n</sup>" continuity.
     *
     * @return The type of continuity between this surface patch and its immediate neighbors.
     */
    @Override
    public abstract int getNumDerivativesOnBoundary();

    /**
     * {@inheritDoc }
     */
    @Override
    public SurfaceBoundary getBoundary() {
        return boundary;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public final double[] getUpNormal(DirectPosition point) {
        return new double [] { 0, 0, 1 };
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public final double getPerimeter() {
        com.vividsolutions.jts.geom.Geometry jtsGeom = getJTSGeometry();
        return jtsGeom.getBoundary().getLength();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public final double getArea() {
        com.vividsolutions.jts.geom.Geometry jtsGeom = getJTSGeometry();
        return jtsGeom.getArea();
    }

    /**
     * Retrieves the equivalent JTS geometry for this object.  Note that this
     * operation may be expensive if the geometry must be computed.
     */
    @Override
    public Geometry getJTSGeometry() {
        if (jtsPeer == null) {
            jtsPeer = calculateJTSPeer();
        }
        return jtsPeer;
    }

    /**
     * This method is invoked to cause the JTS object to be recalculated the
     * next time it is requested.  This method will be called by the
     * underlying guts of the code when something has changed.
     */
    @Override
    public void invalidateCachedJTSPeer() {
        jtsPeer = null;
    }

    public abstract com.vividsolutions.jts.geom.Geometry calculateJTSPeer();

    @Override
    public boolean equals(Object object) {
        if (object == this)
            return true;

        if (object instanceof JTSSurfacePatch) {
            JTSSurfacePatch that = (JTSSurfacePatch) object;
            return Utilities.equals(this.boundary,      that.boundary)      &&
                   Utilities.equals(this.interpolation, that.interpolation) &&
                   Utilities.equals(this.surface,       that.surface);
        }
        return false;
     }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (this.surface != null ? this.surface.hashCode() : 0);
        hash = 31 * hash + (this.interpolation != null ? this.interpolation.hashCode() : 0);
        hash = 31 * hash + (this.boundary != null ? this.boundary.hashCode() : 0);
        return hash;
    }

     @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString()).append('\n');
        if (boundary != null) {
            sb.append("boundary:").append(boundary).append('\n');
        }
        if (interpolation != null) {
            sb.append("interpolation:").append(interpolation).append('\n');
        }
        if (jtsPeer != null) {
            sb.append("jtsPeer:").append(jtsPeer).append('\n');
        }
        if (surface != null) {
            sb.append("surface:").append(surface).append('\n');
        }
        return sb.toString();
    }
}
