package tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class GenerateAST {
    static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: GenetareAST <output directory>");
            System.exit(64);
        }
        String outputDir = args[0];

        defineAst(outputDir, "TestGen", Arrays.asList(
                //opis strukture AST-ova koje ce generator napisati u jednom generisanju
                "Binary   : Expr left, Token operator, Expr right",
                "Grouping : Expr expression",
                "Literal  : Object value",
                "Unary    : Token operator, Expr right"
        ));
    }

    private static void defineAst(
            String outputDir, String baseName, List<String> types)
            throws IOException {
        String path = outputDir + "/" + baseName + ".java";

        //fajl za pisanje
        PrintWriter writer = new PrintWriter(path, "UTF-8");

        //sadrzaj koji ce biti upisan u fajl koji se kreira
        writer.println("package test;");
        writer.println();
        writer.println("import java.util.List;");
        writer.println();
        //nad-klasa
        writer.println("abstract class " + baseName + " {");

        //definisemo visitor interfejs
        defineVisitor(writer, baseName, types);

        // osnovna accept() metoda
        writer.println();
        writer.println("  abstract <R> R accept(Visitor<R> visitor);");

        //definicija svake AST pod-klase
        for(String type : types){
            String className = type.split(":")[0].trim();

            //ignorisati ovaj split :, deli isti kod na 2 ista dela
            String fields = type.split(":")[1].trim();
            defineType(writer, baseName, className, fields);
        }
        writer.println("}");
        writer.close();
    }

    private static void defineType(
            PrintWriter writer, String baseName,
            String className, String fieldList){
        //pod-klasa
        writer.println("  static class "+ className+" extends "+ baseName + " {");

        String[] fields = fieldList.split(", ");

        writer.println();
        writer.println("    @Override");
        writer.println("    <R> R accept(Visitor<R> visitor) {");
        writer.println("      return visitor.visit" +
                className + baseName + "(this);");
        writer.println("    }");

        //polja
        writer.println();
        for (String field : fields) {
            writer.println("    final " + field + ";");
        }

        //konstruktor
        writer.println("    " + className + "(" + fieldList +") {");
        for(String field:fields){
            String name = field.split(" ")[1];
            writer.println("      this." + name + " = " + name + ";");
        }

        writer.println("  }");
        writer.println("  }");

    }

    private static void defineVisitor(
            PrintWriter writer, String baseName, List<String> types){
        writer.println("    interface Visitor<R> {");

        for(String type: types){
            String typeName = type.split(":")[0].trim();
            writer.println("    R visit"+typeName + baseName + "(" +
                    typeName + " " + baseName.toLowerCase()+ ");");
        }

        writer.println("    }");
    }
}
