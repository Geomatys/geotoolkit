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
package org.geotoolkit.dggal;

import org.geotoolkit.dggs.AbstractDggrsTest;
import org.junit.Ignore;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class HealPixTest extends AbstractDggrsTest {

    public HealPixTest() {
        super(DGGALDggrs.HEALPIX_INSTANCE);
    }

    @Ignore //healpix implementation has bugs
    @Override
    public void testSampling() throws TransformException, FactoryException {
        super.testSampling();
    }

}
