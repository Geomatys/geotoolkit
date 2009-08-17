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
import java.util.HashMap;
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

import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.Factories;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.naming.DefaultNameFactory;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.util.collection.WeakValueHashMap;
import org.geotoolkit.util.Utilities;


/**
 * An identification of a CRS object which is both an {@linkplain Identifier identifier}
 * and a {@linkplain GenericName name}. The main interface implemented by this class is
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
     * The name of this identifier as a generic name. If {@code null}, will be constructed
     * only when first needed. This field is serialized (instead of being recreated after
     * deserialization) because it may be a user-supplied value.
     */
    private GenericName name;

    /**
     * Constructs an identifier from a set of properties. The content of the properties map is used
     * as described in the {@linkplain DefaultReferenceIdentifier#DefaultReferenceIdentifier(Map)
     * super-class constructor}.
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
        super(properties, standalone);
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
     * The last element in the sequence of {@linkplain #getParsedNames parsed names}.
     * By default, this is the same value than {@link #getCode} provided as a local name.
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
     */
    @Override
    public List<? extends LocalName> getParsedNames() {
        return getName().getParsedNames();
    }

    /**
     * Returns this name expanded with the specified scope. One may represent this operation
     * as a concatenation of the specified {@code name} with {@code this}.
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
            return Utilities.equals(this.getName(), that.getName());
        }
        return false;
    }
}
