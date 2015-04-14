package TrumpCard;

public enum CharacterName {
    IronMan, Batman, Spiderman,
    TonyStark, BruceWayne, PeterParker,
    Ultron, Joker, GreenGoblin;

    public static boolean isVillain(CharacterName name) {
        switch (name)
        {
            case Ultron:
                return true;
            case Joker:
                return true;
            case GreenGoblin:
                return true;
            default:
                return false;
        }
    }

    public static boolean isHero(CharacterName name) {
        switch (name)
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

    public static boolean isHuman(CharacterName name) {
        switch (name)
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
}
