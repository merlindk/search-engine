package com.thebadtouch.searchengine.entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Document {
    private Integer docId;
    private String name;

    @Id
    @Column(name = "doc_id", nullable = false)
    public Integer getDocId() {
        return docId;
    }

    public void setDocId(Integer docId) {
        this.docId = docId;
    }

    @Basic
    @Column(name = "name", nullable = true, length = -1)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Document document = (Document) o;
        return Objects.equals(docId, document.docId) &&
                Objects.equals(name, document.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(docId, name);
    }
}
