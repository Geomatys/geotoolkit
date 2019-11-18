/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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
package org.geotoolkit.image.io;

import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.internal.system.DefaultFactories;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.crs.AbstractCRS;
import org.apache.sis.referencing.cs.AxesConvention;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.Utilities;
import org.geotoolkit.coverage.io.CoverageIO;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.ImageCoverageReader;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.image.io.plugin.TiffImageReader;
import org.geotoolkit.lang.Setup;
import org.geotoolkit.test.TestData;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.opengis.referencing.crs.CRSFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.util.FactoryException;

/**
 * @author Johann Sorel (Geomatys)
 * @author Alexis Manin (Geomatys)
 * Testing {@link org.geotoolkit.image.io.plugin.TiffImageReader} and {@link org.geotoolkit.image.io.plugin.TiffImageWriter} for
 * spatial images.
 *
 * TODO : Debug ignored tests.
 */
public class GeoTiffRWTest extends org.geotoolkit.test.TestBase {    // LGPL

    private final File tempDir;

    public GeoTiffRWTest() throws IOException {
        tempDir = Files.createTempDirectory("GTiffRWTest").toFile();
    }

    @BeforeClass
    public static void init() {
        Setup.initialize(null);
    }

    @After
    public void deleteTempFiles() throws IOException {
        Files.walkFileTree(tempDir.toPath(), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return super.postVisitDirectory(dir, exc);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return super.visitFile(file, attrs);
            }
        });
    }

    private final CRSFactory longlatFactory = DefaultFactories.forBuildin(CRSFactory.class);

    @Test
    public void test1() throws Exception {

        test("002025_0100_010722_l7_01_utm2.tiff", AbstractCRS.castOrCopy(CRS.forCode("EPSG:26921")).forConvention(AxesConvention.RIGHT_HANDED),
                new AffineTransform(1968.5, 0, 0, -1973.271028037383076, 688054.25, 5683177.364485980942845));
    }

    @Test
    @Ignore
    public void test2() throws Exception {
        //TODO : A parameter name doesn't match here : latitude of center.
        final CoordinateReferenceSystem sourceCRS = CRS.fromWKT(
         "    PROJCS[\"Albers_Conic_Equal_Area\","
        +"    GEOGCS[\"NAD27\","
        +"        DATUM[\"North_American_Datum_1927\","
        +"            SPHEROID[\"Clarke 1866\",6378206.4,294.9786982139006,"
        +"                AUTHORITY[\"EPSG\",\"7008\"]],"
        +"            AUTHORITY[\"EPSG\",\"6267\"]],"
        +"        PRIMEM[\"Greenwich\",0],"
        +"        UNIT[\"degree\",0.0174532925199433],"
        +"        AUTHORITY[\"EPSG\",\"4267\"]],"
        +"    PROJECTION[\"Albers_Conic_Equal_Area\"],"
        +"    PARAMETER[\"standard_parallel_1\",33.90363402777778],"
        +"    PARAMETER[\"standard_parallel_2\",33.62529002777777],"
        +"    PARAMETER[\"latitude_of_center\",33.76446202777777],"
        +"    PARAMETER[\"longitude_of_center\",-117.4745428888889],"
        +"    PARAMETER[\"false_easting\",0],"
        +"    PARAMETER[\"false_northing\",0],"
        +"    UNIT[\"metre\",1,"
        +"        AUTHORITY[\"EPSG\",\"9001\"]]]");
        //Origin = (-15312.865311534629654,15350.009177431464195)
        //Pixel Size = (257.5,-257.5)
        test("ace.tiff", sourceCRS,
                new AffineTransform(257.5, 0, 0, -257.5, -15312.865311534629654, 15350.009177431464195));

        //Origin = (1871084.500973120098934,693307.084818160044961)
        //Pixel Size = (257.735425600161136,-257.916629199947522)
        test("albers2.tiff",  sourceCRS,
                new AffineTransform(257.735425600161136, 0, 0, -257.916629199947522, 1871084.500973120098934, 693307.084818160044961));

    }

    @Test
    public void test3() throws Exception {
        //Origin = (440818,99902)
        //Pixel Size = (256,-256)
        test("bogot.tiff", AbstractCRS.castOrCopy(CRS.forCode("EPSG:21892")).forConvention(AxesConvention.RIGHT_HANDED),
                new AffineTransform(256, 0, 0, -256, 440818, 99902));
    }

    @Test
    public void test4() throws Exception {
        //Origin = (577252.740264483261853,4659702.512972613796592)
        //Pixel Size = (1537.233673966386050,-1527.550597774195239)
        test("c41078a.tiff", AbstractCRS.castOrCopy(CRS.forCode("EPSG:32617")).forConvention(AxesConvention.RIGHT_HANDED),
                new AffineTransform(1537.233673966386050, 0, 0, -1527.550597774195239, 577252.740264483261853, 4659702.512972613796592));
    }

    @Test
    public void test5() throws Exception {
        //Origin = (-113.116327999999996,47.564808800000002)
        //Pixel Size = (0.0278,-0.0278)
        test("cir.tif", AbstractCRS.castOrCopy(CRS.forCode("EPSG:4269")).forConvention(AxesConvention.RIGHT_HANDED),
                new AffineTransform(0.0278, 0, 0, -0.0278, -113.116327999999996, 47.564808800000002));
    }

    @Test
    public void test6() throws Exception {
        //Origin = (79074.166666666671517,1439192.637681159656495)
        //Pixel Size = (190.333333333333343,-190.724637681159408)
        test("erdas_spnad8.tiff", AbstractCRS.castOrCopy(CRS.forCode("EPSG:26966")).forConvention(AxesConvention.RIGHT_HANDED),
                new AffineTransform(190.333333333333343, 0, 0, -190.724637681159408, 79074.166666666671517, 1439192.637681159656495));
    }

    @Test
    public void test7() throws Exception {
        //Origin = (664769.191709000035189,4600950.488848333247006)
        //Pixel Size = (839.977999999999838,-846.395733333329304)
        test("f41078a.tiff", AbstractCRS.castOrCopy(CRS.forCode("EPSG:32617")).forConvention(AxesConvention.RIGHT_HANDED),
                new AffineTransform(839.977999999999838, 0, 0, -846.395733333329304, 664769.191709000035189, 4600950.488848333247006));
    }

    @Test
    @Ignore
    public void test8() throws Exception {
        //TODO BUG in JSR-275 units
        //Origin = (2250175,1377040)
        //Pixel Size = (5,-5)
        test("gaarc_subset.tiff", CRS.forCode("EPSG:26967"),
                new AffineTransform(5, 0, 0, -5, 2250175, 1377040));
        }

    @Test
    public void test9() throws Exception {
        //Origin = (613872.879663333296776,227462.954336666676681)
        //Pixel Size = (84.618316666649960,-84.618316666698476)
        test("gauss_.tiff", AbstractCRS.castOrCopy(CRS.forCode("EPSG:28405")).forConvention(AxesConvention.RIGHT_HANDED),
                new AffineTransform(84.618316666649960, 0, 0, -84.618316666698476, 613872.879663333296776, 227462.954336666676681));
    }

    @Test
    public void test10() throws Exception {
        //Origin = (-2.235599743981481,2.923495299537037)
        //Pixel Size = (0.000787391203704,-0.000787391203704)
        test("geo.tiff", AbstractCRS.castOrCopy(CRS.forCode("EPSG:4301")).forConvention(AxesConvention.RIGHT_HANDED),
                new AffineTransform(0.000787391203704, 0, 0, -0.000787391203704, -2.235599743981481, 2.923495299537037));
    }

    @Test
    @Ignore
    public void test11() throws Exception {
        //TODO bug in axis order
        //Origin = (1871084.537213840056211,693307.084818160044961)
        //Pixel Size = (257.916629199947522,-257.916629199947522)
        test("lamb_con.tiff", longlatFactory.createFromWKT(
                 "PROJCS[\"Lambert_Conformal_Conic_2SP\","
                +"GEOGCS[\"North_American_Datum_1927\","
                +"    DATUM[\"North_American_Datum_1927\","
                +"        SPHEROID[\"Clarke 1866\",6378206.4,294.9786982139006,"
                +"            AUTHORITY[\"EPSG\",\"7008\"]],"
                +"        AUTHORITY[\"EPSG\",\"6267\"]],"
                +"    PRIMEM[\"Greenwich\",0],"
                +"    UNIT[\"unknown\",0.0174532925199433]],"
                +"PROJECTION[\"Lambert_Conformal_Conic_2SP\"],"
                +"PARAMETER[\"standard_parallel_1\",40],"
                +"PARAMETER[\"standard_parallel_2\",50],"
                +"PARAMETER[\"latitude_of_origin\",45],"
                +"PARAMETER[\"central_meridian\",-90],"
                +"PARAMETER[\"false_easting\",0.001],"
                +"PARAMETER[\"false_northing\",0.002],"
                +"UNIT[\"unknown\",1]]"),
                new AffineTransform(257.916629199947522, 0, 0, -257.916629199947522, 1871084.537213840056211, 693307.084818160044961));
    }

    @Test
    public void test12() throws Exception {

        //Origin = (-117.640105492592596,33.902752573232327)
        //Pixel Size = (0.002777125925926,-0.002301575757576)
        test("latlon.tiff", AbstractCRS.castOrCopy(CRS.forCode("EPSG:4267")).forConvention(AxesConvention.RIGHT_HANDED),
                new AffineTransform(0.002777125925926, 0, 0, -0.002301575757576, -117.640105492592596, 33.902752573232327));
    }

    @Test
    @Ignore("There is a tiny difference between the expected and actual CRS (the last 3 digits in a matrix coefficient)."
            + " The cause of this tiny difference has not yet been identified, but may be caused by the accuracy difference"
            + " between SIS and Geotk in the calculation of flattening factor or excentricity. We are waiting for the complete"
            + " port of referencing module to SIS before to verify if the difference is still present.")
    public void test13() throws Exception {

//        //Origin = (-15312.865311483006735,15349.948768731206656)
//        //Pixel Size = (257.5,-257.5)
        test("lcc-2.tiff", longlatFactory.createFromWKT(
                "PROJCS[\"Lambert_Conformal_Conic_2SP\","
                +"GEOGCS[\"NAD27\","
                +"    DATUM[\"North_American_Datum_1927\","
                +"        SPHEROID[\"Clarke 1866\",6378206.4,294.9786982138982,"
                +"            AUTHORITY[\"EPSG\",\"7008\"]],"
                +"        AUTHORITY[\"EPSG\",\"6267\"]],"
                +"    PRIMEM[\"Greenwich\",0],"
                +"    UNIT[\"degree\",0.0174532925199433],"
                +"    AUTHORITY[\"EPSG\",\"4267\"]],"
                +"PROJECTION[\"Lambert_Conformal_Conic_2SP\"],"
                +"PARAMETER[\"standard_parallel_1\",33.90363402777778],"
                +"PARAMETER[\"standard_parallel_2\",33.62529002777777],"
                +"PARAMETER[\"latitude_of_origin\",0],"
                +"PARAMETER[\"central_meridian\",-117.4745428888889],"
                +"PARAMETER[\"false_easting\",0],"
                +"PARAMETER[\"false_northing\",0],"
                +"UNIT[\"metre\",1,"
                +"    AUTHORITY[\"EPSG\",\"9001\"]]]"),
              new AffineTransform(257.5, 0, 0, -257.5, -15312.865311483006735, 15349.948768731206656));

    }

    @Test
    @Ignore
    public void test14() throws Exception {
        // TODO : A problems with CRS Units ?
//        //Origin = (-368113.666666666686069,92083.666666666671517)
//        //Pixel Size = (42.666666666666664,-42.666666666666664)
        test("lcc-datu.tiff", longlatFactory.createFromWKT(
                "PROJCS[\"Lambert_Conformal_Conic_2SP\","
                +"GEOGCS[\"North_American_Datum_1983\","
                +"    DATUM[\"North_American_Datum_1983\","
                +"        SPHEROID[\"GRS 1980\",6378137,298.2572221010002,"
                +"            AUTHORITY[\"EPSG\",\"7019\"]],"
                +"        AUTHORITY[\"EPSG\",\"6269\"]],"
                +"    PRIMEM[\"Greenwich\",0],"
                +"    UNIT[\"\",0.0174532925199433]],"
                +"PROJECTION[\"Lambert_Conformal_Conic_2SP\"],"
                +"PARAMETER[\"standard_parallel_1\",37],"
                +"PARAMETER[\"standard_parallel_2\",39.5],"
                +"PARAMETER[\"latitude_of_origin\",36],"
                +"PARAMETER[\"central_meridian\",-79.5],"
                +"PARAMETER[\"false_easting\",0],"
                +"PARAMETER[\"false_northing\",0],"
                +"UNIT[\"unknown\",1]]"),
                new AffineTransform(42.666666666666664, 0, 0, -42.666666666666664, -368113.666666666686069, 92083.666666666671517));
    }

    @Test
    @Ignore
    public void test15() throws Exception {
        final CoordinateReferenceSystem sourceCRS = CRS.fromWKT(
            "    PROJCS[\"Mercator_1SP\","
            +"    GEOGCS[\"North_American_Datum_1927\","
            +"        DATUM[\"North_American_Datum_1927\","
            +"            SPHEROID[\"Clarke 1866\",6378206.4,294.9786982139006,"
            +"                AUTHORITY[\"EPSG\",\"7008\"]],"
            +"            AUTHORITY[\"EPSG\",\"6267\"]],"
            +"        PRIMEM[\"Greenwich\",0],"
            +"        UNIT[\"\",0.0174532925199433]],"
            +"    PROJECTION[\"Mercator_1SP\"],"
            +"    PARAMETER[\"central_meridian\",-90],"
            +"    PARAMETER[\"scale_factor\",0.829916312080482],"
            +"    PARAMETER[\"false_easting\",0.001],"
            +"    PARAMETER[\"false_northing\",0.002],"
            +"    UNIT[\"unknown\",1]]");
//            Origin = (1871084.537213840056211,693307.084818160161376)
//            Pixel Size = (257.916629199947522,-257.916629199947522)
        test("mer.tiff", sourceCRS,
                new AffineTransform(257.916629199947522, 0, 0, -257.916629199947522, 1871084.537213840056211, 693307.084818160161376));
    }

    @Test
    @Ignore
    public void test16() throws Exception {
        final CoordinateReferenceSystem sourceCRS = CRS.fromWKT(
         "    PROJCS[\"Oblique_Mercator\","
        +"    GEOGCS[\"North_American_Datum_1927\","
        +"        DATUM[\"North_American_Datum_1927\","
        +"            SPHEROID[\"Clarke 1866\",6378206.4,294.9786982139006,"
        +"                AUTHORITY[\"EPSG\",\"7008\"]],"
        +"            AUTHORITY[\"EPSG\",\"6267\"]],"
        +"        PRIMEM[\"Greenwich\",0],"
        +"        UNIT[\"\",0.0174532925199433]],"
        +"    PROJECTION[\"Hotine_Oblique_Mercator\"],"
        +"    PARAMETER[\"latitude_of_center\",40],"
        +"    PARAMETER[\"longitude_of_center\",0],"
        +"    PARAMETER[\"azimuth\",303.053393],"
        +"    PARAMETER[\"rectified_grid_angle\",90],"
        +"    PARAMETER[\"scale_factor\",1],"
        +"    PARAMETER[\"false_easting\",0],"
        +"    PARAMETER[\"false_northing\",0],"
                 +"    UNIT[\"unknown\",1]]");
//Origin = (1871084.537213840056211,693307.084818160044961)
//Pixel Size = (257.916629199947522,-257.916629199947522)
        test("merc_ob.tiff", sourceCRS,
                new AffineTransform(257.916629199947522, 0, 0, -257.916629199947522, 1871084.537213840056211, 693307.084818160044961));
    }

    @Test
    @Ignore
    public void test17() throws Exception {
        final CoordinateReferenceSystem sourceCRS = CRS.fromWKT(
                 "    PROJCS[\"Mercator_1SP\","
                +"    GEOGCS[\"NAD27\","
                +"        DATUM[\"North_American_Datum_1927\","
                +"            SPHEROID[\"Clarke 1866\",6378206.4,294.9786982139006,"
                +"                AUTHORITY[\"EPSG\",\"7008\"]],"
                +"            AUTHORITY[\"EPSG\",\"6267\"]],"
                +"        PRIMEM[\"Greenwich\",0],"
                +"        UNIT[\"degree\",0.0174532925199433],"
                +"        AUTHORITY[\"EPSG\",\"4267\"]],"
                +"    PROJECTION[\"Mercator_1SP\"],"
                +"    PARAMETER[\"central_meridian\",-117.4745428888889],"
                +"    PARAMETER[\"scale_factor\",1],"
                +"    PARAMETER[\"false_easting\",0],"
                +"    PARAMETER[\"false_northing\",0],"
                +"    UNIT[\"metre\",1,"
                +"        AUTHORITY[\"EPSG\",\"9001\"]]]");
//Origin = (-15337.635771224038763,3321889.111796239390969)
//Pixel Size = (257.5,-257.5)
        test("mercato.tiff", sourceCRS,
                new AffineTransform(257.5, 0, 0, -257.5, -15337.635771224038763, 3321889.111796239390969));
    }

    @Test
    @Ignore
    public void test18() throws Exception {

//    Projection = Transverse Mercator\",
        final CoordinateReferenceSystem sourceCRS = CRS.fromWKT(
                 "    GEOGCS[\"Rome_1940\","
                +"        DATUM[\"Rome_1940\","
                +"            SPHEROID[\"International 1909\",6378388,297.0000000284015]],"
                +"        PRIMEM[\"Greenwich\",0],"
                +"        UNIT[\"degree\",0.0174532925199433]],"
                +"    PROJECTION[\"Transverse_Mercator\"],"
                +"    PARAMETER[\"latitude_of_origin\",0],"
                +"    PARAMETER[\"central_meridian\",-3.45233333],"
                +"    PARAMETER[\"scale_factor\",0.9996],"
                +"    PARAMETER[\"false_easting\",1500000],"
                +"    PARAMETER[\"false_northing\",0],"
                +"    UNIT[\"metre\",1,"
                +"        AUTHORITY[\"EPSG\",\"9001\"]]]");
//Origin = (1404775.351438903948292,5000600.319504191167653)
//Pixel Size = (0.231864343174723,-0.231958667423210)
        test("milanogeo1.tif", sourceCRS,
                new AffineTransform(0.231864343174723, 0, 0, -0.231958667423210, 1404775.351438903948292, 5000600.319504191167653));

    }

    @Test
    public void test19() throws Exception {

        final CoordinateReferenceSystem sourceCRS = CRS.fromWKT(
                 "    PROJCS[\"NTF (Paris) / Nord France\","
                +"    GEOGCS[\"NTF (Paris)\","
                +"        DATUM[\"Nouvelle_Triangulation_Francaise_Paris\","
                +"            SPHEROID[\"Clarke 1880 (IGN)\",6378249.2,293.4660212936265,"
                +"                AUTHORITY[\"EPSG\",\"7011\"]],"
                +"            AUTHORITY[\"EPSG\",\"6807\"]],"
                +"        PRIMEM[\"Paris\",2.5969213],"
                +"        UNIT[\"grad\",0.01570796326794897],"
                +"        AUTHORITY[\"EPSG\",\"4807\"]],"
                +"    PROJECTION[\"Lambert_Conformal_Conic_1SP\"],"
                +"    PARAMETER[\"latitude_of_origin\",49.5],"
                +"    PARAMETER[\"central_meridian\",0],"
                +"    PARAMETER[\"scale_factor\",0.999877341],"
                +"    PARAMETER[\"false_easting\",600000],"
                +"    PARAMETER[\"false_northing\",200000],"
                +"    UNIT[\"metre\",1,"
                +"        AUTHORITY[\"EPSG\",\"9001\"]],"
                +"    AUTHORITY[\"EPSG\",\"27591\"]]");
//Origin = (440818,99902)
//Pixel Size = (256,-256)
        test("ntf_nor.tiff", AbstractCRS.castOrCopy(CRS.forCode("EPSG:27591")).forConvention(AxesConvention.RIGHT_HANDED),
                new AffineTransform(256, 0, 0, -256, 440818, 99902));

    }

    @Test
    public void test20() throws Exception {

        final CoordinateReferenceSystem sourceCRS = CRS.fromWKT(
                "    PROJCS[\"WGS 84 / UTM zone 17N\","
                +"    GEOGCS[\"WGS 84\","
                +"        DATUM[\"WGS_1984\","
                +"            SPHEROID[\"WGS 84\",6378137,298.257223563,"
                +"                AUTHORITY[\"EPSG\",\"7030\"]],"
                +"            AUTHORITY[\"EPSG\",\"6326\"]],"
                +"        PRIMEM[\"Greenwich\",0],"
                +"        UNIT[\"degree\",0.0174532925199433],"
                +"        AUTHORITY[\"EPSG\",\"4326\"]],"
                +"    PROJECTION[\"Transverse_Mercator\"],"
                +"    PARAMETER[\"latitude_of_origin\",0],"
                +"    PARAMETER[\"central_meridian\",-81],"
                +"    PARAMETER[\"scale_factor\",0.9996],"
                +"    PARAMETER[\"false_easting\",500000],"
                +"    PARAMETER[\"false_northing\",0],"
                +"    UNIT[\"metre\",1,"
                +"        AUTHORITY[\"EPSG\",\"9001\"]],"
                +"    AUTHORITY[\"EPSG\",\"32617\"]]");
//Origin = (677814.317641000030562,4555435.672149925492704)
//Pixel Size = (101.071680000000441,-101.584948148147234)
        test("o41078a.tiff", AbstractCRS.castOrCopy(CRS.forCode("EPSG:32617")).forConvention(AxesConvention.RIGHT_HANDED),
                new AffineTransform(101.071680000000441, 0, 0, -101.584948148147234, 677814.317641000030562, 4555435.672149925492704));

    }

    @Test
    @Ignore
    public void test21() throws Exception {
        final CoordinateReferenceSystem sourceCRS = CRS.fromWKT("PROJCS[\"Oblique_Mercator\","
        +"    GEOGCS[\"NAD27\","
        +"        DATUM[\"North_American_Datum_1927\","
        +"            SPHEROID[\"Clarke 1866\",6378206.4,294.9786982139006,"
        +"                AUTHORITY[\"EPSG\",\"7008\"]],"
        +"            AUTHORITY[\"EPSG\",\"6267\"]],"
        +"        PRIMEM[\"Greenwich\",0],"
        +"        UNIT[\"degree\",0.0174532925199433],"
        +"        AUTHORITY[\"EPSG\",\"4267\"]],"
        +"    PROJECTION[\"Hotine_Oblique_Mercator\"],"
        +"    PARAMETER[\"latitude_of_center\",33.76446202777777],"
        +"    PARAMETER[\"longitude_of_center\",-117.4745428888889],"
        +"    PARAMETER[\"azimuth\",0],"
        +"    PARAMETER[\"rectified_grid_angle\",90],"
        +"    PARAMETER[\"scale_factor\",1],"
        +"    PARAMETER[\"false_easting\",0],"
        +"    PARAMETER[\"false_northing\",0],"
        +"    UNIT[\"metre\",1,"
        +"        AUTHORITY[\"EPSG\",\"9001\"]]]");
//Origin = (-15312.880298927562762,15350.024225590750575)
//Pixel Size = (257.5,-257.5)
        test("oblqmer.tiff", sourceCRS,
                new AffineTransform(257.5, 0, 0, -257.5, -15312.880298927562762, 15350.024225590750575));
    }

    @Test
    @Ignore
    public void test22() throws Exception {
        final CoordinateReferenceSystem sourceCRS = CRS.fromWKT("PROJCS[\"NAD27 / California zone VI\","
        +"    GEOGCS[\"NAD27\","
        +"        DATUM[\"North_American_Datum_1927\","
        +"            SPHEROID[\"Clarke 1866\",6378206.4,294.9786982139006,"
        +"                AUTHORITY[\"EPSG\",\"7008\"]],"
        +"            AUTHORITY[\"EPSG\",\"6267\"]],"
        +"        PRIMEM[\"Greenwich\",0],"
        +"        UNIT[\"degree\",0.0174532925199433],"
        +"        AUTHORITY[\"EPSG\",\"4267\"]],"
        +"    PROJECTION[\"Lambert_Conformal_Conic_2SP\"],"
        +"    PARAMETER[\"standard_parallel_1\",33.88333333333333],"
        +"    PARAMETER[\"standard_parallel_2\",32.78333333333333],"
        +"    PARAMETER[\"latitude_of_origin\",32.16666666666666],"
        +"    PARAMETER[\"central_meridian\",-116.25],"
        +"    PARAMETER[\"false_easting\",2000000],"
        +"    PARAMETER[\"false_northing\",0],"
        +"    UNIT[\"US survey foot\",0.3048006096012192,"
        +"        AUTHORITY[\"EPSG\",\"9003\"]],"
        +"    AUTHORITY[\"EPSG\",\"26746\"]]");
//Origin = (1577464.744246162474155,634018.079368813545443)
//Pixel Size = (845.577813324999738,-860.515102372881643)
        test("sp2.tiff", sourceCRS, //CRS.forCode("EPSG:26746",true),
                new AffineTransform(845.577813324999738, 0, 0, -860.515102372881643, 1577464.744246162474155, 634018.079368813545443));
    }

    @Test
    public void test23() throws Exception {

final CoordinateReferenceSystem sourceCRS = CRS.fromWKT("PROJCS[\"NAD83 / Alabama West\","
+"    GEOGCS[\"NAD83\","
+"        DATUM[\"North_American_Datum_1983\","
+"            SPHEROID[\"GRS 1980\",6378137,298.2572221010002,"
+"                AUTHORITY[\"EPSG\",\"7019\"]],"
+"            AUTHORITY[\"EPSG\",\"6269\"]],"
+"        PRIMEM[\"Greenwich\",0],"
+"        UNIT[\"degree\",0.0174532925199433],"
+"        AUTHORITY[\"EPSG\",\"4269\"]],"
+"    PROJECTION[\"Transverse_Mercator\"],"
+"    PARAMETER[\"latitude_of_origin\",30],"
+"    PARAMETER[\"central_meridian\",-87.5],"
+"    PARAMETER[\"scale_factor\",0.999933333],"
+"    PARAMETER[\"false_easting\",600000],"
+"    PARAMETER[\"false_northing\",0],"
+"    UNIT[\"metre\",1,"
+"        AUTHORITY[\"EPSG\",\"9001\"]],"
+"    AUTHORITY[\"EPSG\",\"26930\"]]");
//Origin = (1871084.537213840056211,693307.084818160044961)
//Pixel Size = (257.916629199947522,-257.916629199947522)
        test("spcs8.tiff", AbstractCRS.castOrCopy(CRS.forCode("EPSG:26930")).forConvention(AxesConvention.RIGHT_HANDED),
                new AffineTransform(257.916629199947522, 0, 0, -257.916629199947522, 1871084.537213840056211, 693307.084818160044961));

    }

    @Test
    @Ignore
    public void test24() throws Exception {
        final CoordinateReferenceSystem sourceCRS = CRS.fromWKT("PROJCS[\"NAD83 / Alabama West\","
        +"    GEOGCS[\"NAD83\","
        +"        DATUM[\"North_American_Datum_1983\","
        +"            SPHEROID[\"GRS 1980\",6378137,298.2572221010002,"
        +"                AUTHORITY[\"EPSG\",\"7019\"]],"
        +"            AUTHORITY[\"EPSG\",\"6269\"]],"
        +"        PRIMEM[\"Greenwich\",0],"
        +"        UNIT[\"degree\",0.0174532925199433],"
        +"        AUTHORITY[\"EPSG\",\"4269\"]],"
        +"    PROJECTION[\"Transverse_Mercator\"],"
        +"    PARAMETER[\"latitude_of_origin\",30],"
        +"    PARAMETER[\"central_meridian\",-87.5],"
        +"    PARAMETER[\"scale_factor\",0.999933333],"
        +"    PARAMETER[\"false_easting\",600000],"
        +"    PARAMETER[\"false_northing\",0],"
        +"    UNIT[\"metre\",1,"
        +"        AUTHORITY[\"EPSG\",\"9001\"]],"
        +"    AUTHORITY[\"EPSG\",\"26930\"]]");
//Origin = (1871084.537213840056211,693307.084818160044961)
//Pixel Size = (257.916629199947522,-257.916629199947522)
        test("spcs83f.tiff", AbstractCRS.castOrCopy(CRS.forCode("EPSG:26930")).forConvention(AxesConvention.RIGHT_HANDED),
                new AffineTransform(257.916629199947522, 0, 0, -257.916629199947522, 1871084.537213840056211, 693307.084818160044961));
    }

    @Test
    public void test25() throws Exception {

final CoordinateReferenceSystem sourceCRS = CRS.fromWKT("PROJCS[\"NAD83 / California zone 6\","
+"    GEOGCS[\"NAD83\","
+"        DATUM[\"North_American_Datum_1983\","
+"            SPHEROID[\"GRS 1980\",6378137,298.2572221010002,"
+"                AUTHORITY[\"EPSG\",\"7019\"]],"
+"            AUTHORITY[\"EPSG\",\"6269\"]],"
+"        PRIMEM[\"Greenwich\",0],"
+"        UNIT[\"degree\",0.0174532925199433],"
+"        AUTHORITY[\"EPSG\",\"4269\"]],"
+"    PROJECTION[\"Lambert_Conformal_Conic_2SP\"],"
+"    PARAMETER[\"standard_parallel_1\",33.88333333333333],"
+"    PARAMETER[\"standard_parallel_2\",32.78333333333333],"
+"    PARAMETER[\"latitude_of_origin\",32.16666666666666],"
+"    PARAMETER[\"central_meridian\",-116.25],"
+"    PARAMETER[\"false_easting\",2000000],"
+"    PARAMETER[\"false_northing\",500000],"
+"    UNIT[\"metre\",1,"
+"        AUTHORITY[\"EPSG\",\"9001\"]],"
+"    AUTHORITY[\"EPSG\",\"26946\"]]");
//Origin = (6138884.595838666893542,2274467.683180795051157)
//Pixel Size = (845.588090932656428,-860.525123901542884)
        test("spif8.tiff", AbstractCRS.castOrCopy(CRS.forCode("EPSG:26946")).forConvention(AxesConvention.RIGHT_HANDED),
                new AffineTransform(845.588090932656428, 0, 0, -860.525123901542884, 6138884.595838666893542, 2274467.683180795051157));

    }

    @Test
    @Ignore
    public void test26() throws Exception {
        final CoordinateReferenceSystem source = CRS.fromWKT(
              "PROJCS[\"Stereographic\","
            + "GEOGCS[\"WGS_1984\","
                + "DATUM[\"WGS_1984\","
                    + "SPHEROID[\"WGS 84\",6378137,298.257223563,"
                        + "AUTHORITY[\"EPSG\",\"7030\"]],"
                    + "AUTHORITY[\"EPSG\",\"6326\"]],"
                + "PRIMEM[\"Greenwich\",0],"
                + " UNIT[\"\",0.0174532925199433]],"
            + "PROJECTION[\"Oblique_Stereographic\"],"
            + "PARAMETER[\"latitude_of_origin\",30],"
            + "PARAMETER[\"central_meridian\",-90],"
            + "PARAMETER[\"scale_factor\",1],"
            + "PARAMETER[\"false_easting\",0],"
            + "PARAMETER[\"false_northing\",0],"
            + "UNIT[\"unknown\",1]]");
//Origin = (613872.879663333296776,227462.954336666676681)
//Pixel Size = (84.618316666649960,-84.618316666698476)
        test("stere.tiff", source,
                new AffineTransform(84.618316666649960, 0, 0, -84.618316666698476, 613872.879663333296776, 227462.954336666676681));
    }

    @Test
    @Ignore
    public void test27() throws Exception {
        final CoordinateReferenceSystem sourceCRS = CRS.fromWKT("PROJCS[\"Polar_Stereographic\","
        +"    GEOGCS[\"WGS_1984\","
        +"        DATUM[\"WGS_1984\","
        +"            SPHEROID[\"WGS 84\",6378137,298.257223563,"
        +"                AUTHORITY[\"EPSG\",\"7030\"]],"
        +"            AUTHORITY[\"EPSG\",\"6326\"]],"
        +"        PRIMEM[\"Greenwich\",0],"
        +"        UNIT[\"\",0.0174532925199433]],"
        +"    PROJECTION[\"Polar_Stereographic\"],"
        +"    PARAMETER[\"latitude_of_origin\",0],"
        +"    PARAMETER[\"central_meridian\",90],"
        +"    PARAMETER[\"scale_factor\",1],"
        +"    PARAMETER[\"false_easting\",0],"
        +"    PARAMETER[\"false_northing\",0],"
        +"    UNIT[\"unknown\",1]]");
        //Origin = (613872.879663333296776,227462.954336666676681)
        //Pixel Size = (84.618316666649960,-84.618316666698476)
        test("stereo_n.tiff", sourceCRS,
                new AffineTransform(84.618316666649960, 0, 0, -84.618316666698476, 613872.879663333296776, 227462.954336666676681));
    }

    @Test
    @Ignore
    public void test28() throws Exception {
        final CoordinateReferenceSystem sourceCRS = CRS.fromWKT("PROJCS[\"Polar_Stereographic\","
        +"    GEOGCS[\"WGS_1984\","
        +"        DATUM[\"WGS_1984\","
        +"            SPHEROID[\"WGS 84\",6378137,298.257223563,"
        +"                AUTHORITY[\"EPSG\",\"7030\"]],"
        +"            AUTHORITY[\"EPSG\",\"6326\"]],"
        +"        PRIMEM[\"Greenwich\",0],"
        +"        UNIT[\"\",0.0174532925199433]],"
        +"    PROJECTION[\"Polar_Stereographic\"],"
        +"    PARAMETER[\"latitude_of_origin\",0],"
        +"    PARAMETER[\"central_meridian\",-90],"
        +"    PARAMETER[\"scale_factor\",1],"
        +"    PARAMETER[\"false_easting\",0],"
        +"    PARAMETER[\"false_northing\",0],"
        +"    UNIT[\"unknown\",1]]");
        //Origin = (613872.879663333296776,227462.954336666676681)
        //Pixel Size = (84.618316666649960,-84.618316666698476)
        test("stereo_s.tiff", sourceCRS,
                new AffineTransform(84.618316666649960, 0, 0, -84.618316666698476, 613872.879663333296776, 227462.954336666676681));
    }

    @Test
    @Ignore
    public void test29() throws Exception {
        final CoordinateReferenceSystem sourceCRS = CRS.fromWKT("PROJCS[\"Polar_Stereographic\","
        +"    GEOGCS[\"WGS_1984\","
        +"        DATUM[\"WGS_1984\","
        +"            SPHEROID[\"WGS 84\",6378137,298.257223563,"
        +"                AUTHORITY[\"EPSG\",\"7030\"]],"
        +"            AUTHORITY[\"EPSG\",\"6326\"]],"
        +"        PRIMEM[\"Greenwich\",0],"
        +"        UNIT[\"\",0.0174532925199433]],"
        +"    PROJECTION[\"Polar_Stereographic\"],"
        +"    PARAMETER[\"latitude_of_origin\",0],"
        +"    PARAMETER[\"central_meridian\",90],"
        +"    PARAMETER[\"scale_factor\",0.994],"
        +"    PARAMETER[\"false_easting\",2000000],"
        +"    PARAMETER[\"false_northing\",2000000],"
        +"    UNIT[\"unknown\",1]]");
//Origin = (613872.879663333296776,227462.954336666676681)
//Pixel Size = (84.618316666649960,-84.618316666698476)
        test("stereo_u.tiff", sourceCRS,
                new AffineTransform(84.618316666649960, 0, 0, -84.618316666698476, 613872.879663333296776, 227462.954336666676681));
    }

    @Test
    @Ignore
    public void test30() throws Exception {
        final CoordinateReferenceSystem sourceCRS = CRS.fromWKT("PROJCS[\"Transverse_Mercator\","
        +"    GEOGCS[\"NAD27\","
        +"        DATUM[\"North_American_Datum_1927\","
        +"            SPHEROID[\"Clarke 1866\",6378206.4,294.9786982139006,"
        +"                AUTHORITY[\"EPSG\",\"7008\"]],"
        +"            AUTHORITY[\"EPSG\",\"6267\"]],"
        +"        PRIMEM[\"Greenwich\",0],"
        +"        UNIT[\"degree\",0.0174532925199433],"
        +"        AUTHORITY[\"EPSG\",\"4267\"]],"
        +"    PROJECTION[\"Transverse_Mercator\"],"
        +"    PARAMETER[\"latitude_of_origin\",33.76446202777777],"
        +"    PARAMETER[\"central_meridian\",-117.4745428888889],"
        +"    PARAMETER[\"scale_factor\",1],"
        +"    PARAMETER[\"false_easting\",0],"
        +"    PARAMETER[\"false_northing\",0],"
        +"    UNIT[\"metre\",1,"
        +"        AUTHORITY[\"EPSG\",\"9001\"]]]");
//Origin = (-15312.880298929545461,15350.024226515102782)
//Pixel Size = (257.5,-257.5)
        test("t.tiff", sourceCRS,
                new AffineTransform(257.5, 0, 0, -257.5, -15312.880298929545461, 15350.024226515102782));
    }

    @Test
    @Ignore
    public void test31() throws Exception {
        final CoordinateReferenceSystem sourceCRS = CRS.fromWKT("PROJCS[\"Transverse_Mercator\","
        +"    GEOGCS[\"North_American_Datum_1927\","
        +"        DATUM[\"North_American_Datum_1927\","
        +"            SPHEROID[\"Clarke 1866\",6378206.4,294.9786982139006,"
        +"                AUTHORITY[\"EPSG\",\"7008\"]],"
        +"            AUTHORITY[\"EPSG\",\"6267\"]],"
        +"        PRIMEM[\"Greenwich\",0],"
        +"        UNIT[\"\",0.0174532925199433]],"
        +"    PROJECTION[\"Transverse_Mercator\"],"
        +"    PARAMETER[\"latitude_of_origin\",30],"
        +"    PARAMETER[\"central_meridian\",-90],"
        +"    PARAMETER[\"scale_factor\",0.99999],"
        +"    PARAMETER[\"false_easting\",0.001],"
        +"    PARAMETER[\"false_northing\",0.002],"
        +"    UNIT[\"unknown\",1]]");
//Origin = (1871084.537213840056211,693307.084818160161376)
//Pixel Size = (257.916629199947522,-257.916629199947522)
        test("trans_mer.tiff", sourceCRS,
                new AffineTransform(257.916629199947522, 0, 0, -257.916629199947522, 1871084.537213840056211, 693307.084818160161376));
    }

    @Test
    @Ignore
    public void test32() throws Exception {
        final CoordinateReferenceSystem sourceCRS = CRS.fromWKT("PROJCS[\"Universal Transverse Mercator North American 1927 Zone Number 16N\","
        +"    GEOGCS[\"North_American_Datum_1927\","
        +"        DATUM[\"North_American_Datum_1927\","
        +"            SPHEROID[\"Clarke 1866\",6378206.4,294.9786982139006,"
        +"                AUTHORITY[\"EPSG\",\"7008\"]],"
        +"            AUTHORITY[\"EPSG\",\"6267\"]],"
        +"        PRIMEM[\"Greenwich\",0],"
        +"        UNIT[\"\",0.0174532925199433]],"
        +"    PROJECTION[\"Transverse_Mercator\"],"
        +"    PARAMETER[\"latitude_of_origin\",0],"
        +"    PARAMETER[\"central_meridian\",-87],"
        +"    PARAMETER[\"scale_factor\",0.9996],"
        +"    PARAMETER[\"false_easting\",500000],"
        +"    PARAMETER[\"false_northing\",0],"
        +"    UNIT[\"unknown\",1]]");
//Origin = (1871084.447231799829751,693307.174800200038590)
//Pixel Size = (257.466718999591365,-257.466718999979378)
        test("ut.tiff", sourceCRS,
                new AffineTransform(257.466718999591365, 0, 0, -257.466718999979378, 1871084.447231799829751, 693307.174800200038590));
    }

    @Test
    public void test33() throws Exception {
        final CoordinateReferenceSystem sourceCRS = CRS.fromWKT("PROJCS[\"NAD27 / UTM zone 11N\","
        +"    GEOGCS[\"NAD27\","
        +"        DATUM[\"North_American_Datum_1927\","
        +"            SPHEROID[\"Clarke 1866\",6378206.4,294.9786982139006,"
        +"                AUTHORITY[\"EPSG\",\"7008\"]],"
        +"            AUTHORITY[\"EPSG\",\"6267\"]],"
        +"        PRIMEM[\"Greenwich\",0],"
        +"        UNIT[\"degree\",0.0174532925199433],"
        +"        AUTHORITY[\"EPSG\",\"4267\"]],"
        +"    PROJECTION[\"Transverse_Mercator\"],"
        +"    PARAMETER[\"latitude_of_origin\",0],"
        +"    PARAMETER[\"central_meridian\",-117],"
        +"    PARAMETER[\"scale_factor\",0.9996],"
        +"    PARAMETER[\"false_easting\",500000],"
        +"    PARAMETER[\"false_northing\",0],"
        +"    UNIT[\"metre\",1,"
        +"        AUTHORITY[\"EPSG\",\"9001\"]],"
        +"    AUTHORITY[\"EPSG\",\"26711\"]]");
//Origin = (440818,3751222)
//Pixel Size = (256,-256)
        test("utm11-2.tiff", AbstractCRS.castOrCopy(CRS.forCode("EPSG:26711")).forConvention(AxesConvention.RIGHT_HANDED),
                new AffineTransform(256, 0, 0, -256, 440818, 3751222));
    }

    // TODO : Activate if the writer manage ImageOutputStream.
    @Test
    @Ignore
    public void testStreamWriting() throws DataStoreException, IOException{

        File file = TestData.file(GeoTiffRWTest.class, "002025_0100_010722_l7_01_utm2.tiff");
        ImageCoverageReader reader = new ImageCoverageReader();
        reader.setInput(file);

        try{
            //first test
            GridCoverage coverage = reader.read(null);

            final File tempFile = File.createTempFile("coverage", ".tiff", tempDir);
            tempFile.deleteOnExit();
            final FileOutputStream stream = new FileOutputStream(tempFile);

            final IIOImage iioimage = new IIOImage(coverage.render(null), null, reader.getCoverageMetadata());
            final ImageWriter writer = ImageIO.getImageWritersByFormatName("geotiff").next();
            writer.setOutput(ImageIO.createImageOutputStream(stream));
            writer.write(null, iioimage, null);
            writer.dispose();

        }finally{
            reader.dispose();
        }

    }


    private void test(final String fileName, final CoordinateReferenceSystem crs, final AffineTransform gridToCRS)
            throws IOException, DataStoreException{

        File file = TestData.file(GeoTiffRWTest.class, fileName);
        ImageCoverageReader reader = new ImageCoverageReader();
        reader.setInput(file);

        try{
            //first test
            GridCoverage coverage = reader.read(null);
            compare(coverage, crs, gridToCRS);

            //write it and test again
            file = write(coverage,reader.getCoverageMetadata());
            reader.dispose();
            final ImageReader imgReader = new TiffImageReader(new TiffImageReader.Spi());
            imgReader.setInput(file);
            reader = (ImageCoverageReader) CoverageIO.createSimpleReader(imgReader);

            //second test
            coverage = reader.read(null);
            compare(coverage, crs, gridToCRS);

        }finally{
            reader.dispose();
        }
    }

    private static void compare(final GridCoverage coverage,
            final CoordinateReferenceSystem crs, final AffineTransform gridToCRS){
        //test coordinate reference system
        final CoordinateReferenceSystem coverageCRS = coverage.getCoordinateReferenceSystem();
        if (!Utilities.equalsApproximately(crs, coverageCRS)) {
            final MathTransform mt;
            try {
                mt = CRS.findOperation(crs, coverageCRS, null).getMathTransform();
            } catch (FactoryException e) {
                throw new AssertionError(e);
            }
            if (!mt.isIdentity()) {
                fail("Source and read CRS shall be approximatively equals. MathTransform between the two is:\n" +
                     ((org.apache.sis.referencing.operation.transform.AbstractMathTransform) mt).toString(org.apache.sis.io.wkt.Convention.INTERNAL));
            }
        }

        //test transform
        final AffineTransform2D rasterTrs = (AffineTransform2D) coverage.getGridGeometry().getGridToCRS(PixelInCell.CELL_CORNER);
        final double[] matrixRaster = new double[6];
        rasterTrs.getMatrix(matrixRaster);
        final double[] matrixReference = new double[6];
        gridToCRS.getMatrix(matrixReference);

        assertArrayEquals(matrixReference, matrixRaster, 1e-6);
    }

    /**
     * Copy coverage, in new file and retest it later.
     */
    private File write(final GridCoverage coverage, final SpatialMetadata metadata) throws IOException, CoverageStoreException{
        final File tempFile = File.createTempFile("coverage", ".tiff", tempDir);
        tempFile.deleteOnExit();

        final IIOImage iioimage = new IIOImage(coverage.render(null), null, metadata);
        final ImageWriter writer = ImageIO.getImageWritersByFormatName("geotiff").next();
//        final ImageWriter writer = new GeoTiffImageWriter(new GeoTiffImageWriter.Spi("TIFF"));
        writer.setOutput(tempFile);
        writer.write(null, iioimage, null);
        writer.dispose();
        return tempFile;
    }

}
