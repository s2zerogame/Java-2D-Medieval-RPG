package main.java.com.khomsi.game.entity;

import main.java.com.khomsi.game.main.GamePanel;
import main.java.com.khomsi.game.main.tools.Tools;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

//parent class for player, monster ect
public class Entity {
    //Set default position
    public int worldX, worldY;
    public int speed;
    //we store our images in this variables
    public BufferedImage up, up1, up2, up3, down, down1, down2, down3,
            left, left1, left2, left3, right, right1, right2, right3;
    public String direction;
    public int spriteCounter = 0;
    public int standCounter = 0;
    public int spriteNum = 0;
    //Default values for every entity
    public Rectangle solidArea = new Rectangle(8, 16, 31, 32);
    public int solidAreaDefaultX, solidAreaDefaultY;
    public boolean collisionOn = false;
    public int lockCounter = 0;
    String[] dialogues = new String[20];
    int dialogIndex = 0;
    GamePanel gamePanel;
    Tools tools = new Tools();

    public Entity(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    public void setAction() {

    }

    public void speak() {
        if (dialogues[dialogIndex] == null) {
            dialogIndex = 0;
        }
        gamePanel.ui.currentDialog = dialogues[dialogIndex];
        dialogIndex++;
        switch (gamePanel.player.direction) {
            case "up" -> direction = "down";
            case "down" -> direction = "up";
            case "left" -> direction = "right";
            case "right" -> direction = "left";
        }
    }

    public void update() {
        setAction();
        collisionOn = false;
        gamePanel.checkCollision.checkTile(this);
        gamePanel.checkCollision.checkObject(this, false);
        gamePanel.checkCollision.checkPlayer(this);

        spriteMovement();
    }

    public void spriteMovement() {
        if (!collisionOn) {
            switch (direction) {
                case "up" -> worldY -= speed;
                case "down" -> worldY += speed;
                case "left" -> worldX -= speed;
                case "right" -> worldX += speed;
            }
        }
        //Changing sprites, depends on nums
        spriteCounter++;
        if (spriteCounter <= 13) {
            spriteNum = 1;
        }
        if (spriteCounter > 13 && spriteCounter <= 23) {
            spriteNum = 2;
        }
        if (spriteCounter > 23 && spriteCounter <= 33) {
            spriteNum = 3;
        }
        if (spriteCounter > 33 && spriteCounter <= 42) {
            spriteNum = 2;
        }
        if (spriteCounter > 42) {
            spriteCounter = 0;
        } else {
            standCounter++;
            //timer before the idle anim starts
            if (standCounter == 42) {
                spriteNum = 0;  // Idle sprite
                standCounter = 0;
            }
        }
    }

    public void draw(Graphics2D graphics2D) {
        //actual coords to draw the stuff on game screen
        int screenX = worldX - gamePanel.player.worldX + gamePanel.player.screenX;
        int screenY = worldY - gamePanel.player.worldY + gamePanel.player.screenY;
        BufferedImage image = characterSpriteDirection();
        tools.optimizeMapDraw(graphics2D, image, gamePanel, screenX, screenY, worldX, worldY);
    }

    //Use this method to change the sprite direction of character
    public BufferedImage characterSpriteDirection() {
        BufferedImage image = null;

        switch (direction) {
            case "up" -> {
                if (spriteNum == 1) image = up1;
                if (spriteNum == 2) image = up2;
                if (spriteNum == 3) image = up3;
                if (spriteNum == 0) image = up;
            }
            case "down" -> {
                if (spriteNum == 1) image = down1;
                if (spriteNum == 2) image = down2;
                if (spriteNum == 3) image = down3;
                if (spriteNum == 0) image = down;
            }
            case "left" -> {
                if (spriteNum == 1) image = left1;
                if (spriteNum == 2) image = left2;
                if (spriteNum == 3) image = left3;
                if (spriteNum == 0) image = left;
            }
            case "right" -> {
                if (spriteNum == 1) image = right1;
                if (spriteNum == 2) image = right2;
                if (spriteNum == 3) image = right3;
                if (spriteNum == 0) image = right;
            }
        }
        return image;
    }

    public BufferedImage setup(String imagePath) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(Objects.requireNonNull(
                    getClass().getResourceAsStream(imagePath + ".png")));
            image = tools.scaledImage(image, GamePanel.TILE_SIZE, GamePanel.TILE_SIZE);
        } catch (IOException e) {
            System.err.println("Error in setup in " + getClass().getSimpleName());
            e.printStackTrace();
        }
        return image;
    }
}
