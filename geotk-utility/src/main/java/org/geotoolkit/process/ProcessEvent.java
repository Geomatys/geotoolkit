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
import org.opengis.parameter.ParameterValueGroup;
import org.apache.sis.util.iso.Types;


/**
 * Event sent by a running {@linkplain Process process} to its {@linkplain ProcessListener listeners}.
 * This event contains an optional user-friendly {@linkplain #getTask() task description} together
 * with the {@linkplain #getProgress() task progression} as a percentage. In addition the event can
 * contains one of the following:
 * <p>
 * <ul>
 *   <li>A {@link ParameterValueGroup} which contain either the final calculation result when
 *       the process {@linkplain ProcessListener#completed completed}, or intermediate values
 *       while the process is still {@linkplain ProcessListener#progressing progressing}.</li>
 *   <li>An {@link Exception} which describe either the fatal error why the process
 *       {@linkplain ProcessListener#failed failed}, or non-fatal warnings while the process
 *       is still {@linkplain ProcessListener#progressing progressing}.</li>
 * </ul>
 *
 * @author Johann Sorel (Geomatys)
 * @version 3.20
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
     * An intermediate calculation or the final result to be returned by {@link #getOutput()},
     * or {@code null} if none.
     *
     * @since 3.20
     */
    private final ParameterValueGroup output;

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
    public ProcessEvent(final Process source) {
        super(source);
        this.progress  = Float.NaN;
        this.task      = null;
        this.exception = null;
        this.output    = null;
    }

    /**
     * Creates a new event with the given source, task and progress.
     *
     * @param source    The source of this event.
     * @param task      A message that describe the task under execution, or {@code null} if none.
     * @param progress  The progress as a number between 0 and 100, or {@link Float#NaN} if undetermined.
     */
    public ProcessEvent(final Process source, final CharSequence task, final float progress) {
        super(source);
        this.progress  = progress;
        this.task      = Types.toInternationalString(task);
        this.exception = null;
        this.output    = null;
    }

    /**
     * Creates a new event with the given source, task, progress and output.
     * The output may be an intermediate calculation, or the final result.
     *
     * @param source    The source of this event.
     * @param task      A message that describe the task under execution, or {@code null} if none.
     * @param progress  The progress as a number between 0 and 100, or {@link Float#NaN} if undetermined.
     * @param output    The output (intermediate calculation of final result), or {@code null} if none.
     *
     * @since 3.20
     */
    public ProcessEvent(final Process source, final CharSequence task, final float progress, final ParameterValueGroup output) {
        super(source);
        this.progress  = progress;
        this.task      = Types.toInternationalString(task);
        this.exception = null;
        this.output    = output;
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
        this.progress  = progress;
        this.task      = Types.toInternationalString(task);
        this.exception = exception;
        this.output    = null;
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
     * Returns the output, which may be an intermediate calculation or the final result.
     * More specifically:
     * <p>
     * <ul>
     *   <li>If the event has been given to {@link ProcessListener#progressing(ProcessEvent)}
     *       then the output (if non-null) is an intermediate calculation.</li>
     *   <li>If the event has been given to {@link ProcessListener#completed(ProcessEvent)}
     *       then the output is the same final result than the one returned by
     *       {@link Process#call()}.</li>
     * </ul>
     *
     * @return The intermediate calculation (when {@linkplain ProcessListener#progressing progressing})
     *         or the final result (when {@linkplain ProcessListener#completed completed}),
     *         or {@code null} if none.
     *
     * @since 3.20
     */
    public ParameterValueGroup getOutput() {
        return output;
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
