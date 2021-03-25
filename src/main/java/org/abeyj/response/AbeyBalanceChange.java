/*
 * Copyright 2019 Web3 Labs LTD.
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
package org.abeyj.response;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.abeyj.response.snail.BalanceChange;
import org.abeyj.protocol.ObjectMapperFactory;
import org.abeyj.protocol.core.Response;

import java.io.IOException;


public class AbeyBalanceChange extends Response<BalanceChange> {

    @Override
    @JsonDeserialize(using = AbeyBalanceChange.ResponseDeserialiser.class)
    public void setResult(BalanceChange balanceChange) {
        super.setResult(balanceChange);
    }

    public BalanceChange getBalanceChange() {
        return getResult();
    }

    public String getMessage() {
        return super.getError().getMessage();
    }

    public static class ResponseDeserialiser extends JsonDeserializer<BalanceChange> {
        private ObjectReader objectReader = ObjectMapperFactory.getObjectReader();

        @Override
        public BalanceChange deserialize(
                JsonParser jsonParser, DeserializationContext deserializationContext)
                throws IOException {
            if (jsonParser.getCurrentToken() != JsonToken.VALUE_NULL) {
                return objectReader.readValue(jsonParser, BalanceChange.class);
            } else {
                return null; // null is wrapped by Optional in above getter
            }
        }
    }
}
