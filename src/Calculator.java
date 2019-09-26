import java.awt.EventQueue;

/**
 * EntryPoint of this Simple Calculator 
 * @author Jimmy801
 */

public class Calculator {
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CalculatorModel calModel = new CalculatorModel();
					CalculatorView calApp = new CalculatorView();
					CalculatorControl calCtrl = new CalculatorControl(calModel, calApp);
					
					calApp.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
