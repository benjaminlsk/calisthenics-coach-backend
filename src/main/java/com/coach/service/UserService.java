package com.coach.service;

import com.coach.dto.UserDTOs.CreateUserRequest;
import com.coach.dto.UserDTOs.UserResponse;
import com.coach.model.User;
import com.coach.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        User user = User.builder()
                .name(request.name())
                .level(request.level())
                .goals(request.goals())
                .weeklyFrequency(request.weeklyFrequency())
                .equipment(request.equipment())
                .injuries(request.injuries())
                .build();

        User saved = userRepository.save(user);
        log.info("Nouvel athlète créé : {} (id={})", saved.getName(), saved.getId());
        return UserResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Utilisateur introuvable : id=" + userId));
    }

    @Transactional(readOnly = true)
    public UserResponse getUserProfile(Long userId) {
        return UserResponse.from(findById(userId));
    }

    @Transactional
    public UserResponse updateProfile(Long userId, CreateUserRequest request) {
        User user = findById(userId);
        user.setName(request.name());
        user.setLevel(request.level());
        user.setGoals(request.goals());
        user.setWeeklyFrequency(request.weeklyFrequency());
        user.setEquipment(request.equipment());
        user.setInjuries(request.injuries());
        return UserResponse.from(userRepository.save(user));
    }
}
