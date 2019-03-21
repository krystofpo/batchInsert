package cz.krystofample.demolibrary.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "user")
    private List<Loan> loans = new ArrayList<>();

    private boolean hasLoans;

    public boolean hasLoans() {
        return hasLoans;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
