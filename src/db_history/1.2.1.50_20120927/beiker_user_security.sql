-- 账户安全,发送验证邮件记录
DROP TABLE IF EXISTS beiker_user_security_url;
CREATE TABLE beiker_user_security_url(
id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
urlsign VARCHAR(6) NOT NULL COMMENT '标识符',
userid INT(10) UNSIGNED NOT NULL COMMENT '用户id',
opid INT(10) UNSIGNED NOT NULL COMMENT '客服id', 
helptime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
isclicked ENUM('y','n') NOT NULL DEFAULT 'n' COMMENT '是否被点击',
index urlsign (`urlsign`)
) COMMENT='用户联系客服发送邮件修改手机号' COLLATE='utf8_general_ci' ENGINE=InnoDB;

-- 客服协助修改操作记录
DROP TABLE IF EXISTS beiker_customer_help;
CREATE TABLE beiker_customer_help(
id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
userid INT(10) UNSIGNED NOT NULL COMMENT '修改的用户id',
newmobile VARCHAR(11) NOT NULL COMMENT '新手机号,如果新旧手机号一样,则是发了验证邮件',
oldmobile VARCHAR(11) NOT NULL COMMENT '旧手机号',
operatorid INT(10) UNSIGNED NOT NULL COMMENT '操作员id',
optime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
index opid (`operatorid`)
) COMMENT='客服帮助修改手机' COLLATE='utf8_general_ci' ENGINE=InnoDB;
