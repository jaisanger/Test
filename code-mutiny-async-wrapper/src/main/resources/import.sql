-- CREATE SEQUENCE set_daily_eod_seq_id;
-- SELECT NEXTVAL('set_daily_eod_seq_id');

-- SELECT NEXTVAL ('set_daily_settlement_seq_id'); 

-- CREATE SEQUENCE set_daily_settlement_seq_id
-- MINVALUE 1 
-- MAXVALUE 999999999 
-- INCREMENT BY 1 
-- START WITH 202700 
-- NOCACHE 
-- NOCYCLE;



CREATE SEQUENCE set_daily_eod_seq_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


CREATE SEQUENCE set_daily_settlement_seq_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
   
   
-- SELECT nextval('set_daily_settlement_seq_id');   

--INSERT INTO set_daily_settlement_seq_id ("last_value",log_cnt,is_called) VALUES
--	 (2,32,true);

-- SELECT "11111111111111111111111111111111111111111111111111111111111111111111111111111111111111111";