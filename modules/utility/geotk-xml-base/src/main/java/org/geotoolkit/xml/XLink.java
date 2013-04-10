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
package org.geotoolkit.xml;

import javax.xml.bind.annotation.XmlTransient;
import net.jcip.annotations.ThreadSafe;


/**
 * The XML attributes defined by OGC in the
 * <a href="http://schemas.opengis.net/xlink/1.0.0/xlinks.xsd">xlink</a> schema.
 *
 * The allowed combinations of any one attribute depend on the value of the special
 * {@link #getType() type} attribute. Following is a summary of the element types
 * (columns) on which the global attributes (rows) are allowed, with an indication
 * of whether a value is required (R) or optional (O)
 * (Source: <a href="http://www.w3.org/TR/xlink/">W3C</a>):
 *
 * <blockquote><table border="1" cellspacing="0" cellpadding="1">
 * <tr bgcolor="lightblue">
 *   <th>&nbsp;</th>
 *   <th width="14%">{@link XLink.Type#SIMPLE simple}</th>
 *   <th width="14%">{@link XLink.Type#EXTENDED extended}</th>
 *   <th width="14%">{@link XLink.Type#LOCATOR locator}</th>
 *   <th width="14%">{@link XLink.Type#ARC arc}</th>
 *   <th width="14%">{@link XLink.Type#RESOURCE resource}</th>
 *   <th width="14%">{@link XLink.Type#TITLE title}</th>
 * </tr>
 *   <tr align="center"><th align="left">&nbsp;{@link #getType() type}</th>
 *   <td>R</td><td>R</td><td>R</td><td>R</td><td>R</td><td>R</td>
 * </tr>
 *   <tr align="center"><th align="left">&nbsp;{@link #getHRef() href}</th>
 *   <td>O</td><td>&nbsp;</td><td>R</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td>
 * </tr>
 *   <tr align="center"><th align="left">&nbsp;{@link #getRole() role}</th>
 *   <td>O</td><td>O</td><td>O</td><td>&nbsp;</td><td>O</td><td>&nbsp;</td>
 * </tr>
 *   <tr align="center"><th align="left">&nbsp;{@link #getArcRole() arcrole}</th>
 *   <td>O</td><td>&nbsp;</td><td>&nbsp;</td><td>O</td><td>&nbsp;</td><td>&nbsp;</td>
 * </tr>
 *   <tr align="center"><th align="left">&nbsp;{@link #getTitle() title}</th>
 *   <td>O</td><td>O</td><td>O</td><td>O</td><td>O</td><td>&nbsp;</td>
 * </tr>
 *   <tr align="center"><th align="left">&nbsp;{@link #getShow() show}</th>
 *   <td>O</td><td>&nbsp;</td><td>&nbsp;</td><td>O</td><td>&nbsp;</td><td>&nbsp;</td>
 * </tr>
 *   <tr align="center"><th align="left">&nbsp;{@link #getActuate() actuate}</th>
 *   <td>O</td><td>&nbsp;</td><td>&nbsp;</td><td>O</td><td>&nbsp;</td><td>&nbsp;</td>
 * </tr>
 *   <tr align="center"><th align="left">&nbsp;{@link #getLabel() label}</th>
 *   <td>&nbsp;</td><td>&nbsp;</td><td>O</td><td>&nbsp;</td><td>O</td><td>&nbsp;</td>
 * </tr>
 *   <tr align="center"><th align="left">&nbsp;{@link #getFrom() from}</th>
 *   <td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>O</td><td>&nbsp;</td><td>&nbsp;</td>
 * </tr>
 *   <tr align="center"><th align="left">&nbsp;{@link #getTo() to}</th>
 *   <td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>O</td><td>&nbsp;</td><td>&nbsp;</td>
 * </tr></table></blockquote>
 *
 * When {@code xlink} attributes are found at unmarshalling time instead of an object definition,
 * those attributes are given to the {@link ObjectLinker#resolve(Class, XLink)} method. Users can
 * override that method in order to fetch an instance in some catalog for the given {@code xlink}
 * values.
 *
 * @author Guilhem Legal (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @see <a href="http://www.w3.org/TR/xlink/">XML Linking Language</a>
 * @see <a href="http://schemas.opengis.net/xlink/1.0.0/xlinks.xsd">OGC schema</a>
 *
 * @since 3.18 (derived from 2.5)
 * @module
 *
 * @deprecated Moved to Apache SIS as {@link org.apache.sis.xml.XLink}.
 */
@ThreadSafe
@XmlTransient
@Deprecated
public class XLink extends org.apache.sis.xml.XLink {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -4349950135677857726L;

    /**
     * Creates a new link. The initial value of all attributes is {@code null}.
     */
    public XLink() {
    }

    /**
     * Creates a new link as a copy of the given link.
     *
     * @param link The link to copy, or {@code null} if none.
     */
    public XLink(final org.apache.sis.xml.XLink link) {
        super(link);
    }
}
