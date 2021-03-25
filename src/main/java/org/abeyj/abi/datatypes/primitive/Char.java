/*
 * Copyright 2019 Web3 Labs Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.abeyj.abi.datatypes.primitive;

import org.abeyj.abi.datatypes.Type;
import org.abeyj.abi.datatypes.Utf8String;

public final class Char extends PrimitiveType<Character> {

    public Char(char value) {
        super(value);
    }

    @Override
    public Type toSolidityType() {
        return new Utf8String(String.valueOf(getValue()));
    }
}
