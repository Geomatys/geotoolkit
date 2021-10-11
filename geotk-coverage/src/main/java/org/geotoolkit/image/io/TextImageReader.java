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
package org.geotoolkit.image.io;

import java.io.*; // Many imports, including some for javadoc only.
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Set;
import java.util.Locale;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import java.nio.channels.ReadableByteChannel;

import org.geotoolkit.io.LineFormat;
import org.geotoolkit.internal.io.LineReader;
import org.geotoolkit.resources.Vocabulary;


/**
 * Base class for image readers that expect a {@link BufferedReader} input source.
 * "<cite>Text images</cite>" are usually ASCII files where pixels values are actually
 * the geophysical values. This base class provides the following conveniences:
 * <p>
 * <ul>
 *   <li>Get a {@link BufferedReader} from the input types, which may be a any type documented
 *       in the {@linkplain StreamImageReader super-class} plus {@link Reader}.</li>
 *   <li>Get a {@link LineFormat} for parsing a whole line as a record. Subclasses can override
 *       this method for parsing text files having non-numeric columns (angles, dates, <i>etc.</i>).</li>
 *   <li>Get the character encoding and the locale (for parsing numbers) from the fields declared
 *       in the {@linkplain Spi Service Provider}. Alternatively, subclasses can also get more
 *       control by overriding the {@link #getCharset(InputStream)} method.</li>
 * </ul>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.08
 *
 * @see TextImageWriter
 *
 * @since 3.08 (derived from 1.2)
 * @module
 */
public abstract class TextImageReader extends StreamImageReader {
    /**
     * {@link #input} as a reader, or {@code null} if none.
     *
     * @see #getReader
     */
    private BufferedReader reader;

    /**
     * Constructs a new image reader.
     *
     * @param provider The {@link ImageReaderSpi} that is constructing this object, or {@code null}.
     */
    protected TextImageReader(final Spi provider) {
        super(provider);
    }

    /**
     * Returns the character set to use for decoding the string from the input stream. The default
     * implementation returns the {@linkplain Spi#charset character set} specified to the
     * {@link Spi} object given to this {@code TextImageReader} constructor. Subclasses can
     * override this method if they want to detect the character encoding in some other way.
     *
     * @param  input The input stream.
     * @return The character encoding, or {@code null} for the platform default encoding.
     * @throws IOException If reading from the input stream failed.
     *
     * @see Spi#charset
     */
    protected Charset getCharset(final InputStream input) throws IOException {
        return (originatingProvider instanceof Spi) ? ((Spi) originatingProvider).charset : null;
    }

    /**
     * Returns the locale specified by the provider for the data to be read,
     * or {@code null} if unspecified.
     *
     * @return The locale for the data to be read, or {@code null} if unspecified.
     */
    final Locale getDataLocale() {
        return (originatingProvider instanceof Spi) ? ((Spi) originatingProvider).locale : null;
    }

    /**
     * Returns the line format to use for parsing every lines in the input stream. The default
     * implementation creates a new {@link LineFormat} instance using the locale specified by
     * {@link Spi#locale}. Subclasses should override this method if they want more control
     * on the parser to be created.
     *
     * @param  imageIndex the index of the image to be queried.
     * @return The object to use for parsing lines of text.
     * @throws IOException If reading from the input stream failed.
     *
     * @see Spi#locale
     */
    protected LineFormat getLineFormat(final int imageIndex) throws IOException {
        final Locale locale = getDataLocale();
        if (locale != null) {
            return new LineFormat(locale);
        }
        return new LineFormat();
    }

    /**
     * Returns the pad value for missing data, or {@link Double#NaN} if none. The pad value will
     * applies to all columns except the one for
     * {@link org.geotoolkit.image.io.plugin.TextRecordImageReader#getColumnX x} and
     * {@link org.geotoolkit.image.io.plugin.TextRecordImageReader#getColumnY y} values, if any.
     * <p>
     * The default implementation returns the pad value specified to the {@link Spi} object given
     * to this {@code TextImageReader} constructor. Subclasses can override this method if they
     * want to detect the pad value in some other way.
     *
     * @param  imageIndex the index of the image to be queried.
     * @return The pad value, or {@link Double#NaN} if none.
     * @throws IOException If reading from the input stream failed.
     *
     * @see Spi#padValue
     */
    protected double getPadValue(final int imageIndex) throws IOException {
        return (originatingProvider instanceof Spi) ? ((Spi) originatingProvider).padValue : Double.NaN;
    }

    /**
     * Returns the {@linkplain #input input} as an {@linkplain BufferedReader buffered reader}.
     * If the input is already a buffered reader, it is returned unchanged. Otherwise this method
     * creates a new {@linkplain LineNumberReader line number reader} from various input types
     * including {@link File}, {@link URL}, {@link URLConnection}, {@link Reader},
     * {@link InputStream} and {@link ImageInputStream}.
     * <p>
     * This method creates a new {@linkplain BufferedReader reader} only when first invoked.
     * All subsequent calls will returns the same instance. Consequently, the returned reader
     * should never be closed by the caller. It may be {@linkplain #close closed} automatically
     * when {@link #setInput setInput(...)}, {@link #reset() reset()} or {@link #dispose()
     * dispose()} methods are invoked.
     *
     * @return {@link #getInput} as a {@link BufferedReader}.
     * @throws IllegalStateException if the {@linkplain #input input} is not set.
     * @throws IOException If the input stream can't be created for an other reason.
     *
     * @see #getInput
     * @see #getInputStream
     */
    protected BufferedReader getReader() throws IllegalStateException, IOException {
        if (reader == null) {
            final Object input = getInput();
            if (input instanceof BufferedReader) {
                reader = (BufferedReader) input;
                closeOnReset = null; // We don't own the underlying reader, so don't close it.
            } else if (input instanceof Reader) {
                reader = new LineReader((Reader) input);
                closeOnReset = null; // We don't own the underlying reader, so don't close it.
            } else {
                final InputStream stream = getInputStream();
                reader = new LineReader(getInputStreamReader(stream));
                if (closeOnReset == stream) {
                    closeOnReset = reader;
                }
            }
        }
        return reader;
    }

    /**
     * Returns the specified {@link InputStream} as a {@link Reader}.
     */
    final Reader getInputStreamReader(final InputStream stream) throws IOException {
        final Charset charset = getCharset(stream);
        return (charset != null) ? new InputStreamReader(stream, charset) : new InputStreamReader(stream);
    }

    /**
     * Returns {@code true} if the specified line is a comment. This method is invoked automatically
     * during a {@link #read read} operation. The default implementation returns {@code true} if the
     * line is empty or if the first non-whitespace character is {@code '#'}, and {@code false}
     * otherwise. Override this method if comment lines should be determined in a different way.
     *
     * @param  line A line to be parsed.
     * @return {@code true} if the line is a comment and should be ignored, or {@code false} if it
     *         should be parsed.
     */
    protected boolean isComment(final String line) {
        final int length = line.length();
        for (int i=0; i<length; i++) {
            final char c = line.charAt(i);
            if (!Character.isSpaceChar(c)) {
                return (c == '#');
            }
        }
        return true;
    }

    /**
     * Returns a string representation of the current stream position. For example this method
     * may returns something like {@code "Line 14 in file HUV18204.asc"}. This method returns
     * {@code null} if the stream position is unknown.
     *
     * @param message An optional message to append to the stream position, or {@code null}
     *        if none.
     * @return A string representation of current stream position.
     */
    protected String getPositionString(final String message) {
        final String file;
        final Object input = getInput();
        if (input instanceof File) {
            file = ((File) input).getName();
        } else if (input instanceof URL) {
            file = ((URL) input).getFile();
        } else {
            file = null;
        }
        final Integer line = (reader instanceof LineNumberReader) ?
                ((LineNumberReader) reader).getLineNumber() : null;

        final Vocabulary resources = Vocabulary.getResources(getLocale());
        final String position;
        if (file != null) {
            if (line != null) {
                position = resources.getString(Vocabulary.Keys.FilePosition_2, file, line);
            } else {
                position = resources.getString(Vocabulary.Keys.File_1, file);
            }
        } else if (line != null) {
            position = resources.getString(Vocabulary.Keys.Line_1, line);
        } else {
            position = null;
        }
        if (position != null) {
            if (message != null) {
                return position + ": " + message;
            } else {
                return position;
            }
        } else {
            return message;
        }
    }

    /**
     * Closes the reader created by {@link #getReader()}. This method does nothing if
     * the reader is the {@linkplain #input input} instance given by the user rather
     * than a reader created by this class from a {@link File} or {@link URL} input.
     *
     * @throws IOException If an error occurred while closing the reader.
     *
     * @see #closeOnReset
     */
    @Override
    protected void close() throws IOException {
        reader = null;
        super.close();
    }




    /**
     * Service provider interface (SPI) for {@link TextImageReader}s. This SPI provides additional
     * fields controlling the character encoding ({@link #charset}), the local to use for parsing
     * numbers, dates or other objects ({@link #locale}) and the value used in place of missing
     * pixel values ({@link #padValue}).
     * <p>
     * By default the {@code charset} and {@code locale} fields are initialized to {@code null},
     * which stands for the platform-dependent character encoding and locale. In addition the
     * {@code padValue} is set to {@link Double#NaN}, which means that there is no pad value. If
     * a subclass wants to fix the encoding, locale and pad value to some format-specific values,
     * it shall specify those values at construction time as in the example below:
     *
     * {@preformat java
     *     public Spi() {
     *         charset  = Charset.forName("ISO-8859-1"); // ISO Latin Alphabet No. 1
     *         locale   = Locale.US;
     *         padValue = -9999;
     *     }
     * }
     *
     * The table below summarizes the initial values.
     * Those values can be modified by subclass constructors.
     * <p>
     * <table border="1">
     *   <tr bgcolor="lightblue">
     *     <th>Field</th>
     *     <th>Value</th>
     *   </tr><tr>
     *     <td>&nbsp;{@link #inputTypes}&nbsp;</td>
     *     <td>&nbsp;{@link String}, {@link File}, {@link URI}, {@link URL}, {@link URLConnection},
     *               {@link Reader}, {@link InputStream}, {@link ImageInputStream},
     *               {@link ReadableByteChannel}&nbsp;</td>
     *   </tr><tr>
     *     <td>&nbsp;{@link #suffixes}&nbsp;</td>
     *     <td>&nbsp;{@code "txt"}, {@code "TXT"},
     *               {@code "asc"}, {@code "ASC"},
     *               {@code "dat"}, {@code "DAT"}&nbsp;</td>
     *   </tr><tr>
     *     <td>&nbsp;{@link #charset}&nbsp;</td>
     *     <td>&nbsp;{@code null} (stands for the
     *         {@linkplain Charset#defaultCharset() platform default})&nbsp;</td>
     *   </tr><tr>
     *     <td>&nbsp;{@link #locale}&nbsp;</td>
     *     <td>&nbsp;{@code null} (stands for the
     *         {@linkplain Locale#getDefault() platform default})&nbsp;</td>
     *   </tr><tr>
     *     <td>&nbsp;{@link #padValue}&nbsp;</td>
     *     <td>&nbsp;{@link Double#NaN} (stands for no pad-value)&nbsp;</td>
     *   </tr><tr>
     *     <td colspan="2" align="center">See
     *     {@linkplain org.geotoolkit.image.io.SpatialImageReader.Spi super-class javadoc}
     *     for remaining fields</td>
     * </tr>
     * </table>
     *
     * @author Martin Desruisseaux (IRD, Geomatys)
     * @version 3.07
     *
     * @see TextImageWriter.Spi
     *
     * @since 3.08 (derived from 2.4)
     * @module
     */
    protected abstract static class Spi extends StreamImageReader.Spi {
        /**
         * List of legal input types for {@link TextImageReader}.
         */
        private static final Class<?>[] INPUT_TYPES = new Class<?>[] {
            File.class,
            Path.class,
            URI.class,
            URL.class,
            URLConnection.class,
            Reader.class,
            InputStream.class,
            ImageInputStream.class,
            ReadableByteChannel.class,
            String.class  // To be interpreted as file path.
        };

        /**
         * Default list of file suffixes. This list is shared with {@link TextImageWriter}.
         */
        static final String[] SUFFIXES = new String[] {
            "txt", "TXT", "asc", "ASC", "dat", "DAT"
        };

        /**
         * Character encoding, or {@code null} for the default. This field is initially
         * {@code null}, which means to use the platform-dependent encoding. Subclasses
         * shall set a non-null value if the files to be decoded use some specific character
         * encoding.
         *
         * @see TextImageReader#getCharset(InputStream)
         */
        protected Charset charset;

        /**
         * The locale for numbers or dates parsing. For example {@link Locale#US} means that
         * numbers are expected to use a dot for the decimal separator. This field is initially
         * {@code null}, which means that the {@linkplain Locale#getDefault() default locale}
         * will be used.
         *
         * @see TextImageReader#getLineFormat(int)
         */
        protected Locale locale;

        /**
         * The pad value, or {@link Double#NaN} if none. Every occurrences of pixel value equals
         * to this pad value will be replaced by {@link Double#NaN} during the read operation.
         * Note that this replacement doesn't apply to non-pixel values (for example <var>x</var>,
         * <var>y</var> coordinates in the format read by
         * {@link org.geotoolkit.image.io.plugin.TextRecordImageReader}).
         *
         * @see TextImageReader#getPadValue(int)
         */
        protected double padValue;

        /**
         * Constructs a quasi-blank {@code TextImageReader.Spi}. This constructor initializes
         * the fields as documented in the <a href="#skip-navbar_top">class javadoc</a>. It is
         * up to the subclass to initialize all other instance variables in order to provide
         * working versions of all methods.
         * <p>
         * For efficiency reasons, the above fields are initialized to shared arrays. Subclasses
         * can assign new arrays, but should not modify the default array content.
         */
        protected Spi() {
            inputTypes = INPUT_TYPES;
            suffixes   = SUFFIXES;
            padValue   = Double.NaN;
        }

        /**
         * Returns {@code true} if the supplied source object appears to be of the format
         * supported by this reader. The default implementation tries to parse the first
         * few lines up to 2048 characters, as below:
         *
         * {@preformat java
         *     return canDecodeInput(source, 2048);
         * }
         *
         * @param  source The object (typically an {@link ImageInputStream}) to be decoded.
         * @return {@code true} if the source <em>seems</em> readable.
         * @throws IOException If an error occurred during reading.
         */
        @Override
        public boolean canDecodeInput(final Object source) throws IOException {
            return canDecodeInput(source, 2048);
        }

        /**
         * Returns {@code true} if the supplied source object appears to be of the format
         * supported by this reader. The default implementation tries to parse the first
         * few lines up to the specified number of characters, then gives those lines to
         * the {@link #isValidHeader(Set)} and {@link #isValidContent(double[][])} methods.
         * <p>
         * The default implementation is suitable for
         * {@link org.geotoolkit.image.io.plugin.TextMatrixImageReader}, i.e.
         * it expects only rows for pixel values (no header) and all rows shall have the
         * same length. If this behavior needs to be changed, consider overriding the
         * {@code isValidHeader} and {@code isValidContent} methods.
         *
         * @param  source The object (typically an {@link ImageInputStream}) to be decoded.
         * @param  readAheadLimit Maximum number of characters to read. If this amount is reached
         *         but this method still unable to make a choice, then it conservatively returns
         *         {@code false}.
         * @return {@code true} if the source <em>seems</em> readable.
         * @throws IOException If an error occurred during reading.
         */
        protected boolean canDecodeInput(final Object source, final int readAheadLimit)
                throws IOException
        {
            final TestReader test = new TestReader(this);
            test.setInput(source);
            try {
                return test.canDecode(readAheadLimit);
            } finally {
                test.close();
            }
        }

        /**
         * Invoked by {@link #canDecodeInput(Object, int)} for determining if the given header is
         * likely to be valid. This method receives in argument a {@code keywords} set containing
         * the first word of every <cite>header lines</cite> (defined below), converted to upper
         * cases using the {@linkplain #locale} defined in this provider.
         * <p>
         * A <cite>header line</cite> is defined as a line which is not a
         * {@linkplain TextImageReader#isComment(String) comment line}, appears before
         * the first row of pixel values and where the first non-blank character is a
         * {@linkplain Character#isJavaIdentifierStart(char) Java identifier start}.
         * <p>
         * The default implementation returns {@code true} if the given set is empty.
         * In other words, by default no header is allowed in the data file.
         *
         * @param  keywords The first word found in every <cite>header lines</cite>,
         *         converted to upper-case.
         * @return {@code true} if the set of keywords is known to this format.
         *
         * @since 3.07
         */
        protected boolean isValidHeader(final Set<String> keywords) {
            return keywords.isEmpty();
        }

        /**
         * Invoked by {@link #canDecodeInput(Object, int)} for determining if the given rows are
         * likely to be valid. This method receives in argument a {@code rows} array containing
         * the first few lines of data. The number of rows depends on the average row length and
         * the {@code readAheadLimit} argument given to {@code canDecodeInput}.
         * <p>
         * The default implementation returns {@code true} if there is at least one row
         * and every row have the same number of columns.
         *
         * @param rows The first few rows.
         * @return {@code true} if the given rows seem to have a valid content.
         */
        protected boolean isValidContent(final double[][] rows) {
            if (rows.length == 0) {
                return false;
            }
            final int length = rows[0].length;
            for (int i=1; i<rows.length; i++) {
                if (rows[i].length != length) {
                    return false;
                }
            }
            return isValidColumnCount(length);
        }

        /**
         * Invoked by {@link #isValidContent(double[][])} for determining if the given number
         * of columns is likely to be valid. This method receives in argument the length of
         * every rows that were given to {@code isValidContent}, when that length is constant.
         * <p>
         * The default implementation returns {@code true} if the number of columns is greater
         * than zero. Subclasses can override this method if they know the expected number of
         * columns.
         *
         * @param  count The number of columns in the first few line of rows.
         * @return {@code true} if the given number of columns seems to be valid.
         *
         * @since 3.07
         */
        protected boolean isValidColumnCount(final int count) {
            return count > 0;
        }
    }
}
