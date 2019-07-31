package de.hda.fbi.db2.stud.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;


/**
 * Category class.
 */
@Entity
@SequenceGenerator(name = "cat_id", allocationSize = 1)
@Table(name = "Category", uniqueConstraints = {@UniqueConstraint(columnNames = {"name"})})
public class Category {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cat_id")
  private int id;
  private String name;

  public Category() {
  }//default constructor

  @OneToMany(mappedBy = "category", fetch = FetchType.EAGER)
  private List<Question> collectionQuestions = new ArrayList<>();

  public List<Question> getCollectionQuestions() {
    return collectionQuestions;
  }

  public void setName(String n) {
    name = n;
  }

  public String getName() {
    return name;
  }

  public void addQuestion(Question q) {
    collectionQuestions.add(q);
  }

  public int getId() {
    return id;
  }

  public String toString() {
    return this.getId() + ". " + this.getName();
  }

}
