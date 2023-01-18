package main.java.com.khomsi.game.objects.equipment;

import main.java.com.khomsi.game.entity.Entity;
import main.java.com.khomsi.game.main.GameManager;

public class AxeObject extends Entity {
    public AxeObject(GameManager gameManager) {
        super(gameManager);
        type = typeAxe;
        name = "Axe";
        down = setup("/objects/axe");
        attackValue = 2;
        attackArea.width = 30;
        attackArea.height = 30;
        itemDescription = "[" + name + "]\n" + "Axe with\n" + attackValue + " attack.";
    }
}
