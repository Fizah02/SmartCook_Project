package com.example.smartcook;

public class ProcedureModel {
    private String no;
    private String steps;

    public ProcedureModel(){

    }

    public ProcedureModel(String no, String steps) {
        this.no = no;
        this.steps = steps;
    }

    public String getNo() {
        return no;
    }

    public String getSteps() {
        return steps;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

}
