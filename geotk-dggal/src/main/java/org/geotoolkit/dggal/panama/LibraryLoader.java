/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.dggal.panama;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.lang.foreign.Arena;
import java.lang.foreign.SymbolLookup;
import java.lang.reflect.InvocationTargetException;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.system.Shutdown;
import org.geotoolkit.dggal.DGGALBindingException;

/**
 * A helper class for loading a native library.
 *
 * @see org.apache.sis.storage.panama.LibraryLoader
 */
public final class LibraryLoader<T extends NativeFunctions> {
    /**
     * Value to assign to {@link NativeFunctions#arena}.
     */
    Arena arena;

    /**
     * Value to assign to {@link NativeFunctions#symbols}.
     */
    SymbolLookup symbols;

    /**
     * Whether the library has been found.
     */
    public LibraryStatus status;

    /**
     * Cause of failure to load the library, or {@code null} if none.
     */
    private Throwable error;

    private final Class<T> clazz;
    private final String commonName;
    private final String windowsName;
    private final String linuxName;

    /**
     * Creates a new instance.
     */
    public LibraryLoader(Class<T> clazz, String commonName, String windowsName, String linuxName) {
        this.clazz = clazz;
        this.commonName = commonName;
        this.windowsName = windowsName;
        this.linuxName = linuxName;
    }

    /**
     * Loads the native library from the given file.
     * Callers should register the returned instance in a {@link java.lang.ref.Cleaner}.
     */
    @SuppressWarnings("restricted")
    public T load(final Path library) {
        status = LibraryStatus.CANNOT_LOAD_LIBRARY;
        arena  = Arena.ofShared();
        final T instance;
        try {
            symbols  = SymbolLookup.libraryLookup(library, arena);
            status   = LibraryStatus.FUNCTION_NOT_FOUND;
            instance = clazz.getConstructor(LibraryLoader.class).newInstance(this);
            status   = LibraryStatus.LOADED;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            arena.close();
            throw new DGGALBindingException(ex);
        } catch (Throwable e) {
            arena.close();
            throw e;
        }
        return instance;
    }

    /**
     * Searches the native library in the default library path.
     */
    @SuppressWarnings("restricted")
    public T global() {
        status = LibraryStatus.CANNOT_LOAD_LIBRARY;
        String filename = (File.separatorChar == '\\') ? windowsName : linuxName;
        try {
            //symbols = SymbolLookup.libraryLookup(filename, Arena.global());
            symbols = SymbolLookup.loaderLookup();
            T instance = clazz.getConstructor(LibraryLoader.class).newInstance(this);
            status = LibraryStatus.LOADED;
            Shutdown.register(instance);
            return instance;
        } catch (Throwable e) {
            error = e;
            return null;
        }
    }

    /**
     * Throws an exception if the loading of the native library failed.
     */
    public void validate() throws DataStoreException {
        if (error != null) {
            throw new DataStoreException(error);
        }
    }

    /**
     * Returns the error as a log message.
     */
    public Optional<LogRecord> getError() {
        if (error == null) {
            return Optional.empty();
        }
        var record = new LogRecord(Level.CONFIG, "Cannot initialize the " + commonName + " library.");
        record.setThrown(error);
        return Optional.of(record);
    }
}
