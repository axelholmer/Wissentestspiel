package de.hda.fbi.db2.stud;

import de.hda.fbi.db2.stud.entity.Answer;
import de.hda.fbi.db2.stud.entity.Category;
import de.hda.fbi.db2.stud.entity.Question;
import java.util.List;
import java.util.Scanner;

/**
 * The GameView class provides the views to the GameController.
 */

public class GameView {

  private GameController gameController;


  public GameView() {

  }

  public void setGameController(GameController gController) {
    gameController = gController;

  }

  public void askForPlayerGame(boolean showErrorMessage) {

    if (showErrorMessage) {
      System.out.println("Ungültige Eingabe! \n");
    }
    Scanner scanner = new Scanner(System.in, "UTF-8");

    System.out.println("Wissensspiel" + "\n");
    System.out.println("Geben Sie Ihren Spielernamen ein" + "\n");
    String playerName = scanner.nextLine();

    gameController.initializeGame(playerName);

  }

  public void showCategoryOptions(List<Category> categories, boolean showErrorMessage,
      int numberOfChosenCategories) {

    if (showErrorMessage) {
      System.out.println(
          "Falsche Eingabe! Wählen Sie eine Kategorie zwischen 1 und " + categories.size());
    }

    Scanner scanner = new Scanner(System.in, "UTF-8");
    String input;

    System.out.println("\nWählen Sie durch Eingabe der Nummer eine Kategorie "
        + "oder drücken Sie 'Enter' um fortzufahren.\n"
        + "Es müssen mindestens zwei Kategorien gewählt werden.\n"
        + "Anzahl der gewählten Kategorien: " + numberOfChosenCategories + "\n");

    //print dbCategories
    String categoryEntry;
    for (Category c : categories) {
      categoryEntry = c.toString();
      if (c.getId() % 5 == 0) {
        System.out.format("%-25s%n", categoryEntry);

      } else {
        System.out.format("%-25s", categoryEntry);
      }
    }
    System.out.println("\n");

    input = scanner.nextLine();

    gameController.checkCategoryChoice(input);

  }

  public void showCategorySummary(List<Category> chosenCategories) {
    System.out.println("Gewählte Kategorien:");
    for (Category c : chosenCategories) {
      System.out.println(c.getName());
    }
  }


  public void showQuestionnaireIntroduction(int numberOfQuestions) {

    System.out.println("\nEs erwarten Sie " + numberOfQuestions + " Fragen.\n");
  }

  public void showQuestion(Question question, int index) {

    int j;
    System.out.println(
        index + ". Frage: " + question.getQuestionText() + " (Kategorie: " + question.getCategory()
            + " )");

    //print all answer options for a specific question
    j = 1;
    for (Answer a : question.getAnswers()) {
      System.out.println("\t" + j + ". " + a.getAnswerText());
      j++;
    }

    this.readAnswer();

  }


  public void readAnswer() {
    Scanner scanner = new Scanner(System.in, "UTF-8");
    String answer;
    System.out.println("\nGeben Sie die Nummer der richtigen Antwort ein:");
    answer = scanner.nextLine();

    gameController.checkAnswer(answer);
  }

  public void showAnswerFeedback(boolean isAnswerCorrect, boolean showErrorMessage,
      String correctAnswer) {

    if (showErrorMessage) {
      System.out
          .println("Ungültige Eingabe! Wählen Sie eine Antwortmöglichkeit zwischen 1 - 4.");
      this.readAnswer();
    } else {
      if (isAnswerCorrect) {
        System.out.println("Die Antwort war richtig!\n");
      } else {
        System.out.println("Die Antwort war falsch. Die richtige Antwort ist:");
        System.out.println(correctAnswer + "\n");
      }
    }

    //gameController.getNextQuestion();

  }

  public void showGameResult(int rightAnswers, int totalQuestions) {

    System.out.println(
        "Sie haben " + rightAnswers + " von " + totalQuestions
            + " Fragen richtig beantwortet.\n");


  }


}
