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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * <p>This class provides utilities to manipulate zip archives.</p>
 *
 * @author Samuel Andrés
 */
public class ZipUtilities {

    static final int BUFFER = 2048;
    static final byte[] data = new byte[BUFFER];


    /**
     * <p>This method allows to put resources into zip archive specified resource.</p>
     *
     * @param zip
     * @param method
     * @param level
     * @param resources
     * @throws IOException
     */
    public static void archive(Object out, int method, int level,Object... files)
            throws IOException{
        CheckedOutputStream checksum = new CheckedOutputStream(toOutputStream(out), new Adler32());
        BufferedOutputStream buf = new BufferedOutputStream(checksum);
        archiveCore(buf, method, level, files);
    }
    
    /**
     * <p>This method allows to put resources into zip archive specified resource.</p>
     * <p>Compression method is set to DEFLATED.</p>
     * <p>Level of compression is set to 9.</p>
     * 
     * @param zip
     * @param resources
     * @throws IOException
     */
    public static void archive(Object zip,Object... resources) throws IOException{
        ZipUtilities.archive(zip, ZipOutputStream.DEFLATED, 9, resources);
    }

    /**
     * <p>This method extract a ZIP archive zip into the specified location.</p>
     *
     * <p>ZIP resource parameter
     *
     * @param zip The archive parameter can be instance of InputStream, File,
     * URL, URI or a String path.
     * @param resource The resource where archive content will be extracted.
     * This location can be specified as instance of File, URL, URI or a String path.
     * @throws IOException
     */
    public static void extract (Object zip, Object resource)
            throws IOException{
        CheckedInputStream checksum = new CheckedInputStream(toInputStream(zip), new Adler32());
        BufferedInputStream buffi = new BufferedInputStream(checksum);
        extractCore(buffi,resource);
    }

    /**
     * <p>This method extract a ZIP archive into the directory which contents it.</p>
     *
     * <p>Be careful, this method does not accept an InputStream parameter, bt only
     * File, URL, URI or a String path.</p>
     *
     * @param zip The archive parameter as File, URL, URI or String path.
     * @throws IOException
     */
    public static void extract(Object zip) throws IOException{
        ZipUtilities.extract(zip, ZipUtilities.getParent(zip));
    }

    /**
     * <p>This method lists the content of indicated archive.</p>
     *
     * @param archive Instance of ZipFile, File or String path
     * @return a list of archive entries.
     * @throws IOException
     */
    public static List<String> listContent(Object archive) throws IOException{
        List<String> out = new ArrayList<String>();
        ZipFile zf = null;
        if(archive instanceof ZipFile){
            zf = (ZipFile) archive;
        } else if(archive instanceof File){
            zf = new ZipFile((File) archive);
        } else if (archive instanceof String){
            zf = new ZipFile((String) archive);
        }
        Enumeration entries = zf.entries();
        while (entries.hasMoreElements()){
            ZipEntry entry = (ZipEntry) entries.nextElement();
            out.add(entry.getName());
        }
        return out;
    }

    /**
     * <p>This method creates a ZIP archive zip OutputStream, from list of resources zip parameter.</p>
     * 
     * @param zip OutputStrem on ZIP resource that will contain resource files to archive.
     * @param method ZIP archive method.
     * @param level ZIP compression level
     * @param resources Resources to archive.
     * @throws IOException
     */
    private static void archiveCore(OutputStream zip, int method, int level,Object... resources)
            throws IOException {

        ZipOutputStream zout = new ZipOutputStream(zip);
        zout.setMethod(method);
        zout.setLevel(level);

        for (int i = 0; i<resources.length; i++){
            InputStream fi = toInputStream(resources[i]);
            BufferedInputStream buffi = new BufferedInputStream(fi, BUFFER);
            ZipEntry entry = new ZipEntry(ZipUtilities.getFileName(resources[i]));
            zout.putNextEntry(entry);

            int count;
            while((count = buffi.read(data,0,BUFFER)) != -1){zout.write(data, 0, count);}
            zout.closeEntry();
            buffi.close();
        }
        zout.close();
    }



    /**
     * <p>This method extract a ZIP archive from an InputStream, into directory
     * whose path is indicated.</p>
     *
     * @param zip InputStream on ZIP resource that contains resources to extract.
     * @param resource The resource which the resources will be extracted in.
     * @throws IOException
     */
    private static void extractCore(InputStream zip, Object resource) throws IOException {
       
        ZipInputStream zis = new ZipInputStream(zip);
        String extractPath = ZipUtilities.getPath(resource);
        
        BufferedOutputStream dest = null;
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null){
            File file = new File(extractPath, entry.getName());
            FileOutputStream fos = new FileOutputStream(file);
            dest = new BufferedOutputStream(fos, BUFFER);
            
            int count;
            while ((count = zis.read(data, 0, BUFFER)) != -1){dest.write(data,0,count);}
            dest.flush();
            dest.close();
        }
        zis.close();
    }

    /**
     * <p>This method gives an OUtputStream to write resource instance of File,
     * or a String path. Resource can be instance of OutputStream and zip
     * this case, the method returns the same OutputStream.</p>
     *
     * @param resource Resource.
     * @return An OutputStream to write zip the given resource.
     * @throws IOException
     */
    private static OutputStream toOutputStream(Object resource) throws
            IOException{
        OutputStream fot = null;
        if(resource instanceof File){
            fot = new FileOutputStream((File)resource);
        } else if(resource instanceof String){
            fot = new FileOutputStream(new File((String) resource));
        } else if (resource instanceof OutputStream){
            fot = (OutputStream) resource;
        }
        return fot;
    }

    /**
     * <p>This method gives an InputStream to read resource instance of File,
     * URL, URI or a String path. Resource can be instance of InputStream and zip
     * this case, the method returns the same InputStream.</p>
     *
     * @param resource Resource.
     * @return An InputStream to read the given resource.
     * @throws IOException
     */
    private static InputStream toInputStream(Object resource)
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
        }
        return fit;
    }

    /**
     * <p>This method returns the path of a given resource which can be
     * instance of File, URL, URI or String.</p>
     *
     * @param resource
     * @return The resource path.
     * @throws MalformedURLException
     */
    private static String getPath(Object resource)
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
     * <p>This method returs the path of the resource container.</p>
     *
     * @param resource
     * @return The path of the resource container.
     * @throws MalformedURLException
     */
    private static String getParent(Object resource)
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
     * <p>This method gives the resource file name.</p>
     *
     * @param resource
     * @return
     * @throws MalformedURLException
     */
    private static String getFileName(Object resource) throws MalformedURLException{
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
