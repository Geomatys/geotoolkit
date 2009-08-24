/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal.image.io;

import java.io.*;
import java.awt.geom.AffineTransform;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.geotoolkit.lang.Static;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.io.ContentFormatException;



/**
 * Utility methods related to the additional files that come with some image formats.
 * Those additional files have the {@code ".tfw"} or {@code ".prj"} suffixes.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
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
     * then the later is returned. If none of them are found but a file with the {@code tfw}
     * extension exists, then that file is returned. If all the above fail, then the returned
     * file is the one with the given extension.
     *
     * @param file The file, or {@code null}.
     * @param extension The wanted extension in lower cases and without the dot separator.
     * @return the file with the given extension, or {@code null} if the given file was null.
     */
    @SuppressWarnings("fallthrough")
    private static File toSupportFile(File file, final String extension) {
        if (file != null) {
            String name = file.getName();
            final File parent = file.getParentFile();
            final int separator = name.lastIndexOf('.');
            if (separator >= 0) {
                if (extension.equalsIgnoreCase(name.substring(separator + 1))) {
                    /*
                     * Already a file having the requested extension. If the file exists, we
                     * are done. Otherwise continue in order to try lower case and upper case.
                     */
                    if (file.isFile()) {
                        return file;
                    }
                }
                name = name.substring(0, separator + 1);
            } else {
                name += '.';
            }
            final String basename = name;
attempts:   for (int i=0; ; i++) {
                switch (i) {
                    case 0: name = basename + extension;               break;
                    case 1: name = basename + extension.toUpperCase(); break;
                    case 2: if (!extension.equals("tfw")) {
                        name = basename + "tfw";
                        break;
                    }
                    case 3: if (!extension.equals("TFW")) {
                        name = basename + "TFW";
                        break;
                    }
                    default: break attempts;
                }
                final File candidate = new File(parent, name);
                if (candidate.isFile()) {
                    return candidate;
                }
                if (i == 0) {
                    file = candidate;
                }
            }
        }
        return file;
    }

    /**
     * Returns the TFW suffix for the given file. Actually {@code ".tfw"} is the default,
     * but other prefix may be returned like {@code ".jgw"} for JPEG files. This method
     * returns always the suffix in lower case.
     */
    private static String toSuffixTFW(final File file) {
        final String name = file.getName().trim();
        final int length = name.length();
        final int separator = name.indexOf('.');
        if (separator >= 0 && separator + 2 < length) {
            return String.valueOf(new char[] {
                Character.toLowerCase(name.charAt(separator + 1)),
                Character.toLowerCase(name.charAt(length - 1)),
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
            if (!name.substring(s+1).endsWith("fw")) {
                name = name.substring(0, s+1) + suffix;
            }
        } else {
            name = name + '.' + suffix;
        }
        file = new File(file.getParent(), name);
        final BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), ENCODING));
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
        file = toSupportFile(file, toSuffixTFW(file));
        if (!file.isFile()) {
            // Formats our own error message instead of the JSE one in order to localize it.
            throw new FileNotFoundException(Errors.format(Errors.Keys.FILE_DOES_NOT_EXIST_$1, file.getName()));
        }
        final LineNumberReader in = new LineNumberReader(new InputStreamReader(new FileInputStream(file), ENCODING));
        final double[] m = new double[6];
        int count = 0;
        String line;
        while ((line = in.readLine()) != null) {
            line = line.trim();
            if (line.length() != 0) {
                if (count >= m.length) {
                    in.close();
                    throw new ContentFormatException(Errors.format(Errors.Keys.FILE_HAS_TOO_MANY_DATA));
                }
                try {
                    m[count++] = Double.parseDouble(line);
                } catch (NumberFormatException e) {
                    in.close();
                    throw new ContentFormatException(Errors.format(Errors.Keys.BAD_LINE_IN_FILE_$2,
                            file.getName(), in.getLineNumber()), e);
                }
            }
        }
        in.close();
        if (count != m.length) {
            throw new EOFException(Errors.format(Errors.Keys.END_OF_DATA_FILE));
        }
        return new AffineTransform(m);
    }

    /**
     * Parses a PRJ file and returns its content as a coordinate reference system.
     *
     * @param  file The file to parse. If it doesn't end with the {@code ".prj"} suffix,
     *         then the file suffix will be replaced by {@code ".prj"}.
     * @return The PRJ file content as a coordinate reference system.
     * @throws IOException If an error occured while parsing the file, including
     *         errors while parsing the WKT.
     */
    public static CoordinateReferenceSystem parsePRJ(File file) throws IOException {
        file = toSupportFile(file, "prj");
        final BufferedReader in = new BufferedReader(new FileReader(file));
        StringBuilder buffer = null;
        String wkt=null, line;
        while ((line = in.readLine()) != null) {
            line = line.trim();
            if (line.length() != 0) {
                if (wkt == null) {
                    wkt = line;
                } else {
                    if (buffer == null) {
                        buffer = new StringBuilder(wkt);
                    }
                    buffer.append('\n').append(line);
                }
            }
        }
        in.close();
        if (buffer != null) {
            wkt = buffer.toString();
        }
        if (wkt == null) {
            throw new EOFException(Errors.format(Errors.Keys.END_OF_DATA_FILE));
        }
        try {
            return CRS.decode(wkt);
        } catch (FactoryException e) {
            throw new ContentFormatException(e.getLocalizedMessage(), e);
        }
    }
}
