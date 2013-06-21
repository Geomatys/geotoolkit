/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
 * Provides methods for marshalling and unmarshalling Geotk objects in XML.
 * The XML format is compliant with ISO 19139 specification for metadata, and
 * compliant with GML for referencing objects.
 * <p>
 * The main class in this package is {@link org.geotoolkit.xml.XML}, which provides
 * property keys that can be used for configuring (un)marshallers and convenience
 * static methods. For example the following code:
 *
 * {@preformat java
 *     XML.marshal(Citations.OGC, System.out);
 * }
 *
 * will produce a string like below:
 *
 * {@preformat xml
 *   <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
 *   <gmd:CI_Citation xmlns:gmd="http://www.isotc211.org/2005/gmd"
 *                    xmlns:gco="http://www.isotc211.org/2005/gco">
 *     <gmd:title>
 *       <gco:CharacterString>Open Geospatial Consortium</gco:CharacterString>
 *     </gmd:title>
 *     ... much more XML below this point ...
 *   </gmd:CI_Citation>
 * }
 *
 * {@section Customizing the XML}
 * In order to parse and format ISO 19139 compliant documents, Geotk needs its own
 * {@link javax.xml.bind.Marshaller} and {@link javax.xml.bind.Unmarshaller} instances
 * (which are actually wrappers around standard instances). Those instances are created
 * and cached by {@link org.geotoolkit.xml.MarshallerPool}, which is used internally by
 * the above-cited {@code XML} class. However developers can instantiate their own
 * {@code MarshallerPool} in order to get more control on the marshalling and unmarshalling
 * processes, including the namespace URLs and the errors handling.
 * <p>
 * The most common namespace URLs are defined in the {@link org.geotoolkit.xml.Namespaces} class.
 * The parsing of some objects like {@link java.net.URL} and {@link java.util.UUID}, together
 * with the behavior in case of parsing error, can be specified by the
 * {@link org.geotoolkit.xml.ObjectConverters} class.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Guilhem Legal (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.00
 * @module
 */
@XmlSchema(elementFormDefault = XmlNsForm.QUALIFIED, xmlns = {
    @XmlNs(prefix = "xlink", namespaceURI = Namespaces.XLINK)
})
@XmlAccessorType(XmlAccessType.NONE)
@XmlJavaTypeAdapter(InternationalStringConverter.class)
package org.geotoolkit.xml;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.sis.internal.jaxb.gco.InternationalStringConverter;
