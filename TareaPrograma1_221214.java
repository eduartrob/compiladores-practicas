
// Código en Java, analizador léxico base
// AnalizadorLexico.java
// Tarea Programa 1 - Implementación de números decimales

import java.util.ArrayList;
import java.util.List;

// 1. Clase para representar el Token (Componente Léxico)
class Token1 {
    String tipo;
    String lexeme;

    public Token1(String tipo, String lexeme) {
        this.tipo = tipo;
        this.lexeme = lexeme;
    }

    @Override
    public String toString() {
        return String.format("<%s, \"%s\">", tipo, lexeme);
    }
}

public class TareaPrograma1_221214 {

    // 2. Definición de la Tabla de Transición
    // Columnas: 0=Letra, 1=Dígito, 2=Punto, 3=Otro
    // Estados: 0=Inicio, 1=ID, 2=NUM, 3=Punto, 4=Decimal
    // Estados de aceptación: 5=Acepta ID, 6=Acepta NUM, 7=Acepta NUM_DEC

    private static final int[][] TABLA = {
            // L D . O
            { 1, 2, -1, 0 }, // S0: Inicio
            { 1, 1, -1, 5 }, // S1: Construyendo ID
            { -1, 2, 3, 6 }, // S2: Construyendo NUM entero
            { -1, 4, -1, -1 }, // S3: Leímos el punto (DEBE seguir dígito)
            { -1, 4, -1, 7 } // S4: Construyendo parte decimal
    };

    // Mapeo de categorías
    private int getCategoria(char c) {
        if (Character.isLetter(c))
            return 0; // Letra
        if (Character.isDigit(c))
            return 1; // Dígito
        if (c == '.')
            return 2; // Punto
        return 3; // Espacios, otros caracteres
    }

    public List<Token1> escanear(String entrada) {
        List<Token1> tokens = new ArrayList<>();
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
                tokens.add(new Token1("ID", lexemaActual.toString()));
                lexemaActual.setLength(0); // Limpiar buffer
                estadoActual = 0;
                // No incrementamos 'i' para que el delimitador se procese en S0
            } else if (siguienteEstado == 6) { // Aceptar NUM
                tokens.add(new Token1("NUM", lexemaActual.toString()));
                lexemaActual.setLength(0);
                estadoActual = 0;
            } else if (siguienteEstado == 7) { // Aceptar NUM_DEC
                tokens.add(new Token1("NUM_DEC", lexemaActual.toString()));
                lexemaActual.setLength(0);
                estadoActual = 0;
            } else {
                // Seguir acumulando caracteres en el estado actual
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
                tipo = "NUM_DEC";
            else
                tipo = "NUM";
            tokens.add(new Token1(tipo, lexemaActual.toString()));
        }

        return tokens;
    }

    public static void main(String[] args) {
        TareaPrograma1_221214 lexer = new TareaPrograma1_221214();
        String codigo = "variable1 = 100; precio = 99.99; temperatura = 24.5; contador = 25; resultado = 3.14159; x = 5; ";

        System.out.println("Entrada: " + codigo);
        List<Token1> resultado = lexer.escanear(codigo);

        System.out.println("\nTokens encontrados:");
        for (Token1 t : resultado) {
            System.out.println(t);
        }
    }
}
