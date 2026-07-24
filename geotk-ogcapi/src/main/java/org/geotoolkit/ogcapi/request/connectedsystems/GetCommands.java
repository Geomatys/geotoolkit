/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.ogcapi.request.connectedsystems;

import java.util.List;
import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 * The {@code GET} operation on {@code /commands}: lists Command resources.
 *
 * @see <a href="https://docs.ogc.org/is/23-002/23-002.html">OGC API - Connected Systems - Part 2</a>
 * @author Johann Sorel (Geomatys)
 */
public final class GetCommands extends RequestParameters {

    private List<String> id;
    private List<String> q;
    private String issueTime;
    private String executionTime;
    private List<String> statusCode;
    private List<String> sender;
    private List<String> system;
    private List<String> foi;
    private List<String> controlledProperty;
    private Integer limit;

    /**
     * Only resources with one of the given identifiers are selected. (optional)
     *
     * @return the id
     */
    public List<String> getId() {
        return id;
    }

    /**
     * @param id the id to set
     * @see #getId()
     */
    public void setId(List<String> id) {
        this.id = id;
    }

    /**
     * @param id the id to set
     * @see #getId()
     */
    public GetCommands id(List<String> id) {
        setId(id);
        return this;
    }

    /**
     * Free-text search on the resource's textual properties. (optional)
     *
     * @return the q
     */
    public List<String> getQ() {
        return q;
    }

    /**
     * @param q the q to set
     * @see #getQ()
     */
    public void setQ(List<String> q) {
        this.q = q;
    }

    /**
     * @param q the q to set
     * @see #getQ()
     */
    public GetCommands q(List<String> q) {
        setQ(q);
        return this;
    }

    /**
     * A date-time or a period, per RFC 3339, filtering on the command issue time. (optional)
     *
     * @return the issueTime
     */
    public String getIssueTime() {
        return issueTime;
    }

    /**
     * @param issueTime the issueTime to set
     * @see #getIssueTime()
     */
    public void setIssueTime(String issueTime) {
        this.issueTime = issueTime;
    }

    /**
     * @param issueTime the issueTime to set
     * @see #getIssueTime()
     */
    public GetCommands issueTime(String issueTime) {
        setIssueTime(issueTime);
        return this;
    }

    /**
     * A date-time or a period, per RFC 3339, filtering on the command execution time. (optional)
     *
     * @return the executionTime
     */
    public String getExecutionTime() {
        return executionTime;
    }

    /**
     * @param executionTime the executionTime to set
     * @see #getExecutionTime()
     */
    public void setExecutionTime(String executionTime) {
        this.executionTime = executionTime;
    }

    /**
     * @param executionTime the executionTime to set
     * @see #getExecutionTime()
     */
    public GetCommands executionTime(String executionTime) {
        setExecutionTime(executionTime);
        return this;
    }

    /**
     * Only commands or control streams with one of the given status codes are selected. (optional)
     *
     * @return the statusCode
     */
    public List<String> getStatusCode() {
        return statusCode;
    }

    /**
     * @param statusCode the statusCode to set
     * @see #getStatusCode()
     */
    public void setStatusCode(List<String> statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * @param statusCode the statusCode to set
     * @see #getStatusCode()
     */
    public GetCommands statusCode(List<String> statusCode) {
        setStatusCode(statusCode);
        return this;
    }

    /**
     * Only resources sent by one of the given senders are selected. (optional)
     *
     * @return the sender
     */
    public List<String> getSender() {
        return sender;
    }

    /**
     * @param sender the sender to set
     * @see #getSender()
     */
    public void setSender(List<String> sender) {
        this.sender = sender;
    }

    /**
     * @param sender the sender to set
     * @see #getSender()
     */
    public GetCommands sender(List<String> sender) {
        setSender(sender);
        return this;
    }

    /**
     * Only resources associated with one of the given system identifiers are selected. (optional)
     *
     * @return the system
     */
    public List<String> getSystem() {
        return system;
    }

    /**
     * @param system the system to set
     * @see #getSystem()
     */
    public void setSystem(List<String> system) {
        this.system = system;
    }

    /**
     * @param system the system to set
     * @see #getSystem()
     */
    public GetCommands system(List<String> system) {
        setSystem(system);
        return this;
    }

    /**
     * Only resources associated with one of the given features of interest are selected. (optional)
     *
     * @return the foi
     */
    public List<String> getFoi() {
        return foi;
    }

    /**
     * @param foi the foi to set
     * @see #getFoi()
     */
    public void setFoi(List<String> foi) {
        this.foi = foi;
    }

    /**
     * @param foi the foi to set
     * @see #getFoi()
     */
    public GetCommands foi(List<String> foi) {
        setFoi(foi);
        return this;
    }

    /**
     * Only resources that control one of the given properties are selected. (optional)
     *
     * @return the controlledProperty
     */
    public List<String> getControlledProperty() {
        return controlledProperty;
    }

    /**
     * @param controlledProperty the controlledProperty to set
     * @see #getControlledProperty()
     */
    public void setControlledProperty(List<String> controlledProperty) {
        this.controlledProperty = controlledProperty;
    }

    /**
     * @param controlledProperty the controlledProperty to set
     * @see #getControlledProperty()
     */
    public GetCommands controlledProperty(List<String> controlledProperty) {
        setControlledProperty(controlledProperty);
        return this;
    }

    /**
     * The optional limit parameter limits the number of items presented in the response. (optional)
     *
     * @return the limit
     */
    public Integer getLimit() {
        return limit;
    }

    /**
     * @param limit the limit to set
     * @see #getLimit()
     */
    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    /**
     * @param limit the limit to set
     * @see #getLimit()
     */
    public GetCommands limit(Integer limit) {
        setLimit(limit);
        return this;
    }

}
