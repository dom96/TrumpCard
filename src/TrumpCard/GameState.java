package TrumpCard;

import javafx.animation.TranslateTransition;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

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

    GameState(Character character)
    {
        this.currentCharacter = character;
        this.crimes = new ArrayList<Crime>();

        crimeIcon = new Image("file:images/crime32.png");
        crimeIconHover = new Image("file:images/crime32_hover.png");
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

    /**
     * Likelihood of a crime being generated as a percentage. 100% doesn't guarantee that one will be generated!
     */
    private double crimeLikelihood(long now)
    {
        int crimeNumber = this.crimes.size();
        // -- Likelihood that a new crime will be generated should decrease with the amount of crimes on the map.
        // -- But increase with the amount of time since the last crime was generated.

        double result = (MAX_CRIMES - crimeNumber);
        if (crimeNumber == 0) {
            result += 40;
        }
        else
        {
            long secsSinceLastCrime = (now - this.lastCrimeGen) / (long)1e9;
            result += secsSinceLastCrime * (crimeNumber/25.0);
        }
        return result;
    }

    private void updateCrimes(Group root, long now) {
        ArrayList<Crime> newCrimes = new ArrayList<Crime>();
        for (int i = 0; i < this.crimes.size(); i++)
        {
            double x = 290 + (i*227.5) + (i*20);
            this.crimes.get(i).translateBox(x);
            if (!this.crimes.get(i).update(now))
            {
                newCrimes.add(crimes.get(i));
            }
            else
            {
                this.crimes.get(i).destroy(root);
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
        currentCharacter.setEnergy(newEnergy);
    }

    public void poll(Group root, long now) {
        // Decide whether a new crime/opportunity should be generated. Check only every 2 seconds.
        if (now - lastCrimeCheck >= 2e9) {
            lastCrimeCheck = now;
            double likelihood = crimeLikelihood(now);

            Random rand = new Random();
            double randDouble = rand.nextDouble();
            System.out.println(likelihood + "%" + ", " + randDouble);

            if (randDouble < likelihood / 100.f && this.crimes.size() < MAX_CRIMES) {
                Crime.CrimeInfo info = Crime.genCrime();
                Crime crime = new Crime(info, now + (long)10e9);
                crime.show(this, root);
                this.crimes.add(crime);
                this.lastCrimeGen = now;
            }
        }
        // Move crime boxes and check crimes for expiry.
        updateCrimes(root, now);

        // Update character stats.
        updateCharacter(now);
    }

}
