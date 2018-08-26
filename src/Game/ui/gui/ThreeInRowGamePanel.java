package Game.ui.gui;

import Game.logic.ObservableGame;
import java.awt.BorderLayout;
import javax.swing.JPanel; 

/** Painel que contem todos os elementos que aparecem na janela.
 * 
 * @author JMSousa (base)
 *
 */
public class ThreeInRowGamePanel extends JPanel
{	
    ObservableGame game;
    GameGrid theGrid;
    PlayerData pd0, pd1;

    public ThreeInRowGamePanel(ObservableGame game)
    {
        this.game=game;
                
        setupComponents();
        setupLayout();
    }

    private void setupComponents()
    {
        theGrid=new GameGrid(game);
        pd0 =new PlayerData(game,0);
        pd1 =new PlayerData(game,1);
   
    }

    private void setupLayout()
    {
        JPanel pCenter, pSouth;

        setLayout(new BorderLayout());

        pCenter=new JPanel();
        pCenter.setLayout(new BorderLayout());
        pCenter.add(theGrid,BorderLayout.NORTH);
        
        pSouth=new JPanel();
        pSouth.add(pd0);
        pSouth.add(pd1);
        pCenter.add(pSouth,BorderLayout.SOUTH);
 
                       
        add(pCenter,BorderLayout.CENTER);

        
        validate();
    }
    
}
