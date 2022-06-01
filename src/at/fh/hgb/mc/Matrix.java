package at.fh.hgb.mc;


import java.awt.*;
import java.awt.geom.Point2D;

public class Matrix {
    /**
     * Two-dimensional array containing the matrix values.
     */
    public double[][] mValues;

    /**
     * Standardkonstruktor
     */
    public Matrix() {
    }

    /**
     * Standardkonstruktor
     *
     * @param _m11 Der Wert des Matrix Feldes 1x1 (Zeile1/Spalte1)
     * @param _m12 Der Wert des Matrix Feldes 1x2 (Zeile1/Spalte2)
     *             …
     */
    public Matrix(double _m11, double _m12, double _m13,
                  double _m21, double _m22, double _m23,
                  double _m31, double _m32, double _m33) {
        mValues = new double[][]{{_m11, _m21, _m31}, {_m12, _m22, _m32}, {_m13, _m23, _m33}};
    }

    /**
     * Getter to fetch a specific value in the matrix.
     */
    public double getMatrixValue(int x, int y){
        return mValues[y][x];
    }

    /**
     * Setter to set a specific value in the matrix.
     */
    public void setMatrixValue(int x, int y, double value){
        mValues[y][x] = value;
    }

    /**
     * Liefert eine String-Repräsentation der Matrix
     *
     * @return Ein String mit dem Inhalt der Matrix
     * @see String
     */
    public String toString() {
        if(mValues == null) return "";

        StringBuilder b = new StringBuilder();
        for (int i = 0; i < mValues[0].length; i++) {
            for (int j = 0; j < mValues.length; j++) {
                b.append(/*mValues[i][j]*/getMatrixValue(i,j));
                if (j < mValues.length - 1) {
                    b.append(", ");
                }
            }
            b.append("\n");
        }
        return b.toString();
    }

    /**
     * Liefert die Invers-Matrix der Transformationsmatrix
     *
     * @return Die Invers-Matrix
     */
    public Matrix invers() {
        Matrix dMatrix = determinantMatrix();
        double determinant = determinant();
        return dMatrix.multiply(1/determinant);
    }


    /**
     * Calculates the determinant of a 3x3 Matrix.
     * @return The resulting determinant in double.
     */
    private double determinant() {
        double a11 = getMatrixValue(0,0);
        double a12 = getMatrixValue(0,1);
        double a13 = getMatrixValue(0,2);
        double a21 = getMatrixValue(1,0);
        double a22 = getMatrixValue(1,1);
        double a23 = getMatrixValue(1,2);
        double a31 = getMatrixValue(2,0);
        double a32 = getMatrixValue(2,1);
        double a33 = getMatrixValue(2,2);

        return a11 * a22 * a33 + a12 * a23 * a31 + a13 * a21 * a32 -
                a11 * a23 * a32 - a12 * a21 * a33 - a13 * a22 * a31;
    }

    /**
     * Calculates a Matrix with the determinant of all 2x2 matrizes contained in a 3x3 matrix.
     * Useful for calculating the inverse matrix of a 3x3 matrix.
     * @return Resulting matrix.
     */
    private Matrix determinantMatrix() {
        double a11 = getMatrixValue(0,0);
        double a12 = getMatrixValue(0,1);
        double a13 = getMatrixValue(0,2);
        double a21 = getMatrixValue(1,0);
        double a22 = getMatrixValue(1,1);
        double a23 = getMatrixValue(1,2);
        double a31 = getMatrixValue(2,0);
        double a32 = getMatrixValue(2,1);
        double a33 = getMatrixValue(2,2);

        double m11 = a22*a33-a23*a32;
        double m12 = a13*a32-a12*a33;
        double m13 = a12*a23-a13*a22;
        double m21 = a23*a31-a21*a33;
        double m22 = a11*a33-a13*a31;
        double m23 = a13*a21-a11*a23;
        double m31 = a21*a32-a22*a31;
        double m32 = a12*a31-a11*a32;
        double m33 = a11*a22-a12*a21;
        return new Matrix(m11,m12,m13,m21,m22,m23,m31,m32,m33);
    }

    /**
     * Performs a scalar-multiplication on a Matrix and returns the resulting Matrix. Calling Matrix will not be altered.
     * @param skalar Value by which the values of the Matrix will be multiplied.
     * @return Matrix containing the multiplication result.
     */
    private Matrix multiply(double skalar){
        double a11 = getMatrixValue(0,0);
        double a12 = getMatrixValue(0,1);
        double a13 = getMatrixValue(0,2);
        double a21 = getMatrixValue(1,0);
        double a22 = getMatrixValue(1,1);
        double a23 = getMatrixValue(1,2);
        double a31 = getMatrixValue(2,0);
        double a32 = getMatrixValue(2,1);
        double a33 = getMatrixValue(2,2);

        return new Matrix(a11*skalar,a12*skalar,a13*skalar,a21*skalar,a22*skalar,a23*skalar,a31*skalar,a32*skalar,a33*skalar);
    }

    /**
     * Liefert eine Matrix, die das Ergebnis einer Matrizen-
     * multiplikation zwischen dieser und der übergebenen Matrix
     * ist
     *
     * @param _other Die Matrix mit der Multipliziert werden soll
     * @return Die Ergebnismatrix der Multiplikation
     */
    public Matrix multiply(Matrix _other) {

        int thisCols = mValues.length;
        int thisRows = mValues[0].length;
        int otherCols = _other.mValues.length;
        int otherRows = _other.mValues[0].length;

        int equalSide;

        Matrix A = new Matrix();
        Matrix B = new Matrix();
        Matrix C = new Matrix();

        if (thisCols == otherRows) {
            A.mValues = this.mValues;
            B.mValues = _other.mValues;
            C.mValues = new double[otherCols][thisRows];
            equalSide = thisCols;
        } else {
            A.mValues = _other.mValues;
            B.mValues = this.mValues;
            C.mValues = new double[thisCols][otherRows];
            equalSide = otherCols;
        }

        for (int i = 0; i < C.mValues[0].length; i++) {
            for (int j = 0; j < C.mValues.length; j++) {
                double sum = 0;

                for (int n = 0; n < equalSide; n++) {
                    sum += A.getMatrixValue(i,n) * B.getMatrixValue(n,j);
                }
                C.setMatrixValue(i,j,sum);
            }

        }
        return C;
    }

    /**
     * Multipliziert einen Punkt mit der Matrix und liefert das
     * Ergebnis der Multiplikation zurück
     *
     * @param _pt Der Punkt, der mit der Matrix multipliziert
     *            werden soll
     * @return Ein neuer Punkt, der das Ergebnis der
     * Multiplikation repräsentiert
     * @see Point
     */
    public Point multiply(Point _pt) {
        Matrix pointMatrix = new Matrix();
        pointMatrix.mValues = new double[][]{{_pt.getX(), _pt.getY(), 1}};

        Matrix resultMatrix = multiply(pointMatrix);

        return new Point((int) resultMatrix.mValues[0][0], (int) resultMatrix.mValues[0][1]);
    }

    /**
     * Multipliziert ein Rechteck mit der Matrix und liefert das
     * Ergebnis der Multiplikation zurück
     *
     * @param _rect Das Rechteck, das mit der Matrix multipliziert
     *              werden soll
     * @return Ein neues Rechteck, das das Ergebnis der
     * Multiplikation repräsentiert
     * @see Rectangle
     */
    public Rectangle multiply(Rectangle _rect) {
        Point rectUpLeft = new Point(_rect.x,_rect.y);
        Point resultUpLeft = multiply(rectUpLeft);

        Point rectDownRight = new Point(_rect.x+_rect.width,_rect.y+_rect.height);
        Point resultDownRight = multiply(rectDownRight);

        Rectangle result = new Rectangle(resultUpLeft);
        result.add(resultDownRight);
        return result;
    }

    /**
     * Multipliziert ein Polygon mit der Matrix und liefert das
     * Ergebnis der Multiplikation zurück
     *
     * @param _poly Das Polygon, das mit der Matrix multipliziert
     *              werden soll
     * @return Ein neues Polygon, das das Ergebnis der
     * Multiplikation repräsentiert
     * @see Polygon
     */
    public Polygon multiply(Polygon _poly) {
        int[] resultPolyX = new int[_poly.npoints];
        int[] resultPolyY = new int[_poly.npoints];

        for (int i = 0; i < _poly.npoints; i++) {
            Matrix polyPointMatrix = new Matrix();
            polyPointMatrix.mValues = new double[][]{{_poly.xpoints[i], _poly.ypoints[i], 1}};

            Matrix resultMatrix = multiply(polyPointMatrix);
            resultPolyX[i] = (int) resultMatrix.mValues[0][0];
            resultPolyY[i] = (int) resultMatrix.mValues[0][1];
        }
        return new Polygon(resultPolyX, resultPolyY, resultPolyX.length);
    }

    /**
     * Liefert eine Translationsmatrix
     *
     * @param _x Der Translationswert der Matrix in X-Richtung
     * @param _y Der Translationswert der Matrix in Y-Richtung
     * @return Die Translationsmatrix
     */
    public static Matrix translate(double _x, double _y) {
        Matrix matrix = new Matrix(1, 0, _x, 0, 1, _y, 0, 0, 1);

        return matrix;
    }

    /**
     * Liefert eine Translationsmatrix
     *
     * @param _pt Ein Punkt, der die Translationswerte enthält
     * @return Die Translationsmatrix
     * @see Point
     */
    public static Matrix translate(Point _pt) {
        return new Matrix(1, 0, _pt.getX(), 0, 1, _pt.getY(), 0, 0, 1);
    }

    /**
     * Liefert eine Skalierungsmatrix
     *
     * @param _scaleVal Der Skalierungswert der Matrix
     * @return Die Skalierungsmatrix
     */
    public static Matrix scale(double _scaleVal) {
        return new Matrix(_scaleVal, 0, 0, 0, _scaleVal, 0, 0, 0, 1);
    }

    /**
     * Liefert eine Spiegelungsmatrix (X-Achse)
     *
     * @return Die Spiegelungsmatrix
     */
    public static Matrix mirrorX() {
        return new Matrix(1, 0, 0, 0, -1, 0, 0, 0, 1);
    }

    /**
     * Liefert eine Spiegelungsmatrix (Y-Achse)
     *
     * @return Die Spiegelungsmatrix
     */
    public static Matrix mirrorY() {
        return new Matrix(-1, 0, 0, 0, 1, 0, 0, 0, 1);
    }

    /**
     * Liefert eine Rotationsmatrix
     *
     * @param _alpha Der Winkel (in rad), um den rotiert werden
     *               soll
     * @return Die Rotationsmatrix
     */
    public static Matrix rotate(double _alpha) {
        return new Matrix(Math.cos(_alpha), -Math.sin(_alpha), 0, Math.sin(_alpha), Math.cos(_alpha), 0, 0, 0, 1);
    }

    /**
     * Liefert den Faktor, der benötigt wird, um das _world-
     * Rechteck in das _win-Rechteck zu skalieren (einzupassen)
     * bezogen auf die X-Achse  Breite
     *
     * @param _world Das Rechteck in Weltkoordinaten
     * @param _win   Das Rechteck in Bildschirmkoordinaten
     * @return Der Skalierungsfaktor
     * @see Rectangle
     */
    public static double getZoomFactorX(Rectangle _world,
                                        Rectangle _win) {
        return _win.getWidth() / _world.getWidth();
    }

    /**
     * Liefert den Faktor, der benötigt wird, um das _world-
     * Rechteck in das _win-Rechteck zu skalieren (einzupassen)
     * bezogen auf die Y-Achse  Höhe
     *
     * @param _world Das Rechteck in Weltkoordinaten
     * @param _win   Das Rechteck in Bildschirmkoordinaten
     * @return Der Skalierungsfaktor
     * @see Rectangle
     */
    public static double getZoomFactorY(Rectangle _world,
                                        Rectangle _win) {
        return _win.getHeight() / _world.getHeight();
    }


    /**
     * Liefert eine Matrix, die alle notwendigen Transformationen
     * beinhaltet (Translation, Skalierung, Spiegelung und
     * Translation), um ein _world-Rechteck in ein _win-Rechteck
     * abzubilden
     *
     * @param _world Das Rechteck in Weltkoordinaten
     * @param _win   Das Rechteck in Bildschirmkoordinaten
     * @return Die Transformationsmatrix
     * @see Rectangle
     */
    public static Matrix zoomToFit(Rectangle _world,
                                   Rectangle _win, boolean mirror) {
        Matrix translationMatrixA = Matrix.translate(-_world.getCenterX(), -_world.getCenterY());

        double zoomFactorY = Matrix.getZoomFactorY(_world,_win);
        double zoomFactorX = Matrix.getZoomFactorX(_world,_win);
        double zoomF;
        if(zoomFactorY < 1 && zoomFactorX < 1){
            zoomF = Math.min(zoomFactorY, zoomFactorX);
        } else if(zoomFactorY >= 1 && zoomFactorX >= 1){
            zoomF = Math.min(zoomFactorY, zoomFactorX);
        } else {
            zoomF = zoomFactorY;
        }
        Matrix scaleMatrix = Matrix.scale(zoomF);

        Matrix mirrorMatrix = Matrix.mirrorX();

        Matrix translationMatrixB = Matrix.translate(_win.getCenterX(), _win.getCenterY());

        if(mirror) {
            Matrix tBm = translationMatrixB.multiply(mirrorMatrix);
            Matrix tBms = tBm.multiply(scaleMatrix);
            return tBms.multiply(translationMatrixA);
        } else {
            Matrix tBms = translationMatrixB.multiply(scaleMatrix);
            return tBms.multiply(translationMatrixA);
        }
    }

    /**
     * Liefert eine Matrix, die eine vorhandene Transformations-
     * matrix erweitert, um an einem bestimmten Punkt um einen
     * bestimmten Faktor in die Karte hinein- bzw. heraus zu
     * zoomen
     *
     * @param _old       Die zu erweiternde Transformationsmatrix
     * @param _zoomPt    Der Punkt an dem gezoomt werden soll
     * @param _zoomScale Der Zoom-Faktor um den gezoomt werden
     *                   soll
     * @return Die neue Transformationsmatrix
     * @see Point
     */
    public static Matrix zoomPoint(Matrix _old,
                                   Point _zoomPt,
                                   double _zoomScale) {
        Matrix translationMatrixA = Matrix.translate(-_zoomPt.getX(), -_zoomPt.getY());
        Matrix scaleMatrix = Matrix.scale(_zoomScale);
        Matrix translationMatrixB = Matrix.translate(_zoomPt.getX(), _zoomPt.getY());

        Matrix ts = translationMatrixB.multiply(scaleMatrix);
        Matrix tst = ts.multiply(translationMatrixA);
        return _old.multiply(tst);
    }

    /**
     * Multiplies a Point with this Matrix and returns the result.
     * @param _pt The Point this Matrix should be multiplied with.
     * @return Resulting Point.
     */
    public Point2D.Double multiply(Point2D.Double _pt) {
        double destx = getMatrixValue(0,0) * _pt.x + getMatrixValue(0,1) * _pt.y;
        double desty = getMatrixValue(1,0) * _pt.x + getMatrixValue(1,1) * _pt.y;
        return new Point2D.Double(destx,desty);
    }

}
