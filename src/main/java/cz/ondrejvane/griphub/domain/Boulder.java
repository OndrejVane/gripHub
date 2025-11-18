package cz.ondrejvane.griphub.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Boulder.
 */
@Entity
@Table(name = "boulder")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Boulder implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Min(value = 0)
    @Column(name = "grade")
    private Integer grade;

    @Column(name = "note")
    private String note;

    @NotNull
    @Min(value = 0)
    @Max(value = 90)
    @Column(name = "slope", nullable = false)
    private Integer slope;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "holds", "boulders" }, allowSetters = true)
    private Wall wall;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "boulder")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "wall", "boulder" }, allowSetters = true)
    private Set<Hold> holds = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Boulder id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Boulder name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getGrade() {
        return this.grade;
    }

    public Boulder grade(Integer grade) {
        this.setGrade(grade);
        return this;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public String getNote() {
        return this.note;
    }

    public Boulder note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getSlope() {
        return this.slope;
    }

    public Boulder slope(Integer slope) {
        this.setSlope(slope);
        return this;
    }

    public void setSlope(Integer slope) {
        this.slope = slope;
    }

    public Wall getWall() {
        return this.wall;
    }

    public void setWall(Wall wall) {
        this.wall = wall;
    }

    public Boulder wall(Wall wall) {
        this.setWall(wall);
        return this;
    }

    public Set<Hold> getHolds() {
        return this.holds;
    }

    public void setHolds(Set<Hold> holds) {
        if (this.holds != null) {
            this.holds.forEach(i -> i.setBoulder(null));
        }
        if (holds != null) {
            holds.forEach(i -> i.setBoulder(this));
        }
        this.holds = holds;
    }

    public Boulder holds(Set<Hold> holds) {
        this.setHolds(holds);
        return this;
    }

    public Boulder addHold(Hold hold) {
        this.holds.add(hold);
        hold.setBoulder(this);
        return this;
    }

    public Boulder removeHold(Hold hold) {
        this.holds.remove(hold);
        hold.setBoulder(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Boulder)) {
            return false;
        }
        return getId() != null && getId().equals(((Boulder) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Boulder{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", grade=" + getGrade() +
            ", note='" + getNote() + "'" +
            ", slope=" + getSlope() +
            "}";
    }
}
