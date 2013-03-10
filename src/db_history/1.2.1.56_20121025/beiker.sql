-- 创建发码商配置信息
CREATE TABLE `beiker_codeoperator_configure` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `guest_id` int(11) unsigned NOT NULL COMMENT '商家主键ID',
  `goods_id` int(11) unsigned NOT NULL COMMENT '商品ID',
  `product_num` varchar(50) NOT NULL DEFAULT '' COMMENT '阳关绿洲产品编号',
  `create_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `update_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '更新时间',
  `api_type` varchar(20) NOT NULL DEFAULT '' COMMENT '发码商类型',
  `description` varchar(100) DEFAULT '' COMMENT '备注',
  `version` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `user_id` int(11) unsigned NOT NULL COMMENT '操作人ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='阳光绿洲配置信息表'

ALTER TABLE beiker_codeoperator_configure ADD INDEX goods_id (goods_id); 

-- 更新前台goods表的相关阳光绿洲商品属性为2
update beiker_goods set send_rules=2 where goodsid in(22075,24534,22064,32033,24535,37467,73641,24603,24522,25832,34792,46475,24671,44439,92403,96212,25314,53893,59779,73330,96084,25644,24477,29651,31230,40716,34177,26747,39448,29991,28840,47268,91126,28848,29072,31022,78961,31567,31566,31900,50487,31896,50486,27714,32581,45438,99516,32597,45436,32602,45434,32604,45432,32580,31887,31889,31617,35989,36195,36640,36643,37196,38838,38839,39446,39895,39897,39890,40733,40734,42133,51970,42131,51973,42132,43249,43253,43256,43258,43260,46854,46860,46861,46862,46549,48827,58558,79912,98561,49166,58528,49634,49635,50363,50364,51878,53436,79920,54500,54501,53269,71549,54075,55453,77280,55987,55988,56880,56191,61194,56142,56143,56144,56145,57993,57997,58000,58001,61069,63395,91123,66265,67921,96189,68239,68241,68243,68245,68860,68856,70412,70413,71889,73501,73490,73626,74062,74064,73752,79993,79769,79770,79771,79776,79778,79780,79782,79784,79786,79788,79790,79792,79794,79772,79773,79774,79775,79777,79779,79781,79783,79785,79787,79789,79791,79793,82454,98341,86097,87200,90125,99227,99231,99745,101969,101970,101971,101967,101968,101944);

-- 更新前台trxorder_goods表的相关阳光绿洲商品属性为2
update beiker_trxorder_goods set is_send_mer_vou=2 where goods_id in(22075,24534,22064,32033,24535,37467,73641,24603,24522,25832,34792,46475,24671,44439,92403,96212,25314,53893,59779,73330,96084,25644,24477,29651,31230,40716,34177,26747,39448,29991,28840,47268,91126,28848,29072,31022,78961,31567,31566,31900,50487,31896,50486,27714,32581,45438,99516,32597,45436,32602,45434,32604,45432,32580,31887,31889,31617,35989,36195,36640,36643,37196,38838,38839,39446,39895,39897,39890,40733,40734,42133,51970,42131,51973,42132,43249,43253,43256,43258,43260,46854,46860,46861,46862,46549,48827,58558,79912,98561,49166,58528,49634,49635,50363,50364,51878,53436,79920,54500,54501,53269,71549,54075,55453,77280,55987,55988,56880,56191,61194,56142,56143,56144,56145,57993,57997,58000,58001,61069,63395,91123,66265,67921,96189,68239,68241,68243,68245,68860,68856,70412,70413,71889,73501,73490,73626,74062,74064,73752,79993,79769,79770,79771,79776,79778,79780,79782,79784,79786,79788,79790,79792,79794,79772,79773,79774,79775,79777,79779,79781,79783,79785,79787,79789,79791,79793,82454,98341,86097,87200,90125,99227,99231,99745,101969,101970,101971,101967,101968,101944);


-- 线上发码商与平台对应关系导入
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (512085275,22075,15000000199,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (512085275,24534,15000000199,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (812382760,22064,15000000337,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (812382760,32033,15000000337,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (812382760,24535,15000000337,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (812382760,37467,15000000337,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (812382760,73641,15000000337,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (17297230,24603,15000000905,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (76138045,24522,15000000542,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (76138045,25832,15000000542,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (76138045,34792,15000000542,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (76138045,46475,15000000542,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (31173616,24671,15000000876,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (31173616,44439,15000000876,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (31173616,92403,15000000876,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (31173616,96212,15000000876,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (73019575,25314,15000000764,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (73019575,53893,15000000764,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (73019575,59779,15000000764,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (73019575,73330,15000000764,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (73019575,96084,15000000764,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (91068082,25644,15000001004,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (97472306,24477,15000000201,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (97472306,29651,15000000201,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (97472306,31230,15000000201,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (97472306,40716,15000000201,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (87897741,34177,15000000993,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (87897741,26747,15000000993,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (87897741,39448,15000000993,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (94594793,29991,15000001150,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (94594793,28840,15000001150,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (94594793,47268,15000001150,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (94594793,91126,15000001150,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (61506187,28848,15000001096,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (61506187,29072,15000001097,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (64936955,31022,15000000520,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (64936955,78961,15000000520,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (34087195,31567,15000000184,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (90622485,31566,15000000183,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (74492905,31900,15000000946,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (74492905,50487,15000000946,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (74492905,31896,15000000947,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (74492905,50486,15000000947,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (14777052,27714,15000000966,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (79213028,32581,15000000444,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (79213028,45438,15000000444,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (79213028,99516,15000000444,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (79213028,32597,15000000443,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (79213028,45436,15000000443,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (79213028,32602,15000000442,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (79213028,45434,15000000442,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (79213028,32604,15000000583,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (79213028,45432,15000000583,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (54049180,32580,15000001259,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (86862645,31887,15000000187,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (86862645,31889,15000000841,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (52130037,31617,15000000575,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (89962689,35989,15000001397,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (37464793,36195,15000001339,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (12454344,36640,15000001485,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (12454344,36643,15000001484,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (95810428,37196,15000001422,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (56420882,38838,15000001551,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (97520853,38839,15000001552,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (87897741,39446,15000001557,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (17648440,39895,15000001535,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (17648440,39897,15000001538,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (20050209,39890,15000001562,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (17648440,40733,15000001537,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (17648440,40734,15000001536,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (98427111,42133,15000001534,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (98427111,51970,15000001534,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (98427111,42131,15000001532,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (98427111,51973,15000001532,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (98427111,42132,15000001533,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (86719391,43249,15000001698,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (86719391,43253,15000001699,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (86719391,43256,15000001694,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (86719391,43258,15000001915,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (86719391,43260,15000001914,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (56420882,46854,15000001765,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (56420882,46860,15000001911,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (97520853,46861,15000001757,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (97520853,46862,15000001770,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (64985521,46549,15000001687,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (86719391,48827,15000001916,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (86719391,58558,15000001916,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (86719391,79912,15000001916,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (86719391,98561,15000001916,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (56420882,49166,15000001909,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (56420882,58528,15000001909,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (46751728,49634,15000001890,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (46751728,49635,15000001889,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (97472306,50363,15000001447,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (97472306,50364,15000001449,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (97472306,51878,15000001449,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (17297230,53436,15000001918,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (17297230,79920,15000001918,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (56420882,54500,15000001978,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (56420882,54501,15000002008,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (24935401,53269,15000001659,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (24935401,71549,15000001659,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (64936955,54075,15000001975,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (86719391,55453,15000002035,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (86719391,77280,15000002035,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (69216639,55987,15000002019,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (69216639,55988,15000002018,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (69216639,56880,15000002018,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (64985521,56191,15000002149,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (64985521,61194,15000002149,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (60876402,56142,15000000390,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (60876402,56143,15000000388,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (60876402,56144,15000000389,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (60876402,56145,15000000391,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (87142589,57993,15000001981,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (87142589,57997,15000001980,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (87142589,58000,15000001979,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (87142589,58001,15000001982,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (11797305,61069,15000002087,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (94594793,63395,15000002160,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (94594793,91123,15000002160,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (87897741,66265,15000002228,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (87897741,67921,15000002228,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (87897741,96189,15000002228,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (94334981,68239,15000002398,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (94334981,68241,15000002401,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (94334981,68243,15000002400,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (94334981,68245,15000002399,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (54038114,68860,15000001358,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (31699527,68856,15000002004,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (37231091,70412,15000002315,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (37231091,70413,15000002316,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (12568434,71889,15000002427,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (56420882,73501,15000002426,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (54038114,73490,15000002451,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (14711160,73626,15000002020,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (18785603,74062,15000002313,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (18785603,74064,15000002314,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (32196124,73752,15000002420,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (97520853,79993,15000002442,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (17855039,79769,15000002471,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (85171348,79770,15000002471,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (19392970,79771,15000002471,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (37231091,79776,15000002471,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (17743991,79778,15000002471,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (18785603,79780,15000002471,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (20654507,79782,15000002471,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (79538653,79784,15000002471,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (62393430,79786,15000002471,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (23155411,79788,15000002471,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (14711160,79790,15000002471,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (69216639,79792,15000002471,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (30207028,79794,15000002471,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (19392970,79772,15000002472,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (85171348,79773,15000002472,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (17855039,79774,15000002472,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (37231091,79775,15000002472,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (17743991,79777,15000002472,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (18785603,79779,15000002472,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (20654507,79781,15000002472,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (79538653,79783,15000002472,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (62393430,79785,15000002472,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (23155411,79787,15000002472,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (14711160,79789,15000002472,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (69216639,79791,15000002472,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (30207028,79793,15000002472,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (86719391,82454,15000001913,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (86719391,98341,15000001913,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (31699527,86097,15000002495,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (77143586,87200,15000002599,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (44754079,90125,15000002371,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (54038114,99227,15000002761,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (54038114,99231,15000002760,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (81938165,99745,15000002763,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (88081056,101969,15000002640,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (88081056,101970,15000002651,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (88081056,101971,15000002652,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (88081056,101967,15000002472,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (88081056,101968,15000002471,now(),now(),'SUNSHINE');
insert into beiker_codeoperator_configure(guest_id,goods_id,product_num,create_date,update_date,api_type) value (69216639,101944,15000002651,now(),now(),'SUNSHINE');

-- 退款申请超期邮件模版
INSERT  INTO `beiker_emailtemplate`(`id`,`templatecode`,`templatecontent`,`templatesubject`) VALUES (NULL,'REFUND_TIMEOUT_AUTOEMAIL','{0}\r\n<table width=\"1000\" border=\"1\" align=\"left\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-size:14px;\">\r\n<tr><td>退款明细ID</td><td>交易订单ID</td><td>用户ID</td><td>商品订单ID</td><td>操作人</td><td>交易订单金额</td><td>商品订单金额</td>\r\n<td>退款金额</td><td>支付金额</td><td>交易流水号</td><td>退款请求号</td></tr>\r\n{1}\r\n</table>','退款申请超期邮件提醒');

