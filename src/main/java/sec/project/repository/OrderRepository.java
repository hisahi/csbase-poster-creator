
package sec.project.repository; 

import org.springframework.data.jpa.repository.JpaRepository;
import sec.project.domain.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    public Order findById(long id);
}
