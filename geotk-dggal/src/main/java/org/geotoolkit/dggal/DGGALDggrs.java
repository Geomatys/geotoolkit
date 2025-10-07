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
package org.geotoolkit.dggal;

import java.lang.foreign.MemorySegment;
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
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.SimpleInternationalString;
import org.apache.sis.util.privy.Constants;
import org.apache.sis.util.resources.Vocabulary;
import org.geotoolkit.dggal.panama.DGGAL;
import org.geotoolkit.dggal.panama.DggalDggrs;
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
public final class DGGALDggrs extends DiscreteGlobalGridReferenceSystem {

    public static final String HEALPIX_IDENTIFIER = "HEALPix";
    public static final String ISEA3H_IDENTIFIER = "ISEA3H";
    public static final String ISEA4R_IDENTIFIER = "ISEA4R";
    public static final String ISEA7H_IDENTIFIER = "ISEA7H";
    public static final String ISEA7H_Z7_IDENTIFIER = "ISEA7H_Z7";
    public static final String ISEA9R_IDENTIFIER = "ISEA9R";
    public static final String IVEA3H_IDENTIFIER = "IVEA3H";
    public static final String IVEA4R_IDENTIFIER = "IVEA4R";
    public static final String IVEA7H_IDENTIFIER = "IVEA7H";
    public static final String IVEA7H_Z7_IDENTIFIER = "IVEA7H_Z7";
    public static final String IVEA9R_IDENTIFIER = "IVEA9R";
    public static final String RTEA3H_IDENTIFIER = "RTEA3H";
    public static final String RTEA4R_IDENTIFIER = "RTEA4R";
    public static final String RTEA7H_IDENTIFIER = "RTEA7H";
    public static final String RTEA7H_Z7_IDENTIFIER = "RTEA7H_Z7";
    public static final String RTEA9R_IDENTIFIER = "RTEA9R";
    public static final String RHEALPIX_IDENTIFIER = "rHEALPix";
    public static final String GNOSISGLOBALGRID_IDENTIFIER = "GNOSISGlobalGrid";

    public static final DGGALDggrs HEALPIX_INSTANCE = new DGGALDggrs(HEALPIX_IDENTIFIER);
    public static final DGGALDggrs ISEA3H_INSTANCE = new DGGALDggrs(ISEA3H_IDENTIFIER);
    public static final DGGALDggrs ISEA4R_INSTANCE = new DGGALDggrs(ISEA4R_IDENTIFIER);
    public static final DGGALDggrs ISEA7H_INSTANCE = new DGGALDggrs(ISEA7H_IDENTIFIER);
    public static final DGGALDggrs ISEA7H_Z7_INSTANCE = new DGGALDggrs(ISEA7H_Z7_IDENTIFIER);
    public static final DGGALDggrs ISEA9R_INSTANCE = new DGGALDggrs(ISEA9R_IDENTIFIER);
    public static final DGGALDggrs IVEA3H_INSTANCE = new DGGALDggrs(IVEA3H_IDENTIFIER);
    public static final DGGALDggrs IVEA4R_INSTANCE = new DGGALDggrs(IVEA4R_IDENTIFIER);
    public static final DGGALDggrs IVEA7H_INSTANCE = new DGGALDggrs(IVEA7H_IDENTIFIER);
    public static final DGGALDggrs IVEA7H_Z7_INSTANCE = new DGGALDggrs(IVEA7H_Z7_IDENTIFIER);
    public static final DGGALDggrs IVEA9R_INSTANCE = new DGGALDggrs(IVEA9R_IDENTIFIER);
    public static final DGGALDggrs RTEA3H_INSTANCE = new DGGALDggrs(RTEA3H_IDENTIFIER);
    public static final DGGALDggrs RTEA4R_INSTANCE = new DGGALDggrs(RTEA4R_IDENTIFIER);
    public static final DGGALDggrs RTEA7H_INSTANCE = new DGGALDggrs(RTEA7H_IDENTIFIER);
    public static final DGGALDggrs RTEA7H_Z7_INSTANCE = new DGGALDggrs(RTEA7H_Z7_IDENTIFIER);
    public static final DGGALDggrs RTEA9R_INSTANCE = new DGGALDggrs(RTEA9R_IDENTIFIER);
    public static final DGGALDggrs RHEALPIX_INSTANCE = new DGGALDggrs(RHEALPIX_IDENTIFIER);
    public static final DGGALDggrs GNOSISGLOBALGRID_INSTANCE = new DGGALDggrs(GNOSISGLOBALGRID_IDENTIFIER);

    private static final ZonalReferenceSystem ZRS = new DefaultZonalReferenceSystem("default", "", true);

    private static DGGAL binding;
    private static MemorySegment mod;

    static synchronized DggalDggrs load(String name) {
        try {
            if (binding == null) {
                binding = DGGAL.global();
                mod = binding.init();
            }
            return binding.newDggrs(mod, name);
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    final DggalDggrs dggal;
    private final String identifier;
    private final String description;
    final DGGALDggs dggs;


    public DGGALDggrs(DggalDggrs dggrs,
            String identifier,
            String description) {
        super(properties(identifier, identifier, null), types(identifier));
        ArgumentChecks.ensureNonNull("dggrs", dggrs);
        this.dggal = dggrs;
        this.identifier = identifier;
        this.description = description;
        try {
            this.dggs = new DGGALDggs(this);
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    public DGGALDggrs(String name) {
        this(load(name), name, name);
    }

    @Override
    public List<String> getKeywords() {
        return List.of(identifier, "dggs");
    }

    @Override
    public Optional<InternationalString> getDescription() {
        return Optional.of(new SimpleInternationalString(description));
    }

    @Override
    public URI getUri() {
        try {
            return new URI("https://dggal.org");
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
        return SubZoneOrder.MORTON_CURVE;
    }

    @Override
    public DiscreteGlobalGridReferenceSystem.Coder createCoder() {
        return new DGGALCoder(this);
    }

    private static LocationType[] types(String identifier) {
        final ModifiableLocationType gzd = new ModifiableLocationType(identifier);
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
