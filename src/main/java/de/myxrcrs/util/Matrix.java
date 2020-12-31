package de.myxrcrs.util;

import net.minecraft.util.Direction;

public class Matrix {
    
    public static double[][] add(double[][] a, double[][] b)throws Exception{
        if(a.length != b.length){
            throw new Exception("Rows Exception.");
        }
        int rows = a.length, columns = a[0].length;
        double[][] c = new double[rows][columns];
        for(int i = 0 ; i < rows ; i++){
            if(a[i].length != columns || b[i].length != columns){
                throw new Exception("Columns Exception.");
            }
            for(int j = 0 ; j < columns ; j++){
                c[i][j] = a[i][j] + b[i][j];
            }
        }
        return c;
    }

    public static double[][] sub(double[][] a, double[][] b)throws Exception{
        if(a.length != b.length){
            throw new Exception("Rows Exception.");
        }
        int rows = a.length, columns = a[0].length;
        double[][] c = new double[rows][columns];
        for(int i = 0 ; i < rows ; i++){
            if(a[i].length != columns || b[i].length != columns){
                throw new Exception("Columns Exception.");
            }
            for(int j = 0 ; j < columns ; j++){
                c[i][j] = a[i][j] - b[i][j];
            }
        }
        return c;
    }

    public static double[][] mul(double[][] a, double k)throws Exception{
        int rows = a.length, columns = a[0].length;
        double[][] c = new double[rows][columns];
        for(int i = 0 ; i < rows ; i++){
            if(a[i].length != columns){
                throw new Exception("Columns Exception.");
            }
            for(int j = 0 ; j < columns ; j++){
                c[i][j] = a[i][j]*k;
            }
        }
        return c;
    }

    public static double[][] mul(double[][] a, double[][] b)throws Exception{
        int rows = a.length, columns = b[0].length;
        int m = a[0].length;
        if(b.length != m){
            throw new Exception("Rows Exception.");
        }
        double[][] c = new double[rows][columns];
        for(int i=0;i<rows;i++){
            for(int j=0;j<columns;j++){
                for(int k=0;k<m;k++){
                    c[i][j]+=a[i][k]*b[k][j];
                }
            }
        }
        return c;
    }

    public static double[][] directionToMatrix(Direction facing){
        switch(facing){
            case NORTH: return new double[][]{{0,-1}};
            case EAST: return new double[][]{{1,0}};
            case SOUTH: return new double[][]{{0,1}};
            case WEST: return new double[][]{{-1,0}};
            default: return new double[][]{{0,-1}};
        }
    }

}
