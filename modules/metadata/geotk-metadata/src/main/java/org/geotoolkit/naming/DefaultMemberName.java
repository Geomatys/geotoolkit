/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011-2012, Geomatys
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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import net.jcip.annotations.Immutable;

import org.opengis.util.MemberName;
import org.opengis.util.NameSpace;
import org.opengis.util.TypeName;

import static org.apache.sis.util.ArgumentChecks.ensureNonNull;


/**
 * The name to identify a member of a {@linkplain org.opengis.util.Record record}.
 * <p>
 * {@code DefaultMemberName} can be instantiated by any of the following methods:
 * <ul>
 *   <li>{@link DefaultNameFactory#createMemberName(NameSpace, CharSequence, TypeName)}</li>
 * </ul>
 *
 * @author Guilhem Legal (Geomatys)
 * @version 3.17
 *
 * @since 3.17
 * @module
 */
@Immutable
@XmlRootElement(name = "MemberName")
public class DefaultMemberName extends DefaultLocalName implements MemberName {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 6252686806895124457L;

    /**
     * The type of the data associated with the record member.
     */
    @XmlElement(required = true)
    private final TypeName attributeType;

    /**
     * Empty constructor to be used by JAXB only. Despite its "final" declaration,
     * the {@link #attributeType} field will be set by JAXB during unmarshalling.
     */
    private DefaultMemberName() {
        attributeType = null;
    }

    /**
     * Constructs a member name from the given character sequence and attribute type.
     *
     * @param scope The scope of this name, or {@code null} for a global scope.
     * @param name The local name (never {@code null}).
     * @param attributeType The type of the data associated with the record member (never {@code null}).
     */
    protected DefaultMemberName(final NameSpace scope, final CharSequence name, final TypeName attributeType) {
        super(scope, name);
        ensureNonNull("attributeType", attributeType);
        this.attributeType = attributeType;
    }

    /**
     * Returns the type of the data associated with the record member.
     */
    @Override
    public TypeName getAttributeType() {
        return attributeType;
    }
}
