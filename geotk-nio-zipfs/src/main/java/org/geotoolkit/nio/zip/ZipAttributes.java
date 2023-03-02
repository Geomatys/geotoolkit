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
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import net.lingala.zip4j.model.FileHeader;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
final class ZipAttributes implements BasicFileAttributeView {
    /**
     * The name of this set of attributes.
     * We currently support only the basic set.
     */
    static final String NAME = "basic";

    /**
     * The path for which to provide attributes.
     */
    private final ZipPath path;

    /**
     * Creates a new set of attributes.
     */
    ZipAttributes(final ZipPath path) {
        this.path = path;
    }

    /**
     * Returns the name of this attribute view, which is fixed to {@value #NAME}.
     */
    @Override
    public String name() {
        return NAME;
    }

    @Override
    public BasicFileAttributes readAttributes() throws IOException {
        return new ZipFileAttributes(path.getHeader());
    }

    @Override
    public void setTimes(FileTime lastModifiedTime, FileTime lastAccessTime, FileTime createTime) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private static final class ZipFileAttributes implements BasicFileAttributes {

        private final FileHeader header;
        private final Instant lastModifiedTime;

        ZipFileAttributes(final FileHeader header) {
            this.header = header;
            this.lastModifiedTime = Instant.ofEpochMilli(header.getLastModifiedTime());
        }

        @Override
        public FileTime creationTime() {
            return lastModifiedTime();
        }

        @Override
        public FileTime lastModifiedTime() {
            return FileTime.from(lastModifiedTime);
        }

        @Override
        public FileTime lastAccessTime() {
            return lastModifiedTime();
        }

        @Override
        public boolean isRegularFile() {
            return !header.isDirectory();
        }

        @Override
        public boolean isDirectory() {
            return header.isDirectory();
        }

        @Override
        public boolean isSymbolicLink() {
            return false;
        }

        @Override
        public boolean isOther() {
            return false;
        }

        @Override
        public long size() {
            return header.getUncompressedSize();
        }

        @Override
        public Object fileKey() {
            return null;
        }
    }
}
