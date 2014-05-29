
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
        put( SteerArriveExample.class, "MEOW" );
        put( SteerArriveOffsetExample.class, "" );
        put( SteerAvoidExample.class, "" );
        put( SteerAvoidObstaclesExample.class, "" );
        put( SteerArriveExample.class, "" );
        put( SteerAvoidWallExample.class, "" );
        put( SteerAwayExample.class, "" );
        put( SteerControllerImmediateExample.class, "" );
        put( SteerDodgeExample.class, "" );
        put( SteerDriveExample.class, "" );
        put( SteerEvadeExample.class, "" );
        put( SteerFlockingExample.class, "" );
        put( SteerFlowFieldExample.class, "" );
        put( SteerPathExample.class, "" );
        put( SteerPathResetExample.class, "" );
        put( SteerPursuitExample.class, "" );
        put( SteerToExample.class, "" );
        put( SteerWanderExample.class, "" );
        /* spatial */
        put( SpatialDatabaseExample.class, "" );
        /* target */
        put( TargetAverageExample.class, "" );
        put( TargetClosestExample.class, "" );
        put( TargetFacingExample.class, "" );
        put( TargetFutureExample.class, "" );
        put( TargetInLineExample.class, "" );
        put( TargetInterposeExample.class, "" );
        put( TargetLocalExample.class, "" );
        put( TargetOffsetExample.class, "" );
        put( TargetSlowestExample.class, "" );
        put( TargetWeakestExample.class, "" );
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

    public MasterExample() throws Exception
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
        
        selectionText = new JTextArea( 10, 50 );
        selectionText.setEditable( false );
        selectionText.setText( "Hello World" );
        
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
