package org.geotoolkit.util.dom;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.sis.test.DependsOnMethod;
import org.apache.sis.test.TestCase;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.internal.sql.DefaultDataSource;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Test dom utility methods from {@link HtmlBuilder}.
 *
 * @author Alexis Manin (Geomatys)
 */
public class HtmlBuilderTest extends TestCase {

    private static URL W3C_VALIDATOR;
    private static Path TEMP_DIR;

    @BeforeClass
    public static void init() throws IOException {
        W3C_VALIDATOR = new URL("https://validator.w3.org/nu/?out=gnu&level=error");
        TEMP_DIR = Files.createTempDirectory("HtmlBuilderTest");
    }

    @AfterClass
    public static void deleteTempDir() {
        try {
            Files.walkFileTree(TEMP_DIR, new SimpleFileVisitor<Path>() {

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
        } catch (IOException e) {
            Logging.getLogger("org.geotoolkit.tests").log(Level.WARNING, "Cannot delete temporary directory used for tests.", e);
        }
    }

    @Test
    public void testAppendChild() throws Exception {
        final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        final String tagName = "bouh";

        HtmlBuilder.appendChild(doc, doc, tagName);
        Assert.assertEquals("Node has not been added to target document.", 1, doc.getElementsByTagName(tagName).getLength());
    }

    @Test
    @DependsOnMethod("testAppendChild")
    public void testNewHtmlDocument() throws Exception {
        final String title = "myHtml";
        Document htmlDoc = HtmlBuilder.newHtmlDocument(DocumentBuilderFactory.newInstance().newDocumentBuilder(), title);

        assertValidHtmlDocument(htmlDoc, title);
    }

    @Test
    @DependsOnMethod("testNewHtmlDocument")
    public void testWriteDocument() throws Exception {
        final String title = "TEST";
        final DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        final Path tmpFile = Files.createTempFile(TEMP_DIR, "test", ".html");

        HtmlBuilder.write(
                HtmlBuilder.newHtmlDocument(docBuilder, title),
                tmpFile,
                HtmlBuilder.createHtmlTransformer());

        validateHtml(tmpFile);
    }

    @Test
    public void testNameComparator() throws Exception {
        final ArrayList<String> var = new ArrayList<>();
        var.add("verts");
        var.add(HtmlBuilder.DEFAULT_NAME);
        var.add("allez");
        var.add("les");

        Collections.sort(var, new HtmlBuilder.NameComparator());

        final StringBuilder result = new StringBuilder();
        result.append(var.get(0));
        for (int i = 1 ; i < var.size() ; i++) {
            result.append(' ').append(var.get(i));
        }

        Assert.assertEquals("Input list has not been sorted alphabetically !", "allez les verts "+HtmlBuilder.DEFAULT_NAME, result.toString());
    }

    @Test
    public void testWrongParameters() throws Exception {
        final Path notDir = Files.createTempFile(TEMP_DIR, "notDir", "html");
        final HtmlBuilder builder = new HtmlBuilder();
        try {
            builder.setOutput(notDir);
            Assert.fail("Html builder should not accept concrete files as output.");
        } catch (IOException e) {
            // good behavior
        }

        builder.setSource(createMockConnection());

        final String badFilter = "unexisting";
        try {
            builder.setCatalog(badFilter);
            Assert.fail("Html builder should not accept a catalog which is not present in target database.");
        } catch (IllegalArgumentException e) {
            // good behavior
        }

        try {
            builder.setSchema(badFilter);
            Assert.fail("Html builder should not accept a schema which is not present in target database.");
        } catch (IllegalArgumentException e) {
            // good behavior
        }
    }

    @Test
    @DependsOnMethod("testWriteDocument")
    public void testBuild() throws Exception {
        final Path myDir = Files.createTempDirectory(TEMP_DIR, "myDear");
        final Connection con = createMockConnection();

        // populate database
        try (final PreparedStatement statement = con.prepareStatement("CREATE TABLE TOTO ("
                + "ID INT PRIMARY KEY NOT NULL,"
                + " A_NUMBER INT DEFAULT 0)");
            ) {
            statement.execute();
        }

        try (final PreparedStatement statement = con.prepareStatement("CREATE TABLE TATA ("
                    + "NAME VARCHAR(65) NOT NULL,"
                    + " BIRTH DATE NOT NULL,"
                    + " NUMBER INT DEFAULT 0,"
                    + " TOTO_ID INT,"
                    + " CONSTRAINT FK_TOTO FOREIGN KEY (TOTO_ID) REFERENCES TOTO(ID),"
                    + " PRIMARY KEY (NAME, BIRTH))")) {
            statement.execute();
        }

        Path result = new HtmlBuilder().setSource(con).setOutput(myDir).build();
        final AtomicBoolean totoFound = new AtomicBoolean(false);
        final AtomicBoolean tataFound = new AtomicBoolean(false);

        Files.walkFileTree(result.getParent(), new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                final String[] name = file.getFileName().toString().split("\\.");
                if (name[1].equalsIgnoreCase("html")) {
                    // If we found an html document, we ensure it's a valid one.
                    try {
                        validateHtml(file);
                    } catch (MalformedURLException ex) {
                        throw new IllegalStateException(ex);
                    }

                    switch (name[0]) {
                        case "TOTO":
                            totoFound.set(true);
                            break;
                        case "TATA":
                            tataFound.set(true);
                            break;
                    }
                }
                return super.visitFile(file, attrs);
            }
        });
        Assert.assertTrue("No html created for table TOTO", totoFound.get());
        Assert.assertTrue("No html created for table TATA", tataFound.get());
    }

    private static void validateHtml(final Path htmlDoc) throws MalformedURLException, IOException {
        HttpsURLConnection con = (HttpsURLConnection) W3C_VALIDATOR.openConnection();
        con.setRequestMethod("POST");
        con.addRequestProperty("Content-type", "text/html; charset=UTF-8");
        con.setDoOutput(true);

        try {
            con.getOutputStream().write(Files.readAllBytes(htmlDoc));
        } catch (UnknownHostException e) {
            Assume.assumeNoException("Cannot reach W3C validator.", e);
        }

        final StringBuilder builder = new StringBuilder();
        int read;
        try (final InputStream stream = con.getInputStream()) {
            while ((read = stream.read()) >= 0)
                builder.append((char)read);
        }

        if (builder.length() > 0) {
            builder.insert(0, System.lineSeparator())
                    .insert(0, htmlDoc.toString())
                    .insert(0, "An error has been detected on a document : ");
            Assert.fail(builder.toString());
        }
    }

    private static void assertValidHtmlDocument(final Document htmlDoc, final String title) throws Exception {

        // Check generic structure
        Assert.assertEquals("Generated doc should have a single <html> markup.", 1, htmlDoc.getElementsByTagName("html").getLength());
        Assert.assertEquals("Generated doc should have a single <head> markup.", 1, htmlDoc.getElementsByTagName("head").getLength());
        Assert.assertEquals("Generated doc should have a single <body> markup.", 1, htmlDoc.getElementsByTagName("body").getLength());

        // Ensure title has been put
        final NodeList titles = htmlDoc.getElementsByTagName("title");
        Assert.assertEquals("Generated doc should have a single <title> markup.", 1, titles.getLength());
        final Node titleNode = titles.item(0);
        Assert.assertEquals("Title markup has not been set correctly !", title, titleNode.getTextContent());
        final Node headNode = titleNode.getParentNode();
        Assert.assertNotNull("Title has not been put in <head> markup !", headNode);
        Assert.assertEquals("Title has not been put in <head> markup !", "head", headNode.getNodeName());

        // Now that we have head node, we ensure it is into html markup.
        final Node htmlNode = headNode.getParentNode();
        Assert.assertNotNull("Head has not been put in <html> markup !", htmlNode);
        Assert.assertEquals("Head has not been put in <html> markup !", "html", htmlNode.getNodeName());

        // And now, we can ensure body is the second child of html node
        Assert.assertTrue("Html document incomplete !", htmlNode.getChildNodes().getLength() > 1);
        Node body = htmlNode.getChildNodes().item(1);
        Assert.assertEquals("Body has not been put in <html> markup !", "body", body.getNodeName());
    }

    /**
     * Note : See MetadataWriter test to open a memory connection on derby.
     * @return A simple connection pointing on an empty database in memory, to test db analysis.
     */
    private static Connection createMockConnection() throws SQLException {
        final DefaultDataSource ds = new DefaultDataSource("jdbc:derby:memory:Test;create=true");
        return ds.getConnection();
    }
}
