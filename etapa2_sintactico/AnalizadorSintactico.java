
import java.util.ArrayList;
import java.util.List;

public class AnalizadorSintactico {

    public static void procesarExpresion(String entrada) {
        System.out.println("════════════════════════════════════════════════════════════════════════════════════════════════════");
        System.out.println("                         EVALUANDO EXPRESIÓN                              ");
        System.out.println("════════════════════════════════════════════════════════════════════════════════════════════════════");
        System.out.println("📝 ENTRADA: " + entrada);
        System.out.println("────────────────────────────────────────────────────────────────────────────────────────────────────");

        // 1. ANÁLISIS LÉXICO (Reutilizando la clase del Lexer)
        AnalizadorLexicoCompleto lexer = new AnalizadorLexicoCompleto();
        List<TokenCompleto> tokensRaw = lexer.escanear(entrada);
        
        // Filtrar tokens de error o espacios (el lexer actual ya los ignora)
        List<TokenCompleto> tokensAletorios = new ArrayList<>();
        for (TokenCompleto t : tokensRaw) {
            tokensAletorios.add(t);
        }

        // 2. ANÁLISIS SINTÁCTICO (Construcción del AST)
        Parser parser = new Parser(tokensAletorios);
        NodeAsignacionOExpresion ast = parser.parseInstruccion();

        // Imprimir el árbol
        System.out.println("\n🌳 ÁRBOL SINTÁCTICO:");
        ast.imprimir();

        // 3. GENERACIÓN DE CÓDIGO INTERMEDIO (Máquina de pila)
        GeneradorCodigoPila generador = new GeneradorCodigoPila();
        
        // Recorremos el AST
        if (ast.esAsignacion()) {
            generador.generarCodigo(ast.asignacion);
        } else {
            generador.generarCodigo(ast.expresion);
        }
        
        // Imprimimos la tabla
        generador.mostrarTablaCodigoIntermedio();
        System.out.println();
    }

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║            ANALIZADOR SINTÁCTICO Y GENERADOR DE CÓDIGO INTERMEDIO (ETAPA 2 DEL COMPILADOR)       ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════════════════════════════════════╝\n");

        // Ejemplos de las fotos y ejercicios propuestos
        
        // Ejemplo 1: Foto 1
        String ecuacion1 = "x + y / (a - b / c) * z + w;";
        procesarExpresion(ecuacion1);
        
        // Ejemplo 2: Foto 3
        String ecuacion2 = "a / b + (c * d) - e * f;";
        procesarExpresion(ecuacion2);
        
        // Ejemplo 3: Asignación simple
        String ecuacion3 = "area = base * altura / 2;";
        procesarExpresion(ecuacion3);
        
        // Ejemplo 4: Ecuación más compleja
        String ecuacion4 = "resultado = (10 + 20) * (30 - 5) / 10;";
        procesarExpresion(ecuacion4);
    }
}
