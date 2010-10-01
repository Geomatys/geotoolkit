/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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
 * JAXB adapters for metadata objects without their wrapper. This package contains adapters for
 * the same objects than the ones handled by {@link org.geotoolkit.internal.jaxb.metadata}, except
 * that the XML is formatted in a "direct" way, without wrappers.
 * <p>
 * <b>Example:</b> given an attribute named {@code "myAttribute"} of type
 * {@link org.opengis.metadata.citation.OnlineResource}, the adapter provided
 * in the parent package would marshall that attribute as below:
 *
 * {@preformat xml
 *   <myAttribute>
 *     <gmd:CI_OnlineResource>
 *       <gmd:linkage>
 *         <gmd:URL>http://blabla.com</gmd:URL>
 *       </gmd:linkage>
 *     </gmd:CI_OnlineResource>
 *   </myAttribute>
 * }
 *
 * Using the adapter provided in this class, the result would rather be:
 *
 * {@preformat xml
 *   <myAttribute>
 *     <gmd:linkage>
 *       <gmd:URL>http://blabla.com</gmd:URL>
 *     </gmd:linkage>
 *   </myAttribute>
 * }
 *
 * @author Guilhem Legal (Geomatys)
 * @version 3.15
 *
 * @since 3.15
 * @module
 */
package org.geotoolkit.internal.jaxb.metadata.direct;
