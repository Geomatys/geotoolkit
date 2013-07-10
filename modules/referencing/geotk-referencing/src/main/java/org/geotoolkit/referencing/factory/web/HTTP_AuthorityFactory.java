/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
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

import net.jcip.annotations.ThreadSafe;

import org.opengis.util.FactoryException;
import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.cs.CSAuthorityFactory;
import org.opengis.referencing.datum.DatumAuthorityFactory;
import org.opengis.referencing.operation.CoordinateOperationAuthorityFactory;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.resources.Errors;
import org.apache.sis.util.iso.DefaultNameSpace;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.referencing.factory.AllAuthoritiesFactory;
import org.geotoolkit.referencing.factory.AuthorityFactoryAdapter;
import org.geotoolkit.referencing.factory.AbstractAuthorityFactory;
import org.geotoolkit.referencing.factory.IdentifiedObjectFinder;


/**
 * Wraps {@linkplain AllAuthoritiesFactory all factories} in a {@code "http://www.opengis.net/"}
 * name space. Example of complete URL:
 *
 * {@preformat text
 *     http://www.opengis.net/gml/srs/epsg.xml#4326
 * }
 *
 * Users don't need to create an instance of this class, since one is automatically
 * registered for use in {@link org.opengis.referencing.ReferencingFactoryFinder}.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.07
 *
 * @since 2.4
 * @module
 */
@ThreadSafe
public class HTTP_AuthorityFactory extends AuthorityFactoryAdapter implements CRSAuthorityFactory,
        CSAuthorityFactory, DatumAuthorityFactory, CoordinateOperationAuthorityFactory
{
    /**
     * The base URL, which is {@value}.
     */
    public static final String BASE_URL = "http://www.opengis.net/gml/srs/";

    /**
     * The backing factory.
     */
    private final AllAuthoritiesFactory factory;

    /**
     * Creates a default wrapper.
     */
    public HTTP_AuthorityFactory() {
        this(EMPTY_HINTS);
    }

    /**
     * Creates a wrapper using the specified hints. For strict compliance with OGC
     * definition of CRS defined by URL, the supplied hints should contains at least the
     * {@link Hints#FORCE_LONGITUDE_FIRST_AXIS_ORDER FORCE_LONGITUDE_FIRST_AXIS_ORDER} hint
     * with value {@link Boolean#FALSE FALSE}.
     *
     * @param userHints The hints to be given to backing factories.
     */
    public HTTP_AuthorityFactory(final Hints userHints) {
        this(AllAuthoritiesFactory.getInstance(removeIgnoredHints(userHints, "http")));
    }

    /**
     * Creates a wrapper around the specified factory. The supplied factory is given unchanged
     * to the {@linkplain AuthorityFactoryAdapter#AuthorityFactoryAdapter(AuthorityFactory)
     * super class constructor}.
     *
     * @param factory The factory on which to delegate object creation.
     */
    public HTTP_AuthorityFactory(final AllAuthoritiesFactory factory) {
        super(factory);
        this.factory = factory;
    }

    /**
     * Overrides the {@code FORCE_LONGITUDE_FIRST_AXIS_ORDER} hint if it should keep its standard
     * value while {@code FactoryRegistry} is checking if the hints of a factory are suitable for
     * user's requirement.
     */
    static Hints removeIgnoredHints(Hints hints, final String authority) {
        if (!forceAxisOrderHonoring(hints, authority)) {
            hints = (hints != null ? hints : EMPTY_HINTS).clone();
            hints.put(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.FALSE);
        }
        return hints;
    }

    /**
     * Returns {@code true} if {@link Hints#FORCE_AXIS_ORDER_HONORING}
     * contains a value for the specified authority.
     *
     * @param  hints The hints to use (may be {@code null}).
     * @param  authority The authority factory under creation.
     * @return {@code true} if the given authority is enumerated in the
     *         {@code FORCE_AXIS_ORDER_HONORING} hint.
     */
    static boolean forceAxisOrderHonoring(final Hints hints, final String authority) {
        Object value = null;
        if (hints != null) {
            value = hints.get(Hints.FORCE_AXIS_ORDER_HONORING);
        }
        if (value instanceof CharSequence) {
            final String list = value.toString();
            int i = 0;
            while ((i = list.indexOf(authority, i)) >= 0) {
                if (i==0 || !Character.isJavaIdentifierPart(list.charAt(i - 1))) {
                    final int j = i + authority.length();
                    if (j==list.length() || !Character.isJavaIdentifierPart(list.charAt(j))) {
                        // Found the authority in the list: we need to use the user's setting.
                        return true;
                    }
                }
                i++;
            }
        }
        return false;
    }

    /**
     * Returns the authority, which contains the {@code "http://www.opengis.net"} identifier.
     */
    @Override
    public Citation getAuthority() {
        return Citations.HTTP_OGC;
    }

    /**
     * Removes the URL base ({@value #BASE_URL}) from the specified code
     * before to pass it to the wrapped factories.
     *
     * @param  code The code given to this factory.
     * @return The code to give to the underlying factories.
     * @throws FactoryException if the code can't be converted.
     */
    @Override
    protected String toBackingFactoryCode(final String code) throws FactoryException {
        String fragment = code.trim();
        final int length = BASE_URL.length();
        if (fragment.regionMatches(true, 0, BASE_URL, 0, length)) {
            fragment = fragment.substring(length);
            if (fragment.indexOf('/') < 0) {
                final int split = fragment.indexOf('#');
                if (split >= 0 && fragment.indexOf('#', split+1) < 0) {
                    String authority = fragment.substring(0, split).trim();
                    final int ext = authority.lastIndexOf('.');
                    if (ext > 0) {
                        // Removes the extension part (typically ".xml")
                        authority = authority.substring(0, ext);
                    }
                    fragment = fragment.substring(split + 1).trim();
                    fragment = authority + DefaultNameSpace.DEFAULT_SEPARATOR + fragment;
                    return fragment;
                }
            }
        }
        throw new NoSuchAuthorityCodeException(Errors.format(
                Errors.Keys.ILLEGAL_ARGUMENT_2, "code", code), BASE_URL, fragment, code);
    }

    /**
     * Returns a finder which can be used for looking up unidentified objects. The default
     * implementation forwards all method calls to the finder of the underlying
     * {@link AllAuthoritiesFactory}. No additional work is performed, except in the case
     * of the {@link IdentifiedObjectFinder#findIdentifier findIdentifier} method which
     * format the code in a {@value #BASE_URL} syntax.
     *
     * @throws FactoryException if the finder can not be created.
     *
     * @since 3.07
     */
    @Override
    public IdentifiedObjectFinder getIdentifiedObjectFinder(Class<? extends IdentifiedObject> type)
            throws FactoryException
    {
        return new Finder(factory, type);
    }

    /**
     * The same finder than the one for {@link URN_AuthorityFactory},
     * but adapted for the {@link HTTP_AuthorityFactory} namespace.
     */
    private static final class Finder extends FinderAdapter {
        Finder(AbstractAuthorityFactory factory, Class<? extends IdentifiedObject> type) throws FactoryException {
            super(factory, type);
        }

        @Override
        StringBuilder path(IdentifiedObject object, ReferenceIdentifier identifier, String codespace) {
            return new StringBuilder(BASE_URL).append(codespace).append(".xml#");
        }
    }

    /**
     * Returns {@code true} if this factory meets the requirements specified by a map of hints.
     * This information is for {@link org.geotoolkit.factory.FactoryRegistry} usage only.
     *
     * @since 3.00
     */
    @Override
    protected boolean hasCompatibleHints(final Hints hints) {
        return super.hasCompatibleHints(removeIgnoredHints(hints, "http"));
    }

    /**
     * Sets the ordering of this factory relative to other factories. By default
     * {@code HTTP_AuthorityFactory} is selected only if there is no suitable instance
     * of {@link AbstractAuthorityFactory} for user request.
     *
     * @since 3.00
     */
    @Override
    protected void setOrdering(final Organizer organizer) {
        super.setOrdering(organizer);
        organizer.after(AbstractAuthorityFactory.class, true);
    }
}
