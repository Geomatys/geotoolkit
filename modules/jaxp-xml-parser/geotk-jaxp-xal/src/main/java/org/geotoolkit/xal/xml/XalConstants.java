/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
package org.geotoolkit.xal.xml;

/**
 *
 * @author Samuel Andrés
 */
public final class XalConstants {

    // NAMESPACES
    public static final String URI_XAL = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0";
    public static final String URI_XSI = "http://www.w3.org/2001/XMLSchema-instance";
    public static final String PREFIX_XSI = "xsi";

    public static final String TAG_XAL = "xAL";

    // ELEMENTARY TAGS
    public static final String TAG_ADDRESS = "Address";
    public static final String TAG_ADDRESS_DETAILS = "AddressDetails";
    public static final String TAG_ADDRESS_IDENTIFIER = "AddressIdentifier";
    public static final String TAG_ADDRESS_LATITUDE = "AddressLatitude";
    public static final String TAG_ADDRESS_LATITUDE_DIRECTION = "AddressLatitudeDirection";
    public static final String TAG_ADDRESS_LINE = "AddressLine";
    public static final String TAG_ADDRESS_LINES = "AddressLines";
    public static final String TAG_ADDRESS_LONGITUDE = "AddressLongitude";
    public static final String TAG_ADDRESS_LONGITUDE_DIRECTION = "AddressLongitudeDirection";
    public static final String TAG_ADMINISTRATIVE_AREA = "AdministrativeArea";
    public static final String TAG_ADMINISTRATIVE_AREA_NAME = "AdministrativeAreaName";
    public static final String TAG_BARCODE = "Barcode";
    public static final String TAG_BUILDING_NAME = "BuildingName";
    public static final String TAG_COUNTRY = "Country";
    public static final String TAG_COUNTRY_NAME = "CountryName";
    public static final String TAG_COUNTRY_NAME_CODE = "CountryNameCode";
    public static final String TAG_DEPARTMENT = "Department";
    public static final String TAG_DEPARTMENT_NAME = "DepartmentName";
    public static final String TAG_DEPENDENT_LOCALITY = "DependentLocality";
    public static final String TAG_DEPENDENT_LOCALITY_NAME = "DependentLocalityName";
    public static final String TAG_DEPENDENT_LOCALITY_NUMBER = "DependentLocalityNumber";
    public static final String TAG_DEPENDENT_THOROUGHFARE = "DependentThoroughfare";
    public static final String TAG_ENDORSEMENT_LINE_CODE = "EndorsementLineCode";
    public static final String TAG_FIRM = "Firm";
    public static final String TAG_FIRM_NAME = "FirmName";
    public static final String TAG_KEY_LINE_CODE = "KeyLineCode";
    public static final String TAG_LARGE_MAIL_USER = "LargeMailUser";
    public static final String TAG_LARGE_MAIL_USER_IDENTIFIER = "LargeMailUserIdentifier";
    public static final String TAG_LARGE_MAIL_USER_NAME = "LargeMailUserName";
    public static final String TAG_LOCALITY = "Locality";
    public static final String TAG_LOCALITY_NAME = "LocalityName";
    public static final String TAG_MAIL_STOP = "MailStop";
    public static final String TAG_MAIL_STOP_NAME = "MailStopName";
    public static final String TAG_MAIL_STOP_NUMBER = "MailStopNumber";
    public static final String TAG_POSTAL_CODE = "PostalCode";
    public static final String TAG_POSTAL_CODE_NUMBER = "PostalCodeNumber";
    public static final String TAG_POSTAL_CODE_NUMBER_EXTENSION = "PostalCodeNumberExtension";
    public static final String TAG_POSTAL_ROUTE = "PostalRoute";
    public static final String TAG_POSTAL_ROUTE_NAME = "PostalRouteName";
    public static final String TAG_POSTAL_ROUTE_NUMBER = "PostalRouteNumber";
    public static final String TAG_POSTAL_SERVICE_ELEMENTS = "PostalServiceElements";
    public static final String TAG_POST_BOX = "PostBox";
    public static final String TAG_POST_BOX_NUMBER = "PostBoxNumber";
    public static final String TAG_POST_BOX_NUMBER_PREFIX = "PostBoxNumberPrefix";
    public static final String TAG_POST_BOX_NUMBER_SUFFIX = "PostBoxNumberSuffix";
    public static final String TAG_POST_BOX_NUMBER_EXTENSION = "PostBoxNumberExtension";
    public static final String TAG_POST_OFFICE = "PostOffice";
    public static final String TAG_POST_OFFICE_NAME = "PostOfficeName";
    public static final String TAG_POST_OFFICE_NUMBER = "PostOfficeNumber";
    public static final String TAG_POST_TOWN = "PostTown";
    public static final String TAG_POST_TOWN_NAME = "PostTownName";
    public static final String TAG_POST_TOWN_SUFFIX = "PostTownSuffix";
    public static final String TAG_PREMISE = "Premise";
    public static final String TAG_PREMISE_LOCATION = "PremiseLocation";
    public static final String TAG_PREMISE_NAME = "PremiseName";
    public static final String TAG_PREMISE_NUMBER = "PremiseNumber";
    public static final String TAG_PREMISE_NUMBER_PREFIX = "PremiseNumberPrefix";
    public static final String TAG_PREMISE_NUMBER_RANGE = "PremiseNumberRange";
    public static final String TAG_PREMISE_NUMBER_RANGE_FROM = "PremiseNumberRangeFrom";
    public static final String TAG_PREMISE_NUMBER_RANGE_TO = "PremiseNumberRangeTo";
    public static final String TAG_PREMISE_NUMBER_SUFFIX = "PremiseNumberSuffix";
    public static final String TAG_SORTING_CODE = "SortingCode";
    public static final String TAG_SUB_ADMINISTRATIVE_AREA = "SubAdministrativeArea";
    public static final String TAG_SUB_ADMINISTRATIVE_AREA_NAME = "SubAdministrativeAreaName";
    public static final String TAG_SUB_PREMISE = "SubPremise";
    public static final String TAG_SUB_PREMISE_LOCATION = "SubPremiseLocation";
    public static final String TAG_SUB_PREMISE_NAME = "SubPremiseName";
    public static final String TAG_SUB_PREMISE_NUMBER = "SubPremiseNumber";
    public static final String TAG_SUB_PREMISE_NUMBER_PREFIX = "SubPremiseNumberPrefix";
    public static final String TAG_SUB_PREMISE_NUMBER_SUFFIX = "SubPremiseNumberSuffix";
    public static final String TAG_SUPPLEMENTARY_POSTAL_SERVICE_DATA = "SupplementaryPostalServiceData";
    public static final String TAG_THOROUGHFARE = "Thoroughfare";
    public static final String TAG_THOROUGHFARE_LEADING_TYPE = "ThoroughfareLeadingType";
    public static final String TAG_THOROUGHFARE_NAME = "ThoroughfareName";
    public static final String TAG_THOROUGHFARE_NUMBER = "ThoroughfareNumber";
    public static final String TAG_THOROUGHFARE_NUMBER_FROM = "ThoroughfareNumberFrom";
    public static final String TAG_THOROUGHFARE_NUMBER_PREFIX = "ThoroughfareNumberPrefix";
    public static final String TAG_THOROUGHFARE_NUMBER_RANGE = "ThoroughfareNumberRange";
    public static final String TAG_THOROUGHFARE_NUMBER_SUFFIX = "ThoroughfareNumberSuffix";
    public static final String TAG_THOROUGHFARE_NUMBER_TO = "ThoroughfareNumberTo";
    public static final String TAG_THOROUGHFARE_POST_DIRECTION = "ThoroughfarePostDirection";
    public static final String TAG_THOROUGHFARE_PRE_DIRECTION = "ThoroughfarePreDirection";
    public static final String TAG_THOROUGHFARE_TRAILING_TYPE = "ThoroughfareTrailingType";

    // ATTRIBUTES
    public static final String ATT_ADDRESS_DETAILS_KEY = "AddressDetailsKey";
    public static final String ATT_ADDRESS_TYPE = "AddressType";
    public static final String ATT_CODE = "Code";
    public static final String ATT_CONNECTOR = "Connector";
    public static final String ATT_CURRENT_STATUS = "CurrentStatus";
    public static final String ATT_DEPENDENT_THOROUGHFARES = "DependentThoroughfares";
    public static final String ATT_DEPENDENT_THOROUGHFARES_CONNECTOR = "DependentThoroughfaresConnector";
    public static final String ATT_DEPENDENT_THOROUGHFARES_INDICATOR = "DependentThoroughfaresIndicator";
    public static final String ATT_DEPENDENT_THOROUGHFARES_TYPE = "DependentThoroughfaresType";
    public static final String ATT_IDENTIFIER_TYPE = "IdentifierType";
    public static final String ATT_INDICATOR = "Indicator";
    public static final String ATT_INDICATOR_OCCURRENCE = "IndicatorOccurrence";
    public static final String ATT_NAME_NUMBER_OCCURRENCE = "NameNumberOccurrence";
    public static final String ATT_NAME_NUMBER_SEPARATOR = "NameNumberSeparator";
    public static final String ATT_NUMBER_EXTENSION_SEPARATOR = "NumberExtensionSeparator";
    public static final String ATT_NUMBER_OCCURRENCE = "NumberOccurrence";
    public static final String ATT_NUMBER_PREFIX_SEPARATOR = "NumberPrefixSeparator";
    public static final String ATT_NUMBER_RANGE_OCCURRENCE = "NumberRangeOccurrence";
    public static final String ATT_NUMBER_SUFFIX_SEPARATOR = "NumberSuffixSeparator";
    public static final String ATT_NUMBER_TYPE = "NumberType";
    public static final String ATT_NUMBER_TYPE_OCCURRENCE = "NumberTypeOccurrence";
    public static final String ATT_PREMISE_DEPENDENCY = "PremiseDependency";
    public static final String ATT_PREMISE_DEPENDENCY_TYPE = "PremiseDependencyType";
    public static final String ATT_PREMISE_NUMBER_SEPARATOR = "PremiseNumberSeparator";
    public static final String ATT_PREMISE_THOROUGHFARE_CONNECTOR = "PremiseThoroughfareConnector";
    public static final String ATT_RANGE_TYPE = "RangeType";
    public static final String ATT_SCHEME = "Scheme";
    public static final String ATT_SEPARATOR = "Separator";
    public static final String ATT_TYPE = "Type";
    public static final String ATT_TYPE_OCCURRENCE = "TypeOccurrence";
    public static final String ATT_USAGE = "Usage";
    public static final String ATT_USAGE_TYPE = "UsageType";
    public static final String ATT_VALID_FROM_DATE = "ValidFromDate";
    public static final String ATT_VALID_TO_DATE = "ValidToDate";
    public static final String ATT_VERSION = "Version";

    private XalConstants(){}
}
