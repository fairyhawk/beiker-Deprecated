-- ----------预付款相关上线

ALTER TABLE beiker_goods ADD isadvance TINYINT(4) NOT NULL DEFAULT 0 COMMENT '是否预付款 0：否; 1：是';

alter table beiker_trxorder_goods add isadvance tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否预付款 0：否 ;1：是';

