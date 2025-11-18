package cz.ondrejvane.griphub.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Climb.
 */
@Entity
@Table(name = "climb")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Climb implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Min(value = 0)
    @Column(name = "attempts")
    private Integer attempts;

    @Column(name = "top_date")
    private Instant topDate;

    @Min(value = 1)
    @Max(value = 5)
    @Column(name = "rate")
    private Integer rate;

    @Column(name = "note")
    private String note;

    @Column(name = "is_top")
    private Boolean isTop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "walls", "holds", "climbs" }, allowSetters = true)
    private Boulder boulder;

    @ManyToOne(optional = false)
    @NotNull
    private User climbedBy;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Climb id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAttempts() {
        return this.attempts;
    }

    public Climb attempts(Integer attempts) {
        this.setAttempts(attempts);
        return this;
    }

    public void setAttempts(Integer attempts) {
        this.attempts = attempts;
    }

    public Instant getTopDate() {
        return this.topDate;
    }

    public Climb topDate(Instant topDate) {
        this.setTopDate(topDate);
        return this;
    }

    public void setTopDate(Instant topDate) {
        this.topDate = topDate;
    }

    public Integer getRate() {
        return this.rate;
    }

    public Climb rate(Integer rate) {
        this.setRate(rate);
        return this;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }

    public String getNote() {
        return this.note;
    }

    public Climb note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Boolean getIsTop() {
        return this.isTop;
    }

    public Climb isTop(Boolean isTop) {
        this.setIsTop(isTop);
        return this;
    }

    public void setIsTop(Boolean isTop) {
        this.isTop = isTop;
    }

    public Boulder getBoulder() {
        return this.boulder;
    }

    public void setBoulder(Boulder boulder) {
        this.boulder = boulder;
    }

    public Climb boulder(Boulder boulder) {
        this.setBoulder(boulder);
        return this;
    }

    public User getClimbedBy() {
        return this.climbedBy;
    }

    public void setClimbedBy(User user) {
        this.climbedBy = user;
    }

    public Climb climbedBy(User user) {
        this.setClimbedBy(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Climb)) {
            return false;
        }
        return getId() != null && getId().equals(((Climb) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Climb{" +
            "id=" + getId() +
            ", attempts=" + getAttempts() +
            ", topDate='" + getTopDate() + "'" +
            ", rate=" + getRate() +
            ", note='" + getNote() + "'" +
            ", isTop='" + getIsTop() + "'" +
            "}";
    }
}
