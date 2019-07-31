package de.hda.fbi.db2.stud.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;

import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * Game class.
 */
@Entity
@SequenceGenerator(name = "game_id", allocationSize = 1)
@Table(name = "Game")
public class Game {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "game_id")
  private int id;

  @Temporal(TemporalType.TIMESTAMP)
  private Date timeStampStart;

  public void setTimeStampStart(Date d) {
    this.timeStampStart = new Date(d.getTime());
    //timeStampStart = d;
  }

  @Temporal(TemporalType.TIMESTAMP)
  private Date timeStampEnd;

  public void setTimeStampEnd(Date d) {
    this.timeStampEnd = new Date(d.getTime());
    //timeStampEnd = d;
  }

  private int maxNoOfQuestionsPerCat;

  public void setMaxNoOfQuestionsPerCat(int m) {
    maxNoOfQuestionsPerCat = m;
  }

  public int getMaxNoOfQuestionsPerCat() {
    return maxNoOfQuestionsPerCat;
  }

  private int rightAnswers = 0;

  public void incrementRightAnswers() {
    rightAnswers++;
  }

  public int getRightAnswers() {
    return rightAnswers;
  }

  //Game owns relationship
  @ManyToOne
  private Player player;

  public Player getGames() {
    return player;
  }

  public void setPlayer(Player p) {
    player = p;
  }

  @ManyToMany
  @JoinTable(
      name = "answersChosen")
  private List<Answer> answersChosen = new ArrayList<>();

  public List<Answer> getAnswersChosen() {
    return answersChosen;
  }

  public void addChosenAnswer(Answer a) {
    answersChosen.add(a);
  }


  @ManyToMany
  @JoinTable(
      name = "categoriesOfGame")
  private List<Category> categoriesOfGame = new ArrayList<>();

  public List<Category> getCategoriesOfGame() {
    return categoriesOfGame;
  }

  public void addCategory(Category c) {
    categoriesOfGame.add(c);
  }

  @ManyToMany
  @JoinTable(
      name = "questionsAsked")
  private List<Question> questionsAsked = new ArrayList<>();

  public List<Question> getQuestionsAsked() {
    return questionsAsked;
  }

  public void addQuestions(List<Question> questions) {
    questionsAsked.addAll(questions);
  }

}

