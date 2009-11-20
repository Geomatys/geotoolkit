/*
 *    GeotoolKit - An Open Source Java GIS Toolkit
 *    http://geotoolkit.org
 *    (C) 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.resources.jaxb.service.code;

import org.geotoolkit.internal.jaxb.code.*;
import javax.xml.bind.annotation.XmlElement;
import org.opengis.service.CouplingType;


/**
 * JAXB adapter for {@link CouplingType}, in order to integrate the value in a tags
 * respecting the ISO-19139 standard. See package documentation to have more information
 * about the handling of CodeList in ISO-19139.
 *
 * @module pending
 * @since 2.5
 * @author Guilhem Legal
 */
public final class CouplingTypeAdapter extends CodeListAdapter<CouplingTypeAdapter, CouplingType> {
    /**
     * Empty constructor for JAXB only.
     */
    private CouplingTypeAdapter() {
    }

    public CouplingTypeAdapter(final CodeListProxy proxy) {
        super(proxy);
    }

    @Override
    protected CouplingTypeAdapter wrap(CodeListProxy proxy) {
        return new CouplingTypeAdapter(proxy);
    }

    @Override
    protected Class<CouplingType> getCodeListClass() {
        return CouplingType.class;
    }

    @Override
    @XmlElement(name = "SV_CouplingType", namespace = "http://www.isotc211.org/2005/srv")
    public CodeListProxy getElement() {
        return proxy;
    }

    public void setElement(final CodeListProxy proxy) {
        this.proxy = proxy;
    }
}
