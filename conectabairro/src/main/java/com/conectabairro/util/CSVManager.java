package com.conectabairro.util;

import java.io.*;
import java.util.List;

public class CSVManager {

    public static void salvar(String caminho, List<String> linhas) throws IOException {

        BufferedWriter writer = new BufferedWriter(new FileWriter(caminho));

        for (String linha : linhas) {
            writer.write(linha);
            writer.newLine();
        }

        writer.close();
    }

}