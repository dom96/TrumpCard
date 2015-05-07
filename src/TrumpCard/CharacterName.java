package TrumpCard;

import javafx.scene.image.Image;

public enum CharacterName {
    IronMan, Batman, Spiderman,
    TonyStark, BruceWayne, PeterParker,
    Ultron, Catwoman, GreenGoblin;

    public boolean isVillain() {
        switch (this)
        {
            case Ultron:
                return true;
            case Catwoman:
                return true;
            case GreenGoblin:
                return true;
            default:
                return false;
        }
    }

    public boolean isHero() {
        switch (this)
        {
            case IronMan:
                return true;
            case Batman:
                return true;
            case Spiderman:
                return true;
            default:
                return false;
        }
    }

    public boolean isHuman() {
        switch (this)
        {
            case TonyStark:
                return true;
            case BruceWayne:
                return true;
            case PeterParker:
                return true;
            default:
                return false;
        }
    }

    public String getFriendlyName()
    {
        switch (this)
        {
            case Ultron: return "Ultron";
            case Catwoman: return "Catwoman";
            case GreenGoblin: return "Green Goblin";
            case IronMan: return "Iron Man";
            case Batman: return "Batman";
            case Spiderman: return "Spiderman";
            case TonyStark: return "Tony Stark";
            case BruceWayne: return "Bruce Wayne";
            case PeterParker: return "Peter Parker";
        }
        return "";
    }

    public String getAlignment()
    {
        if (this.isHuman())
        {
            return "Neutral";
        }

        if (this.isVillain())
        {
            return "Evil";
        }

        if (this.isHero())
        {
            return "Good";
        }
        return "Neutral";
    }

    public int getStrength()
    {
        switch (this)
        {
            case Ultron: return 6;
            case IronMan: return 6;
            case GreenGoblin: return 4;
            case Spiderman: return 4;
        }
        return 3;
    }

    public int getIntelligence()
    {
        switch (this)
        {
            case Ultron: return 4;
            case IronMan: return 6;
            case GreenGoblin: return 4;
            case Spiderman: return 4;


        }
        return 3;
    }

    public int getDurability()
    {
        switch (this)
        {
            case Ultron: return 7;
            case IronMan: return 6;
            case GreenGoblin: return 4;
            case Spiderman: return 3;
        }
        return 3;
    }

    public String getDescription()
    {
        switch (this)
        {
            case Ultron:
                return "Ultron-1 was constructed by Dr. Hank Pym\n" +
                        "of the Avengers as the famed \n" +
                        "scientist/adventurer was experimenting\n" +
                        "in high-intelligence robotics. Ultron\n" +
                        "became sentient and rebelled, \n" +
                        "hypnotizing Pym and brainwashing him into\n" +
                        "forgetting that Ultron had ever existed.\n" +
                        "He immediately began improving upon his\n" +
                        "rudimentary design, quickly upgrading \n" +
                        "himself several times from Ultron-2, \n" +
                        "Ultron-3 and Ultron-4, to finally \n" +
                        "becoming Ultron-5.";

            case TonyStark:
                return "Tony Stark is, for the lack of a better \n" +
                        "word, complicated. During his early days\n" +
                        "of success, Stark was a man who only \n" +
                        "cared about fame and wealth. He had no \n" +
                        "sense of responsibility or humility, \n" +
                        "always rubbing his success on the face \n" +
                        "of everyone he met.\n";
            case IronMan:
                return "An American industrialist, and ingenious \n" +
                        "engineer, Tony Stark suffers a severe \n" +
                        "chest injury during a kidnapping in \n" +
                        "which his captors attempt to force him\n" +
                        "to build a weapon of mass destruction.\n" +
                        "He instead creates a powered suit of \n" +
                        "armor to save his life and escape captivity.\n" +
                        "Thus creating the Iron Man.";
        }
        return "";
    }

    public boolean isRelatedTo(CharacterName name)
    {
        switch (this)
        {
            case Spiderman:
            case GreenGoblin:
            case PeterParker:
                return name == Spiderman || name == PeterParker || name == GreenGoblin;
            case Ultron:
            case TonyStark:
            case IronMan:
                return name == IronMan || name == Ultron || name == TonyStark;
            case Catwoman:
            case BruceWayne:
            case Batman:
                return name == Batman || name == Catwoman || name == BruceWayne;
            default:
                return false;
        }
    }

    public Image loadImage()
    {
        return UIUtils.loadImage("file:images/character_" + this.name().toLowerCase() + ".jpg");
    }
}
