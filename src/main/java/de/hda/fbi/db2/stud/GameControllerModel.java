package de.hda.fbi.db2.stud;

import de.hda.fbi.db2.stud.entity.Category;
import de.hda.fbi.db2.stud.entity.Game;
import de.hda.fbi.db2.stud.entity.Question;
import java.util.List;

/**
 * The GameControllerModel class contains the model structure which is needed by the
 * GameController.
 */


public class GameControllerModel {

  public Game game;
  public List<Category> dbCategories;
  public int questionIndex = 0;
  public Question currentQuestion;
  public static final int minimumCategoriesRequired = 2;

}
