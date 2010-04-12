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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
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
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.util.logging.Logging;


/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class FileUtilities {

    private static final Logger LOGGER = Logging.getLogger(FileUtilities.class);

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
                    LOGGER.severe("URL, " + url + "cannot be converted to a URI");
                }
            }
        } catch (IOException ex) {
            LOGGER.severe("The resources for the package" + packagee + ", could not be obtained");
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
                    LOGGER.severe("URL, " + url + "cannot be converted to a URI");
                }
            }
        } catch (IOException ex) {
            LOGGER.severe("The resources for the package" + packagee + ", could not be obtained");
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
                    LOGGER.severe("URL, " + url + "cannot be converted to a URI");
                }
            }
        } catch (IOException ex) {
            LOGGER.severe("The resources for the package" + packagee + ", could not be obtained");
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
                        result.addAll(scan(uri, fileP, true));
                    } catch (URISyntaxException e) {
                        LOGGER.severe("URL, " + url + "cannot be converted to a URI");
                    }
                }
            } catch (IOException ex) {
                LOGGER.severe("The resources for the package" + p + ", could not be obtained");
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
                result.addAll(scanDirectory(f, filePackageName, directory));
            } else if (!directory) {
                LOGGER.info("added :" + f.getPath());
                result.add(f.getPath());
            }
        } else if (scheme.equals("jar") || scheme.equals("zip")) {
            try {
                final URI jarUri = URI.create(u.getSchemeSpecificPart());
                String jarFile = jarUri.getPath();
                jarFile = jarFile.substring(0, jarFile.indexOf('!'));
                result.addAll(scanJar(new File(jarFile), filePackageName, directory));

            } catch (IllegalArgumentException ex) {
                LOGGER.warning("unable to scan jar file: " + u.getSchemeSpecificPart() + "\n cause:" + ex.getMessage());
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
                    result.add(parent.replace('/', '.') + '.' + child.getName());
                }
                result.addAll(scanDirectory(child, parent, directory));
            } else if (!directory) {
                LOGGER.info("added :" + child.getPath());
                result.add(child.getPath());
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
                result.add(s);
            } else if (!e.isDirectory() && e.getName().startsWith(parent) && !directory) {
                String s = e.getName().replace('/', '.');
                s = s.substring(0, s.length() - 1);
                result.add(s);
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

}
