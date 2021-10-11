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
import org.apache.sis.referencing.CommonCRS;

/**
 * Test resolution class with {@code MathTransform} which represent transformation
 * from source {@code CoordinateReferenceSystem} with epsg code 2154 to target
 * {@code CoordinateReferenceSystem} with epsg code 4326.
 *
 * @author Remi Marechal (Geomatys).
 */
public class Resolution2154To4326Test extends ResolutionCRSTest {
    public Resolution2154To4326Test() throws NoninvertibleTransformException, NoSuchAuthorityCodeException, FactoryException {
        super(CRS.forCode("EPSG:2154"), CommonCRS.WGS84.geographic(), new double[] {4300, 253}, 3);
    }
}
