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

import java.net.URI;
import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleTypeContainer;

/**
 *
 * @author Samuel Andrés
 * @module pending
 */
public class DefaultAlias extends DefaultAbstractObject implements Alias {

    private URI targetHref;
    private URI sourceHref;

    /**
     *
     */
    public DefaultAlias() {
    }

    /**
     * 
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param targetHref
     * @param sourceHref
     * @param abstractAliasSimpleExtensions
     * @param abstractAliasObjectExtensions
     */
    public DefaultAlias(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes,
            URI targetHref, URI sourceHref,
            List<SimpleTypeContainer> abstractAliasSimpleExtensions,
            List<Object> abstractAliasObjectExtensions) {
        super(objectSimpleExtensions, idAttributes);
        this.targetHref = targetHref;
        this.sourceHref = sourceHref;
        if (abstractAliasSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.ALIAS).addAll(abstractAliasSimpleExtensions);
        }
        if (abstractAliasObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.ALIAS).addAll(abstractAliasObjectExtensions);
        }
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public URI getTargetHref() {
        return this.targetHref;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public URI getSourceHref() {
        return this.sourceHref;
    }

    /**
     * 
     * @{@inheritDoc }
     */
    @Override
    public void setTargetHref(URI targetHref) {
        this.targetHref = targetHref;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setSourceHref(URI sourceHref) {
        this.sourceHref = sourceHref;
    }
}
