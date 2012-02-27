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
package org.geotoolkit.process;

import java.util.EventObject;
import org.opengis.util.InternationalString;
import org.geotoolkit.util.SimpleInternationalString;


/**
 * Event send by a running {@linkplain Process process} to its {@linkplain ProcessListener listeners}.
 * This event contains an optional user-friendly {@linkplain #getTask() task description} together
 * with the {@linkplain #getProgress() task progression} as a percentage. An optional
 * {@linkplain #getException() exception} can also be specified if case of error or warning.
 *
 * @author Johann Sorel (Geomatys)
 * @version 3.19
 *
 * @since 3.19
 * @module
 */
public class ProcessEvent extends EventObject {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 622026012845207483L;

    /**
     * A message that describe the task under execution, or {@code null} if none.
     *
     * @see #getTask()
     */
    private final InternationalString task;

    /**
     * The current progress as a percent completed, from 0 to 100 inclusive.
     * The {@link Float#NaN NaN} value means that the process is in an undetermined state.
     */
    private final float progress;

    /**
     * An error or warning that occurred while executing the task, or {@code null} if none.
     * The exception is considered fatal if this {@code ProcessEvent} was given to the
     * {@link ProcessListener#failed(ProcessEvent)} method. Otherwise the exception is
     * considered as a warning only.
     */
    private final Exception exception;

    /**
     * Creates a new event with the given source but no message, no exception and an
     * undetermined progress state.
     *
     * @param source The source of this event.
     */
    public ProcessEvent(final Process source){
        this(source, null, Float.NaN, null);
    }

    /**
     * Creates a new event with the given source, task and progress.
     *
     * @param source    The source of this event.
     * @param task      A message that describe the task under execution, or {@code null} if none.
     * @param progress  The progress as a number between 0 and 100, or {@link Float#NaN} if undetermined.
     */
    public ProcessEvent(final Process source, final CharSequence task, final float progress) {
        this(source, task, progress, null);
    }

    /**
     * Creates a new event with the given source, task, progress and exception.
     *
     * @param source    The source of this event.
     * @param task      A message that describe the task under execution, or {@code null} if none.
     * @param progress  The progress as a number between 0 and 100, or {@link Float#NaN} if undetermined.
     * @param exception An error or warning that occurred while executing the task, or {@code null} if none.
     */
    public ProcessEvent(final Process source, final CharSequence task, final float progress, final Exception exception) {
        super(source);
        this.progress = progress;
        this.task = SimpleInternationalString.wrap(task);
        this.exception = exception;
    }

    /**
     * Returns the source of this event.
     */
    @Override
    public Process getSource() {
        return (Process) super.getSource();
    }

    /**
     * Returns a message that describes the task under execution, or {@code null} if none.
     * The message may contain user friendly information provided by the process. This message
     * may be show in a dialog box or elsewhere, at listener choice.
     *
     * @return A message that describe the tasks under execution, or {@code null} if none.
     */
    public InternationalString getTask() {
        return task;
    }

    /**
     * Returns the current progress as a percent completed, from 0 to 100 inclusive.
     * The {@link Float#NaN NaN} value means that the process is in an undetermined state.
     *
     * @return The current progress from 0 to 100 inclusive, or {@link Float#NaN} if undetermined.
     */
    public float getProgress() {
        return progress;
    }

    /**
     * Returns an error or warning that occurred while executing the task, or {@code null} if none.
     * The exception is considered fatal if this {@code ProcessEvent} was given to the
     * {@link ProcessListener#failed(ProcessEvent)} method. Otherwise the exception is
     * considered as a warning only.
     *
     * @return Error or warning that occurred while executing the task, or {@code null} if none.
     */
    public Exception getException() {
        return exception;
    }

    /**
     * Returns a string representation of this event.
     */
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder(getClass().getSimpleName());
        buffer.append("[source=").append(source);
        if (task != null) {
            buffer.append(", task=\"").append(task).append('"');
        }
        if (!Float.isNaN(progress)) {
            buffer.append(", progress=").append(progress).append('%');
        }
        return buffer.append(']').toString();
    }
}
