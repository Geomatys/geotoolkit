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

import java.util.UUID;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.geotoolkit.xml.Namespaces;
import org.geotoolkit.internal.jaxb.UUIDs;
import org.geotoolkit.internal.jaxb.MarshalContext;


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
 * @version 3.13
 *
 * @see XmlAdapter
 *
 * @since 2.5
 * @module
 */
public abstract class MetadataAdapter<ValueType extends MetadataAdapter<ValueType,BoundType>, BoundType>
        extends XmlAdapter<ValueType,BoundType>
{
    /**
     * The wrapped GeoAPI metadata interface.
     */
    protected BoundType metadata;

    /**
     * An identifier for the metadata, or {@code null} if none. This field is initialized
     * at construction time to the value registered in {@link UUIDs}, if any.
     *
     * @since 3.13
     */
    @XmlAttribute(namespace = Namespaces.GCO)
    protected String uuid;

    /**
     * A URN to an external resources, or to an other part of a XML document, or an identifier.
     * The {@code uuidref} attribute is used to refer to an XML element that has a corresponding
     * {@code uuid} attribute.
     *
     * @see <a href="http://www.schemacentral.com/sc/niem21/a-uuidref-1.html">Usage of uuidref</a>
     *
     * @since 3.13
     */
    @XmlAttribute(namespace = Namespaces.GCO)
    protected String uuidref;

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
        this.metadata = metadata;
        final UUID id = UUIDs.DEFAULT.getUUID(metadata);
        if (id != null) {
            uuid = id.toString();
        }
    }

    /**
     * Creates a new instance of this class wrapping the given metadata.
     * This method is invoked by {@link #marshal} after making sure that
     * {@code value} is not null.
     *
     * @param value The GeoAPI interface to wrap.
     * @return The adapter.
     */
    protected abstract ValueType wrap(final BoundType value);

    /**
     * Converts a GeoAPI interface to the appropriate adapter for the way it will be
     * marshalled into an XML file or stream. JAXB calls automatically this method at
     * marshalling time.
     *
     * @param value The bound type value, here the interface.
     * @return The adapter for the given value.
     */
    @Override
    public final ValueType marshal(final BoundType value) {
        if (value == null) {
            return null;
        }
        return wrap(value);
    }

    /**
     * Converts an adapter read from an XML stream to the GeoAPI interface which will
     * contains this value. JAXB calls automatically this method at unmarshalling time.
     *
     * @param value The adapter for a metadata value.
     * @return An instance of the GeoAPI interface which represents the metadata value.
     *
     * @todo We should replace the (BoundType) cast by a call to Class.cast(Object).
     */
    @Override
    @SuppressWarnings("unchecked")
    public final BoundType unmarshal(final ValueType value) {
        if (value == null) {
            return null;
        }
        if (value.metadata == null && uuidref != null) {
            final UUID uuid = MarshalContext.converters().toUUID(uuidref);
            value.metadata = (BoundType) UUIDs.DEFAULT.lookup(uuid);
        }
        return value.metadata;
    }

    /**
     * Returns the Geotk implementation class generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     * <p>
     * The return value is usually an implementation of {@code BoundType}. But in
     * some situations this is Java type like {@link String}. For this raison the
     * return type is declared as {@code Object} here, but subclasses shall restrict
     * that to a more specific type.
     *
     * @return The metadata to be marshalled.
     *
     * @since 3.05
     */
    public abstract Object getElement();
}
