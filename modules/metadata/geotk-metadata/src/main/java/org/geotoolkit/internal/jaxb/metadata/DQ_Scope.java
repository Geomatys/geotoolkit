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
package org.geotoolkit.internal.jaxb.metadata;

import javax.xml.bind.annotation.XmlElementRef;
import org.opengis.metadata.quality.Scope;
import org.apache.sis.metadata.iso.quality.DefaultScope;
import org.geotoolkit.internal.jaxb.gco.PropertyType;


/**
 * JAXB adapter in order to map implementing class with the GeoAPI interface. See
 * package documentation for more information about JAXB and interface.
 *
 * @author Guilhem Legal (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.00
 * @module
 */
public final class DQ_Scope extends PropertyType<DQ_Scope, Scope> {
    /**
     * Empty constructor for JAXB only.
     */
    public DQ_Scope() {
    }

    /**
     * Wraps a Scope value with a {@code MD_Scope} element at marshalling-time.
     *
     * @param metadata The metadata value to marshall.
     */
    private DQ_Scope(final Scope metadata) {
        super(metadata);
    }

    /**
     * Returns the Scope value covered by a {@code MD_Scope} element.
     *
     * @param value The value to marshall.
     * @return The adapter which covers the metadata value.
     */
    @Override
    protected DQ_Scope wrap(final Scope value) {
        return new DQ_Scope(value);
    }

    /**
     * Returns the GeoAPI interface which is bound by this adapter.
     */
    @Override
    protected Class<Scope> getBoundType() {
        return Scope.class;
    }

    /**
     * Returns the {@link DefaultScope} generated from the metadata value.
     * This method is systematically called at marshalling-time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    @XmlElementRef
    public DefaultScope getElement() {
        return skip() ? null : DefaultScope.castOrCopy(metadata);
    }

    /**
     * Sets the value for the {@link DefaultScope}. This method is systematically
     * called at unmarshalling-time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setElement(final DefaultScope metadata) {
        this.metadata = metadata;
    }
}
