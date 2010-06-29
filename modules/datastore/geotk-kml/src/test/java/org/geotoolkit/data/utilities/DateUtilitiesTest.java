package org.geotoolkit.data.utilities;

import java.util.Calendar;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Samuel Andrés
 */
public class DateUtilitiesTest {

    public DateUtilitiesTest() {
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

    /**
     * Arbitrary date.
     */
    @Test
    public void firstTest(){
        String inputDate = "1876-08-02T22:31:54.543+01:00";
        DateUtilities du = new DateUtilities();
        Calendar calendar = du.getCalendar(inputDate);
        String outputDate = DateUtilities.getFormatedString(calendar, false);
        assertEquals("1876-08-02T22:31:54.543+01:00", outputDate);
    }

    /**
     * gYear format
     */
    @Test
    public void testSimplegYear(){
        String inputDate = "2003";
        DateUtilities du = new DateUtilities();
        Calendar calendar = du.getCalendar(inputDate);
        String outputDate = du.getFormatedString(false);
        assertEquals("2003+01:00", outputDate);
    }

    /**
     * gYear format to dateTime
     */
    @Test
    public void testSimplegYearToDateTime(){
        String inputDate = "2003";
        DateUtilities du = new DateUtilities();
        Calendar calendar = du.getCalendar(inputDate);
        String outputDate = du.getFormatedString(true);
        assertEquals("2003-01-01T00:00:00+01:00", outputDate);
    }

    /**
     * gYear format (negative)
     */
    @Test
    public void testSimpleNegativegYear(){
        String inputDate = "-2003";
        DateUtilities du = new DateUtilities();
        Calendar calendar = du.getCalendar(inputDate);
        String outputDate = du.getFormatedString(false);
        assertEquals("-2003+01:00", outputDate);
    }

    /**
     * gYear format (negative) to dateTime
     */
    @Test
    public void testSimpleNegativegYearToDateTime(){
        String inputDate = "-2003";
        DateUtilities du = new DateUtilities();
        Calendar calendar = du.getCalendar(inputDate);
        String outputDate = du.getFormatedString(true);
        assertEquals("-2003-01-01T00:00:00+01:00", outputDate);
    }

    /**
     * gYearMonth format
     */
    @Test
    public void testSimplegYearMonth(){
        String inputDate = "2003-06";
        DateUtilities du = new DateUtilities();
        Calendar calendar = du.getCalendar(inputDate);
        String outputDate = du.getFormatedString(false);
        assertEquals("2003-06+01:00", outputDate);
    }

    /**
     * gYearMoth format to dateTime
     */
    @Test
    public void testSimplegYearMonthToDateTime(){
        String inputDate = "2003-08";
        DateUtilities du = new DateUtilities();
        Calendar calendar = du.getCalendar(inputDate);
        String outputDate = du.getFormatedString(true);
        assertEquals("2003-08-01T00:00:00+01:00", outputDate);
    }

    /**
     * gYearMonth format (negative year)
     */
    @Test
    public void testSimpleNegativegYearMonth(){
        String inputDate = "-2003-12";
        DateUtilities du = new DateUtilities();
        Calendar calendar = du.getCalendar(inputDate);
        String outputDate = du.getFormatedString(false);
        assertEquals("-2003-12+01:00", outputDate);
    }

    /**
     * gYearMonth format (negative year) to dateTime
     */
    @Test
    public void testSimpleNegativegYearMonthToDateTime(){
        String inputDate = "-2003-05";
        DateUtilities du = new DateUtilities();
        Calendar calendar = du.getCalendar(inputDate);
        String outputDate = du.getFormatedString(true);
        assertEquals("-2003-05-01T00:00:00+01:00", outputDate);
    }

}