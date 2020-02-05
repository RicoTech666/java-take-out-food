package com.thoughtworks;

import java.rmi.MarshalledObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class App {

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        System.out.println("请点餐（菜品Id x 数量，用逗号隔开）：");
        String selectedItems = scan.nextLine();
        String summary = Charge(selectedItems);
        System.out.println(summary);
    }

    /**
     * 接收用户选择的菜品和数量，返回计算后的汇总信息
     *
     * @param selectedItems 选择的菜品信息
     */
    public static String Charge(String selectedItems) {
        Map<String, Integer> inputMap = parseInput(selectedItems);
        Map<String, Double> pricesToBePrinted = getPricesToBePrinted(inputMap);
        double totalPrice = getTotalPrice(pricesToBePrinted);
        Map<String, Double> promotions = getPromotions(inputMap, totalPrice);
        double finalPrice = getFinalPrice(totalPrice, promotions);

        String summary = "============= 订餐明细 =============\n";
        for (Map.Entry<String, Double> entry : pricesToBePrinted.entrySet()) {
            summary += entry.getKey() + "\n";
        }
        summary += "-----------------------------------";
        if (finalPrice != totalPrice) {
            summary += "使用优惠：\n";
            for (Map.Entry<String, Double> entry : promotions.entrySet()) {
                summary += entry.getKey() + "\n";
            }
            summary += "-----------------------------------\n";
        }
        summary += "总计：" + finalPrice + "元\n" + "===================================";
        return summary;
    }

    public static Map<String, Integer> parseInput(String selectedItems) {
        String[] selectedItemsArr = selectedItems.split(
                ",");
        Map<String, Integer> inputMap = new HashMap();
        for (String element : selectedItemsArr
        ) {
            inputMap.put(element.split(" x ")[0], Integer.parseInt(element.split(" x ")[1]));
        }
        return inputMap;
    }

    public static Map<String, Double> getPricesToBePrinted(Map<String, Integer> inputMap) {
        String[] itemIds = getItemIds();
        String[] itemNames = getItemNames();
        double[] itemPrices = getItemPrices();
        Map<String, Double> pricesToBePrinted = new HashMap<>();
        for (Map.Entry<String, Integer> entry : inputMap.entrySet()) {
            for (int i = 0; i < itemIds.length; i++) {
                if (entry.getKey().equals(itemIds[i])) {
                    pricesToBePrinted.put(itemNames[i] + " x " + entry.getValue() + " = " +
                            entry.getValue() * itemPrices[i] + "元", entry.getValue() * itemPrices[i]);
                }
            }
        }
        return pricesToBePrinted;
    }

    public static double getTotalPrice(Map<String, Double> pricesToBePrinted) {
        double totalPrice = 0;
        for (Map.Entry<String, Double> entry : pricesToBePrinted.entrySet()) {
            totalPrice += entry.getValue();
        }
        return totalPrice;
    }

    public static Map<String, Double> getPromotions(Map<String, Integer> inputMap, double totalPrice) {
        String[] halfPriceIds = getHalfPriceIds();
        String[] itemIds = getItemIds();
        String[] itemNames = getItemNames();
        double[] itemPrices = getItemPrices();
        double discountWithHalfPrice = 0;
        String promotionStr = "指定菜品半价(";
        ArrayList<String> discountedNames = new ArrayList<String>();

        for (Map.Entry<String, Integer> entry : inputMap.entrySet()) {
            for (int i = 0; i < halfPriceIds.length; i++) {
                if (entry.getKey().equals(halfPriceIds[i])) {
                    for (int j = 0; j < itemIds.length; j++) {
                        if (entry.getKey().equals(itemIds[j])) {
                            discountWithHalfPrice += entry.getValue() * itemPrices[j] / 2;
                            discountedNames.add(itemNames[j]);
                        }
                    }
                }
            }
        }
        if (!discountedNames.isEmpty()) {
            for (int i = 0; i < discountedNames.size(); i++) {
                if (0 != i) {
                    promotionStr += discountedNames[i] + ", ";
                } else {
                    promotionStr += discountedNames + ")";
                }
            }

        }

        double discountWithFixedPrice = 0;
        if (totalPrice > 30) {
            discountWithFixedPrice = 6;
        }

        Map<String, Double> promotions = new HashMap<>();
        if (discountWithFixedPrice >= discountWithHalfPrice) {
            promotions.put("满30减6元，省6元", discountWithFixedPrice);
        } else {
            promotions.put(promotionStr + ", 省" + discountWithHalfPrice + "元"，discountWithHalfPrice);
        }

        return promotions;
    }

    public static double getFinalPrice(double totalPrice, Map<String, Double> promotions) {
        double finalPrice = 0D;
        for (Map.Entry<String, Double> entry : promotions.entrySet()) {
            finalPrice = totalPrice - entry.getValue();
        }
        return finalPrice;
    }

    /**
     * 获取每个菜品依次的编号
     */
    public static String[] getItemIds() {
        return new String[]{"ITEM0001", "ITEM0013", "ITEM0022", "ITEM0030"};
    }

    /**
     * 获取每个菜品依次的名称
     */
    public static String[] getItemNames() {
        return new String[]{"黄焖鸡", "肉夹馍", "凉皮", "冰粉"};
    }

    /**
     * 获取每个菜品依次的价格
     */
    public static double[] getItemPrices() {
        return new double[]{18.00, 6.00, 8.00, 2.00};
    }

    /**
     * 获取半价菜品的编号
     */
    public static String[] getHalfPriceIds() {
        return new String[]{"ITEM0001", "ITEM0022"};
    }
}
