package com.example.spring03_shop.board.service;

import java.io.File;
import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.spring03_shop.board.dto.BoardDTO;
import com.example.spring03_shop.board.dto.PageDTO;
import com.example.spring03_shop.board.entity.BoardEntity;
import com.example.spring03_shop.board.repository.BoardRepository;

import jakarta.transaction.Transactional;

@Service
public class BoardServiceImp implements BoardService {

	@Autowired
	private BoardRepository boardRepository;
	
	public BoardServiceImp() {
		
	}
	@Transactional
	@Override
	public long countProcess() {
		long cnt = boardRepository.count();
		return cnt;
	}

	@Transactional
	@Override
	public List<BoardDTO> listProcess(PageDTO pv) {
		List<BoardEntity> listBoardEntity = boardRepository.findPagedBoardsByRownum(pv);
		//stream<BoardEntity> => /stream<BoardDTO> => List <BoardDTO> 최종적으로변경
		List<BoardDTO> listBoardDTO = listBoardEntity.stream().map(BoardDTO::toDTO).collect(Collectors.toList());
		return listBoardDTO;
	}
	

	@Transactional
	@Override
	public void insertProcess(BoardDTO dto) {
		long newId = boardRepository.getNextVal(); // 별도의 시퀀스 조회필요!
		dto.setNum(newId);
		
		// 답변이 아니면 reg와 num을 동일하게 설정
		if(dto.getRef() == null || dto.getRef()==0) {
			dto.setRef((int)newId);
			dto.setRef((int)newId);
			dto.setReStep(0);
			dto.setReLevel(0);
		} else {
			int ref = dto.getRef();
			int reStep = dto.getReStep();
			boardRepository.increaseReStep(ref,reStep);
			dto.setReStep(dto.getReStep()+1);
			dto.setReLevel(dto.getReLevel() +1);
		}
				
			dto.setRegDate(new Date(System.currentTimeMillis()));
			dto.setReadCount(0);
			BoardEntity boardEntity = dto.toEntity();
			boardRepository.save(boardEntity);		
					
		}
		

	@Transactional
	@Override
	public BoardDTO contentProcess(long num) {
		boardRepository.increaseReadCount(num);
		BoardEntity boardEntity = boardRepository.findWithMemberNameByNum(num);
		return BoardDTO.toDTO(boardEntity);
	}

	@Transactional
	@Override
	public void updateProcess(BoardDTO dto, String tempDir) {
		String filename = dto.getUpload();
		
		//수정할 첨부파일이 있으면
		if(filename != null) {
			String uploadFilename = boardRepository.getUploadfilename(dto.getNum());
			// 기존에 저장된 파링이 존재하면
			if(uploadFilename != null) {
				File file = new File(tempDir,uploadFilename);
				file.delete();
			}
			
		}
		
		BoardEntity boardEntity = dto.toEntity();
		boardRepository.updateBoard(boardEntity);
		
	}
	
	@Transactional
	@Override
	public void deleteProcess(long num, String tempDir) {
		String  uploadFilename = boardRepository.getUploadfilename(num);
		
		//파일 첨부가 있으면
		if(uploadFilename != null) {
			File file = new File(tempDir, uploadFilename);
			file.delete();
		}
		
		boardRepository.deleteById(num);
	}

			
		
	}
	
	

//=====================================
//그룹      출력순서    출력들여쓰기
//num    ref    re_step   re_level
//1      1         0       0               => 제목글1
//2      2         0       0               => 제목글2
//3      1         4       1               => 제목1  => 답변3
//4      1         1       1               => 제목1  => 답변4
//5      1         2       2               => 제목1  => 답변4  => 답변5
//6      6         0       0               => 제목글3
//7      1         3       3                 => 제목1  => 답변4  => 답변5 => 답변7
//ref DESC,  re_step ASC
//제목6
//제목2
//제목1
//답변4
// 답변5
//    답변7
//답변3

