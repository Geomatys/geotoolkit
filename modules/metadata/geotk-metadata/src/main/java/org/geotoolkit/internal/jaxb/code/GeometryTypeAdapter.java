/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.internal.jaxb.code;

import javax.xml.bind.annotation.XmlElement;
import org.opengis.metadata.acquisition.GeometryType;


/**
 * JAXB adapter for {@link GeometryType}, in order to integrate the value in an element respecting
 * the ISO-19139 standard. See package documentation for more information about the handling
 * of {@code CodeList} in ISO-19139.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.05
 *
 * @since 3.02
 * @module
 */
public final class GeometryTypeAdapter extends CodeListAdapter<GeometryTypeAdapter, GeometryType> {
    /**
     * Ensures that the adapted code list class is loaded.
     */
    static {
        ensureClassLoaded(GeometryType.class);
    }

    /**
     * Empty constructor for JAXB only.
     */
    public GeometryTypeAdapter() {
    }

    /**
     * Creates a new adapter for the given proxy.
     */
    private GeometryTypeAdapter(final CodeListProxy proxy) {
        super(proxy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected GeometryTypeAdapter wrap(CodeListProxy proxy) {
        return new GeometryTypeAdapter(proxy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<GeometryType> getCodeListClass() {
        return GeometryType.class;
    }

    /**
     * Invoked by JAXB on marshalling.
     *
     * @return The value to be marshalled.
     */
    @Override
    @XmlElement(name = "MI_GeometryTypeCode")
    public CodeListProxy getElement() {
        return proxy;
    }

    /**
     * Invoked by JAXB on unmarshalling.
     *
     * @param proxy The unmarshalled value.
     */
    public void setElement(final CodeListProxy proxy) {
        this.proxy = proxy;
    }
}
