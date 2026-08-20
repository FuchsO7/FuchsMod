package de.fuchsmod.features.general;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import de.fuchsmod.commands.Debug;
import net.minecraft.network.chat.Component;

import java.util.*;

public class Calculator {
    private static final SimpleCommandExceptionType BRACKET_MISMATCH_EXCEPTION = new SimpleCommandExceptionType(Component.literal("Mismatched brackets"));
    private static final SimpleCommandExceptionType INVALID_EXPRESSION_EXCEPTION = new SimpleCommandExceptionType(Component.literal("Invalid Expression"));
    private static final SimpleCommandExceptionType DIVISION_BY_ZERO = new SimpleCommandExceptionType(Component.literal("Division by 0"));
    public static boolean enableCalculatorCommandsDebug = false;

    private interface MathOperation {
        int getPriority();
    }

    private enum MathOperator implements MathOperation {
        ADD(1),
        SUB(1),
        NEG(4),
        MUL(2),
        DIV(2),
        MOD(2),
        POW(3);

        private final int priority;

        MathOperator(int priority) {
            this.priority = priority;
        }

        @Override
        public int getPriority() {
            return this.priority;
        }
    }

    private enum Brackets implements MathOperation {
        OPENING,
        CLOSING;

        @Override
        public int getPriority() {
            return 0;
        }
    }

    private enum MathFunction implements MathOperation {
        SQRT,
        ROUND,
        ABS,
        SIGN,
        LN,
        LG,
        SIN,
        COS,
        TAN,
        ASIN,
        ACOS,
        ATAN;

        @Override
        public int getPriority() {
            return 5;
        }
    }

    private enum MathBifunction implements MathOperation {
        LOG;

        @Override
        public int getPriority() {
            return 5;
        }
    }

    private static class ExpressionParser {
        private int index = 0;
        private final String expression;

        public ExpressionParser(String expr) {
            expression = expr;
        }

        private Queue<Object> parseExpression() throws CommandSyntaxException {
            final Deque<MathOperation> operations = new ArrayDeque<>();
            final Queue<Object> equation = new LinkedList<>();
            boolean expectOperand = true;

            while (index < expression.length()) {
                char character = expression.charAt(index);
                if (character == ',') {
                    while (!operations.isEmpty() && operations.peek() != Brackets.OPENING) {
                        equation.add(operations.pop());
                    }
                    index++;
                    expectOperand = true;
                } else if (Character.isDigit(character)) {
                    double number = parseNumber();
                    Debug.sendDebugMessage("Adding %s to equation".formatted(number), enableCalculatorCommandsDebug);
                    equation.add(number);
                    expectOperand = false;
                } else if (Character.isLetter(character)) {
                    MathOperation function = parseFunction();
                    Debug.sendDebugMessage("Pushing %s to stack".formatted(function), enableCalculatorCommandsDebug);
                    operations.push(function);
                    expectOperand = true;
                } else {
                    MathOperation operation = parseOperator(expectOperand);

                    if (operation == Brackets.OPENING) {
                        operations.push(operation);
                        expectOperand = true;
                    } else if (operation == Brackets.CLOSING) {
                        while (!operations.isEmpty() && operations.peek() != Brackets.OPENING) {
                            equation.add(operations.pop());
                        }
                        try {
                            operations.pop();
                        } catch (NoSuchElementException _) {
                            throw BRACKET_MISMATCH_EXCEPTION.create();
                        }
                        if (operations.peek() instanceof MathFunction || operations.peek() instanceof MathBifunction) {
                            equation.add(operations.pop());
                        }
                        expectOperand = false;
                    } else {
                        while (!operations.isEmpty()) {
                            MathOperation top = operations.peek();
                            if (top == Brackets.OPENING)
                                break;

                            boolean lowerOrEqualPriority = operation == MathOperator.NEG || operation == MathOperator.POW ?
                                    operation.getPriority() < top.getPriority() : operation.getPriority() <= top.getPriority();
                            if (!lowerOrEqualPriority)
                                break;

                            MathOperation op = operations.pop();
                            Debug.sendDebugMessage("Popped %s from stack".formatted(op), enableCalculatorCommandsDebug);
                            if (op instanceof Brackets) {
                                throw BRACKET_MISMATCH_EXCEPTION.create();
                            }
                            equation.add(op);
                            Debug.sendDebugMessage("Added %s to equation".formatted(op), enableCalculatorCommandsDebug);
                        }
                        Debug.sendDebugMessage("Pushing %s to stack".formatted(operation), enableCalculatorCommandsDebug);
                        operations.push(operation);
                        expectOperand = true;
                    }
                }
            }

            Debug.sendDebugMessage("Clearing stack, adding to equation", enableCalculatorCommandsDebug);
            while (!operations.isEmpty()) {
                MathOperation operation = operations.pop();
                if (operation instanceof Brackets) {
                    throw BRACKET_MISMATCH_EXCEPTION.create();
                }
                Debug.sendDebugMessage("Added %s to equation".formatted(operation), enableCalculatorCommandsDebug);
                equation.add(operation);
            }

            return equation;
        }

        private double parseNumber() {
            Debug.sendDebugMessage("Parsing number in %s".formatted(expression.substring(index)), enableCalculatorCommandsDebug);
            int startIndex = index;
            while (index < expression.length() && (Character.isDigit(expression.charAt(index)) || expression.charAt(index) == '.')) {
                index++;
            }
            double number = Double.parseDouble(expression.substring(startIndex, index));
            Debug.sendDebugMessage("Found number %s".formatted(number), enableCalculatorCommandsDebug);
            return number;
        }

        private MathOperation parseFunction() throws CommandSyntaxException {
            Debug.sendDebugMessage("Parsing function in %s".formatted(expression.substring(index)), enableCalculatorCommandsDebug);
            int startIndex = index;
            while (index < expression.length() && Character.isLetter(expression.charAt(index))) {
                index++;
            }
            String function = expression.substring(startIndex, index).toUpperCase();
            Debug.sendDebugMessage("Found function %s".formatted(function), enableCalculatorCommandsDebug);

            try {
                return MathFunction.valueOf(function);
            } catch (IllegalArgumentException _) {
            }
            try {
                return MathBifunction.valueOf(function);
            } catch (IllegalArgumentException _) {
                throw new SimpleCommandExceptionType(Component.literal("Unknown Function %s".formatted(function))).create();
            }
        }

        private MathOperation parseOperator(boolean expectOperand) throws CommandSyntaxException {
            Debug.sendDebugMessage("Parsing Operation in %s".formatted(expression.substring(index)), enableCalculatorCommandsDebug);
            char operator = expression.charAt(index++);
            if (expectOperand && operator == '-') {
                return MathOperator.NEG;
            }
            Debug.sendDebugMessage("Found operator %s".formatted(operator), enableCalculatorCommandsDebug);
            return switch (operator) {
                case '+' -> MathOperator.ADD;
                case '-' -> MathOperator.SUB;
                case '*' -> MathOperator.MUL;
                case '/' -> MathOperator.DIV;
                case '%' -> MathOperator.MOD;
                case '^' -> MathOperator.POW;
                case '(' -> Brackets.OPENING;
                case ')' -> Brackets.CLOSING;
                default ->
                        throw new SimpleCommandExceptionType(Component.literal("Unknown Operator %s".formatted(operator))).create();
            };
        }
    }

    private static double evaluateOperation(double a, double b, MathOperator operator) throws CommandSyntaxException {
        Debug.sendDebugMessage("Evaluating operation %s: %s, %s".formatted(operator, a ,b), enableCalculatorCommandsDebug);
        return switch (operator) {
            case ADD -> a + b;
            case SUB -> a - b;
            case MUL -> a * b;
            case DIV -> {
                if (b == 0.0)
                    throw DIVISION_BY_ZERO.create();
                yield a / b;
            }
            case MOD -> a % b;
            case POW -> Math.pow(a, b);
            default ->
                    throw new SimpleCommandExceptionType(Component.literal("Unknown Operator %s".formatted(operator))).create();
        };
    }

    private static double evaluateFunction(double a, MathFunction function) throws CommandSyntaxException {
        Debug.sendDebugMessage("Evaluating function %s: %s".formatted(function, a), enableCalculatorCommandsDebug);
        return switch (function) {
            case SQRT -> Math.sqrt(a);
            case ROUND -> Math.round(a);
            case ABS -> Math.abs(a);
            case SIGN -> Math.signum(a);
            case LN -> Math.log(a);
            case LG -> Math.log10(a);
            case SIN -> Math.sin(a);
            case COS -> Math.cos(a);
            case TAN -> Math.tan(a);
            case ASIN -> Math.asin(a);
            case ACOS -> Math.acos(a);
            case ATAN -> Math.atan(a);
            default ->
                    throw new SimpleCommandExceptionType(Component.literal("Unknown Function %s".formatted(function))).create();
        };
    }

    private static double evaluateBifunction(double a, double b, MathBifunction function) throws CommandSyntaxException {
        Debug.sendDebugMessage("Evaluating function %s: %s, %s".formatted(function, a, b), enableCalculatorCommandsDebug);
        return switch (function) {
            case LOG -> Math.log(a) / Math.log(b);
            default ->
                    throw new SimpleCommandExceptionType(Component.literal("Unknown Function %s".formatted(function))).create();
        };
    }

    private static double evaluateEquation(Queue<Object> equation) throws CommandSyntaxException {
        final Deque<Double> results = new ArrayDeque<>();

        for (Object symbol : equation) {
            if (symbol instanceof Double number) {
                Debug.sendDebugMessage("Pushing %s to result stack".formatted(symbol), enableCalculatorCommandsDebug);
                results.push(number);
            } else if (symbol instanceof MathOperator operator) {
                Debug.sendDebugMessage("Found operation %s".formatted(operator), enableCalculatorCommandsDebug);
                double result;
                if (operator == MathOperator.NEG) {
                    if (results.isEmpty())
                        throw INVALID_EXPRESSION_EXCEPTION.create();
                    result = -results.pop();
                } else {
                    if (results.size() < 2)
                        throw INVALID_EXPRESSION_EXCEPTION.create();
                    double b = results.pop();
                    double a = results.pop();
                    result = evaluateOperation(a, b, operator);
                }
                Debug.sendDebugMessage("Pushing %s to result stack".formatted(result), enableCalculatorCommandsDebug);
                results.push(result);
            } else if (symbol instanceof MathFunction function) {
                Debug.sendDebugMessage("Found function %s".formatted(function), enableCalculatorCommandsDebug);
                if (results.isEmpty())
                    throw INVALID_EXPRESSION_EXCEPTION.create();
                double a = results.pop();
                double result = evaluateFunction(a, function);
                Debug.sendDebugMessage("Pushing %s to result stack".formatted(result), enableCalculatorCommandsDebug);
                results.push(result);
            } else if (symbol instanceof MathBifunction function) {
                Debug.sendDebugMessage("Found operation %s".formatted(function), enableCalculatorCommandsDebug);
                if (results.size() < 2)
                    throw INVALID_EXPRESSION_EXCEPTION.create();
                double b = results.pop();
                double a = results.pop();
                double result = evaluateBifunction(a, b, function);
                Debug.sendDebugMessage("Pushing %s to result stack".formatted(result), enableCalculatorCommandsDebug);
                results.push(result);
            }
        }
        Debug.sendDebugMessage("Calculation done, Results: %s".formatted(Arrays.toString(results.toArray())), enableCalculatorCommandsDebug);
        if (results.size() != 1) {
            throw INVALID_EXPRESSION_EXCEPTION.create();
        }
        return results.pop();
    }

    public static double calculateExpression(String expression) throws CommandSyntaxException {
        expression = expression.replace(" ", "");
        Debug.sendDebugMessage("Calculating Expression %s".formatted(expression), enableCalculatorCommandsDebug);
        Queue<Object> equation = new ExpressionParser(expression).parseExpression();
        Debug.sendDebugMessage("Parsed Expression into %s".formatted(Arrays.toString(equation.toArray())), enableCalculatorCommandsDebug);
        return evaluateEquation(equation);
    }
}
