ALTER TABLE beiker_adwebtrxinfo ADD status ENUM('1','2') NOT NULL DEFAULT '1' COMMENT '交易状态 1：订单初始化 ; 2：交易成功';


