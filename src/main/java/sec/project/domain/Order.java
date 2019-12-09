
package sec.project.domain; 

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "PosterOrder")
public class Order extends AbstractPersistable<Long> {

    @Id
    private Long id;
    private int quantity;
    @Enumerated(EnumType.STRING)
    private PosterSize posterSize;
    private String address;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId")
    private User user;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "posterId")
    private Poster poster;
    
    public Order() {
        super();
    }
    
    public Order(Poster poster, User user, PosterSize size, int quantity, String address) {
        this.poster = poster;
        this.user = user;
        this.posterSize = size;
        this.quantity = quantity;
        this.address = address;
    }

    public Long getId() {
        return id;
    }
    
    public enum PosterSize {
        NORMAL
    }
}
