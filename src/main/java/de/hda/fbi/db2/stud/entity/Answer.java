package de.hda.fbi.db2.stud.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Answer class.
 */
@Entity
@SequenceGenerator(name = "answ_id", allocationSize = 1)
@Table(name = "Answer")
public class Answer {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "answ_id")
  private int id;

  private String answerText;

  public Answer(){} //default constructor

  @ManyToOne
  private Question question;

  public  void setQuestion(Question q){
    question = q;
  }

  public  void setAnswerText(String a){
    answerText = a;
  }

  public  String getAnswerText(){
    return answerText;
  }

}
