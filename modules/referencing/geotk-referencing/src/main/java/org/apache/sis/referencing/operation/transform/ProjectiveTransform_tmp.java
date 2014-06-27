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

import org.opengis.referencing.operation.Matrix;
import org.apache.sis.util.ComparisonMode;


/**
 * @deprecated Temporary bridge class to be removed after Geotk code have been ported to Apache SIS.
 */
@Deprecated
public class ProjectiveTransform_tmp extends ProjectiveTransform {
    protected ProjectiveTransform_tmp(final Matrix matrix) {
        super(matrix);
    }

    @Override
    protected ProjectiveTransform createInverse(final Matrix matrix) {
        return super.createInverse(matrix);
    }

    protected static boolean equals2(final LinearTransform t1, final Object t2, final ComparisonMode mode) {
        return equals(t1, t2, mode);
    }
}
