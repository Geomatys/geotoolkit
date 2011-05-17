/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011, Geomatys
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


/**
 * A marker interface for empty XML elements. Note that an "empty" XML element may not be an
 * empty Java object, since the Java object can still be associated with {@link XLink} or
 * {@link NilReason} attributes. Those attributes are not part of ISO 19115, but may appear
 * in ISO 19139 XML documents like below:
 *
 * <blockquote><table border="1"><tr bgcolor="lightblue">
 *   <th>Non-empty <code>Series</code> element</th>
 *   <th>Empty <code>Series</code> element</th>
 * </tr><tr><td>
 * {@preformat xml
 *   <gmd:CI_Citation>
 *     <gmd:series>
 *       <gmd:CI_Series>
 *         <!-- Some content here -->
 *       </gmd:CI_Series>
 *     </gmd:series>
 *   </gmd:CI_Citation>
 * }
 * </td><td>
 * {@preformat xml
 *   <gmd:CI_Citation>
 *     <gmd:series nilReason="unknown"/>
 *   </gmd:CI_Citation>
 * }
 * </td></tr></table></blockquote>
 *
 * The reason why an object is empty can be obtained by the {@link #getNilReason()} method.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.18
 * @module
 */
public interface EmptyObject {
    /**
     * Returns the reason why this object is empty.
     *
     * @return The reason why this object is empty.
     */
    NilReason getNilReason();
}
