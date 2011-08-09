/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2011, Geomatys
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

/**
 * Operation applied to some given data to achieve whatever analyze or model transformation.
 * The interfaces are abstract enough to handle more than GIS data. Anything can be used here
 * for whatever purpose that can be seen as a process.
 * <p>
 * This package can be seen as an integration of ISO 19115 {@link org.opengis.metadata.lineage}
 * package with the {@link java.util.concurrent} package, completed by the ISO 19111
 * {@link org.opengis.parameter} package. The integration is done as below:
 * <p>
 * <ul>
 *   <li>{@link org.geotoolkit.process.ProcessDescriptor} extends the ISO 19115
 *       {@link org.opengis.metadata.lineage.Processing} interface.</li>
 *   <li>{@link org.geotoolkit.process.Process} extends the JDK {@link java.util.concurrent.Callable}
 *       interface, and thus can be given to {@linkplain java.util.concurrent.ExecutorService executor
 *       service} for execution in a background thread.</li>
 *   <li>{@link org.geotoolkit.process.Process} input and output parameters are stored in
 *       ISO 19111 {@link org.opengis.parameter.ParameterValueGroup} objects.</li>
 *   <li>The process outputs are encouraged to contain an ISO 19115
 *       {@link org.opengis.metadata.lineage.ProcessStep} element assigned to the
 *       {@link org.geotoolkit.process.ProcessDescriptor#PROCESS_STEP} parameter.</li>
 * </ul>
 * <p>
 * The following example creates a process for a task named {@code "MyProcess"} (the available
 * process names are factory-dependent), sets a few parameters and launches the process with
 * progress reports sent to the {@linkplain java.io.Console console}:
 *
 * {@preformat java
 *     ExecutorService     exec    = ...; // Some executor from java.util.concurrent
 *     ProcessFactory      factory = ...;
 *     ProcessDescriptor   desc    = factory.getDescriptor("MyProcess");
 *     ParameterValueGroup param   = desc.getInputDescriptor().createValue();
 *     param.parameter("someParam1").setValue(myValue1);
 *     param.parameter("someParam2").setValue(myValue2);
 *     // etc...
 *     Process process = desc.create(param);
 *     process.addListener(new ProgressPrinter());
 *     Future<ParameterValueGroup> task = exec.submit(process);
 *     // Do some other tasks while the process run in background.
 *     ParameterValueGroup output = task.get();
 * }
 *
 * @author Johann Sorel (Geomatys)
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.19
 *
 * @see org.opengis.metadata.lineage.Processing
 * @see java.util.concurrent.ExecutorService
 *
 * @since 3.19
 * @module
 */
package org.geotoolkit.process;
