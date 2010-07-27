/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleTypeContainer;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultBasicLink implements BasicLink {

    private final Extensions extensions = new Extensions();
    private IdAttributes idAttributes;
    private String href;

    /**
     * 
     */
    public DefaultBasicLink() {
    }

    /**
     * 
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param href
     * @param basicLinkSimpleExtensions
     * @param basicLinkObjectExtensions
     */
    public DefaultBasicLink(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes, String href,
            List<SimpleTypeContainer> basicLinkSimpleExtensions,
            List<Object> basicLinkObjectExtensions) {
        this.idAttributes = idAttributes;
        this.href = href;
        if (objectSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.OBJECT).addAll(objectSimpleExtensions);
        }
        if (basicLinkSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.BASIC_LINK).addAll(basicLinkSimpleExtensions);
        }
        if (basicLinkObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.BASIC_LINK).addAll(basicLinkObjectExtensions);
        }
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public IdAttributes getIdAttributes() {
        return this.idAttributes;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getHref() {
        return this.href;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setIdAttributes(IdAttributes idAttributes) {
        this.idAttributes = idAttributes;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setHref(String href) {
        this.href = href;
    }

    @Override
    public String toString() {
        String resultat = "BasicLinkDefault : "
                + "\n\thref : " + this.href;
        return resultat;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Extensions extensions() {
        return this.extensions;
    }
}
