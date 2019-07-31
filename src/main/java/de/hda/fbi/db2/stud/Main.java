package de.hda.fbi.db2.stud;

import java.io.IOException;
import java.net.URISyntaxException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.Scanner;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import de.hda.fbi.db2.stud.entity.Answer;
import de.hda.fbi.db2.stud.entity.Category;
import de.hda.fbi.db2.stud.entity.Question;
import de.hda.fbi.db2.tools.CsvDataReader;


public class Main {

  /**
   * Main Method and Entry-Point.
   *
   * @param args Command-Line Arguments.
   */
  public static void main(String[] args) {

    Boolean continueLoop = true;
    Boolean inputIsNumeric;
    Boolean optionIsValid;
    Scanner scanner = new Scanner(System.in, "UTF-8");
    while (continueLoop) {
      String input;
      int option = 0;
      System.out.println("\n" + "Funktionsübersicht");
      System.out.println("-1- Neues Spiel starten");
      System.out.println("-2- Weitere CSV-Datei einlesen");
      System.out.println("-3- Massendaten generieren");
      System.out.println("-4- Daten analysieren");
      System.out.println("-0- Spiel beenden");
      System.out.println("Geben Sie die gewünschte Nummer ein");
      input = scanner.nextLine();

      //check if input is an int
      try {
        option = Integer.parseInt(input);
        inputIsNumeric = true;
      } catch (NumberFormatException e) {
        inputIsNumeric = false;
      }

      //check if input is a valid number
      optionIsValid = 0 <= option && option <= 4;

      if (inputIsNumeric && optionIsValid) {

        switch (option) {
          case 0:
            continueLoop = false;
            break;
          case 1:
            launchGame();
            break;
          case 2:
            try {

              //Read default csv
              final List<String[]> defaultCsvLines = CsvDataReader.read();

              //CSV file has already been read
              persistCSVFile(defaultCsvLines);

              //Read (if available) additional csv-files and default csv-file
              List<String> availableFiles = CsvDataReader.getAvailableFiles();
              for (String availableFile : availableFiles) {
                final List<String[]> additionalCsvLines = CsvDataReader
                    .read(availableFile);
              }
            } catch (URISyntaxException use) {
              System.out.println(use);
            } catch (IOException ioe) {
              System.out.println(ioe);
            }
            break;
          case 3:
            GenerateGameData generateGameData = new GenerateGameData();
            generateGameData.startDataGeneration();
            break;
          case 4:
            DataAnalyzer dataAnalyzer = new DataAnalyzer();
            dataAnalyzer.analyze();
            break;
          default:
            break;
        }

      } else {
        System.out.println("Falsche Eingabe");
      }
    }
  }

  public String getGreeting() {
    return "app should have a greeting";
  }

  private static void persistCSVFile(List<String[]> csvLines) {

    EntityManagerFactory emf;
    EntityManager em; // Die EntityManagerFactory erstellen
    emf = Persistence.createEntityManagerFactory("postgresPU");
    em = emf.createEntityManager();

    Map<String, Category> categories = new HashMap<>();
    int numberOfLines = 0;
    int rightAnswerIndex = 0;
    Answer rightAnswer;
    String[] line;

    System.out.format("%n%-120s%-20s%-80s%-80s%-80s%-80s%-10s%n%n",
        "Frage", "Kategorie", "Antwort_1", "Antwort_2", "Antwort_3",
        "Antwort_4", "Richtige Antwort");

    for (int i = 1; i < csvLines.size(); i++) {

      em.getTransaction().begin();

      line = csvLines.get(i);

      Question question = new Question();
      Answer answer1 = new Answer();
      Answer answer2 = new Answer();
      Answer answer3 = new Answer();
      Answer answer4 = new Answer();
      Category category = new Category();

      //initialize questions
      question.setId(Integer.parseInt(line[0]));
      question.setQuestionText(line[1]);
      question.addAnwer(answer1);
      question.addAnwer(answer2);
      question.addAnwer(answer3);
      question.addAnwer(answer4);

      //initialize answers
      answer1.setAnswerText(line[2]);
      answer2.setAnswerText(line[3]);
      answer3.setAnswerText(line[4]);
      answer4.setAnswerText(line[5]);
      answer1.setQuestion(question);
      answer2.setQuestion(question);
      answer3.setQuestion(question);
      answer4.setQuestion(question);

      rightAnswerIndex = (Integer.parseInt(line[6]));
      rightAnswer = question.getAnswers().get(rightAnswerIndex - 1);
      question.setRightAnswer(rightAnswer);

      //check if category is already in database
      String categoryName = line[7];

      Query query = em.createQuery(
          "SELECT c FROM Category c WHERE c.name =:categoryName")
          .setParameter("categoryName", categoryName);

      List<Category> list = query.getResultList();

      if (!list.isEmpty() && list.size() == 1) {
        //fetch exisiting Category from Database
        int categoryId = (list.get(0).getId());
        category = em.find(Category.class, categoryId);
      } else {
        //initialize new category object
        category.setName(line[7]);
        category.addQuestion(question);

      }

      question.setCategory(category);

      numberOfLines += 1;

      //update dbCategories hashmap in order to access questions via dbCategories
      if (categories.containsKey(category.getName())
      ) {
        categories.get(category.getName()).addQuestion(question);
      } else {
        categories.put(category.getName(), category);

      }

      em.persist(category);
      em.persist(question);
      em.persist(answer1);
      em.persist(answer2);
      em.persist(answer3);
      em.persist(answer4);
      em.getTransaction().commit();

      System.out.format("%-120s%-20s%-80s%-80s%-80s%-80s%-10d%n",
          question.getQuestionText(), category.getName(), answer1.getAnswerText(),
          answer2.getAnswerText(), answer3.getAnswerText(),
          answer4.getAnswerText(), rightAnswerIndex);
    }

    em.close();
    System.out.println("\n" + "number of dbCategories" + "\t" + categories.size() + "\n");
    System.out.println("number of lines" + "\t" + numberOfLines + "\n");

  }

  private static void launchGame() {

    GameController gameController = new GameController();
    gameController.launchGame();

  }

}
