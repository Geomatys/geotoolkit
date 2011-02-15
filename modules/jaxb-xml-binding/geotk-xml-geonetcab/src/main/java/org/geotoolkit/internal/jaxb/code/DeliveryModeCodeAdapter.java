/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011, Geomatys
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
import org.geotoolkit.geotnetcab.GNC_DeliveryModeCode;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @version 3.17
 *
 * @since 3.17
 * @module pending
 */
public class DeliveryModeCodeAdapter extends CodeListAdapter<DeliveryModeCodeAdapter, GNC_DeliveryModeCode> {

    /**
     * Empty constructor for JAXB only.
     */
    public DeliveryModeCodeAdapter() {
    }

    /**
     * Creates a new adapter for the given proxy.
     */
    private DeliveryModeCodeAdapter(final CodeListProxy proxy) {
        super(proxy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DeliveryModeCodeAdapter wrap(final CodeListProxy proxy) {
        return new DeliveryModeCodeAdapter(proxy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<GNC_DeliveryModeCode> getCodeListClass() {
        return GNC_DeliveryModeCode.class;
    }

    /**
     * Invoked by JAXB on marshalling.
     *
     * @return The value to be marshalled.
     */
    @Override
    @XmlElement(name = "GNC_DeliveryModeCode", namespace="http://www.mdweb-project.org/files/xsd")
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
