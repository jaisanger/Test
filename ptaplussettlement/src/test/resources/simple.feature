Feature: a Partner GL needs to be settled
    
    Scenario: partner amount credited in p2m gl has to be transferred to cbs
        Given it's non EOD settlement
        When A Scheduled request for Settlement is triggered
        Then Call the fino-Settlement-Service with Settlement Request Parameters
        And Call the cbs-Settlement-Service with Settlement Request Parameters

    Scenario: collect the meta data for settlement
        Given a SettlementRequest S000000001 to fino-Settlement-Service
        When The request is for File Based Settlement
                And a BatchID is batch1
        Then generate a corelationId
                And get the list of Ledgers in the Batch1
                And get the respective threshold time and threshold amount
                And get the respective IntermediaryGL and PurchaseGL
                And for each Ledger get unsettled transactions
        
    Scenario: all transactions have been settled
        Given a ledger GL1
        And threshold amount ta1 
        And threshold time tt1
        And IntermediaryGL IMGL1
        And PurchaseGL NPCIGL1
        When every transaction in transaction_leg_table for GL1 before (now - threshold time ) is settled at fino
        Then skip the settlement
        And log the message

    Scenario: transactions have to be settled
        Given a ledger GL1
        And threshold amount ta1
        And threshold time tt1
        And Intermediary GL IMGL1
        And PurchaseGL NPCIGL1
        When there is transaction in transaction_leg_table for GL1 before (now -threshold time) which is not settled
        Then  get list of all the transactions
            And  calculate settlement amount
   
    Scenario: calculate settlement amount
        Given a ledger GL1
        And threshold amount ta1
        And threshold time tt1
        And Intermediary GL IMGL1
        And PurchaseGL NPCIGL1
        And list of non settled transactions
        When request for settled amount
        Then sum of all the credit transactions amount in GL1
            And subtract the all the debit transaction amount in GL1
            And subtract the threshold amount ta1
            And do the settlement transactions

    Scenario: if settle amount is less than or equal to zero
        Given a ledger GL1
        And threshold amount ta1
        And threshold time tt1
        And Intermediary GL IMGL1
        And PurchaseGL NPCIGL1
        And list of non settled transactions
        And a calculated settle amount X
        When settle amount X is less than or equal to zero
        Then skip the settlement
        And log the message

    Scenario: settlement transaction is required
        Given a ledgers GL1
        And threshold amount ta1
        And threshold time tt1
        And Intermediary GL IMGL1
        And PurchaseGL NPCIGL1
        And list of non settled transactions
        And a calculated settle amount X
        When settle amount X is greater than 0
        Then post a transaction to debit x from partnerGL GL1 and credit to the IntermediaryGL IMGL1
            And post a transaction to debit amount x from IntermediaryGL IMGL1 and credit to the PurchaseGL NPCIGL1
            And update the transaction status

    Scenario:  failed settlement transactions
        Given a ledgers GL1
        And threshold amount ta1
        And threshold time tt1
        And Intermediary GL IMGL1
        And PurchaseGL NPCIGL1
        And list of non settled transactions
        And a calculated settle amount X
        And settlement transaction status
        When both the transactions has been failed at fino end
        Then create an alert for fino 
        And log the message

    Scenario: successfull settlement transactions at fino end
        Given  a ledgers GL1
        And threshold amount ta1
        And threshold time tt1
        And Intermediary GL IMGL1
        And PurchaseGL NPCIGL1
        And list of non settled transactions 
        And a calculated settle amount X
        And settlement transaction status
        When both the transactions has been succeed at fino end
        Then update the isSettledatFino flag to TRUE for each transaction in non settled transactions list 
        And insert new entry in cbs_transaction_leg table for amount x to debit from  PurchaseGL and credit to IntermediaryGl
        And insert new entry in cbs_transaction_leg table for amount x to debit from IntermediaryGL and  credit to partnerGl 
        And request for cbs settlement
        
    Scenario:  request for settlement at cbs end
        Given a cbs_transaction_leg table
        When request to settlement at cbs
        Then  create ift file for the unsettled transactions in cbs_transaction_leg
        And upload the file to the minio
        And return the success message

    Scenario: status update of settlement transactions at cbs
        Given an IFT response file is recieved
        When a transaction succeed at cbs end
        Then update the isSettled flag to TRUE in cbs_transaction_leg



    Scenario: partner amount credited in p2m gl has to be transferred to cbs
        Given it's EOD settlement
        When A Scheduled request for Settlement is triggered
        Then Call the fino-Settlement-Service with Settlement Request Parameters
        And Call the cbs-Settlement-Service with Settlement Request1 api based
        And Call the balance-Match-service
        And Call the cbs-Settlement-Service with Settlement Request2 file based


    Scenario: collect the meta data for settlement
        Given a SettlementRequest S000000001 to fino-Settlement-Service
        And it's EOD settlement
        When a BatchID is batch1
        Then generate a corelationId
                And get the list of Ledgers in the Batch1
                And get the respective IntermediaryGL and PurchaseGL
                And for each ledger get the ledger balance
        
    Scenario: gl balance is zero no settlement is required
        Given a ledger GL1
        And IntermediaryGL IMGL1
        And PurchaseGL NPCIGL1
        And GL1 ledger balance
        When if the balance of GL1 is zero
        Then skip the EOD

    Scenario: EOD settlement is requested
        Given a ledger GL1
        And threshold amount ta1
        And threshold time tt1
        And Intermediary GL IMGL1
        And PurchaseGL NPCIGL1
        And GL1 ledger balance X
        When GL1 ledger balance is non zero
        Then post a transaction to debit x from partnerGL GL1 and credit to the IntermediaryGL IMGL1
            And debit amount x from IntermediaryGL IMGL1 and credit to the PurchaseGL NPCIGL1
            And update cbs_transaction_leg table


    Scenario: eod failed at fino
        Given settlement transaction status
        When both the transactions has been failed at fino end
        Then create an alert for fino 
        And log the message
        And retry for the same GL

    Scenario: eod succeed at fino
        Given settlement transaction status
        When both the transactions has been succeed at fino end
        Then insert new entry in cbs_transaction_leg table for amount x to debit from  PurchaseGL and credit to IntermediaryGl
        And insert new entry in cbs_transaction_leg table for amount x to debit from IntermediaryGL and  credit to partnerGl 

    Scenario: create request for cbs settlement
        Given a cbs_transaction_leg table
        And no of gls settle in one go N
        And it's api based settlement
        When cbs-Settlement-Service has been called
        Then  create a response for N unsettled transactions in cbs_transaction_leg
        And update the isSettedByApi flag TRUE
        And return the success message

    Scenario: create request for cbs settlement
        Given a cbs_transaction_leg table
        And it's file based settlement
        When cbs-Settlement-Service has been called
        Then  create ift file for the unsettled transactions in cbs_transaction_leg
        And upload the file to the minio
        And return the success message

    Scenario: balance-Match-service has been called
        Given eod has been completed at fino end
        When request for balance match
        Then get the list of all the ledgers
        And for each ledger check transaction sum
    
    Scenario: balance amount matched 
        Given a ledger GL1
        And settlement at fino end has been completed
        When sum of credited amount is equal to sum of debited amount for the GL1
        Then update the isSettledatFino flag to TRUE for each transaction in transaction_leg_table

    Scenario: balance amount not matched 
        Given a ledger GL1
        And settlement at fino end has been completed
        When sum of credited amount is not equal to sum of debited amount for the GL1
        Then check for each transaction if it has not been settled or not

    Scenario:  transaction has been settle 
        Given a ledger GL1
        And settlement at fino end has been completed
        When transaction t1 is settled
        Then update the isSettledatFino flag to TRUE for transaction t1 in transaction_leg_table

    Scenario: status update of settlement transactions at cbs
            Given an IFT response file is recieved
            When a transaction succeed at cbs end
            Then update the isSettled flag to TRUE in cbs_transaction_leg