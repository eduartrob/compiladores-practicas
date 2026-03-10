
import java.util.ArrayList;
import java.util.List;

// Estructura para almacenar una línea de la tabla de código intermedio
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

// Generador de código intermedio (Máquina de pila)
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

    // Retorna la representación en string de la pila actual
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

    // Agregar un paso a la tabla
    private void registrarPaso(String instruccion, String explicacion) {
        pasos.add(new PasoPila(contadorPasos++, instruccion, getEstadoPila(), explicacion));
    }

    // Iniciar la generación de código para un AST
    public void generarCodigo(NodoAST raiz) {
        pasos.clear();
        contadorPasos = 1;
        contadorTemporales = 1;
        pilaSimulada.clear();
        
        recorridoPostOrden(raiz);
        
        // Si el árbol era una asignación, el último paso saca el resultado
        // En este evaluador de expresiones puras, el final queda en pila
    }

    // Recorrido DFS (Post-Orden) para generar código de pila
    private void recorridoPostOrden(NodoAST nodo) {
        if (nodo == null) return;

        // Si es hoja (id o num)
        if (nodo instanceof NodoHoja) {
            String valor = nodo.getValor();
            pilaSimulada.add(valor); // PUSH a la pila
            registrarPaso("PUSH " + valor, "Se mete el valor de " + valor);
            return;
        }

        // Si es operación interna
        if (nodo instanceof NodoOperacion) {
            NodoOperacion op = (NodoOperacion) nodo;
            
            // Primero hijo izquierdo
            recorridoPostOrden(op.izquierdo);
            
            // Luego hijo derecho
            recorridoPostOrden(op.derecho);
            
            // Finalmente aplicamos la operación
            aplicarOperacion(op.operador);
            return;
        }
        
        // Si es asignación completa
        if (nodo instanceof NodoAsignacion) {
            NodoAsignacion asig = (NodoAsignacion) nodo;
            
            // Evaluamos la expresión de la derecha
            recorridoPostOrden(asig.expresion);
            
            // Asignamos el resultado a la variable
            String variable = asig.variable.getValor();
            String resultado = pilaSimulada.isEmpty() ? "error" : pilaSimulada.remove(pilaSimulada.size() - 1);
            
            registrarPaso("POP " + variable, "Se saca el resultado y se asigna a " + variable);
            return;
        }
    }

    // Ejecuta la operación sacando 2 operandos y metiendo un temporal
    private void aplicarOperacion(String operador) {
        if (pilaSimulada.size() < 2) {
            System.err.println("Error de semántica: Pila mal formada para operación " + operador);
            return;
        }

        // Sacamos los operandos en orden inverso (primero derecho, luego izquierdo)
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

        pilaSimulada.add(tResult); // Metemos el temporal (tj) a la pila
        
        String explicacion = String.format("Se sacan %s y %s, se %s (%s)", op1, op2, accion, tResult);
        registrarPaso(instruccion, explicacion);
    }
    
    // Imprimir la tabla completa
    public void mostrarTablaCodigoIntermedio() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                        TABLA DE CÓDIGO INTERMEDIO (MÁQUINA DE PILA)                              ║");
        System.out.println("╠══════╦══════════════╦══════════════════════╦═════════════════════════════════════════════════════╣");
        System.out.println("║ Paso ║ Instrucción  ║ Pila (Stack)         ║ Explicación                                         ║");
        System.out.println("╠══════╬══════════════╬══════════════════════╬═════════════════════════════════════════════════════╣");
        
        for (PasoPila p : pasos) {
            // Un pequeño padding para que se vea como tabla
            System.out.printf("║ %-4d ║ %-12s ║ %-20s ║ %-51s ║\n", p.paso, p.instruccion, p.estadoPila, p.explicacion);
        }
        
        System.out.println("╚══════╩══════════════╩══════════════════════╩═════════════════════════════════════════════════════╝");
    }
}
