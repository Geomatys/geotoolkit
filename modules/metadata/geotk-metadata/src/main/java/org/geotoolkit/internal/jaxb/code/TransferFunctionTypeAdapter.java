/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
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
import org.opengis.metadata.content.TransferFunctionType;


/**
 * JAXB adapter for {@link TransferFunctionType}, in order to integrate the value in an element respecting
 * the ISO-19139 standard. See package documentation for more information about the handling
 * of {@code CodeList} in ISO-19139.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.02
 *
 * @since 3.02
 * @module
 */
public final class TransferFunctionTypeAdapter extends CodeListAdapter<TransferFunctionTypeAdapter, TransferFunctionType> {
    /**
     * Ensures that the adapted code list class is loaded.
     */
    static {
        ensureClassLoaded(TransferFunctionType.class);
    }

    /**
     * Empty constructor for JAXB only.
     */
    public TransferFunctionTypeAdapter() {
    }

    /**
     * Creates a new adapter for the given proxy.
     */
    private TransferFunctionTypeAdapter(final CodeListProxy proxy) {
        super(proxy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected TransferFunctionTypeAdapter wrap(CodeListProxy proxy) {
        return new TransferFunctionTypeAdapter(proxy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<TransferFunctionType> getCodeListClass() {
        return TransferFunctionType.class;
    }

    /**
     * Invoked by JAXB on marshalling.
     *
     * @return The value to be marshalled.
     */
    @XmlElement(name = "MI_TransferFunctionTypeCode")
    public CodeListProxy getCodeListProxy() {
        return proxy;
    }

    /**
     * Invoked by JAXB on unmarshalling.
     *
     * @param proxy The unmarshalled value.
     */
    public void setCodeListProxy(final CodeListProxy proxy) {
        this.proxy = proxy;
    }
}
