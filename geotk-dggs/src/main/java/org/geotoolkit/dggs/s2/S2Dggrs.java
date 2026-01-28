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
package org.geotoolkit.dggs.s2;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.sis.metadata.iso.citation.Citations;
import org.apache.sis.metadata.iso.extent.Extents;
import org.apache.sis.referencing.ImmutableIdentifier;
import org.apache.sis.referencing.gazetteer.ModifiableLocationType;
import org.apache.sis.util.SimpleInternationalString;
import org.apache.sis.util.internal.shared.Constants;
import org.apache.sis.util.resources.Vocabulary;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGridReferenceSystem;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGridSystem;
import org.geotoolkit.referencing.dggs.SubZoneOrder;
import org.geotoolkit.referencing.dggs.ZonalReferenceSystem;
import org.geotoolkit.referencing.dggs.internal.shared.DefaultZonalReferenceSystem;
import org.opengis.metadata.citation.Party;
import org.opengis.referencing.ObjectDomain;
import org.opengis.referencing.gazetteer.LocationType;
import org.opengis.util.InternationalString;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class S2Dggrs extends DiscreteGlobalGridReferenceSystem {

    /**
     * Identifier for this reference system.
     */
    public static final String IDENTIFIER = "S2";
    public static final S2Dggrs INSTANCE = new S2Dggrs();

    private static final ZonalReferenceSystem ZRS = new DefaultZonalReferenceSystem("default", "", true);

    final S2Dggs dggs;

    public S2Dggrs() {
        super(properties(IDENTIFIER, IDENTIFIER, null), types());
        this.dggs = new S2Dggs(this);
    }

    @Override
    public List<String> getKeywords() {
        return List.of("s2", "dggs");
    }

    @Override
    public Optional<InternationalString> getDescription() {
        return Optional.of(new SimpleInternationalString(
              "A unique feature of the S2 library is that unlike traditional geographic information systems, "
            + "which represent data as flat two-dimensional projections (similar to an atlas), the S2 "
            + "library represents all data on a three-dimensional sphere (similar to a globe). This makes "
            + "it possible to build a worldwide geographic database with no seams or singularities, using a "
            + "single coordinate system, and with low distortion everywhere compared to the true shape of the Earth. "
            + "While the Earth is not quite spherical, it is much closer to being a sphere than it is to being flat!"));
    }

    @Override
    public URI getUri() {
        try {
            return new URI("http://s2geometry.io/");
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
    public SubZoneOrder getSubZoneOrder() {
        return SubZoneOrder.HILBERT_CURVE;
    }

    @Override
    public Coder createCoder() {
        return new S2Coder(this);
    }

    private static LocationType[] types() {
        final ModifiableLocationType gzd = new ModifiableLocationType(IDENTIFIER);
        gzd.addIdentification(Vocabulary.formatInternational(Vocabulary.Keys.Code));
        return new LocationType[] {gzd};
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
}
