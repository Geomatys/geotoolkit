/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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

import java.io.File;
import java.util.Arrays;
import javax.imageio.IIOException;

import org.geotoolkit.resources.Errors;
import org.apache.sis.util.resources.IndexedResourceBundle;
import org.geotoolkit.internal.image.io.Formats;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.util.converter.Classes;


/**
 * Thrown if the {@linkplain javax.imageio.ImageReader#setInput(Object, boolean, boolean) input
 * given to a reader} or the {@linkplain javax.imageio.ImageWriter#setOutput(Object) output
 * given to a writer} is invalid. This exception is thrown for errors that can not be detected
 * at {@code setInput(...)} or {@code setOutput(...)} invocation time, but are rather detected
 * when the first read or write operation is attempted.
 * <p>
 * This exception is used for programmatic errors only. It shall not be used for corrupted files
 * or connection problems. This exception is used for example by {@link ImageReaderAdapter} and
 * {@link ImageWriterAdapter} because they defer the initialization of their underlying image
 * reader or writer until first needed.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.10
 *
 * @since 3.08
 * @module
 */
public class InvalidImageStoreException extends IIOException {
    /**
     * Serial version for compatibility with different versions.
     */
    private static final long serialVersionUID = 7306875489752707142L;

    /**
     * Constructs a new exception with the specified detail message.
     * The detail message is saved for later retrieval by the {@link #getMessage()} method.
     *
     * @param message The details message.
     */
    public InvalidImageStoreException(final String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     * The cause is saved for later retrieval by the {@link #getCause()} method.
     *
     * @param message The details message.
     * @param cause The cause.
     */
    public InvalidImageStoreException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Builds an error message for an input of unsupported type.
     *
     * @param  resources The resource bundle to use for formatting the message, or {@code null} if unknown.
     * @param  io        The actual input or output object.
     * @param  expected  The expected input or output types.
     * @param  write     {@code true} for building a message for write operation, or {@code false}
     *                   for read operation.
     *
     * @since 3.10
     */
    InvalidImageStoreException(final IndexedResourceBundle resources, final Object io,
            final Class<?>[] expected, final boolean write)
    {
        super(unknownType(resources, io, expected, write));
    }

    /**
     * Workaround for RFE #4093999 ("Relax constraint on placement of this()/super()
     * call in constructors").
     */
    private static String unknownType(final IndexedResourceBundle resources, final Object io,
            final Class<?>[] expected, final boolean write)
    {
        /*
         * In the particular case where the input or output object is a file,
         * check if the file (read mode) and the parent directory (read/write
         * mode) exists.
         */
        if (io instanceof File) {
            File file = (File) io;
            if (write) {
                file = file.getParentFile();
                if (file != null && !file.isDirectory()) {
                    return resources.getString(Errors.Keys.NOT_A_DIRECTORY_1, file);
                }
            } else if (!file.exists()) {
                int key = Errors.Keys.FILE_DOES_NOT_EXIST_1;
                final File parent = file.getParentFile();
                if (parent != null && !parent.isDirectory()) {
                    key = Errors.Keys.NOT_A_DIRECTORY_1;
                    file = parent;
                }
                return resources.getString(key, file);
            }
        }
        /*
         * If the given input or output was supposed to be a supported type, format an error
         * message saying that we can't read or write to that input or output object.
         */
        if (expected != null) {
            for (final Class<?> e : expected) {
                if (e.isInstance(io)) {
                    return resources.getString(
                            write ? Errors.Keys.CANT_WRITE_FILE_1 : Errors.Keys.CANT_READ_FILE_1,
                            IOUtilities.name(io));
                }
            }
        }
        /*
         * If the input or output object is not a supported type, format an error
         * message with the list of expected types.
         */
        String message = resources.getString(Errors.Keys.UNKNOWN_TYPE_1, Classes.getShortClassName(io));
        if (expected != null && expected.length != 0) {
            String[] names = new String[expected.length];
            for (int i=0; i<expected.length; i++) {
                names[i] = Classes.getShortName(expected[i]);
            }
            names = Formats.simplify(names);
            message = message + ' ' + resources.getString(
                    Errors.Keys.EXPECTED_ONE_OF_1, Arrays.toString(names));
        }
        return message;
    }
}
