// package com.finobank.ptaplus;

// import static org.junit.jupiter.api.Assertions.assertEquals;

// import java.io.File;
// import java.math.BigInteger;
// import java.util.ArrayList;

// import javax.inject.Inject;
// import javax.ws.rs.core.Response;

// import org.eclipse.microprofile.rest.client.inject.RestClient;
// import com.finobank.ptaplus.SettlementUtils.CorelationId;
// import com.finobank.ptaplus.client.SettlementBatchRules;
// import com.finobank.ptaplus.payload.request.SettlementRequest;
// import com.finobank.ptaplus.repository.TransactionLeg;
// import com.finobank.ptaplus.repository.model.SettlementAudit;
// import com.finobank.ptaplus.repository.model.Transactions;
// import com.finobank.ptaplus.service.FinoAlerts;
// import com.finobank.ptaplus.service.IFTFileGenerator;
// import com.finobank.ptaplus.service.SettlementServices;
// import com.finobank.ptaplus.service.SettlementTransactionUtils;

// import io.cucumber.java.en.Given;
// import io.cucumber.java.en.Then;
// import io.cucumber.java.en.When;
// import io.quarkiverse.cucumber.CucumberQuarkusTest;

// // @RunWith(Cucumber.class)
// // @RunWith(Cucumber.class)
// public class CucumberTest extends CucumberQuarkusTest {

//     @Inject
//     SettlementServices settlementServices;

//     @Inject
//     CorelationId corelationId;

//     @Inject
//     FinoAlerts finoAlerts;

//     @Inject
//     TransactionLeg transactionLeg;

//     @Inject
//     IFTFileGenerator iftFileGenerator;

//     @Inject
//     SettlementTransactionUtils settlementTransactionUtils;


//     @RestClient
//     SettlementBatchRules settlementBatchRules;


//     @Given("it's non EOD settlement")
//     public void it_s_non_eod_settlement(SettlementRequest settlementRequest) {
//     }
    
//     @When("A Scheduled request for Settlement is triggered")
//     public void a_scheduled_request_for_settlement_is_triggered() {
//     }
    
//     @Then("Call the fino-Settlement-Service with Settlement Request Parameters")
//     public void call_the_fino_settlement_service_with_settlement_request_parameters() {
//         SettlementRequest settlementRequest=new SettlementRequest();
//         settlementRequest.setBatchId("BatchId1");
//         settlementRequest.setApiExecutionMode(false);
//         assertEquals(200,settlementServices.SettlementP2mPay(settlementRequest).getStatus());
//         // assertEquals(new SettlementResponse("0", "Settlement Completed At finoEnd."),(SettlementResponse) settlementServices.SettlementP2mPay(settlementRequest).getEntity());
//     }
    
//     @Then("Call the cbs-Settlement-Service with Settlement Request Parameters")
//     public void call_the_cbs_settlement_service_with_settlement_request_parameters() {
//         SettlementRequest settlementRequest=new SettlementRequest();
//         settlementRequest.setBatchId("BatchId1");
//         settlementRequest.setApiExecutionMode(false);
//         assertEquals(200,settlementServices.SettlementCbsApi().getStatus());
//         // assertEquals(new SettlementResponse("0", "Settlement Completed At cbsEnd."),(SettlementResponse) settlementServices.SettlementP2mPay(settlementRequest).getEntity());
//         }
    
//     @Given("a SettlementRequest S000000001 to fino-Settlement-Service")
//     public void a_settlement_request_s000000001_to_fino_settlement_service() {
//     }
    
//     @When("The request is for File Based Settlement")
//     public void the_request_is_for_file_based_settlement() {

//     }
    
//     @When("a BatchID is batch1")
//     public void a_batch_id_is_batch1() {

//     }
    
//     @Then("generate a corelationId")
//     public void generate_a_corelation_id() {

//     }
    
//     @Then("get the list of Ledgers in the Batch1")
//     public void get_the_list_of_ledgers_in_the_batch1() {
//         assertEquals(new ArrayList<TransactionLeg>(), settlementBatchRules.getGroupRule());
//     }
    
//     @Then("get the respective threshold time and threshold amount")
//     public void get_the_respective_threshold_time_and_threshold_amount() {
        

//     }
    
//     @Then("get the respective IntermediaryGL and PurchaseGL")
//     public void get_the_respective_intermediary_gl_and_purchase_gl() {
        

//     }
    
//     @Then("for each Ledger get unsettled transactions")
//     public void for_each_ledger_get_unsettled_transactions() {
//         assertEquals(new ArrayList<Transactions>(),transactionLeg.getUnsettledTransactions());

//     }
    
//     @Given("a ledger GL1")
//     public void a_ledger_gl1() {

//     }
    
//     @Given("threshold amount ta1")
//     public void threshold_amount_ta1() {
        

//     }
    
//     @Given("threshold time tt1")
//     public void threshold_time_tt1() {
        

//     }
    
//     @Given("PurchaseGL NPCIGL1")
//     public void purchase_gl_npcigl1() {
        

//     }
    
//     @When("every transaction in transaction_leg_table for GL1 before \\(now - threshold time ) is settled at fino")
//     public void every_transaction_in_transaction_leg_table_for_gl1_before_now_threshold_time_is_settled_at_fino() {
//         assertEquals(new ArrayList<Transactions>(),settlementTransactionUtils.getUnsettledTransactions());
//     }
    
//     @Then("skip the settlement")
//     public void skip_the_settlement() {

//     }
    
//     @Then("log the message")
//     public void log_the_message() {
        
//     }
    
//     @Given("Intermediary GL IMGL1")
//     public void intermediary_gl_imgl1() {
        
//     }
    
//     @When("there is transaction in transaction_leg_table for GL1 before \\(now -threshold time) which is not settled")
//     public void there_is_transaction_in_transaction_leg_table_for_gl1_before_now_threshold_time_which_is_not_settled() {

//     }
    
//     @Then("get list of all the transactions")
//     public void get_list_of_all_the_transactions() {
//         assertEquals(new ArrayList<Transactions>(),settlementTransactionUtils.getUnsettledTransactions());        
//     }
    
//     @Then("calculate settlement amount")
//     public void calculate_settlement_amount() {
//         assertEquals(BigInteger.ZERO,settlementTransactionUtils.calculateSettleAmmount(new ArrayList<Transactions>()));

//     }
    
//     @Given("list of non settled transactions")
//     public void list_of_non_settled_transactions() {
        

//     }
    
//     @When("request for settled amount")
//     public void request_for_settled_amount() {
//         assertEquals(BigInteger.ZERO,settlementTransactionUtils.calculateSettleAmmount(new ArrayList<Transactions>()));

//     }
    
//     @Then("sum of all the credit transactions amount in GL1")
//     public void sum_of_all_the_credit_transactions_amount_in_gl1() {
//         assertEquals(BigInteger.ZERO,settlementTransactionUtils.creditedAmmount(new ArrayList<Transactions>()));


//     }
    
//     @Then("subtract the all the debit transaction amount in GL1")
//     public void subtract_the_all_the_debit_transaction_amount_in_gl1() {
//         assertEquals(BigInteger.ZERO,settlementTransactionUtils.debitedAmmount(new ArrayList<Transactions>()));


//     }
    
//     @Then("subtract the threshold amount ta1")
//     public void subtract_the_threshold_amount_ta1() {
        

//     }
    
//     @Then("do the settlement transactions")
//     public void do_the_settlement_transactions() {
        
//     }
    
//     @Given("a calculated settle amount X")
//     public void a_calculated_settle_amount_x() {
        

//     }
    
//     @When("settle amount X is less than or equal to zero")
//     public void settle_amount_x_is_less_than_or_equal_to_zero() {
        

//     }
    
//     @Given("a ledgers GL1")
//     public void a_ledgers_gl1() {
        

//     }
    
//     @When("settle amount X is greater than {int}")
//     public void settle_amount_x_is_greater_than(Integer int1) {
        

//     }
    
//     @Then("post a transaction to debit x from partnerGL GL1 and credit to the IntermediaryGL IMGL1")
//     public void post_a_transaction_to_debit_x_from_partner_gl_gl1_and_credit_to_the_intermediary_gl_imgl1() {
//         assertEquals(Response.ok().build(), settlementServices.postTransaction());

//     }
    
//     @Then("post a transaction to debit amount x from IntermediaryGL IMGL1 and credit to the PurchaseGL NPCIGL1")
//     public void post_a_transaction_to_debit_amount_x_from_intermediary_gl_imgl1_and_credit_to_the_purchase_gl_npcigl1() {
//         assertEquals(Response.ok().build(), settlementServices.postTransaction());

//     }
    
//     @Then("update the transaction status")
//     public void update_the_transaction_status() {
//         settlementServices.updateSettlementStatus(new ArrayList<Transactions>());
//     }
    
//     @Given("settlement transaction status")
//     public void settlement_transaction_status() {
        

//     }
    
//     @When("both the transactions has been failed at fino end")
//     public void both_the_transactions_has_been_failed_at_fino_end() {
        

//     }
    
//     @Then("create an alert for fino")
//     public void create_an_alert_for_fino() {
//         finoAlerts.throwAlert("settlement for {} is failed", "P2");

//     }
    
//     @When("both the transactions has been succeed at fino end")
//     public void both_the_transactions_has_been_succeed_at_fino_end() {
        
//     }
    
//     @Then("update the isSettledatFino flag to TRUE for each transaction in non settled transactions list")
//     public void update_the_is_settledat_fino_flag_to_true_for_each_transaction_in_non_settled_transactions_list() {
        

//     }
    
//     @Then("insert new entry in cbs_transaction_leg table for amount x to debit from  PurchaseGL and credit to IntermediaryGl")
//     public void insert_new_entry_in_cbs_transaction_leg_table_for_amount_x_to_debit_from_purchase_gl_and_credit_to_intermediary_gl() {
//         settlementServices.insertSettlementEntry("query");   
//     }
    
//     @Then("insert new entry in cbs_transaction_leg table for amount x to debit from IntermediaryGL and  credit to partnerGl")
//     public void insert_new_entry_in_cbs_transaction_leg_table_for_amount_x_to_debit_from_intermediary_gl_and_credit_to_partner_gl() {
//         settlementServices.insertSettlementEntry("query");   

//     }
    
//     @Then("request for cbs settlement")
//     public void request_for_cbs_settlement() {
//         assertEquals(Response.ok().build(),settlementServices.SettlementCbsApi());

//     }
    
//     @Given("a cbs_transaction_leg table")
//     public void a_cbs_transaction_leg_table() {
        

//     }
    
//     @When("request to settlement at cbs")
//     public void request_to_settlement_at_cbs() {
        

//     }
    
//     @Then("create ift file for the unsettled transactions in cbs_transaction_leg")
//     public void create_ift_file_for_the_unsettled_transactions_in_cbs_transaction_leg() {
//         assertEquals(null,iftFileGenerator.generateIFT(new ArrayList<SettlementAudit>()));

//     }
    
//     @Then("upload the file to the minio")
//     public void upload_the_file_to_the_minio() {
//         assertEquals(null,iftFileGenerator.uploadFile(new File("")));
//     }
    
//     @Then("return the success message")
//     public void return_the_success_message() {
        

//     }
    
//     @Given("an IFT response file is recieved")
//     public void an_ift_response_file_is_recieved() {
        

//     }
    
//     @When("a transaction succeed at cbs end")
//     public void a_transaction_succeed_at_cbs_end() {
        

//     }
    
//     @Then("update the isSettled flag to TRUE in cbs_transaction_leg")
//     public void update_the_is_settled_flag_to_true_in_cbs_transaction_leg() {
        

//     }
    
//     @Given("it's EOD settlement")
//     public void it_s_eod_settlement() {
        

//     }
    
//     @Then("Call the cbs-Settlement-Service with Settlement Request1 api based")
//     public void call_the_cbs_settlement_service_with_settlement_request1_api_based() {
        

//     }
    
//     @Then("Call the balance-Match-service")
//     public void call_the_balance_match_service() {
        

//     }
    
//     @Then("Call the cbs-Settlement-Service with Settlement Request2 file based")
//     public void call_the_cbs_settlement_service_with_settlement_request2_file_based() {
        

//     }
    
//     @Then("for each ledger get the ledger balance")
//     public void for_each_ledger_get_the_ledger_balance() {
        

//     }
    
//     @Given("GL1 ledger balance")
//     public void gl1_ledger_balance() {
        

//     }
    
//     @When("if the balance of GL1 is zero")
//     public void if_the_balance_of_gl1_is_zero() {
        

//     }
    
//     @Then("skip the EOD")
//     public void skip_the_eod() {
        

//     }
    
//     @Given("GL1 ledger balance X")
//     public void gl1_ledger_balance_x() {
        

//     }
    
//     @When("GL1 ledger balance is non zero")
//     public void gl1_ledger_balance_is_non_zero() {
        

//     }
    
//     @Then("debit amount x from IntermediaryGL IMGL1 and credit to the PurchaseGL NPCIGL1")
//     public void debit_amount_x_from_intermediary_gl_imgl1_and_credit_to_the_purchase_gl_npcigl1() {
        

//     }
    
//     @Then("update cbs_transaction_leg table")
//     public void update_cbs_transaction_leg_table() {
        

//     }
    
//     @Then("retry for the same GL")
//     public void retry_for_the_same_gl() {
        

//     }
    
//     @Given("no of gls settle in one go N")
//     public void no_of_gls_settle_in_one_go_n() {
        

//     }
    
//     @Given("it's api based settlement")
//     public void it_s_api_based_settlement() {
        

//     }
    
//     @When("cbs-Settlement-Service has been called")
//     public void cbs_settlement_service_has_been_called() {
        

//     }
    
//     @Then("create a response for N unsettled transactions in cbs_transaction_leg")
//     public void create_a_response_for_n_unsettled_transactions_in_cbs_transaction_leg() {
        

//     }
    
//     @Then("update the isSettedByApi flag TRUE")
//     public void update_the_is_setted_by_api_flag_true() {
        

//     }
    
//     @Given("it's file based settlement")
//     public void it_s_file_based_settlement() {
        

//     }
    
//     @Given("eod has been completed at fino end")
//     public void eod_has_been_completed_at_fino_end() {
        

//     }
    
//     @When("request for balance match")
//     public void request_for_balance_match() {
        

//     }
    
//     @Then("get the list of all the ledgers")
//     public void get_the_list_of_all_the_ledgers() {
        
//     }
    
//     @Then("for each ledger check transaction sum")
//     public void for_each_ledger_check_transaction_sum() {
        

//     }
    
//     @Given("settlement at fino end has been completed")
//     public void settlement_at_fino_end_has_been_completed() {
        

//     }
    
//     @When("sum of credited amount is equal to sum of debited amount for the GL1")
//     public void sum_of_credited_amount_is_equal_to_sum_of_debited_amount_for_the_gl1() {
        

//     }
    
//     @Then("update the isSettledatFino flag to TRUE for each transaction in transaction_leg_table")
//     public void update_the_is_settledat_fino_flag_to_true_for_each_transaction_in_transaction_leg_table() {
        

//     }
    
//     @When("sum of credited amount is not equal to sum of debited amount for the GL1")
//     public void sum_of_credited_amount_is_not_equal_to_sum_of_debited_amount_for_the_gl1() {
        

//     }
    
//     @Then("check for each transaction if it has not been settled or not")
//     public void check_for_each_transaction_if_it_has_not_been_settled_or_not() {
        

//     }
    
//     @When("transaction t1 is settled")
//     public void transaction_t1_is_settled() {
        

//     }
    
//     @Then("update the isSettledatFino flag to TRUE for transaction t1 in transaction_leg_table")
//     public void update_the_is_settledat_fino_flag_to_true_for_transaction_t1_in_transaction_leg_table() {
        

//     }

//     @Given("IntermediaryGL IMGL1")
//     public void IntermediaryGL_IMGL() {
//         // Write code here that turns the phrase above into concrete actions
//     }

// }