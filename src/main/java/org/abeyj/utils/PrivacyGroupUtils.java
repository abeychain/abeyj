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
package org.abeyj.utils;

import org.abeyj.crypto.Hash;
import org.abeyj.rlp.RlpEncoder;
import org.abeyj.rlp.RlpList;
import org.abeyj.rlp.RlpString;
import org.abeyj.rlp.RlpType;

import java.util.*;
import java.util.stream.Collectors;

public class PrivacyGroupUtils {
    public static Base64String generateLegacyGroup(
            final Base64String privateFrom, final List<Base64String> privateFor) {
        final List<byte[]> stringList = new ArrayList<>();
        stringList.add(Base64.getDecoder().decode(privateFrom.toString()));
        privateFor.forEach(item -> stringList.add(item.raw()));

        final List<RlpType> rlpList =
                stringList.stream()
                        .distinct()
                        .sorted(Comparator.comparing(Arrays::hashCode))
                        .map(RlpString::create)
                        .collect(Collectors.toList());

        return Base64String.wrap(Hash.sha3(RlpEncoder.encode(new RlpList(rlpList))));
    }
}
