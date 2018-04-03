package org.leanpoker.player;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Map;

public class Player {

    static final String VERSION = "Default Java folding player";

    public static int betRequest(JsonElement request) {
        Gson gson = new Gson();
        GameState gs = gson.fromJson(request, GameState.class);

        int highestbet = gs.current_buy_in;
        int mySelfID = gs.in_action;
        PlayerObj p = gs.players[mySelfID];
        int myStack = p.stack;
        int mybet = p.bet;
        int bigBlind = gs.small_blind*2;

        CardObj[] myCards = p.hole_cards;

        int one = myCards[0].getRankAsNumber();
        int two = myCards[1].getRankAsNumber();

        String oneColour = myCards[0].suit;
        String twoColour = myCards[1].suit;

        System.err.println("All cards");
        ArrayList<PlayerObj> allPlayers = new ArrayList<>();

        for (PlayerObj thisg : gs.players){
            allPlayers.add(thisg);
        }



        System.err.println(p.name);
        for (int j=0; j<p.hole_cards.length; j++)
        {
            System.err.println(p.hole_cards[j]);
        }

        CardObj[] communityCards = gs.community_cards;
        int bet = 0;


        if (communityCards.length == 0){


            bet = checkForPreFlop(one, two, oneColour, twoColour, bigBlind);

            if (bet != 0){
               bet = highestbet + bet - p.bet;
            }




        }else{
           // mit unsern karten plus karten in der mitte iwas bilden

        }


        return bet;
    }

    public static void showdown(JsonElement game) {
    }

    public static int checkForPreFlop(int one, int two, String oneColour, String twoColour, int bigBlind){

        int bet = 0;

        if (one == 13 || two == 13){

            bet = 4 * bigBlind;
        }

        if (one == two){

            bet = 4 * bigBlind;
        }

        if((one == 12|| two == 12) && oneColour.equals(twoColour)){
            bet = 4 * bigBlind;
        }

        if ((one == 12|| two == 12) && (one >= 4) && (two >= 4)){
            bet = 4 * bigBlind;

        }

        if ((one == 11 || two == 11) && (one >= 5) && (two >= 5) && oneColour.equals(twoColour)){
            bet = 4 * bigBlind;
        }

        if ((one == 11 || two == 11) && (one >= 7) && (two >= 7)){
            bet = 4 * bigBlind;
        }

        if ((one == 10 || two == 10) && (one >= 7) && (two >= 7) && oneColour.equals(twoColour)){
            bet = 4 * bigBlind;
        }

        if ((one == 10 || two == 10) && (one >= 9) && (two >= 9)){
            bet = 4 * bigBlind;
        }

        return bet;


    }

}

class GameState {
    public String tournament_id;
    public String game_id;
    public  int round;
    public int bet_index;
    public int small_blind;
    public int current_buy_in;
    public int pot;
    public int minimum_raise;
    public int dealer;
    public int orbits;
    public int in_action;
    public PlayerObj[] players;
    public CardObj[] community_cards;
}

class PlayerObj {
    public int id;
    public String name;
    public String status;
    public String version;
    public int stack;
    public int bet;
    public CardObj[] hole_cards;
}


class CardObj {
    public String suit;
    public String rank;
    // 2,3,4,5,6,7,8,9,10,J,Q,K,A

    public String toString()
    {
        return suit + "/" + rank;
    }


    public int getRankAsNumber(){

        switch (rank){

            case "2":
                return 1;
            case "3":
                return 2;
            case "4":
                return 3;
            case "5":
                return 4;
            case "6":
                return 5;
            case "7":
                return 6;
            case "8":
                return 7;
            case "9":
                return 8;
            case "10":
                return 9;
            case "J":
                return 10;
            case "Q":
                return 11;
            case "K":
                return 12;
            case "A":
                return 13;
            default:
                return -1;
        }
    }
}
