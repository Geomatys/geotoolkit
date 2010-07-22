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
public abstract class DefaultAbstractView extends DefaultAbstractObject implements AbstractView {

    protected DefaultAbstractView() {
    }

    /**
     * 
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param abstractViewSimpleExtensions
     * @param abstractViewObjectExtensions
     */
    protected DefaultAbstractView(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractViewSimpleExtensions,
            List<AbstractObject> abstractViewObjectExtensions) {
        super(objectSimpleExtensions, idAttributes);
        if (abstractViewSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.VIEW).addAll(abstractViewSimpleExtensions);
        }
        if (abstractViewObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.VIEW).addAll(abstractViewObjectExtensions);
        }
    }
}
