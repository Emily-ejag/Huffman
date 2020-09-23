/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package final_programacion3_huffman.src.srv;

import java.io.*;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author PBE
 */
public class Lector {

    /**
     *
     */
    public Map<String,Nodo> frecuencia;

    public Lector() {
        this.frecuencia = new HashMap<>();
    }


    public List<Nodo> frecuenciaSimbolo(String ruta){
        FileReader reader = null;
        try {
            File archivo = new File(ruta);
            reader = new FileReader(archivo);
            BufferedReader buffReader = new BufferedReader(reader);
            PrintWriter writer = new PrintWriter("binario_original.txt", "UTF-8");

            String s = buffReader.readLine();
            while(s!=null){
                int len = s.length();
                for(int i=0;i<=len-1;i++){
                    Character letra = s.charAt(i);
                    writer.print(Integer.toBinaryString((int) letra));

                    if(frecuencia.containsKey(Character.toString(letra))){
                        this.frecuencia.put(Character.toString(letra), frecuencia.get(Character.toString(letra)).incrementarFrecuencia());
                    }
                    else{
                        this.frecuencia.put(Character.toString(letra), new Nodo(Character.toString(letra)));
                    }
                }
                s = buffReader.readLine();
                if(s!=null){

                    if (frecuencia.containsKey("\n")){
                        this.frecuencia.put("\n", frecuencia.get("\n").incrementarFrecuencia());
                    }
                    else {
                        this.frecuencia.put("\n", new Nodo("\n"));
                    }
                }
                else{
                    this.frecuencia.put("EOF", new Nodo("EOF"));
                }
            }
            writer.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Lector.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Lector.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(Lector.class.getName()).log(Level.SEVERE, null, ex);
            }
        }


        List<Nodo> listaFrecuencia = new LinkedList<>();
        for(String clave: this.frecuencia.keySet()){
            listaFrecuencia.add(this.frecuencia.get(clave));
        }
        Collections.sort(listaFrecuencia, new CaracterComparator());

        return listaFrecuencia;
    }
    public void GenerarArchivoBinario(Map<String,byte[]> claves, String ruta){
        FileOutputStream escritor = null;
        FileReader reader = null;
        try {
            File archivoR = new File(ruta);
            reader = new FileReader(archivoR);
            File archivo = new File("huffman.dat");
            PrintWriter writer = new PrintWriter("binario_huffman.txt", "UTF-8");

            escritor = new FileOutputStream(archivo);
            BufferedReader buffReader = new BufferedReader(reader);
            String s = buffReader.readLine();
            int k = 0;
            int entero = 0;
            while(s!=null){
                int len = s.length();
                for(int i=0;i<=len-1;i++){
                    Character letra = s.charAt(i);
                    byte[] representacion = claves.get(Character.toString(letra));
                    for(byte j: representacion){
                        if (k == 8){
                            writer.print((int) j);
                            escritor.write(entero);
                            entero = j*(int) Math.pow(2,7);
                            k = 1;
                        }
                        else{
                            entero += j*(int) Math.pow(2,7-k);
                            k += 1;
                        }
                    }
                }
                s = buffReader.readLine();
                if (claves.containsKey("\n")){
                    byte[] representacion = claves.get("\n");
                    for(byte j: representacion){
                        if (k == 8){
                            escritor.write(entero);
                            entero = j*(int) Math.pow(2,7);
                            k = 1;
                        }
                        else{
                            entero += j*(int) Math.pow(2,7-k);
                            k += 1;
                        }
                    }
                }
            }
            writer.close();
            s = buffReader.readLine();
            byte[] representacion = claves.get("EOF");
            for(byte j: representacion){
                if (k == 8){
                    escritor.write(entero);
                    entero = j*(int) Math.pow(2,7);
                    k = 1;
                }
                else{
                    entero += j*(int) Math.pow(2,7-k);
                    k += 1;
                }
            }
            escritor.write(entero);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Lector.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Lector.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                escritor.close();
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(Lector.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public String Descomprimir(Nodo raiz, String path){
        FileInputStream lector = null;
        String texto = "";
        try {
            File archivo = new File(path);
            lector = new FileInputStream(archivo);
            Nodo nav = raiz;
            int s = lector.read();
            while(s!= -1){
                List<Integer> bit8 = bit8List(s);
                for(Integer entero: bit8){
                    if(nav.getRamaDer() == null && nav.getRamaIzq() == null){
                        if(nav.getClave() == "EOF"){
                            return texto;
                        }
                        //nos sale eso porque estamos contando el numero de caracteres!
                        texto = texto + nav.getClave();
                        nav = raiz;
                        if (entero == 0){
                            nav = nav.getRamaIzq();
                        }
                        else{
                            nav = nav.getRamaDer();
                        }
                    }
                    else{
                        if (entero == 0){
                            nav = nav.getRamaIzq();
                        }
                        else{
                            nav = nav.getRamaDer();
                        }
                    }
                }
                s = lector.read();
            }
            if(nav.getRamaDer() == null && nav.getRamaIzq() == null){
                    texto = texto + nav.getClave();
                    nav = raiz;
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Lector.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Lector.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                lector.close();
            } catch (IOException ex) {
                Logger.getLogger(Lector.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        return texto;
    }

    private List<Integer> bit8List(int num){
        List<Integer> bitList = new ArrayList();
        while(num > 0){
            if(num%2==0){
                bitList.add(0);
            }
            else{
                bitList.add(1);
            }
            num = num/2;
        }
        int len = bitList.size();
        if(len <8){
            while (len < 8){
                bitList.add(0);
                len = bitList.size();
            }
        }
        Collections.reverse(bitList);
        return bitList;
    }

}
