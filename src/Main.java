import java.awt.*;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.*;
import org.dreambot.api.methods.container.impl.bank.*;
import org.dreambot.api.methods.interactive.*;
import org.dreambot.api.methods.map.*;
import org.dreambot.api.methods.skills.*;
import org.dreambot.api.methods.walking.impl.*;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.Category;
import org.dreambot.api.utilities.*;
import org.dreambot.api.utilities.impl.*;
import org.dreambot.api.wrappers.interactive.*;

import static org.dreambot.api.methods.container.impl.bank.Bank.*;
import static org.dreambot.api.methods.skills.SkillTracker.getGainedExperience;

@ScriptManifest(author = "Sem", name = "Seed Thiever", version = 1.0, description = "Master Farmer Thief", category = Category.THIEVING)
public class Main extends AbstractScript {

    Condition inCombat = new Condition() {
        public boolean verify() {
            return Players.getLocal().isInCombat();
        }
    };

    GameObject bankBooth = GameObjects.closest("Bank booth");
    Timer timer;
    public long hour = 3600000; //Milliseconds in an hour
    int HerbTime = 4800000; //Time to grow herbs (Milliseconds)

    public void onStart() {
        SkillTracker.start(Skill.THIEVING); //Start tracking xp
        log("Script opstarten.");
        log("Begin van het script.");

        timer = new Timer(4800000);
        super.onStart();
    }

    Area DraynorSquare = new Area(3083, 3253, 3077, 3248); // Coördinates of Draynor Market Square
    int GuamCount = Inventory.count("Guam seed"); //https://dreambot.org/forums/index.php?/topic/20359-calculating-quantity-of-items/
    NPC farmer = NPCs.closest("Master Farmer");

    @Override
    public void onPaint(Graphics g) {
        int actionsTaken = (int) Math.floor(getGainedExperience(Skill.THIEVING) / 43);

        g.setColor(Color.GREEN);
        g.setFont(new Font("Ariel", Font.PLAIN, 18));
        g.drawString("Test Thiever", 20, 40);
        g.drawString("XP gained: " + getGainedExperience(Skill.THIEVING), 20, 80);
        g.drawString("Xp / Hour: " + SkillTracker.getGainedExperiencePerHour(Skill.THIEVING), 20, 100);
        g.drawString("Actions taken: " + actionsTaken, 20, 120);
        g.drawString("Guam Seeds: " + GuamCount, 20, 140);
        g.drawString("Time: " + timer, 20, 160);
    }

    private enum State {
        STEAL, BANK, HERBRUN
    }

    private State getState() {
        if (!Inventory.isFull())
            return State.STEAL;
        if (Inventory.isFull())
            return State.BANK;
        if (timer.finished())
            return State.HERBRUN;
        return null;
    }

    public int onLoop() {
        switch (getState()) {
            case STEAL:
                if (farmer != null && !Players.getLocal().isInCombat()) {
                    farmer.interact("Pickpocket");
                    sleep(Calculations.random(500, 2000));
                } else if (Players.getLocal().isInCombat()) {
                    log("In de aanval!");
                    Sleep.sleepWhile(inCombat, 10000);
                    sleep(Calculations.random(1500, 3500));
                }
                break;
            case BANK:
                log("Onderweg naar de Bank.");
                bankBooth.interact("Bank");
                Sleep.sleepUntil(Bank::isOpen, Calculations.random(3000, 5000));
                if (Bank.isOpen() && Inventory.isFull()) {
                    openTab(7);
                    depositAllItems();
                    sleep(Calculations.random(400, 1000));
                    log("Terug lopen naar Master Farmer.");
                    if (!Players.getLocal().isAnimating()) {
                        if (!DraynorSquare.contains(Players.getLocal().getTile())) {
                            Walking.walk(DraynorSquare.getRandomTile());
                            return (Calculations.random(5000, 7000));
                        }
                    }
                }



                
            case HERBRUN:

                // Item ID's
                int dramenStaff = 772;
                int skillsNecklace6 = 11968;
                int ardyCloak2 = 13122;
                int SnapdragonSeed = 5300;
                int runePouch = 12791;
                int hoodGraceful = 11850;
                int topGraceful = 11854;
                int legsGraceful = 11856;
                int glovesGraceful = 11858;
                int bootsGraceful = 11860;
                int capeGraceful = 11852;

                Area DraynorBank = new Area(3092, 3246, 3097, 3240);        //  Draynor Bank
                Area FarmFalador = new Area(3055, 3313, 3061, 3309);        //  Falador     (Runes / POH)
                Area FarmPhasmatys = new Area(3603, 3532, 3607, 3526);      //  Port Phasmatys   (Fairy Rings)
                Area FarmCatherby = new Area(2811, 3466, 2816, 3461);       //  Catherby         (Runes)
                Area FarmArdougne = new Area(2668, 3377, 2673, 3372);       //  Ardougne         (Cape / Runes)
                Area FarmingGuild = new Area(1236, 3729, 1241, 3724);       //  Farming Guild    (Skills Necklace)
                Area FarmHosidius = new Area(1736, 3553, 1741, 3548);       //  Hosidius         (Skills Necklace)
                Area FarmHarmony = new Area(3787, 2840, 3792, 2835);       // Via Mos Le'Haemless scroll ->  Brother Tranquility (teleport to Harmony)
                //Area Shortcut = new Area(1718, 3552, 1725, 3549);


        }
        return Calculations.random(500, 600);
    }

    private void sleepUntil(Object isOpen, int random) {
    }

    public void onExit() {
        log("Einde van het script.");
        log("Script afsluiten.");
    }
}