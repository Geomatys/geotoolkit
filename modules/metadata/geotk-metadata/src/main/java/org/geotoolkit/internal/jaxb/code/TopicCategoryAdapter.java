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
import org.opengis.metadata.identification.TopicCategory;
import org.geotoolkit.internal.jaxb.metadata.MetadataAdapter;
import org.geotoolkit.internal.CodeLists;


/**
 * JAXB adapter for {@link TopicCategory}, in order to integrate the value in an element
 * complying with ISO-19139 standard. See package documentation for more information
 * about the handling of {@code CodeList} in ISO-19139.
 * <p>
 * This particular class extends {@link MetadataAdapter} rather than {@link CodeListAdapter}
 * because it is not formatted like the other code list. More specificially, the value shall
 * not be wrapped in a {@link CodeListProxy}.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.05
 *
 * @since 2.5
 * @module
 */
public final class TopicCategoryAdapter extends MetadataAdapter<TopicCategoryAdapter, TopicCategory> {
    /**
     * Ensures that the adapted code list class is loaded.
     */
    static {
        CodeListAdapter.ensureClassLoaded(TopicCategory.class);
    }

    /**
     * Empty constructor for JAXB only.
     */
    public TopicCategoryAdapter() {
    }

    /**
     * Creates a new adapter for the given proxy.
     */
    private TopicCategoryAdapter(final TopicCategory topicCategory) {
        super(topicCategory);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected TopicCategoryAdapter wrap(TopicCategory value) {
        return new TopicCategoryAdapter(value);
    }

    /**
     * Invoked by JAXB on marshalling.
     *
     * @return The value to be marshalled.
     */
    @Override
    @XmlElement(name = "MD_TopicCategoryCode")
    public String getElement() {
        return metadata.identifier();
    }

    /**
     * Invoked by JAXB on unmarshalling.
     *
     * @param metadata The unmarshalled value.
     */
    public void setElement(final String metadata) {
        this.metadata = CodeLists.valueOf(TopicCategory.class, metadata);
    }
}
