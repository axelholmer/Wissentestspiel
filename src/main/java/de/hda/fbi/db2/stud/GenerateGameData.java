package de.hda.fbi.db2.stud;

import de.hda.fbi.db2.stud.entity.Answer;
import de.hda.fbi.db2.stud.entity.Category;
import de.hda.fbi.db2.stud.entity.Game;
import de.hda.fbi.db2.stud.entity.Player;
import de.hda.fbi.db2.stud.entity.Question;
import java.sql.Time;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Random;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 * The GenerateGameData class generates mass data.
 */
public class GenerateGameData {

  private List<Category> dbCategories;
  private EntityManager em;

  public GenerateGameData() {

    EntityManagerFactory emf = Persistence.createEntityManagerFactory("postgresPU");
    em = emf.createEntityManager();
    Query query = em.createQuery(
        "SELECT c FROM Category c order by c.id");
    dbCategories = query.getResultList();
  }


  public void startDataGeneration() {
    int batchCount = 0;
    LocalDateTime startGeneration = LocalDateTime.now();

    int timeSalt;
    int numberOfPlayers = 10000;
    int firstPlayerLoop = 10000 / 100;
    int numberOfGamesPerPlayer = 100;

    //every game has two categories
    int firstCategory;
    int secondCategory;

    int maxNumberOfQuestions;
    int chosenAnswerIndex;
    Answer chosenAnswer;
    Random randomGenerator = new Random();

    for (int i = 0; i < firstPlayerLoop; i++) {
      em.getTransaction().begin();

      for (int m = 0; m < numberOfPlayers / firstPlayerLoop; m++) {
        Player player = new Player();
        int playerIndex = i * numberOfPlayers / firstPlayerLoop + m;
        player.setName("player" + playerIndex);
        em.persist(player);

        timeSalt = randomGenerator.nextInt(30);

        for (int j = 0; j < numberOfGamesPerPlayer; j++) {
          Game game = new Game();

          game.setPlayer(player);

          game.setTimeStampStart(generateTimestamp(timeSalt + j));

          firstCategory = randomGenerator.nextInt(51);
          secondCategory = (firstCategory + randomGenerator.nextInt(24) + 1) % 50;

          game.addCategory(dbCategories.get(firstCategory));
          game.addCategory(dbCategories.get(secondCategory));

          maxNumberOfQuestions = randomGenerator.nextInt(4) + 1;
          game.setMaxNoOfQuestionsPerCat(maxNumberOfQuestions);

          for (Category category : game.getCategoriesOfGame()) {
            if (category.getCollectionQuestions().size() <= maxNumberOfQuestions) {
              game.addQuestions(category.getCollectionQuestions());
            } else {
              game.addQuestions(
                  category.getCollectionQuestions().subList(0, maxNumberOfQuestions));
            }
          }

          //add answers and update rightAnswer-count
          for (Question question : game.getQuestionsAsked()) {
            chosenAnswerIndex = randomGenerator.nextInt(4);
            chosenAnswer = question.getAnswers().get(chosenAnswerIndex);
            game.addChosenAnswer(chosenAnswer);

            if (chosenAnswer == question.getRightAnswer()) {
              game.incrementRightAnswers();
            }
          }

          game.setTimeStampEnd(generateTimestamp(timeSalt + j));
          em.persist(game);
        }
      }

      batchCount++;
      System.out
          .println("Number of generated games: " + batchCount * 10000 + "\tTime: " + Time
              .from(Instant.now()));
      em.getTransaction().commit();
      em.clear();

    }

    if (em.getTransaction().isActive()) {
      em.getTransaction().commit();
    }

    em.close();
    LocalDateTime endGeneration = LocalDateTime.now();
    Duration duration = Duration.between(endGeneration, startGeneration);
    long durationInMinutes = Math.abs(duration.toMinutes());
    int totalDataGenerated = numberOfPlayers * numberOfGamesPerPlayer;

    System.out.println(totalDataGenerated + " entries were created.");
    System.out.println("Data generation took " + durationInMinutes + " minutes");
  }

  public Date generateTimestamp(int timeSalt) {

    int randNumber = (int) LocalDate.of(2019, 1, 1).toEpochDay() + timeSalt;
    LocalDate randDate = LocalDate.ofEpochDay(randNumber);
    return Date.from(randDate.atTime(LocalTime.now()).atZone(ZoneId.systemDefault()).toInstant());

  }

}
