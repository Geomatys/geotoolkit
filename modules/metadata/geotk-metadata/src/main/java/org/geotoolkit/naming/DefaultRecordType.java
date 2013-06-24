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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.naming;

import java.util.Set;
import java.util.Map;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlType;
import net.jcip.annotations.Immutable;

import org.opengis.util.Type;
import org.opengis.util.Record;
import org.opengis.util.RecordType;
import org.opengis.util.RecordSchema;
import org.opengis.util.TypeName;
import org.opengis.util.MemberName;
import org.opengis.util.NameSpace;

import org.geotoolkit.util.collection.XCollections;


/**
 * Provides a record of data type in a manner similar to a strongly typed {@link Map}.
 * A record is <em>strongly</em> typed and may be better thought of as a mathematical tuple.
 * The "keys" are strictly controlled {@link MemberName}s and are usually defined in the
 * context of a schema.
 *
 * @author Jody Garnett (Refractions)
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.04
 *
 * @since 2.4
 * @module
 *
 * @deprecated Moved to the {@link org.apache.sis.util.iso} package.
 */
@Deprecated
@Immutable
@XmlType(name = "RecordType")
public class DefaultRecordType implements RecordType, Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -116458723163877388L;

    /**
     * The schema that contains this record type.
     */
    private final RecordSchema parent;

    /**
     * The name that identifies this record type.
     */
    private final TypeName name;

    /**
     * The members and their types.
     */
    private final Map<MemberName,Type> memberTypes;

    /**
     * Empty constructor only used by JAXB.
     */
    private DefaultRecordType() {
        parent = null;
        name   = null;
        memberTypes = Collections.emptyMap();
    }

    /**
     * Creates a record with the same attributes that the given one.
     *
     * @param recordType The {@code RecordType} to copy.
     *
     * @since 3.00
     */
    public DefaultRecordType(final RecordType recordType) {
        parent = recordType.getContainer();
        name = recordType.getTypeName();
        memberTypes = recordType.getMemberTypes();
    }

    /**
     * Creates a record with all member types specified.
     *
     * @param parent  The schema that contains this record type.
     * @param name    The name that identifies this record type.
     * @param members The name of the members to be included in this record type.
     */
    public DefaultRecordType(final RecordSchema parent, final TypeName name,
                             final Map<MemberName,Type> members)
    {
        this.parent = parent;
        this.name = name;
        memberTypes = XCollections.unmodifiableMap(new LinkedHashMap<>(members));
    }

    /**
     * Returns the name that identifies this record type. If this {@code RecordType} is contained
     * in a {@linkplain RecordSchema record schema}, then the record type name should be valid in
     * the {@linkplain NameSpace name space} of the record schema:
     *
     * {@preformat java
     *     getContainer().getSchemaName().scope()
     * }
     *
     * @return The name that identifies this record type.
     */
    @Override
    public TypeName getTypeName() {
        return name;
    }

    /**
     * Returns the schema that contains this record type.
     *
     * @return The schema that contains this record type.
     */
    @Override
    public RecordSchema getContainer() {
        return parent;
    }

    /**
     * Returns the unmodifiable dictionary of all (<var>name</var>, <var>type</var>)
     * pairs in this record type.
     *
     * @return The dictionary of (<var>name</var>, <var>type</var>) pairs.
     */
    @Override
    public Map<MemberName,Type> getMemberTypes() {
        return memberTypes;
    }

    /**
     * Returns the set of attribute names defined in this {@code RecordType}'s dictionary.
     * If there are no attributes, this method returns the empty set.
     * <p>
     * This method is functionally equivalent to:
     *
     * {@preformat java
     *     getAttributeTypes().keySet();
     * }
     *
     * @return The set of attribute names.
     */
    @Override
    public Set<MemberName> getMembers() {
        return memberTypes.keySet();
    }

    /**
     * Looks up the provided attribute name and returns the associated type name. If the attribute
     * name is not defined in this record type, then this method returns {@code null}.
     * <p>
     * This method is functionally equivalent to:
     *
     * {@preformat java
     *     getAttributeTypes().get(name).getTypeName();
     * }
     *
     * @param  memberName The attribute name for which to get the associated type name.
     * @return The associated type name, or {@code null} if none.
     */
    @Override
    public TypeName locate(final MemberName memberName) {
        final Type type = memberTypes.get(memberName);
        return (type != null) ? type.getTypeName() : null;
    }

    /**
     * Determines if the specified record is compatible with this record type. This method returns
     * {@code true} if the specified {@code record} argument is non-null and the following condition
     * holds:
     *
     * {@preformat java
     *     getMembers().containsAll(record.getAttributes().keySet())
     * }
     * @param  record The record to test for compatibility.
     * @return {@code true} if the given record is compatible with this {@code RecordType}.
     */
    @Override
    public boolean isInstance(final Record record) {
        return memberTypes.keySet().containsAll(record.getAttributes().keySet());
    }

    /**
     * Returns a hash code value for this {@code RecordType}.
     */
    @Override
    public int hashCode() {
        int code = memberTypes.hashCode();
        if (name   != null) code = 31*code + name.hashCode();
        if (parent != null) code = 31*code + parent.hashCode();
        return code;
    }

    /**
     * Compares the given object with this {@code RecordType} for equality.
     *
     * @param other The object to compare with this {@code RecordType}.
     * @return {@code true} if both objects are equal.
     */
    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (other != null && other.getClass() == getClass()) {
            final DefaultRecordType that = (DefaultRecordType) other;
            return Objects.equals(name, that.name) &&
                   Objects.equals(parent, that.parent) &&
                   Objects.equals(memberTypes, that.memberTypes);
        }
        return false;
    }

    /**
     * Returns a string representation of this {@code RecordType}.
     */
    @Override
    public String toString() {
        return "RecordType[\"" + name + "\"]";
    }
}
