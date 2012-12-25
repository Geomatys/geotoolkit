/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
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
import java.util.Set;
import java.util.Iterator;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.logging.Level;
import java.io.Serializable;
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

import org.geotoolkit.util.Deprecable;
import org.geotoolkit.util.ComparisonMode;
import org.geotoolkit.util.LenientComparable;
import org.geotoolkit.util.DefaultInternationalString;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.util.converter.Classes;
import org.apache.sis.internal.util.Citations;
import org.geotoolkit.internal.jaxb.gco.StringConverter;
import org.geotoolkit.internal.jaxb.referencing.RS_Identifier;
import org.geotoolkit.io.wkt.FormattableObject;
import org.geotoolkit.resources.Loggings;
import org.geotoolkit.resources.Errors;
import net.jcip.annotations.ThreadSafe;
import net.jcip.annotations.Immutable;
import org.geotoolkit.xml.Namespaces;

import static org.geotoolkit.util.Utilities.deepEquals;
import static org.apache.sis.util.ArgumentChecks.ensureNonNull;
import static org.geotoolkit.internal.InternalUtilities.nonEmptySet;
import static org.geotoolkit.internal.referencing.CRSUtilities.getReferencingGroup;


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
 * @version 3.21
 *
 * @since 1.2
 * @module
 */
@Immutable
@ThreadSafe
@XmlType(name="IdentifiedObjectType", propOrder={
    "identifier",
    "name"
})
public class AbstractIdentifiedObject extends FormattableObject implements IdentifiedObject,
        LenientComparable, Deprecable, Serializable
{
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -5173281694258483264L;

    /**
     * The name for this object or code. Should never be {@code null}.
     */
    @XmlElement
    @XmlJavaTypeAdapter(RS_Identifier.ToString.class)
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
     * The cached hash code value, or 0 if not yet computed. This field is calculated only when
     * first needed. We do not declare it {@code volatile} because it is not a big deal if this
     * field is calculated many time, and the same value should be produced by all computations.
     * The only possible outdated value is 0, which is okay.
     *
     * @since 3.18
     */
    private transient int hashCode;

    /**
     * Constructs a new object in which every attributes are set to a default value.
     * <strong>This is not a valid object.</strong> This constructor is strictly
     * reserved to JAXB, which will assign values to the fields using reflexion.
     */
    private AbstractIdentifiedObject() {
        this(org.geotoolkit.internal.referencing.NilReferencingObject.INSTANCE);
    }

    /**
     * Constructs a new identified object with the same values than the specified one.
     * This copy constructor provides a way to convert an arbitrary implementation into a
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
     *     <td nowrap>&nbsp;{@link #getAlias()}</td>
     *   </tr>
     *   <tr>
     *     <td nowrap>&nbsp;{@value org.opengis.metadata.Identifier#AUTHORITY_KEY}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link String} or {@link Citation}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link ReferenceIdentifier#getAuthority()} on the {@linkplain #getName() name}</td>
     *   </tr>
     *   <tr>
     *     <td nowrap>&nbsp;{@value org.opengis.referencing.ReferenceIdentifier#CODESPACE_KEY}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link String}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link ReferenceIdentifier#getCodeSpace()} on the {@linkplain #getName() name}</td>
     *   </tr>
     *   <tr>
     *     <td nowrap>&nbsp;{@value org.opengis.referencing.ReferenceIdentifier#VERSION_KEY}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link String}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link ReferenceIdentifier#getVersion()} on the {@linkplain #getName() name}</td>
     *   </tr>
     *   <tr>
     *     <td nowrap>&nbsp;{@value org.opengis.referencing.IdentifiedObject#IDENTIFIERS_KEY}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link ReferenceIdentifier} or <code>{@linkplain ReferenceIdentifier}[]</code>&nbsp;</td>
     *     <td nowrap>&nbsp;{@link #getIdentifiers()}</td>
     *   </tr>
     *   <tr>
     *     <td nowrap>&nbsp;{@value org.opengis.referencing.IdentifiedObject#REMARKS_KEY}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link String} or {@link InternationalString}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link #getRemarks()}</td>
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
                Logging.log(AbstractIdentifiedObject.class, "<init>",
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
                        Logging.log(AbstractIdentifiedObject.class, "<init>",
                                Loggings.format(Level.WARNING, Loggings.Keys.LOCALES_DISCARTED));
                    }
                }
            }
        }
        /*
         * Stores the definitive reference to the attributes. Note that casts are performed only
         * there (not before). This is a wanted feature, since we want to catch ClassCastExceptions
         * are rethrown them as a more informative exception.
         */
        String key=null; Object value=null;
        try {
            key = NAME_KEY;        this.name        =             (ReferenceIdentifier)   (value = name);
            key = ALIAS_KEY;       this.alias       = nonEmptySet((GenericName[])         (value = alias));
            key = IDENTIFIERS_KEY; this.identifiers = nonEmptySet((ReferenceIdentifier[]) (value = identifiers));
            key = REMARKS_KEY;     this.remarks     =             (InternationalString)   (value = remarks);
        } catch (ClassCastException exception) {
            throw new InvalidParameterValueException(Errors.format(
                    Errors.Keys.ILLEGAL_ARGUMENT_$2, key, value), exception, key, value);
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
     * Returns this object name according the given authority. This method checks first the
     * {@linkplain #getName() primary name}, then all {@linkplain #getAlias() alias} in their
     * iteration order.
     *
     * <ul>
     *   <li><p>If the name or alias implements the {@link ReferenceIdentifier} interface,
     *       then this method compares the {@linkplain ReferenceIdentifier#getAuthority()
     *       identifier authority} against the specified citation using the
     *       {@link org.geotoolkit.metadata.iso.citation.Citations#identifierMatches(Citation,Citation) identifierMatches}
     *       method. If a matching is found, then this method returns the
     *       {@linkplain ReferenceIdentifier#getCode identifier code} of this object.</p></li>
     *
     *   <li><p>Otherwise, if the alias implements the {@link GenericName} interface, then this
     *       method compares the {@linkplain GenericName#scope name scope} against the specified
     *       citation using the {@linkplain org.geotoolkit.metadata.iso.citation.Citations#identifierMatches(Citation,String)
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
     * @see IdentifiedObjects#getName(IdentifiedObject, Citation)
     *
     * @since 2.2
     */
    public String getName(final Citation authority) {
        return IdentifiedObjects.name(this, authority, null);
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
     * @see IdentifiedObjects#getIdentifier(IdentifiedObject, Citation)
     *
     * @since 2.2
     */
    public ReferenceIdentifier getIdentifier(final Citation authority) {
        return IdentifiedObjects.identifier(this, authority);
    }

    /**
     * Comments on or information about this object, including data source information.
     */
    @Override
    public InternationalString getRemarks(){
        return remarks;
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
     *
     * @see IdentifiedObjects#nameMatches(IdentifiedObject, String)
     */
    public boolean nameMatches(final String name) {
        return IdentifiedObjects.nameMatches(this, alias, name);
    }

    /**
     * Returns {@code true} if this object is deprecated. Deprecated objects exist in some
     * {@linkplain org.opengis.referencing.AuthorityFactory authority factories} like the
     * EPSG database. Deprecated objects are usually obtained from a deprecated authority
     * code. For this reason, the default implementation applies the following rules:
     * <p>
     * <ul>
     *   <li>If the {@linkplain #getName() name}
     *       {@linkplain DefaultReferenceIdentifier#isDeprecated() is deprecated},
     *       then returns {@code true}.</li>
     *   <li>Otherwise if <strong>every</strong> {@linkplain #getIdentifiers() identifiers}
     *       {@linkplain DefaultReferenceIdentifier#isDeprecated() are deprecated}, ignoring
     *       the identifiers that are not instance of {@link DefaultReferenceIdentifier}
     *       (because they can not be tested), then returns {@code true}.</li>
     *   <li>Otherwise returns {@code false}.</li>
     * </ul>
     *
     * @return {@code true} if this object is deprecated.
     *
     * @see DefaultReferenceIdentifier#isDeprecated()
     *
     * @since 3.20
     */
    @Override
    public boolean isDeprecated() {
        if (name instanceof DefaultReferenceIdentifier) {
            if (((DefaultReferenceIdentifier) name).isDeprecated()) {
                return true;
            }
        }
        boolean isDeprecated = false;
        for (final ReferenceIdentifier identifier : identifiers) {
            if (identifier instanceof DefaultReferenceIdentifier) {
                isDeprecated = ((DefaultReferenceIdentifier) identifier).isDeprecated();
                if (!isDeprecated) break;
            }
        }
        return isDeprecated;
    }

    /**
     * Compares the specified object with this object for equality.
     * This method is implemented as below (omitting assertions):
     *
     * {@preformat java
     *     return equals(other, ComparisonMode.STRICT);
     * }
     *
     * @param  object The other object (may be {@code null}).
     * @return {@code true} if both objects are equal.
     */
    @Override
    public final boolean equals(final Object object) {
        final boolean eq = equals(object, ComparisonMode.STRICT);
        // If objects are equal, then they must have the same hash code value.
        assert !eq || hashCode() == object.hashCode() : this;
        return eq;
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
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == null) {
            return false;
        }
        final Class<? extends IdentifiedObject> thisType = getClass();
        final Class<?> thatType = object.getClass();
        if (thisType == thatType) {
            /*
             * If the classes are the same, then the hash codes should be computed in the same
             * way. Since those codes are cached, this is an efficient way to quickly check if
             * the two objects are different. Note that using the hash codes for comparisons
             * that ignore metadata is okay only if the implementation note described in the
             * 'computeHashCode()' javadoc hold (metadata not used in hash code computation).
             */
            if (mode.ordinal() < ComparisonMode.APPROXIMATIVE.ordinal()) {
                final int tc = hashCode;
                if (tc != 0) {
                    final int oc = ((AbstractIdentifiedObject) object).hashCode;
                    if (oc != 0 && tc != oc) {
                        return false;
                    }
                }
            }
        } else {
            if (mode == ComparisonMode.STRICT) { // Same classes was required for this mode.
                return false;
            }
            if (!Classes.implementSameInterfaces(thisType, thatType, getReferencingGroup(thisType))) {
                return false;
            }
        }
        switch (mode) {
            case STRICT: {
                final AbstractIdentifiedObject that = (AbstractIdentifiedObject) object;
                return Objects.equals(name,        that.name)        &&
                       Objects.equals(alias,       that.alias)       &&
                       Objects.equals(identifiers, that.identifiers) &&
                       Objects.equals(remarks,     that.remarks);
            }
            case BY_CONTRACT: {
                final IdentifiedObject that = (IdentifiedObject) object;
                return deepEquals(getName(),        that.getName(),        mode) &&
                       deepEquals(getAlias(),       that.getAlias(),       mode) &&
                       deepEquals(getIdentifiers(), that.getIdentifiers(), mode) &&
                       deepEquals(getRemarks(),     that.getRemarks(),     mode);
            }
            case IGNORE_METADATA:
            case APPROXIMATIVE:
            case DEBUG: {
                return true;
            }
            default: {
                throw new IllegalArgumentException(Errors.format(Errors.Keys.UNKNOWN_ENUM_$1, mode));
            }
        }
    }

    /**
     * Returns a hash value for this identified object. This method invokes {@link #computeHashCode()}
     * when first needed and caches the value for future invocations. Subclasses should override
     * {@code computeHashCode()} instead than this method.
     *
     * {@section Implementation specific feature}
     * In the Geotk implementation, the {@linkplain #getName() name}, {@linkplain #getIdentifiers()
     * identifiers} and {@linkplain #getRemarks() remarks} are not used for hash code computation.
     * Consequently two identified objects will return the same hash value if they are equal in the
     * sense of <code>{@linkplain #equals(Object, ComparisonMode) equals}(&hellip;,
     * {@linkplain ComparisonMode#IGNORE_METADATA})</code>. This feature allows users to
     * implement metadata-insensitive {@link java.util.HashMap}.
     *
     * @return The hash code value. This value may change between different execution of the
     *         Geotk library.
     */
    @Override
    public final int hashCode() { // No need to synchronize; ok if invoked twice.
        int hash = hashCode;
        if (hash == 0) {
            hash = computeHashCode();
            if (hash == 0) {
                hash = -1;
            }
            hashCode = hash;
        }
        assert hash == -1 || hash == computeHashCode() : this;
        return hash;
    }

    /**
     * Computes a hash value for this identified object. This method is invoked by
     * {@link #hashCode()} when first needed.
     *
     * {@section Implementation specific feature}
     * In the Geotk implementation, the {@linkplain #getName() name}, {@linkplain #getIdentifiers()
     * identifiers} and {@linkplain #getRemarks() remarks} are not used for hash code computation.
     * Consequently two identified objects will return the same hash value if they are equal in the
     * sense of <code>{@linkplain #equals(Object, ComparisonMode) equals}(&hellip;,
     * {@linkplain ComparisonMode#IGNORE_METADATA})</code>. This feature allows users to
     * implement metadata-insensitive {@link java.util.HashMap}.
     *
     * @return The hash code value. This value may change between different execution of the
     *         Geotk library.
     *
     * @since 3.18
     */
    protected int computeHashCode() {
        // Subclasses need to overrides this!!!!
        int code = (int) serialVersionUID;
        final Class<?>[] types = Classes.getLeafInterfaces(getClass(), IdentifiedObject.class);
        if (types != null) {
            for (final Class<?> type : types) {
                // Use a plain addition in order to be insensitive to array element order.
                code += type.hashCode();
            }
        }
        return code;
    }
}
