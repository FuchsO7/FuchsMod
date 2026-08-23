package de.fuchsmod.features.general;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CalculatorTest {

    @Test
    void testSimpleOperations() throws Calculator.CalculatorException {
        String[] expressions = {"1 + 2", "23.3 + 35.5", "4 * 8", "-3 / 6", "10 % 3"};
        Double[] expectedResults = {3.0, 58.8, 32.0, -0.5, 1.0};

        for (int i = 0; i < expressions.length; i++) {
            double expectedResult = expectedResults[i];
            double actualResult = Calculator.calculateExpression(expressions[i]);
            Assertions.assertEquals(expectedResult, actualResult,
                    "Test failed for expression %s".formatted(expressions[i]));
        }
    }

    @Test
    void testOperationOrder() throws Calculator.CalculatorException {
        String[] expressions = {"2 + 3 * 4", "(2 + 3) * 4", "-(3 + 5) * -4", "10 - 15 / 3", "2^3^2", "(2^3)^2"};
        Double[] expectedResults = {14.0, 20.0, 32.0, 5.0, 512.0, 64.0};

        for (int i = 0; i < expressions.length; i++) {
            double expectedResult = expectedResults[i];
            double actualResult = Calculator.calculateExpression(expressions[i]);
            Assertions.assertEquals(expectedResult, actualResult,
                    "Test failed for expression %s".formatted(expressions[i]));
        }
    }

    @Test
    void testFunctions() throws Calculator.CalculatorException {
        String[] expressions = {"sqrt(4)", "abs(-3)", "ln(3)", "lg(100)", "log(0.25, 2)", "round(5.5)", "-(sqrt(16) * -3) / 2", "cos(1)", "asin(0.5)"};
        Double[] expectedResults = {2.0, 3.0, Math.log(3), 2.0, -2.0, 6.0, 6.0, Math.cos(1), Math.asin(0.5)};

        for (int i = 0; i < expressions.length; i++) {
            double expectedResult = expectedResults[i];
            double actualResult = Calculator.calculateExpression(expressions[i]);
            Assertions.assertEquals(expectedResult, actualResult,
                    "Test failed for expression %s".formatted(expressions[i]));
        }
    }

    @Test
    void testExceptions() throws Calculator.CalculatorException {
        String[] expressions = {"1.1.1", "1 + ", "foo(1)", "(1 + 1", "(1) + 1)", "1 + * 1", "1 § 1", "1 / 0"};

        for (String expression : expressions) {
            Assertions.assertThrows(Calculator.CalculatorException.class, () -> Calculator.calculateExpression(expression),
                    "CommandSyntaxException was not thrown for %s".formatted(expression));
        }
    }
}
