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
 * @module pending
 */
public abstract class DefaultAbstractStyleSelector extends DefaultAbstractObject implements AbstractStyleSelector {

    /**
     * 
     */
    protected DefaultAbstractStyleSelector() {
    }

    /**
     * 
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param abstractStyleSelectorSimpleExtensions
     * @param abstractStyleSelectorObjectExtensions
     */
    protected DefaultAbstractStyleSelector(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleTypeContainer> abstractStyleSelectorSimpleExtensions,
            List<Object> abstractStyleSelectorObjectExtensions) {
        super(objectSimpleExtensions, idAttributes);
        if (abstractStyleSelectorSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.STYLE_SELECTOR).addAll(abstractStyleSelectorSimpleExtensions);
        }
        if (abstractStyleSelectorObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.STYLE_SELECTOR).addAll(abstractStyleSelectorObjectExtensions);
        }
    }

    @Override
    public String toString() {
        String resultat = super.toString()
                + "\n\tAbstractStyleSelectorDefault : ";
        return resultat;
    }
}
