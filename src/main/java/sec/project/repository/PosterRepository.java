
package sec.project.repository; 

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import sec.project.domain.Poster;
import sec.project.domain.User;

public interface PosterRepository extends JpaRepository<Poster, Long> {
    public Poster findById(long id);
    public List<Poster> findByIsPublicTrue();
    public List<Poster> findByUserId(User user);
}
