// package org.finobank.ptaplus;
// import io.cucumber.java.en.And;
// import io.cucumber.java.en.Given;
// import io.cucumber.java.en.Then;
// import io.cucumber.java.en.When;

// public class simpleDefinitions {
//     @Given("it's non EOD settlement")
//     public void itSNonEODSettlement() {
//     }

//     @When("A Scheduled request for Settlement is triggered")
//     public void aScheduledRequestForSettlementIsTriggered() {
//     }

//     @Then("Call the fino-Settlement-Service with Settlement Request Parameters")
//     public void callTheFinoSettlementServiceWithSettlementRequestParameters() {
//     }

//     @And("Call the cbs-Settlement-Service with Settlement Request Parameters")
//     public void callTheCbsSettlementServiceWithSettlementRequestParameters() {
//     }

//     @Given("a SettlementRequest S{int} to fino-Settlement-Service")
//     public void aSettlementRequestSToFinoSettlementService(int arg0) {
//     }

//     @When("The request is for File Based Settlement")
//     public void theRequestIsForFileBasedSettlement() {
//     }

//     @And("a BatchID is batch{int}")
//     public void aBatchIDIsBatch(int arg0) {
//     }

//     @Then("generate a corelationId")
//     public void generateACorelationId() {
//     }

//     @And("get the list of Ledgers in the Batch{int}")
//     public void getTheListOfLedgersInTheBatch(int arg0) {
//     }

//     @And("get the respective threshold time and threshold amount")
//     public void getTheRespectiveThresholdTimeAndThresholdAmount() {
//     }

//     @And("get the respective IntermediaryGL and PurchaseGL")
//     public void getTheRespectiveIntermediaryGLAndPurchaseGL() {
//     }

//     @And("for each Ledger get unsettled transactions")
//     public void forEachLedgerGetUnsettledTransactions() {
//     }

//     @Given("a ledger GL{int}")
//     public void aLedgerGL(int arg0) {
//     }

//     @And("threshold amount ta{int}")
//     public void thresholdAmountTa(int arg0) {
//     }

//     @And("threshold time tt{int}")
//     public void thresholdTimeTt(int arg0) {
//     }

//     @And("IntermediaryGL IMGL{int}")
//     public void intermediaryglIMGL(int arg0) {
//     }

//     @And("PurchaseGL NPCIGL{int}")
//     public void purchaseglNPCIGL(int arg0) {
//     }

//     @When("every transaction in transaction_leg_table for GL{int} before \\(now - threshold time ) is settled at fino")
//     public void everyTransactionInTransaction_leg_tableForGLBeforeNowThresholdTimeIsSettledAtFino(int arg0) {
//     }

//     @Then("skip the settlement")
//     public void skipTheSettlement() {
//     }

//     @And("log the message")
//     public void logTheMessage() {
//     }

//     @And("Intermediary GL IMGL{int}")
//     public void intermediaryGLIMGL(int arg0) {
//     }

//     @When("there is transaction in transaction_leg_table for GL{int} before \\(now -threshold time) which is not settled")
//     public void thereIsTransactionInTransaction_leg_tableForGLBeforeNowThresholdTimeWhichIsNotSettled(int arg0) {
//     }

//     @Then("get list of all the transactions")
//     public void getListOfAllTheTransactions() {
//     }

//     @And("calculate settlement amount")
//     public void calculateSettlementAmount() {
//     }

//     @And("list of non settled transactions")
//     public void listOfNonSettledTransactions() {
//     }

//     @When("request for settled amount")
//     public void requestForSettledAmount() {
//     }

//     @Then("sum of all the credit transactions amount in GL{int}")
//     public void sumOfAllTheCreditTransactionsAmountInGL(int arg0) {
//     }

//     @And("subtract the all the debit transaction amount in GL{int}")
//     public void subtractTheAllTheDebitTransactionAmountInGL(int arg0) {
//     }

//     @And("subtract the threshold amount ta{int}")
//     public void subtractTheThresholdAmountTa(int arg0) {
//     }

//     @And("do the settlement transactions")
//     public void doTheSettlementTransactions() {
//     }

//     @And("a calculated settle amount X")
//     public void aCalculatedSettleAmountX() {
//     }

//     @When("settle amount X is less than or equal to zero")
//     public void settleAmountXIsLessThanOrEqualToZero() {
//     }

//     @Given("a ledgers GL{int}")
//     public void aLedgersGL(int arg0) {
//     }

//     @And("a calculated settle amount x")
//     public void aCalculatedSettleAmountX() {
//     }

//     @When("settle amount X is greater than {int}")
//     public void settleAmountXIsGreaterThan(int arg0) {
//     }

//     @Then("post a transaction to debit x from partnerGL GL{int} and credit to the IntermediaryGL IMGL{int}")
//     public void postATransactionToDebitXFromPartnerGLGLAndCreditToTheIntermediaryGLIMGL(int arg0, int arg1) {
//     }

//     @And("post a transaction to debit amount x from IntermediaryGL IMGL{int} and credit to the PurchaseGL NPCIGL{int}")
//     public void postATransactionToDebitAmountXFromIntermediaryGLIMGLAndCreditToThePurchaseGLNPCIGL(int arg0, int arg1) {
//     }

//     @And("update the transaction status")
//     public void updateTheTransactionStatus() {
//     }

//     @And("settlement transaction status")
//     public void settlementTransactionStatus() {
//     }

//     @When("both the transactions has been failed at fino end")
//     public void bothTheTransactionsHasBeenFailedAtFinoEnd() {
//     }

//     @Then("create an alert for fino")
//     public void createAnAlertForFino() {
//     }

//     @When("both the transactions has been succeed at fino end")
//     public void bothTheTransactionsHasBeenSucceedAtFinoEnd() {
//     }

//     @Then("update the isSettledatFino flag to TRUE for each transaction in non settled transactions list")
//     public void updateTheIsSettledatFinoFlagToTRUEForEachTransactionInNonSettledTransactionsList() {
//     }

//     @And("insert new entry in cbs_transaction_leg table for amount x to debit from  PurchaseGL and credit to IntermediaryGl")
//     public void insertNewEntryInCbs_transaction_legTableForAmountXToDebitFromPurchaseGLAndCreditToIntermediaryGl() {
//     }

//     @And("insert new entry in cbs_transaction_leg table for amount x to debit from IntermediaryGL and  credit to partnerGl")
//     public void insertNewEntryInCbs_transaction_legTableForAmountXToDebitFromIntermediaryGLAndCreditToPartnerGl() {
//     }

//     @And("request for cbs settlement")
//     public void requestForCbsSettlement() {
//     }

//     @Given("a cbs_transaction_leg table")
//     public void aCbs_transaction_legTable() {
//     }

//     @When("request to settlement at cbs")
//     public void requestToSettlementAtCbs() {
//     }

//     @Then("create ift file for the unsettled transactions in cbs_transaction_leg")
//     public void createIftFileForTheUnsettledTransactionsInCbs_transaction_leg() {
//     }

//     @And("upload the file to the minio")
//     public void uploadTheFileToTheMinio() {
//     }

//     @And("return the success message")
//     public void returnTheSuccessMessage() {
//     }

//     @Given("an IFT response file is recieved")
//     public void anIFTResponseFileIsRecieved() {
//     }

//     @When("a transaction succeed at cbs end")
//     public void aTransactionSucceedAtCbsEnd() {
//     }

//     @Then("update the isSettled flag to TRUE in cbs_transaction_leg")
//     public void updateTheIsSettledFlagToTRUEInCbs_transaction_leg() {
//     }

//     @Given("it's EOD settlement")
//     public void itSEODSettlement() {
//     }

//     @And("Call the cbs-Settlement-Service with Settlement Request{int} api based")
//     public void callTheCbsSettlementServiceWithSettlementRequestApiBased(int arg0) {
//     }

//     @And("Call the balance-Match-service")
//     public void callTheBalanceMatchService() {
//     }

//     @And("Call the cbs-Settlement-Service with Settlement Request{int} file based")
//     public void callTheCbsSettlementServiceWithSettlementRequestFileBased(int arg0) {
//     }

//     @And("for each ledger get the ledger balance")
//     public void forEachLedgerGetTheLedgerBalance() {
//     }

//     @And("GL{int} ledger balance")
//     public void glLedgerBalance(int arg0) {
//     }

//     @When("if the balance of GL{int} is zero")
//     public void ifTheBalanceOfGLIsZero(int arg0) {
//     }

//     @Then("skip the EOD")
//     public void skipTheEOD() {
//     }

//     @And("GL{int} ledger balance X")
//     public void glLedgerBalanceX(int arg0) {
//     }

//     @When("GL{int} ledger balance is non zero")
//     public void glLedgerBalanceIsNonZero(int arg0) {
//     }

//     @And("debit amount x from IntermediaryGL IMGL{int} and credit to the PurchaseGL NPCIGL{int}")
//     public void debitAmountXFromIntermediaryGLIMGLAndCreditToThePurchaseGLNPCIGL(int arg0, int arg1) {
//     }

//     @And("update cbs_transaction_leg table")
//     public void updateCbs_transaction_legTable() {
//     }

//     @And("retry for the same GL")
//     public void retryForTheSameGL() {
//     }

//     @And("no of gls settle in one go N")
//     public void noOfGlsSettleInOneGoN() {
//     }

//     @And("it's api based settlement")
//     public void itSApiBasedSettlement() {
//     }

//     @When("cbs-Settlement-Service has been called")
//     public void cbsSettlementServiceHasBeenCalled() {
//     }

//     @Then("create a response for N unsettled transactions in cbs_transaction_leg")
//     public void createAResponseForNUnsettledTransactionsInCbs_transaction_leg() {
//     }

//     @And("update the isSettedByApi flag TRUE")
//     public void updateTheIsSettedByApiFlagTRUE() {
//     }

//     @And("it's file based settlement")
//     public void itSFileBasedSettlement() {
//     }

//     @Given("eod has been completed at fino end")
//     public void eodHasBeenCompletedAtFinoEnd() {
//     }

//     @When("request for balance match")
//     public void requestForBalanceMatch() {
//     }

//     @Then("get the list of all the ledgers")
//     public void getTheListOfAllTheLedgers() {
//     }

//     @And("for each ledger check transaction sum")
//     public void forEachLedgerCheckTransactionSum() {
//     }

//     @And("settlement at fino end has been completed")
//     public void settlementAtFinoEndHasBeenCompleted() {
//     }

//     @When("sum of credited amount is equal to sum of debited amount for the GL{int}")
//     public void sumOfCreditedAmountIsEqualToSumOfDebitedAmountForTheGL(int arg0) {
//     }

//     @Then("update the isSettledatFino flag to TRUE for each transaction in transaction_leg_table")
//     public void updateTheIsSettledatFinoFlagToTRUEForEachTransactionInTransaction_leg_table() {
//     }

//     @When("sum of credited amount is not equal to sum of debited amount for the GL{int}")
//     public void sumOfCreditedAmountIsNotEqualToSumOfDebitedAmountForTheGL(int arg0) {
//     }

//     @Then("check for each transaction if it has not been settled or not")
//     public void checkForEachTransactionIfItHasNotBeenSettledOrNot() {
//     }

//     @When("transaction t{int} is settled")
//     public void transactionTIsSettled(int arg0) {
//     }

//     @Then("update the isSettledatFino flag to TRUE for transaction t{int} in transaction_leg_table")
//     public void updateTheIsSettledatFinoFlagToTRUEForTransactionTInTransaction_leg_table(int arg0) {
//     }
// }
