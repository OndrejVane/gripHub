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

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Min(value = 0)
    @Column(name = "grade", nullable = false)
    private Integer grade;

    @Column(name = "note")
    private String note;

    @NotNull
    @Min(value = 0)
    @Max(value = 90)
    @Column(name = "slope", nullable = false)
    private Integer slope;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_boulder__wall",
        joinColumns = @JoinColumn(name = "boulder_id"),
        inverseJoinColumns = @JoinColumn(name = "wall_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "holds", "boulders" }, allowSetters = true)
    private Set<Wall> walls = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "boulder")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "wall", "boulder" }, allowSetters = true)
    private Set<Hold> holds = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "boulder")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "boulder", "climbedBy" }, allowSetters = true)
    private Set<Climb> climbs = new HashSet<>();

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

    public Set<Wall> getWalls() {
        return this.walls;
    }

    public void setWalls(Set<Wall> walls) {
        this.walls = walls;
    }

    public Boulder walls(Set<Wall> walls) {
        this.setWalls(walls);
        return this;
    }

    public Boulder addWall(Wall wall) {
        this.walls.add(wall);
        return this;
    }

    public Boulder removeWall(Wall wall) {
        this.walls.remove(wall);
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

    public Set<Climb> getClimbs() {
        return this.climbs;
    }

    public void setClimbs(Set<Climb> climbs) {
        if (this.climbs != null) {
            this.climbs.forEach(i -> i.setBoulder(null));
        }
        if (climbs != null) {
            climbs.forEach(i -> i.setBoulder(this));
        }
        this.climbs = climbs;
    }

    public Boulder climbs(Set<Climb> climbs) {
        this.setClimbs(climbs);
        return this;
    }

    public Boulder addClimb(Climb climb) {
        this.climbs.add(climb);
        climb.setBoulder(this);
        return this;
    }

    public Boulder removeClimb(Climb climb) {
        this.climbs.remove(climb);
        climb.setBoulder(null);
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
