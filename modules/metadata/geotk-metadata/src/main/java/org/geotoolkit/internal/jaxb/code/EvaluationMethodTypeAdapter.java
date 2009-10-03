/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
import org.opengis.metadata.quality.EvaluationMethodType;


/**
 * JAXB adapter for {@link EvaluationMethodType}, in order to integrate the value in an element
 * complying with ISO-19139 standard. See package documentation for more information about the
 * handling of {@code CodeList} in ISO-19139.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.05
 *
 * @since 3.04
 * @module
 */
public final class EvaluationMethodTypeAdapter extends CodeListAdapter<EvaluationMethodTypeAdapter, EvaluationMethodType> {
    /**
     * Ensures that the adapted code list class is loaded.
     */
    static {
        ensureClassLoaded(EvaluationMethodType.class);
    }

    /**
     * Empty constructor for JAXB only.
     */
    public EvaluationMethodTypeAdapter() {
    }

    /**
     * Creates a new adapter for the given proxy.
     */
    private EvaluationMethodTypeAdapter(final CodeListProxy proxy) {
        super(proxy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected EvaluationMethodTypeAdapter wrap(CodeListProxy proxy) {
        return new EvaluationMethodTypeAdapter(proxy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<EvaluationMethodType> getCodeListClass() {
        return EvaluationMethodType.class;
    }

    /**
     * Invoked by JAXB on marshalling.
     *
     * @return The value to be marshalled.
     */
    @Override
    @XmlElement(name = "DQ_EvaluationMethodTypeCode")
    public CodeListProxy getElement() {
        return this.proxy;
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
