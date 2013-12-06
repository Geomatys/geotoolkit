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

package org.geotoolkit.report;

import net.sf.jasperreports.engine.JRField;
import org.opengis.feature.Feature;
import org.opengis.feature.type.PropertyDescriptor;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface JRFieldRenderer {

    /**
     * Return true if this renderer can handle this field.
     */
    boolean canHandle(JRField field);

    /**
     *
     * @param field
     * @return
     * @throws IllegalArgumentException
     *     if the field type match this renderer but some additional
     *     field parameters are missing
     */
    PropertyDescriptor createDescriptor(JRField field) throws IllegalArgumentException;

    /**
     * Prepare the field value to be rendered.
     */
    Object createValue(JRField field, Feature feature);

}
