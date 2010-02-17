/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotoolkit.temporal.object;

import java.lang.annotation.Annotation;
import org.geotoolkit.internal.StringUtilities;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 *
 * @author sorel
 */
public class UtilsTest implements Test{

    @Override
    public Class<? extends Throwable> expected() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long timeout() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Test
    public void UtilTest(){

        

    }

    @Test
    public void OccurenceTest(){
        String str;
        int nb;

        str = "gredgfdgdfhdkljgfdhvndkvfduhnfjfiodj";
        nb = Utils.getOccurence(str, '-');
        assertEquals(0, nb);


        str = "-gredgfdg-dfhdkljgfdh-vndkvfduhnfjf-iodj-";
        nb = Utils.getOccurence(str, '-');
        assertEquals(5, nb);

        str = "gredgfdgdfhdkljgfdhvndkvfduhnfjfiodj";
        nb = Utils.getOccurence(str, "-");
        assertEquals(0, nb);


        str = "-gredgfdg-dfhdkljgfdh-vndkvfduhnfjf-iodj-";
        nb = Utils.getOccurence(str, "-");
        assertEquals(5, nb);


    }

}
