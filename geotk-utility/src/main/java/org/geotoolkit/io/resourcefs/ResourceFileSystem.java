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
import java.util.function.Supplier;
import org.apache.sis.util.ArgumentChecks;

/**
 * A single file resource file system.
 *
 * @author Johann Sorel (Geomatys)
 */
final class ResourceFileSystem extends FileSystem {

    static final String SEPARATOR = "/";

    final ResourceFileSystemProvider provider;
    final ResourceFileStore store;
    private boolean open = true;

    /**
     * @param provider not null
     * @param path zip archive path, not null
     */
    ResourceFileSystem(ResourceFileSystemProvider provider, String fileName, Supplier<byte[]> dataSupplier) {
        ArgumentChecks.ensureNonNull("provider", provider);
        ArgumentChecks.ensureNonNull("path", fileName);
        this.provider = provider;
        this.store = new ResourceFileStore(fileName, dataSupplier);
    }

    @Override
    public FileSystemProvider provider() {
        return provider;
    }

    @Override
    public synchronized void close() throws IOException {
        open = false;
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
        return Collections.singleton(ResourceAttributes.NAME);
    }

    @Override
    public Path getPath(String first, String... more) {
        final StringJoiner joiner = new StringJoiner(SEPARATOR, "","");
        joiner.add(first);
        for (String m : more) joiner.add(m);
        return getPath(joiner.toString());
    }

    Path getPath(String fullPath) {
        return new ResourcePath(this, fullPath);
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
