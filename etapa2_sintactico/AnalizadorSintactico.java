
import java.util.ArrayList;
import java.util.List;

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
