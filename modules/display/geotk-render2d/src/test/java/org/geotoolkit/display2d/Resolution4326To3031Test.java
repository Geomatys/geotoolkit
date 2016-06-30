/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.display2d;

import org.apache.sis.referencing.CRS;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.util.FactoryException;

/**
 * Test resolution class with {@code MathTransform} which represent transformation
 * from source {@code CoordinateReferenceSystem} with epsg code 4326 to target
 * {@code CoordinateReferenceSystem} with epsg code 3031.
 *
 * @author Remi Marechal (Geomatys).
 */
public class Resolution4326To3031Test extends ResolutionCRSTest {

    public Resolution4326To3031Test() throws NoninvertibleTransformException, NoSuchAuthorityCodeException, FactoryException  {
        super(CRS.forCode("EPSG:4326"), CRS.forCode("EPSG:3031"), new double[]{4300, 253}, 1.5);
    }
}
