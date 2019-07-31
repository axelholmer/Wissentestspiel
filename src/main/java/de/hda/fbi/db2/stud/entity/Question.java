package de.hda.fbi.db2.stud.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Question class.
 */
@Entity
@Table(name = "Question")
public class Question {
    @Id
    private int id;
    private String questionText;

@OneToOne
    private Answer rightAnswer;

    public Question(){} //default constructor

@ManyToOne
    private Category category;
    public Category getCategory() {
        return category;
    }

@OneToMany(mappedBy = "question", fetch = FetchType.EAGER)
    private List<Answer> answers = new ArrayList<>();
    public List<Answer> getAnswers() {
        return answers;
    }

    public  void setId(int id1){
        id = id1;
    }

    public  int getId(){
        return id;
    }

    public  void setRightAnswer(Answer a){
        rightAnswer = a;
    }
    public Answer getRightAnswer(){
        return rightAnswer;
    }

    public  void setQuestionText(String q){
        questionText = q;
    }

    public  String getQuestionText(){
        return questionText;
    }

    public  void addAnwer(Answer a){
        answers.add(a);
    }

    public  void setCategory(Category c){
        category = c;
    }

}
