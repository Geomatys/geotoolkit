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
package org.geotoolkit.storage.uri;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.sis.util.ArgumentChecks;

/**
 * URI resolver which support :
 * - local URI
 * - local zip URI with pattern file:/archive.zip!/file.ext
 * - local zip URI with jar pattern jar:file:/archive.zip!/file.ext
 *
 * @author Johann Sorel (Geomatys)
 */
public final class URIResolver implements AutoCloseable {

    private final URI reference;
    private final boolean isZip;
    //first evaluated when needed
    private Boolean isOnFileSystem;
    private FileSystem fileSystem;

    /**
     * Create a resolver for URI which will be children of the reference.
     *
     * @param reference not null
     */
    public URIResolver(URI reference) {
        ArgumentChecks.ensureNonNull("reference", reference);
        String str = reference.toString();

        //see if we are dealing with a jar path and remove it
        if (str.startsWith("jar:")) {
            str = str.substring(4);
        }

        final int zipIdx = str.toLowerCase().indexOf(".zip");
        isZip = zipIdx > 0;
        if (isZip) {
            try {
                reference = format(new URI(str.substring(0, zipIdx+4)));
            } catch (URISyntaxException ex) {
                throw new IllegalArgumentException("Invalid URI " + str);
            }
        }
        this.reference = reference;
    }

    /**
     * This method could be synchronized but causes a lot of locks.
     * We are better of recomputing the value a few times in the worst case.
     *
     * @return Path if uri is on the filesytem, null otherwise
     */
    public Path toPath(URI uri) {

        //resolve the reference first
        if (this.isOnFileSystem == null) {
            synchronized (this) {
                Path rootPath;
                try {
                    rootPath = Paths.get(reference);
                    this.isOnFileSystem = Boolean.TRUE;
                } catch (FileSystemNotFoundException | SecurityException | IllegalArgumentException ex) {
                    return null;
                }

                if (isOnFileSystem && isZip) {
                    if (fileSystem == null) {
                        try {
                            fileSystem = FileSystems.newFileSystem(rootPath);
                        } catch (IOException ex) {
                            throw new IllegalStateException("Failed to open zip filesystem on " + reference);
                        }
                    }
                }
            }
        }

        final Boolean isOnFs = this.isOnFileSystem;
        if (isOnFs) {
            if (fileSystem != null) {
                try {
                    uri = format(uri);
                } catch (URISyntaxException ex) {
                    throw new IllegalArgumentException("Incorrect URI " + uri, ex);
                }
                final String uriPath = uri.toString();
                final String refPath = reference.toString();
                if (!uriPath.startsWith(refPath)) {
                    throw new IllegalArgumentException(uriPath + "is not a children of " + refPath);
                }
                String zipPath = uriPath.substring(refPath.length());
                if (!zipPath.startsWith("!/")) {
                    throw new IllegalArgumentException(uriPath + "is incorrect, should have the pattern archive.zip!/file.ext");
                }
                return fileSystem.getPath(zipPath.substring(2));
            } else {
                try {
                    final Path path = Paths.get(uri);
                    this.isOnFileSystem = Boolean.TRUE;
                    return path;
                } catch (FileSystemNotFoundException | SecurityException | IllegalArgumentException ex) {
                    return null;
                }
            }
        }
        return null;
    }

    @Override
    public void close() throws Exception {
        if (fileSystem != null) {
            fileSystem.close();
        }
    }

    private static URI format(URI uri) throws URISyntaxException {
        String str = uri.toString();
        //see if we are dealing with a jar path and remove it
        if (str.startsWith("jar:")) {
            str = str.substring(4);
            uri = new URI(str);
        }
        //removes any duplicated / or empty elements in the uri
        return new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), uri.getPath(), uri.getQuery(), uri.getFragment());
    }

}
