package juliana.auth_forum_service.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import juliana.auth_forum_service.entities.ForumPost;

@Repository
public interface ForumPostRepository extends JpaRepository<ForumPost, Long> {

    List<ForumPost> findAllByOrderByCreatedAtDesc();

    List<ForumPost> findByAuthorIdOrderByCreatedAtDesc(Long authorId);
}
