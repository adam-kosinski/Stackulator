import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.scene.layout.VBox;
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
		stack_display = new Text("Stack: " + stack.toString());
		showing_result = false;
		isRadians = true;
	}
	@Override
	public void start(Stage stage)
	{
		BorderPane bp = new BorderPane();
		bp.setId("main");
		
		//stuff on the top
		VBox top = new VBox();
		top.setId("top");
		bp.setTop(top);
		top.getChildren().add(display);
		stack_display.setId("stack_display");
		top.getChildren().add(stack_display);
		
		//button layout preparation
		GridPane left = new GridPane(); //contains number pad, operations
		GridPane right = new GridPane(); //contains functions
		left.getStyleClass().addAll("side","gridpane");
		right.getStyleClass().addAll("side","gridpane");
		
		//set up left side
		GridPane numbers_and_above = new GridPane();
		numbers_and_above.getStyleClass().add("gridpane");
		GridPane above_numbers = new GridPane();
		GridPane number_pad = new GridPane();
		above_numbers.getStyleClass().add("gridpane");
		number_pad.getStyleClass().add("gridpane");
		numbers_and_above.add(above_numbers, 0, 0);
		numbers_and_above.add(number_pad, 0, 1);
		left.add(numbers_and_above, 0, 0);
		
		GridPane operations = new GridPane();
		operations.getStyleClass().add("gridpane");
		left.add(operations, 1, 0);
		
		//spacer
		VBox spacer = new VBox();
		spacer.setMinWidth(10);
		
		//set up right side
		GridPane function_pad = new GridPane();
		right.add(function_pad, 0, 0);
		
		//put everything in
		GridPane container = new GridPane();
		bp.setCenter(container);
		container.add(left, 0, 0); //args: child, col_idx, row_idx
		container.add(spacer, 1, 0);
		container.add(right, 2, 0);
		function_pad.getStyleClass().add("gridpane");
		
		
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
			number_pad.add(new NumberButton("" + c), i % 3, i / 3);
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
				stack_display.setText("Stack: " + stack.toString());
				display.setText("");
			}
			else
			{
				display.setText("ERROR");
			}
		});
		enter.setId("enter");
		enter.getStyleClass().add("button");
		enter.setMinWidth(105);
		enter.setMaxWidth(105);
		above_numbers.add(enter, 0, 0);
		
		//backspace
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
		backspace.setId("backspace");
		backspace.getStyleClass().add("button");
		backspace.setMinWidth(50);
		backspace.setMaxWidth(50);
		above_numbers.add(backspace, 1, 0);

		
		//clear buttons
		Button clear = new Button("C");
		clear.setOnAction( e ->
		{
			stack.clear();
			display.setText("");
			stack_display.setText("Stack: " + stack.toString());
			showing_result = false;
		});
		Button clear_entry = new Button("CE");
		clear_entry.setOnAction( e ->
		{
			display.setText("");
			showing_result = false;
		});
		clear.getStyleClass().addAll("button","clear_button");
		clear.setMinWidth(65);
		clear_entry.getStyleClass().addAll("button","clear_button");
		clear_entry.setMinWidth(65);
		function_pad.add(clear, 0, 0);
		function_pad.add(clear_entry, 0, 1);
		
		//radian/deg mode button
		Button rad_deg = new Button("RAD");
		rad_deg.setOnAction( e ->
		{
			if(isRadians)
			{
				isRadians = false;
				rad_deg.setText("DEG");
			}
			else
			{
				isRadians = true;
				rad_deg.setText("RAD");
			}
		});
		rad_deg.getStyleClass().add("button");
		
		//function buttons
		FunctionButton ln = new FunctionButton("ln", x -> Math.log(x));
		FunctionButton log10 = new FunctionButton("log10", x -> Math.log10(x));
		FunctionButton sqrt = new FunctionButton("sqrt", x -> Math.sqrt(x));
		FunctionButton negate = new FunctionButton("(-)", x -> -x);
		
		FunctionButton sin = new FunctionButton("sin", x -> {return isRadians ? Math.sin(x) : Math.sin(Math.toRadians(x));});
		FunctionButton asin = new FunctionButton("asin", x -> {return isRadians ? Math.asin(x) : Math.toDegrees(Math.asin(x));});
		
		FunctionButton cos = new FunctionButton("cos", x -> {return isRadians ? Math.cos(x) : Math.cos(Math.toRadians(x));});
		FunctionButton acos = new FunctionButton("acos", x -> {return isRadians ? Math.acos(x) : Math.toDegrees(Math.acos(x));});
		
		FunctionButton tan = new FunctionButton("tan", x -> {return isRadians ? Math.tan(x) : Math.tan(Math.toRadians(x));});
		FunctionButton atan = new FunctionButton("atan", x -> {return isRadians ? Math.atan(x) : Math.toDegrees(Math.atan(x));});
		
		function_pad.add(negate, 2, 0);
		function_pad.add(sqrt, 2, 1);
		function_pad.add(ln, 2, 2);
		function_pad.add(log10, 2, 3);
		function_pad.add(rad_deg, 3, 0);
		function_pad.add(sin, 3, 1);
		function_pad.add(asin, 4, 1);
		function_pad.add(cos, 3, 2);
		function_pad.add(acos, 4, 2);
		function_pad.add(tan, 3, 3);
		function_pad.add(atan, 4, 3);
		
		//op buttons
		OpButton plus = new OpButton("+", (a,b) -> a+b);
		OpButton minus = new OpButton("-", (a,b) -> a-b);
		OpButton times = new OpButton("*", (a,b) -> a*b);
		OpButton divide = new OpButton("/", (a,b) -> a/b);
		OpButton pow = new OpButton("^", (a,b) -> Math.pow(a,b));
		operations.add(plus, 0, 0);
		operations.add(minus, 0, 1);
		operations.add(times, 0, 2);
		operations.add(divide, 0, 3);
		operations.add(pow, 0, 4);
		

		
		Scene scene = new Scene(bp, 650, 400);
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
			this.setMaxWidth(50);
			
			this.symbol = symbol;
			setOnAction( e ->
			{
				if(showing_result)
				{
					double d = getDisplayValue();
					if(!Double.isNaN(d))
					{
						stack.push(getDisplayValue());
						stack_display.setText("Stack: " + stack.toString());
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
			this.setMinWidth(50);
			this.setMaxWidth(50);
			
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
				stack_display.setText("Stack:" + stack.toString());
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