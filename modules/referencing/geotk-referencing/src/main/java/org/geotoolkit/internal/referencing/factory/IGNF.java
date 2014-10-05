/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.internal.referencing.factory;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Collections;

import org.opengis.util.FactoryException;
import org.opengis.util.InternationalString;
import org.opengis.metadata.citation.Citation;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.metadata.Identifier;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.metadata.Citations;
import org.apache.sis.metadata.iso.ImmutableIdentifier;
import org.apache.sis.referencing.NamedIdentifier;
import org.geotoolkit.referencing.cs.PredefinedCS;
import org.apache.sis.referencing.crs.DefaultGeographicCRS;
import org.apache.sis.referencing.datum.DefaultGeodeticDatum;
import org.geotoolkit.referencing.operation.DefiningConversion;
import org.geotoolkit.referencing.factory.DirectAuthorityFactory;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.IdentifiedObjects;


/**
 * The IGNF authority factory.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.14
 * @module
 */
public final class IGNF extends DirectAuthorityFactory implements CRSAuthorityFactory {
    /**
     * The map of pre-defined CRS. Will be created when first needed. Keys are IGNF codes.
     * Values are initially projection names, to be replaced by the actual CRS when first
     * created.
     */
    private final Map<String,Object> crsMap = new TreeMap<>();

    /**
     * The authority codes, as an unmodifiable view over the keys in the {@link #crsMap}.
     */
    private final Set<String> codes;

    /**
     * Creates a new authority factory.
     */
    public IGNF() {
        super(EMPTY_HINTS);
        crsMap.put("MILLER", "Miller_Cylindrical");
        codes = Collections.unmodifiableSet(crsMap.keySet());
    }

    /**
     * Returns the IGNF authority.
     */
    @Override
    public Citation getAuthority() {
        return Citations.IGNF;
    }

    /**
     * Returns the list of supported codes.
     */
    @Override
    public Set<String> getAuthorityCodes(Class<? extends IdentifiedObject> type) {
        return type.isAssignableFrom(ProjectedCRS.class) ? codes : Collections.<String>emptySet();
    }

    /**
     * Returns the CRS name for the given code.
     */
    @Override
    public InternationalString getDescriptionText(String code) throws FactoryException {
        return new SimpleInternationalString(createObject(code).getName().getCode());
    }

    /**
     * Creates an object from the specified code. The default implementation delegates to
     * <code>{@linkplain #createCoordinateReferenceSystem createCoordinateReferenceSystem}(code)</code>.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public IdentifiedObject createObject(final String code) throws FactoryException {
        return createCoordinateReferenceSystem(code);
    }

    /**
     * Creates a coordinate reference system from the specified code.
     *
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public synchronized CoordinateReferenceSystem createCoordinateReferenceSystem(final String code)
            throws FactoryException
    {
        final String key = trimAuthority(code).toUpperCase();
        final Object value = crsMap.get(key);
        if (value == null) {
            throw noSuchAuthorityCode(CoordinateReferenceSystem.class, code);
        }
        if (value instanceof CoordinateReferenceSystem) {
            return (CoordinateReferenceSystem) value;
        }
        /*
         * Following code is currently for IGNF:MILLER only.
         */
        // Creates the datum
        final Map<String,Object> properties = new HashMap<>(4);
        properties.put(DefaultGeodeticDatum.NAME_KEY, new NamedIdentifier(Citations.OGC, "GRS80"));
        final DefaultGeodeticDatum datum = new DefaultGeodeticDatum(properties,
                CommonCRS.ETRS89.ellipsoid(), CommonCRS.ETRS89.primeMeridian());

        // Creates the projection.
        final ParameterValueGroup param = factories.getMathTransformFactory().getDefaultParameters((String) value);
        param.parameter("semi_major").setValue(6378137);
        param.parameter("semi_minor").setValue(6378137);
        final Identifier[] identifiers = {
            new NamedIdentifier(Citations.IGNF, "MILLER"),
            new ImmutableIdentifier(Citations.EPSG, "EPSG", "310642901"), // Unofficial
            new ImmutableIdentifier(Citations.EPSG, "EPSG", "54003") // Unofficial
        };
        properties.clear();
        properties.put(ProjectedCRS.NAME_KEY, identifiers[0]);
        properties.put(ProjectedCRS.IDENTIFIERS_KEY, identifiers);
        final ProjectedCRS crs = factories.getCRSFactory().createProjectedCRS(properties,
                new DefaultGeographicCRS(IdentifiedObjects.getProperties(datum), datum, PredefinedCS.GEODETIC_2D),
                new DefiningConversion("Miller", param), PredefinedCS.PROJECTED);
        crsMap.put(key, crs);
        return crs;
    }
}
