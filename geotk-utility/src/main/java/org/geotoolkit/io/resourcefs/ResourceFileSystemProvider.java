/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.io.resourcefs;

import java.io.IOException;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessMode;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.LinkOption;
import java.nio.file.NotDirectoryException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;

/**
 * A single file resource filesytem with reading capabilities.
 * Used to expose a resource which is normaly a file but located in a different
 * storage (database, archive, ...)
 *
 * @author Johann Sorel (Geomatys)
 */
public final class ResourceFileSystemProvider extends FileSystemProvider {

    public static final ResourceFileSystemProvider INSTANCE = new ResourceFileSystemProvider();

    static final String SCHEME = "resource";

    @Override
    public String getScheme() {
        return SCHEME;
    }

    public FileSystem newFileSystem(String fileName, Supplier<byte[]> dataSupplier) {
        return new ResourceFileSystem(this, fileName, dataSupplier);
    }

    @Override
    public FileSystem newFileSystem(URI uri, Map<String, ?> env) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public FileSystem getFileSystem(URI uri) {
        final String identifier = splitPart(uri);
        throw new FileSystemNotFoundException("Filesystem not found for uri " + identifier);
    }

    @Override
    public Path getPath(URI uri) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
        final ResourcePath zpath = castOrException(path);

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

        if (createNew) {
            throw new IOException("Creating file not supported");
        }

        if (!write) {
            final byte[] datas = zpath.fileSystem.store.dataSupplier.get();
            return new SeekableInMemoryByteChannel(datas);
        } else {
            throw new UnsupportedOperationException("Writing not supported");
        }
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(Path dir, DirectoryStream.Filter<? super Path> filter) throws IOException {
        final ResourcePath zpath = castOrException(dir);
        if (!zpath.isAbsolute()) {
            throw new NotDirectoryException("Path is not absolute");
        }

        return new DirectoryStream<Path>() {
            @Override
            public Iterator<Path> iterator() {
                return Collections.EMPTY_LIST.iterator();
            }

            @Override
            public void close() throws IOException {
                //do nothing
            }
        };
    }

    @Override
    public void createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException {
        throw new IOException("Create operation not supported");
    }

    @Override
    public void delete(Path path) throws IOException {
        throw new IOException("Delete operation not supported");
    }

    @Override
    public void copy(Path source, Path target, CopyOption... options) throws IOException {
        throw new IOException("Copy operation not supported");
    }

    @Override
    public void move(Path source, Path target, CopyOption... options) throws IOException {
        throw new IOException("Move operation not supported");
    }

    @Override
    public boolean isSameFile(Path path1, Path path2) throws IOException {
        final ResourcePath zpath1 = castOrException(path1);
        final ResourcePath zpath2 = castOrException(path2);
        return zpath1.getPath().equals(zpath2.getPath());
    }

    @Override
    public boolean isHidden(Path path) throws IOException {
        return false;
    }

    @Override
    public FileStore getFileStore(Path path) throws IOException {
        final ResourcePath zpath = castOrException(path);
        return zpath.fileSystem.store;
    }

    @Override
    public void checkAccess(Path path, AccessMode... modes) throws IOException {
        if (List.of(modes).contains(AccessMode.WRITE)) {
            throw new UnsupportedOperationException("Writing not supported");
        }
        castOrException(path);
    }

    @Override
    public <V extends FileAttributeView> V getFileAttributeView(Path path, Class<V> type, LinkOption... options) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public <A extends BasicFileAttributes> A readAttributes(Path path, Class<A> type, LinkOption... options) throws IOException {
        if (type.isAssignableFrom(BasicFileAttributes.class)) {
            final ResourcePath zpath = castOrException(path);
            return type.cast(new ResourceAttributes(zpath).readAttributes());
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

    private static ResourcePath castOrException(Path path) {
        if (path instanceof ResourcePath) {
            return (ResourcePath) path;
        } else {
            throw new IllegalArgumentException("Path is not from this provider");
        }
    }

    private static String splitPart(URI uri) {
        if (!SCHEME.equals(uri.getScheme())) {
            throw new IllegalArgumentException("Invalid uri schema");
        }
        return uri.getSchemeSpecificPart();
    }

}
