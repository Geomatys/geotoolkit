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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import net.lingala.zip4j.model.ZipParameters;
import org.apache.sis.util.ArgumentChecks;

/**
 * Emulate a writable SeekableByteChannel by using a temporary file for the zip entry.
 * Once the channel is closed, all the writen datas are pushed in the zip archive.
 *
 * @author Johann Sorel (Geomatys)
 */
final class ZipReadWriteChannel implements SeekableByteChannel {

    private final ZipFileStore zip;
    private final ZipParameters parameters;
    private final Path temp;
    private final SeekableByteChannel channel;

    /**
     * @param zip the zip archive, not null.
     * @param parameters the zip file entry to create or update, not null.
     * @param temp temporary file, will be deleted on closing, not null.
     * @throws IOException if temporary file channel creation fails
     */
    ZipReadWriteChannel(ZipFileStore zip, ZipParameters parameters, Path temp) throws IOException {
        ArgumentChecks.ensureNonNull("zip", zip);
        ArgumentChecks.ensureNonNull("parameters", parameters);
        ArgumentChecks.ensureNonNull("temp", temp);
        this.zip = zip;
        this.parameters = parameters;
        this.temp = temp;
        this.channel = Files.newByteChannel(temp, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        return channel.read(dst);
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        return channel.write(src);
    }

    @Override
    public long position() throws IOException {
        return channel.position();
    }

    @Override
    public SeekableByteChannel position(long newPosition) throws IOException {
        channel.position(newPosition);
        return this;
    }

    @Override
    public long size() throws IOException {
        return channel.size();
    }

    @Override
    public SeekableByteChannel truncate(long size) throws IOException {
        channel.truncate(size);
        return this;
    }

    @Override
    public boolean isOpen() {
        return channel.isOpen();
    }

    /**
     * Push written datas in zip archive and delete temporary file.
     */
    @Override
    public void close() throws IOException {
        if (channel.isOpen()) {
            channel.close();
            try (InputStream input = new BufferedInputStream(Files.newInputStream(temp))) {
                zip.addStream(input, parameters);
            }
            Files.deleteIfExists(temp);
        }
    }
}
