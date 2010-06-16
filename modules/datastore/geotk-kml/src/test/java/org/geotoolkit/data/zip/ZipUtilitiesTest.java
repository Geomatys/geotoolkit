package org.geotoolkit.data.zip;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.Adler32;
import java.util.zip.CRC32;
import java.util.zip.Checksum;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
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

    private final static Checksum CHECKSUM = new Adler32();

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
    public void fileTestStored() throws IOException{

        File file1 = File.createTempFile("file1",".txt");
        File archive = File.createTempFile("archive",".zip");
        file1.deleteOnExit();
        archive.deleteOnExit();

        ZipUtilities.setChecksumAlgorithm(CHECKSUM);
        ZipUtilities.zip(archive,file1);
        ZipUtilities.unzip(archive);
        
        List<String> zipContent = listContent(archive);
        assertEquals(zipContent.get(0), file1.getName());
    }

    @Test
    public void fileTest() throws IOException{

        File file1 = File.createTempFile("file1",".txt");
        File archive = File.createTempFile("archive",".zip");
        file1.deleteOnExit();
        archive.deleteOnExit();

        ZipUtilities.setChecksumAlgorithm(CHECKSUM);
        ZipUtilities.zip(archive,ZipOutputStream.DEFLATED,9,file1);
        ZipUtilities.unzip(archive);

        List<String> zipContent = listContent(archive);
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

        ZipUtilities.setChecksumAlgorithm(CHECKSUM);
        ZipUtilities.zip(archivePath,ZipOutputStream.DEFLATED,9,file1Path);
        ZipUtilities.unzip(archivePath);

        List<String> zipContent = listContent(archive);
        assertEquals(zipContent.get(0), file1.getName());
    }

    @Test
    public void urlTest() throws IOException{

        File file1 = File.createTempFile("file1",".txt");
        File archive = File.createTempFile("archive",".zip");
        file1.deleteOnExit();
        archive.deleteOnExit();

        URL url1 = new URL("file://"+file1.getAbsolutePath());
        URL urlArchive = new URL("file://"+archive.getAbsolutePath());

        ZipUtilities.setChecksumAlgorithm(CHECKSUM);
        ZipUtilities.zip(archive,ZipOutputStream.DEFLATED,9,url1);
        ZipUtilities.unzip(urlArchive);

        List<String> zipContent = listContent(archive);
        assertEquals(zipContent.get(0), file1.getName());
    }

    @Test
    public void uriTest() throws IOException{

        File file1 = File.createTempFile("file1",".txt");
        File archive = File.createTempFile("archive",".zip");
        file1.deleteOnExit();
        archive.deleteOnExit();

        URI uri1 = URI.create("file://"+file1.getAbsolutePath());
        URI uriArchive = URI.create("file://"+archive.getAbsolutePath());

        ZipUtilities.setChecksumAlgorithm(CHECKSUM);
        ZipUtilities.zip(archive,ZipOutputStream.DEFLATED,9,uri1);
        ZipUtilities.unzip(uriArchive);

        List<String> zipContent = listContent(archive);
        assertEquals(zipContent.get(0), file1.getName());
    }

    /**
     * <p>This method lists the content of indicated archive.</p>
     *
     * @param archive Instance of ZipFile, File or String path
     * @return a list of archive entries.
     * @throws IOException
     */
    public static List<String> listContent(final Object archive)
            throws IOException{

        final List<String> out = new ArrayList<String>();
        ZipFile zf = null;

        if(archive instanceof ZipFile){
            zf = (ZipFile) archive;
        } else if(archive instanceof File){
            zf = new ZipFile((File) archive);
        } else if (archive instanceof String){
            zf = new ZipFile((String) archive);
        }

        Enumeration entries = zf.entries();
        while (entries.hasMoreElements()){
            ZipEntry entry = (ZipEntry) entries.nextElement();
            out.add(entry.getName());
        }
        return out;
    }

}