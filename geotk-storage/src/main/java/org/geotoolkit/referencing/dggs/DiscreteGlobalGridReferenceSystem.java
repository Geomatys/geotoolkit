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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import javax.measure.IncommensurableException;
import org.apache.sis.referencing.gazetteer.ReferencingByIdentifiers;
import org.opengis.geometry.DirectPosition;
import org.opengis.metadata.citation.Party;
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

    private Party party;
    private boolean partyCreated = false;

    public DiscreteGlobalGridReferenceSystem(Map<String, ?> properties, LocationType[] types) {
        super(properties, types);
    }

    @Override
    public Party getOverallOwner() {
        if (!partyCreated) {
            partyCreated = true;
            try {
                party = DiscreteGlobalGridReferenceSystems.createParty(getName().getCode(), getGridSystem());
                if (party == null) partyCreated = false;
            } catch (TransformException | IOException | URISyntaxException ex) {
                //do nothing
            }
        }
        return party;
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
         * {@inheritDoc }
         */
        @Override
        public abstract String encode(DirectPosition dp) throws TransformException;

        /**
         * Convert a location to a zone identifier.
         */
        public abstract Object encodeIdentifier(DirectPosition dp) throws TransformException;

        /**
         * Compute the zone object for given identifier
         */
        public Zone decode(Object zid) throws TransformException {
            return getReferenceSystem().getGridSystem().getHierarchy().getZone(zid);
        }

        @Override
        public Zone decode(CharSequence cs) throws TransformException {
            return getReferenceSystem().getGridSystem().getHierarchy().getZone(cs);
        }
    }
}
