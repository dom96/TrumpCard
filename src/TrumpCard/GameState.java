package TrumpCard;

import com.sun.javaws.exceptions.InvalidArgumentException;
import javafx.animation.TranslateTransition;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Random;

public class GameState {
    private Character currentCharacter;
    private ArrayList<Crime> crimes;

    private long lastCrimeGen; // Time when last crime was generated.
    private long lastCrimeCheck; // Time when it was last checked whether crime should be generated.

    private long lastCharacterUpdate; // Last time character stats were updated.

    private final int MAX_CRIMES = 4; // Max amount of crimes at the same time.

    private Image crimeIcon;
    private Image crimeIconHover;

    private long gameAge; // Time this game has been running in miliseconds.
    private long lastPoll;

    private Difficulty difficulty;

    GameState(Character character, Difficulty difficulty)
    {
        this.currentCharacter = character;
        this.crimes = new ArrayList<Crime>();

        crimeIcon = new Image("file:images/crime32.png");
        crimeIconHover = new Image("file:images/crime32_hover.png");

        this.difficulty = difficulty;
    }

    public Character getCharacter()
    {
        return this.currentCharacter;
    }

    public ArrayList<Crime> getCrimes() {
        return this.crimes;
    }

    public Image getCrimeIcon() {
        return crimeIcon;
    }

    public Image getCrimeIconHover() {
        return crimeIconHover;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void pause() {
        currentCharacter.pause();
        for (Crime c : crimes)
        {
            c.pause();
        }
    }

    public void resume() {
        currentCharacter.resume();
        for (Crime c : crimes)
        {
            c.resume();
        }
    }

    /**
     * If the character is at a crime location then returns the Crime on which the character is at.
     * If not then returns ``null``!
     */
    public Crime getCrimeAtCharacterPos() {
        for (Crime crime : crimes)
        {
            if (crime.getWindowPos().equals(currentCharacter.getPos()))
            {
                return crime;
            }
        }
        return null;
    }

    public void commitCrime(Crime crime, Group root) {
        // Decrease the actions because we are doing an evil thing.
        double newActions = currentCharacter.getActions() - 1;
        currentCharacter.setActions(newActions);

        // Decrease energy.
        double newEnergy = currentCharacter.getEnergy() - crime.getEnergyUse(difficulty);
        currentCharacter.setEnergy(newEnergy);

        // Increase score.
        int scoreBonus = crime.getEnergyUse(difficulty) * 10;
        currentCharacter.setScore(currentCharacter.getScore() + scoreBonus);

        crime.destroy(root);
    }

    public void fightCrime(Crime crime, Group root) {
        // Increase the actions because we are doing a good thing.
        double newActions = currentCharacter.getActions() + 1;
        currentCharacter.setActions(newActions);

        // Decrease energy.
        double newEnergy = currentCharacter.getEnergy() - crime.getEnergyUse(difficulty);
        currentCharacter.setEnergy(newEnergy);

        // Increase score.
        int scoreBonus = crime.getEnergyUse(difficulty) * 10;
        currentCharacter.setScore(currentCharacter.getScore() + scoreBonus);

        crime.destroy(root);
    }

    public void ignoreCrime(Crime crime, Group root)
    {
        // Normalise actions towards human, i.e. increase if < 50, decrease if > 50.
        double newActions = currentCharacter.getActions();
        if (newActions >= 50)
        {
            newActions -= 1;
        }
        else
        {
            newActions += 1;
        }
        currentCharacter.setActions(newActions);

        crime.destroy(root);
    }

    /**
     * Likelihood of a crime being generated as a percentage. 100% doesn't guarantee that one will be generated!
     */
    public double crimeLikelihood(long now)
    {
        int crimeNumber = this.crimes.size();
        // -- Likelihood that a new crime will be generated should decrease with the amount of crimes on the map.
        // -- But increase with the amount of time since the last crime was generated.

        double result = (MAX_CRIMES - crimeNumber);

        long secsSinceLastCrime = (now - this.lastCrimeGen) / (long) 1e9;
        // Scale to 100% crime likelihood by the time 60 seconds elapsed since the last crime gen.
        result += secsSinceLastCrime * 1.67;
        // Add some likelihood based on number of crimes present.
        switch (difficulty)
        {
            case Easy:
                result += 2 / ((crimeNumber * 2) + 1);
                break;
            case Medium:
                result += 5 / ((crimeNumber * 2) + 1);
                break;
            case Hard:
                result += 10 / ((crimeNumber * 2) + 1);
                break;
        }
        // Make game progressively harder by increasing the likelihood throughout the game.
        double ageFactor = 10000;
        switch (difficulty)
        {
            case Easy:
                ageFactor = 10000;
                break;
            case Medium:
                ageFactor = 7500;
                break;
            case Hard:
                ageFactor = 5000;
                break;
        }
        result += gameAge / ageFactor;
        return result;
    }

    private Character toHero(Character character) {
        switch (character.getName())
        {
            case TonyStark:
            case Ultron:
                character.setName(CharacterName.IronMan);
                break;
            case BruceWayne:
            case Catwoman:
                character.setName(CharacterName.Batman);
                break;
            case PeterParker:
            case GreenGoblin:
                character.setName(CharacterName.Spiderman);
                break;
            default:
                throw new InvalidParameterException("Can't turn " + character.getName().name() + " into villain.");
        }

        // I was going to create a new instance of Hero here. That would require me to copy all
        // the fields from `character` manually though. I decided to simply keep what the player
        // originally started with.

        return character;
    }

    private Character toVillain(Character character) {
        switch (character.getName())
        {
            case TonyStark:
            case IronMan:
                character.setName(CharacterName.Ultron);
                break;
            case BruceWayne:
            case Batman:
                character.setName(CharacterName.Catwoman);
                break;
            case PeterParker:
            case Spiderman:
                character.setName(CharacterName.GreenGoblin);
                break;
            default:
                throw new InvalidParameterException("Can't turn " + character.getName().name() + " into hero.");
        }

        return character;
    }

    private Character toHuman(Character character) {
        switch (character.getName())
        {
            case Ultron:
            case IronMan:
                character.setName(CharacterName.TonyStark);
                break;
            case Catwoman:
            case Batman:
                character.setName(CharacterName.BruceWayne);
                break;
            case GreenGoblin:
            case Spiderman:
                character.setName(CharacterName.PeterParker);
                break;
            default:
                throw new InvalidParameterException("Can't turn " + character.getName().name() + " into human.");
        }


        return character;
    }

    private void updateCrimes(Group root, long now) {
        ArrayList<Crime> newCrimes = new ArrayList<Crime>();
        for (int i = 0; i < this.crimes.size(); i++)
        {
            double x = 290 + (i*227.5) + (i*20);
            Crime crime = this.crimes.get(i);
            crime.translateBox(x);
            if (!crime.update(now))
            {
                if (!crime.isDestroyed()) {
                    newCrimes.add(crime);
                }
            }
            else
            {
                ignoreCrime(crime, root);
            }
        }
        crimes = newCrimes;
    }

    private void updateCharacter(long now) {
        // Only update the character stats every 200 miliseconds.
        if (now - lastCharacterUpdate < 200e6)
        {
            return;
        }
        lastCharacterUpdate = now;
        double newEnergy = currentCharacter.getEnergy();
        switch (currentCharacter.getStatus()) {
            case Moving:
                // When the character is moving energy depletes faster.
                newEnergy -= 0.5;
                break;
            case Still:
                // Some energy depletes.
                newEnergy -= 0.1;
                break;
            case Sleeping:
                // Energy is restored when sleeping.
                newEnergy += 0.6;
                break;
        }
        newEnergy = Math.max(0, newEnergy);
        newEnergy = Math.min(99.9, newEnergy);
        currentCharacter.setEnergy(newEnergy);

        // Check if character has turned into hero/human/villain.
        if (currentCharacter.getActions() >= 60)
        {
            // Hero
            if (!CharacterName.isHero(currentCharacter.getName()))
            {
                currentCharacter = toHero(currentCharacter);
            }
        }
        else if (currentCharacter.getActions() >= 40)
        {
            // Human
            if (!CharacterName.isHuman(currentCharacter.getName()))
            {
                currentCharacter = toHuman(currentCharacter);
            }
        }
        else
        {
            // Villain
            if (!CharacterName.isVillain(currentCharacter.getName()))
            {
                currentCharacter = toVillain(currentCharacter);
            }
        }
    }

    public void poll(Group root, long now, Label errorLabel) {
        // Decide whether a new crime/opportunity should be generated. Check only every 2 seconds.
        if (now - lastCrimeCheck >= 2e9 && !currentCharacter.isPaused()) {
            lastCrimeCheck = now;
            double likelihood = crimeLikelihood(now);
            // Always generate a crime at the beginning of the game.
            if (lastCrimeGen == 0)
            {
                likelihood = 100;
            }

            Random rand = new Random();
            double randDouble = rand.nextDouble();
            System.out.println(likelihood + "%" + ", " + randDouble);

            if (randDouble < likelihood / 100.f && this.crimes.size() < MAX_CRIMES) {
                Crime crime = Crime.genCrime();
                crime.show(this, root, errorLabel);
                this.crimes.add(crime);
                this.lastCrimeGen = now;
            }
        }
        // Move crime boxes and check crimes for expiry.
        updateCrimes(root, now);

        // Update character stats.
        updateCharacter(now);

        // Update game age
        if (lastPoll != 0) {
            gameAge += Math.round((now - lastPoll) / 1.0e6);
        }
        lastPoll = now;
    }

    public enum Difficulty {
        Easy, Medium, Hard
    }

}
