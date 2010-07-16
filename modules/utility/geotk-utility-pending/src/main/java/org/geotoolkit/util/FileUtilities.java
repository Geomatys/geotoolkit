/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.CheckedOutputStream;
import java.util.zip.Checksum;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.util.logging.Logging;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @module pending
 * @Static
 */
public class FileUtilities {

    private static final Logger LOGGER = Logging.getLogger(FileUtilities.class);
    private static final int BUFFER = 2048;

    private FileUtilities(){}

    /**
     * This method delete recursively a file or a folder.
     * 
     * @param file The File or directory to delete.
     */
    public static void deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            for (File f : dir.listFiles()) {
                deleteDirectory(f);
            }
        }
        dir.delete();
    }

    /**
     * Append the specified text at the end of the File.
     *
     * @param text The text to append to the file.
     * @param urlFile The url file.
     *
     * @throws IOException if the file does not exist or cannot be read.
     */
    public static void appendToFile(String text, String urlFile) throws IOException {

        //true means we append a the end of the file
        final FileWriter fw = new FileWriter(urlFile, true);
        final BufferedWriter output = new BufferedWriter(fw);

        output.write(text);
        output.newLine();
        output.flush();
        output.close();
    }

    /**
     * Empty a file.
     *
     * @param urlFile The url file.
     *
     * @throws IOException if the file does not exist or cannot be read.
     */
    public static void emptyFile(final String urlFile) throws IOException {
        final File file = new File(urlFile);
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
    }

    /**
     * Read the contents of a file into string.
     *
     * @param f the file name
     * @return The file contents as string
     * @throws IOException if the file does not exist or cannot be read.
     */
    public static String getStringFromFile(File f) throws IOException {

        final StringBuilder sb = new StringBuilder();
        final BufferedReader br = new BufferedReader(new FileReader(f));
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append('\n');
        }
        br.close();
        return sb.toString();
    }

    /**
     * Searches in the Context ClassLoader for the named directory and returns it.
     *
     * @param packagee The name of package.
     *
     * @return A directory if it exist.
     */
    public static File getDirectoryFromResource(final String packagee) {
        File result = null;
        final ClassLoader classloader = Thread.currentThread().getContextClassLoader();

        try {
            final String fileP = packagee.replace('.', '/');
            final Enumeration<URL> urls = classloader.getResources(fileP);
            while (urls.hasMoreElements()) {
                final URL url = urls.nextElement();
                try {
                    final URI uri = url.toURI();
                    result = scanDir(uri, fileP);
                } catch (URISyntaxException e) {
                    LOGGER.log(Level.SEVERE, "URL, {0}cannot be converted to a URI", url);
                }
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "The resources for the package{0}, could not be obtained.\nCause:{1}", new Object[]{packagee, ex.getMessage()});
        }

        return result;
    }

    /**
     * Searches in the Context ClassLoader for the named file and returns it.
     *
     * @param packagee The name of package.
     *
     * @return A directory if it exist.
     */
    public static File getFileFromResource(String packagee) {
        File result = null;
        final ClassLoader classloader = Thread.currentThread().getContextClassLoader();

        try {
            final String extension = packagee.substring(packagee.lastIndexOf('.'), packagee.length());
            packagee = packagee.substring(0, packagee.lastIndexOf('.'));
            final String fileP = packagee.replace('.', '/') + extension;
            final Enumeration<URL> urls = classloader.getResources(fileP);
            while (urls.hasMoreElements()) {
                final URL url = urls.nextElement();
                try {
                    final URI uri = url.toURI();
                    result = scanFile(uri, fileP);
                } catch (URISyntaxException e) {
                    LOGGER.log(Level.SEVERE, "URL, {0}cannot be converted to a URI", url);
                }
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "The resources for the package{0}, could not be obtained.\nCause:{1}", new Object[]{packagee, ex.getMessage()});
        }


        return result;
    }

    /**
     * Load the properties from a properies file.
     *
     * If the file does not exist it will be created and an empty Properties object will be return.
     *
     * @param f a properties file.
     *
     * @return a Properties Object.
     */
    public static Properties getPropertiesFromFile(final File f) throws IOException {
        if (f != null) {
            final Properties prop = new Properties();
            if (f.exists()) {
                final FileInputStream in = new FileInputStream(f);
                prop.load(in);
                in.close();
            } else {
                f.createNewFile();
            }
            return prop;
        } else {
            throw new IllegalArgumentException(" the properties file can't be null");
        }
    }

    /**
     * store an Properties object "prop" into the specified File
     *
     * @param prop A properties Object.
     * @param f    A file.
     * @throws IOException
     */
    public static void storeProperties(final Properties prop, final File f) throws IOException {
        if (prop == null || f == null) {
            throw new IllegalArgumentException(" the properties or file can't be null");
        } else {
            final FileOutputStream out = new FileOutputStream(f);
            prop.store(out, "");
            out.close();
        }
    }

    /**
     * Scan a resource file (a JAR or a directory) and return it as a File.
     *
     * @param u The URI of the file.
     * @param filePackageName The package to scan.
     *
     * @return a list of package names.
     * @throws java.io.IOException
     */
    public static File scanDir(final URI u, final String filePackageName) throws IOException {
        final String scheme = u.getScheme();
        if (scheme.equals("file")) {
            final File f = new File(u.getPath());
            if (f.isDirectory()) {
                return f;
            }
        } else if (scheme.equals("jar") || scheme.equals("zip")) {
            final File f = new File(System.getProperty("java.io.tmpdir") + "/Constellation");
            boolean created = true;
            if (!f.exists()) {
                created = f.mkdir();
            }
            if (created) {
                try {
                    String cleanedUri = u.getSchemeSpecificPart();
                    if (cleanedUri.indexOf('!') != -1) {
                        cleanedUri = cleanedUri.substring(0, cleanedUri.indexOf('!'));
                    }
                    URI newUri = new URI(cleanedUri);
                    InputStream i = newUri.toURL().openStream();
                    IOUtilities.unzip(i, f);
                } catch (URISyntaxException ex) {
                    LOGGER.log(Level.WARNING, null, ex);
                }
                final File fConfig = new File(f, filePackageName);
                if (fConfig.exists() && fConfig.isDirectory()) {
                    return fConfig;
                } else {
                    LOGGER.info("The configuration directory was not found in the temporary folder.");
                }
            } else {
                LOGGER.info("The Constellation directory can't be created in the temporary folder.");
            }
        }
        return null;
    }

    /**
     * Scan a resource file (a JAR or a directory) and return it as a File.
     *
     * @param u The URI of the file.
     * @param filePackageName The package to scan.
     *
     * @return a list of package names.
     * @throws java.io.IOException
     */
    public static File scanFile(final URI u, final String filePackageName) throws IOException {
        final String scheme = u.getScheme();
        if (scheme.equals("file")) {
            return new File(u.getPath());
        }
        if (scheme.equals("jar") || scheme.equals("zip")) {
            final File f = new File(System.getProperty("java.io.tmpdir") + "/Constellation");
            boolean created = true;
            if (!f.exists()) {
                created = f.mkdir();
            }
            if (created) {
                final File fConfig = new File(f, filePackageName);
                if (fConfig.exists() && fConfig.isDirectory()) {
                    return fConfig;
                } else {
                    LOGGER.info("The configuration directory was not found in the temporary folder.");
                }
            } else {
                LOGGER.info("The Constellation directory can't be created in the temporary folder.");
            }
        }
        return null;
    }

    /**
     * Searches in the Context ClassLoader for the named files and returns a
     * {@code List<String>} with, for each named package,
     *
     * @param packages The names of the packages to scan in Java format, i.e.
     *                   using the "." separator, may be null.
     *
     * @return A list of package names.
     */
    public static List<String> searchSubFiles(final String packagee) {
        final List<String> result = new ArrayList<String>();
        final ClassLoader classloader = Thread.currentThread().getContextClassLoader();

        try {
            final String fileP = packagee.replace('.', '/');
            final Enumeration<URL> urls = classloader.getResources(fileP);
            while (urls.hasMoreElements()) {
                final URL url = urls.nextElement();
                try {
                    final URI uri = url.toURI();
                    result.addAll(scan(uri, fileP, false));
                } catch (URISyntaxException e) {
                    LOGGER.log(Level.SEVERE, "URL, {0}cannot be converted to a URI", url);
                }
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "The resources for the package {0}, could not be obtained. \ncause:{1}", new Object[]{packagee, ex.getMessage()});
        }
        return result;
    }

    /**
     * Searches in the Context ClassLoader for the named packages and returns a
     * {@code List<String>} with, for each named package,
     *
     * @param packages The names of the packages to scan in Java format, i.e.
     *                   using the "." separator, may be null.
     *
     * @return A list of package names.
     */
    public static List<String> searchSubPackage(final String... packages) {
        final List<String> result = new ArrayList<String>();
        final ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        for (String p : packages) {
            try {
                final String fileP = p.replace('.', '/');
                final Enumeration<URL> urls = classloader.getResources(fileP);
                while (urls.hasMoreElements()) {
                    final URL url = urls.nextElement();
                    try {
                        final URI uri = url.toURI();
                        List<String> scanned = scan(uri, fileP, true);
                        for (String s : scanned) {
                            if (!result.contains(s)) {
                                result.add(s);
                            }
                        }
                    } catch (URISyntaxException e) {
                        LOGGER.log(Level.SEVERE, "URL, {0} cannot be converted to a URI", url);
                    }
                }
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, "The resources for the package" + p + ", could not be obtained.", ex);
            }
        }
        return result;
    }

    /**
     * Scan a resource file (a JAR or a directory) to find the sub-package names of
     * the specified "filePackageName"
     *
     * @param u The URI of the file.
     * @param filePackageName The package to scan.
     *
     * @return a list of package names.
     * @throws java.io.IOException
     */
    public static List<String> scan(final URI u, final String filePackageName, boolean directory) throws IOException {
        final List<String> result = new ArrayList<String>();
        final String scheme = u.getScheme();
        if (scheme.equals("file")) {
            final File f = new File(u.getPath());
            if (f.isDirectory()) {
                List<String> scanned = scanDirectory(f, filePackageName, directory);
                for (String s : scanned) {
                    if (!result.contains(s)) {
                        result.add(s);
                    }
                }
            } else if (!directory) {
                result.add(f.getPath());
            }
        } else if (scheme.equals("jar") || scheme.equals("zip")) {
            try {
                String brut = u.getSchemeSpecificPart();
                URI uri = URI.create(brut.replaceAll(" ", "%20"));
                String jarFile = uri.getPath();
                jarFile = jarFile.substring(0, jarFile.indexOf('!'));
                List<String> scanned = scanJar(new File(jarFile), filePackageName, directory);
                for (String s : scanned) {
                    if (!result.contains(s)) {
                        result.add(s);
                    }
                }

            } catch (IllegalArgumentException ex) {
                LOGGER.log(Level.WARNING, "unable to scan jar file: {0}\n cause:{1}", new Object[]{u.getSchemeSpecificPart(), ex.getMessage()});
            }
        }
        return result;
    }

    /**
     * Scan a directory to find the sub-package names of
     * the specified "parent" package
     *
     * @param root The root file (directory) of the package to scan.
     * @param parent the package name.
     *
     * @return a list of package names.
     */
    public static List<String> scanDirectory(final File root, final String parent, boolean directory) {
        final List<String> result = new ArrayList<String>();
        for (File child : root.listFiles()) {
            if (child.isDirectory()) {
                if (directory) {
                    String s = parent.replace('/', '.') + '.' + child.getName();
                    if (!result.contains(s)) {
                        result.add(s);
                    }
                }
                List<String> scanned = scanDirectory(child, parent, directory);
                for (String s : scanned) {
                    if (!result.contains(s)) {
                        result.add(s);
                    }
                }
            } else if (!directory) {
                if (!result.contains(child.getPath())) {
                    result.add(child.getPath());
                }
            }
        }
        return result;
    }

    /**
     * Scan a jar to find the sub-package names of
     * the specified "parent" package
     *
     * @param file the jar file containing the package to scan
     * @param parent the package name.
     *
     * @return a list of package names.
     * @throws java.io.IOException
     */
    public static List<String> scanJar(final File file, final String parent, boolean directory) throws IOException {
        final List<String> result = new ArrayList<String>();
        final JarFile jar = new JarFile(file);
        final Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            final JarEntry e = entries.nextElement();
            if (e.isDirectory() && e.getName().startsWith(parent) && directory) {
                String s = e.getName().replace('/', '.');
                s = s.substring(0, s.length() - 1);
                if (!result.contains(s)) {
                    result.add(s);
                }
            } else if (!e.isDirectory() && e.getName().startsWith(parent) && !directory) {
                String s = e.getName().replace('/', '.');
                s = s.substring(0, s.length() - 1);
                if (!result.contains(s)) {
                    result.add(s);
                }
            }
        }
        return result;
    }

    /**
     * Write the contents of a file into string.
     *
     * @param f the file name
     * @return The file contents as string
     * @throws IOException if the file does not exist or cannot be read.
     */
    public static void stringToFile(File f, String s) throws IOException {

        final BufferedWriter bw = new BufferedWriter(new FileWriter(f));
        bw.write(s);
        bw.close();
    }

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
    public static void zip(final Object zip, final Checksum checksum, final Object... resources) throws IOException {
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
            throws IOException {

        final BufferedOutputStream buf;
        if (checksum != null) {
            CheckedOutputStream cos = new CheckedOutputStream(toOutputStream(zip), checksum);
            buf = new BufferedOutputStream(cos);
        } else {
            buf = new BufferedOutputStream(toOutputStream(zip));
        }

        final ZipOutputStream zout = new ZipOutputStream(buf);
        try {
            zout.setMethod(method);
            zout.setLevel(level);
            zipCore(zout, method, level, "", resources);
        } finally {
            zout.close();
        }
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
    private static void zipCore(final ZipOutputStream zout, final int method, final int level, final String entryPath, final Object... resources)
            throws IOException {

        final byte[] data = new byte[BUFFER];
        final CRC32 crc = new CRC32();
        boolean stored = false;

        if (ZipOutputStream.STORED == method) {
            stored = true;
            for (Object resource : resources) {
                if (!(resource instanceof File)) {
                    throw new IllegalArgumentException("This compression is supported with File resources only.");
                }
            }
        } else if (ZipOutputStream.DEFLATED != method) {
            throw new IllegalArgumentException("This compression method is not supported.");
        }

        if (Double.isNaN(level) || Double.isInfinite(level) || level > 9 || level < 0) {
            throw new IllegalArgumentException("Illegal compression level.");
        }

        for (int i = 0; i < resources.length; i++) {
            final ZipEntry entry = new ZipEntry(entryPath + getFileName(resources[i]));
            if (stored) {
                final File file = (File) resources[i];
                entry.setCompressedSize(file.length());
                entry.setSize(file.length());
                entry.setCrc(crc.getValue());
            }

            if (resources[i] instanceof File && ((File) resources[i]).isDirectory()) {
                final String zipName = new StringBuilder(entryPath).append(((File) resources[i]).getName()).
                        append(((File) resources[i]).isDirectory() ? '/' : "").toString();
                zipCore(zout, method, level, zipName, (Object[]) ((File) resources[i]).listFiles());
                continue;
            }

            zout.putNextEntry(entry);

            BufferedInputStream buffi = new BufferedInputStream(toInputStream(resources[i]), BUFFER);
            if (stored) {
                try {
                    crc.reset();
                    int bytesRead;
                    while ((bytesRead = buffi.read(data, 0, BUFFER)) != -1) {
                        crc.update(data, 0, bytesRead);
                    }
                } finally {
                    buffi.close();
                }
                buffi = new BufferedInputStream(toInputStream(resources[i]), BUFFER);
            }

            try {
                int count;
                while (-1 != (count = buffi.read(data, 0, BUFFER))) {
                    zout.write(data, 0, count);
                }
            } finally {
                zout.closeEntry();
                buffi.close();
            }
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
    public static void unzip(final Object zip, final Checksum checksum) throws IOException {
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
            throws IOException {
        final BufferedInputStream buffi;
        if (checksum != null) {
            CheckedInputStream cis = new CheckedInputStream(toInputStream(zip), checksum);
            buffi = new BufferedInputStream(cis);
        } else {
            buffi = new BufferedInputStream(toInputStream(zip));
        }
        unzipCore(buffi, resource);
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
            final String extractPath = getPath(resource);
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                final File file = new File(extractPath, entry.getName());
                if (entry.isDirectory()) {
                    file.mkdirs();
                    continue;
                }
                file.getParentFile().mkdirs();
                final OutputStream fos = toOutputStream(file);
                final BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);
                try {
                    int count;
                    while ((count = zis.read(data, 0, BUFFER)) != -1) {
                        dest.write(data, 0, count);
                    }
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
            throws IOException {

        OutputStream fot = null;
        if (resource instanceof File) {
            fot = new FileOutputStream((File) resource);
        } else if (resource instanceof String) {
            fot = new FileOutputStream(new File((String) resource));
        } else if (resource instanceof OutputStream) {
            fot = (OutputStream) resource;
        } else if (resource != null) {
            throw new IllegalArgumentException("This argument must be instance of File, "
                    + "String (representing a path) or OutputStream.");
        } else {
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
            throws IOException {

        InputStream fit = null;
        if (resource instanceof File) {
            fit = new FileInputStream((File) resource);
        } else if (resource instanceof URL) {
            fit = ((URL) resource).openStream();
        } else if (resource instanceof URI) {
            fit = ((URI) resource).toURL().openStream();
        } else if (resource instanceof String) {
            fit = new FileInputStream(new File((String) resource));
        } else if (resource instanceof InputStream) {
            fit = (InputStream) resource;
        } else if (resource != null) {
            throw new IllegalArgumentException("This argument must be instance of File, "
                    + "String (representing a path), URL, URI or InputStream.");
        } else {
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
            throws MalformedURLException {

        String extractPath = null;
        if (resource instanceof File) {
            extractPath = ((File) resource).getPath();
        } else if (resource instanceof URL) {
            extractPath = ((URL) resource).getPath();
        } else if (resource instanceof URI) {
            extractPath = (((URI) resource).toURL()).getPath();
        } else if (resource instanceof String) {
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
            throws MalformedURLException {

        String extractPath = null;
        if (resource instanceof File) {
            extractPath = ((File) resource).getParent();
        } else {
            extractPath = getPath(resource);
            extractPath = extractPath.substring(0, extractPath.lastIndexOf(File.separator) + 1);
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
            throws MalformedURLException {

        String fileName = null;
        if (resource instanceof File) {
            fileName = ((File) resource).getName();
        } else {
            fileName = getPath(resource);
            fileName = fileName.substring(fileName.lastIndexOf(File.separator) + 1, fileName.length());
        }
        return fileName;
    }
}
