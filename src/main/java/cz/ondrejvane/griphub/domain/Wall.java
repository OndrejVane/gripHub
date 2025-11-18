package cz.ondrejvane.griphub.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Wall.
 */
@Entity
@Table(name = "wall")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Wall implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @NotNull
    @Min(value = 0)
    @Column(name = "height", nullable = false)
    private Integer height;

    @NotNull
    @Min(value = 0)
    @Column(name = "width", nullable = false)
    private Integer width;

    @NotNull
    @Min(value = 0)
    @Max(value = 90)
    @Column(name = "min_slope", nullable = false)
    private Integer minSlope;

    @NotNull
    @Min(value = 0)
    @Max(value = 90)
    @Column(name = "max_slope", nullable = false)
    private Integer maxSlope;

    @Lob
    @Column(name = "photo", nullable = false)
    private byte[] photo;

    @NotNull
    @Column(name = "photo_content_type", nullable = false)
    private String photoContentType;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Wall id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Wall name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getHeight() {
        return this.height;
    }

    public Wall height(Integer height) {
        this.setHeight(height);
        return this;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWidth() {
        return this.width;
    }

    public Wall width(Integer width) {
        this.setWidth(width);
        return this;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getMinSlope() {
        return this.minSlope;
    }

    public Wall minSlope(Integer minSlope) {
        this.setMinSlope(minSlope);
        return this;
    }

    public void setMinSlope(Integer minSlope) {
        this.minSlope = minSlope;
    }

    public Integer getMaxSlope() {
        return this.maxSlope;
    }

    public Wall maxSlope(Integer maxSlope) {
        this.setMaxSlope(maxSlope);
        return this;
    }

    public void setMaxSlope(Integer maxSlope) {
        this.maxSlope = maxSlope;
    }

    public byte[] getPhoto() {
        return this.photo;
    }

    public Wall photo(byte[] photo) {
        this.setPhoto(photo);
        return this;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public String getPhotoContentType() {
        return this.photoContentType;
    }

    public Wall photoContentType(String photoContentType) {
        this.photoContentType = photoContentType;
        return this;
    }

    public void setPhotoContentType(String photoContentType) {
        this.photoContentType = photoContentType;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Wall)) {
            return false;
        }
        return getId() != null && getId().equals(((Wall) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Wall{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", height=" + getHeight() +
            ", width=" + getWidth() +
            ", minSlope=" + getMinSlope() +
            ", maxSlope=" + getMaxSlope() +
            ", photo='" + getPhoto() + "'" +
            ", photoContentType='" + getPhotoContentType() + "'" +
            "}";
    }
}
