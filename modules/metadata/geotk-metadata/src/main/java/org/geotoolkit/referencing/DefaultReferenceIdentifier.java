/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.referencing;

import java.util.Map;
import java.util.Locale;
import java.util.Collection;
import java.util.Objects;
import java.util.logging.Level;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import net.jcip.annotations.Immutable;

import org.opengis.metadata.Identifier;
import org.opengis.metadata.citation.Citation;
import org.opengis.parameter.InvalidParameterValueException;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.util.InternationalString;

import org.geotoolkit.xml.Namespaces;
import org.apache.sis.util.Locales;
import org.apache.sis.util.Deprecable;
import org.apache.sis.util.iso.DefaultInternationalString;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.apache.sis.internal.jaxb.metadata.CI_Citation;
import org.apache.sis.internal.jaxb.metadata.ReferenceSystemMetadata;
import org.apache.sis.internal.jaxb.gco.StringAdapter;
import org.geotoolkit.resources.Loggings;
import org.geotoolkit.resources.Errors;

import static org.apache.sis.util.ArgumentChecks.ensureNonNull;
import static org.opengis.referencing.IdentifiedObject.NAME_KEY;
import static org.opengis.referencing.IdentifiedObject.REMARKS_KEY;


/**
 * An identification of a {@link org.opengis.referencing.crs.CoordinateReferenceSystem} object.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.03
 *
 * @since 3.03 (derived from 2.6)
 * @module
 */
@Immutable
@XmlRootElement(name = "RS_Identifier", namespace = Namespaces.GMD)
public class DefaultReferenceIdentifier implements ReferenceIdentifier, Deprecable, Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 2004263079254434562L;

    /**
     * Identifier code or name, optionally from a controlled list or pattern defined by a code space.
     *
     * @see #getCode()
     */
    @XmlElement(required = true, namespace = Namespaces.GMD)
    @XmlJavaTypeAdapter(StringAdapter.class)
    final String code;

    /**
     * Name or identifier of the person or organization responsible for namespace, or
     * {@code null} if not available. This is often an abbreviation of the authority name.
     *
     * @see #getCodeSpace()
     */
    @XmlElement(required = true, namespace = Namespaces.GMD)
    @XmlJavaTypeAdapter(StringAdapter.class)
    final String codeSpace;

    /**
     * Organization or party responsible for definition and maintenance of the code space or code,
     * or {@code null} if not available.
     *
     * @see #getAuthority()
     */
    @XmlElement(required = true, namespace = Namespaces.GMD)
    @XmlJavaTypeAdapter(CI_Citation.class)
    final Citation authority;

    /**
     * Identifier of the version of the associated code space or code as specified
     * by the code space or code authority, or {@code null} if not available. This
     * version is included only when the {@linkplain #getCode code} uses versions.
     * When appropriate, the edition is identified by the effective date, coded using
     * ISO 8601 date format.
     *
     * @see #getVersion()
     */
    @XmlElement(namespace = Namespaces.GMD)
    private final String version;

    /**
     * Comments on or information about this identifier, or {@code null} if none.
     *
     * @see #getRemarks
     */
    private final InternationalString remarks;

    /**
     * Empty constructor for JAXB.
     */
    private DefaultReferenceIdentifier() {
        code      = null;
        codeSpace = null;
        authority = null;
        version   = null;
        remarks   = null;
    }

    /**
     * Creates a new identifier from the specified one. This is a copy constructor
     * which will get the code, codespace, authority, version and (if available)
     * the remarks from the given identifier.
     *
     * @param identifier The identifier to copy.
     */
    public DefaultReferenceIdentifier(final ReferenceIdentifier identifier) {
        ensureNonNull("identifier", identifier);
        code      = identifier.getCode();
        codeSpace = identifier.getCodeSpace();
        authority = identifier.getAuthority();
        version   = identifier.getVersion();
        if (identifier instanceof DefaultReferenceIdentifier) {
            remarks = ((DefaultReferenceIdentifier) identifier).getRemarks();
        } else {
            remarks = null;
        }
    }

    /**
     * Creates a new identifier from the specified code and authority.
     *
     * @param authority
     *          Organization or party responsible for definition and maintenance of the code
     *          space or code.
     * @param codeSpace
     *          Name or identifier of the person or organization responsible for namespace.
     *          This is often an abbreviation of the authority name.
     * @param code
     *          Identifier code or name, optionally from a controlled list or pattern defined by
     *          a code space. The code can not be null.
     */
    public DefaultReferenceIdentifier(final Citation authority, final String codeSpace, final String code) {
        this(authority, codeSpace, code, null, null);
    }

    /**
     * Creates a new identifier from the specified code and authority, with an optional
     * version number and remarks.
     *
     * @param authority
     *          Organization or party responsible for definition and maintenance of the code
     *          space or code, or {@code null} if not available.
     * @param codeSpace
     *          Name or identifier of the person or organization responsible for namespace, or
     *          {@code null} if not available. This is often an abbreviation of the authority name.
     * @param code
     *          Identifier code or name, optionally from a controlled list or pattern defined by
     *          a code space. The code can not be null.
     * @param version
     *          The version of the associated code space or code as specified by the code authority,
     *          or {@code null} if none.
     * @param remarks
     *          Comments on or information about this identifier, or {@code null} if none.
     */
    public DefaultReferenceIdentifier(final Citation authority, final String codeSpace, final String code,
            final String version, final InternationalString remarks)
    {
        ensureNonNull("code", code);
        this.code      = code;
        this.codeSpace = codeSpace;
        this.authority = authority;
        this.version   = version;
        this.remarks   = remarks;
    }

    /**
     * Constructs an identifier from a set of properties. Keys are strings from the table below.
     * Keys are case-insensitive, and leading and trailing spaces are ignored. The map given in
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
     *     <td nowrap>&nbsp;{@link #getCode()}</td>
     *   </tr>
     *   <tr>
     *     <td nowrap>&nbsp;{@value org.opengis.referencing.ReferenceIdentifier#CODESPACE_KEY}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link String}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link #getCodeSpace()}</td>
     *   </tr>
     *   <tr>
     *     <td nowrap>&nbsp;{@value org.opengis.metadata.Identifier#AUTHORITY_KEY}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link String} or {@link Citation}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link #getAuthority()}</td>
     *   </tr>
     *   <tr>
     *     <td nowrap>&nbsp;{@value org.opengis.referencing.ReferenceIdentifier#VERSION_KEY}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link String}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link #getVersion()}</td>
     *   </tr>
     *   <tr>
     *     <td nowrap>&nbsp;{@value org.opengis.referencing.IdentifiedObject#REMARKS_KEY}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link String} or {@link InternationalString}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link #getRemarks()}</td>
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
    public DefaultReferenceIdentifier(final Map<String,?> properties) throws IllegalArgumentException {
        this(properties, true);
    }

    /**
     * Implementation of the constructor. The remarks in the {@code properties} map will be
     * parsed only if the {@code standalone} argument is set to {@code true}, i.e. this
     * identifier is being constructed as a standalone object. If {@code false}, then this
     * identifier is assumed to be constructed from inside the {@link AbstractIdentifiedObject}
     * constructor.
     *
     * @param  properties The properties to parse, as described in the public constructor.
     * @param  standalone {@code true} for parsing "remarks" as well.
     * @throws InvalidParameterValueException if a property has an invalid value.
     * @throws IllegalArgumentException if a property is invalid for some other reason.
     */
    DefaultReferenceIdentifier(final Map<String,?> properties, final boolean standalone)
            throws IllegalArgumentException
    {
        ensureNonNull("properties", properties);
        Object code      = null;
        Object codeSpace = null;
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
         * this identifier. Hopefully, most users will fill a map with only useful entries.
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
                        codeSpace = value;
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
                final Locale locale = Locales.parseSuffix(REMARKS_KEY, key);
                if (locale != null) {
                    localized.add(locale, value.toString());
                }
            }
        }
        /*
         * Gets the localized remarks, if it was not yet set. If a user specified remarks
         * both as InternationalString and as String for some locales (which is a weird
         * usage...), then current implementation discards the later with a warning.
         */
        if (localized!=null && !localized.getLocales().isEmpty()) {
            if (remarks == null) {
                remarks = localized;
            } else {
                Logging.log(DefaultReferenceIdentifier.class, "<init>",
                    Loggings.format(Level.WARNING, Loggings.Keys.LOCALES_DISCARTED));
            }
        }
        /*
         * Completes the code space if it was not explicitly set. We take the first
         * identifier if there is any, otherwise we take the shortest title.
         */
        if (codeSpace == null && authority instanceof Citation) {
            codeSpace = getCodeSpace((Citation) authority);
        }
        /*
         * Stores the definitive reference to the attributes. Note that casts are performed only
         * there (not before). This is a wanted feature, since we want to catch ClassCastExceptions
         * are rethrown them as more informative exceptions.
         */
        try {
            key=      CODE_KEY; this.code      = (String)              (value = code);
            key=   VERSION_KEY; this.version   = (String)              (value = version);
            key= CODESPACE_KEY; this.codeSpace = (String)              (value = codeSpace);
            key= AUTHORITY_KEY; this.authority = (Citation)            (value = authority);
            key=   REMARKS_KEY; this.remarks   = (InternationalString) (value = remarks);
        } catch (ClassCastException exception) {
            throw new InvalidParameterValueException(
                    Errors.format(Errors.Keys.ILLEGAL_ARGUMENT_2, key, value), exception, key, value);
        }
        ensureNonNull(CODE_KEY, code);
    }

    /**
     * Returns the shortest title inferred from the specified authority.
     * This is used both for creating a generic name, or for inferring a
     * default identifier code space.
     */
    static InternationalString getShortestTitle(final Citation authority) {
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
     * Tries to get a code space from the specified authority. This method scans first
     * through the identifier, then through the titles if no suitable identifier were found.
     */
    static String getCodeSpace(final Citation authority) {
        if (authority != null) {
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
        }
        return null;
    }

    /**
     * Returns {@code true} if the specified string looks like a valid code space.
     * This method, together with {@link #getShortestTitle}, uses somewhat heuristic
     * rules that may change in future Geotk versions.
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
     * Identifier code or name, optionally from a controlled list or pattern.
     *
     * @return The code, never {@code null}.
     *
     * @see NamedIdentifier#tip
     */
    @Override
    public String getCode() {
        return code;
    }

    /**
     * Name or identifier of the person or organization responsible for namespace.
     *
     * @return The code space, or {@code null} if not available.
     *
     * @see NamedIdentifier#head
     * @see NamedIdentifier#scope
     */
    @Override
    public String getCodeSpace() {
        return codeSpace;
    }

    /**
     * Organization or party responsible for definition and maintenance of the
     * {@linkplain #getCode code}.
     *
     * @return The authority, or {@code null} if not available.
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
     */
    @Override
    public String getVersion() {
        return version;
    }

    /**
     * Comments on or information about this identifier, or {@code null} if none.
     *
     * @return Optional comments about this identifier.
     */
    public InternationalString getRemarks() {
        return remarks;
    }

    /**
     * Returns {@code true} if the object represented by this identifier is deprecated. In such
     * case, the {@linkplain #getRemarks() remarks} may contains the new identifier to use.
     * <p>
     * The default implementation returns {@code false} in all cases.
     *
     * @see AbstractIdentifiedObject#isDeprecated()
     *
     * @return {@code true} if this code is deprecated.
     */
    @Override
    public boolean isDeprecated() {
        return false;
    }

    /**
     * Returns a hash code value for this object.
     */
    @Override
    public int hashCode() {
        int hash = (int) serialVersionUID;
        if (code != null) {
            hash ^= code.hashCode();
        }
        if (codeSpace != null) {
            hash = hash*31 + codeSpace.hashCode();
        }
        return hash;
    }

    /**
     * Compares this object with the given one for equality.
     *
     * @param object The object to compare with this identifier.
     * @return {@code true} if both objects are equal.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object != null && object.getClass() == getClass()) {
            final DefaultReferenceIdentifier that = (DefaultReferenceIdentifier) object;
            return Objects.equals(code,      that.code)      &&
                   Objects.equals(codeSpace, that.codeSpace) &&
                   Objects.equals(authority, that.authority) &&
                   Objects.equals(version,   that.version)   &&
                   Objects.equals(remarks,   that.remarks);
        }
        return false;
    }

    /**
     * Returns a string representation of this identifier.
     * The default implementation returns a pseudo-WKT format.
     *
     * {@note The <code>NamedIdentifier</code> subclass overrides this method with a different
     *        behavior, in order to be compliant with the contract of the <code>GenericName</code>
     *        interface.}
     *
     * @see IdentifiedObjects#toString(Identifier)
     * @see NamedIdentifier#toString()
     */
    @Override
    public String toString() {
        return ReferenceSystemMetadata.toString("IDENTIFIER", authority, codeSpace, code, isDeprecated());
    }
}
