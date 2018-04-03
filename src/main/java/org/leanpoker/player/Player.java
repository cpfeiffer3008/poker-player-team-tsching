package org.leanpoker.player;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

public class Player {

    static final String VERSION = "Default Java folding player";

    public static int betRequest(JsonElement request) {
        Gson gson = new Gson();
        GameState gs = gson.fromJson(request, GameState.class);

        int bet = 0;
        int mySelfID = gs.in_action;

        System.err.println("All cards");
        PlayerObj p = gs.players[mySelfID];
        System.err.println(p.name);
        for (int j=0; j<p.hole_cards.length; j++)
        {
            System.err.println(p.hole_cards[j]);
        }


        return 21;
    }

    public static void showdown(JsonElement game) {
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
}
