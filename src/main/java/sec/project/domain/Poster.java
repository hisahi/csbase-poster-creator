
package sec.project.domain; 

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
public class Poster extends AbstractPersistable<Long> {

    @Id
    private Long id;
    @Lob
    private byte[] image;
    private String title;
    private String description;
    private boolean isPublic;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId")
    private User userId;
    
    public Poster() {
        super();
    }
    
    public Poster(byte[] image, String title, String description, User userId, boolean isPublic) {
        this.image = image;
        this.title = title;
        this.description = description;
        this.userId = userId;
        this.isPublic = isPublic;
    }

    public Long getId() {
        return id;
    }
    
    public byte[] getImage() {
        return image;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public User getUser() {
        return userId;
    }
    
    public boolean isPublic() {
        return isPublic;
    }
    
}
