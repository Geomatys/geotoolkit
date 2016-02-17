/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.internal.sql;

import java.sql.Types;
import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests {@link TypeMapper}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.09
 *
 * @since 3.09
 */
public final strictfp class TypeMapperTest extends org.geotoolkit.test.TestBase {
    /**
     * Tests {@link TypeMapper#toJavaType(int)}.
     */
    @Test
    public void testToJavaType() {
        assertEquals(Integer.class, TypeMapper.toJavaType(Types.INTEGER));
        assertEquals(Boolean.class, TypeMapper.toJavaType(Types.BOOLEAN));
        assertNull  (               TypeMapper.toJavaType(Types.LONGVARCHAR));
    }
}
