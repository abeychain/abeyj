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
package org.abeyj.response.committee;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectReader;
import org.abeyj.protocol.ObjectMapperFactory;

import java.io.IOException;
import java.util.List;


public class CommitteeInfo {
    private String beginSnailNumber;
    private String endSnailNumber;
    private String memberCount;
    private String beginNumber;
    private String endNumber;

    private List<CommitteeMember> members;


    public CommitteeInfo() {
    }


    public CommitteeInfo(String beginSnailNumber, String endSnailNumber, String memberCount, String beginNumber, String endNumber) {
        this.beginSnailNumber = beginSnailNumber;
        this.endSnailNumber = endSnailNumber;
        this.memberCount = memberCount;
        this.beginNumber = beginNumber;
        this.endNumber = endNumber;
    }


    @Override
    public String toString() {
        return "CommitteeInfo{" +
                "beginSnailNumber='" + beginSnailNumber + '\'' +
                ", endSnailNumber='" + endSnailNumber + '\'' +
                ", memberCount='" + memberCount + '\'' +
                ", beginNumber='" + beginNumber + '\'' +
                ", endNumber='" + endNumber + '\'' +
                ", members=" + members +
                '}';
    }

    public static class CommitteeMember {
        public String coinbase;
        public String PKey;
        public String flag;
        public String type;

        public String getCoinbase() {
            return coinbase;
        }

        public void setCoinbase(String coinbase) {
            this.coinbase = coinbase;
        }

        public String getFlag() {
            return flag;
        }

        public void setFlag(String flag) {
            this.flag = flag;
        }

        public String getPKey() {
            return PKey;
        }

        public void setPKey(String PKey) {
            this.PKey = PKey;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return "CommitteeMember{" +
                    "coinbase='" + coinbase + '\'' +
                    ", PKey='" + PKey + '\'' +
                    ", flag='" + flag + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }

        public static class ResponseDeserialiser extends JsonDeserializer<CommitteeMember> {

            private ObjectReader objectReader = ObjectMapperFactory.getObjectReader();

            @Override
            public CommitteeMember deserialize(
                    JsonParser jsonParser, DeserializationContext deserializationContext)
                    throws IOException {
                if (jsonParser.getCurrentToken() != JsonToken.VALUE_NULL) {
                    return objectReader.readValue(jsonParser, CommitteeMember.class);
                } else {
                    return null; // null is wrapped by Optional in above getter
                }
            }
        }
    }

    public List<CommitteeMember> getMembers() {
        return members;
    }

    public void setMembers(List<CommitteeMember> members) {
        this.members = members;
    }

    public String getBeginSnailNumber() {
        return beginSnailNumber;
    }

    public void setBeginSnailNumber(String beginSnailNumber) {
        this.beginSnailNumber = beginSnailNumber;
    }

    public String getEndSnailNumber() {
        return endSnailNumber;
    }

    public void setEndSnailNumber(String endSnailNumber) {
        this.endSnailNumber = endSnailNumber;
    }

    public String getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(String memberCount) {
        this.memberCount = memberCount;
    }

    public String getBeginNumber() {
        return beginNumber;
    }

    public void setBeginNumber(String beginNumber) {
        this.beginNumber = beginNumber;
    }

    public String getEndNumber() {
        return endNumber;
    }

    public void setEndNumber(String endNumber) {
        this.endNumber = endNumber;
    }

    public static class ResponseDeserialiser extends JsonDeserializer<CommitteeInfo> {

        private ObjectReader objectReader = ObjectMapperFactory.getObjectReader();

        @Override
        public CommitteeInfo deserialize(
                JsonParser jsonParser, DeserializationContext deserializationContext)
                throws IOException {
            if (jsonParser.getCurrentToken() != JsonToken.VALUE_NULL) {
                return objectReader.readValue(jsonParser, CommitteeInfo.class);
            } else {
                return null; // null is wrapped by Optional in above getter
            }
        }
    }
}
