UPDATE beiker_goods bg,beiker_catlog_good bcg SET bcg.discountrate=bg.discount WHERE bg.goodsid=bcg.goodid;

ALTER TABLE `beiker_user`	ADD COLUMN `user_ip` VARCHAR(50) NOT NULL DEFAULT '';

-- 苏州开通酒店旅游频道
INSERT INTO beiker_catlog_relation (catlogid, catlogextid, areaid, catlogisavailable, catlogextisavaliable, createtime) VALUES (10500, 0, 180, 1, 1, NOW());
INSERT INTO beiker_catlog_relation (catlogid, catlogextid, areaid, catlogisavailable, catlogextisavaliable, createtime) VALUES (10500, 10501, 180, 1, 1, NOW());
INSERT INTO beiker_catlog_relation (catlogid, catlogextid, areaid, catlogisavailable, catlogextisavaliable, createtime) VALUES (10500, 10502, 180, 1, 1, NOW());
INSERT INTO beiker_catlog_relation (catlogid, catlogextid, areaid, catlogisavailable, catlogextisavaliable, createtime) VALUES (10500, 10503, 180, 1, 1, NOW());
INSERT INTO beiker_catlog_relation (catlogid, catlogextid, areaid, catlogisavailable, catlogextisavaliable, createtime) VALUES (10500, 10504, 180, 1, 1, NOW());
INSERT INTO beiker_catlog_relation (catlogid, catlogextid, areaid, catlogisavailable, catlogextisavaliable, createtime) VALUES (10500, 10505, 180, 1, 1, NOW());
INSERT INTO beiker_catlog_relation (catlogid, catlogextid, areaid, catlogisavailable, catlogextisavaliable, createtime) VALUES (10500, 10506, 180, 1, 1, NOW());
INSERT INTO beiker_catlog_relation (catlogid, catlogextid, areaid, catlogisavailable, catlogextisavaliable, createtime) VALUES (10500, 10507, 180, 1, 1, NOW());
INSERT INTO beiker_catlog_relation (catlogid, catlogextid, areaid, catlogisavailable, catlogextisavaliable, createtime) VALUES (10500, 10508, 180, 1, 1, NOW());
INSERT INTO beiker_catlog_relation (catlogid, catlogextid, areaid, catlogisavailable, catlogextisavaliable, createtime) VALUES (10500, 10509, 180, 1, 1, NOW());
INSERT INTO beiker_catlog_relation (catlogid, catlogextid, areaid, catlogisavailable, catlogextisavaliable, createtime) VALUES (10500, 10510, 180, 1, 1, NOW());
INSERT INTO beiker_catlog_relation (catlogid, catlogextid, areaid, catlogisavailable, catlogextisavaliable, createtime) VALUES (10500, 10511, 180, 1, 1, NOW());
INSERT INTO beiker_catlog_relation (catlogid, catlogextid, areaid, catlogisavailable, catlogextisavaliable, createtime) VALUES (10500, 10512, 180, 1, 1, NOW());
INSERT INTO beiker_catlog_relation (catlogid, catlogextid, areaid, catlogisavailable, catlogextisavaliable, createtime) VALUES (10500, 10513, 180, 1, 1, NOW());

-- 南昌开通酒店旅游频道
INSERT INTO beiker_catlog_relation (catlogid, catlogextid, areaid, catlogisavailable, catlogextisavaliable, createtime) VALUES (10500, 0, 226, 1, 1, NOW());
INSERT INTO beiker_catlog_relation (catlogid, catlogextid, areaid, catlogisavailable, catlogextisavaliable, createtime) VALUES (10500, 10501, 226, 1, 1, NOW());
INSERT INTO beiker_catlog_relation (catlogid, catlogextid, areaid, catlogisavailable, catlogextisavaliable, createtime) VALUES (10500, 10502, 226, 1, 1, NOW());
INSERT INTO beiker_catlog_relation (catlogid, catlogextid, areaid, catlogisavailable, catlogextisavaliable, createtime) VALUES (10500, 10503, 226, 1, 1, NOW());
INSERT INTO beiker_catlog_relation (catlogid, catlogextid, areaid, catlogisavailable, catlogextisavaliable, createtime) VALUES (10500, 10504, 226, 1, 1, NOW());
INSERT INTO beiker_catlog_relation (catlogid, catlogextid, areaid, catlogisavailable, catlogextisavaliable, createtime) VALUES (10500, 10505, 226, 1, 1, NOW());
INSERT INTO beiker_catlog_relation (catlogid, catlogextid, areaid, catlogisavailable, catlogextisavaliable, createtime) VALUES (10500, 10506, 226, 1, 1, NOW());
INSERT INTO beiker_catlog_relation (catlogid, catlogextid, areaid, catlogisavailable, catlogextisavaliable, createtime) VALUES (10500, 10507, 226, 1, 1, NOW());
INSERT INTO beiker_catlog_relation (catlogid, catlogextid, areaid, catlogisavailable, catlogextisavaliable, createtime) VALUES (10500, 10508, 226, 1, 1, NOW());
INSERT INTO beiker_catlog_relation (catlogid, catlogextid, areaid, catlogisavailable, catlogextisavaliable, createtime) VALUES (10500, 10509, 226, 1, 1, NOW());
INSERT INTO beiker_catlog_relation (catlogid, catlogextid, areaid, catlogisavailable, catlogextisavaliable, createtime) VALUES (10500, 10510, 226, 1, 1, NOW());
INSERT INTO beiker_catlog_relation (catlogid, catlogextid, areaid, catlogisavailable, catlogextisavaliable, createtime) VALUES (10500, 10511, 226, 1, 1, NOW());
INSERT INTO beiker_catlog_relation (catlogid, catlogextid, areaid, catlogisavailable, catlogextisavaliable, createtime) VALUES (10500, 10512, 226, 1, 1, NOW());
INSERT INTO beiker_catlog_relation (catlogid, catlogextid, areaid, catlogisavailable, catlogextisavaliable, createtime) VALUES (10500, 10513, 226, 1, 1, NOW());

-- 苏州商品分类调整
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10509 WHERE goodid = 63146 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10509 WHERE goodid = 63149 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10513 WHERE goodid = 63894 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10507 WHERE goodid = 64293 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10507 WHERE goodid = 64294 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 64296 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10507 WHERE goodid = 64907 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10507 WHERE goodid = 64910 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 65355 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 65356 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10513 WHERE goodid = 65405 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10507 WHERE goodid = 65564 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10507 WHERE goodid = 65565 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10507 WHERE goodid = 65567 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10507 WHERE goodid = 65570 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10507 WHERE goodid = 65574 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10507 WHERE goodid = 65577 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 65746 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10513 WHERE goodid = 65762 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10513 WHERE goodid = 65765 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 65773 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10507 WHERE goodid = 65801 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 65806 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10513 WHERE goodid = 65900 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 66316 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10513 WHERE goodid = 66395 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10513 WHERE goodid = 66396 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10509 WHERE goodid = 66735 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10509 WHERE goodid = 66740 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10513 WHERE goodid = 66852 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10513 WHERE goodid = 66853 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 66891 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 66895 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10513 WHERE goodid = 67417 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10513 WHERE goodid = 67834 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10507 WHERE goodid = 67865 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 69099 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10507 WHERE goodid = 69884 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 70303 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 70305 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10513 WHERE goodid = 70499 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10513 WHERE goodid = 70755 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10513 WHERE goodid = 70758 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10507 WHERE goodid = 70904 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10507 WHERE goodid = 70909 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10513 WHERE goodid = 71183 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10513 WHERE goodid = 71349 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10513 WHERE goodid = 71351 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10507 WHERE goodid = 71481 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 71983 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 71984 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 72277 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 72300 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10507 WHERE goodid = 72500 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10507 WHERE goodid = 72502 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 72521 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 72522 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10513 WHERE goodid = 73917 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10513 WHERE goodid = 73920 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10513 WHERE goodid = 73922 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10507 WHERE goodid = 74138 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10507 WHERE goodid = 74142 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 75439 AND tagid = 10400 AND tagextid = 10407 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10504 WHERE goodid = 63368 AND tagid = 10200 AND tagextid = 10208 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10504 WHERE goodid = 64498 AND tagid = 10200 AND tagextid = 10208 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10504 WHERE goodid = 64501 AND tagid = 10200 AND tagextid = 10208 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10504 WHERE goodid = 64503 AND tagid = 10200 AND tagextid = 10208 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10504 WHERE goodid = 64505 AND tagid = 10200 AND tagextid = 10208 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10504 WHERE goodid = 64506 AND tagid = 10200 AND tagextid = 10208 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10504 WHERE goodid = 64507 AND tagid = 10200 AND tagextid = 10208 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10504 WHERE goodid = 64509 AND tagid = 10200 AND tagextid = 10208 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10506 WHERE goodid = 64511 AND tagid = 10200 AND tagextid = 10208 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10506 WHERE goodid = 64514 AND tagid = 10200 AND tagextid = 10208 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10506 WHERE goodid = 65110 AND tagid = 10200 AND tagextid = 10208 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10506 WHERE goodid = 65112 AND tagid = 10200 AND tagextid = 10208 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10506 WHERE goodid = 65832 AND tagid = 10200 AND tagextid = 10208 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10506 WHERE goodid = 65835 AND tagid = 10200 AND tagextid = 10208 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10506 WHERE goodid = 65836 AND tagid = 10200 AND tagextid = 10208 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10506 WHERE goodid = 65839 AND tagid = 10200 AND tagextid = 10208 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10506 WHERE goodid = 66926 AND tagid = 10200 AND tagextid = 10208 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10506 WHERE goodid = 70918 AND tagid = 10200 AND tagextid = 10208 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10506 WHERE goodid = 70919 AND tagid = 10200 AND tagextid = 10208 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10506 WHERE goodid = 74057 AND tagid = 10200 AND tagextid = 10208 AND area_id = 180;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10506 WHERE goodid = 74058 AND tagid = 10200 AND tagextid = 10208 AND area_id = 180;

-- 南昌商品分类调整
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 44633 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 52182 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 52218 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10513 WHERE goodid = 62207 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10513 WHERE goodid = 62208 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10513 WHERE goodid = 62209 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10513 WHERE goodid = 62210 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10509 WHERE goodid = 66337 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10509 WHERE goodid = 66339 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10513 WHERE goodid = 66364 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10509 WHERE goodid = 68270 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 70083 AND tagid = 10500 AND tagextid = 10502 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 70236 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10513 WHERE goodid = 60332 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 60924 AND tagid = 10500 AND tagextid = 10502 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 57364 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 48603 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 57204 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 57139 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 70072 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 57178 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 62523 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 57365 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 50059 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 47995 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 50061 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 74862 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 47999 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 50060 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 57182 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 69706 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 69714 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 74849 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 74858 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 74909 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 58509 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 58510 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 60877 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 62896 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 70244 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 70246 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 34027 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 34030 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 34570 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 35060 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 52300 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10513 WHERE goodid = 56435 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10513 WHERE goodid = 56436 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10513 WHERE goodid = 58864 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 61297 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 62028 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 62898 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 63419 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 63420 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 63422 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 63423 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 63425 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 64049 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 55910 AND tagid = 10500 AND tagextid = 10502 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 55911 AND tagid = 10500 AND tagextid = 10502 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 46075 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 46076 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 46535 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 47088 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 47617 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 47619 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 48493 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 48494 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 49941 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 61359 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10509 WHERE goodid = 68839 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 68840 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10508 WHERE goodid = 70250 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10513 WHERE goodid = 65637 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10513 WHERE goodid = 65638 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10507 WHERE goodid = 66814 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10507 WHERE goodid = 66815 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;
UPDATE beiker_catlog_good SET tagid = 10500,tagextid = 10513 WHERE goodid = 72479 AND tagid = 10400 AND tagextid = 10407 AND area_id = 226;

-- 修改过期时间短信模版
INSERT INTO beiker_smstemplate(smstitle, smstype, smscontent)
VALUES (
          'SMS_UPDATELOSEDATE_DELAY',
          'SMS_UPDATELOSEDATE_DELAY',
          '您订购的“{0}”,订单号{1},服务密码{2},有效期延长至{3},敬请关注【千品网】') ;
          
          
INSERT INTO beiker_smstemplate(smstitle, smstype, smscontent)
VALUES (
          'SMS_UPDATELOSEDATE_ADVANCE',
          'SMS_UPDATELOSEDATE_ADVANCE',
          '您订购的“{0}”,订单号{1},服务密码{2},有效期提前至{3},敬请关注【千品网】') ;  

