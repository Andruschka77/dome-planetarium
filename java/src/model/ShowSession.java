package model;

import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

@Getter
@Setter
public class ShowSession {

    private int idSession;
    private String scheduledTime;
    private Hall hall;
    private MediaProgram program;
    private Employee responsibleEmployee;
    private int viewersCount;
    private String status;
    private int totalExpectedFrames;

    private List<SessionProjector> sessionProjectors = new ArrayList<>(); // Проекторы сеанса
    private List<TelemetryIncident> incidents = new ArrayList<>();

    public ShowSession(
            int idSession,
            String scheduledTime,
            Hall hall,
            MediaProgram program,
            Employee responsibleEmployee,
            int viewersCount,
            String status
    ) {
        this.idSession = idSession;
        this.scheduledTime = scheduledTime;
        this.hall = hall;
        this.program = program;
        this.responsibleEmployee = responsibleEmployee;
        this.viewersCount = viewersCount;
        this.status = status;
        this.totalExpectedFrames = program.getTotalExpectedFrames();
    }

    // ---- Вспомогательный класс статистики сеанса ----
    public static class TelemetryStats {

        public final int totalIncidents;
        public final int totalDroppedFrames;
        public final double droppedFrameRatePercent;
        public final double stabilityIndexPercent;
        public final int hwIssuesCount;
        public final int syncJitterCount;
        public final int swErrorsCount;
        public final double hwIssuesRatio;
        public final double syncJitterRatio;
        public final double swErrorsRatio;
        public final String sessionVerdict;

        public TelemetryStats(
                int totalIncidents,
                int totalDroppedFrames,
                double droppedFrameRatePercent,
                double stabilityIndexPercent,
                int hwIssuesCount,
                int syncJitterCount,
                int swErrorsCount,
                double hwIssuesRatio,
                double syncJitterRatio,
                double swErrorsRatio,
                String sessionVerdict
        ) {
            this.totalIncidents = totalIncidents;
            this.totalDroppedFrames = totalDroppedFrames;
            this.droppedFrameRatePercent = droppedFrameRatePercent;
            this.stabilityIndexPercent = stabilityIndexPercent;
            this.hwIssuesCount = hwIssuesCount;
            this.syncJitterCount = syncJitterCount;
            this.swErrorsCount = swErrorsCount;
            this.hwIssuesRatio = hwIssuesRatio;
            this.syncJitterRatio = syncJitterRatio;
            this.swErrorsRatio = swErrorsRatio;
            this.sessionVerdict = sessionVerdict;
        }

    }

    /**
     * Алгоритм 2: Статистический анализ телеметрии и показателей качества сеанса.
     * Временная сложность: O(K), где K — количество записей в журнале инцидентов.
     * Ёмкостная сложность: O(1) — агрегация метрик в объект фиксированного размера.
     * @param incidents журнал инцидентов/аномалий сеанса
     * @return статистика качества показа (TelemetryStats)
     */
    public TelemetryStats analyzeSessionTelemetry(List<TelemetryIncident> incidents) {

        if (incidents == null) {
            incidents = Collections.emptyList();
        }

        if (this.totalExpectedFrames <= 0) {
            return new TelemetryStats(
                    0,
                    0,
                    0.0,
                    0.0,
                    0,
                    0,
                    0,
                    0.0,
                    0.0,
                    0.0,
                    "Некорректная длительность сеанса"
            );
        }

        int hwOverheatCount = 0;
        int syncJitterCount = 0;
        int decoderErrCount = 0;
        int totalDropped = 0;

        for (TelemetryIncident inc : incidents) {
            totalDropped += inc.getDroppedFrames();
            switch (inc.getIncidentType().toLowerCase()) {
                case "thermal_warning" -> hwOverheatCount++;
                case "sync_jitter" -> syncJitterCount++;
                default -> decoderErrCount++;
            }
        }

        int total = incidents.size();

        double hwRatio = total > 0 ? Math.round(hwOverheatCount * 1000.0 / total) / 1000.0 : 0.0;
        double syncRatio = total > 0 ? Math.round(syncJitterCount * 1000.0 / total) / 1000.0 : 0.0;
        double swRatio = total > 0 ? Math.round(decoderErrCount * 1000.0 / total) / 1000.0 : 0.0;

        double droppedRate = Math.round(totalDropped * 100.0 / totalExpectedFrames * 1000.0) / 1000.0;
        double stabilityIndex = Math.max(0.0,
                Math.round((100.0 - droppedRate - total * 0.5) * 100.0) / 100.0);

        String verdict = stabilityIndex >= 98.0 ? "Показ эталонного качества"
                : stabilityIndex >= 90.0 ? "Показ удовлетворительного качества"
                : "Сеанс с критическими сбоями";

        return new TelemetryStats(
                total,
                totalDropped,
                droppedRate,
                stabilityIndex,
                hwOverheatCount,
                syncJitterCount,
                decoderErrCount,
                hwRatio,
                syncRatio,
                swRatio,
                verdict
        );
    }

}