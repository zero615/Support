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
import java.util.logging.Logger;

import com.zero.tools.resource.util.Duo;
import com.zero.tools.resource.exception.ResourceException;
import com.zero.tools.resource.data.ResResSpec;
import com.zero.tools.resource.data.ResResource;
import com.zero.tools.resource.xml.ResValuesXmlSerializable;


public class ResStyleValue extends ResBagValue implements
        ResValuesXmlSerializable {
    ResStyleValue(ResReferenceValue parent,
                  Duo<Integer, ResScalarValue>[] items, ResValueFactory factory) {
        super(parent);

        mItems = new Duo[items.length];
        for (int i = 0; i < items.length; i++) {
            mItems[i] = new Duo<ResReferenceValue, ResScalarValue>(
                    factory.newReference(items[i].m1, null), items[i].m2);
        }
    }

    @Override
    public void serializeToResValuesXml(XmlSerializer serializer,
                                        ResResource res) throws IOException, ResourceException {
        serializer.startTag(null, "style");
        serializer.attribute(null, "name", res.getResSpec().getName());
        if (!mParent.isNull() && !mParent.referentIsNull()) {
            serializer.attribute(null, "parent", mParent.encodeAsResXmlAttr());
        } else if (res.getResSpec().getName().indexOf('.') != -1) {
            serializer.attribute(null, "parent", "");
        }
        for (int i = 0; i < mItems.length; i++) {
            ResResSpec spec = mItems[i].m1.getReferent();

            if (spec == null) {
                LOGGER.fine(String.format("null reference: m1=0x%08x(%s), m2=0x%08x(%s)",
                        mItems[i].m1.getRawIntValue(), mItems[i].m1.getType(), mItems[i].m2.getRawIntValue(), mItems[i].m2.getType()));
                continue;
            }

            String name = null;
            String value = null;

            ResValue resource = spec.getDefaultResource().getValue();
            if (resource instanceof ResReferenceValue) {
                continue;
            } else if (resource instanceof ResAttr) {
                ResAttr attr = (ResAttr) resource;
                value = attr.convertToResXmlFormat(mItems[i].m2);
                name = spec.getFullName(res.getResSpec().getPackage(), true);
            } else {
                name = "@" + spec.getFullName(res.getResSpec().getPackage(), false);
            }

            if (value == null) {
                value = mItems[i].m2.encodeAsResXmlValue();
            }

            if (value == null) {
                continue;
            }

            serializer.startTag(null, "item");
            serializer.attribute(null, "name", name);
            serializer.text(value);
            serializer.endTag(null, "item");
        }
        serializer.endTag(null, "style");
    }

    private final Duo<ResReferenceValue, ResScalarValue>[] mItems;

    private static final Logger LOGGER = Logger.getLogger(ResStyleValue.class.getName());
}
