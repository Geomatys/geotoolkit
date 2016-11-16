/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.processing;

import org.geotoolkit.process.ProcessEvent;
import org.geotoolkit.process.ProcessListener;

/**
 * Implement ProcessMonitor with all methods empty.
 * Used by subclass which only need to implement a single method.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class ProcessListenerAdapter implements ProcessListener {

    @Override
    public void started(final ProcessEvent event) {
    }

    @Override
    public void progressing(final ProcessEvent event) {
    }

    @Override
    public void completed(final ProcessEvent event) {
    }

    @Override
    public void failed(final ProcessEvent event) {
    }

    @Override
    public void paused(final ProcessEvent event) {
    }

    @Override
    public void resumed(final ProcessEvent event) {
    }

}
