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
package org.geotoolkit.internal.jaxb.code;

import javax.xml.bind.annotation.XmlElement;
import org.opengis.metadata.identification.InitiativeType;


/**
 * JAXB adapter for {@link InitiativeType}, in order to integrate the value in a element
 * complying with ISO-19139 standard. See package documentation to have more information
 * about the handling of CodeList in ISO-19139.
 *
 * @author Guilhem Legal (Geomatys)
 * @version 3.05
 *
 * @since 3.00
 * @module
 */
public final class DS_InitiativeTypeCode extends CodeListAdapter<DS_InitiativeTypeCode, InitiativeType> {
    /**
     * Ensures that the adapted code list class is loaded.
     */
    static {
        ensureClassLoaded(InitiativeType.class);
    }

    /**
     * Empty constructor for JAXB only.
     */
    public DS_InitiativeTypeCode() {
    }

    /**
     * Creates a new adapter for the given proxy.
     */
    private DS_InitiativeTypeCode(final CodeListProxy proxy) {
        super(proxy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DS_InitiativeTypeCode wrap(CodeListProxy proxy) {
        return new DS_InitiativeTypeCode(proxy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<InitiativeType> getCodeListClass() {
        return InitiativeType.class;
    }

    /**
     * Invoked by JAXB on marshalling.
     *
     * @return The value to be marshalled.
     */
    @Override
    @XmlElement(name = "DS_InitiativeTypeCode")
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
