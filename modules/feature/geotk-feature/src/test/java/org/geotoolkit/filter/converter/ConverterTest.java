/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotoolkit.filter.converter;

import java.util.Date;
import org.apache.sis.util.ObjectConverters;
import org.junit.Test;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ConverterTest extends org.geotoolkit.test.TestBase {

    public ConverterTest() {
    }

    @Test
    public void testDate(){
        final String date1 = "2006-10-01";

        Date d = ObjectConverters.convert(date1, Date.class);
        System.out.println(d);
    }
}
