/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.gml.xml;

import java.util.List;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @module
 */
public interface LocationProperty {

    Code getLocationKeyWord();

    List<String> getNilReason();

    String getRemoteSchema();

    /**
     * The 'actuate' attribute is used to communicate the desired timing
     *   of traversal from the starting resource to the ending resource;
     *   it's value should be treated as follows:
     *   onLoad - traverse to the ending resource immediately on loading
     *            the starting resource
     *   onRequest - traverse from the starting resource to the ending
     *               resource only on a post-loading event triggered for
     *               this purpose
     *   other - behavior is unconstrained; examine other markup in link
     *           for hints
     *   none - behavior is unconstrained
     */
    String getActuate();

    String getArcrole();

    String getHref();

    String getRole();

    /**
     * The 'show' attribute is used to communicate the desired presentation
     *   of the ending resource on traversal from the starting resource; it's
     *   value should be treated as follows:
     *   new - load ending resource in a new window, frame, pane, or other
     *         presentation context
     *   replace - load the resource in the same window, frame, pane, or
     *             other presentation context
     *   embed - load ending resource in place of the presentation of the
     *           starting resource
     *   other - behavior is unconstrained; examine other markup in the
     *           link for hints
     *   none - behavior is unconstrained
     */
    String getShow();

    String getTitle();

    String getType();

}
