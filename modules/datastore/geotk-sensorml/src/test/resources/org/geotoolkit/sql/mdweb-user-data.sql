
INSERT INTO "Storage"."Catalogs" ("code", "name") VALUES ('MDATA', 'MDWEB data');

INSERT INTO "Users"."Users" ("login", "password", "form", "creationDate", "expirationDate") VALUES ('admin', 'adminadmin', NULL, '2008-01-01', '2010-01-01');
INSERT INTO "Storage"."Forms" ("identifier", "catalog", "title", "inputLogin", "validationLogin", "profile", "updateDate", "isValidated", "isPublished", "type") VALUES (1, 'MDATA', 'description of admin', 'admin', NULL, NULL, '2008-04-16', 1, 1, 'templateForm');
UPDATE "Users"."Users" SET "form"=1;

INSERT INTO "Storage"."Values" ("form", "path", "ordinal", "type", "typeStandard", "id_value") VALUES (1, 'ISO 19115:CI_ResponsibleParty', 1, 'CI_ResponsibleParty', 'ISO 19115', 'ISO 19115:CI_ResponsibleParty.1');
INSERT INTO "Storage"."Values" ("form", "path", "ordinal", "type", "typeStandard", "id_value") VALUES (1, 'ISO 19115:CI_ResponsibleParty:contactInfo', 1, 'CI_Contact', 'ISO 19115', 'ISO 19115:CI_ResponsibleParty.1:contactInfo.1');
INSERT INTO "Storage"."Values" ("form", "path", "ordinal", "type", "typeStandard", "id_value") VALUES (1, 'ISO 19115:CI_ResponsibleParty:contactInfo:phone', 1, 'CI_Telephone', 'ISO 19115', 'ISO 19115:CI_ResponsibleParty.1:contactInfo.1:phone.1');
INSERT INTO "Storage"."Values" ("form", "path", "ordinal", "type", "typeStandard", "id_value") VALUES (1, 'ISO 19115:CI_ResponsibleParty:contactInfo:address', 1, 'CI_Address', 'ISO 19115', 'ISO 19115:CI_ResponsibleParty.1:contactInfo.1:address.1');
INSERT INTO "Storage"."Values" ("form", "path", "ordinal", "type", "typeStandard", "id_value") VALUES (1, 'ISO 19115:CI_ResponsibleParty:contactInfo:onlineResource', 1, 'CI_OnlineResource', 'ISO 19115', 'ISO 19115:CI_ResponsibleParty.1:contactInfo.1:onlineResource.1');

INSERT INTO "Storage"."TextValues" ("form", "path", "ordinal", "type", "typeStandard", "id_value", "value") VALUES (1, 'ISO 19115:CI_ResponsibleParty:individualName', 1, NULL, NULL, 'ISO 19115:CI_ResponsibleParty.1:individualName.1', 'Barde-Julien');
INSERT INTO "Storage"."TextValues" ("form", "path", "ordinal", "type", "typeStandard", "id_value", "value") VALUES (1, 'ISO 19115:CI_ResponsibleParty:contactInfo:address:electronicMailAddress', 1, NULL, NULL, 'ISO 19115:CI_ResponsibleParty.1:contactInfo.1:address.1:electronicMailAddress.1', 'juldebar@gmail.com');
INSERT INTO "Storage"."TextValues" ("form", "path", "ordinal", "type", "typeStandard", "id_value", "value") VALUES (1, 'ISO 19115:CI_ResponsibleParty:contactInfo:address:country', 1, NULL, NULL, 'ISO 19115:CI_ResponsibleParty.1:contactInfo.1:address.1:country.1', 'FR');
