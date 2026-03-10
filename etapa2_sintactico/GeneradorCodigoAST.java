
import java.util.ArrayList;
import java.util.List;

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
