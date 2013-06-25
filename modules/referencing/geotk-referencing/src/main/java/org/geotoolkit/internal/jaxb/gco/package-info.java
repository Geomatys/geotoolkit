/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
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

/**
 * Miscellaneous objects and adapters defined in the {@code "gco"} namespace.
 * For example, a {@link java.lang.String} value has to be marshalled this way:
 *
 * {@preformat text
 *     <gco:CharacterString>my text</gco:CharacterString>
 * }
 *
 * In this example, {@code gco} is the prefix for the {@code http://www.isotc211.org/2005/gco}
 * namespace URL.
 * <p>
 * This package includes:
 *
 * <ul>
 *   <li><p>JAXB adapters for primitive types.
 *   JAXB can write directly Java primitive type at marshalling time "as is". However ISO-19139
 *   requires those values to be surrounded by elements representing the data type. The role of
 *   these adapters is to add these elements around the value.</p></li>
 *
 *   <li><p>JAXB adapters for <cite>unit of measure</cite> as specified in the ISO-19103
 *   specifications. For example, a measure marshalled with JAXB will be formatted like
 *   {@code <gco:Measure uom="m">220.0</gco:Measure>}.</p></li>
 *
 *   <li><p>JAXB adapters for date and time.</p></li>
 * </ul>
 *
 * Classes prefixed by two letters, like {@code "GO_Decimal"}, are also wrappers around the actual
 * object to be marshalled. See the {@link org.geotoolkit.internal.jaxb.metadata} package for more
 * explanation around wrappers. Note that the two-letters prefixes used in this package (not to be
 * confused with the three-letters prefixes used in XML documents) are not defined by OGC/ISO
 * specifications; they are used only for consistency with current practice in
 * {@link org.geotoolkit.internal.jaxb.metadata} and similar packages.
 *
 * {@section Object identification and reference}
 * <ul>
 *   <li><p><code>org.geotoolkit.<b>metadata.iso</b></code> public packages:
 *   <ul>
 *     <li>Implement the ISO 19139 {@code Foo_Type}, where <var>Foo</var> is the ISO name of a class.</li>
 *     <li>Contains the {@code gco:ObjectIdentification} group of attributes ({@code id}, {@code uuid}).</li>
 *     <li>May be subclasses of {@link org.apache.sis.internal.jaxb.gco.ObjectIdentification} (but this is not always possible).</li>
 *   </ul></p></li>
 *   <li><p><code>org.geotoolkit.<b>internal.jaxb</b></code> private packages:
 *   <ul>
 *     <li>Implement the ISO 19139 {@code Foo_PropertyType} as subclasses of the {@link org.apache.sis.internal.jaxb.gco.PropertyType} class.</li>
 *     <li>Contains the {@code gco:ObjectReference} group of attributes ({@code xlink}, {@code uuidref}).</li>
 *     <li>Attributes are declared in the {@link org.apache.sis.internal.jaxb.gco.ObjectReference} Java class.</li>
 *    </ul></p></li>
 * </ul>
 * <p>
 * Those two kinds of types are marshalled as below:
 *
 * {@preformat xml
 *   <MD_MetaData>
 *     <property uuidref="…">
 *       <Foo_Type uuid="…">
 *         ...
 *       </Foo_Type>
 *     </property>
 *   </MD_MetaData>
 * }
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @see javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
 *
 * @since 2.5
 * @module
 */
@XmlSchema(elementFormDefault = XmlNsForm.QUALIFIED, namespace = Namespaces.GCO, xmlns = {
    @XmlNs(prefix = "gco", namespaceURI = Namespaces.GCO),
    @XmlNs(prefix = "gmx", namespaceURI = Namespaces.GMX)
})
@XmlAccessorType(XmlAccessType.NONE)
/*
 * Do NOT define a package-level adapter for InternationalString,
 * because such adapter shall NOT apply to GO_CharacterString.getAnchor().
 */
package org.geotoolkit.internal.jaxb.gco;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import org.apache.sis.xml.Namespaces;
