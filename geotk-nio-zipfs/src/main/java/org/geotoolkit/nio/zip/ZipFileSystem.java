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

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.WatchService;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.StringJoiner;
import org.apache.sis.util.ArgumentChecks;

/**
 * Zip filesytem using zip4j backend with reading and writing capabilities.
 *
 * @author Johann Sorel (Geomatys)
 */
final class ZipFileSystem extends FileSystem {

    static final String SEPARATOR = "/";

    final ZipFileSystemProvider provider;
    final ZipFileStore store;
    private boolean open = true;

    /**
     * @param provider not null
     * @param path zip archive path, not null
     */
    ZipFileSystem(ZipFileSystemProvider provider, Path path) {
        ArgumentChecks.ensureNonNull("provider", provider);
        ArgumentChecks.ensureNonNull("path", path);
        this.provider = provider;
        this.store = new ZipFileStore(path);
    }

    @Override
    public FileSystemProvider provider() {
        return provider;
    }

    @Override
    public synchronized void close() throws IOException {
        open = false;
        store.close();
        provider.dispose(store.getFileURI().toString());
    }

    @Override
    public synchronized boolean isOpen() {
        return !open;
    }

    @Override
    public boolean isReadOnly() {
        return store.isReadOnly();
    }

    @Override
    public String getSeparator() {
        return SEPARATOR;
    }

    @Override
    public Iterable<Path> getRootDirectories() {
        return Collections.singletonList(getPath(SEPARATOR));
    }

    @Override
    public Iterable<FileStore> getFileStores() {
        return Arrays.asList(store);
    }

    @Override
    public Set<String> supportedFileAttributeViews() {
        return Collections.singleton(ZipAttributes.NAME);
    }

    @Override
    public Path getPath(String first, String... more) {
        final StringJoiner joiner = new StringJoiner(SEPARATOR, "","");
        joiner.add(first);
        for (String m : more) joiner.add(m);
        return getPath(joiner.toString());
    }

    Path getPath(String fullPath) {
        return new ZipPath(this, fullPath);
    }

    @Override
    public PathMatcher getPathMatcher(String syntaxAndPattern) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public UserPrincipalLookupService getUserPrincipalLookupService() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public WatchService newWatchService() throws IOException {
        throw new UnsupportedOperationException("Not supported.");
    }

}
