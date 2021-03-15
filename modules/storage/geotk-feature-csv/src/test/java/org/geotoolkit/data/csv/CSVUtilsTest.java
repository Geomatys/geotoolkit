/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2021, Geomatys
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
package org.geotoolkit.data.csv;

import java.util.List;
import java.util.Scanner;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class CSVUtilsTest {

    @Test
    public void toStringListTest() throws Exception{
        String line = "\"30140\";25049001;\"055-P-001 - Men er Roue\";\"47.534765\";\"-3.093748\";\"REPHY\";7;\"Support : Masse d'eau. eau brute - Niveau : Surface (0-1m)\";\"Biologie/Phytoplancton\";\"FLORTOT\";\"Flore Totale - abondance de cellules\";\"1\";248;18/12/07;400;\"l-1\";\"0\"";
        Scanner scanner = new Scanner(line);
        List<String> results = CSVUtils.toStringList(scanner, line, ';');
        assertEquals(17, results.size());
        assertEquals("30140", results.get(0));
        assertEquals("0", results.get(16));

    }




}
