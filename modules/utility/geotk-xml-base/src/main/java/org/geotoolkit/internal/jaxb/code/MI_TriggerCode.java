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
import org.opengis.metadata.acquisition.Trigger;
import org.geotoolkit.xml.Namespaces;


/**
 * JAXB adapter for {@link Trigger}, in order to integrate the value in an element respecting
 * the ISO-19139 standard. See package documentation for more information about the handling
 * of {@code CodeList} in ISO-19139.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.07
 *
 * @since 3.02
 * @module
 */
public final class MI_TriggerCode extends CodeListAdapter<MI_TriggerCode, Trigger> {
    /**
     * Ensures that the adapted code list class is loaded.
     */
    static {
        ensureClassLoaded(Trigger.class);
    }

    /**
     * Empty constructor for JAXB only.
     */
    public MI_TriggerCode() {
    }

    /**
     * Creates a new adapter for the given proxy.
     */
    private MI_TriggerCode(final CodeListProxy proxy) {
        super(proxy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected MI_TriggerCode wrap(CodeListProxy proxy) {
        return new MI_TriggerCode(proxy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<Trigger> getCodeListClass() {
        return Trigger.class;
    }

    /**
     * Invoked by JAXB on marshaling.
     *
     * @return The value to be marshalled.
     */
    @Override
    @XmlElement(name = "MI_TriggerCode", namespace = Namespaces.GMI)
    public CodeListProxy getElement() {
        return proxy;
    }

    /**
     * Invoked by JAXB on unmarshaling.
     *
     * @param proxy The unmarshalled value.
     */
    public void setElement(final CodeListProxy proxy) {
        this.proxy = proxy;
    }
}
