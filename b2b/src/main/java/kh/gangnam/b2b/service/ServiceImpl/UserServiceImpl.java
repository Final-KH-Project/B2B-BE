package kh.gangnam.b2b.service.ServiceImpl;

import kh.gangnam.b2b.dto.user.UserDTO;
import kh.gangnam.b2b.entity.auth.User;
import kh.gangnam.b2b.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 프로필 조회
    public UserDTO getUserInfoByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        // User 엔티티를 UserDto로 변환하여 반환
        return UserDTO.fromEntity(user);
    }
    // 패스워드 변경
    public void updatePassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    // 부서 변경
    public void updateDepartment(Long userId, String department) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        user.setDepartment(department);
        userRepository.save(user);
    }
    // 직급 변경
    public void updatePosition(Long userId, String position) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        user.setPosition(position);
        userRepository.save(user);
    }
    // 전화번호 변경
    public void updatePhoneNumber(Long userId, String phoneNumber) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        user.setPhoneNumber(phoneNumber);
        userRepository.save(user);
    }
    // 프로필 이미지 변경
    public void updateProfileImage(Long userId, String profileImageUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        user.setProfile(profileImageUrl);
        userRepository.save(user);
    }
}
