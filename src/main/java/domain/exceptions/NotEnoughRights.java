package domain.exceptions;

import domain.RightType;

public class NotEnoughRights extends Exception {
    public NotEnoughRights(RightType right) {
        super("Not enough rights. You need: " + right);
    }
}
