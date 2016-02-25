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
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
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
import org.geotoolkit.gui.swing.tree.Trees;
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
     * @throws FileNotFoundException if the shapefile associated with file is not found
     */
    public ShpFiles(final Object path) throws IllegalArgumentException {
        this(path,false);
    }

    /**
     * Searches for all the files and adds then to the map of files.
     *
     * @param path any one of the shapefile files
     */
    public ShpFiles(final Object path, final boolean loadQix) throws IllegalArgumentException {
        URL url = null;

        if(path instanceof String){
            try {
                url = new URL(path.toString());
            } catch (MalformedURLException e) {
                try {
                    url = Paths.get(path.toString()).toUri().toURL();
                } catch (MalformedURLException ex) {
                    throw new IllegalArgumentException(
                            "Path object can not be converted to a valid URL",ex);
                }
            }
        }else if(path instanceof Path){
            try {
                url = ((Path) path).toUri().toURL();
            } catch (MalformedURLException ex) {
                throw new IllegalArgumentException(
                        "Path object can not be converted to a valid URL",ex);
            }
        }else if(path instanceof URI){
            try {
                url = ((URI) path).toURL();
            } catch (MalformedURLException ex) {
                throw new IllegalArgumentException(
                        "Path object can not be converted to a valid URL",ex);
            }
        }else if(path instanceof File){
            try {
                url = ((File) path).toURI().toURL();
            } catch (MalformedURLException ex) {
                throw new IllegalArgumentException(
                        "Path object can not be converted to a valid URL",ex);
            }
        }else if(path instanceof URL){
            url = (URL) path;
        }else{
            throw new IllegalArgumentException(
                    "Path object can not be converted to a valid URL : " +path);
        }

        loadQuadTree = loadQix;

        final String base = baseName(url);
        if (base == null) {
            throw new IllegalArgumentException(url.getPath()
                            + " is not one of the files types that is known to be associated with a shapefile");
        }

        final String urlString = url.toExternalForm();
        final char lastChar = urlString.charAt(urlString.length()-1);
        final boolean upperCase = Character.isUpperCase(lastChar);

        //retrive all file uris associated with this shapefile
        for(final ShpFileType type : ShpFileType.values()) {

            final String extensionWithPeriod;
            if(upperCase){
                extensionWithPeriod = type.extensionWithPeriod.toUpperCase();
            }else{
                extensionWithPeriod = type.extensionWithPeriod.toLowerCase();
            }

            final URL newURL;
            try {
                newURL = new URL(url, base+extensionWithPeriod);
                uris.put(type, newURL.toURI());
            } catch (URISyntaxException | MalformedURLException e) {
                // shouldn't happen because the starting url was constructable
                throw new RuntimeException(e);
            }

        }

        // if the files are local check each file to see if it exists
        // if not then search for a file of the same name but try all combinations of the
        // different cases that the extension can be made up of.
        // IE Shp, SHP, Shp, ShP etc...
        if( isLocal() ){
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

    void aquiereReadLock(){
        readWriteLock.readLock().lock();
    }

    void releaseReadLock(){
        readWriteLock.readLock().unlock();
    }

    void aquiereWriteLock(){
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
     * Acquire a File for read only purposes. It is recommended that get*Stream or
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
     * @return the File type requested
     */
    public File getFile(final ShpFileType type) {
        if(!isLocal() ){
            throw new IllegalStateException("This method only applies if the files are local");
        }
        final URI uri = getURI(type);
        return toFile(uri);
    }

    /**
     * Acquire a Path for read only purposes. It is recommended that get*Stream or
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
     * @return the Path type requested
     */
    public Path getPath(final ShpFileType type) {
        if(!isLocal() ){
            throw new IllegalStateException("This method only applies if the files are local");
        }
        return toPath(getURI(type));
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
    public boolean isLocal() {
        return isLocal(uris.get(ShpFileType.SHP));
    }

    /**
     * Delete all the shapefile files. If the files are not local or the one
     * files cannot be deleted return false.
     */
    public boolean delete() {
        aquiereWriteLock();

        boolean retVal = true;
        try{
            if (isLocal()) {
                final Collection<URI> values = uris.values();
                for (URI uri : values) {
                    final Path p = toPath(uri);
                    try {
                        Files.deleteIfExists(p);
                    } catch (IOException e) {
                        retVal = false;
                    }
                }
            } else {
                retVal = false;
            }
        }finally{
            releaseWriteLock();
        }
        return retVal;
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
        final URI uri = getURI(type);

        try {
            return IOUtilities.open(uri);
        }catch(Throwable e){
            if( e instanceof IOException ){
                throw (IOException) e;
            } else if( e instanceof RuntimeException){
                throw (RuntimeException) e;
            } else if( e instanceof Error ){
                throw (Error) e;
            } else {
                throw new RuntimeException(e);
            }
        }
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
        final URI uri = getURI(type);

        try {
            return IOUtilities.openWrite(uri, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        } catch (Throwable e) {
            if (e instanceof IOException) {
                throw (IOException) e;
            } else if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else if (e instanceof Error) {
                throw (Error) e;
            } else {
                throw new RuntimeException(e);
            }
        }
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
     * @param type
     *                the type of file to open the channel to.
     * @param requestor
     *                the object requesting the channel
     *
     */
    public ReadableByteChannel getReadChannel(final ShpFileType type) throws IOException {
        final URI uri = getURI(type);
        return getReadChannel(uri);
    }

    public ReadableByteChannel getReadChannel(final URI uri) throws IOException {
        ReadableByteChannel channel = null;
        try {
            if (isLocal()) {
                final File file = toFile(uri);

                if (!file.exists()) {
                    throw new FileNotFoundException(file.toString());
                }
                if (!file.canRead()) {
                    throw new IOException("File is unreadable : " + file);
                }

                final RandomAccessFile raf = new RandomAccessFile(file, "r");
                channel = raf.getChannel();

            } else {
                final URL url = uri.toURL();
                final InputStream in = url.openConnection().getInputStream();
                channel = Channels.newChannel(in);
            }
        } catch (Throwable e) {
            if (e instanceof IOException) {
                throw (IOException) e;
            } else if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else if (e instanceof Error) {
                throw (Error) e;
            } else {
                throw new RuntimeException(e);
            }
        }
        return channel;
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
        final URI uri = getURI(type);
        return getWriteChannel(uri);
    }

    public WritableByteChannel getWriteChannel(final URI uri) throws IOException {

        try {
            final WritableByteChannel channel;
            if (isLocal()) {
                final File file = toFile(uri);
                final RandomAccessFile raf = new RandomAccessFile(file, "rw");
                channel = raf.getChannel();
                ((FileChannel) channel).lock();
            } else {
                final URL url = uri.toURL();
                final OutputStream out = url.openConnection().getOutputStream();
                channel = Channels.newChannel(out);
            }

            return channel;
        } catch (Throwable e) {
            if (e instanceof IOException) {
                throw (IOException) e;
            } else if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else if (e instanceof Error) {
                throw (Error) e;
            } else {
                throw new RuntimeException(e);
            }
        }
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
        if (!isLocal()) {
            throw new IllegalArgumentException("This method only makes sense if the files are local");
        }
        if (uri == null) {
            return false;
        }
        return Files.exists(toPath(uri));
    }


    ////////////////////////////////////////////////////////////////////////////
    /////////////// utils methods //////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    private static String baseName(final Object obj) {
        for(final ShpFileType type : ShpFileType.values()) {
            String base = null;
            if(obj instanceof File) {
                base = type.toBase( (File)obj);
            } else if(obj instanceof Path) {
                base = type.toBase( (Path)obj );
            } else if(obj instanceof URL) {
                base = type.toBase( (URL)obj );
            } else if(obj instanceof URI) {
                base = type.toBase( (URI)obj );
            }

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


    /**
     * Try to convert an URI into a Path.
     * @param uri
     * @return
     */
    public static Path toPath(final URI uri) {
        try {
            return Paths.get(uri);
        } catch (IllegalArgumentException | FileSystemNotFoundException e1) {
            //fallback with old api
            return toFile(uri).toPath();
        }
    }

    public static File toFile(final URI uri) {
        try {
            return new File(uri);
        } catch (IllegalArgumentException ex) {
            //should not happen, in case try the old way
            //throw new RuntimeException(ex);

            String string = uri.toString();
            if(!ShpFiles.isLocal(uri)){
                try {
                    string = URLDecoder.decode(string, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    // Shouldn't happen
                }
            }

            final String path3;
            final String simplePrefix = "file:/";
            final String standardPrefix = simplePrefix + "/";

            if (string.startsWith(standardPrefix)) {
                path3 = string.substring(standardPrefix.length());
            } else if (string.startsWith(simplePrefix)) {
                path3 = string.substring(simplePrefix.length() - 1);
            } else {
                final String auth = uri.getAuthority();
                final String path2 = uri.getPath().replace("%20", " ");
                if (auth != null && !auth.equals("")) {
                    path3 = "//" + auth + path2;
                } else {
                    path3 = path2;
                }
            }

            return new File(path3);
        }
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

            if (!isLocal()) {
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
        return Trees.toString("ShpFiles", uris.entrySet());
    }

    public static boolean isLocal(final URI uri){
        return "file".equalsIgnoreCase(uri.getScheme());
    }

}
