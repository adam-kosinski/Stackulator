import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.scene.layout.HBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Button;

import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;

public class Stackulator extends Application
{
	private Text display;
	private Text stack_display;
	private AStack<Double> stack;
	private boolean showing_result;
	private boolean isRadians;
	
	public Stackulator()
	{
		stack = new AStack<Double>();
		display = new Text();
		stack_display = new Text(stack.toString());
		showing_result = false;
		isRadians = true;
	}
	@Override
	public void start(Stage stage)
	{
		BorderPane bp = new BorderPane();
		
		//stuff on the top
		HBox top = new HBox();
		top.setId("top");
		bp.setTop(top);
		top.getChildren().add(display);
		
		//other
		bp.setBottom(stack_display);
		
		//button layout preparation
		GridPane left = new GridPane();
		GridPane right = new GridPane();
		GridPane container = new GridPane();
		bp.setCenter(container);
		container.add(left, 0, 0); //args: child, col_idx, row_idx
		container.add(right, 1, 0);
		
		
		//make number buttons
		NumberButton[] numbers = new NumberButton[10];
		for(int k=0; k<10; k++)
		{
			numbers[k] = new NumberButton(""+k);
		}
		NumberButton point = new NumberButton(".");
		NumberButton exponent = new NumberButton("E");
		
		//add number buttons		
		int i = 0;
		for (char c : "789456123.0E".toCharArray()) {
			left.add(new NumberButton("" + c), i % 3, i / 3);
			i++;
		}
		
		//enter key
		Button enter = new Button("ENTER");
		enter.setOnAction( e ->
		{
			double d = getDisplayValue();
			if(!Double.isNaN(d))
			{
				stack.push(d);
				stack_display.setText(stack.toString());
				display.setText("");
			}
			else
			{
				display.setText("ERROR");
			}
		});
		enter.getStyleClass().add("button");
		right.add(enter, 0, 0);
		
		//clear buttons
		Button clear = new Button("C");
		clear.setOnAction( e ->
		{
			stack.clear();
			display.setText("");
			stack_display.setText(stack.toString());
			showing_result = false;
		});
		Button clear_entry = new Button("CE");
		clear_entry.setOnAction( e ->
		{
			display.setText("");
			showing_result = false;
		});
		Button backspace = new Button("<-");
		backspace.setOnAction( e ->
		{
			if(display.getText().length() > 0)
			{
				int len = display.getText().length();
				display.setText(display.getText().substring(0, len-1));
				showing_result = false;
			}
		});
		clear.getStyleClass().add("button");
		clear_entry.getStyleClass().add("button");
		backspace.getStyleClass().add("button");
		
		right.add(clear, 1, 0);
		right.add(clear_entry, 1, 1);
		right.add(backspace, 1, 2);
		
		//radian/deg mode button
		Button rad_deg = new Button("to deg");
		rad_deg.setOnAction( e ->
		{
			if(isRadians)
			{
				isRadians = false;
				rad_deg.setText("to rad");
			}
			else
			{
				isRadians = true;
				rad_deg.setText("to deg");
			}
		});
		rad_deg.getStyleClass().add("button");
		
		//function buttons
		FunctionButton ln = new FunctionButton("ln", x -> Math.log(x));
		FunctionButton log10 = new FunctionButton("log10", x -> Math.log10(x));
		FunctionButton sqrt = new FunctionButton("sqrt", x -> Math.sqrt(x));
		FunctionButton negate = new FunctionButton("(-)", x -> -x);
		FunctionButton sin = new FunctionButton("sin", x ->
		{
			return isRadians ? Math.sin(x) : Math.sin(Math.toRadians(x));
		});
		FunctionButton asin = new FunctionButton("asin", x ->
		{
			return isRadians ? Math.asin(x) : Math.toDegrees(Math.asin(x));
		});
		FunctionButton cos = new FunctionButton("cos", Math::cos);
		FunctionButton tan = new FunctionButton("tan", Math::tan);
		
		right.add(rad_deg, 3, 0);
		right.add(ln, 2, 0);
		right.add(log10, 2, 1);
		right.add(sqrt, 2, 2);
		right.add(negate, 2, 3);
		right.add(sin, 2, 4);
		right.add(asin, 2, 5);
		right.add(cos, 2, 6);
		right.add(tan, 2, 7);
		
		//op buttons
		OpButton plus = new OpButton("+", (a,b) -> a+b);
		OpButton minus = new OpButton("-", (a,b) -> a-b);
		OpButton times = new OpButton("*", (a,b) -> a*b);
		OpButton divide = new OpButton("/", (a,b) -> a/b);
		OpButton pow = new OpButton("^", (a,b) -> Math.pow(a,b));
		right.add(plus, 0, 1);
		right.add(minus, 0, 2);
		right.add(times, 0, 3);
		right.add(divide, 0, 4);
		right.add(pow, 0, 5);
		
		left.getStyleClass().add("gridpane");
		right.getStyleClass().add("gridpane");
		
		bp.setId("main");
		
		Scene scene = new Scene(bp, 800, 500);
		scene.getStylesheets().add("./stackulator.css");
		stage.setScene(scene);
		stage.show();
	}
	
	private double getDisplayValue() {
		String s = display.getText();
		double d;
		try
		{
			d = Double.parseDouble(s);
		}
		catch (NumberFormatException ex)
		{
			System.out.println("Invalid input to display");
			d = Double.NaN;
		}
		return d;
	}

	
	
	class NumberButton extends Button
	{
		private final String symbol;
		public NumberButton(String symbol)
		{
			super(symbol);
			
			this.getStyleClass().addAll("button","number_button");
			this.setMinWidth(50);
			
			this.symbol = symbol;
			setOnAction( e ->
			{
				if(showing_result)
				{
					double d = getDisplayValue();
					if(!Double.isNaN(d))
					{
						stack.push(getDisplayValue());
						stack_display.setText(stack.toString());
					}
					display.setText("");
				}
				display.setText(display.getText() + symbol);
				showing_result = false;
			});
		}
	}
	
	class OpButton extends Button
	{
		private final DoubleBinaryOperator op;
		public OpButton(String symbol, DoubleBinaryOperator op)
		{
			super(symbol);
			
			this.getStyleClass().addAll("button","op_button");
			
			this.op = op;
			setOnAction( e ->
			{				
				if(stack.size() == 0)
				{
					System.out.println("Nothing on stack");
					display.setText("Nothing on stack");
					return;
				}
				
				double first = stack.pop();
				double second = getDisplayValue();
				if(Double.isNaN(second))
				{
					display.setText("ERROR");
					return;
				}
				double result = op.applyAsDouble(first, second);
				stack_display.setText(stack.toString());
				display.setText("" + result);
				showing_result = true;
			});
		}
	}
	
	class FunctionButton extends Button
	{
		private DoubleUnaryOperator function;
		public FunctionButton(String symbol, DoubleUnaryOperator function)
		{
			super(symbol);
			
			this.getStyleClass().addAll("button","function_button");
			this.setMinWidth(100);
			
			this.function = function;
			this.setOnAction( e -> 
			{
				double d = getDisplayValue();
				if(Double.isNaN(d))
				{
					display.setText("ERROR");
					return;
				}
				
				double result = function.applyAsDouble(d);
				display.setText("" + result);
				showing_result = true;
			});
		}
	}
	
	
}