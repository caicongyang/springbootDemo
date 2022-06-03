package com.caicongyang.local;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;

public class Test1 {


    private static Comparator<StoreAvailableStockNumDTO> compare() {
        return (i1, i2) -> {
            BigDecimal b1 = i1.getVirtualAvailableStockNum();
            BigDecimal b2 = i2.getVirtualAvailableStockNum();
            if (b1 == null && b1 == null) {
                return 0;
            } else if (b1 != null && b2 == null) {
                return -1;
            } else if (b1 == null) {
                return 1;
            }
            if (b1.compareTo(b2) > 0){
                return -1;
            }else if(b1.compareTo(b2) == 0) {
                return  0;
            }else{
                return  1;
            }
        };

    }

    public static void main(String[] args) {

        StoreAvailableStockNumDTO dto = new StoreAvailableStockNumDTO();
        dto.setVirtualAvailableStockNum(new BigDecimal(10));


        StoreAvailableStockNumDTO dto1 = new StoreAvailableStockNumDTO();
        dto1.setVirtualAvailableStockNum(new BigDecimal(12));

        ArrayList<StoreAvailableStockNumDTO> list   = new ArrayList<>();

        list.add(dto);
        list.add(dto1);

        Comparator<StoreAvailableStockNumDTO> comparator = compare();

        list.sort(comparator);

        for (StoreAvailableStockNumDTO key :list) {
            System.out.println(key.getVirtualAvailableStockNum());
        }



    }
}
