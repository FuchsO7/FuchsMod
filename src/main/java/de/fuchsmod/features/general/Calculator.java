package de.fuchsmod.features.general;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.*;

import static de.fuchsmod.FuchsMod.LOGGER;

public class Calculator {
    private static final Minecraft client = Minecraft.getInstance();
    private static final Queue<Object> equation = new LinkedList<>();
    private static final Deque<MathOperation> operations = new ArrayDeque<>();
    private static final Deque<Double> results = new ArrayDeque<>();
    private static int index = 0;
    private static String expression;

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
        OPENING(100),
        CLOSING(0);

        private final int priority;

        Brackets(int priority) {
            this.priority = priority;
        }

        @Override
        public int getPriority() {
            return this.priority;
        }
    }

    private enum MathFunction implements MathOperation {
        SQRT,
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

    private static double parseNumber() {
        sendCalculatorDebugMessage("Parsing number in %s".formatted(expression.substring(index)));
        for (int endIndex = index; endIndex < expression.length(); endIndex++) {
            if (!Character.isDigit(expression.charAt(endIndex)) && expression.charAt(endIndex) != '.') {
                double number = Double.parseDouble(expression.substring(index, endIndex));
                index = endIndex;
                sendCalculatorDebugMessage("Found number %s".formatted(number));
                return number;
            }
        }
        double number = Double.parseDouble(expression.substring(index));
        index = expression.length();
        sendCalculatorDebugMessage("Found number %s".formatted(number));
        return number;
    }

    private static MathOperation parseFunction() throws CommandSyntaxException {
        sendCalculatorDebugMessage("Parsing function in %s".formatted(expression.substring(index)));
        for (int endIndex = index; endIndex < expression.length(); endIndex++) {
            if (!Character.isLetter(expression.charAt(endIndex))) {
                String function = expression.substring(index, endIndex).toUpperCase();
                index = endIndex;
                sendCalculatorDebugMessage("Found function %s".formatted(function));
                try {
                    return MathFunction.valueOf(function);
                } catch (Exception _) {}
                try {
                    return MathBifunction.valueOf(function);
                }
                catch (Exception _) {
                    throw new SimpleCommandExceptionType(Component.literal("Unknown Function %s".formatted(function))).create();
                }
            }
        }
        throw new SimpleCommandExceptionType(Component.literal("Function at end of expression")).create();
    }

    private static MathOperation parseOperator() throws CommandSyntaxException {
        sendCalculatorDebugMessage("Parsing Operation in %s".formatted(expression.substring(index)));
        String operator = expression.substring(index, ++index);
        sendCalculatorDebugMessage("Found operator %s".formatted(operator));
        return switch (operator) {
            case "+" -> MathOperator.ADD;
            case "-" -> MathOperator.SUB;
            case "*" -> MathOperator.MUL;
            case "/" -> MathOperator.DIV;
            case "%" -> MathOperator.MOD;
            case "^" -> MathOperator.POW;
            case "(" -> Brackets.OPENING;
            case ")" -> Brackets.CLOSING;
            default ->
                    throw new SimpleCommandExceptionType(Component.literal("Unknown Operator %s".formatted(operator))).create();
        };
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

    private static void reset() {
        index = 0;
        equation.clear();
        operations.clear();
        results.clear();
    }

    private static void parseExpression() throws CommandSyntaxException {
        while (index < expression.length()) {
            if (expression.charAt(index) == ',') {
                index++;
                continue;
            }
            else if (Character.isDigit(expression.charAt(index))) {
                double number = parseNumber();
                sendCalculatorDebugMessage("Adding %s to equation".formatted(number));
                equation.add(number);
            } else if (Character.isLetter(expression.charAt(index))) {
                MathOperation function = parseFunction();
                sendCalculatorDebugMessage("Pushing %s to stack".formatted(function));
                operations.push(function);
            } else {
                MathOperation operation = parseOperator();
                while (operations.peek() != null && operation.getPriority() <= operations.peek().getPriority()) {
                    if (operations.peek() == Brackets.OPENING && operation != Brackets.CLOSING)
                        break;
                    MathOperation op = operations.pop();
                    sendCalculatorDebugMessage("Popped %s from stack".formatted(op));
                    if (!(op instanceof Brackets)) {
                        sendCalculatorDebugMessage("Added %s to equation".formatted(op));
                        equation.add(op);
                    }
                }
                sendCalculatorDebugMessage("Pushing %s to stack".formatted(operation));
                operations.push(operation);
            }
        }
        sendCalculatorDebugMessage("Clearing stack, adding to equation");
        while (operations.peek() != null) {
            MathOperation op = operations.pop();
            if (!(op instanceof Brackets)) {
                sendCalculatorDebugMessage("Added %s to equation".formatted(op));
                equation.add(op);
            }
        }
    }

    private static double calculateExpression() throws CommandSyntaxException {
        for (Object symbol : equation.toArray()) {
            if (symbol instanceof MathOperator operator) {
                sendCalculatorDebugMessage("Found operation %s".formatted(operator));
                double b = Objects.requireNonNull(results.poll(), "No number found for operator %s".formatted(operator));
                double a = operator == MathOperator.SUB ? Objects.requireNonNullElse(results.poll(), 0.0) :
                        Objects.requireNonNull(results.poll(), "No number found for operator %s".formatted(operator));
                double result = evaluateOperation(a, b, operator);
                sendCalculatorDebugMessage("Pushing %s to result stack".formatted(result));
                results.push(result);
            } else if (symbol instanceof MathFunction function) {
                sendCalculatorDebugMessage("Found function %s".formatted(function));
                double a = Objects.requireNonNull(results.poll(), "No number found for function %s".formatted(function));
                double result = evaluateFunction(a, function);
                sendCalculatorDebugMessage("Pushing %s to result stack".formatted(result));
                results.push(result);
            } else if (symbol instanceof MathBifunction function) {
                sendCalculatorDebugMessage("Found operation %s".formatted(function));
                double b = Objects.requireNonNull(results.poll(), "No number found for operator %s".formatted(function));
                double a = Objects.requireNonNull(results.poll(), "No number found for operator %s".formatted(function));
                double result = evaluateBifunction(a, b, function);
                sendCalculatorDebugMessage("Pushing %s to result stack".formatted(result));
                results.push(result);
            } else {
                sendCalculatorDebugMessage("Pushing %s to result stack".formatted(symbol));
                results.push((double) symbol);
            }
        }
        sendCalculatorDebugMessage("Calculation done, Results: %s".formatted(Arrays.toString(results.toArray())));
        return results.peek();
    }

    public static double evaluateExpression(String expr) throws CommandSyntaxException {
        reset();
        expression = expr.replace(" ", "");
        sendCalculatorDebugMessage("Calculating Expression %s".formatted(expression));
        parseExpression();
        sendCalculatorDebugMessage("Parsed Expression into %s".formatted(Arrays.toString(equation.toArray())));
        return calculateExpression();
    }
}
