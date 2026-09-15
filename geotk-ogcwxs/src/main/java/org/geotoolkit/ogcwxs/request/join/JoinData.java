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
package org.geotoolkit.ogcwxs.request.join;

import org.geotoolkit.ogcwxs.request.RequestParameters;

/**
 * TJS {@code JoinData} request parameters.
 *
 * <p>The {@code AttributeData} XSD choice (GetDataURL or GetDataXML) is exposed as two groups of
 * fields; set only one group. {@code GetDataXML}'s own fields ({@code getDataFrameworkURI},
 * {@code getDataDatasetURI}, {@code getDataAttributes}, {@code getDataLinkageKeys},
 * {@code getDataHost}, {@code getDataLanguage}) are only used when {@code getDataURL} is left
 * unset, and mirror the equivalent {@link GetData} request parameters inline.</p>
 *
 * <blockquote><q>
 * The JoinData operation instructs the server to merge an attribute data table encoded in
 * GDAS format (i.e. obtainable via a GetData request) with its spatial framework.
 * </q><i><small><a href="https://docs.ogc.org/is/10-070r2/10-070r2.pdf">OGC 10-070r2, &sect;15.1</a></small></i>
 * </blockquote>
 * <pre>
 * Service=TJS&amp;
 * Version=1.0&amp;
 * Request=JoinData&amp;
 * Language=en-CA&amp;
 * FrameworkURI=http://foo.bar/foo&amp;
 * GetDataURL=http://foo.bar2/foo&amp;
 * StylingURL=http://foo.bar3/foo&amp;
 * StylingIdentifier=SLD_1.0
 * </pre>
 *
 * @author Quentin Bialota (Geomatys)
 * @see #language(String)
 * @see #getDataURL(String)
 * @see #getDataFrameworkURI(String)
 * @see #getDataDatasetURI(String)
 * @see #getDataAttributes(String)
 * @see #getDataLinkageKeys(String)
 * @see #getDataHost(String)
 * @see #getDataLanguage(String)
 * @see #mapStylingIdentifier(String)
 * @see #mapStylingURL(String)
 * @see #classificationURL(String)
 * @see #update(String)
 */
public final class JoinData extends RequestParameters {

    private String language;
    private String getDataURL;
    private String getDataFrameworkURI;
    private String getDataDatasetURI;
    private String getDataAttributes;
    private String getDataLinkageKeys;
    private String getDataHost;
    private String getDataLanguage;
    private String mapStylingIdentifier;
    private String mapStylingURL;
    private String classificationURL;
    private String update;

    /**
     * <blockquote><q>
     * Language identifier for human readable text.
     * </q><i><small><a href="https://docs.ogc.org/is/10-070r2/10-070r2.pdf">OGC 10-070r2, Table 71</a></small></i>
     * </blockquote>
     *
     * @return the language
     * @see #language(String)
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @param language the language to set
     * @see #language(String)
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * @param language the language to set
     * @see #getLanguage()
     * @see #setLanguage(String)
     */
    public JoinData language(String language) {
        setLanguage(language);
        return this;
    }

    /**
     * <blockquote><q>
     * A URL which returns the attribute data table encoded in GDAS format.
     * </q><i><small><a href="https://docs.ogc.org/is/10-070r2/10-070r2.pdf">OGC 10-070r2, Table 71</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     * <li>May be a TJS GetData request (via HTTP GET), a stored response to a GetData
     * request, or a web process returning content compliant with the GetData response
     * schema.</li>
     * <li>Mutually exclusive with the {@code getDataXxx} fields on this class.</li>
     * </ul>
     *
     * @return the getDataURL
     * @see #getDataURL(String)
     */
    public String getGetDataURL() {
        return getDataURL;
    }

    /**
     * @param getDataURL the getDataURL to set
     * @see #getDataURL(String)
     */
    public void setGetDataURL(String getDataURL) {
        this.getDataURL = getDataURL;
    }

    /**
     * @param getDataURL the getDataURL to set
     * @see #getGetDataURL()
     * @see #setGetDataURL(String)
     */
    public JoinData getDataURL(String getDataURL) {
        setGetDataURL(getDataURL);
        return this;
    }

    /**
     * Inline equivalent of {@link GetData#getFrameworkURI()}, used only when
     * {@link #getGetDataURL()} is left unset.
     *
     * @return the getDataFrameworkURI
     * @see #getDataFrameworkURI(String)
     */
    public String getGetDataFrameworkURI() {
        return getDataFrameworkURI;
    }

    /**
     * @param getDataFrameworkURI the getDataFrameworkURI to set
     * @see #getDataFrameworkURI(String)
     */
    public void setGetDataFrameworkURI(String getDataFrameworkURI) {
        this.getDataFrameworkURI = getDataFrameworkURI;
    }

    /**
     * @param getDataFrameworkURI the getDataFrameworkURI to set
     * @see #getGetDataFrameworkURI()
     * @see #setGetDataFrameworkURI(String)
     */
    public JoinData getDataFrameworkURI(String getDataFrameworkURI) {
        setGetDataFrameworkURI(getDataFrameworkURI);
        return this;
    }

    /**
     * Inline equivalent of {@link GetData#getDatasetURI()}, used only when
     * {@link #getGetDataURL()} is left unset.
     *
     * @return the getDataDatasetURI
     * @see #getDataDatasetURI(String)
     */
    public String getGetDataDatasetURI() {
        return getDataDatasetURI;
    }

    /**
     * @param getDataDatasetURI the getDataDatasetURI to set
     * @see #getDataDatasetURI(String)
     */
    public void setGetDataDatasetURI(String getDataDatasetURI) {
        this.getDataDatasetURI = getDataDatasetURI;
    }

    /**
     * @param getDataDatasetURI the getDataDatasetURI to set
     * @see #getGetDataDatasetURI()
     * @see #setGetDataDatasetURI(String)
     */
    public JoinData getDataDatasetURI(String getDataDatasetURI) {
        setGetDataDatasetURI(getDataDatasetURI);
        return this;
    }

    /**
     * Inline equivalent of {@link GetData#getAttributes()}, used only when
     * {@link #getGetDataURL()} is left unset.
     *
     * @return the getDataAttributes
     * @see #getDataAttributes(String)
     */
    public String getGetDataAttributes() {
        return getDataAttributes;
    }

    /**
     * @param getDataAttributes the getDataAttributes to set
     * @see #getDataAttributes(String)
     */
    public void setGetDataAttributes(String getDataAttributes) {
        this.getDataAttributes = getDataAttributes;
    }

    /**
     * @param getDataAttributes the getDataAttributes to set
     * @see #getGetDataAttributes()
     * @see #setGetDataAttributes(String)
     */
    public JoinData getDataAttributes(String getDataAttributes) {
        setGetDataAttributes(getDataAttributes);
        return this;
    }

    /**
     * Inline equivalent of {@link GetData#getLinkageKeys()}, used only when
     * {@link #getGetDataURL()} is left unset.
     *
     * @return the getDataLinkageKeys
     * @see #getDataLinkageKeys(String)
     */
    public String getGetDataLinkageKeys() {
        return getDataLinkageKeys;
    }

    /**
     * @param getDataLinkageKeys the getDataLinkageKeys to set
     * @see #getDataLinkageKeys(String)
     */
    public void setGetDataLinkageKeys(String getDataLinkageKeys) {
        this.getDataLinkageKeys = getDataLinkageKeys;
    }

    /**
     * @param getDataLinkageKeys the getDataLinkageKeys to set
     * @see #getGetDataLinkageKeys()
     * @see #setGetDataLinkageKeys(String)
     */
    public JoinData getDataLinkageKeys(String getDataLinkageKeys) {
        setGetDataLinkageKeys(getDataLinkageKeys);
        return this;
    }

    /**
     * Host to use when building the inline GetData request, used only when
     * {@link #getGetDataURL()} is left unset.
     *
     * @return the getDataHost
     * @see #getDataHost(String)
     */
    public String getGetDataHost() {
        return getDataHost;
    }

    /**
     * @param getDataHost the getDataHost to set
     * @see #getDataHost(String)
     */
    public void setGetDataHost(String getDataHost) {
        this.getDataHost = getDataHost;
    }

    /**
     * @param getDataHost the getDataHost to set
     * @see #getGetDataHost()
     * @see #setGetDataHost(String)
     */
    public JoinData getDataHost(String getDataHost) {
        setGetDataHost(getDataHost);
        return this;
    }

    /**
     * Inline equivalent of {@link GetData#getLanguage()}, used only when
     * {@link #getGetDataURL()} is left unset.
     *
     * @return the getDataLanguage
     * @see #getDataLanguage(String)
     */
    public String getGetDataLanguage() {
        return getDataLanguage;
    }

    /**
     * @param getDataLanguage the getDataLanguage to set
     * @see #getDataLanguage(String)
     */
    public void setGetDataLanguage(String getDataLanguage) {
        this.getDataLanguage = getDataLanguage;
    }

    /**
     * @param getDataLanguage the getDataLanguage to set
     * @see #getGetDataLanguage()
     * @see #setGetDataLanguage(String)
     */
    public JoinData getDataLanguage(String getDataLanguage) {
        setGetDataLanguage(getDataLanguage);
        return this;
    }

    /**
     * <blockquote><q>
     * Name that identifies the type of styling to be invoked.
     * </q><i><small><a href="https://docs.ogc.org/is/10-070r2/10-070r2.pdf">OGC 10-070r2, Table 71</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     * <li>Value is an Identifier listed in the DescribeJoinAbilities response.</li>
     * <li>If absent, the server assumes that {@link #getMapStylingURL()}, if present, is
     * the default form of styling supported by the server.</li>
     * </ul>
     *
     * @return the mapStylingIdentifier
     * @see #mapStylingIdentifier(String)
     */
    public String getMapStylingIdentifier() {
        return mapStylingIdentifier;
    }

    /**
     * @param mapStylingIdentifier the mapStylingIdentifier to set
     * @see #mapStylingIdentifier(String)
     */
    public void setMapStylingIdentifier(String mapStylingIdentifier) {
        this.mapStylingIdentifier = mapStylingIdentifier;
    }

    /**
     * @param mapStylingIdentifier the mapStylingIdentifier to set
     * @see #getMapStylingIdentifier()
     * @see #setMapStylingIdentifier(String)
     */
    public JoinData mapStylingIdentifier(String mapStylingIdentifier) {
        setMapStylingIdentifier(mapStylingIdentifier);
        return this;
    }

    /**
     * <blockquote><q>
     * URL that contains the styling information to be applied to any WMS output.
     * </q><i><small><a href="https://docs.ogc.org/is/10-070r2/10-070r2.pdf">OGC 10-070r2, Table 71</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     * <li>If absent, the server applies its default styling to any WMS output.</li>
     * </ul>
     *
     * @return the mapStylingURL
     * @see #mapStylingURL(String)
     */
    public String getMapStylingURL() {
        return mapStylingURL;
    }

    /**
     * @param mapStylingURL the mapStylingURL to set
     * @see #mapStylingURL(String)
     */
    public void setMapStylingURL(String mapStylingURL) {
        this.mapStylingURL = mapStylingURL;
    }

    /**
     * @param mapStylingURL the mapStylingURL to set
     * @see #getMapStylingURL()
     * @see #setMapStylingURL(String)
     */
    public JoinData mapStylingURL(String mapStylingURL) {
        setMapStylingURL(mapStylingURL);
        return this;
    }

    /**
     * <blockquote><q>
     * URL that returns a file describing a data classification to be applied to the output
     * (e.g. the classification to be used for a legend in the case where the output is a
     * WMS).
     * </q><i><small><a href="https://docs.ogc.org/is/10-070r2/10-070r2.pdf">OGC 10-070r2, Table 71</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     * <li>If absent, the server applies a default classification as determined by the
     * server.</li>
     * </ul>
     *
     * @return the classificationURL
     * @see #classificationURL(String)
     */
    public String getClassificationURL() {
        return classificationURL;
    }

    /**
     * @param classificationURL the classificationURL to set
     * @see #classificationURL(String)
     */
    public void setClassificationURL(String classificationURL) {
        this.classificationURL = classificationURL;
    }

    /**
     * @param classificationURL the classificationURL to set
     * @see #getClassificationURL()
     * @see #setClassificationURL(String)
     */
    public JoinData classificationURL(String classificationURL) {
        setClassificationURL(classificationURL);
        return this;
    }

    /**
     * <blockquote><q>
     * Identifies if JoinData products should by updated/updatable. Value is "true" or
     * "false". Default is "false".
     * </q><i><small><a href="https://docs.ogc.org/is/10-070r2/10-070r2.pdf">OGC 10-070r2, Table 71</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     * <li>If {@code true}, the server attempts to update an existing set of JoinData
     * products produced earlier by an identical request; URLs of such products do not
     * change across subsequent update requests.</li>
     * </ul>
     *
     * @return the update
     * @see #update(String)
     */
    public String getUpdate() {
        return update;
    }

    /**
     * @param update the update to set
     * @see #update(String)
     */
    public void setUpdate(String update) {
        this.update = update;
    }

    /**
     * @param update the update to set
     * @see #getUpdate()
     * @see #setUpdate(String)
     */
    public JoinData update(String update) {
        setUpdate(update);
        return this;
    }
}
