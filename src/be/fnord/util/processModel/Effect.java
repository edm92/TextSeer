package be.fnord.util.processModel;

/**
 * This class will contain the advanced version of an effect.
 *
 * Aim is to have WFF load old simple effects as logic statements, and then if given in the form
 *
 _JSONEFFECT{
 "Name": "Review employee assignments",
 "Type": "activity",

 "QOS": {
 "COST": "$20",
 "TIME": "5m",
 "SKILL": "LVL2"
 },

 "EFFECT": [
 "A & B",
 "A & D"
 ],

 "CONSTRAINT": [
 "~C",
 "~F",
 "~G"
 ],

 "GOAL": [
 "A"
 ],
 "RESOURCE":{
 "COMPUTER-CPU": "10"
 }
 }

 *  Then fill in this type of effect.
 * Created by edm92 on 25/03/2014.
 */
public class Effect {

    private String name = "task";       // More the name of the task than the effect
    private String type = "activity";   // More the type of activity than the effect
    

}
