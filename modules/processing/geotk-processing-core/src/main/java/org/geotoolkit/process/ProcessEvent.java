/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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

package org.geotoolkit.process;

import java.util.EventObject;
import org.opengis.util.InternationalString;

/**
 * Event send by the process when it is running.
 * @author johann Sorel (Geomatys)
 */
public class ProcessEvent extends EventObject {

    public static final int PROGRESS_INDETERMINATE = Integer.MIN_VALUE;

    private final int progress;
    private final InternationalString message;
    private final Throwable sub;

    public ProcessEvent(Object source){
        this(source,PROGRESS_INDETERMINATE,null,null);
    }
    
    public ProcessEvent(Object source, int progress, InternationalString message, Throwable sub){
        super(source);
        this.progress = progress;
        this.message = message;
        this.sub = sub;
    }

    /**
     * The process may send some user friendly informations that could be displayed
     * in a dialog.
     * @return InternationalString or null
     */
    public InternationalString getMessage() {
        return message;
    }

    /**
     * Send the progressing value of the process
     * @return int between 0 and 100 or -1 if indeterminate
     */
    public int getProgress() {
        return progress;
    }

    /**
     * If and error or warning has occured is it available with this method.
     */
    public Throwable getThrowable() {
        return sub;
    }

}
