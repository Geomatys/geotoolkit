/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.index.tree.manager.postgres;

import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Guilhem Legal (geomatys)
 */
public class PGDatasourceTest {
    
    @Test
    public void insertTest()  {
        String databaseURL = "postgres://cstl:admin@localhost:5432/cstl-test";
        
        Map<String, String> results = PGDataSource.extractDbInfo(databaseURL);
        
        Assert.assertEquals("cstl",      results.get("username"));
        Assert.assertEquals("admin",     results.get("password"));
        Assert.assertEquals("localhost", results.get("host"));
        Assert.assertEquals("5432",      results.get("port"));
        Assert.assertEquals("cstl-test", results.get("database"));
    }
}
