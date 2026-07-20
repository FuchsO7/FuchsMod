package de.fuchsmod.features.general;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.*;

public class Calculator {
    private static final Minecraft client = Minecraft.getInstance();
    private static final Queue<Object> equation = new LinkedList<>();
    private static final Deque<MathOperation> operations = new ArrayDeque<>();
    private static final Deque<Double> results = new ArrayDeque<>();
    private static int index = 0;
    private static String expression;

    public static boolean enableCalculatorCommandsDebug = false;

    private interface MathOperation {
        int getPriority();
    }

    private enum MathOperator implements MathOperation {
        ADD(1),
        SUB(1),
        MUL(2),
        DIV(2),
        MOD(2),
        POW(3),
        BRACKET(4);

        private final int priority;

        MathOperator(int priority) {
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
        LOG,
        LN,
        LB,
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

    private static double parseNumber() {
        for (int endIndex = index; endIndex < expression.length(); endIndex++) {
            if (!Character.isDigit(expression.charAt(endIndex)) && expression.charAt(endIndex) != '.') {
                double number = Double.parseDouble(expression.substring(index, endIndex));
                index = endIndex;
                return number;
            }
        }
        double number = Double.parseDouble(expression.substring(index));
        index = expression.length();
        return number;
    }

    private static MathFunction parseFunction() throws CommandSyntaxException {
        for (int endIndex = index; endIndex < expression.length(); endIndex++) {
            if (!Character.isLetter(expression.charAt(endIndex))) {
                String function = expression.substring(index, endIndex).toLowerCase();
                index = endIndex;
                try {
                    return MathFunction.valueOf(function);
                } catch (Exception e) {
                    throw new SimpleCommandExceptionType(Component.literal("Unknown Function %s".formatted(e))).create();
                }
            }
        }
        throw new SimpleCommandExceptionType(Component.literal("Function at end of expression")).create();
    }

    private static MathOperator parseOperator() throws CommandSyntaxException {
        String operator = expression.substring(index, ++index);
        return switch (operator) {
            case "+" -> MathOperator.ADD;
            case "-" -> MathOperator.SUB;
            case "*" -> MathOperator.MUL;
            case "/" -> MathOperator.DIV;
            case "%" -> MathOperator.MOD;
            case "^" -> MathOperator.POW;
            default ->
                    throw new SimpleCommandExceptionType(Component.literal("Unknown Operator %s".formatted(operator))).create();
        };
    }

    private static double evaluateOperation(double a, double b, MathOperator operator) throws CommandSyntaxException {
        return switch (operator) {
            case ADD -> a + b;
            case SUB -> a - b;
            case MUL -> a * b;
            case DIV -> a / b;
            case MOD -> a % b;
            case POW -> Math.pow(a, b);
            default ->
                    throw new SimpleCommandExceptionType(Component.literal("Unknown Operator %s".formatted(operator))).create();
        };
    }

    private static double evaluateFunction(double a, MathFunction function) throws CommandSyntaxException {
        return switch (function) {
            case SQRT -> Math.sqrt(a);
            case SIGN -> Math.signum(a);
            case LN -> Math.log(a);
            case LB -> Math.log(a) / Math.log(2.0);
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

    private static void reset() {
        index = 0;
        equation.clear();
        operations.clear();
        results.clear();
    }

    private static void parseExpression() throws CommandSyntaxException {
        while (index < expression.length()) {
            if (Character.isDigit(expression.charAt(index))) {
                equation.offer(parseNumber());
            } else if (Character.isLetter(expression.charAt(index))) {
                MathFunction function = parseFunction();
                operations.offer(function);
            } else {
                MathOperator operator = parseOperator();
                while (operations.peek() != null && operator.getPriority() <= operations.peek().getPriority()) {
                    equation.offer(operations.pop());
                }
                operations.push(operator);
            }
        }
        while (operations.peek() != null) {
            equation.offer(operations.pop());
        }
    }

    private static double calculateExpression() throws CommandSyntaxException {
        for (Object symbol : equation.toArray()) {
            if (symbol instanceof MathOperator operator) {
                double b = results.pop();
                double a = Objects.requireNonNullElse(results.pop(), 0.0);
                results.push(evaluateOperation(a, b, operator));
            } else
                results.push((double) symbol);
        }
        return results.peek();
    }

    public static double evaluateExpression(String expr) throws CommandSyntaxException {
        reset();
        expression = expr.replace(" ", "");
        client.player.sendSystemMessage(Component.literal(expression));
        parseExpression();
        client.player.sendSystemMessage(Component.literal(Arrays.toString(equation.toArray())));
        return calculateExpression();
    }
}
