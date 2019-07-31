package de.hda.fbi.db2.stud;

import de.hda.fbi.db2.stud.entity.Answer;
import de.hda.fbi.db2.stud.entity.Category;
import de.hda.fbi.db2.stud.entity.Game;
import de.hda.fbi.db2.stud.entity.Player;
import de.hda.fbi.db2.stud.entity.Question;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 * The GameController class manages the GameControllerModel and the Gameview.
 */
public class GameController {


  private GameView gameView;
  private EntityManager em;
  private GameControllerModel gameModel;

  public GameController() {

    gameView = new GameView();
    gameView.setGameController(this);
    gameModel = new GameControllerModel();

    EntityManagerFactory emf = Persistence.createEntityManagerFactory("postgresPU");
    em = emf.createEntityManager();
  }

  /**
   * This method launches methods that gather information from the user
   * or publish information to the user without waiting for user interaction.
   */
  public void launchGame() {

    getPlayerName();
    getCategories();
    prepareQuestions();
    startQuestionnaire();
    finalizeGame();
  }

  /**
   * This methods launches a view method to ask for the user's name.
   */
  private void getPlayerName() {
    gameView.askForPlayerGame(false);
  }


  /**
   * This methods creates a new game object and saves it in the database.
   * If needed creates a new player object,
   * otherwise it fetches the existing player from the database.
   */
  public void initializeGame(String playerName) {

    if (playerName.isEmpty()) {
      gameView.askForPlayerGame(true);
    }

    em.getTransaction().begin();
    boolean playerExists = false;
    Player player = null;
    int playerId;

    //get list of existing players
    Query query = em.createQuery(
        "SELECT p FROM Player p");

    List<Player> players = query.getResultList();

    //check if player is already registered
    for (Player n : players) {
      if (n.getName().equals(playerName)) {
        playerId = n.getId();
        player = em.find(Player.class, playerId);
        playerExists = true;
        break;
      }
    }
    //add new player if not
    if (!playerExists) {
      player = new Player();
      player.setName(playerName);
      em.persist(player);
      // System.out.println("A new player " + player + " was created" + "\n");
    }

    gameModel.game = new Game();
    gameModel.game.setPlayer(player);
    gameModel.game.setTimeStampStart(Date.from(Instant.now()));

    em.persist(player);
    em.persist(gameModel.game);
    em.getTransaction().commit();

  }

  /**
   * This methods fetches all available categories from the database and
   * launches the gameview method for showing the categories to the user.
   */
  public void getCategories() {
    //get list of dbCategories
    Query query = em.createQuery(
        "SELECT c FROM Category c order by c.id");

    gameModel.dbCategories = query.getResultList();

    gameView.showCategoryOptions(gameModel.dbCategories, false,
        gameModel.game.getCategoriesOfGame().size());

  }

  /**
   * This methods validates the user's input and stores the chosen category in the game object.
   * It calls the showCategoryOptions method as long as the user wants to add further categories
   * and as long as he has not chosen at least the minimum number of required categories.
   */
  public void checkCategoryChoice(String input) {

    int inputAsInt = 0;
    boolean inputIsValid;
    try {
      inputAsInt = Integer.parseInt(input);
      inputIsValid = 0 < inputAsInt && inputAsInt <= gameModel.dbCategories.size();
    } catch (NumberFormatException e) {
      inputIsValid = false;
    }

    if (inputIsValid) {
      gameModel.game.addCategory(em.find(Category.class, inputAsInt));
      gameView.showCategoryOptions(gameModel.dbCategories, false,
          gameModel.game.getCategoriesOfGame().size());
    } else {
      if (gameModel.game.getCategoriesOfGame().size() >= gameModel.minimumCategoriesRequired) {
        gameView.showCategorySummary(gameModel.game.getCategoriesOfGame());
      } else {
        gameView.showCategoryOptions(gameModel.dbCategories, true,
            gameModel.game.getCategoriesOfGame().size());
      }
    }
  }

  /**
   * This methods sets a random maximum number of questions per categories
   * and adds questions from the selected categories to the game.
   */
  private void prepareQuestions() {

    //generate max number of questions per category
    Random randomGenerator = new Random();
    int maxQuestions = randomGenerator.nextInt(9) + 1;
    gameModel.game.setMaxNoOfQuestionsPerCat(maxQuestions);
    List<Question> questions;

    //pick questions
    for (Category c : gameModel.game.getCategoriesOfGame()) {
      //if category has less or as much questions as max number of questions add all
      if (c.getCollectionQuestions().size() <= maxQuestions) {
        gameModel.game.addQuestions(c.getCollectionQuestions());
      }
      //if category has more questions than max number
      // then shuffle and pick the first n (= max number)
      else {
        questions = c.getCollectionQuestions();
        Collections.shuffle(questions);
        questions = questions.subList(0, maxQuestions);
        gameModel.game.addQuestions(questions);
      }
    }

    //shuffle questions
    Collections.shuffle(gameModel.game.getQuestionsAsked());


  }

  /**
   * This methods passes the total number of questions to the gameview and launches
   * the questionnaire.
   */
  private void startQuestionnaire() {

    gameView.showQuestionnaireIntroduction(gameModel.game.getQuestionsAsked().size());
    this.getNextQuestion();

  }


  /**
   * This methods passes the next question to the gameview
   * and updates the current question attribute.
   */
  public void getNextQuestion() {

    //checks if there are still questions that need to be asked
    while (gameModel.questionIndex < gameModel.game.getQuestionsAsked().size()) {
      gameModel.currentQuestion = gameModel.game.getQuestionsAsked().get(gameModel.questionIndex);
      gameModel.questionIndex++;
      gameView.showQuestion(gameModel.currentQuestion, gameModel.questionIndex);
    }
  }


  public void checkAnswer(String answer) {
    boolean validAnswer;
    int answerAsInt = 0;

    //check if input is an int and if answer is valid (between 1-4 )
    try {
      answerAsInt = Integer.parseInt(answer);
      validAnswer = 0 < answerAsInt && answerAsInt <= 4;
    } catch (NumberFormatException e) {
      validAnswer = false;
    }

    if (validAnswer) {
      Answer chosenAnswer = gameModel.currentQuestion.getAnswers().get(answerAsInt - 1);
      gameModel.game.addChosenAnswer(chosenAnswer);
      if (chosenAnswer == gameModel.currentQuestion.getRightAnswer()) {
        gameModel.game.incrementRightAnswers();
        gameView.showAnswerFeedback(true, false, "");
      } else {
        gameView.showAnswerFeedback(false, false,
            gameModel.currentQuestion.getRightAnswer().getAnswerText());
      }
    } else {
      gameView.showAnswerFeedback(false, true, "");
    }


  }

  public void finalizeGame() {

    gameModel.game.setTimeStampEnd(Date.from(Instant.now()));
    em.getTransaction().begin();
    //overrides object in database with detached game object
    em.merge(gameModel.game);
    em.getTransaction().commit();
    em.close();

    gameView.showGameResult(gameModel.game.getRightAnswers(),
        gameModel.game.getQuestionsAsked().size());

  }


}
