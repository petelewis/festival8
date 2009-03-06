/*
Copyright 2006 by Sean Luke and George Mason University
Licensed under the Academic Free License version 3.0
See the file "LICENSE" for more information
 *
 * Adapted by Peter Lewis 2009
 */
package festival;

import sim.portrayal.continuous.*;
import sim.engine.*;
import sim.display.*;
import javax.swing.*;
import java.awt.Color;

public class FestivalWithUI extends GUIState {

    public Display2D display;
    public JFrame displayFrame;
    ContinuousPortrayal2D coPortrayal = new ContinuousPortrayal2D();

    public static void main(String[] args) {
        FestivalWithUI co = new FestivalWithUI();
        Console c = new Console(co);
        c.setVisible(true);
    }

    public FestivalWithUI() {
        super(new FestivalNoUI(System.currentTimeMillis()));
    }

    public FestivalWithUI(SimState state) {
        super(state);
    }

    public static String getName() {
        return "Festival";
    }

    @Override
    public void start() {
        super.start();
        setupPortrayals();
    }

    @Override
    public void load(SimState state) {
        super.load(state);
        setupPortrayals();
    }

    public void setupPortrayals() {
        // tell the portrayals what to portray and how to portray them
        coPortrayal.setField(((FestivalNoUI) state).environment);
        // reschedule the displayer
        display.reset();
        display.setBackdrop(Color.green);

        StagePortrayal overlayStage1 = new StagePortrayal(0, 370, 20, 60, "red");
        display.attach(overlayStage1, "Stage1");

        StagePortrayal overlayStage2 = new StagePortrayal(780, 370, 20, 60, "red");
        display.attach(overlayStage2, "Stage2");

        BarPortrayal overlayBar = new BarPortrayal(400, 750, 50, 50, "orange");
        display.attach(overlayBar, "Bar");
        FoodstallPortrayal overlayBurgerking = new FoodstallPortrayal(10, 10, 50, 50, "orange");
        display.attach(overlayBurgerking, "Burgerking");

        ToiletPortrayal overlayToilet = new ToiletPortrayal(790, 10, 70, 70, "yellow");
        display.attach(overlayToilet, "Toilet");

        // redraw the display
        display.repaint();
    }

    @Override
    public void init(Controller c) {
        super.init(c);

        // make the displayer
        display = new Display2D(800, 700, this, 1);

        displayFrame = display.createFrame();
        displayFrame.setTitle("Festival");
        c.registerFrame(displayFrame);   // register the frame so it appears in the "Display" list
        displayFrame.setVisible(true);
        display.attach(coPortrayal, "People");
    }

    @Override
    public void quit() {
        super.quit();

        if (displayFrame != null) {
            displayFrame.dispose();
        }
        displayFrame = null;
        display = null;
    }
}
