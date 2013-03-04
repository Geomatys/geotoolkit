/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.naming;

import java.util.List;
import java.util.Collections;
import java.io.ObjectStreamException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import net.jcip.annotations.Immutable;

import org.opengis.util.NameSpace;
import org.opengis.util.LocalName;
import org.opengis.util.GenericName;
import org.opengis.util.InternationalString;

import org.geotoolkit.util.Utilities;
import org.geotoolkit.util.SimpleInternationalString;
import org.geotoolkit.internal.jaxb.gco.CharSequenceAdapter;
import org.geotoolkit.xml.Namespaces;

import static org.apache.sis.util.ArgumentChecks.ensureNonNull;


/**
 * Identifier within a {@linkplain NameSpace name space} for a local object. Local names are
 * names which are directly accessible to and maintained by a name space. Names are local to
 * one and only one name space. The name space within which they are local is indicated by
 * the {@linkplain #scope() scope}.
 * <p>
 * {@code DefaultLocalName} can be instantiated by any of the following methods:
 * <ul>
 *   <li>{@link DefaultNameFactory#createLocalName(NameSpace, CharSequence)}</li>
 *   <li>{@link DefaultNameFactory#createGenericName(NameSpace, CharSequence[])} with an array of length 1</li>
 *   <li>{@link DefaultNameFactory#parseGenericName(NameSpace, CharSequence)} without separator</li>
 * </ul>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 2.1
 * @module
 */
@Immutable
@XmlRootElement(name = "LocalName")
public class DefaultLocalName extends AbstractName implements LocalName {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 8747478206456790138L;

    /**
     * The scope of this name, or {@code null} if the scope is the unique {@code GLOBAL} instance.
     * We don't use direct reference to {@code GLOBAL} because {@code null} is used as a sentinel
     * value for stopping iterative searches (using GLOBAL would have higher risk of never-ending
     * loops in case of bug), and in order to reduce the stream size during serialization.
     */
    final NameSpace scope;

    /**
     * The name, either as a {@link String} or an {@link InternationalString}.
     */
    @XmlJavaTypeAdapter(CharSequenceAdapter.class)
    @XmlElement(name = "aName", namespace = Namespaces.GCO)
    final CharSequence name;

    /**
     * Empty constructor to be used by JAXB only. Despite its "final" declaration,
     * the {@link #name} field will be set by JAXB during unmarshalling.
     */
    DefaultLocalName() {
        scope = null;
        name = null;
    }

    /**
     * Constructs a local name from the given character sequence. If the character sequence is an
     * instance of {@link InternationalString}, then its {@link InternationalString#toString(java.util.Locale)
     * toString(null)} method will be invoked for fetching an unlocalized name. Otherwise
     * the {@link CharSequence#toString() toString()} method will be used.
     *
     * @param scope The scope of this name, or {@code null} for a global scope.
     * @param name The local name (never {@code null}).
     */
    protected DefaultLocalName(NameSpace scope, final CharSequence name) {
        ensureNonNull("name", name);
        if (GlobalNameSpace.GLOBAL == scope) {
            scope = null; // Handled specially by scope().
        }
        this.scope = scope;
        if (name instanceof InternationalString) {
            if (name.getClass() == SimpleInternationalString.class) {
                /*
                 * In the special case of SimpleInternationalString, we will retain the String
                 * flavor instead than InternationalString (this is done by name.toString() at
                 * the end of this constructor). It will not cause any lost of information since
                 * SimpleInternationalString contains only one String. This simplification allows
                 * the equals(Object) method to return "true" for DefaultLocalName that would
                 * otherwise be considered different.
                 *
                 * In order to reduce the amount of objects created, we retain the full
                 * InternationalString in the "asString" field, which is NOT considered
                 * by equals(Object). This is the value returned by toInternationalString().
                 */
                asString = name;
            } else {
                /*
                 * For any InternationalString that are not SimpleInternationalString, we retain
                 * the given name and we do NOT set the "asString" field. It will be computed on
                 * the fly when first needed.
                 */
                this.name = name;
                return;
            }
        }
        this.name = name.toString();
    }

    /**
     * Returns the scope (name space) in which this name is local. This method returns a
     * non-null value in all cases, even when the scope given to the constructor was null.
     */
    @Override
    public NameSpace scope() {
        return (scope != null) ? scope : GlobalNameSpace.GLOBAL;
    }

    /**
     * Returns the depth, which is always 1 for a local name.
     */
    @Override
    public final int depth() {
        return 1;
    }

    /**
     * Returns the sequence of local name for this {@linkplain GenericName generic name}.
     * Since this object is itself a locale name, this method always returns a singleton
     * containing only {@code this}.
     */
    @Override
    public final List<DefaultLocalName> getParsedNames() {
        return Collections.singletonList(this);
    }

    /**
     * Returns {@code this} since this object is already a local name.
     */
    @Override
    public final LocalName head() {
        return this;
    }

    /**
     * Returns {@code this} since this object is already a local name.
     */
    @Override
    public final LocalName tip() {
        return this;
    }

    /**
     * Returns a locale-independent string representation of this local name.
     * This string does not include the scope, which is consistent with the
     * {@linkplain #getParsedNames() parsed names} definition.
     */
    @Override
    public synchronized String toString() {
        if (asString == null) {
            if (name instanceof InternationalString) {
                // We really want the 'null' locale, not the system default one.
                asString = ((InternationalString) name).toString(null);
            } else {
                asString = name.toString();
            }
        } else if (asString instanceof InternationalString) {
            return ((InternationalString) asString).toString(null);
        }
        return asString.toString();
    }

    /**
     * Returns a local-dependent string representation of this locale name.
     */
    @Override
    public synchronized InternationalString toInternationalString() {
        if (!(asString instanceof InternationalString)) {
            if (name instanceof InternationalString) {
                asString = name;
            } else {
                asString = new SimpleInternationalString(name.toString());
            }
        }
        return (InternationalString) asString;
    }

    /**
     * Compares this name with the specified object for order. Returns a negative integer,
     * zero, or a positive integer as this name lexicographically precedes, is equal to,
     * or follows the specified object. The comparison is case-insensitive.
     *
     * @param name The other name to compare with this name.
     * @return -1 if this name precedes the given one, +1 if it follows, 0 if equals.
     */
    @Override
    public int compareTo(final GenericName name) {
        if (name instanceof LocalName) {
            return toString().compareToIgnoreCase(name.toString());
        } else {
            return super.compareTo(name);
        }
    }

    /**
     * Compares this local name with the specified object for equality.
     *
     * @param object The object to compare with this name for equality.
     * @return {@code true} if the given object is equal to this name.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object != null && object.getClass() == getClass()) {
            final DefaultLocalName that = (DefaultLocalName) object;
            return Utilities.equals(this.scope, that.scope) &&
                   Utilities.equals(this.name,  that.name);
        }
        return false;
    }

    /**
     * Returns a hash code value for this local name.
     */
    @Override
    public int hashCode() {
        if (hash == 0) {
            int code = (int) serialVersionUID;
            if (scope != null) {
                code ^= scope.hashCode();
            }
            if (name != null) {
                code += 31 * name.hashCode();
            }
            hash = code;
        }
        return hash;
    }

    /**
     * If an instance already exists for the deserialized name, returns that instance.
     * <p>
     * Because of its private access, this method is <strong>not</strong> invoked if the
     * deserialized class is a subclass. This is the intended behavior since we don't want
     * to replace an instance of a user-defined class.
     *
     * @return The unique instance.
     * @throws ObjectStreamException Should never happen.
     */
    private Object readResolve() throws ObjectStreamException {
        final DefaultNameSpace ns;
        if (scope == null) { // NOSONAR: readResolve() is intentionally private.
            ns = GlobalNameSpace.GLOBAL;
        } else if (scope instanceof DefaultNameSpace) {
            ns = (DefaultNameSpace) scope;
        } else {
            return this;
        }
        return ns.local(name, this);
    }
}
