
// Analizador Léxico Completo - Etapa 1 del Compilador
// Compiladores e Intérpretes

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Clase para representar un símbolo en la tabla
class SimboloCompleto {
    int id;
    String nombre;
    String tipo;
    String valor;

    public SimboloCompleto(int id, String nombre, String tipo, String valor) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.valor = valor;
    }

    @Override
    public String toString() {
        return String.format("| %3d | %-20s | %-18s | %-15s |", id, nombre, tipo, valor);
    }
}

// Clase para representar el Token
class TokenCompleto {
    String tipo;
    String lexema;

    public TokenCompleto(String tipo, String lexema) {
        this.tipo = tipo;
        this.lexema = lexema;
    }

    @Override
    public String toString() {
        return String.format("%-20s ( %s )", tipo, lexema);
    }
}

public class AnalizadorLexicoCompleto {

    // Tabla de símbolos
    private List<SimboloCompleto> tablaSimbolos;
    private int contadorId;

    // Mapeo de palabras reservadas a su tipo de token
    private static final Map<String, String> PALABRAS_RESERVADAS = new HashMap<>();
    static {
        // Estructuras de control
        PALABRAS_RESERVADAS.put("if", "KEY_IF");
        PALABRAS_RESERVADAS.put("then", "KEY_THEN");
        PALABRAS_RESERVADAS.put("else", "KEY_ELSE");
        PALABRAS_RESERVADAS.put("case", "KEY_CASE");
        PALABRAS_RESERVADAS.put("while", "KEY_WHILE");
        PALABRAS_RESERVADAS.put("for", "KEY_FOR");
        PALABRAS_RESERVADAS.put("do", "KEY_DO");
        PALABRAS_RESERVADAS.put("return", "KEY_RETURN");
        PALABRAS_RESERVADAS.put("break", "KEY_BREAK");
        PALABRAS_RESERVADAS.put("continue", "KEY_CONTINUE");

        // Tipos de datos
        PALABRAS_RESERVADAS.put("int", "KEY_INT");
        PALABRAS_RESERVADAS.put("float", "KEY_FLOAT");
        PALABRAS_RESERVADAS.put("string", "KEY_STRING");
        PALABRAS_RESERVADAS.put("date", "KEY_DATE");
        PALABRAS_RESERVADAS.put("bool", "KEY_BOOL");
        PALABRAS_RESERVADAS.put("void", "KEY_VOID");

        // Operadores lógicos (como palabras)
        PALABRAS_RESERVADAS.put("and", "OPERA_AND");
        PALABRAS_RESERVADAS.put("or", "OPERA_OR");
        PALABRAS_RESERVADAS.put("not", "OPERA_NOT");
        PALABRAS_RESERVADAS.put("true", "KEY_TRUE");
        PALABRAS_RESERVADAS.put("false", "KEY_FALSE");
    }

    // Mapeo de operadores y delimitadores de un solo carácter
    private static final Map<Character, String> OPERADORES_SIMPLES = new HashMap<>();
    static {
        OPERADORES_SIMPLES.put('+', "OPERA_SUMA");
        OPERADORES_SIMPLES.put('-', "OPERA_RESTA");
        OPERADORES_SIMPLES.put('*', "OPERA_MULT");
        OPERADORES_SIMPLES.put('/', "OPERA_DIVID");
        OPERADORES_SIMPLES.put('=', "ASIGNA");
        OPERADORES_SIMPLES.put(';', "FIN_SENTENCIA");
        OPERADORES_SIMPLES.put('{', "INI_BLOQUE");
        OPERADORES_SIMPLES.put('}', "FIN_BLOQUE");
        OPERADORES_SIMPLES.put('(', "ABRE_PARENTESIS");
        OPERADORES_SIMPLES.put(')', "CIERRA_PARENTESIS");
        OPERADORES_SIMPLES.put('<', "OPERA_MENOR");
        OPERADORES_SIMPLES.put('>', "OPERA_MAYOR");
        OPERADORES_SIMPLES.put(',', "COMA");
    }

    // Constructor
    public AnalizadorLexicoCompleto() {
        tablaSimbolos = new ArrayList<>();
        contadorId = 1;
        cargarPalabrasReservadas();
    }

    // Carga las palabras reservadas en la tabla de símbolos
    private void cargarPalabrasReservadas() {
        for (Map.Entry<String, String> entry : PALABRAS_RESERVADAS.entrySet()) {
            tablaSimbolos.add(new SimboloCompleto(contadorId++, entry.getKey(), entry.getValue(), "-"));
        }
    }

    // Verifica si un símbolo ya existe en la tabla (por nombre)
    private boolean existeSimbolo(String nombre) {
        for (SimboloCompleto s : tablaSimbolos) {
            if (s.nombre.equals(nombre)) {
                return true;
            }
        }
        return false;
    }

    // Obtiene el tipo de un símbolo existente
    private String getTipoSimbolo(String nombre) {
        for (SimboloCompleto s : tablaSimbolos) {
            if (s.nombre.equals(nombre)) {
                return s.tipo;
            }
        }
        return null;
    }

    // Inserta un símbolo en la tabla
    private void insertarSimbolo(String nombre, String tipo, String valor) {
        if (!existeSimbolo(nombre)) {
            tablaSimbolos.add(new SimboloCompleto(contadorId++, nombre, tipo, valor));
        }
    }

    // Método principal de escaneo
    public List<TokenCompleto> escanear(String entrada) {
        List<TokenCompleto> tokens = new ArrayList<>();
        int i = 0;
        int longitud = entrada.length();

        while (i < longitud) {
            char c = entrada.charAt(i);

            // Ignorar espacios en blanco
            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }

            // Identificadores y palabras reservadas
            if (Character.isLetter(c) || c == '_' || c == '$') {
                StringBuilder lexema = new StringBuilder();
                while (i < longitud && (Character.isLetterOrDigit(entrada.charAt(i))
                        || entrada.charAt(i) == '_' || entrada.charAt(i) == '$')) {
                    lexema.append(entrada.charAt(i));
                    i++;
                }
                String palabra = lexema.toString();

                // Verificar si es palabra reservada
                if (PALABRAS_RESERVADAS.containsKey(palabra)) {
                    tokens.add(new TokenCompleto(PALABRAS_RESERVADAS.get(palabra), palabra));
                } else {
                    tokens.add(new TokenCompleto("ID", palabra));
                    insertarSimbolo(palabra, "ID", "-");
                }
                continue;
            }

            // Números (enteros y decimales)
            if (Character.isDigit(c)) {
                StringBuilder lexema = new StringBuilder();
                boolean esDecimal = false;

                while (i < longitud && (Character.isDigit(entrada.charAt(i)) || entrada.charAt(i) == '.')) {
                    if (entrada.charAt(i) == '.') {
                        if (esDecimal)
                            break; // Ya es decimal, no agregar otro punto
                        esDecimal = true;
                    }
                    lexema.append(entrada.charAt(i));
                    i++;
                }

                String numero = lexema.toString();
                if (esDecimal) {
                    tokens.add(new TokenCompleto("NUM_FLOAT", numero));
                    insertarSimbolo(numero, "NUM_FLOAT", numero);
                } else {
                    tokens.add(new TokenCompleto("NUM", numero));
                    insertarSimbolo(numero, "NUM", numero);
                }
                continue;
            }

            // Cadenas de texto (entre comillas)
            if (c == '"') {
                StringBuilder lexema = new StringBuilder();
                lexema.append(c); // Incluir comilla inicial
                i++; // Avanzar después de la comilla inicial

                while (i < longitud && entrada.charAt(i) != '"') {
                    lexema.append(entrada.charAt(i));
                    i++;
                }

                if (i < longitud) {
                    lexema.append(entrada.charAt(i)); // Incluir comilla final
                    i++;
                }

                String cadena = lexema.toString();
                tokens.add(new TokenCompleto("CADENA", cadena));
                insertarSimbolo(cadena, "CADENA", cadena);
                continue;
            }

            // Operadores y delimitadores
            if (OPERADORES_SIMPLES.containsKey(c)) {
                tokens.add(new TokenCompleto(OPERADORES_SIMPLES.get(c), String.valueOf(c)));
                i++;
                continue;
            }

            // Carácter no reconocido
            System.err.println("Error Léxico: Carácter no reconocido '" + c + "' en posición " + i);
            i++;
        }

        return tokens;
    }

    // Muestra la tabla de símbolos
    public void mostrarTablaSimbolos() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                         TABLA DE SÍMBOLOS                            ║");
        System.out.println("╠═════╦══════════════════════╦════════════════════╦═════════════════════╣");
        System.out.println("║ ID  ║       NOMBRE         ║        TIPO        ║       VALOR         ║");
        System.out.println("╠═════╬══════════════════════╬════════════════════╬═════════════════════╣");
        for (SimboloCompleto s : tablaSimbolos) {
            System.out.println(s);
        }
        System.out.println("╚═════╩══════════════════════╩════════════════════╩═════════════════════╝");
    }

    // Mostrar tokens agrupados por categoría
    public void mostrarTokensPorCategoria(List<TokenCompleto> tokens) {
        System.out.println("\n════════════════════════════════════════════════════════════════════════");
        System.out.println("                         TOKENS ENCONTRADOS                             ");
        System.out.println("════════════════════════════════════════════════════════════════════════");

        // Agrupar por tipo
        Map<String, List<TokenCompleto>> grupos = new HashMap<>();
        for (TokenCompleto t : tokens) {
            String categoria = obtenerCategoria(t.tipo);
            grupos.computeIfAbsent(categoria, k -> new ArrayList<>()).add(t);
        }

        // Mostrar cada grupo
        String[] orden = { "PALABRAS RESERVADAS", "IDENTIFICADORES", "NÚMEROS", "CADENAS",
                "DELIMITADORES", "OPERADORES MATEMÁTICOS", "OPERADORES LÓGICOS", "OTROS" };

        for (String cat : orden) {
            if (grupos.containsKey(cat)) {
                System.out.println("\n─── " + cat + " ───");
                for (TokenCompleto t : grupos.get(cat)) {
                    System.out.println("  " + t);
                }
            }
        }
    }

    private String obtenerCategoria(String tipo) {
        if (tipo.startsWith("KEY_"))
            return "PALABRAS RESERVADAS";
        if (tipo.equals("ID"))
            return "IDENTIFICADORES";
        if (tipo.equals("NUM") || tipo.equals("NUM_FLOAT"))
            return "NÚMEROS";
        if (tipo.equals("CADENA"))
            return "CADENAS";
        if (tipo.contains("SENTENCIA") || tipo.contains("BLOQUE") || tipo.contains("PARENTESIS") || tipo.equals("COMA"))
            return "DELIMITADORES";
        if (tipo.contains("SUMA") || tipo.contains("RESTA") || tipo.contains("MULT") || tipo.contains("DIVID")
                || tipo.equals("ASIGNA") || tipo.contains("MENOR") || tipo.contains("MAYOR"))
            return "OPERADORES MATEMÁTICOS";
        if (tipo.contains("AND") || tipo.contains("OR") || tipo.contains("NOT"))
            return "OPERADORES LÓGICOS";
        return "OTROS";
    }

    public static void main(String[] args) {
        AnalizadorLexicoCompleto lexer = new AnalizadorLexicoCompleto();

        // Código de ejemplo que demuestra TODOS los tipos de tokens
        String codigo = """
                int x = 100;
                float precio = 3.14;
                string nombre = "Eduardo";
                date fecha;

                if (x > 0) then {
                    y = x + 5;
                    z = y * 2;
                } else {
                    y = 0;
                }

                while (contador < 10) do {
                    suma = suma + 1;
                    resta = suma - 1;
                    mult = resta * 2;
                    div = mult / 2;
                }

                resultado = a and b or not c;

                if (activo == true) then {
                    return valor;
                }
                """;

        System.out.println("════════════════════════════════════════════════════════════════════════");
        System.out.println("          ANALIZADOR LÉXICO COMPLETO - ETAPA 1 DEL COMPILADOR          ");
        System.out.println("════════════════════════════════════════════════════════════════════════");
        System.out.println("\n📝 CÓDIGO FUENTE DE ENTRADA:");
        System.out.println("────────────────────────────────────────────────────────────────────────");
        System.out.println(codigo);
        System.out.println("────────────────────────────────────────────────────────────────────────");

        List<TokenCompleto> resultado = lexer.escanear(codigo);

        // Mostrar tokens organizados por categoría
        lexer.mostrarTokensPorCategoria(resultado);

        // Mostrar todos los tokens en orden
        System.out.println("\n════════════════════════════════════════════════════════════════════════");
        System.out.println("                    SECUENCIA DE TOKENS (en orden)                      ");
        System.out.println("════════════════════════════════════════════════════════════════════════");
        int contador = 1;
        for (TokenCompleto t : resultado) {
            System.out.printf("%3d. %s%n", contador++, t);
        }

        // Mostrar la tabla de símbolos
        lexer.mostrarTablaSimbolos();

        System.out.println("\n✅ Análisis léxico completado. Total de tokens: " + resultado.size());
    }
}
