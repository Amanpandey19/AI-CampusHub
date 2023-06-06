package com.eCampusNITK.Models;

import java.util.ArrayList;

public class SubjectList {
    public ArrayList<Class_Subject> getSubjectList()
    {
        ArrayList<Class_Subject> class_subjectArrayList = new ArrayList<>();
        class_subjectArrayList.add(new Class_Subject("Web Technologies", false));
        class_subjectArrayList.add(new Class_Subject("Artificial Intelligence", false));
        class_subjectArrayList.add(new Class_Subject("Data Analytics", false));
        class_subjectArrayList.add(new Class_Subject("Graph Theory", false));
        class_subjectArrayList.add(new Class_Subject("Digital Image Processing", false));
        class_subjectArrayList.add(new Class_Subject("Discrete Mathematics", false));
        class_subjectArrayList.add(new Class_Subject("Object Oriented Programming using Java ", false));
        class_subjectArrayList.add(new Class_Subject("Object Oriented Programming using Java Lab", false));
        class_subjectArrayList.add(new Class_Subject("Microprocessors", false));
        class_subjectArrayList.add(new Class_Subject("Data Structures", false));
        class_subjectArrayList.add(new Class_Subject("Fundamentals Of Management", false));
        class_subjectArrayList.add(new Class_Subject("Operating System", false));
        class_subjectArrayList.add(new Class_Subject("Organizational Behavior", false));
        class_subjectArrayList.add(new Class_Subject("Computer Programming using C Lab", false));
        class_subjectArrayList.add(new Class_Subject("Computer Programming using", false));
        class_subjectArrayList.add(new Class_Subject("Free and Open Source Software Systems Lab", false));
        class_subjectArrayList.add(new Class_Subject("Computer Organization and Architecture", false));
        class_subjectArrayList.add(new Class_Subject("Design & Analysis of Algorithms", false));
        class_subjectArrayList.add(new Class_Subject("Data Structures Lab", false));
        class_subjectArrayList.add(new Class_Subject("Database Management System", false));
        class_subjectArrayList.add(new Class_Subject("Database Management System Lab", false));
        class_subjectArrayList.add(new Class_Subject("Network and Programming Security", false));
        class_subjectArrayList.add(new Class_Subject("Software Engineering", false));
        class_subjectArrayList.add(new Class_Subject("Automata", false));
        class_subjectArrayList.add(new Class_Subject("Computer Networks", false));

        return class_subjectArrayList;
    }
}
