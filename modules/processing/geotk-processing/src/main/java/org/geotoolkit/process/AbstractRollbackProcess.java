/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

import org.opengis.parameter.ParameterValueGroup;

/**
 * Abstract class for processes that can rollback their processed result.
 * For example a process that only create a File can delete it to restore system
 * in this original state.
 * Constructor {@link #AbstractRollbackProcess(ProcessDescriptor, org.opengis.parameter.ParameterValueGroup, boolean)}
 * add a {@code rollbackOnFail} parameter that indicate if the process must self rollback if something went wrong
 * during {@link #execute()}. By default {@code rollbackOnFail} is {@code false}.
 *
 * @author Quentin Boileau (Geomatys)
 * @version 4.0
 *
 * @since 4.0
 */
public abstract class AbstractRollbackProcess extends AbstractProcess {

    /**
     * Flag for allow auto rollback if process execution fail.
     */
    protected boolean rollbackOnFail;

    /**
     * Internal flag to prevent multiple calls on {@link #rollback()}.
     */
    protected boolean rollbackDone = false;

    /**
     * {@inheritDoc}
     */
    public AbstractRollbackProcess(ProcessDescriptor desc, ParameterValueGroup input) {
        super(desc, input);
        this.rollbackOnFail = false;
    }

    /**
     * {@inheritDoc}
     * @param rollbackOnFail flag indicate if the process must self rollback if something went wrong
     * during {@link #execute()}
     */
    public AbstractRollbackProcess(ProcessDescriptor desc, ParameterValueGroup input, boolean rollbackOnFail) {
        super(desc, input);
        this.rollbackOnFail = rollbackOnFail;
    }

    /**
     * {@inheritDoc}
     * If {@code rollbackOnFail} is {@code true} and process fail, {@link #rollback()} is called.
     * And if {@link #rollback()} also failed, rollback exception will be logged.
     */
    @Override
    public ParameterValueGroup call() throws ProcessException {
        try {
            return super.call();
        } catch (ProcessException ex) {
            if (rollbackOnFail) {
                try {
                    rollback();
                } catch (ProcessException rbEx) {
                    ex.addSuppressed(rbEx);
                }
            }
            throw ex;
        }
    }

    /**
     * Rollback {@link #execute()} call.
     *
     * @throws ProcessException if something when wrong during rollback.
     */
    public void rollback() throws ProcessException {
        if (!rollbackDone) {
            executeRollback();
            rollbackDone = true;
        }
    }

    /**
     * Implementation of rollback action.
     * Implementation must also handle partial rollback
     * if something when wrong in the middle of {@link #execute()}.
     * For example a process that create two files must not fail on rollback because
     * the last file was not created because of the {@link #execute()} exception.
     * @throws ProcessException
     */
    protected abstract void executeRollback() throws ProcessException;

}
