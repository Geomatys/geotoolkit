/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2021, Geomatys
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
package org.geotoolkit.observation.model;

import org.opengis.observation.Phenomenon;

/**
 * A model object representing a field in an Observation result along with the
 * (simple) phenomenon associated.
 *
 * @author Guilhem Legal (Geomatys)
 */
public class FieldPhenomenon {

    private final Phenomenon phenomenon;
    private final Field field;

    public FieldPhenomenon(Phenomenon phenomenon, Field field) {
        this.field = field;
        this.phenomenon = phenomenon;
    }

    public Phenomenon getPhenomenon() {
        return phenomenon;
    }

    public Field getField() {
        return field;
    }
}
