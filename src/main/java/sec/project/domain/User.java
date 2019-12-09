package sec.project.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
public class User extends AbstractPersistable<Long> {

    @Id
    private Long id;
    private String username;
    private String password;
    private String fullname;
    private String email;
    private String address;
    private String cardNumber;
    private String cvv;
    private String country;
    private String expiry;
    
    @OneToMany(mappedBy = "userId", fetch = FetchType.LAZY)
    private List<Poster> posters = new ArrayList<Poster>();

    public User() {
        super();
    }

    public User(String username, String password, String fullname, String email) {
        this();
        this.username = username;
        this.password = password;
        this.fullname = fullname;
        this.email = email;
        this.address = "";
        this.cardNumber = "";
        this.cvv = "";
        this.country = "";
        this.expiry = "";
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

}
