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
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Locale;
import java.util.Collections;
import java.text.ParseException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageReadParam;

import org.apache.sis.util.ArraysExt;
import org.geotoolkit.io.LineFormat;


/**
 * A dummy implementation of {@link TextImageReader} used only by default implementation
 * of {@link TextImageReader.Spi#canDecodeInput}. This class is more lightweight than
 * loading the real image reader implementation.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.08
 *
 * @since 3.08 (derived from 2.4)
 * @module
 */
final class TestReader extends TextImageReader {
    /**
     * The input stream to {@linkplain InputStream#reset reset}, or {@code null} if none. This
     * field may be assigned by {@link #getReader()}. If it is non-null, then the {@code reset()}
     * method of this stream shall be invoked instead than {@link Reader#reset()}.
     * <p>
     * This stream will never be closed by this {@code TestReader} class, i.e. it should never
     * be the same instance than {@link #closeOnReset}.
     */
    private InputStream marked;

    /**
     * The keys found in the header (without value), or {@code null} if none.
     */
    private Set<String> keywords;

    /**
     * The object to use for parsing the lines, or {@code null} if not yet created.
     */
    private LineFormat parser;

    /**
     * The rows which have been parsed, or {@code null} if not yet created.
     */
    private double[][] rows;

    /**
     * The number of valid entries in {@link #rowCount}.
     */
    private int rowCount;

    /**
     * Creates a new reader for the specified provider.
     * The provider is mandatory and can not be null.
     */
    public TestReader(final TextImageReader.Spi provider) {
        super(provider);
    }

    /**
     * Returns a null width.
     */
    @Override
    public int getWidth(int imageIndex) {
        return 0;
    }

    /**
     * Returns a null height.
     */
    @Override
    public int getHeight(int imageIndex) {
        return 0;
    }

    /**
     * Throws an {@link UnsupportedOperationException}.
     */
    @Override
    public BufferedImage read(final int imageIndex, final ImageReadParam param) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the {@linkplain #input input} as a {@linkplain Reader reader}, which doesn't need to
     * be {@linkplain BufferedReader buffered}. If the reader is an instance supplied explicitly by
     * the user, then it will be {@linkplain Reader#mark marked} with the specified read ahead limit.
     *
     * @return {@link #getInput} as a {@link Reader}, or {@code null} if this method
     *         can't provide a reader suitable for {@code canDecode}.
     * @throws IllegalStateException if the {@linkplain #input input} is not set.
     * @throws IOException If the input stream can't be created for an other reason.
     */
    private Reader getReader(final int readAheadLimit) throws IllegalStateException, IOException {
        final Object input = getInput();
        if (input instanceof Reader) {
            final Reader reader = (Reader) input;
            if (!reader.markSupported()) {
                return null;
            }
            reader.mark(readAheadLimit);
            return reader;
            // Do not set 'closeOnReset' since we don't own the reader.
        }
        final InputStream stream = getInputStream();
        if (closeOnReset == null) {
            // If we are not allowed to close and reopen a new stream on ImageReader.read, then
            // we must be able to mark the stream otherwise we will not support canDecode(...).
            if (!stream.markSupported()) {
                return null;
            }
            stream.mark(readAheadLimit);
            marked = stream;
        }
        final Reader reader = getInputStreamReader(stream);
        if (closeOnReset == stream) {
            closeOnReset = reader;
        }
        return reader;
    }

    /**
     * Resets the stream to the marked position.
     */
    private void reset(final Reader reader) throws IOException {
        final InputStream m = marked;
        if (m != null) {
            marked = null;
            m.reset();
            // Do not close the Reader, since we don't
            // want to close the underlying InputStream.
        } else if (reader != null && reader != closeOnReset) {
            reader.reset();
        } else {
            super.close();
        }
    }

    /**
     * Checks if the {@linkplain #getReader reader} seems to contains a readable ASCII file.
     * This method tries to read the first few lines. The caller is responsable for invoking
     * {@link #close} after this method.
     *
     * {@section How to change the default behavior}
     * This method invokes {@link #parseLine(String)} for each line found. When we have
     * reached the {@code readAheadLimit}, this method invokes {@link #isValidContent()}.
     * The default implementations are:
     * <p>
     * <ul>
     *   <li>
     *     {@code parseLine} skips the comment lines and stores the values in the
     *     {@link #rows} field.</li>
     *   <li>
     *     {@code isValidContent} delegates to the method below with the data
     *     that has been collected in the previous step:
     *     <ul>
     *       <li>{@link TextImageReader.Spi#isValidHeader(Set)}</li>
     *       <li>{@link TextImageReader.Spi#isValidContent(double[][])}</li>
     *     </ul>
     *   </li>
     * </ul>
     * <p>
     * If a different behavior is wanted, those two methods should be made accessible and overridden.
     *
     * @param  readAheadLimit Maximum number of characters to read. If this amount is reached
     *         but this method still unable to make a choice, then it returns {@code null}.
     * @return {@code true} if the source <em>seems</em> readable, {@code false} otherwise.
     * @throws IOException If an error occurred during reading.
     */
    final boolean canDecode(int readAheadLimit) throws IOException {
        final Reader input = getReader(readAheadLimit);
        if (input == null) {
            return false;
        }
        /*
         * Cheap test first: read only a few bytes an check if there is characters which seem
         * to be binary. If we pass this cheap test, then fill the remaining of the buffer and
         * check again.
         */
        final char[] buffer = new char[readAheadLimit];
        readAheadLimit = input.read(buffer, 0, Math.min(readAheadLimit, 256));
        if (readAheadLimit < 0 || containsBinary(buffer, 0, readAheadLimit)) {
            reset(input);
            return false;
        }
        if (true) { // Set to 'false' if we want to use only the above 256 characters.
            final int more = input.read(buffer, readAheadLimit, buffer.length - readAheadLimit);
            if (more >= 0) {
                if (containsBinary(buffer, readAheadLimit, readAheadLimit + more)) {
                    reset(input);
                    return false;
                }
                readAheadLimit += more;
            }
        }
        /*
         * At this point we have determined that the stream is (apparently) not binary.
         * Now parse the content of the above buffer.
         */
        int lower = 0;
scan:   while (lower < readAheadLimit) {
            // Skip line feeds at the beginning of the line. They may be
            // trailing characters from the previous iteration of the loop.
            char c = buffer[lower];
            if (c == '\r' || c == '\n') {
                lower++;
                continue;
            }
            // Search the end of line. If we reach the end of the buffer,
            // do not attempt to parse that last line since it is incomplete.
            int upper = lower;
            while ((c = buffer[upper]) != '\r' && c != '\n') {
                if (++upper >= readAheadLimit) {
                    break scan;
                }
            }
            // Try to parse a line.
            final String line = new String(buffer, lower, upper-lower);
            if (!isComment(line) && !parseLine(line)) {
                reset(input);
                return false;
            }
            lower = upper;
        }
        reset(input);
        return isValidContent();
    }

    /**
     * Returns {@code true} if the given range in the given buffer seems to contains binary data.
     * This is a cheap test before the more expensive parsing of data.
     */
    private static boolean containsBinary(final char[] buffer, int lower, final int upper) {
        while (lower < upper) {
            final char c = buffer[lower++];
            if (c < 32 && !Character.isWhitespace(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Invoked by {@link #canDecode(int)} for each line found before the {@code readAheadLimit}.
     * The default implementation stores the first word in the {@linkplain #keywords} set if it
     * seem to be an identifier, or parses the line as a row of numbers and store the result in
     * the {@link #rows} field otherwise.
     *
     * @param  line The line to parse.
     * @return {@code true} if the line is valid, or {@code false} otherwise.
     * @throws IOException If an error occurred while processing the line.
     *
     * @since 3.07
     */
    private boolean parseLine(final String line) throws IOException {
        /*
         * First, check if we have a header keyword. We check the header only
         * if the parsing of rows did not started yet. Headers after the rows
         * are not allowed.
         */
        if (rows == null) {
            String keyword = line.trim();
            final int length = keyword.length();
            if (length == 0) {
                // Ignore blank lines. Note that some formats could consider blank lines as
                // the beginning of next band or next image. This TestReader just ignore them.
                return true;
            }
            if (Character.isJavaIdentifierStart(keyword.charAt(0))) {
                int stop = 0;
                while (++stop < length && Character.isJavaIdentifierPart(keyword.charAt(stop)));
                keyword = keyword.substring(0, stop);
                if (keywords == null) {
                    keywords = new HashSet<>();
                }
                final Locale locale = getDataLocale();
                keyword = (locale != null) ? keyword.toUpperCase(locale) : keyword.toUpperCase();
                keywords.add(keyword);
                return true;
            }
        }
        /*
         * If we reach this point, the line is not considered a header.
         * Try to parse it as a row of pixel values.
         */
        if (parser == null) {
            parser = getLineFormat(0);
            rows = new double[16][];
        }
        try {
            if (parser.setLine(line) != 0) {
                if (rowCount == rows.length) {
                    rows = Arrays.copyOf(rows, rows.length * 2);
                }
                rows[rowCount] = parser.getValues(rows[rowCount]);
                rowCount++;
            }
        } catch (ParseException exception) {
            return false;
        }
        return true;
    }

    /**
     * Invoked by {@link #canDecode(int)} after every lines have been parsed. The default
     * implementation gives {@link #keywords} to {@link TextImageReader.Spi#isValidHader(Set)}
     * and the {@link #rows} to {@link TextImageReader.Spi#isValidContent(double[][])}.
     *
     * @return {@code true} if the content is valid, or {@code false} otherwise.
     * @throws IOException If an error occurred.
     *
     * @since 3.07
     */
    private boolean isValidContent() throws IOException {
        final TextImageReader.Spi spi = (TextImageReader.Spi) originatingProvider;
        if (keywords == null) {
            keywords = Collections.emptySet();
        }
        if (spi.isValidHeader(keywords)) {
            if (rows == null) {
                rows = new double[0][];
            } else {
                rows = ArraysExt.resize(rows, rowCount);
            }
            return spi.isValidContent(rows);
        }
        return false;
    }

    /**
     * Closes the reader created by this class.
     */
    @Override
    protected void close() throws IOException {
        reset(null);
        marked   = null;
        keywords = null;
        parser   = null;
        rows     = null;
        rowCount = 0;
        super.close();
    }
}
