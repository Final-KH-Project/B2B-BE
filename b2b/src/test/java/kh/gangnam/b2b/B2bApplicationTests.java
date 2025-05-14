package kh.gangnam.b2b;

import kh.gangnam.b2b.entity.auth.User;
import kh.gangnam.b2b.entity.board.Board;
import kh.gangnam.b2b.entity.board.BoardType;
import kh.gangnam.b2b.repository.board.BoardRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class B2bApplicationTests {
	@Autowired
	BoardRepository repository;
	//@Test
	void dumyBoard() {


		User user=new User();
		user.setUserId(1L);
		for(int i=1; i<=100; i++){
			BoardType boardType = BoardType.useTypeNo(i%4+101);
			Board entity=Board.builder()
					.title("제목"+i)
					.content("내용"+i)
					.type(boardType)
					.author(user)
					.build();
			repository.save(entity);
		}

	}

}
