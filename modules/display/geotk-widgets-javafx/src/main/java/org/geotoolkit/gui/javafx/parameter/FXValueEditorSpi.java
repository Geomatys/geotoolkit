/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.gui.javafx.parameter;

import java.util.Optional;
import java.util.ServiceLoader;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.opengis.feature.AttributeType;
import org.opengis.parameter.ParameterDescriptor;
import org.apache.sis.internal.system.SystemListener;

/**
 * A service provider for javaFx editors of values which can be defined by a
 * {@link ParameterDescriptor} or {@link AttributeType}.
 *
 * @author Alexis Manin (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public abstract class FXValueEditorSpi {

    /** List of all registered Spis in current context. */
    private static final ServiceLoader<FXValueEditorSpi> SERVICE_LOADER = ServiceLoader.load(FXValueEditorSpi.class);

    /**
     * Service loader can be reloaded / accessed from multiple threads, we have to synnchronize its accesses.
     */
    private static final ReentrantReadWriteLock CONTEXT_LOCK = new ReentrantReadWriteLock();

    /**
     * Listen on classpath changes (due to OSGi, etc.) to reload service loader and so, be aware of new editors arrival.
     */
    private static final  SystemListener CONTEXT_LISTENER = new SystemListener("geotk-widgets-javafx") {
        @Override
        protected void classpathChanged() {
            CONTEXT_LOCK.writeLock().lock();
            try {
                SERVICE_LOADER.reload();
            } finally {
                CONTEXT_LOCK.writeLock().unlock();
            }
        }
    };
    static {
        SystemListener.add(CONTEXT_LISTENER);
    }

    /**
     * Check if the current provider can work on values whose definition is compatible
     * with given attribute type.
     * @param property The type of the property to edit.
     * @return True if Spi can provide an editor matching given type, false otherwise.
     */
    public boolean canHandle(AttributeType property){
        return canHandle(property.getValueClass());
    }

    /**
     * Check if the current provider can work on values whose definition is compatible
     * with given descriptor.
     * @param param The descriptor of the property to edit.
     * @return True if Spi can provide an editor matching given type, false otherwise.
     */
    public boolean canHandle(ParameterDescriptor param){
        return canHandle(param.getValueClass());
    }

    /**
     * Check if current Spi can provide objects of input class.
     * @param binding The type of object which must be provided by an editor.
     * @return True if this Spi can create an editor which will work on objects
     * of given type.
     */
    public abstract boolean canHandle(Class binding);

    /**
     * Search an editor capable of editing values compliant with input attribute type.
     *
     * @param target The attribute type describing the value to edit.
     * @return An editor matching given type, or an empty optional.
     */
    public static final Optional<FXValueEditor> findEditor(final AttributeType target) {
        CONTEXT_LOCK.readLock().lock();
        try {
            for (final FXValueEditorSpi spi : SERVICE_LOADER) {
                if (spi.canHandle(target)) {
                    final FXValueEditor editor = spi.createEditor();
                    editor.setAttributeType(target);
                    return Optional.of(editor);
                }
            }
        } finally {
            CONTEXT_LOCK.readLock().unlock();
        }
        return Optional.empty();
    }

    /**
     * Search an editor capable of editing values compliant with input descriptor.
     *
     * @param target The descriptor of the parameter to edit.
     * @return An editor matching given type, or an empty optional.
     */
    public static final Optional<FXValueEditor> findEditor(final ParameterDescriptor target) {
        CONTEXT_LOCK.readLock().lock();
        try {
            for (final FXValueEditorSpi spi : SERVICE_LOADER) {
                if (spi.canHandle(target)) {
                    final FXValueEditor editor = spi.createEditor();
                    editor.setParamDesc(target);
                    return Optional.of(editor);
                }
            }
        } finally {
            CONTEXT_LOCK.readLock().unlock();
        }
        return Optional.empty();
    }

    /**
     * @return an editor provided by current Spi.
     */
    public abstract FXValueEditor createEditor();
}
