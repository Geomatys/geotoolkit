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


/**
 * Extend {@link org.geotoolkit.process.Process} interface to add a rollback method used
 * to revert process {@link org.geotoolkit.process.Process#call()} actions.
 *
 * @author Quentin Boileau (Geomatys)
 * @version 4.0
 *
 * @see org.geotoolkit.process.Process
 * @since 4.0
 */
public interface RollbackableProcess extends Process {

    /**
     * Rollback {@link org.geotoolkit.process.Process#call()}.
     * Implementers must also handle partial rollback
     * if something when wrong in the middle of {@link org.geotoolkit.process.Process#call()}.
     *
     * For example a process that create two files must not fail on rollback because
     * the last file was not created because of the {@link org.geotoolkit.process.Process#call()} exception.
     *
     * @throws ProcessException if something when wrong during rollback.
     */
    void rollback() throws ProcessException;
}
