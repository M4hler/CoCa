package com.cthulhu.services;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;

@Service
public class XmlValidationService {
    public boolean validate(String xsdFile, String xmlFile) throws SAXException {
        var factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        var schemaFile = new StreamSource(new ClassPathResource(xsdFile).getPath());
        var schema = factory.newSchema(schemaFile);
        var validator = schema.newValidator();

        try {
            var source = new StreamSource(new ClassPathResource(xmlFile).getPath());
            validator.validate(source);
            return true;
        }
        catch(SAXException | IOException e) {
            return false;
        }
    }
}
