
package org.magnos.steer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.magnos.steer.behavior.SteerArriveExample;
import org.magnos.steer.behavior.SteerArriveOffsetExample;
import org.magnos.steer.behavior.SteerAvoidExample;
import org.magnos.steer.behavior.SteerAvoidObstaclesExample;
import org.magnos.steer.behavior.SteerAvoidWallExample;
import org.magnos.steer.behavior.SteerAwayExample;
import org.magnos.steer.behavior.SteerBasicExample;
import org.magnos.steer.behavior.SteerControllerImmediateExample;
import org.magnos.steer.behavior.SteerDodgeExample;
import org.magnos.steer.behavior.SteerDriveExample;
import org.magnos.steer.behavior.SteerEvadeExample;
import org.magnos.steer.behavior.SteerFlockingExample;
import org.magnos.steer.behavior.SteerFlowFieldExample;
import org.magnos.steer.behavior.SteerPathExample;
import org.magnos.steer.behavior.SteerPathResetExample;
import org.magnos.steer.behavior.SteerPursuitExample;
import org.magnos.steer.behavior.SteerToExample;
import org.magnos.steer.behavior.SteerWanderExample;
import org.magnos.steer.spatial.SpatialDatabaseExample;
import org.magnos.steer.target.TargetAverageExample;
import org.magnos.steer.target.TargetClosestExample;
import org.magnos.steer.target.TargetFacingExample;
import org.magnos.steer.target.TargetFutureExample;
import org.magnos.steer.target.TargetInLineExample;
import org.magnos.steer.target.TargetInterposeExample;
import org.magnos.steer.target.TargetLocalExample;
import org.magnos.steer.target.TargetOffsetExample;
import org.magnos.steer.target.TargetSlowestExample;
import org.magnos.steer.target.TargetWeakestExample;

import com.gameprogblog.engine.Game;
import com.gameprogblog.engine.GameLoop;
import com.gameprogblog.engine.GameLoopVariable;
import com.gameprogblog.engine.GameScreen;
import com.gameprogblog.engine.GameState;
import com.gameprogblog.engine.Scene;
import com.gameprogblog.engine.input.GameInput;


public class MasterExample implements Game
{

    public static void main( String[] args ) throws Exception
    {
        MasterExample game = new MasterExample();
        GameLoop loop = new GameLoopVariable( 0.1f );
        GameScreen screen = new GameScreen( SteerBasicExample.DEFAULT_WIDTH, SteerBasicExample.DEFAULT_HEIGHT, true, loop, game );
        screen.setBackground( Color.black );
        game.screen = screen;
        game.window = GameScreen.showWindow( screen, "MasterExample", false );
        game.setExample( 0 );
        screen.start();
    }

    @SuppressWarnings ({ "serial" })
    private static Map<Class<? extends Game>, String> EXAMPLES = new LinkedHashMap<Class<? extends Game>, String>() {{
        /* behavior */
        put( SteerArriveExample.class, "The blue subject moves towards the mouse, and when the mouse is inside it's white caution circle, it slows down until it stops at the point." );
        put( SteerArriveOffsetExample.class, "A chain of subjects that need to arrive a fixed amount behind another subject (or the mouse for the first entity)." );
        put( SteerAvoidExample.class, "The yellow subject moves away from the blue subject when it enters the red circle and it is infront of the blue subject (i.e. in view)." );
        put( SteerAvoidObstaclesExample.class, "A set of subjects are moving in a constant direction and they are trying to avoid collisions with each other." );
        put( SteerAvoidWallExample.class, "The blue entity is stuck in a room made up of 4 walls." );
        put( SteerAwayExample.class, "The yellow subject moves away from the blue subject when it enters the red circle." );
        put( SteerControllerImmediateExample.class, "In immediate mode the steering forces are applied directly to the subject's position instead of velocity." );
        put( SteerDodgeExample.class, "The red subject will actively avoid being hit by a green subject if a collision may occur." );
        put( SteerDriveExample.class, "Driving forces are applied when any of the arrow keys are pressed." );
        put( SteerEvadeExample.class, "A combination of SteerAvoid and TargetFuture where the orange subject avoids the potential intersection with the blue subject when it enters the yellow circle." );
        put( SteerFlockingExample.class, "A wandering red subject and a flock of yellow subjects. The green subjects are driven by staying away from the red subject, cohesion, alignment, separation with nearby subjects, and when none of those forces exist it wanders. The yellow circle is for testing which subjects are near to the mouse (noted by a white line to the subject from the mouse)." );
        put( SteerFlowFieldExample.class, "The subject follows the forces of the field beneath it." );
        put( SteerPathExample.class, "The yellow subject closely follows the path and avoids the blue subject at the same time while the blue subject loosely follows the path." );
        put( SteerPathResetExample.class, "The yellow subject closely follows the path and avoids the blue subject at the same time while the blue subject loosely follows the path. When the subjects arrive at the end of the path (upper left) they are reset to the beginning, forcing a loop." );
        put( SteerPursuitExample.class, "The yellow subject is steering towards the blue wandering subject's future position. This is a combination between SteerTo and TargetFuture." );
        put( SteerToExample.class, "The blue subject steers towards the mouse when it's inside the gray circle." );
        put( SteerWanderExample.class, "The blue subject wanders around the world randomly." );
        /* spatial */
        put( SpatialDatabaseExample.class, "Tests all available spatial database implementations for performance & correctedness with respect to collision detection, containment queries, and KNN queries. Press H for help." );
        /* target */
        put( TargetAverageExample.class, "The red subject targets the average location of all white wandering subjects within the yellow circle." );
        put( TargetClosestExample.class, "The red subject follows the closest wandering white subject in view (180deg FOV)." );
        put( TargetFacingExample.class, "The blue subject wanders while the orange subject steers away from it when it's in front of it and the green subject steers away from it when the blue subject is behind it." );
        put( TargetFutureExample.class, "The yellow subject steers to the future position of the blue subject." );
        put( TargetInLineExample.class, "The blue subject targets the line between the white and gray subjects." );
        put( TargetInterposeExample.class, "The blue subject targets 25% of the way between the white subject and the gray subject." );
        put( TargetLocalExample.class, "The yellow subject steers to the blue subject when the blue subject enters it's local space (yellow circle)." );
        put( TargetOffsetExample.class, "The green subject steers to an offset from the blue subject that is relative to the blue subject's direction while the yellow subject steers to an offset that doesn't care about the blue subjects orientation." );
        put( TargetSlowestExample.class, "The red subject targets the slowest white wandering subject in its yellow circle." );
        put( TargetWeakestExample.class, "The red subject targets the weakest white wandering subject in its yellow circle. Weakness is determined based on the subjects distance and speed." );
    }};
    
    private static List<Class<? extends Game>> EXAMPLES_LIST = new ArrayList<Class<? extends Game>>( EXAMPLES.keySet() );

    private Game current;
    private GameScreen screen;
    private JFrame window;
    private boolean playing;
    private int currentIndex = -1;
    private int nextIndex = -1;
    
    private JFrame selectionWindow;
    private JList selectionList;
    private JScrollPane selectionScroller;
    private JTextArea selectionText;
    private JPanel selectionPanel;

    public MasterExample()
    {
        /* selection */
        DefaultListModel selectionListModel = new DefaultListModel();
        for (Class<?> exampleClass : EXAMPLES_LIST)
        {
            selectionListModel.addElement( exampleClass.getSimpleName() );
        }
        
        selectionList = new JList();
        selectionList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        selectionList.setLayoutOrientation( JList.VERTICAL );
        selectionList.setVisibleRowCount( 10 );
        selectionList.setModel( selectionListModel );
        selectionList.addListSelectionListener( new ListSelectionListener() {
            public void valueChanged( ListSelectionEvent event ) {
                if (event.getValueIsAdjusting()) {
                    nextIndex = selectionList.getSelectedIndex();
                }
            }
        } );
        
        selectionScroller = new JScrollPane( selectionList );
        selectionScroller.setPreferredSize( new Dimension( 300, 200 ) );
        selectionScroller.setBorder( new EmptyBorder(5, 5, 5, 5) );
        
        selectionText = new JTextArea( 10, 25 );
        selectionText.setEditable( false );
        selectionText.setLineWrap( true );
        selectionText.setWrapStyleWord( true );
        selectionText.setBorder( new EmptyBorder(5, 5, 5, 5) );
        
        selectionPanel = new JPanel( new GridLayout( 0, 1 ) );
        selectionPanel.add( selectionScroller );
        selectionPanel.add( selectionText );
        
        selectionWindow = new JFrame( "Example Selection" );
        selectionWindow.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        selectionWindow.add( selectionPanel );
        selectionWindow.pack();
        selectionWindow.setVisible( true );
    }

    public void setExample( int index ) throws Exception
    {
        Class<? extends Game> clazz = EXAMPLES_LIST.get( index );
        String gameExplanation = EXAMPLES.get( clazz );
        Constructor<? extends Game> constructor = clazz.getConstructor( int.class, int.class );
        Game game = constructor.newInstance( SteerBasicExample.DEFAULT_WIDTH, SteerBasicExample.DEFAULT_HEIGHT );

        if (current != null)
        {
            current.destroy();
        }
        
        current = game;
        current.start( screen.getScene() );
        window.setTitle( clazz.getSimpleName() );
        selectionText.setText( gameExplanation );
        currentIndex = nextIndex = index;
    }

    @Override
    public void start( Scene scene )
    {
        playing = true;
        
        current.start( scene );
    }

    @Override
    public void input( GameInput input )
    {
        if (input.keyDown[KeyEvent.VK_ESCAPE])
        {
            playing = false;
        }
        
        current.input( input );
    }

    @Override
    public void update( GameState state, Scene scene )
    {
        if (nextIndex != currentIndex)
        {
            try
            {
                setExample( nextIndex );
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        
        current.update( state, scene );
    }

    @Override
    public void draw( GameState state, Graphics2D gr, Scene scene )
    {
        current.draw( state, gr, scene );
    }

    @Override
    public void destroy()
    {
        current.destroy();
    }

    @Override
    public boolean isPlaying()
    {
        return playing;
    }

}
