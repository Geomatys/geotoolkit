/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.apache.sis.referencing.operation.transform;

import java.util.List;
import org.opengis.referencing.operation.MathTransform;


/**
 * @deprecated Temporary bridge class to be removed after Geotk code have been ported to Apache SIS.
 */
@Deprecated
public abstract class AbstractMathTransform2D_tmp extends AbstractMathTransform2D {
    @Override
    protected int beforeFormat(final List<Object> transforms, int index, final boolean inverse) {
        return super.beforeFormat(transforms, index, inverse);
    }
}
