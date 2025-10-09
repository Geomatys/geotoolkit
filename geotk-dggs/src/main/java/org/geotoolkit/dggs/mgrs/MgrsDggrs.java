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
package org.geotoolkit.dggs.mgrs;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.sis.metadata.iso.citation.Citations;
import org.apache.sis.metadata.iso.extent.Extents;
import org.apache.sis.referencing.ImmutableIdentifier;
import org.apache.sis.referencing.gazetteer.MilitaryGridReferenceSystem;
import org.apache.sis.referencing.gazetteer.ModifiableLocationType;
import org.apache.sis.referencing.gazetteer.ReferencingByIdentifiers;
import org.apache.sis.util.internal.shared.Constants;
import org.apache.sis.util.resources.Vocabulary;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGridReferenceSystem;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGridReferenceSystem.Coder;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGridSystem;
import org.geotoolkit.referencing.dggs.SubZoneOrder;
import org.geotoolkit.referencing.dggs.ZonalReferenceSystem;
import org.geotoolkit.referencing.dggs.internal.shared.DefaultZonalReferenceSystem;
import org.opengis.metadata.citation.Party;
import static org.opengis.referencing.IdentifiedObject.IDENTIFIERS_KEY;
import static org.opengis.referencing.IdentifiedObject.NAME_KEY;
import org.opengis.referencing.ObjectDomain;
import org.opengis.referencing.gazetteer.LocationType;
import static org.opengis.referencing.gazetteer.ReferenceSystemUsingIdentifiers.OVERALL_OWNER_KEY;
import static org.opengis.referencing.gazetteer.ReferenceSystemUsingIdentifiers.THEME_KEY;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class MgrsDggrs extends DiscreteGlobalGridReferenceSystem {

    static final MilitaryGridReferenceSystem MGRS = new MilitaryGridReferenceSystem();
    private static final ZonalReferenceSystem ZRS = new DefaultZonalReferenceSystem("default", "", true);

    final MgrsDggs dggs;

    public MgrsDggrs() {
        super(properties(MGRS.getName().toString(), MGRS.getName().toString(), null), types(MGRS));
        this.dggs = new MgrsDggs(this);
    }

    @Override
    public List<String> getKeywords() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public SubZoneOrder getSubZoneOrder() {
        return SubZoneOrder.MORTON_CURVE;
    }

    @Override
    public URI getUri() {
        try {
            return new URI("https://en.wikipedia.org/wiki/Military_Grid_Reference_System");
        } catch (URISyntaxException ex) {
            return null;
        }
    }

    @Override
    public DiscreteGlobalGridSystem getGridSystem() {
        return dggs;
    }

    @Override
    public ZonalReferenceSystem getZonalSystem() {
        return ZRS;
    }

    @Override
    public Coder createCoder() {
        return new MgrsCoder(this);
    }

    /**
     * Convenience method for helping subclasses to build their argument for the constructor.
     * The returned properties have the domain of validity set to the whole word and the theme to "mapping".
     *
     * @param name   the reference system name as an {@link org.opengis.metadata.Identifier} or a {@link String}.
     * @param id     an identifier for the reference system. Use SIS namespace until we find an authority for them.
     * @param party  the overall owner, or {@code null} if none.
     */
    private static Map<String,Object> properties(final Object name, final String id, final Party party) {
        final Map<String,Object> properties = new HashMap<>(8);
        properties.put(NAME_KEY, name);
        properties.put(IDENTIFIERS_KEY, new ImmutableIdentifier(Citations.SIS, Constants.SIS, id));
        properties.put(ObjectDomain.DOMAIN_OF_VALIDITY_KEY, Extents.WORLD);
        properties.put(THEME_KEY, Vocabulary.formatInternational(Vocabulary.Keys.Mapping));
        properties.put(OVERALL_OWNER_KEY, party);
        return properties;
    }

    private static LocationType[] types(ReferencingByIdentifiers rbi) {
        final ModifiableLocationType gzd = new ModifiableLocationType(rbi.getName().toString());
        gzd.addIdentification(Vocabulary.formatInternational(Vocabulary.Keys.Code));
        return new LocationType[] {gzd};
    }
}
