import javax.swing.*;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

public class AsteroidGameController extends JComponent implements ActionListener, Runnable
{
    public JFrame space = new JFrame();
    private int widthOfScreen = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
    private int heightOfScreen = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;
    private Image spaceImage = Toolkit.getDefaultToolkit().createImage(getClass().getResource("spacePicture.jpg"));
    public Timer ticker = new Timer(30, this);// THIS IS THE TIMER
    public int[] asteroid1XPoints =
            { 21, 16, 20, 15, 0, -19, -17, -21, -15 };
    public int[] asteroid1YPoints =
            { 24, 19, 18, 16, 17, 24, 20, 17, 18 };
    private int speedOfShip = 0;
    private int speedLimitOfShip = 10;
    private int middleScreenXPos = widthOfScreen / 2;
    private int middleScreenYPos = heightOfScreen / 2;
    private int directionOfHeadOfShip = 90; // degrees
    public int colorChangeController;
    public int colorChanger = (int) directionOfHeadOfShip - colorChangeController;
    public ArrayList<Asteroid> asteroidList = new ArrayList<>();
    public ArrayList<Laser> projectileList = new ArrayList<>();
    private Laser shot;
    public AffineTransform identity = new AffineTransform(); // identity
    public Random r = new Random();
    public int asteroidSpawnQuadrantPicker;
    public Ship spaceDrone = new Ship(middleScreenXPos, middleScreenYPos, widthOfScreen, heightOfScreen);
    public Utilities util = new Utilities(spaceDrone, projectileList);
    private int firingRateDelay = (100);
    public Timer shotTicker = new Timer(firingRateDelay, util);
    private Timer powerUpTimeLimit = new Timer(100, util);
    private Timer endingDelayTicker = new Timer(300, null);
    private int score;
    public URL soundAddress;
    public AudioClip soundFile;
    public boolean shipDestroyed;
    private int asteroidSpeedLimit = 5;
    private int fastAsteroidSpeed = 15;
    private int asteroidDestroyedNumber = 1;
    private int fastAsteroidCounter;
    private int fastAsteroidInterval = 22;
    private int powerUpCounter;
    private int powerUpInterval = 20;
    private int gameWinQuota = 100;
    private int asteroidLimit = 14;
    private double asteroidScaleFactor = 1.5;
    private double asteroidSpeed;
    private PowerUp powerUp = new PowerUp(-1000, -1000, r.nextInt(90), 0, 3, false, false);
    public ArrayList<PowerUp> powerUpList = new ArrayList<>();

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new AsteroidGameController());
    }

    public void run()
    {
        /*********************************************************
         * spawn asteroids
         *********************************************************/
        JOptionPane.showMessageDialog(space,
                "Greetings untrained third rate drone pilot! Thank you for accepting this job!\nGenericFuturisticIndustries Inc. LTD needs your help in clearing the space around this quadrant of asteroids to prepare for space station construction\n\nPlease enjoy this irish music while you do your job :)");
        for (int j = 0; j < asteroidLimit; j++)
        {
            asteroidSpawner();
        }
        spaceDrone.setScreenHeight(heightOfScreen);
        spaceDrone.setScreenWidth(widthOfScreen);
        ticker.start();// repainting timer
        shotTicker.start();
        space.setSize(widthOfScreen, heightOfScreen);
        space.setVisible(true);
        space.setDefaultCloseOperation(space.EXIT_ON_CLOSE);
        space.add(this);
        space.setBackground(Color.BLACK);
        space.setTitle("HEY! GUESS WHAT? I'M A TITLE!");
        space.addKeyListener(util);
//		util.playMusic(); // TURN THIS ON TO ALLOW MUSIC TO BE PLAYED

    }
    public void powerUpSpawner()
    {
        switch (new Random().nextInt(4))
        {
            // powerUpXPos, powerUpYPos, course, speed, rotation,
            // isTouchingShip,
            // isTouchingLaser
            case 0: // north
                powerUp = new PowerUp(r.nextInt(widthOfScreen), 0, r.nextInt(90) - 135, 5, 3, false, false);
                break;
            case 1: // south
                powerUp = new PowerUp(r.nextInt(widthOfScreen), heightOfScreen, r.nextInt(90) + 45, 5, 3, false, false);
                break;
            case 2: // east
                powerUp = new PowerUp(widthOfScreen, r.nextInt(heightOfScreen), r.nextInt(90) - 225, 10, 3, false, false);
                break;
            case 3: // west
                powerUp = new PowerUp(0, r.nextInt(heightOfScreen), r.nextInt(100) - 50, 5, 3, false, false);
                break;
        }
        powerUpCounter = 0;
        // if (Area powerUpArea = new Area(powerUp.collisionArea))
        {

        }
    }

    // TO DO:
    // Add in the power up
    // Make the asteroid collision radius adjust with the asteroid size

    public void asteroidSpawner() // Try to find a way to combine these commands
    // into one in order to reduce space taken.
    {
        asteroidSpeed = (Math.random() * asteroidSpeedLimit) + 2;
        if (fastAsteroidCounter >= fastAsteroidInterval)
        {
            fastAsteroidCounter = 15;
            asteroidSpeed = (int) (Math.random() * fastAsteroidSpeed) + 6;
        }
        asteroidScaleFactor = (asteroidScaleFactor * Math.random()) + .8;
        asteroidSpawnQuadrantPicker = r.nextInt(4);
        switch (asteroidSpawnQuadrantPicker)
        {
            case 0: // west
                asteroidList.add(new Asteroid(-50, r.nextInt(heightOfScreen), r.nextInt(90) - 45, (int) asteroidSpeed, asteroidScaleFactor, Math.random(), true));
                break;
            case 1: // narth
                asteroidList.add(new Asteroid(r.nextInt(widthOfScreen), -50, r.nextInt(90) - 135, (int) asteroidSpeed, asteroidScaleFactor, Math.random(), true));
                break;
            case 2: // east
                asteroidList.add(new Asteroid(widthOfScreen + 50, r.nextInt(heightOfScreen), r.nextInt(90) - 225, (int) asteroidSpeed, asteroidScaleFactor, Math.random(), true));
                break;
            case 3: // south
                asteroidList.add(new Asteroid(r.nextInt(widthOfScreen), heightOfScreen + 50, r.nextInt(90) + 45, (int) asteroidSpeed, asteroidScaleFactor, Math.random(), true));
                break;
        }
    }

    public void asteroidPieceCreator(int asteroidXPos, int asteroidYPos, int course, double speed, double scaleFactor)
    {
        asteroidList.add(new Asteroid(asteroidXPos, asteroidYPos, r.nextInt(360) - 45, (int) (Math.random() * asteroidSpeedLimit) + 3, asteroidScaleFactor / 2, 0.001, false));
        // xpos, ypos, course, speed, scale factor, rotation speed, fragment
    }
    @Override
    public void actionPerformed(ActionEvent e)
    {
        double rotationDegree = Math.toRadians(directionOfHeadOfShip);
        spaceDrone = util.shipMovementRegulator(rotationDegree, directionOfHeadOfShip, speedOfShip, speedLimitOfShip, colorChangeController);
        repaint();// calls on paint
    }

    public void paint(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;
        g2.setTransform(identity);
        g2.scale((double) widthOfScreen / spaceImage.getWidth(this), (double) heightOfScreen / spaceImage.getHeight(this));
        g2.drawImage(spaceImage, 0, 0, null, null);
        g2.setColor(Color.green);
        g2.drawRect(widthOfScreen / 2, heightOfScreen / 4 - 50, 80, 50);
        g2.setColor(Color.white);
        g2.drawString("Arrow keys to move\nHold space to shoot", widthOfScreen / 4, heightOfScreen / 3);
        g2.setFont(new Font("Sans", Font.PLAIN, 40));
        g2.drawString("" + score, (widthOfScreen / 2), heightOfScreen / 4);
        g2.setTransform(identity);
        if (powerUpCounter > (powerUpInterval + r.nextInt(20)) && powerUp.getIsPowerUpOnScreen() == false)
        {
            powerUpSpawner();
            System.out.println(powerUpInterval + r.nextInt(20));
        }
        spaceDrone.paintShip(g2);
        powerUp.paintPowerUp(g2);
        for (int i = 0; i < asteroidList.size(); i++)
        {
            g2.setTransform(identity); // cleans up screen
            asteroidList.get(i).paintAsteroid(g2);
            Asteroid asteroid = asteroidList.get(i);
            Area asteroidArea = new Area(asteroid.collisionArea);
            AffineTransform asteroidAT = new AffineTransform();
            asteroidAT.setToTranslation(asteroidList.get(i).asteroidXPos, asteroidList.get(i).asteroidYPos);
            asteroidArea.transform(asteroidAT);
            if (util.isOffScreen(asteroid.asteroidXPos, asteroid.asteroidYPos, widthOfScreen, heightOfScreen))
            {
                asteroidList.remove(i);
                if (asteroidDestroyedNumber < gameWinQuota)
                {
                    asteroidSpawner();
                } // fast asteroid spawner
            }
            for (int j = 0; j < projectileList.size(); j++) // checking all
            // bullets
            {
                shot = projectileList.get(j);
                Area shotArea = new Area(shot.shotShape);
                AffineTransform shotAT = new AffineTransform();
                shotAT.setToTranslation(shot.laserXPos, shot.laserYPos);
                shotArea.transform(shotAT);
                shotArea.intersect(asteroidArea); // ***** Do this for ship area
                // and power up area

                if (util.isOffScreen(shot.laserXPos, shot.laserYPos, widthOfScreen, heightOfScreen))
                {
                    projectileList.remove(j);
                }
                if (!shotArea.isEmpty()) // asteroid Hit
                {
                    int iXPos = asteroidList.get(i).asteroidXPos;
                    int iYPos = asteroidList.get(i).asteroidYPos;
                    asteroidList.remove(i);
                    projectileList.remove(j);
                    asteroidDestroyedNumber++;
                    fastAsteroidCounter++;
                    powerUpCounter++;
                    score = score + 1;
                    if (asteroid.isAWholePiece)
                    {
                        for (int k = 0; k < 3; k++)
                        {
                            asteroidPieceCreator(iXPos, iYPos, 33, Math.random() * asteroidSpeedLimit, .3);
                        }
                    }
                }
            }
            Area leftShipArea = new Area(spaceDrone.shipLeftSide);
            Area rightShipArea = new Area(spaceDrone.shipRightSide);
            AffineTransform spaceDroneAT = new AffineTransform();
            spaceDroneAT.setToTranslation(spaceDrone.getShipXPos(), spaceDrone.getShipYPos());
            leftShipArea.transform(spaceDroneAT);
            leftShipArea.intersect(asteroidArea);
            rightShipArea.intersect(asteroidArea);
            if (!leftShipArea.isEmpty()) // Need help figuring out how to
            // differentiate between asteroid
            // collision and power up collision
            {
                spaceDrone.canopy.reset();
                spaceDrone.shipLeftSide.reset();
                spaceDrone.shipRightSide.reset();
                spaceDrone.setShipDestroyed(true);
                util.setShipDestroyed(true);
                util.playExplosionSound();
                endingDelayTicker.start();
                if (endingDelayTicker.isRunning()) // CHANGE THIS MECHANIC SO
                // THAT THE MESSAGE IS
                // DELAYED BY THE TIMER
                {
                    JOptionPane.showMessageDialog(null, "You suck");
                    int tryAgain = JOptionPane.showConfirmDialog(null, "Would you like to try again?");
                    if (tryAgain == 0)
                    {
                        space.dispose();
//						util.stopMusic();
                        new AsteroidGameController().run();
                    }
                    if (tryAgain == 2)
                    {
                    }
                    if (tryAgain == 1)
                    {
                        System.exit(0);
                    }
                }

            }
            if (!leftShipArea.isEmpty())
            {
//				if (power.isEmpty())
//				{
//					
//					powerUpTimeLimit.start();
//					firingRateDelay = 100;
//					System.out.println("collision");
//				}
            }
            // if (powerUpTimeLimit > firingRateDelay)
            // {
            //
            // }
            if (asteroidList.size() < 1)
            {
                util.gameComplete = true;
                JOptionPane.showMessageDialog(null, "Congratulations! Your job is complete!\nYou win!\nMaybe you're not so third rate after all.");
                int tryAgain = JOptionPane.showConfirmDialog(null, "Would you like to play again?");
                if (tryAgain == 0)
                {
                    space.dispose();
//					util.stopMusic();
                    new AsteroidGameController().run();
                }
                if (tryAgain == 2)
                {
                }
                if (tryAgain == 1)
                {
                    System.exit(0);
                }
            }
        }
        for (int j = 0; j < projectileList.size(); j++) // checking all bullets
        {
            Laser shot = projectileList.get(j);
            g2.setTransform(identity);
            shot.paintProjectile(g2);
        }
    }
}