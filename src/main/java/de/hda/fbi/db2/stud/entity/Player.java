package de.hda.fbi.db2.stud.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;


/**
 * Player class.
 */
@Entity
@SequenceGenerator(name = "player_id", allocationSize = 1)
@Table(name = "Player", uniqueConstraints = {@UniqueConstraint(columnNames = {"name"})})
public class Player {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "player_id")
  private int id;
  public int getId(){
    return id;
  }

  private String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @OneToMany(mappedBy = "player")
 private List<Game> games = new ArrayList<>();
  public List<Game> getGames() {
    return games;
  }

}
