import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


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

class TokenCompleto {
    String tipo;
    String lexema;

    public TokenCompleto(String tipo, String lexema) {
        this.tipo = tipo;
        this.lexema = lexema;
    }

    @Override
    public String toString() {
        return String.format("%-22s ( %s )", tipo, lexema);
    }
}

class AnalizadorLexicoCompleto {

    private List<SimboloCompleto> tablaSimbolos;
    private int contadorId;

    private static final Map<String, String> PALABRAS_RESERVADAS = new LinkedHashMap<>();
    static {
        // Control de flujo
        PALABRAS_RESERVADAS.put("if", "KEY_IF");
        PALABRAS_RESERVADAS.put("then", "KEY_THEN");
        PALABRAS_RESERVADAS.put("else", "KEY_ELSE");
        PALABRAS_RESERVADAS.put("case", "KEY_CASE");
        PALABRAS_RESERVADAS.put("do", "KEY_DO");
        PALABRAS_RESERVADAS.put("while", "KEY_WHILE");
        PALABRAS_RESERVADAS.put("switch", "KEY_SWITCH");
        PALABRAS_RESERVADAS.put("break", "KEY_BREAK");
        PALABRAS_RESERVADAS.put("for", "KEY_FOR");
        PALABRAS_RESERVADAS.put("continue", "KEY_CONTINUE");
        PALABRAS_RESERVADAS.put("return", "KEY_RETURN");

        // Estructuras y clases
        PALABRAS_RESERVADAS.put("class", "KEY_CLASS");
        PALABRAS_RESERVADAS.put("new", "KEY_NEW");
        PALABRAS_RESERVADAS.put("interface", "KEY_INTERFACE");
        PALABRAS_RESERVADAS.put("package", "KEY_PACKAGE");

        // Modificadores de acceso
        PALABRAS_RESERVADAS.put("public", "KEY_PUBLIC");
        PALABRAS_RESERVADAS.put("private", "KEY_PRIVATE");
        PALABRAS_RESERVADAS.put("static", "KEY_STATIC");
        PALABRAS_RESERVADAS.put("final", "KEY_FINAL");
        PALABRAS_RESERVADAS.put("void", "KEY_VOID");
        PALABRAS_RESERVADAS.put("main", "KEY_MAIN");

        // Tipos de datos
        PALABRAS_RESERVADAS.put("int", "KEY_INT");
        PALABRAS_RESERVADAS.put("long", "KEY_LONG");
        PALABRAS_RESERVADAS.put("float", "KEY_FLOAT");
        PALABRAS_RESERVADAS.put("double", "KEY_DOUBLE");
        PALABRAS_RESERVADAS.put("char", "KEY_CHAR");
        PALABRAS_RESERVADAS.put("boolean", "KEY_BOOLEAN");
        PALABRAS_RESERVADAS.put("string", "KEY_STRING");
        PALABRAS_RESERVADAS.put("local_date", "KEY_LOCAL_DATE");
        PALABRAS_RESERVADAS.put("local_time", "KEY_LOCAL_TIME");
        PALABRAS_RESERVADAS.put("bool", "KEY_BOOL");
        PALABRAS_RESERVADAS.put("date", "KEY_DATE");
        PALABRAS_RESERVADAS.put("true", "KEY_TRUE");
        PALABRAS_RESERVADAS.put("false", "KEY_FALSE");

        // Operadores lógicos
        PALABRAS_RESERVADAS.put("and", "OPERA_AND");
        PALABRAS_RESERVADAS.put("or", "OPERA_OR");
        PALABRAS_RESERVADAS.put("not", "OPERA_NOT");
    }

    private static final Map<Character, String> OPERADORES_SIMPLES = new HashMap<>();
    static {
        OPERADORES_SIMPLES.put('+', "OPERA_SUMA");
        OPERADORES_SIMPLES.put('-', "OPERA_RESTA");
        OPERADORES_SIMPLES.put('*', "OPERA_MULT");
        OPERADORES_SIMPLES.put('/', "OPERA_DIVID");
        OPERADORES_SIMPLES.put(';', "FIN_SENTENCIA");
        OPERADORES_SIMPLES.put('{', "INI_BLOQUE");
        OPERADORES_SIMPLES.put('}', "FIN_BLOQUE");
        OPERADORES_SIMPLES.put('(', "ABRE_PARENTESIS");
        OPERADORES_SIMPLES.put(')', "CIERRA_PARENTESIS");
        OPERADORES_SIMPLES.put(',', "COMA");
    }

    public AnalizadorLexicoCompleto() {
        tablaSimbolos = new ArrayList<>();
        contadorId = 1;
        cargarPalabrasReservadas();
    }

    private void cargarPalabrasReservadas() {
        for (Map.Entry<String, String> entry : PALABRAS_RESERVADAS.entrySet()) {
            tablaSimbolos.add(new SimboloCompleto(contadorId++, entry.getKey(), entry.getValue(), "-"));
        }
    }

    private boolean existeSimbolo(String nombre) {
        for (SimboloCompleto s : tablaSimbolos) {
            if (s.nombre.equals(nombre))
                return true;
        }
        return false;
    }

    private void insertarSimbolo(String nombre, String tipo, String valor) {
        if (!existeSimbolo(nombre)) {
            tablaSimbolos.add(new SimboloCompleto(contadorId++, nombre, tipo, valor));
        }
    }

    public List<TokenCompleto> escanear(String entrada) {
        List<TokenCompleto> tokens = new ArrayList<>();
        int i = 0;
        int longitud = entrada.length();

        while (i < longitud) {
            char c = entrada.charAt(i);

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

                if (PALABRAS_RESERVADAS.containsKey(palabra)) {
                    tokens.add(new TokenCompleto(PALABRAS_RESERVADAS.get(palabra), palabra));
                } else {
                    tokens.add(new TokenCompleto("ID", palabra));
                    insertarSimbolo(palabra, "ID", "-");
                }
                continue;
            }

            // Números enteros y decimales
            if (Character.isDigit(c)) {
                StringBuilder lexema = new StringBuilder();
                boolean esDecimal = false;

                while (i < longitud && (Character.isDigit(entrada.charAt(i)) || entrada.charAt(i) == '.')) {
                    if (entrada.charAt(i) == '.') {
                        if (esDecimal)
                            break;
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

            // Cadenas de texto
            if (c == '"') {
                StringBuilder lexema = new StringBuilder();
                lexema.append(c);
                i++;

                while (i < longitud && entrada.charAt(i) != '"') {
                    lexema.append(entrada.charAt(i));
                    i++;
                }

                if (i < longitud) {
                    lexema.append(entrada.charAt(i));
                    i++;
                }

                String cadena = lexema.toString();
                tokens.add(new TokenCompleto("CADENA", cadena));
                insertarSimbolo(cadena, "CADENA", cadena);
                continue;
            }

            // Operadores compuestos (2 caracteres): <=, >=, !=, ==
            if (i + 1 < longitud) {
                char siguiente = entrada.charAt(i + 1);

                if (c == '<' && siguiente == '=') {
                    tokens.add(new TokenCompleto("OPERA_MENOR_IGUAL", "<="));
                    i += 2;
                    continue;
                }
                if (c == '>' && siguiente == '=') {
                    tokens.add(new TokenCompleto("OPERA_MAYOR_IGUAL", ">="));
                    i += 2;
                    continue;
                }
                if (c == '!' && siguiente == '=') {
                    tokens.add(new TokenCompleto("OPERA_DIFERENTE", "!="));
                    i += 2;
                    continue;
                }
                if (c == '=' && siguiente == '=') {
                    tokens.add(new TokenCompleto("OPERA_IGUALDAD", "=="));
                    i += 2;
                    continue;
                }
            }

            // Operadores simples: < > =
            if (c == '<') {
                tokens.add(new TokenCompleto("OPERA_MENOR", "<"));
                i++;
                continue;
            }
            if (c == '>') {
                tokens.add(new TokenCompleto("OPERA_MAYOR", ">"));
                i++;
                continue;
            }
            if (c == '=') {
                tokens.add(new TokenCompleto("ASIGNA", "="));
                i++;
                continue;
            }
            if (c == '!') {
                tokens.add(new TokenCompleto("OPERA_NOT", "!"));
                i++;
                continue;
            }

            // Operadores y delimitadores simples
            if (OPERADORES_SIMPLES.containsKey(c)) {
                tokens.add(new TokenCompleto(OPERADORES_SIMPLES.get(c), String.valueOf(c)));
                i++;
                continue;
            }

            System.err.println("Error Léxico: Carácter no reconocido '" + c + "' en posición " + i);
            i++;
        }

        return tokens;
    }

    public void mostrarTablaSimbolos() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                         TABLA DE SÍMBOLOS                           ║");
        System.out.println("╠═════╦══════════════════════╦════════════════════╦═════════════════════╣");
        System.out.println("║ ID  ║       NOMBRE         ║        TIPO        ║       VALOR         ║");
        System.out.println("╠═════╬══════════════════════╬════════════════════╬═════════════════════╣");
        for (SimboloCompleto s : tablaSimbolos) {
            System.out.println(s);
        }
        System.out.println("╚═════╩══════════════════════╩════════════════════╩═════════════════════╝");
    }

    public void mostrarTokensPorCategoria(List<TokenCompleto> tokens) {
        System.out.println("\n════════════════════════════════════════════════════════════════════════");
        System.out.println("                         TOKENS ENCONTRADOS                             ");
        System.out.println("════════════════════════════════════════════════════════════════════════");

        Map<String, List<TokenCompleto>> grupos = new HashMap<>();
        for (TokenCompleto t : tokens) {
            String categoria = obtenerCategoria(t.tipo);
            grupos.computeIfAbsent(categoria, k -> new ArrayList<>()).add(t);
        }

        String[] orden = { "PALABRAS RESERVADAS", "IDENTIFICADORES", "NÚMEROS", "CADENAS",
                "DELIMITADORES", "ASIGNACIÓN", "OPERADORES MATEMÁTICOS",
                "OPERADORES DE COMPARACIÓN", "OPERADORES LÓGICOS", "OTROS" };

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
        if (tipo.equals("ASIGNA"))
            return "ASIGNACIÓN";
        if (tipo.contains("SUMA") || tipo.contains("RESTA") || tipo.contains("MULT") || tipo.contains("DIVID"))
            return "OPERADORES MATEMÁTICOS";
        if (tipo.contains("MENOR") || tipo.contains("MAYOR") || tipo.contains("IGUALDAD") || tipo.contains("DIFERENTE"))
            return "OPERADORES DE COMPARACIÓN";
        if (tipo.contains("AND") || tipo.contains("OR") || tipo.contains("NOT"))
            return "OPERADORES LÓGICOS";
        return "OTROS";
    }

    public static void main(String[] args) {
        AnalizadorLexicoCompleto lexer = new AnalizadorLexicoCompleto();

        String codigo = """
                public class Main {
                    static void main() {
                        int x = 100;
                        long grande = 999999;
                        float precio = 3.14;
                        double pi = 3.14159;
                        char letra = "a";
                        boolean activo = true;
                        string nombre = "Eduardo";
                        local_date fecha;
                        local_time hora;

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

                        for (i = 0; i < 100; i = i + 1) {
                            total = total + i;
                        }

                        switch (opcion) {
                            case 1;
                                break;
                        }

                        if (x >= 10 and y <= 20) then {
                            resultado = x;
                        }

                        if (x != y or not activo) then {
                            continue;
                        }

                        if (a == b) then {
                            return a;
                        }

                        Persona p = new Persona();
                    }
                }
                """;

        System.out.println("════════════════════════════════════════════════════════════════════════");
        System.out.println("          ANALIZADOR LÉXICO COMPLETO - ETAPA 1 DEL COMPILADOR          ");
        System.out.println("════════════════════════════════════════════════════════════════════════");
        System.out.println("\nCÓDIGO FUENTE DE ENTRADA:");
        System.out.println("────────────────────────────────────────────────────────────────────────");
        System.out.println(codigo);
        System.out.println("────────────────────────────────────────────────────────────────────────");

        List<TokenCompleto> resultado = lexer.escanear(codigo);

        lexer.mostrarTokensPorCategoria(resultado);

        System.out.println("\n════════════════════════════════════════════════════════════════════════");
        System.out.println("                    SECUENCIA DE TOKENS (en orden)                      ");
        System.out.println("════════════════════════════════════════════════════════════════════════");
        int contador = 1;
        for (TokenCompleto t : resultado) {
            System.out.printf("%3d. %s%n", contador++, t);
        }

        lexer.mostrarTablaSimbolos();

        System.out.println("\nAnálisis léxico completado. Total de tokens: " + resultado.size());
    }
}


// nodos del ast
abstract class NodoAST {
    public abstract String getValor();
    
// imprimir en consola
    public abstract void imprimir(String prefijo, boolean esUltimo);
}

// hoja de id o num
class NodoHoja extends NodoAST {
    String tipo; 
    String lexema;

    public NodoHoja(String tipo, String lexema) {
        this.tipo = tipo;
        this.lexema = lexema;
    }

    @Override
    public String getValor() {
        return lexema;
    }

    @Override
    public void imprimir(String prefijo, boolean esUltimo) {
        System.out.println(prefijo + (esUltimo ? "└── " : "├── ") + tipo + "(" + lexema + ")");
    }
}

// operacion matematica
class NodoOperacion extends NodoAST {
    String operador;
    String tipoNodo; 
    NodoAST izquierdo;
    NodoAST derecho;

    public NodoOperacion(String tipoNodo, String operador, NodoAST izquierdo, NodoAST derecho) {
        this.tipoNodo = tipoNodo;
        this.operador = operador;
        this.izquierdo = izquierdo;
        this.derecho = derecho;
    }

    @Override
    public String getValor() {
        return operador;
    }

    @Override
    public void imprimir(String prefijo, boolean esUltimo) {
        System.out.println(prefijo + (esUltimo ? "└── " : "├── ") + tipoNodo);
        
        String nuevoPrefijo = prefijo + (esUltimo ? "    " : "│   ");
        
// subarbol izquierdo
        if (izquierdo != null) {
            izquierdo.imprimir(nuevoPrefijo, false);
        }
        
        System.out.println(nuevoPrefijo + "├── " + operador);
        
// subarbol derecho
        if (derecho != null) {
            derecho.imprimir(nuevoPrefijo, true);
        }
    }
}

// asignacion
class NodoAsignacion extends NodoAST {
    NodoHoja variable;
    NodoAST expresion;

    public NodoAsignacion(NodoHoja variable, NodoAST expresion) {
        this.variable = variable;
        this.expresion = expresion;
    }

    @Override
    public String getValor() {
        return "=";
    }

    @Override
    public void imprimir(String prefijo, boolean esUltimo) {
        System.out.println(prefijo + "ASIGNACIÓN ( = )");
        variable.imprimir(prefijo + "    ", false);
        expresion.imprimir(prefijo + "    ", true);
    }
}

// analizador sintactico recursivo
class Parser {
    private List<TokenCompleto> tokens;
    private int posicionActual;
    
    public Parser(List<TokenCompleto> tokens) {
        this.tokens = tokens;
        this.posicionActual = 0;
    }
    
// obtener token actual
    private TokenCompleto obtenerToken() {
        if (posicionActual < tokens.size()) {
            return tokens.get(posicionActual);
        }
        return new TokenCompleto("EOF", "");
    }
    
// consumir token
    private void consumir() {
        posicionActual++;
    }
    
// parseo de instruccion
    public NodeAsignacionOExpresion parseInstruccion() {
// verificamos asignacion
        int inicio = posicionActual;
        
        if (obtenerToken().tipo.equals("ID")) {
            TokenCompleto idVar = obtenerToken();
            consumir(); 
            
            if (obtenerToken().tipo.equals("ASIGNA")) {
                consumir(); 
                
// parseamos expresion
                NodoAST expr = parseE(); 
                
// ignoramos fin bateria
                if (obtenerToken().tipo.equals("FIN_SENTENCIA")) {
                    consumir();
                }
                
                return new NodeAsignacionOExpresion(
                    new NodoAsignacion(new NodoHoja("id", idVar.lexema), expr)
                );
            }
        }
        
// parseamos expresion
        posicionActual = inicio;
        NodoAST expr = parseE();
        
        if (obtenerToken().tipo.equals("FIN_SENTENCIA")) {
            consumir();
        }
        
        return new NodeAsignacionOExpresion(expr);
    }

// sumas y restas
    private NodoAST parseE() {
        NodoAST nodoIzq = parseT(); 
        
        while (obtenerToken().tipo.equals("OPERA_SUMA") || obtenerToken().tipo.equals("OPERA_RESTA")) {
            TokenCompleto operador = obtenerToken();
            consumir(); 
            NodoAST nodoDer = parseT(); 
            
            nodoIzq = new NodoOperacion("E", operador.lexema, nodoIzq, nodoDer);
        }
        
        return nodoIzq;
    }
    
// mult y div
    private NodoAST parseT() {
        NodoAST nodoIzq = parseF(); 
        
        while (obtenerToken().tipo.equals("OPERA_MULT") || obtenerToken().tipo.equals("OPERA_DIVID")) {
            TokenCompleto operador = obtenerToken();
            consumir(); 
            NodoAST nodoDer = parseF(); 
            
            nodoIzq = new NodoOperacion("T", operador.lexema, nodoIzq, nodoDer);
        }
        
        return nodoIzq;
    }
    
// id num y parentesis
    private NodoAST parseF() {
        TokenCompleto token = obtenerToken();
        
// caso id
        if (token.tipo.equals("ID")) {
            consumir();
            return new NodoHoja("id", token.lexema);
        }
        
// caso num
        if (token.tipo.equals("NUM") || token.tipo.equals("NUM_FLOAT")) {
            consumir();
            return new NodoHoja("num", token.lexema);
        }
        
// caso parentesis
        if (token.tipo.equals("ABRE_PARENTESIS")) {
            consumir(); 
            NodoAST nodoExpr = parseE(); 
            
            if (obtenerToken().tipo.equals("CIERRA_PARENTESIS")) {
                consumir(); 
            } else {
                System.err.println("Error de sintaxis: Falta paréntesis");
            }

            return nodoExpr;
        }
        
        System.err.println("Error sintaxis");
        consumir(); 
        return new NodoHoja("ERR", "error");
    }
}

// clase wrapper asignacion expresion
class NodeAsignacionOExpresion {
    NodoAsignacion asignacion;
    NodoAST expresion;
    
    public NodeAsignacionOExpresion(NodoAsignacion asig) {
        this.asignacion = asig;
        this.expresion = null;
    }
    
    public NodeAsignacionOExpresion(NodoAST expr) {
        this.asignacion = null;
        this.expresion = expr;
    }
    
    public boolean esAsignacion() {
        return asignacion != null;
    }
    
    public NodoAST getRaizExpresion() {
        return esAsignacion() ? asignacion.expresion : expresion;
    }
    
    public void imprimir() {
        if (esAsignacion()) {
            asignacion.imprimir("", true);
        } else {
            System.out.println("EXPRESIÓN ( E )");
            expresion.imprimir("    ", true);
        }
    }
}


// linea tabla codigo intermedio
class PasoPila {
    int paso;
    String instruccion;
    String estadoPila;
    String explicacion;

    public PasoPila(int paso, String instruccion, String estadoPila, String explicacion) {
        this.paso = paso;
        this.instruccion = instruccion;
        this.estadoPila = estadoPila;
        this.explicacion = explicacion;
    }

    public void imprimirFila() {
        System.out.printf("| %-4d | %-12s | %-20s | %-50s\n", paso, instruccion, estadoPila, explicacion);
    }
}

// generador de codigo intermedio
class GeneradorCodigoPila {
    private List<PasoPila> pasos;
    private int contadorPasos;
    private int contadorTemporales;
    private List<String> pilaSimulada;

    public GeneradorCodigoPila() {
        pasos = new ArrayList<>();
        contadorPasos = 1;
        contadorTemporales = 1;
        pilaSimulada = new ArrayList<>();
    }

// string de pila actual
    private String getEstadoPila() {
        if (pilaSimulada.isEmpty()) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < pilaSimulada.size(); i++) {
            sb.append(pilaSimulada.get(i));
            if (i < pilaSimulada.size() - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

// agregar paso
    private void registrarPaso(String instruccion, String explicacion) {
        pasos.add(new PasoPila(contadorPasos++, instruccion, getEstadoPila(), explicacion));
    }

// iniciar codigo para ast
    public void generarCodigo(NodoAST raiz) {
        pasos.clear();
        contadorPasos = 1;
        contadorTemporales = 1;
        pilaSimulada.clear();
        
        recorridoPostOrden(raiz);
        
    }

// generar codigo de pila
    private void recorridoPostOrden(NodoAST nodo) {
        if (nodo == null) return;

// si es id o num
        if (nodo instanceof NodoHoja) {
            String valor = nodo.getValor();
            pilaSimulada.add(valor); 
            registrarPaso("PUSH " + valor, "Se mete el valor de " + valor);
            return;
        }

// operacion
        if (nodo instanceof NodoOperacion) {
            NodoOperacion op = (NodoOperacion) nodo;
            
// hijo izquierdo
            recorridoPostOrden(op.izquierdo);
            
// hijo derecho
            recorridoPostOrden(op.derecho);
            
// aplicar operacion
            aplicarOperacion(op.operador);
            return;
        }
        
// asignacion completa
        if (nodo instanceof NodoAsignacion) {
            NodoAsignacion asig = (NodoAsignacion) nodo;
            
// expresion
            recorridoPostOrden(asig.expresion);
            
// asiga resultado
            String variable = asig.variable.getValor();
            String resultado = pilaSimulada.isEmpty() ? "error" : pilaSimulada.remove(pilaSimulada.size() - 1);
            
            registrarPaso("POP " + variable, "Se saca el resultado y se asigna a " + variable);
            return;
        }
    }

// sacar operandos y meter temporal
    private void aplicarOperacion(String operador) {
        if (pilaSimulada.size() < 2) {
            System.err.println("Error de semántica: Pila mal formada para operación " + operador);
            return;
        }

// inverse
        String op2 = pilaSimulada.remove(pilaSimulada.size() - 1);
        String op1 = pilaSimulada.remove(pilaSimulada.size() - 1);
        
        String instruccion = "";
        String accion = "";
        String tResult = "t" + contadorTemporales++;

        switch (operador) {
            case "+":
                instruccion = "ADD";
                accion = "suma y mete";
                break;
            case "-":
                instruccion = "SUB";
                accion = "resta y mete";
                break;
            case "*":
                instruccion = "MUL";
                accion = "multiplica y mete";
                break;
            case "/":
                instruccion = "DIV";
                accion = "divide y mete";
                break;
            default:
                instruccion = "NOP";
                accion = "opera";
        }

        pilaSimulada.add(tResult); 
        
        String explicacion = String.format("Se sacan %s y %s, se %s (%s)", op1, op2, accion, tResult);
        registrarPaso(instruccion, explicacion);
    }
    
// imprimir tabla
    public void mostrarTablaCodigoIntermedio() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                        TABLA DE CÓDIGO INTERMEDIO (MÁQUINA DE PILA)                              ║");
        System.out.println("╠══════╦══════════════╦══════════════════════╦═════════════════════════════════════════════════════╣");
        System.out.println("║ Paso ║ Instrucción  ║ Pila (Stack)         ║ Explicación                                         ║");
        System.out.println("╠══════╬══════════════╬══════════════════════╬═════════════════════════════════════════════════════╣");
        
        for (PasoPila p : pasos) {
            System.out.printf("║ %-4d ║ %-12s ║ %-20s ║ %-51s ║\n", p.paso, p.instruccion, p.estadoPila, p.explicacion);
        }
        
        System.out.println("╚══════╩══════════════╩══════════════════════╩═════════════════════════════════════════════════════╝");
    }
}


public class AnalizadorSintactico {

    public static void procesarExpresion(String entrada) {
        System.out.println("════════════════════════════════════════════════════════════════════════════════════════════════════");
        System.out.println("                         EVALUANDO EXPRESIÓN                              ");
        System.out.println("════════════════════════════════════════════════════════════════════════════════════════════════════");
        System.out.println("📝 ENTRADA: " + entrada);
        System.out.println("────────────────────────────────────────────────────────────────────────────────────────────────────");

// analizador lexico
        AnalizadorLexicoCompleto lexer = new AnalizadorLexicoCompleto();
        List<TokenCompleto> tokensRaw = lexer.escanear(entrada);
        
// quitar tokens null
        List<TokenCompleto> tokensAletorios = new ArrayList<>();
        for (TokenCompleto t : tokensRaw) {
            tokensAletorios.add(t);
        }

// analizador sintactico
        Parser parser = new Parser(tokensAletorios);
        NodeAsignacionOExpresion ast = parser.parseInstruccion();

// imprimir arbol
        System.out.println("\n🌳 ÁRBOL SINTÁCTICO:");
        ast.imprimir();

// generacion de codigo intermedio
        GeneradorCodigoPila generador = new GeneradorCodigoPila();
        
// ast recorrido
        if (ast.esAsignacion()) {
            generador.generarCodigo(ast.asignacion);
        } else {
            generador.generarCodigo(ast.expresion);
        }
        
// tabla resultado
        generador.mostrarTablaCodigoIntermedio();
        System.out.println();
    }

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║            ANALIZADOR SINTÁCTICO Y GENERADOR DE CÓDIGO INTERMEDIO (ETAPA 2 DEL COMPILADOR)       ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════════════════════════════════════╝\n");

// ejemplos
        
// ejemplo 1
        String ecuacion1 = "x + y / (a - b / c) * z + w;";
        procesarExpresion(ecuacion1);
        
// ejemplo 2
        String ecuacion2 = "a / b + (c * d) - e * f;";
        procesarExpresion(ecuacion2);
        
// ejemplo 3
        String ecuacion3 = "area = base * altura / 2;";
        procesarExpresion(ecuacion3);
        
// ejemplo 4
        String ecuacion4 = "resultado = (10 + 20) * (30 - 5) / 10;";
        procesarExpresion(ecuacion4);
    }
}
