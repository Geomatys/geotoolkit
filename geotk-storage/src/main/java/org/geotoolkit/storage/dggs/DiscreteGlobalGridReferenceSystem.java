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
package org.geotoolkit.storage.dggs;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import javax.measure.IncommensurableException;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.gazetteer.ReferencingByIdentifiers;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.gazetteer.LocationType;
import org.opengis.referencing.operation.TransformException;

/**
 * Integrated system comprised of a specific discrete global grid hierarchy, spatiotemporal referencing by
 * zone identifiers and deterministic sub-zone ordering.
 *
 * @author Johann Sorel (Geomatys)
 * @see https://docs.ogc.org/DRAFTS/21-038r1.html#term-dggrs
 */
public abstract class DiscreteGlobalGridReferenceSystem extends ReferencingByIdentifiers {

    public DiscreteGlobalGridReferenceSystem(Map<String, ?> properties, LocationType[] types) {
        super(properties, types);
    }

    /**
     * A set of key words defining this DGGS.
     *
     * @return list of key words, never null, can be empty.
     */
    public abstract List<String> getKeywords();

    /**
     * Link to the DGGS definition.
     *
     * @return DGGS specification site.
     */
    public abstract URI getUri();

    /**
     * Returns the default depth (or refinement level) of this DGGS.
     * Some DGGS may have unusual geometries at low levels, such as spheres.
     * Those levels are often skipped and the default depth becomes greater then 0.
     *
     * @return default DGGS depth.
     */
    public abstract int getDefaultDepth();

    /**
     * Get root cell identifiers.
     *
     * @return root cells identifiers
     */
    public abstract List<ZonalIdentifier> getRootZoneIds();

    /**
     * Returns the global grid system.
     *
     * @return global grid system, never null
     */
    public abstract DiscreteGlobalGridSystem getGridSystem();

    /**
     * Returns the zone reference system.
     *
     * @return zonal reference system, never null
     */
    public abstract ZonalReferenceSystem getZonalSystem();

    /**
     * Returns a description of the child zone ordering.
     *
     * @return sub zone order, never null
     */
    public abstract SubZoneOrder getSubZoneOrder();

    /**
     * {@inheritDoc }
     */
    @Override
    public abstract Coder createCoder();

    public abstract static class Coder extends ReferencingByIdentifiers.Coder {

        /**
         * @return base DGGRS
         */
        @Override
        public abstract DiscreteGlobalGridReferenceSystem getReferenceSystem();

        /**
         * @return coder hierarchy level
         */
        public abstract int getPrecisionLevel();

        /**
         * @param level set coder hierarchy level
         * @throws IncommensurableException
         */
        public abstract void setPrecisionLevel(int level) throws IncommensurableException;

        /**
         * Convert long identifier to a text identifier
         */
        public abstract String idToText(long hash);

        /**
         * Convert text identifier to a long identifier
         */
        public abstract long idToNumeric(CharSequence cs);

        /**
         * {@inheritDoc }
         */
        @Override
        public String encode(DirectPosition dp) throws TransformException {
            return idToText(encodeNumeric(dp));
        }

        /**
         * Convert a location to a long zone identifier.
         */
        public abstract long encodeNumeric(DirectPosition dp) throws TransformException;

        /**
         * Convert a location to a zone identifier.
         */
        public ZonalIdentifier encodeIdentifier(DirectPosition dp) throws TransformException {
            return new ZonalIdentifier.Long(encodeNumeric(dp));
        }

        /**
         * Compute the zone object for given identifier
         */
        public Zone decode(ZonalIdentifier zid) throws TransformException {
            if (zid instanceof ZonalIdentifier.Long l) {
                return decode(l.getValue());
            } else if (zid instanceof ZonalIdentifier.Text t) {
                return decode(t.getValue());
            } else {
                throw new TransformException("Unsupported zonal identifier");
            }
        }

        /**
         * Compute the zone object for given identifier
         */
        public abstract Zone decode(long hash) throws TransformException;

        /**
         * Compute the zone object for given identifier
         */
        @Override
        public Zone decode(CharSequence cs) throws TransformException {
            return decode(idToNumeric(cs));
        };

        /**
         * Search zones which intersect the given area.
         *
         * Default implementation uses brute force search starting from root cells.
         *
         * @param env searched area
         * @return stream of zones.
         * @throws TransformException
         */
        public Stream<Zone> intersect(Envelope env) throws TransformException {
            final DiscreteGlobalGridReferenceSystem dggrs = getReferenceSystem();
            final GeneralEnvelope genv = new GeneralEnvelope(Envelopes.transform(env, dggrs.getGridSystem().getCrs()));
            final Coder coder = dggrs.createCoder();

            List<ZonalIdentifier> rootZoneIds = getReferenceSystem().getRootZoneIds();
            List<Zone> zones = new ArrayList<>(rootZoneIds.size());
            for (ZonalIdentifier s : rootZoneIds) {
                zones.add(coder.decode(s));
            }
            return zones.stream().flatMap((z) -> search(z, genv, getPrecisionLevel()));
        }

        private static Stream<Zone> search(Zone zone, GeneralEnvelope env, int level) {
            if (env.intersects(zone.getEnvelope())) {
                if (zone.getLocationType().getRefinementLevel() == level) {
                    return Stream.of(zone);
                } else {
                    //search children
                    return zone.getChildren().stream().flatMap((c) -> search(c,env,level));
                }
            }
            return Stream.empty();
        }

    }
}
