/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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
package org.geotoolkit.data.zip;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.CheckedOutputStream;
import java.util.zip.Checksum;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * <p>This class provides utilities to manipulate zip archives.</p>
 *
 * @author Samuel Andrés
 */
public class ZipUtilities {

    private static final int BUFFER = 2048;

    private ZipUtilities(){}
    
    /**
     * <p>This method allows to put resources into zip archive specified resource, 
     * with compression :</p>
     * <ul>
     * <li>Compression method is set to DEFLATED.</li>
     * <li>Level of compression is set to 9.</li>
     * </ul>
     * 
     * @param zip The resource which files will be archived into. This argument must be
     * instance of File, String (representing a path), or OutputStream. Cannot be null.
     * @param checksum Checksum object (instance of Alder32 or CRC32).
     * @param resources The files to compress. Tese objects can be File instances or
     * String representing files paths, URL, URI or InputStream. Cannot be null.
     * @throws IOException
     */
    public static void zip(final Object zip,  final Checksum checksum, final Object... resources) throws IOException{
        zip(zip, ZipOutputStream.STORED, 0, checksum, resources);
    }

    /**
     * <p>This method allows to put resources into zip archive specified resource.</p>
     *
     * @param zip The resource which files will be archived into. This argument must be
     * instance of File, String (representing a path), or OutputStream. Cannot be null.
     * @param method The compression method is a static int constant from ZipOutputSteeam with
     * two theorical possible values :
     * <ul>
     * <li>DEFLATED to compress archive.</li>
     * <li>STORED to let the archive uncompressed (unsupported).</li>
     * </ul>
     * @param level The compression level is an integer between 0 (not compressed) to 9 (best compression).
     * @param checksum Checksum object (instance of Alder32 or CRC32).
     * @param resources The files to compress. Tese objects can be File instances or
     * String representing files paths, URL, URI or InputStream. Cannot be null.
     * @throws IOException
     */
    public static void zip(final Object zip, final int method, final int level, final Checksum checksum, final Object... resources)
            throws IOException{

        final BufferedOutputStream buf;
        if (checksum != null){
            CheckedOutputStream cos = new CheckedOutputStream(toOutputStream(zip), checksum);
            buf = new BufferedOutputStream(cos);
        } else {
            buf = new BufferedOutputStream(toOutputStream(zip));
        }
        zipCore(buf, method, level, resources);
    }

    /**
     * <p>This method creates an OutputStream with ZIP archive from the list of resources parameter.</p>
     *
     * @param zip OutputStrem on ZIP archive that will contain archives of resource files.
     * @param method The compression method is a static int constant from ZipOutputSteeam with
     * two theorical possible values : 
     * <ul>
     * <li>DEFLATED to compress archive.</li>
     * <li>STORED to let the archive uncompressed (unsupported).</li>
     * </ul>
     * @param level The compression level is an integer between 0 (not compressed) to 9 (best compression).
     * @param resources The files to compress. Tese objects can be File instances or
     * String representing files paths, URL, URI or InputStream. Cannot be null.
     * @throws IOException
     */
    private static void zipCore(final OutputStream zip, final int method, final int level, final Object... resources)
            throws IOException {


        final byte[] data = new byte[BUFFER];
        final ZipOutputStream zout = new ZipOutputStream(zip);
        final CRC32 crc = new CRC32();
        boolean stored = false;


        if (ZipOutputStream.STORED == method){
            stored = true;
            for (Object resource : resources){
                if (!(resource instanceof File)){
                    throw new IllegalArgumentException("This compression is supported with File resources only.");
                }
            }
        } else if (ZipOutputStream.DEFLATED != method)
            throw new IllegalArgumentException("This compression method is not supported.");
        if (Double.isNaN(level) || Double.isInfinite(level) || level > 9 || level < 0)
            throw new IllegalArgumentException("Illegal compression level.");

        try {
            zout.setMethod(method);
            zout.setLevel(level);

            for (int i = 0; i<resources.length; i++){
                InputStream fi = toInputStream(resources[i]);
                BufferedInputStream buffi = new BufferedInputStream(fi, BUFFER);

                crc.reset();
                    int bytesRead;
                while ((bytesRead = buffi.read(data)) != -1) {
                    crc.update(data, 0, bytesRead);
                }

                try {
                    ZipEntry entry = new ZipEntry(getFileName(resources[i]));
                    if(stored){
                        File file = (File) resources[i];
                        entry.setCompressedSize(file.length());
                        entry.setSize(file.length());
                        entry.setCrc(crc.getValue());
                    }
                    zout.putNextEntry(entry);
                    int count;
                    while((count = buffi.read(data,0,BUFFER)) != -1){zout.write(data, 0, count);}
                } finally {
                    zout.closeEntry();
                    buffi.close();
                }
            }
        } finally {
            zout.close();
        }
    }

    /**
     * <p>This method extracts a ZIP archive into the directory which contents it.</p>
     *
     * @param zip The archive parameter as File, URL, URI, InputStream or String path.
     * This argument cannot be null.
     * @param checksum Checksum object (instance of Alder32 or CRC32).
     * @throws IOException
     */
    public static void unzip(final Object zip, final Checksum checksum) throws IOException{
        unzip(zip, getParent(zip), checksum);
    }

    /**
     * <p>This method extract a ZIP archive into the specified location.</p>
     *
     * <p>ZIP resource parameter
     *
     * @param zip The archive parameter can be instance of InputStream, File,
     * URL, URI or a String path. This argument cannot be null.
     * @param resource The resource where archive content will be extracted.
     * This resource location can be specified as instance of File or a String path.
     * This argument cannot be null.
     * @param checksum Checksum object (instance of Alder32 or CRC32).
     * @throws IOException
     */
    public static void unzip(final Object zip, final Object resource, final Checksum checksum)
            throws IOException{
        final BufferedInputStream buffi;
        if (checksum != null){
            CheckedInputStream cis = new CheckedInputStream(toInputStream(zip), checksum);
            buffi = new BufferedInputStream(cis);
        } else {
            buffi = new BufferedInputStream(toInputStream(zip));
        }
        unzipCore(buffi,resource);
    }

    /**
     * <p>This method extract a ZIP archive from an InputStream, into directory
     * whose path is indicated by resource object.</p>
     *
     * @param zip InputStream on ZIP resource that contains resources to extract.
     * @param resource The resource where files will be extracted.
     * Must be instance of File or a String path. This argument cannot be null.
     * @throws IOException
     */
    private static void unzipCore(final InputStream zip, final Object resource)
            throws IOException {

        final byte[] data = new byte[BUFFER];
        final ZipInputStream zis = new ZipInputStream(zip);
        
        try {
            String extractPath = ZipUtilities.getPath(resource);
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null){
                OutputStream fos = toOutputStream(new File(extractPath, entry.getName()));
                BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);
                try {
                    int count;
                    while ((count = zis.read(data, 0, BUFFER)) != -1){dest.write(data,0,count);}
                    dest.flush();
                } finally {
                    dest.close();
                }
            }
        } finally {
            zis.close();
        }
    }

    /**
     * <p>This method gives an OutputStream to write resource instance of File,
     * or a String path. Resource can be instance of OutputStream and in
     * this case, the method returns the same OutputStream.</p>
     *
     * @param resource Resource instance of OutputStream, File, or a String Path.
     * This argument cannot be null.
     * @return An OutputStream to write zip the given resource.
     * @throws IOException
     */
    private static OutputStream toOutputStream(final Object resource)
            throws IOException{

        OutputStream fot = null;
        if(resource instanceof File){
            fot = new FileOutputStream((File)resource);
        } else if(resource instanceof String){
            fot = new FileOutputStream(new File((String) resource));
        } else if (resource instanceof OutputStream){
            fot = (OutputStream) resource;
        } else if (resource != null){
            throw new IllegalArgumentException("This argument must be instance of File, " +
                    "String (representing a path) or OutputStream.");
        } else{
            throw new NullPointerException("This argument cannot be null.");
        }
        return fot;
    }

    /**
     * <p>This method gives an InputStream to read resource instance of File,
     * URL, URI or a String path. Resource can be instance of InputStream and in
     * this case, the method returns the same InputStream.</p>
     *
     * @param resource Resource instance of InputStream, File, URL, URI, or a String path.
     * This argument cannot be null.
     * @return An InputStream to read the given resource.
     * @throws IOException
     */
    private static InputStream toInputStream(final Object resource)
            throws IOException{

        InputStream fit = null;
        if(resource instanceof File){
            fit = new FileInputStream((File)resource);
        } else if(resource instanceof URL){
            fit = ((URL)resource).openStream();
        } else if(resource instanceof URI){
            fit = ((URI)resource).toURL().openStream();
        } else if(resource instanceof String){
            fit = new FileInputStream(new File((String) resource));
        } else if(resource instanceof InputStream){
            fit = (InputStream) resource;
        } else if (resource != null){
            throw new IllegalArgumentException("This argument must be instance of File, " +
                    "String (representing a path), URL, URI or InputStream.");
        } else{
            throw new NullPointerException("This argument cannot be null.");
        }
        return fit;
    }

    /**
     * <p>This method returns the path of a given resource which can be
     * instance of File, URL, URI or String. Returns null in other cases.</p>
     *
     * @param resource instance of File, URL, URI, or String path.
     * @return The resource path.
     * @throws MalformedURLException
     */
    private static String getPath(final Object resource)
            throws MalformedURLException{

        String extractPath = null;
        if(resource instanceof File){
            extractPath = ((File) resource).getPath();
        } else if (resource instanceof URL){
            extractPath = ((URL) resource).getPath();
        } else if (resource instanceof URI){
            extractPath = (((URI) resource).toURL()).getPath();
        } else if (resource instanceof String){
            extractPath = (String) resource;
        }
        return extractPath;
    }

    /**
     * <p>This method returs the path of the resource container (parent directory).
     * Resource can be an instance of File, URL, URI or String path.
     * Return snull in other cases.</p>
     *
     * @param resource instance of File, URL, URI, or String path.
     * @return The path of the resource container.
     * @throws MalformedURLException
     */
    private static String getParent(final Object resource)
            throws MalformedURLException{

        String extractPath = null;
        if(resource instanceof File){
            extractPath = ((File) resource).getParent();
        } else {
            extractPath = ZipUtilities.getPath(resource);
            extractPath = extractPath.substring(0, extractPath.lastIndexOf(File.separator)+1);
        }
        return extractPath;
    }

    /**
     * <p>This method gives the resource file name.
     * Resource can be an instance of File, URL, URI or String path.
     * Return snull in other cases.</p>
     *
     * @param resource instance of File, URL, URI, or String path.
     * @return
     * @throws MalformedURLException
     */
    private static String getFileName(final Object resource)
            throws MalformedURLException{

        String fileName = null;
        if(resource instanceof File){
            fileName = ((File) resource).getName();
        }  else {
            fileName = ZipUtilities.getPath(resource);
            fileName = fileName.substring(fileName.lastIndexOf(File.separator)+1, fileName.length());
        }
        return fileName;
    }
}
