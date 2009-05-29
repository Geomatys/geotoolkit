/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.gml.xml.v311modified;

import java.util.List;

/**
 * gml:ReferenceType is intended to be used in application schemas directly, if a property element shall use a "by-reference only" encoding.
 *
 * @author Guilhem Legal
 */
interface Reference {
    
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
   
    /**
     * Encoding a GML property inline vs. by-reference shall not imply anything about the "ownership" of the contained or referenced GML Object, i.e. the encoding style shall not imply any "deep-copy" or "deep-delete" semantics. 
     * To express ownership over the contained or referenced GML Object, the gml:OwnershipAttributeGroup attribute group may be added to object-valued property elements. If the attribute group is not part of the content model of such a property element, then the value may not be "owned".
     * When the value of the owns attribute is "true", the existence of inline or referenced object(s) depends upon the existence of the parent object.
     */
    java.lang.Boolean getOwns();
    
}
