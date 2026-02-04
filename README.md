# Compiladores e Intérpretes - Prácticas

Este repositorio contiene las tareas y programas desarrollados para la materia de **Compiladores e Intérpretes**.

## 📁 Contenido

### Tarea Programa 1 - Analizador Léxico con Decimales
**Archivo:** `TareaPrograma1_221214.java`

Implementación de un analizador léxico que reconoce:
- **ID** - Identificadores (variables)
- **NUM** - Números enteros
- **NUM_DEC** - Números decimales (ej: 3.14, 99.99)

#### Modificaciones realizadas:
- Se agregó una nueva categoría para el punto (`.`) en la función `getCategoria()`
- Se expandió la tabla de transiciones de 3 a 4 columnas
- Se agregaron estados para manejar la parte decimal

---

### Tarea Programa 2 - Identificadores con _ y $
**Archivo:** `TareaPrograma2_221214.java`

Extensión del analizador léxico para permitir que los identificadores:
- Inicien con letra, guión bajo (`_`) o signo de pesos (`$`)
- Contengan estos caracteres en cualquier posición

#### Ejemplos de identificadores válidos:
- `_variable`
- `$precio`
- `mi_valor`
- `$total_final`
- `nombre$`

---

### Etapa 1 - Analizador Léxico Completo
**Archivo:** `AnalizadorLexicoCompleto.java`

Analizador léxico completo que reconoce todos los tokens necesarios para un compilador:

| Categoría | Tokens |
|-----------|--------|
| Palabras reservadas | `KEY_IF`, `KEY_THEN`, `KEY_ELSE`, `KEY_WHILE`, `KEY_FOR`, `KEY_DO`, `KEY_INT`, `KEY_FLOAT`, `KEY_STRING`, `KEY_DATE`, etc. |
| Identificadores | `ID` |
| Números | `NUM`, `NUM_FLOAT` |
| Cadenas | `CADENA` |
| Delimitadores | `FIN_SENTENCIA`, `INI_BLOQUE`, `FIN_BLOQUE`, `ABRE_PARENTESIS`, `CIERRA_PARENTESIS` |
| Operadores matemáticos | `OPERA_SUMA`, `OPERA_RESTA`, `OPERA_MULT`, `OPERA_DIVID`, `ASIGNA` |
| Operadores lógicos | `OPERA_AND`, `OPERA_OR`, `OPERA_NOT` |

---

## 🚀 Cómo ejecutar

```bash
# Tarea 1
javac TareaPrograma1_221214.java
java TareaPrograma1_221214

# Tarea 2
javac TareaPrograma2_221214.java
java TareaPrograma2_221214

# Tarea 3
javac TareaPrograma3_221214.java
java TareaPrograma3_221214

# Etapa 1 - Analizador Léxico Completo
javac AnalizadorLexicoCompleto.java
java AnalizadorLexicoCompleto
```

## 📊 Tabla de Transiciones

| Estado | Letra/\_/$ | Dígito | Punto | Otro |
|--------|------------|--------|-------|------|
| S0 (Inicio) | S1 | S2 | error | S0 |
| S1 (ID) | S1 | S1 | error | S5 (acepta ID) |
| S2 (NUM) | error | S2 | S3 | S6 (acepta NUM) |
| S3 (después del .) | error | S4 | error | error |
| S4 (decimal) | error | S4 | error | S7 (acepta NUM_DEC) |

## 👤 Autor
- **Matrícula:** 221214

## 📚 Materia
Compiladores e Intérpretes
