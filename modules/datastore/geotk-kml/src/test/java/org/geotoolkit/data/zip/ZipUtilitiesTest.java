package org.geotoolkit.data.zip;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.List;
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
public class ZipUtilitiesTest {

    public ZipUtilitiesTest() {
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
    public void fileTest() throws IOException{

        File file1 = File.createTempFile("file1",".txt");
        File archive = File.createTempFile("archive",".zip");
        file1.deleteOnExit();
        archive.deleteOnExit();

        ZipUtilities.archive(archive,file1);
        ZipUtilities.extract(archive);
        
        List<String> zipContent = ZipUtilities.listContent(archive);
        assertEquals(zipContent.get(0), file1.getName());
    }

    @Test
    public void stringTest() throws IOException{

        File file1 = File.createTempFile("file1",".txt");
        File archive = File.createTempFile("archive",".zip");
        file1.deleteOnExit();
        archive.deleteOnExit();

        String file1Path = file1.getAbsolutePath();
        String archivePath = archive.getAbsolutePath();

        ZipUtilities.archive(archivePath,file1Path);
        ZipUtilities.extract(archive);

        List<String> zipContent = ZipUtilities.listContent(archive);
        assertEquals(zipContent.get(0), file1.getName());
    }

    @Test
    public void urlTest() throws IOException{

        File file1 = File.createTempFile("file1",".txt");
        File archive = File.createTempFile("archive",".zip");
        file1.deleteOnExit();
        archive.deleteOnExit();

        URL url1 = new URL("file://"+file1.getAbsolutePath());

        ZipUtilities.archive(archive,url1);
        ZipUtilities.extract(archive);

        List<String> zipContent = ZipUtilities.listContent(archive);
        assertEquals(zipContent.get(0), file1.getName());
    }

    @Test
    public void uriTest() throws IOException{

        File file1 = File.createTempFile("file1",".txt");
        File archive = File.createTempFile("archive",".zip");
        file1.deleteOnExit();
        archive.deleteOnExit();

        URI uri1 = URI.create("file://"+file1.getAbsolutePath());

        ZipUtilities.archive(archive,uri1);
        ZipUtilities.extract(archive);

        List<String> zipContent = ZipUtilities.listContent(archive);
        assertEquals(zipContent.get(0), file1.getName());
    }

}