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
public abstract class DefaultAbstractObject implements AbstractObject {

    private final Extensions extensions = new Extensions();
    protected IdAttributes idAttributes;

    /**
     * 
     */
    protected DefaultAbstractObject() {
    }

    /**
     * 
     * @param objectSimpleExtensions
     * @param idAttributes
     */
    protected DefaultAbstractObject(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes) {
        if (objectSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.OBJECT).addAll(objectSimpleExtensions);
        }
        this.idAttributes = idAttributes;
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
    public void setIdAttributes(IdAttributes idAttributes) {
        this.idAttributes = idAttributes;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Extensions extensions() {
        return extensions;
    }

    @Override
    public String toString() {
        String resultat = "Abstract Object : ";
        return resultat;
    }
}
