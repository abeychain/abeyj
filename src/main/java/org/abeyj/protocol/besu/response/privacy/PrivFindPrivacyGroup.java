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
package org.abeyj.protocol.besu.response.privacy;

import org.abeyj.protocol.core.Response;

import java.util.List;

public class PrivFindPrivacyGroup extends Response<List<PrivacyGroup>> {
    public List<PrivacyGroup> getGroups() {
        return getResult();
    }
}
