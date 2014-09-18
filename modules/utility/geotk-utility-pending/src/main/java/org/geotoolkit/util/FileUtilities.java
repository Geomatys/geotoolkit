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

//import java.io.*;
//import java.net.MalformedURLException;
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.Enumeration;
//import java.util.List;
//import java.util.Properties;
//import java.util.jar.JarEntry;
//import java.util.jar.JarFile;
//import java.util.logging.Level;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
//import java.util.zip.CRC32;
//import java.util.zip.CheckedInputStream;
//import java.util.zip.CheckedOutputStream;
//import java.util.zip.Checksum;
//import java.util.zip.ZipEntry;
//import java.util.zip.ZipInputStream;
//import java.util.zip.ZipOutputStream;
//import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.lang.Static;
import org.apache.sis.util.logging.Logging;

import static org.apache.sis.util.ArgumentChecks.*;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @module pending
 * @Static
 */
public final class FileUtilities extends Static {

    private static final Logger LOGGER = Logging.getLogger(FileUtilities.class);
//    private static final int BUFFER = 2048;
//
//    private FileUtilities(){}
//
    /**
     * Copy all files and directories, from the given source to the target destination.
     *
     * @param src The source file to copy.
     * @param dest The destination directory.
     * @throws IOException
     */
    public static void copy(final File src, final File dest) throws IOException {
        if (src == null || dest == null) {
            LOGGER.warning("Source and destination files must not be null for the copy");
            return;
        }
        if (!src.exists()) {
            LOGGER.log(Level.WARNING, "The source file does not exist: {0}", src);
            return;
        }
        if (src.isDirectory()) {
            if (!dest.exists()) {
                if(!dest.mkdir()){
                    throw new IOException("Failed to create directory : " + dest);
                }
            }
            final String files[] = src.list();
            for (int i = 0; i < files.length; i++) {
                copy(new File(src, files[i]), new File(dest, files[i]));
            }
        } else {
            FileInputStream from = null;
            FileOutputStream to = null;
            try {
                from = new FileInputStream(src);
                to = new FileOutputStream(dest);
                final byte[] buffer = new byte[4096];
                int bytesRead;

                while ((bytesRead = from.read(buffer)) != -1) {
                    to.write(buffer, 0, bytesRead); // write
                }
            } finally {
                if (from != null) {
                    try {
                        from.close();
                    } catch (IOException e) {
                        LOGGER.log(Level.FINE, e.getLocalizedMessage(), e);
                    }
                }
                if (to != null) {
                    try {
                        to.close();
                    } catch (IOException e) {
                        LOGGER.log(Level.FINE, e.getLocalizedMessage(), e);
                    }
                }
            }
        }
    }

//    /**
//     * This method delete recursively a file or a folder.
//     *
//     * @param file The File or directory to delete.
//     */
//    public static boolean deleteDirectory(final File dir) {
//        if (dir.isDirectory()) {
//            for (File f : dir.listFiles()) {
//                deleteDirectory(f);
//            }
//        }
//        return dir.delete();
//    }

    /**
     * This method delete recursively a file or a folder.
     *
     * @param directory The Path directory to delete.
     */
    public static void deleteDirectory(final Path directory) throws IOException {

        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }

        });
    }


//
//    /**
//     * Append the specified text at the end of the File.
//     *
//     * @param text The text to append to the file.
//     * @param urlFile The url file.
//     *
//     * @throws IOException if the file does not exist or cannot be read.
//     */
//    public static void appendToFile(final String text, final String urlFile) throws IOException {
//
//        //true means we append a the end of the file
//        final FileWriter fw = new FileWriter(urlFile, true);
//        final BufferedWriter output = new BufferedWriter(fw);
//
//        try{
//            output.write(text);
//            output.newLine();
//            output.flush();
//        }finally{
//            output.close();
//        }
//    }
//
//    /**
//     * Empty a file.
//     *
//     * @param urlFile The url file.
//     *
//     * @throws IOException if the file does not exist or cannot be read.
//     */
//    public static void emptyFile(final String urlFile) throws IOException {
//        final File file = new File(urlFile);
//        if (file.exists()) {
//            if(!file.delete()){
//                throw new IOException("Failed to delete file : " + file);
//            }
//        }
//        if(!file.createNewFile()){
//            //should have been deleted before, so we expect this to be true.
//            throw new IOException("Failed to create file : " + file);
//        }
//    }
//
    /**
     * Read the contents of a file into string.
     *
     * @param f the file name
     * @return The file contents as string
     * @throws IOException if the file does not exist or cannot be read.
     */
    public static String getStringFromFile(final File f) throws IOException {

        final StringBuilder sb = new StringBuilder();
        final BufferedReader br = new BufferedReader(new FileReader(f));
        String line;
        try{
            while ((line = br.readLine()) != null) {
                sb.append(line).append('\n');
            }
        }finally{
            br.close();
        }
        return sb.toString();
    }


    /**
     * Read the contents of a stream into string.
     *
     * @param stream the file name
     * @return The file contents as string
     * @throws IOException if the file does not exist or cannot be read.
     */
    public static String getStringFromStream(final InputStream stream) throws IOException {

        final StringBuilder sb = new StringBuilder();
        final BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
        String line;
        try {
            while ((line = br.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } finally {
            br.close();
            stream.close();
        }
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

//    /**
//     * Get the file name without extension of a {@link File}.
//     *
//     * @param file Should not be {@code null}.
//     * @return The file name without extension if any.
//     */
//    public static String getFileName(final File file) {
//        if (file.isDirectory()) {
//            return file.getName();
//        }
//
//        final String fileName = file.getName();
//        if (!fileName.contains(".")) {
//            return fileName;
//        }
//
//        return fileName.substring(0, fileName.lastIndexOf("."));
//    }
//
    public static File buildFileFromStream(final InputStream in, final File dest) throws IOException {
        if (in != null && dest != null) {
            final FileOutputStream fos = new FileOutputStream(dest);
            try {
                final byte[] buffer = new byte[4096];
                int bytesRead;

                while ((bytesRead = in.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead); // write
                }
            } finally {
                if (fos != null) {
                    fos.close();
                }
                try {
                    in.close();
                } catch (IOException e) {
                    LOGGER.log(Level.FINE, e.getLocalizedMessage(), e);
                }
            }
            return dest;
        }
        return null;
    }


    /**
     * Load the properties from a properies file.
     * <p/>
     * If the file does not exist it will be created and an empty Properties object will be return.
     *
     * @param f a properties file.
     * @return a Properties Object.
     */
    public static Properties getPropertiesFromFile(final File f) throws IOException {
        if (f != null) {
            final Properties prop = new Properties();
            if (f.exists()) {
                final FileInputStream in = new FileInputStream(f);
                try {
                    prop.load(in);
                } finally {
                    in.close();
                }
            } else {
                if (!f.createNewFile()) {
                    throw new IOException("Failed to create file : " + f);
                }
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
            try {
                prop.store(out, "");
            } finally {
                out.close();
            }
        }
    }

    private static FileSystem createZipFileSystem(final Path zipPath,
                                                  boolean create)
            throws IOException {

        final URI uri = URI.create("jar:file:" + zipPath.toUri().getPath());

        final Map<String, String> env = new HashMap<>();
        if (create) {
            env.put("create", "true");
        }
        return FileSystems.newFileSystem(uri, env);
    }

    public static List<File> unzip(final Path zipFile, final Path destDir) throws IOException {
        final List<File> result = new ArrayList<>();
        final Path computedDestDir;
        if (destDir == null){

            final String fileExt = extractExtension(zipFile.toFile().getName());
            final String suffix = "." + (fileExt != null ? fileExt : "tmp");
            computedDestDir = File.createTempFile(zipFile.toFile().getName(), suffix).toPath();
        } else {
            computedDestDir = destDir;
        }

        if (Files.notExists(destDir)) {
            Files.createDirectories(destDir);
        }

        try (FileSystem zipFileSystem = createZipFileSystem(zipFile, false)) {
            final Path root = zipFileSystem.getPath("/");
            Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    final Path destFile = Paths.get(computedDestDir.toString(),
                            file.toString());
                    System.out.printf("Extracting file %s to %s\n", file, destFile);
                    Files.copy(file, destFile, StandardCopyOption.REPLACE_EXISTING);
                    result.add(destFile.toFile());
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    final Path dirToCreate = Paths.get(computedDestDir.toString(), dir.toString());
                    if (Files.notExists(dirToCreate)) {
                        Files.createDirectory(dirToCreate);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        return result;
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
        if ("file".equals(scheme)) {
            final File f = new File(u.getPath());
            if (f.isDirectory()) {
                return f;
            }
        } else if ("jar".equals(scheme) || "zip".equals(scheme)) {
            final File f = new File(System.getProperty("java.io.tmpdir") + "/geotoolkit");
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
                    final URI newUri = new URI(cleanedUri);
                    final InputStream i = newUri.toURL().openStream();
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
        if ("file".equals(scheme)) {
            return new File(u.getPath());
        }
        if ("jar".equals(scheme) || "zip".equals(scheme)) {
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
//
//    /**
//     * Searches in the Context ClassLoader for the named files and returns a
//     * {@code List<String>} with, for each named package,
//     *
//     * @param packages The names of the packages to scan in Java format, i.e.
//     *                   using the "." separator, may be null.
//     *
//     * @return A list of package names.
//     */
//    public static List<String> searchSubFiles(final String packagee) {
//        final List<String> result = new ArrayList<String>();
//        final ClassLoader classloader = Thread.currentThread().getContextClassLoader();
//
//        try {
//            final String fileP = packagee.replace('.', '/');
//            final Enumeration<URL> urls = classloader.getResources(fileP);
//            while (urls.hasMoreElements()) {
//                final URL url = urls.nextElement();
//                try {
//                    final URI uri = url.toURI();
//                    result.addAll(scan(uri, fileP, false));
//                } catch (URISyntaxException e) {
//                    LOGGER.log(Level.SEVERE, "URL, {0}cannot be converted to a URI", url);
//                }
//            }
//        } catch (IOException ex) {
//            LOGGER.log(Level.SEVERE, "The resources for the package {0}, could not be obtained. \ncause:{1}", new Object[]{packagee, ex.getMessage()});
//        }
//        return result;
//    }
//
//    /**
//     * Searches in the Context ClassLoader for the named packages and returns a
//     * {@code List<String>} with, for each named package,
//     *
//     * @param packages The names of the packages to scan in Java format, i.e.
//     *                   using the "." separator, may be null.
//     *
//     * @return A list of package names.
//     */
//    public static List<String> searchSubPackage(final String... packages) {
//        final List<String> result = new ArrayList<String>();
//        final ClassLoader classloader = Thread.currentThread().getContextClassLoader();
//        for (String p : packages) {
//            try {
//                final String fileP = p.replace('.', '/');
//                final Enumeration<URL> urls = classloader.getResources(fileP);
//                while (urls.hasMoreElements()) {
//                    final URL url = urls.nextElement();
//                    try {
//                        final URI uri = url.toURI();
//                        final List<String> scanned = scan(uri, fileP, true);
//                        for (final String s : scanned) {
//                            if (!result.contains(s)) {
//                                result.add(s);
//                            }
//                        }
//                    } catch (URISyntaxException e) {
//                        LOGGER.log(Level.SEVERE, "URL, {0} cannot be converted to a URI", url);
//                    }
//                }
//            } catch (IOException ex) {
//                LOGGER.log(Level.WARNING, "The resources for the package" + p + ", could not be obtained.", ex);
//            }
//        }
//        return result;
//    }
//
//    /**
//     * Scan a resource file (a JAR or a directory) to find the sub-package names of
//     * the specified "filePackageName"
//     *
//     * @param u The URI of the file.
//     * @param filePackageName The package to scan.
//     *
//     * @return a list of package names.
//     * @throws java.io.IOException
//     */
//    public static List<String> scan(final URI u, final String filePackageName, final boolean directory) throws IOException {
//        final List<String> result = new ArrayList<String>();
//        final String scheme = u.getScheme();
//        if ("file".equals(scheme)) {
//            final File f = new File(u.getPath());
//            if (f.isDirectory()) {
//                final List<String> scanned = scanDirectory(f, filePackageName, directory);
//                for (final String s : scanned) {
//                    if (!result.contains(s)) {
//                        result.add(s);
//                    }
//                }
//            } else if (!directory) {
//                result.add(f.getPath());
//            }
//        } else if ("jar".equals(scheme) || "zip".equals(scheme)) {
//            try {
//                final String brut = u.getSchemeSpecificPart();
//                final URI uri = URI.create(brut.replaceAll(" ", "%20"));
//                String jarFile = uri.getPath();
//                jarFile = jarFile.substring(0, jarFile.indexOf('!'));
//                final List<String> scanned = scanJar(new File(jarFile), filePackageName, directory);
//                for (final String s : scanned) {
//                    if (!result.contains(s)) {
//                        result.add(s);
//                    }
//                }
//
//            } catch (IllegalArgumentException ex) {
//                LOGGER.log(Level.WARNING, "unable to scan jar file: {0}\n cause:{1}", new Object[]{u.getSchemeSpecificPart(), ex.getMessage()});
//            }
//        }
//        return result;
//    }
//
    /**
     * Scan a directory to find the sub-package names of
     * the specified "parent" package
     *
     * @param root The root file (directory) of the package to scan.
     * @param parent the package name.
     *
     * @return a list of package names.
     */
    public static List<String> scanDirectory(final File root, final String parent, final boolean directory) {
        final List<String> result = new ArrayList<String>();
        for (File child : root.listFiles()) {
            if (child.isDirectory()) {
                if (directory) {
                    final String s = parent.replace('/', '.') + '.' + child.getName();
                    if (!result.contains(s)) {
                        result.add(s);
                    }
                }
                final List<String> scanned = scanDirectory(child, parent, directory);
                for (final String s : scanned) {
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

//    /**
//     * Scan a jar to find the sub-package names of
//     * the specified "parent" package
//     *
//     * @param file the jar file containing the package to scan
//     * @param parent the package name.
//     *
//     * @return a list of package names.
//     * @throws java.io.IOException
//     */
//    public static List<String> scanJar(final File file, final String parent, final boolean directory) throws IOException {
//        final List<String> result = new ArrayList<String>();
//        final JarFile jar = new JarFile(file);
//        final Enumeration<JarEntry> entries = jar.entries();
//        while (entries.hasMoreElements()) {
//            final JarEntry e = entries.nextElement();
//            if (e.isDirectory() && e.getName().startsWith(parent) && directory) {
//                String s = e.getName().replace('/', '.');
//                s = s.substring(0, s.length() - 1);
//                if (!result.contains(s)) {
//                    result.add(s);
//                }
//            } else if (!e.isDirectory() && e.getName().startsWith(parent) && !directory) {
//                String s = e.getName().replace('/', '.');
//                s = s.substring(0, s.length() - 1);
//                if (!result.contains(s)) {
//                    result.add(s);
//                }
//            }
//        }
//        return result;
//    }
//
    /**
     * Write the contents of a string into file.
     *
     * @param f the file to write into.
     * @return The file content as string
     * @throws IOException if the file does not exist or cannot be read.
     */
    public static void stringToFile(final File f, final String s) throws IOException {

        final BufferedWriter bw = new BufferedWriter(new FileWriter(f));
        try{
            bw.write(s);
        }finally{
            bw.close();
        }
    }

    /**
     * <p>This method allows to put resources into zip archive specified resource.</p>
     *
     */
    public static void zip(final Path zipLocation,final boolean createZip, final Path... toBeAdded) throws IOException, URISyntaxException {
        Map<String, String> env = new HashMap<String, String>();
        // check if file exists
        if (createZip && zipLocation.toFile().exists()){
            zipLocation.toFile().delete();
        }

        env.put("create", String.valueOf(createZip));
        // use a Zip filesystem URI
        URI fileUri = zipLocation.toUri(); // here
        URI zipUri = new URI("jar:" + fileUri.getScheme(), fileUri.getPath(), null);
        System.out.println(zipUri);
        // URI uri = URI.create("jar:file:"+zipLocation); // here creates the
        // zip
        // try with resource
        try (FileSystem zipfs = FileSystems.newFileSystem(zipUri, env)) {
            // Create internal path in the zipfs
            // copy a file into the zip file
            for (int i = 0; i < toBeAdded.length ; i++) {
                Files.copy(toBeAdded[i], zipfs.getPath("/" + toBeAdded[i].toFile().getName()), StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }


    public static void createZip(Path zipLocation, Path toBeAdded, String internalPath) throws Throwable {

    }




//
//    /**
//     * <p>This method allows to put resources into zip archive specified resource,
//     * without compression</p>
//     *
//     * @param zip The resource which files will be archived into. This argument must be
//     * instance of File, String (representing a path), or OutputStream. Cannot be null.
//     * @param checksum Checksum object (instance of Alder32 or CRC32).
//     * @param resources The files to compress. Tese objects can be File instances or
//     * String representing files paths, URL, URI or InputStream. Cannot be null.
//     * @throws IOException
//     */
//    public static void zip(final Object zip, final Checksum checksum, final Object... resources) throws IOException {
//        zip(zip, ZipOutputStream.STORED, 0, checksum, resources);
//    }
//
//    /**
//     * <p>This method allows to put resources into zip archive specified resource.</p>
//     *
//     * @param zip The resource which files will be archived into. This argument must be
//     * instance of File, String (representing a path), or OutputStream. Cannot be null.
//     * @param method The compression method is a static int constant from ZipOutputSteeam with
//     * two theorical possible values :
//     * <ul>
//     * <li>{@link ZipOutputStream#DEFLATED} to compress archive.</li>
//     * <li>{@link ZipOutputStream#STORED} to let the archive uncompressed (unsupported).</li>
//     * </ul>
//     * @param level The compression level is an integer between 0 (not compressed) to 9 (best compression).
//     * @param checksum Checksum object (instance of Alder32 or CRC32).
//     * @param resources The files to compress. Tese objects can be File instances or
//     * String representing files paths, URL, URI or InputStream. Cannot be null.
//     * @throws IOException
//     */
//    public static void zip(final Object zip, final int method, final int level, final Checksum checksum, final Object... resources)
//            throws IOException {
//
//        final BufferedOutputStream buf;
//        if (checksum != null) {
//            final CheckedOutputStream cos = new CheckedOutputStream(toOutputStream(zip), checksum);
//            buf = new BufferedOutputStream(cos);
//        } else {
//            buf = new BufferedOutputStream(toOutputStream(zip));
//        }
//
//        final ZipOutputStream zout = new ZipOutputStream(buf);
//        zout.setMethod(method);
//        zout.setLevel(level);
//        zipCore(zout, method, level, "", resources);
//        zout.close();
//    }
//
//    /**
//     * <p>This method creates an OutputStream with ZIP archive from the list of resources parameter.</p>
//     *
//     * @param zip OutputStrem on ZIP archive that will contain archives of resource files.
//     * @param method The compression method is a static int constant from ZipOutputSteeam with
//     * two theorical possible values :
//     * <ul>
//     * <li>{@link ZipOutputStream#DEFLATED} to compress archive.</li>
//     * <li>{@link ZipOutputStream#STORED} to let the archive uncompressed (unsupported).</li>
//     * </ul>
//     * @param level The compression level is an integer between 0 (not compressed) to 9 (best compression).
//     * @param resources The files to compress. Tese objects can be File instances or
//     * String representing files paths, URL, URI or InputStream. Cannot be null.
//     * @throws IOException
//     */
//    private static void zipCore(final ZipOutputStream zout, final int method, final int level, final String entryPath, final Object... resources)
//            throws IOException {
//
//        final byte[] data = new byte[BUFFER];
//        final CRC32 crc = new CRC32();
//        boolean stored = false;
//
//        if (ZipOutputStream.STORED == method) {
//            stored = true;
//            for (Object resource : resources) {
//                if (!(resource instanceof File)) {
//                    throw new IllegalArgumentException("This compression is supported with File resources only.");
//                }
//            }
//        } else if (ZipOutputStream.DEFLATED != method) {
//            throw new IllegalArgumentException("This compression method is not supported.");
//        }
//
//        if (Double.isNaN(level) || Double.isInfinite(level) || level > 9 || level < 0) {
//            throw new IllegalArgumentException("Illegal compression level.");
//        }
//
//        for (int i = 0; i < resources.length; i++) {
//            final ZipEntry entry = new ZipEntry(entryPath + getFileName(resources[i]));
//            if (stored) {
//                final File file = (File) resources[i];
//                entry.setCompressedSize(file.length());
//                entry.setSize(file.length());
//                entry.setCrc(crc.getValue());
//            }
//
//            if (resources[i] instanceof File && ((File) resources[i]).isDirectory()) {
//                final String zipName = new StringBuilder(entryPath).append(((File) resources[i]).getName()).
//                        append(((File) resources[i]).isDirectory() ? '/' : "").toString();
//                zipCore(zout, method, level, zipName, (Object[]) ((File) resources[i]).listFiles());
//                continue;
//            }
//
//            zout.putNextEntry(entry);
//
//            BufferedInputStream buffi = new BufferedInputStream(toInputStream(resources[i]), BUFFER);
//            if (stored) {
//                try {
//                    crc.reset();
//                    int bytesRead;
//                    while ((bytesRead = buffi.read(data, 0, BUFFER)) != -1) {
//                        crc.update(data, 0, bytesRead);
//                    }
//                } finally {
//                    buffi.close();
//                }
//                buffi = new BufferedInputStream(toInputStream(resources[i]), BUFFER);
//            }
//
//            try {
//                int count;
//                while (-1 != (count = buffi.read(data, 0, BUFFER))) {
//                    zout.write(data, 0, count);
//                }
//            } finally {
//                zout.closeEntry();
//                buffi.close();
//            }
//        }
//
//    }
//
//    /**
//     * <p>This method extracts a ZIP archive into the directory which contents it.</p>
//     *
//     * @param zip The archive parameter as File, URL, URI, InputStream or String path.
//     * This argument cannot be null.
//     * @param checksum Checksum object (instance of Alder32 or CRC32).
//     * @throws IOException
//     */
//    public static List<File> unzip(final Object zip, final Checksum checksum) throws IOException {
//        return unzip(zip, getParent(zip), checksum);
//    }
//
//    /**
//     * <p>This method extract a ZIP archive into the specified location.</p>
//     *
//     * <p>ZIP resource parameter
//     *
//     * @param zip The archive parameter can be instance of InputStream, File,
//     * URL, URI or a String path. This argument cannot be null.
//     * @param resource The resource where archive content will be extracted.
//     * This resource location can be specified as instance of File or a String path.
//     * This argument cannot be null.
//     * @param checksum Checksum object (instance of Alder32 or CRC32).
//     * @throws IOException
//     */
//    public static List<File> unzip(final Object zip, final Object resource, final Checksum checksum)
//            throws IOException {
//        final BufferedInputStream buffi;
//        if (checksum != null) {
//            final CheckedInputStream cis = new CheckedInputStream(toInputStream(zip), checksum);
//            buffi = new BufferedInputStream(cis);
//        } else {
//            buffi = new BufferedInputStream(toInputStream(zip));
//        }
//        return unzipCore(buffi, resource);
//    }
//
//    /**
//     * <p>This method extract a ZIP archive from an InputStream, into directory
//     * whose path is indicated by resource object.</p>
//     *
//     * @param zip InputStream on ZIP resource that contains resources to extract.
//     * @param resource The resource where files will be extracted.
//     * Must be instance of File or a String path. This argument cannot be null.
//     * @throws IOException
//     */
//    private static List<File> unzipCore(final InputStream zip, final Object resource)
//            throws IOException {
//
//        final byte[] data = new byte[BUFFER];
//        final ZipInputStream zis = new ZipInputStream(zip);
//
//        final List<File> unzipped = new ArrayList<File>();
//
//        try {
//            final String extractPath = getPath(resource);
//            ZipEntry entry;
//            while ((entry = zis.getNextEntry()) != null) {
//                final File file = new File(extractPath, entry.getName());
//                if (entry.isDirectory()) {
//                    file.mkdirs();
//                    continue;
//                }
//                file.getParentFile().mkdirs();
//                unzipped.add(file);
//                final OutputStream fos = toOutputStream(file);
//                final BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);
//                try {
//                    int count;
//                    while ((count = zis.read(data, 0, BUFFER)) != -1) {
//                        dest.write(data, 0, count);
//                    }
//                    dest.flush();
//                } finally {
//                    dest.close();
//                }
//            }
//        } finally {
//            zis.close();
//        }
//
//        return unzipped;
//    }
//
//    /**
//     * This method returns a file list from a Zip file.
//     *
//     * @param is InputStream of the Zip File.
//     * @return List of Files.
//     */
//    public static List<File> unZipFileList(final InputStream is) {
//        ZipInputStream in = null;
//        final List<File> files = new ArrayList<File>();
//        try {
//            in = new ZipInputStream(is);
//            ZipEntry zi;
//            while ((zi = in.getNextEntry()) != null) {
//                if (!zi.isDirectory()) {
//                    final String fileName = removeDirectory(zi.getName());
//                    final String fileExt = extractExtension(zi.getName());
//                    final String suffix = "." + (fileExt != null ? fileExt : "tmp");
//
//                    final File f = File.createTempFile(fileName, suffix);
//
//                    final FileOutputStream out = new FileOutputStream(f);
//                    int c = 0;
//                    while ((c = in.read()) != -1) {
//                        out.write(c);
//                    }
//                    out.close();
//                    files.add(f);
//                }
//            }
//            in.close();
//        } catch (FileNotFoundException ex) {
//            LOGGER.log(Level.WARNING, null, ex);
//        } catch (IOException ex) {
//            LOGGER.log(Level.WARNING, null, ex);
//        } finally {
//            try {
//                in.close();
//            } catch (IOException ex) {
//                LOGGER.log(Level.WARNING, null, ex);
//            }
//        }
//        return files;
//    }
//
//    /**
//     * Remove the directory names before the file name.
//     *
//     * @param fileName A zipEntry file name.
//     * @return The zipEntry name without the directory structure and the file extention if exist.
//     */
//    private static String removeDirectory(final String fileName) {
//        final int index = fileName.lastIndexOf('/');
//        final int dotIndex = fileName.lastIndexOf('.');
//        if (index != -1) {
//            if(dotIndex != -1){
//                return fileName.substring(index + 1, dotIndex);
//            }else{
//                return fileName.substring(index + 1);
//            }
//        }
//        return fileName;
//    }
//
    /**
     * Extract the file extension from a string.
     * If the there is no extension, return null.
     *
     * @param fileName A zipEntry file name.
     * @return The zipEntry extension, or null if not found.
     */
    private static String extractExtension(final String fileName) {
        final int dotIndex = fileName.lastIndexOf('.');
        if(dotIndex != -1){
            return fileName.substring(dotIndex + 1);
        }else{
            return null;
        }
    }
//
//
//    /**
//     * Return an unic name for the file in the specified list fileNames.
//     *
//     * @param fileName a file name.
//     * @param fileNames a list of unic file name
//     * @return an unic name for the file in the list fileNames.
//     */
//    private static String getUnicFileName(final String fileName, final List<String> fileNames) {
//        int i = 0;
//        String newName = fileName + i;
//        while (fileNames.contains(newName)) {
//            i++;
//            newName = newName.substring(0, newName.length() - 1);
//            newName = newName + i;
//        }
//        return newName;
//    }
//
//    /**
//     * <p>This method gives an OutputStream to write resource instance of File,
//     * or a String path. Resource can be instance of OutputStream and in
//     * this case, the method returns the same OutputStream.</p>
//     *
//     * @param resource Resource instance of OutputStream, File, or a String Path.
//     * This argument cannot be null.
//     * @return An OutputStream to write zip the given resource.
//     * @throws IOException
//     */
//    private static OutputStream toOutputStream(final Object resource)
//            throws IOException {
//        ensureNonNull("resource", resource);
//
//        OutputStream fot = null;
//        if (resource instanceof File) {
//            fot = new FileOutputStream((File) resource);
//        } else if (resource instanceof String) {
//            fot = new FileOutputStream(new File((String) resource));
//        } else if (resource instanceof OutputStream) {
//            fot = (OutputStream) resource;
//        } else if (resource != null) {
//            throw new IllegalArgumentException("This argument must be instance of File, "
//                    + "String (representing a path) or OutputStream.");
//        }
//        return fot;
//    }
//
//    /**
//     * <p>This method gives an InputStream to read resource instance of File,
//     * URL, URI or a String path. Resource can be instance of InputStream and in
//     * this case, the method returns the same InputStream.</p>
//     *
//     * @param resource Resource instance of InputStream, File, URL, URI, or a String path.
//     * This argument cannot be null.
//     * @return An InputStream to read the given resource.
//     * @throws IOException
//     */
//    private static InputStream toInputStream(final Object resource)
//            throws IOException {
//        ensureNonNull("resource", resource);
//
//        InputStream fit = null;
//        if (resource instanceof File) {
//            fit = new FileInputStream((File) resource);
//        } else if (resource instanceof URL) {
//            fit = ((URL) resource).openStream();
//        } else if (resource instanceof URI) {
//            fit = ((URI) resource).toURL().openStream();
//        } else if (resource instanceof String) {
//            fit = new FileInputStream(new File((String) resource));
//        } else if (resource instanceof InputStream) {
//            fit = (InputStream) resource;
//        } else if (resource != null) {
//            throw new IllegalArgumentException("This argument must be instance of File, "
//                    + "String (representing a path), URL, URI or InputStream.");
//        }
//        return fit;
//    }
//
//    /**
//     * <p>This method returns the path of a given resource which can be
//     * instance of File, URL, URI or String. Returns null in other cases.</p>
//     *
//     * @param resource instance of File, URL, URI, or String path.
//     * @return The resource path.
//     * @throws MalformedURLException
//     */
//    private static String getPath(final Object resource)
//            throws MalformedURLException {
//
//        String extractPath = null;
//        if (resource instanceof File) {
//            extractPath = ((File) resource).getPath();
//        } else if (resource instanceof URL) {
//            extractPath = ((URL) resource).getPath();
//        } else if (resource instanceof URI) {
//            extractPath = (((URI) resource).toURL()).getPath();
//        } else if (resource instanceof String) {
//            extractPath = (String) resource;
//        }
//        return extractPath;
//    }
//
//    /**
//     * <p>This method returs the path of the resource container (parent directory).
//     * Resource can be an instance of File, URL, URI or String path.
//     * Return snull in other cases.</p>
//     *
//     * @param resource instance of File, URL, URI, or String path.
//     * @return The path of the resource container.
//     * @throws MalformedURLException
//     */
//    private static String getParent(final Object resource)
//            throws MalformedURLException {
//
//        String extractPath = null;
//        if (resource instanceof File) {
//            extractPath = ((File) resource).getParent();
//        } else {
//            extractPath = getPath(resource);
//            extractPath = extractPath.substring(0, extractPath.lastIndexOf(File.separator) + 1);
//        }
//        return extractPath;
//    }
//
//    /**
//     * <p>This method gives the resource file name.
//     * Resource can be an instance of File, URL, URI or String path.
//     * Return snull in other cases.</p>
//     *
//     * @param resource instance of File, URL, URI, or String path.
//     * @return
//     * @throws MalformedURLException
//     */
//    private static String getFileName(final Object resource)
//            throws MalformedURLException {
//
//        String fileName = null;
//        if (resource instanceof File) {
//            fileName = ((File) resource).getName();
//        } else {
//            fileName = getPath(resource);
//            fileName = fileName.substring(fileName.lastIndexOf(File.separator) + 1, fileName.length());
//        }
//        return fileName;
//    }
//
//    /**
//     * Search a parent folder for input file, one which actually exists on the file system.
//     * @param child The file to search parent for.
//     * @return the first exising parent folder of the given file, or null if we cannot find any.
//     */
//    public static File getExistingParent(final File child) {
//        final File parent = child.getParentFile();
//        if (parent != null && !parent.exists()) {
//            return getExistingParent(parent);
//        }
//        return parent;
//    }
//
//    /**
//     * Return a recursive list of files under given directory.
//     * @param directory start directory. If directory is a File and not a directory,
//     *                  file list will be returned with only given directory.
//     * @return a list of all files recursively from start directory File. Or only directory file if
//     * given directory File is empty or not a directory.
//     */
//    public static List<File> scanDirectory(File directory) {
//        return scanDirectory(directory, null);
//    }
//
    /**
     * Return a recursive list of files under given directory.
     * @param directory start directory. If directory is a File and not a directory,
     *                  and is accepted by filter, file list will be returned with only given directory.
     * @param filter FileFilter
     * @return a list of all files recursively from start directory File. Or only directory file if
     * given directory File is empty or not a directory.
     */
    public static List<File> scanDirectory(File directory, FileFilter filter) {
        final List<File> files = new ArrayList<File>();

        if (directory != null) {
            if (directory.isDirectory()) {
                scanDirectory(directory, files, filter);
            } else {
                if (filter == null || (filter.accept(directory))) {
                    files.add(directory);
                }
            }
        }
        return files;
    }

    /**
     * Recursive implementation of scanDirectory() method.
     * @param root
     * @param files
     * @param filter FileFilter
     */
    private static void scanDirectory(File root, List<File> files, FileFilter filter) {
        if (root != null) {
            if (filter == null || (filter.accept(root))) {
                files.add(root);
            }

            for (File child : root.listFiles()) {
                if (child.isDirectory()) {
                    scanDirectory(child, files, filter);
                } else {
                    if (filter == null || (filter.accept(child))) {
                        files.add(child);
                    }
                }
            }
        }
    }
}
