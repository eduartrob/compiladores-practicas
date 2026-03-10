
// Código en Java, analizador léxico base
// AnalizadorLexico.java

import java.util.ArrayList;
import java.util.List;

// 1. Clase para representar el Token (Componente Léxico)
class Token {
    String tipo;
    String lexeme;

    public Token(String tipo, String lexeme) {
        this.tipo = tipo;
        this.lexeme = lexeme;
    }

    @Override
    public String toString() {
        return String.format("<%s, \"%s\">", tipo, lexeme);
    }
}

public class TareaPrograma2_221214 {

    // 2. Definición de la Tabla de Transición
    // Columnas: 0=Letra, 1=Dígito, 2=Espacio/Otro
    // Columnas: 0=Letra, 1=Dígito, 2=Punto 3=Otro

    // Estados: 0=Inicio, 1=ID, 2=NUM, 3=Aceptar ID, 4=Aceptar NUM
    // aceptar un . y luego los decimales
    /*
     * private static final int[][] TABLA = {
     * // L D O
     * { 1, 2, 0 }, // S0: Inicio
     * { 1, 1, 3 }, // S1: Construyendo ID (acepta L o D)
     * { -1, 2, 4 } // S2: Construyendo NUM (solo acepta D)
     * 
     * };
     */

    private static final int[][] TABLA = {
            // L D . O
            { 1, 2, -1, 0 }, // S0: Inicio
            { 1, 1, -1, 5 }, // S1: Construyendo ID
            { -1, 2, 3, 6 }, // S2: Construyendo NUM entero
            { -1, 4, -1, -1 }, // S3: Leímos el punto (DEBE seguir dígito)
            { -1, 4, -1, 7 } // S4: Construyendo parte decimal
    };

    // Mapeo de categorías
    // Modificado: _ y $ se tratan como letras para IDs
    private int getCategoria(char c) {
        if (Character.isLetter(c) || c == '_' || c == '$')
            return 0; // Letra, guión bajo o signo de pesos
        if (Character.isDigit(c))
            return 1;
        if (c == '.')
            return 2;
        return 3; // Espacios, otros caracteres
    }

    public List<Token> escanear(String entrada) {
        List<Token> tokens = new ArrayList<>();
        int estadoActual = 0;
        StringBuilder lexemaActual = new StringBuilder();
        int i = 0;

        // Simulamos el manejo del buffer de entrada con un puntero 'i'
        while (i < entrada.length()) {
            char c = entrada.charAt(i);
            int cat = getCategoria(c);
            int siguienteEstado = TABLA[estadoActual][cat];

            if (siguienteEstado == -1) {
                System.err.println("Error Léxico: Carácter inesperado '" + c + "'");
                break;
            }

            // Lógica de transición
            if (siguienteEstado == 5) { // Aceptar ID
                tokens.add(new Token("ID", lexemaActual.toString()));
                lexemaActual.setLength(0); // Limpiar buffer
                estadoActual = 0;
                // No incrementamos 'i' para que el delimitador se procese en S0
            } else if (siguienteEstado == 6) { // Aceptar NUM
                tokens.add(new Token("NUM", lexemaActual.toString()));
                lexemaActual.setLength(0);
                estadoActual = 0;
            } else if (siguienteEstado == 7) { // Aceptar NUM_DEC
                tokens.add(new Token("NUM_DEC", lexemaActual.toString()));
                lexemaActual.setLength(0);
                estadoActual = 0;
            } else {
                // Seguir acumulando caracteres en el estado actual (S1 o S2)
                if (siguienteEstado != 0) {
                    lexemaActual.append(c);
                }
                estadoActual = siguienteEstado;
                i++; // Avanzar puntero
            }
        }

        // Procesar el último token si quedó algo en el buffer al terminar el string
        if (lexemaActual.length() > 0) {
            String tipo;
            if (estadoActual == 1)
                tipo = "ID";
            else if (estadoActual == 4)
                tipo = "NUM_DEC"; // ¡NUEVO!
            else
                tipo = "NUM";
            tokens.add(new Token(tipo, lexemaActual.toString()));
        }

        return tokens;
    }

    public static void main(String[] args) {
        TareaPrograma2_221214 lexer = new TareaPrograma2_221214();
        String codigo = "_variable = 100; $precio = 99.99; mi_valor = 24.5; $total_final = 25; _x1 = 3.14; nombre$ = 5; ";

        System.out.println("Entrada: " + codigo);
        List<Token> resultado = lexer.escanear(codigo);

        System.out.println("\nTokens encontrados:");
        for (Token t : resultado) {
            System.out.println(t);
        }
    }
}