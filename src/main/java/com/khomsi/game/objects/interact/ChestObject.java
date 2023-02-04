package main.java.com.khomsi.game.objects.interact;

import main.java.com.khomsi.game.entity.Entity;
import main.java.com.khomsi.game.main.GameManager;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Objects;

public class ChestObject extends Entity {
    Entity loot;
    boolean opened = false;

    public ChestObject(GameManager gameManager, Entity loot) {
        super(gameManager);
        this.loot = loot;
        type = TYPE_OBSTACLE;
        name = "Chest";
        image = setup("/objects/chest");
        image2 = setup("/objects/chest_opened");
        down = image;
        collision = true;
        solidArea.x = 4;
        solidArea.y = 16;
        solidArea.width = 40;
        solidArea.height = 32;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
    }

    @Override
    public void interact() {
        gameManager.gameState = gameManager.dialogueState;
        if (!opened) {
            gameManager.playSE(4);
            StringBuilder sb = new StringBuilder();
            sb.append("You opened the chest and found a ").append(loot.name).append("!");
            if (!gameManager.player.canObtainItem(loot)) {
                sb.append("\n...But you can't carry any more!");
            } else {
                sb.append("\nYou obtain the ").append(loot.name).append("!");
                down = image2;
                opened = true;
            }
            gameManager.ui.currentDialog = sb.toString();
        } else {
            gameManager.ui.currentDialog = "This chest is empty!";
        }
    }
}
