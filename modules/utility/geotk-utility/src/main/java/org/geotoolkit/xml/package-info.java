/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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
 * Provides methods for marshalling and unmarshalling Geotk objects in XML.
 * The XML format is compliant with ISO 19139 specification for metadata, and
 * compliant with GML for referencing objects.
 * <p>
 * The easiest way to use this package is to use the static methods defined in
 * the {@link org.geotoolkit.xml.XML} class. For example the following code:
 *
 * {@preformat java
 *     String xml = XML.marshal(Citations.OGC);
 *     System.out.println(xml);
 * }
 *
 * will produce a string like below:
 *
 * {@preformat xml
 *   <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
 *   <gmd:CI_Citation xmlns:gmd="http://www.isotc211.org/2005/gmd"
 *                    xmlns:gco="http://www.isotc211.org/2005/gco">
 *     <gmd:title>
 *       <gco:CharacterString>My citation</gco:CharacterString>
 *     </gmd:title>
 *   </gmd:CI_Citation>
 * }
 *
 * The static methods defined in the {@code XML} class use internaly a shared
 * {@link org.geotoolkit.xml.MarshallerPool}. Developers can instantiate their
 * one {@code MarshallerPool} in order to get more control on the marshalling
 * and unmarshalling processes, including the namespaces to declare and the errors
 * handling.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Guilhem Legal (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.00
 * @module
 */
package org.geotoolkit.xml;
