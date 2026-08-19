package de.fuchsmod.features.general;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.*;

import static de.fuchsmod.FuchsMod.LOGGER;

public class Calculator {
    private static final Minecraft client = Minecraft.getInstance();
    // FIXME: Add proper exception handling

    public static boolean enableCalculatorCommandsDebug = false;

    private static void sendCalculatorDebugMessage(String message) {
        if (enableCalculatorCommandsDebug) {
            if (client.player != null)
                client.player.sendSystemMessage(Component.literal(message));
            LOGGER.info(message);
        }
    }

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

    public static class ExpressionParser {
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
                    sendCalculatorDebugMessage("Adding %s to equation".formatted(number));
                    equation.add(number);
                    expectOperand = false;
                } else if (Character.isLetter(character)) {
                    MathOperation function = parseFunction();
                    sendCalculatorDebugMessage("Pushing %s to stack".formatted(function));
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
                            throw new SimpleCommandExceptionType(Component.literal("Mismatched brackets")).create();
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
                            sendCalculatorDebugMessage("Popped %s from stack".formatted(op));
                            if (op instanceof Brackets) {
                                throw new SimpleCommandExceptionType(Component.literal("Mismatched brackets")).create();
                            }
                            equation.add(op);
                            sendCalculatorDebugMessage("Added %s to equation".formatted(op));
                        }
                        sendCalculatorDebugMessage("Pushing %s to stack".formatted(operation));
                        operations.push(operation);
                        expectOperand = true;
                    }
                }
            }

            sendCalculatorDebugMessage("Clearing stack, adding to equation");
            while (!operations.isEmpty()) {
                MathOperation operation = operations.pop();
                if (operation instanceof Brackets) {
                    throw new SimpleCommandExceptionType(Component.literal("Mismatched brackets")).create();
                }
                sendCalculatorDebugMessage("Added %s to equation".formatted(operation));
                equation.add(operation);
            }

            return equation;
        }

        private double parseNumber() {
            sendCalculatorDebugMessage("Parsing number in %s".formatted(expression.substring(index)));
            int startIndex = index;
            while (index < expression.length() && (Character.isDigit(expression.charAt(index)) || expression.charAt(index) == '.')) {
                index++;
            }
            double number = Double.parseDouble(expression.substring(startIndex, index));
            sendCalculatorDebugMessage("Found number %s".formatted(number));
            return number;
        }

        private MathOperation parseFunction() throws CommandSyntaxException {
            sendCalculatorDebugMessage("Parsing function in %s".formatted(expression.substring(index)));
            int startIndex = index;
            while (index < expression.length() && Character.isLetter(expression.charAt(index))) {
                index++;
            }
            String function = expression.substring(startIndex, index).toUpperCase();
            sendCalculatorDebugMessage("Found function %s".formatted(function));

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
            sendCalculatorDebugMessage("Parsing Operation in %s".formatted(expression.substring(index)));
            char operator = expression.charAt(index++);
            if (expectOperand && operator == '-') {
                return MathOperator.NEG;
            }
            sendCalculatorDebugMessage("Found operator %s".formatted(operator));
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
        sendCalculatorDebugMessage("Evaluating operation %s: %s, %s".formatted(operator, a ,b));
        return switch (operator) {
            case ADD -> a + b;
            case SUB -> a - b;
            case MUL -> a * b;
            case DIV -> {
                if (b == 0.0)
                    throw new SimpleCommandExceptionType(Component.literal("Division by 0")).create();
                yield a / b;
            }
            case MOD -> a % b;
            case POW -> Math.pow(a, b);
            default ->
                    throw new SimpleCommandExceptionType(Component.literal("Unknown Operator %s".formatted(operator))).create();
        };
    }

    private static double evaluateFunction(double a, MathFunction function) throws CommandSyntaxException {
        sendCalculatorDebugMessage("Evaluating function %s: %s".formatted(function, a));
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
        sendCalculatorDebugMessage("Evaluating function %s: %s, %s".formatted(function, a, b));
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
                sendCalculatorDebugMessage("Pushing %s to result stack".formatted(symbol));
                results.push(number);
            } else if (symbol instanceof MathOperator operator) {
                sendCalculatorDebugMessage("Found operation %s".formatted(operator));
                double result;
                if (operator == MathOperator.NEG) {
                    if (results.isEmpty())
                        throw new SimpleCommandExceptionType(Component.literal("Invalid expression")).create();
                    result = -results.pop();
                } else {
                    if (results.size() < 2)
                        throw new SimpleCommandExceptionType(Component.literal("Invalid expression")).create();
                    double b = results.pop();
                    double a = results.pop();
                    result = evaluateOperation(a, b, operator);
                }
                sendCalculatorDebugMessage("Pushing %s to result stack".formatted(result));
                results.push(result);
            } else if (symbol instanceof MathFunction function) {
                sendCalculatorDebugMessage("Found function %s".formatted(function));
                if (results.isEmpty())
                    throw new SimpleCommandExceptionType(Component.literal("Invalid expression")).create();
                double a = results.pop();
                double result = evaluateFunction(a, function);
                sendCalculatorDebugMessage("Pushing %s to result stack".formatted(result));
                results.push(result);
            } else if (symbol instanceof MathBifunction function) {
                sendCalculatorDebugMessage("Found operation %s".formatted(function));
                if (results.size() < 2)
                    throw new SimpleCommandExceptionType(Component.literal("Invalid expression")).create();
                double b = results.pop();
                double a = results.pop();
                double result = evaluateBifunction(a, b, function);
                sendCalculatorDebugMessage("Pushing %s to result stack".formatted(result));
                results.push(result);
            }
        }
        sendCalculatorDebugMessage("Calculation done, Results: %s".formatted(Arrays.toString(results.toArray())));
        if (results.size() != 1) {
            throw new SimpleCommandExceptionType(Component.literal("Invalid expression result")).create();
        }
        return results.pop();
    }

    public static double calculateExpression(String expression) throws CommandSyntaxException {
        expression = expression.replace(" ", "");
        sendCalculatorDebugMessage("Calculating Expression %s".formatted(expression));
        Queue<Object> equation = new ExpressionParser(expression).parseExpression();
        sendCalculatorDebugMessage("Parsed Expression into %s".formatted(Arrays.toString(equation.toArray())));
        return evaluateEquation(equation);
    }
}
