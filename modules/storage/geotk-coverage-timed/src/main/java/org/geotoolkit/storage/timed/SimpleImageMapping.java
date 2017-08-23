/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017, Geomatys
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
package org.geotoolkit.storage.timed;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.collection.Cache;
import org.geotoolkit.index.tree.Tree;
import org.geotoolkit.index.tree.TreeElementMapper;
import org.opengis.geometry.Envelope;

/**
 * An RTRee identifier mapper composed of two files. First file contains list of
 * indexed image paths. The other contains a list of long. Each long is the start
 * position of a path in the path file.
 *
 * To find a path from its identifier, do : (identifier -1) * (Long.SIZE / Byte.SIZE). It gives you
 * a position in index file. At this position is the offset, in path file, where
 * is written the path you search for.
 *
 * @author Alexis Manin (Geomatys)
 */
public class SimpleImageMapping implements TreeElementMapper<Path> {

    /**
     * Name of the file to put image paths into.
     */
    private static final String PATH_FILE_NAME = "image-paths";
    /**
     * Name of the file which will contain indices of paths.
     */
    private static final String ID_FILE_NAME = "image-identifiers";

    /**
     * A path whose serves solely for cutting image paths before writing them.
     */
    private final Path relativizer;

    final Path pathFile;
    final SeekableByteChannel pathAccess;
    //final ByteBuffer pathBuffer;

    final Path indexFile;
    final SeekableByteChannel indexAccess;

    final Object lock;
    final AtomicBoolean closed;

    final Function<Path, Envelope> envelopeComputer;

    /**
     * Cache spatial informations of indexed files.
     */
    final Cache<Path, Envelope> envelopes = new Cache<>(20, 20, true);

    /**
     * Creates a mapper which will read/write index entries in given directory.
     * @param indexDir The folder in which this mapper will read and write.
     * @param relativizer A path to use for truncating image paths before writing
     * them. It's an optional (can be null) utiliy to write less characters.
     * @param envelopeComputer A function for computing an image envelope. The
     * resulting envelope must be compatible with the tree associated to current
     * mapper.
     * @throws IOException
     */
    public SimpleImageMapping(final Path indexDir, final Path relativizer, final Function<Path, Envelope> envelopeComputer) throws IOException {
        ArgumentChecks.ensureNonNull("Mapper file", indexDir);
        ArgumentChecks.ensureNonNull("Envelope computer", envelopeComputer);

        if (!Files.isDirectory(indexDir)) {
            Files.createDirectory(indexDir);
        }

        pathFile = indexDir.resolve(PATH_FILE_NAME);
        indexFile = indexDir.resolve(ID_FILE_NAME);

        if (!Files.exists(pathFile)) {
            Files.createFile(pathFile);
        }

        if (!Files.exists(indexFile)) {
            Files.createFile(indexFile);
        }

        pathAccess = Files.newByteChannel(pathFile, StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        indexAccess = Files.newByteChannel(indexFile, StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);

        lock = TimedUtils.acquireLock(pathFile);

        closed = new AtomicBoolean(false);

        this.relativizer = relativizer;
        this.envelopeComputer = envelopeComputer;
    }

    /**
     * Format a path into UTF-8 string. The aim is to prepare the character
     * sequence which will be written into the path file. To save characters, we
     * relativise path according to the root path given at built.
     *
     * Note : we make an UTF-8 version of the indexedd path. We put the length
     * of it to be able to re-read it easily.
     *
     * @param data Path to convert
     * @return a buffer whose first two bytes are the length of the UTF-8
     * string. Then the UTF-8 representation follows. The buffer position is set
     * to 0.
     */
    private ByteBuffer encodePath(final Path data) throws IllegalArgumentException {
        final String pathStr = relativizer == null ? data.toString() : relativizer.relativize(data).toString();
        final byte[] path = pathStr.getBytes(StandardCharsets.UTF_8);
        if (path.length > Short.MAX_VALUE) {
            throw new IllegalArgumentException("Given path is too long. UTF-8 representation in bytes must not be more than " + Short.MAX_VALUE);
        }
        final byte[] toWrite = new byte[path.length + 2];
        System.arraycopy(path, 0, toWrite, 2, path.length);
        ByteBuffer pathBuffer = ByteBuffer.wrap(toWrite);
        pathBuffer.putShort((short) path.length);
        pathBuffer.position(0);
        return pathBuffer;
    }

    private Path decodePath(final SeekableByteChannel input) throws IOException {
        final ByteBuffer buf = ByteBuffer.allocate(Short.BYTES);
        int read;
        do {
            read = input.read(buf);
        } while (read >=0 && buf.hasRemaining());
        if (buf.hasRemaining())
            throw new EOFException();
        buf.position(0);
        final short pathLength = buf.getShort();

        final ByteBuffer pathBuf = ByteBuffer.wrap(new byte[pathLength]);
        do {
            read = input.read(pathBuf);
        } while (read >=0 && pathBuf.hasRemaining());

        return decodePath(pathBuf.array());
    }

    /**
     * Build the path associated to the given UTF8 string.
     * @param utf8Data UTF8 representation of the path to decode.
     * @return The decoded path, resolved against {@link #relativizer}.
     */
    private Path decodePath(final byte[] utf8Data) {
        final String path = new String(utf8Data, StandardCharsets.UTF_8);
        return relativizer == null? Paths.get(path) : relativizer.resolve(path);
    }

    @Override
    public int getTreeIdentifier(Path object) throws IOException {
        ArgumentChecks.ensureNonNull("Path to find", object);
        synchronized (lock) {
            pathAccess.position(0);
            Path read;
            boolean found;
            long position;
            do {
                position = pathAccess.position();
                read = decodePath(pathAccess);
                found = object.equals(read);
            } while (!found && pathAccess.position() < pathAccess.size());

            if (found) {
                indexAccess.position(0);
                final ByteBuffer buf = ByteBuffer.allocate(Long.BYTES);
                int id = 0;
                found = false;
                do {
                    id++;
                    buf.position(0);
                    do {
                        indexAccess.read(buf);
                    } while (buf.hasRemaining());
                    buf.position(0);
                    found = position == buf.getLong();
                } while (!found && indexAccess.position() < indexAccess.size());

                if (found) {
                    return id;
                }
            }
        }

        throw new IOException("Cannot find any identifier for given path.");
    }

    @Override
    public void clear() throws IOException {}

    @Override
    public void flush() throws IOException {}

    @Override
    public void close() throws IOException {
        synchronized (lock) {
            try (final Closeable pathCloser = pathAccess; final Closeable idCloser = indexAccess) {
                closed.set(true);
            }
        }
    }

    @Override
    public boolean isClosed() {
        return closed.get();
    }

    @Override
    public Map<Integer, Path> getFullMap() throws IOException {
        throw new UnsupportedOperationException("Forbidden");
    }

    @Override
    public Envelope getEnvelope(Path object) throws IOException {
        return envelopeComputer.apply(object);
    }

    @Override
    public void setTreeIdentifier(Path object, int treeIdentifier) throws IOException {
        ByteBuffer pathBuffer = encodePath(object);
        final ByteBuffer idBuffer = ByteBuffer.allocate(Long.BYTES);

        synchronized (lock) {
            // append given path at the end of the index file.
            final long pathPosition = pathAccess.size();
            pathAccess.position(pathPosition);
            do {
                pathAccess.write(pathBuffer);
            } while (pathBuffer.hasRemaining());

            // Now, we must written the pointer to the path in identifier file.
            indexAccess.position(getIndexPosition(treeIdentifier));
            idBuffer.putLong(pathPosition);
            idBuffer.position(0);
            indexAccess.write(idBuffer);
        }
    }

    @Override
    public Path getObjectFromTreeIdentifier(int treeIdentifier) throws IOException {
        final long indexPos = getIndexPosition(treeIdentifier);
        ByteBuffer buf = ByteBuffer.allocate(Long.BYTES);

        synchronized (lock) {
            indexAccess.position(indexPos);
            do {
                indexAccess.read(buf);
            } while (buf.hasRemaining());
            buf.position(0);

            final long pathPosition = buf.getLong();
            pathAccess.position(pathPosition);
            return decodePath(pathAccess);
        }
    }

    /**
     * Get the position related to the given identifier in index file.
     *
     * @param treeIdentifier The {@link Tree} identifier to retrieve an element for.
     * @return the position in {@link #indexAccess} corresponding to the given id.
     */
    private static long getIndexPosition(final int treeIdentifier) {
        // Note : we apply -1 to the given identifier, because rtree ids starts at 1.
        return (treeIdentifier -1) * (long)(Long.SIZE / Byte.SIZE);
    }
}
