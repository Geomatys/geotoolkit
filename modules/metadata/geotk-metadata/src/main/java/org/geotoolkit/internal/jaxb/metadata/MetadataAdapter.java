/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2011, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal.jaxb.metadata;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.geotoolkit.internal.jaxb.gco.PropertyType;


/**
 * Base class for adapters from GeoAPI interfaces to their Geotk implementation.
 * Implementation subclasses are actually both JAXB adapters and wrappers around
 * the value to be marshalled. Wrappers exist because ISO 19139 have the strange
 * habit to wrap every properties in an extra level, for example:
 *
 * {@preformat xml
 *   <CI_ResponsibleParty>
 *     <contactInfo>
 *       <CI_Contact>
 *         ...
 *       </CI_Contact>
 *     </contactInfo>
 *   </CI_ResponsibleParty>
 * }
 *
 * The {@code </CI_Contact>} level is not really necessary, and JAXB is not designed for inserting
 * such level since it is not the usual way to write XML. In order to get this output with JAXB, we
 * have to wrap metadata object in an additional object. So each {@code MetadataAdapter} subclass
 * is both a JAXB adapter and a wrapper. We have merged those functionalities in order to avoid
 * doubling the amount of classes, which is already large.
 * <p>
 * In ISO 19139 terminology:
 * <ul>
 *   <li>the public classes defined in the {@code org.geotoolkit.metadata.iso} packages are defined
 *       as {@code Foo_Type} in ISO 19139, where <var>Foo</var> is the ISO name of a class.</li>
 *   <li>the {@code MetadataAdapter} subclasses are defined as {@code Foo_PropertyType} in
 *       ISO 19139 schemas.</li>
 * </ul>
 *
 * @param <ValueType> The adapter subclass.
 * @param <BoundType> The interface being adapted.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @see XmlAdapter
 *
 * @since 2.5
 * @module
 */
public abstract class MetadataAdapter<ValueType extends MetadataAdapter<ValueType,BoundType>, BoundType>
        extends PropertyType<ValueType,BoundType>
{
    /**
     * Empty constructor for subclasses only.
     */
    protected MetadataAdapter() {
    }

    /**
     * Builds an adapter for the given GeoAPI interface.
     *
     * @param metadata The interface to wrap.
     */
    protected MetadataAdapter(final BoundType metadata) {
        super(metadata);
    }
}
