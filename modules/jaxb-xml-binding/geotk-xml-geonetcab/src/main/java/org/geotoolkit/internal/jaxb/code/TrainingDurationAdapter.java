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
import org.geotoolkit.geotnetcab.GNC_TrainingDurationCode;
import org.opengis.metadata.identification.CharacterSet;


/**
 * JAXB adapter for {@link GNC_TrainingDurationCode}, in order to integrate the value in an element
 * complying with ISO-19139 standard. See package documentation for more information about
 * the handling of {@code CodeList} in ISO-19139.
 *
 * @author Guilhem Legal (Geomatys)
 * @version 3.16
 *
 * @since 3.16
 * @module pending
 */
public final class TrainingDurationAdapter extends CodeListAdapter<TrainingDurationAdapter, GNC_TrainingDurationCode> {
    /**
     * Ensures that the adapted code list class is loaded.
     */
    static {
        ensureClassLoaded(CharacterSet.class);
    }

    /**
     * Empty constructor for JAXB only.
     */
    public TrainingDurationAdapter() {
    }

    /**
     * Creates a new adapter for the given proxy.
     */
    private TrainingDurationAdapter(final CodeListProxy proxy) {
        super(proxy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected TrainingDurationAdapter wrap(final CodeListProxy proxy) {
        return new TrainingDurationAdapter(proxy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<GNC_TrainingDurationCode> getCodeListClass() {
        return GNC_TrainingDurationCode.class;
    }

    /**
     * Invoked by JAXB on marshalling.
     *
     * @return The value to be marshalled.
     */
    @Override
    @XmlElement(name = "GNC_TrainingDurationCode", namespace="http://www.mdweb-project.org/files/xsd")
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
