package dev.umairalishah.ctsauth.repository;

import dev.umairalishah.ctsauth.model.Role;
import dev.umairalishah.ctsauth.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmail_returnsUser_whenPresent() {
        userRepository.save(new User("Umair Ali Shah", "umair@example.com", "hashed-password", Role.ADMIN));

        Optional<User> found = userRepository.findByEmail("umair@example.com");

        assertThat(found).isPresent();
        assertThat(found.get().getFullName()).isEqualTo("Umair Ali Shah");
    }

    @Test
    void existsByEmail_reflectsSavedState() {
        assertThat(userRepository.existsByEmail("new@example.com")).isFalse();

        userRepository.save(new User("New User", "new@example.com", "hashed-password", Role.CONTRIBUTOR));

        assertThat(userRepository.existsByEmail("new@example.com")).isTrue();
    }

    @Test
    void createdAt_isPopulatedOnSave() {
        User saved = userRepository.save(new User("Reviewer", "reviewer@example.com", "hashed-password", Role.REVIEWER));

        assertThat(saved.getCreatedAt()).isNotNull();
    }
}
