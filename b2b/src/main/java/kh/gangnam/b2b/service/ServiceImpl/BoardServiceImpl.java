package kh.gangnam.b2b.service.ServiceImpl;

import jakarta.persistence.EntityNotFoundException;
import kh.gangnam.b2b.dto.board.BoardDTO;
import kh.gangnam.b2b.dto.board.request.*;
import kh.gangnam.b2b.entity.board.*;
import kh.gangnam.b2b.repository.board.*;
import kh.gangnam.b2b.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BoardServiceImpl implements BoardService {

    private final BoardRepository repository;

        // Board 서비스 비즈니스 로직 구현



    @Override
    public BoardSaveResponse saveBoard(SaveRequest saveRequest) {

        Board result=repository.save(saveRequest.toEntity());
        System.out.println("[][][]:"+result);
        System.out.println("userId:"+result.getAuthor().getUserId());
        System.out.println("username:"+result.getAuthor().getUsername());
        return BoardSaveResponse.fromEntity(result);
        //return BoardResponse.fromEntity(repository.save(saveRequest.toEntity()));
    }

    @Override
    public List<BoardResponse> getList(int type, int page, int size) {
        BoardType boardType = BoardType.useTypeNo(type);
        /*
        List<BoardResponse> result=repository.findAllByType(boardType).stream()
                .map(BoardResponse::fromEntity)
                .toList();

         */

        Sort sort=Sort.by(Sort.Direction.DESC, "boardId");
        Pageable pageable= PageRequest.of(page-1,size, sort);

        return repository.findAllByType(boardType,pageable).stream()
                .map(BoardResponse::fromEntity)
                .toList();
    }



    @Override
    public BoardResponse get(int type, Long boardId) {
        return repository.findById(boardId)
                .map(BoardResponse::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 게시글입니다."));
    }

    @Transactional
    @Override
    public BoardResponse update(int type, Long boardId, UpdateRequest request) {
        BoardType boardType = BoardType.useTypeNo(type);

        Board board = repository.findById(boardId).orElseThrow()
                .update(request);

        //return BoardResponse.fromEntity(board);
        return BoardResponse.fromEntity(board);

    }

    @Override
    public ResponseEntity<String> deleteBoard(String type, Long id) {

        return null;
    }



}
