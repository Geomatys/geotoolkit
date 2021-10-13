/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.coverage.worldfile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import org.apache.sis.internal.storage.io.ChannelImageInputStream;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.storage.DataStores;
import org.apache.sis.storage.ProbeResult;
import org.apache.sis.storage.StorageConnector;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.internal.image.io.SupportFiles;
import org.geotoolkit.test.VerifiableStorageConnector;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeFalse;

/**
 * Tests for FileCoverageStore
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class FileCoverageProviderTest extends org.geotoolkit.test.TestBase {

    public FileCoverageProviderTest() {
    }

    @Test
    public void testFactory() {

        boolean found = false;
        for(DataStoreProvider fact : DataStores.providers()){
            if(fact instanceof FileCoverageProvider){
                found = true;
            }
        }

        if(!found){
            fail("Factory not found");
        }
    }

    /**
     * Ensure the provider probeContent match for simple image files with *.prj or *.tfw.
     */
    @Test
    public void testProbeContentWorldFile() throws IOException, DataStoreException {

        final Path file = Files.createTempFile("geo", ".png");
        file.toFile().deleteOnExit();
        final Path filePrj = (Path) SupportFiles.changeExtension(file, "prj");
        Files.createFile(filePrj);
        filePrj.toFile().deleteOnExit();
        final Path fileTfw = (Path) SupportFiles.changeExtension(file, "pgw");
        Files.createFile(fileTfw);
        fileTfw.toFile().deleteOnExit();

        final BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        ImageIO.write(img, "png", file.toFile());

        final StorageConnector cnx = new VerifiableStorageConnector("FileCoverage on World-file", file);

        final FileCoverageProvider provider = FileCoverageProvider.provider();

        final ProbeResult result = provider.probeContent(cnx);
        Assert.assertEquals(true, result.isSupported());
        cnx.closeAllExcept(null);
    }

    /**
     * Ensure the provider probeContent do not match for simple image files
     * without *.prj or *.tfw.
     *
     */
    @Test
    public void testProbeContentNotWorldFile() throws IOException, DataStoreException {

        final Path file = Files.createTempFile("nogeo", ".png");
        file.toFile().deleteOnExit();

        final BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        ImageIO.write(img, "png", file.toFile());

        final StorageConnector cnx = new VerifiableStorageConnector("FileCoverage on single image", file);

        final FileCoverageProvider provider = FileCoverageProvider.provider();

        final ProbeResult result = provider.probeContent(cnx);
        Assert.assertEquals(false, result.isSupported());
        cnx.closeAllExcept(null);
    }

    /**
     * Verify that probing on an unsupported file format wont corrupt source storage connector
     */
    @Test
    public void probeUnrelatedContent() throws Exception {
        final Path target = Files.createTempFile("unrelated", ".shp");
        try {
            Files.write(target, Arrays.asList("Lorem ipsum", "etc.", "don't want empty file", "end"));

            final StorageConnector cnx = new VerifiableStorageConnector("FileCoverage on unrelated file", target);
            FileCoverageProvider.provider().probeContent(cnx);
            cnx.closeAllExcept(null);
        } finally {
            Files.delete(target);
        }
    }

    @Test
    public void probeAllImageSpis() throws Exception {
        final Path randomData = Files.createTempFile("allSpis", "");
        try {
            final byte[] data = new byte[1024];
            new Random().nextBytes(data);
            Files.write(randomData, data);
            final VerifiableStorageConnector connector = new VerifiableStorageConnector("Probe All Image SPIs", randomData);
            final ImageInputStream iim = connector.getStorageAs(ImageInputStream.class);
            assumeFalse("Image input stream needed.", iim == null);
            for (ImageReaderSpi spi : FileCoverageProvider.SPIS.keySet()) {
                try {
                    spi.canDecodeInput(iim);
                    connector.verifyAll();
                    for (Class type : spi.getInputTypes()) {
                        try {
                            final Object storage = connector.getStorageAs(type);
                            if (storage != null) {
                                spi.canDecodeInput(storage);
                                connector.verifyAll();
                            }
                        } catch (UnconvertibleObjectException e) {
                            // Ignore
                        }
                    }
                } catch (AssertionError e) {
                    throw new AssertionError("SPI: " + spi, e);
                }
            }
        } finally {
            Files.delete(randomData);
        }
    }

    @Test
    public void no_error_raised_by_third_party_file_system() throws Exception {
        final Path file = new MockPath(Paths.get("target", "wf.png"));
        final ProbeResult probe = FileCoverageProvider.provider().probeContent(new StorageConnector(file));
        assertNotNull(probe);
    }

    /**
     * Simulate path provided by third-party file-systems (Ex: HDFS), that throw errors when trying to call toFile().
     */
    private static class MockPath implements Path {

        final Path locationSimulator;

        private MockPath(Path locationSimulator) {
            this.locationSimulator = locationSimulator;
        }

        @Override
        public FileSystem getFileSystem() {
            return new FileSystem() {
                @Override
                public FileSystemProvider provider() {
                    return new FileSystemProvider() {
                        @Override
                        public String getScheme() { return "mock"; }

                        @Override
                        public FileSystem newFileSystem(URI uri, Map<String, ?> env) throws IOException {
                            throw new UnsupportedOperationException("Not supported yet");
                        }

                        @Override
                        public FileSystem getFileSystem(URI uri) {
                            throw new UnsupportedOperationException("Not supported yet");
                        }

                        @Override
                        public Path getPath(URI uri) {
                            throw new UnsupportedOperationException("Not supported yet");
                        }

                        @Override
                        public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
                            return new SeekableByteChannel() {
                                @Override
                                public int read(ByteBuffer dst) throws IOException {
                                    return -1;
                                }

                                @Override
                                public int write(ByteBuffer src) throws IOException {
                                    throw new UnsupportedOperationException("Not supported yet");
                                }

                                @Override
                                public long position() throws IOException { return 0; }

                                @Override
                                public SeekableByteChannel position(long newPosition) throws IOException { return this; }

                                @Override
                                public long size() throws IOException { return 0; }

                                @Override
                                public SeekableByteChannel truncate(long size) throws IOException { return this; }

                                @Override
                                public boolean isOpen() { return true; }

                                @Override
                                public void close() throws IOException { }
                            };
                        }

                        @Override
                        public DirectoryStream<Path> newDirectoryStream(Path dir, DirectoryStream.Filter<? super Path> filter) throws IOException {
                            throw new UnsupportedOperationException("Not supported yet");
                        }

                        @Override
                        public void createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException {
                            throw new UnsupportedOperationException("Not supported yet");
                        }

                        @Override
                        public void delete(Path path) throws IOException {
                            throw new UnsupportedOperationException("Not supported yet");
                        }

                        @Override
                        public void copy(Path source, Path target, CopyOption... options) throws IOException {
                            throw new UnsupportedOperationException("Not supported yet");
                        }

                        @Override
                        public void move(Path source, Path target, CopyOption... options) throws IOException {
                            throw new UnsupportedOperationException("Not supported yet");
                        }

                        @Override
                        public boolean isSameFile(Path path, Path path2) throws IOException {
                            throw new UnsupportedOperationException("Not supported yet");
                        }

                        @Override
                        public boolean isHidden(Path path) throws IOException {
                            throw new UnsupportedOperationException("Not supported yet");
                        }

                        @Override
                        public FileStore getFileStore(Path path) throws IOException {
                            throw new UnsupportedOperationException("Not supported yet");
                        }

                        @Override
                        public void checkAccess(Path path, AccessMode... modes) throws IOException {
                            throw new UnsupportedOperationException("Not supported yet");
                        }

                        @Override
                        public <V extends FileAttributeView> V getFileAttributeView(Path path, Class<V> type, LinkOption... options) {
                            throw new UnsupportedOperationException("Not supported yet");
                        }

                        @Override
                        public <A extends BasicFileAttributes> A readAttributes(Path path, Class<A> type, LinkOption... options) throws IOException {
                            throw new UnsupportedEncodingException();
                        }

                        @Override
                        public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) throws IOException {
                            throw new UnsupportedOperationException("Not supported yet");
                        }

                        @Override
                        public void setAttribute(Path path, String attribute, Object value, LinkOption... options) throws IOException {
                            throw new UnsupportedOperationException("Not supported yet");
                        }
                    };
                }

                @Override
                public void close() throws IOException { }

                @Override
                public boolean isOpen() { return true; }

                @Override
                public boolean isReadOnly() { return true; }

                @Override
                public String getSeparator() { return File.separator; }

                @Override
                public Iterable<Path> getRootDirectories() {
                    return new MockPath(FileSystems.getDefault().getRootDirectories().iterator().next());
                }

                @Override
                public Iterable<FileStore> getFileStores() { return Collections.EMPTY_LIST; }

                @Override
                public Set<String> supportedFileAttributeViews() {
                    throw new UnsupportedOperationException("Not supported yet");
                }

                @Override
                public Path getPath(String first, String... more) {
                    throw new UnsupportedOperationException("Not supported yet");
                }

                @Override
                public PathMatcher getPathMatcher(String syntaxAndPattern) {
                    throw new UnsupportedOperationException("Not supported yet");
                }

                @Override
                public UserPrincipalLookupService getUserPrincipalLookupService() {
                    throw new UnsupportedOperationException("Not supported yet");
                }

                @Override
                public WatchService newWatchService() throws IOException {
                    throw new UnsupportedOperationException("Not supported yet");
                }
            };
        }

        @Override
        public boolean isAbsolute() {
            throw new UnsupportedOperationException("Not supported yet");
        }

        @Override
        public Path getRoot() {
            throw new UnsupportedOperationException("Not supported yet");
        }

        @Override
        public Path getFileName() { return locationSimulator.getFileName(); }

        @Override
        public Path getParent() { return new MockPath(locationSimulator.getParent()); }

        @Override
        public int getNameCount() { return locationSimulator.getNameCount(); }

        @Override
        public Path getName(int index) {
            throw new UnsupportedOperationException("Not supported yet");
        }

        @Override
        public Path subpath(int beginIndex, int endIndex) {
            throw new UnsupportedOperationException("Not supported yet");
        }

        @Override
        public boolean startsWith(Path other) {
            throw new UnsupportedOperationException("Not supported yet");
        }

        @Override
        public boolean endsWith(Path other) {
            throw new UnsupportedOperationException("Not supported yet");
        }

        @Override
        public Path normalize() {
            throw new UnsupportedOperationException("Not supported yet");
        }

        @Override
        public Path resolve(Path other) {
            throw new UnsupportedOperationException("Not supported yet");
        }

        @Override
        public Path relativize(Path other) {
            throw new UnsupportedOperationException("Not supported yet");
        }

        @Override
        public URI toUri() {
            throw new UnsupportedOperationException("Not supported yet");
        }

        @Override
        public Path toAbsolutePath() {
            throw new UnsupportedOperationException("Not supported yet");
        }

        @Override
        public Path toRealPath(LinkOption... options) throws IOException {
            throw new UnsupportedOperationException("Not supported yet");
        }

        @Override
        public WatchKey register(WatchService watcher, WatchEvent.Kind<?>[] events, WatchEvent.Modifier... modifiers) throws IOException {
            throw new UnsupportedOperationException("Not supported yet");
        }

        @Override
        public int compareTo(Path other) {
            throw new UnsupportedOperationException("Not supported yet");
        }
    }
}
