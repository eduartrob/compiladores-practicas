
// Código en Java, analizador léxico con Tabla de Símbolos
// Tarea Programa 3 - Compiladores e Intérpretes

import java.util.ArrayList;
import java.util.List;

// Clase para representar un símbolo en la tabla
class Simbolo {
    int id;
    String nombre;
    String tipo;
    String valor;

    public Simbolo(int id, String nombre, String tipo, String valor) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.valor = valor;
    }

    @Override
    public String toString() {
        return String.format("| %3d | %-15s | %-12s | %-10s |", id, nombre, tipo, valor);
    }
}

// Clase para representar el Token
class Token3 {
    String tipo;
    String lexeme;

    public Token3(String tipo, String lexeme) {
        this.tipo = tipo;
        this.lexeme = lexeme;
    }

    @Override
    public String toString() {
        return String.format("<%s, \"%s\">", tipo, lexeme);
    }
}

public class TareaPrograma3_221214 {

    // Tabla de símbolos
    private List<Simbolo> tablaSimbolos;
    private int contadorId;

    // Palabras reservadas del lenguaje
    private static final String[] PALABRAS_RESERVADAS = {
            "if", "else", "while", "for", "do", "switch", "case", "break",
            "continue", "return", "int", "float", "double", "char", "void",
            "public", "private", "class", "static", "final"
    };

    // Tabla de transiciones
    private static final int[][] TABLA = {
            // L D . O
            { 1, 2, -1, 0 }, // S0: Inicio
            { 1, 1, -1, 5 }, // S1: Construyendo ID
            { -1, 2, 3, 6 }, // S2: Construyendo NUM entero
            { -1, 4, -1, -1 }, // S3: Leímos el punto
            { -1, 4, -1, 7 } // S4: Construyendo parte decimal
    };

    // Constructor: inicializa la tabla y carga palabras reservadas
    public TareaPrograma3_221214() {
        tablaSimbolos = new ArrayList<>();
        contadorId = 1;
        cargarPalabrasReservadas();
    }

    // Carga las palabras reservadas en la tabla de símbolos
    private void cargarPalabrasReservadas() {
        for (String palabra : PALABRAS_RESERVADAS) {
            tablaSimbolos.add(new Simbolo(contadorId++, palabra, "RESERVADA", "-"));
        }
    }

    // Verifica si un símbolo ya existe en la tabla (por nombre)
    private boolean existeSimbolo(String nombre) {
        for (Simbolo s : tablaSimbolos) {
            if (s.nombre.equals(nombre)) {
                return true;
            }
        }
        return false;
    }

    // Inserta un símbolo en la tabla (si no existe para IDs)
    private void insertarSimbolo(String nombre, String tipo, String valor) {
        // Para identificadores, verificar que no exista
        if (tipo.equals("ID") && existeSimbolo(nombre)) {
            return; // Ya existe, no se agrega
        }
        // Para números, siempre se agregan (pueden repetirse valores)
        tablaSimbolos.add(new Simbolo(contadorId++, nombre, tipo, valor));
    }

    // Mapeo de categorías
    private int getCategoria(char c) {
        if (Character.isLetter(c) || c == '_' || c == '$')
            return 0;
        if (Character.isDigit(c))
            return 1;
        if (c == '.')
            return 2;
        return 3;
    }

    public List<Token3> escanear(String entrada) {
        List<Token3> tokens = new ArrayList<>();
        int estadoActual = 0;
        StringBuilder lexemaActual = new StringBuilder();
        int i = 0;

        while (i < entrada.length()) {
            char c = entrada.charAt(i);
            int cat = getCategoria(c);
            int siguienteEstado = TABLA[estadoActual][cat];

            if (siguienteEstado == -1) {
                System.err.println("Error Léxico: Carácter inesperado '" + c + "'");
                break;
            }

            if (siguienteEstado == 5) { // Aceptar ID
                String lexema = lexemaActual.toString();
                // Verificar si es palabra reservada
                if (existeSimbolo(lexema)) {
                    tokens.add(new Token3("RESERVADA", lexema));
                } else {
                    tokens.add(new Token3("ID", lexema));
                    insertarSimbolo(lexema, "ID", "-");
                }
                lexemaActual.setLength(0);
                estadoActual = 0;
            } else if (siguienteEstado == 6) { // Aceptar NUM
                String lexema = lexemaActual.toString();
                tokens.add(new Token3("NUM", lexema));
                insertarSimbolo(lexema, "NUM", lexema);
                lexemaActual.setLength(0);
                estadoActual = 0;
            } else if (siguienteEstado == 7) { // Aceptar NUM_DEC
                String lexema = lexemaActual.toString();
                tokens.add(new Token3("NUM_DEC", lexema));
                insertarSimbolo(lexema, "NUM_DEC", lexema);
                lexemaActual.setLength(0);
                estadoActual = 0;
            } else {
                if (siguienteEstado != 0) {
                    lexemaActual.append(c);
                }
                estadoActual = siguienteEstado;
                i++;
            }
        }

        // Procesar el último token
        if (lexemaActual.length() > 0) {
            String lexema = lexemaActual.toString();
            String tipo;
            if (estadoActual == 1) {
                if (existeSimbolo(lexema)) {
                    tipo = "RESERVADA";
                } else {
                    tipo = "ID";
                    insertarSimbolo(lexema, "ID", "-");
                }
            } else if (estadoActual == 4) {
                tipo = "NUM_DEC";
                insertarSimbolo(lexema, "NUM_DEC", lexema);
            } else {
                tipo = "NUM";
                insertarSimbolo(lexema, "NUM", lexema);
            }
            tokens.add(new Token3(tipo, lexema));
        }

        return tokens;
    }

    // Muestra la tabla de símbolos
    public void mostrarTablaSimbolos() {
        System.out.println("\n╔═════════════════════════════════════════════════════╗");
        System.out.println("║              TABLA DE SÍMBOLOS                      ║");
        System.out.println("╠═════╦═════════════════╦══════════════╦══════════════╣");
        System.out.println("║ ID  ║     NOMBRE      ║     TIPO     ║    VALOR     ║");
        System.out.println("╠═════╬═════════════════╬══════════════╬══════════════╣");
        for (Simbolo s : tablaSimbolos) {
            System.out.println(s);
        }
        System.out.println("╚═════╩═════════════════╩══════════════╩══════════════╝");
    }

    public static void main(String[] args) {
        TareaPrograma3_221214 lexer = new TareaPrograma3_221214();

        // Código de ejemplo con palabras reservadas, IDs, números y decimales
        String codigo = "int x = 100; float precio = 99.99; if x > 0 then y = x; while contador < 10 do suma = suma + 3.14; ";

        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("         ANALIZADOR LÉXICO CON TABLA DE SÍMBOLOS       ");
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("\nEntrada: " + codigo);

        List<Token3> resultado = lexer.escanear(codigo);

        System.out.println("\n───────────────────────────────────────────────────────");
        System.out.println("                  TOKENS ENCONTRADOS                   ");
        System.out.println("───────────────────────────────────────────────────────");
        for (Token3 t : resultado) {
            System.out.println(t);
        }

        // Mostrar la tabla de símbolos al final
        lexer.mostrarTablaSimbolos();
    }
}
