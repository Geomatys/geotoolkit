/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal.image.io;

import java.io.*;
import javax.imageio.IIOException;
import java.awt.geom.AffineTransform;
import java.net.URL;
import java.net.URI;

import org.geotoolkit.lang.Static;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.io.ContentFormatException;


/**
 * Utility methods related to the additional files that come with some image formats.
 * Those additional files have the {@code ".tfw"} or {@code ".prj"} suffixes.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.11
 *
 * @since 3.00
 * @module
 */
public final class SupportFiles extends Static {
    /**
     * The encoding of TFW files.
     */
    private static final String ENCODING = "ISO-8859-1";

    /**
     * Sequences of TFW suffixes which don't follow the usual rules. The usual rule is to either
     * append {@code 'w'} to the suffix, or to keep only the first and the last letter of the
     * suffix and append {@code 'w'} to that.
     * <p>
     * In the list of arrays below, the first element of each array is the suffix for wich a
     * special case is needed, and the following elements are the special cases.
     *
     * @since 3.10
     */
    private static final String[][] SPECIAL_CASES = {
        new String[] {"jpg",  "jpw", "jpegw"},  // No need to declare "jgw" and "jpgw".
        new String[] {"jpeg", "jpw", "jpgw"},   // No need to declare "jgw" and "jpegw".
        new String[] {"tif",  "tiffw"},
        new String[] {"bmp",  "bmw"}
    };

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
     * This method returns preferably a file with lower case extension. However if no
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
     * @param  isTFW {@code true} if this method is invoked for the TFW file.
     * @return the file with the given extension.
     */
    @SuppressWarnings("fallthrough")
    private static File toSupportFile(final File file, final String extension, final boolean isTFW) {
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
        File fallback = file;           // To be used only if no existing file is found.
        String[] specialCases = null;   // To be used only if the standard cases didn't worked.
        int specialCaseIndex = 0;
attmpt: for (int caseNumber=0; ; caseNumber++) {
            switch (caseNumber) {
                /*
                 * Try with the preferred extension given in argument. For TFW files, this is
                 * the first letter, the last letter and 'w'. Example: "pgw" for "png" files.
                 */
                case 0: {
                    buffer.append(extension);
                    break;
                }
                /*
                 * Same extension than above, but with upper cases. Exemple: "PGW" for "png"
                 * files. This is the last attempt made for files that are not TFW files.
                 */
                case 1: {
                    buffer.append(extension.toUpperCase());
                    break;
                }
                /*
                 * If we are looking for a TFW file, try the extension of the existing file
                 * with the 'w' letter appended. Exemple: "pngw" for "png" files.
                 */
                case 2: {
                    if (!isTFW) {
                        break attmpt; // Every cases below this point are for TFW files only.
                    }
                    buffer.append(currentExtension).append('w');
                    break;
                }
                /*
                 * Same than above, but in upper cases. Example: "PNGW" for "png" files.
                 */
                case 3: {
                    buffer.append(currentExtension.toUpperCase()).append('W');
                    break;
                }
                /*
                 * Get the list of special cases, which will be tested in the next block.
                 * If no special cases are found, we will skip the next two switch cases.
                 */
                case 4: {
                    for (final String[] candidate : SPECIAL_CASES) {
                        if (currentExtension.equalsIgnoreCase(candidate[0])) {
                            specialCases = candidate;
                            break;
                        }
                    }
                    if (specialCases == null) {
                        caseNumber += 2; // Skip the next 2 switch cases.
                        continue;
                    }
                    caseNumber++;
                    // fall through
                }
                /*
                 * Try the special case in lower cases. Example: "bmw" for "bmp" files.
                 */
                case 5: {
                    buffer.append(specialCases[++specialCaseIndex]);
                    break;
                }
                /*
                 * Same than above, but in upper case. Example: "BMW" for "bmp" files. If there
                 * is more special cases, we will redo this block and the previous one.
                 */
                case 6: {
                    buffer.append(specialCases[specialCaseIndex].toUpperCase());
                    if (specialCaseIndex + 1 != specialCases.length) {
                        caseNumber -= 2; // Go back 2 switch cases.
                    }
                    break;
                }
                /*
                 * Check the "tfw" extension, if it was not already done. Note that the
                 * 'extension' argument is always in lower cases, so it can not be equal
                 * to "TFW". If those two last attempts didn't worked, we are done.
                 */
                case 7: {
                    if (extension.equals("tfw")) {
                        continue;
                    }
                    buffer.append("tfw");
                    break;
                }
                case 8: {
                    buffer.append("TFW");
                    break;
                }
                default: {
                    break attmpt;
                }
            }
            final File candidate = new File(parent, buffer.toString());
            if (candidate.isFile()) {
                return candidate;
            }
            buffer.setLength(base);
            // Retain the first attempt, which will be
            // used as a fallback if no file was found.
            if (caseNumber == 0) {
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
     * While not mandatory, it is recommended to invoke {@link IOUtilities#tryToFile(Object)}
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
    public static Object changeExtension(final Object path, String extension) throws IOException {
        if (path != null) {
            boolean isTFW = extension.equals("tfw");
            if (isTFW) {
                extension = toSuffixTFW(path);
            }
            if (path instanceof File) {
                return toSupportFile((File) path, extension, isTFW);
            }
            final Object renamed = IOUtilities.changeExtension(path, extension);
            if (renamed != null) {
                return renamed;
            }
            throw new IIOException(Errors.format(Errors.Keys.UNKNOWN_TYPE_$1, path.getClass()));
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
     * @throws IOException if an error occurred while writing the file.
     */
    public static void writeTFW(File file, final AffineTransform tr) throws IOException {
        final String suffix = toSuffixTFW(file);
        final String name = file.getName();
        final StringBuilder buffer = new StringBuilder(name);
        final int s = name.lastIndexOf('.');
        if (s >= 0) {
            buffer.setLength(s + 1);
        } else {
            buffer.append('.');
        }
        file = new File(file.getParent(), buffer.append(suffix).toString());
        writeTFW(new FileOutputStream(file), tr);
    }

    /**
     * Writes the given affine transform as a TFW file.
     *
     * @param  stream The output stream where to write. This stream will be closed by this method.
     * @param  tr The affine transform to write.
     * @throws IOException if an error occurred while writing the file.
     */
    public static void writeTFW(final OutputStream stream, final AffineTransform tr) throws IOException {
        try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(stream, ENCODING))) {
            final double[] matrix = new double[6];
            tr.getMatrix(matrix);
            for (int i=0; i<matrix.length; i++) {
                out.write(String.valueOf(matrix[i]));
                out.newLine();
            }
        }
    }

    /**
     * Parses a TFW file and returns its content as an affine transform.
     *
     * @param  file The file to parse. If it doesn't end with the {@code ".tfw"} suffix (for TIFF
     *         file) or {@code ".jgw"} suffix (for JPEG file), then the suffix of the given file
     *         will be replaced by the appropriate suffix.
     * @return The TFW file content as an affine transform.
     * @throws IOException If an error occurred while parsing the file, including
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
     * @throws IOException If an error occurred while parsing the file, including
     *         errors while parsing the numbers.
     *
     * @since 3.07
     */
    public static AffineTransform parseTFW(final InputStream input, final Object filename) throws IOException {
        final double[] m;
        int count;
        try (LineNumberReader in = new LineNumberReader(new InputStreamReader(input, ENCODING))) {
            m = new double[6];
            count = 0;
            String line;
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && line.charAt(0) != '#') {
                    if (count >= m.length) {
                        throw new ContentFormatException(Errors.format(Errors.Keys.FILE_HAS_TOO_MANY_DATA));
                    }
                    try {
                        m[count++] = Double.parseDouble(line);
                    } catch (NumberFormatException e) {
                        throw new ContentFormatException(Errors.format(Errors.Keys.ILLEGAL_LINE_IN_FILE_$2,
                                IOUtilities.name(filename), in.getLineNumber()), e);
                    }
                }
            }
        }
        if (count != m.length) {
            throw new EOFException(Errors.format(Errors.Keys.END_OF_DATA_FILE));
        }
        return new AffineTransform(m);
    }
}
