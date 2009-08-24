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
package org.geotoolkit.internal.jaxb.metadata;

import javax.xml.bind.annotation.XmlElement;
import org.geotoolkit.metadata.iso.quality.AbstractElement;
import org.opengis.metadata.quality.Element;


/**
 * JAXB adapter mapping implementing class to the GeoAPI interface. See
 * package documentation for more information about JAXB and interface.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.02
 *
 * @since 3.02
 * @module
 */
public final class ElementAdapter extends MetadataAdapter<ElementAdapter,Element> {
    /**
     * Empty constructor for JAXB only.
     */
    public ElementAdapter() {
    }

    /**
     * Wraps an Element value with a {@code DQ_Element} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private ElementAdapter(final Element metadata) {
        super(metadata);
    }

    /**
     * Returns the Element value wrapped by a {@code DQ_Element} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected ElementAdapter wrap(final Element value) {
        return new ElementAdapter(value);
    }

    /**
     * Returns the {@link AbstractElement} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @XmlElement(name = "DQ_Element")
    public AbstractElement getElement() {
        final Element metadata = this.metadata;
        return (metadata instanceof AbstractElement) ?
            (AbstractElement) metadata : new AbstractElement(metadata);
    }

    /**
     * Sets the value for the {@link DefaultElement}. This method is systematically
     * called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setElement(final AbstractElement metadata) {
        this.metadata = metadata;
    }
}
