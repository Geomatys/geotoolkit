/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2011, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing;

import java.util.Map;
import java.util.Set;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Comparator;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.logging.Level;
import java.io.Serializable;
import java.io.ObjectStreamException;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.opengis.util.LocalName;
import org.opengis.util.ScopedName;
import org.opengis.util.GenericName;
import org.opengis.util.InternationalString;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.ObjectFactory;
import org.opengis.referencing.AuthorityFactory;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.parameter.InvalidParameterValueException;

import org.geotoolkit.util.Utilities;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.util.DefaultInternationalString;
import org.geotoolkit.internal.Citations;
import org.geotoolkit.internal.jaxb.text.StringConverter;
import org.geotoolkit.internal.jaxb.referencing.ReferenceIdentifierAdapter;
import org.geotoolkit.io.wkt.FormattableObject;
import org.geotoolkit.resources.Loggings;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.lang.ThreadSafe;
import org.geotoolkit.lang.Immutable;
import org.geotoolkit.xml.Namespaces;

import static org.geotoolkit.util.Utilities.ensureNonNull;


/**
 * A base class for metadata applicable to reference system objects. When {@link AuthorityFactory}
 * is used to create an object, the {@linkplain ReferenceIdentifier#getAuthority authority} and
 * {@linkplain ReferenceIdentifier#getCode authority code} values are set to the authority
 * name of the factory object, and the authority code supplied by the client, respectively. When
 * {@link ObjectFactory} creates an object, the {@linkplain #getName() name} is set to the value
 * supplied by the client and all of the other metadata items are left empty.
 * <p>
 * This class is conceptually <cite>abstract</cite>, even if it is technically possible to
 * instantiate it. Typical applications should create instances of the most specific subclass with
 * {@code Default} prefix instead. An exception to this rule may occurs when it is not possible to
 * identify the exact type. For example it is not possible to infer the exact coordinate system from
 * <A HREF="http://www.geoapi.org/snapshot/javadoc/org/opengis/referencing/doc-files/WKT.html"><cite>Well
 * Known Text</cite></A> is some cases (e.g. in a {@code LOCAL_CS} element). In such exceptional
 * situation, a plain {@link org.geotoolkit.referencing.cs.AbstractCS} object may be instantiated.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.14
 *
 * @since 1.2
 * @module
 */
@Immutable
@ThreadSafe(concurrent = true)
@XmlType(propOrder={
    "identifier",
    "name"
})
public class AbstractIdentifiedObject extends FormattableObject implements IdentifiedObject, Serializable {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -5173281694258483264L;

    /**
     * An empty array of identifiers. This is useful for fetching identifiers as an array,
     * using the following idiom:
     *
     * {@preformat java
     *     getIdentifiers().toArray(EMPTY_IDENTIFIER_ARRAY);
     * }
     *
     * @see #getIdentifiers()
     */
    public static final ReferenceIdentifier[] EMPTY_IDENTIFIER_ARRAY = new ReferenceIdentifier[0];

    /**
     * An empty array of alias. This is useful for fetching alias as an array,
     * using the following idiom:
     *
     * {@preformat java
     *     getAlias().toArray(EMPTY_ALIAS_ARRAY);
     * }
     *
     * @see #getAlias()
     */
    public static final GenericName[] EMPTY_ALIAS_ARRAY = new GenericName[0];

    /**
     * A comparator for sorting identified objects by {@linkplain #getName() name}.
     */
    public static final Comparator<IdentifiedObject> NAME_COMPARATOR = new NameComparator();

    /**
     * {@link #NAME_COMPARATOR} implementation as a named class (rather than anonymous)
     * for more predictable serialization.
     */
    private static final class NameComparator implements Comparator<IdentifiedObject>, Serializable {
        /** For cross-version compatibility. */
        private static final long serialVersionUID = -6605097017814062198L;

        /** Compares the given identified objects for order. */
        @Override public int compare(final IdentifiedObject o1, final IdentifiedObject o2) {
            return doCompare(o1.getName().getCode(), o2.getName().getCode());
        }

        /** Canonicalizes to the singleton on deserialization. */
        protected Object readResolve() throws ObjectStreamException {
            return NAME_COMPARATOR;
        }
    }

    /**
     * A comparator for sorting identified objects by {@linkplain #getIdentifiers identifiers}.
     * Identifiers are compared in their iteration order.
     */
    public static final Comparator<IdentifiedObject> IDENTIFIER_COMPARATOR = new IdentifierComparator();

    /**
     * {@link #IDENTIFIER_COMPARATOR} implementation as a named class (rather than anonymous)
     * for more predictable serialization.
     */
    private static final class IdentifierComparator implements Comparator<IdentifiedObject>, Serializable {
        /** For cross-version compatibility. */
        private static final long serialVersionUID = -7315726806679993522L;

        /** Compares the given identified objects for order. */
        @Override public int compare(final IdentifiedObject o1, final IdentifiedObject o2) {
            Collection<ReferenceIdentifier> a1 = o1.getIdentifiers();
            Collection<ReferenceIdentifier> a2 = o2.getIdentifiers();
            if (a1 == null) a1 = Collections.emptySet();
            if (a2 == null) a2 = Collections.emptySet();
            final Iterator<ReferenceIdentifier> i1 = a1.iterator();
            final Iterator<ReferenceIdentifier> i2 = a2.iterator();
            boolean n1, n2;
            while ((n1 = i1.hasNext()) & (n2 = i2.hasNext())) {  // NOSONAR: Really '&', not '&&'
                final int c = doCompare(i1.next().getCode(), i2.next().getCode());
                if (c != 0) {
                    return c;
                }
            }
            if (n1) return +1;
            if (n2) return -1;
            return 0;
        }

        /** Canonicalizes to the singleton on deserialization. */
        protected Object readResolve() throws ObjectStreamException {
            return IDENTIFIER_COMPARATOR;
        }
    }

    /**
     * A comparator for sorting identified objects by {@linkplain #getRemarks remarks}.
     */
    public static final Comparator<IdentifiedObject> REMARKS_COMPARATOR = new RemarksComparator();

    /**
     * {@link #REMARKS_COMPARATOR} implementation as a named class (rather than anonymous)
     * for more predictable serialization.
     */
    private static final class RemarksComparator implements Comparator<IdentifiedObject>, Serializable {
        /** For cross-version compatibility. */
        private static final long serialVersionUID = -6675419613224162715L;

        /** Compares the given identified objects for order. */
        @Override public int compare(final IdentifiedObject o1, final IdentifiedObject o2) {
            return doCompare(o1.getRemarks(), o2.getRemarks());
        }

        /** Canonicalizes to the singleton on deserialization. */
        protected Object readResolve() throws ObjectStreamException {
            return REMARKS_COMPARATOR;
        }
    }

    /**
     * The name for this object or code. Should never be {@code null}.
     */
    @XmlElement
    @XmlJavaTypeAdapter(ReferenceIdentifierAdapter.ToString.class)
    private final ReferenceIdentifier name;

    /**
     * An alternative name by which this object is identified.
     */
    private final Collection<GenericName> alias;

    /**
     * An identifier which references elsewhere the object's defining information.
     * Alternatively an identifier by which this object can be referenced.
     */
    private final Set<ReferenceIdentifier> identifiers;

    /**
     * Comments on or information about this object, or {@code null} if none.
     */
    private final InternationalString remarks;

    /**
     * Constructs a new object in which every attributes are set to a default value.
     * <strong>This is not a valid object.</strong> This constructor is strictly
     * reserved to JAXB, which will assign values to the fields using reflexion.
     */
    private AbstractIdentifiedObject() {
        this(org.geotoolkit.internal.referencing.NullReferencingObject.INSTANCE);
    }

    /**
     * Constructs a new identified object with the same values than the specified one.
     * This copy constructor provides a way to wrap an arbitrary implementation into a
     * Geotk one or a user-defined one (as a subclass), usually in order to leverage
     * some implementation-specific API. This constructor performs a shallow copy,
     * i.e. the properties are not cloned.
     *
     * @param object The object to copy.
     */
    public AbstractIdentifiedObject(final IdentifiedObject object) {
        ensureNonNull("object", object);
        Collection<GenericName>  as;
        Set<ReferenceIdentifier> id;
        name    = object.getName();
        as      = object.getAlias();
        id      = object.getIdentifiers();
        remarks = object.getRemarks();
        if (as != null && as.isEmpty()) as = null;
        if (id != null && id.isEmpty()) id = null;
        alias = as;
        identifiers = id;
    }

    /**
     * Constructs an object from a set of properties. Keys are strings from the table below.
     * Key are case-insensitive, and leading and trailing spaces are ignored. The map given in
     * argument shall contains at least a {@code "name"} property. Other properties listed
     * in the table below are optional.
     * <p>
     * <table border='1'>
     *   <tr bgcolor="#CCCCFF" class="TableHeadingColor">
     *     <th nowrap>Property name</th>
     *     <th nowrap>Value type</th>
     *     <th nowrap>Value given to</th>
     *   </tr>
     *   <tr>
     *     <td nowrap>&nbsp;{@value org.opengis.referencing.IdentifiedObject#NAME_KEY}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link String} or {@link ReferenceIdentifier}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link #getName()}</td>
     *   </tr>
     *   <tr>
     *     <td nowrap>&nbsp;{@value org.opengis.referencing.IdentifiedObject#ALIAS_KEY}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link CharSequence}, {@link GenericName} or an array of those&nbsp;</td>
     *     <td nowrap>&nbsp;{@link #getAlias}</td>
     *   </tr>
     *   <tr>
     *     <td nowrap>&nbsp;{@value org.opengis.metadata.Identifier#AUTHORITY_KEY}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link String} or {@link Citation}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link ReferenceIdentifier#getAuthority} on the {@linkplain #getName() name}</td>
     *   </tr>
     *   <tr>
     *     <td nowrap>&nbsp;{@value org.opengis.referencing.ReferenceIdentifier#CODESPACE_KEY}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link String}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link ReferenceIdentifier#getCodeSpace} on the {@linkplain #getName() name}</td>
     *   </tr>
     *   <tr>
     *     <td nowrap>&nbsp;{@value org.opengis.referencing.ReferenceIdentifier#VERSION_KEY}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link String}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link ReferenceIdentifier#getVersion} on the {@linkplain #getName() name}</td>
     *   </tr>
     *   <tr>
     *     <td nowrap>&nbsp;{@value org.opengis.referencing.IdentifiedObject#IDENTIFIERS_KEY}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link ReferenceIdentifier} or <code>{@linkplain ReferenceIdentifier}[]</code>&nbsp;</td>
     *     <td nowrap>&nbsp;{@link #getIdentifiers}</td>
     *   </tr>
     *   <tr>
     *     <td nowrap>&nbsp;{@value org.opengis.referencing.IdentifiedObject#REMARKS_KEY}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link String} or {@link InternationalString}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link #getRemarks}</td>
     *   </tr>
     * </table>
     * <p>
     * Additionally, all localizable attributes like {@code "remarks"} may have a language and
     * country code suffix. For example the {@code "remarks_fr"} property stands for remarks in
     * {@linkplain java.util.Locale#FRENCH French} and the {@code "remarks_fr_CA"} property stands
     * for remarks in {@linkplain java.util.Locale#CANADA_FRENCH French Canadian}.
     * <p>
     * Note that the {@code "authority"} and {@code "version"} properties are ignored if the
     * {@code "name"} property is already a {@link Citation} object instead of a {@link String}.
     *
     * @param  properties The properties to be given to this identified object.
     * @throws IllegalArgumentException if a property has an invalid value.
     */
    public AbstractIdentifiedObject(final Map<String,?> properties) throws IllegalArgumentException {
        this(properties, null, null);
    }

    /**
     * Constructs an object from a set of properties and copy unrecognized properties in the
     * specified map. The {@code properties} argument is treated as in the {@linkplain
     * AbstractIdentifiedObject#AbstractIdentifiedObject(Map) one argument constructor}. All
     * properties unknown to this {@code AbstractIdentifiedObject} constructor are copied
     * in the {@code subProperties} map, after their key has been normalized (usually
     * lower case, leading and trailing space removed).
     * <p>
     * If {@code localizables} is non-null, then all keys listed in this argument are
     * treated as localizable one (i.e. may have a suffix like "_fr", "_de", etc.). Localizable
     * properties are stored in the {@code subProperties} map as {@link InternationalString}
     * objects.
     *
     * @param properties    Set of properties. Should contains at least {@code "name"}.
     * @param subProperties The map in which to copy unrecognized properties.
     * @param localizables  Optional list of localized properties.
     * @throws IllegalArgumentException if a property has an invalid value.
     */
    protected AbstractIdentifiedObject(final Map<String,?>      properties,
                                       final Map<String,Object> subProperties,
                                       final String[]           localizables)
            throws IllegalArgumentException
    {
        ensureNonNull("properties", properties);
        Object name        = null;
        Object alias       = null;
        Object identifiers = null;
        Object remarks     = null;
        DefaultInternationalString       i18n = null;
        DefaultInternationalString[] sub_i18n = null;
        /*
         * Iterate through each map entry. This have two purposes:
         *
         *   1) Ignore case (a call to properties.get("foo") can't do that)
         *   2) Find localized remarks.
         *
         * This algorithm is sub-optimal if the map contains a lot of entries of no interest to
         * this object. Hopefully, most users will fill a map only with useful entries.
         */
nextKey:for (final Map.Entry<String,?> entry : properties.entrySet()) {
            String    key   = entry.getKey().trim().toLowerCase();
            Object    value = entry.getValue();
            /*
             * Note: String.hashCode() is part of J2SE specification,
             *       so it should not change across implementations.
             */
            switch (key.hashCode()) {
                // Fix case for common keywords. They are not used
                // by this class, but are used by some subclasses.
                case -1528693765: if (key.equalsIgnoreCase("anchorPoint"))                 key="anchorPoint";                 break;
                case -1805658881: if (key.equalsIgnoreCase("bursaWolf"))                   key="bursaWolf";                   break;
                case   109688209: if (key.equalsIgnoreCase("operationVersion"))            key="operationVersion";            break;
                case  1479434472: if (key.equalsIgnoreCase("coordinateOperationAccuracy")) key="coordinateOperationAccuracy"; break;
                case  1126917133: if (key.equalsIgnoreCase("positionalAccuracy"))          key="positionalAccuracy";          break;
                case  1127093059: if (key.equalsIgnoreCase("realizationEpoch"))            key="realizationEpoch";            break;
                case  1790520781: if (key.equalsIgnoreCase("domainOfValidity"))            key="domainOfValidity";            break;
                case -1109785975: if (key.equalsIgnoreCase("validArea"))                   key="validArea";                   break;

                // -------------------------------------
                // "name": String or ReferenceIdentifier
                // -------------------------------------
                case 3373707: {
                    if (key.equals(NAME_KEY)) {
                        if (value instanceof String) {
                            name = new NamedIdentifier(properties, false);
                            assert value.equals(((Identifier) name).getCode()) : name;
                        } else {
                            // Should be an instance of ReferenceIdentifier, but we don't check
                            // here. The type will be checked at the end of this method, which
                            // will thrown an exception with detailed message in case of mismatch.
                            name = value;
                        }
                        continue nextKey;
                    }
                    break;
                }
                // -------------------------------------------------------------------
                // "alias": CharSequence, CharSequence[], GenericName or GenericName[]
                // -------------------------------------------------------------------
                case 92902992: {
                    if (key.equals(ALIAS_KEY)) {
                        alias = NamedIdentifier.getNameFactory().toArray(value);
                        continue nextKey;
                    }
                    break;
                }
                // -----------------------------------------------------------
                // "identifiers": ReferenceIdentifier or ReferenceIdentifier[]
                // -----------------------------------------------------------
                case 1368189162: {
                    if (key.equals(IDENTIFIERS_KEY)) {
                        if (value != null) {
                            if (value instanceof ReferenceIdentifier) {
                                identifiers = new ReferenceIdentifier[] {
                                    (ReferenceIdentifier) value
                                };
                            } else {
                                identifiers = value;
                            }
                        }
                        continue nextKey;
                    }
                    break;
                }
                // ----------------------------------------
                // "remarks": String or InternationalString
                // ----------------------------------------
                case 1091415283: {
                    if (key.equals(REMARKS_KEY)) {
                        if (value instanceof InternationalString) {
                            remarks = value;
                            continue nextKey;
                        }
                    }
                    break;
                }
            }
            /*
             * Search for additional locales for remarks (e.g. "remarks_fr").
             * 'growable.add(...)' will add the value only if the key starts
             * with the "remarks" prefix.
             */
            if (value instanceof String) {
                if (i18n == null) {
                    if (remarks instanceof DefaultInternationalString) {
                        i18n = (DefaultInternationalString) remarks;
                    } else {
                        i18n = new DefaultInternationalString();
                    }
                }
                if (i18n.add(REMARKS_KEY, key, value.toString())) {
                    continue nextKey;
                }
            }
            /*
             * Search for user-specified localizable properties.
             */
            if (subProperties == null) {
                continue nextKey;
            }
            if (localizables != null) {
                for (int i=0; i<localizables.length; i++) {
                    final String prefix = localizables[i];
                    if (key.equals(prefix)) {
                        if (value instanceof InternationalString) {
                            // Stores the value in 'subProperties' after the loop.
                            break;
                        }
                    }
                    if (value instanceof String) {
                        if (sub_i18n == null) {
                            sub_i18n = new DefaultInternationalString[localizables.length];
                        }
                        if (sub_i18n[i] == null) {
                            final Object previous = subProperties.get(prefix);
                            if (previous instanceof DefaultInternationalString) {
                                sub_i18n[i] = (DefaultInternationalString) previous;
                            } else {
                                sub_i18n[i] = new DefaultInternationalString();
                            }
                        }
                        if (sub_i18n[i].add(prefix, key, value.toString())) {
                            continue nextKey;
                        }
                    }
                }
            }
            subProperties.put(key, value);
        }
        /*
         * Get the localized remarks, if it was not yet set. If a user specified remarks
         * both as InternationalString and as String for some locales (which is a weird
         * usage...), then current implementation discart the later with a warning.
         */
        if (i18n!=null && !i18n.getLocales().isEmpty()) {
            if (remarks == null) {
                remarks = i18n;
            } else if (!i18n.isSubsetOf(remarks)) {
                Logging.log(AbstractIdentifiedObject.class,
                        Loggings.format(Level.WARNING, Loggings.Keys.LOCALES_DISCARTED));
            }
        }
        /*
         * Get the localized user-defined properties.
         */
        if (subProperties!=null && sub_i18n!=null) {
            for (int i=0; i<sub_i18n.length; i++) {
                i18n = sub_i18n[i];
                if (i18n!=null && !i18n.getLocales().isEmpty()) {
                    final String prefix = localizables[i];
                    final Object current = subProperties.get(prefix);
                    if (current == null) {
                        subProperties.put(prefix, i18n);
                    } else if (!i18n.isSubsetOf(current)) {
                        Logging.log(AbstractIdentifiedObject.class,
                                Loggings.format(Level.WARNING, Loggings.Keys.LOCALES_DISCARTED));
                    }
                }
            }
        }
        /*
         * Stores the definitive reference to the attributes. Note that casts are performed only
         * there (not before). This is a wanted feature, since we want to catch ClassCastExceptions
         * are rethrown them as more informative exceptions.
         */
        String key=null; Object value=null;
        try {
            key = NAME_KEY;
            this.name = (ReferenceIdentifier) (value = name);

            key = ALIAS_KEY;
            this.alias = asSet((GenericName[]) (value = alias));

            key = IDENTIFIERS_KEY;
            this.identifiers = asSet((ReferenceIdentifier[]) (value = identifiers));

            key = REMARKS_KEY;
            this.remarks = (InternationalString) (value = remarks);
        } catch (ClassCastException exception) {
            InvalidParameterValueException e = new InvalidParameterValueException(Errors.format(
                    Errors.Keys.ILLEGAL_ARGUMENT_$2, key, value), key, value);
            e.initCause(exception);
            throw e;
        }
        ensureNonNull(NAME_KEY, name);
        ensureNonNull(NAME_KEY, name.toString());
    }

    /**
     * The {@code gml:id}, which is mandatory. The current implementation searches for the first
     * identifier, regardless its authority. If no identifier is found, then the name is used.
     * If no name is found (which should not occur for valid objects), then this method returns
     * {@code null}.
     * <p>
     * When an identifier has been found, this method returns the concatenation of its code
     * space with its code, <em>without separator</em>. For example this method may return
     * {@code "EPSG4326"}, not {@code "EPSG:4326"}.
     */
    @XmlID
    @XmlAttribute(name = "id", namespace = Namespaces.GML, required = true)
    @XmlJavaTypeAdapter(StringConverter.class)
    final String getID() {
        ReferenceIdentifier id = getIdentifier(null);
        if (id == null) {
            id = getName();
            if (id == null) {
                return null;
            }
        }
        String code = id.getCodeSpace();
        if (code != null) {
            code += id.getCode();
        } else {
            code = id.getCode();
        }
        code = code.replace(":", ""); // Usually not needed, but done as a paranoiac safety.
        return code;
    }

    /**
     * The primary name by which this object is identified.
     *
     * @see #getName(Citation)
     */
    @Override
    public ReferenceIdentifier getName() {
        return name;
    }

    /**
     * An alternative name by which this object is identified.
     *
     * @return The aliases, or an empty array if there is none.
     *
     * @see #getName(Citation)
     */
    @Override
    public Collection<GenericName> getAlias() {
        if (alias == null) {
            return Collections.emptySet();
        }
        return alias;
    }

    /**
     * An identifier which references elsewhere the object's defining information.
     * Alternatively an identifier by which this object can be referenced.
     *
     * @return This object identifiers, or an empty array if there is none.
     *
     * @see #getIdentifier(Citation)
     */
    @Override
    public Set<ReferenceIdentifier> getIdentifiers() {
        if (identifiers == null) {
            return Collections.emptySet();
        }
        return identifiers;
    }

    /**
     * Returns the first identifier found, or {@code null} if none.
     * This method is invoked by JAXB at marshalling time.
     */
    @XmlElement
    final ReferenceIdentifier getIdentifier() {
        final Set<ReferenceIdentifier> identifiers = this.identifiers;
        if (identifiers != null) {
            final Iterator<ReferenceIdentifier> it = identifiers.iterator();
            if (it.hasNext()) {
                return it.next();
            }
        }
        return null;
    }

    /**
     * Comments on or information about this object, including data source information.
     */
    @Override
    public InternationalString getRemarks(){
        return remarks;
    }

    /**
     * Returns the informations provided in the specified identified object as a map of
     * properties. The returned map contains key such as {@link #NAME_KEY NAME_KEY}, and
     * values from methods such as {@link #getName}.
     *
     * @param  info The identified object to view as a properties map.
     * @return An view of the identified object as an immutable map.
     */
    public static Map<String,?> getProperties(final IdentifiedObject info) {
        ensureNonNull("info", info);
        return new Properties(info);
    }

    /**
     * Returns the properties to be given to an identified object derived from the specified one.
     * This method is typically used for creating a new CRS identical to an existing one except
     * for axis units. This method returns the same properties than the supplied argument (as of
     * <code>{@linkplain #getProperties(IdentifiedObject) getProperties}(info)</code>), except for
     * the following:
     * <p>
     * <ul>
     *   <li>The {@linkplain #getName() name}'s authority is replaced by the specified one.</li>
     *   <li>All {@linkplain #getIdentifiers identifiers} are removed, because the new object
     *       to be created is probably not endorsed by the original authority.</li>
     * </ul>
     * <p>
     * This method returns a mutable map. Consequently, callers can add their own identifiers
     * directly to this map if they wish.
     *
     * @param  info The identified object to view as a properties map.
     * @param  authority The new authority for the object to be created, or {@code null} if it
     *         is not going to have any declared authority.
     * @return An view of the identified object as a mutable map.
     */
    public static Map<String,Object> getProperties(final IdentifiedObject info, final Citation authority) {
        final Map<String,Object> properties = new HashMap<String,Object>(getProperties(info));
        properties.put(NAME_KEY, new NamedIdentifier(authority, info.getName().getCode()));
        properties.remove(IDENTIFIERS_KEY);
        return properties;
    }

    /**
     * Returns an identifier according the given authority. This method checks all
     * {@linkplain #getIdentifiers identifiers} in their iteration order. It returns the first
     * identifier with an {@linkplain ReferenceIdentifier#getAuthority authority} citation
     * {@linkplain org.geotoolkit.metadata.iso.citation.Citations#identifierMatches(Citation,
     * Citation) matching} the specified authority.
     *
     * @param  authority The authority for the identifier to return, or {@code null} for
     *         the first identifier regardless its authority.
     * @return The object's identifier, or {@code null} if no identifier matching the specified
     *         authority was found.
     *
     * @since 2.2
     */
    public ReferenceIdentifier getIdentifier(final Citation authority) {
        return identifier(this, authority);
    }

    /**
     * Returns an identifier according the given authority. This method performs the same search
     * than {@link #getIdentifier(Citation)} on arbitrary implementations of GeoAPI interface.
     *
     * @param  info The object to get the identifier from.
     * @param  authority The authority for the identifier to return, or {@code null} for
     *         the first identifier regardless its authority.
     * @return The object's identifier, or {@code null} if no identifier matching the specified
     *         authority was found.
     *
     * @since 2.2
     */
    public static ReferenceIdentifier getIdentifier(final IdentifiedObject info, final Citation authority) {
        if (info instanceof AbstractIdentifiedObject) {
            // Gives a chances to subclasses to get their overridden method invoked.
            return ((AbstractIdentifiedObject) info).getIdentifier(authority);
        }
        return identifier(info, authority);
    }

    /**
     * Returns an identifier according the given authority.
     *
     * @param  info The object to get the identifier from.
     * @param  authority The authority for the identifier to return, or {@code null}.
     * @return The object's identifier, or {@code null} if none.
     */
    private static ReferenceIdentifier identifier(final IdentifiedObject info, final Citation authority) {
        if (info == null) {
            return null;
        }
        for (final ReferenceIdentifier identifier : info.getIdentifiers()) {
            if (authority == null) {
                return identifier;
            }
            final Citation infoAuthority = identifier.getAuthority();
            if (infoAuthority != null) {
                if (Citations.identifierMatches(authority, infoAuthority)) {
                    return identifier;
                }
            }
        }
        return (authority == null) ? info.getName() : null;
    }

    /**
     * Returns this object's name according the given authority. This method checks first the
     * {@linkplain #getName() primary name}, then all {@linkplain #getAlias() alias} in their
     * iteration order.
     *
     * <ul>
     *   <li><p>If the name or alias implements the {@link ReferenceIdentifier} interface,
     *       then this method compares the {@linkplain ReferenceIdentifier#getAuthority
     *       identifier authority} against the specified citation using the
     *       {@link Citations#identifierMatches(Citation,Citation) identifierMatches}
     *       method. If a matching is found, then this method returns the
     *       {@linkplain ReferenceIdentifier#getCode identifier code} of this object.</p></li>
     *
     *   <li><p>Otherwise, if the alias implements the {@link GenericName} interface, then this
     *       method compares the {@linkplain GenericName#scope name scope} against the specified
     *       citation using the {@linkplain Citations#identifierMatches(Citation,String)
     *       identifierMatches} method. If a matching is found, then this method returns the
     *       {@linkplain GenericName#tip name tip} of this object.</p></li>
     * </ul>
     *
     * Note that alias may implement both the {@link ReferenceIdentifier} and {@link GenericName}
     * interfaces (for example {@link NamedIdentifier}). In such cases, the identifier view has
     * precedence.
     *
     * @param  authority The authority for the name to return, or {@code null} for any authority.
     * @return The object's name (either a {@linkplain ReferenceIdentifier#getCode code}
     *         or a {@linkplain GenericName#tip name tip}), or {@code null} if
     *         no name matching the specified authority was found.
     *
     * @see #getName()
     * @see #getAlias()
     *
     * @since 2.2
     */
    public String getName(final Citation authority) {
        return name(this, authority);
    }

    /**
     * Returns an object's name according the given authority. This method performs the same search
     * than {@link #getName(Citation)} on arbitrary implementations of GeoAPI interface.
     *
     * @param  info The object to get the name from.
     * @param  authority The authority for the name to return, or {@code null} for any authority.
     * @return The object's name (either a {@linkplain ReferenceIdentifier#getCode code} or a
     *         {@linkplain GenericName#tip name tip}), or {@code null} if no name matching the
     *         specified authority was found.
     *
     * @since 2.2
     */
    public static String getName(final IdentifiedObject info, final Citation authority) {
        if (info instanceof AbstractIdentifiedObject) {
            // Gives a chances to subclasses to get their overridden method invoked.
            return ((AbstractIdentifiedObject) info).getName(authority);
        }
        return name(info, authority);
    }

    /**
     * Returns an object's name according the given authority.
     *
     * @param  info The object to get the name from.
     * @param  authority The authority for the name to return, or {@code null} for any authority.
     * @return The object's name (either a {@linkplain ReferenceIdentifier#getCode code} or a
     *         {@linkplain GenericName#tip name tip}), or {@code null} if no name matching the
     *         specified authority was found.
     */
    private static String name(final IdentifiedObject info, final Citation authority) {
        Identifier identifier = info.getName();
        if (authority == null) {
            return identifier.getCode();
        }
        String name = null;
        Citation infoAuthority = identifier.getAuthority();
        if (infoAuthority != null) {
            if (Citations.identifierMatches(authority, infoAuthority)) {
                name = identifier.getCode();
            } else {
                for (final GenericName alias : info.getAlias()) {
                    if (alias instanceof Identifier) {
                        identifier = (Identifier) alias;
                        infoAuthority = identifier.getAuthority();
                        if (infoAuthority != null) {
                            if (Citations.identifierMatches(authority, infoAuthority)) {
                                name = identifier.getCode();
                                break;
                            }
                        }
                    } else {
                        final GenericName scope = alias.scope().name();
                        if (scope != null) {
                            if (Citations.identifierMatches(authority, scope.toString())) {
                                name = alias.toString();
                                break;
                            }
                        }
                    }
                }
            }
        }
        return name;
    }

    /**
     * Returns {@code true} if either the {@linkplain #getName() primary name} or at least
     * one {@linkplain #getAlias alias} matches the specified string. This method performs
     * the search in the following order, regardless of any authority:
     * <p>
     * <ul>
     *   <li>The {@linkplain #getName() primary name} of this object</li>
     *   <li>The {@linkplain ScopedName fully qualified name} of an alias</li>
     *   <li>The {@linkplain LocalName local name} of an alias</li>
     * </ul>
     *
     * @param  name The name to compare.
     * @return {@code true} if the primary name of at least one alias
     *         matches the specified {@code name}.
     */
    public boolean nameMatches(final String name) {
        return nameMatches(this, alias, name);
    }

    /**
     * Returns {@code true} if either the {@linkplain #getName() primary name} or at least
     * one {@linkplain #getAlias alias} matches the specified string. This method performs the
     * same check than the {@linkplain #nameMatches(String) non-static method} on arbitrary
     * object implementing the GeoAPI interface.
     *
     * @param  object The object to check.
     * @param  name The name.
     * @return {@code true} if the primary name of at least one alias
     *         matches the specified {@code name}.
     */
    public static boolean nameMatches(final IdentifiedObject object, final String name) {
        if (object instanceof AbstractIdentifiedObject) {
            return ((AbstractIdentifiedObject) object).nameMatches(name);
        } else {
            return nameMatches(object, object.getAlias(), name);
        }
    }

    /**
     * Returns {@code true} if the {@linkplain #getName() primary name} of an object matches
     * the primary name or one {@linkplain #getAlias alias} of the other object.
     *
     * @param o1 The first object to compare by name.
     * @param o2 The second object to compare by name.
     * @return {@code true} if both objects have a common name.
     *
     * @since 2.4
     */
    public static boolean nameMatches(final IdentifiedObject o1, final IdentifiedObject o2) {
        return nameMatches(o1, o2.getName().getCode()) ||
               nameMatches(o2, o1.getName().getCode());
    }

    /**
     * Returns {@code true} if the {@linkplain #getName() primary name} of the given object
     * or one of the given alias matches the given name.
     *
     * @param  object The object to check.
     * @param  alias  The list of alias in {@code object} (may be {@code null}).
     *                This method will never modify this list. Consequently, it
     *                may be a direct reference to an internal array.
     * @param  name   The name for which to check for equality.
     * @return {@code true} if the primary name or at least one alias matches the given {@code name}.
     */
    private static boolean nameMatches(final IdentifiedObject object,
                                       final Collection<GenericName> alias, String name)
    {
        name = name.trim();
        if (name.equalsIgnoreCase(object.getName().getCode().trim())) {
            return true;
        }
        if (alias != null) {
            for (GenericName asName : alias) {
                asName = asName.toFullyQualifiedName();
                while (asName != null) {
                    if (name.equalsIgnoreCase(asName.toString().trim())) {
                        return true;
                    }
                    if (!(asName instanceof ScopedName)) {
                        break;
                    }
                    asName = ((ScopedName) asName).tail();
                }
            }
        }
        return false;
    }

    /**
     * Compares the specified object with this object for equality.
     *
     * @param  object The other object (may be {@code null}).
     * @return {@code true} if both objects are equal.
     */
    @Override
    public final boolean equals(final Object object) {
        return (object instanceof AbstractIdentifiedObject) &&
                equals((AbstractIdentifiedObject) object, ComparisonMode.STRICT);
    }

    /**
     * Compares this object with the specified object for equality.
     * The strictness level is controlled by the second argument:
     * <p>
     * <ul>
     *   <li>If {@code mode} is {@link ComparisonMode#STRICT STRICT}, then all available properties
     *       are compared including {@linkplain #getName() name}, {@linkplain #getRemarks remarks},
     *       {@linkplain #getIdentifiers identifiers code}, etc.</li>
     *   <li>If {@code mode} is {@link ComparisonMode#IGNORE_METADATA IGNORE_METADATA}, then this
     *       method compare only the properties needed for computing transformations. In other
     *       words, {@code sourceCS.equals(targetCS, false)} returns {@code true} only if the
     *       transformation from {@code sourceCS} to {@code targetCS} is the identity transform,
     *       no matter what {@link #getName()} said.</li>
     * </ul>
     * <p>
     * Some subclasses (especially {@link org.geotoolkit.referencing.datum.AbstractDatum}
     * and {@link org.geotoolkit.parameter.AbstractParameterDescriptor}) will test for the
     * {@linkplain #getName() name}, since objects with different name have completely
     * different meaning. For example nothing differentiate the {@code "semi_major"} and
     * {@code "semi_minor"} parameters except the name. The name comparison may be loose
     * however, i.e. we may accept a name matching an alias.
     *
     * @param  object The object to compare to {@code this}.
     * @param  mode {@link ComparisonMode#STRICT STRICT} for performing a strict comparison, or
     *         {@link ComparisonMode#IGNORE_METADATA IGNORE_METADATA} for comparing only properties
     *         relevant to transformations.
     * @return {@code true} if both objects are equal.
     *
     * @since 3.14
     */
    public boolean equals(final AbstractIdentifiedObject object, final ComparisonMode mode) {
        if (object!=null && object.getClass().equals(getClass())) {
            if (!mode.equals(ComparisonMode.STRICT)) {
                return true;
            }
            return Utilities.equals(name,        object.name       ) &&
                   Utilities.equals(alias,       object.alias      ) &&
                   Utilities.equals(identifiers, object.identifiers) &&
                   Utilities.equals(remarks,     object.remarks    );
        }
        return false;
    }

    /**
     * Compares two OpenGIS's {@code IdentifiedObject} objects for equality. This convenience
     * method is provided for implementation of {@code equals} in subclasses.
     *
     * @param  object1 The first object to compare (may be {@code null}).
     * @param  object2 The second object to compare (may be {@code null}).
     * @param  mode {@code STRICT} for performing a strict comparison, or {@code IGNORE_METADATA}
     *         for comparing only properties relevant to transformations.
     * @return {@code true} if both objects are equal.
     *
     * @since 3.14
     */
    protected static boolean equals(final IdentifiedObject object1,
                                    final IdentifiedObject object2,
                                    final ComparisonMode mode)
    {
        if (object1 == object2) {
            return true;
        }
        if (object1 == null || object2 == null) {
            return false;
        }
        if (!(object1 instanceof AbstractIdentifiedObject)) return object1.equals(object2);
        if (!(object2 instanceof AbstractIdentifiedObject)) return object2.equals(object1);
        return ((AbstractIdentifiedObject) object1).equals((AbstractIdentifiedObject) object2, mode);
    }

    /**
     * Compares two arrays of OpenGIS's {@code IdentifiedObject} objects for equality. This
     * convenience method is provided for implementation of {@code equals} method in subclasses.
     *
     * @param  array1 The first array to compare (may be {@code null}).
     * @param  array2 The second array to compare (may be {@code null}).
     * @param  mode {@code STRICT} for performing a strict comparison, or {@code IGNORE_METADATA}
     *         for comparing only properties relevant to transformations.
     * @return {@code true} if both arrays are equal.
     *
     * @since 3.14
     */
    protected static boolean equals(final IdentifiedObject[] array1,
                                    final IdentifiedObject[] array2,
                                    final ComparisonMode mode)
    {
        if (array1 != array2) {
            if ((array1 == null) || (array2 == null) || (array1.length != array2.length)) {
                return false;
            }
            for (int i=array1.length; --i>=0;) {
                if (!equals(array1[i], array2[i], mode)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Compares two collections of OpenGIS's {@code IdentifiedObject} objects for equality. The
     * comparison take order in account, which make it more appropriate for {@link java.util.List}
     * or {@link LinkedHashSet} comparisons. This convenience method is provided for
     * implementation of {@code equals} method in subclasses.
     *
     * @param  collection1 The first collection to compare (may be {@code null}).
     * @param  collection2 The second collection to compare (may be {@code null}).
     * @param  mode {@code STRICT} for performing a strict comparison, or {@code IGNORE_METADATA}
     *         for comparing only properties relevant to transformations.
     * @return {@code true} if both collections are equal.
     *
     * @since 3.14
     */
    protected static boolean equals(final Collection<? extends IdentifiedObject> collection1,
                                    final Collection<? extends IdentifiedObject> collection2,
                                    final ComparisonMode mode)
    {
        if (collection1 == collection2) {
            return true;
        }
        if (collection1==null || collection2==null) {
            return false;
        }
        final Iterator<? extends IdentifiedObject> it1 = collection1.iterator();
        final Iterator<? extends IdentifiedObject> it2 = collection2.iterator();
        while (it1.hasNext()) {
            if (!it2.hasNext() || !equals(it1.next(), it2.next(), mode)) {
                return false;
            }
        }
        return !it2.hasNext();
    }

    /**
     * Compares two objects for order. Any object may be null. This method is
     * used for implementation of {@link #NAME_COMPARATOR} and its friends.
     */
    private static <E extends Comparable<E>> int doCompare(final E c1, final E c2) {
        if (c1 == null) {
            return (c2==null) ? 0 : -1;
        }
        if (c2 == null) {
            return +1;
        }
        return c1.compareTo(c2);
    }

    /**
     * Returns a hash value for this identified object. {@linkplain #getName() Name},
     * {@linkplain #getIdentifiers identifiers} and {@linkplain #getRemarks remarks}
     * are not taken in account. In other words, two identified objects will return
     * the same hash value if they are equal in the sense of <code>{@linkplain
     * #equals(AbstractIdentifiedObject,ComparisonMode) equals}(AbstractIdentifiedObject,
     * ComparisonMode.<strong>IGNORE_METADATA</strong>)</code>.
     *
     * @return The hash code value. This value doesn't need to be the same
     *         in past or future versions of this class.
     */
    @Override
    public int hashCode() {
        // Subclasses need to overrides this!!!!
        return (int)serialVersionUID ^ getClass().hashCode();
    }

    /**
     * Returns the specified array as an immutable set, or {@code null} if the
     * array is empty or null. This is a convenience method for sub-classes
     * constructors.
     *
     * @param  <E> The type of array elements.
     * @param  array The array to copy in a set. May be {@code null}.
     * @return A set containing the array elements, or {@code null} if none or empty.
     */
    protected static <E> Set<E> asSet(final E[] array) {
        if (array == null) {
            return null;
        }
        switch (array.length) {
            case 0:  return null;
            case 1:  return Collections.singleton(array[0]);
            default: return Collections.unmodifiableSet(new LinkedHashSet<E>(Arrays.asList(array)));
        }
    }
}
