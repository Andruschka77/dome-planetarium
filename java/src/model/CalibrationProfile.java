package model;

import lombok.Getter;
import lombok.Setter;
import service.CalibrationRule;
import java.util.ArrayList;
import java.util.List;
import service.StandardBlendRuleImpl;

@Getter
@Setter
public class CalibrationProfile {
    private int idProfile;
    private Projector projector;
    private String calibrationDate;
    private int meshPointsCount;
    private int blendWidthPx;
    private double avgErrorPx;
    private boolean active;
    private List<CalibrationGridPoint> gridPoints;

    public CalibrationProfile(int idProfile, Projector projector, String calibrationDate) {
        this.idProfile = idProfile;
        this.projector = projector;
        this.calibrationDate = calibrationDate;
        this.gridPoints = new ArrayList<>();
        this.active = true;
    }

    public void addGridPoint(CalibrationGridPoint pt) {
        pt.setProfile(this);
        gridPoints.add(pt);
    }

    // ---- Вспомогательный класс результата по одному проектору ----
    public static class ProjectorResult {

        public final int idProjector;
        public final String model;
        public final int pointsChecked;
        public final double rmsErrorPx;
        public final double maxDeviationPx;
        public final int blendMaskWidthPx;
        public final String status;
        public final boolean recalibrationNeeded;

        public ProjectorResult(
                int idProjector,
                String model,
                int pointsChecked,
                double rmsErrorPx,
                double maxDeviationPx,
                int blendMaskWidthPx,
                String status,
                boolean recalibrationNeeded
        ) {
            this.idProjector = idProjector;
            this.model = model;
            this.pointsChecked = pointsChecked;
            this.rmsErrorPx = rmsErrorPx;
            this.maxDeviationPx = maxDeviationPx;
            this.blendMaskWidthPx = blendMaskWidthPx;
            this.status = status;
            this.recalibrationNeeded = recalibrationNeeded;
        }

    }

    // ---- Вспомогательный класс итогового отчёта по кластеру ----
    public static class ClusterReport {

        public final List<ProjectorResult> projectorResults;
        public final boolean isClusterReady;
        public final int totalRecalibrationNeeded;

        public ClusterReport(
                List<ProjectorResult> projectorResults,
                boolean isClusterReady,
                int totalRecalibrationNeeded
        ) {
            this.projectorResults = projectorResults;
            this.isClusterReady = isClusterReady;
            this.totalRecalibrationNeeded = totalRecalibrationNeeded;
        }

    }

    /**
     * Алгоритм 1: Проверка оптического сведения кластера проекторов и расчёт параметров коррекции.
     * Временная сложность: O(N * M), где N — число проекторов, M — число контрольных точек сетки.
     * Ёмкостная сложность: O(N) для формирования отчёта по кластеру.
     * @param profiles          список калибровочных профилей кластера
     * @param toleranceThreshold допустимый предел RMS в пикселях
     * @param rule              правило расчёта ширины зоны краевого сшивки
     * @return отчёт по кластеру (ClusterReport)
     */
    public static ClusterReport verifyClusterCalibration(
            List<CalibrationProfile> profiles,
            double toleranceThreshold,
            CalibrationRule rule
    ) {

        if (rule == null) {
            rule = new StandardBlendRuleImpl();
        }

        List<ProjectorResult> results = new ArrayList<>();
        int totalRecalibrationNeeded = 0;
        boolean isClusterReady = true;

        for (CalibrationProfile profile : profiles) {
            List<CalibrationGridPoint> points = profile.getGridPoints();
            double sumSquaredError = 0.0;
            double maxDeviation = 0.0;
            int pointsCount = points.size();

            for (CalibrationGridPoint pt : points) {
                double dx = pt.getActualX() - pt.getTargetX();
                double dy = pt.getActualY() - pt.getTargetY();
                double dist = Math.sqrt(dx * dx + dy * dy);
                sumSquaredError += dist * dist;
                if (dist > maxDeviation) maxDeviation = dist;
            }

            double rmsError = pointsCount > 0 ? Math.sqrt(sumSquaredError / pointsCount) : 0.0;
            boolean needsRecalib = rmsError > toleranceThreshold;
            int blendWidth = rule.calculateBlendWidth(rmsError, toleranceThreshold);

            if (needsRecalib) {
                totalRecalibrationNeeded++;
                isClusterReady = false;
            }

            results.add(new ProjectorResult(
                    profile.getProjector().getIdProjector(),
                    profile.getProjector().getModel(),
                    pointsCount,
                    Math.round(rmsError * 100.0) / 100.0,
                    Math.round(maxDeviation * 100.0) / 100.0,
                    blendWidth,
                    needsRecalib ? "Требуется юстировка" : "В норме",
                    needsRecalib
            ));
        }

        return new ClusterReport(results, isClusterReady, totalRecalibrationNeeded);
    }

}