package calculator.ast;

import calculator.interpreter.Environment;
import calculator.errors.EvaluationError;
import calculator.gui.ImageDrawer;
import datastructures.concrete.DoubleLinkedList;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IList;

/**
 * All of the static methods in this class are given the exact same parameters for
 * consistency. You can often ignore some of these parameters when implementing your
 * methods.
 *
 * Some of these methods should be recursive. You may want to consider using public-private
 * pairs in some cases.
 */
public class ExpressionManipulators {
    /**
     * Accepts an 'toDouble(inner)' AstNode and returns a new node containing the simplified version
     * of the 'inner' AstNode.
     *
     * Preconditions:
     *
     * - The 'node' parameter is an operation AstNode with the name 'toDouble'.
     * - The 'node' parameter has exactly one child: the AstNode to convert into a double.
     *
     * Postconditions:
     *
     * - Returns a number AstNode containing the computed double.
     *
     * For example, if this method receives the AstNode corresponding to
     * 'toDouble(3 + 4)', this method should return the AstNode corresponding
     * to '7'.
     *
     * @throws EvaluationError  if any of the expressions contains an undefined variable.
     * @throws EvaluationError  if any of the expressions uses an unknown operation.
     */
    public static AstNode handleToDouble(Environment env, AstNode node) {
        // To help you get started, we've implemented this method for you.
        return new AstNode(toDoubleHelper(env.getVariables(), node.getChildren().get(0)));
    }

    private static double toDoubleHelper(IDictionary<String, AstNode> variables, AstNode node) {
         if (node.isNumber()) {
            return node.getNumericValue();
        } else if (node.isVariable()) {
            String key = node.getName();
            if (variables.containsKey(key)) {
                return toDoubleHelper(variables, variables.get(node.getName()));
            } else {
                throw new EvaluationError("Expression contains undefined variable");
            }
        } else { //is operation
            String name = node.getName();
            if (name.equals("+")) {
                return toDoubleHelper(variables, node.getChildren().get(0)) + 
                       toDoubleHelper(variables, node.getChildren().get(1));
            } else if (name.equals("-")) {
                return toDoubleHelper(variables, node.getChildren().get(0)) - 
                       toDoubleHelper(variables, node.getChildren().get(1));
            } else if (name.equals("*")) {
                return toDoubleHelper(variables, node.getChildren().get(0)) * 
                       toDoubleHelper(variables, node.getChildren().get(1));
            } else if (name.equals("/")) {
                return toDoubleHelper(variables, node.getChildren().get(0)) / 
                       toDoubleHelper(variables, node.getChildren().get(1));
            } else if (name.equals("^")) {
                return Math.pow(toDoubleHelper(variables, node.getChildren().get(0)),
                       toDoubleHelper(variables, node.getChildren().get(1)));
            } else if (name.equals("sin")) {
                return Math.sin(toDoubleHelper(variables, node.getChildren().get(0)));
            } else if (name.equals("cos")) {
                return Math.cos(toDoubleHelper(variables, node.getChildren().get(0)));
            } else if (name.equals("negate")) {
                return -toDoubleHelper(variables, node.getChildren().get(0));
            } else {
                throw new EvaluationError("Expression contains undefined operation"); // figure out syntax
            }
        }
    }

    /**
     * Accepts a 'simplify(inner)' AstNode and returns a new node containing the simplified version
     * of the 'inner' AstNode.
     *
     * Preconditions:
     *
     * - The 'node' parameter is an operation AstNode with the name 'simplify'.
     * - The 'node' parameter has exactly one child: the AstNode to simplify
     *
     * Postconditions:
     *
     * - Returns an AstNode containing the simplified inner parameter.
     *
     * For example, if we received the AstNode corresponding to the expression
     * "simplify(3 + 4)", you would return the AstNode corresponding to the
     * number "7".
     *
     * Note: there are many possible simplifications we could implement here,
     * but you are only required to implement a single one: constant folding.
     *
     * That is, whenever you see expressions of the form "NUM + NUM", or
     * "NUM - NUM", or "NUM * NUM", simplify them.
     */
    public static AstNode handleSimplify(Environment env, AstNode node) {
        // Try writing this one on your own!
        // Hint 1: Your code will likely be structured roughly similarly
        //         to your "handleToDouble" method
        // Hint 2: When you're implementing constant folding, you may want
        //         to call your "handleToDouble" method in some way
        
        return (handleSimplifyHelper(env.getVariables(), node.getChildren().get(0), env));
    
    }
    private static AstNode handleSimplifyHelper(IDictionary<String, AstNode> variables, AstNode node, Environment env) {
        if (node.isNumber()) {
            return node;
        } else if (node.isVariable()) {
            String key = node.getName();
            if (variables.containsKey(key)) {
                return handleSimplifyHelper(variables, variables.get(key), env);
            } else { //if variable is not defined
                return node;
            }
        } else { // if node is operation
            String name = node.getName();
            IList<AstNode> newChildren = new DoubleLinkedList<>(); 
            if (checkValidity(variables, node)) {
                newChildren.add(node.getChildren().get(0));
                newChildren.add(node.getChildren().get(1));
                AstNode temp = new AstNode(name, newChildren);
                IList<AstNode> singleChild = new DoubleLinkedList<>();
                singleChild.add(temp);
                AstNode doubleNode = new AstNode("toDouble", singleChild);
                return handleToDouble(env, doubleNode);
            } else {
                newChildren.add(handleSimplifyHelper(variables, node.getChildren().get(0), env));
                if (node.getChildren().size() > 1) {
                    newChildren.add(handleSimplifyHelper(variables, node.getChildren().get(1), env));
                }
                AstNode temp = new AstNode(name, newChildren);
                return temp;
            }
        }
    }
   
    private static boolean checkValidity(IDictionary<String, AstNode> variables, AstNode current) {
        boolean rightFlag = false;
        String names = current.getName();
        boolean operFlag = names.equals("+") || names.equals("-") || names.equals("*"); // change all
        boolean leftFlag = current.getChildren().get(0).isNumber() || 
                variables.containsKey(current.getChildren().get(0).getName()); 
        if (current.getChildren().size() > 1) {
                rightFlag = current.getChildren().get(1).isNumber() || 
                variables.containsKey(current.getChildren().get(1).getName());
        }
        return operFlag && leftFlag && rightFlag;       
    }
    
    /**
     * Accepts a 'plot(exprToPlot, var, varMin, varMax, step)' AstNode and
     * generates the corresponding plot. Returns some arbitrary AstNode.
     *
     * Example 1:
     *
     * >>> plot(3 * x, x, 2, 5, 0.5)
     *
     * This method will receive the AstNode corresponding to 'plot(3 * x, x, 2, 5, 0.5)'.
     * Your 'handlePlot' method is then responsible for plotting the equation
     * "3 * x", varying "x" from 2 to 5 in increments of 0.5.
     *
     * In this case, this means you'll be plotting the following points:
     *
     * [(2, 6), (2.5, 7.5), (3, 9), (3.5, 10.5), (4, 12), (4.5, 13.5), (5, 15)]
     *
     * ---
     *
     * Another example: now, we're plotting the quadratic equation "a^2 + 4a + 4"
     * from -10 to 10 in 0.01 increments. In this case, "a" is our "x" variable.
     *
     * >>> c := 4
     * 4
     * >>> step := 0.01
     * 0.01
     * >>> plot(a^2 + c*a + a, a, -10, 10, step)
     *
     * ---
     *
     * @throws EvaluationError  if any of the expressions contains an undefined variable.
     * @throws EvaluationError  if varMin > varMax
     * @throws EvaluationError  if 'var' was already defined
     * @throws EvaluationError  if 'step' is zero or negative
     */
    public static AstNode plot(Environment env, AstNode node) {
        IDictionary<String, AstNode> variables = env.getVariables();
        IList<AstNode> parameters = node.getChildren();
        AstNode expression = parameters.get(0);
        AstNode var = parameters.get(1);
        double varMin = toDoubleHelper(variables, parameters.get(2));
        double varMax = toDoubleHelper(variables, parameters.get(3));
        double step = toDoubleHelper(variables, parameters.get(4));
        if (variables.containsKey(var.getName())) {
            throw new EvaluationError("var is already defined.");
        } else if (varMin > varMax) {
            throw new EvaluationError("varMin is greater than varMax.");
        } else if (step <= 0) {
            throw new EvaluationError("Invalid step size.");
        }
        IList<Double> xVals = new DoubleLinkedList<Double>();
        double current = varMin;
        while (current <= varMax) {
            xVals.add(current);
            current = current + step;
        }
        IList<Double> yVals = new DoubleLinkedList<Double>();
        for (int i = 0; i < xVals.size(); i++) {
            variables.put(var.getName(), new AstNode(xVals.get(i)));
            yVals.add(toDoubleHelper(variables, expression));
        }
        variables.remove(var.getName());
        ImageDrawer graph = env.getImageDrawer();
        graph.drawScatterPlot("Function", "x-values", "f(x) values", xVals, yVals);
        return new AstNode(1);
        }
}