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
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.StringJoiner;
import org.apache.sis.util.ArgumentChecks;

/**
 * In memory Path.
 *
 * @author Johann Sorel (Geomatys)
 */
final class ResourcePath implements Path {

    final ResourceFileSystem fileSystem;
    private final String path;

    //computed when needed
    private String[] elements;
    private boolean isAbsolute;
    private boolean isRoot;

    /**
     * @param fileSystem path file system, not null
     * @param path path, not null
     */
    ResourcePath(ResourceFileSystem fileSystem, String path) {
        ArgumentChecks.ensureNonNull("fileSystem", fileSystem);
        ArgumentChecks.ensureNonNull("path", path);
        this.fileSystem = fileSystem;
        this.path = path;
    }

    String getPath() {
        return path;
    }

    @Override
    public FileSystem getFileSystem() {
        return this.fileSystem;
    }

    @Override
    public boolean isAbsolute() {
        prepare();
        return isAbsolute;
    }

    @Override
    public Path getRoot() {
        prepare();
        if (isRoot) {
            return this;
        } else if (isAbsolute) {
            return new ResourcePath(fileSystem, ResourceFileSystem.SEPARATOR);
        } else {
            return null;
        }
    }

    @Override
    public Path getFileName() {
        prepare();
        return elements.length == 0 ? null : getName(elements.length - 1);
    }

    @Override
    public Path getParent() {
        prepare();
        return elements.length == 0 ? null : subpath(0, elements.length - 1);
    }

    @Override
    public int getNameCount() {
        prepare();
        return elements.length;
    }

    @Override
    public Path getName(int index) {
        prepare();
        return new ResourcePath(fileSystem, elements[index]);
    }

    @Override
    public Path subpath(int beginIndex, int endIndex) {
        prepare();
        final StringJoiner joiner = new StringJoiner(ResourceFileSystem.SEPARATOR);
        for (int i = beginIndex; i < endIndex; i++) joiner.add(elements[i]);
        return new ResourcePath(fileSystem, joiner.toString() + ResourceFileSystem.SEPARATOR);
    }

    @Override
    public boolean startsWith(Path other) {
        final ResourcePath zother = (ResourcePath) other;
        return this.path.startsWith(zother.path);
    }

    @Override
    public boolean endsWith(Path other) {
        final ResourcePath zother = (ResourcePath) other;
        return this.path.endsWith(zother.path);
    }

    @Override
    public Path normalize() {
        return this;
    }

    @Override
    public Path resolve(Path other) {
        if (other.isAbsolute()) {
            return other;
        }
        String part = other.normalize().toString();
        if (part.isEmpty()) {
            return this;
        }
        part = part.replace(other.getFileSystem().getSeparator(), fileSystem.getSeparator());
        if (path.endsWith(fileSystem.getSeparator()) || part.startsWith(fileSystem.getSeparator())) {
            part = path + part;
        } else {
            part = path + fileSystem.getSeparator() + part;
        }
        return new ResourcePath(fileSystem, part);
    }

    @Override
    public Path relativize(Path other) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public URI toUri() {
        prepare();
        if (isAbsolute) {
            try {
                return new URI(ResourceFileSystemProvider.SCHEME + ":" + fileSystem.store.fileName);
            } catch (URISyntaxException ex) {
                throw new IllegalStateException();
            }
        }
        return null;
    }

    @Override
    public Path toAbsolutePath() {
        prepare();
        if (isAbsolute) {
            return this;
        } else {
            throw new UnsupportedOperationException("Path can not be made absolute");
        }
    }

    @Override
    public Path toRealPath(LinkOption... options) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public WatchKey register(WatchService watcher, WatchEvent.Kind<?>[] events, WatchEvent.Modifier... modifiers) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int compareTo(Path other) {
        final ResourcePath zother = (ResourcePath) other;
        return this.path.compareTo(zother.path);
    }

    private void prepare() {
        if (elements != null) return;

        isAbsolute = path.startsWith(ResourceFileSystem.SEPARATOR);
        if (path.equals(ResourceFileSystem.SEPARATOR)) {
            isRoot = true;
            elements = new String[0];
        } else {
            String[] parts = path.split(ResourceFileSystem.SEPARATOR);
            isRoot = false;
            elements = parts;
        }
    }

    @Override
    public String toString() {
        return path;
    }
}
