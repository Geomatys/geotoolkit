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
package org.geotoolkit.internal.jaxb.code;

import javax.xml.bind.annotation.XmlElement;
import org.opengis.metadata.identification.TopicCategory;


/**
 * JAXB adapter for {@link TopicCategory}, in order to integrate the value in an element
 * complying with ISO-19139 standard. See package documentation for more information
 * about the handling of {@code CodeList} in ISO-19139.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Guihem Legal (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 2.5
 * @module
 */
public final class MD_TopicCategoryCode extends CodeListAdapter<MD_TopicCategoryCode, TopicCategory> {
    /**
     * Ensures that the adapted code list class is loaded.
     */
    static {
        ensureClassLoaded(TopicCategory.class);
    }

    /**
     * Empty constructor for JAXB only.
     */
    public MD_TopicCategoryCode() {
    }

    /**
     * Creates a new adapter for the given proxy.
     */
    private MD_TopicCategoryCode(final CodeListProxy proxy) {
        super(proxy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected MD_TopicCategoryCode wrap(CodeListProxy proxy) {
        return new MD_TopicCategoryCode(proxy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<TopicCategory> getCodeListClass() {
        return TopicCategory.class;
    }

    /**
     * Returns {@code true} since this code list is actually an enum.
     *
     * @since 3.18
     */
    @Override
    protected boolean isEnum() {
        return true;
    }

    /**
     * Invoked by JAXB on marshalling.
     *
     * @return The value to be marshalled.
     */
    @Override
    @XmlElement(name = "MD_TopicCategoryCode")
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
