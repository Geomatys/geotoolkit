/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.internal.image.io;

import java.io.*;
import javax.imageio.IIOException;
import java.awt.geom.AffineTransform;
import java.net.URL;
import java.net.URI;

import org.geotoolkit.lang.Static;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.io.ContentFormatException;



/**
 * Utility methods related to the additional files that come with some image formats.
 * Those additional files have the {@code ".tfw"} or {@code ".prj"} suffixes.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.07
 *
 * @since 3.00
 * @module
 */
@Static
public final class SupportFiles {
    /**
     * The encoding of TFW files.
     */
    private static final String ENCODING = "ISO-8859-1";

    /**
     * Do not allow instantiation of this class.
     */
    private SupportFiles() {
    }

    /**
     * Returns a file with the same path than the given one, except for the extension
     * which has been replaced by the given one. If the file already has this extension
     * (according a case-insensitive comparison), then it is returned unchanged.
     * <p>
     * This method returns preferrably a file with lower case extension. However if no
     * such file exists but a file of the same name with upper case extension exists,
     * then the later is returned.
     * <p>
     * If none of the above was found and the {@code tfw} argument is {@code true}, then
     * if a file with the same name and extension than the given {@code file}, with only
     * a {@code 'w'} character appended, is found, then that file is returned. Otherwise
     * if a file with the {@code tfw} extension exists, that file is returned.
     * <p>
     * If all the above fail, then the returned file is the one with the given extension.
     *
     * @param  file The image file.
     * @param  extension The wanted extension in lower cases and without the dot separator.
     * @param  tfw {@code true} if this method is invoked for the TFW file.
     * @return the file with the given extension.
     */
    private static File toSupportFile(final File file, final String extension, final boolean tfw) {
        final File parent = file.getParentFile();
        final StringBuilder buffer = new StringBuilder(file.getName());
        int base = buffer.lastIndexOf(".");
        final String currentExtension;
        if (base >= 0) {
            currentExtension = buffer.substring(++base);
            buffer.setLength(base);
        } else {
            currentExtension = "";
            base = buffer.append('.').length();
        }
        File fallback = file;
        final int stop = tfw ? 6 : 2;
        for (int i=0; i<stop; i++) {
            switch (i) {
                case 0: buffer.append(extension); break;
                case 1: buffer.append(extension.toUpperCase()); break;
                case 2: {
                    buffer.append(currentExtension).append('w');
                    break;
                }
                case 3: {
                    buffer.append(currentExtension.toUpperCase()).append('W');
                    break;
                }
                case 4: {
                    if (extension.equals("tfw")) {
                        continue;
                    }
                    buffer.append("tfw");
                    break;
                }
                case 5: {
                    if (extension.equals("TFW")) {
                        continue;
                    }
                    buffer.append("TFW");
                    break;
                }
                default: throw new AssertionError(i);
            }
            final File candidate = new File(parent, buffer.toString());
            if (candidate.isFile()) {
                return candidate;
            }
            buffer.setLength(base);
            // Retain the first attempt, which will be
            // used as a fallback if no file was found.
            if (i == 0) {
                fallback = candidate;
            }
        }
        return fallback;
    }

    /**
     * Returns a new file or URL equivalent to the given {@link String}, {@link File}, {@link URL}
     * or {@link URI} argument, with its extension replaced by the given one. The given extension
     * shall be all lowercase and without leading dot character.
     * <p>
     * The {@code "tfw"} extension is handled especially, in that {@code "tfw} will actually be
     * used only as a fallback if no file exist with the extension for <cite>World File</cite>.
     * <p>
     * While not mandatory, it is recommanded to invoke {@link IOUtilities#tryToFile(Object)}
     * before this method in order to increase the chances to pass a {@link File} argument.
     * This allows us to check if the file exists.
     *
     * @param  path The path as a {@link String}, {@link File}, {@link URL} or {@link URI}.
     * @param  extension The new extension, in lower cases and without leading dot.
     * @return The path with the new extension, or {@code null} if the given path was null.
     * @throws IOException If the given object is not recognized, or attempt to replace it
     *         extension does not result in a valid URL.
     *
     * @since 3.07
     */
    public static Object changeExtension(Object path, String extension) throws IOException {
        if (path != null) {
            boolean isTFW = extension.equals("tfw");
            if (isTFW) {
                extension = toSuffixTFW(path);
            }
            if (path instanceof File) {
                return toSupportFile((File) path, extension, isTFW);
            }
            path = IOUtilities.changeExtension(path, extension);
            if (path == null) {
                throw new IIOException(Errors.format(Errors.Keys.UNKNOW_TYPE_$1, Classes.getClass(path)));
            }
        }
        return path;
    }

    /**
     * Returns the TFW suffix for the given file, URL or URI.
     * This method returns always the suffix in lower case.
     *
     * @param  file The file for which we want to change the suffix.
     * @return The TFW suffix of the given file.
     */
    public static String toSuffixTFW(final Object file) {
        final String ext = IOUtilities.extension(file);
        final int length = ext.length();
        if (length >= 2) {
            return String.valueOf(new char[] {
                Character.toLowerCase(ext.charAt(0)),
                Character.toLowerCase(ext.charAt(length - 1)),
                'w'
            });
        }
        return "tfw";
    }

    /**
     * Writes the given affine transform as a TFW file.
     *
     * @param  file The filename of the <strong>image</strong>. The suffix will be replaced.
     * @param  tr The affine transform to write.
     * @throws IOException if an error occured while writing the file.
     */
    public static void writeTFW(File file, final AffineTransform tr) throws IOException {
        final String suffix = toSuffixTFW(file);
        String name = file.getName();
        final int s = name.lastIndexOf('.');
        if (s >= 0) {
            name = name.substring(0, s+1) + suffix;
        } else {
            name = name + '.' + suffix;
        }
        file = new File(file.getParent(), name);
        writeTFW(new FileOutputStream(file), tr);
    }

    /**
     * Writes the given affine transform as a TFW file.
     *
     * @param  stream The output stream where to write. This stream will be closed by this method.
     * @param  tr The affine transform to write.
     * @throws IOException if an error occured while writing the file.
     */
    public static void writeTFW(final OutputStream stream, final AffineTransform tr) throws IOException {
        final BufferedWriter out = new BufferedWriter(new OutputStreamWriter(stream, ENCODING));
        final double[] matrix = new double[6];
        tr.getMatrix(matrix);
        for (int i=0; i<matrix.length; i++) {
            out.write(String.valueOf(matrix[i]));
            out.newLine();
        }
        out.close();
    }

    /**
     * Parses a TFW file and returns its content as an affine transform.
     *
     * @param  file The file to parse. If it doesn't end with the {@code ".tfw"} suffix (for TIFF
     *         file) or {@code ".jgw"} suffix (for JPEG file), then the suffix of the given file
     *         will be replaced by the appropriate suffix.
     * @return The TFW file content as an affine transform.
     * @throws IOException If an error occured while parsing the file, including
     *         errors while parsing the numbers.
     */
    public static AffineTransform parseTFW(File file) throws IOException {
        file = toSupportFile(file, toSuffixTFW(file), true);
        if (!file.isFile()) {
            // Formats our own error message instead of the JSE one in order to localize it.
            throw new FileNotFoundException(Errors.format(Errors.Keys.FILE_DOES_NOT_EXIST_$1, file.getName()));
        }
        return parseTFW(new FileInputStream(file), file.getName());
    }

    /**
     * Parses a TFW file and returns its content as an affine transform.
     *
     * @param  input The input stream of the file to parse. Will be closed by this method.
     * @param  filename The name of the file being parsed. Used only for formatting error message.
     *         Can be a {@link String}, {@link File}, {@link URL} or {@link URI}.
     * @return The TFW file content as an affine transform.
     * @throws IOException If an error occured while parsing the file, including
     *         errors while parsing the numbers.
     *
     * @since 3.07
     */
    public static AffineTransform parseTFW(final InputStream input, final Object filename) throws IOException {
        final LineNumberReader in = new LineNumberReader(new InputStreamReader(input, ENCODING));
        final double[] m = new double[6];
        int count = 0;
        String line;
        while ((line = in.readLine()) != null) {
            line = line.trim();
            if (line.length() != 0 && line.charAt(0) != '#') {
                if (count >= m.length) {
                    in.close();
                    throw new ContentFormatException(Errors.format(Errors.Keys.FILE_HAS_TOO_MANY_DATA));
                }
                try {
                    m[count++] = Double.parseDouble(line);
                } catch (NumberFormatException e) {
                    in.close();
                    throw new ContentFormatException(Errors.format(Errors.Keys.BAD_LINE_IN_FILE_$2,
                            IOUtilities.name(filename), in.getLineNumber()), e);
                }
            }
        }
        in.close();
        if (count != m.length) {
            throw new EOFException(Errors.format(Errors.Keys.END_OF_DATA_FILE));
        }
        return new AffineTransform(m);
    }
}
