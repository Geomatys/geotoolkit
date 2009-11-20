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
package org.geotoolkit.resources.jaxb.feature.catalog.code;

import org.geotoolkit.internal.jaxb.code.*;
import javax.xml.bind.annotation.XmlElement;
import org.opengis.feature.catalog.RoleType;

/**
 * JAXB adapter for {@link RoleType}, in order to integrate the value in a tags
 * respecting the ISO-19139 standard. See package documentation to have more information
 * about the handling of CodeList in ISO-19139.
 *
 * @module pending
 * @since 2.5
 * @author Guilhem Legal
 */
public final class RoleTypeAdapter extends CodeListAdapter<RoleTypeAdapter, RoleType> {
    /**
     * Empty constructor for JAXB only.
     */
    private RoleTypeAdapter() {
    }

    public RoleTypeAdapter(final CodeListProxy proxy) {
        super(proxy);
    }

    @Override
    protected RoleTypeAdapter wrap(CodeListProxy proxy) {
        return new RoleTypeAdapter(proxy);
    }

    @Override
    protected Class<RoleType> getCodeListClass() {
        return RoleType.class;
    }

    @Override
    @XmlElement(name = "FC_RoleType", namespace = "http://www.isotc211.org/2005/gfc")
    public CodeListProxy getElement() {
        return proxy;
    }

    public void setElement(final CodeListProxy proxy) {
        this.proxy = proxy;
    }
}
