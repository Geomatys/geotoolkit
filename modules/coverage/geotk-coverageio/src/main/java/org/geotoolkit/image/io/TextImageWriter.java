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
import java.nio.charset.Charset;
import java.nio.channels.WritableByteChannel;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.awt.image.DataBuffer;
import javax.imageio.IIOImage;
import javax.imageio.ImageWriteParam;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;
import javax.media.jai.iterator.RectIter;


/**
 * Base class for image writers that expect a {@link BufferedWriter} output.
 * "<cite>Text images</cite>" are usually ASCII files where pixels values are
 * actually the geophysical values. This base class provides the following conveniences:
 * <p>
 * <ul>
 *   <li>Get a {@link BufferedWriter} from the output types, which may be a any type documented
 *       in the {@linkplain StreamImageWriter super-class} plus {@link Writer}.</li>
 *   <li>Offer a {@link #createNumberFormat createNumberFormat(...)} method which set the
 *       {@linkplain NumberFormat#getMaximumFractionDigits() number of fraction digits} to
 *       a value determined from the sample values present in the image.</li>
 *   <li>Get the character encoding and the locale (for formating numbers) from the fields declared
 *       in the {@linkplain Spi Service Provider}. Alternatively, subclasses can also get more
 *       control by overriding the {@link #getCharset(ImageWriteParam)} method.</li>
 * </ul>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.08
 *
 * @see TextImageReader
 *
 * @since 3.08 (derived from 1.2)
 * @module
 */
public abstract class TextImageWriter extends StreamImageWriter {
    /**
     * Maximum number of digits to be allowed by {@link #createNumberFormat}.
     */
    private static final int MAXIMUM_DIGITS = 12;

    /**
     * Do not force the formatting of fraction digits if the sample values are equal or
     * greater than 1E+6.
     */
    private static final double NODIGITS_THRESHOLD = 1E+6;

    /**
     * Number of digits to check after the last one, as 10<sup>-n</sup>. The default value is
     * 1E-1. This means that if the digit immediately after the last one is 0, we will consider
     * that we have reached the intended precision.
     * <p>
     * For adjusting the number of digits to check after the last one, just put this number
     * as a negative power in place of the "-1" above.
     */
    private static final double DELTA_THRESHOLD = 1E-1;

    /**
     * The maximum value found during the last call to {@link #createNumberFormat}.
     */
    private double maximum;

    /**
     * {@link #output} as a buffered writer, or {@code null} if none.
     *
     * @see #getWriter
     */
    private BufferedWriter writer;

    /**
     * Constructs a {@code TextImageWriter}.
     *
     * @param provider The {@link ImageWriterSpi} that is constructing this object, or {@code null}.
     */
    protected TextImageWriter(final Spi provider) {
        super(provider);
    }

    /**
     * Returns the locale to use for encoding values, or {@code null} for the
     * {@linkplain Locale#getDefault default}. The default implementation returns the
     * {@linkplain Spi#locale locale} specified to the {@code Spi} object given to this
     * {@code TextImageWriter} constructor. Subclasses can override this method if they
     * want to specify the data locale in some other way.
     * <p>
     * <b>Note:</b> This locale should not be confused with {@link #getLocale}.
     *
     * @param  parameters The write parameters, or {@code null} for the defaults.
     * @return The locale to use for parsing numbers in the image file.
     *
     * @see Spi#locale
     */
    protected Locale getDataLocale(final ImageWriteParam parameters) {
        return (originatingProvider instanceof Spi) ? ((Spi) originatingProvider).locale : null;
    }

    /**
     * Returns the character set to use for encoding the string to the output stream.
     * The default implementation returns the {@linkplain Spi#charset character set}
     * specified to the {@code Spi} object given to this {@code TextImageWriter} constructor.
     * Subclasses can override this method if they want to specify the character encoding in
     * some other way.
     *
     * @param  parameters The write parameters, or {@code null} for the defaults.
     * @return The character encoding, or {@code null} for the platform default encoding.
     * @throws IOException If reading from the output stream failed.
     *
     * @see Spi#charset
     */
    protected Charset getCharset(final ImageWriteParam parameters) throws IOException {
        return (originatingProvider instanceof Spi) ? ((Spi) originatingProvider).charset : null;
    }

    /**
     * Returns the line separator to use when writing to the output stream. The default
     * implementation returns the {@linkplain Spi#lineSeparator line separator} specified
     * to the {@link Spi} object given to this {@code TextImageWriter} constructor. Subclasses
     * can override this method if they want to specify the line separator in some other way.
     *
     * @param  parameters The write parameters, or {@code null} for the defaults.
     * @return The line separator to use for writing the image.
     *
     * @see Spi#lineSeparator
     */
    protected String getLineSeparator(final ImageWriteParam parameters) {
        if (originatingProvider instanceof Spi) {
            final String lineSeparator = ((Spi) originatingProvider).lineSeparator;
            if (lineSeparator != null) {
                return lineSeparator;
            }
        }
        return System.lineSeparator();
    }

    /**
     * Returns the {@linkplain #output output} as a writer. If the output is already a buffered
     * writer, then it is returned unchanged. Otherwise the default implementation creates a new
     * {@link BufferedWriter} from various output types including {@link File}, {@link URL},
     * {@link URLConnection}, {@link Writer}, {@link OutputStream} and {@link ImageOutputStream}.
     * <p>
     * This method creates the new writer only when first invoked. All subsequent calls will
     * returns the same instance. Consequently, the returned writer should never be closed by
     * the caller. It may be {@linkplain #close closed} automatically when {@link #setOutput
     * setOutput(Object)}, {@link #reset() reset()} or {@link #dispose() dispose()} methods
     * are invoked.
     *
     * @param  parameters The write parameters, or {@code null} for the defaults.
     * @return {@link #getOutput} usually as a {@link BufferedWriter}.
     * @throws IllegalStateException if the {@linkplain #output output} is not set.
     * @throws IOException If the output stream can't be created for an other reason.
     *
     * @see #getOutput
     * @see #getOutputStream
     */
    protected BufferedWriter getWriter(final ImageWriteParam parameters)
            throws IllegalStateException, IOException
    {
        if (writer == null) {
            final Object output = getOutput();
            if (output instanceof BufferedWriter) {
                writer = (BufferedWriter) output;
                closeOnReset = null; // We don't own the underlying writer, so don't close it.
            } else if (output instanceof Writer) {
                writer = new BufferedWriter((Writer) output);
                closeOnReset = null; // We don't own the underlying writer, so don't close it.
            } else {
                final OutputStream stream = getOutputStream();
                final Charset charset = getCharset(parameters);
                writer = new BufferedWriter((charset != null) ?
                        new OutputStreamWriter(stream, charset) : new OutputStreamWriter(stream));
                if (closeOnReset == stream) {
                    closeOnReset = writer;
                }
            }
        }
        return writer;
    }

    /**
     * Returns a number format to be used for formatting the sample values in the given image.
     *
     * @param image The image or raster to be written.
     * @param parameters The write parameters, or {@code null} if the whole image will be written.
     * @return A number format appropriate for the given image.
     */
    protected NumberFormat createNumberFormat(final IIOImage image, final ImageWriteParam parameters) {
        final Locale locale = getDataLocale(parameters);
        final int type = image.hasRaster() ? image.getRaster().getTransferType() :
                         image.getRenderedImage().getSampleModel().getDataType();
        if (type != DataBuffer.TYPE_FLOAT && type != DataBuffer.TYPE_DOUBLE) {
            maximum = (1 << DataBuffer.getDataTypeSize(type)) - 1;
            return (locale != null) ? NumberFormat.getIntegerInstance(locale)
                                    : NumberFormat.getIntegerInstance();
        }
        int digits = 0;
        double multiple = 1;
        maximum = Double.NEGATIVE_INFINITY;
        final RectIter iterator = createRectIter(image, parameters);
        if (!iterator.finishedBands()) do {
            if (!iterator.finishedLines()) do {
                if (!iterator.finishedPixels()) do {
                    final double value = Math.abs(iterator.getSampleDouble());
                    if (Double.isInfinite(value)) {
                        continue;
                    }
                    // Following code is NaN tolerant - no need for explicit check.
                    if (value > maximum) {
                        maximum = value;
                    }
                    while (true) {
                        double scaled = value * multiple;
                        if (type == DataBuffer.TYPE_FLOAT) {
                            scaled = (float) scaled; // Drops the extra digits.
                        }
                        // Condition below uses '!' in order to cath NaN values.
                        if (!(Math.abs(scaled - Math.rint(scaled)) >= DELTA_THRESHOLD)) {
                            break;
                        }
                        if (++digits > MAXIMUM_DIGITS) {
                            return createNumberFormat(locale);
                        }
                        multiple *= 10;
                    }
                } while (!iterator.nextPixelDone());
                iterator.startPixels();
            } while (!iterator.nextLineDone());
            iterator.startLines();
        } while (!iterator.nextBandDone());
        /*
         * 'digits' should now be the exact number of fraction digits to format. However the above
         * algorithm do not work if all values are smaller (in absolute value) to DELTA_THRESHOLD,
         * in which case 'digits' is still set to 0. In such case it is better to keep the default
         * format unchanged, since it should be generic enough.
         */
        final NumberFormat format = createNumberFormat(locale);
        if (digits != 0 || maximum >= DELTA_THRESHOLD) {
            format.setMaximumFractionDigits(digits);
            if (maximum < NODIGITS_THRESHOLD) {
                format.setMinimumFractionDigits(digits);
            }
        }
        return format;
    }

    /**
     * Returns the number format for the given locale. If the given locale is null,
     * then the default locale is used.
     */
    private static NumberFormat createNumberFormat(final Locale locale) {
        return (locale != null) ? NumberFormat.getNumberInstance(locale) : NumberFormat.getNumberInstance();
    }

    /**
     * Returns the expected position of the fraction part for numbers to be formatted using the
     * given format. This method should be invoked after {@link #createNumberFormat}, but the
     * given format doesn't need to be the instance returned by the later.
     *
     * @param  format The format to be used for formatting numbers.
     * @return The expected position of the fraction part.
     */
    protected FieldPosition getExpectedFractionPosition(final NumberFormat format) {
        final int minimumDigits = (int) Math.floor(Math.log10(maximum)) + 1;
        int width  = Math.max(format.getMinimumIntegerDigits(), minimumDigits);
        int digits = Math.min(format.getMaximumFractionDigits(), MAXIMUM_DIGITS);
        if (format instanceof DecimalFormat) {
            final DecimalFormat decimal = (DecimalFormat) format;
            if (digits > 0 || decimal.isDecimalSeparatorAlwaysShown()) {
                width++;
            }
            width  += Math.max(decimal.getNegativePrefix().length(),
                               decimal.getPositivePrefix().length());
            digits += Math.max(decimal.getNegativeSuffix().length(),
                               decimal.getPositiveSuffix().length());
        }
        final FieldPosition position = new FieldPosition(NumberFormat.FRACTION_FIELD);
        position.setBeginIndex(width);
        position.setEndIndex(width += digits); // NOSONAR (see below)
        // 'width' is now the full width. We don't do anything with it at this time,
        // but maybe in some future version...
        return position;
    }

    /**
     * Closes the writer created by {@link #getWriter getWriter(...)}. This method does nothing
     * if the writer is the {@linkplain #output output} instance given by the user rather than
     * a writer created by this class from a {@link File} or {@link URL} output.
     *
     * @throws IOException If an error occurred while closing the writer.
     *
     * @see #closeOnReset
     */
    @Override
    protected void close() throws IOException {
        writer = null;
        super.close();
    }




    /**
     * Service provider interface (SPI) for {@link TextImageWriter}. This SPI provides additional
     * fields controling the character encoding ({@link #charset}), the local to use for formating
     * numbers, dates or other objects ({@link #locale}) and the line separator
     * ({@link #lineSeparator}).
     * <p>
     * By default the {@code charset}, {@code locale} and {@code lineSeparator} fields are
     * initialized to {@code null}, which stands for the platform-dependent defaults. If a
     * subclass wants to fix the encoding and locale to some format-specific values, it shall
     * specify those values at construction time as in the example below:
     *
     * {@preformat java
     *     public Spi() {
     *         charset = Charset.forName("ISO-8859-1"); // ISO Latin Alphabet No. 1
     *         locale  = Locale.US;
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
     *     <td>&nbsp;{@link #outputTypes}&nbsp;</td>
     *     <td>&nbsp;{@link String}, {@link File}, {@link URI}, {@link URL}, {@link URLConnection},
     *               {@link Writer}, {@link OutputStream}, {@link ImageOutputStream},
     *               {@link WritableByteChannel}&nbsp;</td>
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
     *     <td>&nbsp;{@link #lineSeparator}&nbsp;</td>
     *     <td>&nbsp;{@code null} (stands for the platform default)&nbsp;</td>
     *   </tr><tr>
     *     <td colspan="2" align="center">See
     *     {@linkplain org.geotoolkit.image.io.SpatialImageWriter.Spi super-class javadoc}
     *     for remaining fields</td>
     * </tr>
     * </table>
     *
     * @author Martin Desruisseaux (IRD, Geomatys)
     * @version 3.07
     *
     * @see TextImageReader.Spi
     *
     * @since 3.08 (derived from 2.4)
     * @module
     */
    protected abstract static class Spi extends StreamImageWriter.Spi {
        /**
         * List of legal output types for {@link TextImageWriter}.
         */
        private static final Class<?>[] OUTPUT_TYPES = new Class<?>[] {
            File.class,
            URI.class,
            URL.class,
            URLConnection.class,
            Writer.class,
            OutputStream.class,
            ImageOutputStream.class,
            WritableByteChannel.class,
            String.class  // To be interpreted as file path.
        };

        /**
         * Character encoding, or {@code null} for the default. This field is initially
         * {@code null}. A value shall be set by subclasses if the files to be encoded
         * use some specific character encoding.
         *
         * @see TextImageWriter#getCharset(ImageWriteParam)
         */
        protected Charset charset;

        /**
         * The locale for numbers formatting. For example {@link Locale#US} means that
         * numbers are expected to use dot as decimal separator. This field is initially
         * {@code null}, which means that default locale should be used.
         *
         * @see TextImageWriter#getDataLocale(ImageWriteParam)
         */
        protected Locale locale;

        /**
         * The line separator to use, or {@code null} for the system default.
         *
         * @see TextImageWriter#getLineSeparator(ImageWriteParam)
         */
        protected String lineSeparator;

        /**
         * Constructs a quasi-blank {@code TextImageWriter.Spi}. This constructor initializes
         * the fields as documented in the <a href="#skip-navbar_top">class javadoc</a>. It is
         * up to the subclass to initialize all other instance variables in order to provide
         * working versions of all methods.
         * <p>
         * For efficiency reasons, the above fields are initialized to shared arrays. Subclasses
         * can assign new arrays, but should not modify the default array content.
         */
        protected Spi() {
            outputTypes = OUTPUT_TYPES;
            suffixes    = TextImageReader.Spi.SUFFIXES;
        }
    }
}
