/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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

import java.io.*;
import java.net.*;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;

import org.geotoolkit.data.shapefile.ShapefileFeatureStoreFactory;

import static org.geotoolkit.data.shapefile.ShapefileFeatureStoreFactory.LOGGER;
import static org.geotoolkit.data.shapefile.lock.ShpFileType.QIX;
import static org.geotoolkit.data.shapefile.lock.ShpFileType.SHP;
import org.geotoolkit.index.quadtree.QuadTree;
import org.geotoolkit.index.quadtree.StoreException;
import org.geotoolkit.index.quadtree.fs.FileSystemIndexStore;
import org.apache.sis.util.collection.WeakHashSet;
import org.geotoolkit.nio.IOUtilities;

/**
 * The collection of all the files that are the shapefile and its metadata and
 * indices.
 *
 * <p>
 * This class has methods for performing actions on the files. Currently mainly
 * for obtaining read and write channels and streams. But in the future a move
 * method may be introduced.
 * </p>
 *
 * <p>
 * Note: The method that require locks (such as getInputStream()) will
 * automatically acquire locks and the javadocs should document how to release
 * the lock. Therefore the methods {@link #acquireRead(ShpFileType, FileReader)}
 * and {@link #acquireWrite(ShpFileType, FileWriter)}
 * </p>
 *
 * @author jesse
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class ShpFiles {

    /**
     * The uris for each type of file that is associated with the shapefile. The
     * key is the type of file
     */
    private final Map<ShpFileType, URI> uris = new EnumMap<>(ShpFileType.class);

    /**
     * A read/write lock, so that we can have concurrent readers
     */
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private final WeakHashSet<AccessManager> managers = new WeakHashSet<>(AccessManager.class);

    private final boolean loadQuadTree;

    /**
     * Searches for all the files and adds then to the map of files.
     *
     * @param path any one of the shapefile files
     * @throws IllegalArgumentException if the shapefile associated with file is not found
     */
    public ShpFiles(final Object path) throws IllegalArgumentException {
        this(path,false);
    }

    /**
     * Searches for all the files and adds then to the map of files.
     *
     * @param path any one of the shapefile files
     * @param loadQix If we should use quad-tree index on input files.
     */
    public ShpFiles(final Object path, final boolean loadQix) throws IllegalArgumentException {
        URI uri = null;

        if(path instanceof String){
            try {
                uri = URI.create(path.toString());
            } catch (IllegalArgumentException e) {
                uri = Paths.get(path.toString()).toUri();
            }
        }else if(path instanceof Path){

            uri = ((Path) path).toUri();

        }else if(path instanceof URI){

            uri = (URI) path;

        }else if(path instanceof File){

            uri = ((File) path).toURI();

        }else if(path instanceof URL){
            try {
                uri = ((URL) path).toURI();
            } catch (URISyntaxException ex) {
                throw new IllegalArgumentException(
                        "URL object can not be converted to a valid URI",ex);
            }
        }else{
            throw new IllegalArgumentException(
                    "Path object can not be converted to a valid URI : " +path);
        }

        loadQuadTree = loadQix;

        final String base = baseName(uri);
        if (base == null) {
            throw new IllegalArgumentException(uri.getPath()
                            + " is not one of the files types that is known to be associated with a shapefile");
        }

        //final String urlString = url.toExternalForm();
        final String uriString = uri.toString();
        final char lastChar = uriString.charAt(uriString.length()-1);
        final boolean upperCase = Character.isUpperCase(lastChar);

        //retrive all file uris associated with this shapefile
        for(final ShpFileType type : ShpFileType.values()) {

            final String extensionWithPeriod;
            if(upperCase){
                extensionWithPeriod = type.extensionWithPeriod.toUpperCase();
            }else{
                extensionWithPeriod = type.extensionWithPeriod.toLowerCase();
            }

            // TODO find a better way
            final URI newURI = URI.create(base + extensionWithPeriod);
            uris.put(type, newURI);
        }

        // if the files are local check each file to see if it exists
        // if not then search for a file of the same name but try all combinations of the
        // different cases that the extension can be made up of.
        // IE Shp, SHP, Shp, ShP etc...
        if( isWritable() ){
            for (final Entry<ShpFileType, URI> entry : uris.entrySet()) {
                if( !exists(entry.getKey()) ){
                    URI value = entry.getValue();

                    final Path candidate = findExistingFile(Paths.get(value));
                    if(candidate!=null){
                        uris.put(entry.getKey(), candidate.toUri());
                    }
                }
            }
        }

    }

    public AccessManager createLocker(){
        final AccessManager locker = new AccessManager(this);
        managers.add(locker);
        return locker;
    }

    void acquireReadLock(){
        readWriteLock.readLock().lock();
    }

    void releaseReadLock(){
        readWriteLock.readLock().unlock();
    }

    void acquireWriteLock(){
        readWriteLock.writeLock().lock();
    }

    void releaseWriteLock(){
        readWriteLock.writeLock().unlock();
    }

    /**
     * @return the URLs (in string form) of all the files for the shapefile datastore.
     */
    public Map<ShpFileType, String> getFileNames() {
        final Map<ShpFileType, String> result = new EnumMap<>(ShpFileType.class);

        for (final Entry<ShpFileType, URI> entry : uris.entrySet()) {
            result.put(entry.getKey(), entry.getValue().toString());
        }

        return result;
    }

    /**
     * Returns the string form of the url that identifies the file indicated by
     * the type parameter or null if it is known that the file does not exist.
     *
     * <p>
     * Note: a URL should NOT be constructed from the string instead the URL
     * should be obtained through calling one of the aquireLock methods.
     *
     * @param type
     *                indicates the type of file the caller is interested in.
     *
     * @return the string form of the url that identifies the file indicated by
     *         the type parameter or null if it is known that the file does not
     *         exist.
     */
    public String get(final ShpFileType type) {
        return uris.get(type).toString();
    }

    /**
     * Acquire a Path for read only purposes. It is recommended that get*Stream
     * or get*Channel methods are used when reading or writing to the file is
     * desired.
     *
     *
     * @see #getInputStream(ShpFileType, FileReader)
     * @see #getReadChannel(ShpFileType, FileReader)
     * @see #getWriteChannel(org.geotoolkit.data.shapefile.ShpFileType,
     * org.geotoolkit.data.shapefile.FileWriter)
     *
     * @param type the type of the file desired.
     * @return the Path type requested
     *
     * @throws IllegalArgumentException If the given shapefile type has no valid
     * URI associated.
     * @throws FileSystemNotFoundException The file system, identified by the
     * URI of the input shapefile type, does not exist and cannot be created
     * automatically, or the provider identified by the URI's scheme component
     * is not installed
     * @throws SecurityException if a security manager is installed and it
     * denies an unspecified permission to access the file system
     */
    public Path getPath(final ShpFileType type) {
        return Paths.get(getURI(type));
    }

    /**
     * Acquire a URL for read only purposes.  It is recommended that get*Stream or
     * get*Channel methods are used when reading or writing to the file is
     * desired.
     *
     *
     * @see #getInputStream(ShpFileType, FileReader)
     * @see #getReadChannel(ShpFileType, FileReader)
     * @see #getWriteChannel(org.geotoolkit.data.shapefile.ShpFileType, org.geotoolkit.data.shapefile.FileWriter)
     *
     * @param type
     *                the type of the file desired.
     * @return the URL to the file of the type requested
     */
    public URI getURI(final ShpFileType type) {
        return uris.get(type);
    }

    /**
     * Determine if the location of this shapefile is local or remote.
     *
     * @return true if local, false if remote
     */
    public boolean isWritable() {
        try {
            Path path = getPath(SHP);
            if (!Files.exists(path)) {

                /* If the file to test does not exist, we must ensure that it's
                 * first existing parent is writable, i.e we can create new data
                 * in it.
                 */
                if (path.getFileSystem().isReadOnly()) {
                    return false;
                }

                path = path.getParent();
                while (!(Files.exists(path) || path.equals(path.getRoot()))) {
                    path = path.getParent();
                }

            }
            return Files.isWritable(path);

        } catch (Exception e) {
            // if not a path, maybe it's a simple URL access for download. In all
            // case, we'll need NIO API for writing purpose.
            LOGGER.log(Level.FINE, "SHP URI cannot be converted to NIO Path.", e);
            return false;
        }
    }

    /**
     * Delete all the shapefile files.
     *
     * @throws IOException e If we failed deleting any of the files, or if the
     * store is read-only.
     */
    public void delete() throws IOException {
        if (!isWritable())
            throw new IOException("Read-only datastore");

        acquireWriteLock();
        try {
            for (URI uri : uris.values()) {
                Files.deleteIfExists(Paths.get(uri));
            }

        } finally {
            releaseWriteLock();
        }
    }

    /**
     * Opens a input stream for the indicated file.
     *
     * @param type
     *                the type of file to open the stream to.
     * @return an input stream
     *
     * @throws IOException
     *                 if a problem occurred opening the stream.
     */
    public InputStream getInputStream(final ShpFileType type) throws IOException {
            return IOUtilities.open(getURI(type));
    }

    /**
     * Opens a output stream for the indicated file.
     *
     * @param type
     *                the type of file to open the stream to.
     * @return an output stream
     *
     * @throws IOException
     *                 if a problem occurred opening the stream.
     */
    public OutputStream getOutputStream(final ShpFileType type) throws IOException {
        return IOUtilities.openWrite(getURI(type), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
    }

    /**
     * Obtain a ReadableByteChannel from the given URL. If the url protocol is
     * file, a FileChannel will be returned. Otherwise a generic channel will be
     * obtained from the uris input stream.
     * <p>
     * A read lock is obtained when this method is called and released when the
     * channel is closed.
     * </p>
     *
     * @param type the type of file to open the channel to.
     * @return A read-only channel on the requested file.
     * @throws java.io.IOException If we cannot open a channel on input data.
     *
     */
    public ReadableByteChannel getReadChannel(final ShpFileType type) throws IOException {
        final URI uri = getURI(type);
        return getReadChannel(uri);
    }

    public ReadableByteChannel getReadChannel(final URI uri) throws IOException {
        return Files.newByteChannel(Paths.get(uri), StandardOpenOption.READ);
    }

    /**
     * Obtain a WritableByteChannel from the given URL. If the url protocol is
     * file, a FileChannel will be returned. Currently, this method will return
     * a generic channel for remote uris, however both shape and dbf writing can
     * only occur with a local FileChannel channel.
     *
     * <p>
     * A write lock is obtained when this method is called and released when the
     * channel is closed.
     * </p>
     *
     *
     * @param type
     *                the type of file to open the stream to.
     *
     * @return a WritableByteChannel for the provided file type
     *
     * @throws IOException
     *                 if there is an error opening the stream
     */
    public WritableByteChannel getWriteChannel(final ShpFileType type) throws IOException {
        return getWriteChannel(getURI(type));
    }

    public WritableByteChannel getWriteChannel(final URI uri) throws IOException {
        return Files.newByteChannel(Paths.get(uri));
    }

    public String getTypeName() {
        final String path = SHP.toBase(uris.get(SHP));
        final int slash = Math.max(0, path.lastIndexOf('/') + 1);

        return path.substring(slash, path.length());
    }

    /**
     * Returns true if the file exists.
     * Throws an exception if the file is not local.
     *
     * @param fileType the type of file to check existance for.
     * @return true if the file exists.
     * @throws IllegalArgumentException if the files are not local.
     */
    public boolean exists(final ShpFileType fileType) throws IllegalArgumentException {
        return exists(uris.get(fileType));
    }

    public boolean exists(final URI uri) throws IllegalArgumentException {
        if (uri == null) {
            return false;
        }

        try {
            return Files.exists(Paths.get(uri));
        } catch (Exception e) {
            // If we were not able to transform input into path, maybe it's an http URL.
            try {
                uri.toURL().openConnection().connect();
                return true;
            } catch (IOException e1) {
                e1.addSuppressed(e);
                LOGGER.log(Level.FINE, "Cannot connect to ".concat(uri.toString()), e1);
                return false;
            }
        }
    }


    ////////////////////////////////////////////////////////////////////////////
    /////////////// utils methods //////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    private static String baseName(final URI obj) {
        for(final ShpFileType type : ShpFileType.values()) {
            String base = type.toBase( (URI)obj );
            if (base != null) {
                return base;
            }
        }
        return null;
    }

    /**
     * Search for a file of the same name but try all combinations of the
     * different cases that the extension can be made up of.
     * exemple : Shp, SHP, Shp, ShP etc...
     */
    private static Path findExistingFile(final Path file) {
        final Path directory = file.getParent();
        if( directory==null || !Files.exists(directory) ) {
            // doesn't exist
            return null;
        }

        List<Path> matchingPaths = new ArrayList<>();
        final String baseFileName = file.getFileName().toString();
        try (final DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory)) {
            for (Path path : directoryStream) {
                final String filename = IOUtilities.filename(path);
                if (baseFileName.equalsIgnoreCase(filename)) {
                    matchingPaths.add(path);
                }
            }
        } catch (IOException e) {
            ShapefileFeatureStoreFactory.LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }

        if(!matchingPaths.isEmpty()){
            return matchingPaths.get(0);
        }
        return null;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Indexes files : store only one of each for all readers //////////////////
    ////////////////////////////////////////////////////////////////////////////

    private FileSystemIndexStore qixStore = null;
    private QuadTree quadTree = null;

    public synchronized void unloadIndexes(){
        if(quadTree != null){
            try {
                quadTree.close();
            } catch (StoreException ex) {
                LOGGER.log(Level.WARNING, "Failed to close quad tree.", ex);
                quadTree = null;
            }
        }
    }

    public synchronized QuadTree getQIX() throws StoreException{
        if(quadTree == null){

            if (!isWritable()) {
                return null;
            }
            final URI treeURI = getURI(QIX);

            try {
                final Path treePath = IOUtilities.toPath(treeURI);
                if (!Files.exists(treePath) || (Files.size(treePath) == 0)) {
                    return null;
                }

                if(qixStore == null){
                    qixStore = new FileSystemIndexStore(treePath);
                }

                if(loadQuadTree){
                    //we store the quad tree for reuse
                    quadTree = qixStore.load();
                    quadTree.loadAll();
                    return quadTree;
                }else{
                    return qixStore.load();
                }

            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, "Failed to get quad tree.", ex);
                return null;
            }
        }

        return quadTree;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("Shapefile URIs :");
        for (final Map.Entry<ShpFileType, URI> entry : uris.entrySet()) {
            builder.append(System.lineSeparator()).append(entry.getKey().name()).append(" -> ").append(entry.getValue());
        }

        return builder.toString();
    }
}
