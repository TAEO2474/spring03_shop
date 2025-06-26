INSERT INTO board 
VALUES(board_num_seq.nextval,'제목1',sysdate,0,board_num_seq.nextval,
0,0,'내용 테스트.......','127.0.0.1','sample.txt', null);

commit;

DESC  board;


ALTER TABLE board
RENAME COLUMN readcount TO  read_count;

SELECT b. * FROM
    (SELECT rownum AS rm,a.*FROM
        (SELECT* FROM board ORDER BY ref DESC, re_step ASC) a)b
WHERE B.rm >= 1 AND b.rm <=3;
	
   
UPDATE board b SET b.re_step =   b.re_step +1
WHERE  b.ref = 1 AND  b.re_step > 3;

SELECT * FROM board ;

DELETE FROM  board ;
COMMIT;
SELECT * FROM board ;

DELETE FROM  board WHERE num=21;
COMMIT;

SELECT department_id,
    CASE WHEN department_id=10 THEN'aaaa'
              WHEN department_id=20 THEN'bbbb'
              ELSE'cccc'
              END AS Other
FROM departments;
