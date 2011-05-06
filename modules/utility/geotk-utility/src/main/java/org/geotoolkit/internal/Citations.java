/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.internal;

import java.util.Collection;
import java.util.Iterator;

import org.opengis.metadata.Identifier;
import org.opengis.metadata.citation.Citation;
import org.opengis.util.InternationalString;

import org.geotoolkit.lang.Static;


/**
 * Utility methods working on {@link Citation} objects. Those methods were used to be defined
 * in the {@link org.geotoolkit.metadata.iso.citation.Citations} class, but moved there since
 * they are needed by {@link org.geotoolkit.factory.FactoryFinder}.
 * <p>
 * For the public facade of those methods, see
 * {@link org.geotoolkit.metadata.iso.citation.Citations}.
 *
 * @author Martin Desruisseaux (IRD)
 * @author Jody Garnett (Refractions)
 * @version 3.18
 *
 * @since 3.04 (derived from 2.2)
 * @module
 */
public final class Citations extends Static {
    /**
     * Do not allows instantiation of this class.
     */
    private Citations() {
    }

    /**
     * Returns {@code true} if at least one {@linkplain Citation#getTitle title} or
     * {@linkplain Citation#getAlternateTitles alternate title} in {@code c1} is equal to a title
     * or alternate title in {@code c2}. The comparison is case-insensitive and ignores leading
     * and trailing spaces. The titles ordering is not significant.
     *
     * @param  c1 The first citation to compare.
     * @param  c2 the second citation to compare.
     * @return {@code true} if at least one title or alternate title matches.
     */
    public static boolean titleMatches(final Citation c1, final Citation c2) {
        InternationalString candidate = c2.getTitle();
        Iterator<? extends InternationalString> iterator = null;
        do {
            // The "null" locale argument is required for getting the unlocalized version.
            final String asString = candidate.toString(null);
            if (titleMatches(c1, asString)) {
                return true;
            }
            final String asLocalized = candidate.toString();
            // The identity Strings comparison below is just an optimization.
            if (asLocalized != asString && titleMatches(c1, asLocalized)) { // NOSONAR (see above)
                return true;
            }
            if (iterator == null) {
                final Collection<? extends InternationalString> titles = c2.getAlternateTitles();
                if (titles == null) {
                    break;
                }
                iterator = titles.iterator();
            }
            if (!iterator.hasNext()) {
                break;
            }
            candidate = iterator.next();
        } while (true);
        return false;
    }

    /**
     * Returns {@code true} if the {@linkplain Citation#getTitle title} or any
     * {@linkplain Citation#getAlternateTitles alternate title} in the given citation
     * matches the given string. The comparison is case-insensitive and ignores leading
     * and trailing spaces.
     *
     * @param  citation The citation to check for.
     * @param  title The title or alternate title to compare.
     * @return {@code true} if the title or alternate title matches the given string.
     */
    public static boolean titleMatches(final Citation citation, String title) {
        title = title.trim();
        InternationalString candidate = citation.getTitle();
        Iterator<? extends InternationalString> iterator = null;
        do {
            // The "null" locale argument is required for getting the unlocalized version.
            final String asString = candidate.toString(null);
            if (asString.trim().equalsIgnoreCase(title)) {
                return true;
            }
            final String asLocalized = candidate.toString();
            // The identity Strings comparison below is just an optimization.
            if (asLocalized != asString && asLocalized.trim().equalsIgnoreCase(title)) { // NOSONAR (see above)
                return true;
            }
            if (iterator == null) {
                final Collection<? extends InternationalString> titles = citation.getAlternateTitles();
                if (titles == null) {
                    break;
                }
                iterator = titles.iterator();
            }
            if (!iterator.hasNext()) {
                break;
            }
            candidate = iterator.next();
        } while (true);
        return false;
    }

    /**
     * Returns {@code true} if at least one {@linkplain Citation#getIdentifiers identifier} in
     * {@code c1} is equal to an identifier in {@code c2}. The comparison is case-insensitive
     * and ignores leading and trailing spaces. The identifier ordering is not significant.
     * <p>
     * If (and <em>only</em> if) the citations do not contains any identifier, then this method
     * fallback on titles comparison using the {@link #titleMatches(Citation,Citation)
     * titleMatches} method. This fallback exists for compatibility with client codes using
     * citation {@linkplain Citation#getTitle titles} without identifiers.
     *
     * @param  c1 The first citation to compare.
     * @param  c2 the second citation to compare.
     * @return {@code true} if at least one identifier, title or alternate title matches.
     */
    public static boolean identifierMatches(Citation c1, Citation c2) {
        if (c1 == c2) {
            return true; // Optimisation for a common case.
        }
        /*
         * If there is no identifier in both citations, fallback on title comparisons. If there is
         * identifiers in only one citation, make sure that this citation is the second one (c2) in
         * order to allow at least one call to 'identifierMatches(c1, String)'.
         */
        Iterator<? extends Identifier> iterator = c2.getIdentifiers().iterator();
        if (!iterator.hasNext()) {
            iterator = c1.getIdentifiers().iterator();
            if (!iterator.hasNext()) {
                return titleMatches(c1, c2);
            }
            c1 = c2;
            c2 = null; // NOSONAR: Just for make sure that we don't use it by accident.
        }
        do {
            final String id = iterator.next().getCode().trim();
            if (identifierMatches(c1, id)) {
                return true;
            }
        } while (iterator.hasNext());
        return false;
    }

    /**
     * Returns {@code true} if any {@linkplain Citation#getIdentifiers identifiers} in the given
     * citation matches the given string. The comparison is case-insensitive and ignores leading
     * and trailing spaces. If (and <em>only</em> if) the citation do not contains any identifier,
     * then this method fallback on titles comparison using the {@link #titleMatches(Citation,
     * String) titleMatches} method. This fallback exists for compatibility with client codes using
     * citation {@linkplain Citation#getTitle titles} without identifiers.
     *
     * @param  citation The citation to check for.
     * @param  identifier The identifier to compare.
     * @return {@code true} if the title or alternate title matches the given string.
     */
    public static boolean identifierMatches(final Citation citation, String identifier) {
        identifier = identifier.trim();
        final Collection<? extends Identifier> identifiers = citation.getIdentifiers();
        if (identifiers != null && !identifiers.isEmpty()) {
            for (final Identifier id : identifiers) {
                final String code = id.getCode().trim();
                if (identifier.equalsIgnoreCase(code)) {
                    return true;
                }
            }
            return false;
        }
        return titleMatches(citation, identifier);
    }

    /**
     * Returns the shortest identifier for the specified citation, or the title if there is
     * no identifier. This method is useful for extracting the namespace from an authority,
     * for example {@code "EPSG"}.
     *
     * @param  citation The citation for which to get the identifier, or {@code null}.
     * @return The shortest identifier of the given citation, or {@code null} if the
     *         given citation was null.
     *
     * @since 2.4
     */
    public static String getIdentifier(final Citation citation) {
        String identifier = null;
        if (citation != null) {
            for (final Identifier id : citation.getIdentifiers()) {
                final String candidate = id.getCode().trim();
                final int length = candidate.length();
                if (length != 0) {
                    if (identifier == null || length < identifier.length()) {
                        identifier = candidate;
                    }
                }
            }
            if (identifier == null) {
                identifier = String.valueOf(citation.getTitle());
            }
        }
        return identifier;
    }
}
