package main.java.com.khomsi.game.objects.gui;

import main.java.com.khomsi.game.entity.Entity;
import main.java.com.khomsi.game.main.GameManager;

public class HeartObject extends Entity {
    public HeartObject(GameManager gameManager) {
        super(gameManager);
        type = TYPE_PICK_UP_ONLY;
        value = 2;
        down = setup("/objects/ui/heart_full",
                GameManager.TILE_SIZE * 2, GameManager.TILE_SIZE * 2);

        name = "Heart";
        image = setup("/objects/ui/heart_full",
                GameManager.TILE_SIZE * 2, GameManager.TILE_SIZE * 2);
        image2 = setup("/objects/ui/heart_half",
                GameManager.TILE_SIZE * 2, GameManager.TILE_SIZE * 2);
        image3 = setup("/objects/ui/heart_empty",
                GameManager.TILE_SIZE * 2, GameManager.TILE_SIZE * 2);
    }

    @Override
    public boolean use(Entity entity) {
        gameManager.playSE(6);
        gameManager.ui.addMessage("Life +" + value);
        entity.hp += value;
        return true;
    }
}
