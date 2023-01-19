package com.example.invmgmt;

import java.io.Serializable;
import java.util.ArrayList;

public class Services {

    public static final String DB_USERS = "users";
    public static final String DB_INV = "inv";

    public static class User implements Serializable {

        private ArrayList<Inventory> inventory = new ArrayList<>();

        private String uid;
        private String display_name;
        private String email;

        public User(String uid, String display_name, String email) {
            this.uid = uid;
            this.display_name = display_name;
            this.email = email;
        }

        public User() {
        }

        public ArrayList<Inventory> getInventory() {
            return inventory;
        }

        public void setInventory(ArrayList<Inventory> inventory) {
            this.inventory = inventory;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getDisplay_name() {
            return display_name;
        }

        public void setDisplay_name(String display_name) {
            this.display_name = display_name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void addInventory(Inventory inv) {
            this.inventory.add(inv);
        }

        public void clearInventory() {
            this.inventory.clear();
        }

        @Override
        public String toString() {
            return "User{" +
                    "inventory=" + inventory +
                    ", uid='" + uid + '\'' +
                    ", display_name='" + display_name + '\'' +
                    ", email='" + email + '\'' +
                    '}';
        }

    }

    public static class Inventory implements Serializable {

        private String id;

        private String pro_name, details = "";

        private String pro_cost;

        private int pro_count;

        private String pro_quantity;

        private String supplier_name;

        private String ref = "";

        public Inventory() {
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDetails() {
            return details;
        }

        public void setDetails(String details) {
            this.details = details;
        }

        public String getPro_name() {
            return pro_name;
        }

        public void setPro_name(String pro_name) {
            this.pro_name = pro_name;
        }

        public String getPro_cost() {
            return pro_cost;
        }

        public void setPro_cost(String pro_cost) {
            this.pro_cost = pro_cost;
        }

        public void incrCount() {
            ++this.pro_count;
        }

        public void decrCount() {
            --this.pro_count;
        }

        public int getPro_count() {
            return pro_count;
        }

        public void setPro_count(int pro_count) {
            this.pro_count = pro_count;
        }

        public String getPro_quantity() {
            return pro_quantity;
        }

        public void setPro_quantity(String pro_quantity) {
            this.pro_quantity = pro_quantity;
        }

        public String getSupplier_name() {
            return supplier_name;
        }

        public void setSupplier_name(String supplier_name) {
            this.supplier_name = supplier_name;
        }

        @Override
        public String toString() {
            return "Inventory{" +
                    "id='" + id + '\'' +
                    ", pro_name='" + pro_name + '\'' +
                    ", pro_cost=" + pro_cost +
                    ", pro_count=" + pro_count +
                    ", pro_quantity='" + pro_quantity + '\'' +
                    ", supplier_name='" + supplier_name + '\'' +
                    ", ref='" + ref + '\'' +
                    '}';
        }

        public String getRef() {
            return ref;
        }

        public void setRef(String ref) {
            this.ref = ref;
        }

    }

}
