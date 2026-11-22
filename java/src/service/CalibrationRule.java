package service;

public interface CalibrationRule {

    /**
     * Рассчитывает ширину зоны краевого сшивки (blending) в пикселях.
     * @param rmsError  среднеквадратичная ошибка проектора (px)
     * @param threshold допустимый порог (px)
     * @return ширина зоны сшивки, 0 если юстировка не пройдена
     */
    int calculateBlendWidth(double rmsError, double threshold);

}