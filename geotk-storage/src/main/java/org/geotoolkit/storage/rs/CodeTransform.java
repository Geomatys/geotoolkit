/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.storage.rs;

import org.geotoolkit.referencing.rs.Code;
import org.opengis.referencing.ReferenceSystem;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public interface CodeTransform {

    ReferenceSystem getRS();

    int getDimension();

    Code toCode(int[] gridPosition) throws TransformException;

    int[] toGrid(Code location) throws TransformException;

    CodeTransform split(int offset, int size);
}
