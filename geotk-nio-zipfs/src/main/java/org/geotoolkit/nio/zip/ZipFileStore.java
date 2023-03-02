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
import java.io.InputStream;
import java.net.URI;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileStoreAttributeView;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.inputstream.ZipInputStream;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import org.apache.sis.util.ArgumentChecks;

/**
 * Zip FileStore implementation.
 *
 * @author Johann Sorel (Geomatys)
 */
final class ZipFileStore extends FileStore {

    private final ZipFile zip;
    private final Path path;
    private final boolean readOnly;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    ZipFileStore(Path path) {
        ArgumentChecks.ensureNonNull("path", path);
        this.zip = new ZipFile(path.toFile());
        this.path = path;
        readOnly = !Files.isWritable(path);
    }

    @Override
    public String name() {
        return zip.getFile().getName();
    }

    @Override
    public String type() {
        return "zip";
    }

    @Override
    public boolean isReadOnly() {
        return readOnly;
    }

    @Override
    public long getTotalSpace() throws IOException {
        return Files.size(path);
    }

    @Override
    public long getUsableSpace() throws IOException {
        return Long.MAX_VALUE;
    }

    @Override
    public long getUnallocatedSpace() throws IOException {
        return Long.MAX_VALUE;
    }

    @Override
    public boolean supportsFileAttributeView(Class<? extends FileAttributeView> type) {
        return BasicFileAttributeView.class.isAssignableFrom(type);
    }

    @Override
    public boolean supportsFileAttributeView(String name) {
        return ZipAttributes.NAME.equalsIgnoreCase(name);
    }

    @Override
    public <V extends FileStoreAttributeView> V getFileStoreAttributeView(Class<V> type) {
        return null;
    }

    @Override
    public Object getAttribute(String attribute) throws IOException {
        throw new UnsupportedOperationException();
    }

    FileHeader getFileHeader(String inzipPath) throws ZipException {
        lock.readLock().lock();
        FileHeader header;
        try {
            header = zip.getFileHeader(inzipPath);
        } finally {
            lock.readLock().unlock();
        }
        return header;
    }

    URI getFileURI() {
        return path.toUri();
    }

    void close() throws IOException {
        lock.writeLock().lock();
        try {
            zip.close();
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Method is synchronized because Zip4j do not support concurrent writing.
     * @param input not null
     * @param parameters not null
     * @throws ZipException
     */
    void addStream(InputStream input, ZipParameters parameters) throws ZipException {
        lock.writeLock().lock();
        try {
            zip.addStream(input, parameters);
        } finally {
            lock.writeLock().unlock();
        }
    }

    void rename(String source, String target) throws ZipException {
        lock.writeLock().lock();
        try {
            zip.renameFile(source, target);
        } finally {
            lock.writeLock().unlock();
        }
    }

    void copy(String source, String target) throws IOException {
        lock.writeLock().lock();
        try (InputStream in = zip.getInputStream(zip.getFileHeader(source))) {
            ZipParameters parameters = new ZipParameters();
            parameters.setFileNameInZip(target);
            parameters.setCompressionLevel(CompressionLevel.NO_COMPRESSION);
            parameters.setCompressionMethod(CompressionMethod.STORE);
            zip.addStream(in, parameters);
        } finally {
            lock.writeLock().unlock();
        }
    }

    void delete(FileHeader header) throws ZipException {
        lock.writeLock().lock();
        try {
            zip.removeFile(header);
        } finally {
            lock.writeLock().unlock();
        }
    }

    ZipInputStream getInputStream(FileHeader header) throws IOException {
        return zip.getInputStream(header);
    }

    List<FileHeader> getFileHeaders() throws ZipException {
        return zip.getFileHeaders();
    }
}
