
import java.util.ArrayList;
import java.util.List;

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
