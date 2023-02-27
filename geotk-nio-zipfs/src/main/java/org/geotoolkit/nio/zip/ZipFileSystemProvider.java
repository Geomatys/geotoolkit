/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2023, Geomatys
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
package org.geotoolkit.nio.zip;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessMode;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.NotDirectoryException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.spi.FileSystemProvider;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.lingala.zip4j.io.inputstream.ZipInputStream;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;

/**
 * Zip filesytem using zip4j backend with reading and writing capabilities.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class ZipFileSystemProvider extends FileSystemProvider {

    static final String SCHEME = "zip";

    /**
     * All file systems created by this provider. Keys are uri paths.
     */
    private final Map<String, ZipFileSystem> fileSystems = new HashMap<>();

    @Override
    public String getScheme() {
        return SCHEME;
    }

    /**
     * Removes the given file system from the cache.
     * This method is invoked after the file system has been closed.
     */
    final void dispose(String identifier) {
        synchronized (fileSystems) {
            fileSystems.remove(identifier);
        }
    }

    @Override
    public FileSystem newFileSystem(URI uri, Map<String, ?> env) throws IOException {
        synchronized (fileSystems) {
            final String[] parts = splitParts(uri);
            final Path path;
            try {
                path = Paths.get(new URI(parts[0]));
            } catch (URISyntaxException ex) {
                throw new IOException("Invalid URI" + parts[0], ex);
            }
            final String identifier = parts[0];
            ZipFileSystem fs = fileSystems.get(identifier);
            if (fs != null) {
                throw new FileSystemAlreadyExistsException();
            }
            fs = new ZipFileSystem(this, path);
            fileSystems.put(identifier, fs);
            return fs;
        }
    }

    @Override
    public FileSystem getFileSystem(URI uri) {
        final String[] parts = splitParts(uri);
        final String identifier = parts[0];
        return getFileSystem(identifier);
    }

    private ZipFileSystem getFileSystem(String identifier) {
        synchronized (fileSystems) {
            final ZipFileSystem fs = fileSystems.get(identifier);
            if (fs != null) {
                return fs;
            }
            throw new FileSystemNotFoundException("Filesystem not found for uri " + identifier);
        }
    }

    @Override
    public Path getPath(URI uri) {
        final String[] parts = splitParts(uri);
        final Path path = Paths.get(parts[0]);
        final String identifier = path.toUri().toString();
        ZipFileSystem fileSystem;
        try {
            fileSystem = getFileSystem(identifier);
        } catch (FileSystemNotFoundException e) {
            try {
                fileSystem = (ZipFileSystem) newFileSystem(uri, null);
            } catch (IOException ex) {
                throw new IllegalStateException(ex);
            }
        }
        return fileSystem.getPath(parts[1]);
    }

    @Override
    public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
        final ZipPath zpath = castOrException(path);

        boolean read = true;
        boolean write = false;
        boolean create = true;
        boolean createNew = false;
        boolean truncate = false;
        for (OpenOption op : options) {
            if (StandardOpenOption.CREATE.equals(op)) {
                create = true;
            } else if (StandardOpenOption.CREATE_NEW.equals(op)) {
                createNew = true;
            } else if (StandardOpenOption.READ.equals(op)) {
                read = true;
            } else if (StandardOpenOption.WRITE.equals(op)) {
                write = true;
            } else if (StandardOpenOption.TRUNCATE_EXISTING.equals(op)) {
                truncate = true;
            } else if (StandardOpenOption.APPEND.equals(op)
                    || StandardOpenOption.DELETE_ON_CLOSE.equals(op)
                    || StandardOpenOption.DSYNC.equals(op)
                    || StandardOpenOption.SPARSE.equals(op)
                    || StandardOpenOption.SYNC.equals(op)) {
                throw new UnsupportedOperationException();
            } else {
                throw new UnsupportedOperationException();
            }
        }

        FileHeader header = zpath.getHeader();
        if (createNew) {
            //check the file do not exist
            if (header != null) {
                throw new FileAlreadyExistsException(zpath.getPath());
            }
        }

        if (!write) {
            if (header == null) {
                throw new FileNotFoundException(zpath.getPath());
            }
            final ZipInputStream input = zpath.fileSystem.store.getInputStream(header);
            return new ZipReadChannel(input, header.getUncompressedSize());
        } else {
            final Path temp = Files.createTempFile("zipfs", ".ze");
            if (header != null && !truncate) {
                //copy entry to a temporary file
                try (final ZipInputStream input = zpath.fileSystem.store.getInputStream(header)) {
                    Files.copy(input, temp, StandardCopyOption.REPLACE_EXISTING);
                }
            }

            final ZipParameters parameters = new ZipParameters();
            parameters.setCompressionLevel(CompressionLevel.NO_COMPRESSION);
            parameters.setCompressionMethod(CompressionMethod.STORE);
            parameters.setFileNameInZip(zpath.getZip4jPath());
            parameters.setOverrideExistingFilesInZip(true);
            return new ZipReadWriteChannel(zpath.fileSystem.store, parameters, temp);
        }
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(Path dir, DirectoryStream.Filter<? super Path> filter) throws IOException {
        final ZipPath zpath = castOrException(dir);
        if (!zpath.isAbsolute()) {
            throw new NotDirectoryException("Path is not absolute");
        }
        final String zip4jath = zpath.getZip4jPath();
        if (!zip4jath.endsWith(ZipFileSystem.SEPARATOR)) {
            throw new NotDirectoryException("Path is not a directory");
        }

        //expensive operation
        final List<FileHeader> allFiles = zpath.fileSystem.store.getFileHeaders();
        final Stream<Path> stream = allFiles.stream().filter(new Predicate<FileHeader>() {
            @Override
            public boolean test(FileHeader fileHeader) {
                final String fileName = fileHeader.getFileName();
                if (fileName.startsWith(zip4jath)) {
                    if (fileName.length() == zip4jath.length()) {
                        //same file
                        return false;
                    }
                    final int split = fileName.indexOf(ZipFileSystem.SEPARATOR, zip4jath.length()+1);
                    if (split < 0) {
                        //child file
                        return true;
                    } else if (split == fileName.length()-1) {
                        //child folder
                        return true;
                    } else {
                        return false;
                    }
                }
                return false;
            }
        }).map((FileHeader h) -> zpath.fileSystem.getPath(ZipFileSystem.SEPARATOR + h.getFileName()));
        return new DirectoryStream<Path>() {

            @Override
            public Iterator<Path> iterator() {
                return stream.iterator();
            }

            @Override
            public void close() throws IOException {
                stream.close();
            }
        };
    }

    @Override
    public void createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException {
        final ZipPath zpath = castOrException(dir);
        if (!zpath.isAbsolute()) {
            throw new NotDirectoryException("Path is not absolute");
        }
        final ZipParameters params = new ZipParameters();
        params.setCompressionMethod(CompressionMethod.STORE);
        params.setCompressionLevel(CompressionLevel.NO_COMPRESSION);
        params.setFileNameInZip(zpath.getZip4jPath());
        zpath.fileSystem.store.addStream(new ByteArrayInputStream(new byte[0]), params);
    }

    @Override
    public void delete(Path path) throws IOException {
        final ZipPath zpath = castOrException(path);
        final FileHeader header = zpath.getHeader();
        if (header == null) {
            throw new NoSuchFileException(zpath.getPath());
        } else if (header.isDirectory()) {
            //check it is empty
            try (final DirectoryStream<Path> stream = newDirectoryStream(path, null)) {
                if (!stream.iterator().hasNext()) {
                    throw new DirectoryNotEmptyException(zpath.getPath());
                }
            }
        }
        //delete it
        zpath.fileSystem.store.delete(header);
    }

    @Override
    public void copy(Path source, Path target, CopyOption... options) throws IOException {
        final ZipPath zsource = castOrException(source);
        final ZipPath ztarget = castOrException(target);

        if (ztarget.fileSystem == zsource.fileSystem) {
            zsource.fileSystem.store.copy(zsource.getZip4jPath(), ztarget.getZip4jPath());
        } else {
            throw new IOException("Not supported yet.");
        }
    }

    @Override
    public void move(Path source, Path target, CopyOption... options) throws IOException {
        final ZipPath zsource = castOrException(source);
        final ZipPath ztarget = castOrException(target);

        if (ztarget.fileSystem == zsource.fileSystem) {
            zsource.fileSystem.store.rename(zsource.getZip4jPath(), ztarget.getZip4jPath());
        } else {
            throw new IOException("Not supported yet.");
        }
    }

    @Override
    public boolean isSameFile(Path path1, Path path2) throws IOException {
        final ZipPath zpath1 = castOrException(path1);
        final ZipPath zpath2 = castOrException(path2);
        return zpath1.getPath().equals(zpath2.getPath());
    }

    @Override
    public boolean isHidden(Path path) throws IOException {
        return false;
    }

    @Override
    public FileStore getFileStore(Path path) throws IOException {
        final ZipPath zpath = castOrException(path);
        return zpath.fileSystem.store;
    }

    @Override
    public void checkAccess(Path path, AccessMode... modes) throws IOException {
        throw new IOException("Not supported yet.");
    }

    @Override
    public <V extends FileAttributeView> V getFileAttributeView(Path path, Class<V> type, LinkOption... options) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <A extends BasicFileAttributes> A readAttributes(Path path, Class<A> type, LinkOption... options) throws IOException {
        if (type.isAssignableFrom(BasicFileAttributes.class)) {
            final ZipPath zpath = castOrException(path);
            return type.cast(new ZipAttributes(zpath).readAttributes());
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) throws IOException {
        throw new IOException("Not supported yet.");
    }

    @Override
    public void setAttribute(Path path, String attribute, Object value, LinkOption... options) throws IOException {
        throw new IOException("Not supported yet.");
    }

    private static ZipPath castOrException(Path path) {
        if (path instanceof ZipPath) {
            return (ZipPath) path;
        } else {
            throw new IllegalArgumentException("Path is not from this provider");
        }
    }

    private static String[] splitParts(URI uri) {
        if (!SCHEME.equals(uri.getScheme())) {
            throw new IllegalArgumentException("Invalid uri schema");
        }
        String part = uri.getSchemeSpecificPart();
        int idx = part.indexOf("!");
        if (idx > 0) {
            return new String[]{part.substring(0, idx), part.substring(idx+1)};
        } else {
            return new String[]{part, "/"};
        }
    }

}
