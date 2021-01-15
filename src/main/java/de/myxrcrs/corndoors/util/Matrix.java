package de.myxrcrs.corndoors.util;

import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.util.Direction;

public class Matrix {
    
    public static double[][] add(double[][] a, double[][] b){
        if(a.length != b.length){
            throw new ArithmeticException("Rows Exception.");
        }
        int rows = a.length, columns = a[0].length;
        double[][] c = new double[rows][columns];
        for(int i = 0 ; i < rows ; i++){
            if(a[i].length != columns || b[i].length != columns){
                throw new ArithmeticException("Columns Exception.");
            }
            for(int j = 0 ; j < columns ; j++){
                c[i][j] = a[i][j] + b[i][j];
            }
        }
        return c;
    }

    public static double[][] sub(double[][] a, double[][] b){
        if(a.length != b.length){
            throw new ArithmeticException("Rows Exception.");
        }
        int rows = a.length, columns = a[0].length;
        double[][] c = new double[rows][columns];
        for(int i = 0 ; i < rows ; i++){
            if(a[i].length != columns || b[i].length != columns){
                throw new ArithmeticException("Columns Exception.");
            }
            for(int j = 0 ; j < columns ; j++){
                c[i][j] = a[i][j] - b[i][j];
            }
        }
        return c;
    }

    public static double[][] mul(double[][] a, double k){
        int rows = a.length, columns = a[0].length;
        double[][] c = new double[rows][columns];
        for(int i = 0 ; i < rows ; i++){
            if(a[i].length != columns){
                throw new ArithmeticException("Columns Exception.");
            }
            for(int j = 0 ; j < columns ; j++){
                c[i][j] = a[i][j]*k;
            }
        }
        return c;
    }

    public static double[][] mul(double[][] a, double[][] b){
        int rows = a.length, columns = b[0].length;
        int m = a[0].length;
        if(b.length != m){
            throw new ArithmeticException("Rows Exception.");
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

    /**
     * Get the unit vector along the given horizontal direction.
     * @param facing Direction.
     * @return A row matrix of the 2d unit vector.
     */
    public static double[][] horizontalDirectionToMatrix(Direction facing){
        switch(facing){
            case NORTH: return new double[][]{{0,-1}};
            case EAST: return new double[][]{{1,0}};
            case SOUTH: return new double[][]{{0,1}};
            case WEST: return new double[][]{{-1,0}};
            default: return new double[][]{{0,-1}};
        }
    }

    /**
     * Get the horizontal direction along the given row matrix of a vector.
     * @param mat A row matrix of the 2d unit vector.
     * @return Direction.
     */
    public static Direction matrixToHorizontalDirection(double[][] mat){
        if(mat.length!=1)throw new ArithmeticException("Rows Exception.");
        if(mat[0].length!=2)throw new ArithmeticException("Columns Exception.");
        if((mat[0][0]==0)==(mat[0][1]==0))throw new ArithmeticException("Direction Exception.");
        return mat[0][0]==0 ? (mat[0][1]>0 ? Direction.SOUTH : Direction.NORTH) : (mat[0][0]>0 ? Direction.EAST : Direction.WEST);
    }

    public static final double[][] HINGE_LEFT = 
            {{ 0, 1},
            { -1, 0}};
    public static final double[][] HINGE_RIGHT = 
            {{ 0,-1},
            {  1, 0}};

    /**
     * Get the unit vector that points at the hinge using the given vector along the facing direction.
     * @param u The vector along the facing direction.
     * @param side The hinge side.
     * <img src="https://img.imgdb.cn/item/60010a163ffa7d37b31c24df.png" alt="vector diagram">
     * @return A row matrix of the 2d unit vector.
     */
    public static double[][] getHingeVector(double[][] u, DoorHingeSide side){
        return Matrix.mul(u,side == DoorHingeSide.LEFT?HINGE_LEFT:HINGE_RIGHT);
    }

}
