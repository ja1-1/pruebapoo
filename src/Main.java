import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Clase que representa una jugada individual en SAN
class Move {
    private final String notation;
    private final String color;

    public Move(String notation, String color) {
        this.notation = notation;
        this.color = color;
    }

    public boolean isValid() {
        String letter = "[a-h]";
        String number = "[1-8]";
        String square = letter + number;
        String piece = "[KQRBN]";
        String castle = "O-O(-O)?";
        String check = "[+#]?";
        String promote = "(=[QRBN])?";

        String advPawn = square + promote + check;
        String pawnTakes = letter + "x" + square + promote + check;
        String pawnMove = "(" + advPawn + "|" + pawnTakes + ")";

        String disambiguation = "(" + letter + "|" + number + "|" + letter + number + ")?";
        String pieceMove = piece + disambiguation + "x?" + square + promote + check;

        String move = "(" + castle + "|" + pawnMove + "|" + pieceMove + ")";
        return this.notation.matches(move);
    }

    public String getNotation() {
        return notation;
    }

    public String getColor() {
        return color;
    }
}

// Clase para representar un turno con jugadas blancas y negras
class Turn {
    private final String turnNumber;
    private final Move whiteMove;
    private final Move blackMove;

    public Turn(String turnNumber, Move whiteMove, Move blackMove) {
        this.turnNumber = turnNumber;
        this.whiteMove = whiteMove;
        this.blackMove = blackMove;
    }

    public String getTurnNumber() {
        return turnNumber;
    }

    public Move getWhiteMove() {
        return whiteMove;
    }

    public Move getBlackMove() {
        return blackMove;
    }
}

// Clase principal para analizar la partida y validar las jugadas
public class Main {
    private final List<Turn> turns = new ArrayList<>();
    private boolean isValid = true;

    public void parseGame(String game) {
        turns.clear();
        Pattern turnPattern = Pattern.compile("(\\d+)\\.\\s+([^\\s]+)(?:\\s+(?!\\d+\\.)([^\\s]+))?");
        Matcher matcher = turnPattern.matcher(game);

        while (matcher.find()) {
            String turnNum = matcher.group(1);
            String whiteNotation = matcher.group(2);
            String blackNotation = matcher.group(3);

            Move whiteMove = new Move(whiteNotation, "white");
            Move blackMove = blackNotation != null ? new Move(blackNotation, "black") : null;

            boolean whiteValid = whiteMove.isValid();
            boolean blackValid = blackMove == null || blackMove.isValid();

            if (!whiteValid) {
                System.out.println("Turn " + turnNum + ": invalid white move '" + whiteNotation + "'");
                isValid = false;
            }
            if (blackMove != null && !blackValid) {
                System.out.println("Turn " + turnNum + ": invalid black move '" + blackNotation + "'");
                isValid = false;
            }

            turns.add(new Turn(turnNum, whiteMove, blackMove));
        }

        if (isValid) {
            System.out.println("Valid match");
        }
    }

    public void printTurns() {
        for (Turn turn : turns) {
            System.out.print(turn.getTurnNumber() + ". " + turn.getWhiteMove().getNotation());
            if (turn.getBlackMove() != null) {
                System.out.print(" " + turn.getBlackMove().getNotation());
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        String game = "1. e4 e5 2. Nf3 Nc6 3. Bb5 a6 4. Ba4 Nf6 5. O-O Be7 6. Re1 b5";
        Main syntax = new Main();
        syntax.parseGame(game);
        if (syntax.isValid) {
            syntax.printTurns();
        }
    }
}