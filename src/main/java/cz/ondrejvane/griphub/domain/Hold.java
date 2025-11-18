package cz.ondrejvane.griphub.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import cz.ondrejvane.griphub.domain.enumeration.Difficulty;
import cz.ondrejvane.griphub.domain.enumeration.HoldType;
import jakarta.persistence.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Hold.
 */
@Entity
@Table(name = "hold")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Hold implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "photo_coordinates_x")
    private Integer photoCoordinatesX;

    @Column(name = "photo_coordinates_y")
    private Integer photoCoordinatesY;

    @Column(name = "jhi_column")
    private Integer column;

    @Column(name = "row")
    private Integer row;

    @Enumerated(EnumType.STRING)
    @Column(name = "hold_type")
    private HoldType holdType;

    @Enumerated(EnumType.STRING)
    @Column(name = "hold_difficulty")
    private Difficulty holdDifficulty;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "holds", "boulders" }, allowSetters = true)
    private Wall wall;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "wall", "holds" }, allowSetters = true)
    private Boulder boulder;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Hold id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPhotoCoordinatesX() {
        return this.photoCoordinatesX;
    }

    public Hold photoCoordinatesX(Integer photoCoordinatesX) {
        this.setPhotoCoordinatesX(photoCoordinatesX);
        return this;
    }

    public void setPhotoCoordinatesX(Integer photoCoordinatesX) {
        this.photoCoordinatesX = photoCoordinatesX;
    }

    public Integer getPhotoCoordinatesY() {
        return this.photoCoordinatesY;
    }

    public Hold photoCoordinatesY(Integer photoCoordinatesY) {
        this.setPhotoCoordinatesY(photoCoordinatesY);
        return this;
    }

    public void setPhotoCoordinatesY(Integer photoCoordinatesY) {
        this.photoCoordinatesY = photoCoordinatesY;
    }

    public Integer getColumn() {
        return this.column;
    }

    public Hold column(Integer column) {
        this.setColumn(column);
        return this;
    }

    public void setColumn(Integer column) {
        this.column = column;
    }

    public Integer getRow() {
        return this.row;
    }

    public Hold row(Integer row) {
        this.setRow(row);
        return this;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

    public HoldType getHoldType() {
        return this.holdType;
    }

    public Hold holdType(HoldType holdType) {
        this.setHoldType(holdType);
        return this;
    }

    public void setHoldType(HoldType holdType) {
        this.holdType = holdType;
    }

    public Difficulty getHoldDifficulty() {
        return this.holdDifficulty;
    }

    public Hold holdDifficulty(Difficulty holdDifficulty) {
        this.setHoldDifficulty(holdDifficulty);
        return this;
    }

    public void setHoldDifficulty(Difficulty holdDifficulty) {
        this.holdDifficulty = holdDifficulty;
    }

    public Wall getWall() {
        return this.wall;
    }

    public void setWall(Wall wall) {
        this.wall = wall;
    }

    public Hold wall(Wall wall) {
        this.setWall(wall);
        return this;
    }

    public Boulder getBoulder() {
        return this.boulder;
    }

    public void setBoulder(Boulder boulder) {
        this.boulder = boulder;
    }

    public Hold boulder(Boulder boulder) {
        this.setBoulder(boulder);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Hold)) {
            return false;
        }
        return getId() != null && getId().equals(((Hold) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Hold{" +
            "id=" + getId() +
            ", photoCoordinatesX=" + getPhotoCoordinatesX() +
            ", photoCoordinatesY=" + getPhotoCoordinatesY() +
            ", column=" + getColumn() +
            ", row=" + getRow() +
            ", holdType='" + getHoldType() + "'" +
            ", holdDifficulty='" + getHoldDifficulty() + "'" +
            "}";
    }
}
