package main.java.com.khomsi.game.objects.projectTiles;

import main.java.com.khomsi.game.entity.Entity;
import main.java.com.khomsi.game.entity.ProjectTile;
import main.java.com.khomsi.game.main.GameManager;

public class FireBallObject extends ProjectTile {
    public FireBallObject(GameManager gameManager) {
        super(gameManager);
        name = "FireBall";
        speed = 7;
        maxHp = 80;
        hp = maxHp;
        attack = 2;
        useCost = 1;
        // if the previous tile is still on the screen,
        // you can't shoot next one
        alive = false;
        itemDescription = "[" + name + "]\n" + "Fireball with\n" + attack + " attack.";
        getImage();
    }

    public void getImage() {
        up = setup("/projectiles/fireballs/blue/fireball_blue_up");
        up1 = setup("/projectiles/fireballs/blue/fireball_blue_up_1");
        up2 = setup("/projectiles/fireballs/blue/fireball_blue_up_2");
        up3 = setup("/projectiles/fireballs/blue/fireball_blue_up_3");

        down = setup("/projectiles/fireballs/blue/fireball_blue_down");
        down1 = setup("/projectiles/fireballs/blue/fireball_blue_down_1");
        down2 = setup("/projectiles/fireballs/blue/fireball_blue_down_2");
        down3 = setup("/projectiles/fireballs/blue/fireball_blue_down_3");

        left = setup("/projectiles/fireballs/blue/fireball_blue_left");
        left1 = setup("/projectiles/fireballs/blue/fireball_blue_left_1");
        left2 = setup("/projectiles/fireballs/blue/fireball_blue_left_2");
        left3 = setup("/projectiles/fireballs/blue/fireball_blue_left_3");

        right = setup("/projectiles/fireballs/blue/fireball_blue_right");
        right1 = setup("/projectiles/fireballs/blue/fireball_blue_right_1");
        right2 = setup("/projectiles/fireballs/blue/fireball_blue_right_2");
        right3 = setup("/projectiles/fireballs/blue/fireball_blue_right_3");
    }

    @Override
    public boolean haveResource(Entity entity) {
        return entity.mana >= useCost;
    }

    @Override
    public void subtractResource(Entity entity) {
        entity.mana -= useCost;
    }
}
