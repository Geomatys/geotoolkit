/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.data.shapefile.lock;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.io.TableAppender;
import org.geotoolkit.data.dbf.DbaseFileReader;
import org.geotoolkit.data.shapefile.fix.IndexedFidReader;
import org.geotoolkit.data.shapefile.fix.IndexedFidWriter;
import org.geotoolkit.data.shapefile.indexed.RecordNumberTracker;
import org.geotoolkit.data.shapefile.shp.ShapefileReader;
import org.geotoolkit.data.shapefile.shx.ShxReader;
import org.geotoolkit.data.dbf.Closeable;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.logging.Logging;

/**
 * Manage reader and writer creation with proper read/write locks.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public final class AccessManager {

    private static final Logger LOGGER =  Logging.getLogger("org.geotoolkit.data.shapefile.lock");

    private static class AccessEntry{

        final ShpFileType type;
        final URI uri;
        final Closeable holder;

        public AccessEntry(ShpFileType type, URI uri, Closeable holder) {
            this.type = type;
            this.uri = uri;
            this.holder = holder;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append(type.name())
            .append("\t").append(!holder.isClosed())
            .append("\t").append(uri)
            .append("\t").append(holder);
            return sb.toString();
        }

    }

    private final ShpFiles files;
    private final List<AccessEntry> readEntries = new ArrayList<>();
    private final List<AccessEntry> writeEntries = new ArrayList<>();
    private final List<StorageFile> tempFiles = new ArrayList<>();

    /**
     * Can only be created by a shpFiles object.
     * @param files
     */
    AccessManager(final ShpFiles files){
        this.files = files;
    }

    private void getReadLock(){
        files.acquireReadLock();
    }

    private void getWriteLock(){
        files.acquireWriteLock();
    }

    private void releaseReadLock(){
        files.releaseReadLock();
    }

    private void releaseWriteLock(){
        files.releaseWriteLock();
    }


    public DbaseFileReader getDBFReader(final boolean memoryMapped, final Charset set) throws IOException{

        final URI uri = files.getURI(ShpFileType.DBF);

        if (uri == null) {
            return null;
        }

        if (files.isWritable() && !files.exists(ShpFileType.DBF)) {
            return null;
        }

        final ReadableByteChannel rbc = toClosingChannel(files.getReadChannel(uri),false);
        final DbaseFileReader reader = new DbaseFileReader(rbc, memoryMapped, set);
        readEntries.add(new AccessEntry(ShpFileType.DBF, uri, reader));
        return reader;
    }

    public ShapefileReader getSHPReader(final boolean strict, final boolean memoryMapped,
            final boolean read3D, final double[] resample) throws IOException, DataStoreException{

        final URI shpUrl = files.getURI(ShpFileType.SHP);
        final ReadableByteChannel shpChannel = toClosingChannel(files.getReadChannel(shpUrl),false);
        final URI shxUrl = files.getURI(ShpFileType.SHX);
        final ReadableByteChannel shxChannel;
        if (shxUrl == null || (files.isWritable() && !files.exists(shxUrl)) ) {
            //shx does not exist
            shxChannel = null;
        }else{
            shxChannel = toClosingChannel(files.getReadChannel(shxUrl),false);
        }

        final ShapefileReader shpReader = new ShapefileReader(
                shpChannel,shxChannel,strict,memoryMapped,read3D,resample);
        readEntries.add(new AccessEntry(ShpFileType.SHP, shpUrl, shpReader));
        readEntries.add(new AccessEntry(ShpFileType.SHX, shxUrl, shpReader));
        return shpReader;
    }

    public ShxReader getSHXReader(final boolean memoryMapped) throws IOException {
        final URI shxUrl = files.getURI(ShpFileType.SHX);
        if (shxUrl == null) {
            return null;
        }

        if (files.isWritable() && !files.exists(shxUrl)) {
            return null;
        }

        final ReadableByteChannel shxChannel = toClosingChannel(files.getReadChannel(shxUrl),false);
        final ShxReader reader = new ShxReader(shxChannel, memoryMapped);
        readEntries.add(new AccessEntry(ShpFileType.SHX, shxUrl, reader));
        return reader;
    }

    public IndexedFidReader getFIXReader(final RecordNumberTracker tracker) throws IOException{
        final URI url = files.getURI(ShpFileType.FIX);
        final ReadableByteChannel rbc = toClosingChannel(files.getReadChannel(url),false);
        final IndexedFidReader reader = new IndexedFidReader(url,rbc, tracker);
        readEntries.add(new AccessEntry(ShpFileType.FIX, url, reader));
        return reader;
    }

    public IndexedFidWriter getFIXWriter(final StorageFile storage) throws IOException{
        if (!files.isWritable()) {
            throw new IllegalArgumentException(
                    "Currently only local files are supported for writing");
        }

        final URI url = files.getURI(ShpFileType.FIX);
        ReadableByteChannel rbc = null;
        try {
            rbc = toClosingChannel(files.getReadChannel(url),true);
        } catch (FileNotFoundException | NoSuchFileException e) {
            rbc = storage.getWriteChannel();
        }

        final IndexedFidWriter writer = new IndexedFidWriter(
                url,rbc,storage.getWriteChannel());
        writeEntries.add(new AccessEntry(ShpFileType.FIX, url, writer));
        return writer;
    }

    /**
     * Obtains a Storage file for the type indicated. An id is provided so that
     * the same file can be obtained at a later time with just the id
     *
     * @param type the type of file to create and return
     *
     * @return StorageFile
     * @throws IOException if temporary files cannot be created
     */
    public StorageFile getStorageFile(final ShpFileType type) throws IOException {
        String baseName = files.getTypeName();
        if (baseName.length() < 3) { // min prefix length for createTempFile
            baseName = baseName + "___".substring(0, 3 - baseName.length());
        }
        final Path tmp = Files.createTempFile(baseName, type.extensionWithPeriod);
        final StorageFile tempFile = new StorageFile(files, tmp, type);
        tempFiles.add(tempFile);
        return tempFile;
    }

    /**
     * Close all readers and writers.
     */
    public void disposeReaderAndWriters(){
        for(final AccessEntry entry : writeEntries){
            try {
                entry.holder.close();
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, "Failed to close writer : "+entry.holder, ex);
            }
        }
        writeEntries.clear();

        for(final AccessEntry entry : readEntries){
            try {
                entry.holder.close();
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, "Failed to close reader : "+entry.holder, ex);
            }
        }
        readEntries.clear();

    }

    public synchronized void replaceStorageFiles() throws IOException{
        replaceStorageFiles(null);
    }

    /**
     * Aquiere a write lock and replace all storage files.
     * At this step all readers and writers must have been closed.
     */
    public synchronized void replaceStorageFiles(Runnable postReplaceRunnable) throws IOException{

        if(!allRWClosed()){
            throw new IOException("Can not replace files while readers or writers are still open :\n"+this.toString());
        }

        getWriteLock();
        try{
            final StorageFile[] files = tempFiles.toArray(new StorageFile[tempFiles.size()]);
            StorageFile.replaceOriginals(files);
        }finally{
            tempFiles.clear();
            //whatever happens we release the lock
            releaseWriteLock();
        }

        if(postReplaceRunnable!=null){
            postReplaceRunnable.run();
        }

    }

    private boolean allRWClosed(){
        boolean cleanState = true;
        for(final AccessEntry entry : readEntries){
            cleanState &= entry.holder.isClosed();
        }
        for(final AccessEntry entry : writeEntries){
            cleanState &= entry.holder.isClosed();
        }
        return cleanState;
    }

    /**
     * Will close all created readers and writers and release any lock.
     */
    public void dispose(){

        if(!tempFiles.isEmpty()){
            LOGGER.log(Level.WARNING, "Disposing manager with temporary files remaining.");
        }

        for(final AccessEntry entry : writeEntries){
            try {
                entry.holder.close();
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, "Failed to close writer : "+entry.holder, ex);
            }
        }

        for(final AccessEntry entry : readEntries){
            try {
                entry.holder.close();
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, "Failed to close reader : "+entry.holder, ex);
            }
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        try{
            final StringWriter writer = new StringWriter();
            final TableAppender tb = new TableAppender(writer);
            tb.appendHorizontalSeparator();
            tb.append("type\topen\tpath\tholder\n");
            tb.appendHorizontalSeparator();

            tb.append("Reading\n");
            for(final AccessEntry entry : readEntries){
                tb.append(entry.toString());
                tb.append('\n');
            }

            tb.appendHorizontalSeparator();

            tb.append("Writing\n");
            for(final AccessEntry entry : writeEntries){
                tb.append(entry.toString());
                tb.append('\n');
            }
            tb.appendHorizontalSeparator();
            tb.flush();
            sb.append(writer.getBuffer().toString()).append("\n");

        }catch(IOException ex){
            //will not happen
        }

        return sb.toString();
    }

    @Override
    protected void finalize() throws Throwable {

        if(!allRWClosed()){
            throw new IOException("Access Manager has not been closed in proper state, readers or writers are still open :\n"+this.toString());
        }

    }

    private ReadableByteChannel toClosingChannel(final ReadableByteChannel channel, final boolean writing){
        if(channel instanceof ClosingFileChannel || channel instanceof ClosingReadableByteChannel){
            throw new RuntimeException("Wrapping an already auto closing channel.");
        }

        if(channel instanceof FileChannel){
            return new ClosingFileChannel((FileChannel)channel, writing);
        }else{
            return new ClosingReadableByteChannel(channel);
        }
    }

    private final class ClosingReadableByteChannel implements ReadableByteChannel{

        private final ReadableByteChannel wrapped;

        private ClosingReadableByteChannel(final ReadableByteChannel wrapped) {
            this.wrapped = wrapped;
            getReadLock();
        }

        @Override
        public int read(final ByteBuffer dst) throws IOException {
            return wrapped.read(dst);
        }

        @Override
        public boolean isOpen() {
            return wrapped.isOpen();
        }

        @Override
        public void close() throws IOException {
            wrapped.close();
            releaseReadLock();
        }

    }

    private final class ClosingFileChannel extends FileChannel implements ReadableByteChannel{

        private final FileChannel wrapped;
        private final boolean write;
        private boolean closed;

        private ClosingFileChannel(final FileChannel channel, final boolean write) {
            this.wrapped = channel;
            this.closed = false;
            this.write = write;
            if(!write){
                getReadLock();
            }
        }

        @Override
        public void force(final boolean metaData) throws IOException {
            wrapped.force(metaData);
        }

        @Override
        public FileLock lock(final long position, final long size, final boolean shared)
                throws IOException {
            return wrapped.lock(position, size, shared);
        }

        @Override
        public MappedByteBuffer map(final MapMode mode, final long position, final long size)
                throws IOException {
            return wrapped.map(mode, position, size);
        }

        @Override
        public long position() throws IOException {
            return wrapped.position();
        }

        @Override
        public FileChannel position(final long newPosition) throws IOException {
            return wrapped.position(newPosition);
        }

        @Override
        public int read(final ByteBuffer dst, final long position) throws IOException {
            return wrapped.read(dst, position);
        }

        @Override
        public int read(final ByteBuffer dst) throws IOException {
            return wrapped.read(dst);
        }

        @Override
        public long read(final ByteBuffer[] dsts, final int offset, final int length)
                throws IOException {
            return wrapped.read(dsts, offset, length);
        }

        @Override
        public long size() throws IOException {
            return wrapped.size();
        }

        @Override
        public long transferFrom(final ReadableByteChannel src, final long position, final long count)
                throws IOException {
            return wrapped.transferFrom(src, position, count);
        }

        @Override
        public long transferTo(final long position, final long count, final WritableByteChannel target)
                throws IOException {
            return wrapped.transferTo(position, count, target);
        }

        @Override
        public FileChannel truncate(final long size) throws IOException {
            return wrapped.truncate(size);
        }

        @Override
        public FileLock tryLock(final long position, final long size, final boolean shared)
                throws IOException {
            return wrapped.tryLock(position, size, shared);
        }

        @Override
        public int write(final ByteBuffer src, final long position) throws IOException {
            return wrapped.write(src, position);
        }

        @Override
        public int write(final ByteBuffer src) throws IOException {
            return wrapped.write(src);
        }

        @Override
        public long write(final ByteBuffer[] srcs, final int offset, final int length)
                throws IOException {
            return wrapped.write(srcs, offset, length);
        }

        @Override
        protected void implCloseChannel() throws IOException {
            try {
                wrapped.close();
            } finally {
                if (!closed) {
                    closed = true;
                    if (!write) {
                        releaseReadLock();
                    }
                }
            }

        }
    }

}
