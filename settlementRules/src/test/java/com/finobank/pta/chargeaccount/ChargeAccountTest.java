// /*
//  * Copyright 2020 Red Hat, Inc. and/or its affiliates.
//  *
//  * Licensed under the Apache License, Version 2.0 (the "License");
//  * you may not use this file except in compliance with the License.
//  * You may obtain a copy of the License at
//  *
//  *       http://www.apache.org/licenses/LICENSE-2.0
//  *
//  * Unless required by applicable law or agreed to in writing, software
//  * distributed under the License is distributed on an "AS IS" BASIS,
//  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  * See the License for the specific language governing permissions and
//  * limitations under the License.
//  */
// package com.finobank.pta.chargeaccount;

// import javax.inject.Inject;

// import org.junit.jupiter.api.Test;

// import com.finobank.pta.settlementConfig.MyAgendaEventListener;

// import io.quarkus.test.junit.QuarkusTest;
// import io.restassured.http.ContentType;

// import static io.restassured.RestAssured.given;
// import static org.hamcrest.CoreMatchers.is;
// //import static org.hamcrest.MatcherAssert.assertThat;

// @QuarkusTest
// public class ChargeAccountTest {

//     @Inject
//     MyAgendaEventListener listener;

//     @Test
//     public void testHelloEndpoint() {
//         given()
//                 .accept(ContentType.JSON)
//                 .contentType(ContentType.JSON)
//                 .body("{\n" + //
//                         "  \"settlementConfigRule\": [\n" + //
//                         "    {\n" + //
//                         "      \"batchId\": \"settlement1\"\n" + //
//                         "    }]\n" + //
//                         "}")
//                 .when()
//                 .post("/batch-config")
//                 .then()
//                 .statusCode(200)
//                 .body(is("[{\"batchId\":\"settlement1\",\"partnerSettlementRules\":[{\"partnerId\":\"partner2\",\"ptaPartnerGl\":\"88333555210\",\"ptaIntermediaryGl\":\"20000000002\",\"ptaPurchaseGl\":\"20000000005\",\"cbsPartnerGl\":\"88333555210\",\"cbsIntermediaryGl\":\"20000000002\",\"cbsPurchaseGl\":\"20000000005\",\"costCenter\":\"9001\",\"ptaTranType\":\"PTANREOD\",\"cbsTranType\":\"PTANREOD\",\"thresholdAmt\":\"57\",\"thresholdTime\":\"61\"},{\"partnerId\":\"partner3\",\"ptaPartnerGl\":\"88333555211\",\"ptaIntermediaryGl\":\"20000000002\",\"ptaPurchaseGl\":\"20000000005\",\"cbsPartnerGl\":\"88333555211\",\"cbsIntermediaryGl\":\"20000000002\",\"cbsPurchaseGl\":\"20000000005\",\"costCenter\":\"9001\",\"ptaTranType\":\"PTANREOD\",\"cbsTranType\":\"PTANREOD\",\"thresholdAmt\":\"58\",\"thresholdTime\":\"62\"}]}]"));

//         //assertThat(listener.counter.get(), is(1));
//     }

// }
