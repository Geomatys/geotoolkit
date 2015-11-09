/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.util;

import org.geotoolkit.util.NamesExt;
import javax.xml.namespace.QName;
import static org.junit.Assert.*;
import org.junit.Test;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class NamesExtTest {

    @Test
    public void createFromQName(){

        GenericName gn = NamesExt.create(new QName(null, "test"));
        assertEquals(null,NamesExt.getNamespace(gn));
        assertEquals("test",gn.tip().toString());
        
        gn = NamesExt.create(new QName("", "test"));
        assertEquals(null,NamesExt.getNamespace(gn));
        assertEquals("test",gn.tip().toString());

        gn = NamesExt.create(new QName("ns", "test"));
        assertEquals("ns",NamesExt.getNamespace(gn));
        assertEquals("test",gn.tip().toString());

    }

}
