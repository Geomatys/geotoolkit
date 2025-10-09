/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.referencing.dggs;

import com.google.common.geometry.S2Polygon;
import java.util.stream.Stream;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridSystems;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.extent.GeographicExtent;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 * Set of zones at the same refinement level, that uniquely and completely cover a globe.
 *
 * @author Johann Sorel (Geomatys)
 * @see https://docs.ogc.org/DRAFTS/21-038r1.html#term-dgg
 */
public interface DiscreteGlobalGrid {

    /**
     * Numerical order of a discrete global grid in the tessellation sequence
     * <p>
     * The discrete global grid with the least number of zones has a refinement level of 0.
     *
     * @return refinement level
     * @see https://docs.ogc.org/DRAFTS/21-038r1.html#term-refinement-level
     */
    int getRefinementLevel();

    /**
     * Create a math transform from long type identifiers to CRS.
     *
     * The returned MathTransform expect long identifiers as double values.
     * Use Double.doubleToRawLongBits(double) to read an identifier
     * and Double.longBitsToDouble(long) to write an identifier.
     *
     * @return MathTransform, never null
     * @throws UnsupportedOperationException if long type identifiers are not supported
     */
    MathTransform createTransformToCrs() throws UnsupportedOperationException;

    /**
     * Create a math transform from CRS to long type identifiers.
     *
     * The returned MathTransform encode long identifiers as double values.
     * Use Double.doubleToRawLongBits(double) to read an identifier
     * and Double.longBitsToDouble(long) to write an identifier.
     *
     * @return MathTransform, never null
     * @throws UnsupportedOperationException if long type identifiers are not supported
     */
    MathTransform createTransformToIdentifiers() throws UnsupportedOperationException;

    /**
     * Get all zones in at this grid level.
     *
     * @return stream of matching zones
     */
    default Stream<Zone> getZones() throws TransformException {
        return getZones((Envelope) null);
    }

    /**
     * Get zone at given location
     *
     * @return matching zone
     */
    Zone getZone(DirectPosition position) throws TransformException;

    /**
     * Get all zones which intersect the given envelope.
     *
     * @param env searched zone, can be null
     * @return stream of matching zones
     */
    default Stream<Zone> getZones(Envelope env) throws TransformException {
        if (env == null) return getZones((GeographicExtent) null);
        env = Envelopes.transform(env, CommonCRS.WGS84.normalizedGeographic());
        S2Polygon polygon = DiscreteGlobalGridSystems.toS2Polygon(env);
        return getZones(DiscreteGlobalGridSystems.toGeographicExtent(polygon));
    }

    /**
     * Get all zones which intersect the given extent.
     *
     * @param parent searched parent
     * @return stream of matching zones
     */
    default Stream<Zone> getZones(Zone parent) throws TransformException {
        return getZones(parent.getGeographicExtent());
    }

    /**
     * Get all zones which intersect the given extent.
     *
     * @param extent searched extent, can be null
     * @return stream of matching zones
     */
    Stream<Zone> getZones(GeographicExtent extent) throws TransformException;

}
