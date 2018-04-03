package org.leanpoker.player;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.Collections;

public class Player {

    static final String VERSION = "Default Java folding player";
    static boolean didBlueff = false;

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

        int km_probability = KMcompareCardsPreFlop(one,two,oneColour,twoColour);

        if (communityCards.length == 0){


            bet = checkForPreFlop(one, two, oneColour, twoColour, bigBlind);


        }
        else {
           // mit unsern karten plus karten in der mitte iwas bilden

            int[] result = checkForSiblings(communityCards,p.hole_cards);




            // checken fuer zwilling, drillinge, vierlinge, full-house

            if (communityCards.length == 3){
                bet += checkForTwins(result);
                bet += checkForTwoTwins(result);
                bet += checkForTripple(result);
            }

            bet += checkForFullHouse(result);
            bet += checkForQuadrupple(result);
            bet += checkForFlush(communityCards, p.hole_cards);

            bet += checkForStraight(communityCards,p.hole_cards);

        }


       /* else if (km_probability >= 50){

            bet = highestbet-p.bet;
        }*/


        if (p.bet == 0){
            didBlueff = false;
        }
        double random = Math.random();
        if (highestbet-p.bet < 100){
        if(random <= 0.5){

            if (bet != 0){
                bet = highestbet + bet - p.bet;
            }

        }else {

            if (bet != 0){
                bet = highestbet - p.bet;
            }
        }}

        if (random <=  0.03){
            didBlueff = true;
            bet = p.stack/2;
        }

        if (didBlueff && bet == 0){
            bet = highestbet-p.bet;
        }
        int km_bestAdvise = einfacher_straight(one,two,oneColour,twoColour,communityCards[0].getRankAsNumber(),communityCards[0].suit,communityCards[1].getRankAsNumber(),communityCards[1].suit,communityCards[2].getRankAsNumber(),communityCards[2].suit);

        if (km_bestAdvise > 50){
            bet = 1000;
        }

        return bet;
    }

    public static int checkForStraight(CardObj[] comm, CardObj[] player){

        ArrayList<Integer> list = new ArrayList<>();

        for (int i = 0; i<comm.length;i++){

            list.add(comm[i].getRankAsNumber());
        }
        for (int i = 0; i<player.length;i++){

            list.add(comm[i].getRankAsNumber());
        }

        Collections.sort(list);


        ArrayList<Integer> result = new ArrayList<>();
        int lastValue = list.get(0);
        int counter = 1;
        for(int i=1; i<list.size();i++) {

            if (list.get(i) == lastValue + 1) {
                counter++;
                lastValue = list.get(i);
                result.add(lastValue);
            } else if (lastValue != list.get(i)){
                counter = 1;
                lastValue = list.get(i);
                result.clear();
            }
            if (counter == 5) {
                result.add(list.get(i));
                break;
            }
        }

        for (int i = 0; i<result.size();i++){

            if(result.get(i) == player[0].getRankAsNumber()){

                for (int j = 0; j<comm.length;j++){

                    if (comm[j].getRankAsNumber() == player[0].getRankAsNumber()){
                        return 0;
                    }else {
                        return 50;
                    }

                }

            }
            if(result.get(i) == player[1].getRankAsNumber()){

                for (int j = 0; j<comm.length;j++){

                    if (comm[j].getRankAsNumber() == player[1].getRankAsNumber()){
                        return 0;
                    }else {
                        return 50;
                    }
                }

            }

        }
        return 0;

    }

    public static int checkForFlush(CardObj[] comm, CardObj[] player){

        int spadesCounter = 0;
        int heartsCounter = 0;
        int diamondsCounter = 0;
        int clubsCounter = 0;

        for(int i =0; i<comm.length;i++){

            switch (comm[i].suit) {

                case "spades":
                    spadesCounter++;
                case "hearts":
                    heartsCounter++;
                case "diamonds":
                    diamondsCounter++;
                case "clubs":
                    clubsCounter++;
                    default:
                        break;
            }
        }

        for(int i =0; i<player.length;i++) {

            switch (player[i].suit) {

                case "spades":
                    spadesCounter++;
                case "hearts":
                    heartsCounter++;
                case "diamonds":
                    diamondsCounter++;
                case "clubs":
                    clubsCounter++;
                default:
                    break;
            }
        }

        if(spadesCounter >=5 || heartsCounter >= 5 || diamondsCounter >= 5 || clubsCounter >= 5){

            return 50;
        }
        return 0;
    }

    public static int checkForTwoTwins(int[] a) {

        int bet = 0;

        if (a[0] == 2 && a[1] == 2){

            bet = 1;

        }

        return bet;
    }

    public static void showdown(JsonElement game) {
    }

    public static  int checkForTwins(int[] a){

        int bet = 0;

       if (a[0] == 0 && a[1] == 2){

           bet = 1;

       }
        if (a[0] == 2 && a[1] == 0){

            bet = 1;

        }

        return bet;


    }
    public static  int checkForQuadrupple(int[] a){

        int bet = 0;

        if (a[0] == 0 && a[1] == 4){

            bet = 1;

        }
        if (a[0] == 4 && a[1] == 0){

            bet = 100;

        }

        return bet;


    }

    public static  int checkForTripple(int[] a){

        int bet = 0;

        if (a[0] == 0 && a[1] == 3){

            bet = 1;

        }
        if (a[0] == 3 && a[1] == 0){

            bet = 10;

        }

        return bet;

    }

    public static  int checkForFullHouse(int[] a){

        int bet = 0;

        if (a[0] == 2 && a[1] == 3){

            bet = 1;

        }
        if (a[0] == 3 && a[1] == 2){

            bet = 75;

        }

        return bet;

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

        if (bet == 0){


        }

        return bet;


    }



    public static int[] checkForSiblings(CardObj[] communityCards, CardObj[] playerCards){

        int counterA = 0;
        int counterB = 0;

        for (int i = 0; i<playerCards.length; i++){



            for (int j= 0; j<communityCards.length; j++){

                if(playerCards[i].rank.equals(communityCards[j].rank)){

                    if (i == 0) {
                        counterA += 1;
                    }
                    else {
                        counterB += 1;
                    }

            }
        }

        }

        if (playerCards[0].rank.equals(playerCards[1].rank)){
            int [] a = {counterA+2, 0};
            return a;
        }

        if (counterA != 0){
            counterA++;
        }

        if (counterB != 0){
            counterB++;
        }

        final int [] b = {counterA,counterB};

        // zwilling or drilling in community

        int counter = 0;

        if((counterA == 0 && counterB != 0 )|| (counterA != 0 && counterB == 0)){

            for(int i = 0; i<communityCards.length; i++) {


                for (int j = i; j < communityCards.length; j++) {

                    if (communityCards[i].rank.equals(communityCards[j].rank) && playerCards[0] != communityCards[i] && playerCards[1] != communityCards[i]) {

                        counter++;

                    }
                }
            }

            if(counterA == 0){
                b[0] = ++counter;
            }else {
                b[1] = ++counter;
            }

        }



        return b;

    }



    public static int KMcompareCardsPreFlop(
    int erstekartewert,
    int zweitekartewert,
    String erstefarbe,
    String zweitefarbe
    ) {
        int wert = 0;

        if (erstefarbe.equals(zweitefarbe)) {
            wert = wert + 50;
        } else {
            wert = wert + 0;
        }

        int temp = 0;
        if (erstekartewert == zweitekartewert) {
            temp = erstekartewert + zweitekartewert;
            wert = wert + 100;

            wert = wert - (13 - erstekartewert); // 13 ist kartenanzehl
        } else {

            temp = (erstekartewert > zweitekartewert) ? // Abstand der Karten
                    erstekartewert - zweitekartewert :
                    zweitekartewert - erstekartewert;
            int hoehereKarte = (erstekartewert > zweitekartewert) ? // höhere Karte
                    erstekartewert:
                    zweitekartewert;

            switch (temp) {
                case 1:
                    wert = wert + 50 - (13 - hoehereKarte);
                    break;
                case 2:
                    wert = wert + 25 - (13 - hoehereKarte);
                    break;
                case 3:
                    wert = wert + 13 - (13 - hoehereKarte);
                    break;
                default:
                    wert = wert + 0;
            }



        }



        return wert;
    }//ende von compareCardsPreFlop


    public static int wahrscheinlichkeitStraightTurnRiver(int stellen, int breite){
        int fehlendeStellen1 = stellen;
        int maximalBreiteDerBreitestenStelle = breite;


        int t = 0;

        if(fehlendeStellen1==0 && maximalBreiteDerBreitestenStelle==0){
            return 100;
        }

        if (fehlendeStellen1==1 && maximalBreiteDerBreitestenStelle==1){
            t = (2*4)+1;
            return t;
        }

        if (fehlendeStellen1==2 && maximalBreiteDerBreitestenStelle==1){
            //save
            t = (2*8)+1;
            return t;
        }
        if (fehlendeStellen1==1 && maximalBreiteDerBreitestenStelle==2){
            //save
            t = (2*12)+1;
            return t;
        }
        if (fehlendeStellen1==2 && maximalBreiteDerBreitestenStelle==2){
            //save
            t = (2*8)+1;
            return t;
        }
        return 0;
    }






    public static int einfacher_straight(
            int erstekartewert,
            int zweitekartewert,
            String erstefarbe,
            String zweitefarbe,
            int ersteComKarteWert,
            String ersteComKarteFarbe,
            int zweiteComKarteWert,
            String zweiteComKarteFarbe,
            int dritteComKarteWert,
            String dritteComKarteFarbe
    ) {
        // straight 23456, 9 10 J Q K egal welche farbe
        int[] tempArray = {
                erstekartewert, zweitekartewert, ersteComKarteWert,
                zweiteComKarteWert, dritteComKarteWert
        };

        // Karten am Tisch
        boolean[] bgitter= new boolean[13];

        // Setzt die Werte f¸r die am Tisch vorhandenen Karten auf true
        for (int i = 0; i < tempArray.length; i++) {
            bgitter[tempArray[i]- 1] = true;
        }

		/*
		for (int j = 0; j < bgitter.length; j++){
			System.out.println(bgitter[j]);
		}*/

        //KM SAVE
        int max = 13; // Zum Herunterz‰hlen
        int anzahlDerTrues = 0;

        // Maximaler Abstand
        int abstand = 0;
        int tempAbstand = 0;
        // Zahl der Stellen mit Abstand
        int stellen = 0;

        for (int i = max; i > 0; i--) {
            if(bgitter[i-1]==true){
                anzahlDerTrues++;
            }
        }
        //System.out.println(anzahlDerTrues);

        if (anzahlDerTrues == 5) {
            boolean gefunden = false;
            int i= max;
            while(true) {
                if(bgitter.length == 0){

                }
                if (bgitter[i-1] == true) {
                    i--;
                    gefunden = true;
                    break;
                }
            }
            if(gefunden){
                int c = 1;
                int falsezaehler=0;
                boolean warFalse = false;

                while(anzahlDerTrues>c)
                {
                    if(bgitter[i-1]==true){
                        //dann weiter
                        if(warFalse){
                            stellen++;
                            warFalse = false;
                            if(falsezaehler>abstand){
                                abstand = falsezaehler;
                                falsezaehler = 0;
                            }
                        }
                        c++;
                    }else{
                        System.out.println("FALSE ERKANNT");
                        warFalse = true;
                        falsezaehler++;

                    }
                    i--;
                }
            }
        }
		/*System.out.println("abstand");
		System.out.println(abstand);
		System.out.println("stellen");
		System.out.println(stellen);*/
        return  wahrscheinlichkeitStraightTurnRiver(stellen,abstand);

    } // Ende einfacher Straight
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
