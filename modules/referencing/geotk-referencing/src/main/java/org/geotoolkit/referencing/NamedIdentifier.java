/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.referencing;

import java.util.Map;
import java.util.List;
import java.util.Locale;
import java.util.HashMap;
import java.util.Collection;
import java.util.logging.Level;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.opengis.util.NameSpace;
import org.opengis.util.LocalName;
import org.opengis.util.ScopedName;
import org.opengis.util.GenericName;
import org.opengis.util.InternationalString;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.Identifier;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.parameter.InvalidParameterValueException;
import static org.opengis.referencing.IdentifiedObject.NAME_KEY;
import static org.opengis.referencing.IdentifiedObject.REMARKS_KEY;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.Factories;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.naming.DefaultNameFactory;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.util.NullArgumentException;
import org.geotoolkit.util.DefaultInternationalString;
import org.geotoolkit.util.collection.WeakValueHashMap;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Loggings;


/**
 * An identification of a CRS object. The main interface implemented by this class is
 * {@link ReferenceIdentifier}. However, this class also implements {@link GenericName}
 * in order to make it possible to reuse the same identifiers in the list of
 * {@linkplain AbstractIdentifiedObject#getAlias aliases}. Casting an alias's
 * {@code GenericName} to an {@code ReferenceIdentifier} gives access to more
 * informations, like the URL of the authority.
 * <p>
 * The generic name will be infered from {@code ReferenceIdentifier} attributes. More
 * specifically, a {@linkplain ScopedName scoped name} will be created using the shortest
 * authority's {@linkplain Citation#getAlternateTitles alternate titles} (or the
 * {@linkplain Citation#getTitle main title} if there is no alternate titles) as the
 * {@linkplain ScopedName#scope scope}, and the {@linkplain #getCode code} as the
 * {@linkplain ScopedName#tip tip}. This heuristic rule seems raisonable since,
 * according ISO 19115, the {@linkplain Citation#getAlternateTitles alternate titles}
 * often contains abreviation (for example "DCW" as an alternative title for
 * "<cite>Digital Chart of the World</cite>").
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.03
 *
 * @since 2.0
 * @module
 */
public class NamedIdentifier extends DefaultReferenceIdentifier implements GenericName {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = 8474731565582774497L;

    /**
     * A pool of {@link NameSpace} values for given {@link InternationalString}.
     */
    private static final Map<CharSequence,NameSpace> SCOPES =
            new WeakValueHashMap<CharSequence,NameSpace>();

    /**
     * The factory for creating new generic names.
     * Will be obtained when first needed.
     */
    private static DefaultNameFactory nameFactory;
    static {
        Factories.addChangeListener(new ChangeListener() {
            @Override public void stateChanged(ChangeEvent e) {
                nameFactory = null;
            }
        });
    }

    /**
     * Identifier of the version of the associated code space or code, as specified
     * by the code space or code authority. This version is included only when the
     * {@linkplain #getCode code} uses versions. When appropriate, the edition is
     * identified by the effective date, coded using ISO 8601 date format.
     *
     * @see #getVersion
     */
    private String version;

    /**
     * Comments on or information about this identifier, or {@code null} if none.
     *
     * @see #getRemarks
     */
    private InternationalString remarks;

    /**
     * The name of this identifier as a generic name. If {@code null}, will be constructed
     * only when first needed. This field is serialized (instead of being recreated after
     * deserialization) because it may be a user-supplied value.
     */
    private GenericName name;

    /**
     * A private constructor used only by JAXB.
     */
    private NamedIdentifier() {
    }

    /**
     * Constructs an identifier from a set of properties. Keys are strings from the table below.
     * Key are case-insensitive, and leading and trailing spaces are ignored. The map given in
     * argument shall contains at least a {@code "code"} property. Other properties listed in
     * the table below are optional.
     * <p>
     * <table border='1'>
     *   <tr bgcolor="#CCCCFF" class="TableHeadingColor">
     *     <th nowrap>Property name</th>
     *     <th nowrap>Value type</th>
     *     <th nowrap>Value given to</th>
     *   </tr>
     *   <tr>
     *     <td nowrap>&nbsp;{@value org.opengis.metadata.Identifier#CODE_KEY}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link String}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link #getCode}</td>
     *   </tr>
     *   <tr>
     *     <td nowrap>&nbsp;{@value org.opengis.referencing.ReferenceIdentifier#CODESPACE_KEY}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link String}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link #getCodeSpace}</td>
     *   </tr>
     *   <tr>
     *     <td nowrap>&nbsp;{@value org.opengis.metadata.Identifier#AUTHORITY_KEY}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link String} or {@link Citation}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link #getAuthority}</td>
     *   </tr>
     *   <tr>
     *     <td nowrap>&nbsp;{@value org.opengis.referencing.ReferenceIdentifier#VERSION_KEY}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link String}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link #getVersion}</td>
     *   </tr>
     *   <tr>
     *     <td nowrap>&nbsp;{@value org.opengis.referencing.IdentifiedObject#REMARKS_KEY}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link String} or {@link InternationalString}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link #getRemarks}</td>
     *   </tr>
     * </table>
     * <p>
     * {@code "remarks"} is a localizable attributes which may have a language and country
     * code suffix. For example the {@code "remarks_fr"} property stands for remarks in
     * {@linkplain Locale#FRENCH French} and the {@code "remarks_fr_CA"} property stands
     * for remarks in {@linkplain Locale#CANADA_FRENCH French Canadian}.
     *
     * @param  properties The properties to be given to this identifier.
     * @throws InvalidParameterValueException if a property has an invalid value.
     * @throws IllegalArgumentException if a property is invalid for some other reason.
     */
    public NamedIdentifier(final Map<String,?> properties) throws IllegalArgumentException {
        this(properties, true);
    }

    /**
     * Constructs an identifier from an authority and code informations. This is a convenience
     * constructor for commonly-used parameters. If more control are wanted (for example adding
     * remarks), use the {@linkplain #NamedIdentifier(Map) constructor with a properties map}.
     *
     * @param authority
     *          The authority (e.g. {@link Citations#OGC OGC} or {@link Citations#EPSG EPSG}).
     * @param code
     *          The code. The {@link InternationalString#toString(Locale) toString(null)} method
     *          is invoked for the code, and the complete international string is retained for
     *          the {@linkplain GenericName generic name}.
     */
    public NamedIdentifier(final Citation authority, final InternationalString code) {
        // The "null" locale argument is required for getting the unlocalized version.
        this(authority, code.toString(null));
        name = createName(authority, code);
    }

    /**
     * Constructs an identifier from an authority and code informations. This is a convenience
     * constructor for commonly-used parameters. If more control are wanted (for example adding
     * remarks), use the {@linkplain #NamedIdentifier(Map) constructor with a properties map}.
     *
     * @param authority The authority (e.g. {@link Citations#OGC OGC} or {@link Citations#EPSG EPSG}).
     * @param code      The code. This parameter is mandatory.
     */
    public NamedIdentifier(final Citation authority, final String code) {
        this(authority, code, null);
    }

    /**
     * Constructs an identifier from an authority and code informations. This is a convenience
     * constructor for commonly-used parameters. If more control are wanted (for example adding
     * remarks), use the {@linkplain #NamedIdentifier(Map) constructor with a properties map}.
     *
     * @param authority The authority (e.g. {@link Citations#OGC OGC} or {@link Citations#EPSG EPSG}).
     * @param code      The code. This parameter is mandatory.
     * @param version   The version, or {@code null} if none.
     */
    public NamedIdentifier(final Citation authority, final String code, final String version) {
        this(toMap(authority, code, version));
    }

    /**
     * Work around for RFE #4093999 in Sun's bug database
     * ("Relax constraint on placement of this()/super() call in constructors").
     */
    private static Map<String,?> toMap(final Citation authority, final String code, final String version) {
        final Map<String,Object> properties = new HashMap<String,Object>(4);
        if (authority != null) properties.put(AUTHORITY_KEY, authority);
        if (code      != null) properties.put(CODE_KEY,      code);
        if (version   != null) properties.put(VERSION_KEY,   version);
        return properties;
    }

    /**
     * Implementation of the constructor. The remarks in the {@code properties} map will be
     * parsed only if the {@code standalone} argument is set to {@code true}, i.e. this
     * identifier is being constructed as a standalone object. If {@code false}, then this
     * identifier is assumed to be constructed from inside the {@link AbstractIdentifiedObject}
     * constructor.
     *
     * @param properties The properties to parse, as described in the public constructor.
     * @param standalone {@code true} for parsing "remarks" as well.
     *
     * @throws InvalidParameterValueException if a property has an invalid value.
     * @throws IllegalArgumentException if a property is invalid for some other reason.
     */
    NamedIdentifier(final Map<String,?> properties, final boolean standalone)
            throws IllegalArgumentException
    {
        ensureNonNull("properties", properties);
        Object code      = null;
        Object codespace = null;
        Object version   = null;
        Object authority = null;
        Object remarks   = null;
        DefaultInternationalString localized = null;
        /*
         * Iterates through each map entry. This have two purposes:
         *
         *   1) Ignore case (a call to properties.get("foo") can't do that)
         *   2) Find localized remarks.
         *
         * This algorithm is sub-optimal if the map contains a lot of entries of no interest to
         * this identifier. Hopefully, most users will fill a map with only usefull entries.
         */
        String key   = null;
        Object value = null;
        for (final Map.Entry<String,?> entry : properties.entrySet()) {
            key   = entry.getKey().trim().toLowerCase();
            value = entry.getValue();
            /*
             * Note: String.hashCode() is part of J2SE specification,
             *       so it should not change across implementations.
             */
            switch (key.hashCode()) {
                case 3373707: {
                    if (!standalone && key.equals(NAME_KEY)) {
                        code = value;
                        continue;
                    }
                    break;
                }
                case 3059181: {
                    if (key.equals(CODE_KEY)) {
                        code = value;
                        continue;
                    }
                    break;
                }
                case -1108676807: {
                    if (key.equals(CODESPACE_KEY)) {
                        codespace = value;
                        continue;
                    }
                    break;

                }
                case 351608024: {
                    if (key.equals(VERSION_KEY)) {
                        version = value;
                        continue;
                    }
                    break;
                }
                case 1475610435: {
                    if (key.equals(AUTHORITY_KEY)) {
                        if (value instanceof String) {
                            value = Citations.fromName(value.toString());
                        }
                        authority = value;
                        continue;
                    }
                    break;
                }
                case 1091415283: {
                    if (standalone && key.equals(REMARKS_KEY)) {
                        if (value instanceof InternationalString) {
                            remarks = value;
                            continue;
                        }
                    }
                    break;
                }
            }
            /*
             * Searches for additional locales (e.g. "remarks_fr").
             */
            if (standalone && value instanceof String) {
                if (localized == null) {
                    if (remarks instanceof DefaultInternationalString) {
                        localized = (DefaultInternationalString) remarks;
                    } else {
                        localized = new DefaultInternationalString();
                    }
                }
                localized.add(REMARKS_KEY, key, value.toString());
            }
        }
        /*
         * Gets the localized remarks, if it was not yet set. If a user specified remarks
         * both as InternationalString and as String for some locales (which is a weird
         * usage...), then current implementation discarts the later with a warning.
         */
        if (localized!=null && !localized.getLocales().isEmpty()) {
            if (remarks == null) {
                remarks = localized;
            } else {
                Logging.log(NamedIdentifier.class,
                    Loggings.format(Level.WARNING, Loggings.Keys.LOCALES_DISCARTED));
            }
        }
        /*
         * Completes the code space if it was not explicitly set. We take the first
         * identifier if there is any, otherwise we take the shortest title.
         */
        if (codespace == null && authority instanceof Citation) {
            codespace = getCodeSpace((Citation) authority);
        }
        /*
         * Stores the definitive reference to the attributes. Note that casts are performed only
         * there (not before). This is a wanted feature, since we want to catch ClassCastExceptions
         * are rethrown them as more informative exceptions.
         */
        try {
            key=      CODE_KEY; this.code      = (String)              (value = code);
            key=   VERSION_KEY; this.version   = (String)              (value = version);
            key= CODESPACE_KEY; this.codespace = (String)              (value = codespace);
            key= AUTHORITY_KEY; this.authority = (Citation)            (value = authority);
            key=   REMARKS_KEY; this.remarks   = (InternationalString) (value = remarks);
        } catch (ClassCastException exception) {
            InvalidParameterValueException e = new InvalidParameterValueException(
                    Errors.format(Errors.Keys.ILLEGAL_ARGUMENT_$2, key, value), key, value);
            e.initCause(exception);
            throw e;
        }
        ensureNonNull(CODE_KEY, code);
    }

    /**
     * Makes sure an argument is non-null. This is method duplicates
     * {@link AbstractIdentifiedObject#ensureNonNull(String, Object)}
     * except for the more accurate stack trace. It is duplicated there
     * in order to avoid a dependency to {@link AbstractIdentifiedObject}.
     *
     * @param  name   Argument name.
     * @param  object User argument.
     * @throws NullArgumentException if {@code object} is null.
     */
    private static void ensureNonNull(final String name, final Object object)
            throws NullArgumentException
    {
        if (object == null) {
            throw new NullArgumentException(Errors.format(Errors.Keys.NULL_ARGUMENT_$1, name));
        }
    }

    /**
     * Identifier code or name, optionally from a controlled list or pattern.
     *
     * @return The code, never {@code null}.
     * @category Identifier
     *
     * @see #tip
     */
    @Override
    public String getCode() {
        return code;
    }

    /**
     * Name or identifier of the person or organization responsible for namespace.
     *
     * @return The codespace, or {@code null} if not available.
     * @category Identifier
     *
     * @see #head
     * @see #scope
     */
    @Override
    public String getCodeSpace() {
        return codespace;
    }

    /**
     * Organization or party responsible for definition and maintenance of the
     * {@linkplain #getCode code}.
     *
     * @return The authority, or {@code null} if not available.
     * @category Identifier
     *
     * @see Citations#EPSG
     */
    @Override
    public Citation getAuthority() {
        return authority;
    }

    /**
     * Identifier of the version of the associated code space or code, as specified by the
     * code authority. This version is included only when the {@linkplain #getCode code}
     * uses versions. When appropriate, the edition is identified by the effective date,
     * coded using ISO 8601 date format.
     *
     * @return The version, or {@code null} if not available.
     * @category Identifier
     */
    @Override
    public String getVersion() {
        return version;
    }

    /**
     * Comments on or information about this identifier, or {@code null} if none.
     *
     * @return Optional comments about this identifier.
     * @category Identifier
     */
    public InternationalString getRemarks() {
        return remarks;
    }

    /**
     * Returns the generic name of this identifier. The name will be constructed
     * automatically the first time it will be needed. The name's scope is infered
     * from the shortest alternative title (if any). This heuristic rule seems raisonable
     * since, according ISO 19115, the {@linkplain Citation#getAlternateTitles alternate
     * titles} often contains abreviation (for example "DCW" as an alternative title for
     * "Digital Chart of the World"). If no alternative title is found or if the main title
     * is yet shorter, then it is used.
     *
     * @category Generic name
     */
    private synchronized GenericName getName() {
        if (name == null) {
            name = createName(authority, code);
        }
        return name;
    }

    /**
     * Constructs a generic name from the specified authority and code.
     *
     * @param  authority The authority, or {@code null} if none.
     * @param  code The code.
     * @return A new generic name for the given authority and code.
     * @category Generic name
     */
    private GenericName createName(final Citation authority, final CharSequence code) {
        final DefaultNameFactory factory = getNameFactory();
        if (authority == null) {
            return factory.createLocalName(null, code);
        }
        final CharSequence title;
        if (codespace != null) {
            title = codespace;
        } else {
            title = getShortestTitle(authority);
        }
        NameSpace scope;
        synchronized (SCOPES) {
            scope = SCOPES.get(title);
            if (scope == null) {
                scope = factory.createNameSpace(factory.createLocalName(null, title));
                SCOPES.put(title, scope);
            }
        }
        return factory.createLocalName(scope, code).toFullyQualifiedName();
    }

    /**
     * Returns the name factory to be used for creating default names when the user did not
     * provides them explicitly. We use the Geotoolkit implementation for making sure that we
     * integrate well with the referencing module, and also for a few extra functionalities.
     *
     * @return The name factory.
     * @category Generic name
     */
    static DefaultNameFactory getNameFactory() {
        // No need to synchronize; this is not a big deal if we ask twice.
        DefaultNameFactory factory = nameFactory;
        if (factory == null) {
            nameFactory = factory = (DefaultNameFactory) FactoryFinder.getNameFactory(
                    new Hints(Hints.NAME_FACTORY, DefaultNameFactory.class));
        }
        return factory;
    }

    /**
     * Returns the shortest title inferred from the specified authority.
     * This is used both for creating a generic name, or for inferring a
     * default identifier code space.
     */
    private static InternationalString getShortestTitle(final Citation authority) {
        InternationalString title = authority.getTitle();
        int length = title.length();
        final Collection<? extends InternationalString> alt = authority.getAlternateTitles();
        if (alt != null) {
            for (final InternationalString candidate : alt) {
                final int candidateLength = candidate.length();
                if (candidateLength > 0 && candidateLength < length) {
                    title = candidate;
                    length = candidateLength;
                }
            }
        }
        return title;
    }

    /**
     * Tries to get a codespace from the specified authority. This method scans first
     * through the identifier, then through the titles if no suitable identifier were found.
     */
    private static String getCodeSpace(final Citation authority) {
        final Collection<? extends Identifier> identifiers = authority.getIdentifiers();
        if (identifiers != null) {
            for (final Identifier id : identifiers) {
                final String identifier = id.getCode();
                if (isValidCodeSpace(identifier)) {
                    return identifier;
                }
            }
        }
        // The "null" locale argument is required for getting the unlocalized version.
        final String title = getShortestTitle(authority).toString(null);
        if (isValidCodeSpace(title)) {
            return title;
        }
        return null;
    }

    /**
     * Returns {@code true} if the specified string looks like a valid code space.
     * This method, together with {@link #getShortestTitle}, uses somewhat heuristic
     * rules that may change in future Geotoolkit versions.
     */
    private static boolean isValidCodeSpace(final String codespace) {
        if (codespace == null) {
            return false;
        }
        for (int i=codespace.length(); --i>=0;) {
            if (!Character.isJavaIdentifierPart(codespace.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * The last element in the sequence of {@linkplain #getParsedNames parsed names}.
     * By default, this is the same value than {@link #getCode} provided as a local name.
     *
     * @category Generic name
     *
     * @see #getCode
     */
    @Override
    public LocalName tip() {
        return getName().tip();
    }

    /**
     * Returns the first element in the sequence of {@linkplain #getParsedNames parsed names}.
     * By default, this is the same value than {@link #getCodeSpace} provided as a local name.
     *
     * @category Generic name
     *
     * @see #scope
     * @see #getCodeSpace
     */
    @Override
    public LocalName head() {
        return getName().head();
    }

    /**
     * Returns the scope (name space) in which this name is local.
     * By default, this is the same value than {@link #getCodeSpace} provided as a name space.
     *
     * @category Generic name
     *
     * @see #head
     * @see #getCodeSpace
     *
     * @since 2.3
     */
    @Override
    public NameSpace scope() {
        return getName().scope();
    }

    /**
     * Returns the depth of this name within the namespace hierarchy.
     *
     * @category Generic name
     *
     * @since 2.3
     */
    @Override
    public int depth() {
        return getName().depth();
    }

    /**
     * Returns the sequence of {@linkplain LocalName local names} making this generic name.
     * The length of this sequence is the {@linkplain #depth depth}. It does not include the
     * {@linkplain #scope scope}.
     *
     * @category Generic name
     */
    @Override
    public List<? extends LocalName> getParsedNames() {
        return getName().getParsedNames();
    }

    /**
     * Returns this name expanded with the specified scope. One may represent this operation
     * as a concatenation of the specified {@code name} with {@code this}.
     *
     * @category Generic name
     *
     * @since 2.3
     */
    @Override
    public ScopedName push(final GenericName scope) {
        return getName().push(scope);
    }

    /**
     * Returns a view of this name as a fully-qualified name.
     *
     * @category Generic name
     *
     * @since 2.3
     */
    @Override
    public GenericName toFullyQualifiedName() {
        return getName().toFullyQualifiedName();
    }

    /**
     * Returns a local-dependent string representation of this generic name. This string
     * is similar to the one returned by {@link #toString} except that each element has
     * been localized in the {@linkplain InternationalString#toString(Locale) specified locale}.
     * If no international string is available, then this method returns an implementation mapping
     * to {@link #toString} for all locales.
     */
    @Override
    public InternationalString toInternationalString() {
        return getName().toInternationalString();
    }

    /**
     * Returns a string representation of this generic name. This string representation
     * is local-independant. It contains all elements listed by {@link #getParsedNames}
     * separated by an arbitrary character (usually {@code :} or {@code /}).
     */
    @Override
    public String toString() {
        return getName().toString();
    }

    /**
     * Compares this name with the specified object for order. Returns a negative integer,
     * zero, or a positive integer as this name lexicographically precedes, is equal to,
     * or follows the specified object.
     *
     * @param object The object to compare with.
     * @return -1 if this identifier precedes the given object, +1 if it follows it.
     */
    @Override
    public int compareTo(final GenericName object) {
        return getName().compareTo(object);
    }

    /**
     * Compares this identifier with the specified object for equality.
     *
     * @param object The object to compare with this name.
     * @return {@code true} if the given object is equal to this name.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (super.equals(object)) {
            final NamedIdentifier that = (NamedIdentifier) object;
            return Utilities.equals(this.version, that.version) &&
                   Utilities.equals(this.remarks, that.remarks);
        }
        return false;
    }

    /**
     * Returns a hash code value for this identifier.
     */
    @Override
    public int hashCode() {
        int hash = (int) serialVersionUID;
        if (code != null) {
            hash ^= code.hashCode();
        }
        if (version != null) {
            hash = hash*31 + version.hashCode();
        }
        return hash;
    }
}
