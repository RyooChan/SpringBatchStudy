-- 주문 테이블 생성
CREATE TABLE spring_batch_study.orders (
                                           id INT NOT NULL AUTO_INCREMENT,
                                           order_item VARCHAR(45) NULL,
                                           price INT NULL,
                                           order_date DATE NULL,
                                           PRIMARY KEY (id))
;

-- 정산 테이블 생성
-- 정산일(account_date)만 추가된다.
CREATE TABLE spring_batch_study.accounts(
                                            id INT NOT NULL AUTO_INCREMENT,
                                            order_item VARCHAR(45) NULL,
                                            price INT NULL,
                                            order_date DATE NULL,
                                            account_date DATE NULL,
                                            PRIMARY KEY (id))
;

INSERT INTO spring_batch_study.orders (order_item, price, order_date)
VALUES ( '카카오 선물', 15000, '2022-03-01' );
INSERT INTO spring_batch_study.orders (order_item, price, order_date)
VALUES ( '배달주문', 18000, '2022-03-01' );
INSERT INTO spring_batch_study.orders (order_item, price, order_date)
VALUES ( '교보문고', 14000, '2022-03-02' );
INSERT INTO spring_batch_study.orders (order_item, price, order_date)
VALUES ( '아이스크림', 3800, '2022-03-03' );
INSERT INTO spring_batch_study.orders (order_item, price, order_date)
VALUES ( '치킨', 21000, '2022-03-04' );
INSERT INTO spring_batch_study.orders (order_item, price, order_date)
VALUES ( '커피', 4000, '2022-03-04' );
INSERT INTO spring_batch_study.orders (order_item, price, order_date)
VALUES ( '교보문고', 13800, '2022-03-05' );
INSERT INTO spring_batch_study.orders (order_item, price, order_date)
VALUES ( '카카오 선물', 5500, '2022-03-06' );

SELECT * FROM spring_batch_study.orders;

SELECT * FROM spring_batch_study.accounts;