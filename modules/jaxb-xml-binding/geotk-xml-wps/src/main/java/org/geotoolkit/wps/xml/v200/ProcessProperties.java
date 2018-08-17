package org.geotoolkit.wps.xml.v200;

import java.util.List;

/**
 * Process execution options, as described in
 * <a href="http://schemas.opengis.net/wps/2.0/wpsDescribeProcess.xsd">WPS 2.0
 * process description model.</a>
 *
 * @author Alexis Manin (Geomatys)
 */
public interface ProcessProperties {

    /**
     * Defines the valid execution modes for a particular process offering.
     *
     * @return Rules valid for the current process
     */
    List<JobControlOptions> getJobControlOptions();

    /**
     * Indicates whether data outputs from this process can be stored by the WPS
     * server as web-accessible resources.
     *
     * @return Rules supported by this process.
     */
    List<DataTransmissionMode> getOutputTransmission();

    /**
     * The process version is an informative element in a process offering. It
     * is not intended for version negotiation but can rather be used to
     * communicate updated or changed process implementations on a particular
     * service instance.
     *
     * @return Version of the current process.
     */
    String getProcessVersion();

    /**
     * Type of the process model. Include when using a different process model
     * than the native process model. This is an extension hook to support
     * processes that have been specified in other OGC Standards, such as
     * SensorML. For those process models, compliance with the abstract process
     * model has to be ensured compatibility with the WPS protocol.
     *
     * @return native to indicate that process is of server native type. Other
     * values not defined yet.
     */
    String getProcessModel();
}
