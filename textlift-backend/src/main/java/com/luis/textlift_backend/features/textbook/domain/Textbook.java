package com.luis.textlift_backend.features.textbook.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.luis.textlift_backend.features.annotation.domain.Annotation;
import com.luis.textlift_backend.features.document.domain.Document;
import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "textbook"
)
public class Textbook {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    //A textbook is mapped to many possible documents
    @OneToMany(
            mappedBy = "textbook",
            cascade = CascadeType.ALL
    )
    @JsonManagedReference
    private List<Document> documents;

    //A textbook is mapped to one annotation
    @OneToOne(
            mappedBy = "textbook",
            cascade = CascadeType.ALL
    )
    private Annotation annotation;

    //Textbook-specific fields
    @Column
    private String textbookName;

    @Column
    private String edition;

    @Column
    private String isbn;

    @Column
    private List<String> authors;

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public Annotation getAnnotation() {
        return annotation;
    }

    public void setAnnotation(Annotation annotation) {
        this.annotation = annotation;
    }

    public String getTextbookName() {
        return textbookName;
    }

    public void setTextbookName(String textbookName) {
        this.textbookName = textbookName;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public UUID getId(){
        return this.id;
    }
}