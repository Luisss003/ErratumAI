package com.luis.textlift_backend.features.annotation.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.luis.textlift_backend.features.textbook.domain.Textbook;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "annotation"
)
public class Annotation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(
            name = "textbook_id",
            unique = true
    )
    private Textbook textbook;

    @OneToMany(
            mappedBy = "annotation",
            cascade = CascadeType.ALL
    )
    @JsonManagedReference
    private List<AnnotationNote> notes;

    @Column
    private Integer version = 0;

    @Column
    @CreatedDate
    private Instant createdAt;

    @Column
    @LastModifiedDate
    private Instant updatedAt;

    public Textbook getTextbook() {
        return textbook;
    }

    public void setTextbook(Textbook textbook) {
        this.textbook = textbook;
    }

    public List<AnnotationNote> getNotes() {
        return notes;
    }

    public void setNotes(List<AnnotationNote> notes) {
        this.notes = notes;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
