package kh.gangnam.b2b.service.ServiceImpl;

import kh.gangnam.b2b.dto.UserDTO;
import kh.gangnam.b2b.entity.auth.User;
import kh.gangnam.b2b.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl {

    private final UserRepository userRepository;

    public UserDTO getUserInfoByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }
        // User 엔티티를 UserDto로 변환하여 반환
        return UserDTO.fromEntity(user);
    }
}
