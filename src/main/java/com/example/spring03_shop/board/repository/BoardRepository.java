package com.example.spring03_shop.board.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.spring03_shop.board.dto.PageDTO;
import com.example.spring03_shop.board.entity.BoardEntity;

@Repository
public interface BoardRepository extends JpaRepository<BoardEntity, Long> {

	@Query(value = """
			SELECT b.* FROM
              (SELECT rownum AS rm, a.*  FROM
                  (SELECT * FROM board ORDER BY ref DESC ,  re_step ASC)a)b
            WHERE  b.rm >= :#{#pv.startRow} AND b.rm <= :#{#pv.endRow}
			""", nativeQuery = true)
	List<BoardEntity> findPagedBoardsByRownum(@Param("pv") PageDTO pv);

	// 시퀀스 증가
	//Native SQL 쿼리
	@Query(value = "SELECT board_num_seq.NEXTVAL FROM dual", nativeQuery = true)
	long getNextVal();
		
	// 답글 순서 조정 (re_step증가시키는 작업)
	// Spring Data JPA에서 사용하는 방법 중 하나인 JPQL + @Query 방식
	@Modifying
	@Query(value="UPDATE BoardEntity b SET b.reStep = b.reStep +1 WHERE  b.ref = ref AND  b.reStep > reStep")
	void increaseReStep(@Param("ref")int ref, @Param("reStep") int restep);

	//조회수 증가
	@Modifying
	@Query(value="UPDATE BoardEntity b SET b.readCount = b.readCount+1 WHERE b.num = :num")
	void increaseReadCount(@Param("num") long num);
	
	//게시글 상세보기
	@Query(value ="SELECT b FROM BoardEntity b WHERE b.num = :num")
	BoardEntity findWithMemberNameByNum(@Param("num")long num);
	
	
	//게시글 수정
	@Modifying
	@Query("""
			UPDATE BoardEntity b
			SET b.subject = :#{#boardEntity.subject},
				b.content = :#{#boardEntity.content},
				b.upload = CASE WHEN :#{#boardEntity.upload} IS NOT NULL THEN :#{#boardEntity.upload} ELSE b.upload END
				WHERE b.num = :#{#boardEntity.num}
			""")
	void updateBoard(@Param("boardEntity") BoardEntity boardEntity);
	
	// 파일첨부 가져오기
	@Query(value ="SELECT b.upload FROM BoardEntity b WHERE b.num = :num")
	String getUploadfilename(@Param("num")long num);
}
