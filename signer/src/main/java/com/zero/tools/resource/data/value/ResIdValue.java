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

import com.zero.tools.resource.data.ResResource;
import com.zero.tools.resource.exception.ResourceException;
import com.zero.tools.resource.xml.ResValuesXmlSerializable;

public class ResIdValue extends ResValue implements ResValuesXmlSerializable {
    @Override
    public void serializeToResValuesXml(XmlSerializer serializer,
                                        ResResource res) throws IOException, ResourceException {
        serializer.startTag(null, "item");
        serializer
                .attribute(null, "type", res.getResSpec().getType().getName());
        serializer.attribute(null, "name", res.getResSpec().getName());
        serializer.endTag(null, "item");
    }
}
