/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotoolkit.style;

import org.opengis.filter.expression.Literal;
import java.awt.Color;
import java.util.Date;
import org.geotoolkit.util.Converters;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sorel
 */
public class ColorTest {

    public ColorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testColor(){

        Color c = new Color(120,112,52,81);
        MutableStyleFactory SF = new DefaultStyleFactory();
        final Literal l = SF.literal(c);
        
        assertTrue(l.getValue() instanceof String);
        assertTrue(l.getValue().toString().startsWith("#"));
        
        Color res = l.evaluate(null, Color.class);
        assertEquals(c, res);


    }


}
