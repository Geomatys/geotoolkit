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
import org.geotoolkit.data.kml.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public abstract class DefaultAbstractSubStyle extends DefaultAbstractObject implements AbstractSubStyle {

    /**
     * 
     */
    protected DefaultAbstractSubStyle() {
    }

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param abstractSubStyleSimpleExtensions
     * @param abstractSubStyleObjectExtensions
     */
    protected DefaultAbstractSubStyle(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractSubStyleSimpleExtensions,
            List<AbstractObject> abstractSubStyleObjectExtensions) {
        super(objectSimpleExtensions, idAttributes);
        if (abstractSubStyleSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.SUB_STYLE).addAll(abstractSubStyleSimpleExtensions);
        }
        if (abstractSubStyleObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.SUB_STYLE).addAll(abstractSubStyleObjectExtensions);
        }
    }

    @Override
    public String toString() {
        String resultat = super.toString()
                + "\n\tAbstractSubStyleDefault : ";
        return resultat;
    }
}
