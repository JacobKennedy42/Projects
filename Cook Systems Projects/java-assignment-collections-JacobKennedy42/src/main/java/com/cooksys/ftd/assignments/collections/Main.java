package com.cooksys.ftd.assignments.collections;

import java.util.Objects;

import com.cooksys.ftd.assignments.collections.model.Manager;
import com.cooksys.ftd.assignments.collections.model.Worker;

public class Main {

    /**
     * TODO [OPTIONAL]: Students may use this main method to manually test their code. They can instantiate Employees
     *  and an OrgChart here and test that the various methods they've implemented work as expected. This class and
     *  method are purely for scratch work, and will not be graded.
     */
    public static void main(String[] args) {

        String a = "dsakjhvdskv";
        String a1 = null;
        String b = "dsakjhvdskv";
        String b1 = null;
        System.out.println(Objects.hash(a, a1) == Objects.hash(b, b1));
    }

}
