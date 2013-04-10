/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2006-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.referencing.factory.web;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.LogRecord;
import net.jcip.annotations.Immutable;

import org.opengis.referencing.AuthorityFactory;
import org.opengis.referencing.NoSuchAuthorityCodeException;

import org.apache.sis.util.Version;
import org.apache.sis.util.Classes;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Loggings;
import org.geotoolkit.referencing.factory.AbstractAuthorityFactory;


/**
 * Splits a URN into its {@link #type} and {@link #version} parts for {@link URN_AuthorityFactory}.
 * This class must be immutable in order to avoid the need for synchronization in the authority
 * factory.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.4
 * @module
 */
@Immutable
final class URN_Parser {
    /**
     * The beginning parts of the URN, typically {@code "urn:ogc:def:"} and {@code "urn:x-ogc:def:"}.
     * All elements in the array are treated as synonymous. Those parts are up to, but do not
     * include, de type part ({@code "crs"}, {@code "cs"}, {@code "datum"}, <i>etc.</i>).
     * They must include a trailing (@value #SEPARATOR} character.
     */
    private static final String[] URN_BASES = new String[] {
        "urn:ogc:def:",
        "urn:x-ogc:def:"
    };

    /**
     * The parts separator in the URN.
     */
    private static final char SEPARATOR = ':';

    /**
     * The parsed code as full URN.
     */
    public final String urn;

    /**
     * The type part of the URN ({@code "crs"}, {@code "cs"}, {@code "datum"}, <i>etc</i>).
     */
    public final URN_Type type;

    /**
     * The authority part of the URN (typically {@code "EPSG"}).
     */
    public final String authority;

    /**
     * The version part of the URN, or {@code null} if none.
     */
    public final Version version;

    /**
     * The code part of the URN.
     */
    public final String code;

    /**
     * Parses the specified URN.
     *
     * @param urn The URN to parse.
     * @throws NoSuchAuthorityCodeException if the URN syntax is invalid.
     *
     * @todo Implementation should be replaced by some mechanism using {@code GenericName}
     *       (at least the call to {@code String.regionMatches}) otherwise this method will
     *       fails if there is spaces around the separator.
     */
    public URN_Parser(final String urn) throws NoSuchAuthorityCodeException {
        this.urn = urn;
        final String code = urn.trim();
        String type = urn; // To be really assigned later.
        for (int i=0; i<URN_BASES.length; i++) {
            final String urnBase = URN_BASES[i];
            final int typeStart = urnBase.length();
            if (code.regionMatches(true, 0, urnBase, 0, typeStart)) {
                final int typeEnd = code.indexOf(SEPARATOR, typeStart);
                if (typeEnd >= 0) {
                    type = code.substring(typeStart, typeEnd).trim();
                    final URN_Type candidate = URN_Type.getInstance(type);
                    if (candidate != null) {
                        final int nameEnd = code.indexOf(SEPARATOR, typeEnd + 1);
                        if (nameEnd >= 0) {
                            final int lastEnd = code.lastIndexOf(SEPARATOR);
                            this.version   = (lastEnd <= nameEnd) ? null
                                           : new Version(code.substring(nameEnd + 1, lastEnd));
                            this.authority = code.substring(typeEnd + 1, nameEnd).trim();
                            this.code      = code.substring(lastEnd + 1).trim();
                            this.type      = candidate;
                            return;
                        }
                    }
                }
            }
        }
        throw new NoSuchAuthorityCodeException(
                Errors.format(Errors.Keys.ILLEGAL_IDENTIFIER_$1, type), "urn:ogc:def", type, urn);
    }

    /**
     * Returns the concatenation of the {@linkplain #authority} and the {@linkplain #code}.
     */
    public String getAuthorityCode() {
        return authority + SEPARATOR + code;
    }

    /**
     * Checks if the type is compatible with the expected one. This method is used as a safety
     * by {@code getFooAuthorityFactory(String)} methods in {@link URN_AuthorityFactory}. If a
     * mismatch is found, a warning is logged but no exception is thrown since it doesn't prevent
     * the class to work in a predicable way. It is just an indication for the user that his URN
     * may be wrong.
     */
    final void logWarningIfTypeMismatch(final Class<? extends AuthorityFactory> expected) {
        if (!expected.isAssignableFrom(type.factoryType)) {
            // Build a simplified URN, omitting "urn:ogc:def" and version number.
            final String urn = "..." + SEPARATOR + type + SEPARATOR + authority + SEPARATOR + code;
            final LogRecord record = Loggings.format(Level.WARNING, Loggings.Keys.MISMATCHED_URN_TYPE_$1, urn);
            // Set the source to the public or protected method.
            record.setSourceClassName(URN_AuthorityFactory.class.getName());
            record.setSourceMethodName("get" + Classes.getShortName(expected));
            final Logger logger = AbstractAuthorityFactory.LOGGER;
            record.setLoggerName(logger.getName());
            logger.log(record);
        }
    }

    /**
     * Returns the URN.
     */
    @Override
    public String toString() {
        return urn;
    }
}
