/*
 * Geotoolkit.org - An Open Source Java GIS Toolkit
 * http://www.geotoolkit.org
 *
 * (C) 2018, Geomatys.
 *
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package org.geotoolkit.util.wmm;

import java.io.IOException;
import java.net.URISyntaxException;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Hasdenteufel Eric (Geomatys)
 */
public final class MagneticModelTest {

     @Test
     public void testRead() throws IOException, URISyntaxException {
         MagneticModel magneticModel = WorldMagneticModel.readMagModel();
         Assert.assertNotNull(magneticModel);
         Assert.assertEquals(12,magneticModel.nMax);
         Assert.assertEquals("WMM-2015",magneticModel.ModelName);
     }
}
