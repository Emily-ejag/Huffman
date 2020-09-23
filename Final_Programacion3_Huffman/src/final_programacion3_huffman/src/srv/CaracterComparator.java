/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package final_programacion3_huffman.src.srv;

import java.util.Comparator;

/**
 *
 * @author PBE
 */
public class CaracterComparator implements Comparator<Nodo> {

    @Override
    public int compare(Nodo o1, Nodo o2) {
        return o1.getFrecuencia().compareTo(o2.getFrecuencia());
    }
    
}
