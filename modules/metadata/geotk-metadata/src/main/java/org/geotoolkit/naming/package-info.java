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
 * Implementation of naming interfaces from {@link org.opengis.util}. Names are <em>immutables</em>.
 * They may be {@linkplain org.geotoolkit.naming.AbstractName#toFullyQualifiedName fully qualified}
 * like {@code "org.opengis.util.Record"}, or they may be relative to a
 * {@linkplain org.geotoolkit.naming.AbstractName#scope scope} like {@code "util.Record"} in the
 * {@code "org.opengis"} scope. The illustration below shows all possible constructions for
 * {@code "org.opengis.util.Record"}.
 *
 * <blockquote><table border="1" cellpadding="15"><tr><td><table border="0" cellspacing="0">
 *   <tr>
 *     <th align="right">org</th>
 *     <th>.</th><th>opengis</th>
 *     <th>.</th><th>util</th>
 *     <th>.</th><th>Record</th>
 *     <th width="50"></th>
 *     <th>{@link org.geotoolkit.naming.AbstractName#scope() scope()}</th>
 *     <th>{@link org.geotoolkit.naming.AbstractName#getParsedNames() getParsedNames()}</th>
 *     <th width="50"></th>
 *     <th>Type</th>
 *   </tr>
 *
 *   <tr align="center">
 *     <td bgcolor="palegoldenrod" colspan="1"><font size="-1">{@linkplain org.geotoolkit.naming.AbstractName#head() head}</font></td><td></td>
 *     <td bgcolor="palegoldenrod" colspan="5"><font size="-1">{@linkplain org.geotoolkit.naming.DefaultScopedName#tail() tail}</font></td>
 *     <td rowspan="2"></td>
 *     <td rowspan="2" bgcolor="beige" align="left">{@linkplain org.geotoolkit.naming.DefaultNameSpace#isGlobal() global}</td>
 *     <td rowspan="2" bgcolor="beige" align="right">{@literal {"org", "opengis", "util", "Record"}}</td>
 *     <td rowspan="2"></td>
 *     <td rowspan="2">{@link org.geotoolkit.naming.DefaultScopedName ScopedName}</td>
 *   </tr>
 *   <tr align="center">
 *     <td bgcolor="wheat" colspan="5"><font size="-1">{@linkplain org.geotoolkit.naming.DefaultScopedName#path() path}</font></td><td></td>
 *     <td bgcolor="wheat" colspan="1"><font size="-1">{@linkplain org.geotoolkit.naming.AbstractName#tip() tip}</font></td>
 *   </tr>
 *
 *   <tr><td colspan="9" height="2"></td></tr>
 *   <tr align="center">
 *     <td bgcolor="palegoldenrod" colspan="1" rowspan="2"><font size="-1">{@linkplain org.geotoolkit.naming.AbstractName#scope scope}</font></td><td rowspan="2"></td>
 *     <td bgcolor="palegoldenrod" colspan="1"><font size="-1">head</font></td><td></td>
 *     <td bgcolor="palegoldenrod" colspan="3"><font size="-1">tail</font></td>
 *     <td rowspan="2"></td>
 *     <td rowspan="2" bgcolor="beige" align="left">{@literal "org"}</td>
 *     <td rowspan="2" bgcolor="beige" align="right">{@literal {"opengis", "util", "Record"}}</td>
 *     <td rowspan="2"></td>
 *     <td rowspan="2">{@code ScopedName}</td>
 *   </tr>
 *   <tr align="center">
 *     <td bgcolor="wheat" colspan="3"><font size="-1">path</font></td><td></td>
 *     <td bgcolor="wheat" colspan="1"><font size="-1">tip</font></td>
 *   </tr>
 *
 *   <tr><td colspan="9" height="3"></td></tr>
 *   <tr align="center">
 *     <td bgcolor="palegoldenrod" colspan="3" rowspan="2"><font size="-1">scope</font></td><td rowspan="2"></td>
 *     <td bgcolor="palegoldenrod" colspan="1"><font size="-1">head</font></td><td></td>
 *     <td bgcolor="palegoldenrod" colspan="1"><font size="-1">tail</font></td>
 *     <td rowspan="2"></td>
 *     <td rowspan="2" bgcolor="beige" align="left">{@literal "org.opengis"}</td>
 *     <td rowspan="2" bgcolor="beige" align="right">{@literal {"util", "Record"}}</td>
 *     <td rowspan="2"></td>
 *     <td rowspan="2">{@code ScopedName}</td>
 *   </tr>
 *   <tr align="center">
 *     <td bgcolor="wheat" colspan="1"><font size="-1">path</font></td><td></td>
 *     <td bgcolor="wheat" colspan="1"><font size="-1">tip</font></td>
 *   </tr>
 *
 *   <tr><td colspan="9" height="3"></td></tr>
 *   <tr align="center">
 *     <td bgcolor="palegoldenrod" colspan="5" rowspan="2"><font size="-1">scope</font></td><td rowspan="2"></td>
 *     <td bgcolor="palegoldenrod" colspan="1"><font size="-1">head</font></td>
 *     <td rowspan="2"></td>
 *     <td rowspan="2" bgcolor="beige" align="left">{@literal "org.opengis.util"}</td>
 *     <td rowspan="2" bgcolor="beige" align="right">{@literal {"Record"}}</td>
 *     <td rowspan="2"></td>
 *     <td rowspan="2">{@link org.geotoolkit.naming.DefaultLocalName LocalName}</td>
 *   </tr>
 *   <tr align="center">
 *     <td bgcolor="wheat" colspan="1"><font size="-1">tip</font></td>
 *   </tr>
 * </table></td></tr></table></blockquote>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.01
 *
 * @since 3.00
 * @module
 */
@XmlSchema(elementFormDefault = XmlNsForm.QUALIFIED, namespace = Namespaces.GCO, xmlns = {
    @XmlNs(prefix = "gco", namespaceURI = Namespaces.GCO)
})
@XmlAccessorType(XmlAccessType.NONE)
@XmlJavaTypeAdapters({
    @XmlJavaTypeAdapter(GO_GenericName.class),
    @XmlJavaTypeAdapter(LocalNameAdapter.class),
    @XmlJavaTypeAdapter(ScopedNameAdapter.class)
})
package org.geotoolkit.naming;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;

import org.apache.sis.xml.Namespaces;
import org.geotoolkit.internal.jaxb.gco.*;
