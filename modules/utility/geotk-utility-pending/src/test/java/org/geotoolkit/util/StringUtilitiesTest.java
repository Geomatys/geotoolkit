/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

import org.junit.Test;
import static org.junit.Assert.*;


/**
 *
 * @author Johann Sorel (Geomatys)
 * @author Cédric Briançon (Geomatys)
 */
public class StringUtilitiesTest {

    @Test
    public void occurenceTest(){
        String str;
        int nb;

        str = "gredgfdgdfhdkljgfdhvndkvfduhnfjfiodj";
        nb = StringUtilities.getOccurence(str, '-');
        assertEquals(0, nb);


        str = "-gredgfdg-dfhdkljgfdh-vndkvfduhnfjf-iodj-";
        nb = StringUtilities.getOccurence(str, '-');
        assertEquals(5, nb);

        str = "gredgfdgdfhdkljgfdhvndkvfduhnfjfiodj";
        nb = StringUtilities.getOccurence(str, "-");
        assertEquals(0, nb);


        str = "-gredgfdg-dfhdkljgfdh-vndkvfduhnfjf-iodj-";
        nb = StringUtilities.getOccurence(str, "-");
        assertEquals(5, nb);
    }

    @Test
    public void positionsTest(){
        String str;
        int[] nb;

        str = "gredgfdgdfhdkljgfdhvndkvfduhnfjfiodj";
        nb = StringUtilities.getIndexes(str, '-');
        assertEquals(0, nb.length);

        str = "-gredgfdg-dfhdkljgfdh-vndkvfduhnfjf-iodj-";
        nb = StringUtilities.getIndexes(str, '-');
        assertEquals(5, nb.length);
        assertEquals(0, nb[0]);
        assertEquals(9, nb[1]);
        assertEquals(21, nb[2]);
        assertEquals(35, nb[3]);
        assertEquals(40, nb[4]);

        str = "---";
        nb = StringUtilities.getIndexes(str, '-');
        assertEquals(3, nb.length);
        assertEquals(0, nb[0]);
        assertEquals(1, nb[1]);
        assertEquals(2, nb[2]);
    }

}