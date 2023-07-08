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
package org.geotoolkit.maven.report;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AsciiDocTestReportTest {

    @Test
    public void test1() {
        //ok
    }

    /**
     * TODO find a way to check test failure reports
     * for now remove the @ignore and uncomment the related block in test4
     */
    @Test
    @Ignore
    public void test2() {
        throw new IllegalArgumentException("fail");
    }

    @Test
    @Ignore
    public void test3() {
        //ok
    }

    /**
     * Verify the content of each report
     * @throws IOException
     */
    @Test
    @Ignore("Not compatible with JUnit 5 vintage")
    public void test4() throws IOException {
        //ok
        Path folder = Paths.get("target").resolve("test-reports");
        Path test1 = folder.resolve("org.geotoolkit.maven.report.AsciiDocTestReportTest-test1-adoc.txt");
        Path test2 = folder.resolve("org.geotoolkit.maven.report.AsciiDocTestReportTest-test2-adoc.txt");
        Path test3 = folder.resolve("org.geotoolkit.maven.report.AsciiDocTestReportTest-test3-adoc.txt");
        Assert.assertTrue(Files.exists(test1));
        Assert.assertTrue(Files.exists(test2));
        Assert.assertTrue(Files.exists(test3));

        List<String> text1 = Files.readAllLines(test1);
        List<String> text2 = Files.readAllLines(test2);
        List<String> text3 = Files.readAllLines(test3);

        //ignore time line which is variable
        Assert.assertEquals("// tag::status[]",         text1.get(0));
        Assert.assertEquals("SUCCESS",                  text1.get(1));
        Assert.assertEquals("// end::status[]",         text1.get(2));
        Assert.assertEquals("// tag::status_fr[]",      text1.get(3));
        Assert.assertEquals("SUCCÈS",                   text1.get(4));
        Assert.assertEquals("// end::status_fr[]",      text1.get(5));
        Assert.assertEquals("// tag::time_pretty[]",    text1.get(6));
        Assert.assertEquals("// end::time_pretty[]",    text1.get(8));
        Assert.assertEquals("// tag::timems[]",         text1.get(9));
        Assert.assertEquals("// end::timems[]",         text1.get(11));

//        Assert.assertEquals("// tag::status[]",         text2.get(0));
//        Assert.assertEquals("FAILED ",                  text2.get(1));
//        Assert.assertEquals("// end::status[]",         text2.get(2));
//        Assert.assertEquals("// tag::status_fr[]",      text2.get(3));
//        Assert.assertEquals("ÉCHOUÉ",                   text2.get(4));
//        Assert.assertEquals("// end::status_fr[]",      text2.get(5));
//        Assert.assertEquals("// tag::time_pretty[]",    text2.get(6));
//        Assert.assertEquals("// end::time_pretty[]",    text2.get(8));
//        Assert.assertEquals("// tag::timems[]",         text2.get(9));
//        Assert.assertEquals("// end::timems[]",         text2.get(11));

        Assert.assertEquals("// tag::status[]",         text3.get(0));
        Assert.assertEquals("IGNORED",                  text3.get(1));
        Assert.assertEquals("// end::status[]",         text3.get(2));
        Assert.assertEquals("// tag::status_fr[]",      text3.get(3));
        Assert.assertEquals("IGNORÉ",                   text3.get(4));
        Assert.assertEquals("// end::status_fr[]",      text3.get(5));
        Assert.assertEquals("// tag::time_pretty[]",    text3.get(6));
        Assert.assertEquals("// end::time_pretty[]",    text3.get(8));
        Assert.assertEquals("// tag::timems[]",         text3.get(9));
        Assert.assertEquals("// end::timems[]",         text3.get(11));


    }
}
