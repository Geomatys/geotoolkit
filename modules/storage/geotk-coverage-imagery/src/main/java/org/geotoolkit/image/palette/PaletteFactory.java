/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.image.palette;

import java.io.*;
import java.util.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.awt.Color;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import javax.imageio.IIOException;

import org.geotoolkit.io.LineFormat;
import org.geotoolkit.io.DefaultFileFilter;
import org.apache.sis.internal.storage.IOUtilities;
import org.apache.sis.util.logging.Logging;
import org.apache.sis.util.collection.WeakHashSet;
import org.geotoolkit.resources.Errors;
import org.apache.sis.util.resources.IndexedResourceBundle;


/**
 * A factory for {@linkplain IndexColorModel index color models} created from RGB values listed
 * in files. The palette definition files are text files containing an arbitrary number of lines,
 * each line containing RGB components ranging from 0 to 255 inclusive. An optional fourth column
 * may be provided for alpha components. Empty lines and lines starting with the {@code '#'}
 * character are ignored. Example:
 *
 * {@preformat text
 *     # RGB codes for SeaWiFs images
 *     # (chlorophylle-a concentration)
 *     033   000   096
 *     032   000   097
 *     031   000   099
 *     030   000   101
 *     029   000   102
 *     028   000   104
 *     026   000   106
 *     025   000   107
 *     etc...
 * }
 *
 * The number of RGB codes doesn't have to match the target {@linkplain IndexColorModel#getMapSize
 * color map size}. RGB codes will be automatically interpolated as needed. For example it is legal
 * to declare only 2 colors, {@linkplain Color#BLUE blue} and {@linkplain Color#RED red} for instance.
 * If an image needs 256 colors, then all needed colors will be interpolated from blue to red, with
 * blue at index 0 and red at index 255.
 * <p>
 * The {@linkplain #getDefault() default instance} provides the following color palettes. More
 * details about those palettes are provided in <a href="doc-files/palettes.html">this page</a>.
 * <p>
 *  <blockquote><table border="1" cellpadding="0" cellspacing="0">
 *    <tr><td><img width="128" height="24" alt="palette" title="grayscale"         src="doc-files/grayscale.png"></td>
 *        <td><img width="128" height="24" alt="palette" title="green-yellow-red"  src="doc-files/green-yellow-red.png"></td>
 *        <td><img width="128" height="24" alt="palette" title="white-cyan-red"    src="doc-files/white-cyan-red.png"></td>
 *        <td><img width="128" height="24" alt="palette" title="dem"               src="doc-files/dem.png"></td></tr>
 *    <tr><td><img width="128" height="24" alt="palette" title="blue"              src="doc-files/blue.png"></td>
 *        <td><img width="128" height="24" alt="palette" title="green-beige-red"   src="doc-files/green-beige-red.png"></td>
 *        <td><img width="128" height="24" alt="palette" title="bell"              src="doc-files/bell.png"></td></tr>
 *    <tr><td><img width="128" height="24" alt="palette" title="cyan-blue"         src="doc-files/cyan-blue.png"></td>
 *        <td><img width="128" height="24" alt="palette" title="blue-beige-red"    src="doc-files/blue-beige-red.png"></td>
 *        <td><img width="128" height="24" alt="palette" title="rainbow"           src="doc-files/rainbow.png"></td></tr>
 *    <tr><td><img width="128" height="24" alt="palette" title="green-inverse"     src="doc-files/green-inverse.png"></td>
 *        <td><img width="128" height="24" alt="palette" title="blue-red"          src="doc-files/blue-red.png"></td>
 *        <td><img width="128" height="24" alt="palette" title="rainbow-t"         src="doc-files/rainbow-t.png"></td></tr>
 *    <tr><td><img width="128" height="24" alt="palette" title="brown-inverse"     src="doc-files/brown-inverse.png"></td>
 *        <td><img width="128" height="24" alt="palette" title="red-blue"          src="doc-files/red-blue.png"></td>
 *        <td><img width="128" height="24" alt="palette" title="rainbow-c"         src="doc-files/rainbow-c.png"></td></tr>
 *    <tr><td><img width="128" height="24" alt="palette" title="red-inverse"       src="doc-files/red-inverse.png"></td>
 *        <td><img width="128" height="24" alt="palette" title="yellow-green-blue" src="doc-files/yellow-green-blue.png"></td>
 *        <td><img width="128" height="24" alt="palette" title="SeaWiFS"           src="doc-files/SeaWiFS.png"></td></tr>
 *  </table></blockquote>
 *
 * {@section Adding custom palettes}
 * To add custom palettes, create a subclass of {@code PaletteFactory} like below:
 *
 * {@preformat java
 *     public class MyPalettes extends PaletteFactory {
 *         public MyPalettes() {
 *             // The call below uses the default options for convenience,
 *             // but we could use an other constructor with more parameters.
 *             super();
 *         }
 *     }
 *
 *     // Optionally, we could override getResourceAsStream(String) if we wanted
 *     // to fetch the color codes from a custom source (e.g. from a database).
 * }
 *
 * In the directory that contain the {@code MyPalettes.class} file, create a "{@code colors}"
 * sub-directory and put the palette definitions (as text files with the "{@code .pal}" suffix)
 * in that directory. The directory and the file suffix can be changed using one of the
 * constructors expecting arguments.
 *
 * Finally, declare the fully-qualified name of {@code MyPalettes} in the following file:
 *
 * {@preformat text
 *     META-INF/services/org.geotoolkit.image.io.PaletteFactory
 * }
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.17
 *
 * @since 1.2
 * @module
 */
public class PaletteFactory {
    /**
     * The file which contains a list of available color palettes. This file is optional.
     * If such file exists in the same directory than the one that contains the palettes,
     * this file will be used by {@link #getAvailableNames()}.
     */
    private static final String LIST_FILE = "list.txt";

    /**
     * The default sub-directory, relative to the {@code PaletteFactory} class directory.
     */
    private static final File DEFAULT_DIRECTORY = new File("colors");

    /**
     * The default palette factory.
     */
    private static PaletteFactory defaultFactory;

    /**
     * The fallback factory, or {@code null} if there is none. The fallback factory
     * will be queried if a palette was not found in current factory.
     * <p>
     * This field should be considered as final. It is modified by {@link #scanForPlugins} only.
     */
    private PaletteFactory fallback;

    /**
     * The class loader from which to load the palette definition files. If {@code null} and
     * {@link #loader} is null as well, then loading will occurs from the system current
     * working directory.
     */
    private final ClassLoader classloader;

    /**
     * An alternative to {@link #classloader} for loading resources. At most one of
     * {@code classloader} and {@code loader} can be non-null. If both are {@code null},
     * then loading will occurs from the system current working directory.
     */
    private final Class<?> loader;

    /**
     * The base directory from which to search for palette definition files.
     * If {@code null}, then the working directory ({@code "."}) is assumed.
     */
    private final File directory;

    /**
     * The file extension.
     */
    private final String extension;

    /**
     * The charset to use for parsing files, or {@code null} for the current default.
     */
    private final Charset charset;

    /**
     * The locale to use for parsing files, or {@code null} for the current default.
     */
    private final Locale locale;

    /**
     * The locale to use for formatting error messages, or {@code null} for the current default.
     * This locale is informative only; there is no guarantee that this locale will be really used.
     */
    private transient ThreadLocal<Locale> warningLocales;

    /**
     * The set of palettes already created and not yet garbage-collected. The {@code getPalette}
     * method implementations shall return {@linkplain WeakHashSet#unique unique} {@code Palette}
     * instances as below:
     *
     * {@preformat java
     *     public Palette getPalette(...) {
     *         Palette palette = ...;
     *         palette = palettes.unique(palette); // Ensure that the instance is unique.
     *         return palette;
     *     }
     * }
     *
     * The purpose is to share existing {@link ColorModel} instances when possible, since they
     * may be big (up to 256 kilobytes for an {@link IndexColorModel} with 16 bits integers).
     * This mechanism works provided that {@link Palette#createImageTypeSpecifier()} lazily
     * create the color model only when first invoked.
     *
     * @since 3.11
     */
    protected final WeakHashSet<Palette> palettes = new WeakHashSet<>(Palette.class);

    /**
     * The set of palettes protected from garbage collection. We protect a palette as long as it
     * holds a reference to a color model - this is necessary in order to prevent multiple creation
     * of the same {@link IndexColorModel}. The references are cleaned by {@link PaletteDisposer}.
     */
    final Set<Palette> protectedPalettes = new HashSet<>();

    /**
     * Gets the default palette factory. The returned factory can provide the palettes listed in
     * the <a href="#skip-navbar_top">class javadoc</a>, together with the palettes defined by any
     * custom factories registered in the way defined by the class javadoc.
     *
     * {@note The scan for custom factories is performed only when this method is first invoked. If
     *        a new scan is desired (for example because new JAR files are added on the classpath,
     *        then <code>scanForPlugins(null)</code> should be invoked explicitly.}
     *
     * If custom factories are found and if they define some palettes of the same name than the
     * ones provided by the build-in factory, then the custom palettes have precedence over the
     * build-in ones.
     *
     * @return The default palette factory.
     */
    public static synchronized PaletteFactory getDefault() {
        if (defaultFactory == null) {
            defaultFactory = new PaletteFactory();
            scanForPlugins(null);
        }
        return defaultFactory;
    }

    /**
     * Lookups for custom palette factories on the classpath. This method is automatically
     * invoked by {@link #getDefault()} and doesn't need to be invoked explicitly, unless
     * new JAR files have been added on the classpath or unless some specific class loader
     * needs to be used.
     * <p>
     * Custom factories shall be declared in the following file:
     *
     * {@preformat text
     *   META-INF/services/org.geotoolkit.image.io.PaletteFactory
     * }
     *
     * Newly discovered factories have precedence over the old ones.
     *
     * @param loader The class loader to use, or {@code null} for the default one.
     *
     * @since 2.4
     */
    public static synchronized void scanForPlugins(final ClassLoader loader) {
        final Set<Class<? extends PaletteFactory>> existings = new HashSet<>();
        for (PaletteFactory p=getDefault(); p!=null; p=p.fallback) {
            existings.add(p.getClass());
        }
        final ServiceLoader<PaletteFactory> factories = (loader == null) ?
                ServiceLoader.load(PaletteFactory.class) :
                ServiceLoader.load(PaletteFactory.class, loader);
        for (final PaletteFactory factory : factories) {
            /*
             * Adds the scanned factory to the chain. There is no public method for doing that
             * because PaletteFactory is quasi-immutable except for this method which modifies
             * the fallback field. It is okay in this context since we just created the factory
             * instance.
             */
            if (existings.add(factory.getClass())) {
                PaletteFactory tail = factory;
                while (tail.fallback != null) {
                    tail = tail.fallback;
                }
                tail.fallback = defaultFactory;
                defaultFactory = factory;
            }
        }
    }

    /**
     * Constructs a default palette factory using this {@linkplain #getClass object class} for
     * loading palette definition files. The default directory is {@code "colors"} relative to
     * the directory of the subclass extending this class. The character encoding is ISO-8859-1
     * and the locale is {@linkplain Locale#US US}.
     * <p>
     * This constructor is protected because is it merely a convenience for subclasses registering
     * themselves as a service in the following file (see <a href="#skip-navbar_top">class javadoc</a>
     * for more details):
     *
     * {@preformat text
     *     META-INF/services/org.geotoolkit.image.io.PaletteFactory
     * }
     *
     * Users should invoke {@link #getDefault} instead, which will return a shared instance
     * of this class together with any custom factories found on the class path.
     *
     * @since 2.5
     */
    protected PaletteFactory() {
        this.classloader = null;
        this.loader      = getClass();
        this.directory   = DEFAULT_DIRECTORY;
        this.extension   = ".pal";
        this.charset     = Charset.forName("ISO-8859-1");
        this.locale      = Locale.US;
    }

    /**
     * Constructs a palette factory which will load the palette definition files from the specified
     * directory. No {@linkplain ClassLoader class loader} is used, i.e. the palettes are read as
     * ordinary file relative to the current working directory (not the classpath).
     *
     * @param directory The base directory for palette definition files relative to current
     *                  directory, or {@code null} for {@code "."}.
     * @param extension File name extension, or {@code null} if there is no extension
     *                  to add to filename. If non-null, this extension will be automatically
     *                  appended to filename. It should starts with the {@code '.'} character.
     * @param charset   The charset to use for parsing files, or {@code null} for the default.
     * @param locale    The locale to use for parsing files, or {@code null} for the default.
     *
     * @since 2.5
     */
    public PaletteFactory(final File    directory,
                          final String  extension,
                          final Charset charset,
                          final Locale  locale)
    {
        this.classloader = null;
        this.loader      = null;
        this.directory   = directory;
        this.extension   = startWithDot(extension);
        this.charset     = charset;
        this.locale      = locale;
    }

    /**
     * Constructs a palette factory using an optional {@linkplain ClassLoader class loader}
     * for loading palette definition files. If {@code loader} is non-null, the definitions
     * will be read using {@link ClassLoader#getResource(String)}, which imply that the
     * {@code directory} argument is relative to the root packages on the classpath.
     *
     * @param fallback  An optional fallback factory, or {@code null} if there is none. The fallback
     *                  factory will be queried if a palette was not found in the current factory.
     * @param loader    An optional class loader to use for loading the palette definition files.
     *                  If {@code null}, loading will occurs from the system current working
     *                  directory.
     * @param directory The base directory for palette definition files. It may be a Java package
     *                  if a {@code loader} were specified. If {@code null}, then {@code "."} is
     *                  assumed.
     * @param extension File name extension, or {@code null} if there is no extension
     *                  to add to filename. If non-null, this extension will be automatically
     *                  appended to filename. It should starts with the {@code '.'} character.
     * @param charset   The charset to use for parsing files, or {@code null} for the default.
     * @param locale    The locale to use for parsing files, or {@code null} for the default.
     */
    public PaletteFactory(final PaletteFactory fallback,
                          final ClassLoader    loader,
                          final File           directory,
                          final String         extension,
                          final Charset        charset,
                          final Locale         locale)
    {
        this.fallback    = fallback;
        this.classloader = loader;
        this.loader      = null;
        this.directory   = directory;
        this.extension   = startWithDot(extension);
        this.charset     = charset;
        this.locale      = locale;
    }

    /**
     * Constructs a palette factory using an optional {@linkplain Class class} for loading
     * palette definition files. If {@code loader} is non-null, the definitions will be read
     * using {@link Class#getResource(String)}, which imply that the {@code directory} argument
     * is relative to the package of the {@code PaletteFactory} subclass.
     * <p>
     * Using a {@linkplain Class class} instead of a {@linkplain ClassLoader class loader} can
     * avoid security issue on some platforms (some platforms do not allow to load resources
     * from a {@code ClassLoader} because it would make possible to load from the root package).
     *
     * @param fallback  An optional fallback factory, or {@code null} if there is none. The fallback
     *                  factory will be queried if a palette was not found in the current factory.
     * @param loader    An optional class to use for loading the palette definition files.
     *                  If {@code null}, loading will occurs from the system current working
     *                  directory.
     * @param directory The base directory for palette definition files. It may be a Java package
     *                  if a {@code loader} were specified. If {@code null}, then {@code "."} is
     *                  assumed.
     * @param extension File name extension, or {@code null} if there is no extension
     *                  to add to filename. If non-null, this extension will be automatically
     *                  appended to filename. It should starts with the {@code '.'} character.
     * @param charset   The charset to use for parsing files, or {@code null} for the default.
     * @param locale    The locale to use for parsing files. or {@code null} for the default.
     *
     * @since 2.2
     */
    public PaletteFactory(final PaletteFactory fallback,
                          final Class<?>       loader,
                          final File           directory,
                          final String         extension,
                          final Charset        charset,
                          final Locale         locale)
    {
        this.fallback    = fallback;
        this.classloader = null;
        this.loader      = loader;
        this.directory   = directory;
        this.extension   = startWithDot(extension);
        this.charset     = charset;
        this.locale      = locale;
    }

    /**
     * Ensures that the given string starts with a dot.
     */
    private static String startWithDot(String extension) {
        if (extension != null && !extension.startsWith(".")) {
            extension = '.' + extension;
        }
        return extension;
    }

    /**
     * Sets the locale to use for formatting warning or error messages. This is typically the
     * {@linkplain javax.imageio.ImageReader#getLocale image reader locale}. This locale is
     * informative only; there is no guarantee that this locale will be really used.
     * <p>
     * This method sets the locale for the current thread only. It is safe to use this palette
     * factory concurrently in many threads, each with their own locale.
     *
     * @param warningLocale The locale for warning or error messages, or {@code null} for the
     *        {@linkplain Locale#getDefault() default locale}.
     *
     * @since 2.4
     */
    public synchronized void setWarningLocale(final Locale warningLocale) {
        if (warningLocale != null) {
            if (warningLocales == null) {
                warningLocales = warningLocales();
            }
            warningLocales.set(warningLocale);
        } else if (warningLocales != null) {
            warningLocales.remove();
        }
    }

    /**
     * Gets the {@linkplain #warningLocales} from the fallback or create a new one. This
     * method invokes itself recursively in order to assign the same {@link ThreadLocal}
     * to every factories in the chain.
     */
    private synchronized ThreadLocal<Locale> warningLocales() {
        if (warningLocales == null) {
            warningLocales = (fallback != null) ? fallback.warningLocales() : new ThreadLocal<Locale>();
        }
        return warningLocales;
    }

    /**
     * Returns the locale set by the last invocation to {@link #setWarningLocale(Locale)}
     * in the current thread.
     *
     * @return The current locale to use for warning messages.
     *
     * @since 2.4
     */
    public synchronized Locale getWarningLocale() {
        final ThreadLocal<Locale> warningLocales = this.warningLocales;
        if (warningLocales != null) {
            return warningLocales.get();
        }
        return null;
    }

    /**
     * Returns the resources for formatting error messages.
     */
    final IndexedResourceBundle getErrorResources() {
        return Errors.getResources(getWarningLocale());
    }

    /**
     * Returns an input stream for reading the specified resource. The default
     * implementation delegates to either {@link Class#getResourceAsStream(String) Class} or
     * {@link ClassLoader#getResourceAsStream(String) ClassLoader} {@code getResourceAsStream(String)}
     * method, depending on the type of the {@code loader} argument given to the constructor.
     * Subclasses may override this method if a more elaborated mechanism is wanted for fetching
     * resources. This is sometime required in the context of applications using particular class
     * loaders.
     *
     * @param name The name of the resource to load, constructed as {@code directory} + {@code name}
     *             + {@code extension} where <var>directory</var> and <var>extension</var> were
     *             specified to the constructor, while {@code name} was given to the
     *             {@link #getPalette} method.
     * @return The input stream, or {@code null} if the resources was not found.
     *
     * @since 2.3
     */
    protected InputStream getResourceAsStream(final String name) {
        if (loader != null) {
            return loader.getResourceAsStream(name);
        }
        if (classloader != null) {
            return classloader.getResourceAsStream(name);
        }
        return null;
    }

    /**
     * Returns the set of available palette names. Any item in this set can be specified as
     * argument to the {@link #getPalette(String, int)} method.
     * <p>
     * If this method can not infer the names of available palettes, then it returns {@code null}.
     * Note that this is not the same than an empty set, which means "there is no palette". The
     * null return value means that the set is unknown.
     *
     * @return The list of available palette name, or {@code null} if this method
     *         is unable to fetch this information.
     */
    public Set<String> getAvailableNames() {
        final Set<String> names = new LinkedHashSet<>();
        return getAvailableNames(names) ? names : null;
    }

    /**
     * Adds available palette names to the specified collection.
     *
     * @param  names The collection where to add the name of the palettes.
     * @return {@code true} if this method has been able to find some informations about palettes.
     */
    private boolean getAvailableNames(final Collection<String> names) {
        /*
         * Queries the fallback first, in order to make the "standard" palettes appear first.
         */
        boolean found = (fallback != null) && fallback.getAvailableNames(names);
        /*
         * First, parse the content of every "list.txt" files found on the classpath. Those files
         * are optional. But if they are present, we assume that their content are accurate (this
         * will not be verified).
         */
        try {
            BufferedReader in = getReader(LIST_FILE, "getAvailableNames");
            if (in != null) {
                readNames(in, names);
                found = true;
            }
            /*
             * We can iterate only with ClassLoader because there is no Class.getResources(...)
             * method. Note that this iteration (when performed) is not completely redundant with
             * the above call to 'getReader' because the user may have overridden
             * PaletteFactory.getResourceAsStream(String).
             */
            if (classloader != null) {
                final String filename = new File(directory, LIST_FILE).getPath().replace(File.separatorChar, '/');
                for (final Enumeration<URL> it=classloader.getResources(filename); it.hasMoreElements();) {
                    final URL url = it.nextElement();
                    in = getReader(url.openStream());
                    readNames(in, names);
                    found = true;
                }
            }
        } catch (IOException e) {
            /*
             * Logs a warning but do not stop. The only consequence is that the names list
             * will be incomplete. We log the message as if it came from getAvailableNames(),
             * because it is the public method that invoked this one.
             */
            Logging.unexpectedException(PaletteFactory.class, "getAvailableNames", e);
        }
        /*
         * After the "list.txt" files, check if the resources can be read as a directory.
         * It may happen if the classpath point toward a directory of .class files rather
         * than a JAR file.
         */
        File dir = (directory != null) ? directory : new File(".");
        try {
            if (classloader != null) {
                dir = IOUtilities.toFile(classloader.getResource(dir.getPath()), null);
            } else if (loader != null) {
                dir = IOUtilities.toFile(loader.getResource(dir.getPath()), null);
            }
        } catch (IOException e) {
            /*
             * The URL to the palette files can not be converted to a File object.
             * Consequently we can not scan the list of files in the directory.
             * Returns only the palettes which were explicitly declared.
             */
            return found;
        }
        if (dir == null) {
            return found;
        }
        final String[] list = dir.list(new DefaultFileFilter('*' + extension));
        if (list == null) {
            return found; // Not a directory.
        }
        final int extLg = extension.length();
        for (final String filename : list) {
            final int lg = filename.length();
            if (lg > extLg && filename.regionMatches(true, lg - extLg, extension, 0, extLg)) {
                names.add(filename.substring(0, lg-extLg));
            }
        }
        return true;
    }

    /**
     * Copies the content of the specified reader to the specified collection.
     * The reader is closed after this operation.
     */
    private static void readNames(final BufferedReader in, final Collection<String> names)
            throws IOException
    {
        String line;
        while ((line = in.readLine()) != null) {
            line = line.trim();
            if (!line.isEmpty() && line.charAt(0) != '#') {
                names.add(line);
            }
        }
        in.close();
    }

    /**
     * Returns a buffered reader for the specified palette.
     *
     * @param  The palette's name to load. This name doesn't need to contains a path
     *         or an extension. Path and extension are set according value specified
     *         at construction time.
     * @return A buffered reader to read {@code name}, or {@code null} if the resource is not found.
     */
    private LineNumberReader getPaletteReader(String name) {
        if (extension != null && !name.endsWith(extension)) {
            name += extension;
        }
        return getReader(name, "getPalette");
    }

    /**
     * Returns a buffered reader for the specified filename.
     *
     * @param  The filename. Path and extension are set according value specified at construction time.
     * @return A buffered reader to read {@code name}, or {@code null} if the resource is not found.
     */
    private LineNumberReader getReader(final String name, final String caller) {
        final File   file = new File(directory, name);
        final String path = file.getPath().replace(File.separatorChar, '/');
        InputStream stream;
        try {
            stream = getResourceAsStream(path);
            if (stream == null) {
                if (file.canRead()) try {
                    stream = new FileInputStream(file);
                } catch (FileNotFoundException e) {
                    /*
                     * Should not occurs, since we checked for file existence. This is not a fatal
                     * error however, since this method is allowed to returns null if the resource
                     * is not available.
                     */
                    Logging.unexpectedException(PaletteFactory.class, caller, e);
                    return null;
                } else {
                    return null;
                }
            }
        } catch (SecurityException e) {
            Logging.recoverableException(PaletteFactory.class, caller, e);
            return null;
        }
        return getReader(stream);
    }

    /**
     * Wraps the specified input stream into a reader.
     */
    private LineNumberReader getReader(final InputStream stream) {
        return new LineNumberReader((charset != null) ?
                new InputStreamReader(stream, charset) : new InputStreamReader(stream));
    }

    /**
     * Reads the colors declared in the specified input stream. Colors must be encoded on 3 or 4
     * columns. If 3 columns, it is assumed RGB values. If 4 columns, it is assumed RGBA values.
     * Values must be in the 0-255 ranges. Empty lines and lines starting by {@code '#'} are
     * ignored.
     *
     * @param  input The stream to read.
     * @param  name  The palette name to read. Used for formatting error message only.
     * @return The colors.
     * @throws IOException if an I/O error occurred.
     * @throws IIOException if a syntax error occurred.
     */
    @SuppressWarnings("fallthrough")
    private Color[] getColors(final LineNumberReader input, final String name) throws IOException {
        int[] values = null;
        final LineFormat reader = (locale!=null) ? new LineFormat(locale) : new LineFormat();
        final List<Color> colors = new ArrayList<>();
        String line; while ((line=input.readLine()) != null) try {
            line = line.trim();
            if (line.isEmpty())            continue;
            if (line.charAt(0) == '#')     continue;
            if (reader.setLine(line) == 0) continue;
            values = reader.getValues(values);
            int A=255,R,G,B;
            switch (values.length) {
                case 4: A = byteValue(values[3]); // fall through
                case 3: B = byteValue(values[2]);
                        G = byteValue(values[1]);
                        R = byteValue(values[0]);
                        break;
                default: {
                    throw syntaxError(input, name, null);
                }
            }
            final Color color;
            try {
                color = new Color(R, G, B, A);
            } catch (IllegalArgumentException exception) {
                /*
                 * Color constructor checks the RGBA value and throws an IllegalArgumentException
                 * if they are not in the 0-255 range. Intercept this exception and rethrows as a
                 * checked IIOException, since we want to notify the user that the palette file is
                 * badly formatted. (additional note: it is somewhat redundant with byteValue(int)
                 * work. Let keep it as a safety).
                 */
                throw syntaxError(input, name, exception);
            }
            colors.add(color);
        } catch (ParseException exception) {
            throw syntaxError(input, name, exception);
        }
        return colors.toArray(new Color[colors.size()]);
    }

    /**
     * Prepares an exception for the specified cause, which may be {@code null}.
     */
    private IIOException syntaxError(final LineNumberReader input, final String name, final Exception cause) {
        String message = getErrorResources().getString(Errors.Keys.ILLEGAL_LINE_IN_FILE_2,
                name, input.getLineNumber());
        if (cause != null) {
            message += cause.getLocalizedMessage();
        }
        return new IIOException(message, cause);
    }

    /**
     * Loads colors from a definition file. If no colors were found in the current palette
     * factory, then the fallback (if any fallback was specified to the constructor) will
     * be queried.
     *
     * {@section Special case for single color}
     * If the given name starts with the {@code '#'} character, then the string is decoded as
     * a color using the {@link Color#decode(String)} method (i.e., the string is interpreted
     * as a hexadecimal RGB value) and the decoded color is returned in an array of length 1.
     *
     * @param  name The name of the palette to load.
     * @return The set of colors, or {@code null} if no palette was found for the given name.
     * @throws IOException if an error occurs during reading.
     */
    public Color[] getColors(final String name) throws IOException {
        final Color[] colors;
        if (name.startsWith("#")) try {
            colors = new Color[] {Color.decode(name)};
        } catch (NumberFormatException e) {
            return null;
        } else {
            try (LineNumberReader reader = getPaletteReader(name)) {
                if (reader == null) {
                    return (fallback != null) ? fallback.getColors(name) : null;
                }
                colors = getColors(reader, name);
            }
        }
        return colors;
    }

    /**
     * Ensures that the specified value is inside the {@code [0..255]} range.
     * If the value is outside that range, a {@link ParseException} is thrown.
     */
    private int byteValue(final int value) throws ParseException {
        if (value>=0 && value<256) {
            return value;
        }
        throw new ParseException(getErrorResources().getString(
                Errors.Keys.RGB_OUT_OF_RANGE_1, value), 0);
    }

    /**
     * Returns the palette of the specified name and size.
     * The default implementation is equivalents to:
     *
     * {@preformat java
     *     return getPalette(name, 0, size, size, 1, 0);
     * }
     *
     * This method does not test the validity of the {@code name} argument. If there is
     * no palette for the given name, then a {@link java.io.FileNotFoundException} will
     * be thrown by the getter methods in the returned palette.
     *
     * @param  name The name of the palette to load.
     * @param  size The {@linkplain IndexColorModel index color model} size.
     * @return The palette.
     *
     * @since 2.4
     */
    public Palette getPalette(final String name, final int size) {
        return getPalette(name, 0, size, size, 1, 0);
    }

    /**
     * Returns a palette with a <cite>pad value</cite> at index 0.
     * The default implementation is equivalents to:
     *
     * {@preformat java
     *     return getPalette(name, 1, size, size, 1, 0);
     * }
     *
     * This method does not test the validity of the {@code name} argument. If there is
     * no palette for the given name, then a {@link java.io.FileNotFoundException} will
     * be thrown by the getter methods in the returned palette.
     *
     * @param  name The name of the palette to load.
     * @param  size The {@linkplain IndexColorModel index color model} size.
     * @return The palette.
     *
     * @since 2.4
     */
    public Palette getPalettePadValueFirst(final String name, final int size) {
        return getPalette(name, 1, size, size, 1, 0);
    }

    /**
     * Returns a palette with <cite>pad value</cite> at the last index.
     * The default implementation is equivalents to:
     *
     * {@preformat java
     *     return getPalette(name, 0, size-1, size, 1, 0);
     * }
     *
     * This method does not test the validity of the {@code name} argument. If there is
     * no palette for the given name, then a {@link java.io.FileNotFoundException} will
     * be thrown by the getter methods in the returned palette.
     *
     * @param  name The name of the palette to load.
     * @param  size The {@linkplain IndexColorModel index color model} size.
     * @return The palette.
     *
     * @since 2.4
     */
    public Palette getPalettePadValueLast(final String name, final int size) {
        return getPalette(name, 0, size-1, size, 1, 0);
    }

    /**
     * Returns the palette of the specified name and size. The RGB colors will be distributed
     * in the range {@code lower} inclusive to {@code upper} exclusive. Remaining pixel values
     * (if any) will be left to a black or transparent color by default.
     * <p>
     * This method does not test the validity of the {@code name} argument. If there is
     * no palette for the given name, then a {@link java.io.FileNotFoundException} will
     * be thrown by the getter methods in the returned palette.
     *
     * @param  name The name of the palette to load.
     * @param lower Index of the first valid element (inclusive) in the
     *              {@linkplain IndexColorModel index color model} to be created.
     * @param upper Index of the last valid element (exclusive) in the
     *              {@linkplain IndexColorModel index color model} to be created.
     * @param size  The size of the {@linkplain IndexColorModel index color model} to be created.
     *              This is the value to be returned by {@link IndexColorModel#getMapSize()}.
     * @param numBands    The number of bands (usually 1).
     * @param visibleBand The band to use for color computations (usually 0).
     * @return The palette.
     *
     * @since 2.4
     */
    public Palette getPalette(final String name, final int lower, final int upper, final int size,
                              final int numBands, final int visibleBand)
    {
        Palette palette = new IndexedPalette(this, name, lower, upper, size, numBands, visibleBand);
        palette = palettes.unique(palette);
        return palette;
    }

    /**
     * Creates a palette suitable for floating point values.
     *
     * @param name        The palette name.
     * @param minimum     The minimal sample value expected.
     * @param maximum     The maximal sample value expected.
     * @param dataType    The data type as a {@link java.awt.image.DataBuffer#TYPE_INT},
     *                    {@link java.awt.image.DataBuffer#TYPE_FLOAT} or
     *                    {@link java.awt.image.DataBuffer#TYPE_DOUBLE} constant.
     * @param numBands    The number of bands (usually 1).
     * @param visibleBand The band to use for color computations (usually 0).
     * @return A palette suitable for floating point values.
     *
     * @since 2.4
     *
     * @todo Current implementation ignores the name and builds a gray scale in all cases.
     *       Future version may improve on that.
     */
    public Palette getContinuousPalette(final String name, final float minimum, final float maximum,
                                        final int dataType, final int numBands, final int visibleBand)
    {
        Palette palette = new ContinuousPalette(this, name, minimum, maximum, dataType, numBands, visibleBand);
        palette = palettes.unique(palette);
        return palette;
    }
}
