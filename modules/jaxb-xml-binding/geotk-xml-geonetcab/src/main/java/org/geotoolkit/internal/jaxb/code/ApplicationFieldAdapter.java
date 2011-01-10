/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
import org.geotoolkit.geotnetcab.GNC_ApplicationFieldCode;
import org.opengis.metadata.identification.CharacterSet;


/**
 * JAXB adapter for {@link GNC_ApplicationFieldCode}, in order to integrate the value in an element
 * complying with ISO-19139 standard. See package documentation for more information about
 * the handling of {@code CodeList} in ISO-19139.
 *
 * @author Guilhem Legal (Geomatys)
 * @version 3.16
 *
 * @since 3.16
 * @module pending
 */
public final class ApplicationFieldAdapter extends CodeListAdapter<ApplicationFieldAdapter, GNC_ApplicationFieldCode> {
    /**
     * Ensures that the adapted code list class is loaded.
     */
    static {
        ensureClassLoaded(CharacterSet.class);
    }

    /**
     * Empty constructor for JAXB only.
     */
    public ApplicationFieldAdapter() {
    }

    /**
     * Creates a new adapter for the given proxy.
     */
    private ApplicationFieldAdapter(final CodeListProxy proxy) {
        super(proxy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ApplicationFieldAdapter wrap(final CodeListProxy proxy) {
        return new ApplicationFieldAdapter(proxy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<GNC_ApplicationFieldCode> getCodeListClass() {
        return GNC_ApplicationFieldCode.class;
    }

    /**
     * Invoked by JAXB on marshalling.
     *
     * @return The value to be marshalled.
     */
    @Override
    @XmlElement(name = "GNC_ApplicationFieldCode", namespace="http://www.mdweb-project.org/files/xsd")
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
