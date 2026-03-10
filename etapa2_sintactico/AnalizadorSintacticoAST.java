
import java.util.ArrayList;
import java.util.List;

// Interfaz para todos los nodos del árbol sintáctico (AST)
abstract class NodoAST {
    public abstract String getValor();
    
    // Método para imprimir el árbol visualmente (en consola)
    public abstract void imprimir(String prefijo, boolean esUltimo);
}

// Nodo que representa un número o un identificador (hoja)
class NodoHoja extends NodoAST {
    String tipo;   // "id" o "num"
    String lexema; // el valor, ej: "x" o "5"

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

// Nodo interno que representa una operación (+, -, *, /)
class NodoOperacion extends NodoAST {
    String operador;
    String tipoNodo; // 'E', 'T' (de la gramática)
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
        
        // Imprimimos primero el sub-árbol izquierdo, luego el operador, luego el derecho
        // Para simular el dibujo de la libreta, el operador normalmente cuelga del mismo tipoNodo
        if (izquierdo != null) {
            izquierdo.imprimir(nuevoPrefijo, false);
        }
        
        System.out.println(nuevoPrefijo + "├── " + operador);
        
        if (derecho != null) {
            derecho.imprimir(nuevoPrefijo, true);
        }
    }
}

// Nodo especial para la asignación (ej: y = x + 5)
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

// Analizador Sintáctico por Descenso Recursivo
class Parser {
    private List<TokenCompleto> tokens;
    private int posicionActual;
    
    public Parser(List<TokenCompleto> tokens) {
        this.tokens = tokens;
        this.posicionActual = 0;
    }
    
    // Método auxiliar para obtener el token actual
    private TokenCompleto obtenerToken() {
        if (posicionActual < tokens.size()) {
            return tokens.get(posicionActual);
        }
        return new TokenCompleto("EOF", "");
    }
    
    // Método auxiliar para consumir un token y avanzar
    private void consumir() {
        posicionActual++;
    }
    
    // Iniciar el parseo de una instrucción (esperamos que sea de la forma ID = Expresión ;)
    public NodeAsignacionOExpresion parseInstruccion() {
        // Guardamos el estado actual por si no es asignacion
        int inicio = posicionActual;
        
        // 1. Verificamos si es una asignacion: ID = ...
        if (obtenerToken().tipo.equals("ID")) {
            TokenCompleto idVar = obtenerToken();
            consumir(); // consumimos ID
            
            if (obtenerToken().tipo.equals("ASIGNA")) {
                consumir(); // consumimos '='
                
                NodoAST expr = parseE(); // Parseamos la expresión aritmética
                
                // Ignoramos el ';' final si existe
                if (obtenerToken().tipo.equals("FIN_SENTENCIA")) {
                    consumir();
                }
                
                return new NodeAsignacionOExpresion(
                    new NodoAsignacion(new NodoHoja("id", idVar.lexema), expr)
                );
            }
        }
        
        // Si no fue asignación, regresamos y probamos parsear una simple expresión
        posicionActual = inicio;
        NodoAST expr = parseE();
        
        if (obtenerToken().tipo.equals("FIN_SENTENCIA")) {
            consumir();
        }
        
        return new NodeAsignacionOExpresion(expr);
    }

    // --- REGLAS DE PRODUCCIÓN DE LA GRAMÁTICA ---
    
    // E -> E + T | E - T
    // Se implementa como: E -> T { ( + | - ) T }
    private NodoAST parseE() {
        NodoAST nodoIzq = parseT(); // T
        
        while (obtenerToken().tipo.equals("OPERA_SUMA") || obtenerToken().tipo.equals("OPERA_RESTA")) {
            TokenCompleto operador = obtenerToken();
            consumir(); // Consumir + o -
            NodoAST nodoDer = parseT(); // T
            
            // Construimos el nodo interno 'E'
            nodoIzq = new NodoOperacion("E", operador.lexema, nodoIzq, nodoDer);
        }
        
        return nodoIzq;
    }
    
    // T -> T * F | T / F
    // Se implementa como: T -> F { ( * | / ) F }
    private NodoAST parseT() {
        NodoAST nodoIzq = parseF(); // F
        
        while (obtenerToken().tipo.equals("OPERA_MULT") || obtenerToken().tipo.equals("OPERA_DIVID")) {
            TokenCompleto operador = obtenerToken();
            consumir(); // Consumir * o /
            NodoAST nodoDer = parseF(); // F
            
            // Construimos el nodo interno 'T'
            nodoIzq = new NodoOperacion("T", operador.lexema, nodoIzq, nodoDer);
        }
        
        return nodoIzq;
    }
    
    // F -> id | num | ( E )
    private NodoAST parseF() {
        TokenCompleto token = obtenerToken();
        
        // Caso: Identificador (id)
        if (token.tipo.equals("ID")) {
            consumir();
            return new NodoHoja("id", token.lexema);
        }
        
        // Caso: Número entero (num)
        if (token.tipo.equals("NUM") || token.tipo.equals("NUM_FLOAT")) {
            consumir();
            return new NodoHoja("num", token.lexema);
        }
        
        // Caso: Agrupación ( E )
        if (token.tipo.equals("ABRE_PARENTESIS")) {
            consumir(); // Consumir '('
            NodoAST nodoExpr = parseE(); // Volver a Expresión
            
            if (obtenerToken().tipo.equals("CIERRA_PARENTESIS")) {
                consumir(); // Consumir ')'
            } else {
                System.err.println("Error de sintaxis: Falta paréntesis de cierre ')'");
            }
            // En nuestra visualización, los paréntesis no los metemos al árbol porque la 
            // jerarquía ya da el orden. Pero a veces el profesor sí pone un nodo (E).
            // Para mantener el dibujo original de la libreta, la (E) sí se visualiza muchas veces abajo de F.
            // Retornaremos un NodoOperacion ficticio o simplemente la rama.
            // Para hacerlo igual a la foto, devolvamos la rama directamente.
            
            // En la foto muestra que bajo F cuelga el paréntesis (E), pero omitiremos el token parentesis 
            // ya que el árbol sirve para quitar sintaxis innecesaria y mantener la estructura lógica.
            return nodoExpr;
        }
        
        System.err.println("Error de sintaxis: Se esperaba ID, NUM o '('. Encontrado: " + token.tipo + " '" + token.lexema + "'");
        consumir(); // Consumir token erroneo para evitar ciclo infinito
        return new NodoHoja("ERR", "error");
    }
}

// Wrapper para devolver un AST que puede ser una simple Expresión o una Asignación completa
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
