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

import java.util.concurrent.Callable;
import java.lang.foreign.Arena;
import java.lang.foreign.Linker;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.FunctionDescriptor;
import java.lang.invoke.MethodHandle;

/**
 * Base class for sets of method handles to native functions.
 *
 * @see org.apache.sis.storage.panama.NativeFunctions
 */
public abstract class NativeFunctions implements Runnable, Callable<Object> {
    /**
     * The arena used for loading the library, or {@code null} for the global arena.
     */
    private final Arena arena;

    /**
     * The lookup for retrieving the address of a symbol in the native library.
     */
    public final SymbolLookup symbols;

    /**
     * The linker to use for fetching method handles from the {@linkplain #symbols}.
     */
    public final Linker linker;

    /**
     * Creates a new set of handles to native functions.
     */
    protected NativeFunctions(final LibraryLoader loader) {
        arena   = loader.arena;
        symbols = loader.symbols;
        linker  = Linker.nativeLinker();
    }

    /**
     * Returns the method handler for the <abbr>DGGAL</abbr> function of given name and signature.
     */
    @SuppressWarnings("restricted")
    protected final MethodHandle lookup(final String function, final FunctionDescriptor signature) {
        return symbols.find(function).map((method) -> linker.downcallHandle(method, signature)).orElseThrow();
    }

    /**
     * Unloads the native library. If the arena is global,
     * then this method should not be invoked before <abbr>JVM</abbr> shutdown.
     */
    @Override
    public void run() {
        if (arena != null) {
            arena.close();
        }
    }

    /**
     * Synonymous of {@link #run()}, used in shutdown hook.
     */
    @Override
    public final Object call() {
        run();
        return null;
    }
}
