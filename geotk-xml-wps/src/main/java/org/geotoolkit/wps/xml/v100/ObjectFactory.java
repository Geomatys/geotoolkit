/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.wps.xml.v100;

import jakarta.xml.bind.annotation.XmlRegistry;
import org.geotoolkit.wps.xml.v200.Reference;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the net.opengis.wps._1_0 package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 *
 * @module
 */
@XmlRegistry
public class ObjectFactory {

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.opengis.wps._1_0
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ProcessStarted }
     *
     */
    public ProcessStarted createProcessStartedType() {
        return new ProcessStarted();
    }

    /**
     * Create an instance of {@link SupportedUOMs }
     *
     */
    public SupportedUOMs createSupportedUOMsType() {
        return new SupportedUOMs();
    }

    /**
     * Create an instance of {@link WSDL }
     *
     */
    public WSDL createWSDL() {
        return new WSDL();
    }

    /**
     * Create an instance of {@link UOMs }
     *
     */
    public UOMs createUOMsType() {
        return new UOMs();
    }

    /**
     * Create an instance of {@link LegacyLanguages }
     *
     */
    public LegacyLanguages createLanguages() {
        return new LegacyLanguages();
    }

    /**
     * Create an instance of {@link SupportedUOMsType.Default }
     *
     */
    public SupportedUOMs.Default createSupportedUOMsTypeDefault() {
        return new SupportedUOMs.Default();
    }

    /**
     * Create an instance of {@link InputReferenceType.Header }
     *
     */
    public Reference.Header createInputReferenceTypeHeader() {
        return new Reference.Header();
    }

    /**
     * Create an instance of {@link ProcessFailed }
     *
     */
    public ProcessFailed createProcessFailedType() {
        return new ProcessFailed();
    }

    /**
     * Create an instance of {@link LegacyLanguage }
     *
     */
    public LegacyLanguage createLanguagesType() {
        return new LegacyLanguage();
    }

    /**
     * Create an instance of {@link Languages.Default }
     *
     */
    public LegacyLanguages.Default createLanguagesDefault() {
        return new LegacyLanguages.Default();
    }
}
