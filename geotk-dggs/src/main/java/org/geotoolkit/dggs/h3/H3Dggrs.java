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
package org.geotoolkit.dggs.h3;

import com.uber.h3core.H3Core;
import java.io.IOException;
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
import org.apache.sis.util.privy.Constants;
import org.apache.sis.util.resources.Vocabulary;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGridReferenceSystem;
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
import org.opengis.util.InternationalString;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class H3Dggrs extends DiscreteGlobalGridReferenceSystem {

    /**
     * Identifier for this reference system.
     */
    public static final String IDENTIFIER = "H3";
    public static final H3Dggrs INSTANCE = new H3Dggrs();

    private static final ZonalReferenceSystem ZRS = new DefaultZonalReferenceSystem("default", "", true);

    static final H3Core H3;

    static {
        try {
            H3 = H3Core.newInstance();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private final H3Dggs dggs;

    public H3Dggrs() {
        super(properties(IDENTIFIER, IDENTIFIER, null), types());
        this.dggs = new H3Dggs(this);
    }

    @Override
    public List<String> getKeywords() {
        return List.of("h3", "dggs");
    }

    @Override
    public Optional<InternationalString> getDescription() {
        return Optional.of(new SimpleInternationalString(
                "H3 is a discrete global grid system for indexing geographies into a hexagonal grid, developed at Uber.\n" +
                "Coordinates can be indexed to cell IDs that each represent a unique cell.\n" +
                "Indexed data can be quickly joined across disparate datasets and aggregated at different levels of precision.\n" +
                "H3 enables a range of algorithms and optimizations based on the grid, including nearest neighbors, shortest path, gradient smoothing, and more."));
    }

    @Override
    public URI getUri() {
        try {
            return new URI("https://h3geo.org/");
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
        return SubZoneOrder.valueOf("IJK_ORDER_HEXAGON", "https://h3geo.org/docs/3.x/core-library/h3Indexing/");
    }

    @Override
    public Coder createCoder() {
        return new H3Coder(this);
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
