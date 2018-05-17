DROP TABLE  IF EXISTS  tbl_goods;
create table tbl_goods (id bigint auto_increment, name varchar(255),price double,type varchar(50),counts int,description varchar(1000));
insert into tbl_goods (name,price,type,counts,description) values ('Iphone 7S',35000,'смартфоны',40,'Лучший подарок вашей девушки. Если вы понимаете о чем я');
insert into tbl_goods (name,price,type,counts,description) values ('Зарядка CHARGE 700',600,'зарядные устройства',100,'Зарядное устройство под разьем Type-C');
insert into tbl_goods (name,price,type,counts,description) values ('Ipad 3 LTE',33000,'планшеты',23,'Планшет с поддержкой симки');
insert into tbl_goods (name,price,type,counts,description) values ('Samsung A5',15000,'смартфоны',44,'Просто Samsung. Не думайте что его ктото оценит');
insert into tbl_goods (name,price,type,counts,description) values ('Kingston FLASH CARD USB3.0 32GB',450,'USB-накопители',44,' Просто флешка');