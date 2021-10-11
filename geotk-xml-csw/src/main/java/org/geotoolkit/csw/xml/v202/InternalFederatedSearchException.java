/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.csw.xml.v202;

import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.csw.xml.FederatedSearchException;
import org.geotoolkit.ows.xml.v100.ExceptionReport;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class InternalFederatedSearchException implements FederatedSearchException {

    protected List<ExceptionReport> exceptionReport;

    public InternalFederatedSearchException() {

    }

    public InternalFederatedSearchException(List<ExceptionReport> exceptionReport) {
        this.exceptionReport = exceptionReport;
    }

    public InternalFederatedSearchException(ExceptionReport exceptionReport) {
        if (exceptionReport != null) {
            this.exceptionReport = new ArrayList<>();
            this.exceptionReport.add(exceptionReport);
        }
    }

    public List<ExceptionReport> getExceptionReport() {
        if (exceptionReport == null) {
            exceptionReport = new ArrayList<>();
        }
        return this.exceptionReport;
    }

    @Override
    public int getMatched() {
        return 0;
    }

    @Override
    public int getReturned() {
        return 0;
    }
}
