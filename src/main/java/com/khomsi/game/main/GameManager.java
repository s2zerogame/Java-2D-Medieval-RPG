package main.java.com.khomsi.game.main;

import main.java.com.khomsi.game.GameApplication;
import main.java.com.khomsi.game.ai.PathFinder;
import main.java.com.khomsi.game.data.SaveLoad;
import main.java.com.khomsi.game.entity.Entity;
import main.java.com.khomsi.game.entity.Player;
import main.java.com.khomsi.game.enviroment.EnvironmentManagement;
import main.java.com.khomsi.game.main.logic.CheckCollision;
import main.java.com.khomsi.game.data.Config;
import main.java.com.khomsi.game.main.logic.EntityGenerator;
import main.java.com.khomsi.game.main.logic.EventHandler;
import main.java.com.khomsi.game.main.tools.*;
import main.java.com.khomsi.game.tiles.Map;
import main.java.com.khomsi.game.tiles.TileManager;
import main.java.com.khomsi.game.tilesinteractive.InteractiveTile;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class GameManager extends JPanel implements Runnable {
    //Screen settings

    static final int ORIGINAL_TILE_SIZE = 16; //16x16 tiles

    //we need to scale the size of hero and game, because on big screens it'll be too small
    static final int SCALE = 3;
    public static final int TILE_SIZE = ORIGINAL_TILE_SIZE * SCALE; //48x48 tiles
    public static final int MAX_SCREEN_COL = 20; //20 tiles horizontal
    public static final int MAX_SCREEN_ROW = 12; //12 tiles vertical
    public static final int SCREEN_WIDTH = TILE_SIZE * MAX_SCREEN_COL; //960 pixels
    public static final int SCREEN_HEIGHT = TILE_SIZE * MAX_SCREEN_ROW; //576 pixels

    //World settings
    public static int maxWorldCol = 50;

    public static int maxWorldRow = 50;
    //TODO change if need to create more maps
    public final int maxMap = 10;
    public int currentMap = 0;
    //For full screen
    int screenWidthFull = SCREEN_WIDTH;
    int screenHeightFull = SCREEN_HEIGHT;
    BufferedImage tempScreen;
    Graphics2D g2d;


    //TOOLS FOR GAME
    public KeyHandler keyHandler = new KeyHandler(this);
    public TileManager tileManager = new TileManager(this);
    public Sound music = new Sound();
    public Sound se = new Sound();
    public UI ui = new UI(this);
    //use threads to start, stop,repeat actions.
    public Thread gameThread;
    public EntityGenerator entityGenerator = new EntityGenerator(this);
    public SaveLoad saveLoad = new SaveLoad(this);
    public Config config = new Config(this);
    public CheckCollision checkCollision = new CheckCollision(this);
    public PlaceObjects placeObjects = new PlaceObjects(this);
    public EventHandler eventHandler = new EventHandler(this);
    public PathFinder pathFinder = new PathFinder(this);
    public EnvironmentManagement enManagement = new EnvironmentManagement(this);
    public Map map = new Map(this);
    //ENTITY AND OBJECTS
    //TODO extend the massive, when you'll have more objects
    public Entity[][] object = new Entity[maxMap][20];
    public Entity[][] npcList = new Entity[maxMap][10];
    public Entity[][] mobs = new Entity[maxMap][10];
    List<Entity> entities = new ArrayList<>();
    public InteractiveTile[][] interactTile = new InteractiveTile[maxMap][50];
    public Entity[][] projectile = new Entity[maxMap][20];
    public List<Entity> particleList = new ArrayList<>();

    public Player player = new Player(this, keyHandler);

    public static final int FPS = 60;
    //GameState
    public int gameState;

    public static final int TITLE_STATE = 0;

    public static final int PLAY_STATE = 1;
    public static final int PAUSE_STATE = 2;
    public static final int DIALOGUE_STATE = 3;
    public static final int CHARACTER_STATE = 4;
    public static final int OPTION_STATE = 5;
    public static final int GAME_OVER_STATE = 6;
    public static final int TRANSITION_STATE = 7;
    public static final int TRADE_STATE = 8;
    public static final int SLEEP_STATE = 9;
    public static final int MAP_STATE = 10;
    //Area
    public int currentArea;
    public int nextArea;
    public static final int OUTSIDE = 11;
    public static final int INDOOR = 12;
    public static final int DUNGEON = 13;
    //Until player doesn't press shift, he doesn't run
    public boolean playerRun = false;
    public boolean fullScreenOn = false;

    public GameManager() {
        //set size of this class
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        //if true, everything drawing from this comp will be done in an offscreen painting buffer.
        //it can improve the performance of rendering
        this.setDoubleBuffered(true);
        this.addKeyListener(keyHandler);
        //focus to receive key input
        this.setFocusable(true);
    }

    public void setupGame() {
        setDefaultObjects();
        enManagement.setup();
        gameState = TITLE_STATE;
        currentArea = OUTSIDE;
        tempScreen = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        //Everything will be recorded in buff tempScreen
        g2d = (Graphics2D) tempScreen.getGraphics();
        if (fullScreenOn)
            setFullScreen();
    }

    public void resetGame(boolean restart) {
        player.setDefaultPosition();
        player.restoreStatus();
        player.resetCounters();
        placeObjects.setNpc();
        placeObjects.setMobs();

        if (restart) {
            player.setDefaultValues();
            placeObjects.setObject();
            placeObjects.setInteractiveTiles();
            enManagement.lightning.resetDay();
        }
    }

    private void setDefaultObjects() {
        placeObjects.setObject();
        placeObjects.setNpc();
        placeObjects.setMobs();
        placeObjects.setInteractiveTiles();
    }

    public void setFullScreen() {
        //Get local screen device
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice graphicsDevice = ge.getDefaultScreenDevice();
        graphicsDevice.setFullScreenWindow(GameApplication.window);
        //Get full screen width/height
        screenWidthFull = GameApplication.window.getWidth();
        screenHeightFull = GameApplication.window.getHeight();
    }

    public void startGameThread() {
        //pass gamePanel to thread
        gameThread = new Thread(this);
        //automatically calls run method
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1_000_000_000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        int drawCount = 0;

        while (gameThread != null) {
            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;

            if (delta >= 1) {
                //update info as character position
                update();
                //draw the screen info with updated info
                drawToTempScreen();
                drawToScreen();
                delta--;
                drawCount++;
            }
            if (timer >= 1_000_000_000 && keyHandler.debugMode) {
                System.out.println("FPS: " + drawCount);
                drawCount = 0;
                timer = 0;
            }
        }
    }

    public void update() {
        //Player loop
        if (gameState == PLAY_STATE) {
            player.update();
            //Npc loop
            for (Entity npc : npcList[currentMap])
                if (npc != null)
                    npc.update();

            //Mob loop
            for (int i = 0; i < mobs[1].length; i++) {
                if (mobs[currentMap][i] != null) {
                    if (mobs[currentMap][i].alive && !mobs[currentMap][i].die)
                        mobs[currentMap][i].update();

                    if (!mobs[currentMap][i].alive) {
                        mobs[currentMap][i].checkDrop();
                        mobs[currentMap][i] = null;
                    }
                }
            }
            //ProjectTiles loop
            for (int i = 0; i < projectile.length; i++) {
                if (projectile[currentMap][i] != null) {
                    if (projectile[currentMap][i].alive) {
                        projectile[currentMap][i].update();
                    }
                    if (!projectile[currentMap][i].alive) {
                        projectile[currentMap][i] = null;
                    }
                }
            }
            //ParticleTiles loop
            updateLoopArrays(particleList);
            //Interactive tiles loop
            for (InteractiveTile tile : interactTile[currentMap]) {
                if (tile != null) {
                    tile.update();
                }
            }
            enManagement.update();
        }
        if (gameState == PAUSE_STATE) {
            //Stop game
        }
    }

    //method to draw the components on screen
    public void drawToTempScreen() {
        long drawStart = 0;
        if (keyHandler.debugMode)
            drawStart = System.nanoTime();

        //Title screen
        if (gameState == TITLE_STATE) {
            ui.draw(g2d);
        }
        //Map screen
        else if (gameState == MAP_STATE) {
            map.drawFullScreenMap(g2d);
        }
        //others
        else {
            //Draw Tiles
            tileManager.draw(g2d);
            entities.add(player);
            //Add and render npc, obj, mobs, projectiles to draw list
            drawMethodArray(npcList);
            drawMethodArray(object);
            drawMethodArray(mobs);
            //Interactive tiles
            drawMethodArray(interactTile);
            drawMethodArray(projectile);
            drawMethodList(particleList);

            //Sort entities
            entities.sort(new EntityComparator());

            //Draw them
            for (Entity entity : entities) {
                entity.draw(g2d);
            }
            //Make list empty to not overload it
            entities.clear();
            //Environment
            enManagement.draw(g2d);
            //Mini map
            map.drawMiniMap(g2d);
            //UI(text)
            ui.draw(g2d);
        }
        debugFunction(g2d, drawStart);
    }

    private void drawMethodList(List<Entity> list) {
        for (Entity object : list) {
            if (object != null)
                entities.add(object);
        }
    }

    private void drawMethodArray(Entity[][] array) {
        for (Entity element : array[currentMap]) {
            if (element != null)
                entities.add(element);
        }
    }

    private void debugFunction(Graphics2D graphics2D, long drawStart) {
        //Debug function
        if (keyHandler.debugMode) {
            long drawEnd = System.nanoTime();
            long passed = drawEnd - drawStart;
            graphics2D.setFont(new Font("Arial", Font.PLAIN, 20));
            graphics2D.setColor(Color.WHITE);
            int x = 10;
            int y = 410;
            int lineHeight = 20;
            graphics2D.drawString("World_X: " + player.worldX, x, y);
            y += lineHeight;
            graphics2D.drawString("World_Y: " + player.worldY, x, y);
            y += lineHeight;
            graphics2D.drawString("Col: " + (player.worldX + player.solidArea.x) / TILE_SIZE, x, y);
            y += lineHeight;
            graphics2D.drawString("Row: : " + (player.worldY + player.solidArea.y) / TILE_SIZE, x, y);
            y += lineHeight;
            graphics2D.drawString("Invincible: : " + player.invincibleCounter, x, y);
            y += lineHeight;
            graphics2D.drawString("Draw Time: " + passed, x, y);
            y += lineHeight;
            graphics2D.drawString("Player's speed: " + player.speed, x, y);
            y += lineHeight;
            graphics2D.drawString("Press Ctrl+F9 after ed map", x, y);
            y += lineHeight;
            graphics2D.drawString("Press F8 to reload tiles", x, y);
            //Show player bounds
            graphics2D.setColor(Color.RED);
            graphics2D.setStroke(new BasicStroke(2));
            graphics2D.drawRect(player.screenX + player.solidArea.x, player.screenY + player.solidArea.y,
                    player.solidArea.width, player.solidArea.height);
        }
    }

    public void updateLoopArrays(List<Entity> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) != null) {
                if (list.get(i).alive) {
                    list.get(i).update();
                }
                if (!list.get(i).alive) {
                    list.remove(i);
                }
            }
        }
    }

    //FIXME full screen problem on current laptop, test on another one
    public void drawToScreen() {
        Graphics graphics = getGraphics();
        graphics.drawImage(tempScreen, 0, 0, screenWidthFull, screenHeightFull, null);
        graphics.dispose();
    }

    //use method to loop the main music
    public void playMusic(int i) {
        music.setFile(i);
        music.play();
        music.loop();
    }

    public void stopMusic() {
        music.stop();
    }

    public void pauseMusic(int time) {
        stopMusic();
        Timer timer = new Timer(time * 1000, arg0 -> playMusic(0));
        timer.setRepeats(false); // Only execute once
        timer.start(); // Go go go!
    }

    public void playSE(int i) {
        se.setFile(i);
        se.play();
    }

    public void changeArea() {
        if (nextArea != currentArea) {
            stopMusic();
            if (nextArea == OUTSIDE) playMusic(0);
            if (nextArea == INDOOR) playMusic(19);
            if (nextArea == DUNGEON) playMusic(20);
        }
        currentArea = nextArea;
        placeObjects.setMobs();
    }
}
