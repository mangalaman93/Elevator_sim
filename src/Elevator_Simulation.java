import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

//The main class
public class Elevator_Simulation extends JApplet implements ChangeListener{
	public JLabel state; // display the state of the elevator
	private JLabel id;  //your name and group
	public ButtonPanel control; //the button control panel
	private Elevator elevator; // the elevator area
	private JSlider slider;  //the slider to change the speed

	//constructor
	public void init() {
		Container container = getContentPane();
		container.setLayout(new BorderLayout(0, 0));
		//adding the button panel in the layout
		control = new ButtonPanel();
		container.add(control, BorderLayout.WEST);
		//adding the top label in the layout
		id = new JLabel("Aman Mangal, SER1", JLabel.CENTER);
		id.setOpaque(true);
		id.setBackground(Color.LIGHT_GRAY);
		container.add(id, BorderLayout.NORTH);
		//adding the elevator area in the layout
		elevator = new Elevator(this);
		container.add(elevator, BorderLayout.CENTER);
		//adding the slider in the east area
		slider = new JSlider(JSlider.VERTICAL, 2, 20, 10);
		container.add(slider, BorderLayout.EAST);
		slider.addChangeListener(this);
		slider.addChangeListener(this);
		slider.setMajorTickSpacing(2);
		slider.setMinorTickSpacing(1);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		//adding variable label
		state = new JLabel("", JLabel.CENTER);
		container.add(state, BorderLayout.SOUTH);
	}

	public void stateChanged(ChangeEvent e) {
		JSlider slid = (JSlider)e.getSource();
		int speed = (int)slid.getValue();
		elevator.tm.setDelay(100/speed);
	}

} //the end of Elevator_Simulation class

//The ButtonPanel class receives and handles button pressing events
class ButtonPanel extends JPanel implements ActionListener {
	public JButton b[] = new JButton[8];  // 8 Buttons
	public boolean bp[] = new boolean[8]; // the state of each button, pressed or not

	//constructor
	public ButtonPanel() {
		this.setLayout(new GridLayout(8, 1, 0, 0));
		//adding the 8 floor button in the layout
		for(int index = 0; index<8; index++){
			b[index] = new JButton();
			b[index].setOpaque(true);
			b[index].setBackground(Color.CYAN);
			b[index].setText("F" + (8 - index));
			//Why this makes a difference
			//b[index].setBorder(new LineBorder(Color.BLACK, 1));
			this.add(b[index]);
			b[index].addActionListener(this);
		}
	}

	//if any button is pressed
	public void actionPerformed(ActionEvent e) {
		//handle the button pressing events
		JButton pressedButton = (JButton)e.getSource();
		pressedButton.setBackground(Color.RED);
		bp[findIndex(pressedButton, b)] = true;
	}

	private int findIndex(JButton button, JButton[] buttonArray){
		for(int index = 0; index<buttonArray.length; index++){
			if(buttonArray[index] == button)
				return index;
		}
		return 0;
	}
} //the end of ButtonPanel class

// The elevator class draws the elevator area and simulates elevator movement
class Elevator extends JPanel implements ActionListener {
	//Declaration of variables
	private Elevator_Simulation app; //the Elevator Simulation frame
	private boolean up; // the elevator is moving up or down
	private int ewidth;  // Elevator width
	private int eheight; // Elevator height
	private int xco;	// The x coordinate of the elevator's upper left corner
	private int yco; // The y coordinate of the elevator's upper left corner
	public int dy0; // Moving interval
	private int topy; //the y coordinate of the top level
	private int bottomy; // the y coordinate of the bottom level
	public Timer tm; //the timer to drive the elevator movement
	//other variables to be used ...
	private int width;  // panel width
	private int height; // panel height
	int counter = 0;

	//constructor
	public Elevator(Elevator_Simulation app) {
		this.app = app;
		this.setBackground(Color.yellow);
		//necessary initialization
		xco = 0;
		yco = 0;
		dy0 = 1;
		tm = new Timer(10, this);
	}

	// Paint elevator area
	public void paintComponent(Graphics g) {
		//obtain geometric values of components for drawing the elevator area
		width = this.getWidth();
		height = this.getHeight();
		//clear the painting canvas
		super.paintComponent(g);
		//start the Timer if not started elsewhere
		tm.start();
		//draw horizontal lines
		g.setColor(Color.BLACK);
		g.drawRect(0, 0, width-1, height-1);
		for(int i=0; i<8; i++){
			int y = i*height/8;
			g.drawLine(0, y, width-1, y);
		}
		//draw the elevator
		g.setColor(Color.LIGHT_GRAY);
		eheight = height/8;
		ewidth = eheight - 10;
		xco = width/2;
		g.fillRect(xco - ewidth/2, yco, ewidth, eheight);
		g.setColor(Color.BLACK);
		g.drawLine(xco, yco, xco, yco + eheight - 2);
		if(counter == 0)
			app.state.setText("The elevator is moving " + (up ?"up" : "down"));
	}

	//Handle the timer events
	public void actionPerformed(ActionEvent e) {
		//loop if the elevator needs to be stopped for a while
		topy = yco/eheight ;
		if((topy*eheight == yco) && app.control.bp[topy]){
			yco -= dy0;
			if(counter != 100){
				counter ++;
				app.state.setText("The elevator is picking pesseger from level " + (8 - topy));
			}
			if(counter == 100){
				counter = 0;
				yco += dy0;
				app.control.b[topy].setBackground(Color.CYAN);
				app.control.bp[topy] = false;
				//check whether to continue moving
				boolean flag = false;
				if(up){
					for(int index = topy-1; index>=0; index--){
						if(app.control.bp[index]){
							flag = true;
							break;
						}
					}
					
				} else{
					for(int index = topy+1; index<=7; index++){
						if(app.control.bp[index]){
							flag = true;
							break;
						}
					}
				}
				if(!flag){
					up = !up;
					dy0 = -dy0;
				}
			}
		}
		//adjust Y coordinate to simulate elevator movement
		yco = yco + dy0;
		//change moving direction when hits the top and bottom
		if(yco<0 || yco>height-eheight){
			up = !up;
			dy0 = -dy0;
		}
		//repaint the panel
		repaint();
		//update the state of the elevator
	}

} //the end of Elevator class