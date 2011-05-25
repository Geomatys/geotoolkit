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
package org.geotoolkit.wps;

import java.util.List;
import org.geotoolkit.client.Request;

/**
 * WPS Execute mutable request interface.
 * 
 * @author Quentin Boileau
 * @module pending
 */
public interface ExecuteRequest extends Request {
    
    
    /**
     * Returns process identifier, never {@code null}.
     */
    String getIdentifier();

    /**
     * Sets process identifiers to use. Must be called.
     */
    void setIdentifier(String identifiers);
    
    
    /**
     * Returns OutputForm "document" or "raw", can be {@code null}.
     */
    String getOutputForm();

    /**
     * Sets OutputForm to use.
     */
    void setOutputForm(String outForm);
    
    /**
     * Returns OutputStorage state, can be {@code null}.
     */
    boolean getOutputStorage();

    /**
     * Sets OutputStorage state.
     */
    void setOutputStorage(boolean outStrorage);
    
    /**
     * Returns OutputLineage state, can be {@code null}.
     */
    boolean getOutputLineage();

    /**
     * Sets OutputLineage state.
     */
    void setOutputLineage(boolean outLineage);
    
    /**
     * Returns OutputStatus state, can be {@code null}.
     */
    boolean getOutputStatus();

    /**
     * Sets OutputStatus state.
     */
    void setOutputStatus(boolean outStatus);
    
    /**
     * Returns Outputs wanted from a process, can be {@code null}.
     */
    List<WPSOutput> getOutputs();

    /**
     * Sets Outputs wanted from a process.
     */
    void setOutputs(List<WPSOutput> outForm);
    
    
    /**
     * Returns Inputs, can be {@code null}.
     */
    List<AbstractWPSInput> getInputs();

    /**
     * Sets Input to a process.
     */
    void setInputs(List<AbstractWPSInput> inputs);
    
}
    