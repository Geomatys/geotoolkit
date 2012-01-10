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
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import org.geotoolkit.data.shapefile.ShapefileDataStoreFactory;
import static org.geotoolkit.data.shapefile.ShapefileDataStoreFactory.ENCODING;
import static org.geotoolkit.data.shapefile.ShapefileDataStoreFactory.LOGGER;
import static org.geotoolkit.data.shapefile.lock.ShpFileType.QIX;
import static org.geotoolkit.data.shapefile.lock.ShpFileType.SHP;
import org.geotoolkit.gui.swing.tree.Trees;
import org.geotoolkit.index.quadtree.QuadTree;
import org.geotoolkit.index.quadtree.StoreException;
import org.geotoolkit.index.quadtree.fs.FileSystemIndexStore;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.util.collection.WeakHashSet;

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
     * The urls for each type of file that is associated with the shapefile. The
     * key is the type of file
     */
    private final Map<ShpFileType, URL> urls = new EnumMap<ShpFileType, URL>(ShpFileType.class);

    /**
     * A read/write lock, so that we can have concurrent readers 
     */
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private final WeakHashSet<AccessManager> managers = WeakHashSet.newInstance(AccessManager.class);
    
    private final boolean loadQuadTree;

    /**
     * Searches for all the files and adds then to the map of files.
     * 
     * @param file any one of the shapefile files
     * @throws FileNotFoundException if the shapefile associated with file is not found
     */
    public ShpFiles(final Object path) throws IllegalArgumentException {
        this(path,false);
    }

    /**
     * Searches for all the files and adds then to the map of files.
     * 
     * @param url any one of the shapefile files
     */
    public ShpFiles(final Object path, final boolean loadQix) throws IllegalArgumentException {
        URL url = null;

        if(path instanceof String){
            try {
                url = new URL(path.toString());
            } catch (MalformedURLException e) {
                try {
                    url = new File(path.toString()).toURI().toURL();
                } catch (MalformedURLException ex) {
                    throw new IllegalArgumentException(
                            "Path object can not be converted to a valid URL",ex);
                }
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

        //retrive all file urls associated with this shapefile
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
            } catch (MalformedURLException e) {
                // shouldn't happen because the starting url was constructable
                throw new RuntimeException(e);
            }
            urls.put(type, newURL);
        }

        // if the files are local check each file to see if it exists
        // if not then search for a file of the same name but try all combinations of the 
        // different cases that the extension can be made up of.
        // IE Shp, SHP, Shp, ShP etc...
        if( isLocal() ){
            for (final Entry<ShpFileType, URL> entry : urls.entrySet()) {
                if( !exists(entry.getKey()) ){
                    final URL candidate = findExistingFile(entry.getValue());
                    if(candidate!=null){
                        urls.put(entry.getKey(), candidate);
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
        final Map<ShpFileType, String> result = new EnumMap<ShpFileType, String>(ShpFileType.class);

        for (final Entry<ShpFileType, URL> entry : urls.entrySet()) {
            result.put(entry.getKey(), entry.getValue().toExternalForm());
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
        return urls.get(type).toExternalForm();
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
        final URL url = getURL(type);
        return toFile(url);
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
    public URL getURL(final ShpFileType type) {
        return urls.get(type);
    }

    /**
     * Determine if the location of this shapefile is local or remote.
     * 
     * @return true if local, false if remote
     */
    public boolean isLocal() {
        return isLocal(urls.get(ShpFileType.SHP));
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
                final Collection<URL> values = urls.values();
                for (URL url : values) {
                    final File f = toFile(url);
                    if (!f.delete()) {
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
        final URL url = getURL(type);

        try {
            return url.openStream();
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
        final URL url = getURL(type);

        try {
            OutputStream out;
            if( isLocal() ){
                final File file = toFile(url);
                out = new FileOutputStream(file);
            }else{
                final URLConnection connection = url.openConnection();
                connection.setDoOutput(true);
                out = connection.getOutputStream();
            }
            
            return out;
        } catch (Throwable e) {;
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
     * obtained from the urls input stream.
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
        final URL url = getURL(type);
        return getReadChannel(url);
    }
    
    public ReadableByteChannel getReadChannel(final URL url) throws IOException {
        ReadableByteChannel channel = null;
        try {
            if (isLocal()) {
                final File file = toFile(url);

                if (!file.exists()) {
                    throw new FileNotFoundException(file.toString());
                }
                if (!file.canRead()) {
                    throw new IOException("File is unreadable : " + file);
                }

                final RandomAccessFile raf = new RandomAccessFile(file, "r");
                channel = raf.getChannel();

            } else {
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
     * a generic channel for remote urls, however both shape and dbf writing can
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
        final URL url = getURL(type);
        return getWriteChannel(url);
    }
    
    public WritableByteChannel getWriteChannel(final URL url) throws IOException {

        try {
            final WritableByteChannel channel;
            if (isLocal()) {
                final File file = toFile(url);
                final RandomAccessFile raf = new RandomAccessFile(file, "rw");
                channel = raf.getChannel();
                ((FileChannel) channel).lock();
            } else {
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
        final String path = SHP.toBase(urls.get(SHP));
        final int slash = Math.max(0, path.lastIndexOf('/') + 1);
        int dot = path.indexOf('.', slash);

        if (dot < 0) {
            dot = path.length();
        }

        return path.substring(slash, dot);
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
        return exists( urls.get(fileType) );
    }
    
    public boolean exists(final URL url) throws IllegalArgumentException {
        if (!isLocal()) {
            throw new IllegalArgumentException("This method only makes sense if the files are local");
        }
        if (url == null) {
            return false;
        }
        return toFile(url).exists();
    }
    
    
    ////////////////////////////////////////////////////////////////////////////
    /////////////// utils methods //////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    private static String baseName(final Object obj) {
        for(final ShpFileType type : ShpFileType.values()) {
            String base = null;
            if(obj instanceof File) {
                base = type.toBase( (File)obj);
            } else if(obj instanceof URL) {
                base = type.toBase( (URL)obj );
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
    private static URL findExistingFile(final URL value) {
        final File file = toFile(value);
        final File directory = file.getParentFile();
        if( directory==null || !directory.exists() ) {
            // doesn't exist
            return null;
        }
        final File[] files = directory.listFiles(new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name) {
                return file.getName().equalsIgnoreCase(name);
            }

        });
        if(files.length>0){
            try {
                return files[0].toURI().toURL();
            } catch (MalformedURLException e) {
                ShapefileDataStoreFactory.LOGGER.log(Level.SEVERE, "", e);
            }
        }
        return null;
    }

    public static File toFile(final URL url) {
        try {
            return new File(url.toURI());
        } catch (URISyntaxException exp) {
            try {
                return IOUtilities.toFile(url, ENCODING);
            } catch (IOException ex) {
                //should not happen, in case try the old way
                //throw new RuntimeException(ex);

                String string = url.toExternalForm();
                if(!ShpFiles.isLocal(url)){
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
                    final String auth = url.getAuthority();
                    final String path2 = url.getPath().replace("%20", " ");
                    if (auth != null && !auth.equals("")) {
                        path3 = "//" + auth + path2;
                    } else {
                        path3 = path2;
                    }
                }

                return new File(path3);
            }
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
            final URL treeURL = getURL(QIX);

            try {
                final File treeFile = toFile(treeURL);
                if (!treeFile.exists() || (treeFile.length() == 0)) {
                    return null;
                }

                if(qixStore == null){
                    qixStore = new FileSystemIndexStore(treeFile);
                }

                if(loadQuadTree){
                    //we store the quad tree for reuse
                    quadTree = qixStore.load();
                    quadTree.loadAll();
                    return quadTree;
                }else{
                    return qixStore.load();
                }
                
            } finally {
            }
        }

        return quadTree;
    }

    @Override
    public String toString() {
        return Trees.toString("ShpFiles", urls.entrySet());
    }

    public static boolean isLocal(final URL url){
        return url.toExternalForm().toLowerCase().startsWith("file:");
    }
    
}
