/*
 * John Diggins
 * Last Modified: 07/05/2017
 * Calculator Pane for my calculator. Contains all the meat of the calculator
 */
package calculator;

import java.text.*;
import java.util.*;
import javafx.event.*;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;

/**
 *
 * @author John
 */
public final class CalcPane extends FlowPane implements EventHandler{
    
    private final Stack<Double> stack;
    private final FlowPane calculator;
    private final TextField displayText;
    private final Tooltip displayTooltip;
    private String number;
    private double left;
    private double right;
    private boolean start;
    private boolean negative;
    DecimalFormat answerFormat;
    
    public CalcPane() {
        
        // style sheet
        this.getStylesheets().add("file:src/resources/style.css");
  
        // initialize stuff
        
        stack = new Stack<>();
        calculator = new FlowPane(); // holds buttons
        displayText = new TextField(""); 
        number = ""; // stores number being entered
        left = 0; 
        right = 0;
        start = true;
        displayTooltip = new Tooltip("Cleared");
        displayText.setTooltip(displayTooltip);
        answerFormat = new DecimalFormat("0.###############");

        
        displayText.getStyleClass().add("display");
        displayText.setEditable(false);
        displayText.setFocusTraversable(false);
        displayText.setMinSize(400, 100);
        displayText.setMaxSize(400, 100);
        displayText.setAlignment(Pos.CENTER_RIGHT);
        
        // create our buttons
        String buttons = "C&/789*456-123+0. ^#%=";
        for(int i = 0; i < buttons.length(); i++)
            addButton(calculator, buttons.substring(i, i+1));
        
        
        getChildren().addAll(displayText, calculator);
        this.setAlignment(Pos.CENTER);
    }
    
    
    private void addButton(FlowPane fp, String s) {
        Button b = new Button(s);
        
        // make 0 and C bigger buttons
        if (s.matches("^0|C$")) {
            b.setMinWidth(200);
            b.setMinWidth(200);
        } else {
            b.setMinWidth(100);
            b.setMaxWidth(100);
        }
        
        // set our button effects
        if(s.matches("\\d"))
            b.getStyleClass().add("n_button");
        else if(s.matches("\\="))
            b.getStyleClass().add("e_button");
        else
            b.getStyleClass().add("op_button");

        b.setMinHeight(100);
        b.setMaxHeight(100);
        fp.getChildren().add(b);
        b.setId(s);
        b.setOnAction(this);
    }

    @Override
    public void handle(Event e) {
        
        try {
            String s = ((Button) e.getSource()).getId();

            // if user enters a number or .
            if (s.matches("\\d|\\.")) {
                if(s.matches("\\.")) {
                    displayText.appendText("0");
                    number += "0";
                }
                displayText.appendText(s);
                displayText.positionCaret(displayText.toString().length());
                number += s;
                start = false;
            } else {
                if (start && s.equals("-")) {
                    displayText.appendText(s);
                    displayText.positionCaret(displayText.toString().length());
                    start = false;
                    negative = true;
                } else {
                        double x;

                        // if theres a number to parse, do it
                        // number must start with a digit, may have optional . and digits after .
                        if (number.matches("^\\d+(\\.?)\\d*$")) {
                          
                            x = Double.parseDouble(number);
                         
                            // check for negative number
                            if (negative == true) 
                                x *= -1;
                            // push # to stack
                            stack.push(x);
                        }

                        displayText.appendText(s);
                        displayText.positionCaret(displayText.toString().length());
                        calculate(s);
                        start = true;
                        number = "";
                        negative = false;
                }
            }
        } catch (NumberFormatException ex) {
            System.out.println(ex);
        }
    }

    public void calculate(String op) {
        try {
            switch (op) {
                case " ": // delimiter for first operand
                    if (stack.size() == 1) {
                        displayText.appendText(" ");
                    }   break;
                case "C": // clear
                    stack.clear();
                    displayText.setText("");
                    displayTooltip.setText("Cleared");
                    start = true;
                    break;
                case "=": // equals
                    left = stack.pop();
                    if (!stack.empty()) {
                        throw new Exception();
                    } 
                    displayText.setText(answerFormat.format(left) + " ");
                    displayTooltip.setText("The result is: " + answerFormat.format(left));
                    stack.push(left);
                    start = true;
                    break;
                case "#": // square root
                    left = stack.pop();
                    stack.push(Math.sqrt(left));
                    break;
                default:
                    right = stack.pop();
                    left = stack.pop();
                    switch (op) {
                        case "+": // add
                            left += right;
                            break;
                        case "-": // subtract
                            left -= right;
                            break;
                        case "*": // multiply
                            left *= right;
                            break;
                        case "/": // divide
                            try{
                                if (right != 0)
                                    left /= right;
                                else
                                    throw new ArithmeticException();
                            } catch (ArithmeticException e) {
                                Alert alertBox = new Alert(AlertType.ERROR);
                                alertBox.setTitle("Divide By 0 Error");
                                alertBox.setHeaderText("Illegal Operation");
                                alertBox.setContentText("Cannot divide by 0");

                                ButtonType okButton = new ButtonType("Ok");

                                alertBox.getButtonTypes().setAll(okButton);
                                Optional<ButtonType> result = alertBox.showAndWait();
                                if (result.get() == okButton) {
                                    alertBox.close();
                                }
                            }
                            break;
                        case "^": // exponent
                            left = Math.pow(left, right);
                            break;
                        case "&": // percent
                            left *= right / 100;
                            break;
                        case "%": // modulus 
                            left %= right;
                            break;
                    }
                    stack.push(left);
            }
        } catch (Exception ex) {
            
            Alert alertBox = new Alert(AlertType.ERROR);
            alertBox.setTitle("User Input Error");
            alertBox.setHeaderText("Incorrect number of operands matching operators");
            alertBox.setContentText("All operators need 2 operands except square root (#),\n which needs only one");

            ButtonType okButton = new ButtonType("Ok");

            alertBox.getButtonTypes().setAll(okButton);
            Optional<ButtonType> result = alertBox.showAndWait();
            if (result.get() == okButton) {
                alertBox.close();
            }
        }
    }
}
