/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.resources;

import java.io.*;
import java.util.*;
import java.text.MessageFormat;
import java.lang.reflect.Field;
import java.util.regex.Pattern;

import org.geotoolkit.util.Utilities;
import org.geotoolkit.console.Option;
import org.geotoolkit.console.CommandLine;
import org.geotoolkit.io.ExpandedTabWriter;


/**
 * Resource compiler runnable from the command line. {@code IndexedResourceCompiler} reads a
 * given list of {@code .properties} files and copies their content to {@code .utf} files using
 * UTF-8 encoding. It also checks for key validity and checks values for {@link MessageFormat}
 * compatibility. Finally, it writes the key values in the Java source files.
 * <p>
 * This class must be run from the maven root of Geotoolkit project. An error will be reported
 * if the expected directories are not found.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 1.2
 */
public class IndexedResourceCompiler extends CommandLine implements FilenameFilter, Comparator<Object> {
    /**
     * Runs the compiler for Geotoolkit resources. This method can be copied and modified
     * in a separated class if this compiler is to be used for an other set of resources.
     *
     * @param arguments Command-line arguments.
     */
    public static void main(final String[] arguments) {
        final File sourceDirectory = new File("modules/utility/geotk-utility/src/main");
        @SuppressWarnings({"unchecked","rawtypes"}) // Generic array creation.
        final Class<? extends IndexedResourceBundle>[] resourcesToProcess = new Class[] {
            org.geotoolkit.resources.Descriptions.class,
            org.geotoolkit.resources.Vocabulary  .class,
            org.geotoolkit.resources.Loggings    .class,
            org.geotoolkit.resources.Errors      .class
        };
        final IndexedResourceCompiler compiler =
                new IndexedResourceCompiler(arguments, sourceDirectory, resourcesToProcess);
        compiler.run();
    }

    /**
     * Extension for properties source files.
     * Must be in the {@code ${sourceDirectory}/java} directory.
     */
    private static final String PROPERTIES_EXT = ".properties";

    /**
     * Extension for resource target files.
     * Will be be in the {@code ${sourceDirectory}/resources} directory.
     */
    private static final String RESOURCES_EXT = ".utf";

    /**
     * Prefix for argument count in resource key names. For example, a resource
     * expecting one argument may have a key name like "HELLO_$1".
     */
    private static final String ARGUMENT_COUNT_PREFIX = "_$";

    /**
     * The maximal length of comment lines.
     */
    private static final int COMMENT_LENGTH = 92;

    /**
     * The name of the inner class which will contains key values.
     */
    private static final String KEYS_INNER_CLASS = "Keys";

    /**
     * Encoding of Java source file (<strong>not</strong> property files).
     */
    private static final String JAVA_ENCODING = "UTF-8";

    /**
     * The {@code main} directory (according Maven standard directory layout) of the module to
     * process. This directory should contains {@code java} and {@code resources} sub-directories,
     * followed by the usual directories for Java packages.
     */
    private final File mainDirectory;

    /**
     * The resource bundle base class being processed.
     * Example: <code>{@linkplain org.geotoolkit.resources.Vocabulary}.class}</code>.
     */
    private Class<? extends IndexedResourceBundle> bundleClass;

    /**
     * Integer IDs allocated to resource keys. This map will be shared for all languages
     * of a given resource bundle.
     */
    private final Map<Integer,String> allocatedIDs = new HashMap<Integer,String>();

    /**
     * Resource keys and their localized values. This map will be cleared for each language
     * in a resource bundle.
     */
    private final Map<Object,Object> resources = new HashMap<Object,Object>();

    /**
     * The resources bundle base classes.
     */
    private final Class<? extends IndexedResourceBundle>[] resourcesToProcess;

    /**
     * {@code true} if the key values should be recomputed. The default
     * is to preserve their values.
     */
    @Option
    private boolean renumber;

    /**
     * Constructs a new {@code IndexedResourceCompiler}.
     *
     * @param arguments
     *          The command-line arguments.
     * @param mainDirectory
     *          The {@code main} directory (according Maven standard directory layout) of the module
     *          to process. This directory should contains {@code java} and {@code resources}
     *          sub-directories, followed by the usual directories for Java packages.
     * @param  resourcesToProcess The resource bundle base classes
     *         (e.g. <code>{@linkplain org.geotoolkit.resources.i18n.Vocabulary}.class}</code>).
     */
    public IndexedResourceCompiler(final String[] arguments, final File mainDirectory,
            final Class<? extends IndexedResourceBundle>[] resourcesToProcess)
    {
        super(null, arguments);
        this.mainDirectory = mainDirectory;
        this.resourcesToProcess = resourcesToProcess;
    }

    /**
     * Run the resource compiler.
     *
     * @param action Should be null.
     */
    @Override
    protected void unknownAction(final String action) {
        if (action != null) {
            super.unknownAction(action);
        }
        if (!mainDirectory.isDirectory()) {
            warning(mainDirectory + " not found or is not a directory.");
            exit(ILLEGAL_ARGUMENT_EXIT_CODE);
            return;
        }
        for (int i=0; i<resourcesToProcess.length; i++) {
            try {
                setResourceBundle(resourcesToProcess[i]);
                scanForResources();
            } catch (IOException exception) {
                printException(exception);
                exit(IO_EXCEPTION_EXIT_CODE);
                return;
            }
        }
        out.flush();
    }

    /**
     * Sets the resource bundle to be processed.
     * The following methods must be invoked before this one:
     *
     * <ul>
     *   <li>{@link #initialize}</li>
     * </ul>
     *
     * @param  bundleClass The resource bundle base class
     *         (e.g. <code>{@linkplain org.geotoolkit.resources.Vocabulary}.class}</code>).
     * @throws IOException If an I/O operation was required and failed.
     */
    private void setResourceBundle(final Class<? extends IndexedResourceBundle> bundleClass)
            throws IOException
    {
        this.bundleClass = bundleClass;
        allocatedIDs.clear();
        resources.clear();
        if (!renumber) try {
            final String classname = bundleClass.getName() + '$' + KEYS_INNER_CLASS;
            final Field[] fields = Class.forName(classname).getFields();
            info("Loading " + classname);
            /*
             * Copies all fields into {@link #allocatedIDs} map.
             */
            Field.setAccessible(fields, true);
            for (int i=fields.length; --i>=0;) {
                final Field field = fields[i];
                final String  key = field.getName();
                try {
                    final Object ID = field.get(null);
                    if (ID instanceof Integer) {
                        allocatedIDs.put((Integer)ID, key);
                    }
                } catch (IllegalAccessException exception) {
                    final File source = new File(classname.replace('.','/') + ".class");
                    warning(source, key, "Access denied", exception);
                }
            }
        } catch (ClassNotFoundException exception) {
            throw new FileNotFoundException(exception.toString());
        }
    }

    /**
     * Scans the package of {@link #bundleClass} for its {@code .properties} files.
     * The following methods must be invoked before this one:
     *
     * <ul>
     *   <li>{@link #initialize}</li>
     *   <li>{@link #setResourceBundle}</li>
     * </ul>
     *
     * The following methods are invoked by this method:
     *
     * <ul>
     *   <li>{@link #loadProperties}</li>
     *   <li>{@link #writeUTF}</li>
     *   <li>{@link #writeJavaSource}</li>
     * </ul>
     *
     * @throws IOException if an input/output operation failed.
     */
    private void scanForResources() throws IOException {
        final String fullname    = bundleClass.getName();
        final int    packageEnd  = fullname.lastIndexOf('.');
        final String packageName = fullname.substring(0, packageEnd);
        final String classname   = fullname.substring(packageEnd + 1);
        final String packageDir  = packageName.replace('.', '/');
        final File   srcDir      = new File(mainDirectory, "java/"      + packageDir);
        final File   utfDir      = new File(mainDirectory, "resources/" + packageDir);
        if (!srcDir.isDirectory()) {
            throw new FileNotFoundException("\"" + srcDir + "\" is not a directory.");
        }
        if (!utfDir.isDirectory()) {
            throw new FileNotFoundException("\"" + utfDir + "\" is not a directory.");
        }
        final File[] content = srcDir.listFiles(this);
        File defaultLanguage = null;
        for (int i=0; i<content.length; i++) {
            final File file = content[i];
            final String filename = file.getName();
            if (filename.startsWith(classname)) {
                loadProperties(file);
                final String noExt = filename.substring(0, filename.length() - PROPERTIES_EXT.length());
                final File utfFile = new File(utfDir, noExt + RESOURCES_EXT);
                writeUTF(utfFile);
                if (noExt.equals(classname)) {
                    defaultLanguage = file;
                }
            }
        }
        if (defaultLanguage != null) {
            resources.clear();
            resources.putAll(loadRawProperties(defaultLanguage));
        }
        writeJavaSource();
    }

    /**
     * Returns {@code true} if the given file is a property file.
     *
     * @param directory The directory (ignored).
     * @param name The file name.
     * @return {@code true} if the given file is a property file.
     */
    @Override
    public final boolean accept(final File directory, final String name) {
        return name.endsWith(PROPERTIES_EXT);
    }

    /**
     * Loads the specified property file. No processing are performed on them.
     *
     * @param  file The property file to load.
     * @return The properties.
     * @throws IOException if the file can not be read.
     */
    private static Properties loadRawProperties(final File file) throws IOException {
        final InputStream input = new FileInputStream(file);
        final Properties properties = new Properties();
        properties.load(input);
        input.close();
        return properties;
    }

    /**
     * Loads all properties from a {@code .properties} file. Resource keys are checked for naming
     * conventions (i.e. resources expecting some arguments must have a key name ending with
     * {@code "_$n"} where {@code "n"} is the number of arguments). This method transforms resource
     * values into legal {@link MessageFormat} patterns when necessary.
     * <p>
     * The following methods must be invoked before this one:
     *
     * <ul>
     *   <li>{@link #initialize}</li>
     *   <li>{@link #setResourceBundle}</li>
     * </ul>
     *
     * @param  file The properties file to read.
     * @throws IOException if an input/output operation failed.
     */
    private void loadProperties(final File file) throws IOException {
        resources.clear();
        final Properties properties = loadRawProperties(file);
        for (final Map.Entry<Object,Object> entry : properties.entrySet()) {
            final String key   = (String) entry.getKey();
            final String value = (String) entry.getValue();
            /*
             * Checks key and value validity.
             */
            if (key.trim().length() == 0) {
                warning(file, key, "Empty key.", null);
                continue;
            }
            if (value.trim().length() == 0) {
                warning(file, key, "Empty value.", null);
                continue;
            }
            /*
             * Checks if the resource value is a legal MessageFormat pattern.
             */
            final MessageFormat message;
            try {
                message = new MessageFormat(toMessageFormatString(value));
            } catch (IllegalArgumentException exception) {
                warning(file, key, "Bad resource value", exception);
                continue;
            }
            /*
             * Checks if the expected arguments count (according to naming conventions)
             * matches the arguments count found in the MessageFormat pattern.
             */
            final int argumentCount;
            final int index = key.lastIndexOf(ARGUMENT_COUNT_PREFIX);
            if (index < 0) {
                argumentCount = 0;
                resources.put(key, value); // Text will not be formatted using MessageFormat.
            } else try {
                String suffix = key.substring(index + ARGUMENT_COUNT_PREFIX.length());
                argumentCount = Integer.parseInt(suffix);
                resources.put(key, message.toPattern());
            } catch (NumberFormatException exception) {
                warning(file, key, "Bad number in resource key", exception);
                continue;
            }
            final int expected = message.getFormatsByArgumentIndex().length;
            if (argumentCount != expected) {
                final String suffix = ARGUMENT_COUNT_PREFIX + expected;
                warning(file, key, "Key name should ends with \"" + suffix + "\".", null);
                continue;
            }
        }
        /*
         * Allocates an ID for each new key.
         */
        final String[] keys = resources.keySet().toArray(new String[resources.size()]);
        Arrays.sort(keys, this);
        int freeID = 0;
        for (int i=0; i<keys.length; i++) {
            final String key = keys[i];
            if (!allocatedIDs.containsValue(key)) {
                Integer ID;
                do {
                    ID = freeID++;
                } while (allocatedIDs.containsKey(ID));
                allocatedIDs.put(ID, key);
            }
        }
    }

    /**
     * Changes a "normal" text string into a pattern compatible with {@link MessageFormat}.
     * The main operation consists of changing ' for '', except for '{' and '}' strings.
     */
    private static String toMessageFormatString(final String text) {
        int level =  0;
        int last  = -1;
        final StringBuilder buffer = new StringBuilder(text);
search: for (int i=0; i<buffer.length(); i++) { // Length of 'buffer' will vary.
            switch (buffer.charAt(i)) {
                /*
                 * Left and right braces take us up or down a level.  Quotes will only be doubled
                 * if we are at level 0.  If the brace is between quotes it will not be taken into
                 * account as it will have been skipped over during the previous pass through the
                 * loop.
                 */
                case '{' : level++; last=i; break;
                case '}' : level--; last=i; break;
                case '\'': {
                    /*
                     * If a brace ('{' or '}') is found between quotes, the entire block is
                     * ignored and we continue with the character following the closing quote.
                     */
                    if (i+2 < buffer.length()  &&  buffer.charAt(i+2) == '\'') {
                        switch (buffer.charAt(i+1)) {
                            case '{': i += 2; continue search;
                            case '}': i += 2; continue search;
                        }
                    }
                    if (level <= 0) {
                        /*
                         * If we weren't between braces, we must double the quotes.
                         */
                        buffer.insert(i++, '\'');
                        continue search;
                    }
                    /*
                     * If we find ourselves between braces, we don't normally need to double
                     * our quotes.  However, the format {0,choice,...} is an exception.
                     */
                    if (last >= 0  &&  buffer.charAt(last) == '{') {
                        int scan = last;
                        do if (scan >= i) continue search;
                        while (Character.isDigit(buffer.charAt(++scan)));
                        final String choice = ",choice,";
                        final int end = scan + choice.length();
                        if (end < buffer.length() && buffer.substring(scan, end).equalsIgnoreCase(choice)) {
                            buffer.insert(i++, '\'');
                            continue search;
                        }
                    }
                }
            }
        }
        return buffer.toString();
    }

    /**
     * Writes UTF file. The following methods must be invoked before this one:
     *
     * <ul>
     *   <li>{@link #initialize}</li>
     *   <li>{@link #setResourceBundle}</li>
     *   <li>{@link #loadProperties}</li>
     * </ul>
     *
     * @param  file The destination file.
     * @throws IOException if an input/output operation failed.
     */
    private void writeUTF(final File file) throws IOException {
        final int count = allocatedIDs.isEmpty() ? 0 : Collections.max(allocatedIDs.keySet()) + 1;
        final DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
        out.writeInt(count);
        for (int i=0; i<count; i++) {
            final String value = (String) resources.get(allocatedIDs.get(i));
            out.writeUTF((value!=null) ? value : "");
        }
        out.close();
    }

    /**
     * Creates a source file for resource keys.
     * The following methods must be invoked before this one:
     *
     * <ul>
     *   <li>{@link #initialize}</li>
     *   <li>{@link #setResourceBundle}</li>
     *   <li>{@link #loadProperties}</li>
     * </ul>
     *
     * @throws IOException if an input/output operation failed.
     */
    private void writeJavaSource() throws IOException {
        /*
         * Opens the source file for reading. We will copy a subset of its content in a buffer.
         */
        final File file = new File(mainDirectory, "java/" + bundleClass.getName().replace('.', '/') + ".java");
        if (!file.getParentFile().isDirectory()) {
            throw new FileNotFoundException("Parent directory not found for " + file);
        }
        final BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), JAVA_ENCODING));
        final String lineSeparator = System.getProperty("line.separator", "\n");
        final StringBuilder buffer = new StringBuilder();
        /*
         * Copies everything up to (including) the declaration of the Keys inner class.
         * The declaration must follow Sun's convention on brace location (i.e. must be
         * on the same line than the class declaration).
         */
        final Pattern classKeys = Pattern.compile("[\\s\\w]*class\\s+" + KEYS_INNER_CLASS + "\\s*\\{");
        String line;
        do {
            line = in.readLine();
            if (line == null) {
                in.close();
                throw new EOFException(file.toString());
            }
            buffer.append(line).append(lineSeparator);
        } while (!classKeys.matcher(line).matches());
        /*
         * Writes the constructor, then write keys values.
         * We stopped reading the file for now (will continue later).
         */
        final String margin = Utilities.spaces(8);
        buffer.append(margin).append("private ").append(KEYS_INNER_CLASS).append("() {").append(lineSeparator)
              .append(margin).append('}').append(lineSeparator);
        final Map.Entry<?,?>[] entries = allocatedIDs.entrySet().toArray(new Map.Entry<?,?>[allocatedIDs.size()]);
        Arrays.sort(entries, this);
        for (int i=0; i<entries.length; i++) {
            buffer.append(lineSeparator);
            final String key = (String) entries[i].getValue();
            final String ID  = entries[i].getKey().toString();
            String message = (String) resources.get(key);
            if (message != null) {
                buffer.append(margin).append("/**").append(lineSeparator);
                while (((message=message.trim()).length()) != 0) {
                    buffer.append(margin).append(" * ");
                    int stop = message.indexOf('\n');
                    if (stop < 0) {
                        stop = message.length();
                    }
                    if (stop > COMMENT_LENGTH) {
                        stop = COMMENT_LENGTH;
                        while (stop>20 && !Character.isWhitespace(message.charAt(stop))) {
                            stop--;
                        }
                    }
                    buffer.append(message.substring(0, stop).trim()).append(lineSeparator);
                    message = message.substring(stop);
                }
                buffer.append(margin).append(" */").append(lineSeparator);
            }
            buffer.append(margin).append("public static final int ")
                  .append(key).append(" = ").append(ID).append(';').append(lineSeparator);
        }
        /*
         * Continue reading the input file, skipping the old key values.
         * Once we have reached the closin bracket, copies all remaining lines.
         */
        int brackets = 1;
        do {
            line = in.readLine();
            if (line == null) {
                in.close();
                throw new EOFException(file.toString());
            }
            for (int i=0; i<line.length(); i++) {
                switch (line.charAt(i)) {
                    case '{': brackets++; break;
                    case '}': brackets--; break;
                }
            }
        } while (brackets != 0);
        buffer.append(line).append(lineSeparator);
        while ((line = in.readLine()) != null) {
            buffer.append(line).append(lineSeparator);
        }
        in.close();
        /*
         * Now writes the results to disk, overwritting the original file.
         */
        final Writer out = new ExpandedTabWriter(new OutputStreamWriter(new FileOutputStream(file), JAVA_ENCODING));
        out.write(buffer.toString());
        out.close();
    }

    /**
     * Compares two resource keys. Object {@code o1} and {@code o2} are usually {@link String}
     * objects representing resource keys (for example, "{@code MISMATCHED_DIMENSION}"), but
     * may also be {@link java.util.Map.Entry}.
     */
    @Override
    public final int compare(Object o1, Object o2) {
        if (o1 instanceof Map.Entry<?,?>) o1 = ((Map.Entry<?,?>) o1).getValue();
        if (o2 instanceof Map.Entry<?,?>) o2 = ((Map.Entry<?,?>) o2).getValue();
        final String key1 = (String) o1;
        final String key2 = (String) o2;
        return key1.compareTo(key2);
    }

    /**
     * Logs the given message at the {@code INFO} level.
     * The default implementation just sent it to the standard output stream.
     *
     * @param message The message to log.
     */
    protected void info(final String message) {
        out.println(message);
    }

    /**
     * Logs the given message at the {@code WARNING} level.
     * The default implementation just sent it to the standard output stream.
     *
     * @param message The message to log.
     */
    protected void warning(final String message) {
        out.println(message);
    }

    /**
     * Logs the given message at the {@code WARNING} level.
     *
     * @param file      File that produced the error, or {@code null} if none.
     * @param key       Resource key that produced the error, or {@code null} if none.
     * @param message   The message string.
     * @param exception An optional exception that is the cause of this warning.
     */
    private void warning(final File file,      final String key,
                         final String message, final Exception exception)
    {
        final StringBuilder buffer = new StringBuilder("ERROR ");
        if (file != null) {
            String filename = file.getPath();
            if (filename.endsWith(PROPERTIES_EXT)) {
                filename = filename.substring(0, filename.length() - PROPERTIES_EXT.length());
            }
            buffer.append('(').append(filename).append(')');
        }
        buffer.append(": ");
        if (key != null) {
            buffer.append('"').append(key).append('"');
        }
        warning(buffer.toString());
        buffer.setLength(0);
        buffer.append(message);
        if (exception != null) {
            buffer.append(": ").append(exception.getLocalizedMessage());
        }
        warning(buffer.toString());
    }
}
