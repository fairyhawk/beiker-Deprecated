-- 合肥增加酒店旅游分类

INSERT INTO `beiker_catlog_relation` (`catlogid`, `catlogextid`, `areaid`, `catlogisavailable`, `catlogextisavaliable`, `createtime`) VALUES (10500, 0, 200, 1, 1, NOW());
INSERT INTO `beiker_catlog_relation` (`catlogid`, `catlogextid`, `areaid`, `catlogisavailable`, `catlogextisavaliable`, `createtime`) VALUES (10500, 10501, 200, 1, 1, NOW());
INSERT INTO `beiker_catlog_relation` (`catlogid`, `catlogextid`, `areaid`, `catlogisavailable`, `catlogextisavaliable`, `createtime`) VALUES (10500, 10502, 200, 1, 1, NOW());
INSERT INTO `beiker_catlog_relation` (`catlogid`, `catlogextid`, `areaid`, `catlogisavailable`, `catlogextisavaliable`, `createtime`) VALUES (10500, 10503, 200, 1, 1, NOW());
INSERT INTO `beiker_catlog_relation` (`catlogid`, `catlogextid`, `areaid`, `catlogisavailable`, `catlogextisavaliable`, `createtime`) VALUES (10500, 10504, 200, 1, 1, NOW());
INSERT INTO `beiker_catlog_relation` (`catlogid`, `catlogextid`, `areaid`, `catlogisavailable`, `catlogextisavaliable`, `createtime`) VALUES (10500, 10505, 200, 1, 1, NOW());
INSERT INTO `beiker_catlog_relation` (`catlogid`, `catlogextid`, `areaid`, `catlogisavailable`, `catlogextisavaliable`, `createtime`) VALUES (10500, 10506, 200, 1, 1, NOW());
INSERT INTO `beiker_catlog_relation` (`catlogid`, `catlogextid`, `areaid`, `catlogisavailable`, `catlogextisavaliable`, `createtime`) VALUES (10500, 10507, 200, 1, 1, NOW());
INSERT INTO `beiker_catlog_relation` (`catlogid`, `catlogextid`, `areaid`, `catlogisavailable`, `catlogextisavaliable`, `createtime`) VALUES (10500, 10508, 200, 1, 1, NOW());
INSERT INTO `beiker_catlog_relation` (`catlogid`, `catlogextid`, `areaid`, `catlogisavailable`, `catlogextisavaliable`, `createtime`) VALUES (10500, 10509, 200, 1, 1, NOW());
INSERT INTO `beiker_catlog_relation` (`catlogid`, `catlogextid`, `areaid`, `catlogisavailable`, `catlogextisavaliable`, `createtime`) VALUES (10500, 10510, 200, 1, 1, NOW());
INSERT INTO `beiker_catlog_relation` (`catlogid`, `catlogextid`, `areaid`, `catlogisavailable`, `catlogextisavaliable`, `createtime`) VALUES (10500, 10511, 200, 1, 1, NOW());
INSERT INTO `beiker_catlog_relation` (`catlogid`, `catlogextid`, `areaid`, `catlogisavailable`, `catlogextisavaliable`, `createtime`) VALUES (10500, 10512, 200, 1, 1, NOW());
INSERT INTO `beiker_catlog_relation` (`catlogid`, `catlogextid`, `areaid`, `catlogisavailable`, `catlogextisavaliable`, `createtime`) VALUES (10500, 10513, 200, 1, 1, NOW());


-- 现有商品归属到酒店旅游分类

UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10508 WHERE goodid = 18192 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10506 WHERE goodid = 28842 AND tagid = 10200 AND tagextid = 10208 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10508 WHERE goodid = 29734 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10508 WHERE goodid = 29736 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10509 WHERE goodid = 29845 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10509 WHERE goodid = 29847 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10509 WHERE goodid = 29848 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10509 WHERE goodid = 29850 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10509 WHERE goodid = 29852 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10509 WHERE goodid = 32900 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10509 WHERE goodid = 32921 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10509 WHERE goodid = 32922 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10509 WHERE goodid = 35838 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10509 WHERE goodid = 35841 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10509 WHERE goodid = 35842 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10508 WHERE goodid = 36261 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10508 WHERE goodid = 36262 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10506 WHERE goodid = 36324 AND tagid = 10200 AND tagextid = 10208 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10511 WHERE goodid = 36410 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10506 WHERE goodid = 37542 AND tagid = 10200 AND tagextid = 10208 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10512 WHERE goodid = 39371 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10513 WHERE goodid = 39724 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10513 WHERE goodid = 39728 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10513 WHERE goodid = 39731 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10508 WHERE goodid = 39822 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10511 WHERE goodid = 39831 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10508 WHERE goodid = 41255 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10508 WHERE goodid = 41257 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10512 WHERE goodid = 41833 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10508 WHERE goodid = 43225 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10508 WHERE goodid = 43226 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10508 WHERE goodid = 43918 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10508 WHERE goodid = 43919 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10508 WHERE goodid = 43920 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10508 WHERE goodid = 44267 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10508 WHERE goodid = 44268 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10508 WHERE goodid = 44803 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10508 WHERE goodid = 44804 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10508 WHERE goodid = 44806 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10508 WHERE goodid = 44868 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10508 WHERE goodid = 44869 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10508 WHERE goodid = 44870 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10511 WHERE goodid = 45668 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10506 WHERE goodid = 46369 AND tagid = 10200 AND tagextid = 10208 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10506 WHERE goodid = 46370 AND tagid = 10200 AND tagextid = 10208 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10508 WHERE goodid = 46957 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10508 WHERE goodid = 46960 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10508 WHERE goodid = 47202 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10508 WHERE goodid = 47203 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10508 WHERE goodid = 47204 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10508 WHERE goodid = 47206 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10508 WHERE goodid = 47208 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10508 WHERE goodid = 47507 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10508 WHERE goodid = 47509 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10508 WHERE goodid = 47513 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10513 WHERE goodid = 47771 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10513 WHERE goodid = 47772 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10508 WHERE goodid = 47800 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10508 WHERE goodid = 47801 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10508 WHERE goodid = 47805 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10508 WHERE goodid = 47807 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10504 WHERE goodid = 48005 AND tagid = 10200 AND tagextid = 10208 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10508 WHERE goodid = 48816 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10508 WHERE goodid = 48818 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10504 WHERE goodid = 48823 AND tagid = 10200 AND tagextid = 10208 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10513 WHERE goodid = 48983 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10508 WHERE goodid = 49178 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10508 WHERE goodid = 49179 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10508 WHERE goodid = 49180 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10508 WHERE goodid = 49181 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10504 WHERE goodid = 51093 AND tagid = 10500 AND tagextid = 10208 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10504 WHERE goodid = 51159 AND tagid = 10200 AND tagextid = 10208 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10504 WHERE goodid = 51160 AND tagid = 10200 AND tagextid = 10208 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10509 WHERE goodid = 51377 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10509 WHERE goodid = 51378 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10509 WHERE goodid = 51379 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10504 WHERE goodid = 51596 AND tagid = 10200 AND tagextid = 10208 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10504 WHERE goodid = 51597 AND tagid = 10200 AND tagextid = 10208 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10504 WHERE goodid = 51604 AND tagid = 10200 AND tagextid = 10208 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10508 WHERE goodid = 52106 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10508 WHERE goodid = 52107 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10506 WHERE goodid = 52503 AND tagid = 10200 AND tagextid = 10208 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10504 WHERE goodid = 52504 AND tagid = 10200 AND tagextid = 10208 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10504 WHERE goodid = 52679 AND tagid = 10200 AND tagextid = 10208 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10504 WHERE goodid = 52682 AND tagid = 10200 AND tagextid = 10208 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10504 WHERE goodid = 52684 AND tagid = 10200 AND tagextid = 10208 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10504 WHERE goodid = 52687 AND tagid = 10200 AND tagextid = 10208 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10504 WHERE goodid = 52690 AND tagid = 10200 AND tagextid = 10208 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10508 WHERE goodid = 52733 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10508 WHERE goodid = 52737 AND tagid = 10400 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10508 WHERE goodid = 53064 AND tagid = 10500 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10508 WHERE goodid = 53191 AND tagid = 10500 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10508 WHERE goodid = 53192 AND tagid = 10500 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10508 WHERE goodid = 53193 AND tagid = 10500 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10508 WHERE goodid = 53303 AND tagid = 10500 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10508 WHERE goodid = 53304 AND tagid = 10500 AND tagextid = 10407 ;
UPDATE beiker_catlog_good SET tagid = 10500 ,tagextid = 10504 WHERE goodid = 54574 AND tagid = 10200 AND tagextid = 10208 ;







