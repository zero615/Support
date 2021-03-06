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
package com.zero.tools.resource.manifest;


import com.zero.tools.resource.exception.ResourceException;
import com.zero.tools.resource.exception.UndefinedResObjectException;
import com.zero.tools.resource.data.ResPackage;
import com.zero.tools.resource.data.ResResSpec;
import com.zero.tools.resource.data.value.ResAttr;
import com.zero.tools.resource.data.value.ResScalarValue;

public class ResAttrDecoder {
    public String decode(int type, int value, String rawValue, int attrResId)
            throws ResourceException {
        ResScalarValue resValue = mCurrentPackage.getValueFactory().factory(
                type, value, rawValue);

        String decoded = null;
        if (attrResId > 0) {
            try {
                ResAttr attr = (ResAttr) getCurrentPackage().getResTable()
                        .getResSpec(attrResId).getDefaultResource().getValue();

                decoded = attr.convertToResXmlFormat(resValue);
            } catch (UndefinedResObjectException | ClassCastException ex) {
                // ignored
            }
        }

        return decoded != null ? decoded : resValue.encodeAsResXmlAttr();
    }

    public String decodeManifestAttr(int attrResId)
        throws ResourceException {

        if (attrResId != 0) {
            ResResSpec resResSpec = getCurrentPackage().getResTable().getResSpec(attrResId);

            if (resResSpec != null) {
                return resResSpec.getName();
            }
        }

        return null;
    }

    public ResPackage getCurrentPackage() throws ResourceException {
        if (mCurrentPackage == null) {
            throw new ResourceException("Current package not set");
        }
        return mCurrentPackage;
    }

    public void setCurrentPackage(ResPackage currentPackage) {
        mCurrentPackage = currentPackage;
    }

    private ResPackage mCurrentPackage;
}
