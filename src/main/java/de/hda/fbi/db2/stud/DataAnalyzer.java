package de.hda.fbi.db2.stud;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 * The DataAnalyzer class provides statisitcal insight on game data.
 */
public class DataAnalyzer {

  private EntityManager em;

  public DataAnalyzer() {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("postgresPU");
    em = emf.createEntityManager();

  }

  public void analyze() {

    //first query
    Scanner scanner = new Scanner(System.in, "UTF-8");

    boolean secondQuerySuccesful = false;
    while (!secondQuerySuccesful) {
      System.out
          .println(
              "\n*****Analyse 1:\nGeben Sie den Zeitraum ein, für den sie sich alle Spieler "
                  + "ausgeben wollen.\nGeben Sie zuerst das Startdatum im Format dd.MM.YYYY ein.");
      String startDate = scanner.nextLine();
      System.out
          .println("Geben Sie nun das Enddatum im Format dd.MM.YYYY ein.");
      String endDate = scanner.nextLine();
      Date start;
      Date end;
      try {
        start = new SimpleDateFormat("dd.MM.yyyy").parse(startDate);
        end = new SimpleDateFormat("dd.MM.yyyy").parse(endDate);
        getPlayerInTimePeriod(start, end);
        secondQuerySuccesful = true;
      } catch (ParseException e) {
        System.out
            .println("Falsches Datumsformat! Das Datum mus im Format dd.MM.yyyy eingegeben werden");
      }
    }

    //second query
    String playerName;
    System.out
        .println(
            "\n*****Analyse 2:\nGeben Sie den Namen des Spielers ein, für den Sie eine "
                + "Detailübersicht erhalten möchten.\n");
    playerName = scanner.nextLine();
    getPlayerDetails(playerName);

    // third query
    System.out
        .println(
            "\n*****Analyse 3: Ausgabe aller Spieler mit Anzahl der gespielten Spiele, "
                + "absteigend geordnet.\n");
    getPlayerRanking();

    //fourth query
    System.out
        .println("\n*****Analyse 4: Ausgabe der am meisten gefragten Kategorie.\n");
    getCategoryRanking();
    em.close();
  }

  private void getPlayerInTimePeriod(Date startDate, Date endDate) {

    Query query = em.createQuery(
        "SELECT g.player.name from Game as g where g.timeStampStart > :start "
            + "and g.timeStampEnd < :end Group by g.player.name");

    List playerNames = query.setParameter(
        "start", startDate).setParameter("end", endDate).getResultList();

    System.out
        .println("\nSpieler die zwischen " + startDate + " und " + endDate + " gespielt haben:");

    if (playerNames.isEmpty()) {
      System.out.println("\nIm genannten Zeitraum wurden keine Spiele gespielt");
    } else {
      for (Iterator i = playerNames.iterator(); i.hasNext(); ) {
        String name = i.next().toString();
        System.out.println(name);
      }
    }

  }

  private void getPlayerDetails(String playerName) {

    Query query = em.createQuery(
        "SELECT g.id, g.timeStampStart, g.timeStampEnd, g.rightAnswers, "
            + "count(g.questionsAsked) from Game as g where g.player.name = :name "
            + "group by g.id order by g.id");

    List games = query.setParameter(
        "name", playerName).getResultList();

    System.out.println("\nÜbersicht für " + playerName);
    //print game id, datum, right answers, total questions

    if (games.isEmpty()) {
      System.out.println("\nSpieler ist unbekannt");
    } else {
      for (Iterator i = games.iterator(); i.hasNext(); ) {
        Object[] element = (Object[]) i.next();
        System.out.format("%-5s%-5s%-7s%-32s%-7s%-32s%-15s%-5d%-15s%-5s%n",
            "Game-Id: ", element[0].toString(), "Start: ", element[1].toString(), "Ende: ",
            element[2].toString(),
            "Richtige Anworten: ", element[3], "Anzahl der Fragen: ", element[4].toString());
      }
    }
  }


  private void getPlayerRanking() {

    Query query = em.createQuery(
        "SELECT g.player.name, count(g.id) as numberOfGames from Game as g "
            + "Group by g.player.name order by numberOfGames desc");

    List playerRanking = query.getResultList();

    if (playerRanking.isEmpty()) {
      System.out.println("\nKeine Spieler in der Datenbank gefunden");
    } else {
      for (Iterator i = playerRanking.iterator(); i.hasNext(); ) {
        Object[] element = (Object[]) i.next();
        System.out.format("%-10s%-15s%-15s%-10s%n",
            "Spieler: ", element[0].toString(), "Anzahl der Spiele: ", element[1].toString());
      }

    }

  }

  private void getCategoryRanking() {
    Query query = em.createQuery(
        "SELECT c.name, count(g.id) as numberOfGames from Game g, Category c where c "
            + "member of g.categoriesOfGame Group by c.name order by numberOfGames desc");

    List categoryRanking = query.getResultList();

    if (categoryRanking.isEmpty()) {
      System.out.println("\nKeine Kategorien in der Datenbank gefunden");
    } else {
      for (Iterator i = categoryRanking.iterator(); i.hasNext(); ) {
        Object[] element = (Object[]) i.next();
        System.out.format("%-25s%-15s%-10s%n",
            element[0].toString(), "Anzahl der Spiele : ", element[1].toString());
      }

    }

  }
}

