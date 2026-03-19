/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.storage.rs.internal.shared;

import org.geotoolkit.referencing.rs.Code;
import org.geotoolkit.storage.rs.CodeTransform;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class SubTransform implements CodeTransform {

    @Override
    public Code toCode(int[] gridPosition) throws TransformException {
        final Object[] ordinates = new Object[getDimension()];
        toAddress(gridPosition, ordinates, 0);
        return new Code(getRS(), ordinates);
    }

    @Override
    public int[] toGrid(Code location) throws TransformException {
        final int[] gridPosition = new int[getDimension()];
        toGrid(location.getOrdinates(), gridPosition, 0);
        return gridPosition;
    }

    public abstract void toAddress(int[] gridPosition, Object[] location, int offset) throws TransformException;

    public abstract void toGrid(Object[] location, int[] gridPosition, int offset) throws TransformException;

}
