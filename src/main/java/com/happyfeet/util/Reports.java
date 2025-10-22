package com.happyfeet.util;

import com.happyfeet.model.entities.Inventario;
import java.util.List;
import java.util.stream.Collectors;

public class Reports {
    public static List<Inventario> lowStock(List<Inventario> items, int threshold) {
        return items.stream().filter(i -> i.getCantidadStock()!=null && i.getCantidadStock() < threshold).collect(Collectors.toList());
    }
}
