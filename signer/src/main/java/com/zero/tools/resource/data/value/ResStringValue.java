/*
 *  Copyright (C) 2010 Ryszard Wiśniewski <brut.alll@gmail.com>
 *  Copyright (C) 2010 Connor Tumbleson <connor.tumbleson@gmail.com>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.zero.tools.resource.data.value;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.util.regex.Pattern;

import com.zero.tools.resource.exception.ResourceException;
import com.zero.tools.resource.data.ResResource;
import com.zero.tools.resource.xml.ResXmlEncoders;


public class ResStringValue extends ResScalarValue {

    public ResStringValue(String value, int rawValue) {
        this(value, rawValue, "string");
    }

    public ResStringValue(String value, int rawValue, String type) {
        super(type, rawValue, value);
    }

    @Override
    public String encodeAsResXmlAttr() {
        return checkIfStringIsNumeric(ResXmlEncoders.encodeAsResXmlAttr(mRawValue));
    }

    @Override
    public String encodeAsResXmlItemValue() {
        return ResXmlEncoders.enumerateNonPositionalSubstitutionsIfRequired(ResXmlEncoders.encodeAsXmlValue(mRawValue));
    }

    @Override
    public String encodeAsResXmlValue() {
        return ResXmlEncoders.encodeAsXmlValue(mRawValue);
    }

    @Override
    protected String encodeAsResXml() throws ResourceException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void serializeExtraXmlAttrs(XmlSerializer serializer, ResResource res) throws IOException {
        if (ResXmlEncoders.hasMultipleNonPositionalSubstitutions(mRawValue)) {
            serializer.attribute(null, "formatted", "false");
        }
    }

    private String checkIfStringIsNumeric(String val) {
        if (val == null || val.isEmpty()) {
            return val;
        }
        return allDigits.matcher(val).matches() ? "\\ " + val : val;
    }

    private static final Pattern allDigits = Pattern.compile("\\d{9,}");
}
