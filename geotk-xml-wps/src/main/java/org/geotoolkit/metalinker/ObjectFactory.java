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

package org.geotoolkit.metalinker;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the org.metalinker package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 *
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Metalink_QNAME = new QName("http://www.metalinker.org/", "metalink");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.metalinker
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link FileType }
     *
     */
    public FileType createFileType() {
        return new FileType();
    }

    /**
     * Create an instance of {@link FileType.Verification }
     *
     */
    public FileType.Verification createFileTypeVerification() {
        return new FileType.Verification();
    }

    /**
     * Create an instance of {@link FileType.Verification.Pieces }
     *
     */
    public FileType.Verification.Pieces createFileTypeVerificationPieces() {
        return new FileType.Verification.Pieces();
    }

    /**
     * Create an instance of {@link ResourcesType }
     *
     */
    public ResourcesType createResourcesType() {
        return new ResourcesType();
    }

    /**
     * Create an instance of {@link MetalinkType }
     *
     */
    public MetalinkType createMetalinkType() {
        return new MetalinkType();
    }

    /**
     * Create an instance of {@link LicenseType }
     *
     */
    public LicenseType createLicenseType() {
        return new LicenseType();
    }

    /**
     * Create an instance of {@link PublisherType }
     *
     */
    public PublisherType createPublisherType() {
        return new PublisherType();
    }

    /**
     * Create an instance of {@link MultimediaType }
     *
     */
    public MultimediaType createMultimediaType() {
        return new MultimediaType();
    }

    /**
     * Create an instance of {@link VideoType }
     *
     */
    public VideoType createVideoType() {
        return new VideoType();
    }

    /**
     * Create an instance of {@link FilesType }
     *
     */
    public FilesType createFilesType() {
        return new FilesType();
    }

    /**
     * Create an instance of {@link AudioType }
     *
     */
    public AudioType createAudioType() {
        return new AudioType();
    }

    /**
     * Create an instance of {@link FileType.Verification.Hash }
     *
     */
    public FileType.Verification.Hash createFileTypeVerificationHash() {
        return new FileType.Verification.Hash();
    }

    /**
     * Create an instance of {@link FileType.Verification.Signature }
     *
     */
    public FileType.Verification.Signature createFileTypeVerificationSignature() {
        return new FileType.Verification.Signature();
    }

    /**
     * Create an instance of {@link FileType.Verification.Pieces.Hash }
     *
     */
    public FileType.Verification.Pieces.Hash createFileTypeVerificationPiecesHash() {
        return new FileType.Verification.Pieces.Hash();
    }

    /**
     * Create an instance of {@link ResourcesType.Url }
     *
     */
    public ResourcesType.Url createResourcesTypeUrl() {
        return new ResourcesType.Url();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MetalinkType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.metalinker.org/", name = "metalink")
    public JAXBElement<MetalinkType> createMetalink(MetalinkType value) {
        return new JAXBElement<MetalinkType>(_Metalink_QNAME, MetalinkType.class, null, value);
    }

}
