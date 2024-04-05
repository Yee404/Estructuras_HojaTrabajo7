import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

//  Página de consulta para expesiones regulares:
//  https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html

public class Diccionario {

    public static void main(String[] args) throws IOException {

        // Exportar diccionario
        BinarySearchTree<String, Association> diccionario = cargarDiccionario("diccionario.txt");

        if (diccionario != null) {
            System.out.println("El diccionario se ha cargado correctamente");

            try (Scanner scanner = new Scanner(System.in)) {
                System.out.println("Ingrese dirección del archivo: ");
                String Archivo = scanner.nextLine();

                String textoTraducido = traducirTexto(diccionario, Archivo);
                if (textoTraducido != null) {
                    System.out.println("Texto traducido:");
                    System.out.println(textoTraducido);
                } else {
                    System.out.println("Error al traducir texto");
                }
            }
        } else {
            System.out.println("Error al cargar el diccionario.");
        }

        // Clasificar palabras según idioma (detección automática) [EXTRA]
        // Registrar palabras en árbol

        // Menú
        Scanner sc = new Scanner(System.in);
        boolean go = true;
        int opcion = 0;

        while (go) {
            Menu();
            opcion = sc.nextInt();

            switch (opcion) {
                case 1:
                    System.out.print("Ingrese el nombre del archivo de texto a traducir: ");
                    String archivoTexto = sc.nextLine();
                    traducirTexto(diccionario, archivoTexto);
                    break;

                case 2:
                    go = false;
                    break;

                default:
                    System.out.println("\u001B[31mOpción no disponible\u001B[37m");
                    break;
            }

        }

    }

    // Menú
    public static void Menu() {
        System.out.println("___MENÚ___");
        System.out.println("1. Traducir texto del archivo .txt");
        System.out.println("2. Salir");
        System.out.println("Ingrese la opción seleccionada: ");
    }

    // Cargat Archivo de diccionario.txt
    private static BinarySearchTree<String, Association> cargarDiccionario(String archivoDiccionario) {
        BinarySearchTree<String, Association> diccionario = new BinarySearchTree<>(String::compareToIgnoreCase);

        try (BufferedReader br = new BufferedReader(new FileReader(archivoDiccionario))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(",");
                if (partes.length == 3) {
                    String PalabraIngles = partes[0].trim();
                    String palabraEspanol = partes[1].trim();
                    String palabraFrances = partes[2].trim();
                    Association asociacion = new Association(PalabraIngles, palabraEspanol, palabraFrances);
                    diccionario.insert(PalabraIngles, asociacion);
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo de diccionario: " + e.getMessage());
            diccionario = null;
        }

        return diccionario;
    }

    // Traducción del texto (Archivo) ... aplicando limitaciones/ especificaciones
    // de expresiones regulares
    private static String traducirTexto(BinarySearchTree<String, Association> diccionario, String archivoTexto) {
        StringBuilder textoTraducido = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(archivoTexto))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                // Dividir la línea en palabras
                String[] palabras = linea.split("\\s+"); // El s+ se refiere a que pueden haber uno o más espacios en el
                                                         // texto
                for (String palabra : palabras) {
                    // Eliminar caracteres no alfabéticos
                    palabra = palabra.replaceAll("[^a-zA-Z]", "");
                    // Buscar la traducción en el diccionario
                    Association traduccion = diccionario.find(palabra.toLowerCase());
                    if (traduccion != null) {
                        // Agregar la traducción al texto ya traducido
                        textoTraducido.append(traduccion.getEspannol()).append(" ");
                    } else {
                        // Agregar el formato a la traducción final
                        textoTraducido.append("*").append(palabra).append("* ").append(" ");
                    }
                }
                // Agregar un salto de línea al final de cada línea del texto
                textoTraducido.append("\n");
            }
        } catch (IOException e) {
            // Manejar cualquier error de lectura del archivo de texto
            System.out.println("Error al leer el archivo de texto: " + e.getMessage());
        }
        // Devolver el texto traducido como una cadena
        return textoTraducido.toString();
    }

}